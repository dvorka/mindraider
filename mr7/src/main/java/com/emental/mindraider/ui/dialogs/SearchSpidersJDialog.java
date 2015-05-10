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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.emental.mindraider.core.MindRaider;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;

/**
 * The RdfModelSearch class.
 */
public class SearchSpidersJDialog extends ProgramIconJDialog {

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The search text field.
     */
    final JTextField searchTextField;

    /**
     * The search button.
     */
    final JButton searchButton;

    /**
     * Constructor.
     */
    public SearchSpidersJDialog() {
        super(Messages.getString("SearchSpidersJDialog.title"));

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 1));
        searchTextField = new JTextField(30);
        searchTextField.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    MindRaider.spidersGraph.getPanel().search(
                            searchTextField.getText());
                }
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        p.add(searchTextField);

        searchButton = new JButton(Messages
                .getString("SearchSpidersJDialog.searchButton"));
        searchButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                MindRaider.spidersGraph.getPanel().search(
                        searchTextField.getText());
            }
        });
        p.add(searchButton);

        setContentPane(p);

        // show
        setSize(250, 105);
        setLocation(750, 120);
        setVisible(true);
    }
}