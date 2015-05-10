/*
 ===========================================================================
   Copyright 2002-2010 Martin Dvorak

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ===========================================================================
*/
package com.mindcognition.mindraider.application.model.outline;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.core.rdf.RdfModel;
import com.emental.mindraider.core.rest.Data;
import com.emental.mindraider.core.rest.Metadata;
import com.emental.mindraider.core.rest.Resource;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.core.rest.properties.AnnotationProperty;
import com.emental.mindraider.core.rest.properties.LabelProperty;
import com.emental.mindraider.core.rest.properties.OriginProperty;
import com.emental.mindraider.core.rest.properties.ResourcePropertyGroup;
import com.emental.mindraider.core.rest.properties.SourceTwikiFileProperty;
import com.emental.mindraider.core.rest.resource.OutlineResource;
import com.emental.mindraider.core.rest.resource.OutlineResourceExpanded;
import com.emental.mindraider.ui.dialogs.ProgressDialogJFrame;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.emental.mindraider.ui.outline.treetable.OutlineTreeInstance;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.commons.representation.twiki.TwikiToHtml;
import com.mindcognition.mindraider.export.Atomizer;
import com.mindcognition.mindraider.export.Identity;
import com.mindcognition.mindraider.export.Opmlizer;
import com.mindcognition.mindraider.export.TWikifier;
import com.mindcognition.mindraider.tools.GlobalIdGenerator;
import com.mindcognition.mindraider.ui.swing.explorer.ExplorerJPanel;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;
import com.mindcognition.mindraider.utils.Utils;
import com.mindcognition.mindraider.utils.Xsl;

/**
 * Notebooks custodian. <br>
 * <br>
 * Notebook is represented as a resource and an RDF model. Note that notebooks are independent of folders (i.e. notebook
 * is not contained in a folder, but it is just linked). <br>
 * On the other hand, notebooks contain a set of resources. Relationships among these resources are captured by the RDF
 * model associated with notebook resource.
 * <ul>
 * <li>notebook.rdf.xml ... notebook metadata i.e. RDF model holding properties of resources and their relationships.
 * Model contains:
 * <ul>
 * <li>Notebook resource - MR type, rdfs:seq, rdfs:label, sub-concepts (no annotation, xlink:href is present in the
 * folders model) </li>
 * <li>Concept resource - MR type, rdfs:seq, rdfs:label, rdfs:comment (annotation snippet), mr:attachment, xlink:href,
 * dc:created </li>
 * </ul>
 * Primary source of information are resources - RDF model is just metadata/search layer.
 * <li>notebook.xml ... resource representing notebook itself
 * <li>dc.rdf.xml ... Dublin Core annotation
 * </ul>
 * Maintenance and handling of notebooks is driven by URIs i.e. labels, NCNames and other characteristics are not
 * importaint - an only think that matters is uniquie notebook's URI.
 */
public class OutlineCustodian {

    private static final Logger logger = Logger.getLogger(OutlineCustodian.class);

    public static final String FILENAME_XML_RESOURCE = "notebook.xml";
    public static final String FILENAME_RDF_MODEL = "notebook.rdf.xml";
    public static final String FILENAME_DC = "dc.rdf.xml";

    public static final String DIRECTORY_CONCEPTS = "concepts";
    public static final String DIRECTORY_ANNOTATIONS = "annotations";

    public static final int FORMAT_TWIKI = 1;
    public static final int FORMAT_OPML = 2;
    public static final int FORMAT_OPML_HTML = 3;
    public static final int FORMAT_TWIKI_HTML = 5;
    public static final int FORMAT_ATOM = 7;
    
    /*
     * predefined folder & notebook names
     */

    public static String MR_DOC_FOLDER_LOCAL_NAME = "MR";
    public static String MR_DOC_NOTEBOOK_INTRODUCTION_LOCAL_NAME = "Introduction";
    public static String MR_DOC_NOTEBOOK_DOCUMENTATION_LOCAL_NAME = "MR_Documentation";
    public static String MR_DOC_NOTEBOOK_FOR_DEVELOPERS_LOCAL_NAME = "For_Developers";

    /**
     * Notebooks location.
     */
    private String notebooksDirectory;

    /**
     * Subscribers array.
     */
    private ArrayList<OutlineCustodianListener> subscribers;

    /**
     * Active notebook resource (note that model is contained in it).
     */
    private OutlineResource activeOutlineResource;

    /**
     * Constructor.
     */
    public OutlineCustodian(String notebooksDirectory) {
        this.notebooksDirectory = notebooksDirectory;

        subscribers = new ArrayList<OutlineCustodianListener>();

        logger.debug("  Notebooks directory is: " + notebooksDirectory);
        // create directory structure (if don't exist)
        Utils.createDirectory(notebooksDirectory);
    }

