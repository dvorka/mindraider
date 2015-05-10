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
package com.mindcognition.mindraider.ui.swing.dialogs;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.explorer.ExplorerJPanel;
import com.mindcognition.mindraider.utils.Utils;

public class NewOutlineJDialog extends AbstractOutlineJDialog {
    private static final Logger logger = Logger.getLogger(NewOutlineJDialog.class);

    public NewOutlineJDialog() throws HeadlessException {
        super(Messages.getString("NewNotebookJDialog.title"),Messages.getString("NewNotebookJDialog.create"));

        final KeyListener keyListener = new KeyListener() {
            public void keyPressed(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
            public void keyReleased(KeyEvent e) {
            }
            public void keyTyped(KeyEvent e) {
            }
        };
        
        outlineTitle.addKeyListener(NewNotebookKeyListener());

        // buttons
        doButton.addActionListener(NewNotebookDoActionListener());
        cancelButton.addActionListener(AbstractNotebookCancelActionListener());
        
        // create on enter/close on escape
        outlineTitle.addKeyListener(keyListener);
        outlineLabels.addKeyListener(keyListener);
        
        // show
        pack();
        Gfx.centerAndShowWindow(this);
    }

    private ActionListener NewNotebookDoActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createOutline();
            }
        };
    }

    private KeyListener NewNotebookKeyListener() {
        return new KeyListener() {

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    createOutline();
                }
            }

            public void keyReleased(KeyEvent keyEvent) {
                JTextField nbnc = getNotebookNcName();
                JTextField nburi = getNotebookUri();
                nbnc.setText(Utils.toNcName(getNotebookLabel().getText()));
                nburi.setText(MindRaiderVocabulary.getNotebookUri(nbnc
                        .getText()));
                setNotebookUri(nburi);
                setNotebookNcName(nbnc);
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        };
    }

    protected void createOutline() {
        if ("".equals(outlineTitle.getText())) {
            JOptionPane
                    .showMessageDialog(
                            this,
                            Messages
                                    .getString("NewNotebookJDialog.notebookNameCannotBeEmpty"),
                            Messages
                                    .getString("NewNotebookJDialog.notebookCreationError"),
                            JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String newNotebookUri = outlineUri.getText();
            String createdUri;
            while (MindRaiderConstants.EXISTS.equals(createdUri = MindRaider.outlineCustodian.create(outlineTitle.getText(), newNotebookUri, null, true))) {
                newNotebookUri += "_";
            }
            
            // process labels - create new for every unknown
            String text=outlineLabels.getText();
            if(text!=null && text.length()>0) {
                String[] labelsArray = text.split(",");
                for(String labelLabel: labelsArray) {
                    labelLabel=labelLabel.trim();
                    String labelNcName=Utils.toNcName(labelLabel);
                    logger.debug(" New label: "+labelLabel+" # "+labelNcName);

                    // make NCName from the label and use it as ID
                    String labelUri = MindRaiderVocabulary.getFolderUri(labelNcName);
                    if(!MindRaider.labelCustodian.exists(labelUri)) {
                        MindRaider.labelCustodian.create(labelLabel, labelUri);
                    }

                    MindRaider.labelCustodian.addOutline(labelUri, createdUri);
                }
            } else {
                // create "all" label, if notebook has no labels
                String allLabelUri = MindRaiderVocabulary.getFolderUri("all");
                if(!MindRaider.labelCustodian.exists(allLabelUri)) {
                    MindRaider.labelCustodian.create("all", allLabelUri);
                }
                MindRaider.labelCustodian.addOutline(allLabelUri, createdUri);
            }
            
            logger.debug("Created notebook with URI: "+createdUri); // {{debug}}
                        
            ExplorerJPanel.getInstance().refresh();
            
            try {
                OutlineJPanel.getInstance().conceptJPanel.clear();
                MindRaider.profile.setActiveOutlineUri(null);
                MindRaider.outlineCustodian.loadOutline(new URI(createdUri));
                OutlineJPanel.getInstance().refresh();
            } catch (URISyntaxException e1) {
                // TODO option with question whether to remove non-existent notebook
                logger.debug("Unable to load Outline: ", e1);
            }
            
            // directly open new concept dialog
            OutlineJPanel.getInstance().newConcept();
        } catch (Exception e) {
            logger.error("createNotebook()", e);
            JOptionPane.showMessageDialog(this, Messages
                    .getString("NewNotebookJDialog.notebookCreationError"),
                    Messages.getString(
                            "NewNotebookJDialog.unableToCreateNotebook", e
                                    .getMessage()), JOptionPane.ERROR_MESSAGE);
        }
        NewOutlineJDialog.this.dispose();
    }
    
    private static final long serialVersionUID = 2638527277546126398L;
}    
