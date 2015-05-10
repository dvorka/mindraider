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
package com.emental.mindraider.ui.outline.treetable;

import java.util.Date;
import java.util.Locale;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.emental.mindraider.ui.outline.NotebookOutlineDirectory;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.ibm.icu.text.SimpleDateFormat;
import com.mindcognition.mindraider.application.model.note.NoteCustodianListener;
import com.mindcognition.mindraider.ui.swing.concept.ClassifierFlagRenderer;

/**
 * Notebook outline tree used as model for the tree table.
 */
public final class OutlineTreeInstance implements NoteCustodianListener {
    private static final Logger cat = Logger.getLogger(OutlineTreeInstance.class);

    public static final String CONCEPT_ANNOTATION_NEW_LINE_MANGLE = "&nbsp;<font color='#bbbbbb'><b>&gt;&gt;</b></font>&nbsp;";
    public static final int ANNOTATION_SNIPPET_LENGTH = 50;
    public static final int MAX_ANNOTATION_LENGTH = 250;

    // concept rendering modes

    public static final int FACET_TREE = 1;
    public static final int FACET_FLAT = 2;
    public static final int FACET_FLAT_CREATION_TIME = 3;

    private NotebookOutlineDirectory outlineRoot;

    private int facet = FACET_TREE;

    /*
     * model
     */

    /**
     * Singleton of this class.
     */
    private static OutlineTreeInstance singleton;

    /**
     * Get singleton of this class.
     *
     * @return singleton of this class.
     */
    public static synchronized OutlineTreeInstance getInstance() {
        if (singleton == null) {
            singleton = new OutlineTreeInstance();
        }
        return singleton;
    }

    /**
     * Constructor.
     *
     * @param notebookName
     *            notebook name for which an outline should be built.
     */
    private OutlineTreeInstance() {
        // default root node
        outlineRoot = new NotebookOutlineDirectory("", "", "Empty Notebook", "");
        rebuildTree();
    }

    /**
     * Rebuild tree according to the active notebook.
     */
    public void rebuildTree() {
        if (MindRaider.profile.getActiveOutlineUri() != null
                && MindRaider.outlineCustodian.getActiveOutlineResource() != null) {
            cat.debug("Rebuilding outline tree for notebook: " + MindRaider.profile.getActiveOutlineUri());

            // recursive rendering
            String notebookUri = MindRaider.profile.getActiveOutlineUri().toString();

            // set up root node
            outlineRoot.removeAllChildren();
            outlineRoot.uri = notebookUri;
            outlineRoot.setLabel(MindRaider.outlineCustodian.getActiveNotebookLabel());
            outlineRoot.setAnnotation(getAnnotationToRender(MindRaider.outlineCustodian.getActiveNotebookAnnotation(),
                    null));
            outlineRoot
                    .setCreated(getCreatedToRender(MindRaider.outlineCustodian.getActiveNotebookCreationTimestamp()));

            try {

                switch (facet) {
                case FACET_TREE:
                    // build one tree level
                    rebuildTree(
                            notebookUri, 
                            notebookUri, 
                            outlineRoot, 
                            MindRaider.outlineCustodian.getActiveOutlineResource().rdfModel.getModel());
                    break;
                case FACET_FLAT:
                default:
                    // build one tree level
                    rebuildTree(
                            notebookUri, 
                            notebookUri, 
                            outlineRoot, 
                            MindRaider.outlineCustodian.getActiveOutlineResource().rdfModel.getModel());
                }

            } catch (Exception e) {
                cat.debug("Unable to rebuild tree for " + notebookUri, e);
            }
        }
    }

