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
package com.emental.mindraider.ui.graph.spiders.color;

import java.awt.Color;

/**
 * Rendering profile containing colors and node types.
 */
public interface SpidersColorProfile {
    
    /**
     * My colors.
     */
    Color darkBlueColor = new Color(0x00, 0x00, 0x77);
    
    Color darkGreenColor = new Color(0x00, 0x77, 0x00);
    
    Color darkGrayColor = new Color(0x11, 0x11, 0x11);
    
    Color darkestGrayColor = new Color(0x07, 0x07, 0x07);
    
    Color grayColor = new Color(0xdd, 0xdd, 0xdd);
    
    Color lightGrayColor = new Color(0xee, 0xee, 0xee);
    
    
    /**
     * Methods.
     */
    
    String getLabel();
    String getUri();
    
    Color getBackroundColor();
    Color getTextColor();
    
    Color getNodeInactiveBorder();
    
    Color getEdgeDefaultColor();
    Color getEdgeExtraColor();
    
    int getLiteralNodeType();
    Color getLiteralBackColor();
    Color getLiteralSelectBackColor();
    Color getLiteralTextColor();
    Color getLiteralInactiveBorderColor();
    Color getLiteralActiveBorderColor();
    
    int getObjectNodeType();
    Color getObjectBackColor();
    Color getObjectTextColor();
    Color getObjectHilightBackColor();
    Color getObjectSelectBackColor();
    
    Color getPredicateBackColor();
    int getPredicateNodeType();
    Color getPredicateTextColor();

    int getSubjectNodeType();
    Color getSubjectBackColor();
    Color getSubjectTextColor();    
    Color getSubjectBackDefaultColor();
    Color getSubjectHilightBackColor();
    Color getSubjectSelectBackColor();
}
