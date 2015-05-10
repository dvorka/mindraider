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

import javax.swing.JPanel;

import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.mindcognition.mindraider.ui.swing.concept.ConceptJPanel;

/**
 * Interface implemented by various concept annotations - both of textual and sketch type.
 */
public abstract class AbstractAnnotationRenderer extends JPanel {

    /**
     * concept resource being edited/viewed
     */
    private ConceptResource conceptResource;

    /**
     * concept JPanel
     */
    private ConceptJPanel conceptJPanel;
    
    /**
     * attachments JPanel
     */
    private JPanel attachmentsJPanel;
    
    /**
     * Open concept annotation in the renderer.
     * 
     * @param noteResource
     *      concept to be opened.
     */
    public abstract void openConceptAnnotation(ConceptResource conceptResource);
    
    /**
     * Flush annotation from the renderer to the concept resource.
     */
    public abstract void flushToResource();
    
    /**
     * Close concept annotation (flush it and clear renderer).
     * 
     * @return 
     *      closed resource.
     */
    public abstract ConceptResource closeConceptAnnotation();
    
    /**
     * Get annotation type OWL class identification.
     *
     * @return OWL class.
     */
    public abstract String getAnnotationTypeOwlClass();

    /**
     * Get label to be used in UI e.g. in combo box.
     *
     * @return label.
     */
    public abstract String getAnnotationTypeLabel();

    /**
     * Get editor foreground color.
     *
     * @return color.
     */
    public abstract Color getForegroundColor();

    /**
     * Get editor background color.
     *
     * @return color.
     */
    public abstract Color getBackgroundColor();
    
    /**
     * Inject attachment JPanel.
     * 
     * @param attachmentsJPanel
     */
    public void setAttachmentsJPanel(JPanel attachmentsJPanel) {
        this.attachmentsJPanel=attachmentsJPanel;
    }

    public ConceptResource getConceptResource() {
        return conceptResource;
    }

    public void setConceptResource(ConceptResource conceptResource) {
        this.conceptResource = conceptResource;
    }

    public ConceptJPanel getConceptPanel() {
        return conceptJPanel;
    }

    /**
     * Inject concept JPanel.
     * 
     * @param conceptJPanel
     */
    public void setConceptPanel(ConceptJPanel conceptJPanel) {
        this.conceptJPanel = conceptJPanel;
    }

    public JPanel getAttachmentsJPanel() {
        return attachmentsJPanel;
    }

    private static final long serialVersionUID = 3855142294654321187L;
}

