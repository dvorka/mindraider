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
package com.mindcognition.mindraider.application.model.label;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.core.rdf.RdfModel;
import com.emental.mindraider.core.rest.Data;
import com.emental.mindraider.core.rest.Metadata;
import com.emental.mindraider.core.rest.Resource;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.core.rest.properties.LabelProperty;
import com.emental.mindraider.core.rest.properties.ResourcePropertyGroup;
import com.emental.mindraider.core.rest.resource.FolderResource;
import com.emental.mindraider.core.rest.resource.OutlineResource;
import com.emental.mindraider.ui.dialogs.ProgressDialogJFrame;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.utils.Utils;

/**
 * Folder custodian takes care of Labels. There are two tasks for folder custdian -
 * Folders/Notebooks tree maintenance (thematical domains ~ mind map) and
 * handling of particular folders. Folders/Notebooks tree captures the
 * thematical hierarchy (rendered by Explorer):
 * <ul>
 *   <li>It is represented using RDF model.</li>
 *   <li>It captures (relative) locations of descriptive files.</li>
 *   <li>It allows up/down/promote/demote operations on folders and notebooks.</li>
 * </ul>
 * Folder is represented as a resource and its relationships. XML resource
 * references a set of notebooks (note that notebooks are not contained directly
 * in the folder - they are just linked). Folder's relationships to notebooks
 * (i.e. location of these notebooks) are captured by RDF model associated with
 * the folder. So Folder is represented using:
 * <ul>
 *   <li>folder.xml ... folder resource - properties and URIs.</li>
 *   <li>dc.rdf.xml ... Dublin Core annotation</li>
 * <ul>
 * Maintenance and handling of folders is driven by URIs i.e. labels, NCNames
 * and other characteristics are not importaint - an only think that matters is
 * uniquie folder's URI. <br>
 * <br>
 * Discarding of resources is treaten as follows:
 * <ul>
 *   <li>Notebook ... discarded notebook is removed from the folder resource,
 *   marked using discarded property and moved to special folder called 'Notebook
 *   trash' (which is not returned by get/...) methods (it is filtered out and
 *   provided only to the trash logic). </li>
 *   <li>Folder ... only folder containing no notebooks can be discarded, a
 *   property saying that it is discarded is added to that folder. </li>
 * </ul>
 */
public class LabelCustodian {
    private static final Logger logger = Logger.getLogger(LabelCustodian.class);

    /**
     * The filename xml resource constant.
     */
    public static final String FILENAME_XML_RESOURCE = "folder.xml";

    /**
     * The filename rdf constant.
     */
    public static final String FILENAME_RDF_MODEL = "folders.rdf.xml";

    /**
     * The filename dc constant.
     */
    public static final String FILENAME_DC = "dc.rdf.xml";

    public String LABEL_TWIKI_IMPORT_URI;
    public String LABEL_ATOM_IMPORT_URI;
    public String LABEL_TRASH_URI;

    /**
     * The folders location.
     */
    private String labelsDirectory;

    /**
     * The folders RDF model location.
     */
    private String labelsRdfModelFile;

    /**
     * The labels model (kept in memory).
     */
    private RdfModel labelsModel;

    /**
     * The subscribers array list.
     */
    private ArrayList<LabelCustodianListener> subscribers;

    /**
     * Constructor.
     *
     * @param labelsDirectory
     *            the folders directory.
     */
    public LabelCustodian(String labelsDirectory) {
        this.labelsDirectory = labelsDirectory;

        subscribers = new ArrayList<LabelCustodianListener>();

        logger.debug("  Folders directory is: " + labelsDirectory);
        // create directory structure (if don't exist)
        Utils.createDirectory(labelsDirectory);

        // initialize
        LABEL_TWIKI_IMPORT_URI = MindRaiderVocabulary.getFolderUri("twikiImport");
        LABEL_ATOM_IMPORT_URI = MindRaiderVocabulary.getFolderUri("atomImport");
        LABEL_TRASH_URI = MindRaiderVocabulary.getFolderUri("notebookTrash");
    }

    /**
     * Initialize folder's custodian.
     *
     * @param labelsDirectory
     */
    public void initialize() {
        labelsRdfModelFile = labelsDirectory + File.separator + FILENAME_RDF_MODEL;
        File f = new File(labelsRdfModelFile);
        if (!f.exists()) {
            // if folders model doesn't exist, let me create it:
            // o create model ontology
            // o add entry for every existing folder
            // o add entry for every existing notebook
            // o initially hierarchy of folders is flat (no folder contains
            // another folder)
            logger.debug("    Going to create RDF model: " + labelsRdfModelFile);
            labelsModel = new RdfModel(labelsRdfModelFile, RdfModel.GENERATED_MODEL_TYPE);
            // initialize model
            com.hp.hpl.jena.rdf.model.Resource mindMapResource = (com.hp.hpl.jena.rdf.model.Resource) labelsModel
                    .newResource(MindRaiderVocabulary.getMindMapUri(), false);
            mindMapResource.addProperty(RDF.type, labelsModel.newResource(MindRaiderConstants.MR_OWL_CLASS_MINDMAP,
                    false));

            // add existing folders/notebooks to the model
            initializeFoldersModel(mindMapResource);
            labelsModel.save();
        } else {
            // load existing model
            logger.debug("  Loading folders model from: " + labelsRdfModelFile);
            labelsModel = new RdfModel(labelsRdfModelFile, RdfModel.loadModel(labelsRdfModelFile));
            logger.debug("  Model loaded!");
        }
    }

