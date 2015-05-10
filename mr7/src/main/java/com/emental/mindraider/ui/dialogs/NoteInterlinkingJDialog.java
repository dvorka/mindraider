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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.mindcognition.mindraider.application.model.note.annotation.NoteInterlinking;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.concept.annotation.renderer.AbstractTextAnnotationRenderer;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;

public class NoteInterlinkingJDialog extends ProgramIconJDialog implements ActionListener {
    private static final Logger logger = Logger.getLogger(NoteInterlinkingJDialog.class);

    final int TEXT_FIELD_LNG = 17;

    private static final String WEB = "WEB";
    private static final String NOTE = "NOTE";
    private static final String OUTLINE = "OUTLINE";

    /**
     * The path text field.
     */
    JTextField outlineForNoteLinkText;
    JTextField outlineForOutlineLinkText;
    String outlineForOutlineLinkUri;
    JTextField noteForNoteLinkText;
    JTextField webLinkText;

    /**
     * The browse button.
     */
    JButton findNoteForNoteLinkButton;
    JButton findOutlineForNoteLinkButton;
    JButton findOutlineForOutlineLinkButton;

    /**
     * The web type radio button.
     */
    JRadioButton noteLinkRadio;
    JRadioButton outlineLinkRadio;
    JRadioButton webLinkRadio;

    /**
     * The concept resource.
     */
    ConceptResource noteResource;

    private AbstractTextAnnotationRenderer textRenderer;

