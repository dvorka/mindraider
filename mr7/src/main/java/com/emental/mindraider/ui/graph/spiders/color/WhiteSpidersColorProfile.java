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

import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.touchgraph.graphlayout.Node;

/**
 * WhiteRenderingProfile.java
 * 
 * @author Martin.Dvorak
 */
public final class WhiteSpidersColorProfile implements SpidersColorProfile {


    public Color backround = Color.WHITE;

    public Color nodeInactiveBorderColor = Color.WHITE;

    public Color edgeDefaultColor = grayColor;

    public Color subjectTextColor = Color.WHITE;

    public Color subjectSelectBackColor = Color.ORANGE;

    public Color subjectHilightBackColor = Color.RED;

    public int subjectNodeType = Node.TYPE_ROUNDRECT;

    public Color predicateBackColor = lightGrayColor;

    public Color predicateTextColor = Color.BLACK;

    public int predicateNodeType = Node.TYPE_ELLIPSE;

    public Color objectBackColor = Color.BLACK;

    public Color objectTextColor = Color.WHITE;

    public Color objectSelectBackColor = Color.ORANGE;

    public Color objectHilightBackColor = darkGrayColor;

    public int objectNodeType = Node.TYPE_ROUNDRECT;

    public Color literalBackColor = darkBlueColor;

    public int literalNodeType = Node.TYPE_RECTANGLE;

    /**
     * getBackround
     * 
     * @return
     */
    public Color getBackround() {
        return backround;
    }

    /**
     * getEdgeDefaultColor
     * 
     * @return
     */
    public Color getEdgeDefaultColor() {
        return edgeDefaultColor;
    }


    /**
     * getObjectBackColor
     * 
     * @return
     */
    public Color getObjectBackColor() {
        return Color.WHITE;
    }

    /**
     * getObjectHilightBackColor
     * 
     * @return
     */
    public Color getObjectHilightBackColor() {
        return objectHilightBackColor;
    }

    /**
     * getObjectNodeType
     * 
     * @return
     */
    public int getObjectNodeType() {
        return objectNodeType;
    }

    /**
     * getObjectSelectBackColor
     * 
     * @return
     */
    public Color getObjectSelectBackColor() {
        return new Color(0x7f,0xc0,0x74);
    }

    /**
     * getObjectTextColor
     * 
     * @return
     */
    public Color getObjectTextColor() {
        return Color.BLACK;
    }

    /**
     * getPredicateBackColor
     * 
     * @return
     */
    public Color getPredicateBackColor() {
        return predicateBackColor;
    }

    /**
     * getPredicateNodeType
     * 
     * @return
     */
    public int getPredicateNodeType() {
        return predicateNodeType;
    }

    /**
     * getPredicateTextColor
     * 
     * @return
     */
    public Color getPredicateTextColor() {
        return predicateTextColor;
    }

    /**
     * getSubjectBackColor
     * 
     * @return
     */
    public Color getSubjectBackColor() {
        return Color.WHITE;
    }

    /**
     * getSubjectBackDefaultColor
     * 
     * @return
     */
    public Color getSubjectBackDefaultColor() {
        return Color.WHITE;
    }

    /**
     * getSubjectHilightBackColor
     * 
     * @return
     */
    public Color getSubjectHilightBackColor() {
        return subjectHilightBackColor;
    }

    /**
     * getSubjectNodeType
     * 
     * @return
     */
    public int getSubjectNodeType() {
        return subjectNodeType;
    }

    /**
     * getSubjectSelectBackColor
     * 
     * @return
     */
    public Color getSubjectSelectBackColor() {
        return new Color(0x7f,0xc0,0x74);
    }

    /**
     * getSubjectTextColor
     * 
     * @return
     */
    public Color getSubjectTextColor() {
        return Color.BLACK;
    }

    public Color getNodeInactiveBorder() {
        return new Color(0x05,0x05,0x05);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.emental.mindrider.profile.color.ColorProfile#getBackroundColor()
     */
    public Color getBackroundColor() {
        // TODO Auto-generated method stub
        return backround;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.emental.mindrider.profile.color.ColorProfile#getTextColor()
     */
    public Color getTextColor() {
        return Color.WHITE;
    }

    public String getLabel() {
        return "White";
    }

    public String getUri() {
        return MindRaiderConstants.MR_RDF_URN+":spiders:color-profiles:white";
    }

    public Color getEdgeExtraColor() {
        return new Color(0xff,0xcd,0x8d);
    }
    
    public Color getLiteralInactiveBorderColor() {
        return new Color(0x05,0x05,0x05);
    }

    public Color getLiteralActiveBorderColor() {
        return Color.WHITE;
    }
    
    public Color getLiteralSelectBackColor() {
        return new Color(0x99,0xb1,0xff);
    }

    public Color getLiteralTextColor() {
        return Color.BLACK;
    }
    
    /**
     * getLiteralBackColor
     * 
     * @return
     */
    public Color getLiteralBackColor() {
        return Color.WHITE;
    }

    /**
     * getLiteralNodeType
     * 
     * @return
     */
    public int getLiteralNodeType() {
        return literalNodeType;
    }
}
