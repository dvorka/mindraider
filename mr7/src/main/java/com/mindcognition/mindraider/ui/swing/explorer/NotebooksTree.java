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
package com.mindcognition.mindraider.ui.swing.explorer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;

public class NotebooksTree extends JTree {
    public static final int LEVEL_ROOT = 2;
    public static final int LEVEL_NOTEBOOKS = 1;

    private DefaultMutableTreeNode notebooksRootNode;
    private DefaultTreeModel notebooksTreeModel;
    private NotebooksTreeToolbar notebooksToolbar;

    private HashSet<String> notebookUris;
    
    public NotebooksTree() {
        // TODO bundle
        notebookUris=new HashSet<String>();
        notebooksRootNode = new DefaultMutableTreeNode(new NotebookNodeUserObject("Outlines",0,null));
        
        notebooksTreeModel = new DefaultTreeModel(notebooksRootNode);
        setModel(notebooksTreeModel);
        
        setEditable(false);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        addTreeExpansionListener(new TreeExpansionListenerImplementation());
        addTreeWillExpandListener(new TreeWillExpandListenerImplementation());
        setShowsRootHandles(true);
        
        setCellRenderer(new OutlinesTreeCellRenderer());
        
        // tree node selection listener
        addTreeSelectionListener(new NotebooksTreeSelectionListener(this));
        
        reloadModel();
    }

    /**
     * Remove all nodes except the root node.
     */
    public void clear() {
        notebooksRootNode.removeAllChildren();
        notebooksTreeModel.reload();
        validate();
    }
    
    public void reloadModel() {
        reloadModel(null);
    }
    
    public void reloadFilteredModel(String text) {
        reloadModel(null, text);
    }
    
    public void reloadModel(String labelUri) {
        reloadModel(labelUri, null);
    }

    private void reloadModel(String labelUri, String notebookPrefix) {
        clear();
        notebookUris.clear();
        
        ResourceDescriptor[] labelDescriptors 
            = MindRaider.labelCustodian.getLabelDescriptors();

        if (!ArrayUtils.isEmpty(labelDescriptors)) {
            for (ResourceDescriptor folderDescriptor : labelDescriptors) {
                String folderUri = folderDescriptor.getUri();

                if(labelUri==null || (labelUri!=null && labelUri.equals(folderUri))) {
                    // insert notebooks
                    ResourceDescriptor[] notebookDescriptors 
                        = MindRaider.labelCustodian.getOutlineDescriptors(folderUri);
                    if (notebookDescriptors != null) {
                        for (ResourceDescriptor notebookDescriptor : notebookDescriptors) {
                            if(notebookPrefix==null ||
                                    (notebookPrefix!=null && notebookDescriptor.getLabel().toLowerCase().startsWith(notebookPrefix.toLowerCase()))) {                                
                                addNodeToTree(notebookDescriptor.getLabel(),notebookDescriptor.getUri());
                            }
                        }
                    }
                }
            }            
            // now expand 0 rows - 0 row is enough (sub nodes has no children)
            expandRow(0);
            setSelectionRow(0);
            
            ((NotebookNodeUserObject)notebooksRootNode.getUserObject()).setNotebooksConcepts(getRowCount()-1);
        }        
        validate();
    }
    
    private DefaultMutableTreeNode addNodeToTree(String notebookLabel, String notebookUri) {
        if(notebookUris.contains(notebookUri)) {
           return null; 
        } else {
            notebookUris.add(notebookUri);
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new NotebookNodeUserObject(notebookLabel,0,notebookUri));
            notebooksTreeModel.insertNodeInto(childNode, notebooksRootNode, notebooksRootNode.getChildCount());
            return childNode;
        }
    }
    
    private static final long serialVersionUID = -3144461544967163845L;

    public DefaultMutableTreeNode getNotebooksRootNode() {
        return notebooksRootNode;
    }

    public boolean isRoot(DefaultMutableTreeNode node) {
        return (notebooksRootNode.equals(node)?true:false);
    }

    public void setToolbar(NotebooksTreeToolbar notebooksToolbar) {
        this.notebooksToolbar=notebooksToolbar;
    }

    public NotebooksTreeToolbar getToolbar() {
        return notebooksToolbar;
    }
}

class NotebookNodeUserObject {
    
    private String title;
    private String notebookUri;
    private int notebooksConcepts;
    
    public NotebookNodeUserObject(String title, int notebooksConcepts, String notebookUri) {
        this.title=title;
        this.notebookUri=notebookUri;
        this.notebooksConcepts=notebooksConcepts;
    }
    
    public void setNotebooksConcepts(int notebooksConcepts) {
        this.notebooksConcepts=notebooksConcepts;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getNotebookUri() {
        return notebookUri;
    }
    
    @Override
    public String toString() {
        return title+(notebooksConcepts>0?" ("+notebooksConcepts+")":"");
    }
}

class NotebooksTreeSelectionListener implements TreeSelectionListener {
    private static final Log logger = LogFactory.getLog(LabelsTreeSelectionListener.class); // {{debug}}
    
    private NotebooksTree notebooksTree;

    public NotebooksTreeSelectionListener(NotebooksTree notebooksTree) {
        this.notebooksTree=notebooksTree;
    }
    
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)notebooksTree.getLastSelectedPathComponent();
        if (node != null && !notebooksTree.isRoot(node)) {
            logger.debug("Outlines tree selection path: "+ node.getPath()[node.getLevel()]);
            if(node.getUserObject() instanceof NotebookNodeUserObject) {
                NotebookNodeUserObject notebookNode=(NotebookNodeUserObject)node.getUserObject();
                // TODO bundle
                StatusBar.show("Outline " + notebookNode.getNotebookUri() + " selected...");

                try {
                    OutlineJPanel.getInstance().conceptJPanel.clear();
                    MindRaider.profile.setActiveOutlineUri(null);
                    MindRaider.outlineCustodian.loadOutline(new URI(notebookNode.getNotebookUri()));
                    OutlineJPanel.getInstance().refresh();
                } catch (URISyntaxException e1) {
                    // TODO option with question whether to remove non-existent notebook
                    logger.debug("Unable to load Outline: ", e1);
                }
            }
            if(notebooksTree.getToolbar()!=null) {
                notebooksTree.getToolbar().enableDisableToolbarButtons(NotebooksTree.LEVEL_NOTEBOOKS);
            }
        } else {
            if(notebooksTree.getToolbar()!=null) {
                notebooksTree.getToolbar().enableDisableToolbarButtons(NotebooksTree.LEVEL_ROOT);
            }
        }
    }
}
