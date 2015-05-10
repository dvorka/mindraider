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
package com.emental.mindraider.ui.editors.color;

import java.awt.Color;

import com.mindcognition.mindraider.commons.MindRaiderConstants;

public class WhiteAnnotationColorProfile implements AnnotationColorProfile {

    public String getLabel() {
        return "White";
    }

    public String getUri() {
        return MindRaiderConstants.MR_RDF_URN+":annotation:color-profiles:white";
    }

    public void fromXml() {
        // TODO Auto-generated method stub

    }

    public void toXml() {
        // TODO Auto-generated method stub

    }

    public Color getBackroundColor() {
        return Color.WHITE;
    }

    public Color getTextColor() {
        return Color.BLACK;
    }

    public Color getEnabledCaretColor() {
        return Color.BLUE;
    }

    public Color getDisabledCaretColor() {
        return Color.LIGHT_GRAY;
    }

    public Color getNormalLinkColor() {
        return Color.BLUE;
    }

    public Color getMindRaiderLinkColor() {
        return Color.GREEN;
    }

    public Color getTodoColor() {
        return Color.BLACK;
    }

    public Color getImportantTodoColor() {
        return Color.RED;
    }

    public Color getFinishedToDoColor() {
        return Color.LIGHT_GRAY;
    }

    public Color getSelectionColor() {
        return Color.decode("#384563");
    }

    public Color getSelectionTextColor() {
        return Color.WHITE;
    }

}
