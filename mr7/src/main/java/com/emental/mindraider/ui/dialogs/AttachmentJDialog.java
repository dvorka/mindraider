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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.resource.AttachmentResource;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.emental.mindraider.ui.dnd.DragAndDropReference;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;

/**
 * The Attach file/link to resource dialog.
 */
public class AttachmentJDialog extends ProgramIconJDialog implements
        ActionListener {

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger
            .getLogger(AttachmentJDialog.class);

    /**
     * The local string constant.
     */
    private static final String LOCAL = "LOCAL";

    /**
     * The web string constant.
     */
    private static final String WEB = "WEB";

    /**
     * The path text field.
     */
    JTextField pathTextField;

    /**
     * The description text field.
     */
    JTextField description;

    /**
     * The browse button.
     */
    JButton browseButton;

    /**
     * The url text field.
     */
    JTextField urlTextField;

    /**
     * The web type radio button.
     */
    JRadioButton webType;

    /**
     * The concept resource.
     */
    ConceptResource conceptResource;

    /**
     * Concetructor.
     * 
     * @param noteResource
     *            The concept resource.
     */
    public AttachmentJDialog(ConceptResource conceptResource) {
        this(conceptResource, null);
    }

    /**
     * Concetructor.
     * 
     * @param noteResource
     *            The concept resource.
     * @param dragAndDropReference
     *            The drag'n'drop reference.
     */
    public AttachmentJDialog(ConceptResource conceptResource,
            DragAndDropReference dragAndDropReference) {

        super(Messages.getString("AttachmentJDialog.title"));
        this.conceptResource = conceptResource;
        getContentPane().setLayout(new BorderLayout());
        JPanel p, pp;

        p = new JPanel();
        p.setLayout(new BorderLayout());
        JLabel intro = new JLabel("<html>&nbsp;&nbsp;"
                + Messages.getString("AttachmentJDialog.introduction")
                + "&nbsp;&nbsp;<br><br></html>");
        p.add(intro, BorderLayout.NORTH);
        p.add(new JLabel("<html>&nbsp;&nbsp;"
                + Messages.getString("AttachmentJDialog.description")
                + "</html>"), BorderLayout.CENTER);
        description = new JTextField(38);
        pp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pp.add(description);
        p.add(pp, BorderLayout.SOUTH);
        getContentPane().add(p, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new TitledBorder(Messages
                .getString("AttachmentJDialog.resource")));

        ButtonGroup attachType = new ButtonGroup();

        JPanel webPanel = new JPanel();
        webPanel.setLayout(new BorderLayout());
        webType = new JRadioButton(Messages.getString("AttachmentJDialog.web"));
        webType.setActionCommand(WEB);
        webType.addActionListener(this);
        webType.setSelected(true);
        attachType.add(webType);
        webPanel.add(webType, BorderLayout.NORTH);
        urlTextField = new JTextField("http://", 35);
        urlTextField.selectAll();
        urlTextField.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    attach();
                }
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel("   "));
        p.add(urlTextField);
        webPanel.add(p, BorderLayout.SOUTH);
        mainPanel.add(webPanel, BorderLayout.NORTH);

        JPanel localPanel = new JPanel();
        localPanel.setLayout(new BorderLayout());
        JRadioButton localType = new JRadioButton(Messages
                .getString("AttachmentJDialog.local"));
        localType.setActionCommand(LOCAL);
        localType.addActionListener(this);
        localPanel.add(localType, BorderLayout.NORTH);
        pathTextField = new JTextField(35);
        pathTextField.setEnabled(false);
        browseButton = new JButton(Messages
                .getString("AttachmentJDialog.browse"));
        browseButton.setToolTipText(Messages
                .getString("AttachmentJDialog.browseTip"));
        browseButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setApproveButtonText(Messages
                        .getString("AttachmentJDialog.attach"));
                fc.setControlButtonsAreShown(true);
                fc.setDialogTitle(Messages
                        .getString("AttachmentJDialog.chooseAttachment"));
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = fc.showOpenDialog(AttachmentJDialog.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    pathTextField.setText(file.toString());
                }
            }
        });
        browseButton.setEnabled(false);
        pp = new JPanel();
        pp.setLayout(new BorderLayout());
        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel("   "));
        pp.add(p, BorderLayout.NORTH);
        p.add(pathTextField);
        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.RIGHT));
        p.add(browseButton);
        pp.add(p, BorderLayout.SOUTH);
        localPanel.add(pp, BorderLayout.SOUTH);
        attachType.add(localType);
        mainPanel.add(localPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // buttons
        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton(Messages
                .getString("AttachmentJDialog.attach"));

        p.add(addButton);
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                attach();
            }
        });
        JButton cancelButton = new JButton(Messages
                .getString("AttachmentJDialog.cancel"));
        p.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                AttachmentJDialog.this.dispose();
            }
        });
        getContentPane().add(p, BorderLayout.SOUTH);

        /*
         * drag and drop initialization
         */
        if (dragAndDropReference != null) {
            if (dragAndDropReference.getType() == DragAndDropReference.BROWSER_LINK) {
                urlTextField.setText(dragAndDropReference.getReference());
                localType.setSelected(false);
                webType.setSelected(true);
                enableWebTypeButtons();
            } else {
                pathTextField.setText(dragAndDropReference.getReference());
                localType.setSelected(true);
                webType.setSelected(false);
                enableLocalTypeButtons();
            }
            description.setText(dragAndDropReference.getTitle());
        }

        pack();
        Gfx.centerAndShowWindow(this);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (LOCAL.equals(e.getActionCommand())) {
            enableLocalTypeButtons();
        } else {
            enableWebTypeButtons();
        }
    }

    /**
     * Enable the web type buttons.
     */
    private void enableWebTypeButtons() {
        browseButton.setEnabled(false);
        pathTextField.setEnabled(false);
        urlTextField.setEnabled(true);
    }

    /**
     * Enable the local type buttons.
     */
    private void enableLocalTypeButtons() {
        browseButton.setEnabled(true);
        pathTextField.setEnabled(true);
        urlTextField.setEnabled(false);
    }

    /**
     * Attach the selected resource.
     */
    void attach() {
        String attachUrl;

        if (webType.isSelected()) {
            attachUrl = urlTextField.getText();
        } else {
            attachUrl = pathTextField.getText();
        }

        if (StringUtils.isEmpty(attachUrl)) {
            JOptionPane.showMessageDialog(MindRaider.mainJFrame, Messages
                    .getString("AttachmentJDialog.errorMessage"), Messages
                    .getString("AttachmentJDialog.error"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String txt = description.getText();
            if (StringUtils.isEmpty(txt)) {
                txt = attachUrl;
            }
            MindRaider.noteCustodian.addAttachment(conceptResource, new AttachmentResource(txt, attachUrl), true);
        } catch (Exception e) {
            logger.debug(Messages.getString(
                    "AttachmentJDialog.unableToAttachResource", new Object[] {
                            attachUrl, e.getMessage() }));
        }
        StatusBar.show(Messages.getString(
                "AttachmentJDialog.attachedWebResource", attachUrl));
        OutlineJPanel.getInstance().conceptJPanel.refresh();
        dispose();
    }
}