    /**
     * Create new outline - directories and associated model.
     * <ul>
     * <li>notebook.rdf.xml ... notebook metadata i.e. RDF model holding properties of resources and their
     * relationships
     * <li>notebook.xml ... resource representing notebook itself
     * </ul>
     * @param label
     * @param uri
     * @param renderUri
     * @return URI of the newly create notebook
     */
    public String create(String label, String uri, String annotation, boolean renderUi) throws Exception {
        logger.debug("Creating new notebook: " + label + " (" + uri + ")");

        // create notebook folder, RDF model and file (to folders cutodian it
        // must be registered explicitely by user)

        String notebookDirectory = getOutlineDirectory(uri);

        if (new File(notebookDirectory).exists()) {
            return "EXISTS";
        }

        Utils.createDirectory(notebookDirectory + DIRECTORY_CONCEPTS);
        Utils.createDirectory(notebookDirectory + DIRECTORY_ANNOTATIONS);

        // notebook resource
        Resource resource = new Resource();
        Metadata meta = resource.getMetadata();
        meta.setAuthor(new URI(MindRaider.profile.getProfileName()));
        meta.setCreated(System.currentTimeMillis());
        meta.setRevision(1);
        meta.setTimestamp(meta.getCreated());
        meta.setUri(new URI(uri));

        // global ID
        resource.addProperty(new OriginProperty(GlobalIdGenerator.generateOutlineUri()));
        
        resource.addProperty(new LabelProperty(label));
        // empty concepts group
        Data data = resource.getData();
        data.addPropertyGroup(new ResourcePropertyGroup(OutlineResource.PROPERTY_GROUP_LABEL_CONCEPTS, new URI(
            OutlineResource.PROPERTY_GROUP_URI_CONCEPTS)));
        resource.setData(data);
        if (annotation == null) {
            annotation = "'" + label + "' outline.";
        }
        resource.addProperty(new AnnotationProperty(annotation));
        // save
        String notebookResourceFilename = notebookDirectory + FILENAME_XML_RESOURCE;
        resource.toXmlFile(notebookResourceFilename);

        // notebook RDF model
        String notebookModelFilename = getModelFilenameByDirectory(notebookDirectory);
        MindRaider.spidersGraph.newModel(notebookModelFilename);
        RdfModel rdfModel = MindRaider.spidersGraph.getRdfModel();
        rdfModel.setFilename(notebookModelFilename);
        rdfModel.setType(RdfModel.FILE_MODEL_TYPE);
        // fill model with initial resource (notebook URI is rdf notebook)
        com.hp.hpl.jena.rdf.model.Resource rdfResource = (com.hp.hpl.jena.rdf.model.Resource) rdfModel.newResource(
            uri,
            false);
        // now create statement(s)
        ResourceDescriptor resourceDescriptor = new ResourceDescriptor(label, uri);
        resourceDescriptor.setCreated(resource.getMetadata().getCreated());
        resourceDescriptor.setAnnotationCite(annotation);
        createOutlineRdfResource(resourceDescriptor, rdfModel.getModel(), rdfResource);
        // save
        rdfModel.save();

        // set active notebooks
        MindRaider.profile.setActiveOutline(new URI(uri));
        activeOutlineResource = new OutlineResource(resource);
        activeOutlineResource.rdfModel = rdfModel;

        // TODO create DC annotation

        // render the model
        if (renderUi) {
            MindRaider.spidersGraph.renderModel();
        }

        // notify subscribers
        for (int i = 0; i < subscribers.size(); i++) {
            ((OutlineCustodianListener) subscribers.get(i)).outlineCreated(activeOutlineResource);
        }

        return uri;
    }

    /**
     * Check whether concept exists within active notebook.
     * @param uri
     * @return
     */
    public boolean conceptExists(String uri) {
        if (MindRaider.profile.getActiveOutlineUri() != null
            && activeOutlineResource != null
            && activeOutlineResource.rdfModel != null) {
            if (activeOutlineResource.rdfModel.getModel().containsResource(ResourceFactory.createResource(uri))) {
                return true;
            }

        }
        return false;
    }

    /**
     * Get notebook filename.
     * @param notebookDirectory
     * @return
     */
    public String getModelFilenameByDirectory(String notebookDirectory) {
        return notebookDirectory + FILENAME_RDF_MODEL;
    }

    /**
     * Get notebook RDF model filename.
     * @param notebookDirectory
     * @return
     */
    public String getResourceFilenameByDirectory(String notebookDirectory) {
        return notebookDirectory + FILENAME_XML_RESOURCE;
    }

    /**
     * Get notebook directory.
     * @param uri The uri.
     * @return Returns the notebook directory.
     */
    public String getOutlineDirectory(String uri) {
        // create directory structure
        String notebookDirectory = notebooksDirectory + File.separator + Utils.getNcNameFromUri(uri) + File.separator;
        return notebookDirectory;
    }

    /**
     * Return the resource file name.
     * @param uri The uri.
     * @return Returns the resource file name.
     */
    public String getResourceFilename(String uri) {
        return getResourceFilenameByDirectory(getOutlineDirectory(uri));
    }

    /**
     * Returns the model file name.
     * @param uri The uri
     * @return Returns the model file name.
     */
    public String getModelFilename(String uri) {
        return getModelFilenameByDirectory(getOutlineDirectory(uri));
    }

