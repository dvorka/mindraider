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

public class AnnotationContentTypeProperty implements ResourceProperty {

    /**
     * logger
     */
    private static final Logger cat
        =Logger.getLogger(AnnotationContentTypeProperty.class);

    public static final String ELEMENT_ANNOTATION_CONTENT_TYPE_URI="annotationContentType";

    /*
     * common fields
     */

    public static String label;
    public static QName qname;

    static {
        qname=new QName("",ELEMENT_ANNOTATION_CONTENT_TYPE_URI);
        label="Annotation content type property";
    }

    /*
     * extra fields
     */

    public String contentType;

    /**
     * Constructor.
     */
    public AnnotationContentTypeProperty() {
    }

    /**
     * Constructor.
     */
    public AnnotationContentTypeProperty(String contentType) {
        this();
        this.contentType=contentType;
    }

    /* (non-Javadoc)
     * @see com.emental.mindraider.rest.properties.ResourceProperty#getQName()
     */
    public QName getQName() {
        return qname;
    }

    /* (non-Javadoc)
     * @see com.emental.mindraider.rest.properties.ResourceProperty#getLabel()
     */
    public String getLabel() {
        // TODO Auto-generated method stub
        return label;
    }

    /* (non-Javadoc)
     * @see com.emental.mindraider.rest.properties.ResourceProperty#fromXml(org.xmlpull.v1.XmlPullParser)
     */
    public void fromXml(XmlPullParser xpp) throws Exception {
        contentType=xpp.nextText();
        cat.debug("  Annotation content type: "+contentType);
        // note that end element is pulled by nextText();
    }

    /* (non-Javadoc)
     * @see com.emental.mindraider.rest.properties.ResourceProperty#toXml(org.xmlpull.v1.XmlSerializer)
     */
    public void toXml(XmlSerializer xs) throws Exception {
        PullParsing.serializeTextElement(xs,ELEMENT_ANNOTATION_CONTENT_TYPE_URI,contentType);
    }
}
