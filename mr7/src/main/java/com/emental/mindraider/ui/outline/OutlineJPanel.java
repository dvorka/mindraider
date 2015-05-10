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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.URI;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.core.rest.properties.AttachmentProperty;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.emental.mindraider.core.rest.resource.OutlineResource;
import com.emental.mindraider.ui.dialogs.AttachmentJDialog;
import com.emental.mindraider.ui.dialogs.NewNoteJDialog;
import com.emental.mindraider.ui.dialogs.OpenOutlineJDialog;
import com.emental.mindraider.ui.frames.MindRaiderMainWindow;
import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.emental.mindraider.ui.outline.treetable.JTreeTable;
import com.emental.mindraider.ui.outline.treetable.NotebookOutlineEntry;
import com.emental.mindraider.ui.outline.treetable.NotebookOutlineModel;
import com.emental.mindraider.ui.outline.treetable.OutlineTreeInstance;
import com.mindcognition.mindraider.application.model.note.NoteCustodian;
import com.mindcognition.mindraider.application.model.outline.OutlineCustodian;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.commons.config.ConfigurationBean;
import com.mindcognition.mindraider.commons.spring.MindRaiderSpringContext;
import com.mindcognition.mindraider.integration.gnowsis.GnowsisClient;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.concept.ConceptJPanel;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;
import com.mindcognition.mindraider.utils.Launcher;
import com.mindcognition.mindraider.utils.Utils;

/**
 * Panel holding notebook -
 *   tree table (NotebookOutline), 
 *   mind map (RDF based graph, tag cloud)
 *   and concept panel (active concept title/annotation)
 * <br>
 * <br>
 * Master is responsible for slots rendering. Knows slots offset, which slots
 * are shown, how is the tree folded, is able to crop the last slot on the page.
 */
