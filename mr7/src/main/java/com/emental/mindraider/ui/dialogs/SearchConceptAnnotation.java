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
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang.StringUtils;

import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.concept.annotation.renderer.AbstractTextAnnotationRenderer;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;

public class SearchConceptAnnotation extends ProgramIconJDialog {

    /**
     * history of searches - transient, kept just for this MR run
     */
    private static ArrayList<String> history;
    static {
        history=new ArrayList<String>();
        history.add("");
        
        lastSearchString="";
    }
    private static String lastSearchString;
    
    private JComboBox searchCombo;
    private AbstractTextAnnotationRenderer renderer;

    /**
     * Constructor.
     */
    public SearchConceptAnnotation(AbstractTextAnnotationRenderer renderer) {
        super("Search Annotation");

        this.renderer=renderer;
        
        JPanel dialogPanel = new JPanel();
        dialogPanel.setBorder(new EmptyBorder(5, 10, 0, 10));
        dialogPanel.setLayout(new BorderLayout());
        
        JPanel contentAndButtons = new JPanel(new GridLayout(2,1));
        JPanel contentPanel= new JPanel(new BorderLayout());

        // 1a.
        // TODO add help like in eclipse
        contentPanel.add(new JLabel(Messages.getString("FtsJDialog.searchString")),BorderLayout.NORTH);
        // 1b.
        searchCombo = new JComboBox(history.toArray());
        searchCombo.setSelectedItem("");
        searchCombo.setPreferredSize(new Dimension(200, 18));
        searchCombo.setEditable(true);
        searchCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ("comboBoxEdited".equals(e.getActionCommand())) {
                    search();
                }
            }
        });
        contentPanel.add(searchCombo,BorderLayout.SOUTH);
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
        lastSearchString = (String) searchCombo.getSelectedItem();
        if (StringUtils.isEmpty(lastSearchString)) {
            StatusBar.show(Messages
                    .getString("FtsJDialog.searchStringEmpty"),
                    Color.RED);
        } else {
            history.add(0,lastSearchString);
            StatusBar.show(Messages.getString("FtsJDialog.found", lastSearchString));            
            renderer.searchAnnotation(lastSearchString,false);
            
            dispose();
        }
    }
    
    public static String getLastSearchString() {
        return lastSearchString;
    }
    
    private static final long serialVersionUID = -1531791348699056550L;
}
