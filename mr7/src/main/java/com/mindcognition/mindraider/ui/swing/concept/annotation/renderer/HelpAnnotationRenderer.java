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

import com.mindcognition.mindraider.ui.swing.concept.annotation.transformer.AnnotationToHtmlTransformer;
import com.mindcognition.mindraider.ui.swing.concept.annotation.transformer.HtmlToHtmlTransformer;

public class HelpAnnotationRenderer extends AbstractTextAnnotationRenderer {

    // TODO bundle
    String welcomeString =
        AnnotationToHtmlTransformer.HTML_HEAD
        + " <h2>Welcome to MindRaider!</h2>"
        + " <br>"
        + " <b>Note Tips:</b>"
        + " <ul>"
        + "  <li>Double click note annotation to <b>edit</b> the note</li>"
        + "  <li>Although there is auto-save, you may <b>save topic</b> using <code>CTRL-S</code> </li>"
        + "  <li>You may <b>search</b> note using <code>CTRL-F</code> (search again using <code>CTRL-G</code>)</li>"
        + " </ul>"
        + " <br>"
        + " <b>Graph Navigator Tips:</b>"
        + " <ul>"
        + "  <li>Double-click graph node to <b>launch</b> it.</li>"
        + "  <li>Click a graph node with <b>right</b> mouse button to <b>search</b> it's label using <a href='http://www.google.com'>Google</a>.</li>"
        + "  <li>Click graph resource node/edge node with <b>right</b> mouse button to <b>manipulate</b> underlaying RDF model.</li>"
        + " </ul>" + AnnotationToHtmlTransformer.HTML_TAIL;

    public static final String LABEL_TEXT = "Introduction";
    
    public HelpAnnotationRenderer() {
        init();
        hideVieverCarret();
        setViewerText(welcomeString);
    }

    public void init() {
        super.init(
                new HtmlToHtmlTransformer(),
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }
    
    @Override
    public String getAnnotationTypeLabel() {
        return LABEL_TEXT;
    }

    @Override
    public String getAnnotationTypeOwlClass() {
        return null;
    }

    @Override
    public Color getBackgroundColor() {
        return null;
    }

    @Override
    public Color getForegroundColor() {
        return null;
    }
    
    private static final long serialVersionUID = -3262196573170843600L;
}
