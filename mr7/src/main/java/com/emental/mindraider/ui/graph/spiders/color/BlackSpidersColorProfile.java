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
public final class BlackSpidersColorProfile implements SpidersColorProfile {

    public Color backround = Color.BLACK;

    public Color nodeInactiveBorderColor = Color.BLACK;

    public Color edgeDefaultColor = grayColor;

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

    public Color literalBackColor = darkBlueColor;

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
        return edgeDefaultColor;
    }

    /**
     * getLiteralBackColor
     * 
     * @return
     */
    public Color getLiteralBackColor() {
        return literalBackColor;
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
        return darkGreenColor;
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
        return darkGreenColor;
    }

    /**
     * getSubjectTextColor
     * 
     * @return
     */
    public Color getSubjectTextColor() {
        return subjectTextColor;
    }

    /**
     * setBackround
     * 
     * @param color
     */
    public void setBackround(Color color) {
        backround = color;
    }

    /**
     * setEdgeDefaultColor
     * 
     * @param color
     */
    public void setEdgeDefaultColor(Color color) {
        edgeDefaultColor = color;
    }

    /**
     * setLiteralBackColor
     * 
     * @param color
     */
    public void setLiteralBackColor(Color color) {
        literalBackColor = color;
    }

    /**
     * setLiteralNodeType
     * 
     * @param i
     */
    public void setLiteralNodeType(int i) {
        literalNodeType = i;
    }

    /**
     * setObjectBackColor
     * 
     * @param color
     */
    public void setObjectBackColor(Color color) {
        objectBackColor = color;
    }

    /**
     * setObjectHilightBackColor
     * 
     * @param color
     */
    public void setObjectHilightBackColor(Color color) {
        objectHilightBackColor = color;
    }

    /**
     * setObjectNodeType
     * 
     * @param i
     */
    public void setObjectNodeType(int i) {
        objectNodeType = i;
    }

    /**
     * setObjectSelectBackColor
     * 
     * @param color
     */
    public void setObjectSelectBackColor(Color color) {
        objectSelectBackColor = color;
    }

    /**
     * setObjectTextColor
     * 
     * @param color
     */
    public void setObjectTextColor(Color color) {
        objectTextColor = color;
    }

    /**
     * setPredicateBackColor
     * 
     * @param color
     */
    public void setPredicateBackColor(Color color) {
        predicateBackColor = color;
    }

    /**
     * setPredicateNodeType
     * 
     * @param i
     */
    public void setPredicateNodeType(int i) {
        predicateNodeType = i;
    }

    /**
     * setPredicateTextColor
     * 
     * @param color
     */
    public void setPredicateTextColor(Color color) {
        predicateTextColor = color;
    }

    /**
     * setSubjectBackColor
     * 
     * @param color
     */
    public void setSubjectBackColor(Color color) {
        subjectBackColor = color;
    }

    /**
     * setSubjectBackDefaultColor
     * 
     * @param color
     */
    public void setSubjectBackDefaultColor(Color color) {
        subjectBackDefaultColor = color;
    }

    /**
     * setSubjectHilightBackColor
     * 
     * @param color
     */
    public void setSubjectHilightBackColor(Color color) {
        subjectHilightBackColor = color;
    }

    /**
     * setSubjectNodeType
     * 
     * @param i
     */
    public void setSubjectNodeType(int i) {
        subjectNodeType = i;
    }

    /**
     * setSubjectSelectBackColor
     * 
     * @param color
     */
    public void setSubjectSelectBackColor(Color color) {
        subjectSelectBackColor = color;
    }

    /**
     * setSubjectTextColor
     * 
     * @param color
     */
    public void setSubjectTextColor(Color color) {
        subjectTextColor = color;
    }

    public Color getNodeInactiveBorder() {
        return nodeInactiveBorderColor;
    }

    public void setNodeInactiveBorder(Color color) {
        this.nodeInactiveBorderColor = color;
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
        return "Black";
    }

    public String getUri() {
        return MindRaiderConstants.MR_RDF_URN+":spiders:color-profiles:black";
    }

    public Color getEdgeExtraColor() {
        // TODO Auto-generated method stub
        return getEdgeDefaultColor();
    }

    public Color getLiteralTextColor() {
        // TODO Auto-generated method stub
        return getObjectTextColor();
    }

    public Color getLiteralInactiveBorderColor() {
        // TODO Auto-generated method stub
        return getObjectHilightBackColor();
    }
    public Color getLiteralActiveBorderColor() {
        // TODO Auto-generated method stub
        return getObjectHilightBackColor();
    }
    
    public Color getLiteralSelectBackColor() {
        return getObjectSelectBackColor();
    }
    
}

