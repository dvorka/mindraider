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

import java.awt.Color;
import java.net.URI;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.application.model.note.annotation.NoteInterlinking;
import com.mindcognition.mindraider.ui.swing.concept.ConceptJPanel;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;
import com.mindcognition.mindraider.utils.Launcher;

/**
 * Concept's text annotation preview hyperlink handler.
 */
public class TextAnnotationPreviewHyperlinkListener implements
        HyperlinkListener {
    private static final Logger logger = Logger.getLogger(TextAnnotationPreviewHyperlinkListener.class);

    /**
     * Preview pane.
     */
    private JEditorPane previewPane;

    /**
     * Concept JPanel.
     */
    private static ConceptJPanel conceptJPanel;

    /**
     * Constructor.
     */
    public TextAnnotationPreviewHyperlinkListener(ConceptJPanel conceptJPanel,
            JEditorPane previewPane) {
        TextAnnotationPreviewHyperlinkListener.conceptJPanel = conceptJPanel;
        this.previewPane = previewPane;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            if (e instanceof HTMLFrameHyperlinkEvent) {
                logger.debug("It is frame hyperlink event...");
                final HTMLFrameHyperlinkEvent hyperEvent = (HTMLFrameHyperlinkEvent) e;

                ((HTMLDocument) previewPane.getDocument())
                        .processHTMLFrameHyperlinkEvent(hyperEvent);
            } else {
                handleMindRaiderHyperlink(e);
            }
        }
    }

    /**
     * Handle MR hyperlink.
     * 
     * @param e
     *            event.
     */
    public static void handleMindRaiderHyperlink(HyperlinkEvent e) {
        logger.debug("It is hyperlink event for URL: " + e.getURL());
        logger.debug("It is hyperlink event for description: " + e.getDescription());

        if (e.getURL() != null) {
            URL url = e.getURL();
            String conceptUri = null;
            
            if(MindRaider.profile.getActiveOutlineUri()==null)
                return;
            
            String notebookUri = MindRaider.profile.getActiveOutlineUri()
                    .toString();
            boolean notebookChanged = false;
            if (NoteInterlinking.MINDRAIDER_LOCAL_RESOURCE_HOSTNAME
                    .equals(url.getHost())) {
                logger.debug("Handling MR resource: " + url);
                String[] query = url.getQuery().split("&");
                for (int i = 0; i < query.length; i++) {
                    String[] splitQuery = query[i].split("=");
                    if (NoteInterlinking.CONCEPT_URI_PARAMETER
                            .equals(splitQuery[0])) {
                        conceptUri = splitQuery[1].replace('~', '#');
                    } else {
                        if (NoteInterlinking.NOTEBOOK_URI_PARAMETER
                                .equals(splitQuery[0])) {
                            notebookUri = splitQuery[1].replace('~', '#');
                            notebookChanged = true;
                        }
                    }
                }

                // open target notebook
                try {
                    // load notebook
                    if (notebookChanged) {
                        MindRaider.profile.setActiveOutlineUri(null);
                        MindRaider.outlineCustodian.loadOutline(new URI(
                                notebookUri));
                        OutlineJPanel.getInstance().refresh();
                    }
                    // load concept
                    if (conceptUri != null) {
                        conceptJPanel.open(MindRaider.noteCustodian.get(
                                notebookUri, conceptUri));
                        // TODO select concept in graph
                    } else {
                        // if notebook is linked, clear concept
                        conceptJPanel.clear();
                    }
                } catch (Exception e1) {
                    logger.error("Unable to open target concept " + conceptUri
                            + " from notebook " + notebookUri + "!", e1);
                }

            } else {
                logger.debug("Handling external link: " + url);
                Launcher.launchInBrowser(e.getURL().toString());
                // alternative browse in MR:
                // previewPane.setPage(e.getURL());
            }
        } else {
            // try to lookup the concept name ;-)
            if(e.getDescription()!=null && e.getDescription().length()>0) {
                // get concept URI for its name
                ResourceDescriptor conceptToShowDescriptor
                    =MindRaider.outlineCustodian.getNoteDescriptorByName(e.getDescription());
                if(conceptToShowDescriptor!=null) {
                    String conceptToShow=conceptToShowDescriptor.getUri();
                    try {
                        OutlineJPanel.getInstance().conceptJPanel.open(MindRaider.noteCustodian.get(
                                MindRaider.profile.getActiveOutlineUri().toString(), 
                                conceptToShow));
                    } catch (Exception e1) {
                        StatusBar.show("Concept with title '"+e.getDescription()+"' not found in active notebook!", Color.red);
                    }
                    OutlineJPanel.getInstance().setSelectedTreeNodeConcept(conceptToShow);
                    
                }
            }
            
        }

    }

}