public final class OutlineJPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(OutlineJPanel.class);

    private static final int LABEL_COLUMN_PREFERRED_WIDTH = 200;
    private static final int LABEL_COLUMN_MAX_WIDTH = 300;

    private static final int DATE_COLUMN_PREFERRED_WIDTH = 50;
    private static final int DATE_COLUMN_MAX_WIDTH = 100;

    /*
     * Table tree classes: 
     *   o NotebookOutlineModel 
     *     ... table tree model 
     *   o NotebookOutlineTreeTable 
     *     ... just helper containing specialized table tree nodes + changes listener 
     *   o treeTable 
     *     ... basic widget behind the scene
     */

    // object tree
    private OutlineTreeInstance outlineTableTree;

    // basic widget
    public JTreeTable treeTable;

    // class extending basic widget - notebook ouline table tree
    private NotebookOutlineModel outlineTableTreeModel;

    /*
     * UI
     */

    public ConceptJPanel conceptJPanel;

    public ButtonGroup buttonGroup;

    public JRadioButton conceptRadio;

    public JComboBox searchTextCombo;

    public JSplitPane treeAndSpidersSplit;

    public JSplitPane rightSiderbarSplitPane;

    public JTabbedPane spidersAndTagsTabs;
    public JTabbedPane outlineAndTreeTabbedPane;

    private JButton newButton, attachButton;
    private JButton discardButton;
    private JButton refreshButton;
    private JButton refactorButton;
    private JButton promoteButton, demoteButton, upButton, downButton, firstButton, lastButton;
    private JButton onTheFlyExportButton, onTheFlyTWikiExportButton, expandTreeButton, collapseTreeButton, mindForgerUploadJButton;
    private JButton twikiExportJButton;
    private JButton gnowsisLinkButton;
    
    private OutlineArchiveJPanel outlineArchiveJPanel;
    private OutlineSorterJPanel outlineSorterJPanel;

    /**
     * singleton
     */
    private static OutlineJPanel singleton;
    public static synchronized OutlineJPanel getInstance() {
        if (singleton == null) {
            singleton = new OutlineJPanel();
        }
        return singleton;
    }

    /**
     * Creates notebook outline.
     */
    private OutlineJPanel() {
        setDoubleBuffered(true);
        setLayout(new BorderLayout());

        /*
         * toolbar
         */
        
        JToolBar toolbar = createToolbar();

        /*
         * tree
         */

        // tree table itself
        outlineTableTree = OutlineTreeInstance.getInstance();
        outlineTableTreeModel = new NotebookOutlineModel(outlineTableTree.getOutlineRoot());
        treeTable = new JTreeTable(outlineTableTreeModel);
        treeTable.tree.addTreeSelectionListener(new TreeSelectionListenerImplementation());
        // add key listener
        treeTable.addKeyListener(new KeyListenerImplementation());

        // label column
        TableColumn tableColumn = treeTable
                .getColumn(NotebookOutlineModel.columnNames[NotebookOutlineModel.COLUMN_LABEL]);
        tableColumn.setMaxWidth(LABEL_COLUMN_MAX_WIDTH);
        tableColumn.setMinWidth(0);
        tableColumn.setPreferredWidth(LABEL_COLUMN_PREFERRED_WIDTH);
        // date column
        tableColumn = treeTable.getColumn(NotebookOutlineModel.columnNames[NotebookOutlineModel.COLUMN_CREATED]);
        tableColumn.setMaxWidth(DATE_COLUMN_MAX_WIDTH);
        tableColumn.setMinWidth(0);
        tableColumn.setPreferredWidth(DATE_COLUMN_PREFERRED_WIDTH);
        // and the rest will be annotation
        JScrollPane treeTableScrollPane = new JScrollPane(treeTable);
                
        // outline treetabble + toolbar panel
        JPanel treeAndToolbarPanel=new JPanel(new BorderLayout());
        treeAndToolbarPanel.add(toolbar,BorderLayout.NORTH);
        treeAndToolbarPanel.add(treeTableScrollPane, BorderLayout.CENTER);        
        
        /*
         * outline / list tabbed pane
         */
        
        outlineAndTreeTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
        outlineAndTreeTabbedPane.add(treeAndToolbarPanel,"Outline");
        outlineSorterJPanel = new OutlineSorterJPanel();
        outlineAndTreeTabbedPane.add(outlineSorterJPanel,"Sorter");
        outlineArchiveJPanel = new OutlineArchiveJPanel();
        outlineAndTreeTabbedPane.add(outlineArchiveJPanel,"Archive");        
        
        /*
         * concept sidebar
         */
        
        conceptJPanel = (ConceptJPanel)MindRaiderSpringContext.getCtx().getBean("conceptPanel");

        /*
         * vertical split of notebook outline and RDF graph
         */

        treeAndSpidersSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        treeAndSpidersSplit.setContinuousLayout(true);
        treeAndSpidersSplit.setOneTouchExpandable(true);
        treeAndSpidersSplit.setDividerLocation(200);
        treeAndSpidersSplit.setLastDividerLocation(150);
        treeAndSpidersSplit.setDividerSize(6);
        treeAndSpidersSplit.add(outlineAndTreeTabbedPane);
        
        // spiders & tags visual navigation
        spidersAndTagsTabs = new JTabbedPane(JTabbedPane.BOTTOM);
        if(MindRaider.profile.isEnableSpiders()) {
            // notebook mind map
            spidersAndTagsTabs.addTab("Mind Map",MindRaider.spidersGraph.getPanel()); // TODO bundle
        }
        // global tags
        spidersAndTagsTabs.addTab("Tag Cloud",MindRaider.tagCustodian.getPanel()); // TODO bundle
        // global mind map
        //spidersAndTagsTabs.addTab("Global Mind Map",new JPanel()); // TODO bundle

        // lazy spiders rendering
        spidersAndTagsTabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                if(sourceTabbedPane.getSelectedIndex()==0) {
                    MindRaider.spidersGraph.renderModel();
                }
            }
        });
        

        if(!new ConfigurationBean().isDefaultTabMindMap()) {
            spidersAndTagsTabs.setSelectedIndex(1);
        }
        
        MindRaider.tagCustodian.redraw();
        
        // add spiders panel
        treeAndSpidersSplit.add(spidersAndTagsTabs);

        /*
         * horizontal split of outline/graph slit and concept sidebar
         */

        rightSiderbarSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeAndSpidersSplit, conceptJPanel);
        rightSiderbarSplitPane.setOneTouchExpandable(true);
        rightSiderbarSplitPane.setContinuousLayout(true);
        rightSiderbarSplitPane.setDividerLocation(500);
        rightSiderbarSplitPane.setLastDividerLocation(500);
        rightSiderbarSplitPane.setDividerSize(6);

        add(rightSiderbarSplitPane);
    }

    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        newButton = new JButton("", IconsRegistry.getImageIcon("add.png"));
        newButton.setToolTipText(Messages.getString("NotebookOutlineJPanel.newConcept"));
        newButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                newConcept();
            }
        });
        toolbar.add(newButton);

        attachButton = new JButton("", IconsRegistry.getImageIcon("attach.png"));
        attachButton.setToolTipText(Messages.getString("NotebookOutlineJPanel.attachDragDropResourceToConcept"));
        attachButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                conceptAttach();
            }
        });
        toolbar.add(attachButton);

        discardButton = new JButton("", IconsRegistry.getImageIcon("explorerDiscardSmall.png"));
        discardButton.setToolTipText(Messages.getString("NotebookOutlineJPanel.discardConcept"));
        discardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conceptDiscard();
            }
        });
        toolbar.add(discardButton);

        toolbar.addSeparator();

        promoteButton = new JButton("", IconsRegistry.getImageIcon("back.png"));
        promoteButton.setToolTipText(Messages.getString("NotebookOutlineJPanel.promoteConceptToParentLevel"));
        promoteButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                conceptPromote();
            }
        });
        toolbar.add(promoteButton);
        firstButton = new JButton("", IconsRegistry.getImageIcon("upup.png"));
        firstButton.setToolTipText(Messages.getString("NotebookOutlineJPanel.moveConceptFirst"));
        firstButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                while(conceptUp());
            }
        });
        toolbar.add(firstButton);
        upButton = new JButton("", IconsRegistry.getImageIcon("up.png"));
        upButton.setToolTipText(Messages.getString("NotebookOutlineJPanel.moveConceptUp"));
        upButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                conceptUp();
            }
        });
        toolbar.add(upButton);
        downButton = new JButton("", IconsRegistry.getImageIcon("down.png"));
        downButton.setToolTipText(Messages.getString("NotebookOutlineJPanel.moveConceptDown"));
        downButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                conceptDown();
            }
        });
        toolbar.add(downButton);
        lastButton = new JButton("", IconsRegistry.getImageIcon("downdown.png"));
        lastButton.setToolTipText(Messages.getString("NotebookOutlineJPanel.moveConceptLast"));
        lastButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                while(conceptDown());
            }
        });
        toolbar.add(lastButton);
        demoteButton = new JButton("", IconsRegistry.getImageIcon("forward.png"));
        demoteButton.setToolTipText(Messages.getString("NotebookOutlineJPanel.demoteConceptToChildrenLevel"));
        demoteButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                conceptDemote();
            }
        });
        toolbar.add(demoteButton);

        toolbar.addSeparator();

        refactorButton = new JButton("", IconsRegistry.getImageIcon("refactorConcept.png"));
        refactorButton.setToolTipText(Messages.getString("NotebookOutlineJPanel.refactorConceptsToAnotherNotebook"));
        refactorButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                /*
                 * in this case refactoring means: o discard of the concept in
                 * this notebook o creation of the concept in the target
                 * notebook concept is not deleted because URN should be never
                 * deleted. this feature will be important for example for
                 * vodyanoi.
                 */
                conceptRefactor();
                treeTable.tree.updateUI();
            }
        });
        toolbar.add(refactorButton);

        refreshButton = new JButton("", IconsRegistry.getImageIcon("explorerReloadSmall.png"));
        refreshButton.setToolTipText(Messages.getString("NotebookOutlineJPanel.reloadNotebook"));
        refreshButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
        toolbar.add(refreshButton);

        toolbar.addSeparator();

        expandTreeButton = new JButton("", IconsRegistry.getImageIcon("expandTree.png"));
        expandTreeButton.setToolTipText("Expand Concept tree");
        expandTreeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                expandAllTreeTableRows();
            }
        });
        toolbar.add(expandTreeButton);

        collapseTreeButton = new JButton("", IconsRegistry.getImageIcon("collapseTree.png"));
        collapseTreeButton.setToolTipText("Collapse Concept tree");
        collapseTreeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                collapseAllTreeTableRows();
            }
        });
        toolbar.add(collapseTreeButton);

        toolbar.addSeparator();

        // Gnowsis: link
        gnowsisLinkButton = new JButton("", IconsRegistry.getImageIcon("gnowsisLink.png"));
        gnowsisLinkButton.setToolTipText("Send Concept to Gnowsis to be linked");
        gnowsisLinkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (MindRaider.profile.getActiveOutline() == null) {
                    JOptionPane.showMessageDialog(OutlineJPanel.this,
                            "To send URI to Gnowsis a notebook must be loaded!", "Gnowsis Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // determine selected resource URI
                DefaultMutableTreeNode node = getSelectedTreeNode();
                if (node != null) {
                    String uri = ((OutlineNode) node).getUri();
                    logger.debug("Gnowsis hub: sending URI: '" + uri + "'");
                    GnowsisClient.linkResource(uri);
                } else {
                    logger.debug("No node selected!");
                }
            }
        });
        if(MindRaider.profile.isEnableGnowsisSupport()) {
            toolbar.add(gnowsisLinkButton);
        }

        // Gnowsis: browse
        gnowsisBrowseButton = new JButton("", IconsRegistry.getImageIcon("gnowsisBrowse.png"));
        gnowsisBrowseButton.setToolTipText("Browse Concept in Gnowsis");
        gnowsisBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (MindRaider.profile.getActiveOutline() == null) {
                    JOptionPane.showMessageDialog(OutlineJPanel.this,
                            "To browse URI in Gnowsis a notebook must be loaded!", "Gnowsis Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // determine selected resource URI
                DefaultMutableTreeNode node = getSelectedTreeNode();
                if (node != null) {
                    String uri = ((OutlineNode) node).getUri();
                    logger.debug("Gnowsis hub: sending browse URI: '" + uri + "'");
                    GnowsisClient.browseResource(uri);
                } else {
                    logger.debug("No node selected!");
                }
            }
        });
        if(MindRaider.profile.isEnableGnowsisSupport()) {
            toolbar.add(gnowsisBrowseButton);
            
            toolbar.addSeparator();
        }
        

