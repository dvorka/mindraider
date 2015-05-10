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
import com.emental.mindraider.core.rest.Metadata;
import com.emental.mindraider.core.rest.Resource;
import com.emental.mindraider.core.rest.properties.LabelProperty;
import com.emental.mindraider.core.rest.properties.NotebookProperty;
import com.emental.mindraider.core.rest.properties.ResourcePropertyGroup;
import com.mindcognition.mindraider.MindRaiderException;
import com.mindcognition.mindraider.commons.MindRaiderConstants;

/**
 * Object representation of the folder resource.
 */
public class FolderResource {

    /**
     * The property group uri notebooks.
     */
    public static final String PROPERTY_GROUP_URI_NOTEBOOKS = "com:e-mental:mindraider:folder:notebooks";

    /**
     * The property group label notebooks.
     */
    public static final String PROPERTY_GROUP_LABEL_NOTEBOOKS = "Notebooks";

    /**
     * The resource.
     */
    private Resource resource;

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(FolderResource.class);

    /**
     * Constructor.
     * 
     * @param resource
     *            the resource
     */
    public FolderResource(Resource resource) {
        this.resource = resource;
        Metadata meta = this.resource.getMetadata();
        meta.setType(MindRaiderConstants.MR_OWL_CLASS_FOLDER);
        this.resource.setMetadata(meta);
    }

    /**
     * Get URI.
     * 
     * @return the uri.
     */
    public String getUri() {
        return resource.getMetadata().getUri().toString();
    }

    /**
     * Get label property.
     * 
     * @return the label name.
     */
    @SuppressWarnings("unchecked")
    public String getLabel() {
        return ((LabelProperty) ((ArrayList) resource.getData().getProperties()
                .get(LabelProperty.qname)).get(0)).getLabelContent();
    }

    /**
     * Get label property.
     * 
     * @return the label property.
     */
    @SuppressWarnings("unchecked")
    public LabelProperty getLabelProperty() {
        return ((LabelProperty) ((ArrayList) resource.getData().getProperties()
                .get(LabelProperty.qname)).get(0));
    }

