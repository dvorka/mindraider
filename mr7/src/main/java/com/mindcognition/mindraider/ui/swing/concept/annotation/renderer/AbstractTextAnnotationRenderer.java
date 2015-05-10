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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.UndoManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.emental.mindraider.ui.dialogs.SearchConceptAnnotation;
import com.emental.mindraider.ui.listeners.TextAnnotationPreviewHyperlinkListener;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.concept.annotation.toolbar.TextAnnotationToolbar;
import com.mindcognition.mindraider.ui.swing.concept.annotation.transformer.AnnotationToHtmlTransformer;

public abstract class AbstractTextAnnotationRenderer extends AbstractAnnotationRenderer {
    private static final Log logger = LogFactory.getLog(AbstractTextAnnotationRenderer.class); // {{debug}}
    
    /**
     * default text area font
     */
    protected Font TEXTAREA_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    /**
     * annotation editor
     */
    protected JTextArea editor;
    private JScrollPane editorScroll;
    private UndoManager undoManager;
    
    /**
     * annotation viewer
     */
    protected JTextPane viewer;
    private HTMLEditorKit kit;
    private JScrollPane viewerScroll;

    private TextAnnotationToolbar toolbar;

    private AnnotationToHtmlTransformer editorToViewerTransfomer;
    
    protected void init(
            AnnotationToHtmlTransformer editorToViewerTransfomer,
            KeyListener editorKeyListener,
            KeyListener viewerKeyListener,
            FocusListener editorFocusListener,
            FocusListener viewerFocusListener,
            MouseListener editorMouseListener,
            MouseListener viewerMouseListener,
            TextAnnotationToolbar toolbar) {
        this.editorToViewerTransfomer=editorToViewerTransfomer;
        this.toolbar = toolbar;
        
        setLayout(new BorderLayout());
        
        JPanel editorViewerPanel=new JPanel();
        editorViewerPanel.setLayout(new BoxLayout(editorViewerPanel,BoxLayout.Y_AXIS));
        
        // editor
        editor = new JTextArea(30,100);        
        configureEditor(editor);
        editor.addKeyListener(editorKeyListener);
        editor.addFocusListener(editorFocusListener);
        editor.addMouseListener(editorMouseListener);
        // TODO bundle
        editorScroll = encapsulateToScroll(editor," Editor ");
        editorViewerPanel.add(editorScroll);
        editorScroll.setVisible(false);
        
        // view
        viewer = new JTextPane();
        configureViewer(viewer);
        viewer.addKeyListener(viewerKeyListener);
        viewer.addFocusListener(viewerFocusListener);
        viewer.addMouseListener(viewerMouseListener);
        // TODO bundle
        viewerScroll = encapsulateToScroll(viewer," Viewer ");
        editorViewerPanel.add(viewerScroll);
        
        add(editorViewerPanel,BorderLayout.CENTER);
        // toolbar
        if(toolbar!=null) {
            add(toolbar,BorderLayout.SOUTH);
        }
        
        // let the container to redraw its content (hide/show)
        validate();
    }
    
    private JScrollPane encapsulateToScroll(Component what, String title) {
        JScrollPane editorScroll = new JScrollPane(what);
        editorScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        editorScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // TODO bundle
        editorScroll.setBorder(new TitledBorder(title));
        return editorScroll;
    }

    private void configureViewer(JTextPane viewerPane) {        
            viewerPane.setContentType("text/html");
            kit = new HTMLEditorKit();
            viewerPane.setEditorKitForContentType("text/html", kit);            
            viewerPane.setEditable(false);                        
            disableViewer();
            
            setViewerText("");
            
            viewerPane.addHyperlinkListener(new TextAnnotationPreviewHyperlinkListener(getConceptPanel(),viewerPane));
    }

    protected void hideVieverCarret() {
        viewer.getCaret().setVisible(false);
    }
    
    protected void setViewerText(String text)  {
        try {
            text=editorToViewerTransfomer.toHtml(text);
            if(text!=null) {
                HTMLDocument document=(HTMLDocument)viewer.getStyledDocument();
                document.remove(0, document.getLength());
                kit.insertHTML(document,0,text,0,0,HTML.Tag.HTML);
            }
        } catch (Exception e) {
            logger.error("Unable to set viewer text: "+text,e); // {{debug}}
        }
        // scroll to upper left corner
        viewer.setCaretPosition(0);
    }

    public void insertTextOnCarretPosition(String textToInsert) {
        try {            
            final int caretPosition = editor.getCaretPosition();
            // link & htmlize
            String linked =
                editor.getDocument().getText(0, caretPosition)+
                textToInsert+
                editor.getDocument().getText(caretPosition,editor.getDocument().getLength()-caretPosition);
            linked = linked.trim();
            // set to UI again
            editor.setText(linked);
            editor.setCaretPosition(caretPosition);
        } catch (Exception e) {
            logger.error("Unable to insert text",e);
        }        
    }
    
