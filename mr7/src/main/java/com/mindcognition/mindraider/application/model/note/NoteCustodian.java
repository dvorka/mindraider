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
package com.mindcognition.mindraider.application.model.note;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.core.rdf.RdfModel;
import com.emental.mindraider.core.rest.Data;
import com.emental.mindraider.core.rest.Metadata;
import com.emental.mindraider.core.rest.Resource;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.core.rest.properties.AnnotationProperty;
import com.emental.mindraider.core.rest.properties.AttachmentProperty;
import com.emental.mindraider.core.rest.properties.LabelProperty;
import com.emental.mindraider.core.rest.properties.NotebookProperty;
import com.emental.mindraider.core.rest.properties.OriginProperty;
import com.emental.mindraider.core.rest.properties.ResourcePropertyGroup;
import com.emental.mindraider.core.rest.resource.AttachmentResource;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.emental.mindraider.core.rest.resource.OutlineResource;
import com.emental.mindraider.core.search.SearchCommander;
import com.emental.mindraider.ui.outline.treetable.OutlineTreeInstance;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.mindcognition.mindraider.MindRaiderException;
import com.mindcognition.mindraider.application.model.outline.OutlineCustodian;
import com.mindcognition.mindraider.application.model.tag.TagEntry;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.tools.GlobalIdGenerator;
import com.mindcognition.mindraider.ui.swing.concept.ClassifierFlagRenderer;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;
import com.mindcognition.mindraider.utils.Utils;

/**
 * Concept custodian. Concept is a resource belonging to exactly one notebook.
 * Concept is represented as XML resource only. It's URI, label, creation time
 * and annotation snippet is propagated to the Notebook's model.
 */
public class NoteCustodian {
    
    /**
     * The move up constant.
     */
    public static final int MOVE_UP = 0;

    /**
     * The move down constant.
     */
    public static final int MOVE_DOWN = 1;

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(NoteCustodian.class);

    /**
     * Notebooks location.
     */
    private String notebooksDirectory;

    /**
     * Subscribers array list.
     */
    private ArrayList<NoteCustodianListener> subscribers;

    private NoteTemplates noteTemplates;
    
    /**
     * Trash of concepts in the current notebook.
     */
    public static String NOTEBOOK_TRASH_LOCAL_NAME = "conceptTrash";

    /**
     * Constructor.
     * 
     * @param notebooksDirectory
     *            the notebook directory.
     */
    public NoteCustodian(String notebooksDirectory) {
        this.notebooksDirectory = notebooksDirectory;
        
        noteTemplates=new NoteTemplates();
        subscribers = new ArrayList<NoteCustodianListener>();
    }

    /**
     * Create concept.
     * 
     * @param notebook
     * @param parentConceptUri
     * @param label
     * @param conceptUri
     * @param annotation
     * @param renderUi
     * @return
     * @throws Exception
     * @deprecated annotation content type must be specified.
     */
    public String create(OutlineResource notebook, String parentConceptUri,
            String label, String conceptUri, String annotation, boolean renderUi)
            throws Exception {
        return create(notebook,parentConceptUri,label,conceptUri,annotation,renderUi,null);
    }

