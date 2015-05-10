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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.ui.graph.spiders.SpidersGraph;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;

/**
 * The New RDF Model.
 */
public class NewRdfModelJDialog extends ProgramIconJDialog {

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The subject namespace text field.
     */
    private JTextField subjectNs;

    /**
     * The subject local name text field.
     */
    private JTextField subjectLocalName;

    /**
     * Constructor.
     */
    public NewRdfModelJDialog() {

        super(Messages.getString("NewRdfModelJDialog.title"));

        JPanel framePanel = new JPanel();
        framePanel.setLayout(new GridLayout(3, 1));

        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.RIGHT));
        p.add(new JLabel(Messages.getString("NewRdfModelJDialog.subject")));

        subjectNs = new JTextField(30);
        subjectNs.setText(MindRaiderConstants.MR_RDF_PREDICATE_NS);
        p.add(subjectNs);
        p.add(new JLabel("#"));

        subjectLocalName = new JTextField(15);
        p.add(subjectLocalName);
        framePanel.add(p);

        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.RIGHT));
        final JCheckBox literalCheckBox = new JCheckBox("literal", false);
        p.add(literalCheckBox);
        framePanel.add(p);

        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton(Messages
                .getString("NewRdfModelJDialog.create"));
        p.add(addButton);
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createModel(literalCheckBox);
            }
        });

        JButton cancelButton = new JButton(Messages
                .getString("NewRdfModelJDialog.cancel"));
        p.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                NewRdfModelJDialog.this.dispose();
            }
        });
        framePanel.add(p);

        subjectLocalName.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    createModel(literalCheckBox);
                }
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        });

        getContentPane().add(framePanel, BorderLayout.CENTER);

        // show
        pack();
        Gfx.centerAndShowWindow(this);

        addWindowListener(new WindowAdapter() {

            public void windowActivated(WindowEvent e) {
                subjectLocalName.requestFocusInWindow();
            }
        });
    }

    /**
     * Create model.
     * 
     * @param literalCheckBox
     *            The literal checkbox.
     */
    protected void createModel(final JCheckBox literalCheckBox) {
        String subj = subjectNs.getText();
        if (StringUtils.isEmpty(subj)) {
            subjectNs.setText(MindRaiderConstants.MR_RDF_NS);
        }
        MindRaider.spidersGraph.newResource(subj + subjectLocalName.getText(),
                literalCheckBox.isSelected());
        MindRaider.masterToolBar
                .setModelLocation(SpidersGraph.MINDRAIDER_NEW_MODEL);
        dispose();
    }
}
