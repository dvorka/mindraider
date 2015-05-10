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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.explorer.ExplorerJPanel;
import com.mindcognition.mindraider.utils.Utils;


public class UpdateOutlineJDialog extends AbstractOutlineJDialog {
    private static final Log logger = LogFactory.getLog(UpdateOutlineJDialog.class); // {{debug}}

    private ResourceDescriptor oldNotebookDescriptor;
    private HashMap<String,ResourceDescriptor> oldLabels=new HashMap<String,ResourceDescriptor>();
    
    public UpdateOutlineJDialog(ResourceDescriptor notebookDescriptor) {
        super("Rename or Change Outline Labels","Update");
        
        final KeyListener keyListener = new KeyListener() {
            public void keyPressed(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    updateNotebook();
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
        
        this.oldNotebookDescriptor=notebookDescriptor;
        outlineTitle.setText(notebookDescriptor.getLabel());
        outlineTitle.addKeyListener(keyListener);
        outlineUri.setText(notebookDescriptor.getUri());
        outlineNcName.setText(notebookDescriptor.getUri().substring(notebookDescriptor.getUri().indexOf('#')+1));

        // initialize categories
        ResourceDescriptor[] notebookFolders = MindRaider.labelCustodian.getNotebookFolders(notebookDescriptor.getUri());
        outlineLabels.setText("");
        outlineLabels.addKeyListener(keyListener);
        if(notebookFolders!=null && notebookFolders.length>0) {
            StringBuffer labelBuffer=new StringBuffer();
            for(ResourceDescriptor label: notebookFolders) {
                oldLabels.put(label.getLabel(),label);
                labelBuffer.append(label.getLabel());
                labelBuffer.append(", ");
            }
            if(labelBuffer.length()>0) {
                outlineLabels.setText(labelBuffer.toString().substring(0,labelBuffer.length()-2));
            }
        }
        
        // buttons
        doButton.addActionListener(UpdateNotebookActionListener());
        cancelButton.addActionListener(AbstractNotebookCancelActionListener());        
        
        // show
        pack();
        Gfx.centerAndShowWindow(this);
    }

    protected void updateNotebook() {
        // update name
        if ("".equals(outlineTitle.getText())) {
            JOptionPane.showMessageDialog(
                            this,
                            Messages.getString("NewNotebookJDialog.notebookNameCannotBeEmpty"),
                            Messages.getString("NewNotebookJDialog.notebookCreationError"),
                            JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            if(!oldNotebookDescriptor.getLabel().equals(outlineTitle.getText())) {
                try {
                    MindRaider.labelCustodian.renameNotebook(oldNotebookDescriptor.getUri(), outlineTitle.getText());
                } catch (Exception e) {
                    logger.error("Unable to rename notebook",e);
                }                
            }                        
        }

        // update labels
        try {
            // notebook URI can not be changed
            
            // process labels - create new for every unknown
            String text=outlineLabels.getText();
            if(text!=null && text.length()>0) {
                // method:
                // - put all old labels to the hashset
                // - on update:
                //  - remove from hashset tags which stayed unchanged
                //  - if tag remained in hashset, it must be deleted
                //  - if new tag is not in hashset, it must be created

                String[] labelsArray = text.split(",");
                for(String labelLabel: labelsArray) {
                    labelLabel=labelLabel.trim();
                    if(labelLabel!=null && labelLabel.length()>0) {
                        String labelNcName=Utils.toNcName(labelLabel);
                        logger.debug(" New label: "+labelLabel+" # "+labelNcName);

                        // remove labels which were not changed from the old labels -> in 
                        // hashset will remain just labels to be removed
                        if(oldLabels.containsKey(labelLabel)) {
                            oldLabels.remove(labelLabel);
                        } else {
                            // label must be created
                            String labelUri = MindRaiderVocabulary.getFolderUri(labelNcName);
                            if(!MindRaider.labelCustodian.exists(labelUri)) {
                                MindRaider.labelCustodian.create(labelLabel, labelUri);
                            }

                            MindRaider.labelCustodian.addOutline(labelUri, oldNotebookDescriptor.getUri());
                        }
                    }
                }
            } else {
                // create "all" label, if notebook has no labels
                String allLabelUri = MindRaiderVocabulary.getFolderUri("all");
                if(!MindRaider.labelCustodian.exists(allLabelUri)) {
                    MindRaider.labelCustodian.create("all", allLabelUri);
                }
                MindRaider.labelCustodian.addOutline(allLabelUri, oldNotebookDescriptor.getUri());
            }
            
            // remove notebook from the folders where it no longer is
            if(oldLabels.size()>0) {
                Iterator<ResourceDescriptor> remainingOldLabels=oldLabels.values().iterator();
                while (remainingOldLabels.hasNext()) {
                    ResourceDescriptor labelDescriptor = remainingOldLabels.next();
                    // remove
                    MindRaider.labelCustodian.discardOutlineFromLabel(labelDescriptor.getUri(), oldNotebookDescriptor.getUri(), false);
                }
            }
                                    
            ExplorerJPanel.getInstance().refresh();
        } catch (Exception e) {
            logger.error("updateNotebook()", e);
            JOptionPane.showMessageDialog(this, Messages
                    .getString("NewNotebookJDialog.notebookCreationError"),
                    Messages.getString(
                            "NewNotebookJDialog.unableToCreateNotebook", e
                                    .getMessage()), JOptionPane.ERROR_MESSAGE);
        }
        UpdateOutlineJDialog.this.dispose();
    }

    protected ActionListener UpdateNotebookActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UpdateOutlineJDialog.this.updateNotebook();
            }
        };
    }
    
    private static final long serialVersionUID = 1074558300423380114L;
}
