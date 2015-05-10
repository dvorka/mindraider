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

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.Metadata;
import com.emental.mindraider.core.rest.Resource;
import com.emental.mindraider.core.rest.properties.AnnotationContentTypeProperty;
import com.emental.mindraider.core.rest.properties.AnnotationProperty;
import com.emental.mindraider.core.rest.properties.AttachmentProperty;
import com.emental.mindraider.core.rest.properties.CategoryProperty;
import com.emental.mindraider.core.rest.properties.LabelProperty;
import com.emental.mindraider.core.rest.properties.NotebookProperty;
import com.emental.mindraider.core.rest.properties.OriginProperty;
import com.emental.mindraider.core.rest.properties.ResourceProperty;
import com.emental.mindraider.core.rest.properties.ResourcePropertyGroup;
import com.mindcognition.mindraider.MindRaiderException;
import com.mindcognition.mindraider.commons.MindRaiderConstants;

/**
 * Concept represents the resource containing:
 * <ul>
 * <li>Common metadata. (label (rdfs:label) and creation time (dc:created) are
 * propagated to RDF model)</li>
 * <li>Annotation - which is the most import part. (annotation citation
 * (rdfs:comment) is propagated to RDF model)</li>
 * <li>Attachments associated with the concept. (only references
 * (mr:attachment) are propagated to the RDF model). Note that xlink:href is not
 * used in the model since it would complicate model browsing (additional
 * attachment resource would be required). Also attachment description is not
 * propagated to the model - since model is just an metadata on top of concepts
 * and such information is not needed there - all details about attachments are
 * stored in the concept itself. </li>
 * <li>Categorization
 * <ul>
 * <li>Resource type - mrOntology:Concept</li>
 * <li>MR common concept ontology - important/personal/todo/work/obsolete</li>
 * </ul>
 * </li>
 * </ul>
 */
public class ConceptResource {

    /**
     * Logger for this class.
     */
    private static final Logger cat = Logger.getLogger(ConceptResource.class);

    /**
     * Property group uri attachments constant.
     */
    public static final String PROPERTY_GROUP_URI_ATTACHMENTS = "com:e-mental:mindraider:folder:notebook:concept:attachments";

    /**
     * Property group label attachments constant.
     */
    public static final String PROPERTY_GROUP_LABEL_ATTACHMENTS = "Attachments";

    /**
     * Property group uri categories constant.
     */
    public static final String PROPERTY_GROUP_URI_CATEGORIES = "com:e-mental:mindraider:folder:notebook:concept:categories";

    /**
     * Property group label categories constant.
     */
    public static final String PROPERTY_GROUP_LABEL_CATEGORIES = "Categories";

    /**
     * The Resource.
     */
    public Resource resource;

    /**
     * Constructor.
     */
    public ConceptResource() {
    }

    /**
     * Constructor.
     *
     * @param resource
     *            the resource
     */
    public ConceptResource(Resource resource) {
        this.resource = resource;
        Metadata meta = this.resource.getMetadata();
        meta.setType(MindRaiderConstants.MR_OWL_CLASS_CONCEPT);
        this.resource.setMetadata(meta);
    }

    /**
     * Returns the URI.
     *
     * @return the uri string.
     */
    public String getUri() {
        return resource.getMetadata().getUri().toString();
    }

    public String getOriginUri() {
        try {
            return ((OriginProperty)((ArrayList)resource.getData().getProperties().get(OriginProperty.qname)).get(0)).getUri();
        } catch (Exception e) {
            //cat.debug("Unable to get origin ID for resource " + resource, e);
            return null;
        }
    }
    
    /**
     * Getter for the label.
     *
     * @returns Returns the label content.
     */
    public String getLabel() {
        try {
            return ((LabelProperty)((ArrayList<ResourceProperty>)resource.getData().getProperties().get(LabelProperty.qname)).get(0)).getLabelContent();
        } catch (Exception e) {
            cat.error("Unable to get label for concept "
                    + resource.getMetadata().getUri(), e);
            return "";
        }

    }

    public void setLabel(String label) {
        LabelProperty labelProperty = getLabelProperty();
        labelProperty.setLabelContent(label);
    }
    
    /**
     * Get label property.
     *
     * @return Returns the label property.
     */
    public LabelProperty getLabelProperty() {
        return ((LabelProperty)((ArrayList<ResourceProperty>)resource.getData().getProperties().get(LabelProperty.qname)).get(0));
    }

    /**
     * Getter for annotation.
     *
     * @return Returns the annotation.
     */
    public String getAnnotation() {
        return ((AnnotationProperty) ((ArrayList<ResourceProperty>) resource.getData()
                .getProperties().get(AnnotationProperty.qname)).get(0))
                .getAnnotation();
    }

