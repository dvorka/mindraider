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
package com.emental.mindraider.core.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.namespace.QName;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import com.emental.mindraider.core.rest.properties.PropertyQName2ClassMap;
import com.emental.mindraider.core.rest.properties.ResourceProperty;
import com.emental.mindraider.core.rest.properties.ResourcePropertyGroup;
import com.mindcognition.mindraider.MindRaiderException;
import com.mindcognition.mindraider.utils.PullParsing;

/**
 * Resource data - set of properties.
 */
public class Data {

    /*
     * TODO property groups are not implemented for now
     */
    
    /**
     * Xml element data constant.
     */
    public static final String ELEMENT_DATA = "data";

    /**
     * Xml property group constant.
     */
    public static final String ELEMENT_PROPERTY_GROUP = "propertyGroup";

    /**
     * Hash of resource properties, key is property QName. Entry of the
     * hashtable is array list of properties.
     */
    public Hashtable<QName,ArrayList<ResourceProperty>> properties;

    /**
     * Hash of resource propety groups, key is group URI. Entry of the hashtable
     * is array list of property groups.
     */
    private Hashtable<URI,ArrayList<ResourcePropertyGroup>> propertyGroups;

    /**
     * Constructor.
     */
    public Data() {
        properties = new Hashtable<QName, ArrayList<ResourceProperty>>();
        propertyGroups = new Hashtable<URI, ArrayList<ResourcePropertyGroup>>();
    }

    /**
     * From XML.
     * 
     * @param xpp
     *            the xml pull parser.
     * @throws Exception
     *             a generic exception.
     */
    public void fromXml(XmlPullParser xpp) throws Exception {
        PullParsing.startElement(xpp, ELEMENT_DATA);

        // while properties are known to the e-mentality config, fetch them
        String elementName;
        ResourceProperty property;
        ResourcePropertyGroup propertyGroup = null;
        while (true) {
            xpp.nextTag();
            elementName = xpp.getName();
            // cat.debug("Parsing element: "+elementName);
            if (ELEMENT_DATA.equals(elementName)
                    && xpp.getEventType() == XmlPullParser.END_TAG) {
                break;
            }

            if (xpp.getEventType() == XmlPullParser.START_TAG) {
                // check whether it is property group
                if (ELEMENT_PROPERTY_GROUP.equals(xpp.getName())) {
                    if (propertyGroup == null) {
                        propertyGroup = new ResourcePropertyGroup();
                        propertyGroup.fromXml(xpp);
                        // continue with properties
                        continue;
                    }
                    throw new XmlPullParserException(
                            "Resource property groups can't be recursive: "
                                    + xpp.getLineNumber() + "/"
                                    + xpp.getColumnNumber());
                }

                // if it's not grup, try to find property in the registry
                property = 
                    PropertyQName2ClassMap.getInstance(new QName(xpp.getNamespace(), xpp.getName()));
                // cat.debug("Instance: "+property);
                if(property==null) {
                    // it is an unknown property - skip it
                    xpp.nextText();
                    continue;
                } else {
                    property.fromXml(xpp);
                }

                if (propertyGroup != null) {
                    propertyGroup.addProperty(property);
                } else {
                    addProperty(property);
                }
            } else {
                // either it is property group closing element or invalid XML
                if (propertyGroup != null
                        && ELEMENT_PROPERTY_GROUP.equals(xpp.getName())) {
                    addPropertyGroup(propertyGroup);
                    propertyGroup = null;
                } else {
                    throw new XmlPullParserException(
                            "Expected a start element, but found end element "
                                    + xpp.getName());
                }
            }
        }
        xpp.require(XmlPullParser.END_TAG, "", ELEMENT_DATA);
    }

    /**
     * Add resource property to properties hash map.
     * 
     * @param property
     *            the resource property.
     * @throws Exception
     *             a generic exception.
     */
    public synchronized void addProperty(ResourceProperty property)
            throws RuntimeException {
        if (property == null) {
            throw new MindRaiderException("Resource property can't be null!");
        }
        // cat.debug("Adding property: "+property.getLabel()+"
        // ("+property.getQName()+")");
        ArrayList<ResourceProperty> propertiesList;
        if ((propertiesList = properties.get(property.getQName())) == null) {
            propertiesList = new ArrayList<ResourceProperty>();
            properties.put(property.getQName(), propertiesList);
        }
        propertiesList.add(property);
    }

