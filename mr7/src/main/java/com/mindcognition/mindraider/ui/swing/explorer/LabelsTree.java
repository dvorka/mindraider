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

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.Metadata;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.core.rest.resource.FolderResource;
import com.mindcognition.mindraider.application.model.label.LabelCustodianListener;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;

/**
 * Tree of labels (formerly notebooks).
 */
public class LabelsTree extends JTree implements LabelCustodianListener {
    
    private static final Log logger = LogFactory.getLog(LabelsTree.class); // {{debug}}

    private DefaultMutableTreeNode labelsRootNode;
    private DefaultTreeModel labelsTreeModel;

    public LabelsTree(NotebooksTree notebooksTree) {
        labelsRootNode = new DefaultMutableTreeNode(new LabelNodeUserObject(Messages.getString("ExplorerJPanel.rootNode"),333,null));

        labelsTreeModel = new DefaultTreeModel(labelsRootNode);
        labelsTreeModel.addTreeModelListener(new LabelsTreeModelListener());
        setModel(labelsTreeModel);
        
        setEditable(false);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        addTreeExpansionListener(new TreeExpansionListenerImplementation());
        addTreeWillExpandListener(new TreeWillExpandListenerImplementation());
        setShowsRootHandles(true);

        // tree rendered
        setCellRenderer(new LabelsTreeCellRenderer());

        // subscribe for folder custodian events e.g. folder creation
        MindRaider.labelCustodian.subscribe(this);
    
        // tree node selection listener
        addTreeSelectionListener(new LabelsTreeSelectionListener(this,notebooksTree));
        
        reloadModel();
    }
    
    /**
     * Remove all nodes except the root node.
     */
    public void clear() {
        labelsRootNode.removeAllChildren();
        labelsTreeModel.reload();
        validate();
    }
    
    public void reloadModel() {
        reloadModel(null);
    }
    
    /**
     * Every node contains folder URI as user object. If you have a node,
     * you may get the URI just by asking for user object. To find the node
     * for the particular folder URI, you have to traverse the tree.
     */
    public void reloadModel(String filter) {
        clear();
        
        ResourceDescriptor[] labelDescriptors 
            = MindRaider.labelCustodian.getLabelDescriptors();

        if (!ArrayUtils.isEmpty(labelDescriptors)) {
            for (ResourceDescriptor folderDescriptor : labelDescriptors) {
                String folderUri = folderDescriptor.getUri();
                if(filter==null ||
                        (filter!=null && folderDescriptor.getLabel().toLowerCase().startsWith(filter.toLowerCase()))) {
                    addNodeToTree(folderDescriptor.getLabel(), folderUri);
                }
            }

            // now expand 0 rows - 0 row is enough (sub nodes has no children)
            expandRow(0);
            setSelectionRow(0);
            
            ((LabelNodeUserObject)labelsRootNode.getUserObject()).setNotebooksWithLabels(getRowCount()-1);
        }
        validate();
    }
    
    private DefaultMutableTreeNode addNodeToTree(String label, String labelUri) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new NotebookNodeUserObject(label,0,labelUri));
        labelsTreeModel.insertNodeInto(childNode, labelsRootNode, labelsRootNode.getChildCount());
        return childNode;
    }
 
    public void labelCreated(FolderResource folderResource) {
        Metadata meta = folderResource.getResource().getMetadata();
        logger.debug("Folder created: " + meta.getUri().toASCIIString());
        // handle creation of the folder
        addNodeToTree(folderResource.getLabel(), meta.getUri().toString());
        
        reloadModel();
    }
    
    private static final long serialVersionUID = 1561237974383327190L;
}

class TreeExpansionListenerImplementation implements TreeExpansionListener {
    private static final Log logger = LogFactory.getLog(TreeExpansionListenerImplementation.class); // {{debug}}

    public void treeCollapsed(TreeExpansionEvent e) {
        logger.debug("Tree colapsed event..." + e.getPath());
    }

