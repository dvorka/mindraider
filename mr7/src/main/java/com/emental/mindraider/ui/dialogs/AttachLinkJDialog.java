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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;

/**
 * The Attach link class.
 */
public class AttachLinkJDialog extends ProgramIconJDialog {

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The attachment URL.
     */
    private JTextField attachmentUrl;

    /**
     * Constructor.
     */
    public AttachLinkJDialog() {
        super("Messages.getString(\"AttachLinkJDialog.title\")");

        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 5));

        p.add(new JLabel(Messages.getString("AttachLinkJDialog.resourceUrl")));

        attachmentUrl = new JTextField("http://", 35);
        attachmentUrl.selectAll();
        attachmentUrl.addKeyListener(new KeyListener() {

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
        p.add(attachmentUrl);

        JButton uploadButton = new JButton(Messages
                .getString("AttachLinkJDialog.attach"));
        uploadButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                attach();
            }
        });
        p.add(uploadButton);

        JButton cancelButton = new JButton(Messages
                .getString("AttachLinkJDialog.cancel"));
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        p.add(cancelButton);

        getContentPane().add(p, BorderLayout.CENTER);

        // show
        pack();
        Gfx.centerAndShowWindow(this);
    }

    /**
     * Attach link to resource.
     */
    protected void attach() {
        String attachUrl = attachmentUrl.getText();
        if (attachUrl == null || "".equals(attachUrl)) {
            JOptionPane.showMessageDialog(AttachLinkJDialog.this, Messages
                    .getString("AttachLinkJDialog.attachmentUrlIsEmpty"),
                    Messages.getString("AttachLinkJDialog.attachmentError"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // TODO merge with concept JPanel code
        // TODO MindRaider.conceptCustodian.addAttachment(null,attachUrl,"No
        // description");
        StatusBar.show(Messages.getString("AttachLinkJDialog.attachment",
                attachUrl));
        OutlineJPanel.getInstance().conceptJPanel.refresh();
        dispose();
    }
}
