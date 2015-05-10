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
package com.mindcognition.mindraider.ui.swing.concept.annotation.renderer;

import java.awt.Color;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;

import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.mindcognition.mindraider.MindRaiderException;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.ui.swing.concept.annotation.listener.EditorAndViewerMouseListener;
import com.mindcognition.mindraider.ui.swing.concept.annotation.listener.EditorFocusListener;
import com.mindcognition.mindraider.ui.swing.concept.annotation.listener.EditorKeyListener;
import com.mindcognition.mindraider.ui.swing.concept.annotation.listener.ViewerKeyListener;
import com.mindcognition.mindraider.ui.swing.concept.annotation.toolbar.TextAnnotationToolbar;
import com.mindcognition.mindraider.ui.swing.concept.annotation.transformer.RichTextToHtmlTransformer;

public class RichTextAnnotationRenderer extends AbstractTextAnnotationRenderer {
    private static final Log logger = LogFactory.getLog(RichTextAnnotationRenderer.class); // {{debug}}

    /**
     * the label text constant.
     * TODO consider bundle
     */
    public static final String LABEL_TEXT = "Rich Text";

    public RichTextAnnotationRenderer() {
    }

    public void init() {
        EditorAndViewerMouseListener editorAndViewerMouseListener 
            = new EditorAndViewerMouseListener(this);
        RichTextEditorKeyListener richTextEditorKeyListener 
            = new RichTextEditorKeyListener(new EditorKeyListener(getConceptPanel(),this), this);

        super.init(
                new RichTextToHtmlTransformer(),
                richTextEditorKeyListener,
                new ViewerKeyListener(getConceptPanel(),this),
                new EditorFocusListener(getConceptPanel()),
                (FocusListener)null,
                editorAndViewerMouseListener,
                editorAndViewerMouseListener,
                new TextAnnotationToolbar(this));
    }
    
    public void openConceptAnnotation(ConceptResource conceptResource) {
        logger.debug("openConceptAnnotation() "+conceptResource.getAnnotationContentType() +" # "+getAnnotationTypeOwlClass());
        setConceptResource(conceptResource);
        
        // verify content type
        if(getAnnotationTypeOwlClass().equals(conceptResource.getAnnotationContentType())) {
            super.openConceptAnnotation(conceptResource);            
        } else {
            throw new MindRaiderException("Invalid annotation content type - it's not: "+getAnnotationTypeOwlClass());
        }
    }
    
    @Override
    public String getAnnotationTypeLabel() {
        return LABEL_TEXT;
    }

    @Override
    public String getAnnotationTypeOwlClass() {
        return MindRaiderConstants.MR_OWL_CONTENT_TYPE_RICH_TEXT;
    }

    @Override
    public Color getBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    public Color getForegroundColor() {
        return Color.BLACK;
    }

    private static final long serialVersionUID = -761668031392375462L;
    
}

class RichTextEditorKeyListener implements KeyListener {
 
    private KeyListener genericEditorKeyListener;
    private RichTextAnnotationRenderer richTextAnnotationRenderer;

    public RichTextEditorKeyListener(KeyListener genericEditorKeyListener, RichTextAnnotationRenderer richTextAnnotationRenderer) {
        this.genericEditorKeyListener=genericEditorKeyListener;
        this.richTextAnnotationRenderer=richTextAnnotationRenderer;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            insertStringToAnnotation(new Date(System.currentTimeMillis()).toLocaleString());
        }    
        if (e.getKeyCode() == KeyEvent.VK_F9) {
            insertStringToAnnotation("-----------------------------------------------------");
        }    
        if (e.getKeyCode() == KeyEvent.VK_F12) {
            insertStringToAnnotation("- eof -");
        }    
        
        genericEditorKeyListener.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        genericEditorKeyListener.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        genericEditorKeyListener.keyTyped(e);
    }    

    private void insertStringToAnnotation(String stringToInsert) {
        JTextArea editor=richTextAnnotationRenderer.editor;
        try {            
            final int caretPosition = editor.getCaretPosition();
            // link & htmlize
            String linked =
                editor.getDocument().getText(0, caretPosition)+
                stringToInsert+
                editor.getDocument().getText(caretPosition,editor.getDocument().getLength()-caretPosition);
            linked = linked.trim();
            // set to UI again
            editor.setText(linked);
            editor.setCaretPosition(caretPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}