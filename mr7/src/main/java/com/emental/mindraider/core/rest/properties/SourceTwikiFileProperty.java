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
 * Source TWiki file property. This property is set on outline that were
 * imported from TWiki in order to know where to save such resource back (to
 * which file).
 */
public class SourceTwikiFileProperty implements ResourceProperty {

    public static final String ELEMENT_SOURCE_TWIKI_FILE = "sourceTwikFileProperty";
    public static final String ELEMENT_URI = "path";
    public static String label;
    public static QName qname;

    private String path;

    static {
        qname = new QName("", ELEMENT_SOURCE_TWIKI_FILE);
        label = "Source TWiki file property";
    }

    public SourceTwikiFileProperty() {
    }

    public SourceTwikiFileProperty(String path) {
        this();
        this.path = path;
    }

    public QName getQName() {
        return qname;
    }

    public String getLabel() {
        return label;
    }

    public void fromXml(XmlPullParser xpp) throws Exception {
        // start element already parsed

        PullParsing.startElement(xpp, ELEMENT_URI);
        path = xpp.nextText();

        PullParsing.endElement(xpp, ELEMENT_SOURCE_TWIKI_FILE);
    }

    public void toXml(XmlSerializer xs) throws Exception {
        xs.startTag("", ELEMENT_SOURCE_TWIKI_FILE);
        PullParsing.serializeTextElement(xs, ELEMENT_URI, path);
        xs.endTag("", ELEMENT_SOURCE_TWIKI_FILE);
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