    public String create(OutlineResource notebook, String parentConceptUri,
            String label, String conceptUri, String annotation, boolean renderUi, String contentTypeOwlClass)
            throws Exception {
        return create(notebook, parentConceptUri, label, conceptUri, annotation, renderUi, contentTypeOwlClass,
                null, null, null, null);
    }    
    /**
     * Create concept.
     * 
     * @param notebook
     *            the notebook resource
     * @param parentConceptUri
     *            can be <code>null</code>, in this case parent is Notebook
     *            resource itself.
     * @param label
     *            the label
     * @param conceptUri
     *            the concept uri
     * @param annotation
     *            the annotation
     * @param renderUi
     *            flag to indicate if user interface should be rendered.
     * @return Returns the concept string.
     * @throws Exception
     *             a generic exception
     */
    public String create(
            OutlineResource notebook, 
            String parentConceptUri,
            String label, 
            String conceptUri, 
            String annotation, 
            boolean renderUi, 
            String contentTypeOwlClass,
            String commaSeparatedTags, 
            String categoryTitle,
            String template,
            AttachmentResource[] attachments) throws Exception {
        
        if (notebook == null) {
            String errorMessage = Messages
                    .getString("ConceptCustodian.unableToCreateNewConcept");
            logger.debug(errorMessage);
            throw new MindRaiderException(errorMessage);
        }

        URI notebookUri = notebook.resource.getMetadata().getUri();

        logger.debug(Messages.getString("ConceptCustodian.creatingNewConcept",
                new Object[] { conceptUri, notebookUri }));

        String conceptFilename = getConceptResourceFilename(notebookUri.toString(), conceptUri);

        // check whether the file/URI exists - if so, then rename
        if (new File(conceptFilename).exists()) {
            return MindRaiderConstants.EXISTS;
        }

        // concept resource
        Resource resource = new Resource();
        Metadata meta = resource.getMetadata();
        meta.setAuthor(new URI(MindRaider.profile.getProfileName()));
        meta.setCreated(System.currentTimeMillis());
        meta.setRevision(1);
        meta.setTimestamp(meta.getCreated());
        meta.setUri(new URI(conceptUri));
        resource.setMetadata(meta);

        // data properties
        resource.addProperty(new LabelProperty(label));
        // annotation
        resource.addProperty(new AnnotationProperty(annotation));
        // enclosing notebook
        resource.addProperty(new NotebookProperty(notebookUri));
        // origin URI
        if(notebook.getOriginUri()!=null) {
            resource.addProperty(new OriginProperty(GlobalIdGenerator.generateNoteId(notebook.getOriginUri())));            
        }

        // concept resource
        ConceptResource conceptResource = new ConceptResource(resource);
        // content type
        if(contentTypeOwlClass!=null) {
            conceptResource.setAnnotationContentType(contentTypeOwlClass);
        }

        // template
        if(template!=null && template.length()>0 && !NoteTemplates.TEMPLATE_NOTE_NONE.equals(template)) {
            String templateLabel=noteTemplates.getTitleForTemplateLabel(template, label);
            String templateAnnotation=noteTemplates.getAnnotationForTemplateLabel(template, annotation);
            conceptResource.setLabel(templateLabel);
            conceptResource.setAnnotation(templateAnnotation);            
        }
        
        if(attachments!=null && attachments.length>0) {
            for(AttachmentResource attachmentResource: attachments) {
                addAttachment(conceptResource, attachmentResource, false);
            }
        }

        // set categories first because the method DELETES all category properties
        
        // category
        addCategoryToCategoryProperties(categoryTitle, conceptResource);
                
        // tags
        if(commaSeparatedTags!=null && commaSeparatedTags.length()>0) {
            // register the categories that do not exist
            TagEntry[] registerCategories = MindRaider.tagCustodian.registerCategories(
                    commaSeparatedTags, 
                    conceptResource.getNotebookUri(), 
                    conceptResource.getUri(), 
                    conceptResource.getLabel(), 
                    resource.metadata.getTimestamp());
            // add categories to the concept resource itself
            if(registerCategories!=null && registerCategories.length>0) {
                for (int i = 0; i < registerCategories.length; i++) {
                    conceptResource.addCategory(
                            registerCategories[i].getTagLabel(), 
                            registerCategories[i].getTagUri());
                }
            }
        }
                
        // empty attachments group
        Data data = resource.getData();
        data.addPropertyGroup(new ResourcePropertyGroup(
                ConceptResource.PROPERTY_GROUP_LABEL_ATTACHMENTS, new URI(
                        ConceptResource.PROPERTY_GROUP_URI_ATTACHMENTS)));
        resource.setData(data);
        
        // save the resource
        resource.toXmlFile(conceptFilename);
        
        // tag snail refresh
        MindRaider.tagCustodian.redraw();
        MindRaider.tagCustodian.toRdf();

        // spiders graph must be updated:
        // o parent concept and other relationships must be updated
        // o model must be saved
        if (MindRaider.spidersGraph != null
                && MindRaider.spidersGraph.getRdfModel() != null) {            
            
            if(contentTypeOwlClass==null) {
                contentTypeOwlClass=MindRaiderConstants.MR_OWL_CONTENT_TYPE_PLAIN_TEXT;
            }
            conceptResource.setAnnotationContentType(contentTypeOwlClass);
            
            RdfModel rdfModel = MindRaider.spidersGraph.getRdfModel();
            com.hp.hpl.jena.rdf.model.Resource parentResource = getParentResource(
                    notebook.resource.getMetadata().getUri().toString(),
                    parentConceptUri, rdfModel);

            if (parentResource == null) {
                logger.debug(Messages.getString(
                        "ConceptCustodian.parentResourceIsNull",
                        parentConceptUri));
            } else {
                // state that new node is type of sequence
                com.hp.hpl.jena.rdf.model.Resource conceptRdf 
                    = rdfModel.getModel().createResource(conceptUri);
                Model oldModel = rdfModel.getModel();

                // * rdfs:type
                conceptRdf.addProperty(RDF.type, RDF.Seq);
                // * MR type
                conceptRdf
                        .addProperty(
                                RDF.type,
                                oldModel
                                        .createResource(MindRaiderConstants.MR_OWL_CLASS_CONCEPT));
                // * rdfs:label
                conceptRdf.addProperty(RDFS.label, oldModel
                        .createLiteral(conceptResource.getLabel()));
                // * dc:created
                conceptRdf.addProperty(DC.date, oldModel
                        .createLiteral(conceptResource.resource.getMetadata()
                                .getCreated()));
                // * rdfs:comment (annotation snippet)
                conceptRdf.addProperty(RDFS.comment, oldModel
                        .createLiteral(OutlineTreeInstance
                                .getAnnotationCite(conceptResource
                                        .getAnnotation())));
                // * xlink:href
                conceptRdf.addProperty(MindRaiderVocabulary.xlinkHref,
                        MindRaider.profile
                                .getRelativePath(MindRaider.noteCustodian
                                        .getConceptResourceFilename(notebookUri
                                                .toString(), conceptUri)));

                Seq seq = rdfModel.getModel().getSeq(parentResource);
                seq.add(conceptRdf);

                MindRaider.spidersGraph.save();
                if (renderUi) {
                    MindRaider.spidersGraph.renderModel();
                }
            }
        } else {

            logger.error(Messages
                    .getString("ConceptCustodian.spidersGraphAreNull"));
        }

        // @todo create DC annotation

        // add concept to the notebook
        // IMPORTANT: parent child relationships of concepts are captured within
        // the RDF model only
        notebook.addConcept(conceptUri);
        notebook.save();

        // notify subscribers
        for (NoteCustodianListener subscriber : subscribers) {
            if (renderUi) {
                subscriber.conceptCreated(new ConceptResource(resource));
            }
        }

        return conceptUri;
    }