    /**
     * Render tree recursively.
     */
    private void rebuildTree(String notebookUri, String parentResourceUri, DefaultMutableTreeNode parentNode,
            Model model) throws Exception {
        cat.debug("=-> rebuildTree: " + notebookUri);

        Resource resource;
        Seq childrenSeq;
        NotebookOutlineEntry entry;
        ResourceDescriptor resourceDescriptor;
        Statement flagStatement;
        String flag = null;

        if(model!=null) {
            Seq parentSeq = model.getSeq(parentResourceUri);
            NodeIterator i = parentSeq.iterator();
            while (i.hasNext()) {
                resource = (Resource) i.nextNode();
                resourceDescriptor = MindRaider.outlineCustodian.getRdfResourceDescriptor(resource);

                // category flag
                if ((flagStatement = resource.getProperty(MindRaiderVocabulary.flagProperty)) != null) {
                    flag = flagStatement.getObject().toString();
                } else {
                    flag = null;
                }

                // TODO handle DISCARDS

                // TODO slow?
                String conceptAnnotationType=null;
                try {
                    conceptAnnotationType=
                            MindRaider.noteCustodian.get(notebookUri, resourceDescriptor.getUri()).getAnnotationContentType();
                } catch(Exception e) {
                }
                
                entry = new NotebookOutlineEntry(
                        resourceDescriptor.getUri(), 
                        resourceDescriptor.getLabel(),
                        getAnnotationToRender(resourceDescriptor.getAnnotationCite(), flag),
                        conceptAnnotationType,
                        getCreatedToRender(resourceDescriptor.getCreated()));
                parentNode.add(entry);

                // process children
                childrenSeq = model.getSeq(resourceDescriptor.getUri());
                if (childrenSeq.size() > 0) {
                    // now dive recursively for each existing node...
                    rebuildTree(notebookUri, resourceDescriptor.getUri(), entry, model);
                }
            }

        }
    }

    /**
     * Flat concepts rendering.
     *
     * @throws Exception
     */
    protected void rebuildFlat() throws Exception {
        String[] concepts = MindRaider.outlineCustodian.getActiveOutlineResource().getConceptUris();
        NotebookOutlineEntry entry;
        if (concepts != null) {
            for (int i = 0; i < concepts.length; i++) {
                ResourceDescriptor resourceDescriptor = MindRaider.outlineCustodian
                        .getRdfResourceDescriptor(concepts[i]);

                // TODO handle DISCARDS

                entry = new NotebookOutlineEntry(resourceDescriptor.getUri(), resourceDescriptor.getLabel(),
                        getAnnotationToRender(resourceDescriptor.getAnnotationCite(), null),
                        // TODO to be loaded in order to properly set icons
                        null,
                        getCreatedToRender(resourceDescriptor.getCreated()));
                outlineRoot.add(entry);
            }
        }
    }

    /**
     * Get outline root.
     *
     * @return
     */
    public NotebookOutlineDirectory getOutlineRoot() {
        return outlineRoot;
    }

    /**
     * Clear notebook outline tree table.
     */
    public void clear() {
        outlineRoot.setLabel("");
        outlineRoot.setAnnotation("");
        outlineRoot.setCreated("");
        outlineRoot.removeAllChildren();
    }

    /**
     * Get annotation cite.
     *
     * @param annotation
     * @return
     */
    public static String getAnnotationCite(String annotation) {
        if (annotation != null) {
            if (annotation.length() >= MAX_ANNOTATION_LENGTH) {
                annotation = annotation.substring(0, MAX_ANNOTATION_LENGTH) + " ...";
            }
        } else {
            annotation = "";
        }
        return annotation;
    }

    private static final int CREATED_TODAY = 1;

    private static final int CREATED_6_DAYS = 2;

    private static final int CREATED_THIS_YEAR = 4;

    private static final int CREATED = 8;

    // sec+min+hour+day+6day
    private static final long SIX_DAYS = 1000 * 60 * 60 * 24 * 6;

