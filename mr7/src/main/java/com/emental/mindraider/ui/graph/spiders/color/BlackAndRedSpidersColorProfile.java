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
 * BlackRenderingProfile.java
 * 
 * @author Martin.Dvorak
 */
public final class BlackAndRedSpidersColorProfile implements SpidersColorProfile {

    public Color backround = Color.BLACK;

    public Color subjectBackDefaultColor = Color.RED;

    public Color subjectBackColor = Color.DARK_GRAY;

    public Color subjectTextColor = Color.WHITE;

    public Color subjectSelectBackColor = Color.ORANGE;

    public Color subjectHilightBackColor = Color.RED;

    public int subjectNodeType = Node.TYPE_RECTANGLE;

    public Color predicateBackColor = darkestGrayColor;

    public Color predicateTextColor = Color.WHITE;

    public int predicateNodeType = Node.TYPE_ELLIPSE;

    public Color objectBackColor = Color.DARK_GRAY;

    public Color objectTextColor = Color.WHITE;

    public Color objectSelectBackColor = Color.ORANGE;

    public Color objectHilightBackColor = Color.GRAY;

    public int objectNodeType = Node.TYPE_RECTANGLE;

    public int literalNodeType = Node.TYPE_ROUNDRECT;

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
        return new Color(0x15,0x15,0x15);
    }

    /**
     * getEdgeDefaultColor
     * 
     * @return
     */
    public Color getEdgeExtraColor() {
        return Color.LIGHT_GRAY;
    }

    /**
     * getLiteralBackColor
     * 
     * @return
     */
    public Color getLiteralBackColor() {
        return Color.BLACK;
    }

    /**
     * getLiteralNodeType
     * 
     * @return
     */
    public int getLiteralNodeType() {
        return literalNodeType;
    }

    /**
     * getObjectBackColor
     * 
     * @return
     */
    public Color getObjectBackColor() {
        return objectBackColor;
    }

    /**
     * getObjectHilightBackColor
     * 
     * @return
     */
    public Color getObjectHilightBackColor() {
        return new Color(0x00,0x95,0xa5);
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
        return new Color(0xf4,0x00,0x23);
    }

    /**
     * getObjectTextColor
     * 
     * @return
     */
    public Color getObjectTextColor() {
        return objectTextColor;
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
        return subjectBackColor;
    }

    /**
     * getSubjectBackDefaultColor
     * 
     * @return
     */
    public Color getSubjectBackDefaultColor() {
        return subjectBackDefaultColor;
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
        return new Color(0xf4,0x00,0x23);
    }

    /**
     * getSubjectTextColor
     * 
     * @return
     */
    public Color getSubjectTextColor() {
        return subjectTextColor;
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
        // TODO Auto-generated method stub
        return getSubjectTextColor();
    }

    public String getLabel() {
        return "Black & Red";
    }

    public String getUri() {
        return MindRaiderConstants.MR_RDF_URN+":spiders:color-profiles:blackAndRed";
    }

    public Color getNodeInactiveBorder() {
        return Color.DARK_GRAY;
    }

    public Color getLiteralTextColor() {
        return new Color(0x99,0x99,0x99);
    }

    public Color getLiteralInactiveBorderColor() {
        return Color.DARK_GRAY;
    }
    public Color getLiteralActiveBorderColor() {
        return Color.WHITE;
    }

    public Color getLiteralSelectBackColor() {
        return getObjectSelectBackColor();
    }
}
