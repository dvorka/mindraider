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
package com.emental.mindraider.ui.table;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import com.emental.mindraider.core.rest.resource.AttachmentResource;

/**
 * Table model holding actual data - (model/view/controller). JTable is just
 * renderer.
 */
public class AttachmentsTableModel extends AbstractTableModel {
    public static final int COLUMN_COUNT = 2;

    private ArrayList<Object[]> tableRows;

    /**
     * attachments
     */
    public AttachmentResource[] attachments;

    /**
     * Constructor.
     */
    public AttachmentsTableModel() {
        tableRows = new ArrayList<Object[]>();
    }

    /**
     * Add an attachment.
     * 
     * @todo type will be constant transformed to image in here.
     * @param type
     *            the ImageIcon
     * @param description
     *            the description string
     */
    public void addAttachment(ImageIcon type, String description) {
        tableRows.add(new Object[] { type, description });
        fireTableDataChanged();
    }

    public void removeAllAttachments() {
        tableRows.clear();
        fireTableDataChanged();
    }

    private String[] columnNames = new String[] { "Type", "Description" };

    public String getColumnName(int col) {
        return columnNames[col].toString();
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    public int getRowCount() {
        return tableRows.size();
    }

    public Object getValueAt(int row, int col) {
        return ((Object[]) tableRows.get(row))[col];
    }

    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void setValueAt(Object value, int row, int col) {
    }
    
    private static final long serialVersionUID = -1872695968714537745L;
}
