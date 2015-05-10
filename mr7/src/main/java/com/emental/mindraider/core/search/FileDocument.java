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
package com.emental.mindraider.core.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * An utility for making Lucene Documents from a File.
 */
public final class FileDocument {

	/**
	 * Makes a document for a File.
	 * <p>
	 * The document has three fields:
	 * <ul>
	 * <li><code>path</code> containing the pathname of the file, as a
	 * stored, tokenized field;
	 * <li><code>modified</code> containing the last modified date of the
	 * file as a keyword field as encoded by <a
	 * href="lucene.document.DateField.html">DateField</a>; and
	 * <li><code>contents</code> containing the full contents of the file, as
	 * a Reader field;
	 * </ul>
	 */
	public static Document Document(
                File f, 
                String notebookLabel,
                String conceptLabel,
                String conceptUri) throws java.io.FileNotFoundException {

		// make a new, empty lucene document
		Document doc = new Document();

        // no assemble the document from fields - some of them will be searchable,
        // others will be available in the result (as document attributes) i.e. stored in the index
        Field field;

        // concept URI as attribute - used to delete the document
        field = new Field("uri", conceptUri, Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(field);
        // path as attribute
        field = new Field("path", f.getPath(), Field.Store.YES, Field.Index.NO);
        doc.add(field);
        // SEARCHABLE concept label 
        field = new Field("conceptLabel", conceptLabel, Field.Store.YES, Field.Index.TOKENIZED);
        doc.add(field);
        // notebook label attribute 
        field = new Field("outlineLabel",notebookLabel,Field.Store.YES, Field.Index.NO);
        doc.add(field);
        // timestamp as attribute
        field = new Field("modified",DateTools.timeToString(f.lastModified(), DateTools.Resolution.SECOND),Field.Store.YES, Field.Index.NO);
        doc.add(field);

        // concept annotation - the most important
		FileInputStream is = new FileInputStream(f);
		Reader reader = new BufferedReader(new InputStreamReader(is));
        field = new Field("contents", reader);
        doc.add(field);

		// return the document
		return doc;
	}

    // default constructor forbidden
	private FileDocument() {
	}
}
