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
package com.mindcognition.mindraider.ui.swing.trash;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
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
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.Metadata;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.core.rest.resource.FolderResource;
import com.emental.mindraider.core.rest.resource.OutlineResource;
import com.emental.mindraider.ui.dialogs.ProgressDialogJFrame;
import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.mindcognition.mindraider.application.model.label.LabelCustodianListener;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.dialogs.RestoreNotebookJDialog;
import com.mindcognition.mindraider.ui.swing.explorer.ExplorerJPanel;
import com.mindcognition.mindraider.utils.SwingWorker;

public class TrashJPanel extends JPanel implements
        TreeWillExpandListener, TreeExpansionListener, LabelCustodianListener {
    private static final Logger logger = Logger.getLogger(TrashJPanel.class);

    public static final int LEVEL_ROOT = 0;
    public static final int LEVEL_FOLDERS = 1;
    public static final int LEVEL_NOTEBOOKS = 2;

    /*
     * UI components
     */

    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected final JTree tree;
    protected JButton undoButton, emptyButton, deleteButton;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();

    /*
     * model
     */

    private HashMap treeNodeToResourceUriMap;

    /*
     * singleton
     */
    private static TrashJPanel singleton;
    public static TrashJPanel getInstance() {
        if (singleton == null) {
            singleton = new TrashJPanel();
        }
        return singleton;
    }

    private ResourceDescriptor[] discardedNotebooksDescriptors;

    /**
     * Constructor.
     */
    private TrashJPanel() {
        treeNodeToResourceUriMap = new HashMap();

        rootNode = new DefaultMutableTreeNode(Messages.getString("TrashJPanel.notebookArchive"));
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(new MyTreeModelListener());

        tree = new JTree(treeModel);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeExpansionListener(this);
        tree.addTreeWillExpandListener(this);
        tree.setShowsRootHandles(true);
        tree.putClientProperty("JTree.lineStyle", "Angled");

        // tree rendered
        // TODO implement own renderer in order to tooltips
        tree.setCellRenderer(new TrashTreeCellRenderer(IconsRegistry
                .getImageIcon("trashFull.png"), IconsRegistry
                .getImageIcon("explorerNotebookIcon.png")));

        setLayout(new BorderLayout());

        // control panel
        JToolBar tp = new JToolBar();
        tp.setLayout(new GridLayout(1, 6));
        undoButton = new JButton("", IconsRegistry
                .getImageIcon("trashUndo.png"));
        undoButton.setEnabled(false);
        undoButton.setToolTipText("Restore Outline");
        undoButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                        .getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                new RestoreNotebookJDialog(
                        (String)treeNodeToResourceUriMap.get(node), 
                        "Restore Outline", 
                        "Restore", 
                        true);
            }
        });
        tp.add(undoButton);

        deleteButton = new JButton("", IconsRegistry
                .getImageIcon("explorerDeleteSmall.png"));
        deleteButton.setToolTipText("Delete Outline");
        deleteButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                        .getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }

                int result = JOptionPane.showConfirmDialog(
                        MindRaider.mainJFrame,
                        "Do you really want to DELETE this Outline?",
                        "Delete Outline", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    MindRaider.labelCustodian
                            .deleteOutline((String) treeNodeToResourceUriMap
                                    .get(node));
                    refresh();
                    ExplorerJPanel.getInstance().refresh();
                }
            }
        });
        tp.add(deleteButton);

        emptyButton = new JButton("", IconsRegistry
                .getImageIcon("trashEmpty.png"));

        emptyButton.setToolTipText(Messages.getString("TrashJPanel.emptyArchive"));
        emptyButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane
                        .showConfirmDialog(
                                MindRaider.mainJFrame,
                                "Do you really want to DELETE all discarded Outlines?",
                                "Empty Trash", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    final SwingWorker worker = new SwingWorker() {

                        public Object construct() {
                            ProgressDialogJFrame progressDialogJFrame = new ProgressDialogJFrame(
                                    "Empty Trash",
                                    "<html><br>&nbsp;&nbsp;<b>Deleting:</b>&nbsp;&nbsp;</html>");
                            try {
                                ResourceDescriptor[] resourceDescriptors = MindRaider.labelCustodian
                                        .getDiscardedOutlineDescriptors();
                                if (resourceDescriptors != null) {
                                    for (int i = 0; i < resourceDescriptors.length; i++) {
                                        MindRaider.labelCustodian
                                                .deleteOutline(resourceDescriptors[i]
                                                        .getUri());
                                    }
                                    refresh();
                                }
                            } finally {
                                if (progressDialogJFrame != null) {
                                    progressDialogJFrame.dispose();
                                }
                            }
                            return null;
                        }
                    };
                    worker.start();
                }
            }
        });
        tp.add(emptyButton);

        add(tp, BorderLayout.NORTH);

        // add the tree
        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);
        // build the whole tree
        buildTree();
        // click handler
        tree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                        .getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                logger.debug("Tree selection path: "
                        + node.getPath()[node.getLevel()]);

                enableDisableToolbarButtons(node.getLevel());
            }
        });
    }

    /**
     * Build tree. This method is called on startup and tree refresh in order to
     * reload disc content. Adding/removing of particular nodes during the
     * program run is performed on individual nodes.
     */
    void buildTree() {
        discardedNotebooksDescriptors = MindRaider.labelCustodian
                .getDiscardedOutlineDescriptors();

        if (!ArrayUtils.isEmpty(discardedNotebooksDescriptors)) {
            for (int i = 0; i < discardedNotebooksDescriptors.length; i++) {
                addDiscardedNotebookNode(discardedNotebooksDescriptors[i]
                        .getLabel(), discardedNotebooksDescriptors[i].getUri());
            }

            // now expland all rows
            for (int i = 0; i < tree.getRowCount(); i++) {
                tree.expandRow(i);
            }
        }

        tree.setSelectionRow(0);
        enableDisableToolbarButtons(0);
    }

    /**
     * Add discarded notebook node.
     *
     * @param uri
     *            notebook node.
     * @return the node.
     */
    public DefaultMutableTreeNode addDiscardedNotebookNode(String label,
            String uri) {
        DefaultMutableTreeNode parent = null;

        Object child = label;
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
        // store node to map to be able to get URI from node object
        treeNodeToResourceUriMap.put(childNode, uri);

        if (parent == null) {
            parent = rootNode;
        }

        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
        return childNode;
    }

    /**
     * Call this method in order to update the tree.
     */
    public void refresh() {
        clear();
        buildTree();
    }

    /**
     * Move notebook up in the folder.
     *
     * @param notebookUri
     * @param folderUri
     */
    protected boolean moveNotebookUp(String folderUri, String notebookUri) {
        logger.debug(" moveNotebookUp: " + folderUri + " " + notebookUri);
        if (folderUri != null && notebookUri != null) {
            try {
                // add notebook to folder
                boolean result = MindRaider.labelCustodian.moveNotebookUp(
                        folderUri, notebookUri);

                // TODO PERFORMANCE move it just in the tree instead of refresh
                refresh();
                return result;
            } catch (Exception e1) {
                logger.error("moveNotebookUp(String, String)", e1);
                JOptionPane.showMessageDialog(TrashJPanel.this,
                        "Outline Manipulation Error",
                        "Unable to move outline up: " + e1.getMessage(),
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        logger.debug("Outline wont be added URIs are null!");
        return false;
    }

    /**
     * Move notebook down in the folder.
     *
     * @param notebookUri
     * @param folderUri
     */
    protected boolean moveNotebookDown(String folderUri, String notebookUri) {
        logger.debug(" moveNotebookDown: " + folderUri + " " + notebookUri);
        if (folderUri != null && notebookUri != null) {
            try {
                boolean result = MindRaider.labelCustodian.moveNotebookDown(
                        folderUri, notebookUri);

                // TODO PERFORMANCE move it just in the tree instead of refresh
                refresh();
                return result;
            } catch (Exception e1) {
                logger.error("moveNotebookDown(String, String)", e1);
                JOptionPane.showMessageDialog(TrashJPanel.this,
                        "Outline Manipulation Error",
                        "Unable to move outline down: " + e1.getMessage(),
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        logger.debug("Outline wont be added URIs are null!");
        return false;
    }

    /**
     * Add notebook node to folder node (on new notebook creation).
     *
     * @param notebookUri
     *            newly created notebook URI.
     */
    public void addNotebookToFolder(String notebookUri) {
        logger.debug("  URI of created notebook is: " + notebookUri);
        if (notebookUri != null) {
            // add notebook to selected folder
            TreePath treePath = tree.getSelectionPath();
            String folderUri = (String) treeNodeToResourceUriMap.get(treePath
                    .getLastPathComponent());
            logger.debug("Enclosing folder URI is: " + folderUri);
            if (folderUri != null) {
                try {
                    // add notebook to folder
                    MindRaider.labelCustodian.addOutline(folderUri,
                            notebookUri);

                    // now add it in the tree
                    OutlineResource notebookResource = MindRaider.outlineCustodian
                            .getActiveOutlineResource();
                    addNotebookNode((DefaultMutableTreeNode) treePath
                            .getLastPathComponent(), notebookResource.resource
                            .getMetadata().getUri().toASCIIString(),
                            notebookResource.getLabel());
                } catch (Exception e1) {
                    logger.error("addNotebookToFolder(String)", e1);
                    JOptionPane.showMessageDialog(TrashJPanel.this,
                            "Outline Creation Error",
                            "Unable to add Outline to folder: "
                                    + e1.getMessage(),
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } else {
            logger
                    .debug("Outline wont be added to folder - it's URI is null!");
        }
    }

    /**
     * Remove all nodes except the root node.
     */
    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
        treeNodeToResourceUriMap.clear();
    }

    /**
     * Remove the currently selected node.
     */
    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection
                    .getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
        }

        // Either there was no selection, or the root was selected.
        toolkit.beep();
    }

    /**
     * Add child to the currently selected node.
     */
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode) (parentPath
                    .getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
            Object child) {
        return addObject(parent, child, false);
    }

    /**
     * Add folder node.
     *
     * @param uri
     *            folder URI.
     * @return the node.
     */
    public DefaultMutableTreeNode addFolderNode(String uri) {
        DefaultMutableTreeNode parent = null;

        // get label from URI
        FolderResource resource = new FolderResource(MindRaider.labelCustodian
                .get(uri));
        Object child = resource.getLabel();
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        // store node to map to be able to get URI from node object
        treeNodeToResourceUriMap.put(childNode, uri);

        if (parent == null) {
            parent = rootNode;
        }

        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
        return childNode;
    }

    /**
     * Add notebook node.
     *
     * @param parent
     *            folder node.
     * @param uri
     *            notebook URI.
     * @param label
     *            notebook label.
     * @return the node.
     */
    public DefaultMutableTreeNode addNotebookNode(
            DefaultMutableTreeNode parent, String uri, String label) {
        Object child = label;
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        // store node to map to be able to get URI from node object
        treeNodeToResourceUriMap.put(childNode, uri);

        if (parent == null) {
            parent = rootNode;
        }

        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        return childNode;
    }

    /**
     * Add an child object to a parent object.
     *
     * @param parent
     *            the parent object.
     * @param child
     *            the child object.
     * @param shouldBeVisible
     *            if <code>true</code> the object should be visible.
     * @return Returns a <code>DefaultMutableTreeNode</code>
     */
    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
            Object child, boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }

        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        // Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    /**
     * Custom MyTreeModelListerer class.
     */
    class MyTreeModelListener implements TreeModelListener {

        /**
         * Logger for this class.
         */
        private final Logger logger = Logger
                .getLogger(MyTreeModelListener.class);

        /**
         * @see javax.swing.event.TreeModelListener#treeNodesChanged(javax.swing.event.TreeModelEvent)
         */
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode) (e.getTreePath()
                    .getLastPathComponent());

            /*
             * If the event lists children, then the changed node is the child
             * of the node we've already gotten. Otherwise, the changed node and
             * the specified node are the same.
             */
            // ToDo
            try {
                int index = e.getChildIndices()[0];
                node = (DefaultMutableTreeNode) (node.getChildAt(index));
            } catch (NullPointerException exc) {
                //
            }

            logger.debug("The user has finished editing the node.");
            logger.debug("New value: " + node.getUserObject());
        }

        public void treeNodesInserted(TreeModelEvent e) {
        }
        public void treeNodesRemoved(TreeModelEvent e) {
        }
        public void treeStructureChanged(TreeModelEvent e) {
        }
    }

    public void treeCollapsed(TreeExpansionEvent e) {
        logger.debug("Tree colapsed event..." + e.getPath());
    }

    /**
     * @see javax.swing.event.TreeExpansionListener#treeExpanded(javax.swing.event.TreeExpansionEvent)
     */
    public void treeExpanded(TreeExpansionEvent e) {
        logger.debug("Tree expanded event..." + e.getPath());
    }

    /**
     * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
     */
    public void treeWillCollapse(TreeExpansionEvent e)
            throws ExpandVetoException {
        logger.debug("Tree will collapse " + e.getPath());
    }

    /**
     * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
     */
    public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
        logger.debug("Tree will expand " + e.getPath());

        /*
         * DefaultMutableTreeNode node = (DefaultMutableTreeNode)
         * tree.getLastSelectedPathComponent(); if (node == null) { return; }
         * logger.debug(""+node.getPath()[node.getLevel()]); // buttons
         * disabling switch(node.getLevel()) { case LEVEL_FOLDERS: // disconnect
         * childrens from the node Enumeration enumeration=node.children(); //
         * delete nodes itself while (enumeration.hasMoreElements()) { Object
         * object=enumeration.nextElement();
         * treeNodeToResourceUriMap.remove(object);
         * treeModel.removeNodeFromParent((MutableTreeNode)object); } // get
         * folder URI logger.debug("Expanding folder:
         * "+treeNodeToResourceUriMap.get(node)); FolderResource folder =new
         * FolderResource(MindRaider.folderCustodian.get((String)treeNodeToResourceUriMap.get(node)));
         * String[] notebookUris=folder.getNotebookUris(); if (notebookUris !=
         * null) { for (int i= 0; i < notebookUris.length; i++) {
         * NotebookResource notebook=new
         * NotebookResource(MindRider.notebookCustodian.get(notebookUris[i]));
         * addNotebookNode(node,notebook.resource.metadata.uri.toASCIIString(),notebook.getLabel()); } } }
         */
    }

    /**
     * @see com.emental.LabelCustodianListener.folder.FolderCustodianListener#folderCreated()
     */
    public void labelCreated(FolderResource folder) {
        Metadata meta = folder.getResource().getMetadata();
        logger.debug("Folder created: " + meta.getUri().toASCIIString());
        // handle creation of the folder
        addFolderNode(meta.getUri().toASCIIString());
    }

    /**
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * Change status in the toolbar buttons.
     *
     * @param level
     *            The level could be <code>LEVEL_ROOT</code> or
     *            <code>LEVEL_FOLDERS</code>
     */
    protected void enableDisableToolbarButtons(int level) {
        // buttons disabling
        switch (level) {
        case LEVEL_ROOT:
            undoButton.setEnabled(false);
            deleteButton.setEnabled(false);
            emptyButton.setEnabled(true);
            break;
        case LEVEL_FOLDERS:
            undoButton.setEnabled(true);
            deleteButton.setEnabled(true);
            emptyButton.setEnabled(true);
            break;
        }
    }
    
    private static final long serialVersionUID = 5028293540089775890L;
}
