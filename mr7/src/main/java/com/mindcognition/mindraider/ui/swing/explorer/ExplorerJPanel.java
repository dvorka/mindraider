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
package com.mindcognition.mindraider.ui.swing.explorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.recent.RecentConceptsTree;

/**
 * Explorer of labels, notebooks and recent concentps.
 */
public final class ExplorerJPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(ExplorerJPanel.class);

    /**
     * The singleton explorer panel.
     */
    private static ExplorerJPanel singleton;

    private LabelsTree labelsTree;
    private NotebooksTree notebooksTree;
    private RecentConceptsTree recentTree;

    private NotebooksTreeToolbar notebooksToolbar;

    /**
     * Constructor.
     */
    private ExplorerJPanel() {
        logger.debug("Assebling Explorer JPanel..."); // {{debug}}
        
        notebooksTree=new NotebooksTree();
        labelsTree=new LabelsTree(notebooksTree);
        recentTree=new RecentConceptsTree();
        
        setLayout(new BorderLayout());

        /*
         * Splits: labels / notebooks / recent concepts
         */
        
        // 0) filter & toolbar
        JPanel filterAndToolPanel=new JPanel(new BorderLayout());
        
        // . filter
        JPanel filterPanel= new JPanel(new BorderLayout());
        filterPanel.add(new JLabel(" "+Messages.getString("ExplorerJPanel.filter")+": "),BorderLayout.WEST);
        JTextField filterTextField = new JTextField();
        filterTextField.addKeyListener(new FilterKeyListener(labelsTree,notebooksTree,filterTextField));
        filterTextField.addFocusListener(new FilterFocusListener(labelsTree,notebooksTree,filterTextField));
        // crazy: top padding/filter/bottom padding
        JPanel topPadding = new JPanel(); topPadding.setPreferredSize(new Dimension(10,2));
        filterPanel.add(topPadding,BorderLayout.NORTH);
        filterPanel.add(filterTextField,BorderLayout.CENTER);
        JPanel bottomPadding = new JPanel(); bottomPadding.setPreferredSize(new Dimension(10,2));
        filterPanel.add(bottomPadding,BorderLayout.SOUTH);
        filterAndToolPanel.add(filterPanel,BorderLayout.CENTER);
        
        // . toolbar
        notebooksToolbar = new NotebooksTreeToolbar(this);
        filterAndToolPanel.add(notebooksToolbar,BorderLayout.EAST);

        // 1) labels split (+ filter text field)
        JPanel tagsPanel=new JPanel(new BorderLayout());
        tagsPanel.add(filterAndToolPanel,BorderLayout.NORTH);

        // 2) notebooks (+ toolbar to create notebook / modify notebook metadata)
        JPanel notebooksPanel=new JPanel(new BorderLayout());
        notebooksTree.setToolbar(notebooksToolbar);
        JScrollPane notebooksScroll = new JScrollPane(notebooksTree);
        notebooksPanel.add(notebooksScroll, BorderLayout.CENTER);
        
        tagsPanel.add(notebooksPanel,BorderLayout.CENTER);
        
        // labels
        JScrollPane labelsScroll = new JScrollPane(labelsTree);
        //tagsPanel.add(labelsScroll,BorderLayout.CENTER);
                        
        // SPLIT: outlines and labels
        JSplitPane tagsAndNotebooksSplit= new JSplitPane(JSplitPane.VERTICAL_SPLIT, tagsPanel, labelsScroll);
        tagsAndNotebooksSplit.setOneTouchExpandable(true);
        tagsAndNotebooksSplit.setDividerLocation(250);
        tagsAndNotebooksSplit.setDividerSize(6);
        tagsAndNotebooksSplit.setContinuousLayout(true);

        // 3) recent
        recentTree = new RecentConceptsTree();
        JScrollPane recentScroll = new JScrollPane(recentTree);
        JPanel recentPanel = new JPanel(new BorderLayout());
        recentPanel.add(recentScroll,BorderLayout.CENTER);
        
        // SPLIT: tags+notebooks split and recent
        JSplitPane tagsNotebooksAndRecentSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tagsAndNotebooksSplit, recentPanel);
        tagsNotebooksAndRecentSplit.setOneTouchExpandable(true);
        tagsNotebooksAndRecentSplit.setDividerLocation(470);
        tagsNotebooksAndRecentSplit.setDividerSize(6);
        tagsNotebooksAndRecentSplit.setContinuousLayout(true);
        
        add(tagsNotebooksAndRecentSplit, BorderLayout.CENTER);
        
        validateTree();     
    }

    public static ExplorerJPanel getInstance() {
        if (singleton == null) {
            singleton = new ExplorerJPanel();
        }
        return singleton;
    }

    public void refresh() {
        // model in trash/explorer has changed - redraw all the subcomponents (should load
        // the data from the model (add and remove all nodes)
        
        labelsTree.reloadModel();
        notebooksTree.reloadModel();
    }

    public LabelsTree getLabelsTree() {
        return labelsTree;
    }

    public NotebooksTree getNotebooksTree() {
        return notebooksTree;
    }

    public RecentConceptsTree getRecentTree() {
        return recentTree;
    }
    
    private static final long serialVersionUID = 7453553312716270523L;
}

class FilterKeyListener implements KeyListener {
    
    private LabelsTree labelsTree;
    private NotebooksTree notebooksTree;
    private JTextField filterTextField;
    
    public FilterKeyListener(LabelsTree labelsTree, NotebooksTree notebooksTree, JTextField filterTextField) {
        this.labelsTree=labelsTree;
        this.notebooksTree=notebooksTree;
        this.filterTextField=filterTextField;
    }
    
    public void keyReleased(KeyEvent keyEvent) {
        // 1) clear the tree
        labelsTree.clear();
        
        if(filterTextField.getText()!=null && filterTextField.getText().length()>0) {
            labelsTree.reloadModel(filterTextField.getText());
            notebooksTree.reloadFilteredModel(filterTextField.getText());
        } else {
            labelsTree.reloadModel();
            notebooksTree.reloadModel();
        }
    }

    public void keyPressed(KeyEvent keyEvent) {
    }
    public void keyTyped(KeyEvent keyEvent) {
    }
}

class FilterFocusListener implements FocusListener {

    private LabelsTree labelsTree;
    private NotebooksTree notebooksTree;
    private JTextField filterTextField;
    
    public FilterFocusListener(LabelsTree labelsTree, NotebooksTree notebooksTree, JTextField filterTextField) {
        this.labelsTree=labelsTree;
        this.notebooksTree=notebooksTree;
        this.filterTextField=filterTextField;
    }
    
    public void focusGained(FocusEvent event) {
        // on gaining focus reinitialize the trees (no filtering)
        labelsTree.reloadModel();
        notebooksTree.reloadModel();
        filterTextField.setText("");
    }

    public void focusLost(FocusEvent event) {
    }
}