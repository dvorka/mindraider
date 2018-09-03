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
import com.mindcognition.mindraider.integration.mindforger.MindForgerClient;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;
import com.mindcognition.mindraider.utils.SwingWorker;

public class MindForgerDownloadOutlineJDialog extends ProgramIconJDialog {
    private static final Logger logger = Logger.getLogger(MindForgerDownloadOutlineJDialog.class);

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
    public MindForgerDownloadOutlineJDialog(ResourceDescriptor[] resourceDescriptors) {
        this(resourceDescriptors, "Download from Online Edition: Select Outline", " Select Outline to be downloaded from the online edition: ", "Download", true);
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
    public MindForgerDownloadOutlineJDialog(
            ResourceDescriptor[] resourceDescriptors,
            String dialogTitle, 
            String selectionLabel, 
            String buttonLabel, 
            boolean showCancel) {
        this(resourceDescriptors, dialogTitle, selectionLabel, buttonLabel, showCancel, null);
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
    public MindForgerDownloadOutlineJDialog(
            ResourceDescriptor[] resourceDescriptors,
            String dialogTitle, 
            String selectionLabel, 
            String buttonLabel, 
            boolean showCancel,
            OutlineToOpen notebookToOpen) {
        super(dialogTitle);

        this.notebookToOpen = notebookToOpen;
        this.resourceDescriptors = resourceDescriptors;

        JPanel framePanel = new JPanel();
        framePanel.setBorder(new EmptyBorder(5, 10, 0, 10));
        framePanel.setLayout(new BorderLayout());

        // NORTH panel
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(new JLabel(selectionLabel), BorderLayout.NORTH);

        final JTextField notebookLabel = new JTextField(TEXTFIELD_WIDTH);
        if (this.resourceDescriptors == null) {
            notebookLabel.setEnabled(false);
        }
        notebookLabel.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    logger.debug(Messages.getString("OpenNotebookJDialog.openSelectedNotebook"));
                    downloadOutline(notebookLabel.getText());
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
                downloadOutline("");
            }
        });

        if (showCancel) {
            JButton cancelButton = new JButton(Messages.getString("OpenNotebookJDialog.cancel"));
            southPanel.add(cancelButton);
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    MindForgerDownloadOutlineJDialog.this.dispose();
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

    protected DefaultListModel getListModel() {
        return defaultListModel;
    }

    protected void downloadOutline(String name) {
        String notebookName = null;
        String mindForgerKey = null;
        int selectedIndex = list.getSelectedIndex();
        logger.debug("Selected index: " + selectedIndex);
        if (selectedIndex >= 0 && selectedIndex < shownDescriptors.size()) {
            ResourceDescriptor resourceDescriptor = shownDescriptors.get(selectedIndex);
            if (resourceDescriptor != null) {
                mindForgerKey = resourceDescriptor.getUri();
                notebookName = resourceDescriptor.getLabel();
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
                            mindForgerKey = shownDescriptors.get(i).getUri();
                            notebookName = shownDescriptors.get(i).getLabel();
                            selected=true;
                        }
                    }
                    
                }
                if(!selected) {
                    mindForgerKey = shownDescriptors.get(0).getUri();
                    notebookName = shownDescriptors.get(0).getLabel();                    
                }
            }
        }
        
        final String notebookMindForgerKey = mindForgerKey;

        if (notebookMindForgerKey != null) {
            logger.debug(Messages.getString("OpenNotebookJDialog.openingNotebook", notebookMindForgerKey));

            if (notebookToOpen != null) {
                // just set the name and return
                notebookToOpen.outlineLabel = notebookName;
                notebookToOpen.outlineUri = notebookMindForgerKey;
            } else {
                logger.debug(Messages.getString("OpenNotebookJDialog.openingNotebook", notebookMindForgerKey));

                final SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        ProgressDialogJFrame progressDialogJFrame = new ProgressDialogJFrame(
                                "MindForger Import", "<html>&nbsp;&nbsp;<b>"
                                        + "Processing entry "
                                        + "</b>&nbsp;&nbsp;</html>");
                        try {
                            MindForgerClient mindForgerClient = new MindForgerClient();
                            String atom=mindForgerClient.downloadOutline(
                                    notebookMindForgerKey,
                                    MindRaider.mindForgerUsername, 
                                    MindRaider.mindForgerPassword, 
                                    progressDialogJFrame);      

                            if(atom==null) {
                                JOptionPane.showMessageDialog( // TODO
                                        progressDialogJFrame,
                                        "Unable to download the selected Outline!", 
                                        "Outline Download Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } finally {
                            if (progressDialogJFrame != null) {
                                progressDialogJFrame.dispose();
                            }
                        }   
                                                
                        return null;
                    }};
                    worker.start();
            }
            MindForgerDownloadOutlineJDialog.this.dispose();            
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