    /**
     * Get notebook names.
     * 
     * @return array of notebook uri.
     */
    @SuppressWarnings("unchecked")
    public String[] getNotebookUris() {
        // presuming that there exist exactly one notebooks group
        ResourcePropertyGroup[] notebooks;
        try {
            notebooks = resource.getData().getPropertyGroup(
                    new URI(PROPERTY_GROUP_URI_NOTEBOOKS));
        } catch (URISyntaxException e) {
            logger.error("getNotebookUris()", e);
            return null;
        }

        if (notebooks != null) {
            NotebookProperty[] properties = (NotebookProperty[]) notebooks[0]
                    .getProperties().toArray(new NotebookProperty[0]);
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
     * Add notebook to folder.
     * 
     * @param notebookUri
     *            URI of the notebook
     * @throws Exception
     *             a generic exception
     */
    public void addNotebook(String notebookUri) throws Exception {
        if (notebookUri == null) {
            throw new MindRaiderException("Notebook URI can't be null!");
        }
        // presuming that there exist exactly one notebooks group
        ResourcePropertyGroup[] notebooks = resource.getData()
                .getPropertyGroup(new URI(PROPERTY_GROUP_URI_NOTEBOOKS));
        if (notebooks != null) {
            notebooks[0]
                    .addProperty(new NotebookProperty(new URI(notebookUri)));
        } else {
            throw new MindRaiderException("There is no property group!");
        }
    }

    /**
     * Remove notebook from folder.
     * 
     * @param notebookUri
     *            URI of the notebook
     * @throws Exception
     *             a generic exception
     */
    @SuppressWarnings("unchecked")
    public void removeNotebook(String notebookUri) throws Exception {
        if (notebookUri == null) {
            throw new MindRaiderException("Notebook URI can't be null!");
        }
        // presuming that there exist exactly one notebooks group
        ResourcePropertyGroup[] notebooks;
        try {
            notebooks = resource.getData().getPropertyGroup(
                    new URI(PROPERTY_GROUP_URI_NOTEBOOKS));
        } catch (URISyntaxException e) {
            logger.error("removeNotebook(String)", e);
            return;
        }

        if (notebooks != null) {
            ArrayList<NotebookProperty> arrProperties = notebooks[0].getProperties();
            Object notebookToDelete=null;
            for (NotebookProperty notebookProperty : arrProperties) {
                if (notebookUri.equals(notebookProperty.getUri().toString())) {
                    notebookToDelete=notebookProperty;
                    break;
                }
            }
            if(notebookToDelete!=null) {
                arrProperties.remove(notebookToDelete);
                logger.debug("Notebook removed: " + notebookUri);
           }
            notebooks[0].setProperties(arrProperties);
        }
    }

    /**
     * Move notebook up in the folder.
     * 
     * @param notebookUri
     *            the notebook uri
     * @return <code>true</code> if moved, else removed (should be placed into
     *         the previous folder
     * @throws Exception
     *             a generic exception
     */
    @SuppressWarnings("unchecked")
    public boolean upNotebook(String notebookUri) throws Exception {
        if (notebookUri == null) {
            throw new MindRaiderException("Notebook URI can't be null!");
        }
        // presuming that there exist exactly one notebooks group
        ResourcePropertyGroup[] notebooks = resource.getData()
                .getPropertyGroup(new URI(PROPERTY_GROUP_URI_NOTEBOOKS));
        if (notebooks != null) {
            // search for the notebook URI
            ArrayList<NotebookProperty> arrProperties = notebooks[0]
                    .getProperties();
            int i = 0;
            for (NotebookProperty notebookProperty : arrProperties) {
                if (notebookProperty.getUri().toASCIIString().equals(
                        notebookUri)) {
                    logger.debug(" Notebook found in properties...");
                    if (i > 0) {
                        arrProperties.remove(i);
                        arrProperties.add(i - 1, notebookProperty);
                        notebooks[0].setProperties(arrProperties);
                        return true;
                    }
                    // @todo inter folder moves of the folder not implemented
                    logger.debug("DELEEEEEEEEEEEEETE!");
                    return false;
                }
                i++;
            }
        } else {
            throw new MindRaiderException("There is no property group!");
        }
        return false;
    }

    /**
     * Move notebook down in the folder.
     * 
     * @param notebookUri
     *            the notebook uri String
     * @return <code>true</code> if moved, else removed (should be placed into
     *         the next folder)
     * @throws Exception
     *             a generic exception
     */
    @SuppressWarnings("unchecked")
    public boolean downNotebook(String notebookUri) throws Exception {
        if (notebookUri == null) {
            throw new MindRaiderException("Notebook URI can't be null!");
        }
        // presuming that there exist exactly one notebooks group
        ResourcePropertyGroup[] notebooks = resource.getData()
                .getPropertyGroup(new URI(PROPERTY_GROUP_URI_NOTEBOOKS));
        if (notebooks != null) {
            // search for the notebook URI
            ArrayList<NotebookProperty> arrProperties = notebooks[0]
                    .getProperties();
            int i = 0;
            for (NotebookProperty notebookProperty : arrProperties) {
                if (notebookProperty.getUri().toASCIIString().equals(
                        notebookUri)) {
                    logger.debug(" Notebook found in properties...");
                    if (i < arrProperties.size() - 1) {
                        arrProperties.remove(i);
                        arrProperties.add(i + 1, notebookProperty);
                        notebooks[0].setProperties(arrProperties);
                        return true;
                    }
                    logger.debug("DELETE!");
                    return false;
                }
            }
        } else {
            throw new MindRaiderException("There is no property group!");
        }
        return false;
    }

    /**
     * Save folder.
     * 
     * @throws Exception
     *             a generic exception
     */
    public void save() throws Exception {
        MindRaider.labelCustodian.save(resource);
    }

    /**
     * Getter for <code>resource</code>.
     * 
     * @return Returns the resource.
     */
    public Resource getResource() {
        return this.resource;
    }

    /**
     * Setter for <code>resource</code>.
     * 
     * @param resource
     *            The resource to set.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }
}