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
package com.mindcognition.mindraider.ui.swing.concept.annotation.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.emental.mindraider.ui.dialogs.SearchConceptAnnotation;
import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.emental.mindraider.ui.panels.bars.NoteAnnotationsToolbar;
import com.mindcognition.mindraider.ui.swing.concept.annotation.renderer.AbstractTextAnnotationRenderer;

public class TextAnnotationToolbar extends NoteAnnotationsToolbar {

    AbstractTextAnnotationRenderer renderer;
    private JButton undoButton;
    private JButton redoButton;
    private JButton searchButton;
    private JButton toggleEditButton;
    private JButton discardButton;
    private JButton pickButton;
    
    public TextAnnotationToolbar(AbstractTextAnnotationRenderer renderer) {
        super();
        
        this.renderer=renderer;
        
        buildToggleEditViewMode();
        buildLinking(renderer);
        buildSearch();
        
        addSeparator();
        
        buildUndo();
        buildRedo();
        
        addSeparator();
        
        buildToggleAttachments(renderer.getAttachmentsJPanel());
        
        addSeparator();
        
        buildPick();
        buildSave(renderer.getConceptPanel());
        buildDiscardConcept();
    }

    public void refreshButtons() {
        searchButton.setEnabled(true);
        undoButton.setEnabled(true);
        redoButton.setEnabled(true);
        saveButton.setEnabled(true);
        toggleEditButton.setEnabled(true);
        discardButton.setEnabled(true);
        pickButton.setEnabled(true);
        
        if(renderer.getUndoManager().canRedo()) {
            redoButton.setEnabled(true);
        } else {
            redoButton.setEnabled(false);
        }
        if(renderer.getUndoManager().canUndo()) {
            undoButton.setEnabled(true);
        } else {
            undoButton.setEnabled(false);
        }
    }

    private void buildSearch() {
        searchButton=new JButton("",IconsRegistry.getImageIcon("searchConcept.png"));
        searchButton.setToolTipText("Search concept annottion [Ctrl-F], search again [Ctrl-G]");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SearchConceptAnnotation(renderer);            
            }
        });
        add(searchButton);
        searchButton.setEnabled(false);
    }
    
    private void buildUndo() {
        undoButton=new JButton("",IconsRegistry.getImageIcon("undo.png"));
        undoButton.setToolTipText("Undo");
        undoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                renderer.getUndoManager().undo();
                refreshButtons();
            }
        });
        add(undoButton);
        undoButton.setEnabled(false);
    }
    
    private void buildRedo() {
        redoButton=new JButton("",IconsRegistry.getImageIcon("redo.png"));
        redoButton.setToolTipText("Redo");
        redoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                renderer.getUndoManager().redo();
                refreshButtons();
            }
        });
        add(redoButton);
        redoButton.setEnabled(false);
    }
    
    private void buildToggleEditViewMode() {
        toggleEditButton=new JButton("",IconsRegistry.getImageIcon("middleText.png"));
        toggleEditButton.setToolTipText("Toggle edit/view mode [Ctrl-E or ESC or mouse double-click]");
        toggleEditButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                renderer.toggleEditViewMode();
            }
        });
        add(toggleEditButton);
        toggleEditButton.setEnabled(false);
    }
    
    private void buildDiscardConcept() {
        discardButton = new JButton("", IconsRegistry.getImageIcon("explorerDiscardSmall.png"));
        discardButton.setToolTipText("Discard");
        discardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectConcept();
                OutlineJPanel.getInstance().conceptDiscard();
            }
        });
        add(discardButton);
        discardButton.setEnabled(false);
    }
    
    private void buildPick() {
        pickButton = new JButton("", IconsRegistry.getImageIcon("picker.png"));
        pickButton.setToolTipText("Select Concept in Outline and Mind Map");
        pickButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectConcept();
            }
        });
        add(pickButton);
        pickButton.setEnabled(false);
    }
    
    private void selectConcept() {
        ConceptResource conceptResource = OutlineJPanel.getInstance().conceptJPanel.getConceptResource();
        if(conceptResource!=null) {
            MindRaider.spidersGraph.selectNodeByUri(conceptResource.getUri());                    
        }
    }

    private static final long serialVersionUID = 7252642845861341726L;
}
