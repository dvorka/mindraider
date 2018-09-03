/*
 ===========================================================================
   Copyright 2002-2018 Martin Dvorak

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
package com.emental.mindraider.ui.frames;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.facet.BriefFacet;
import com.emental.mindraider.core.facet.FacetCustodian;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.core.search.SearchCommander;
import com.emental.mindraider.ui.dialogs.AboutJDialog;
import com.emental.mindraider.ui.dialogs.FtsJDialog;
import com.emental.mindraider.ui.dialogs.MindForgerCredentialsDialog;
import com.emental.mindraider.ui.dialogs.MindForgerDownloadOutlineJDialog;
import com.emental.mindraider.ui.dialogs.MindForgerUploadOutlineJDialog;
import com.emental.mindraider.ui.dialogs.OpenConceptByTagJDialog;
import com.emental.mindraider.ui.dialogs.OpenNoteJDialog;
import com.emental.mindraider.ui.dialogs.OpenOutlineJDialog;
import com.emental.mindraider.ui.dialogs.ProgressDialogJFrame;
import com.emental.mindraider.ui.dnd.DragAndDropReference;
import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.emental.mindraider.ui.graph.spiders.SpidersGraph;
import com.emental.mindraider.ui.menus.UriJRadioButtonMenuItem;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.application.model.outline.OutlineCustodian;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.commons.config.ConfigurationBean;
import com.mindcognition.mindraider.commons.config.PreferencesJDialog;
import com.mindcognition.mindraider.install.Installer;
import com.mindcognition.mindraider.integration.mindforger.MindForgerClient;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.tools.Checker;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.NewOutlineJDialog;
import com.mindcognition.mindraider.ui.swing.explorer.ExplorerJPanel;
import com.mindcognition.mindraider.ui.swing.main.MasterToolBar;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;
import com.mindcognition.mindraider.ui.swing.trash.TrashJPanel;
import com.mindcognition.mindraider.utils.Launcher;
import com.mindcognition.mindraider.utils.SwingWorker;
import com.mindcognition.mindraider.utils.Utils;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public final class MindRaiderMainWindow extends JFrame implements DropTargetListener, ComponentListener {
    private static final Logger logger = Logger.getLogger(MindRaiderMainWindow.class);

    public JSplitPane leftSidebarSplitPane;
    public DragAndDropReference dragAndDropReference;
    private boolean latelyMaximized;
    private ConfigurationBean configuration;
    
    private static MindRaiderMainWindow singleton;
    public static MindRaiderMainWindow getInstance() {
        if (singleton == null) {
            singleton = new MindRaiderMainWindow();
        }
        return singleton;
    }

    private MindRaiderMainWindow() {
        super(MindRaider.getTitle(), Gfx.getGraphicsConfiguration());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        // catch resize
        addComponentListener(this);

        configuration=new ConfigurationBean();
        
        // drag & drop registration
        DropTarget dropTarget = new DropTarget(this, (DropTargetListener) this);
        this.setDropTarget(dropTarget);

        // warn on different java version
        // checkJavaVersion();

        singleton = this;

        setIconImage(IconsRegistry.getImage("programIcon.gif"));
        SplashScreen splash = new SplashScreen(this, false);
        splash.showSplashScreen();

        // kernel init
        MindRaider.preSetProfiles();
        // message in here because of locales
        logger.debug(Messages.getString("MindRaiderJFrame.bootingKernel"));

        // master control panel
        MindRaider.setMasterToolBar(new MasterToolBar());
        getContentPane().add(MindRaider.masterToolBar, BorderLayout.NORTH);

        // status bar
        getContentPane().add(StatusBar.getStatusBar(), BorderLayout.SOUTH);

        // build menu
        buildMenu(MindRaider.spidersGraph);
        // profile
        MindRaider.setProfiles();

        // left sidebar: folder/notebooks hierarchy, taxonomies, ...
        final JTabbedPane leftSidebar = new JTabbedPane(SwingConstants.BOTTOM);
        leftSidebar.setTabPlacement(SwingConstants.TOP);
        // TODO add icons to tabs
        leftSidebar.addTab(Messages.getString("MindRaiderJFrame.explorer"),
                ExplorerJPanel.getInstance());
        
        // TODO just blank panel
        //leftSidebar.addTab("Tags",new OutlookBarMain());
        
        leftSidebar.addTab(Messages.getString("MindRaiderJFrame.trash"), /* IconsRegistry.getImageIcon("trashFull.png"), */                        
        TrashJPanel.getInstance());
                
        leftSidebar.setSelectedIndex(0);
        leftSidebar.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent arg0) {
                if (arg0.getSource() instanceof JTabbedPane) {
                    if (leftSidebar.getSelectedIndex() == 1) {
                        // refresh trash
                        TrashJPanel.getInstance().refresh();
                    }
                }
            }
        });

        // main panel: (notebook outline & RDF Navigator) + Control panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(OutlineJPanel.getInstance(), BorderLayout.CENTER);

        // split: left sidebar/main panel
        leftSidebarSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftSidebar, mainPanel);
        leftSidebarSplitPane.setOneTouchExpandable(true);
        leftSidebarSplitPane.setDividerLocation(200);
        leftSidebarSplitPane.setLastDividerLocation(200);
        leftSidebarSplitPane.setDividerSize(6);
        leftSidebarSplitPane.setContinuousLayout(true);
        getContentPane().add(leftSidebarSplitPane, BorderLayout.CENTER);

        Gfx.centerAndShowWindow(this, 1024, 768);

        MindRaider.postSetProfiles();

        if(!configuration.isShowSpidersTagSnailPane()) {
            OutlineJPanel.getInstance().hideSpiders();
        }
        
        splash.hideSplash();        
    }

    /**
     * Build main menu.
     * 
     * @param spiders
     */
    private void buildMenu(final SpidersGraph spiders) {
        JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem, subMenuItem;
        JRadioButtonMenuItem rbMenuItem;

        // create the menu bar
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // - main menu -------------------------------------------------------
        menu = new JMenu(MindRaiderConstants.MR_TITLE);
        menu.setMnemonic(KeyEvent.VK_M);

        menuItem = new JMenuItem(Messages
                .getString("MindRaiderJFrame.setActiveNotebookAsHome"));
        menuItem.setMnemonic(KeyEvent.VK_H);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                MindRaider.profile.setHomeNotebook();
            }
        });
        menu.add(menuItem);
        menu.add(menuItem);

        menuItem = new JMenuItem(Messages
                .getString("MindRaiderJFrame.preferences"));
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new PreferencesJDialog();
            }
        });
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.exit"),
                KeyEvent.VK_X);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                exitMindRaider();
            }
        });
        menu.add(menuItem);

        menuBar.add(menu);

        // - Find ----------------------------------------------------------

        menu = new JMenu(Messages.getString("MindRaiderJFrame.search"));
        menu.setMnemonic(KeyEvent.VK_F);

        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.searchNotebooks"));
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new OpenOutlineJDialog();
            }
        });
        menu.add(menuItem);
                
        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.searchFulltext"));
        menuItem.setMnemonic(KeyEvent.VK_F);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        menuItem.setEnabled(true);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new FtsJDialog();
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.searchConceptsInNotebook"));
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (MindRaider.profile.getActiveOutlineUri() != null) {
                    new OpenNoteJDialog();
                }
            }
        });
        menu.add(menuItem);
        
        // search by tag
        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.searchConceptsByTag"));
        menuItem.setEnabled(true);
        menuItem.setMnemonic(KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
                ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new OpenConceptByTagJDialog();
            }
        });
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(Messages.getString("MindRaiderMainWindow.previousNote"));
        menuItem.setEnabled(true);
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MindRaider.recentConcepts.moveOneNoteBack();                
            }
        });
        menu.add(menuItem);
        
        // global RDF search
