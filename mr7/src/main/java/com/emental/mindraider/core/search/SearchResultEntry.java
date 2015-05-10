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

/**
 * Search result entry.
 */
public class SearchResultEntry {

	/**
	 * The notebook label.
	 */
	private String notebookLabel;

	/**
	 * The concept label.
	 */
	private String conceptLabel;

	/**
	 * The path.
	 */
	private String path;

	/**
	 * Constructor.
	 * 
	 * @param outlineLabel
	 *            the notebook label
	 * @param conceptLabel
	 *            the concept label
	 * @param path
	 *            the path
	 */
	public SearchResultEntry(String notebookLabel, String conceptLabel,
			String path) {
		this.notebookLabel = notebookLabel;
		this.conceptLabel = conceptLabel;
		this.path = path;
	}

	/**
	 * Getter for <code>conceptLabel</code>.
	 * 
	 * @return Returns the conceptLabel.
	 */
	public String getConceptLabel() {
		return this.conceptLabel;
	}

	/**
	 * Setter for <code>conceptLabel</code>.
	 * 
	 * @param conceptLabel
	 *            The conceptLabel to set.
	 */
	public void setConceptLabel(String conceptLabel) {
		this.conceptLabel = conceptLabel;
	}

	/**
	 * Getter for <code>outlineLabel</code>.
	 * 
	 * @return Returns the outlineLabel.
	 */
	public String getNotebookLabel() {
		return this.notebookLabel;
	}

	/**
	 * Setter for <code>outlineLabel</code>.
	 * 
	 * @param outlineLabel
	 *            The outlineLabel to set.
	 */
	public void setNotebookLabel(String notebookLabel) {
		this.notebookLabel = notebookLabel;
	}

	/**
	 * Getter for <code>path</code>.
	 * 
	 * @return Returns the path.
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Setter for <code>path</code>.
	 * 
	 * @param path
	 *            The path to set.
	 */
	public void setPath(String path) {
		this.path = path;
	}
}
