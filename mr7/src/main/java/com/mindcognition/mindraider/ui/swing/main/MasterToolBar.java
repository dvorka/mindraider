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
package com.mindcognition.mindraider.ui.swing.main;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.core.search.SearchCommander;
import com.emental.mindraider.ui.dialogs.OpenConceptByTagJDialog;
import com.emental.mindraider.ui.dialogs.OpenOutlineJDialog;
import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.application.model.outline.OutlineCustodian;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.utils.Utils;

public class MasterToolBar extends JToolBar {
    
    static final String SEARCH_LABEL = "label";
    static final String SEARCH_CONCEPT = "note";
    static final String SEARCH_FOR_CONCEPT = "for note";
    static final String SEARCH_FOR_NOTEBOOK = "for outline";
    JButton ftsButton;
    JComboBox searchTextCombo;
    JRadioButton conceptRadio;
    
    // TODO pulldown of location textfield to return to history: textfield 2 combo change - SeachConceptAnnotation class
    /**
     * history of searches - transient, kept just for this MR run
     */
    private static ArrayList<String> searchHistory;
    static {
        searchHistory=new ArrayList<String>();
        searchHistory.add("");
    }
    
    private static final Logger logger = Logger.getLogger(MasterToolBar.class);
    JButton jbutton;
    JButton left, homeNotebook, right, home, history, search, content, channels, bookmarks, trash, graph, socialMap;
    JTextField location;

    public MasterToolBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        left = new JButton(IconsRegistry.getImageIcon("browserBack.png"));
        left.setToolTipText("Go back one Outline");
        left.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    if (MindRaider.history.canMoveBack()) {
                        MindRaider.outlineCustodian.loadOutline(new URI(MindRaider.history.back()));
                        OutlineJPanel.getInstance().refresh();
                    }
                } catch (URISyntaxException e1) {
                    //
                }

            }
        });
        add(left);

        homeNotebook = new JButton(IconsRegistry.getImageIcon("browserHome.png"));
        homeNotebook.setToolTipText("Your Home Outline");
        homeNotebook.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String homeNotebook = MindRaider.profile.getHomeNotebook();
                if (homeNotebook != null) {
                    try {
                        MindRaider.outlineCustodian.loadOutline(new URI(homeNotebook));
                        OutlineJPanel.getInstance().refresh();
                    } catch (Exception e1) {
                        logger.error("actionPerformed() - Error: unable to load notebook!", e1);
                        logger.error("actionPerformed(ActionEvent)", e1);
                    }
                }
            }
        });
        add(homeNotebook);

        right = new JButton(IconsRegistry.getImageIcon("browserForward.png"));
        right.setToolTipText("Go forward one Outline");
        right.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    if (MindRaider.history.canMoveForward()) {
                        MindRaider.outlineCustodian.loadOutline(new URI(MindRaider.history.forward()));
                        OutlineJPanel.getInstance().refresh();
                    }
                } catch (URISyntaxException e1) {
                    //
                }
            }
        });
        add(right);

        location = new JTextField("", 55);
        final JTextField searchTextField = new JTextField("", 55);
        searchTextField.setEditable(true);
        searchTextField.setToolTipText("Ctrl-a to search again");
        searchTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField cb = (JTextField) e.getSource();
                String searchString=cb.getText();
                SearchCommander.searchNotebooks(searchString);
            }
        });
        searchTextField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                JTextField cb = (JTextField) e.getSource();
                cb.setSelectionStart(0);
                cb.setSelectionEnd(cb.getDocument().getLength());
            }
            public void focusLost(FocusEvent e) {
            }
        });
                
        JPanel p = new JPanel();
        p.setBorder(new EtchedBorder());
        p.add(new JLabel(" "+Messages.getString("MasterToolBar.search")+": "));
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        p.add(searchTextField);

        // search type
        ftsButton = new JButton(IconsRegistry.getImageIcon("searchLabels.png"));
        ftsButton.setMargin(new Insets(3, 3, 3, 3));
        ftsButton.setToolTipText("Fulltext search");
        ftsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SearchCommander.searchNotebooks(searchTextField.getText());
            }
        });
        p.add(ftsButton);        
        add(p);
        
        
        search = new JButton(IconsRegistry.getImageIcon("searchNotebookByName.png"));
        search.setToolTipText("Search Outline by name");
        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new OpenOutlineJDialog();
            }
        });
        add(search);
        
        search = new JButton(IconsRegistry.getImageIcon("searchConceptByTag.png"));
        search.setToolTipText("Search Note by tag");
        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new OpenConceptByTagJDialog();
            }
        });
        add(search);
        
        //addSeparator();
        
        search = new JButton(IconsRegistry.getImageIcon("toggleLeft.png"));
        search.setToolTipText("Show/hide explorer panel");
        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (MindRaider.mainJFrame.leftSidebarSplitPane.getDividerLocation() == 1) {
                    MindRaider.mainJFrame.leftSidebarSplitPane
                            .setDividerLocation(MindRaider.mainJFrame.leftSidebarSplitPane.getLastDividerLocation());
                } else {
                    MindRaider.mainJFrame.closeLeftSidebar();
                }
            }
        });
        add(search);

        search = new JButton(IconsRegistry.getImageIcon("toggleDown.png"));
        search.setToolTipText("Show/hide Outline panel");
        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OutlineJPanel.getInstance().toggleConceptTree();
            }
        });
        add(search);

        content = new JButton(IconsRegistry.getImageIcon("toggleUp.png"));
        content.setToolTipText("Show/hide navigator");
        content.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                OutlineJPanel.getInstance().toggleRdfNavigator();
            }
        });
        add(content);

        content = new JButton(IconsRegistry.getImageIcon("toggleRight.png"));
        content.setToolTipText("Show/hide Note panel");
        content.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                OutlineJPanel.getInstance().toggleRightSidebar();
            }
        });
        add(content);

        addSeparator();

        channels = new JButton(IconsRegistry.getImageIcon("browserHelp.png"));
        channels.setToolTipText("Open MR help Outline");
        channels.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    MindRaider.outlineCustodian.loadOutline(new URI(MindRaiderVocabulary
                            .getNotebookUri(OutlineCustodian.MR_DOC_NOTEBOOK_DOCUMENTATION_LOCAL_NAME)));
                    OutlineJPanel.getInstance().refresh();
                } catch (Exception e1) {
                    logger.debug("Unable to load help Outline!", e1);
                }
            }
        });
        add(channels);
    }

    public void setModelLocation(String modelLocation) {
        if (modelLocation != null && !modelLocation.startsWith("http")) {
            location.setText(Utils.normalizePath(modelLocation));
        } else {
            location.setText(modelLocation);
        }
    }

    public void refreshHistory() {
        left.setEnabled(MindRaider.history.canMoveBack());
        right.setEnabled(MindRaider.history.canMoveForward());
    }

    public void toggleVisibility() {
        if(isVisible()) {
            setVisible(false);
        } else {
            setVisible(true);
        }
    }
    
    private static final long serialVersionUID = 2714865646430313903L;
}