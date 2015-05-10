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
package com.mindcognition.mindraider.tools;

import java.net.URI;
import java.util.HashSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.RdfModel;
import com.emental.mindraider.core.rest.Resource;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.core.rest.properties.AnnotationContentTypeProperty;
import com.emental.mindraider.core.rest.properties.AnnotationProperty;
import com.emental.mindraider.core.rest.properties.AttachmentProperty;
import com.emental.mindraider.core.rest.properties.LabelProperty;
import com.emental.mindraider.core.rest.properties.NotebookProperty;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.emental.mindraider.core.rest.resource.OutlineResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.mindcognition.mindraider.application.model.outline.OutlineCustodian;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;

/**
 * Repository integrity check.
 */
public class Checker {
    private static final Log logger = LogFactory.getLog(Checker.class); // {{debug}}
    
    // TODO: detect concepts that are in RDF, but not on filesystem - concept.xml (e.g. CMS Prototype has such)
    
    public static void checkAndFixRepositoryAsync() {
        Thread thread = new Thread() {
            public void run() {
            	Checker.checkAndFixRepository();
            }
        };
        thread.setDaemon(true);
        thread.start();
    	
    }
    
    public static void checkAndFixRepository() {
        logger.debug("Repository integrity check..."); // {{debug}}
        // fields
        HashSet<ResourceDescriptor> allOutlines=new HashSet<ResourceDescriptor>();
        OutlineCustodian outlineCustodian=MindRaider.outlineCustodian;
        int fixes=0;
        int totalConcepts=0;
        
        // labels (folders) RDF
        Model labelsRdfModel = MindRaider.labelCustodian.getRdfModel(); // folders.rdf.xml
                
        ResourceDescriptor[] labelDescriptors 
            = MindRaider.labelCustodian.getLabelDescriptors();
        
        if (!ArrayUtils.isEmpty(labelDescriptors)) {
            for (ResourceDescriptor labelDescriptor : labelDescriptors) {
                String labelUri = labelDescriptor.getUri();
                
                // check whether [label]/folder.xml exists (eventually re-create it)
                StatusBar.show("Checking label XML resource: "+labelUri);
                Resource labelXmlResource = MindRaider.labelCustodian.get(labelUri);
                if(labelXmlResource==null) {
                    try {
                        StatusBar.show("Fixing label XML resource: "+labelUri);
                        // create directory
                        String labelDirectory = MindRaider.labelCustodian.createLabelDirectory(labelUri);
                        // resource
                        Resource resource = MindRaider.labelCustodian.createXmlResourceForLabel(labelDescriptor.getLabel(), labelUri);
                        MindRaider.labelCustodian.addOutlinesGroupToLabelXmlResource(resource);
                        resource.toXmlFile(MindRaider.labelCustodian.getLabelXmlResourceFileName(labelDirectory));
                        
                        // label resource doesn't exist => must be re-created from RDF
                        ResourceDescriptor[] outlineDescriptors                
                            = MindRaider.labelCustodian.getOutlineDescriptors(labelUri);
                        if(outlineDescriptors!=null && outlineDescriptors.length>0) {
                            for (int i = 0; i < outlineDescriptors.length; i++) {
                                MindRaider.labelCustodian.addOutlineToLabelXmlResourceAndSave(labelUri, outlineDescriptors[i].getUri());
                                System.out.println("Fixing label XML resource: "+labelUri+" -> "+outlineDescriptors[i].getUri());
                                ++fixes;
                            }
                        }
                    } catch(Exception ee) {
                        logger.debug("Unable to fix label: "+labelDescriptor.getUri(),ee); // {{debug}}
                    }
                }

                // folder.rdf.xml
                ResourceDescriptor[] outlineDescriptors                
                    = MindRaider.labelCustodian.getOutlineDescriptors(labelUri);
                if (outlineDescriptors != null) {
                    for (ResourceDescriptor outlineDescriptor : outlineDescriptors) {
                        if(!allOutlines.contains(outlineDescriptor)) {
                            allOutlines.add(outlineDescriptor);
                            StatusBar.show("Checking outline: "+outlineDescriptor.getLabel()+" ("+outlineDescriptor.getUri()+")");
                            logger.debug("  Outline: '"+outlineDescriptor.getLabel()+"', "+outlineDescriptor.getUri()); // {{debug}}

                            Model outlineRdfModel;
                            OutlineResource outlineResource;
                            
                            Resource outlineXmlResource;
                            String outlineModelFilename;
                            String outlineResourceFilename;
                            
                            try {
                                // outline's RDF (notebook.rdf.xml)
                                outlineModelFilename = outlineCustodian.getModelFilenameByDirectory(outlineCustodian.getOutlineDirectory(outlineDescriptor.getUri()));
                                logger.debug("    RDF: "+outlineModelFilename);
                                outlineRdfModel = RdfModel.loadModel(outlineModelFilename,false);
                                
                                // detect whether it is active outline
                                if(MindRaider.outlineCustodian.getActiveOutlineResource()!=null 
                                        && MindRaider.outlineCustodian.getActiveOutlineResource().getUri().equals(outlineDescriptor.getUri())) {
                                    //JOptionPane.showConfirmDialog(MindRaider.mainJFrame, "Fixing active outline: "+outlineDescriptor.getUri());
                                    outlineRdfModel=MindRaider.spidersGraph.getRdfModel().getModel();
                                }
                                
                                if(outlineRdfModel==null) {
                                    // RDF model doesn't exist - such outline can not be restored, just delete it
                                    final String fixMessage = "Fix: removing broken outline '"+outlineDescriptor.getLabel()+"'";
                                    StatusBar.show(fixMessage);
                                    System.out.println(fixMessage);
                                    
                                    // check that outline is NOT BROKEN - otherwise standard functions will not
                                    // be able to discard and delete it
                                    com.hp.hpl.jena.rdf.model.Resource orphan 
                                        = labelsRdfModel.getResource(outlineDescriptor.getUri());
                                    if(orphan!=null) {
                                        logger.debug("  Orphan outline found: "+outlineDescriptor.getUri()); // {{debug}}
                                        if(RdfModel.getLabel(labelsRdfModel, orphan)==null) {
                                            logger.debug("    ... having no label"); // {{debug}}
                                            if(RdfModel.getHref(labelsRdfModel, orphan)==null) {
                                                logger.debug("    ... having no href"); // {{debug}}
                                                // if it has no HREF, then fix it -> standard functions will delete that
                                                String relativePath = MindRaider.profile.getRelativePath(outlineCustodian.getOutlineDirectory(outlineDescriptor.getUri()));
                                                RdfModel.setHref(orphan, relativePath+OutlineCustodian.FILENAME_XML_RESOURCE);
                                            }
                                        }
                                    }
                                    
                                    MindRaider.labelCustodian.discardOutline(outlineDescriptor.getUri());
                                    MindRaider.labelCustodian.deleteOutline(outlineDescriptor.getUri());                                                                                                           
                                    ++fixes;
                                    
                                    continue;
                                }
                                
                                
                                // outline's XML (notebook.xml)
                                outlineResourceFilename = outlineCustodian.getResourceFilenameByDirectory(outlineCustodian.getOutlineDirectory(outlineDescriptor.getUri()));
                                logger.debug("    XML: "+outlineResourceFilename);
                                outlineXmlResource = new Resource(outlineResourceFilename);
                                outlineResource = new OutlineResource(outlineXmlResource);
                            } catch (Exception e) {
                                logger.debug("Unable to load outline" + outlineDescriptor.getUri(), e);
                                // TODO fix it
                                continue;
                            }
                            //logger.debug("    Loaded: "+outlineRdfModel+" / "+outlineXmlResource); // {{debug}}
                            
                            
                            
                            // FIX outline label: on rename changed only in folder's RDF, not xml name (and notebook's XML)
                            // FIX rename: notebook name is changed on rename ONLY in the labels (folders) RDF model,
                            //             in here it is propagated to notebook's XML and (notebook.xml) and RDF (notebook.rdf.xml) 
                            String outlineLabel=MindRaider.labelCustodian.getOutlineDescriptor(outlineDescriptor.getUri()).getLabel();
                            String outlineComment = "'"+outlineLabel+"' outline.";
                            if(outlineLabel!=null && outlineLabel.length()>0) {
                                if(!outlineLabel.equals(outlineResource.getLabel())) {
                                    fixes++;
                                    StatusBar.show("Fixing title and description: "+outlineDescriptor.getUri());
                                    System.out.println("    Fix: inconsistent outline's title & description ("+outlineDescriptor.getUri()+")"); // {{debug}}
                                    logger.debug("      Label's RDF  : "+outlineLabel); // {{debug}}                                    
                                    logger.debug("      Outline's XML: "+outlineResource.getLabel()); // {{debug}}                                    
                                    if(outlineResource.getLabelProperty()!=null) {
                                        outlineResource.getLabelProperty().setLabelContent(outlineLabel);
                                    }
                                    if(outlineResource.getAnnotationProperty()!=null) {
                                        outlineResource.getAnnotationProperty().setAnnotation(outlineComment);
                                    }
                                    try {
                                        outlineResource.save();
                                    } catch (Exception e) {
                                        logger.debug("Unable to save outline XML resource",e); // {{debug}}
                                    }
                                }
                            }
                            com.hp.hpl.jena.rdf.model.Resource rdfResource = outlineRdfModel.getResource(outlineDescriptor.getUri());
                            if (rdfResource != null) {
                                rdfResource.removeAll(RDFS.label);
                                rdfResource.addProperty(RDFS.label, outlineLabel);
                                rdfResource.removeAll(RDFS.comment);
                                rdfResource.addProperty(RDFS.comment, outlineComment);
                                RdfModel.saveModel(outlineRdfModel, outlineModelFilename);
                            }
                            
                            
                            
                            // iterate outline's concepts
                            final SimpleSelector simpleSelector = new SimpleSelector(null,RDF.type,outlineRdfModel.createResource(MindRaiderConstants.MR_OWL_CLASS_CONCEPT));
                            StmtIterator conceptsIterator = outlineRdfModel.listStatements(simpleSelector);
                            while (conceptsIterator.hasNext()) {
                                ++totalConcepts;
                                Statement statement=(Statement)conceptsIterator.next();
                                
                                final com.hp.hpl.jena.rdf.model.Resource conceptRdfResource = statement.getSubject();
                                //logger.debug("  Concept: " +totalConcepts+" "+conceptRdfResource.getURI());
                                
                                // TODO check whether the concept is in notebook.xml
                                
                                // load note resource [concept name].xml
                                try {
                                    ConceptResource noteResource 
                                        = MindRaider.noteCustodian.get(outlineDescriptor.getUri(), conceptRdfResource.getURI());
                                    
                                    // TODO check and fix note's attachments: if attachment is in the resource and not in RDF, add it to RDF
                                    logger.debug("Attachments:");
                                    AttachmentProperty[] attachments = noteResource.getAttachments();
                                    if(attachments!=null && attachments.length>0) {
                                        for (AttachmentProperty attachmentProperty : attachments) {
                                            logger.debug("  "+attachmentProperty.getUrl());
                                            
                                            StmtIterator listStatements = outlineRdfModel.listStatements(
                                                    conceptRdfResource,
                                                    outlineRdfModel.getProperty(MindRaiderConstants.MR_RDF_NS, "attachment"),
                                                    attachmentProperty.getUrl());
                                            if(!listStatements.hasNext()) {
                                                //JOptionPane.showConfirmDialog(MindRaider.mainJFrame, "Missing attach in RDF: "+attachmentProperty.getUrl());
                                                conceptRdfResource.addProperty(
                                                        outlineRdfModel.getProperty(MindRaiderConstants.MR_RDF_NS+"attachment"), attachmentProperty.getUrl());
                                                RdfModel.saveModel(outlineRdfModel, outlineModelFilename);  
                                                ++fixes;
                                            }
                                        }
                                    }
                                    
                                } catch(Exception e) {
                                    // there is a problem (file doesn't exist, it is empty file, ...)
                                    // fix: build *.xml resource from RDF and write it back
                                    
                                    // rdf contains: label/timestamp/comment/?attachments ignored for now
                                    String label = RdfModel.getLabel(outlineRdfModel, conceptRdfResource);
                                    String comment = RdfModel.getComment(outlineRdfModel, conceptRdfResource);
                                    long timestamp= RdfModel.getTimestamp(outlineRdfModel, conceptRdfResource);
                                                      
                                    try {
                                        ConceptResource conceptResource = new ConceptResource(new Resource(
                                                MindRaider.profile.getProfileName(),
                                                timestamp,
                                                1,
                                                System.currentTimeMillis(),
                                                conceptRdfResource.getURI()));
                                        conceptResource.resource.getMetadata().setMindRaiderVersion(MindRaider.getVersion());
                                        conceptResource.resource.getMetadata().setType(MindRaiderConstants.MR_OWL_CLASS_CONCEPT);
                                        conceptResource.resource.getData().addProperty(new LabelProperty(label));
                                        conceptResource.resource.getData().addProperty(new AnnotationProperty(comment));
                                        conceptResource.resource.getData().addProperty(new AnnotationContentTypeProperty(MindRaiderConstants.MR_OWL_CONTENT_TYPE_PLAIN_TEXT));
                                        conceptResource.resource.getData().addProperty(new NotebookProperty(new URI(outlineDescriptor.getUri())));
                                        
                                        conceptResource.resource.toXmlFile(MindRaider.noteCustodian.getConceptResourceFilename(outlineDescriptor.getUri(), conceptRdfResource.getURI()));
                                    } catch(Exception exception) {
                                        logger.error("Unable to ressurect concept from RDF - deleting "+conceptRdfResource.getURI(),e);

                                        // TODO purge concept from the filesystem (a robust implementation that expects 
                                        // that [concept].xml is not there/is locked
                                        
                                        // TODO do purge
                                    }                                                                        
                                }
                            }
                            
                            
                            
                            
                            // TODO FIX: remove concepts from notebook.xml.rdf, that do not exist in notebook.xml OR rather create notebook.xml from what's in RDF
                            // TODO FIX: concepts in RDF vs. notebook.xml vs. on the filesystem 
                            
                            // TODO run discarded :-)
                            MindRaider.outlineCustodian.getDiscardedConceptDescriptors(outlineDescriptor.getUri());
                            
                        }
                    }
                }
            }            
        }
        
        // TODO rebuild search index (after low level changes, FTS index must be updated)
        
        // clean memory
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
        
        logger.debug("Total outlines: "+allOutlines.size()); // {{debug}}
        logger.debug("Fixed problems: "+fixes); // {{debug}}
        StatusBar.show("Check & fix results: total outlines "+allOutlines.size()+", total concepts "+totalConcepts+", fixed problems "+fixes);
    }
}