//        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.searchRdql"));
//        menuItem.setEnabled(false);
//        menuItem.setMnemonic(KeyEvent.VK_R);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
//                ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
//        menuItem.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                // TODO rdql to be implemented
//            }
//        });
//        menu.add(menuItem);
        
        menuBar.add(menu);

        // - view ------------------------------------------------------------
        menu = new JMenu(Messages.getString("MindRaiderJFrame.view"));
        menu.setMnemonic(KeyEvent.VK_V);

        // TODO localize L&F menu
        ButtonGroup lfGroup = new ButtonGroup();
        submenu = new JMenu(Messages.getString("MindRaiderJFrame.lookAndFeel"));
        logger
                .debug("Look and feel is: "
                        + MindRaider.profile.getLookAndFeel()); // {{debug}}
        submenu.setMnemonic(KeyEvent.VK_L);
        subMenuItem = new JRadioButtonMenuItem(Messages
                .getString("MindRaiderJFrame.lookAndFeelNative"));
        if (MindRaider.LF_NATIVE.equals(MindRaider.profile.getLookAndFeel())) {
            subMenuItem.setSelected(true);
        }
        subMenuItem.setEnabled(true);
        subMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setLookAndFeel(MindRaider.LF_NATIVE);
            }
        });
        submenu.add(subMenuItem);
        lfGroup.add(subMenuItem);
        subMenuItem = new JRadioButtonMenuItem(Messages
                .getString("MindRaiderJFrame.lookAndFeelJava"));
        if (MindRaider.LF_JAVA_DEFAULT.equals(MindRaider.profile
                .getLookAndFeel())) {
            subMenuItem.setSelected(true);
        }
        subMenuItem.setEnabled(true);
        subMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setLookAndFeel(MindRaider.LF_JAVA_DEFAULT);
            }
        });
        submenu.add(subMenuItem);
        lfGroup.add(subMenuItem);
        menu.add(submenu);

        menu.addSeparator();
        menuItem = new JMenuItem(Messages
                .getString("MindRaiderJFrame.leftSideBar"));
        menuItem.setMnemonic(KeyEvent.VK_L);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (leftSidebarSplitPane.getDividerLocation() == 1) {
                    leftSidebarSplitPane.resetToPreferredSizes();
                } else {
                    closeLeftSidebar();
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(Messages
                .getString("MindRaiderJFrame.rightSideBar"));
        menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                OutlineJPanel.getInstance().toggleRightSidebar();
            }
        });
        menu.add(menuItem);

        // TODO tips to be implemented
        // JCheckBoxMenuItem helpCheckbox=new JCheckBoxMenuItem("Tips",true);
        // menu.add(helpCheckbox);

        // TODO localize
        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.toolbar"));
        menuItem.setMnemonic(KeyEvent.VK_T);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                MindRaider.masterToolBar.toggleVisibility();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(Messages
                .getString("MindRaiderJFrame.rdfNavigatorDashboard"));
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                MindRaider.spidersGraph.getGlPanel().toggleControlPanel();
            }
        });
        menu.add(menuItem);

        JCheckBoxMenuItem checkboxMenuItem;
        ButtonGroup colorSchemeGroup;

