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
package com.mindcognition.mindraider.application.model.tag;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.OwlOntology;
import com.emental.mindraider.core.rdf.RdfCustodian;
import com.emental.mindraider.ui.listeners.TagSnailHyperlinkListener;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.mindcognition.mindraider.MindRaiderException;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.utils.Utils;

/**
 * Tag navigator is class responsible for keeping tag information (save/load OWL model), 
 * its rendering (tag cloud) and searching.
 * 
 * Concept category vs. concept tags:
 * <ul>
 *   <li>Concept might have 0 or 1 category - it gives it a color (red/green/...)
 *   <li>Concept might have 0 or n tags (used to create a cloud)
 *   <li>Category ontology: http://e-mental.com/mindraider/owl/flag#
 *   <li>Tag ontology:      http://e-mental.com/mindraider/owl/tag#
 *   <li>Categories are stored in:
 *         concept's resource.xml,
 *         notebook.rdf.xml and 
 *         ontologies/tags.owl.xml (search index)
 *   <li>Tags are stored in:
 *         concept's resource.xml and
 *         ontologies/tags.owl.xml (search index)
 *   <li>Save:
 *     <ul>  
 *       <li>Remove all categories for the concept from: resource, rdf and search index
 *       <li>Remove all the tags from: resource and search index
 *       <li>Set the new category to: resource, rdf and search index
 *       <li>Add each tag to: resource and search index
 *     </ul>  
 *   <li>Show:
 *     <ul>  
 *       <li>Non redundancy: when rendered in concept JPanel - 
 *             the category is shown in the pull down ONLY (not between tags) and 
 *             tags are never used as categories ('important' tag can not colorize the concept)
 *     </ul>  
 * </ul>
 * 
 * Note that tags are converted to lowercase in order to make tag snail concise.
 */
public class TagCustodian implements MindRaiderConstants {
    private static final Logger logger = Logger.getLogger(TagCustodian.class);

    /*
     * TODO remove all the concept tag on its deletion 
     * TODO MVC - split UI and business logic - getHtml()
     */
    
    /**
     * Special URI identifying page with all tags
     */
    public static final String ALL_TAGS_URL="http://ALL_TAGS_FLAG"; 
    
    /**
     * tag pane
     */
    private JEditorPane tagPane=new JEditorPane();
    
    /*
     * Search use cases:
     *  o get all the tags
     *  o get resources tagged with this tag
     *  o get tags related to this tag (all tags which are used together for some resource)
     *  
     * Index update use cases:
     *  o remove a concept and decrease all the tags that it contained by one, then add it again
     */
    
    /**
     * hash of tags URIs (key), as value there is number of references to the tag.
     */
    private Hashtable<String, TagEntry> tags=new Hashtable<String, TagEntry>();
    
    /**
     * hash of tags labels (key), as value there is number of references to the tag.
     */
    private Hashtable<String, TagEntry> tagLabels=new Hashtable<String, TagEntry>();
    
    /**
     * Types of the HTML output
     */
    public enum HtmlOutput {
        ALL_TAGS, CHOSEN_TAG
    }
    
    // TODO bind this to color profile via file()
    private String htmlHead=
        "<html>"+
        " <head>" +
        "   <style type='text/css'>" +
        "     a {" +
        "         color: #C6E2FF;" +
        "     } "+
        "     ul, ol {" +
        "         margin-top: 0px;" +
        "         margin-bottom: 3px;" +
        "         margin-left: 25px;" +
        "     }" +
        "     body {" +
        "         color: white; "+
        "         font-family: arial, helvetica, sans-serif; " +
        "         font-size: small;" +
        "     }"+
        "   </style>" +
        " </head>" +
        " <body>";
    
    private String htmlTail=
        " </body>" + "</html>";
    
    /**
     * tag welcome screen 
     */
    private String fakeTagSnail=
        htmlHead+
        "Tag index not initialized yet."+
        htmlTail;

    /**
     * treshold used to distinguish size of of the font tags
     */
    private float treshold;
    
    /**
     * Constructor
     *
     */
    public TagCustodian() {
    }

    /**
     * Remove all tag registrations.
     */
    public void clear() {
        tags.clear();
        tagLabels.clear();
    }
    
