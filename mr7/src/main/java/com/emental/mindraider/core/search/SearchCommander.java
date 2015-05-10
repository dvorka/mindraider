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

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.Resource;
import com.emental.mindraider.core.rest.properties.CategoryProperty;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.emental.mindraider.ui.dialogs.SearchResultsJDialog;
import com.mindcognition.mindraider.MindRaiderException;
import com.mindcognition.mindraider.application.model.tag.TagEntryImpl;
import com.mindcognition.mindraider.application.model.tag.TaggedResourceEntry;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;

/**
 * Search functionality concentrated.
 */
public class SearchCommander {
    private static final Logger logger = Logger.getLogger(SearchCommander.class);

    private static String CONCEPTS_DIRECTORY_NAME = "concepts";

    private static final String action = Messages.getString("SearchCommander.addingFile")+"  ";
    
    /**
     * Initialize. Detect whether search index exists, if it doesn't exist, then
     * it is created.
     */
    public static void initialize() {
        File f = new File(getSearchIndexPath());
        if (!f.exists()) {
            rebuildSearchAndTagIndices();
        }
    }

    /**
     * Rebuild search index.
     */
    public static void rebuildSearchAndTagIndices() {
        logger.debug(Messages.getString("SearchCommander.reindexing",MindRaider.profile.getNotebooksDirectory()));

        Thread thread = new Thread() {
            public void run() {                
                try {
                    SearchCommander.rebuild(MindRaider.profile.getNotebooksDirectory());
                } catch (IOException e) {
                    logger.error(Messages.getString("SearchCommand.unableToRebuildSearchIndex"), e);
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Search all notebooks.
     * 
     * @param searchString
     *            the search string
     */
    public static void searchNotebooks(String searchString) {
        logger.debug(Messages.getString("SearchCommander.searchNotebooks", searchString));
        
        if (StringUtils.isNotEmpty(searchString)) {
            SearchResultEntry[] result = SearchCommander.search(searchString);

            // filter out what's shouldn't be there
            if (result != null) {
                ArrayList<SearchResultEntry> filtered = new ArrayList<SearchResultEntry>();
                for (SearchResultEntry searchResultElement : result) {
                    if (searchResultElement != null && searchResultElement.getPath().endsWith("rdf.xml")) {
                        continue;
                    }
                    filtered.add(searchResultElement);
                }

                if (filtered.size() > 0) {
                    // show result
                    new SearchResultsJDialog(Messages.getString("SearchCommander.ftsSearchResults"),filtered);
                } else {
                    StatusBar.show(Messages.getString("SearchCommander.searchStringNotFound", searchString));
                }
            }
            // TODO filter out rdf.xml and render clickable list demo ;-)
        }
    }
    
    /**
     * Generate search index.
     * 
     * @param directory
     *            the directory
     * @param rebuildSearchIndexJDialog
     *            the rebuild search index JDialog
     * @throws IOException
     *             the I/O exception
     */
    public static void rebuild(String directory) throws IOException {
        if (StringUtils.isNotEmpty(directory)) {
            Date start = new Date();
            try {
                // experiment in here with analyzers
                // SearchEngine searchEngine = new
                // SearchEngine(getSearchIndexPath());
                // searchEngine.rebuildIndex();
                IndexWriter writer = new IndexWriter(getSearchIndexPath(), new StandardAnalyzer(), true);
                
                // infiltrated tags - remove the tag information
                MindRaider.tagCustodian.clear();
                SearchCommander.indexDocs(writer, new File(directory));
                writer.optimize();
                writer.close();

                Date end = new Date();
                String finalMessage = "FTS index rebuilt in " + (end.getTime() - start.getTime()) + " milliseconds";
                StatusBar.setText(finalMessage);
            } catch (IOException e) {
                System.err.println(e.getClass() + ": " + e.getMessage());
            } finally {
                try {
                    MindRaider.tagCustodian.redraw(); 
                    MindRaider.tagCustodian.toRdf();
                } catch (MindRaiderException e) {
                    logger.error("Unable to save tag ontology!",e);
                }
            }
        }
    }

    /**
     * Index documents.
     * 
     * @param writer
     *            the index writer
     * @param file
     *            the file to write
     * @param rebuildSearchIndexJDialog
     *            the rebuild search index JDialog
     * @throws IOException
     *             the I/O exception
     */
    public static void indexDocs(IndexWriter writer, File file) throws IOException {
        // do not try to index files that cannot be read
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                // an IO error could occur
                if (files != null) {
                    for (String filename : files) {
                        indexDocs(writer, new File(file, filename));
                    }
                }
            } else {
                StatusBar.setText(action,file.getAbsolutePath(),70);

                try {
                    // I'm interested only in indexing of concepts
                    if (file.getAbsolutePath().indexOf(File.separator + CONCEPTS_DIRECTORY_NAME + File.separator) >= 0) {
                        ConceptResource conceptResource = new ConceptResource(new Resource(file.getAbsolutePath()));
                                                
                        // FTS index
                        // TODO parse notebook label from the path for now
                        String notebookUri = conceptResource.getNotebookUri();
                        String notebookLabel;
                        if (notebookUri != null && (notebookUri.indexOf("#") >= 0)) {
                            // TODO from time to time the last letter is killed
                            notebookLabel = notebookUri.substring(notebookUri.indexOf("#") + 1,
                                    notebookUri.length());
                            // TODO ugly hack - label must be loaded from the model (slow)
                            notebookLabel = notebookLabel.replaceAll("_", " ");
                        } else {
                            notebookLabel = "Notebook";
                        }

                        // tag (infiltrated)
                        CategoryProperty[] tagsAndFlag = conceptResource.getCategories();
                        if(tagsAndFlag!=null && tagsAndFlag.length>0) {
                            for(CategoryProperty tagOrFlag: tagsAndFlag) {
                                // only tags (not the flag!) are indexed
                                if(tagOrFlag.getCategoryValue()!=null && tagOrFlag.getCategoryValue().length()>0) {
                                    if(!tagOrFlag.getCategoryValue().startsWith(MindRaiderConstants.MR_OWL_FLAG_NS)) {
                                        MindRaider.tagCustodian.addOrInc(
                                                new TagEntryImpl(
                                                        tagOrFlag.getCategoryValue(),
                                                        tagOrFlag.getCategoryCaption(),
                                                        1),
                                                new TaggedResourceEntry(
                                                        notebookUri,
                                                        notebookLabel,
                                                        conceptResource.getUri(),
                                                        conceptResource.getLabel(),
                                                        conceptResource.resource.getMetadata().getTimestamp(),
                                                        file.getAbsoluteFile().getAbsolutePath()));
                                    }
                                }
                            }
                        }
                        
                        // write it to index
                        writer.addDocument(
                                FileDocument.Document(file,
                                notebookLabel,
                                conceptResource.getLabel(),
                                conceptResource.getUri()));
                    }
                } catch(EOFException e) {
                    logger.debug("Unable to read file "+file.getAbsolutePath(),e);
                }
                // at least on windows, some temporary files raise this
                // exception with an "access denied" message
                // checking if the file can be read doesn't help
                catch (Exception e) {
                    logger.debug("File not found!", e);
                }
            }
        }
    }

    /**
     * Return the search index path.
     * 
     * @return the index path
     */
    private static String getSearchIndexPath() {
        return MindRaider.profile.getHomeDirectory() + File.separator + "SearchIndex";
    }

    /**
     * Execute search.
     * 
     * @param queryString
     *            the query String
     * @return the search result entry array
     */
    public static SearchResultEntry[] search(String queryString) {
        SearchEngine searchEngine = new SearchEngine(getSearchIndexPath());
        SearchResultEntry[] results = searchEngine.search(queryString);
        searchEngine = null;
        return results;
    }

    public static void updateIndex(File conceptFile, String notebookLabel, String conceptLabel, String conceptUri) {
        // TODO the body of this method to by in asynchronous thread
        IndexWriter writer;
        try {
            writer = new IndexWriter(getSearchIndexPath(), new StandardAnalyzer(), false);
            // update document via concept URI
            logger.debug("UPDATing FTS index for concept: "+conceptFile+" # "+notebookLabel+" # "+conceptLabel+" # "+conceptUri); // {{debug}}
            Document document = FileDocument.Document(conceptFile,
                    notebookLabel,
                    conceptLabel,
                    conceptUri);
            writer.deleteDocuments(new Term("uri",conceptUri));
            writer.addDocument(document);
            // TODO removed just for now (before it will be done in async)
            //writer.optimize();
            writer.close();
        } catch (Exception e) {
            logger.debug("Unable to update FTS index",e); // {{debug}}
            // TODO close it in finally
        }
    }
    
    /**
     * Delete from index.
     * 
     * @param args
     *            params to delete.
     */
    public static void deleteFromIndex(String[] args) {
        new SearchEngine(getSearchIndexPath());
        //searchEngine.deleteFromIndex(args);
    }
}