//        JButton button = new JButton("", IconsRegistry.getImageIcon("collapseTree.png"));
//        button.setToolTipText("Hide/Show annotation column");
//        button.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                hideTreeTableAnnotationColumn();
//            }
//        });
//        mainPanelControls.add(button);
//        mainPanelControls.addSeparator();

        onTheFlyTWikiExportButton = new JButton("", IconsRegistry.getImageIcon("mozillaTwiki.png"));
        onTheFlyTWikiExportButton.setToolTipText("'On the fly' TWiki Export 2 HTML");
        onTheFlyTWikiExportButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (MindRaider.profile.getActiveOutline() == null) {
                    JOptionPane.showMessageDialog(OutlineJPanel.this,
                            "To export a notebook it must be loaded!", "Export Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // prepare directory
                String exportDirectory = MindRaider.profile.getHomeDirectory() + File.separator + "export"
                        + File.separator + "twiki" + File.separator + "tmp";
                Utils.createDirectory(exportDirectory);
                String dstFileName = exportDirectory + File.separator + "TWIKI-EXPORT-"
                        + MindRaider.outlineCustodian.getActiveNotebookNcName() + ".html";
                logger.debug("Exporting to file: " + dstFileName);
                MindRaider.outlineCustodian.exportOutline(OutlineCustodian.FORMAT_TWIKI_HTML, dstFileName);
                Launcher.launchInBrowser(dstFileName);
            }
        });
        // TODO removed mainPanelControls.add(onTheFlyTWikiExportButton);

        twikiExportJButton = new JButton("", IconsRegistry.getImageIcon("twikiExport.png"));
        twikiExportJButton.setToolTipText("Export back to the imported TWiki file");
        twikiExportJButton.setEnabled(false);
        twikiExportJButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (MindRaider.profile.getActiveOutline() == null) {
                    JOptionPane.showMessageDialog(OutlineJPanel.this,
                            "To export a notebook it must be loaded!", "Export Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // take the path from the notebook custodian
                String dstFileName;
                OutlineResource notebookResource = MindRaider.outlineCustodian.getActiveOutlineResource();
                if (notebookResource != null && notebookResource.getSourceTWikiFileProperty() != null) {
                    dstFileName = notebookResource.getSourceTWikiFileProperty().getPath();
                    StatusBar.show("Exporting Notebook to TWiki file: '" + dstFileName + "'...");
                    MindRaider.outlineCustodian.exportOutline(OutlineCustodian.FORMAT_TWIKI, dstFileName);
                }
            }
        });
        toolbar.add(twikiExportJButton);

        // TODO removed mainPanelControls.addSeparator();

        onTheFlyExportButton = new JButton("", IconsRegistry.getImageIcon("mozilla.png"));
        onTheFlyExportButton.setToolTipText("'On the fly' OPML Export 2 HTML");
        onTheFlyExportButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (MindRaider.profile.getActiveOutline() == null) {
                    JOptionPane.showMessageDialog(OutlineJPanel.this,
                            "To export a notebook it must be loaded!", "Export Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // prepare directory
                String exportDirectory = MindRaider.profile.getHomeDirectory() + File.separator + "export"
                        + File.separator + "opml" + File.separator + "tmp";
                Utils.createDirectory(exportDirectory);
                String dstFileName = exportDirectory + File.separator + "OPML-EXPORT-"
                        + MindRaider.outlineCustodian.getActiveNotebookNcName() + ".html";
                logger.debug("Exporting to file: " + dstFileName);
                MindRaider.outlineCustodian.exportOutline(OutlineCustodian.FORMAT_OPML_HTML, dstFileName);
                Launcher.launchInBrowser(dstFileName);

            }
        });
        // TODO removed mainPanelControls.add(onTheFlyExportButton);
        
        toolbar.addSeparator();
        
        mindForgerUploadJButton = new JButton("", IconsRegistry.getImageIcon("tasks-internet.png"));
        mindForgerUploadJButton.setToolTipText("Upload this Outline to Online Edition");
        mindForgerUploadJButton.setEnabled(false);
        mindForgerUploadJButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (MindRaider.profile.getActiveOutline() == null) {
                    JOptionPane.showMessageDialog(OutlineJPanel.this,
                            "To upload an Outline it must be loaded!", "Upload Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // take the path from the notebook custodian
                MindRaiderMainWindow.getInstance().handleMindForgerActiveOutlineUpload();
            }
        });
        toolbar.add(mindForgerUploadJButton);
        
        return toolbar;
    }

    /**
     * Refresh.
     */
    public void refresh() {
        logger.debug("Notebook Outline refresh...");

        // refresh tree
        if (MindRaider.profile.getActiveOutlineUri() != null) {
            outlineTableTree.rebuildTree();
        } else {
            outlineTableTree.clear();
        }
        // sorter
        outlineSorterJPanel.updateUI();
        outlineSorterJPanel.refresh();
        // archive
        outlineArchiveJPanel.updateUI();
        int archivedConcepts=outlineArchiveJPanel.refresh();
        // archive tab
        if(outlineAndTreeTabbedPane!=null) {
            outlineAndTreeTabbedPane.setTitleAt(2,"Archive ("+archivedConcepts+")");
        }

        // redraw widget
        treeTable.updateUI();
        // select root
        treeTable.tree.setSelectionRow(0);
        // TODO make this optional (in profile)
        expandAllTreeTableRows();

        // update buttons status
        enableDisableToolbarButtons(0);
        if (MindRaider.profile.getActiveOutlineUri() != null) {
            newButton.setEnabled(true);
            expandTreeButton.setEnabled(true);
            refreshButton.setEnabled(true);
            onTheFlyExportButton.setEnabled(true);
            onTheFlyTWikiExportButton.setEnabled(true);
            mindForgerUploadJButton.setEnabled(true);
            
            // TWiki import - determine whether active notebook has set twiki
            // import property
            OutlineResource notebookResource = MindRaider.outlineCustodian.getActiveOutlineResource();
            if (notebookResource != null && notebookResource.getSourceTWikiFileProperty() != null) {
                twikiExportJButton.setEnabled(true);
            } else {
                twikiExportJButton.setEnabled(false);
            }
            
            // try to load concept (the first one)
            // @todo load it from the last clicked resource
//            ResourceDescriptor[] conceptDescriptors = MindRaider.notebookCustodian.getConceptDescriptors();
//            if(conceptDescriptors!=null && conceptDescriptors.length>0) {
//                try {
//                    conceptJPanel.refresh(MindRaider.conceptCustodian.get(
//                            MindRaider.profile.getActiveNotebookUri().toString(), 
//                            conceptDescriptors[0].getUri()));
//                } catch (Exception e) {
//                    logger.debug("Unable to load default concept!");
//                }            
//            }
        } else {
            newButton.setEnabled(false);
            expandTreeButton.setEnabled(false);
            refreshButton.setEnabled(false);
            onTheFlyExportButton.setEnabled(false);
            onTheFlyTWikiExportButton.setEnabled(false);
            twikiExportJButton.setEnabled(false);
            mindForgerUploadJButton.setEnabled(false);            
        }        
        
        treeTable.tree.updateUI();
    }

    /**
     * Expand all tree table rows.
     */
    public void expandAllTreeTableRows() {
        for (int i = 0; i < treeTable.tree.getRowCount(); i++) {
            treeTable.tree.expandRow(i);
        }
    }

    /**
     * Collapse all tree table rows.
     */
    public void collapseAllTreeTableRows() {
        try {
            for (int i = 0; i < treeTable.tree.getRowCount(); i++) {
                if (i > 0) {
                    treeTable.tree.collapseRow(i);
                }
            }
        } catch (Exception e) {
            //
        }
    }

    /**
     * Clear.
     */
    public void clear() {
        outlineTableTree.clear();
        conceptJPanel.clear();
        treeTable.updateUI();
    }

    /**
     * Redraw.
     */
    public void redrawTreeTable() {
        if (MindRaider.profile.getActiveOutlineUri() != null) {
            treeTable.updateUI();
        }
    }

    private final class KeyListenerImplementation implements KeyListener {
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.isControlDown()) {
                // if(keyEvent.getKeyCode() == KeyEvent.VK_N) {
                // NotebookOutlineJPanel.getInstance().conceptNew();
                // return;
                // }
                if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
                    OutlineJPanel.getInstance().conceptPromote();
                    return;
                }
                if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
                    OutlineJPanel.getInstance().conceptUp();
                    return;
                }
                if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
                    OutlineJPanel.getInstance().conceptDown();
                    return;
                }
                if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
                    OutlineJPanel.getInstance().conceptDemote();
                    return;
                }
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_TAB) {
                OutlineJPanel.getInstance().conceptDemote();
                return;
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_DELETE) {
                OutlineJPanel.getInstance().conceptDiscard();
                return;
            }
        }

        public void keyReleased(KeyEvent keyEvent) {
        }

        public void keyTyped(KeyEvent keyEvent) {
        }
    }

    private final class TreeSelectionListenerImplementation implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode selectedNode 
                = (DefaultMutableTreeNode) treeTable.tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                logger.debug("OUTLINE: tree selection path: " + selectedNode.getPath()[selectedNode.getLevel()]);

                enableDisableToolbarButtons(selectedNode.getLevel());

                // skip root from rendering
                if (selectedNode.getLevel() != 0) {
                    String conceptUri = ((NotebookOutlineEntry) selectedNode).uri;
                    try {
                        // now refresh concept JPanel
                        if (conceptUri != null) {
                            conceptJPanel.open(
                                    MindRaider.noteCustodian.get(MindRaider.profile.getActiveOutlineUri().toString(), 
                                                                    conceptUri));
                            
                            // recent concepts
                            MindRaider.recentConcepts.addRecentConcept(
                                    treeTable.tree.getSelectionModel().getSelectionPath().getLastPathComponent().toString(),
                                    conceptUri,
                                    MindRaider.outlineCustodian.getActiveNotebookLabel(),
                                    MindRaider.outlineCustodian.getActiveOutlineResource().getUri());
                            
                            // select concept in the graph
                            MindRaider.spidersGraph.selectNodeByUri(conceptUri);
                        } else {
                            logger.debug("Concept URI is null - fallback to refresh...");
                            refresh();
                        }
                    } catch (Exception e1) {
                        logger.debug("Unable to select concept " + conceptUri, e1);
                    }
                }
            }
        }
    }

    /**
     * The renderer used for String in the TreeTable. The only thing it does, is
     * to format a null String as '---'.
     */
    static class BookmarksStringRenderer extends DefaultTableCellRenderer {

        /**
         * Serial version.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Constructor.
         */
        public BookmarksStringRenderer() {
            super();
        }

        /**
         * Set value.
         */
        public void setValue(Object value) {
            setText((value == null) ? "---" : value.toString());
        }
    }

    /**
     * Get notebook outline.
     *
     * @return
     */
    public OutlineTreeInstance getOutlineTableTree() {
        return outlineTableTree;
    }

    /**
     * Get concept JPanel.
     *
     * @return
     */
    public ConceptJPanel getConceptJPanel() {
        return conceptJPanel;
    }

    /*
     * UI
     */

    /*
     * get selected... used for rendering
     */


    /**
     * Get URI of the selected concept.
     */
    public String getSelectedConceptUri() {
        TreePath treePath = treeTable.tree.getSelectionPath();

        if (treePath != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            if (node != null) {
                return ((OutlineNode) node).getUri();
            }
        }
        return null;
    }

    /**
     * Get selected tree node.
     *
     * @return
     */
    public DefaultMutableTreeNode getSelectedTreeNode() {
        TreePath treePath = treeTable.tree.getSelectionPath();

        if (treePath == null) {
            return null;
        }
        return (DefaultMutableTreeNode) treePath.getLastPathComponent();
    }

    /**
     * Set selected tree node.
     *
     * @param conceptUri
     */
    public void setSelectedTreeNodeConcept(String conceptUri) {
        logger.debug("=-> setSelectedTreeNodeConcept");
        // try to load concept with this URI
        if (conceptUri != null
                && MindRaiderVocabulary.isConceptUri(conceptUri)
                && MindRaider.profile.getActiveOutlineUri() != null
                && !conceptUri.equals(NoteCustodian.getTrashConceptUri(MindRaider.profile.getActiveOutlineUri()
                        .toString()))) {
            try {
                // select particular concept in the table - find it yourself
                int rows = treeTable.tree.getRowCount();
                TreePath treePath;
                OutlineNode node;
                for (int i = 0; i < rows; i++) {
                    if (i > 0) {
                        treePath = treeTable.tree.getPathForRow(i);
                        // logger.debug("Tree path: "+treePath);
                        node = (OutlineNode) treePath.getLastPathComponent();
                        if (conceptUri.equals(node.getUri())) {
                            // logger.debug("Bingo: "+conceptUri);
                            treeTable.tree.makeVisible(treePath);
                            treeTable.tree.getSelectionModel().setSelectionPath(treePath);
                            
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug("Unable to load Concept for selected graph node: " + conceptUri, e);
            }
        }
    }

    /*
     * UI
     */

    private boolean rightSidebarLatelyMaximized = true;

    public void toggleRightSidebar() {
        if (!rightSidebarLatelyMaximized) {
            rightSiderbarSplitPane.setDividerLocation(rightSiderbarSplitPane.getLastDividerLocation());
        } else {
            rightSiderbarSplitPane.setDividerLocation(2000);
        }
        rightSiderbarSplitPane.updateUI();

        rightSidebarLatelyMaximized = !rightSidebarLatelyMaximized;
    }

    public void hideRightSiderbar() {
        toggleRightSidebar();
        if (rightSidebarLatelyMaximized) {
            toggleRightSidebar();
        }
    }

    private boolean conceptTreeLatelyMaximized = true;

    private AbstractButton gnowsisBrowseButton;

    public void toggleConceptTree() {
        conceptTreeLatelyMaximized = !conceptTreeLatelyMaximized;
        if (conceptTreeLatelyMaximized) {
            treeAndSpidersSplit.setDividerLocation(treeAndSpidersSplit.getLastDividerLocation());
        } else {
            treeAndSpidersSplit.setDividerLocation(2000);
        }
        treeAndSpidersSplit.updateUI();
    }
    
    public void hideSpiders() {
        treeAndSpidersSplit.setDividerLocation(2000);
        treeAndSpidersSplit.updateUI();    
    }

    public void hideConceptTree() {
        // use divider location to determine current status
        if (treeAndSpidersSplit.getDividerLocation() != 0) {
            // maximize rdf navigator
            treeAndSpidersSplit.setDividerLocation(0);
        }
        treeAndSpidersSplit.updateUI();
    }

    public void toggleRdfNavigator() {
        // use divider location to determine current status
        if (treeAndSpidersSplit.getDividerLocation() == 0) {
            // concept tree is minimized - rdf navigator maximized - get last
            // position
            treeAndSpidersSplit.setDividerLocation(treeAndSpidersSplit.getLastDividerLocation());
        } else {
            // maximize rdf navigator
            treeAndSpidersSplit.setDividerLocation(0);
        }
        treeAndSpidersSplit.updateUI();
    }

    /**
     * Enable/disable buttons.
     *
     * @param node
     */
    protected void enableDisableToolbarButtons(int level) {
        // enable/disable buttons
        if (level == 0) {
            newButton.setEnabled(true);
            discardButton.setEnabled(false);
            promoteButton.setEnabled(false);
            demoteButton.setEnabled(false);
            upButton.setEnabled(false);
            firstButton.setEnabled(false);
            downButton.setEnabled(false);
            lastButton.setEnabled(false);
            gnowsisLinkButton.setEnabled(false);
            gnowsisBrowseButton.setEnabled(false);
            refactorButton.setEnabled(false);
        } else {
            newButton.setEnabled(true);
            discardButton.setEnabled(true);
            promoteButton.setEnabled(true);
            demoteButton.setEnabled(true);
            upButton.setEnabled(true);
            firstButton.setEnabled(true);
            downButton.setEnabled(true);
            lastButton.setEnabled(true);
            gnowsisLinkButton.setEnabled(true);
            gnowsisBrowseButton.setEnabled(true);
            refactorButton.setEnabled(true);
        }

        enableDisableAttachToolbarButton();
    }

    public void disableAllToolbarButtons() {
        newButton.setEnabled(false);
        discardButton.setEnabled(false);
        promoteButton.setEnabled(false);
        demoteButton.setEnabled(false);
        upButton.setEnabled(false);
        firstButton.setEnabled(false);
        downButton.setEnabled(false);
        lastButton.setEnabled(false);
        gnowsisLinkButton.setEnabled(false);
        gnowsisBrowseButton.setEnabled(false);

        expandTreeButton.setEnabled(false);
        collapseTreeButton.setEnabled(false);
        refreshButton.setEnabled(false);
        onTheFlyExportButton.setEnabled(false);
        onTheFlyTWikiExportButton.setEnabled(false);
        twikiExportJButton.setEnabled(false);
        mindForgerUploadJButton.setEnabled(false);

        refactorButton.setEnabled(false);

        attachButton.setEnabled(false);
    }

    /**
     * Enable/disable attach toolbar button.
     */
    public void enableDisableAttachToolbarButton() {
        if (MindRaider.mainJFrame != null && MindRaider.mainJFrame.dragAndDropReference != null) {
            attachButton.setEnabled(true);
        } else {
            attachButton.setEnabled(false);
        }
    }

    /**
     * New concept.
     */
    public void newConcept() {
        if (MindRaider.profile.getActiveOutlineUri() != null) {
            new NewNoteJDialog(MindRaider.profile.getActiveOutlineUri().toString(),conceptJPanel.getAnnotationsCustodian());
        } else {
            JOptionPane.showMessageDialog(OutlineJPanel.this,
                    "To define the Concept a Notebook must be opened!", "Concept Creation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Attach resource dropped to the MR window to current concept.
     */
    public void conceptAttach() {
        if (MindRaider.profile.getActiveOutlineUri() != null) {
            ConceptResource conceptResource = conceptJPanel.getConceptResource();
            if (conceptResource != null) {
                new AttachmentJDialog(conceptResource, MindRaider.mainJFrame.dragAndDropReference);
            } else {
                JOptionPane.showMessageDialog(OutlineJPanel.this,
                        "To attach reference to the active Concept a Concept must be selected!",
                        "D&D Concept Attach Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(OutlineJPanel.this,
                    "To attach reference to the active Concept a Notebook must be opened!", "D&D Concept Attach Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Discard concept.
     */
    public void conceptDiscard() {
        if (MindRaider.profile.getActiveOutlineUri() == null) {
            JOptionPane.showMessageDialog(OutlineJPanel.this, Messages
                    .getString("NotebookOutlineJPanel.toDiscardConceptTheNotebookMustBeOpened"), Messages
                    .getString("NotebookOutlineJPanel.discardConceptError"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        // move concept up in the tree
        DefaultMutableTreeNode node = getSelectedTreeNode();
        if (node != null) {
            if (node.isLeaf() && node.getParent()!=null) {
                try {
                    if (JOptionPane.showConfirmDialog(MindRaider.mainJFrame, Messages.getString(
                            "NotebookOutlineJPanel.doYouWantToDiscardConcept", node.toString()), Messages
                            .getString("NotebookOutlineJPanel.discardConcept"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                        return;
                    }

                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                    String notebookUri = MindRaider.outlineCustodian.getActiveOutlineResource().resource
                            .getMetadata().getUri().toString();
                    MindRaider.noteCustodian.discard(notebookUri, ((OutlineNode) parent).getUri(),
                            ((OutlineNode) node).getUri());
                    refresh();
                    MindRaider.spidersGraph.selectNodeByUri(notebookUri);
                    MindRaider.spidersGraph.renderModel();
                    
                    conceptJPanel.clear();
                } catch (Exception e1) {
                    logger.debug(Messages.getString("NotebookOutlineJPanel.unableToDiscardConcept"), e1);
                    StatusBar.show(Messages.getString("NotebookOutlineJPanel.unableToDiscardConcept"));
                }
            } else {
                StatusBar.show(Messages.getString("NotebookOutlineJPanel.discardingOnlyLeafConcepts"));
                JOptionPane.showMessageDialog(OutlineJPanel.this, Messages
                        .getString("NotebookOutlineJPanel.discardingOnlyLeafConcepts"), "Concept Discard Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            logger.debug(Messages.getString("NotebookOutlineJPanel.noNodeSelected"));
        }
    }

    public void conceptRefactor() {
        String sourceOutlineUri;
        String sourceConceptUri;
        
        if (MindRaider.profile.getActiveOutlineUri() == null) {
            JOptionPane.showMessageDialog(OutlineJPanel.this, Messages
                    .getString("NotebookOutlineJPanel.toRefactorConceptTheNotebookMustBeOpened"), Messages
                    .getString("NotebookOutlineJPanel.refactoringError"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultMutableTreeNode node = getSelectedTreeNode();
        if (node != null) {
            if (node.isLeaf()) {
                try {
                    if (JOptionPane.showConfirmDialog(MindRaider.mainJFrame, Messages.getString(
                            "NotebookOutlineJPanel.doYouWantToRefactorConcept", node.toString()), Messages
                            .getString("NotebookOutlineJPanel.refactorConcept"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                        return;
                    }

                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                    sourceOutlineUri = MindRaider.outlineCustodian.getActiveOutlineResource().resource
                            .getMetadata().getUri().toString();
                    sourceConceptUri = ((OutlineNode)node).getUri();
                    MindRaider.noteCustodian.discard(sourceOutlineUri, ((OutlineNode)parent).getUri(),
                            sourceConceptUri);
                    ConceptResource conceptResource = MindRaider.noteCustodian.get(sourceOutlineUri,
                            sourceConceptUri);
                    
                    // choose target outline and create new concept into this notebook
                    new OpenOutlineJDialog(
                    Messages.getString("NotebookOutlineJPanel.refactorConcept"), Messages
                            .getString("NotebookOutlineJPanel.selectTargetNotebook"), Messages
                            .getString("NotebookOutlineJPanel.refactor"), false);

                    // create that concept in the target notebook (put it to the root)
                    String targetConceptUri = conceptResource.resource.getMetadata().getUri().toString();
                    while (MindRaiderConstants.EXISTS.equals(MindRaider.noteCustodian.create(
                            MindRaider.outlineCustodian.getActiveOutlineResource(), null, conceptResource.getLabel(),
                            targetConceptUri, conceptResource.getAnnotation(), false))) {
                        targetConceptUri += "_";
                    }
                    // refactor also attachments
                    AttachmentProperty[] attachments = conceptResource.getAttachments();
                    if (!ArrayUtils.isEmpty(attachments)) {
                        ConceptResource newConceptResource = MindRaider.noteCustodian.get(
                                MindRaider.outlineCustodian.getActiveOutlineResource().resource.getMetadata()
                                        .getUri().toString(), targetConceptUri);
                        for (AttachmentProperty attachment : attachments) {
                            newConceptResource.addAttachment(attachment.getDescription(), attachment.getUrl());
                        }
                        newConceptResource.save();
                    }

                    // delete discarded concept in the source outline
                    MindRaider.noteCustodian.deleteConcept(sourceOutlineUri, sourceConceptUri);
                    
                    refresh();
                    MindRaider.spidersGraph.selectNodeByUri(sourceOutlineUri);
                    MindRaider.spidersGraph.renderModel();
                } catch (Exception e1) {
                    logger.debug(Messages.getString("NotebookOutlineJPanel.unableToRefactorConcept"), e1);
                    StatusBar.show(Messages.getString("NotebookOutlineJPanel.unableToRefactorConcept"));
                }
            } else {
                StatusBar.show(Messages.getString("NotebookOutlineJPanel.discardingingOnlyLeafConcepts"));
                JOptionPane.showMessageDialog(OutlineJPanel.this, Messages
                        .getString("NotebookOutlineJPanel.refactoringOnlyLeafConcepts"), Messages
                        .getString("NotebookOutlineJPanel.refactoringError"), JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            logger.debug(Messages.getString("NotebookOutlineJPanel.noNodeSelected"));
        }

    }

    /**
     * Promote concept.
     */
    public void conceptPromote() {
        // imagine the row and move it to the level up (left) in this row
        // node must be stored as parent's parent
        DefaultMutableTreeNode node = getSelectedTreeNode();
        if (node != null) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            if (parent != null) {
                DefaultMutableTreeNode pparent = (DefaultMutableTreeNode) parent.getParent();
                if (pparent != null) {
                    // now put it right behind previous parent
                    int parentIndex = pparent.getIndex(parent);

                    parent.remove(node);
                    pparent.insert(node, parentIndex + 1);
                    treeTable.updateUI();
                    logger.debug(Messages.getString("NotebookOutlineJPanel.noNodePromoted"));

                    MindRaider.noteCustodian.promote(MindRaider.outlineCustodian.getActiveOutlineResource(),
                            ((OutlineNode) pparent).getUri(), ((OutlineNode) parent).getUri(),
                            ((OutlineNode) node).getUri());
                } else {
                    logger.debug(Messages.getString("NotebookOutlineJPanel.noParentsParent"));
                }
            } else {
                logger.debug(Messages.getString("NotebookOutlineJPanel.noParent"));
            }
        } else {
            logger.debug(Messages.getString("NotebookOutlineJPanel.noNodeSelected"));
        }
    }

    /**
     * Up concept.
     */
    public boolean conceptUp() {
        // move concept up in the tree
        DefaultMutableTreeNode node = getSelectedTreeNode();
        if (node != null) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            if (parent != null) {
                DefaultMutableTreeNode previousSibling = node.getPreviousSibling();
                if (previousSibling != null) {
                    // move it up in the model
                    if (MindRaider.noteCustodian.up(MindRaider.outlineCustodian.getActiveOutlineResource(),
                            ((OutlineNode) parent).getUri(), ((OutlineNode) node).getUri())) {
                        int siblingIndex = parent.getIndex(previousSibling);
                        parent.remove(node);
                        parent.insert(node, siblingIndex);
                        treeTable.updateUI();
                        logger.debug(Messages.getString("NotebookOutlineJPanel.noMovedUp"));
                        return true;
                    }
                    // else it is the first concept in the sequence
                } else {
                    logger.debug("No sibling!");
                }
            } else {
                logger.debug(Messages.getString("NotebookOutlineJPanel.noParent"));
            }
        } else {
            logger.debug(Messages.getString("NotebookOutlineJPanel.noNodeSelected"));
        }
        return false;
    }

    /**
     * Down concept.
     */
    public boolean conceptDown() {
        // move concept down in the tree
        DefaultMutableTreeNode node = getSelectedTreeNode();
        if (node != null) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            if (parent != null) {
                DefaultMutableTreeNode nextSibling = node.getNextSibling();
                if (nextSibling != null) {
                    if (MindRaider.noteCustodian.down(MindRaider.outlineCustodian.getActiveOutlineResource(),
                            ((OutlineNode) parent).getUri(), ((OutlineNode) node).getUri())) {
                        int siblingIndex = parent.getIndex(nextSibling);
                        parent.remove(node);
                        parent.insert(node, siblingIndex);
                        treeTable.updateUI();
                        logger.debug("Node moved down!");
                        return true;
                    }
                    // else node the last in the sequence
                } else {
                    logger.debug("No sibling!");
                }
            } else {
                logger.debug(Messages.getString("NotebookOutlineJPanel.noParent"));
            }
        } else {
            logger.debug(Messages.getString("NotebookOutlineJPanel.noNodeSelected"));
        }
        return false;
    }

    /**
     * Demote concept.
     */
    public void conceptDemote() {
        // current node becomes the first child of the previous sibling, if node
        // has index 0, then do nothing
        DefaultMutableTreeNode node = getSelectedTreeNode();
        if (node != null) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            if (parent != null) {
                DefaultMutableTreeNode previous = (DefaultMutableTreeNode) parent.getChildBefore(node);
                if (previous != null) {
                    parent.remove(node);
                    previous.insert(node, previous.getChildCount());
                    treeTable.updateUI();

                    logger.debug(Messages.getString("NotebookOutlineJPanel.conceptDemoted"));

                    MindRaider.noteCustodian.demote(MindRaider.outlineCustodian.getActiveOutlineResource(),
                            ((OutlineNode) parent).getUri(), ((OutlineNode) node).getUri());

                    setSelectedTreeNodeConcept(((OutlineNode) node).getUri());
                } else {
                    logger.debug(Messages.getString("NotebookOutlineJPanel.isTheFirstChild"));
                }
            } else {
                logger.debug(Messages.getString("NotebookOutlineJPanel.noParent"));
            }
        } else {
            logger.debug(Messages.getString("NotebookOutlineJPanel.noNodeSelected!"));
        }
    }

    public void loadConcept(String notebookUri, String conceptUri) {
        if(notebookUri!=null && conceptUri!=null) {
            try {
                MindRaider.outlineCustodian.loadOutline(new URI(notebookUri));
                OutlineJPanel.getInstance().refresh();
                OutlineJPanel.getInstance().conceptJPanel.open(MindRaider.noteCustodian.get(notebookUri, conceptUri));            
                MindRaider.spidersGraph.selectNodeByUri(conceptUri);                    
            } catch (Exception e) {
                logger.debug("Unable to load concept!",e); // {{debug}}
            }            
        }
    }
    
    private static final long serialVersionUID = 5042795015913549213L;
}