    /**
     * Get tag by label.
     * 
     * @return tag descriptor.
     */
    public TagEntry getByLabel(String label) {
        if(label!=null) {
            return tagLabels.get(label.toLowerCase());
        } else {
            return null;
        }
    }
    
    /**
     * Add tag registration.
     * 
     * @param tag
     */
    public void add(TagEntry tag) {
        tags.put(tag.getTagUri(), tag);
        tagLabels.put(tag.getTagLabel(), tag);
    }

    /**
     * Increase tag cardinality by one - if it is not known yet, register
     * it as a new tag.
     *  
     * @param tag
     * @param targetResource folder, notebook or concept resource
     */
    public void addOrInc(TagEntry tag, TaggedResourceEntry taggedResource) {
        TagEntry entry = tagLabels.get(tag.getTagLabel());
        if(entry==null) {
            tag.addResource(taggedResource);
            add(tag);
        } else {
            entry.inc();
            entry.addResource(taggedResource);
        }
    }
    
    /**
     * Decrease references to the tag. If number of references is <=0, then delete the tag.
     * 
     * @param tagLabel
     */
    public void decOrRemoveByLabel(String tagLabel) {
        if(tagLabel!=null) {
            tagLabel=tagLabel.toLowerCase();
            TagEntry entry;
            if((entry=tagLabels.get(tagLabel))!=null) {
                entry.dec();
                if(entry.isDead()) {
                    tags.remove(entry.getTagUri());
                    tagLabels.remove(entry.getTagLabel());
                    logger.debug("Tag removed: "+tagLabel);
                    return;
                }
            }
        }
        logger.debug("Remove - tag not found: "+tagLabel);
    }
    
    /**
     * Increment tag descriptor.
     * 
     * @param tagLabel
     */
    public void incByLabel(String tagLabel, TaggedResourceEntry taggedResourceEntry) {
        if(tagLabel!=null) {
            tagLabel=tagLabel.toLowerCase();
            TagEntry entry;
            if((entry=tagLabels.get(tagLabel))!=null) {
                entry.addResource(taggedResourceEntry);
                entry.inc();
            }
        }
    }
    
    public void incByLabel(String tagLabel, int increment) {
        if(tagLabel!=null) {
            tagLabel=tagLabel.toLowerCase();
            TagEntry entry;
            if((entry=tagLabels.get(tagLabel))!=null) {
                entry.setCardinality(entry.getCardinality()+increment);
            }
        }
    }

    /**
     * Redraw panel: get all tags, sort them by cardinality, remember min and max (font decision), 
     * write them to HTML
     */
    public void redraw() {
        redraw(getHtml(HtmlOutput.ALL_TAGS));
    }

    public void redraw(String html) {
        tagPane.setText(html);
    }
    
    /**
     * Get UI panel.
     *
     * @return UI panel;
     */
    public Component getPanel() {
        // just write tags one by one - as a text and set proper size
                
        tagPane = new JEditorPane(
                "text/html","");
        tagPane.setBackground(new Color(0x00, 0x00, 0x00)); // f6f8ff
        tagPane.setEditable(false);
        tagPane.setEditorKit(new HTMLEditorKit());
        tagPane.setText(fakeTagSnail);
        //previewPane.addHyperlinkListener(new TextAnnotationPreviewHyperlingListener(this,previewPane));
        JScrollPane previewScrollPane = new JScrollPane(tagPane);
        previewScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        previewScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //previewScrollPane.setBorder(new TitledBorder("Tag Navigator")); // TODO bundle
        
        tagPane.addHyperlinkListener(new TagSnailHyperlinkListener());

        return previewScrollPane;
    }
    
    public TagEntry[] getAllTags() {
        Collection<TagEntry> values = tags.values();
        if(values==null || values.size()==0) {
            return null;
        } else {
            return values.toArray(new TagEntry[0]);
        }
    }
    
    public String getHtml(HtmlOutput outputType) {
        return getHtml(outputType,null);
    }
    