//        if (!MindRaider.OUTLINER_PERSPECTIVE.equals(MindRaider.profile
//                .getUiPerspective())) {

            menu.addSeparator();

            // Facets
            submenu = new JMenu(Messages.getString("MindRaiderJFrame.facet"));
            submenu.setMnemonic(KeyEvent.VK_F);
            colorSchemeGroup = new ButtonGroup();

            String[] facetLabels = FacetCustodian.getInstance()
                    .getFacetLabels();
            if (!ArrayUtils.isEmpty(facetLabels)) {
                for (String facetLabel : facetLabels) {
                    rbMenuItem = new JRadioButtonMenuItem(facetLabel);
                    rbMenuItem.addActionListener(new FacetActionListener(facetLabel));
                    colorSchemeGroup.add(rbMenuItem);
                    submenu.add(rbMenuItem);
                    if (BriefFacet.LABEL.equals(facetLabel)) {
                        rbMenuItem.setSelected(true);
                    }
                }

            }
            menu.add(submenu);

            checkboxMenuItem = new JCheckBoxMenuItem(Messages
                    .getString("MindRaiderJFrame.graphLabelAsUri"));
            checkboxMenuItem.setMnemonic(KeyEvent.VK_G);
            checkboxMenuItem.setState(MindRaider.spidersGraph.isUriLabels());
            checkboxMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() instanceof JCheckBoxMenuItem) {
                        JCheckBoxMenuItem j = (JCheckBoxMenuItem) e.getSource();
                        MindRaider.spidersGraph.setUriLabels(j.getState());
                        MindRaider.spidersGraph.renderModel();
                        MindRaider.profile.setGraphShowLabelsAsUris(j
                                .getState());
                        MindRaider.profile.save();
                    }
                }
            });

            menu.add(checkboxMenuItem);

            checkboxMenuItem = new JCheckBoxMenuItem(Messages
                    .getString("MindRaiderJFrame.predicateNodes"));
            checkboxMenuItem.setMnemonic(KeyEvent.VK_P);
            checkboxMenuItem.setState(!MindRaider.spidersGraph
                    .getHidePredicates());
            checkboxMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() instanceof JCheckBoxMenuItem) {
                        JCheckBoxMenuItem j = (JCheckBoxMenuItem) e.getSource();
                        MindRaider.spidersGraph.hidePredicates(!j.getState());
                        MindRaider.spidersGraph.renderModel();
                        MindRaider.profile
                                .setGraphHidePredicates(!j.getState());
                        MindRaider.profile.save();
                    }
                }
            });
            menu.add(checkboxMenuItem);

            checkboxMenuItem = new JCheckBoxMenuItem(Messages
                    .getString("MindRaiderJFrame.multilineLabels"));
            checkboxMenuItem.setMnemonic(KeyEvent.VK_M);
            checkboxMenuItem.setState(MindRaider.spidersGraph
                    .isMultilineNodes());
            checkboxMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() instanceof JCheckBoxMenuItem) {
                        JCheckBoxMenuItem j = (JCheckBoxMenuItem) e.getSource();
                        MindRaider.spidersGraph.setMultilineNodes(j.getState());
                        MindRaider.spidersGraph.renderModel();
                        MindRaider.profile
                                .setGraphMultilineLabels(j.getState());
                        MindRaider.profile.save();
                    }
                }
            });
            menu.add(checkboxMenuItem);
