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
package com.emental.mindraider.ui.dnd;

import org.apache.log4j.Logger;

/**
 * Representation of the link/file/... droped to the MindRaider window.
 * 
 * @author Martin.Dvorak
 */
public class DragAndDropReference {

    /**
     * Logger for this class.
     */
    private static final Logger cat = Logger
            .getLogger(DragAndDropReference.class);

    /**
     * The browser link constant.
     */
    public static final int BROWSER_LINK = 1;

    /**
     * The explorer link constant.
     */
    public static final int EXPLORER_LINK = 2;

    /**
     * The title.
     */
    private String title;

    /**
     * The reference.
     */
    private String reference;

    /**
     * The type.
     */
    private int type;

    /**
     * Constructor.
     * 
     * @param reference
     *            the reference.
     * @param type
     *            the type.
     */
    public DragAndDropReference(String reference, int type) {
        this(reference, reference, type);
    }

    /**
     * Constructor.
     * 
     * @param title
     *            the title.
     * @param reference
     *            the reference.
     * @param type
     *            the type.
     */
    public DragAndDropReference(String title, String reference, int type) {
        this.title = title;
        this.reference = reference;
        this.type = type;
    }

    /**
     * Debug values.
     */
    public void debug() {
        cat.debug("DnD: '" + title + "', '" + reference + "'");
    }

    /**
     * Getter for <code>reference</code>.
     * 
     * @return Returns the reference.
     */
    public String getReference() {
        return this.reference;
    }

    /**
     * Setter for <code>reference</code>.
     * 
     * @param reference
     *            The reference to set.
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Getter for <code>title</code>.
     * 
     * @return Returns the title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Setter for <code>title</code>.
     * 
     * @param title
     *            The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for <code>type</code>.
     * 
     * @return Returns the type.
     */
    public int getType() {
        return this.type;
    }

    /**
     * Setter for <code>type</code>.
     * 
     * @param type
     *            The type to set.
     */
    public void setType(int type) {
        this.type = type;
    }
}