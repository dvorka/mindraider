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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.explorer.ExplorerJPanel;
import com.mindcognition.mindraider.ui.swing.trash.TrashJPanel;
import com.mindcognition.mindraider.utils.Utils;

public class RestoreNotebookJDialog extends ProgramIconJDialog {
    private static final Logger logger = Logger.getLogger(RestoreNotebookJDialog.class);

    private JTextField notebookLabels;

    /**
     * The notebook Uri.
     */
    private String notebookUri;

    /**
     * Constructor.
     *
     * @param notebookUri
     *            The notebook uri.
     * @param dialogLabel
     *            The dialog label.
     * @param restoreButtonLabel
     *            The restore button label.
     * @param showCancelButton
     *            The show cancel button option.
     * @throws HeadlessException
     *             The headless exception
     */
    public RestoreNotebookJDialog(
            String notebookUri, 
            String dialogLabel,
            String restoreButtonLabel, 
            boolean showCancelButton) throws HeadlessException {
        super(dialogLabel);

        this.notebookUri = notebookUri;

        JPanel framePanel = new JPanel();
        framePanel.setLayout(new GridLayout(2, 1));

        JPanel pp=new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        // TODO bundle
        pp.add(new JLabel("Labels:"));
        notebookLabels = new JTextField(AbstractOutlineJDialog.TEXTFIELD_WIDTH);
        notebookLabels.setToolTipText("Comma separated list of labels");
        pp.add(notebookLabels);
        framePanel.add(pp);
        
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton openButton = new JButton(restoreButtonLabel);
        p.add(openButton);
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // determine tags
                //  - if there are no tags, then restore to the "all" tag
                //  - otherwise put to all
                
                // process labels - create new for every unknown
                String text=notebookLabels.getText();
                ArrayList<String> restoreToLabels=new ArrayList<String>();
                if(text!=null && text.length()>0) {
                    String[] labelsArray = text.split(",");
                    for(String labelLabel: labelsArray) {
                        labelLabel=labelLabel.trim();
                        if(labelLabel!=null && labelLabel.length()>0) {
                            String labelNcName=Utils.toNcName(labelLabel);
                            logger.debug(" New label: "+labelLabel+" # "+labelNcName);
                            String labelUri = MindRaiderVocabulary.getFolderUri(labelNcName);
                            if(!MindRaider.labelCustodian.exists(labelUri)) {
                                MindRaider.labelCustodian.create(labelLabel, labelUri);
                            }
                            restoreToLabels.add(labelUri);
                        }
                    }
                    // take the first label and restore notebook to it
                    MindRaider.labelCustodian.restoreNotebook(restoreToLabels.get(0),getNotebookUri());
                    
                    // add notebook to others
                    if(restoreToLabels.size()>1) {
                        for (int i = 1; i < restoreToLabels.size(); i++) {
                            try {
                                MindRaider.labelCustodian.addOutline(restoreToLabels.get(i), getNotebookUri());
                            } catch (Exception ex) {
                                logger.error("Unable to tag notebook on restore", ex);
                            }
                        }
                    }
                } else {
                    // create "all" label, if notebook has no labels
                    String allLabelUri = MindRaiderVocabulary.getFolderUri("all");
                    if(!MindRaider.labelCustodian.exists(allLabelUri)) {
                        MindRaider.labelCustodian.create("all", allLabelUri);
                    }
                    // take the first label and restore notebook to it
                    MindRaider.labelCustodian.restoreNotebook(restoreToLabels.get(0),getNotebookUri());
                }

                RestoreNotebookJDialog.this.dispose();
                TrashJPanel.getInstance().refresh();
                ExplorerJPanel.getInstance().refresh();

            }
        });

        if (showCancelButton) {
            JButton cancelButton = new JButton(Messages
                    .getString("RestoreNotebookJDialog.cancel"));
            p.add(cancelButton);
            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    RestoreNotebookJDialog.this.dispose();
                }
            });
        }
        framePanel.add(p);

        getContentPane().add(framePanel, BorderLayout.CENTER);

        // show
        pack();
        Gfx.centerAndShowWindow(this);
    }

    /**
     * Returns the current notebook uri.
     *
     * @return Returns the <code>notebookUri</code>.
     */
    protected String getNotebookUri() {
        return notebookUri;
    }

    /**
     * Setter for <code>notebookUri</code>.
     *
     * @param notebookUri
     *            The notebookUri to set.
     */
    public void setNotebookUri(String notebookUri) {
        this.notebookUri = notebookUri;
    }

    public JTextField getNotebookLabels() {
        return notebookLabels;
    }

    public void setNotebookLabels(JTextField notebookLabels) {
        this.notebookLabels = notebookLabels;
    }

    private static final long serialVersionUID = -7266941123841240217L;
}