    public NoteInterlinkingJDialog(AbstractTextAnnotationRenderer textRenderer) {
        super(Messages.getString("NoteInterlinkingJDialog.title"));

        this.textRenderer=textRenderer;
        outlineForOutlineLinkUri=MindRaider.outlineCustodian.getActiveOutlineResource().getUri();
        
        getContentPane().setLayout(new BorderLayout());
        JPanel p, pp;

        p = new JPanel();
        p.setLayout(new BorderLayout());
        JLabel intro = new JLabel("  "+Messages.getString("NoteInterlinkingJDialog.introduction"));
        p.add(intro, BorderLayout.NORTH);
        add(p, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new TitledBorder(Messages.getString("NoteInterlinkingJDialog.linkTarget")));

        ButtonGroup buttonGroup = new ButtonGroup();

        JPanel webPanel = new JPanel();
        webPanel.setLayout(new GridLayout(7,1));
        noteLinkRadio = new JRadioButton(Messages.getString("NoteInterlinkingJDialog.concept"));
        noteLinkRadio.setActionCommand(NOTE);
        noteLinkRadio.addActionListener(this);
        noteLinkRadio.setSelected(true);
        buttonGroup.add(noteLinkRadio);
        webPanel.add(noteLinkRadio);


        
        // find concept
        p = buildFindNotePanel();
        webPanel.add(p);
        // find notebook & concept
        p = buildFindOutlineForNotePanel();
        webPanel.add(p);
        
        mainPanel.add(webPanel, BorderLayout.CENTER);

        
        
        JPanel soutMainPanel=new JPanel(new BorderLayout());

        
        
        // *) notebook link
        outlineLinkRadio = new JRadioButton(Messages.getString("NoteInterlinkingJDialog.notebook"));
        outlineLinkRadio.setActionCommand(OUTLINE);
        outlineLinkRadio.addActionListener(this);
        webPanel.add(outlineLinkRadio);
        pp = buildFindOutlinePanel();
        webPanel.add(pp);
        buttonGroup.add(outlineLinkRadio);


        
        // *) web link
        webLinkRadio = new JRadioButton("Web");
        webLinkRadio.setActionCommand(WEB);
        webLinkRadio.addActionListener(this);
        webPanel.add(webLinkRadio);
        pp = buildWebPanel();
        webPanel.add(pp);
        buttonGroup.add(webLinkRadio);

        
        
        // complete SOUTH stuff
        mainPanel.add(soutMainPanel,BorderLayout.SOUTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        

        
        // set up buttons
        enableNoteTypeButtons();
        
        
        
        // buttons
        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton(Messages.getString("NoteInterlinkingJDialog.create"));

        p.add(addButton);
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                buildLink();
            }
        });
        JButton cancelButton = new JButton(Messages.getString("NoteInterlinkingJDialog.cancel"));
        p.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                NoteInterlinkingJDialog.this.dispose();
            }
        });
        getContentPane().add(p, BorderLayout.SOUTH);

        pack();
        Gfx.centerAndShowWindow(this);
    }
    
    private String getOutlineToOpenUri() {
        return outlineForOutlineLinkUri;
    }

    private JPanel buildFindNotePanel() {
        // TODO bug: concepts must be searched in the notebook that was selected in other finder
        JPanel p;
        noteForNoteLinkText = new JTextField(TEXT_FIELD_LNG);
        findNoteForNoteLinkButton = new JButton(Messages.getString("NoteInterlinkingJDialog.findConcept"));
        findNoteForNoteLinkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OpenNoteJDialog.NoteToOpen noteToOpen=new OpenNoteJDialog.NoteToOpen();
                new OpenNoteJDialog(
                        "OpenNoteJDialog.title", 
                        "OpenNoteJDialog.selectConceptToOpen", 
                        "OpenNoteJDialog.open",
                        getOutlineToOpenUri(),
                        noteToOpen);
                if(noteToOpen.conceptLabel!=null) {
                    noteForNoteLinkText.setText(noteToOpen.conceptLabel);
                }
            }
        });
        
        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel(" Note:     "));
        p.add(noteForNoteLinkText);
        p.add(findNoteForNoteLinkButton);

        return p;
    }

    private JPanel buildFindOutlinePanel() {
        JPanel p;
        outlineForOutlineLinkText = new JTextField(TEXT_FIELD_LNG);
        findOutlineForOutlineLinkButton = new JButton(Messages.getString("NoteInterlinkingJDialog.findNotebook"));
        findOutlineForOutlineLinkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OpenOutlineJDialog.OutlineToOpen outlineToOpen=new OpenOutlineJDialog.OutlineToOpen();
                new OpenOutlineJDialog("Find Target Outline", " Type target Outline label: ", "Link", true, outlineToOpen);
                if(outlineToOpen.outlineLabel!=null) {
                    outlineForOutlineLinkText.setText(outlineToOpen.outlineLabel);
                }
            }
        });

        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel(" Outline: "));
        p.add(outlineForOutlineLinkText);
        p.add(findOutlineForOutlineLinkButton);

        return p;
    }

    private JPanel buildFindOutlineForNotePanel() {
        JPanel p;
        outlineForNoteLinkText = new JTextField(TEXT_FIELD_LNG);
        findOutlineForNoteLinkButton = new JButton(Messages.getString("NoteInterlinkingJDialog.findNotebook"));
        findOutlineForNoteLinkButton.setToolTipText(Messages.getString("NoteInterlinkingJDialog.findNotebookTooltip"));
        findOutlineForNoteLinkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OpenOutlineJDialog.OutlineToOpen noteToOpen=new OpenOutlineJDialog.OutlineToOpen();
                new OpenOutlineJDialog("Find Target Outline", " Type target Outline label: ", "Link", true, noteToOpen);
                if(noteToOpen.outlineLabel!=null) {
                    outlineForNoteLinkText.setText(noteToOpen.outlineLabel);
                    noteForNoteLinkText.setText("");
                    outlineForOutlineLinkUri=noteToOpen.outlineUri;
                }
            }
        });
        
        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel(" Outline: "));
        p.add(outlineForNoteLinkText);
        p.add(findOutlineForNoteLinkButton);

        return p;
    }

    /**
     * Build find web panel.
     *
     * @return
     */
    private JPanel buildWebPanel() {
        JPanel pp;
        webLinkText = new JTextField(TEXT_FIELD_LNG*2);
        pp = new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pp.add(webLinkText);
        return pp;
    }

    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (OUTLINE.equals(e.getActionCommand())) {
            enableOutlineTypeButtons();
        } else {
            if(WEB.equals(e.getActionCommand())) {
                enableWebTypeButtons();
            } else {
                enableNoteTypeButtons();
            }
        }
    }

    /**
     * Enable the web type buttons.
     */
    private void enableWebTypeButtons() {
        findNoteForNoteLinkButton.setEnabled(false);
        findOutlineForNoteLinkButton.setEnabled(false);
        outlineForNoteLinkText.setEnabled(false);
        noteForNoteLinkText.setEnabled(false);

        findOutlineForOutlineLinkButton.setEnabled(false);
        outlineForOutlineLinkText.setEnabled(false);
        
        webLinkText.setEnabled(true);
    }

    /**
     * Enable the web type buttons.
     */
    private void enableNoteTypeButtons() {
        findNoteForNoteLinkButton.setEnabled(true);
        findOutlineForNoteLinkButton.setEnabled(true);        
        outlineForNoteLinkText.setEnabled(true);
        noteForNoteLinkText.setEnabled(true);

        findOutlineForOutlineLinkButton.setEnabled(false);
        outlineForOutlineLinkText.setEnabled(false);

        webLinkText.setEnabled(false);
    }

    /**
     * Enable the local type buttons.
     */
    private void enableOutlineTypeButtons() {
        findNoteForNoteLinkButton.setEnabled(false);
        findOutlineForNoteLinkButton.setEnabled(false);
        outlineForNoteLinkText.setEnabled(false);
        noteForNoteLinkText.setEnabled(false);

        findOutlineForOutlineLinkButton.setEnabled(true);
        outlineForOutlineLinkText.setEnabled(true);

        webLinkText.setEnabled(false);
    }

    /**
     * Attach the selected resource.
     */
    void buildLink() {
        String link=null;

        if (outlineLinkRadio.isSelected()) {
            if(outlineForOutlineLinkText.getText()==null || "".equals(outlineForOutlineLinkText.getText())) {
                JOptionPane.showMessageDialog(MindRaider.mainJFrame, Messages.getString("NoteInterlinkingJDialog.errorMessageNotebook"),
                        Messages.getString("NoteInterlinkingJDialog.error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            link=NoteInterlinking.LINK_PREFIX+outlineForOutlineLinkText.getText()+NoteInterlinking.LINK_SUFIX;
        } else {
            if(noteLinkRadio.isSelected()) {
                if(noteForNoteLinkText.getText()==null || "".equals(noteForNoteLinkText.getText())) {
                    JOptionPane.showMessageDialog(MindRaider.mainJFrame, Messages.getString("NoteInterlinkingJDialog.errorMessageConcept"),
                            Messages.getString("NoteInterlinkingJDialog.error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                link=NoteInterlinking.LINK_PREFIX;
                if(outlineForNoteLinkText.getText()!=null && !"".equals(outlineForNoteLinkText.getText())) {
                    link+=outlineForNoteLinkText.getText()+"#";
                }
                link+=noteForNoteLinkText.getText()+NoteInterlinking.LINK_SUFIX;
            } else {
                if(webLinkText.getText()==null || "".equals(webLinkText.getText())) {
                    JOptionPane.showMessageDialog(MindRaider.mainJFrame, Messages.getString("NoteInterlinkingJDialog.errorMessageWeb"),
                            Messages.getString("NoteInterlinkingJDialog.error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                link=NoteInterlinking.LINK_PREFIX+webLinkText.getText()+"]["+webLinkText.getText()+NoteInterlinking.LINK_SUFIX;
            }
        }

        logger.debug("Link is: "+link);
        dispose();

        textRenderer.insertTextOnCarretPosition(link);
        
        // set it in the concept annotation
        logger.error("Inject link method refactored elsewhere!");
    }

    private static final long serialVersionUID = 1L;
}
