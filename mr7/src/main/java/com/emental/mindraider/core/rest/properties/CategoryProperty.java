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

import javax.xml.namespace.QName;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import com.mindcognition.mindraider.utils.PullParsing;

public class CategoryProperty implements ResourceProperty {

    /**
     * The element category constant.
     */
    public static final String ELEMENT_CATEGORY = "category";

    /**
     * The element caption constant.
     */
    public static final String ELEMENT_CAPTION = "caption";

    /**
     * The element value constant.
     */
    public static final String ELEMENT_VALUE = "value";

    /**
     * The label property.
     */
    private String label;

    /**
     * The QName property.
     */
    private QName qname;

    /**
     * The taxonomy property.
     */
    private URI taxonomy;

    /**
     * The category caption property.
     */
    public String categoryCaption;

    /**
     * The category value property.
     */
    private String categoryValue;

    /**
     * Constructor.
     */
    public CategoryProperty() {
        qname = new QName("", ELEMENT_CATEGORY);
        label = "Category";
    }

    /**
     * Constructor.
     * @param categoryUri the category Uri String
     */
    public CategoryProperty(String categoryUri) {
        this();

        this.categoryValue = categoryUri;
        this.categoryCaption = categoryUri;
    }

    /**
     * Constructor.
     * @param caption the caption
     * @param categoryUri the category Uri String
     */
    public CategoryProperty(String caption, String categoryUri)
    {
        this();

        this.categoryCaption = caption;
        this.categoryValue = categoryUri;
    }

    /**
     * @see com.emental.mindraider.core.rest.properties.ResourceProperty#getQName()
     */
    public QName getQName()
    {
        return qname;
    }

    /**
     * @see com.emental.mindraider.core.rest.properties.ResourceProperty#getLabel()
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * @see com.emental.mindraider.core.rest.properties.ResourceProperty#fromXml(org.xmlpull.v1.XmlPullParser)
     */
    public void fromXml(XmlPullParser xpp) throws Exception
    {
        // start element already parsed

        PullParsing.startElement(xpp, ELEMENT_CAPTION);
        categoryCaption = xpp.nextText();

        PullParsing.startElement(xpp, ELEMENT_VALUE);
        categoryValue = xpp.nextText();

        PullParsing.endElement(xpp, ELEMENT_CATEGORY);
    }

    /**
     * @see com.emental.mindrider.rest.properties.ResourceProperty#toXml(org.xmlpull.v1.XmlSerializer)
     */
    public void toXml(XmlSerializer xs) throws Exception
    {
        xs.startTag("", ELEMENT_CATEGORY);
        PullParsing.serializeTextElement(xs, ELEMENT_CAPTION, categoryCaption);
        PullParsing.serializeTextElement(xs, ELEMENT_VALUE, categoryValue);
        xs.endTag("", ELEMENT_CATEGORY);
    }

    /**
     * Getter for <code>categoryCaption</code>.
     * @return Returns the categoryCaption.
     */
    public String getCategoryCaption()
    {
        return this.categoryCaption;
    }

    /**
     * Setter for <code>categoryCaption</code>.
     * @param categoryCaption The categoryCaption to set.
     */
    public void setCategoryCaption(String categoryCaption)
    {
        this.categoryCaption = categoryCaption;
    }

    /**
     * Getter for <code>categoryValue</code>.
     * @return Returns the categoryValue.
     */
    public String getCategoryValue()
    {
        return this.categoryValue;
    }

    /**
     * Setter for <code>categoryValue</code>.
     * @param categoryValue The categoryValue to set.
     */
    public void setCategoryValue(String categoryValue)
    {
        this.categoryValue = categoryValue;
    }

    /**
     * Getter for <code>qname</code>.
     * @return Returns the qname.
     */
    public QName getQname()
    {
        return this.qname;
    }

    /**
     * Setter for <code>qname</code>.
     * @param qname The qname to set.
     */
    public void setQname(QName qname)
    {
        this.qname = qname;
    }

    /**
     * Getter for <code>taxonomy</code>.
     * @return Returns the taxonomy.
     */
    public URI getTaxonomy()
    {
        return this.taxonomy;
    }

    /**
     * Setter for <code>taxonomy</code>.
     * @param taxonomy The taxonomy to set.
     */
    public void setTaxonomy(URI taxonomy)
    {
        this.taxonomy = taxonomy;
    }

    /**
     * Setter for <code>label</code>.
     * @param label The label to set.
     */
    public void setLabel(String label)
    {
        this.label = label;
    }
    
    @Override
    public String toString() {
        return "[caption="+categoryCaption+"; value="+categoryValue+"]";
    }
}
