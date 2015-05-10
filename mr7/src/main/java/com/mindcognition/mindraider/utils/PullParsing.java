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
package com.mindcognition.mindraider.utils;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class PullParsing {

	/**
	 * Parse start of an element.
	 * 
	 * @param xpp
	 * @param elementName
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static void startElement(XmlPullParser xpp, String elementName)
			throws XmlPullParserException, IOException {
		xpp.nextTag();
		xpp.require(XmlPullParser.START_TAG, "", elementName);
	}

	/**
	 * Parse the end of an element.
	 * 
	 * @param xpp
	 *            the xpp parser.
	 * @param elementName
	 *            the element name.
	 * @throws XmlPullParserException
	 *             the xpp parser exception.
	 * @throws IOException
	 *             the I/O exception.
	 */
	public static void endElement(XmlPullParser xpp, String elementName)
			throws XmlPullParserException, IOException {
		xpp.nextTag();
		xpp.require(XmlPullParser.END_TAG, "", elementName);
	}

	/**
	 * Serialize element with textual content only.
	 * 
	 * @param xs
	 *            the xml serializer.
	 * @param elementName
	 *            the element name.
	 * @param text
	 *            the text.
	 * @throws Exception
	 *             a generic exception.
	 */
	public static void serializeTextElement(XmlSerializer xs,
			String elementName, String text) throws Exception {
		xs.startTag("", elementName);
		// xs.text(new String(text.getBytes("UTF-8")));
		xs.text(text);
		xs.endTag("", elementName);
	}
}