    /**
     * Get annotation content type.
     */
    public String getAnnotationContentType() {
        try {
            return ((AnnotationContentTypeProperty)((ArrayList<ResourceProperty>)resource.data.properties.get(AnnotationContentTypeProperty.qname)).get(0)).contentType;
        } catch(Exception e) {
            // content type property is optional
            return "";
        }
    }
    /**
     * Getter for annotation property.
     *
     * @return the annotation property.
     */
    public AnnotationProperty getAnnotationProperty() {
        return ((AnnotationProperty) ((ArrayList<ResourceProperty>) resource.getData()
                .getProperties().get(AnnotationProperty.qname)).get(0));
    }

    /**
     * Get annotation content type property.
     */
    public AnnotationContentTypeProperty getAnnotationContentTypeProperty() {
        ArrayList<ResourceProperty> arrayList
            =(ArrayList<ResourceProperty>)resource.data.properties.get(AnnotationContentTypeProperty.qname);
        if(arrayList!=null) {
            return (AnnotationContentTypeProperty)arrayList.get(0);
        } else {
            return null;
        }
    }
    /**
     * Setter for annotation.
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
     * Set annotation content type (it is presumed that this property is always set).
     *
     * @param contentType annotation content type to be set.
     * @throws Exception thrown on error.
     */
    public void setAnnotationContentType(String contentType) throws RuntimeException {
        // reuse existing property
        AnnotationContentTypeProperty annotationContentTypeProperty=getAnnotationContentTypeProperty();
        if(annotationContentTypeProperty!=null) {
            annotationContentTypeProperty.contentType=contentType;
        } else {
            // create new annotation property
            resource.addProperty(new AnnotationContentTypeProperty(contentType));
        }
    }

    /**
     * Getger for parent notebook URI.
     *
     * @return Returns the notebook URI.
     */
    public String getNotebookUri() {
        return ((NotebookProperty) ((ArrayList<ResourceProperty>) resource.getData()
                .getProperties().get(NotebookProperty.qname)).get(0)).getUri()
                .toASCIIString();
    }

    /**
     * Add category to concept.
     *
     * @param caption
     *            the caption.
     * @param categoryUri
     *            the category uri.
     */
    public void addCategory(String caption, String categoryUri) {
        try {
            if (categoryUri == null) {
                throw new MindRaiderException("Category URI can't be null!");
            }
            // presuming that there exist exactly one category group
            ResourcePropertyGroup[] categories 
                = resource.getData().getPropertyGroup(new URI(PROPERTY_GROUP_URI_CATEGORIES));
            if (categories == null) {
                categories = new ResourcePropertyGroup[] { 
                        new ResourcePropertyGroup(
                                ConceptResource.PROPERTY_GROUP_LABEL_CATEGORIES,
                                new URI(ConceptResource.PROPERTY_GROUP_URI_CATEGORIES)) };
                resource.getData().addPropertyGroup(categories[0]);
            }

            if (caption == null) {
                categories[0].addProperty(new CategoryProperty(categoryUri));
            } else {
                categories[0].addProperty(new CategoryProperty(caption,categoryUri));
            }
        } catch (Exception e) {
            cat.debug("Unable to add category!", e);
        }
    }
    
    /**
     * Check whether group of categories exists.
     *
     * @return Returns <code>true</code> if category exists, otherwise
     *         <code>false</code>.
     */
    public boolean categoryExist() {
        try {
            ResourcePropertyGroup[] categories = resource.getData()
                    .getPropertyGroup(new URI(PROPERTY_GROUP_URI_CATEGORIES));
            if (categories != null) {
                return true;
            }
        } catch (Exception e) {
            cat.debug("Unable to get categories property group: ", e);
        }
        return false;
    }

    /**
     * Return the categories properties.
     *
     * @return Returns an array of <code>CategoryProperties</code>.
     */
    @SuppressWarnings("unchecked")
    public CategoryProperty[] getCategories() {
        if (categoryExist()) {
            ResourcePropertyGroup[] categories;
            try {
                categories = resource.getData().getPropertyGroup(
                        new URI(PROPERTY_GROUP_URI_CATEGORIES));
                return (CategoryProperty[]) categories[0].getProperties()
                        .toArray(new CategoryProperty[0]);
            } catch (URISyntaxException e) {
                cat.debug("Unable to get categories!", e);
            }
        }
        return null;
    }

