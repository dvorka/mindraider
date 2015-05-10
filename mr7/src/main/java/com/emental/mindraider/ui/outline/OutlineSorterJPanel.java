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
package com.emental.mindraider.ui.outline;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.core.rest.properties.CategoryProperty;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.emental.mindraider.ui.outline.treetable.OutlineTreeInstance;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class OutlineSorterJPanel extends JPanel {
    private SorterTableModel tableModel;
    private JTable table;

    public static final int COLUMN_URI= -1;
    public static final int COLUMN_NAME = 0;
    public static final int COLUMN_ANNOTATION = 1;
    public static final int COLUMN_CATEGORY= 2;
    public static final int COLUMN_REVISION = 3;
    public static final int COLUMN_MODIFIED = 4;
    public static final int COLUMN_CREATED= 5;
    
    public OutlineSorterJPanel() {
        setLayout(new BorderLayout());
                        
        // table with archived concepts (title)
        // let table model to load discarded concepts itself
        tableModel = new SorterTableModel();
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(new SorterListSelectionListener(table,tableModel));
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.getColumnModel().getColumn(COLUMN_NAME).setPreferredWidth(150);
        table.getColumnModel().getColumn(COLUMN_ANNOTATION).setPreferredWidth(220);
        table.getColumnModel().getColumn(COLUMN_CREATED).setPreferredWidth(60);
        table.getColumnModel().getColumn(COLUMN_MODIFIED).setPreferredWidth(60);
        table.getColumnModel().getColumn(COLUMN_REVISION).setPreferredWidth(35);
        
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
        table.setRowSorter(sorter);        
        sorter.setComparator(COLUMN_REVISION, new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return o2.intValue()-o1.intValue();
            }
        });
        final Comparator<String> timestampComparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(OutlineTreeInstance.getCreatedTimestampFromHtml(o2)>OutlineTreeInstance.getCreatedTimestampFromHtml(o1)) {
                    return 1;
                } else {
                    if(OutlineTreeInstance.getCreatedTimestampFromHtml(o2)==OutlineTreeInstance.getCreatedTimestampFromHtml(o1)) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            }
        };
        sorter.setComparator(COLUMN_MODIFIED, timestampComparator);
        sorter.setComparator(COLUMN_CREATED, timestampComparator);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        add(scroll,BorderLayout.CENTER);
    }
    
    public void refresh() {
        // outline custodian - get discarded models
        if(MindRaider.profile!=null) {
            final URI activeOutlineUri = MindRaider.profile.getActiveOutlineUri();
            if(tableModel!=null) {
                if(activeOutlineUri!=null) {
                    tableModel.refresh(activeOutlineUri.toString());
                } else {
                    tableModel.clear();
                }
            }
        }
        ((AbstractTableModel)table.getModel()).fireTableDataChanged();
        table.updateUI();
    }

    private static final long serialVersionUID = 5958552481049265993L;
}

class SorterTableModel extends AbstractTableModel {
    private static final Log logger = LogFactory.getLog(SorterTableModel.class); // {{debug}}

    private String[] columnNames = {"Title","Annotation","Category","Revision","Modified","Created"};
    public ResourceDescriptor[] activeConcepts=null;
    public HashMap<String, ResourceDescriptor> activeConceptsByUri=new HashMap<String, ResourceDescriptor>();
    
    public SorterTableModel() {
    }

    public void clear() {
        activeConcepts=null;
    }