    /**
     * Create and generate URI.
     *
     * @param label
     *            the label String
     * @param uri
     *            the uri String
     * @return URI of the created folder.
     */
    public String createAndGenerateUri(String label, String uri) {
        while (MindRaider.labelCustodian.exists(uri)) {
            uri += "_";
        }
        MindRaider.labelCustodian.create(label, uri);
        return uri;
    }

    /**
     * Create new folder with given URI.
     * <ul>
     * <li>folder.xml ... resource representing folder itself. It contains
     * properties that define notebooks associated with this folder.</li>
     * <li>folder.rdf.xml ... RDF model holding relationships</li>
     * <li>dc.rdf.xml ... Dublin core annotation</li>
     * </ul>
     *
     * @param label
     *            must be NCName String
     * @param uri
     *            the uri String
     * @return the new folder String
     */
    public String create(String label, String uri) {
        logger.debug("Creating new label: " + label + " (" + uri + ")");

        try {
            // determine, whether the folder already exists
            if (exists(uri)) {
                return "EXISTS";
            }

            // create directory
            String folderDirectory = createLabelDirectory(uri);

            // build XML resource
            Resource resource = createXmlResourceForLabel(label, uri);

            // empty notebooks group
            addOutlinesGroupToLabelXmlResource(resource);

            // save XML resource
            String folderResource = getLabelXmlResourceFileName(folderDirectory);
            resource.toXmlFile(folderResource);

            // add new folder to the RDF model
            addFolderToModel(
                    labelsModel.getModel().getSeq(MindRaiderVocabulary.getMindMapUri()), 
                    labelsModel.getModel().getResource(MindRaiderConstants.MR_OWL_CLASS_FOLDER), 
                    resource, 
                    folderResource);
            labelsModel.save();

            // TODO folder DC - check jena
            // String folderDc=folderDirectory+File.separator+FILENAME_DC;

            // notify subscribers
            for (int i = 0; i < subscribers.size(); i++) {
                ((LabelCustodianListener) subscribers.get(i)).labelCreated(new FolderResource(resource));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(MindRaider.mainJFrame, "Unable to create folder: " + e.getMessage(),
                    "Folder Creation Error", JOptionPane.ERROR_MESSAGE);
            logger.error("Unable to create Folder!", e);
            return null;
        }

        return uri;
    }

    public String getLabelXmlResourceFileName(String folderDirectory) {
        String folderResource = folderDirectory + FILENAME_XML_RESOURCE;
        return folderResource;
    }

    public void addOutlinesGroupToLabelXmlResource(Resource resource) throws Exception, URISyntaxException {
        Data data = resource.getData();
        data.addPropertyGroup(new ResourcePropertyGroup(FolderResource.PROPERTY_GROUP_LABEL_NOTEBOOKS, new URI(
                FolderResource.PROPERTY_GROUP_URI_NOTEBOOKS)));
        resource.setData(data);
    }

    public Resource createXmlResourceForLabel(String label, String uri) throws URISyntaxException {
        Resource resource = new Resource();
        Metadata meta = resource.getMetadata();
        meta.setAuthor(new URI(MindRaider.profile.getProfileName()));
        meta.setCreated(System.currentTimeMillis());
        meta.setRevision(1);
        meta.setTimestamp(meta.getCreated());
        meta.setUri(new URI(uri));
        resource.setMetadata(meta);
        resource.addProperty(new LabelProperty(label));
        return resource;
    }

    public String createLabelDirectory(String uri) {
        String folderDirectory = labelsDirectory + File.separator + Utils.getNcNameFromUri(uri);
        Utils.createDirectory(folderDirectory);
        folderDirectory += File.separator;
        return folderDirectory;
    }

    /**
     * Save folder resource.
     *
     * @param resource
     *            the resource.
     * @throws Exceptiona
     *             generic exception.
     */
    public void save(Resource resource) throws Exception {
        resource.toXmlFile(getFolderPathFromUri(resource.getMetadata().getUri().toString()));
    }

    /**
     * Get path from URI.
     *
     * @param uri
     *            the uri String
     * @return the folder path String
     */
    public String getFolderPathFromUri(String uri) {
        return (labelsDirectory + File.separator + Utils.getNcNameFromUri(uri) + File.separator + FILENAME_XML_RESOURCE);
    }

    /**
     * Get folder resource.
     *
     * @param uri
     *            URI of the folder to be loaded.
     * @return <code>null</code> if folder not found.
     */
    public Resource get(String uri) {
        Resource resource = null;

        if (uri != null && uri.length() > 0) {
            String relativePath = getFolderOrNotebookLocationFromModel(uri);
            if (relativePath != null) {
                String path = MindRaider.profile.getAbsolutePath(relativePath);
                logger.debug("Going to load folder resource: " + path);
                try {
                    resource = new Resource(path);
                } catch (Exception e) {
                    logger.debug("Unable to load folder resource: " + path);
                    return null;
                }
            }
        }
        return resource;
    }

    /**
     * Get URIs of all folders.
     *
     * @return the uri String array
     */
    public String[] getFolderUris() {
        // TODO cope with discarded folders

        ArrayList<String> result = new ArrayList<String>();
        StmtIterator i = labelsModel.getModel().listStatements(null, RDF.type,
                labelsModel.getResource(MindRaiderConstants.MR_OWL_CLASS_FOLDER));
        while (i.hasNext()) {
            String folderUri = i.nextStatement().getSubject().getURI();
            if (!LABEL_TRASH_URI.equals(folderUri)) {
                result.add(folderUri);
            }
        }

        if (result.size() == 0) {
            return null;
        }

        return (String[]) (result.toArray(new String[result.size()]));
    }

    /**
     * Get folder resource descriptor.
     *
     * @param folderUri
     * @return
     */
    public ResourceDescriptor getFolderDescriptor(String folderUri) {
        if (folderUri != null) {
            ResourceDescriptor[] resourceDescriptors = getLabelDescriptors();
            if (resourceDescriptors != null && resourceDescriptors.length > 0) {
                for (int i = 0; i < resourceDescriptors.length; i++) {
                    if (folderUri.equals(resourceDescriptors[i].getUri())) {
                        return resourceDescriptors[i];
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get descriptors of all folders.
     *
     * @return folder descriptors.
     */
    public ResourceDescriptor[] getLabelDescriptors() {
        ArrayList<ResourceDescriptor> result = new ArrayList<ResourceDescriptor>();
        Seq mindMap = labelsModel.getModel().getSeq(MindRaiderVocabulary.getMindMapUri());
        if (mindMap != null) {
            String folderUri;
            for (int i = 0; i < mindMap.size(); i++) {
                folderUri = mindMap.getResource(i + 1).toString();
                if (!LABEL_TRASH_URI.equals(folderUri)) {
                    String folderLabel = labelsModel.getModel().getResource(folderUri).getProperty(RDFS.label)
                            .getString();
                    result.add(new ResourceDescriptor(folderLabel, folderUri));
                }
            }
        }

        if (result.size() == 0) {
            return null;
        }

        return (ResourceDescriptor[]) (result.toArray(new ResourceDescriptor[result.size()]));
    }

    /**
     * Get notebook resource descriptor.
     *
     * @param notebookUri
     * @return
     */
    public ResourceDescriptor getOutlineDescriptor(String notebookUri) {
        if (notebookUri != null) {
            ResourceDescriptor[] resourceDescriptors = getNotebookDescriptors();
            if (resourceDescriptors != null && resourceDescriptors.length > 0) {
                for (int i = 0; i < resourceDescriptors.length; i++) {
                    if (notebookUri.equals(resourceDescriptors[i].getUri())) {
                        return resourceDescriptors[i];
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get descriptors of all notebooks.
     */
    public ResourceDescriptor[] getNotebookDescriptors() {
        // TODO inefficient version - take it directly from the RDF Model
        HashSet<ResourceDescriptor> result = new HashSet<ResourceDescriptor>();

        ResourceDescriptor[] folderDescriptors = getLabelDescriptors();
        if (folderDescriptors != null && folderDescriptors.length > 0) {
            for (int i = 0; i < folderDescriptors.length; i++) {
                ResourceDescriptor[] notebookDescriptors = getOutlineDescriptors(folderDescriptors[i].getUri());
                if (notebookDescriptors != null && notebookDescriptors.length > 0) {
                    result.addAll(Arrays.asList(notebookDescriptors));
                }
            }
        }

        if (result.size() > 0) {
            return (ResourceDescriptor[]) result.toArray(new ResourceDescriptor[result.size()]);
        }
        return null;
    }

    /**
     * Get descriptors of all notebooks within particular folder.
     *
     * @return notebook descriptors.
     */
    public ResourceDescriptor[] getOutlineDescriptors(String folderUri) {
        // TODO cope with discarded notebooks

        ArrayList<ResourceDescriptor> result = new ArrayList<ResourceDescriptor>();

        Seq folderSequence = labelsModel.getModel().getSeq(folderUri);
        for (int i = 0; i < folderSequence.size(); i++) {
            ResourceDescriptor resourceDescriptor = new ResourceDescriptor();
            resourceDescriptor.setUri(folderSequence.getResource(i + 1).toString());
            // logger.debug("<-> getNotebookDescriptors: "+resourceDescriptor.uri);
            Statement labelProperty 
                = labelsModel.getModel().getResource(resourceDescriptor.getUri()).getProperty(RDFS.label);
            if (labelProperty != null) {
                resourceDescriptor.setLabel(labelProperty.getObject().toString());
            } else {
                resourceDescriptor.setLabel("[NO LABEL]");
            }
            result.add(resourceDescriptor);
        }

        if (result.size() == 0) {
            return null;
        }

        return (ResourceDescriptor[]) (result.toArray(new ResourceDescriptor[result.size()]));
    }

    /**
     * Get all the notebook's folders (labels).
     */
    public ResourceDescriptor[] getNotebookFolders(String notebookUri) {
        com.hp.hpl.jena.rdf.model.Resource notebookRdfResource = labelsModel.getResource(notebookUri);
        StmtIterator listStatements = labelsModel.getModel().listStatements(null, null, notebookRdfResource);
        
        // get all the incomming triplets and test whether the subject is folder URI
        logger.debug("Notebook's folders:"); // {{debug}}
        ArrayList<ResourceDescriptor> result=new ArrayList<ResourceDescriptor>();
        while (listStatements.hasNext()) {
            Statement statement = (Statement) listStatements.next();
            logger.debug("  "+statement.getSubject()); // {{debug}}
            // get label
            NodeIterator labelIterator = labelsModel.getModel().listObjectsOfProperty(statement.getSubject(), RDFS.label);
            if(labelIterator.hasNext()) {
                String label = labelIterator.next().toString();
                logger.debug("  label: "+label); // {{debug}}
                result.add(new ResourceDescriptor(label,statement.getSubject().toString()));
            }
        }
        
        return result.toArray(new ResourceDescriptor[result.size()]);
    }
    
    /**
     * Get descriptors of of discarded notebooks.
     *
     * @return notebook descriptors.
     */
    public ResourceDescriptor[] getDiscardedOutlineDescriptors() {
        return getOutlineDescriptors(LABEL_TRASH_URI);
    }

    /**
     * Detect whether notebook is in trash.
     *
     * @param uri
     * @return
     */
    public boolean isInTrashNotebook(String uri) {
        if (uri != null) {
            ResourceDescriptor[] resourceDescriptors = getDiscardedOutlineDescriptors();
            if (resourceDescriptors != null && resourceDescriptors.length > 0) {
                for (int i = 0; i < resourceDescriptors.length; i++) {
                    if (uri.equals(resourceDescriptors[i].getUri())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Add notebook to folder.
     *
     * @param folderUri
     * @param outlineUri
     */
    public FolderResource addOutline(String folderUri, String outlineUri) throws Exception {
        if (folderUri == null || outlineUri == null) {
            logger.error("Folder URI/notebook URI is null!");
            return null;
        }

        logger.debug("Checking existince of the folder URI: "+folderUri);
        if(!exists(folderUri)) {
            create(folderUri.substring(folderUri.indexOf("#")+1), folderUri);
        }
        
        FolderResource folder = addOutlineToLabelXmlResourceAndSave(folderUri, outlineUri);

        // add new notebook to the RDF model
        addOutlineToModel(labelsModel.getModel().getSeq(folderUri), labelsModel.getModel().getResource(
                MindRaiderConstants.MR_OWL_CLASS_NOTEBOOK), MindRaider.outlineCustodian.get(outlineUri),
                MindRaider.outlineCustodian.getResourceFilename(outlineUri));
        labelsModel.save();

        return folder;
    }

    public FolderResource addOutlineToLabelXmlResourceAndSave(String folderUri, String outlineUri) throws Exception {
        FolderResource folder = new FolderResource(get(folderUri));
        folder.addNotebook(outlineUri);
        save(folder.getResource());
        return folder;
    }

    /**
     * Move notebook up.
     *
     * @param folderUri
     * @param notebookUri
     */
    public boolean moveNotebookUp(String folderUri, String notebookUri) throws Exception {
        // move notebook in the model
        return labelsModel.upInSequence(labelsModel.getResource(folderUri), notebookUri);
    }

    /**
     * Move notebook down.
     *
     * @param folderUri
     * @param notebookUri
     */
    public boolean moveNotebookDown(String folderUri, String notebookUri) throws Exception {
        return labelsModel.downInSequence(labelsModel.getResource(folderUri), notebookUri);
    }

    /**
     * Move folder down.
     *
     * @param folderUri
     * @param outlineUri
     */
    public boolean moveDown(String folderUri) throws Exception {
        return labelsModel.downInSequence(labelsModel.getModel().getSeq(MindRaiderVocabulary.getMindMapUri()),
                folderUri);
    }

    /**
     * Move folder up.
     *
     * @param folderUri
     * @param outlineUri
     */
    public boolean moveUp(String folderUri) throws Exception {
        return labelsModel.upInSequence(labelsModel.getModel().getSeq(MindRaiderVocabulary.getMindMapUri()),
                folderUri);
    }

    /**
     * Check whether folder or notebook exists.
     *
     * @param uri
     *            the uri String
     * @return Returns <code>true</code> if uri exists, otherwise
     *         <code>false</code>
     */
    public boolean exists(String uri) {
        if (labelsModel.getModel().containsResource(ResourceFactory.createResource(uri))) {
            return true;
        }
        return false;
    }

    /**
     * Rename folder.
     *
     * @param folderUri
     *            the folder Uri
     * @param newLabel
     *            the new label
     * @throws Exception
     *             a generic exception
     */
    public void rename(String folderUri, String newLabel) throws Exception {
        logger.debug("Renaming folder '" + folderUri + "' to '" + newLabel + "'");
        if (folderUri != null && newLabel != null) {
            // rename folder resource
            FolderResource folderResource = new FolderResource(get(folderUri));
            folderResource.getLabelProperty().setLabelContent(newLabel);
            folderResource.save();

            // change label in the tree
            Seq folderSeq = labelsModel.getModel().getSeq(folderUri);
            if (folderSeq != null) {
                folderSeq.removeAll(RDFS.label);
                folderSeq.addProperty(RDFS.label, newLabel);
                labelsModel.save();
            }
        }
    }

    /**
     * Rename notebook in the model.
     *
     * @param notebookUri
     *            the notebook uri
     * @param newLabel
     *            the new label
     * @throws Exception
     *             a generic exception
     */
    public void renameNotebook(String notebookUri, String newLabel) throws Exception {
        if (notebookUri != null && newLabel != null) {
            // change label in the tree
            com.hp.hpl.jena.rdf.model.Resource notebookResource = labelsModel.getModel().getResource(notebookUri);
            if (notebookResource != null) {
                notebookResource.removeAll(RDFS.label);
                notebookResource.addProperty(RDFS.label, newLabel);
                labelsModel.save();
            }

            // rename also XML (if notebook is active)
            final OutlineResource activeNotebookResource = MindRaider.outlineCustodian.getActiveOutlineResource();
            if(MindRaider.profile!=null 
                    && activeNotebookResource!=null
                    && MindRaider.profile.getActiveOutline()!=null                     
                    && notebookUri.equals(MindRaider.profile.getActiveOutline().toString())) {                
                activeNotebookResource.getLabelProperty().setLabelContent(newLabel);
                activeNotebookResource.getAnnotationProperty().setAnnotation("'"+newLabel+"' notebook.");
                
            }
        }
    }

    /**
     * Discard label.
     *
     * @param uri
     *            folder URI.
     * @return Returns <code>true</code> if it discards, otherwise
     *         <code>false</code>
     * @throws Exception
     *             a generic exception
     */
    public boolean discardLabel(String uri) throws Exception {
        if (uri != null && !LABEL_TRASH_URI.equals(uri)) {
            // only empty folder can be discarded
            Seq folderSeq = labelsModel.getModel().getSeq(uri);
            if (folderSeq.size() == 0) {
                folderSeq.removeProperties();
                // remove it from model mind map
                Seq mindMap = labelsModel.getModel().getSeq(MindRaiderVocabulary.getMindMapUri());
                int idx = mindMap.indexOf(folderSeq);
                if (idx >= 0) {
                    try {
                        mindMap.remove(idx);
                    } catch(Exception e) {
                        logger.error("Removal of the discarded property in the sequence failed: #"+idx+" "+uri); // {{debug}}
                    }
                }
                labelsModel.save();

                // delete it from file system
                Utils.deleteSubtree(new File(getFolderPathFromUri(uri)).getParentFile());

                logger.debug("Folder deleted: "+uri); // {{debug}}
                
                return true;
            }
        }
        return false;
    }

    /**
     * Delete notebook.
     *
     * @param uri
     *            the uri String
     */
    public void deleteOutline(String uri) {
        if (uri != null) {
            String path = getNotebookLocationFromModel(uri);
            if (path != null && path.length() > 0) {
                try {
                    Utils.deleteSubtree(new File(MindRaider.profile.getAbsolutePath(path)).getParentFile());
                } catch (Exception e) {
                    logger.error("Unable to delete outline files: " + path, e);
                }
                
                try {
                    // delete notebook from the model
                    com.hp.hpl.jena.rdf.model.Resource notebookRdfResource = labelsModel.getResource(uri);
                    // seems like that only outgoing properties are deleted (not incomming)
                    notebookRdfResource.removeProperties();
                    // remove it from the discarded holder
                    Seq folderSequence = labelsModel.getModel().getSeq(LABEL_TRASH_URI);
                    folderSequence.remove(folderSequence.indexOf(notebookRdfResource));
                    labelsModel.save();
                } catch (Exception e) {
                    logger.error("Unable to delete outline from the RDF model: " + uri, e);
                }                
            } else {
                logger.debug("Path for the resource " + uri + " is null!");
            }
        }
    }

    /**
     * Discard notebook - in this case discarding means removing notebook from
     * the folder (both in model and folder resource). BUT note that notebook
     * doesn't have to be discarded itself (since in principle it can be shared
     * by multiple folders). If there is a folder referencing such notebook,
     * then notebook is not discarded. But notebook may also become orphan, in
     * this case it is discarded automatically.
     *
     * @param labelUri
     *            the folder uri
     * @param outlineUri
     *            the notebook uri
     * @throws Exception
     *             a generic exception
     */
    public void discardOutlineFromLabel(String labelUri, String outlineUri, boolean discard) throws Exception {
        logger.debug("Discarding outline from the particular label: "+labelUri+" # "+outlineUri);
        if (labelUri != null && outlineUri != null) {
            FolderResource folderResource = new FolderResource(get(labelUri));
            folderResource.removeNotebook(outlineUri);
            save(folderResource.getResource());

            // mark notebook as discarded in the model and move it to the
            // discarded notebooks
            com.hp.hpl.jena.rdf.model.Resource folderRdfResource = labelsModel.getResource(labelUri);
            com.hp.hpl.jena.rdf.model.Resource notebookRdfResource = labelsModel.getResource(outlineUri);

            // remove notebook from folder
            Seq folderSequence = labelsModel.getModel().getSeq(folderRdfResource);
            int idx = folderSequence.indexOf(notebookRdfResource);
            if (idx >= 0) {
                folderSequence.remove(idx);
            }

            if(discard) {
                markNotebookAsDiscarded(outlineUri, notebookRdfResource);
            }

            // will be discarded only if empty
            discardLabel(labelUri);
            
            // save model
            labelsModel.save();
        }
    }
    
    private void markNotebookAsDiscarded(String notebookUri, com.hp.hpl.jena.rdf.model.Resource notebookRdfResource) throws Exception {
        // mark notebook resource as discarded
        Resource notebookResource = MindRaider.outlineCustodian.get(notebookUri);
        if (notebookResource != null) {
            new OutlineResource(notebookResource).discard();
        }
        // mark notebook as discarded
        notebookRdfResource.addProperty(MindRaiderVocabulary.isDiscarded, true);
        // add to the discarded sequence
        Seq discardedNotebooksFolder = labelsModel.getModel().getSeq(LABEL_TRASH_URI);
        discardedNotebooksFolder.add(notebookRdfResource);
    }

    /**
     * Restore notebook - move it from the trash folder back to another folder.
     *
     * @param folderUri
     *            the folder uri
     * @param notebookUri
     *            the notebook uri
     */
    public void restoreNotebook(String folderUri, String notebookUri) {
        if (folderUri != null && notebookUri != null) {
            com.hp.hpl.jena.rdf.model.Resource notebookRdfResource = labelsModel.getResource(notebookUri);

            // remove discarded flag
            notebookRdfResource.removeAll(MindRaiderVocabulary.isDiscarded);
            // remove it from trash
            Seq discardedNotebooksFolder = labelsModel.getModel().getSeq(LABEL_TRASH_URI);
            int idx = discardedNotebooksFolder.indexOf(notebookRdfResource);
            if (idx > 0) {
                discardedNotebooksFolder.remove(idx);
            }
            // put it the target folder
            Seq folderRdfResource = labelsModel.getModel().getSeq(folderUri);
            folderRdfResource.add(notebookRdfResource);

            labelsModel.save();
        }
    }

    /**
     * Discard notebook by removing it from folders (labels) to which the folder belongs.
     * Then move the notebook to the "discarded" folder
     *
     * @param notebookUri
     *            the notebook uri
     * @throws Exception
     *             a generic exception
     */
    public void discardOutline(String notebookUri) {
        logger.debug("Discard notebook from ALL folders: "+notebookUri); // {{debug}}
        if (notebookUri != null) {
            try {
                // find ALL folders pointing to this notebook
                com.hp.hpl.jena.rdf.model.Resource notebookRdfResource = labelsModel.getResource(notebookUri);

                // in the folders model, there are metadata just about folders i.e. there is just URI of the notebook, thus
                // to notebook resource in the RDF model, there are just incomming triplets (bag) from the folder URIs
                // therefore notebook's folders might simply obtained by getting all the triplets having object notebook name,
                // subject is then folder
                StmtIterator statements = labelsModel.getModel().listStatements(null, null, notebookRdfResource);
                ArrayList<String> folderRdfResources=new ArrayList<String>();
                logger.debug("removing notebook from the following folders on the filesystem:"); // {{debug}}
                while (statements.hasNext()) {
                    Statement stmt=(Statement)statements.next();
                    String folderUri=stmt.getSubject().getURI();
                    logger.debug("   "+folderUri); // {{debug}}

                    // save the resource for later processing
                    folderRdfResources.add(folderUri);

                    // remove notebook from the folder resource on the filesystem
                    FolderResource folderResource = new FolderResource(get(folderUri));
                    folderResource.removeNotebook(notebookUri);
                    save(folderResource.getResource());
                }

                logger.debug("Removing notebook from the following folders in RDF Model:"); // {{debug}}
                for (int i = 0; i < folderRdfResources.size(); i++) {
                    logger.debug("   "+folderRdfResources.get(i)); // {{debug}}
                    // remove notebook from folder in the RDF model
                    Seq folderSequence = labelsModel.getModel().getSeq(labelsModel.getResource(folderRdfResources.get(i)));
                    int idx = folderSequence.indexOf(notebookRdfResource);
                    if (idx >= 0) {
                        folderSequence.remove(idx);
                    }                    
                }

                // mark notebook as discarded in the folder model
                markNotebookAsDiscarded(notebookUri, notebookRdfResource);
                
                // deletes are occassional - let me do sanity check and delete all the empty folders
                String[] folderUris = getFolderUris();
                for (String folderUri : folderUris) {
                    discardLabel(folderUri);
                }
                
                labelsModel.save();                
            } catch(Exception e) {
                logger.error("Unable to discard notebook "+notebookUri,e);
            }
        }
    }

    /**
     * Subscribe for custodian events.
     *
     * @param listener
     *            subscriber.
     */
    public void subscribe(LabelCustodianListener listener) {
        subscribers.add(listener);
    }

    public Model getRdfModel() {
        return labelsModel.getModel();
    }
    
    public void saveRdfModel() {
        labelsModel.save();
    }
    
    /**
     * Return model as String.
     *
     * @return Returns the model as String.
     */
    public String getModelAsString() {
        StringWriter result = new StringWriter();
        labelsModel.getModel().write(result);
        return result.toString();
    }

    /**
     * Initialize folder model.
     *
     * @param mindMapResource
     *            the mind map resource.
     */
    private void initializeFoldersModel(com.hp.hpl.jena.rdf.model.Resource mindMapResource) {
        ProgressDialogJFrame progressDialogJFrame = new ProgressDialogJFrame("Folder/Notebook Model Builder",
                "<html>&nbsp;&nbsp;<b>Upgrading resource:</b>&nbsp;&nbsp;</html>");

        try {
            File f = new File(labelsDirectory);
            File[] s = f.listFiles();

            // mind map is bag
            mindMapResource.addProperty(RDF.type, RDF.Seq);
            Seq mindMapSequence = labelsModel.getModel().getSeq(mindMapResource.getURI());
            com.hp.hpl.jena.rdf.model.Resource folderRdfResource, folderType, notebookType;
            Seq folderSequence;
            FolderResource folder;
            String[] notebooks;
            String notebookPath;
            folderType = (com.hp.hpl.jena.rdf.model.Resource) labelsModel.newResource(
                    MindRaiderConstants.MR_OWL_CLASS_FOLDER, false);
            notebookType = (com.hp.hpl.jena.rdf.model.Resource) labelsModel.newResource(
                    MindRaiderConstants.MR_OWL_CLASS_NOTEBOOK, false);

            // create 'Notebook Trash'
            create("Notebook Trash", LABEL_TRASH_URI);

            // scan file system
            if (s != null) {
                Resource fResource, nResource;
                for (int i = 0; i < s.length; i++) {
                    if (s[i].isDirectory()) {
                        try {
                            String folderResourcePath = s[i].getAbsolutePath() + File.separator + FILENAME_XML_RESOURCE;
                            fResource = new Resource(folderResourcePath);

                            progressDialogJFrame.setProgressMessage(fResource.getMetadata().getUri().toString());

                            // add folder to the model
                            folderRdfResource = addFolderToModel(mindMapSequence, folderType, fResource,
                                    folderResourcePath);

                            // add folder's notebooks to the model (folder is
                            // also sequence)
                            folderSequence = labelsModel.getModel().getSeq(folderRdfResource);
                            // add all the notebooks to the model
                            folder = new FolderResource(fResource);
                            if ((notebooks = folder.getNotebookUris()) != null) {
                                for (int j = 0; j < notebooks.length; j++) {
                                    // load notebook
                                    nResource = MindRaider.outlineCustodian.get(notebooks[j]);

                                    progressDialogJFrame
                                            .setProgressMessage(nResource.getMetadata().getUri().toString());

                                    if (nResource != null) {
                                        notebookPath = MindRaider.outlineCustodian.getResourceFilename(notebooks[j]);
                                        addOutlineToModel(folderSequence, notebookType, nResource, notebookPath);
                                    }
                                }
                            }

                            // now folder is regular part of the model (so I can
                            // use standard methods)
                            progressDialogJFrame
                                    .setActionMessage("<html>&nbsp;&nbsp;<b>Verifying resource:</b>&nbsp;&nbsp;</html>");

                            // cope with discarded folders
                            if (fResource.getMetadata().isDiscarded()) {
                                // if folder is not empty, then mark it's
                                // notebooks as discarded
                                // and delete the folder (from the file system)
                                String[] uris = folder.getNotebookUris();
                                if (uris != null) {
                                    for (int j = 0; j < uris.length; j++) {
                                        progressDialogJFrame.setProgressMessage(uris[j]);

                                        discardOutlineFromLabel(fResource.getMetadata().getUri().toString(), uris[j], true);
                                    }
                                }
                            }

                        } catch (Exception e) {
                            logger.debug("Unable to load Folder: ", e);
                            continue;
                        }
                    }
                }

                progressDialogJFrame.setActionMessage("<html>&nbsp;&nbsp;<b>Orphan detection:</b>&nbsp;&nbsp;</html>");

                // now all discarded folders are deleted; discarded notebooks
                // are not referenced from OK folders, but
                // there can be
                // some notebook orphans...
                // ... so review all existing notebooks
                String[] notebookUris = MindRaider.outlineCustodian.fsGetNotebooksUris();
                if (notebookUris != null && notebookUris.length > 0) {
                    for (int j = 0; j < notebookUris.length; j++) {
                        progressDialogJFrame.setProgressMessage(notebookUris[j]);

                        // if discarded notebook not in trash folder, then add
                        // it there
                        Resource resource = MindRaider.outlineCustodian.get(notebookUris[j]);
                        if (resource != null) {
                            if (resource.getMetadata().isDiscarded()) {
                                if (!isInTrashNotebook(notebookUris[j])) {
                                    // add it there
                                    initializeFoldersModelHelper(notebookUris, j, resource);
                                }
                            } else {
                                // if it's not discarded, it must be present in
                                // some existing folder - check it
                                if (!exists(notebookUris[j])) {
                                    // it exists only on the file system -
                                    // insert it into the trash
                                    OutlineResource notebookResource = new OutlineResource(resource);
                                    try {
                                        notebookResource.discard();
                                    } catch (Exception e) {
                                        //
                                    }
                                    initializeFoldersModelHelper(notebookUris, j, resource);
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            if (progressDialogJFrame != null) {
                progressDialogJFrame.dispose();
                progressDialogJFrame = null;
            }
            // save the model once again
            labelsModel.save();
        }
    }

    /**
     * Initialize folders model helper.
     *
     * @param notebookUris
     * @param j
     * @param resource
     */
    private void initializeFoldersModelHelper(String[] notebookUris, int j, Resource resource) {
        String notebookPath;
        com.hp.hpl.jena.rdf.model.Resource notebookRdfResource = labelsModel.getResource(notebookUris[j]);
        notebookRdfResource.addProperty(MindRaiderVocabulary.isDiscarded, true);
        // rdfs:label - specify label (it's not in the model)
        notebookRdfResource.addProperty(RDFS.label, new OutlineResource(resource).getLabel());
        // xlink:href - specify relative location (along with profile's
        // repository home it allows to construct full path
        notebookPath = MindRaider.outlineCustodian.getModelFilenameByDirectory(MindRaider.outlineCustodian
                .getOutlineDirectory(notebookUris[j]));
        notebookRdfResource.addProperty(MindRaiderVocabulary.xlinkHref, MindRaider.profile
                .getRelativePath(notebookPath));
        // add to the discarded sequence
        Seq discardedNotebooksFolder = labelsModel.getModel().getSeq(LABEL_TRASH_URI);
        discardedNotebooksFolder.add(notebookRdfResource);
    }

    /**
     * Add notebook to the model.
     *
     * @param folderSequence
     *            the folder sequence.
     * @param notebookType
     *            the notebook type resource.
     * @param nResource
     *            the resource.
     * @param notebookPath
     *            the notebook path String.
     * @return Returns the added resource.
     */
    private com.hp.hpl.jena.rdf.model.Resource addOutlineToModel(Seq folderSequence,
            com.hp.hpl.jena.rdf.model.Resource notebookType, Resource nResource, String notebookPath) {
        com.hp.hpl.jena.rdf.model.Resource notebookRdfResource = (com.hp.hpl.jena.rdf.model.Resource) labelsModel
                .newResource(nResource.getMetadata().getUri().toString(), false);
        // rdf:type - it's notebook
        notebookRdfResource.addProperty(RDF.type, notebookType);
        // xlink:href - specify relative location (along with profile's
        // repository home it allows to construct full path
        notebookRdfResource.addProperty(MindRaiderVocabulary.xlinkHref, MindRaider.profile
                .getRelativePath(notebookPath));
        // rdfs:label
        notebookRdfResource.addProperty(RDFS.label, new OutlineResource(nResource).getLabel());
        // add notebook to the folder's sequence
        folderSequence.add(notebookRdfResource);

        return notebookRdfResource;
    }

    /**
     * Add folder to the model.
     *
     * @param mindMapSequence
     *            the mind map sequence.
     * @param folderType
     *            the folder type.
     * @param fResource
     *            the resource.
     * @param folderResourcePath
     *            the folder resource path String
     * @return the resource
     */
    private com.hp.hpl.jena.rdf.model.Resource addFolderToModel(
            Seq mindMapSequence,
            com.hp.hpl.jena.rdf.model.Resource folderType, 
            Resource fResource, 
            String folderResourcePath) {
        
        com.hp.hpl.jena.rdf.model.Resource folderRdfResource;
        folderRdfResource 
            = (com.hp.hpl.jena.rdf.model.Resource) labelsModel.newResource(fResource.getMetadata().getUri().toString(), false);
        // rdf:type - it's sequence
        folderRdfResource.addProperty(RDF.type, RDF.Seq);
        // rdf:type - it's folder
        folderRdfResource.addProperty(RDF.type, folderType);
        // xlink:href - specify relative location (along with profile's
        // repository home it allows to construct full path
        folderRdfResource.addProperty(MindRaiderVocabulary.xlinkHref, MindRaider.profile.getRelativePath(folderResourcePath));
        // rdfs:label
        folderRdfResource.addProperty(RDFS.label, new FolderResource(fResource).getLabel());
        // add folder to the parent sequence
        mindMapSequence.add(folderRdfResource);
        return folderRdfResource;
    }

    /**
     * Get folder location from the model.
     *
     * @param resourceUri
     *            the resource uri
     * @return the flder or notebook location String
     */
    public String getFolderOrNotebookLocationFromModel(String resourceUri) {
        StmtIterator i = labelsModel.getModel().listStatements(labelsModel.getResource(resourceUri),
                MindRaiderVocabulary.xlinkHref, (RDFNode) null);
        if (i.hasNext()) {
            return i.nextStatement().getObject().toString();
        }
        return null;
    }

    /**
     * Get notebook location from the model.
     *
     * @param notebookUri
     *            the nodebook uri String
     * @return the notebook location String
     */
    private String getNotebookLocationFromModel(String notebookUri) {
        StmtIterator i = labelsModel.getModel().listStatements(labelsModel.getResource(notebookUri),
                MindRaiderVocabulary.xlinkHref, (RDFNode) null);
        if (i.hasNext()) {
            return i.nextStatement().getObject().toString();
        }
        return null;
    }
}