    public void addCategoryToCategoryProperties(String categoryTitle, ConceptResource conceptResource) {
        if(categoryTitle!=null) {
            String categoryUri
                =(String)ClassifierFlagRenderer.comboLabel2OwlClassMap.get(categoryTitle);
            setCategorizationFlag(conceptResource, categoryTitle, categoryUri);
        }
    }

    /**
     * Save concept.
     * 
     * @param noteResource
     *            the concept resource to save.
     * @throws Exception
     *             a generic exception.
     */
    public void save(ConceptResource conceptResource) throws Exception {
        if (conceptResource != null) {
            // build path to concept file
            final String conceptUri = conceptResource.resource.metadata.uri.toString();
            String conceptFilename = getConceptResourceFilename(conceptResource.getNotebookUri(), conceptUri);
            conceptResource.resource.toXmlFile(conceptFilename);

            // update search index
            String notebookLabel=MindRaider.outlineCustodian.getActiveNotebookLabel();
            if(notebookLabel==null) {
                notebookLabel=conceptResource.getNotebookUri();
            }
            SearchCommander.updateIndex(new File(conceptFilename), notebookLabel, conceptResource.getLabel(), conceptUri);
            
            // update label and annotation in the notebook model
            if (MindRaider.spidersGraph != null
                    && MindRaider.spidersGraph.getRdfModel() != null) {
                Model model = MindRaider.spidersGraph.getRdfModel().getModel();
                com.hp.hpl.jena.rdf.model.Resource conceptRdfResource = model
                        .getResource(conceptUri);
                conceptRdfResource.removeAll(RDFS.label);
                conceptRdfResource.addProperty(RDFS.label, (conceptResource
                        .getLabel() != null ? conceptResource.getLabel() : ""));
                conceptRdfResource.removeAll(RDFS.comment);
                conceptRdfResource.addProperty(RDFS.comment,
                        OutlineTreeInstance
                                .getAnnotationCite(conceptResource
                                        .getAnnotation()));
                MindRaider.spidersGraph.save();
                //System.out.println("PROFILER spiders graph save : "+(System.currentTimeMillis()-profileSpiderStart)+"(ms)");        
            }
        }
        //System.out.println("PROFILER concept custodian save : "+(System.currentTimeMillis()-profileStart)+"(ms)");        
    }

    /**
     * Save concept resource and update metadata in the given model.
     * 
     * @param noteResource
     *            the concept resource to save.
     * @param model
     *            the metadata model.
     */
    public void save(ConceptResource conceptResource, Model model) {
        if (conceptResource == null || model == null) {
            return;
        }

        URI conceptResourceUri = conceptResource.resource.getMetadata()
                .getUri();
        // build path
        String conceptFilename = getConceptResourceFilename(conceptResource
                .getNotebookUri(), conceptResourceUri.toString());
        conceptResource.resource.toXmlFile(conceptFilename);

        com.hp.hpl.jena.rdf.model.Resource conceptRdfResource = model
                .getResource(conceptResourceUri.toString());
        conceptRdfResource.removeAll(RDFS.label);
        conceptRdfResource.addProperty(RDFS.label,
                (conceptResource.getLabel() != null ? conceptResource
                        .getLabel() : ""));
        conceptRdfResource.removeAll(RDFS.comment);
        conceptRdfResource.addProperty(RDFS.comment,
                OutlineTreeInstance.getAnnotationCite(conceptResource
                        .getAnnotation()));

    }