    public String getHtml(HtmlOutput outputType, String tagUri) {
        
        StringBuffer tagSnail=new StringBuffer(htmlHead);
                
        switch(outputType) {
        case ALL_TAGS:
            // show all the tags known to MR, at the bottom of the page show links to flickr, del.icio.us, etc.
            
            Collection<TagEntry> values = tags.values();
            if(values!=null) {
                TagEntry[] tagEntries = values.toArray(new TagEntry[0]);
                
                if(tagEntries!=null && tagEntries.length>0) {
                    // TODO sort not needed always - just determining max is enough ;-)
                    Arrays.sort(tagEntries, new TagEntryCardinalityComparator());

                    // factorize max in order to make the snail written in different fonts
                    // i need to divide tags to 6 groups:
                    //  1) determine maximal cardinality - they are ordered by cardinality, take the last
                    //  2) groupWidth = max/6 (if gw==0, then it is 1)
                    //  3) map tag to a group as follows:
                    //       g = tagCardinality/groupWidth (if g==0, then it is 1)
                    //     
                    //String[] cssSizes=new String[]{"x-small","small","medium","large","x-large","xx-large"};
                    String[] cssSizes=new String[]{"small","medium","large","x-large","xx-large"};
                    //String[] cssSizes=new String[]{"medium","large","x-large","xx-large"};
                    //String[] cssSizes=new String[]{"small","xx-large"};
                    float maxCardinality=tagEntries[tagEntries.length-1].getCardinality();
                    //treshold = maxCardinality/2;
                    float cardinalityGroupWidth=((float)maxCardinality)/(float)cssSizes.length;
                    cardinalityGroupWidth=(cardinalityGroupWidth==0.0f?1.0f:cardinalityGroupWidth);
                    
                    Arrays.sort(tagEntries, new TagEntryNameComparator());
                    //TODO tagSnail.append("<span style='font-size: large'>Tags</span>");
//                    tagSnail.append("<span style='font-size: x-small'>x-small</span>");
//                    tagSnail.append("<span style='font-size: small'>small</span>");
//                    tagSnail.append("<span style='font-size: medium'>medium</span>");
//                    tagSnail.append("<span style='font-size: large'>large</span>");
//                    tagSnail.append("<span style='font-size: x-large'>x-large</span>");
//                    tagSnail.append("<span style='font-size: xx-large'>xx-large</span>");
                    
                    tagSnail.append("<center>");
                    for(TagEntry tagEntry: tagEntries) {
                        tagSnail.append("<a title='");
                        tagSnail.append(tagEntry.getCardinality());
                        tagSnail.append("' href='");
                        tagSnail.append(tagEntry.getTagUri());
                        tagSnail.append("'>");
                        
                        float cssGroupFloat=((float)tagEntry.getCardinality())/cardinalityGroupWidth;
                        int cssGroup=Math.round(cssGroupFloat);
                        logger.debug("CSS Group: "+cssGroup+" / "+tagEntry.getCardinality()+" / "+maxCardinality+" / "+cardinalityGroupWidth);
                        cssGroup=(cssGroup<1?1:cssGroup);
                        cssGroup=(cssGroup>=cssSizes.length?cssSizes.length-1:cssGroup);
                        tagSnail.append("<span style='font-family: serif; font-size: "+cssSizes[cssGroup]+"'>");
//                        if(tagEntry.getCardinality() >= treshold ) {
//                            tagSnail.append("<span style='font-size: xx-large'; font-weight: bold>");
//                        } else {
//                            tagSnail.append("<span style='font-size: small'>");
//                        }
                        tagSnail.append(tagEntry.getTagLabel());
                        tagSnail.append("</span></a> &nbsp; ");
                    }
                    tagSnail.append("</center>");
                    
                    // TODO tail - simply not usable
//                    tagSnail.append("<br><center>Check tags @ " +
//                            "<a href='http://del.icio.us/'>del.icio.us</a> | " +
//                            "<a href='http://www.flickr.com/photos/tags/'>flickr</a> | " +
//                            "<a href='http://www.technorati.com/tag/'>technorati</a>" +
//                            "</center>");
                }
            }
            
            logger.debug(tagSnail);
            
            break;
        case CHOSEN_TAG:
            String tagLabel = tags.get(tagUri).getTagLabel();
            
            // show just chosen tag - render bread crumb (all clickable), show all related tags (used in same resource with this tag)
            // and link to this tag at flicker, delicio, etc.
            tagSnail.append("<span style='font-size: large'><a href='"+ALL_TAGS_URL+"'>Tags</a> / ");
            tagSnail.append(tagLabel.replaceAll(" ", "&nbsp;"));
            tagSnail.append("</span>");

            TagEntry[] tagEntries=getRelatedTag(tagUri);
            if(tagEntries!=null) {
                tagSnail.append("<br><br>Related tags:");
                tagSnail.append("<center>");
                for(TagEntry tagEntry: tagEntries) {
                    tagSnail.append("<a title='");
                    tagSnail.append(tagEntry.getCardinality());
                    tagSnail.append("' href='");
                    tagSnail.append(tagEntry.getTagUri());
                    tagSnail.append("'>");
                    if(tagEntry.getCardinality() >= treshold ) {
                        tagSnail.append("<span style='font-size: xx-large'; font-weight: bold>");
                    } else {
                        tagSnail.append("<span style='font-size: small'>");
                    }
                    tagSnail.append(tagEntry.getTagLabel());
                    tagSnail.append("</span></a> ");
                }
                tagSnail.append("</center>");
            }
            
            tagSnail.append("<br><center>Find tag "+tagLabel+" @ " +
                    "<a href='http://del.icio.us/popular/"+tagLabel+"'>del.icio.us</a> | " +
                    "<a href='http://www.flickr.com/photos/tags/"+tagLabel+"/'>flickr</a> | " +
                    "<a href='http://www.technorati.com/tag/"+tagLabel+"'>technorati</a>" +
                    "</center>");
            break;
        }

        tagSnail.append(htmlTail);
        
        return tagSnail.toString();
    }
    
