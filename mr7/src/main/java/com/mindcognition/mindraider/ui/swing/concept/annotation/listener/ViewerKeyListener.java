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
package com.mindcognition.mindraider.ui.swing.concept.annotation.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.emental.mindraider.ui.dialogs.SearchConceptAnnotation;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.concept.ConceptJPanel;
import com.mindcognition.mindraider.ui.swing.concept.annotation.renderer.AbstractTextAnnotationRenderer;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;

public class ViewerKeyListener implements KeyListener {

    private ConceptJPanel conceptJPanel; 
    private AbstractTextAnnotationRenderer renderer;
    
    public ViewerKeyListener(ConceptJPanel conceptJPanel, AbstractTextAnnotationRenderer renderer) {
        this.conceptJPanel=conceptJPanel;
        this.renderer=renderer;
    }
    
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_S && keyEvent.isControlDown()) {
            conceptJPanel.save();
            // TODO inefficient: just its annotation can be refreshed
            OutlineJPanel.getInstance().refresh();
            // TODO inefficient: preview only if not in jarnal mode
            conceptJPanel.refreshPreviewPane();

            StatusBar.show(Messages.getString("ConceptJPanel.conceptAnnotationSaved"));
            return;
        }

        if (keyEvent.getKeyCode() == KeyEvent.VK_F && keyEvent.isControlDown()) {
            new SearchConceptAnnotation(renderer);            
        }
        if (keyEvent.getKeyCode() == KeyEvent.VK_G && keyEvent.isControlDown()) {
            renderer.searchConceptAnnotationAgain();
        }
        
        if (keyEvent.getKeyCode() == KeyEvent.VK_E && keyEvent.isControlDown() || keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
            renderer.toEditMode();
        }
    }
    public void keyReleased(KeyEvent keyEvent) {
    }
    public void keyTyped(KeyEvent keyEvent) {
    }
}
