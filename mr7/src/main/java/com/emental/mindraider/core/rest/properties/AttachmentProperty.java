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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import com.mindcognition.mindraider.utils.PullParsing;

/**
 * Attachment property.
 */
public class AttachmentProperty implements ResourceProperty {

    /**
     * The label String property.
     */
    public static String label;

    /**
     * The QName property.
     */
    public static QName qname;

    /**
     * The element attachment property.
     */
    public static final String ELEMENT_ATTACHMENT = "attachmentProperty";

    /**
     * The element description property.
     */
    public static final String ELEMENT_DESCRIPTION = "description";

    /**
     * The element url property.
     */
    public static final String ELEMENT_URL = "url";

    static
    {
        qname = new QName("", ELEMENT_ATTACHMENT);
        label = "Attachment property";
    }

    /**
     * The description property.
     */
    private String description;

    /**
     * The url property.
     */
    private String url;

    /**
     * Constructor.
     */
    public AttachmentProperty()
    {
    }

    /**
     * Constructor.
     * @param description the description
     * @param url the url
     */
    public AttachmentProperty(String description, String url)
    {
        this.description = description;
        this.url = url;
    }

    /**
     * Constructor.
     * @param url the url
     */
    public AttachmentProperty(String url)
    {
        this(url, url);
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

        PullParsing.startElement(xpp, ELEMENT_DESCRIPTION);
        description = new String(xpp.nextText().getBytes(), "UTF-8");
        PullParsing.startElement(xpp, ELEMENT_URL);
        url = xpp.nextText();

        PullParsing.endElement(xpp, ELEMENT_ATTACHMENT);
    }

    /**
     * @see com.emental.mindraider.core.rest.properties.ResourceProperty#toXml(org.xmlpull.v1.XmlSerializer)
     */
    public void toXml(XmlSerializer xs) throws Exception
    {
        xs.startTag("", ELEMENT_ATTACHMENT);

        if (description == null)
        {
            description = url;
        }
        PullParsing.serializeTextElement(xs, ELEMENT_DESCRIPTION, description);
        PullParsing.serializeTextElement(xs, ELEMENT_URL, url);

        xs.endTag("", ELEMENT_ATTACHMENT);
    }

    /**
     * Getter for <code>description</code>.
     * @return Returns the description.
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Setter for <code>description</code>.
     * @param description The description to set.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Getter for <code>url</code>.
     * @return Returns the url.
     */
    public String getUrl()
    {
        return this.url;
    }

    /**
     * Setter for <code>url</code>.
     * @param url The url to set.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }
}
