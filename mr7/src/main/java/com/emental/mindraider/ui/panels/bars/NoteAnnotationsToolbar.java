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
package com.emental.mindraider.ui.panels.bars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.emental.mindraider.ui.dialogs.NoteInterlinkingJDialog;
import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.mindcognition.mindraider.ui.swing.concept.ConceptJPanel;
import com.mindcognition.mindraider.ui.swing.concept.annotation.renderer.AbstractTextAnnotationRenderer;

/**
 * Toolbar used to manage concept annotations. It is used by the concept widget.
 */
public class NoteAnnotationsToolbar extends JToolBar {
    
    protected JButton toggleAttachmentsButton;
    protected JButton saveButton;
    protected JButton linkButton;

    public NoteAnnotationsToolbar() {
    }
    
    public void buildToggleAttachments(final JPanel attachmentJPanel) {
        toggleAttachmentsButton=new JButton("",IconsRegistry.getImageIcon("attach.png"));
        toggleAttachmentsButton.setToolTipText("Show/hide attachments panel");
        toggleAttachmentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(attachmentJPanel.isVisible()) {
                    attachmentJPanel.setVisible(false);
                } else {
                    attachmentJPanel.setVisible(true);
                }
            }
        });
        add(toggleAttachmentsButton);
    }

    public void buildSave(final ConceptJPanel conceptJPanel) {
        saveButton=new JButton("",IconsRegistry.getImageIcon("save.png"));
        saveButton.setToolTipText("Save [Ctrl-S]");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conceptJPanel.refreshPreviewPane();
            }
        });
        add(saveButton);
        saveButton.setEnabled(false);
    }

    public void buildLinking(final AbstractTextAnnotationRenderer textRenderer) {
        linkButton = new JButton("",IconsRegistry.getImageIcon("interlinking.png"));
        linkButton.setToolTipText("Create link to Note or Outline [Ctrl-L]");
        linkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new NoteInterlinkingJDialog(textRenderer);
            }
        });
        add(linkButton);
    }
    
    private static final long serialVersionUID = 4293340503848587478L;
}
