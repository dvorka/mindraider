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

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;

/**
 * A utility class to manage Lucene Search Engine and remove dependencies of it
 * from MindRaider UI.
 */
public class SearchEngine {
	private static final Logger logger = Logger.getLogger(SearchEngine.class);

	private String indexPath;

	/**
	 * Constructor.
	 * 
	 * @param indexPath
	 *            the index path
	 */
	public SearchEngine(String indexPath) {
		this.indexPath = indexPath;
	}

	/**
	 * Execute search using index.
	 * 
	 * @param indexPath
	 *            the index path
	 * @param queryString
	 *            the query string
	 * @return Returns an array of <code>SearchResultEntry</code> otherwise
	 *         null.
	 */
	public SearchResultEntry[] search(String queryString) {
		ArrayList<SearchResultEntry> result = new ArrayList<SearchResultEntry>();

		try {
			Searcher searcher = new IndexSearcher(indexPath);
			Analyzer analyzer = new StandardAnalyzer();

			String line = queryString;

			logger.debug("Query: " + line);
            QueryParser queryParser=new QueryParser("contents", analyzer);
            Query query = queryParser.parse(line);
            
			logger.debug("\nSearching for: '" + query.toString("contents")
					+ "'");
			Hits hits = searcher.search(query);
			logger.debug("Search result: " + hits.length()
					+ " total matching documents");

			for (int i = 0; i < hits.length(); i++) {
				Document doc = hits.doc(i);
				String path = doc.get("path");

				if (path != null) {
					logger.debug(i + ". " + path);
					// i'm interested only in concepts so filter out
					// non-concepts (HACK)
					// TODO this filter is here because of obsolete indexes -
					// there should be deleted
					if (path.indexOf(File.separator + "concepts"
							+ File.separator) >= 0) {
						result.add(new SearchResultEntry(doc
								.get("outlineLabel"), doc.get("conceptLabel"),
								doc.get("path")));
//						 logger.debug("path:\n"+doc.get("path"));
//						 logger.debug("modified:\n"+doc.get("modified"));
//						 logger.debug("notebook:\n"+doc.get("outlineLabel"));
//						 logger.debug("concept:\n"+doc.get("conceptLabel"));
					}

				} else {
					String url = doc.get("url");
					if (url != null) {
						logger.debug(i + ". " + url);
						logger.debug("   - " + doc.get("title"));
					} else {
						logger.debug(i + ". "
								+ "No path nor URL for this document");
					}
				}
			}
			searcher.close();

			return (SearchResultEntry[]) result
					.toArray(new SearchResultEntry[result.size()]);
		} catch (Exception e) {
			logger.error("Caught a " + e.getClass() + "\n with message: "
					+ e.getMessage(), e);
		}
		return null;
	}
}