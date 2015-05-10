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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.RdfModel;
import com.emental.mindraider.core.rest.Metadata;
import com.emental.mindraider.core.rest.Resource;
import com.emental.mindraider.core.rest.properties.AnnotationProperty;
import com.emental.mindraider.core.rest.properties.ConceptProperty;
import com.emental.mindraider.core.rest.properties.LabelProperty;
import com.emental.mindraider.core.rest.properties.OriginProperty;
import com.emental.mindraider.core.rest.properties.ResourceProperty;
import com.emental.mindraider.core.rest.properties.ResourcePropertyGroup;
import com.emental.mindraider.core.rest.properties.SourceTwikiFileProperty;
import com.mindcognition.mindraider.MindRaiderException;
import com.mindcognition.mindraider.commons.MindRaiderConstants;

/**
 * Notebook represents the resource containing:
 * <ul>
 * <li>Common metadata. (label (rdfs:label) and creation time (dc:created) are
 * propagated to RDF model)
 * <ul>
 * <li><code>discarded</code> ... boolean</li>
 * <li><code>type</code> ... resource type mrOntology:Notebook</li>
 * </ul>
 * </li>
 * <li>Data:
 * <ul>
 * <li><code>importedFrom</code> ... if imported from a TWiki file, twiki
 * icon is highlighted in order to enable back save to that file.</li>
 * <li><code>propertyGroup/conceptProperty</code> ... contained concepts
 * (order is given by the RDF model)</li>
 * </ul>
 * </li>
 * </li>
 */
public class OutlineResource {

    /**
     * Logger for this class.
     */
    private static final Logger cat = Logger.getLogger(OutlineResource.class);

    /**
     * The property group uri concept constant.
     */
    public static final String PROPERTY_GROUP_URI_CONCEPTS = "com:e-mental:mindraider:folder:notebook:concepts";

    /**
     * The property group label concept.
     */
    public static final String PROPERTY_GROUP_LABEL_CONCEPTS = "Concepts";

    /**
     * Resource.
     */
    public Resource resource;

    /**
     * Associated RDF model (not saved to XML).
     */
    public RdfModel rdfModel;

    /**
     * Constructor.
     */
    public OutlineResource() {
    }

    /**
     * Constructor.
     *
     * @param resource
     *            resource
     */
    public OutlineResource(Resource resource) {
        this.resource = resource;
        Metadata meta = this.resource.getMetadata();
        meta.setType(MindRaiderConstants.MR_OWL_CLASS_NOTEBOOK);
        this.resource.setMetadata(meta);
    }

    /**
     * Returns the URI.
     *
     * @return the <code>resource.metadata.uri</code> as string
     */
    public String getUri() {
        return resource.getMetadata().getUri().toString();
    }

    public String getOriginUri() {
        try {
            return ((OriginProperty)((ArrayList)resource.getData().getProperties().get(OriginProperty.qname)).get(0)).getUri();
        } catch (Exception e) {
            cat.debug("Unable to get origin ID for resource " + resource, e);
            return null;
        }
    }
    
    /**
     * Returns the label content property.
     *
     * @return the <code>label</code> content
     */
    public String getLabel() {
        try {
            return ((LabelProperty)((ArrayList)resource.getData().getProperties().get(LabelProperty.qname)).get(0)).getLabelContent();
        } catch (Exception e) {
            cat.debug("Unable to get label for resource " + resource, e);
            return "";
        }
    }

    /**
     * Returns the label property.
     *
     * @return the label property
     */
    public LabelProperty getLabelProperty() {
        try {
            return ((LabelProperty) ((ArrayList) resource.getData().getProperties().get(LabelProperty.qname)).get(0));
        } catch (Exception e) {
            cat.debug("Unable to get label for resource" + resource, e);
            return new LabelProperty("");
        }
    }

