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
package com.emental.mindraider.ui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.concept.ConceptJPanel;

/**
 * Content type combo listener.
 */
public class ContentTypeComboActionListener implements ActionListener {

    private static final Logger logger = Logger
            .getLogger(ContentTypeComboActionListener.class);

    /**
     * Concept JPanel.
     */
    private ConceptJPanel conceptJPanel;

    /**
     * Constructor.
     * 
     * @param conceptJPanel
     */
    public ContentTypeComboActionListener(ConceptJPanel conceptJPanel) {
        this.conceptJPanel = conceptJPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() instanceof JComboBox) {
            String actionString;
            JComboBox comboBox = ((JComboBox) event.getSource());
            if ((actionString = (String) comboBox.getSelectedItem()) != null) {

                // ask user whether he really wants to change the content type
                // (previous annotation is lost)
                ConceptResource conceptResource = conceptJPanel.getConceptResource();
                if (JOptionPane
                        .showConfirmDialog(
                                MindRaider.mainJFrame,
                                Messages.getString("ContentTypeComboActionListener.doYouWantChangeTypeAnnotationTo",actionString),
                                Messages.getString("ContentTypeComboActionListener.changeAnnotationType"),
                                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    conceptJPanel.refreshContentTypeCombo(conceptResource);
                    return;
                }

                // update resource
                try {
                    conceptJPanel.annotationSetResourceContentType();
                    conceptResource.save();
                    conceptJPanel.open(conceptResource,false);
                } catch (Exception e) {
                    logger.error("Unable to save concept resource on concept type change!",e);
                }
            }
        }
    }
}
