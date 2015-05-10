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
import com.mindcognition.mindraider.ui.swing.concept.annotation.transformer.TWikiToHtmlTransformer;

public class TWikiAnnotationRenderer extends AbstractTextAnnotationRenderer {

    private static final Log logger = LogFactory.getLog(TWikiAnnotationRenderer.class); // {{debug}}

    /**
     * the label text constant.
     * TODO consider bundle
     */
    public static final String LABEL_TEXT = "TWiki";

    public TWikiAnnotationRenderer() {
    }

    public void init() {
        EditorAndViewerMouseListener editorAndViewerMouseListener = new EditorAndViewerMouseListener(this);
        super.init(
                new TWikiToHtmlTransformer(this),
                new EditorKeyListener(getConceptPanel(),this),
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
        
    /*
     * (non-Javadoc)
     *
     * @see com.emental.mindraider.concept.annotation.ConceptAnnotation#getAnnotationContentTypeOwlClass()
     */
    public String getAnnotationTypeOwlClass() {
        return MindRaiderConstants.MR_OWL_CONTENT_TYPE_TWIKI;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.emental.mindraider.concept.annotation.ConceptAnnotation#getLabel()
     */
    public String getAnnotationTypeLabel() {
        // it must be possible to localize label
        return LABEL_TEXT;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.emental.mindraider.concept.annotation.ConceptAnnotationType#getForegroundColor()
     */
    public Color getForegroundColor() {
        return Color.WHITE;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.emental.mindraider.concept.annotation.ConceptAnnotationType#getBackgroundColor()
     */
    public Color getBackgroundColor() {
        return Color.BLACK;
    }
    
    private static final long serialVersionUID = 6669239446058820454L;
}