//        }

        menu.addSeparator();

        // Antialias
        checkboxMenuItem = new JCheckBoxMenuItem(Messages
                .getString("MindRaiderJFrame.antiAliased"), true);
        checkboxMenuItem.setMnemonic(KeyEvent.VK_A);
        checkboxMenuItem.setState(SpidersGraph.antialiased);
        checkboxMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JCheckBoxMenuItem) {
                    JCheckBoxMenuItem j = (JCheckBoxMenuItem) e.getSource();
                    SpidersGraph.antialiased = j.getState();
                    MindRaider.spidersGraph.renderModel();
                }
            }
        });
        menu.add(checkboxMenuItem);

        // Enable hyperbolic
        checkboxMenuItem = new JCheckBoxMenuItem(Messages.getString("MindRaiderJFrame.hyperbolic"), true);
        checkboxMenuItem.setMnemonic(KeyEvent.VK_H);
        checkboxMenuItem.setState(SpidersGraph.hyperbolic);
        checkboxMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JCheckBoxMenuItem) {
                    JCheckBoxMenuItem j = (JCheckBoxMenuItem) e.getSource();
                    SpidersGraph.hyperbolic = j.getState();
                    MindRaider.spidersGraph.renderModel();
                }
            }
        });
        menu.add(checkboxMenuItem);

        // Show FPS
        checkboxMenuItem = new JCheckBoxMenuItem(Messages.getString("MindRaiderJFrame.fps"), true);
        checkboxMenuItem.setMnemonic(KeyEvent.VK_F);
        checkboxMenuItem.setState(SpidersGraph.fps);
        checkboxMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JCheckBoxMenuItem) {
                    JCheckBoxMenuItem j = (JCheckBoxMenuItem) e.getSource();
                    SpidersGraph.fps = j.getState();
                    MindRaider.spidersGraph.renderModel();
                }
            }
        });
        menu.add(checkboxMenuItem);

        
        
        // Graph color scheme
        submenu = new JMenu(Messages.getString("MindRaiderJFrame.colorScheme"));
        submenu.setMnemonic(KeyEvent.VK_C);
        String[] allProfilesUris = MindRaider.spidersColorProfileRegistry.getAllProfilesUris();
        colorSchemeGroup = new ButtonGroup();
        for (int i = 0; i < allProfilesUris.length; i++) {
            rbMenuItem = new UriJRadioButtonMenuItem(
                    MindRaider.spidersColorProfileRegistry.getColorProfileByUri(allProfilesUris[i]).getLabel(),
                    allProfilesUris[i]);
            rbMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(e.getSource() instanceof UriJRadioButtonMenuItem) {
                        MindRaider.spidersColorProfileRegistry.setCurrentProfile(
                                ((UriJRadioButtonMenuItem)e.getSource()).uri);
                        MindRaider.spidersGraph
                            .setRenderingProfile(MindRaider.spidersColorProfileRegistry.getCurrentProfile());
                        MindRaider.spidersGraph.renderModel();
                    }
                }
            });
            colorSchemeGroup.add(rbMenuItem);
            submenu.add(rbMenuItem);
        }
        menu.add(submenu);

        
        
        // Annotation color scheme
        submenu = new JMenu(Messages.getString("MindRaiderJFrame.colorSchemeAnnotation"));
        submenu.setMnemonic(KeyEvent.VK_A);
        allProfilesUris = MindRaider.annotationColorProfileRegistry.getAllProfilesUris();
        colorSchemeGroup = new ButtonGroup();
        for (int i = 0; i < allProfilesUris.length; i++) {
            rbMenuItem = new UriJRadioButtonMenuItem(
                    MindRaider.annotationColorProfileRegistry.getColorProfileByUri(allProfilesUris[i]).getLabel(),
                    allProfilesUris[i]);
            rbMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(e.getSource() instanceof UriJRadioButtonMenuItem) {
                        MindRaider.annotationColorProfileRegistry.setCurrentProfile(
                                ((UriJRadioButtonMenuItem)e.getSource()).uri);
                        OutlineJPanel.getInstance().conceptJPanel.refresh();
                    }
                }
            });
            colorSchemeGroup.add(rbMenuItem);
            submenu.add(rbMenuItem);
        }
        menu.add(submenu);
        
        
        
        menu.addSeparator();

        checkboxMenuItem = new JCheckBoxMenuItem(Messages
                .getString("MindRaiderJFrame.fullScreen"));
        checkboxMenuItem.setMnemonic(KeyEvent.VK_U);
        checkboxMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F11,0));
        checkboxMenuItem.setState(false);
        checkboxMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JCheckBoxMenuItem) {
                    JCheckBoxMenuItem j = (JCheckBoxMenuItem) e.getSource();
                    if (j.getState()) {
                        Gfx.toggleFullScreen(MindRaiderMainWindow.this);
                    } else {
                        Gfx.toggleFullScreen(null);
                    }
                }
            }
        });
        menu.add(checkboxMenuItem);

        menuBar.add(menu);

        // - outline
        // ----------------------------------------------------------------------
        menu = new JMenu(Messages.getString("MindRaiderJFrame.notebook"));
        menu.setMnemonic(KeyEvent.VK_N);

        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.newNotebook"));
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // TODO clear should be optional - only if creation finished
                // MindRider.spidersGraph.clear();
                new NewOutlineJDialog();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.open"));
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new OpenOutlineJDialog();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.close"));
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                MindRaider.outlineCustodian.close();
                OutlineJPanel.getInstance().refresh();
                MindRaider.spidersGraph.renderModel();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.discard"));
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.setEnabled(false); // TODO discard method must be implemented
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        MindRaiderMainWindow.this, Messages.getString(
                                "MindRaiderJFrame.confirmDiscardNotebook",
                                MindRaider.profile.getActiveOutline()));
                if (result == JOptionPane.YES_OPTION) {
                    if (MindRaider.profile.getActiveOutlineUri() != null) {
                        try {
                            MindRaider.labelCustodian
                                    .discardOutline(MindRaider.profile
                                            .getActiveOutlineUri().toString());
                            MindRaider.outlineCustodian.close();
                        } catch (Exception e1) {
                            logger
                                    .error(
                                            Messages
                                                    .getString("MindRaiderJFrame.unableToDiscardNotebook"),
                                            e1);
                        }
                    }
                }
            }
        });
        menu.add(menuItem);

        menu.addSeparator();

        // export
        submenu = new JMenu(Messages.getString("MindRaiderJFrame.export"));
        submenu.setMnemonic(KeyEvent.VK_E);
        // Atom
        subMenuItem = new JMenuItem("Atom");
        subMenuItem.setEnabled(true);
        subMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportActiveOutlineToAtom();
            }
        });
        submenu.add(subMenuItem);
        
        // OPML
        subMenuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.opml"));
        subMenuItem.setEnabled(true);
        subMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (MindRaider.profile.getActiveOutline() == null) {
                    JOptionPane
                            .showMessageDialog(
                                    MindRaiderMainWindow.this,
                                    Messages
                                            .getString("MindRaiderJFrame.exportNotebookWarning"),
                                    Messages
                                            .getString("MindRaiderJFrame.exportError"),

                                    JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JFileChooser fc = new JFileChooser();
                fc.setApproveButtonText(Messages
                        .getString("MindRaiderJFrame.export"));
                fc.setControlButtonsAreShown(true);
                fc.setDialogTitle(Messages
                        .getString("MindRaiderJFrame.chooseExportDirectory"));
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                // prepare directory
                String exportDirectory = MindRaider.profile.getHomeDirectory()
                        + File.separator + "export" + File.separator + "opml";
                Utils.createDirectory(exportDirectory);
                fc.setCurrentDirectory(new File(exportDirectory));
                int returnVal = fc.showOpenDialog(MindRaiderMainWindow.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String dstFileName = fc.getSelectedFile().getAbsolutePath()
                            + File.separator
                            + "OPML-EXPORT-"
                            + MindRaider.outlineCustodian
                                    .getActiveNotebookNcName() + ".xml";
                    logger.debug(Messages.getString(
                            "MindRaiderJFrame.exportingToFile", dstFileName));
                    MindRaider.outlineCustodian.exportOutline(
                            OutlineCustodian.FORMAT_OPML, dstFileName);
                    Launcher.launchViaStart(dstFileName);
                } else {
                    logger
                            .debug(Messages
                                    .getString("MindRaiderJFrame.exportCommandCancelledByUser"));
                }
            }
        });
        submenu.add(subMenuItem);
        // TWiki
        subMenuItem = new JMenuItem("TWiki");
        subMenuItem.setEnabled(true);
        subMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (MindRaider.profile.getActiveOutline() == null) {
                    JOptionPane
                            .showMessageDialog(
                                    MindRaiderMainWindow.this,
                                    Messages
                                            .getString("MindRaiderJFrame.exportNotebookWarning"),
                                    Messages
                                            .getString("MindRaiderJFrame.exportError"),
                                    JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JFileChooser fc = new JFileChooser();
                fc.setApproveButtonText(Messages
                        .getString("MindRaiderJFrame.export"));
                fc.setControlButtonsAreShown(true);
                fc.setDialogTitle(Messages
                        .getString("MindRaiderJFrame.chooseExportDirectory"));
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                // prepare directory
                String exportDirectory = MindRaider.profile.getHomeDirectory()
                        + File.separator + "export" + File.separator + "twiki";
                Utils.createDirectory(exportDirectory);
                fc.setCurrentDirectory(new File(exportDirectory));
                int returnVal = fc.showOpenDialog(MindRaiderMainWindow.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    final String dstFileName = fc.getSelectedFile()
                            .getAbsolutePath()
                            + File.separator
                            + "TWIKI-EXPORT-"
                            + MindRaider.outlineCustodian
                                    .getActiveNotebookNcName() + ".txt";
                    logger.debug(Messages.getString(
                            "MindRaiderJFrame.exportingToFile", dstFileName));

                    MindRaider.outlineCustodian.exportOutline(
                            OutlineCustodian.FORMAT_TWIKI, dstFileName);
                } else {
                    logger
                            .debug(Messages
                                    .getString("MindRaiderJFrame.exportCommandCancelledByUser"));
                }
            }
        });
        submenu.add(subMenuItem);
        
        menu.add(submenu);

        // import
        submenu = new JMenu(Messages.getString("MindRaiderJFrame.import"));
        submenu.setMnemonic(KeyEvent.VK_I);
        
        subMenuItem = new JMenuItem("Atom");
        subMenuItem.setEnabled(true);
        subMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importFromAtom();
            }
        });
        submenu.add(subMenuItem);
                
        // TWiki
        subMenuItem = new JMenuItem("TWiki");
        subMenuItem.setEnabled(true);
        subMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // choose file to be transformed
                OutlineJPanel.getInstance().clear();
                MindRaider.profile.setActiveOutlineUri(null);
                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(MindRaiderMainWindow.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    final File file = fc.getSelectedFile();
                    MindRaider.profile.deleteActiveModel();
                    logger.debug(Messages.getString(
                            "MindRaiderJFrame.importingTWikiTopic", file.getAbsolutePath()));
                    
                    // perform it async
                    final SwingWorker worker = new SwingWorker() {

                        public Object construct() {
                            ProgressDialogJFrame progressDialogJFrame = new ProgressDialogJFrame(
                                    Messages
                                            .getString("MindRaiderJFrame.twikiImport"),
                                    Messages
                                            .getString("MindRaiderJFrame.processingTopicTWiki"));
                            try {
                                MindRaider.outlineCustodian.importNotebook(
                                        OutlineCustodian.FORMAT_TWIKI,
                                        (file!=null?file.getAbsolutePath():null), progressDialogJFrame);
                            } finally {
                                if (progressDialogJFrame != null) {
                                    progressDialogJFrame.dispose();
                                }
                            }
                            return null;
                        }
                    };
                    worker.start();
                } else {
                    logger
                            .debug(Messages
                                    .getString("MindRaiderJFrame.openCommandCancelledByUser"));
                }
            }
        });
        submenu.add(subMenuItem);
        
        menu.add(submenu);

        menuBar.add(menu);

        // - note
        // ----------------------------------------------------------------------
        menu = new JMenu(Messages.getString("MindRaiderJFrame.concept"));
        menu.setMnemonic(KeyEvent.VK_C);

        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.new"));
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                OutlineJPanel.getInstance().newConcept();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.open"));
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_N, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (MindRaider.profile.getActiveOutlineUri() != null) {
                    new OpenNoteJDialog();
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.discard"));
        // do not accelerate this command with DEL - it's already handled
        // elsewhere
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OutlineJPanel.getInstance().conceptDiscard();
            }
        });
        menu.add(menuItem);


        menu.addSeparator();

        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.up"));
        menuItem.setMnemonic(KeyEvent.VK_U);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP,
                ActionEvent.CTRL_MASK));
        menuItem.setEnabled(true);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                OutlineJPanel.getInstance().conceptUp();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.promote"));
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
                ActionEvent.CTRL_MASK));
        menuItem.setEnabled(true);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                OutlineJPanel.getInstance().conceptPromote();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.demote"));
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
                ActionEvent.CTRL_MASK));
        menuItem.setEnabled(true);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                OutlineJPanel.getInstance().conceptDemote();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.down"));
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,ActionEvent.CTRL_MASK));
        menuItem.setEnabled(true);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                OutlineJPanel.getInstance().conceptDown();
            }
        });
        menu.add(menuItem);

        menuBar.add(menu);
        
        // - Tools -----------------------------------------------------------

        menu = new JMenu(Messages.getString("MindRaiderJFrame.tools"));
        menu.setMnemonic(KeyEvent.VK_T);
        
        menuItem = new JMenuItem(Messages.getString("MindRaiderMainWindow.checkAndFix"));
        menuItem.setMnemonic(KeyEvent.VK_F);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Checker.checkAndFixRepositoryAsync();
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.backupRepository"));
        menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Installer.backupRepositoryAsync();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(Messages
                .getString("MindRaiderJFrame.rebuildSearchIndex"));
        menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                SearchCommander.rebuildSearchAndTagIndices();
            }
        });
        menu.add(menuItem);
                        
        menu.addSeparator();
                       
        menuItem = new JMenuItem(Messages.getString("MindRaiderMainWindow.captureScreen"));
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setApproveButtonText(Messages.getString("MindRaiderJFrame.screenshot"));
                fc.setControlButtonsAreShown(true);
                fc.setDialogTitle(Messages.getString("MindRaiderJFrame.chooseScreenshotDirectory"));
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                // prepare directory
                String exportDirectory = MindRaider.profile.getHomeDirectory()
                        + File.separator + "Screenshots";
                Utils.createDirectory(exportDirectory);
                fc.setCurrentDirectory(new File(exportDirectory));
                int returnVal = fc.showOpenDialog(MindRaiderMainWindow.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    final String filename = fc.getSelectedFile()
                            .getAbsolutePath()
                            + File.separator + "screenshot.jpg";

                    // do it in async (redraw screen)
                    Thread thread = new Thread() {
                        public void run() {                                                        
                            OutputStream file = null;
                            try {
                                file = new FileOutputStream(filename);

                                Robot robot = new Robot();
                                robot.delay(1000);

                                JPEGImageEncoder encoder = JPEGCodec
                                        .createJPEGEncoder(file);
                                encoder.encode(robot
                                        .createScreenCapture(new Rectangle(
                                                Toolkit.getDefaultToolkit()
                                                        .getScreenSize())));
                            } catch (Exception e1) {
                                logger.error("Unable to capture screen!", e1);
                            } finally {
                                if (file != null) {
                                    try {
                                        file.close();
                                    } catch (IOException e1) {
                                        logger.error("Unable to close stream",
                                                e1);
                                    }
                                }
                            }
                        }
                    };
                    thread.setDaemon(true);
                    thread.start();

                }
            }
        });
        menu.add(menuItem);
        
        menuBar.add(menu);
        
        // - MindForger -----------------------------------------------------------

        menu = new JMenu(Messages.getString("MindRaiderMainWindow.menuMindForger"));
        menu.setMnemonic(KeyEvent.VK_O);
        //menu.setIcon(IconsRegistry.getImageIcon("tasks-internet.png"));

        menuItem = new JMenuItem(Messages.getString("MindRaiderMainWindow.menuMindForgerVideoTutorial"));
        menuItem.setMnemonic(KeyEvent.VK_G);
        menuItem.setToolTipText("http://mindraider.sourceforge.net/mindforger.html");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Launcher.launchInBrowser("http://mindraider.sourceforge.net/mindforger.html");                
            }
        });
        menuItem.setEnabled(true);
        menu.add(menuItem);
        
        menuItem = new JMenuItem(Messages.getString("MindRaiderMainWindow.signUp"));
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.setToolTipText("http://www.mindforger.com");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Launcher.launchInBrowser("http://www.mindforger.com");                
            }
        });
        menuItem.setEnabled(true);
        menu.add(menuItem);
        
        menu.addSeparator();        
        
        menuItem = new JMenuItem(Messages.getString("MindRaiderMainWindow.menuMindForgerUpload"));
        menuItem.setMnemonic(KeyEvent.VK_U);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // fork in order to enable status updates in the main window
                new MindForgerUploadOutlineJDialog();
            }
        });
        menuItem.setEnabled(true);
        menu.add(menuItem);
        
        menuItem = new JMenuItem(Messages.getString("MindRaiderMainWindow.menuMindForgerDownload"));
        menuItem.setMnemonic(KeyEvent.VK_U);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                downloadAnOutlineFromMindForger();
            }
        });
        menuItem.setEnabled(true);
        menu.add(menuItem);
        
        menu.addSeparator();        
        
        menuItem = new JMenuItem(Messages.getString("MindRaiderMainWindow.menuMindForgerMyOutlines"));
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.setEnabled(true);
        menuItem.setToolTipText("http://web.mindforger.com");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Launcher.launchInBrowser("http://web.mindforger.com");                
            }
        });
        menu.add(menuItem);
           
        menuBar.add(menu);
        
        // - align Help on right -------------------------------------------------------------

        menuBar.add(Box.createHorizontalGlue());
        
        // - help -------------------------------------------------------------
        menu = new JMenu(Messages.getString("MindRaiderJFrame.help"));
        menu.setMnemonic(KeyEvent.VK_H);

        menuItem = new JMenuItem(Messages
                .getString("MindRaiderJFrame.documentation"));
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    MindRaider.outlineCustodian.loadOutline(
                            new URI(MindRaiderVocabulary.getNotebookUri(OutlineCustodian.MR_DOC_NOTEBOOK_DOCUMENTATION_LOCAL_NAME)));
                    OutlineJPanel.getInstance().refresh();
                } catch (Exception e1) {
                    logger.error(Messages.getString("MindRaiderJFrame.unableToLoadHelp", e1.getMessage()));
                }
            }
        });
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(Messages.getString("MindRaiderMainWindow.webHomepage"));
        menuItem.setMnemonic(KeyEvent.VK_H);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Launcher.launchInBrowser("http://mindraider.sourceforge.net");
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem(Messages.getString("MindRaiderMainWindow.reportBug"));
        menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Launcher.launchInBrowser("http://sourceforge.net/forum/?group_id=128454");
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem(Messages.getString("MindRaiderMainWindow.updateCheck"));
        menuItem.setMnemonic(KeyEvent.VK_F);
        menuItem.setEnabled(true);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// just open html page at:
            	//   http://mindraider.sourceforge.net/update-7.2.html
            	// this page will either contain "you have the last version" or will ask user to
                // download the latest version from main page
                Launcher.launchInBrowser(
                        "http://mindraider.sourceforge.net/" +
                        "update-"+MindRaiderConstants.majorVersion+"."+MindRaiderConstants.minorVersion+".html");
            }
        });
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(Messages.getString("MindRaiderJFrame.about",
                MindRaiderConstants.MR_TITLE));
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new AboutJDialog();
            }
        });
        menu.add(menuItem);

        menuBar.add(menu);
    }

    /**
     * Show spiders graph only.
     */
    protected void showSpidersGraphOnly() {
        if (leftSidebarSplitPane.getDividerLocation() > 1) {
            closeLeftSidebar();
        }
        OutlineJPanel.getInstance().hideRightSiderbar();
        OutlineJPanel.getInstance().hideConceptTree();
        OutlineJPanel.getInstance().disableAllToolbarButtons();
    }

    /**
     * Close left side bar.
     */
    public void closeLeftSidebar() {
        leftSidebarSplitPane.setDividerLocation(0);
    }

    /**
     * Maximize left side bar.
     * 
     * @param maximizeButton
     *            the maximize JButton
     */
    public void maximizeLeftSidebar(JButton maximizeButton) {
        if (latelyMaximized) {
            restoreLeftSidebar();
            if (maximizeButton != null) {
                maximizeButton.setIcon(IconsRegistry
                        .getImageIcon("explorerMaximizeRight.png"));
                maximizeButton.setToolTipText(Messages
                        .getString("MindRaiderJFrame.maximizeExplorerSidebar"));
            }
            latelyMaximized = false;
        } else {
            leftSidebarSplitPane.setDividerLocation(2000);
            if (maximizeButton != null) {
                maximizeButton.setIcon(IconsRegistry
                        .getImageIcon("explorerRestoreLeft.png"));
                maximizeButton.setToolTipText(Messages
                        .getString("MindRaiderJFrame.restoreExplorerSidebar"));
            }
            latelyMaximized = true;
        }
    }

    /**
     * Restore the left sidebar.
     */
    public void restoreLeftSidebar() {
        leftSidebarSplitPane.setDividerLocation(leftSidebarSplitPane
                .getLastDividerLocation());
    }

    private static class FacetActionListener implements ActionListener {

        private String facetLabel;

        public FacetActionListener(String facetLabel) {
            this.facetLabel = facetLabel;
        }

        public void actionPerformed(ActionEvent arg0) {
            MindRaider.spidersGraph.setFacet(FacetCustodian.getInstance().getFacet(facetLabel));
            MindRaider.spidersGraph.renderModel();
        }
    }

    public void dragEnter(DropTargetDragEvent arg0) {
    }

    public void dragOver(DropTargetDragEvent arg0) {
    }

    public void dropActionChanged(DropTargetDragEvent arg0) {
    }

    public void drop(DropTargetDropEvent evt) {
        logger.debug("=-> drop");

        try {
            Transferable t = evt.getTransferable();

            if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                logger.debug(" Accepting 'string' data flavor...");
                evt.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                evt.getDropTargetContext().dropComplete(true);

                logger.debug("DnD: '" + s + "'");
                if (s != null) {
                    int indexOf = s.indexOf("\n");
                    if (indexOf != -1) {
                        dragAndDropReference = new DragAndDropReference(s
                                .substring(indexOf + 1), s
                                .substring(0, indexOf),
                                DragAndDropReference.BROWSER_LINK);
                    } else {
                        dragAndDropReference = new DragAndDropReference(s,
                                DragAndDropReference.BROWSER_LINK);
                    }
                }
            } else {
                if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    logger.debug(" Accepting 'file list' data flavor...");
                    evt.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    List<Object> list = (List<Object>)t.getTransferData(DataFlavor.javaFileListFlavor);
                    if (list != null) {
                        Iterator<Object> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            Object next = iterator.next();
                            if (next instanceof File) {
                                logger.debug(" DnD file: " + next);
                                dragAndDropReference = new DragAndDropReference(
                                        ((File) next).getAbsolutePath(),
                                        DragAndDropReference.EXPLORER_LINK);
                            }
                        }
                    }
                } else {
                    logger.debug("DnD rejected! ");
                    dragAndDropReference = null;

                    // DataFlavor[] dfs=t.getTransferDataFlavors();
                    // for (int i = 0; i < dfs.length; i++) {
                    // logger.debug(" "+i+" ... "+dfs[i].getMimeType());
                    // logger.debug(" "+i+" ...
                    // "+dfs[i].getDefaultRepresentationClassAsString());
                    // logger.debug(" "+i+" ...
                    // "+dfs[i].getHumanPresentableName());
                    // }
                }
            }
        } catch (Exception e) {
            logger.debug("Drag&Drop error:", e);
            dragAndDropReference = null;
        }

        OutlineJPanel.getInstance().enableDisableAttachToolbarButton();

        if (dragAndDropReference != null) {
            JOptionPane
                    .showMessageDialog(
                            this,
                            "Dropped local/web resource reference stored! Use \n'clip' icon from Notebook outline toolbar to attach it.",
                            "Drag&Drop Info", JOptionPane.INFORMATION_MESSAGE);

            dragAndDropReference.debug();
        }
        logger.debug("<-= drop");
    }

    public void dragExit(DropTargetEvent arg0) {
    }

    private void setLookAndFeel(String lookAndFeel) {
        MindRaider.profile.setLookAndFeel(lookAndFeel);
        MindRaider.profile.save();
        JOptionPane.showMessageDialog(MindRaiderMainWindow.this,
                "To apply new L&F please restart "
                        + MindRaiderConstants.MR_TITLE + "!");
    }

    private void exitMindRaider() {
        logger.debug(Messages.getString("MindRaiderJFrame.exiting",
                MindRaiderConstants.MR_TITLE));
        System.exit(0);
    }

    private static final long serialVersionUID = 4092376300386589094L;
    
    /*
     * component listener
     */
    
    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }
    @Override
    public void componentResized(ComponentEvent e) {
        if(!configuration.isShowSpidersTagSnailPane()) {
            OutlineJPanel.getInstance().hideSpiders();
        }
    }
    @Override
    public void componentShown(ComponentEvent e) {
    }

    private void uploadActiveOutlineToMindForger() {
        if (MindRaider.profile.getActiveOutline() != null) {                                    
            StatusBar.show("Exporting active Outline '"+MindRaider.outlineCustodian.getActiveNotebookNcName()+"'...");
            String exportDirectory = MindRaider.profile.getHomeDirectory()+File.separator+"export"+File.separator+"sharing";
            Utils.createDirectory(exportDirectory);
            String dstDirectory = exportDirectory;
            Utils.createDirectory(dstDirectory);
            dstDirectory = dstDirectory+File.separator;
            String dstFile =                    
                    "MindRaider-"
                    + MindRaider.outlineCustodian.getActiveNotebookNcName() 
                    + "-" 
                    + Utils.getCurrentDataTimeAsPrettyString() 
                    + ".atom.xml";
            logger.debug(Messages.getString("MindRaiderJFrame.exportingToFile", dstFile));
            
            StatusBar.show("Getting credentials...");
            MindForgerCredentialsDialog mindForgerCredentialsDialog = new MindForgerCredentialsDialog(MindForgerCredentialsDialog.Type.UPLOAD);
            boolean cancelled=mindForgerCredentialsDialog.cancelled;
            mindForgerCredentialsDialog=null;
            if(!cancelled) {
                StatusBar.show("Exporting Outline to Atom...");        
                MindRaider.outlineCustodian.exportOutline(OutlineCustodian.FORMAT_ATOM, dstDirectory+dstFile);
                
                StatusBar.show("Starting worker...");
                MindForgerClient mindForgerClient = new MindForgerClient();
                try {                        
                    mindForgerClient.uploadOutline(
                            dstDirectory,
                            dstFile,
                            MindRaider.mindForgerUsername, 
                            MindRaider.mindForgerPassword, 
                            this);            
                } catch(Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Unable to upload Outline - "+e.getMessage(), "Outline Upload Error",
                            JOptionPane.ERROR_MESSAGE);
                }                
            }                        
        } else {
            StatusBar.show("Please open an Outline to be uploaded.");            
        }
    }

    private void downloadAnOutlineFromMindForger() {
        // fork in order to enable status updates in the main window
        // 1. log in (reuse of credentials between upload and download)
        // 2. get the list of outlines (name + mrID +mfID)
        // 3. User selects outline from the list (search like dialog with filter)
        // 4. User selects and clicks Download
        // 5. Warning 'Local dialog will be overwritten' may be popped  Yes/No
        StatusBar.show("Getting credentials...");
        MindForgerCredentialsDialog mindForgerCredentialsDialog = new MindForgerCredentialsDialog(MindForgerCredentialsDialog.Type.DOWNLOAD);
        boolean cancelled=mindForgerCredentialsDialog.cancelled;
        mindForgerCredentialsDialog=null;
        if(!cancelled) {
            StatusBar.show("Downloading your MindForger Outlines names - starting worker...");
            ResourceDescriptor[] outlineDescriptors;
            MindForgerClient mindForgerClient = new MindForgerClient();
            try {                        
                outlineDescriptors=mindForgerClient.downloadOutlinesList(
                        MindRaider.mindForgerUsername, 
                        MindRaider.mindForgerPassword, 
                        this);            
            } catch(Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Unable to download your Outline list from MindForger - "+e.getMessage(), 
                        "Outline List Download Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if(outlineDescriptors!=null && outlineDescriptors.length>0) {
                // show the list of downloaded outlines
                new MindForgerDownloadOutlineJDialog(outlineDescriptors);                
            } else {
                StatusBar.show("No descriptors found! Please create an Outline in MindForger first.");                
            }
        }
    }
    
    private void exportActiveOutlineToAtom() {
        if (MindRaider.profile.getActiveOutline() == null) {
            JOptionPane
                    .showMessageDialog(
                            MindRaiderMainWindow.this,
                            Messages
                                    .getString("MindRaiderJFrame.exportNotebookWarning"),
                            Messages
                                    .getString("MindRaiderJFrame.exportError"),
                            JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setApproveButtonText(Messages.getString("MindRaiderJFrame.export"));
        fc.setControlButtonsAreShown(true);
        fc.setDialogTitle(Messages.getString("MindRaiderJFrame.chooseExportDirectory"));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // prepare directory
        String exportDirectory = MindRaider.profile.getHomeDirectory()+File.separator+"export"+File.separator+"sharing";
        Utils.createDirectory(exportDirectory);
        fc.setCurrentDirectory(new File(exportDirectory));
        int returnVal = fc.showOpenDialog(MindRaiderMainWindow.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String dstDirectory = fc.getSelectedFile().getAbsolutePath();
            Utils.createDirectory(dstDirectory);
            final String dstFileName = dstDirectory
                    + File.separator
                    + "MindRaider-"
                    + MindRaider.outlineCustodian.getActiveNotebookNcName() 
                    + "-" 
                    + Utils.getCurrentDataTimeAsPrettyString() 
                    + ".atom.xml";
            logger.debug(Messages.getString("MindRaiderJFrame.exportingToFile", dstFileName));

            MindRaider.outlineCustodian.exportOutline(OutlineCustodian.FORMAT_ATOM, dstFileName);
        } else {
            logger
                    .debug(Messages
                            .getString("MindRaiderJFrame.exportCommandCancelledByUser"));
        }
    }

    private void importFromAtom() {
        // choose file to be transformed
        OutlineJPanel.getInstance().clear();
        MindRaider.profile.setActiveOutlineUri(null);
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(MindRaiderMainWindow.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = fc.getSelectedFile();
            MindRaider.profile.deleteActiveModel();
            logger.debug(Messages.getString("MindRaiderJFrame.importingTWikiTopic", file.getAbsolutePath()));
            
            // perform it async
            final SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    ProgressDialogJFrame progressDialogJFrame = new ProgressDialogJFrame("Atom Import","Importing Outline from Atom...");
                    try {
                        MindRaider.outlineCustodian.importNotebook(
                                OutlineCustodian.FORMAT_ATOM,(file!=null?file.getAbsolutePath():null), 
                                progressDialogJFrame);
                    } finally {
                        if (progressDialogJFrame != null) {
                            progressDialogJFrame.dispose();
                        }
                    }
                    return null;
                }
            };
            worker.start();
        } else {
            logger.debug(Messages.getString("MindRaiderJFrame.openCommandCancelledByUser"));
        }
    }

    public void handleMindForgerActiveOutlineUpload() {
        Thread thread = new Thread() {
            public void run() {                
                try {
                    uploadActiveOutlineToMindForger();
                } catch (Exception e) {
                    logger.error("Unable to upload active Outline to MindForger!", e);
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}