    /**
     * Get notebook resource. Load all the notebooks and verify whether URI fits.
     * @param uri notebook URI
     * @return <code>null</code> if notebook not found.
     */
    public Resource get(String uri) {
        String notebookPath = getResourceFilenameByDirectory(getOutlineDirectory(uri));
        if (notebookPath != null) {
            try {
                return new Resource(notebookPath);
            }
            catch (Exception e) {
                logger.debug("Unable to load notebook: " + uri, e);
            }
        }
        return null;
    }

    /**
     * Get discarded concept descriptors of an outline.
     * 
     * @param outlineUri outline URI
     * @return concept descriptors
     */
    public ResourceDescriptor[] getDiscardedConceptDescriptors(String outlineUri) {
        if (outlineUri == null || "".equals(outlineUri)) {
            StatusBar.show("Unable to load Outline - URI is null!");
            return null;
        }
        StatusBar.show("Loading outline '" + outlineUri + "'...");
        String notebookResourceFilename = getResourceFilenameByDirectory(getOutlineDirectory(outlineUri.toString()));
        Resource resource;
        OutlineResource outlineResource;
        try {
            resource = new Resource(notebookResourceFilename);
            outlineResource = new OutlineResource(resource);
        }
        catch (Exception e) {
            logger.debug("Unable to load outline " + outlineUri, e);
            return null;
        }
        String[] conceptUris = outlineResource.getConceptUris();
        
        ArrayList<ResourceDescriptor> result = new ArrayList<ResourceDescriptor>();
        if(conceptUris!=null) {
            // load RDF model
            String outlineModelFilename = getModelFilenameByDirectory(getOutlineDirectory(outlineUri.toString()));
            Model rdfModel;
            try {
                rdfModel = RdfModel.loadModel(outlineModelFilename);
            }
            catch (Exception e1) {
                logger.debug("Unable to load outline model: " + e1.getMessage(), e1);
                return null;
            }
             
            if(rdfModel!=null) {
                for (int i = 0; i < conceptUris.length; i++) {
                    com.hp.hpl.jena.rdf.model.Resource conceptRdfresource = rdfModel.getResource(conceptUris[i]);
                    // check whether it is discarded
                    if(rdfModel.getProperty(conceptRdfresource, MindRaiderVocabulary.isDiscarded)!=null) {
                        logger.debug("DISCARDED "+conceptUris[i]);
                        if(conceptRdfresource!=null) {
                            ResourceDescriptor fullResourceDescriptor = getRdfResourceDescriptor(conceptRdfresource);
                            if(fullResourceDescriptor!=null) {
                                result.add(fullResourceDescriptor);
                            }
                        }
                    }
                }
            } else {
                // outline is broken - let's remove it
            }
            
        }
        return result.toArray(new ResourceDescriptor[result.size()]);
    }

    public ResourceDescriptor[] getNonDiscardedConceptDescriptors() {
        return getConceptDescriptors(true);
    }

    public ResourceDescriptor[] getAllNoteDescriptors(String outlineUri) {
        Model outlineModel;
        try {
            outlineModel = MindRaider.outlineCustodian.getModel(outlineUri);
            Property property = RDF.type;
            String literal = MindRaiderConstants.MR_OWL_CLASS_CONCEPT;
            StmtIterator i = outlineModel.listStatements((com.hp.hpl.jena.rdf.model.Resource)null,property,outlineModel.getResource(literal));
            ArrayList<String> noteUris=new ArrayList<String>();
            while(i.hasNext()) {
                Statement s=i.nextStatement();
                noteUris.add(s.getSubject().getURI());
            }
            return getDescriptorsForNoteUris(true, outlineModel, noteUris.toArray(new String[noteUris.size()]));
        } catch (Exception e) {
            logger.debug("Unable to get resource descriptors",e); // {{debug}}
        }

        return null;
    }
    
    /**
     * Get descriptors of all the concepts from the active notebook (both discarded and not discarded).
     * 
     * @return concept descriptors.
     */
    public ResourceDescriptor[] getAllNoteDescriptors() {
        return getConceptDescriptors(false);
    }
    
    public ResourceDescriptor[] getConceptDescriptors(boolean skipDiscarded) {
        if (activeOutlineResource != null) {
            String[] conceptUris = activeOutlineResource.getConceptUris();
            if (conceptUris != null && conceptUris.length > 0) {
                return getDescriptorsForNoteUris(skipDiscarded, activeOutlineResource.rdfModel.getModel(), conceptUris);
            }
        }
        return null;
    }

