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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.ui.swing.dialogs.NewOutlineJDialog;
import com.mindcognition.mindraider.ui.swing.dialogs.UpdateOutlineJDialog;

public class NotebooksTreeToolbar extends JToolBar {

    private JButton newButton, discardButton, updateButton;
    
    public NotebooksTreeToolbar(ExplorerJPanel explorerJPanel) {
        // add
        newButton = new JButton("", IconsRegistry.getImageIcon("add.png"));
        // TODO bundle
        newButton.setToolTipText("New Outline");
        newButton.addActionListener(new NewNotebookActionListener(explorerJPanel));
        add(newButton);
        
        // update
        updateButton = new JButton("", IconsRegistry.getImageIcon("properties.png"));
        // TODO bundle
        updateButton.setToolTipText("Rename or Change Outline Labels");
        updateButton.addActionListener(new UpdateNotebookActionListener(explorerJPanel));
        updateButton.setEnabled(false);
        add(updateButton);
        
        // remove
        discardButton = new JButton("", IconsRegistry.getImageIcon("explorerDiscardSmall.png"));
        // TODO bundle
        discardButton.setToolTipText("Move Outline to Archive");
        discardButton.addActionListener(new DiscardNotebookActionListener(explorerJPanel));
        discardButton.setEnabled(false);
        add(discardButton);

        setFloatable(false);
    }
        
    /**
     * It enable or disable the toolab buttons.
     *
     * @param level
     *            the level
     * @param node
     *            the mutable tree node
     */
    public void enableDisableToolbarButtons(int level) {
        // buttons disabling
        switch (level) {
        case NotebooksTree.LEVEL_ROOT:
            newButton.setEnabled(true);
            discardButton.setEnabled(false);
            updateButton.setEnabled(false);
            break;
        case NotebooksTree.LEVEL_NOTEBOOKS:
            newButton.setEnabled(true);
            discardButton.setEnabled(true);
            updateButton.setEnabled(true);
            break;
        default:
            break;
        }
    }
    
    private static final long serialVersionUID = 6731081573866364052L;
}

class DiscardNotebookActionListener implements ActionListener {
    private ExplorerJPanel explorerJPanel;

    public DiscardNotebookActionListener(ExplorerJPanel explorerJPanel) {
        this.explorerJPanel=explorerJPanel;
    }
    
    public void actionPerformed(ActionEvent e) {
        // find notebook through node's user object URI, discard notebook (move it to the archive), 
        // tags are preserved until notebook is erased

        DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode)explorerJPanel.getNotebooksTree().getLastSelectedPathComponent();
        if(selectedNode.getDepth()==0) {
            // it is leaf node - a notebook

            final String notebookUri = ((NotebookNodeUserObject)selectedNode.getUserObject()).getNotebookUri();
            ResourceDescriptor resourceDescriptor 
            = MindRaider.labelCustodian.getOutlineDescriptor(notebookUri);
            if (JOptionPane
                    .showConfirmDialog(
                            MindRaider.mainJFrame,
                            "Do you really want to discard Outline '"
                            + (resourceDescriptor != null ? resourceDescriptor
                                    .getLabel()
                                    : notebookUri) + "'?",
                                    "Archive Outline",
                                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                
                // discard the notebook from ALL folders where it presents
                MindRaider.labelCustodian.discardOutline(notebookUri);
                explorerJPanel.refresh();
                OutlineJPanel.getInstance().clear();
                MindRaider.spidersGraph.clear();
                MindRaider.profile.setActiveOutlineUri(null);
                return;
            } 
        } else {
            JOptionPane.showMessageDialog(MindRaider.mainJFrame, "Please select Outline to be discarded!");
        }
    }
}

class NewNotebookActionListener implements ActionListener {
    private static final Log logger = LogFactory.getLog(NewNotebookActionListener.class); // {{debug}}

    private ExplorerJPanel explorerJPanel;

    public NewNotebookActionListener(ExplorerJPanel explorerJPanel) {
        this.explorerJPanel=explorerJPanel;
    }
    
    public void actionPerformed(ActionEvent e) {
        logger.debug("Going to create Outline...");
        new NewOutlineJDialog();
        explorerJPanel.refresh();
    }
}

class UpdateNotebookActionListener implements ActionListener {
    private static final Log logger = LogFactory.getLog(UpdateNotebookActionListener.class); // {{debug}}

    private ExplorerJPanel explorerJPanel;

    public UpdateNotebookActionListener(ExplorerJPanel explorerJPanel) {
        this.explorerJPanel=explorerJPanel;
    }
    
    public void actionPerformed(ActionEvent e) {
        logger.debug("Going to update Outline...");
        
        DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode)explorerJPanel.getNotebooksTree().getLastSelectedPathComponent();
        if(selectedNode.getDepth()==0) {
            // it is leaf node - a notebook

            final String notebookUri = ((NotebookNodeUserObject)selectedNode.getUserObject()).getNotebookUri();
            ResourceDescriptor resourceDescriptor 
                = MindRaider.labelCustodian.getOutlineDescriptor(notebookUri);
            
            new UpdateOutlineJDialog(resourceDescriptor);
            explorerJPanel.refresh();
                return;
        } else {
            JOptionPane.showMessageDialog(MindRaider.mainJFrame, "Please select Outline to be updated!");
        }
    }
}
