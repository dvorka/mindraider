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
package com.emental.mindraider;

import junit.framework.TestCase;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;

/**
 * MindRaiderVocabulary test class.
 */
public class MindRaiderVocabularyTest extends TestCase {

    /**
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * The getMindMapUri test case.
     */
    public void testGetMindMapUri() {
        String expectedUrl = "http://" + MindRaider.profile.getHostname()
                + "/e-mentality/mindmap#" + MindRaider.user.getName();
        String actualUrl = MindRaiderVocabulary.getMindMapUri();
        assertEquals("actualUrl is: " + actualUrl, expectedUrl, actualUrl);
    }

    /**
     * The getFolderUriSkeleton test case.
     */
    public void testGetFolderUriSkeleton() {
        String expectedFolder = "http://" + MindRaider.profile.getHostname()
                + "/e-mentality/folder#";
        String actualFolder = MindRaiderVocabulary.getFolderUriSkeleton();
        assertEquals("actualFolder is: " + actualFolder, expectedFolder,
                actualFolder);
    }

    /**
     * The getFolderUri test case.
     */
    public void testGetFolderUri() {
        String folderNcName = "testfolder";
        String expectedFolderUri = "http://" + MindRaider.profile.getHostname()
                + "/e-mentality/folder#" + folderNcName;
        String actualFolderUri = MindRaiderVocabulary.getFolderUriSkeleton()
                + folderNcName;
        assertEquals("actualFolderUri" + actualFolderUri, expectedFolderUri,
                actualFolderUri);
    }

    /**
     * The getNotebookUriSkeleton test case.
     */
    public void testGetNotebookUriSkeleton() {
        String expectedNotebookUriSkeleton = "http://"
                + MindRaider.profile.getHostname() + "/e-mentality/notebook#";
        String actualNotebookUriSkeleton = MindRaiderVocabulary
                .getNotebookUriSkeleton();
        assertEquals("actualNotebookUriSkeleton is: "
                + actualNotebookUriSkeleton, expectedNotebookUriSkeleton,
                actualNotebookUriSkeleton);
    }

    /**
     * The getNotebookUri test case.
     */
    public void testGetNotebookUri() {
        String notebookNcName = "test";
        String expectedNotebookUri = "http://"
                + MindRaider.profile.getHostname() + "/e-mentality/notebook#"
                + notebookNcName;
        String actualNotebookUri = MindRaiderVocabulary
                .getNotebookUri(notebookNcName);
        assertEquals("actualNotebookUri is: " + actualNotebookUri,
                expectedNotebookUri, actualNotebookUri);
    }

    /**
     * The isConceptUrl test case.
     */
    public void testIsConceptUrl() {
        String conceptUrl = "http://e-mentality/concept/conceptTest";
        assertEquals("conceptUrl is: " + conceptUrl, true, MindRaiderVocabulary
                .isConceptUri(conceptUrl));
    }

    /**
     * The isNotebookUri test case.
     */
    public void testIsNotebookUri() {
        String notebookUri = "http://e-mentality/notebook/test";
        assertEquals("notebookUri is: " + notebookUri, true,
                MindRaiderVocabulary.isNotebookUri(notebookUri));
    }

    /**
     * Note that this method is wrong - URIs should be opaque. public void
     * isMindRaiderResourceUriTest() { String uri = "... "; return
     * isNotebookUri(uri) || isConceptUri(uri) || (uri != null &&
     * uri.startsWith(MindRaiderConstants.MR_RDF_NS)); }
     */

    /**
     * The getConceptUriSkeleton test case.
     */
    public void testGetConceptUriSkeleton() {
        String notebookNcName = "test";
        String expectedConceptUriSkeleton = "http://"
                + MindRaider.profile.getHostname() + "/e-mentality/concept/"
                + notebookNcName + "#";
        String actualConceptUriSkeleton = MindRaiderVocabulary
                .getConceptUriSkeleton(notebookNcName);
        assertEquals("actualConceptUriSkeleton is:" + actualConceptUriSkeleton,
                expectedConceptUriSkeleton, actualConceptUriSkeleton);

    }

    /**
     * The getConceptUri test case.
     */
    public void testConceptUri() {
        String notebookNcName = "testNotebook";
        String conceptNcName = "testConcept";
        String expectedConceptUri = "http://"
                + MindRaider.profile.getHostname() + "/e-mentality/concept/"
                + notebookNcName + "#" + conceptNcName;
        String actualConceptUri = MindRaiderVocabulary
                .getConceptUriSkeleton(notebookNcName)
                + conceptNcName;
        assertEquals("actualConceptUri is: " + actualConceptUri,
                expectedConceptUri, actualConceptUri);
    }

    /**
     * The getResourceUriSkeleton test case.
     */
    public void testGetResourceUriSkeleton() {
        String expectedResourceUriSkeleton = "http://"
                + MindRaider.profile.getHostname() + "/e-mentality/";
        String actualResourceUriSkeleton = MindRaiderVocabulary
                .getResourceUriSkeleton();
        assertEquals("actualResourceUriSkeleton is: "
                + actualResourceUriSkeleton, expectedResourceUriSkeleton,
                actualResourceUriSkeleton);
    }

}
