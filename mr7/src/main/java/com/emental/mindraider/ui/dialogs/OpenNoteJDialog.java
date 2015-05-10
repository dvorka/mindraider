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
package com.emental.mindraider.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;

public class OpenNoteJDialog extends ProgramIconJDialog {

    /**
     * Helper holder for concepts to be passed outside.
     */
    public static class NoteToOpen {
        public String conceptLabel;
        public String conceptUri;

        public NoteToOpen() {
        }
    }

    private static final Logger logger = Logger.getLogger(OpenNoteJDialog.class);

    /**
     * The text field width constant.
     */
    private static final int TEXTFIELD_WIDTH = 30;

    /**
     * The resource descriptor array.
     */
    ResourceDescriptor[] resourceDescriptors;

    /**
     * The shown descriptors array.
     */
    ArrayList<ResourceDescriptor> shownDescriptors = new ArrayList<ResourceDescriptor>();

    /**
     * The list.
     */
    private JList list;

    /**
     * The default model.
     */
    private DefaultListModel defaultListModel;

    private NoteToOpen noteToOpen;

    /**
     * Constructor.
     * 
     * @param dialogTitle
     * @param selectionLabel
     * @param buttonLabel
     * @param string 
     * @param showCancel
     * @param noteToOpen
     */
    public OpenNoteJDialog(
            String dialogTitle, 
            String selectionLabel, 
            String buttonLabel, 
            String outlineUri, 
            NoteToOpen noteToOpen) {
        super(Messages.getString(dialogTitle));

        this.noteToOpen = noteToOpen;

        if(outlineUri!=null) {
            resourceDescriptors = MindRaider.outlineCustodian.getAllNoteDescriptors(outlineUri);
        } else {
            resourceDescriptors = MindRaider.outlineCustodian.getAllNoteDescriptors();            
        }
        
        if (resourceDescriptors == null) {
            resourceDescriptors = new ResourceDescriptor[0];
        }
        logger.debug(Messages.getString("OpenNoteJDialog.conceptsToOpen",
                resourceDescriptors.length));

        JPanel framePanel = new JPanel();
        framePanel.setBorder(new EmptyBorder(5, 10, 0, 10));
        framePanel.setLayout(new BorderLayout());

        // NORTH panel
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(new JLabel(Messages.getString(selectionLabel)),
                BorderLayout.NORTH);

        final JTextField conceptLabel = new JTextField(TEXTFIELD_WIDTH);
        if (resourceDescriptors == null) {
            conceptLabel.setEnabled(false);
        }
        conceptLabel.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    logger.debug(Messages.getString("OpenNoteJDialog.openSelectedConcept"));
                    try {
                        openConcept(conceptLabel.getText());
                    } catch (Exception e1) {
                        logger.debug(Messages.getString("OpenNoteJDialog.matchingConcepts", e1.getMessage()));
                    }
                }
            }

            public void keyReleased(KeyEvent keyEvent) {
                refreshNotesList(conceptLabel);
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        northPanel.add(conceptLabel, BorderLayout.SOUTH);
        framePanel.add(northPanel, BorderLayout.NORTH);

        // CENTER panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(new JLabel(Messages
                .getString("OpenNoteJDialog.matchingConcepts")),
                BorderLayout.NORTH);

        defaultListModel = new DefaultListModel();
        list = new JList(defaultListModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.setSelectionForeground(Color.white);
        list.setSelectionBackground(Color.black);
        // list.addListSelectionListener(this);
        list.setVisibleRowCount(15);
        JScrollPane listScrollPane = new JScrollPane(list);
        centerPanel.add(listScrollPane, BorderLayout.SOUTH);
        framePanel.add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton openButton = new JButton(Messages.getString(buttonLabel));
        southPanel.add(openButton);
        openButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    openConcept("");
                } catch (Exception e1) {
                    logger.debug(Messages.getString(
                            "OpenNoteJDialog.unableToOpenConcept", e1
                                    .getMessage()));
                }
            }
        });

        JButton cancelButton = new JButton(Messages
                .getString("OpenNoteJDialog.cancel"));
        southPanel.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                OpenNoteJDialog.this.dispose();
            }
        });
        framePanel.add(southPanel, BorderLayout.SOUTH);

        getContentPane().add(framePanel, BorderLayout.CENTER);

        refreshNotesList(conceptLabel);                
        
        // show
        pack();
        Gfx.centerAndShowWindow(this);
    }

    /**
     * Constructor.
     */
    public OpenNoteJDialog() {
        this("OpenNoteJDialog.title",
             "OpenNoteJDialog.selectConceptToOpen",
             "OpenNoteJDialog.open", 
             null, 
             null);
    }
    
    /**
     * List model helper.
     * 
     * @return list model.
     */
    protected DefaultListModel getListModel() {
        return defaultListModel;
    }

    /**
     * Open selected notebook.
     * @param string 
     * 
     * @throws Exception
     *             A generic exception
     */
    protected void openConcept(String name) throws Exception {
        String conceptToShow = null;
        String conceptLabel = null;
        int selectedIndex = list.getSelectedIndex();
        logger.debug("Selected index: " + selectedIndex);
        if (selectedIndex >= 0 && selectedIndex < shownDescriptors.size()) {
            ResourceDescriptor resourceDescriptor = shownDescriptors.get(selectedIndex);
            if (resourceDescriptor != null) {
                conceptToShow = resourceDescriptor.getUri();
                conceptLabel = resourceDescriptor.getLabel();
            }
        } else {
            if (shownDescriptors.size() > 0) {
                // try to find exact match
                boolean selected=false;
                if(shownDescriptors.size()>1) {
                    for (int i = 0; i < shownDescriptors.size(); i++) {
                        if(name==null) {
                            name="";
                        }
                        if(shownDescriptors.get(i).getLabel().toLowerCase().equals(name.toLowerCase())) {
                            conceptToShow = shownDescriptors.get(i).getUri();
                            conceptLabel = shownDescriptors.get(i).getLabel();
                            selected=true;
                        }
                    }
                    
                }
                if(!selected) {
                    conceptToShow = shownDescriptors.get(0).getUri();
                    conceptLabel = shownDescriptors.get(0).getLabel();
                }
            }
        }

        if (conceptToShow != null) {
            logger.debug(Messages.getString(
                    "OpenNoteJDialog.openingConcept", conceptToShow));

            // recent tree
            MindRaider.recentConcepts.addRecentConcept(
                    conceptLabel,
                    conceptToShow,
                    MindRaider.outlineCustodian.getActiveNotebookLabel(),
                    MindRaider.outlineCustodian.getActiveOutlineResource().getUri());

            // open
            if (noteToOpen != null) {
                noteToOpen.conceptLabel = conceptLabel;
                noteToOpen.conceptUri = conceptToShow;
            } else {
                try {
                    OutlineJPanel.getInstance().conceptJPanel
                            .open(MindRaider.noteCustodian.get(
                                    MindRaider.profile.getActiveOutlineUri()
                                            .toString(), conceptToShow));
                    OutlineJPanel.getInstance()
                            .setSelectedTreeNodeConcept(conceptToShow);
                } catch (URISyntaxException e1) {
                    logger.debug(Messages.getString(
                            "OpenNoteJDialog.unableToLoadConcept", e1
                                    .getMessage()));
                    JOptionPane
                            .showMessageDialog(
                                    OpenNoteJDialog.this,
                                    Messages
                                            .getString("OpenNoteJDialog.loadConceptError"),
                                    Messages
                                            .getString(
                                                    "OpenNoteJDialog.unableToLoadConcept",
                                                    e1.getMessage()),
                                    JOptionPane.ERROR_MESSAGE);
                }
            }

            OpenNoteJDialog.this.dispose();
        }
    }
    
    private void refreshNotesList(final JTextField conceptLabel) {
        getListModel().clear();
        shownDescriptors.clear();
        for (ResourceDescriptor resource : resourceDescriptors) {
            if (resource.getLabel().toLowerCase().startsWith(conceptLabel.getText().toLowerCase())) {
                getListModel().addElement(resource.getLabel());
                shownDescriptors.add(resource);
            }
        }
    }

    private static final long serialVersionUID = 1L;
}