    public int refresh(String outlineUri) {
        Model rdfModel = MindRaider.outlineCustodian.getActiveOutlineResource().rdfModel.getModel();
        activeConcepts=MindRaider.outlineCustodian.getNonDiscardedConceptDescriptors();
        
        if(activeConcepts!=null) {
            for (int i = 0; i < activeConcepts.length; i++) {
                Resource rdfResource = rdfModel.getResource(activeConcepts[i].getUri());
                activeConcepts[i] = MindRaider.outlineCustodian.getRdfResourceDescriptor(rdfResource);  
                // load the concept XML and complete the descriptor with the details
                try {
                    ConceptResource conceptResource 
                        = MindRaider.noteCustodian.get(outlineUri, activeConcepts[i].getUri());
                    activeConcepts[i].setRevision(conceptResource.resource.getMetadata().getRevision());
                    activeConcepts[i].setModified(conceptResource.resource.getMetadata().getTimestamp());
                    CategoryProperty[] categories = conceptResource.getCategories();
                    if(categories!=null && categories.length>0) {
                        activeConcepts[i].setCategory(categories[0].categoryCaption);
                    } else {
                        activeConcepts[i].setCategory("");
                    }
                } catch (Exception e) {
                    logger.error("Unable to load concept XML: "+e); // {{debug}}
                    activeConcepts[i].setRevision(1);
                    activeConcepts[i].setModified(System.currentTimeMillis());
                    activeConcepts[i].setCategory("");
                }
                activeConceptsByUri.put(activeConcepts[i].getUri(), activeConcepts[i]);
            }        	
        }

        return getRowCount();
    }

    protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader() {
            public String getToolTipText(MouseEvent e) {
                java.awt.Point p = e.getPoint();
                int index = columnModel.getColumnIndexAtX(p.x);
                int realIndex = columnModel.getColumn(index).getModelIndex();
                return columnNames[realIndex];
            }
            private static final long serialVersionUID = -3219707005673982727L;
        };
    }    
    
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public int getRowCount() {
        return (activeConcepts==null?0:activeConcepts.length);
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        if(activeConcepts==null || activeConcepts.length<=row) {
            return null;
        } else {
            switch (col) {
            case OutlineSorterJPanel.COLUMN_URI:
                return activeConcepts[row].getUri();
            case OutlineSorterJPanel.COLUMN_NAME:
                return activeConcepts[row].getLabel();
            case OutlineSorterJPanel.COLUMN_ANNOTATION:
                return OutlineTreeInstance.getAnnotationToRender(activeConcepts[row].getAnnotationCite(), null);
            case OutlineSorterJPanel.COLUMN_MODIFIED:
                return OutlineTreeInstance.getCreatedToRender(activeConcepts[row].getModified());
            case OutlineSorterJPanel.COLUMN_CREATED:
                return OutlineTreeInstance.getCreatedToRender(activeConcepts[row].getCreated());
            case OutlineSorterJPanel.COLUMN_REVISION:
                return new Long(activeConcepts[row].getRevision());
            case OutlineSorterJPanel.COLUMN_CATEGORY:
                return ""+activeConcepts[row].getCategory();
            default:
                return "";
            }
        }
    }
    
    @Override
    public boolean isCellEditable(int arg0, int arg1) {
        return false;
    }

    private static final long serialVersionUID = 1L;
}

class SorterListSelectionListener implements ListSelectionListener {

    private JTable table;
    private SorterTableModel outlineSorterJPanel;

    public SorterListSelectionListener(JTable table, SorterTableModel tableModel) {
        this.table=table;
        this.outlineSorterJPanel=tableModel;
    }
    
    @Override
    public void valueChanged(ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) {
            int selectedRow = table.getSelectedRow();
            if(selectedRow>=0) {
                // map the row to the model (column sorters may change it)
                selectedRow=table.convertRowIndexToModel(selectedRow);
                
                if(outlineSorterJPanel.activeConcepts!=null && outlineSorterJPanel.activeConcepts.length>selectedRow) {
                    OutlineJPanel.getInstance().loadConcept(
                            MindRaider.profile.getActiveOutlineUri().toString(),
                            (String)outlineSorterJPanel.getValueAt(selectedRow, OutlineSorterJPanel.COLUMN_URI));
                    OutlineJPanel.getInstance().refresh();
                }
                return;
            }
        }
    }
};
