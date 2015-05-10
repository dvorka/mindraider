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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.application.model.note.NoteTemplates;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.concept.ClassifierFlagRenderer;
import com.mindcognition.mindraider.ui.swing.concept.annotation.ConceptAnnotationsCustodian;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;
import com.mindcognition.mindraider.utils.Utils;

public class NewNoteJDialog extends ProgramIconJDialog {
    private static final Logger logger = Logger.getLogger(NewNoteJDialog.class);

    public static final int TEXTFIELD_WIDTH = 39;

    private JTextField conceptLabel;
    private JTextField conceptNcName;
    private JTextField conceptUri;
    private JComboBox contentTypeCombo;
    private JComboBox categoriesCombo;
    private JComboBox templateCombo;
    private JComboBox positionCombo;
    private String notebookUri;
    private ConceptAnnotationsCustodian conceptAnnotationsCustodian;
    private JTextField tagsTextField;
    
    public NewNoteJDialog(String notebookUri, ConceptAnnotationsCustodian conceptAnnotationsCustodian) throws HeadlessException {
        super(Messages.getString("NewConceptJDialog.title"));
        
        final KeyListener keyListener = new KeyListener() {
            public void keyPressed(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    createConcept();
                }
                if(keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
            public void keyReleased(KeyEvent e) {
            }
            public void keyTyped(KeyEvent e) {
            }
        };
        
        this.notebookUri = notebookUri;
        this.conceptAnnotationsCustodian=conceptAnnotationsCustodian;
        
        JPanel framePanel = new JPanel();
        framePanel.setLayout(new BorderLayout());

        // title
        JPanel p = new JPanel(new GridLayout(6,1));
        p.setBorder(new TitledBorder(Messages.getString("NewConceptJDialog.basic")));
        JPanel pp=new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pp.add(new JLabel(Messages.getString("NewConceptJDialog.label")));
        conceptLabel = new JTextField(TEXTFIELD_WIDTH);
        conceptLabel.addKeyListener(keyListener);
        conceptLabel.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent keyEvent) {
            }
            public void keyReleased(KeyEvent keyEvent) {
                conceptNcName.setText(Utils.toNcName(conceptLabel.getText()));
                conceptUri.setText(MindRaiderVocabulary.getConceptUri(Utils
                        .getNcNameFromUri(getNotebookUri()), conceptNcName
                        .getText()));
            }
            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        pp.add(conceptLabel);
        p.add(pp,BorderLayout.NORTH);
        
        // tags
        pp=new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pp.add(new JLabel(Messages.getString("ConceptJPanel.tags")));
        tagsTextField = new JTextField(TEXTFIELD_WIDTH);
        tagsTextField.setToolTipText(Messages.getString("ConceptJPanel.tagsTooltip"));
        tagsTextField.setEnabled(true);
        tagsTextField.addKeyListener(keyListener);
        pp.add(tagsTextField);
        p.add(pp,BorderLayout.CENTER);
        
        // content type
        pp=new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pp.add(new JLabel(Messages.getString("ConceptJPanel.contentType")));
        DefaultComboBoxModel contentTypeComboModel = new DefaultComboBoxModel();
        contentTypeCombo = new JComboBox(contentTypeComboModel);
        // pull down of annotation types - user might change the annotation type
        final String[] annotationTypeLabels = conceptAnnotationsCustodian.getAnnotationTypeLabels();
        for(String label: annotationTypeLabels) {
            contentTypeComboModel.addElement(label);
        }
        contentTypeCombo.setSelectedIndex(0);
        contentTypeCombo.setEnabled(true);
        contentTypeCombo.addKeyListener(keyListener);
        Dimension metaEntryDimension = new Dimension(318, 18);
        contentTypeCombo.setPreferredSize(metaEntryDimension);
        pp.add(contentTypeCombo);
        p.add(pp,BorderLayout.SOUTH);
        framePanel.add(p, BorderLayout.CENTER);

        // categories
        pp=new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pp.add(new JLabel(Messages.getString("ConceptJPanel.flag")));
        categoriesCombo = new JComboBox(ClassifierFlagRenderer.comboLabels);
        categoriesCombo.setSelectedIndex(0);
        categoriesCombo.setEnabled(true);
        categoriesCombo.addKeyListener(keyListener);
        metaEntryDimension = new Dimension(318, 18);
        categoriesCombo.setPreferredSize(metaEntryDimension);
        pp.add(categoriesCombo);
        p.add(pp,BorderLayout.SOUTH);
        framePanel.add(p, BorderLayout.NORTH);

        
        
        pp=new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        // TODO bundle
        pp.add(new JLabel("Template"));
        templateCombo = new JComboBox(new String[]{
                NoteTemplates.TEMPLATE_NOTE_NONE, 
                NoteTemplates.TEMPLATE_NOTE_BRIEFING, 
                NoteTemplates.TEMPLATE_NOTE_MEETING, 
                NoteTemplates.TEMPLATE_NOTE_REPORT,
                NoteTemplates.TEMPLATE_NOTE_COACH_ACHIEVEMENT,
                NoteTemplates.TEMPLATE_NOTE_COACH_GOAL,
                NoteTemplates.TEMPLATE_NOTE_COACH_REALITY,
                NoteTemplates.TEMPLATE_NOTE_COACH_OPTIONS,
                NoteTemplates.TEMPLATE_NOTE_COACH_WILL});
        templateCombo.setSelectedIndex(0);
        templateCombo.setEnabled(true);
        templateCombo.addKeyListener(keyListener);
        metaEntryDimension = new Dimension(318, 18);
        templateCombo.setPreferredSize(metaEntryDimension);
        pp.add(templateCombo);
        p.add(pp,BorderLayout.SOUTH);
        framePanel.add(p, BorderLayout.NORTH);


        pp=new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        // TODO bundle
        pp.add(new JLabel("Position"));
        positionCombo = new JComboBox(new String[]{
                "The last child"});
        positionCombo.setSelectedIndex(0);
        positionCombo.setEnabled(true);
        positionCombo.addKeyListener(keyListener);
        metaEntryDimension = new Dimension(318, 18);
        positionCombo.setPreferredSize(metaEntryDimension);
        pp.add(positionCombo);
        p.add(pp,BorderLayout.SOUTH);
        framePanel.add(p, BorderLayout.NORTH);
        
        
        
        
        // advanced
        p = new JPanel();
        p.setLayout(new GridLayout(2, 1));
        p.setBorder(new TitledBorder(Messages
                .getString("NewConceptJDialog.advanced")));
        pp = new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pp.add(new JLabel(Messages.getString("NewConceptJDialog.ncName")));
        conceptNcName = new JTextField(TEXTFIELD_WIDTH);
        conceptNcName.setEnabled(false);
        pp.add(conceptNcName);
        p.add(pp);
        // ...
        pp = new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pp.add(new JLabel(Messages.getString("NewConceptJDialog.uri")));
        conceptUri = new JTextField(TEXTFIELD_WIDTH);
        conceptUri.setText(MindRaiderVocabulary.getConceptUriSkeleton(Utils
                .getNcNameFromUri(notebookUri)));
        conceptUri.setEnabled(false);
        pp.add(conceptUri);
        p.add(pp);
        framePanel.add(p, BorderLayout.CENTER);


        
        // buttons
        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton(Messages
                .getString("NewConceptJDialog.create"));
        p.add(addButton);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createConcept();
            }
        });

        JButton cancelButton = new JButton(Messages
                .getString("NewConceptJDialog.cancel"));
        p.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                NewNoteJDialog.this.dispose();
            }
        });
        framePanel.add(p, BorderLayout.SOUTH);

        getContentPane().add(framePanel, BorderLayout.CENTER);

        // show
        pack();
        Gfx.centerAndShowWindow(this);
    }

    /**
     * Getter for <code>notebookUri</code>.
     * 
     * @return Returns the notebook uri value.
     */
    protected String getNotebookUri() {
        return notebookUri;
    }

    /**
     * Create a concept.
     */
    protected void createConcept() {
        if ("".equals(conceptLabel.getText())) {
            JOptionPane.showMessageDialog(
                    this,
                    Messages.getString("NewConceptJDialog.conceptNameCannotBeEmpty"),
                    Messages.getString("NewConceptJDialog.conceptCreationError"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // new node is child of either notebook (returns null) or another concept node
            String conceptTitle
                =conceptLabel.getText();

            String conceptTags
                =tagsTextField.getText();

            String conceptAnnotation
                ="";
            
            String conceptAnnotationContentType=null;
            try {
                final String actionString = (String)contentTypeCombo.getSelectedItem();
                logger.debug("Content type action string: "+actionString); // {{debug}}
                Object owlClass = conceptAnnotationsCustodian.getOwlClassForLabel(actionString);
                logger.debug("    OWL - new content type: "+owlClass); // {{debug}}
                conceptAnnotationContentType=owlClass.toString();
            } catch (Exception e) {
                logger.debug("Unable to set concept annotation type!",e);
            }

            String conceptCategoryTitle
                =(String)categoriesCombo.getSelectedItem();
            
            String parentConceptUri 
                = OutlineJPanel.getInstance().getSelectedConceptUri();
            
            String template
               = (String)templateCombo.getSelectedItem();

            String newConceptUri = conceptUri.getText();
            logger.debug(Messages.getString("NewConceptJDialog.infoCreateChild", parentConceptUri));
            while (MindRaiderConstants.EXISTS.equals(
                        MindRaider.noteCustodian.create(
                                MindRaider.outlineCustodian.getActiveOutlineResource(),
                                parentConceptUri, 
                                conceptTitle,
                                newConceptUri, 
                                conceptAnnotation, 
                                true,
                                conceptAnnotationContentType,
                                conceptTags,
                                conceptCategoryTitle,
                                template,
                                null))) {
                newConceptUri += "_";
            }
        } catch (Exception e) {
            Object[] error = { conceptUri.getText(), e.getMessage() };
            Messages.getString("NewConceptJDialog.unableToCreateConcept", error);
            JOptionPane.showMessageDialog(
                    this, 
                    Messages.getString("NewConceptJDialog.unableToCreateConcept", error), 
                    Messages.getString("NewConceptJDialog.conceptCreationError"),
                    JOptionPane.ERROR_MESSAGE);
        }
        NewNoteJDialog.this.dispose();
    }
    
    private static final long serialVersionUID = 4919268768681238760L;
}
