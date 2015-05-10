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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;

import com.emental.mindraider.core.MindRaider;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;

/**
 * Add triplet.
 */
public class AddLinkToConceptJDialog extends ProgramIconJDialog {

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The predicate NS text field.
     */
    private JTextField predicateNs;

    /**
     * The predicate local name text field.
     */
    private JTextField predicateLocalName;

    /**
     * The object NS text field.
     */
    private JTextField objectNs;

    /**
     * The object local name text field.
     */
    private JButton findConceptButton;

    /**
     * Constructor.
     */
    public AddLinkToConceptJDialog() {
        super("Add Link To Note");

        JPanel framePanel = new JPanel();
        framePanel.setLayout(new GridLayout(3, 1));

        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.RIGHT));
        p.add(new JLabel(" Predicate: "));

        predicateNs = new JTextField(38);
        predicateNs.setText(
                "http://" + MindRaider.profile.getHostname()
                + "/e-mentality/conceptLink/custom#");
        predicateNs.selectAll();
        p.add(predicateNs);
        p.add(new JLabel("#"));
        predicateLocalName = new JTextField(15);
        p.add(predicateLocalName);
        framePanel.add(p);

        
        
        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel(" Note: "));

        objectNs = new JTextField(38);
        objectNs.setText("");
        objectNs.selectAll();
        p.add(objectNs);
        
        findConceptButton = new JButton("Find Note");
        findConceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OpenNoteJDialog.NoteToOpen conceptToOpen=new OpenNoteJDialog.NoteToOpen();
                new OpenNoteJDialog("OpenNoteJDialog.titleLink", "OpenNoteJDialog.selectConceptToLink", "OpenNoteJDialog.link", null, conceptToOpen);
                if(conceptToOpen.conceptLabel!=null) {
                    objectNs.setText(conceptToOpen.conceptUri);
                }
            }

        });
        p.add(findConceptButton);
        framePanel.add(p);


        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add");
        p.add(addButton);
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createTriplet();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        p.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                AddLinkToConceptJDialog.this.dispose();
            }
        });
        framePanel.add(p);

        getContentPane().add(framePanel, BorderLayout.CENTER);

        // show
        pack();
        Gfx.centerAndShowWindow(this);
    }

    /**
     * Create a triplet.
     */
    protected void createTriplet() {
        if (StringUtils.isEmpty(predicateNs.getText())) {
            predicateNs.setText(MindRaiderConstants.MR_RDF_NS);
        }

        if (StringUtils.isEmpty(predicateLocalName.getText())) {
            predicateLocalName.setText(MindRaiderConstants.MR_RDF_PREDICATE);
        }

        if (StringUtils.isEmpty(objectNs.getText())) {
            objectNs.setText(MindRaiderConstants.MR_RDF_NS);
        }

        if (StringUtils.isEmpty(findConceptButton.getText())) {
            JOptionPane.showMessageDialog(MindRaider.mainJFrame,
                    "You must specify object name.",
                    "Statement Creation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MindRaider.spidersGraph.createStatement(
                new QName(predicateNs.getText(), predicateLocalName.getText()),
                new QName(null,objectNs.getText()),
                false);
        AddLinkToConceptJDialog.this.dispose();
    }

}
