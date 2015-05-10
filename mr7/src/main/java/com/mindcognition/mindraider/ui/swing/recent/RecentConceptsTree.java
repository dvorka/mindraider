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
package com.mindcognition.mindraider.ui.swing.recent;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.emental.mindraider.ui.outline.OutlineJPanel;

/**
 * Transient in-memory (lost on MR exit) list of recently accessed concepts.
 */
public class RecentConceptsTree extends JTree {
    
    /**
     * tree node root
     */
    private DefaultMutableTreeNode rootNode;
    
    /**
     * tree model
     */
    private DefaultTreeModel model;
    
    
    private Hashtable<String, DefaultMutableTreeNode> nonRedundantConcepts;
    private ArrayList<DefaultMutableTreeNode> orderedConcepts;
    
    /**
     * Load all the taxonomies and build the default tree.
     */
    public RecentConceptsTree() {
        // in memory history
        nonRedundantConcepts=new Hashtable<String, DefaultMutableTreeNode>();
        orderedConcepts=new ArrayList<DefaultMutableTreeNode>();
        
        rootNode = new DefaultMutableTreeNode("Recent Concepts");
        model = new DefaultTreeModel(rootNode);
        setModel(model);
        
        setEditable(true);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setShowsRootHandles(true);

        setCellRenderer(new RecentConceptsTreeCellRenderer(IconsRegistry.getImageIcon("history.png")));
        
        MindRaider.recentConcepts=this;
        
        final JTree self=this;
        
        // click handler
        addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)self.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                ConceptInfo conceptToLoad=(ConceptInfo)node.getUserObject();
                
                // move it to the first position
                addRecentConcept(conceptToLoad.conceptName, conceptToLoad.conceptUri, conceptToLoad.notebookName, conceptToLoad.notebookUri);
                
                OutlineJPanel.getInstance().loadConcept(conceptToLoad.notebookUri, conceptToLoad.conceptUri);
            }
        });        
    }

    public void addRecentConcept(String conceptName, String conceptUri, String notebookName, String notebookUri) {
        String key=conceptName+" ("+notebookName+")";
        DefaultMutableTreeNode existingNode;
        if((existingNode=nonRedundantConcepts.get(key))==null) {
            // add new node to the array
            DefaultMutableTreeNode treeNode
                =new DefaultMutableTreeNode(new ConceptInfo(conceptName, conceptUri, notebookName, notebookUri));
            orderedConcepts.add(0, treeNode);
            nonRedundantConcepts.put(key, treeNode);
        } else {
            // move the existing node to the first position
            moveNodeToTheHead(existingNode);
        }
        
        refreshListInUi();
    }

    private void refreshListInUi() {
        rootNode.removeAllChildren();
        model.reload();
        for (int i = 0; i < orderedConcepts.size(); i++) {
            rootNode.add(orderedConcepts.get(i));                        
        }
        expandRow(0);
    }

    private void moveNodeToTheHead(DefaultMutableTreeNode existingNode) {
        orderedConcepts.remove(existingNode);
        orderedConcepts.add(0, existingNode);
    }

    public void moveOneNoteBack() {
        if(orderedConcepts.size()>1) {
            ConceptInfo conceptInfo = (ConceptInfo)orderedConcepts.get(1).getUserObject();
            OutlineJPanel.getInstance().loadConcept(conceptInfo.notebookUri, conceptInfo.conceptUri);
            moveNodeToTheHead(orderedConcepts.get(1));
            refreshListInUi();
        }
    }
    
    private static class ConceptInfo {
        public String conceptName;
        public String notebookName;
        public String conceptUri;
        public String notebookUri;

        public ConceptInfo(String conceptName, String conceptUri, String notebookName, String notebokUri) {
            this.conceptName = conceptName;
            this.notebookName = notebookName;
            this.conceptUri = conceptUri;
            this.notebookUri = notebokUri;
        }

        public String toString() {
            return conceptName+ " ("+notebookName+")";
        }
    }    
       
    private static final long serialVersionUID = 2402735044513487755L;
}