    private int blinkRate;
    
    public void enableViewer() {
        viewer.setEditable(true);
        viewer.setCaretColor(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getEnabledCaretColor());
        viewer.getCaret().setBlinkRate(blinkRate);
        viewer.getCaret().setVisible(true);
        
        viewer.setSelectionColor(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getSelectionColor());
        viewer.setSelectedTextColor(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getSelectionTextColor());
    }

    private void disableViewer() {
        viewer.setBackground(new Color(0xff, 0xff, 0xff)); // f6f8ff        
        
        viewer.setEditable(false);
        // TODO use colors from the configuration
        viewer.setCaretColor(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getBackroundColor());
        viewer.getCaret().setBlinkRate(0);
        viewer.setCaretColor(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getDisabledCaretColor());
        viewer.getCaret().setVisible(true);
    }
    
    
    private void configureEditor(JTextArea annotationTextArea) {
        // TODO use colors from the configuration
        annotationTextArea.setForeground(Color.WHITE);
        annotationTextArea.setBackground(Color.BLACK);
        annotationTextArea.setCaretColor(Color.RED);
        annotationTextArea.setSelectionColor(Color.YELLOW);
        annotationTextArea.setLineWrap(true);
        annotationTextArea.setWrapStyleWord(true);
        annotationTextArea.setFont(TEXTAREA_FONT);;
        
        // undo and redo
        undoManager = new UndoManager();
        annotationTextArea.getDocument().addUndoableEditListener(new EditorUndoListner(undoManager,toolbar));
    }
    
    public void openConceptAnnotation(ConceptResource conceptResource) {
        logger.debug("Getting annotation: "+conceptResource.getAnnotation());
        
        // set to editor...
        editor.setText(conceptResource.getAnnotation());
        editor.setCaretPosition(0);
        undoManager.discardAllEdits();
        
        // ... and viewer
        setViewerText(editor.getText());
        
        // toolbar
        toolbar.refreshButtons();
    }
    
    /**
     * Copy the text from the editor to the concept's annotation. 
     */
    public void flushToResource() {
        if(getConceptResource()!=null) {
            getConceptResource().setAnnotation(editor.getText());
        } else {
            logger.warn("Active resource is null when flushing!"); // {{debug}}
        }
    }    
    
    /**
     * Close the resource to be saved.
     */
    public ConceptResource closeConceptAnnotation() {
        flushToResource();
        return getConceptResource();
    }

    public void toViewMode() {
        // no selected text - otherwise selection is deleted
        setViewerText(editor.getText());
        editorScroll.setVisible(false);
        viewerScroll.setVisible(true);
        validate();
    }

    public void toEditMode() {
        viewerScroll.setVisible(false);
        editorScroll.setVisible(true);
        validate();
    }

    public boolean inViewMode() {
        return viewerScroll.isVisible();
    }
    
    public void toggleEditViewMode() {
        if(inViewMode()) {
            toEditMode();
        } else {
            toViewMode();
        }
        validate();
    }
    
    /**
     * Search annotation from the current carret position.
     */
    public void searchAnnotation(String searchString, boolean again) {
        logger.debug("searchAnnotation() "+searchString); // {{debug}}
        JTextComponent textComponent;
        if(inViewMode()) {
            textComponent=viewer;
            logger.debug("Searching viewer..."); // {{debug}}
        } else {
            textComponent=editor;
            logger.debug("Searching editor..."); // {{debug}}
        }

        if(!again) {
            textComponent.setCaretPosition(0);
        }
        
        if (searchString != null && searchString.length() > 0) {
            Document document = textComponent.getDocument();
            try {
                int idx = textComponent.getDocument().getText(0, document.getLength()).indexOf(searchString,
                          textComponent.getCaretPosition());
                if (idx < 0) {
                    // try it from the beginning
                    idx = textComponent.getDocument().getText(0, document.getLength()).indexOf(searchString);
                } else if (idx > 0) {
                    textComponent.setCaretPosition(idx);
                    textComponent.requestFocus();
                    textComponent.select(idx, idx + searchString.length());
                }
            } catch (Exception e) {
                // TODO no bundle!
                logger.debug(Messages.getString("ConceptJPanel.unableToSearch", e.getMessage()));
            }
        }
    }
    
    public void searchConceptAnnotationAgain() {
        String searchString = SearchConceptAnnotation.getLastSearchString();
        if (searchString != null) {
            searchAnnotation(searchString,true);
        }
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }    

    private static final long serialVersionUID = 1L;
}

class EditorUndoListner implements UndoableEditListener {
    
    private UndoManager undoManager;
    private TextAnnotationToolbar toolbar;

    public EditorUndoListner(UndoManager undoManager, TextAnnotationToolbar toolbar) {
        this.undoManager=undoManager;
        this.toolbar=toolbar;
    }
    
    public void undoableEditHappened(UndoableEditEvent e) {
        undoManager.addEdit(e.getEdit());
        toolbar.refreshButtons();
    }
}
