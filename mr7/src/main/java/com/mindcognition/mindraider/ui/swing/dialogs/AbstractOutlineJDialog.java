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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.mindcognition.mindraider.l10n.Messages;


abstract public class AbstractOutlineJDialog extends ProgramIconJDialog {
    protected static final int TEXTFIELD_WIDTH = 39;

    protected JTextField outlineTitle;
    protected JTextField outlineNcName;
    protected JTextField outlineUri;
    protected JTextField outlineLabels;
    
    protected JButton doButton;
    protected JButton cancelButton;
    
    public AbstractOutlineJDialog(String title, String action) {
        super(title);
        
        JPanel framePanel = new JPanel();
        framePanel.setLayout(new BorderLayout());

        // notebook name
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 1));
        p.setBorder(new TitledBorder(Messages
                .getString("NewNotebookJDialog.basic")));
        JPanel pp = new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pp.add(new JLabel(Messages.getString("NewNotebookJDialog.label")));
        outlineTitle = new JTextField(TEXTFIELD_WIDTH);
        pp.add(outlineTitle);
        p.add(pp);
        
        pp=new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        // TODO bundle
        pp.add(new JLabel("Labels:"));
        outlineLabels = new JTextField(TEXTFIELD_WIDTH);
        outlineLabels.setToolTipText("Comma separated list of labels");
        pp.add(outlineLabels);
        p.add(pp);
        
        framePanel.add(p, BorderLayout.NORTH);

        // advanced
        p = new JPanel();
        p.setLayout(new GridLayout(2, 1));
        p.setBorder(new TitledBorder(Messages
                .getString("NewNotebookJDialog.advanced")));
        pp = new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pp.add(new JLabel(Messages.getString("NewNotebookJDialog.ncName")));
        outlineNcName = new JTextField(TEXTFIELD_WIDTH);
        outlineNcName.setEnabled(false);
        pp.add(outlineNcName);
        p.add(pp);

        pp = new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pp.add(new JLabel(Messages.getString("NewNotebookJDialog.uri")));
        outlineUri = new JTextField(TEXTFIELD_WIDTH);
        outlineUri.setText(MindRaiderVocabulary.getNotebookUriSkeleton());
        outlineUri.setEnabled(false);
        pp.add(outlineUri);
        p.add(pp);
        framePanel.add(p, BorderLayout.CENTER);
        
        // buttons
        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        doButton = new JButton(action);
        p.add(doButton);
        cancelButton = new JButton(Messages.getString("NewNotebookJDialog.cancel"));
        p.add(cancelButton);
        framePanel.add(p, BorderLayout.SOUTH);
        getContentPane().add(framePanel, BorderLayout.CENTER);
    }
    

    /**
     * Getter for <code>notebookNcName</code>.
     * 
     * @return Returns the notebookNcName.
     */
    public JTextField getNotebookNcName() {
        return this.outlineNcName;
    }

    /**
     * Setter for <code>notebookNcName</code>.
     * 
     * @param notebookNcName
     *            The notebookNcName to set.
     */
    public void setNotebookNcName(JTextField notebookNcName) {
        this.outlineNcName = notebookNcName;
    }

    /**
     * Getter for <code>outlineLabel</code>.
     * 
     * @return Returns the outlineLabel.
     */
    public JTextField getNotebookLabel() {
        return this.outlineTitle;
    }

    /**
     * Setter for <code>outlineLabel</code>.
     * 
     * @param outlineLabel
     *            The outlineLabel to set.
     */
    public void setNotebookLabel(JTextField notebookLabel) {
        this.outlineTitle = notebookLabel;
    }

    /**
     * Getter for <code>notebookUri</code>.
     * 
     * @return Returns the notebookUri.
     */
    public JTextField getNotebookUri() {
        return this.outlineUri;
    }

    /**
     * Setter for <code>notebookUri</code>.
     * 
     * @param notebookUri
     *            The notebookUri to set.
     */
    public void setNotebookUri(JTextField notebookUri) {
        this.outlineUri = notebookUri;
    }
    
    protected ActionListener AbstractNotebookCancelActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AbstractOutlineJDialog.this.dispose();
            }
        };
    }
        
    private static final long serialVersionUID = 2633154230361071281L;        
}