    /**
     * Get concepts URIs. We're presuming that there exists exactly one concepts
     * group.
     *
     * @return array of uri.
     */
    @SuppressWarnings("unchecked")
    public String[] getConceptUris() {
        ResourcePropertyGroup[] concepts;
        try {
            concepts = resource.getData().getPropertyGroup(new URI(PROPERTY_GROUP_URI_CONCEPTS));
        } catch (URISyntaxException e) {
            cat.debug("URISyntaxException: " + e.getMessage());
            return null;
        }

        if (concepts != null) {
            ConceptProperty[] properties = (ConceptProperty[]) concepts[0].getProperties().toArray(
                    new ConceptProperty[0]);
            if (!ArrayUtils.isEmpty(properties)) {
                String[] result = new String[properties.length];
                for (int i = 0; i < properties.length; i++) {
                    result[i] = properties[i].getUri().toASCIIString();
                }
                return result;
            }
        }
        return null;
    }

    /**
     * Get annotation property.
     */
    public AnnotationProperty getAnnotationProperty() {
        ArrayList annotationProperties = (ArrayList) resource.getData().getProperties().get(AnnotationProperty.qname);
        if (annotationProperties == null) {
            return null;
        }
        return (AnnotationProperty) annotationProperties.get(0);
    }

    /**
     * Set annotation.
     *
     * @param annotation
     *            annotation to be set.
     */
    public void setAnnotation(String annotation) {
        // reuse existing property
        AnnotationProperty annotationProperty = getAnnotationProperty();
        if (annotationProperty != null) {
            annotationProperty.setAnnotation(annotation);
        }
    }

    /**
     * Add concept to notebook. If a concept is discard, it is marked as
     * discarded in notebook only - concept itself remains unchanged.
     *
     * @param conceptUri
     *            URI of the concept
     */
    public void addConcept(String conceptUri) throws Exception {
        if (conceptUri == null) {
            throw new MindRaiderException("Concept URI can't be null!");
        }
        // presuming that there exist exactly one concepts group
        ResourcePropertyGroup[] concepts = resource.getData().getPropertyGroup(new URI(PROPERTY_GROUP_URI_CONCEPTS));
        if (concepts != null) {
            concepts[0].addProperty(new ConceptProperty(new URI(conceptUri)));
        } else {
            throw new MindRaiderException("There is no property group!");
        }
    }
    
    @SuppressWarnings("unchecked")
    public void removeConcept(String conceptUri) throws Exception {
        if (conceptUri == null) {
            throw new MindRaiderException("Concept URI can't be null!");
        }
        // presuming that there exist exactly one concepts group
        ResourcePropertyGroup[] concepts = resource.getData().getPropertyGroup(new URI(PROPERTY_GROUP_URI_CONCEPTS));
        if (concepts != null) {
            ArrayList<ResourceProperty> properties = concepts[0].getProperties();
            if(properties!=null && properties.size()>0) {
                for (int i = 0; i < properties.size(); i++) {
                    ResourceProperty resourceProperty = properties.get(i);
                    if((resourceProperty instanceof ConceptProperty) && conceptUri.equals(((ConceptProperty)resourceProperty).getUri().toString())) {
                        concepts[0].removeProperty(resourceProperty);
                        return;
                    }
                }
            }
        } else {
            throw new MindRaiderException("There is no property group!");
        }        
    }

    /**
     * Get source TWiki file property
     */    
    public SourceTwikiFileProperty getSourceTWikiFileProperty() {
        ArrayList sourceTWikiFileProperties = (ArrayList) resource.getData().getProperties().get(
                SourceTwikiFileProperty.qname);
        if (sourceTWikiFileProperties == null) {
            return null;
        }
        return (SourceTwikiFileProperty) sourceTWikiFileProperties.get(0);
    }

    /**
     * Save notebook.
     *
     * @throws Exception
     */
    public void save() throws Exception {
        MindRaider.outlineCustodian.save(resource);
    }

    /**
     * Discard this resource (and save).
     *
     * @throws Exception
     */
    public void discard() throws Exception {
        Metadata meta = resource.getMetadata();
        meta.setDiscarded(true);
        resource.setMetadata(meta);
        save();

        // remove it from history
        MindRaider.history.remove(resource.getMetadata().getUri().toString());
    }
}