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

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import com.mindcognition.mindraider.utils.PullParsing;

/**
 * The LabelProperty.
 */
public class LabelProperty implements ResourceProperty {

    /**
     * The element label property.
     */
    public static final String ELEMENT_LABEL = "labelProperty";

    /**
     * The label property.
     */
    public static String label;

    /**
     * The qname property.
     */
    public static QName qname;

    /**
     * Logger for this class.
     */
    private static final Logger cat = Logger.getLogger(LabelProperty.class);

    static {
        qname = new QName("", ELEMENT_LABEL);
        label = "Label property";
    }

    /**
     * The label content property.
     */
    private String labelContent;

    /**
     * Constructor.
     */
    public LabelProperty() {
        qname = new QName("", ELEMENT_LABEL);
        label = "Label property";
    }

    /**
     * Constructor.
     * 
     * @param labelContent
     *            the label content
     */
    public LabelProperty(String labelContent) {
        this();
        this.labelContent = labelContent;
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
        labelContent = xpp.nextText();
        cat.debug("  Label property text: " + labelContent);
        // note that end element is pulled by nextText();
    }

    /**
     * @see com.emental.mindraider.core.rest.properties.ResourceProperty#toXml(org.xmlpull.v1.XmlSerializer)
     */
    public void toXml(XmlSerializer xs) throws Exception {
        PullParsing.serializeTextElement(xs, ELEMENT_LABEL, labelContent);
    }

    /**
     * Getter for <code>labelContent</code>.
     * 
     * @return Returns the labelContent.
     */
    public String getLabelContent() {
        return this.labelContent;
    }

    /**
     * Setter for <code>labelContent</code>.
     * 
     * @param labelContent
     *            The labelContent to set.
     */
    public void setLabelContent(String labelContent) {
        this.labelContent = labelContent;
    }
}