    public void treeExpanded(TreeExpansionEvent e) {
        logger.debug("Tree expanded event..." + e.getPath());
    }
}

class TreeWillExpandListenerImplementation implements TreeWillExpandListener  {
    private static final Log logger = LogFactory.getLog(TreeWillExpandListenerImplementation.class); // {{debug}}

    public void treeWillCollapse(TreeExpansionEvent e)
            throws ExpandVetoException {
        logger.debug("Tree will collapse " + e.getPath());
    }

    public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
        logger.debug("Tree will expand " + e.getPath());
    }
}

class LabelsTreeModelListener implements TreeModelListener {

    public static final int LEVEL_ROOT = 0;
    public static final int LEVEL_LABELS = 1;
    
    private final Logger logger = Logger.getLogger(LabelsTreeModelListener.class);

    public void treeNodesChanged(TreeModelEvent e) {
        DefaultMutableTreeNode node=(DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());

        /*
         * If the event lists children, then the changed node is the child
         * of the node we've already gotten. Otherwise, the changed node and
         * the specified node are the same.
         */
        // TODO
        try {
            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode) (node.getChildAt(index));

            // TODO RENAME NOT IMPLEMENTED investigate how is the node stored into the user object
/*            
            if (node.getLevel() == LEVEL_LABELS) {
                MindRaider.folderCustodian.rename(
                        (String) getTreeNodeToResourceUriMap().get(node),
                        (String) node.getUserObject());
            } else {
                if (node.getLevel() == LEVEL_NOTEBOOKS) {
                    MindRaider.notebookCustodian.rename(
                            (String) getTreeNodeToResourceUriMap()
                                    .get(node), (String) node
                                    .getUserObject());
                    MindRaider.profile.setActiveNotebookUri(null);
                    MindRaider.notebookCustodian.loadNotebook(new URI(
                            (String) getTreeNodeToResourceUriMap()
                                    .get(node)));
                    NotebookJPanel.getInstance().refresh();
                }
            }
*/            
        } catch (Exception ex) {
            logger.debug("Unable to found node!", ex);
        }

        logger.debug("New value: " + node.getUserObject());
    }

    public void treeNodesInserted(TreeModelEvent e) {
    }
    public void treeNodesRemoved(TreeModelEvent e) {
    }
    public void treeStructureChanged(TreeModelEvent e) {
    }
}

class LabelNodeUserObject {
    
    private String title;
    private String labelUri;
    private int notebooksWithLabel;
    
    public LabelNodeUserObject(String title, int notebooksWithLabel, String labelUri) {
        this.title=title;
        this.labelUri=labelUri;
        this.notebooksWithLabel=notebooksWithLabel;
    }
    
    public void setNotebooksWithLabels(int notebooksWithLabel) {
        this.notebooksWithLabel=notebooksWithLabel;
    }

    public String getLabelUri() {
        return labelUri;
    }
    
    @Override
    public String toString() {
        return title+(notebooksWithLabel>0?" ("+notebooksWithLabel+")":"");
    }
}

class LabelsTreeSelectionListener implements TreeSelectionListener {
    private static final Log logger = LogFactory.getLog(NotebooksTreeSelectionListener.class); // {{debug}}
    
    private LabelsTree labelsTree;
    private NotebooksTree notebooksTree;

    public LabelsTreeSelectionListener(LabelsTree labelsTree, NotebooksTree notebooksTree) {
        this.labelsTree=labelsTree;
        this.notebooksTree=notebooksTree;
    }
    
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)labelsTree.getLastSelectedPathComponent();
        if (node != null) {
            logger.debug("Labels tree selection path: "+ node.getPath()[node.getLevel()]);
            if(node.getUserObject() instanceof NotebookNodeUserObject) {
                NotebookNodeUserObject labelNode=(NotebookNodeUserObject)node.getUserObject();
                // TODO bundle
                StatusBar.show("Label " + labelNode.getNotebookUri() + " selected...");

                notebooksTree.reloadModel(labelNode.getNotebookUri());
            }
        }
    }
}