    /**
     * Get concept by URI.
     * 
     * @param outlineUri
     *            the notebook uri.
     * @param conceptUri
     *            the concept uri.
     * @return Returns the concept resource.
     * @throws Exception
     *             a generic exception.
     */
    public ConceptResource get(String outlineUri, String conceptUri)
            throws Exception {
        
        if (outlineUri != null && conceptUri != null
                && MindRaiderVocabulary.isConceptUri(conceptUri)) {
            
            String conceptFilename 
                = getConceptResourceFilename(outlineUri,conceptUri);
            Resource resource = new Resource(conceptFilename);
            
            // TODO if the filesize==0 or unable to parse, throw lower exception
            // catch it here and attempt to build ConceptResource from RDF model
            // ... a partial fix
            
            return new ConceptResource(resource);
        }
        
        throw new MindRaiderException("Concept/notebook URI null or not a concept!");
    }

    /**
     * Get concept path.
     * 
     * @param outlineUri
     *            the notebook uri.
     * @param conceptUri
     *            the concept uri.
     * @return Returns the concept resource filename.
     */
    public String getConceptResourceFilename(String outlineUri,
            String conceptUri) {
        if (outlineUri == null || conceptUri == null) {
            return null;
        }

        String notebookNcName = Utils.getNcNameFromUri(outlineUri);
        String conceptFilename = notebooksDirectory + File.separator
                + notebookNcName + File.separator
                + OutlineCustodian.DIRECTORY_CONCEPTS + File.separator
                + Utils.getNcNameFromUri(conceptUri) + ".xml";
        return conceptFilename;
    }

    /**
     * Get directory where to save/load other annotations like Jarnal.
     * 
     * @param notebookUri
     *            notebook URI.
     * @param conceptUri
     *            concept URI.
     * @return directory where to save/load other annotation like Jarnal.
     */
    public String getConceptResourceAnnotationsDirectoryName(
            String notebookUri, String conceptUri) {
        if (notebookUri != null && conceptUri != null) {
            String notebookNcName = Utils.getNcNameFromUri(notebookUri);
            String conceptFilename = notebooksDirectory + File.separator
                    + notebookNcName + File.separator
                    + OutlineCustodian.DIRECTORY_ANNOTATIONS + File.separator;
            return conceptFilename;
        }
        return null;
    }

    /**
     * Check whether concept exits in the active notebook.
     * 
     * @param conceptUri
     *            concept URI.
     * @return Returns <code>true</code> if given concept uri exists,
     *         otherwise <code>false</code>.
     */
    public boolean exists(String conceptUri) {
        return MindRaider.outlineCustodian.conceptExists(conceptUri);
    }

    public void undiscard(String outlineUri, String conceptUri) throws Exception {
        String outlineFilename = MindRaider.outlineCustodian.getModelFilename(outlineUri);
        Model outlineModel=null;
        URI activeOutlineUri = MindRaider.profile.getActiveOutlineUri();

        if(outlineUri!=null && activeOutlineUri!=null && outlineUri.equals(activeOutlineUri.toString())) {
            // use active outline model
            RdfModel rdfModel = MindRaider.spidersGraph.getRdfModel();
            if(rdfModel!=null) {
                outlineModel = rdfModel.getModel();
            }
        } else {
            RdfModel.loadModel(outlineFilename);
        }
        
        // RDF: remove the concept from the trash sequence
        String trashUri = getTrashConceptUri(outlineUri);
        Seq trashRdfSequence = outlineModel.getSeq(trashUri);
        int indexOf = trashRdfSequence.indexOf(outlineModel.getResource(conceptUri));
        trashRdfSequence.remove(indexOf);
        
        // RDF: remove discarded property of the concept
        com.hp.hpl.jena.rdf.model.Resource conceptRdfResource = outlineModel.getResource(conceptUri);
        StmtIterator listStatements = outlineModel.listStatements(conceptRdfResource, MindRaiderVocabulary.isDiscarded, true);
        if(listStatements!=null && listStatements.hasNext()) {
            Statement statement=(Statement)listStatements.next();
            statement.remove();
        }
        
        // RDF: get the root resource ~ outline URI
        Seq outlineRdfResource = outlineModel.getSeq(outlineUri);
        
        // RDF: add the concept's URI to outline's resource sequence
        outlineRdfResource.add(conceptRdfResource);

        // RDF save it back
        RdfModel.saveModel(outlineModel, outlineFilename);        
        
        // XML: set the parentResource element in the concept's XML
        ConceptResource conceptResource = get(outlineUri, conceptUri);
        conceptResource.save();
    }
    
