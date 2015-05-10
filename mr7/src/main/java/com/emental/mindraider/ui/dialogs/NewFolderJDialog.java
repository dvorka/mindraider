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
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;
import com.mindcognition.mindraider.utils.Utils;

public class NewFolderJDialog extends ProgramIconJDialog {

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The text field width constant.
     */
    public static final int TEXTFIELD_WIDTH = 39;

    /**
     * The folder label text field.
     */
    private JTextField folderLabel;

    /**
     * The Nc folder name text field.
     */
    private JTextField folderNcName;

    /**
     * The folder Uri text field.
     */
    private JTextField folderUri;

    /**
     * Constructor.
     * 
     * @throws java.awt.HeadlessException
     *             The Headless exception
     */
    public NewFolderJDialog() throws HeadlessException {
        super(Messages.getString("NewFolderJDialog.title"));

        JPanel framePanel = new JPanel();
        framePanel.setLayout(new BorderLayout());

        // folder name
        JPanel p = new JPanel();
        p.setBorder(new TitledBorder(Messages
                .getString("NewFolderJDialog.basic")));
        p.setLayout(new FlowLayout(FlowLayout.RIGHT));
        p.add(new JLabel(Messages.getString("NewFolderJDialog.label")));
        folderLabel = new JTextField(TEXTFIELD_WIDTH);
        folderLabel.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    createFolder();
                }
            }

            public void keyReleased(KeyEvent keyEvent) {
                folderNcName.setText(Utils.toNcName(folderLabel.getText()));
                folderUri.setText(MindRaiderVocabulary.getFolderUri(folderNcName.getText()));
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        p.add(folderLabel);
        framePanel.add(p, BorderLayout.NORTH);

        // advanced
        p = new JPanel();
        p.setLayout(new GridLayout(2, 1));
        p.setBorder(new TitledBorder(Messages
                .getString("NewFolderJDialog.advanced")));
        JPanel pp = new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pp.add(new JLabel(Messages.getString("NewFolderJDialog.ncName")));
        folderNcName = new JTextField(TEXTFIELD_WIDTH);
        folderNcName.setEnabled(false);
        folderNcName.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        pp.add(folderNcName);
        p.add(pp);

        pp = new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pp.add(new JLabel(Messages.getString("NewFolderJDialog.uri")));
        folderUri = new JTextField(TEXTFIELD_WIDTH);
        folderUri.setText(MindRaiderVocabulary.getFolderUriSkeleton());
        folderUri.setEnabled(false);
        folderUri.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        pp.add(folderUri);
        p.add(pp);
        framePanel.add(p, BorderLayout.CENTER);

        // buttons
        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton(Messages
                .getString("NewFolderJDialog.create"));
        p.add(addButton);
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createFolder();
            }
        });

        JButton cancelButton = new JButton(Messages
                .getString("NewFolderJDialog.cancel"));
        p.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                NewFolderJDialog.this.dispose();
            }
        });
        framePanel.add(p, BorderLayout.SOUTH);

        getContentPane().add(framePanel, BorderLayout.CENTER);

        // show
        pack();
        Gfx.centerAndShowWindow(this);
    }

    /**
     * Create folder.
     */
    protected void createFolder() {
        if ("".equals(folderLabel.getText())) {
            JOptionPane.showMessageDialog(this, Messages
                    .getString("NewFolderJDialog.folderCannotBeEmpty"),
                    Messages.getString("NewFolderJDialog.folderCreationError"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        MindRaider.labelCustodian.createAndGenerateUri(folderLabel.getText(),
                folderUri.getText());

        NewFolderJDialog.this.dispose();
    }

    /**
     * Getter for <code>folderNcName</code>.
     * 
     * @return Returns the folderNcName.
     */
    public JTextField getFolderNcName() {
        return this.folderNcName;
    }

    /**
     * Setter for <code>folderNcName</code>.
     * 
     * @param folderNcName
     *            The folderNcName to set.
     */
    public void setFolderNcName(JTextField folderNcName) {
        this.folderNcName = folderNcName;
    }

    /**
     * Getter for <code>folderUri</code>.
     * 
     * @return Returns the folderUri.
     */
    public JTextField getFolderUri() {
        return this.folderUri;
    }

    /**
     * Setter for <code>folderUri</code>.
     * 
     * @param folderUri
     *            The folderUri to set.
     */
    public void setFolderUri(JTextField folderUri) {
        this.folderUri = folderUri;
    }
}
