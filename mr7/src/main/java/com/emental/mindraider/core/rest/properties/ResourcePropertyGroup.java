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
package com.emental.mindraider.core.rest.properties;

import java.net.URI;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/**
 * Resource property group.
 */
public class ResourcePropertyGroup implements ResourceProperty {

    /**
     * The element property group constant.
     */
    public static final String ELEMENT_PROPERTY_GROUP = "propertyGroup";

    /**
     * The attribute uri constant.
     */
    public static final String ATTRIBUTE_URI = "uri";

    /**
     * The attribute label constant.
     */
    public static final String ATTRIBUTE_LABEL = "label";

    /**
     * property group qname.
     */
    public static QName qname;

    /**
     * Logger for this class.
     */
    private static final Logger cat = Logger.getLogger(ResourcePropertyGroup.class);

    /**
     * propety group label.
     */
    private String label;

    /**
     * property group type.
     */
    private URI uri;

    /**
     * property group properties.
     */
    private ArrayList<ResourceProperty> properties;

    static {
        qname = new QName("", ELEMENT_PROPERTY_GROUP);
    }

    /**
     * Constructor.
     */
    public ResourcePropertyGroup() {
        properties = new ArrayList<ResourceProperty>();
    }

    /**
     * Constructor.
     * 
     * @param label
     *            the String label
     * @param uri
     *            the URI
     */
    public ResourcePropertyGroup(String label, URI uri) {
        this();
        this.label = label;
        this.uri = uri;
    }

    /**
     * @see com.emental.mindraider.core.rest.properties.ResourceProperty#getLabel()
     */
    public String getLabel() {
        return label;
    }

    /**
     * @see com.emental.mindraider.core.rest.properties.ResourceProperty#getQName()
     */
    public QName getQName() {
        return qname;
    }

    /**
     * @see com.emental.mindraider.core.rest.properties.ResourceProperty#fromXml(org.xmlpull.v1.XmlPullParser)
     */
    public void fromXml(XmlPullParser xpp) throws Exception {
        // parse attributes
        if (xpp.getAttributeCount() == 2) {
            uri = new URI(xpp.getAttributeValue("", ATTRIBUTE_URI));
            label = xpp.getAttributeValue("", ATTRIBUTE_LABEL);
            cat.debug("Got property group: " + label + " (" + uri + ")");
            return;
        }
        throw new XmlPullParserException("Invalid number of attributes in "
                + xpp.getName());
    }

    /**
     * Get property group type URI.
     * 
     * @return property group type URI.
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Add resource property into the group.
     * 
     * @param resourceProperty
     *            resource property to be added.
     */
    public void addProperty(ResourceProperty resourceProperty) {
        properties.add(resourceProperty);
    }
    
    public void removeProperty(ResourceProperty resourceProperty) {
        properties.remove(resourceProperty);
    }

    /**
     * Clear all properties from the group.
     */
    public void clear() {
        properties.clear();
    }

    /**
     * @see com.emental.mindrider.rest.properties.ResourceProperty#toXml(org.xmlpull.v1.XmlSerializer)
     */
    public void toXml(XmlSerializer xs) throws Exception {
        xs.startTag("", ELEMENT_PROPERTY_GROUP);
        xs.attribute("", ATTRIBUTE_URI, uri.toASCIIString());
        xs.attribute("", ATTRIBUTE_LABEL, label);

        // now write group's properties
        for (ResourceProperty resource : properties) {
            resource.toXml(xs);
        }

        xs.endTag("", ELEMENT_PROPERTY_GROUP);
    }

    /**
     * Getter for <code>properties</code>.
     * 
     * @return Returns the properties.
     */
    @SuppressWarnings("unchecked")
    public ArrayList getProperties() {
        return this.properties;
    }

    /**
     * Setter for <code>properties</code>.
     * 
     * @param properties
     *            The properties to set.
     */
    @SuppressWarnings("unchecked")    
    public void setProperties(ArrayList properties) {
        this.properties = properties;
    }

    /**
     * Setter for <code>label</code>.
     * 
     * @param label
     *            The label to set.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Setter for <code>uri</code>.
     * 
     * @param uri
     *            The uri to set.
     */
    public void setUri(URI uri) {
        this.uri = uri;
    }
}