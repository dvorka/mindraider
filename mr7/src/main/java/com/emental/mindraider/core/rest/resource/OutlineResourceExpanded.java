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
package com.emental.mindraider.core.rest.resource;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.core.rest.Resource;
import com.emental.mindraider.core.rest.properties.ConceptTreeNode;
import com.emental.mindraider.core.rest.properties.ConceptTreeProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Seq;
import com.mindcognition.mindraider.application.model.note.annotation.AnnotationTransformer;
import com.mindcognition.mindraider.utils.Xsl;

/**
 * Notebook resource representation that directly includes expanded concepts.
 */
public class OutlineResourceExpanded {
    private static final Logger cat = Logger.getLogger(OutlineResourceExpanded.class);

    public static final String PROPERTY_GROUP_URI_CONCEPTS = "com:e-mental:mindraider:folder:notebook:concepts:expanded";
    public static final String PROPERTY_GROUP_LABEL_CONCEPTS = "Concepts expanded";

    public Resource resource;
    public OutlineResource outlineResource;
    private AnnotationTransformer annotationTransformer;

    /**
     * Constructor.
     * 
     * @param resource
     *            notebook.
     * @param annotationTransformer
     *            transformer used to modify annotations (can be
     *            <code>null</code>)
     */
    public OutlineResourceExpanded(OutlineResource outlineResource, AnnotationTransformer annotationTransformer) throws Exception {
        this.outlineResource = outlineResource;
        this.annotationTransformer = annotationTransformer;

        long timestamp = System.currentTimeMillis();
        resource = new Resource(MindRaider.profile.getProfileName(),
                timestamp,
                1,
                timestamp,
                MindRaiderVocabulary.getNotebookUri("TwikiImport" + timestamp));

        buildNotesTreeRecursively();
    }

    /**
     * Save notebook. Take URI by URI from the notebook resource and expand it
     * to the stream.
     * 
     * @throws Exception
     * @see {@link com.emental.mindraider.core.rest.properties.ConceptTreeProperty}
     */
    public void save(String exportedNotebookFilename, String xslFile) throws Exception {
        // self-save
        String metaRepresentationFileName = exportedNotebookFilename + ".expanded.notebook.xml";

        cat.debug("Going to write expanded notebook: " + outlineResource);
        resource.toXmlFile(metaRepresentationFileName);

        // stylesheet that representation
        if (xslFile != null) {
            Xsl.xsl(metaRepresentationFileName, exportedNotebookFilename, xslFile);
        }
    }

    private void buildNotesTreeRecursively() throws Exception {
        if (MindRaider.outlineCustodian.getActiveOutlineResource() == null) {
            cat.debug("No notebook opened - leaving!");
            return;
        }
        
        String outlineUri 
            = MindRaider.outlineCustodian.getActiveOutlineResource().resource.getMetadata().getUri().toString();
        // get model
        Model model = MindRaider.spidersGraph.getRdfModel().getModel();
        // root resource is notebook resource (use statement about the fact that
        // it is notebook)
        String rootResourceUri = outlineUri;
        // concept tree property to be built (note that root node contains
        // notebook resource)
        ConceptTreeNode root = new ConceptTreeNode();
        root.setConcept(MindRaider.outlineCustodian.get(rootResourceUri));
        // build concept tree property (model to be traversed/parent concept
        // tree node)
        // TODO enrich resources with attachments properties
        buildNotesTree(model, rootResourceUri, outlineUri, root, annotationTransformer);

        // create and set the property
        ConceptTreeProperty conceptTreeProperty = new ConceptTreeProperty(root);
        resource.addProperty(conceptTreeProperty);
    }

    private void buildNotesTree(
            Model model,
            String parentResourceUri,
            String notebookUri,
            ConceptTreeNode parentNode,
            AnnotationTransformer annotationTransformer) throws Exception {
        String conceptUri;
        ConceptResource concept;
        RDFNode statement;

        Seq seq = model.getSeq(parentResourceUri);
        NodeIterator i = seq.iterator();

        cat.debug("<-> concept tree builder - got root: " + parentResourceUri);

        ConceptTreeNode child;
        while (i.hasNext()) {
            statement = i.nextNode();
            // add children to parent node
            conceptUri = statement.toString();
            cat.debug("  <-> concept tree builder - child '" + conceptUri + "'");
            concept = MindRaider.noteCustodian.get(notebookUri, conceptUri);

            child = new ConceptTreeNode();
            // TODO create resource clone to avoid changing of annotations
            if (annotationTransformer != null) {
                concept.setAnnotation(annotationTransformer.transform(concept.getAnnotation()));
            }
            child.setConcept(concept.resource);
            parentNode.getChildren().add(child);

            // now dive recursively for each existing node...
            buildNotesTree(model, conceptUri, notebookUri, child, annotationTransformer);
        }
    }
}