    /**
     * Save tags ontology. If it doesn't exists, build it from scratch.
     * 
     * TODO called on EVERY SAVE of the notebook - very inefficient. 
     *  
     * @throws MindRaiderException 
     */
    public void toRdf() throws MindRaiderException {
        // build the ontology and save it using RDF custodian
        
        // create ontology using runtime information
        // (tag is class and tagged resource is an instance of this class)
        // take tag one by one and create statements
        OwlOntology tagOntology=new OwlOntology();
        OntModel ontology = tagOntology.getOntology();
        
        OntClass tag= ontology.createClass(MindRaiderConstants.MR_OWL_TAG);
        OntClass flag = ontology.createClass(MindRaiderConstants.MR_OWL_FLAG);

        // properties
        ObjectProperty flagPropertyType = ontology.createObjectProperty(MindRaiderConstants.MR_OWL_PROPERTY_FLAG);
        flagPropertyType.addRange(flag);
        ObjectProperty tagPropertyType = ontology.createObjectProperty(MindRaiderConstants.MR_OWL_PROPERTY_TAG);
        tagPropertyType.addRange(tag);
        
        Collection<TagEntry> values = tags.values();
        if(values!=null) {
            TagEntryImpl[] tagEntries = values.toArray(new TagEntryImpl[0]);
            Property tagProperty = ontology.getProperty(MindRaiderConstants.MR_OWL_PROPERTY_TAG);
            Property inNotebookProperty = ontology.createProperty(MindRaiderConstants.MR_OWL_PROPERTY_IN_NOTEBOOK);
            if(tagEntries!=null && tagEntries.length>0) {
                for(TagEntry tagEntry: tagEntries) {
                    OntClass tagClass = ontology.createClass(tagEntry.getTagUri());
                    tagClass.addLabel(tagEntry.getTagLabel(), "en");
                    flag.addSubClass(tagClass);
                    tag.addSubClass(tagClass);
                    
                    // include tagged resources - iterate the hashmap
                    TaggedResourceEntry[] resources = tagEntry.getResources();
                    if(resources!=null && resources.length>0) {
                        logger.debug("  Tag entry resources: "+resources.length);
                        for(TaggedResourceEntry resource: resources) {
                            Resource conceptResource = ontology.createResource(resource.conceptUri);
                            // tagged resource is the instance of the class
                            ontology.add(ontology.createStatement(conceptResource, tagProperty, tagClass));
                            ontology.add(ontology.createStatement(conceptResource, RDFS.label, ontology.createLiteral(resource.conceptLabel, "en")));
                            // timestamp and notebook information is not stored - it would just duplicate
                            // what's stored in notebook/concepts models - this information will be filled
                            // on concept load - btw this would also cause problems e.g. on refactoring of
                            // concepts and notebooks
                            // ... but I will store it anyway :-) concept is addressed in a notebook
                            Resource notebookResource= ontology.createResource(resource.notebookUri);
                            ontology.add(ontology.createStatement(
                                    conceptResource, 
                                    inNotebookProperty,
                                    notebookResource));
                            ontology.add(ontology.createStatement(
                                    notebookResource, 
                                    RDFS.label, 
                                    ontology.createLiteral(resource.notebookLabel, "en")));
                        }
                    }
                }
            }
        }
        
        // store the ontology
        MindRaider.rdfCustodian.saveOntology(RdfCustodian.FILENAME_TAGS_ONTOLOGY,tagOntology);
    }
    
