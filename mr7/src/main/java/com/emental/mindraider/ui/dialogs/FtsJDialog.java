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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang.StringUtils;

import com.emental.mindraider.core.search.SearchCommander;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;

public class FtsJDialog extends ProgramIconJDialog {

    private JComboBox ftsCombo;

    public FtsJDialog() {
        super(Messages.getString("FtsJDialog.title"));

        JPanel dialogPanel = new JPanel();
        dialogPanel.setBorder(new EmptyBorder(5, 10, 0, 10));
        dialogPanel.setLayout(new BorderLayout());
        
        JPanel contentAndButtons = new JPanel(new GridLayout(2,1));
        JPanel contentPanel= new JPanel(new BorderLayout());

        // 1a.
        // TODO add help like in eclipse
        contentPanel.add(new JLabel(Messages.getString("FtsJDialog.searchString")),BorderLayout.NORTH);
        // 1b.
        String[] knownSearches = new String[] { 
                "",
                "RDF",
                "mind",
                "concept",
                "China" };
        ftsCombo = new JComboBox(knownSearches);
        ftsCombo.setPreferredSize(new Dimension(200, 18));
        ftsCombo.setEditable(true);
        ftsCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ("comboBoxEdited".equals(e.getActionCommand())) {
                    search();
                }
            }
        });
        contentPanel.add(ftsCombo,BorderLayout.SOUTH);
        contentAndButtons.add(contentPanel);
        
        // 2.
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 5));
        JButton searchButton = new JButton(Messages
                .getString("FtsJDialog.searchButton"));
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        p.add(searchButton);

        JButton cancelButton = new JButton(Messages
                .getString("FtsJDialog.cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        p.add(cancelButton);
        
        contentAndButtons.add(p);
        
        dialogPanel.add(contentAndButtons,BorderLayout.CENTER);

        getContentPane().add(dialogPanel, BorderLayout.CENTER);

        // show
        pack();
        Gfx.centerAndShowWindow(this);
    }

    /**
     * Upload the model.
     */
    protected void search() {
        String searchString = (String) ftsCombo.getSelectedItem();
        if (StringUtils.isEmpty(searchString)) {
            StatusBar.show(Messages.getString("FtsJDialog.searchStringEmpty"),Color.RED);
        } else {
            dispose();
            StatusBar.show(Messages.getString("FtsJDialog.found", searchString));
            SearchCommander.searchNotebooks(searchString);
        }
    }
    
    private static final long serialVersionUID = 1L;
}