    public static long getCreatedTimestampFromHtml(String html) {
        if(html!=null) {
            final String BEGIN = "hidden='";
            int begin=html.indexOf(BEGIN)+BEGIN.length();
            int end=html.indexOf("'/>");
            if(begin>=0 && end>=0) {
                String stringLong=html.substring(begin,end);
                return Long.valueOf(stringLong);
            }
        }
        return 0l;
    }
    
    /**
     * Get string to be rendered for creation time (today, 6 days, this year,
     * another year).
     *
     * @param created
     * @return
     */
    public static String getCreatedToRender(long created) {
        int type = CREATED;

        Date date = new Date(created);
        Date today = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat;

        /*
         * if (date.getYear() == today.getYear() && date.getMonth() ==
         * today.getMonth() && date.getDate() == today.getDate())
         */
        if (DateUtils.isSameDay(date, today)) {
            type = CREATED_TODAY;
        } else {
            if (System.currentTimeMillis() - created < SIX_DAYS) {
                type = CREATED_6_DAYS;
            } else {
                if (date.getYear() == today.getYear()) {
                    type = CREATED_THIS_YEAR;
                }
            }
        }

        String bgColor = null, text = null;
        switch (type) {
        case CREATED:
            bgColor = "bbbbbb";
            simpleDateFormat = new SimpleDateFormat("yyyy");
            text = simpleDateFormat.format(date);
            break;
        case CREATED_6_DAYS:
            bgColor = "555555";
            simpleDateFormat = new SimpleDateFormat("EEE", new Locale("en", "US"));
            text = simpleDateFormat.format(date);
            break;
        case CREATED_THIS_YEAR:
            bgColor = "888888";
            simpleDateFormat = new SimpleDateFormat("MMM dd", new Locale("en", "US"));
            text = simpleDateFormat.format(date);
            break;
        case CREATED_TODAY:
            bgColor = "000000";
            simpleDateFormat = new SimpleDateFormat("HH:mm");
            text = simpleDateFormat.format(date);
            break;
        }

        // TODO colorize & transform according to the date

        // note that long timestamp is added to the HTML - it is convenient for parsing/sorting/processing
        return "<html><body bgColor='#" + bgColor + "'><font color='#ffffff'>&nbsp;&nbsp;" + text
                + "&nbsp;&nbsp;</font><span hidden='"+created+"'/></body></html>";
    }

    /**
     * Get annotation to be rendered.
     *
     * @param annotation
     * @return
     */
    public static String getAnnotationToRender(String annotation, String categoryUri) {
        annotation = getAnnotationCite(annotation);
        return "<html>" + " <body " + ClassifierFlagRenderer.getHtmlColorForUri(categoryUri) + ">" + "&nbsp;"
                + annotation.replaceAll("\n", CONCEPT_ANNOTATION_NEW_LINE_MANGLE) + "&nbsp;" + " </body>" + "</html>";
    }

    public int getFacet() {
        return facet;
    }

    public void setFacet(int facet) {
        this.facet = facet;
    }

    /*
     * listener methods (on concept creation, etc.)
     */

    public void conceptCreated(ConceptResource newConcept) {
        // add node to the tree table model

        NotebookOutlineEntry entry = new NotebookOutlineEntry(
                newConcept.getUri(), 
                newConcept.getLabel(),
                getAnnotationToRender(newConcept.getAnnotation(), null),
                newConcept.getAnnotationContentType(),
                getCreatedToRender(newConcept.resource.getMetadata().getCreated()));

        // find parent (curently selected) resource and add a new node to the
        // tree
        DefaultMutableTreeNode parentNode = OutlineJPanel.getInstance().getSelectedTreeNode();

        if (parentNode == null) {
            cat.debug("NEW CONCEPT: adding new concept to root...");
            parentNode = outlineRoot;
        } else {
            cat.debug("NEW CONCEPT: adding new concept to tree...");
        }
        parentNode.add(entry);

        OutlineJPanel.getInstance().refresh();
        OutlineJPanel.getInstance().setSelectedTreeNodeConcept(newConcept.getUri());
    }
}