    /**
     * Load tag ontology - if it doesn't exist, run renidexing.
     */
    public void fromRdf() {
        logger.debug("Loading tag ontology..."); 
        clear();
        
        OwlOntology tagOntology=MindRaider.rdfCustodian.loadOntology(RdfCustodian.FILENAME_TAGS_ONTOLOGY);

        // find all the tag class sub-resources and fill the hashtable with them
        OntModel ontology = tagOntology.getOntology();
        Resource tagClass = ontology.getResource(MindRaiderConstants.MR_OWL_TAG);
        StmtIterator iterator = ontology.listStatements(null,RDFS.subClassOf,tagClass);
        while(iterator.hasNext()) {            
            // URI
            Statement statement=(Statement)iterator.nextStatement();
            String tagUri=statement.getSubject().getURI().toString();
            
            if(tagUri.equals(MindRaiderConstants.MR_OWL_TAG)) {
                continue;
            }
            
            // label
            StmtIterator labelIterator = ontology.listStatements(statement.getSubject(), RDFS.label, (Literal)null);
            String tagLabel=tagUri;
            if(labelIterator.hasNext()) {
                tagLabel=labelIterator.nextStatement().getLiteral().getString();
            }

            TagEntryImpl tag = 
                new TagEntryImpl(
                        tagUri,
                        tagLabel,
                        0);                        
            
            // cardinality == number of resources attached to the tag (count); stmt direction is concept to tag
            StmtIterator taggedConcepts 
                = ontology.listStatements(
                        null, ontology.getProperty(MindRaiderConstants.MR_OWL_PROPERTY_TAG),statement.getSubject());
            int cardinality=0;
            while(taggedConcepts.hasNext()) {
                Statement taggedConceptStatement = taggedConcepts.nextStatement();

                // get concept
                Resource conceptRdfResource = ((Resource)taggedConceptStatement.getSubject());
                String conceptUri=conceptRdfResource.getURI().toString();
                String conceptLabel=conceptUri;
                labelIterator = ontology.listStatements(conceptRdfResource, RDFS.label, (Literal)null);
                if(labelIterator.hasNext()) {
                    conceptLabel=labelIterator.nextStatement().getLiteral().getString();
                }
                
                // get notebook
                StmtIterator notebookOfTheConceptStatement = ontology.listStatements(taggedConceptStatement.getSubject(),ontology.getProperty(MindRaiderConstants.MR_OWL_PROPERTY_IN_NOTEBOOK),(Resource)null);
                String notebookUri=null;
                RDFNode notebookRdfResource=null;
                if(notebookOfTheConceptStatement.hasNext()) {
                    notebookRdfResource = notebookOfTheConceptStatement.nextStatement().getObject();
                    notebookUri=((Resource)notebookRdfResource).getURI().toString();
                } else 
                    continue;
                String notebookLabel=notebookUri;
                labelIterator = ontology.listStatements((Resource)notebookRdfResource, RDFS.label, (Literal)null);
                if(labelIterator.hasNext()) {
                    notebookLabel=labelIterator.nextStatement().getLiteral().getString();
                }
                
                // TODO remove path from here! inefficient! It must be determined when the notebook is loaded
                // in search navigator - here it is too slow!
                tag.addResource(new TaggedResourceEntry(
                        notebookUri,
                        notebookLabel,
                        conceptUri,
                        conceptLabel,
                        0, // TODO not stored
                        MindRaider.noteCustodian.getConceptResourceFilename(notebookUri, conceptUri)));
                cardinality++;
            }
            tag.cardinality=cardinality;
            
            
            logger.debug("  Tag entry: "+tagUri+" @ "+tagLabel+" @ "+cardinality);
            
            
            // place it to the hash
            add(tag);
        }
    }
    
