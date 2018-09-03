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
package com.emental.mindraider.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URI;
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
import com.emental.mindraider.ui.frames.MindRaiderMainWindow;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;

public class MindForgerUploadOutlineJDialog extends ProgramIconJDialog {
    private static final Logger logger = Logger.getLogger(MindForgerUploadOutlineJDialog.class);

    private static final int TEXTFIELD_WIDTH = 30;

    /**
     * The resource descriptor array.
     */
    ResourceDescriptor[] resourceDescriptors;

    /**
     * The shown descriptor array.
     */
    ArrayList<ResourceDescriptor> shownDescriptors = new ArrayList<ResourceDescriptor>();

    /**
     * The jlist.
     */
    private JList list;

    /**
     * The default model.
     */
    private DefaultListModel defaultListModel;

    /**
     * Field that is used to return notebook to be opened back to the caller.
     */
    private OutlineToOpen notebookToOpen;

    /**
     * Constructor.
     */
    public MindForgerUploadOutlineJDialog() {
        this("Upload to Online Edition: Select Outline", " Select Outline to be uploaded: ", "Next >", true);
    }

    /**
     * Constructor.
     *
     * @param dialogTitle
     *            The dialog title.
     * @param selectionLabel
     *            The selection label text.
     * @param buttonLabel
     *            The button label text.
     * @param showCancel
     *            The show cancel flag.
     */
    public MindForgerUploadOutlineJDialog(String dialogTitle, String selectionLabel, String buttonLabel, boolean showCancel) {
        this(dialogTitle, selectionLabel, buttonLabel, showCancel,null);
    }

    /**
     * Holder for returning back notebooks to be opened.
     */
    public static class OutlineToOpen {
        public String outlineLabel;
        public String outlineUri;

        public OutlineToOpen() {
        }

        public OutlineToOpen(String notebookLabel) {
            this.outlineLabel = notebookLabel;
        }
    }

    /**
     * Constructor.
     *
     * @param dialogTitle
     * @param selectionLabel
     * @param buttonLabel
     * @param showCancel
     * @param notebookToOpen
     */
    public MindForgerUploadOutlineJDialog(String dialogTitle, String selectionLabel, String buttonLabel, boolean showCancel,
            OutlineToOpen notebookToOpen) {
        super(dialogTitle);

        this.notebookToOpen = notebookToOpen;

        resourceDescriptors = MindRaider.labelCustodian.getNotebookDescriptors();

        JPanel framePanel = new JPanel();
        framePanel.setBorder(new EmptyBorder(5, 10, 0, 10));
        framePanel.setLayout(new BorderLayout());

        // NORTH panel
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(new JLabel(selectionLabel), BorderLayout.NORTH);

        final JTextField notebookLabel = new JTextField(TEXTFIELD_WIDTH);
        if (resourceDescriptors == null) {
            notebookLabel.setEnabled(false);
        }
        notebookLabel.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    logger.debug(Messages.getString("OpenNotebookJDialog.openSelectedNotebook"));
                    openAndUploadOutline(notebookLabel.getText());
                }
            }

            public void keyReleased(KeyEvent keyEvent) {
                getListModel().clear();
                shownDescriptors.clear();
                refreshOutlinesList(notebookLabel);
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        northPanel.add(notebookLabel, BorderLayout.SOUTH);
        framePanel.add(northPanel, BorderLayout.NORTH);

        // CENTER panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(new JLabel(Messages.getString("OpenNotebookJDialog.matchingNotebook")), BorderLayout.NORTH);

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
        JButton openButton = new JButton(buttonLabel);
        southPanel.add(openButton);
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAndUploadOutline("");
            }
        });

        if (showCancel) {
            JButton cancelButton = new JButton(Messages.getString("OpenNotebookJDialog.cancel"));
            southPanel.add(cancelButton);
            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    MindForgerUploadOutlineJDialog.this.dispose();
                }
            });
        }

        framePanel.add(southPanel, BorderLayout.SOUTH);

        getContentPane().add(framePanel, BorderLayout.CENTER);

        refreshOutlinesList(notebookLabel);
        
        // show
        pack();
        Gfx.centerAndShowWindow(this);
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
     */
    protected void openAndUploadOutline(String name) {
        String notebookToShowUri = null;
        String notebookLabel = null;
        int selectedIndex = list.getSelectedIndex();
        logger.debug("Selected index: " + selectedIndex);
        if (selectedIndex >= 0 && selectedIndex < shownDescriptors.size()) {
            ResourceDescriptor resourceDescriptor = shownDescriptors.get(selectedIndex);
            if (resourceDescriptor != null) {
                notebookToShowUri = resourceDescriptor.getUri();
                notebookLabel = resourceDescriptor.getLabel();
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
                            notebookToShowUri = shownDescriptors.get(i).getUri();
                            notebookLabel = shownDescriptors.get(i).getLabel();
                            selected=true;
                        }
                    }
                    
                }
                if(!selected) {
                    notebookToShowUri = shownDescriptors.get(0).getUri();
                    notebookLabel = shownDescriptors.get(0).getLabel();                    
                }
            }
        }

        if (notebookToShowUri != null) {
            logger.debug(Messages.getString("OpenNotebookJDialog.openingNotebook", notebookToShowUri));

            if (notebookToOpen != null) {
                // just set the name and return
                notebookToOpen.outlineLabel = notebookLabel;
                notebookToOpen.outlineUri = notebookToShowUri;
                
            } else {
                logger.debug(Messages.getString("OpenNotebookJDialog.openingNotebook", notebookToShowUri));

                try {
                    MindRaider.outlineCustodian.loadOutline(new URI(notebookToShowUri));
                    OutlineJPanel.getInstance().refresh();
                    OutlineJPanel.getInstance().conceptJPanel.clear();
                    
                    // upload the opened outline
                    MindRaiderMainWindow.getInstance().handleMindForgerActiveOutlineUpload();                    
                } catch (URISyntaxException e1) {
                    logger.debug(Messages.getString("OpenNotebookJDialog.unableToOpenNotebook", e1.getMessage()));
                    JOptionPane.showMessageDialog(MindForgerUploadOutlineJDialog.this, Messages
                            .getString("OpenNotebookJDialog.notebookLoadError"), Messages.getString(
                            "OpenNotebookJDialog.unableToLoadNotebook", e1.getMessage()), JOptionPane.ERROR_MESSAGE);
                }
            }
            MindForgerUploadOutlineJDialog.this.dispose();            
        }
    }

    private void refreshOutlinesList(final JTextField notebookLabel) {
        for (ResourceDescriptor resource : resourceDescriptors) {
            if (resource.getLabel().toLowerCase().startsWith(notebookLabel.getText().toLowerCase())) {
                getListModel().addElement(resource.getLabel());
                shownDescriptors.add(resource);
            }
        }
    }

    private static final long serialVersionUID = -3087165950062164911L;
}