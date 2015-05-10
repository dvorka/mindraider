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
package com.emental.mindraider.ui.graph.spiders;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.ui.dialogs.AddLinkToConceptJDialog;
import com.emental.mindraider.ui.dialogs.AddTripletJDialog;
import com.emental.mindraider.ui.dialogs.NewRdfModelJDialog;
import com.emental.mindraider.ui.dialogs.SearchSpidersJDialog;
import com.emental.mindraider.ui.menus.UriJMenuItem;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;
import com.mindcognition.mindraider.utils.Launcher;
import com.touchgraph.graphlayout.TGException;
import com.touchgraph.graphlayout.TGPanel;
import com.touchgraph.graphlayout.interaction.DragMultiselectUI;
import com.touchgraph.graphlayout.interaction.GLEditUI;
import com.touchgraph.graphlayout.interaction.GLNavigateUI;
import com.touchgraph.graphlayout.interaction.GLPanel;

public class GraphPopUpBuilder {
    private static final Logger logger = Logger.getLogger(GraphPopUpBuilder.class);

    public static JPopupMenu buildNavigationNodePopup(final TGPanel tgPanel, final GLNavigateUI glNavigateUI) {
        JPopupMenu nodePopup = new JPopupMenu();
        JMenuItem menuItem;
        ActionListener action;

        menuItem = new JMenuItem("Open in browser");
        action = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glNavigateUI.getPopupNode() != null) {
                    // must be switched browser/start - do know why
                    Launcher.launchViaStart(MindRaider.spidersGraph.getSelectedNodeLabel());
                }
            }
        };
        menuItem.addActionListener(action);
        nodePopup.add(menuItem);

        menuItem = new JMenuItem("Launch via 'start'");
        action = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glNavigateUI.getPopupNode() != null) {
                    // must be switched browser/start - do know why
                    Launcher.launchInBrowser(MindRaider.spidersGraph.getSelectedNodeLabel());
                }
            }
        };
        menuItem.addActionListener(action);
        nodePopup.add(menuItem);

        menuItem = new JMenuItem("Search via Google");
        action = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glNavigateUI.getPopupNode() != null) {
                    Launcher.launchViaGoogle(MindRaider.spidersGraph.getSelectedNodeLabel());
                }
            }
        };
        menuItem.addActionListener(action);
        nodePopup.add(menuItem);

        // menuItem = new JMenuItem("Open as Channel");
        // action = new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // if(glNavigateUI.popupNode!=null) {
        // Launcher.launchAsChannel();
        // }
        // }
        // };
        // menuItem.addActionListener(action);
        // nodePopup.add(menuItem);

        nodePopup.addSeparator();
        
        menuItem = new JMenuItem("Add concept link...");
        action = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glNavigateUI.getPopupNode() != null) {
                    if (MindRaider.spidersGraph.getSelectedNode() == null) {
                        JOptionPane.showMessageDialog(MindRaider.mainJFrame, "Subject node must be selected!",
                                "Concepts linking Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        new AddLinkToConceptJDialog();
                    }
                }
            }
        };
        menuItem.addActionListener(action);
        nodePopup.add(menuItem);

        menuItem = new JMenuItem("Add triplet...");
        action = new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                if (glNavigateUI.getPopupNode() != null) {
                    if (MindRaider.spidersGraph.getSelectedNode() == null) {
                        JOptionPane.showMessageDialog(MindRaider.mainJFrame, "Subject node must be selected!",
                                "Triplet Creation Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        new AddTripletJDialog();
                    }
                }
            }
        };
        menuItem.addActionListener(action);
        nodePopup.add(menuItem);

        JMenu navigateMenu;
        nodePopup.addSeparator();

        navigateMenu = new JMenu("Node");

        menuItem = new JMenuItem("Expand");
        action = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glNavigateUI.getPopupNode() != null) {
                    tgPanel.expandNode(glNavigateUI.getPopupNode());
                }
            }
        };

        menuItem.addActionListener(action);
        navigateMenu.add(menuItem);

        menuItem = new JMenuItem("Collapse");
        ActionListener collapseAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glNavigateUI.getPopupNode() != null) {
                    tgPanel.collapseNode(glNavigateUI.getPopupNode());
                }
            }
        };

        menuItem.addActionListener(collapseAction);
        navigateMenu.add(menuItem);

        menuItem = new JMenuItem("Hide");
        ActionListener hideAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glNavigateUI.getPopupNode() != null) {
                    tgPanel.hideNode(glNavigateUI.getPopupNode());
                }
            }
        };

        menuItem.addActionListener(hideAction);
        navigateMenu.add(menuItem);

        nodePopup.add(navigateMenu);
        nodePopup.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuCanceled(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                tgPanel.setMaintainMouseOver(false);
                tgPanel.setMouseOverN(null);
                tgPanel.repaint();
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
        });

        return nodePopup;
    }

    /**
     * Build the navigation edge popup
     *
     * @param tgPanel
     *            the TGPanel
     * @param glNavigateUI
     *            the GLNavigateUI
     * @return the JPopupMenu
     */
    public static JPopupMenu buildNavigationEdgePopup(final TGPanel tgPanel, final GLNavigateUI glNavigateUI) {
        JPopupMenu edgePopup = new JPopupMenu();
        JMenuItem menuItem;

        menuItem = new JMenuItem("Hide Edge");
        ActionListener hideAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glNavigateUI.getPopupEdge() != null) {
                    tgPanel.hideEdge(glNavigateUI.getPopupEdge());
                }
            }
        };

        menuItem.addActionListener(hideAction);
        edgePopup.add(menuItem);

        edgePopup.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuCanceled(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                tgPanel.setMaintainMouseOver(false);
                tgPanel.setMouseOverE(null);
                tgPanel.repaint();
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
        });

        return edgePopup;
    }

    /**
     * Build navigation popup.
     *
     * @param horizontalSB
     *            the horizontal scrollbar
     * @param verticalSB
     *            the vertical scrollbar
     * @param topPanel
     *            the top panel
     * @return the JPopupMenu
     */
    public static JPopupMenu buildNavigationPopup(final JScrollBar horizontalSB, final JScrollBar verticalSB,
            final JPanel topPanel) {
        JPopupMenu glPopup = new JPopupMenu();

        JMenu menu;
        JMenuItem menuItem;
        ActionListener action;

        menuItem = new JMenuItem("Search...");
        action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SearchSpidersJDialog();
            }
        };
        menuItem.addActionListener(action);
        glPopup.add(menuItem);

        menu = new JMenu("Label as...");
        menuItem = new JMenuItem("URI");
        action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MindRaider.spidersGraph.showUriLables(true);
            }
        };
        menuItem.addActionListener(action);
        menu.add(menuItem);
        menuItem = new JMenuItem("Local Name");
        action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MindRaider.spidersGraph.showUriLables(false);
            }
        };
        menuItem.addActionListener(action);
        menu.add(menuItem);
        glPopup.add(menu);

        menu = new JMenu("Predicates...");
        menuItem = new JMenuItem("Show");
        action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MindRaider.spidersGraph.hidePredicates(false);
                MindRaider.spidersGraph.renderModel();
            }
        };
        menuItem.addActionListener(action);
        menu.add(menuItem);
        menuItem = new JMenuItem("Hide");
        action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MindRaider.spidersGraph.hidePredicates(true);
                MindRaider.spidersGraph.renderModel();
            }
        };
        menuItem.addActionListener(action);
        menu.add(menuItem);
        glPopup.add(menu);

        menuItem = new JMenuItem("Redraw");
        action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MindRaider.spidersGraph.renderModel();
            }
        };
        menuItem.addActionListener(action);
        glPopup.add(menuItem);

        menu = new JMenu("Color scheme...");
        String[] allProfilesUris = MindRaider.spidersColorProfileRegistry.getAllProfilesUris();
        for (int i = 0; i < allProfilesUris.length; i++) {
            menuItem = new UriJMenuItem(
                    MindRaider.spidersColorProfileRegistry.getColorProfileByUri(allProfilesUris[i]).getLabel(),
                    allProfilesUris[i]);
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(e.getSource() instanceof UriJMenuItem) {
                        MindRaider.spidersColorProfileRegistry.setCurrentProfile(
                                ((UriJMenuItem)e.getSource()).uri);
                        MindRaider.spidersGraph
                            .setRenderingProfile(MindRaider.spidersColorProfileRegistry.getCurrentProfile());
                        MindRaider.spidersGraph.renderModel();
                    }
                }
            });
            menu.add(menuItem);
        }
        glPopup.add(menu);

        glPopup.addSeparator();

        menuItem = new JMenuItem("New resource...");
        action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new NewRdfModelJDialog();
            }
        };
        menuItem.addActionListener(action);
        glPopup.add(menuItem);

        glPopup.addSeparator();

        menuItem = new JMenuItem("Toggle Controls");
        action = new ActionListener() {

            boolean controlsVisible = true;

            public void actionPerformed(ActionEvent e) {
                controlsVisible = !controlsVisible;
                horizontalSB.setVisible(controlsVisible);
                verticalSB.setVisible(controlsVisible);
                topPanel.setVisible(controlsVisible);
            }
        };
        menuItem.addActionListener(action);
        glPopup.add(menuItem);
        /*
         * menuItem = new JMenuItem("Edit Mode"); action = new ActionListener() {
         * public void actionPerformed(ActionEvent e) {
         * if(GLPanel.editModeCheckBox.isSelected()) {
         * GLPanel.editModeCheckBox.setSelected(false);
         * MindRider.spidersGraph.getPanel().tgUIManager.activate("Navigate"); }
         * else { GLPanel.editModeCheckBox.setSelected(true);
         * MindRider.spidersGraph.getPanel().tgUIManager.activate("Edit"); } } };
         * menuItem.addActionListener(action); glPopup.add(menuItem);
         */
        return glPopup;
    }

    /**
     * Build the editation node popup.
     *
     * @param tgPanel
     *            the TGPanel
     * @param glEditUI
     *            the GLEditUI
     * @return the JPopupMenu
     */
    public static JPopupMenu buildEditationNodePopup(final TGPanel tgPanel, final GLEditUI glEditUI) {
        JPopupMenu nodePopup = new JPopupMenu();
        JMenuItem menuItem;

        JMenu navigateMenu = new JMenu("Attachment");

        menuItem = new JMenuItem("Delete Statement");
        ActionListener deleteNodeAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glEditUI.popupNode != null) {
                    // @todo
                    // SpiderModel.deleteStatement(SpiderModel.tgPanel.getSelect());
                }
            }
        };
        menuItem.addActionListener(deleteNodeAction);
        nodePopup.add(menuItem);

        menuItem = new JMenuItem("Add...");
        ActionListener expandAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glEditUI.popupNode != null) {
                    JFileChooser fc = new JFileChooser();
                    fc.setApproveButtonText("Attach");
                    fc.setControlButtonsAreShown(true);
                    fc.setDialogTitle("Choose Attachment");
                    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    int returnVal = fc.showOpenDialog(tgPanel);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        if (logger.isDebugEnabled()) {
                            logger.debug("actionPerformed(ActionEvent)");
                        }
                        /*
                         * Node subjectNode=SpiderModel.tgPanel.getSelect();
                         * Node
                         * predicateNode=SpiderModel.newPredicateNode(EMENTALITY_RDF_PREDICATE_NS,"attachment","attachment");
                         * Node
                         * objectNode=SpiderModel.newObjectNode(true,null,file.getAbsolutePath(),file.getAbsolutePath());
                         * try { SpiderModel.tgPanel.addNode(predicateNode);
                         * SpiderModel.tgPanel.addNode(objectNode);
                         * SpiderModel.tgPanel.addEdge(subjectNode,predicateNode,Edge.DEFAULT_LENGTH);
                         * SpiderModel.tgPanel.addEdge(predicateNode,objectNode,Edge.DEFAULT_LENGTH); }
                         * catch (TGException e1) { logger.error("error " +
                         * el.getMessage()); }
                         * SpiderModel.addStatement(subjectNode, predicateNode,
                         * objectNode); SpiderModel.save();
                         * SpiderModel.tgPanel.repaint();
                         */
                        StatusBar.show("Attachment " + file.getAbsolutePath());
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("actionPerformed(ActionEvent)");
                        }
                    }
                }
            }
        };
        menuItem.addActionListener(expandAction);
        navigateMenu.add(menuItem);

        menuItem = new JMenuItem("Annotation");
        expandAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glEditUI.popupNode != null) {
                    String annotationFile = MindRaider.profile.getNotebooksDirectory() + File.separator
                            + MindRaider.profile.getActiveOutline() + File.separator + "annotations" + File.separator
                            + MindRaider.spidersGraph.getSelectedNode().getLabel() + "_" + System.currentTimeMillis()
                            + ".txt";

                    StatusBar.show("Creating annotation " + annotationFile + " ...");
                    /*
                     * Node subjectNode=SpiderModel.tgPanel.getSelect(); Node
                     * predicateNode=SpiderModel.newPredicateNode(EMENTALITY_RDF_PREDICATE_NS,"annotation","attachment");
                     * Node
                     * objectNode=SpiderModel.newObjectNode(true,null,annotationFile,annotationFile);
                     * try { SpiderModel.tgPanel.addNode(predicateNode);
                     * SpiderModel.tgPanel.addNode(objectNode);
                     * SpiderModel.tgPanel.addEdge(subjectNode,predicateNode,Edge.DEFAULT_LENGTH);
                     * SpiderModel.tgPanel.addEdge(predicateNode,objectNode,Edge.DEFAULT_LENGTH); }
                     * catch (TGException e1) { log.error("error " +
                     * el.getMessage()); } SpiderModel.addStatement(subjectNode,
                     * predicateNode, objectNode); SpiderModel.save();
                     * SpiderModel.tgPanel.repaint();
                     * SpiderModel.tgPanel.setSelect(objectNode);
                     * Tools.renewFile(annotationFile);
                     * Launcher.launchInExplorer();
                     */
                }
            }
        };
        menuItem.addActionListener(expandAction);
        navigateMenu.add(menuItem);
        /*
         * menuItem = new JMenuItem("Launch"); expandAction = new
         * ActionListener() { public void actionPerformed(ActionEvent e) {
         * if(glEditUI.popupNode!=null) { Launcher.launchInExplorer(); } } };
         * menuItem.addActionListener(expandAction); navigateMenu.add(menuItem);
         */
        nodePopup.add(navigateMenu);

        navigateMenu = new JMenu("Node");

        menuItem = new JMenuItem("Expand");
        expandAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glEditUI.popupNode != null) {
                    tgPanel.expandNode(glEditUI.popupNode);
                }
            }
        };

        menuItem.addActionListener(expandAction);
        navigateMenu.add(menuItem);

        menuItem = new JMenuItem("Collapse");
        ActionListener collapseAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glEditUI.popupNode != null) {
                    tgPanel.collapseNode(glEditUI.popupNode);
                }
            }
        };
        menuItem.addActionListener(collapseAction);
        navigateMenu.add(menuItem);

        menuItem = new JMenuItem("Hide");
        ActionListener hideAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // Node select = tgPanel.getSelect();
                if (glEditUI.popupNode != null) {
                    tgPanel.hideNode(glEditUI.popupNode);
                }
            }
        };
        menuItem.addActionListener(hideAction);
        navigateMenu.add(menuItem);

        menuItem = new JMenuItem("Delete");
        deleteNodeAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glEditUI.popupNode != null) {
                    tgPanel.deleteNode(glEditUI.popupNode);
                }
            }
        };
        menuItem.addActionListener(deleteNodeAction);
        navigateMenu.add(menuItem);

        menuItem = new JMenuItem("Dump");
        deleteNodeAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glEditUI.popupNode != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("actionPerformed(ActionEvent)");
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("actionPerformed(ActionEvent)");
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("actionPerformed(ActionEvent)");
                    }
                }
            }
        };
        menuItem.addActionListener(deleteNodeAction);
        navigateMenu.add(menuItem);

        nodePopup.add(navigateMenu);

        nodePopup.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuCanceled(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                tgPanel.setMaintainMouseOver(false);
                tgPanel.setMouseOverN(null);
                tgPanel.repaint();
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
        });

        return nodePopup;
    }

    /**
     * Build the editation edge popup.
     *
     * @param tgPanel
     *            the TGPanel
     * @param glEditUI
     *            the GLEditUI
     * @return the JPopupMenu
     */
    public static JPopupMenu buildEditationEdgePopup(final TGPanel tgPanel, final GLEditUI glEditUI) {
        JPopupMenu edgePopup = new JPopupMenu();
        JMenuItem menuItem;

        menuItem = new JMenuItem("Relax Edge");
        ActionListener relaxEdgeAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glEditUI.popupEdge != null) {
                    glEditUI.popupEdge.setLength(glEditUI.popupEdge.getLength() * 4);
                    tgPanel.resetDamper();
                }
            }
        };
        menuItem.addActionListener(relaxEdgeAction);
        edgePopup.add(menuItem);

        menuItem = new JMenuItem("Tighten Edge");
        ActionListener tightenEdgeAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glEditUI.popupEdge != null) {
                    glEditUI.popupEdge.setLength(glEditUI.popupEdge.getLength() / 4);
                    tgPanel.resetDamper();
                }
            }
        };
        menuItem.addActionListener(tightenEdgeAction);
        edgePopup.add(menuItem);

        menuItem = new JMenuItem("Delete Edge");
        ActionListener deleteEdgeAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (glEditUI.popupEdge != null) {
                    tgPanel.deleteEdge(glEditUI.popupEdge);
                }
            }
        };
        menuItem.addActionListener(deleteEdgeAction);
        edgePopup.add(menuItem);

        edgePopup.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuCanceled(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                tgPanel.setMaintainMouseOver(false);
                tgPanel.setMouseOverE(null);
                tgPanel.repaint();
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
        });

        return edgePopup;
    }

    /**
     * Build the editation popup.
     *
     * @param tgPanel
     *            the TGPanel
     * @param glEditUI
     *            the GLEditUI
     * @param dragMultiselectUI
     *            the dragMultiselectUI
     * @return the JPopupMenu
     */
    public static JPopupMenu buildEditationPopup(final TGPanel tgPanel, final GLEditUI glEditUI,
            final DragMultiselectUI dragMultiselectUI) {
        JPopupMenu backPopup = new JPopupMenu();
        JMenuItem menuItem;

        menuItem = new JMenuItem("Navigation Mode");
        ActionListener toggleControlsAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (GLPanel.editModeCheckBox.isSelected()) {
                    GLPanel.editModeCheckBox.setSelected(false);
                    MindRaider.spidersGraph.getPanel().tgUIManager.activate("Navigate");
                } else {
                    GLPanel.editModeCheckBox.setSelected(true);
                    MindRaider.spidersGraph.getPanel().tgUIManager.activate("Edit");
                }
            }
        };
        menuItem.addActionListener(toggleControlsAction);
        backPopup.add(menuItem);

        backPopup.addSeparator();

        menuItem = new JMenuItem("New Graph");
        ActionListener startOverAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                tgPanel.clearAll();
                tgPanel.clearSelect();
                try {
                    tgPanel.addNode();
                } catch (TGException tge) {
                    logger.error("actionPerformed(ActionEvent) - " + tge.getMessage(), tge);
                    tge.printStackTrace(System.err);
                }
                tgPanel.fireResetEvent();
                tgPanel.repaint();
            }
        };
        menuItem.addActionListener(startOverAction);
        backPopup.add(menuItem);

        menuItem = new JMenuItem("Multi-select");
        ActionListener multiselectAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dragMultiselectUI.activate(glEditUI);
            }
        };
        menuItem.addActionListener(multiselectAction);
        backPopup.add(menuItem);

        return backPopup;
    }
}
