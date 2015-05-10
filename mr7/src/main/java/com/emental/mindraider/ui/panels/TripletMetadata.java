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
package com.emental.mindraider.ui.panels;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * TripletMetadata.java
 * 
 * @author Martin.Dvorak
 */
public class TripletMetadata extends JPanel {

    /**
     * Serial versoin UID.
     */
    private static final long serialVersionUID = 1L;

    private JPanel notebookPanel, nodePanel, modelPanel, p, gp;

    private JTextField nodeLabel, nodeId, notebookName, modelLocation;

    public TripletMetadata() {
        setLayout(new GridLayout(3, 1));

        // notebook
        notebookPanel = new JPanel();
        notebookPanel.setBorder(new TitledBorder(" Notebook "));
        gp = new JPanel();
        gp.setLayout(new GridLayout(2, 1));
        notebookPanel.add(gp);

        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.RIGHT));
        p.add(new JLabel("Name: "));
        notebookName = new JTextField(30);
        p.add(notebookName);
        gp.add(p);

        add(notebookPanel);

        // model
        modelPanel = new JPanel();
        modelPanel.setBorder(new TitledBorder(" Model "));
        gp = new JPanel();
        gp.setLayout(new GridLayout(2, 1));
        modelPanel.add(gp);

        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.RIGHT));
        p.add(new JLabel("Location: "));
        modelLocation = new JTextField(30);
        p.add(modelLocation);
        gp.add(p);

        add(modelPanel);

        // node
        nodePanel = new JPanel();
        nodePanel.setBorder(new TitledBorder(" Node "));
        gp = new JPanel();
        gp.setLayout(new GridLayout(2, 1));
        nodePanel.add(gp);

        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.RIGHT));
        p.add(new JLabel("Label: "));
        nodeLabel = new JTextField(30);
        p.add(nodeLabel);
        gp.add(p);

        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.RIGHT));
        p.add(new JLabel("ID: "));
        nodeId = new JTextField(30);
        p.add(nodeId);
        gp.add(p);

        add(nodePanel);
    }
}