    /**
     * Discard the concept - mark it in its internals as discarded and remove it
     * from the graph.
     * 
     * @param outlineUri
     *            URI of the parent concept (it must be RDF sequence)
     * @param conceptUri
     *            concept to be discarded
     * @throws Exception
     *             a generic exception.
     */
    public void discard(String outlineUri, String parentConceptUri, String conceptUri) throws Exception {
        if (outlineUri == null || parentConceptUri == null
                || conceptUri == null) {
            logger.debug(Messages.getString(
                    "ConceptCustodian.aConceptElementIsNull", new Object[] {
                            outlineUri, parentConceptUri, conceptUri }));
            return;
        }

        // mark as deleted in the REST resource
        ConceptResource conceptResource = get(outlineUri, conceptUri);
        Metadata meta = conceptResource.resource.getMetadata();
        meta.setDiscarded(true);
        conceptResource.resource.setMetadata(meta);
        save(conceptResource);

        // remove it from RDF graph
        if (MindRaider.spidersGraph != null) {
            // deleting concept from parent sequence
            RdfModel rdfModel = MindRaider.spidersGraph.getRdfModel();

            // get parent's parent and move the node there
            com.hp.hpl.jena.rdf.model.Resource parentResource = getParentResource(
                    outlineUri, parentConceptUri, rdfModel);

            if (parentResource == null) {
                logger.debug(Messages
                        .getString("ConceptCustodian.unableToFindRDFModel"));
            } else {
                Model model = rdfModel.getModel();
                Seq parentSeq = model.getSeq(parentResource);
                com.hp.hpl.jena.rdf.model.Resource conceptRdfResource = model
                        .getResource(conceptUri);
                int conceptIndex = parentSeq.indexOf(conceptRdfResource);

                logger.debug(Messages.getString(
                        "ConceptCustodian.discardConceptIndexSequence",
                        conceptIndex));
                if (conceptIndex > 0) {
                    parentSeq.remove(conceptIndex);

                    // move it to the trash notebook
                    String trashUri = getTrashConceptUri(outlineUri);

                    Seq trashRdf = model.getSeq(trashUri);
                    trashRdf.add(conceptRdfResource);
                    // mark notebook as discarded
                    conceptRdfResource.addProperty(MindRaiderVocabulary.isDiscarded, true);

                    MindRaider.spidersGraph.save();
                    logger.debug(Messages.getString(
                            "ConceptCustodian.conceptDeleted", conceptUri));
                } else {
                    logger
                            .debug(Messages
                                    .getString("ConceptCustodian.conceptNotFoundInSequence"));
                }
            }

        } else {
            logger.debug(Messages
                    .getString("ConceptCustodian.spiderGraphsAreNull"));
        }
    }
    
    public void deleteDiscardedConcepts(String outlineUri) {
        // get discarded concepts
        ResourceDescriptor[] discardedConceptDescriptors 
            = MindRaider.outlineCustodian.getDiscardedConceptDescriptors(outlineUri);
        
        if(discardedConceptDescriptors!=null && discardedConceptDescriptors.length>0) {
            for (int i = 0; i < discardedConceptDescriptors.length; i++) {
                deleteConcept(outlineUri, discardedConceptDescriptors[i].getUri());
            }
        }
        
    }    