    /**
     * Remove category by URI.
     *
     * @param categoryUri
     *            the category uri to remove.
     */
    public void removeCategoryByUri(String categoryUri) {
        if (categoryUri != null && categoryExist()) {
            ResourcePropertyGroup[] categories;
            try {
                categories = resource.getData().getPropertyGroup(
                        new URI(PROPERTY_GROUP_URI_CATEGORIES));
                for (int i = 0; i < categories[0].getProperties().size(); i++) {
                    if (categoryUri.equals(((CategoryProperty) categories[0]
                            .getProperties().get(i)).getCategoryValue())) {
                        cat.debug("Removing category property for: "
                                + categoryUri);
                        categories[0].getProperties().remove(i);
                        return;
                    }
                }
            } catch (URISyntaxException e) {
                cat.debug("Unable to get categories!", e);
            }
        }
    }

    public void removeCategories() {
        if (categoryExist()) {
            try {
                ResourcePropertyGroup[] categories = resource.getData().getPropertyGroup(new URI(PROPERTY_GROUP_URI_CATEGORIES));
                categories[0].clear();
            } catch (URISyntaxException e) {
                cat.debug("Unable to get categories", e);
            }
        }
    }

    /**
     * Add attachment to concept. Note that only concept url is represented in
     * the notebook's RDF model. Detailed attachment information is stored only
     * in this resource.
     *
     * @param description
     *            the attachment description
     * @param url
     *            the attachment url.
     * @throws Exception
     *             a generic exception.
     */
    public void addAttachment(String description, String url) throws Exception {
        if (url == null) {
            throw new MindRaiderException(
                    "Attachment description/url can't be null!");
        }
        // presuming that there exist exactly one attachments group
        ResourcePropertyGroup[] attachments = resource.getData()
                .getPropertyGroup(new URI(PROPERTY_GROUP_URI_ATTACHMENTS));
        if (attachments != null) {
            if (description == null) {
                attachments[0].addProperty(new AttachmentProperty(url));
            } else {
                attachments[0].addProperty(new AttachmentProperty(description,
                        url));
            }
        } else {
            throw new MindRaiderException("There is no property group!");
        }
    }

    /**
     * Check whether group of attachments exists.
     *
     * @return Returns <code>true</code> if attachment exists, otherwise
     *         <code>false</code>.
     */
    public boolean attachmentsExist() {
        try {
            ResourcePropertyGroup[] attachments = resource.getData()
                    .getPropertyGroup(new URI(PROPERTY_GROUP_URI_ATTACHMENTS));
            if (attachments != null) {
                return true;
            }
        } catch (Exception e) {
            cat.debug("Unable to get attachments property group: ", e);
        }
        return false;
    }

    /**
     * Returns all attachments.
     *
     * @return Returns an array of <code>AttachmentProperty</code>
     */
    @SuppressWarnings("unchecked")
    public AttachmentProperty[] getAttachments() {
        if (attachmentsExist()) {
            ResourcePropertyGroup[] attachments;
            try {
                attachments = resource.getData().getPropertyGroup(
                        new URI(PROPERTY_GROUP_URI_ATTACHMENTS));
                return (AttachmentProperty[]) attachments[0].getProperties()
                        .toArray(new AttachmentProperty[0]);
            } catch (URISyntaxException e) {
                cat.debug("Unable to get attachments!", e);
            }
        }
        return null;
    }

    /**
     * Remove attachment by URL.
     *
     * @param attachmentUrl
     *            the attachment url.
     */
    @SuppressWarnings("unchecked")
    public void removeAttachmentByUrl(String attachmentUrl) {
        if (attachmentUrl != null && attachmentsExist()) {
            ResourcePropertyGroup[] attachments;
            try {
                attachments = resource.getData().getPropertyGroup(
                        new URI(PROPERTY_GROUP_URI_ATTACHMENTS));
                ArrayList attachProperties = attachments[0].getProperties();
                for (Object obj : attachProperties) {
                    AttachmentProperty att = (AttachmentProperty) obj;
                    String attachUrl = att.getUrl();
                    if (attachmentUrl.equals(attachUrl)) {
                        cat.debug("Removing attachment property for: "
                                + attachmentUrl);
                        attachProperties.remove(att);
                        return;
                    }
                }
                attachments[0].setProperties(attachProperties);
            } catch (URISyntaxException e) {
                cat.debug("Unable to get attachments!", e);
            }
        }
    }

    /**
     * Discard this resource (and save).
     *
     * @throws Exception
     *             a generic exception.
     */
    public void discard() throws Exception {
        Metadata meta = resource.getMetadata();
        meta.setDiscarded(true);
        resource.setMetadata(meta);
        save();
    }

    /**
     * Save concept.
     *
     * @throws Exception
     *             a generic exception.
     */
    public void save() throws Exception {
        MindRaider.noteCustodian.save(this);
    }
}