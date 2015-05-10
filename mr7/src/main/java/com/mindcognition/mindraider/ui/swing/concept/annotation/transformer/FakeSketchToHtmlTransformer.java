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
package com.mindcognition.mindraider.ui.swing.concept.annotation.transformer;

import com.mindcognition.mindraider.ui.swing.concept.annotation.renderer.AbstractTextAnnotationRenderer;

public class FakeSketchToHtmlTransformer implements AnnotationToHtmlTransformer {

    private AbstractTextAnnotationRenderer renderer;
    
    public FakeSketchToHtmlTransformer(AbstractTextAnnotationRenderer renderer) {
        this.renderer=renderer;
    }
    
    public String toHtml(String editorText) {
        if(renderer.getConceptResource()!=null) {
            return
            HTML_HEAD +
            "Sketch functionality has been removed from MindRaider starting from version 7.7:<br>"+
            "Your sketches might be found in:<br>"+
            "&nbsp;&nbsp;$HOME/MindRaider/Notebooks/[notebook name]/annotations/*.jaj<br>"+
            "Download Jarnal to further open and edit them:"+
            "&nbsp;&nbsp;http://levine.sscnet.ucla.edu/general/software/tc1000/jarnal-down.htm"+
            HTML_TAIL;
        } else {
            return null;
        }
    }
}