    /**
     * Get all the resources tagged with the given tag
     * 
     * @param label
     */
    public TaggedResourceEntry[] getTaggedResourcesByLabel(String label) {
        if(label!=null) {
            TagEntry tag;
            if((tag=tagLabels.get(label.toLowerCase()))==null) {
                return null;
            } else {
                return tag.getResources();
            }        
        }
        return null;
    }
    
    public TaggedResourceEntry[] getTaggedResourcesByUri(String uri) {
        if(uri!=null) {
            TagEntry tag;
            if((tag=tags.get(uri))==null) {
                return null;
            } else {
                return tag.getResources();
            }        
        }
        return null;
    }
    
    /**
     * Get all the related tags - find all the tags which are used
     * together with the given tag in some resource. I.e. get tag entry,
     * take all tagged resources and merge tags of these resources; finally
     * return them.
     * 
     * @param tagUri    URI of the tag whose relates should be found
     */
    public TagEntry[] getRelatedTag(String tagUri) {
        TagEntry tag;
        if((tag=tags.get(tagUri))!=null) {
            TaggedResourceEntry[] resources = tag.getResources();
            if(resources!=null) {
                Collection<TagEntry> values = tags.values();
                if(values!=null) {
                    TagEntry[] tagEntries = values.toArray(new TagEntry[0]);
                    TaggedResourceEntry[] tagResources;
                    
                    HashSet<TagEntry> result=new HashSet<TagEntry>();
                    // for each resource go through the all tag entries. if tag
                    // entry has the resource in its tagged resources, add the tag
                    // the the result (except self)
                    for(TaggedResourceEntry resource: resources) {
                        NEXT_TAG:for(TagEntry tagEntry: tagEntries) {
                            if(!tagUri.equals(tagEntry.getTagUri())) {
                                tagResources = tagEntry.getResources();
                                if(tagResources!=null && tagResources.length>0) {
                                    for (TaggedResourceEntry resultEntry: tagResources) {
                                        if(resource.conceptUri.equals(resultEntry.conceptUri)) {
                                            result.add(tagEntry);
                                            continue NEXT_TAG;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // build the result
                    if(result.size()!=0) {
                        return result.toArray(new TagEntry[result.size()]);
                    }
                }
            }
        }
        return null;
    }
    
    public TagEntry[] registerCategories(String commaSeparatedTags, String notebookUri, String conceptUri, String conceptLabel, long conceptTimestamp) {
        ArrayList<TagEntry> result=new ArrayList<TagEntry>();
        if(commaSeparatedTags!=null && commaSeparatedTags.length()>0) {
            String[] tags = commaSeparatedTags.split(",");
            for(String tag: tags) {
                tag=tag.trim();
                logger.debug(" Tag: "+tag);
                
                // get tag's registration from tag navigator -> get URI (if not known, then it is registered)
                TagEntry tagEntry = MindRaider.tagCustodian.getByLabel(tag);
                if(tagEntry==null) {
                    // build unique URI, it must be NC name (no spaces) and it must not exist!
                    String tagLocalName=Utils.toNcName(tag);
                    // create tag entry
                    tagEntry=new TagEntryImpl(MindRaiderConstants.MR_OWL_TAG_NS+tagLocalName,tag,1);
                    addResourceToTagEntry(tagEntry, notebookUri, conceptUri, conceptLabel, conceptTimestamp);
                    MindRaider.tagCustodian.add(tagEntry);
                } else {
                    // tag entry already exists - add reference
                    tagEntry.inc();
                    addResourceToTagEntry(tagEntry, notebookUri, conceptUri, conceptLabel, conceptTimestamp);
                }
                result.add(tagEntry);
            }
        }
        return result.toArray(new TagEntry[result.size()]);
    }

    public TagEntry addResourceToTagEntry(TagEntry tagEntry, String notebookUri, String conceptUri, String conceptLabel, long conceptTimestamp) {
        tagEntry.addResource(
                new TaggedResourceEntry(
                        notebookUri,
                        MindRaider.outlineCustodian.getActiveNotebookLabel(),
                        conceptUri,
                        conceptLabel,
                        conceptTimestamp,
                        MindRaider.noteCustodian.getConceptResourceFilename(notebookUri, conceptUri)));
        return tagEntry;
    }
    
}