    private ResourceDescriptor[] getDescriptorsForNoteUris(boolean skipDiscarded, Model model, String[] conceptUris) {
        if(model==null) {
            return new ResourceDescriptor[0];
        }
        
        ArrayList<ResourceDescriptor> result = new ArrayList<ResourceDescriptor>();
        for (int i = 0; i < conceptUris.length; i++) {
            com.hp.hpl.jena.rdf.model.Resource conceptRdfResource = model.getResource(conceptUris[i]);
            
            try {
                if(skipDiscarded && model.contains(conceptRdfResource, MindRaiderVocabulary.isDiscarded, true)) {
                    continue;
                } else {
                    result.add(
                            new ResourceDescriptor(conceptRdfResource.getProperty(RDFS.label).getObject().toString(), 
                                    conceptUris[i]));                            
                }
            }
            catch (Exception e) {
                logger.debug("Error: ", e);
            }
        }
        return result.toArray(new ResourceDescriptor[result.size()]);
    }
    
    // TODO improve performance - this method is SLOW
    public ResourceDescriptor getNoteDescriptorByName(String noteName) {
        if(noteName!=null && noteName.length()>0) {
            ResourceDescriptor[] descriptors=getAllNoteDescriptors();
            if(descriptors!=null) {
                for (int i = 0; i < descriptors.length; i++) {
                    if(noteName.equals(descriptors[i].getLabel())) {
                        return descriptors[i];
                    }
                }
            }
            
        }
        return null;
    }