    /**
     * Delete already discarded concept regardless in which outline it is (active or non-active).
     */
    public void deleteConcept(String outlineUri, String conceptUri) {
        logger.debug("deleteConcept() "+outlineUri+"   "+conceptUri); // {{debug}}
        // if the outline is active, then use active model (otherwise memory vs. filesystem would be inconsistent)

        StatusBar.show("Deleting concept "+conceptUri+"...");
        
        String outlineFilename = MindRaider.outlineCustodian.getModelFilename(outlineUri);
        // load outline RDF Model
        Model outlineModel=null;
        URI activeOutlineUri = MindRaider.profile.getActiveOutlineUri();
        boolean deletingFromActiveOutline=false;
        if(outlineUri!=null && activeOutlineUri!=null && outlineUri.equals(activeOutlineUri.toString())) {
            deletingFromActiveOutline=true;
            // use active outline model
            RdfModel rdfModel = MindRaider.spidersGraph.getRdfModel();
            if(rdfModel!=null) {
                outlineModel = rdfModel.getModel();
            }
        } else {
            // load the model from the file system
            outlineModel=RdfModel.loadModel(outlineFilename);
        }
        
        // RDF Model: remove concept from the trash sequence
        String trashUri = getTrashConceptUri(outlineUri);
        Seq trashRdfSequence = outlineModel.getSeq(trashUri);
        int indexOf = trashRdfSequence.indexOf(outlineModel.getResource(conceptUri));
        trashRdfSequence.remove(indexOf);

        // RDF Model: remove all triplets where subject is the concept
        outlineModel.getResource(conceptUri).removeProperties();

        // RDF Model: save it back
        RdfModel.saveModel(outlineModel, outlineFilename);        
        
        // outline XML: remove concept property
        OutlineResource outlineXmlResource;
        if(deletingFromActiveOutline) {
            outlineXmlResource = MindRaider.outlineCustodian.getActiveOutlineResource();
        } else {
            outlineXmlResource=new OutlineResource(MindRaider.outlineCustodian.get(outlineUri));
        }
        try {
            outlineXmlResource.removeConcept(conceptUri);
            outlineXmlResource.save();
        } catch (Exception e) {
            logger.error("Unable to remove XML resource for concept "+conceptUri,e); // {{debug}}
        }
        
        // concept XML: delete the file
        String conceptResourceFilename = getConceptResourceFilename(outlineUri, conceptUri);
        try {
            Utils.deleteSubtree(new File(conceptResourceFilename));
        } catch (Exception e) {
            logger.debug("Unable to delete file "+conceptResourceFilename,e); // {{debug}}
        }
    }
    
    /**
     * Get trash concept URI.
     * 
     * @param outlineUri
     *            the notebook uri.
     * @return Returns the trash concept uri.
     */
    public static String getTrashConceptUri(String outlineUri) {
        return MindRaiderVocabulary.getConceptUri(Utils.getNcNameFromUri(outlineUri), NOTEBOOK_TRASH_LOCAL_NAME);
    }

    /**
     * Subscribe for custodian events.
     * 
     * @param listener
     *            subscriber.
     */
    public void subscribe(NoteCustodianListener listener) {
        subscribers.add(listener);
    }

    /**
     * Set category (all existing categories are removed and a new is set).
     * 
     * @param concept
     *            the concept resource.
     * @param categoryTitle
     *            the caption.
     * @param categoryUri
     *            the category uri.
     */
    public void setCategorizationFlag(ConceptResource concept, String categoryTitle, String categoryUri) {
        if (concept != null) {
            concept.removeCategories();
            if (categoryUri != null) {
                // set class for active resource
                concept.addCategory(categoryTitle, categoryUri);
            }
            try {
                concept.save();
            } catch (Exception e) {
                logger.debug(Messages
                        .getString("ConceptCustodian.unableToSaveResource", e
                                .getMessage()));
            }

            // spiders graph must be updated
            if (MindRaider.spidersGraph != null
                    && MindRaider.spidersGraph.getRdfModel() != null) {
                Model model = MindRaider.spidersGraph.getRdfModel().getModel();
                com.hp.hpl.jena.rdf.model.Resource resource = model
                        .getResource(concept.resource.getMetadata().getUri()
                                .toString());
                if (resource != null) {
                    // remove it in the graph
                    resource.removeAll(MindRaiderVocabulary.flagProperty);
                    // add it in the graph
                    resource.addProperty(MindRaiderVocabulary.flagProperty,
                            model.getResource(categoryUri));
                    // TODO perhaps wrong - ns/local name separated when
                    // creating resource
                    MindRaider.spidersGraph.save();
                }
            }

        }
    }

    /**
     * Add attachment to concept.
     * 
     * @param concept
     *            the concept resource.
     * @param attachmentResource
     *            the attachment resource.
     * @throws Exception
     *             a generic exception.
     */
    public void addAttachment(ConceptResource concept, AttachmentResource attachmentResource, boolean updateUi) throws Exception {
        if (concept != null && attachmentResource != null) {
            // store attachment also to concept XML
            if (!concept.attachmentsExist()) {
                Data data = concept.resource.getData();
                data.addPropertyGroup(new ResourcePropertyGroup(
                                ConceptResource.PROPERTY_GROUP_LABEL_ATTACHMENTS,
                                new URI(ConceptResource.PROPERTY_GROUP_URI_ATTACHMENTS)));
                concept.resource.setData(data);
            }
            // add attachments there (if exists)
            concept.addAttachment(
                    attachmentResource.getDescription(),
                    attachmentResource.getUrl());
            save(concept);

            if(updateUi) {
                // spiders graph must be updated
                if (MindRaider.spidersGraph != null && MindRaider.spidersGraph.getRdfModel() != null) {
                    MindRaider.spidersGraph.createStatement(new QName(
                            MindRaiderConstants.MR_RDF_NS, "attachment"),
                            new QName(null, attachmentResource.getUrl()), true);
                }                
            }
        }
    }

