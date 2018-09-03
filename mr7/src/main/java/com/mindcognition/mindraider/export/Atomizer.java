/*
 ===========================================================================
   Copyright 2002-2018 Martin Dvorak

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
package com.mindcognition.mindraider.export;


import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.parser.Parser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.core.rest.Resource;
import com.emental.mindraider.core.rest.properties.AnnotationProperty;
import com.emental.mindraider.core.rest.properties.AttachmentProperty;
import com.emental.mindraider.core.rest.properties.CategoryProperty;
import com.emental.mindraider.core.rest.properties.ConceptTreeNode;
import com.emental.mindraider.core.rest.properties.ConceptTreeProperty;
import com.emental.mindraider.core.rest.properties.OriginProperty;
import com.emental.mindraider.core.rest.properties.ResourceProperty;
import com.emental.mindraider.core.rest.resource.AttachmentResource;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.emental.mindraider.core.rest.resource.OutlineResource;
import com.emental.mindraider.core.rest.resource.OutlineResourceExpanded;
import com.emental.mindraider.ui.dialogs.ProgressDialogJFrame;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.tools.GlobalIdGenerator;
import com.mindcognition.mindraider.ui.swing.explorer.ExplorerJPanel;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;
import com.mindcognition.mindraider.utils.Utils;

public class Atomizer {
    private static final Log logger = LogFactory.getLog(Atomizer.class); // {{debug}}

    public static final String HREF_HOMEPAGE = "http://mindraider.sourceforge.net";
    public static final String ATOM_REL_CHILD_NOTE = "child-note";
    public static final String ATOM_REL_ATTACHMENT = "attachment";

    public static final String ATOM_CAT_OUTLINE = MindRaiderConstants.MR_ATOM_TAXONOMY+":outline";
    public static final String ATOM_CAT_MR_TYPES = MindRaiderConstants.MR_ATOM_TAXONOMY+":types";
    public static final String ATOM_CAT_VERSION = MindRaiderConstants.MR_ATOM_TAXONOMY+":version";
    public static final String ATOM_CAT_REVISION = MindRaiderConstants.MR_ATOM_TAXONOMY+":note:revision";
    public static final String ATOM_CAT_ANNOTATION_TYPE = MindRaiderConstants.MR_ATOM_TAXONOMY+":annotation:type";
    public static final String ATOM_CAT_LABEL = MindRaiderConstants.MR_ATOM_TAXONOMY+":outline:label";
    public static final String ATOM_CAT_TAG = MindRaiderConstants.MR_ATOM_TAXONOMY+":note:tag";
    
    /*
     * to Atom
     */
    
    public static void to(OutlineResourceExpanded sourceOutline, String targetFilename) {
        logger.debug("outlineToAtom()"); // {{debug}}
                        
        HashMap<String, String> noteUriToGlobalId=new HashMap<String, String>();
        
        // TODO serialize also labels
               
        Abdera abdera = new Abdera();
        Feed feed = abdera.newFeed();

        final String globalOutlineUri = GlobalIdGenerator.generateOutlineUri();
        if(sourceOutline.outlineResource.getOriginUri()==null) {
            sourceOutline.resource.addProperty(new OriginProperty(globalOutlineUri));
        }
        
        feed.setId(globalOutlineUri);
        feed.setTitle(sourceOutline.outlineResource.getLabel());
        AnnotationProperty annotationProperty = sourceOutline.outlineResource.getAnnotationProperty();
        String annotation=null;
        if (annotationProperty!=null) {
            annotation = annotationProperty.getAnnotation();            
        }
        feed.setSubtitle((annotation==null?sourceOutline.outlineResource.getLabel()+" Outline.":annotation));
        feed.setGenerator(HREF_HOMEPAGE,MindRaider.getVersion(),MindRaiderConstants.MR_TITLE);
        feed.addLink(HREF_HOMEPAGE,"mindraider-homepage");
        feed.addCategory(ATOM_CAT_OUTLINE, "exported", "Exported");

        ArrayList<ResourceProperty> arrayList = sourceOutline.resource.getData().getProperties().get(ConceptTreeProperty.qname);
        // root of the concept tree hierarchy is outline
        if(arrayList!=null && arrayList.size()>0) {
            logger.debug("  "+arrayList.size()+" ConceptTreeProperties");
            ConceptTreeProperty conceptTreeProperty = (ConceptTreeProperty)arrayList.get(0);
            ConceptTreeNode rootNode = conceptTreeProperty.getRoot();
            OutlineResource outlineNote = new OutlineResource(rootNode.getConcept());
            feed.setUpdated(new Date(outlineNote.resource.metadata.getCreated()));
            feed.addAuthor(outlineNote.resource.metadata.getAuthor().toString());
            feed.addCategory(ATOM_CAT_REVISION, ""+outlineNote.resource.metadata.getRevision(), null);
            feed.addCategory(ATOM_CAT_VERSION, MindRaider.getVersion(), null);            
            feed.addCategory(ATOM_CAT_MR_TYPES, MindRaiderConstants.MR_OWL_CLASS_NOTEBOOK, null);
            ArrayList<ConceptTreeNode> children = rootNode.getChildren();
            if(children!=null && children.size()>0) {
                for (int i = 0; i < children.size(); i++) {
                    Resource child = children.get(i).getConcept();
                    ConceptResource conceptResource = new ConceptResource(child);
                    String globalConceptUri = getNoteOriginUri(globalOutlineUri, conceptResource, noteUriToGlobalId); 
                    feed.addLink(globalConceptUri, ATOM_REL_CHILD_NOTE);
                }
            }
            
            // recursion
            toOneLevel(feed, rootNode, globalOutlineUri, noteUriToGlobalId);
        } else {
            logger.debug("  No ConceptTreeProperty found - no entries to export.");            
        }
                
        try {
            FileUtils.writeStringToFile(new File(targetFilename), feed.toString(), "utf-8");
        } catch (IOException e) {
            logger.error("Unable to write exported outline",e);
        }
        
        // save outline to store Origin properties
        try {
            MindRaider.outlineCustodian.getActiveOutlineResource().save();
            // reload and refresh
            MindRaider.outlineCustodian.loadOutline(new URI(MindRaider.outlineCustodian.getActiveOutlineResource().getUri()));
            OutlineJPanel.getInstance().refresh();
            OutlineJPanel.getInstance().conceptJPanel.clear();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }        
    }

    private static String getNoteOriginUri(final String globalOutlineUri, ConceptResource conceptResource, HashMap<String, String> noteUriToGlobalId) {
        if(noteUriToGlobalId.containsKey(conceptResource.getUri())) {
            return noteUriToGlobalId.get(conceptResource.getUri());
        } else {
            String globalConceptUri=conceptResource.getOriginUri();
            if(globalConceptUri==null) {
                globalConceptUri=GlobalIdGenerator.generateNoteId(globalOutlineUri);
                conceptResource.resource.addProperty(new OriginProperty(globalConceptUri));
            }
            noteUriToGlobalId.put(conceptResource.getUri(), conceptResource.getOriginUri());
            return globalConceptUri;            
        }
    }

    private static void toOneLevel(Feed feed, ConceptTreeNode rootNode, String globalOutlineId, HashMap<String, String> noteUriToGlobalId) {
        ArrayList<ConceptTreeNode> children = rootNode.getChildren();
        if(children!=null && children.size()>0) {
            for (int i = 0; i < children.size(); i++) {
                Resource child = children.get(i).getConcept();
                ArrayList<ConceptTreeNode> childChildren = children.get(i).children;
                Entry entry = toAtomEntry(feed, new ConceptResource(child), childChildren, globalOutlineId, noteUriToGlobalId);
                feed.addEntry(entry);
                
                toOneLevel(feed,children.get(i), globalOutlineId, noteUriToGlobalId);
            }
        }
    }

    private static Entry toAtomEntry(
            Feed feed,
            ConceptResource note,
            ArrayList<ConceptTreeNode> children,
            String globalOutlineId, 
            HashMap<String, String> noteUriToGlobalId) {
        
        Entry entry = feed.addEntry();
        entry.setPublished(new Date(note.resource.metadata.getCreated()));
        entry.setUpdated(new Date(note.resource.metadata.getTimestamp()));
        String globalConceptUri = getNoteOriginUri(globalOutlineId, note, noteUriToGlobalId); 
        entry.setId(globalConceptUri);
        entry.setTitle(note.getLabel());
        entry.setSummary(note.getAnnotation());
        // children
        if(children!=null && children.size()>0) {
            for (int i = 0; i < children.size(); i++) {
                globalConceptUri = getNoteOriginUri(globalOutlineId, new ConceptResource(children.get(i).concept), noteUriToGlobalId);                 
                entry.addLink(globalConceptUri,ATOM_REL_CHILD_NOTE);                                     
            }
        }
        // attachments
        AttachmentProperty[] attachments = note.getAttachments();
        if(attachments!=null && attachments.length>0) {
            for (AttachmentProperty attachmentProperty : attachments) {
                String url=attachmentProperty.getUrl().replace(" ", "%20");
                logger.debug("Creating link from the attachment: '"+url+"' # '"+attachmentProperty.getDescription()+"'");
                entry.addLink(url,ATOM_REL_ATTACHMENT,null,attachmentProperty.getDescription(),null,0);                     
            }
        }
        // cats
        entry.addCategory(ATOM_CAT_REVISION, ""+note.resource.metadata.getRevision(), null);
        entry.addCategory(ATOM_CAT_MR_TYPES, note.resource.metadata.getType(), null);
        entry.addCategory(ATOM_CAT_VERSION, note.resource.metadata.getMindRaiderVersion(), null);            
        entry.addCategory(ATOM_CAT_ANNOTATION_TYPE, note.getAnnotationContentType(), null);
        CategoryProperty[] categories = note.getCategories();
        if(categories!=null && categories.length>0) {
            for (CategoryProperty categoryProperty : categories) {
                entry.addCategory(ATOM_CAT_TAG, categoryProperty.getCategoryValue(), categoryProperty.getCategoryCaption());                    
            }
        }
        
        return entry;
    }        
    
    /*
     * from Atom
     */
    
    public static String from(Reader reader, ProgressDialogJFrame progressDialogJFrame) {
        try {
            Abdera abdera = new Abdera();
            Parser parser = abdera.getParser();
            Document<Feed> doc = parser.parse(reader);
            
            Feed feed = doc.getRoot();
            
            HashMap<String, Entry> entryByUri=new HashMap<String, Entry>();
            
            logger.debug(feed.getTitle());
            for (Entry entry : feed.getEntries()) {
                logger.debug("\t"+entry.getTitle());
                entryByUri.put(entry.getId().toString(), entry);
            }
            
            String outlineUri = null;
            String outlineLabel;
            String labelUri = MindRaider.labelCustodian.LABEL_TWIKI_IMPORT_URI;
            MindRaider.labelCustodian.create("Atom Import", MindRaider.labelCustodian.LABEL_ATOM_IMPORT_URI);
            outlineLabel = feed.getTitle()+" ("+Utils.getCurrentDataTimeAsPrettyString()+")";
            logger.debug("Outline label: '"+outlineLabel+"'");

            outlineUri = MindRaiderVocabulary.getNotebookUri(Utils.toNcName(outlineLabel));
            String createdUri;
            while (MindRaiderConstants.EXISTS.equals(createdUri = MindRaider.outlineCustodian.create(
                    outlineLabel,
                    outlineUri,
                    feed.getSubtitle(),
                    false))) {
                outlineUri += "_";
            }
            outlineUri = createdUri;
            MindRaider.labelCustodian.addOutline(labelUri, outlineUri);
            OutlineResource activeOutlineResource = MindRaider.outlineCustodian.getActiveOutlineResource();
            // fill in notebook resource
            activeOutlineResource.save();
            logger.debug("Outline created: " + outlineUri);
            
            String parentNoteUri=activeOutlineResource.getUri();            
            List<Link> childrenLinks = feed.getLinks(Atomizer.ATOM_REL_CHILD_NOTE);
            fromOneLevel(progressDialogJFrame, entryByUri, outlineUri, parentNoteUri, childrenLinks, activeOutlineResource);
            
            return outlineUri;
        } catch(Exception e) {
            logger.debug("Unable to import Outline from Atom",e);
            return null;
        } finally {
            // now refresh notebook outline
            ExplorerJPanel.getInstance().refresh();
            OutlineJPanel.getInstance().refresh();
            MindRaider.spidersGraph.renderModel();            
        }
    }

    private static void fromOneLevel(
            ProgressDialogJFrame progressDialogJFrame,
            HashMap<String, Entry> entryByUri,
            String outlineUri,
            String parentNoteUri,
            List<Link> childrenLinks,
            OutlineResource activeOutlineResource) throws Exception {
        
        for (int i = 0; i < childrenLinks.size(); i++) {
            String href = childrenLinks.get(i).getHref().toString();
            Entry entry = entryByUri.get(href);
        
            String annotationContentType=entry.getCategories(Atomizer.ATOM_CAT_ANNOTATION_TYPE).get(0).getTerm();
            // get all categories: filter out flag, if it is there
            String commaSeparatedTags=null;
            String categoryTitle=null;
            List<Category> categories = entry.getCategories(Atomizer.ATOM_CAT_TAG);
            if(categories.size()>0) {
                for (int j = 0; j < categories.size(); j++) {
                    String term = categories.get(j).getTerm();
                    if(term.startsWith(MindRaiderConstants.MR_OWL_FLAG_NS)) {
                        categoryTitle=categories.get(j).getLabel();
                    } else {
                        if(commaSeparatedTags==null) {
                            commaSeparatedTags=categories.get(j).getLabel();
                        } else {
                            commaSeparatedTags+=","+categories.get(j).getLabel();
                        }
                    }
                }
            }
            String template=null;
            
            List<Link> attachmentsList = entry.getLinks("attachment");
            AttachmentResource[] attachments=null;
            if(attachmentsList.size()>0) {
                attachments=new AttachmentResource[attachmentsList.size()];
                for (int k=0; k<attachmentsList.size(); k++) {
                    attachments[k]=new AttachmentResource(attachmentsList.get(k).getTitle(),attachmentsList.get(k).getHref().toString().replace("%20", " "));
                }
            }
            
            StatusBar.show("Creating concept '" + entry.getTitle() + "'...");
            String noteUri = MindRaider.noteCustodian.create(
                    activeOutlineResource,
                    parentNoteUri,
                    entry.getTitle(),
                    MindRaiderVocabulary.getConceptUri(Utils.getNcNameFromUri(outlineUri), Utils.toNcName(entry.getTitle())+"-atom-note-"+System.currentTimeMillis()),
                    entry.getSummary(),
                    false,
                    annotationContentType,
                    commaSeparatedTags,
                    categoryTitle,
                    template,
                    attachments);

            if (progressDialogJFrame != null) {
                progressDialogJFrame.setProgressMessage(entry.getTitle());
            }
            
            fromOneLevel(progressDialogJFrame, entryByUri, outlineUri, noteUri, entry.getLinks(Atomizer.ATOM_REL_CHILD_NOTE), activeOutlineResource);
        }
    }
}
