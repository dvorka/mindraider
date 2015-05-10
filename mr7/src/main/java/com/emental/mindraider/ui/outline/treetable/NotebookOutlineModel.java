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
package com.emental.mindraider.ui.outline.treetable;

import javax.swing.treetable.DynamicTreeTableModel;
import javax.swing.treetable.TreeTableModel;

import com.emental.mindraider.ui.outline.NotebookOutlineDirectory;

/**
 * Notebook outline model. In fact this class is tree table adaptor from
 * Notebook's RDF model.
 */
public class NotebookOutlineModel extends DynamicTreeTableModel {

    public static final int COLUMN_LABEL = 0;

    public static final int COLUMN_ANNOTATION = 1;

    public static final int COLUMN_CREATED = 2;

    /**
     * Names of the columns.
     */
    public static final String[] columnNames = { "Title", "Annotation", "Created" };

    /**
     * Method names used to access the data to display.
     */
    private static final String[] methodNames = { "getLabel", "getAnnotation", "getCreated" };

    /**
     * Method names used to set the data.
     */
    private static final String[] setterMethodNames = { "setLabel", "setAnnotation", "setCreated" };

    /**
     * Classes presenting the data.
     */
    private static final Class[] classes = { TreeTableModel.class, String.class, String.class };

    /**
     * Constructor.
     *
     * @param root
     */
    public NotebookOutlineModel(NotebookOutlineDirectory root) {
        super(root, columnNames, methodNames, setterMethodNames, classes);
    }

    /**
     * <code>isCellEditable</code> is invoked by the JTreeTable to determine
     * if a particular entry can be added. This is overridden to return true for
     * the first column, assuming the node isn't the root, as well as returning
     * two for the second column if the node is a BookmarkEntry. For all other
     * columns this returns false.
     */
    public boolean isCellEditable(Object node, int column) {
        switch (column) {
        case 0:
            /*
             * WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING
             * WARNING This COLUMN must remain always editable! otherwise it is
             * not possible to expand the tree node! WARNING WARNING WARNING
             * WARNING WARNING WARNING WARNING WARNING WARNING
             */
            // Allow editing of the name, as long as not the root.
            return (node != getRoot());
        case 1:
            // Allow editing of the location, as long as not a
            // directory
            // return (node instanceof
            // NotebookOutlineTreeInstance.NotebookOutlineEntry);
            return false;
        default:
            // Don't allow editing of the date fields.
            return false;
        }
    }
}