    /**
     * Add resource property group to the groups hash map.
     * 
     * @param propertyGroup
     *            the resource property group.
     * @throws Exception
     *             a generic exception.
     */
    public synchronized void addPropertyGroup(
            ResourcePropertyGroup propertyGroup) throws Exception {
        if (propertyGroup == null) {
            throw new XmlPullParserException(
                    "Resource property group can't be null!");
        }
        // cat.debug("Adding property group: "+propertyGroup.getLabel()+"
        // ("+propertyGroup.getQName()+")");
        ArrayList<ResourcePropertyGroup> propertyGroupsList;
        URI propertyGroupUri = propertyGroup.getUri();
        if ((propertyGroupsList = propertyGroups
                .get(propertyGroupUri)) == null) {
            propertyGroupsList = new ArrayList<ResourcePropertyGroup>();
            propertyGroups.put(propertyGroupUri, propertyGroupsList);
        }
        propertyGroupsList.add(propertyGroup);
    }

    /**
     * Return a synchronized resource property group array.
     * 
     * @param uri
     *            the Uri.
     * @return the resource property group array.
     */
    public synchronized ResourcePropertyGroup[] getPropertyGroup(URI uri) {
        if (uri != null) {
            ArrayList<ResourcePropertyGroup> arrayList = (ArrayList<ResourcePropertyGroup>) propertyGroups.get(uri);
            if (arrayList != null) {
                return arrayList.toArray(new ResourcePropertyGroup[arrayList.size()]);
            }
        }
        return null;
    }

    /**
     * To XML.
     * 
     * @param xs
     *            the xml serializer.
     * @throws Exception
     *             a generic exception.
     */
    public void toXml(XmlSerializer xs) throws Exception {
        xs.startTag("", ELEMENT_DATA);

        // serialize properties and then property groups
        ArrayList<ResourceProperty>[] arraysResourceProperties = (ArrayList[])properties.values().toArray(new ArrayList[properties.size()]);
        for (ArrayList<ResourceProperty> arrayProperty : arraysResourceProperties) {
            if (arrayProperty != null) {
                for (ResourceProperty resource : arrayProperty) {
                    resource.toXml(xs);
                }
            }
        }

        // ...and groups
        ArrayList<ResourceProperty>[] resourcePropertyGroups = (ArrayList[]) propertyGroups.values().toArray(new ArrayList[properties.size()]);
        for (ArrayList<ResourceProperty> arrayProperty : resourcePropertyGroups) {
            if (arrayProperty != null) {
                for (ResourceProperty resource : arrayProperty) {
                    resource.toXml(xs);
                }
            }
        }
        xs.endTag("", ELEMENT_DATA);
    }

    /**
     * Getter for <code>properties</code>.
     * 
     * @return Returns the properties.
     */
    public Hashtable<QName,ArrayList<ResourceProperty>> getProperties() {
        return this.properties;
    }

    /**
     * Setter for <code>properties</code>.
     * 
     * @param properties
     *            The properties to set.
     */
    public void setProperties(Hashtable<QName,ArrayList<ResourceProperty>> properties) {
        this.properties = properties;
    }

    /**
     * Getter for <code>propertyGroups</code>.
     * 
     * @return Returns the propertyGroups.
     */
    public Hashtable<URI,ArrayList<ResourcePropertyGroup>> getPropertyGroups() {
        return this.propertyGroups;
    }

    /**
     * Setter for <code>propertyGroups</code>.
     * 
     * @param propertyGroups
     *            The propertyGroups to set.
     */
    public void setPropertyGroups(Hashtable<URI,ArrayList<ResourcePropertyGroup>> propertyGroups) {
        this.propertyGroups = propertyGroups;
    }
}