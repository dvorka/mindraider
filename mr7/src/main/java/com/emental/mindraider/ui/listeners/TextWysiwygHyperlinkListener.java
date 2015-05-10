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

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.apache.log4j.Logger;

import com.mindcognition.mindraider.ui.swing.concept.annotation.wysiwyg.TextWysiwyg;

public class TextWysiwygHyperlinkListener implements HyperlinkListener {

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(TextAnnotationPreviewHyperlinkListener.class);

    /**
     * Wysiwyg
     */
    private TextWysiwyg wysiwyg;

    /**
     * Constructor.
     * 
     * @param textWysiwyg   text wysiwyg.
     */
    public TextWysiwygHyperlinkListener(TextWysiwyg textWysiwyg) {
        this.wysiwyg=textWysiwyg;
    }
    
    /*
     * (non-Javadoc)
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            if (e instanceof HTMLFrameHyperlinkEvent) {
                logger.debug("It is frame hyperlink event...");
                final HTMLFrameHyperlinkEvent hyperEvent = (HTMLFrameHyperlinkEvent) e;

                ((HTMLDocument)wysiwyg.getDocument()).processHTMLFrameHyperlinkEvent(hyperEvent);
            } else {
                TextAnnotationPreviewHyperlinkListener.handleMindRaiderHyperlink(e);
            }
        }
    }

}