    /**
     * Get attachments of a concept.
     * 
     * @param noteResource
     *            concept resource
     * @return concept's attachments.
     */
    public AttachmentResource[] getAttachments(ConceptResource conceptResource) {
        if (conceptResource != null) {
            AttachmentProperty[] properties = conceptResource.getAttachments();
            if (!ArrayUtils.isEmpty(properties)) {
                ArrayList<AttachmentResource> result = new ArrayList<AttachmentResource>();
                for (AttachmentProperty attachmentProperty : properties) {
                    result.add(new AttachmentResource(attachmentProperty
                            .getDescription(), attachmentProperty.getUrl()));
                }
                return result.toArray(new AttachmentResource[result.size()]);
            }
        }
        return null;
    }

    /**
     * Remove attachment.
     * 
     * @param notebookUri
     *            the notebook uri.
     * @param concept
     *            the concept resource.
     * @param attachmentUrl
     *            the attachment url.
     */
    public void removeAttachment(String notebookUri, ConceptResource concept,
            String attachmentUrl) {
        Model model = MindRaider.spidersGraph.getRdfModel().getModel();
        RdfModel.deleteStatementByObject(model, model
                .createLiteral(attachmentUrl));
        MindRaider.spidersGraph.save();

        // delete attachment also from concept resource
        concept.removeAttachmentByUrl(attachmentUrl);
        try {
            save(concept);
        } catch (Exception e) {
            logger.debug(Messages.getString(
                    "ConceptCustodian.unableToSaveConcept", e.getMessage()));
        }
    }

    /**
     * Move concept backward in the RDF sequence (switch with previous sibling).
     * 
     * @param notebook
     *            the notebook resource.
     * @param parentConceptUri
     *            the parent concept uri.
     * @param conceptUri
     *            the concept uri.
     * @return Returns <code>true</code> if concept is move backward,
     *         otherwise <code>false</code>.
     */
    public boolean up(OutlineResource notebook, String parentConceptUri,
            String conceptUri) {
        return moveConceptUpDown(notebook, parentConceptUri, conceptUri,
                MOVE_UP);
    }

    /**
     * Move concept forward in the RDF sequence.
     * 
     * @param notebook
     *            the notebook resource.
     * @param parentConceptUri
     *            the parent concept uri.
     * @param conceptUri
     *            the concept uri.
     * @return Returns <code>true</code> if concept is moved down, otherwise
     *         <code>false</code>.
     */
    public boolean down(OutlineResource notebook, String parentConceptUri,
            String conceptUri) {
        return moveConceptUpDown(notebook, parentConceptUri, conceptUri,
                MOVE_DOWN);
    }