    /**
     * Get notebook path on the file system.
     * @param uri notebook URI
     * @return <code>null</code> if notebook not found.
     */
    public String fsGetPath(String uri) {
        if (uri != null) {
            File f = new File(notebooksDirectory);
            File[] s = f.listFiles();

            if (s != null) {
                Resource resource;
                for (int i = 0; i < s.length; i++) {
                    if (s[i].isDirectory()) {
                        String notebookPath = s[i].getAbsolutePath() + File.separator + FILENAME_XML_RESOURCE;
                        try {
                            resource = new Resource(notebookPath);
                        }
                        catch (Exception e) {
                            logger.error("fsGetPath(String)", e);
                            continue;
                        }
                        if (uri.equals(resource.getMetadata().getUri().toASCIIString())) {
                            logger.debug("  Got resource path: " + notebookPath);
                            return notebookPath;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get URIs of all notebooks.
     * @return Returns the array of notebook uri.
     */
    public String[] fsGetNotebooksUris() {
        File f = new File(notebooksDirectory);
        File[] s = f.listFiles();

        ArrayList<String> result = new ArrayList<String>();
        if (s != null) {
            Resource resource;
            for (int i = 0; i < s.length; i++) {
                if (s[i].isDirectory()) {
                    try {
                        resource = new Resource(s[i].getAbsolutePath() + File.separator + FILENAME_XML_RESOURCE);
                    }
                    catch (Exception e) {
                        logger.error("fsGetNotebooksUris()", e);
                        continue;
                    }
                    result.add(resource.getMetadata().getUri().toASCIIString());
                }
            }
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Get notebook resource. Load all the notebooks and verify whether URI fits.
     * @param uri notebook URI
     * @return <code>null</code> if notebook not found.
     */
    public Resource fsGet(String uri) {
        if (uri != null) {
            File f = new File(notebooksDirectory);
            File[] s = f.listFiles();

            if (s != null) {
                Resource resource;
                for (int i = 0; i < s.length; i++) {
                    if (s[i].isDirectory()) {
                        try {
                            resource = new Resource(s[i].getAbsolutePath() + File.separator + FILENAME_XML_RESOURCE);
                        }
                        catch (Exception e) {
                            logger.error("fsGet(String)", e);
                            continue;
                        }
                        if (uri.equals(resource.getMetadata().getUri().toASCIIString())) {
                            logger.debug("  Got resource for uri: " + uri);
                            return resource;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get Notebook URI by it's local name.
     * @param localName
     * @return
     * @throws Exception
     */
    public String getOutlineUriByLocalName(String localName) throws Exception {
        return MindRaiderVocabulary.getNotebookUri(localName);
    }

    /**
     * Rename notebook.
     * @param notebookUri
     * @param newLabel
     * @throws Exception
     */
    public void rename(String notebookUri, String newLabel) throws Exception {
        logger.debug("Renaming notebook '" + notebookUri + "' to '" + newLabel + "'");
        if (notebookUri != null && newLabel != null) {
            // rename folder resource (notebook.xml)
            OutlineResource notebookResource = new OutlineResource(get(notebookUri));
            notebookResource.getLabelProperty().setLabelContent(newLabel);
            notebookResource.save();

            // change label in the tree (folder.rdf.xml)
            MindRaider.labelCustodian.renameNotebook(notebookUri, newLabel);
        }
    }

    /**
     * Load a notebook.
     * @param uri notebook URI.
     */
    public boolean loadOutline(URI uri) {
        if (uri == null || "".equals(uri)) {
            StatusBar.show("Unable to load Notebook - URI is null!");
            return false;
        }

        // check whether notebook is not already loaded
        if (uri.equals(MindRaider.profile.getActiveOutline()) && activeOutlineResource != null) {
            StatusBar.show("Notebook '" + uri.toString() + "' already loaded ;-)");
            return false;
        }

        StatusBar.show("Loading notebook '" + uri + "'...");
        String notebookResourceFilename = getResourceFilenameByDirectory(getOutlineDirectory(uri.toString()));
        Resource resource;
        try {
            resource = new Resource(notebookResourceFilename);
        }
        catch (Exception e) {
            logger.debug("Unable to load notebook " + uri, e);
            StatusBar.show("Error: Unable to load notebook " + uri + "! " + e.getMessage(), Color.RED);
            return false;
        }

        // load RDF model
        String notebookModelFilename = getModelFilenameByDirectory(getOutlineDirectory(uri.toString()));
        try {
            MindRaider.spidersGraph.load(notebookModelFilename);
        } catch (Exception e1) {
            logger.debug("Unable to load notebook model: " + e1.getMessage(), e1);
            MindRaider.profile.setActiveOutline(null);
            MindRaider.profile.setActiveOutlineUri(null);
            activeOutlineResource = null;
                        
            return false;
        }
        MindRaider.spidersGraph.selectNodeByUri(uri.toString());
        MindRaider.masterToolBar.setModelLocation(notebookModelFilename);

        // set notebook URI in profile
        MindRaider.profile.setActiveOutline(uri);
        activeOutlineResource = new OutlineResource(resource);
        activeOutlineResource.rdfModel = MindRaider.spidersGraph.getRdfModel();

        if (resource != null && resource.getMetadata()!=null && resource.getMetadata().getUri()!=null) {
            MindRaider.history.add(resource.getMetadata().getUri().toString());
        }
        else {
            logger.error("Resource " + uri + "not loaded is null!");
            return false;
        }
        return true;
    }

    /**
     * Save notebook resource.
     * @param resource
     */
    public void save(Resource resource) throws Exception {
        if (resource != null) {
            resource.toXmlFile(getResourceFilename(resource.getMetadata().getUri().toString()));
        }
    }

    /**
     * Close active notebook.
     */
    public void close() {
        MindRaider.profile.setActiveOutlineUri(null);
        activeOutlineResource = null;
        MindRaider.spidersGraph.clear();
        MindRaider.spidersGraph.renderModel();
    }

    /**
     * Check whether notebook exists.
     * @param uri
     */
    public boolean exists(String uri) {
        return MindRaider.labelCustodian.exists(uri);
    }

    /**
     * Get directory of the active notebook.
     * @return active notebook directory
     */
    public String getActiveNotebookDirectory() {
        if (activeOutlineResource != null) {
            String uri = activeOutlineResource.resource.getMetadata().getUri().toASCIIString();
            return notebooksDirectory + File.separator + Utils.getNcNameFromUri(uri);
        }
        return null;
    }

    /**
     * Subscribe.
     * @param listener
     */
    public void subscribe(OutlineCustodianListener listener) {
        subscribers.add(listener);
    }

    /**
     * Get active notebook NCName.
     */
    public String getActiveNotebookNcName() {
        if (activeOutlineResource != null) {
            return Utils.getNcNameFromUri(activeOutlineResource.resource.getMetadata().getUri().toASCIIString());
        }
        return null;
    }

    /**
     * Get model associated with the notebook.
     * @param uri notebook URI.
     * @return model.
     * @throws Exception
     */
    public Model getModel(String uri) throws Exception {
        if (uri != null) {
            String notebookModelPath = getModelFilename(uri);
            RdfModel rdfModel = new RdfModel(notebookModelPath);
            if (rdfModel != null) {
                return rdfModel.getModel();
            }
        }
        return null;
    }

    /*
     * import/export
     */

    /**
     * Import from source file and create new notebook.
     * 
     * @param importType
     * @param srcFileName
     */
    public void importNotebook(int importType, String srcFileName, ProgressDialogJFrame progressDialogJFrame) {
        logger.debug("=-> notebook import: " + srcFileName);

        if (srcFileName == null) {
            return;
        }

        try {
            switch (importType) {
            case FORMAT_ATOM:
                Reader reader=new FileReader(new File(srcFileName));
                Atomizer.from(reader, progressDialogJFrame);
                break;
            case FORMAT_TWIKI:
                twikiImport(srcFileName, progressDialogJFrame);
                break;
            }
        }
        catch (Exception e) {
            logger.debug("Unable to import: ", e);
            return;
        }

        MindRaider.mainJFrame.requestFocus();
    }

    /**
     * TWiki import.
     * @param srcFileName
     */
    private void twikiImport(String srcFileName, ProgressDialogJFrame progressDialogJFrame) throws Exception {
        logger.debug("=-> TWiki import: " + srcFileName);

        // DO NOT BUILD EXPANDED NOTEBOOK, but create new notebook, build URIs
        // and use notebook custodian
        // to add concepts into that notebook
        // o you must track path up to the root to know where to add parent

        // file reader and read line by line...
        // o from begin to the first ---++ store to the annotation of the newly
        // created notebook
        // o set label property to the name of the top level root ---+

        FileReader fileReader = new FileReader(srcFileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        // create new notebook in TWiki import folder
        String folderUri = MindRaider.labelCustodian.LABEL_TWIKI_IMPORT_URI;
        String notebookUri = null;
        MindRaider.labelCustodian.create("TWiki Import", MindRaider.labelCustodian.LABEL_TWIKI_IMPORT_URI);

        String[] parentConceptUris = new String[50];

        String notebookLabel, line;
        String lastConceptName = null;
        StringBuffer annotation = new StringBuffer();
        while ((line = bufferedReader.readLine()) != null) {

            // match for section
            if (Pattern.matches("^---[+]+ .*", line)) {
                // if it is root, take the label
                if (Pattern.matches("^---[+]{1} .*", line)) {
                    notebookLabel = line.substring(5);
                    logger.debug("LABEL: " + notebookLabel);

                    notebookUri = MindRaiderVocabulary.getNotebookUri(Utils.toNcName(notebookLabel));
                    String createdUri;

                    while (MindRaiderConstants.EXISTS.equals(createdUri = create(
                        notebookLabel,
                        notebookUri,
                        null,
                        false))) {
                        notebookUri += "_";
                    }
                    notebookUri = createdUri;
                    MindRaider.labelCustodian.addOutline(folderUri, notebookUri);
                    // set source TWiki file property
                    activeOutlineResource.resource.addProperty(new SourceTwikiFileProperty(srcFileName));
                    activeOutlineResource.save();
                    logger.debug("Notebook created: " + notebookUri);
                }
                else {
                    twikiImportProcessLine(
                        progressDialogJFrame,
                        notebookUri,
                        parentConceptUris,
                        lastConceptName,
                        annotation);
                    lastConceptName = line;
                }
                logger.debug(" SECTION: " + line);
            }
            else {

                // read annotation of the current concept
                annotation.append(line);
                annotation.append("\n");
            }
        }
        // add the last opened section
        twikiImportProcessLine(progressDialogJFrame, notebookUri, parentConceptUris, lastConceptName, annotation);

        // close everything
        fileReader.close();

        // now refresh notebook outline
        ExplorerJPanel.getInstance().refresh();
        OutlineJPanel.getInstance().refresh();
        MindRaider.spidersGraph.renderModel();
        // note that back export to twiki button is enabled to according to
        // "TWiki source" property
        // of the active notebook
    }

    /**
     * Process line when importing from TWiki.
     * 
     * @param progressDialogJFrame
     * @param notebookUri
     * @param parentConceptUris
     * @param lastConceptName
     * @param annotation
     * @throws Exception
     */
    private void twikiImportProcessLine(
            ProgressDialogJFrame progressDialogJFrame, 
            String notebookUri,
            String[] parentConceptUris, 
            String lastConceptName, 
            StringBuffer annotation) 
    throws Exception 
    {
        if (lastConceptName == null) {
            // this is notebook annotation (typically TOC before the first
            // section of depth 2)
            activeOutlineResource.setAnnotation(annotation.toString());
            activeOutlineResource.save();
            annotation.setLength(0);
        }
        else {

            // write previous section annotation
            // if(annotation.length()>0) {
            logger.debug("ANNOTATION:\n" + annotation.toString());
            StatusBar.show("Creating concept '" + lastConceptName + "'...");

            int depth = lastConceptName.indexOf(' ');
            lastConceptName = lastConceptName.substring(depth + 1);
            depth -= 4;
            logger.debug("Depth is: " + depth);
            logger.debug("Label is: " + lastConceptName);

            parentConceptUris[depth] = MindRaider.noteCustodian.create(
                activeOutlineResource,
                parentConceptUris[depth - 1],
                lastConceptName,
                MindRaiderVocabulary.getConceptUri(Utils.getNcNameFromUri(notebookUri), "tempConcept"
                    + System.currentTimeMillis()),
                annotation.toString(),
                false,
                MindRaiderConstants.MR_OWL_CONTENT_TYPE_TWIKI);
            
            if (progressDialogJFrame != null) {
                progressDialogJFrame.setProgressMessage(lastConceptName);
            }

            annotation.setLength(0);
            // }
        }
    }

    public void exportOutline(int exportType, String dstFileName) {
        logger.debug("=-> Exporting to " + dstFileName);

        if (MindRaider.outlineCustodian.activeOutlineResource != null) {

            OutlineResourceExpanded notebookResourceExpanded;
            String resourcesFilePrefix = MindRaider.profile.getHomeDirectory() + File.separator + "lib"
                    + File.separator;
            String xslFilePrefix = resourcesFilePrefix + "xsl" + File.separator;
            String cssFilePrefix = resourcesFilePrefix + "css" + File.separator;
            String jsFilePrefix = resourcesFilePrefix + "js" + File.separator;

            try {
                switch (exportType) {
                case FORMAT_ATOM:
                    notebookResourceExpanded = new OutlineResourceExpanded(
                            MindRaider.outlineCustodian.activeOutlineResource,
                            new Identity());
                    Atomizer.to(notebookResourceExpanded, dstFileName);
                    break;
                case FORMAT_TWIKI:
                    notebookResourceExpanded = new OutlineResourceExpanded(MindRaider.outlineCustodian.activeOutlineResource,
                            TWikifier.getInstance());
                    notebookResourceExpanded.save(dstFileName, xslFilePrefix + "export2TWiki.xsl");
                    break;

                case FORMAT_TWIKI_HTML:
                    notebookResourceExpanded = new OutlineResourceExpanded(MindRaider.outlineCustodian.activeOutlineResource,
                            TWikifier.getInstance());
                    String tmpOpml = dstFileName + ".tmp";
                    notebookResourceExpanded.save(tmpOpml, xslFilePrefix + "export2TWiki.xsl");
                    // now we have TWiki, let's use TWiki2Html and create HTML

                    // TODO TWikifier
                    // String twikifiedAnnotation
                    // =TWikifier.getInstance().transform(annotation.getText());
                    FileInputStream fileInputStream = new FileInputStream(new File(tmpOpml));
                    String htmlContent = "<html>" + " <head>" + "   <style type='text/css'>"
                            + "     ul, ol {" + "         margin-top: 0px;" + "         margin-bottom: 3px;"
                            + "         margin-left: 25px;" + "     }" + "     body {"
                            + "         font-family: arial, helvetica, sans-serif; "
                            + "         font-size: small;" + "     }" + "   </style>" + " </head>"
                            + "<body>\n" + TwikiToHtml.translate(fileInputStream) + "\n</body>" + "</html>";
                    // save it to the file
                    File twikiHtmlFile = new File(dstFileName);
                    FileWriter fileWriter = null;
                    try {
                        fileWriter = new FileWriter(twikiHtmlFile);
                        fileWriter.write(htmlContent);
                    } finally {
                        fileWriter.flush();
                        fileWriter.close();
                    }
                    break;

                case FORMAT_OPML:
                    notebookResourceExpanded = new OutlineResourceExpanded(MindRaider.outlineCustodian.activeOutlineResource,
                            Opmlizer.getInstance());
                    // TODO show attachments as nodes in the outline
                    notebookResourceExpanded.save(dstFileName, xslFilePrefix + "export2Opml.xsl");
                    break;

                case FORMAT_OPML_HTML:
                    notebookResourceExpanded = new OutlineResourceExpanded(MindRaider.outlineCustodian.activeOutlineResource,
                            Opmlizer.getInstance());
                    tmpOpml = dstFileName + ".tmp";
                    notebookResourceExpanded.save(tmpOpml, xslFilePrefix + "export2OpmlInternal.xsl");
                    // now we have OPML XML, let's use XSL and create HTML; (css
                    // and js in current directory)
                    Xsl.xsl(tmpOpml, dstFileName, xslFilePrefix + "opml2Html.xsl");
                    // copy css + js to target directory
                    File dstDir = new File(dstFileName);
                    String dstDirectory = dstDir.getParent();

                    String srcOpmlCss = cssFilePrefix + "opml.css";
                    String destOpmlCss = dstDirectory + File.separator + "opml.css";
                    FileUtils.copyFile(new File(srcOpmlCss), new File(destOpmlCss));

                    String srcOpmlJs = jsFilePrefix + "opml.js";
                    String destOpmlJs = dstDirectory + File.separator + "opml.js";
                    FileUtils.copyFile(new File(srcOpmlJs), new File(destOpmlJs));
                    break;

                }
            } catch (Exception e) {
                logger.error("Unable to export notebook!", e);
            }
        }
    }

    /**
     * Returns the active notebook label.
     * 
     * @return the notebook label string.
     */
    public String getActiveNotebookLabel() {
        if (activeOutlineResource != null) {
            return activeOutlineResource.getLabel();
        }
        return null;
    }

    /**
     * Returns the notebook creation timestamp.
     * 
     * @return the long timestamp.
     */
    public long getActiveNotebookCreationTimestamp() {
        if (activeOutlineResource != null) {
            return activeOutlineResource.resource.getMetadata().getCreated();
        }
        return 0;
    }

    /**
     * Returns the notebook annotation.
     * 
     * @return the annotation string.
     */
    public String getActiveNotebookAnnotation() {
        if (activeOutlineResource != null) {
            AnnotationProperty annotationProperty = activeOutlineResource.getAnnotationProperty();
            if (annotationProperty != null) {
                return annotationProperty.getAnnotation();
            }
        }
        return null;
    }

    /**
     * Returns the notebook childred count.
     * 
     * @return the number of children.
     */
    public int getActiveNotebookChildCount() {
        if (activeOutlineResource != null) {
            Seq seq = activeOutlineResource.rdfModel.getModel()
                    .getSeq(activeOutlineResource.resource.getMetadata().getUri().toString());
            return seq.size();
        }
        return 0;
    }

    /**
     * Return the resource descriptor for child the given indexed position.
     * 
     * @param i
     *            the index.
     * @return the resource descriptor.
     */
    public ResourceDescriptor getActiveNotebookChildAt(int i) {
        if (activeOutlineResource != null) {
            Seq seq = activeOutlineResource.rdfModel.getModel()
                    .getSeq(activeOutlineResource.resource.getMetadata().getUri().toString());

            return getRdfResourceDescriptor(i, seq);
        }
        return null;
    }

    /**
     * Returns the resource descriptor for notebook concept child at index
     * position.
     * 
     * @param i
     *            the index position.
     * @param conceptUri
     *            the concept string.
     * @return rhe resource descriptor.
     */
    public ResourceDescriptor getActiveNotebookConceptChildAt(int i, String conceptUri) {
        if (activeOutlineResource != null && conceptUri != null) {
            Seq seq = activeOutlineResource.rdfModel.getModel().getSeq(conceptUri);

            return getRdfResourceDescriptor(i, seq);
        }
        return null;
    }

    /**
     * Returns the children count for active notebook concept.
     * 
     * @param conceptUri
     *            the concept uri string.
     * @return the number of children.
     */
    public int getActiveNotebookConceptChildCount(String conceptUri) {
        if (activeOutlineResource != null && conceptUri != null && conceptUri.length() > 0) {
            Seq seq = activeOutlineResource.rdfModel.getModel().getSeq(conceptUri);
            return seq.size();
        }
        return 0;
    }

    /**
     * Returns the full resource descriptor for the given concept uri.
     * 
     * @param conceptUri
     *            the concept uri string.
     * @return the resource descriptor.
     */
    public ResourceDescriptor getRdfResourceDescriptor(String conceptUri) {
        if (activeOutlineResource != null && conceptUri != null && conceptUri.length() > 0) {
            com.hp.hpl.jena.rdf.model.Resource resource = activeOutlineResource.rdfModel.getModel()
                    .getResource(conceptUri);
            return getRdfResourceDescriptor(resource);
        }
        return null;
    }

    /**
     * Get detailed resource descriptor.
     * 
     * @param i
     *            the index
     * @param seq
     *            the Seq
     */
    private ResourceDescriptor getRdfResourceDescriptor(int i, Seq seq) {
        ResourceDescriptor result = null;
        if (i < seq.size()) {
            com.hp.hpl.jena.rdf.model.Resource resource = seq.getResource(i + 1);
            if (resource != null) {
                result = getRdfResourceDescriptor(resource);
            }
        }
        return result;
    }

    /**
     * Get full resource descriptor.
     * 
     * @param seq
     *            the Seq
     * @param resource
     *            the resource
     * @return Returns the ResourceDescriptor
     */
    public ResourceDescriptor getRdfResourceDescriptor(com.hp.hpl.jena.rdf.model.Resource resource) {
        ResourceDescriptor result;
        Statement statement;
        result = new ResourceDescriptor();
        result.setUri(resource.getURI());

        // try to get label (if any, use URI local name)
        if ((statement = resource.getProperty(RDFS.label)) != null) {
            result.setLabel(statement.getObject().toString());
        } else {
            result.setLabel(Utils.getNcNameFromUri(result.getUri()));
        }

        // try to get annotation cite
        if ((statement = resource.getProperty(RDFS.comment)) != null) {
            result.setAnnotationCite(statement.getObject().toString());
        } else {
            result.setAnnotationCite("");
        }

        // try to get creation time (if any, use 1.1.1970)
        if ((statement = resource.getProperty(DC.date)) != null) {
            if (statement.getObject().toString() != null) {
                result.setCreated(Long.valueOf(statement.getObject().toString()).longValue());
            }
        } else {
            result.setCreated(0);
        }
        return result;
    }

    /**
     * Create notebook RDF resource.
     * 
     * @param notebook
     *            the notebook
     * @param oldModel
     *            the old model
     * @param notebookRdf
     *            the notebook RDF
     */
    public static void createOutlineRdfResource(ResourceDescriptor notebook,
            Model oldModel,
            com.hp.hpl.jena.rdf.model.Resource notebookRdf) {
        notebookRdf.addProperty(RDF.type, RDF.Seq);
        // * MR type
        notebookRdf.addProperty(RDF.type, oldModel.createResource(MindRaiderConstants.MR_OWL_CLASS_NOTEBOOK));
        // * rdfs:label
        notebookRdf.addProperty(RDFS.label, oldModel.createLiteral(notebook.getLabel()));
        // * DC.date
        notebookRdf.addProperty(DC.date, oldModel.createLiteral(notebook.getCreated()));
        // * rdfs:comment (annotation snippet)
        notebookRdf.addProperty(RDFS.comment,
                oldModel.createLiteral(OutlineTreeInstance.getAnnotationCite(notebook.getAnnotationCite())));
    }

    /**
     * Getter for <code>activeNotebookResource</code>.
     * 
     * @return Returns the activeNotebookResource.
     */
    public OutlineResource getActiveOutlineResource() {
        return this.activeOutlineResource;
    }

    /**
     * Setter for <code>activeNotebookResource</code>.
     * 
     * @param activeOutlineResource
     *            The activeNotebookResource to set.
     */
    public void setActiveOutlineResource(OutlineResource activeOutlineResource) {
        this.activeOutlineResource = activeOutlineResource;
    }
}
