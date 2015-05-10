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
 * Annotation property.
 */
public class AnnotationProperty implements ResourceProperty {

	/**
	 * The element annotation constant.
	 */
	public static final String ELEMENT_ANNOTATION = "annotationProperty";

	/**
	 * The label String property.
	 */
	public static String label;

	/**
	 * The QName property.
	 */
	public static QName qname;

	static {
		qname = new QName("", ELEMENT_ANNOTATION);
		label = "Annotation property";
	}

	/**
	 * The annotation property.
	 */
	private String annotation;

	/**
	 * Constructor.
	 */
	public AnnotationProperty() {
	}

	/**
	 * Constructor.
	 * 
	 * @param annotation
	 *            the annotation property
	 */
	public AnnotationProperty(String annotation) {
		this();
		this.annotation = annotation;
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
		annotation = xpp.nextText();
		//cat.debug("  Annotation (from): " + annotation);
		// note that end element is pulled by nextText();
	}

	/**
	 * @see com.emental.mindraider.core.rest.properties.ResourceProperty#toXml(org.xmlpull.v1.XmlSerializer)
	 */
	public void toXml(XmlSerializer xs) throws Exception {
        //cat.debug("  Annotation (to): " + annotation);
		PullParsing.serializeTextElement(xs, ELEMENT_ANNOTATION, annotation);
	}

	/**
	 * Getter for <code>annotation</code>.
	 * 
	 * @return Returns the annotation.
	 */
	public String getAnnotation() {
		return this.annotation;
	}

	/**
	 * Setter for <code>annotation</code>.
	 * 
	 * @param annotation
	 *            The annotation to set.
	 */
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
}
