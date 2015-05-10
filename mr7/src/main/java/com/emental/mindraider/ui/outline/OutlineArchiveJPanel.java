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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.emental.mindraider.ui.outline.treetable.OutlineTreeInstance;

public class OutlineArchiveJPanel extends JPanel {

    JButton undoButton;
    JButton deleteButton;
    JButton purgeButton;
    
    JTable table;
    private ArchiveTableModel tableModel;

    public OutlineArchiveJPanel() {
        setLayout(new BorderLayout());
        
        // table with archived concepts (title)
        // let table model to load discarded concepts itself
        tableModel = new ArchiveTableModel(this);
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        JScrollPane scroll = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        
        
        JToolBar toolbar = new JToolBar();
        
        undoButton = new JButton("", IconsRegistry.getImageIcon("trashUndo.png"));
        undoButton.setEnabled(false);
        undoButton.setToolTipText("Restore Note");
        undoButton.addActionListener(new UndiscardConceptActionListener(tableModel,table));
        toolbar.add(undoButton);

        deleteButton = new JButton("", IconsRegistry
                .getImageIcon("explorerDeleteSmall.png"));
        deleteButton.setEnabled(true);
        deleteButton.setToolTipText("Delete Note");
        deleteButton.addActionListener(new DeleteConceptActionListener(tableModel,table));
        toolbar.add(deleteButton);
        
        purgeButton = new JButton("", IconsRegistry.getImageIcon("trashFull.png"));
        purgeButton.setEnabled(true);
        purgeButton.setToolTipText("Empty Notes Archive");
        purgeButton.addActionListener(new EmptyArchiveActionListener());
        toolbar.add(purgeButton);

        add(toolbar,BorderLayout.NORTH);
                
        add(scroll,BorderLayout.CENTER);
    }
    
    public int refresh() {        
        // outline custodian - get discarded models
        if(MindRaider.profile!=null) {
            final URI activeOutlineUri = MindRaider.profile.getActiveOutlineUri();
            if(tableModel!=null) {
                if(activeOutlineUri!=null) {
                    ((AbstractTableModel)table.getModel()).fireTableDataChanged();
                    table.updateUI();        
                    return tableModel.refresh(activeOutlineUri.toString());
                } else {
                    tableModel.clear();
                }
            }
        }
        ((AbstractTableModel)table.getModel()).fireTableDataChanged();                
        table.updateUI();        
        return 0;
    }

    private static final long serialVersionUID = 5958552481049265993L;
}

class ArchiveTableModel extends AbstractTableModel {
    private String[] columnNames = {"Title","Annotation", "Created"};
    private ResourceDescriptor[] discardedConcepts=null;
    private OutlineArchiveJPanel archive;

    public ArchiveTableModel(OutlineArchiveJPanel archive) {
        this.archive=archive;
    }

    public void clear() {
        discardedConcepts=null;
    }

    public int refresh(String outlineUri) {
        discardedConcepts=MindRaider.outlineCustodian.getDiscardedConceptDescriptors(outlineUri);
        
        int rowCount = ((ArchiveTableModel)(archive.table).getModel()).getRowCount();
        if(rowCount<=0) {
            archive.purgeButton.setEnabled(false);
            archive.deleteButton.setEnabled(false);
            archive.undoButton.setEnabled(false);
        } else {
            archive.purgeButton.setEnabled(true);
            archive.deleteButton.setEnabled(true);
            archive.undoButton.setEnabled(true);
        }
        
        return getRowCount();
    }

    public ResourceDescriptor[] getDiscardedConcepts() {
        return discardedConcepts;
    }
    
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return (discardedConcepts==null?0:discardedConcepts.length);
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        if(discardedConcepts==null || discardedConcepts.length<=row) {
            return null;
        } else {
            switch (col) {
            case 0:
                return discardedConcepts[row].getLabel();
            case 1:
                return OutlineTreeInstance.getAnnotationToRender(discardedConcepts[row].getAnnotationCite(), null);
            default:
                return OutlineTreeInstance.getCreatedToRender(discardedConcepts[row].getCreated());
            }
        }
    }
    
    @Override
    public boolean isCellEditable(int arg0, int arg1) {
        return false;
    }

    private static final long serialVersionUID = 1L;
}

class UndiscardConceptActionListener implements ActionListener {
    private static final Log logger = LogFactory.getLog(UndiscardConceptActionListener.class); // {{debug}}

    private ArchiveTableModel tableModel;
    private JTable table;

    public UndiscardConceptActionListener(ArchiveTableModel tableModel, JTable table) {
        this.tableModel=tableModel;
        this.table=table;
    }

    public void actionPerformed(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(
                MindRaider.mainJFrame,
                "Do you really want to restore this Note?",
                "Restore Note", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            // determine selected concept parameters
            int selectedRow = table.getSelectedRow();
            if(selectedRow>=0) {
                // map the row to the model (column sorters may change it)
                selectedRow=table.convertRowIndexToModel(selectedRow);

                if(tableModel.getDiscardedConcepts()!=null && tableModel.getDiscardedConcepts().length>selectedRow) {
                    try {
                        MindRaider.noteCustodian.undiscard(
                                MindRaider.profile.getActiveOutlineUri().toString(), 
                                tableModel.getDiscardedConcepts()[selectedRow].getUri());
                    } catch (Exception e1) {
                        logger.debug("Unable to restore the note!",e1); // {{debug}}
                    }
                    OutlineJPanel.getInstance().refresh();
                }
                return;                    
            }
        }
    }
}

class DeleteConceptActionListener implements ActionListener {
    private ArchiveTableModel tableModel;
    private JTable table;

    public DeleteConceptActionListener(ArchiveTableModel tableModel, JTable table) {
        this.tableModel=tableModel;
        this.table=table;
    }

    public void actionPerformed(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(
                MindRaider.mainJFrame,
                "Do you really want to delete this Note?",
                "Delete Note", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            // determine selected concept parameters
            int selectedRow = table.getSelectedRow();
            if(selectedRow>=0) {
                // map the row to the model (column sorters may change it)
                selectedRow=table.convertRowIndexToModel(selectedRow);
                                        
                    if(tableModel.getDiscardedConcepts()!=null && tableModel.getDiscardedConcepts().length>selectedRow) {
                        MindRaider.noteCustodian.deleteConcept(
                                MindRaider.profile.getActiveOutlineUri().toString(), 
                                tableModel.getDiscardedConcepts()[selectedRow].getUri());
                        OutlineJPanel.getInstance().refresh();
                    }
                return;                    
            }
        }
    }
}

class EmptyArchiveActionListener implements ActionListener {

    public EmptyArchiveActionListener() {
    }
    
    public void actionPerformed(ActionEvent e) {
        int result = JOptionPane
        .showConfirmDialog(
                MindRaider.mainJFrame,
                "Do you really want to delete all discarded Notes?",
                "Empty Archive", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            MindRaider.noteCustodian.deleteDiscardedConcepts(MindRaider.profile.getActiveOutlineUri().toString());
            OutlineJPanel.getInstance().refresh();
        }
    }
}