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

import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

public class Xsl {
	private static final Logger cat = Logger.getLogger(Xsl.class);

	/**
	 * Apply XSL stylesheet.
	 * 
	 * @param inputFile
	 * @param outputFile
	 * @param xslFile
	 */
	public static void xsl(String inputFile, String outputFile, String xslFile) {
		cat.debug("=-> XSL stylesheeting: " + inputFile + " -> " + xslFile + " -> "
				+ outputFile);
		try {
			TransformerFactory factory = TransformerFactory.newInstance();

			Templates templates = factory.newTemplates(new StreamSource(
					new FileInputStream(xslFile)));

			Transformer transformer = templates.newTransformer();

			FileInputStream fileInputStream = new FileInputStream(inputFile);
			Source source = new StreamSource(fileInputStream);
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
			Result result = new StreamResult(fileOutputStream);

			transformer.transform(source, result);
			cat.debug("Transformed!");

			fileOutputStream.flush();
			fileOutputStream.close();
			fileInputStream.close();
		} catch (Exception e) {
			cat.error("Unable to stylesheet!", e);
		} finally {
		}
	}
}