    /**
     * Move concept forward in the RDF sequence.
     * 
     * @param notebook
     *            the notebook resource.
     * @param parentConceptUri
     *            the parent concept uri.
     * @param conceptUri
     *            the concept uri.
     * @param direction
     *            the direction towards move the concept.
     * @return Returns <code>true</code> if concept is moved down, otherwise
     *         <code>false</code>.
     */
    private boolean moveConceptUpDown(OutlineResource notebook,
            String parentConceptUri, String conceptUri, int direction) {
        logger.debug("=->"
                + ((direction == NoteCustodian.MOVE_DOWN) ? "DOWN: "
                        : "UP: ") + conceptUri);

        if (MindRaider.spidersGraph == null
                || MindRaider.spidersGraph.getRdfModel() == null) {
            logger.debug(Messages
                    .getString("ConceptCustodian.spidersGraphAreNull"));
        } else {
            RdfModel rdfModel = MindRaider.spidersGraph.getRdfModel();

            com.hp.hpl.jena.rdf.model.Resource parentResource = getParentResource(
                    notebook.resource.getMetadata().getUri().toString(),
                    parentConceptUri, rdfModel);

            if (parentResource == null) {
                logger.debug(Messages.getString(
                        "ConceptCustodian.unableToFindParentResource",
                        parentConceptUri));
            } else {
                if (direction == NoteCustodian.MOVE_DOWN) {
                    if (rdfModel.downInSequence(parentResource, conceptUri)) {
                        return true;
                    }
                } else {
                    if (rdfModel.upInSequence(parentResource, conceptUri)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Move concept up one level (in the outliner mode).
     * 
     * @param notebook
     *            the notebook resource.
     * @param parentParentConceptUri
     *            the parent parent concept uri.
     * @param parentConceptUri
     *            the parent concept uri.
     * @param conceptUri
     *            the concept uri
     */
    public void promote(OutlineResource notebook,
            String parentParentConceptUri, String parentConceptUri,
            String conceptUri) {
        if (MindRaider.spidersGraph != null
                && MindRaider.spidersGraph.getRdfModel() != null) {
            RdfModel rdfModel = MindRaider.spidersGraph.getRdfModel();

            // get parent's parent and move the node there
            String notebookUri = notebook.resource.getMetadata().getUri()
                    .toString();
            com.hp.hpl.jena.rdf.model.Resource parentResource = getParentResource(
                    notebookUri, parentConceptUri, rdfModel);
            com.hp.hpl.jena.rdf.model.Resource parentParentResource = getParentResource(
                    notebookUri, parentParentConceptUri, rdfModel);

            if (parentResource == null || parentParentResource == null) {
                logger
                        .debug(Messages
                                .getString("ConceptCustodian.unableToFindParentParent"));
            } else {
                Seq parentSeq = rdfModel.getModel().getSeq(parentResource);
                com.hp.hpl.jena.rdf.model.Resource conceptResource = rdfModel
                        .getModel().getResource(conceptUri);
                int conceptIndex = parentSeq.indexOf(conceptResource);
                logger.debug(Messages.getString(
                        "ConceptCustodian.promoteConceptIndex", conceptIndex));

                // move it to parent's parent (behind parent)
                Seq parentParentSeq = rdfModel.getModel().getSeq(
                        parentParentResource);
                int parentIndex = parentParentSeq.indexOf(parentResource);
                parentParentSeq.add(parentIndex + 1, conceptResource);
                parentSeq.remove(conceptIndex);

                MindRaider.spidersGraph.save();
                MindRaider.spidersGraph.renderModel();
            }
        } else {
            logger.error(Messages
                    .getString("ConceptCustodian.spidersGraphAreNull"));
        }

    }

    /**
     * Move concept down one level (in the outliner mode).
     * 
     * @param notebook
     *            the notebook resource.
     * @param parentConceptUri
     *            the parent concept uri.
     * @param conceptUri
     *            the concept uri.
     */
    public void demote(OutlineResource notebook, String parentConceptUri,
            String conceptUri) {
        if (MindRaider.spidersGraph != null
                && MindRaider.spidersGraph.getRdfModel() != null) {
            RdfModel rdfModel = MindRaider.spidersGraph.getRdfModel();

            // get previous sibling and make parent of the current node from it
            com.hp.hpl.jena.rdf.model.Resource parentResource = getParentResource(
                    notebook.resource.getMetadata().getUri().toString(),
                    parentConceptUri, rdfModel);

            if (parentResource == null) {
                logger.debug(Messages.getString(
                        "ConceptCustodian.unableToFindParentResource",
                        parentConceptUri));
            } else {
                Seq seq = rdfModel.getModel().getSeq(parentResource);
                com.hp.hpl.jena.rdf.model.Resource conceptResource = rdfModel
                        .getModel().getResource(conceptUri);
                int conceptIndex = seq.indexOf(conceptResource);
                logger.debug(Messages.getString(
                        "ConceptCustodian.demoteConceptIndex", conceptIndex));
                if (conceptIndex > 0) {
                    com.hp.hpl.jena.rdf.model.Resource siblingResource = seq
                            .getResource(conceptIndex - 1);
                    Seq siblingSeq = rdfModel.getModel()
                            .getSeq(siblingResource);
                    siblingSeq.add(conceptResource);
                    seq.remove(conceptIndex);

                    MindRaider.spidersGraph.save();
                    MindRaider.spidersGraph.renderModel();
                }
            }
        } else {
            logger.error("demote() - Spiders graph is null!", null);
        }
    }

    /**
     * Get parent resource from the model.
     * 
     * @param notebook
     *            the notebook Uri.
     * @param parentConceptUri
     *            the parent concept uri.
     * @param rdfModel
     *            the RDF model
     * @return Returns a <code>Resource</code>, otherwise <code>null</code>.
     */
    private com.hp.hpl.jena.rdf.model.Resource getParentResource(
            String notebookUri, String parentConceptUri, RdfModel rdfModel) {
        com.hp.hpl.jena.rdf.model.Resource parentResource;
        if (parentConceptUri == null) {
            // it's root child (notebook)
            parentResource = rdfModel.getResource(notebookUri);
        } else {
            // another concept resource is parent
            parentResource = rdfModel.getResource(parentConceptUri);
        }
        return parentResource;
    }

}
