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

/**
 * Notebook property.
 */
public class NotebookProperty implements ResourceProperty {

    /**
     * The element notebook property constant.
     */
    public static final String ELEMENT_NOTEBOOK = "notebookProperty";

    /**
     * The element uri constant.
     */
    public static final String ELEMENT_URI = "uri";

    /**
     * The label string property.
     */
    public static String label;

    /**
     * The qname property.
     */
    public static QName qname;

    static
    {
        qname = new QName("", ELEMENT_NOTEBOOK);
        label = "Notebook property";
    }

    /**
     * The URI property.
     */
    private URI uri;

    /**
     * Constructor.
     */
    public NotebookProperty()
    {
    }

    /**
     * Constructor.
     * @param uri the URI object
     */
    public NotebookProperty(URI uri)
    {
        this();
        this.uri = uri;
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

        PullParsing.startElement(xpp, ELEMENT_URI);
        uri = new URI(xpp.nextText());

        PullParsing.endElement(xpp, ELEMENT_NOTEBOOK);
    }

    /**
     * @see com.emental.mindraider.core.rest.properties.ResourceProperty#toXml(org.xmlpull.v1.XmlSerializer)
     */
    public void toXml(XmlSerializer xs) throws Exception
    {
        xs.startTag("", ELEMENT_NOTEBOOK);
        PullParsing.serializeTextElement(xs, ELEMENT_URI, uri.toASCIIString());
        xs.endTag("", ELEMENT_NOTEBOOK);
    }


    /**
     * Getter for <code>uri</code>.
     * @return Returns the uri.
     */
    public URI getUri()
    {
        return this.uri;
    }


    /**
     * Setter for <code>uri</code>.
     * @param uri The uri to set.
     */
    public void setUri(URI uri)
    {
        this.uri = uri;
    }
}