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
package com.emental.mindraider.ui.combos;

import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;

/**
 * Search combo box history model.
 */
public class SearchComboModel extends DefaultComboBoxModel {
	/**
     * The search history array list.
     */
    private ArrayList<String> searchHistory;

    /**
     * Constructor.
     */
    public SearchComboModel() {
        searchHistory = new ArrayList<String>();
    }

    /**
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        return searchHistory.size();
    }

    /**
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int arg0) {
        return searchHistory.get(arg0);
    }

    /**
     * Add an item to history.
     * 
     * @param searchString
     *            The string element to add.
     */
    public void addHistoryItem(String searchString) {
        searchHistory.add(0, searchString);
        fireContentsChanged(searchString, 0, searchHistory.size());
    }

    private static final long serialVersionUID = -3965788815700193585L;
}