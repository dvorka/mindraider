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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;

import com.emental.mindraider.core.MindRaider;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;

/**
 * The download model dialog.
 * 
 * @author Martin.Dvorak
 * @version $Revision: 1.7 $ ($Author: mindraider $)
 */
public class DownloadModelJDialog extends ProgramIconJDialog {

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The URL model combo.
     */
    private JComboBox modelUrlCombo;

    /**
     * Constructor.
     * 
     * @param union
     *            made union of models.
     */
    public DownloadModelJDialog(boolean union) {
        super(Messages.getString("DownloadModelJDialog.title"));

        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 5));

        p.add(new JLabel(Messages.getString("DownloadModelJDialog.url")));

        String[] knowUris = new String[] { "http://", "http://wymiwyg.org/",
                "http://www.osar.ch",
                "http://wymiwyg.org/.rdf?appendLang=en&till=50",
                "http://www.osar.ch/.rdf?appendLang=de&till=50" };
        modelUrlCombo = new JComboBox(knowUris);
        modelUrlCombo.setEditable(true);
        modelUrlCombo.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    upload();
                }
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        p.add(modelUrlCombo);

        JButton uploadButton = new JButton(Messages
                .getString("DownloadModelJDialog.download"));
        uploadButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                upload();
            }
        });
        p.add(uploadButton);

        JButton cancelButton = new JButton(Messages
                .getString("DownloadModelJDialog.cancel"));
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
     * Upload the model.
     */
    protected void upload() {
        String modelUrl = (String) modelUrlCombo.getSelectedItem();
        if (StringUtils.isEmpty(modelUrl)) {
            StatusBar.show(Messages
                    .getString("DownloadModelJDialog.invalidModelLocation"),
                    Color.RED);
        } else {
            try {
                MindRaider.spidersGraph.load(modelUrl);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, Messages
                        .getString("DownloadModelJDialog.loadModelError"),
                        Messages.getString(
                                "DownloadModelJDialog.unableToLoadModel", e
                                        .getMessage()),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            StatusBar.show(Messages.getString(
                    "DownloadModelJDialog.modelDownloaded", modelUrl));
        }
        dispose();
    }
}
