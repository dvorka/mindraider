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
package com.emental.mindraider.ui.graph.spiders;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGPanel;

/**
 * Extends touchgraph node to allow mulitline label.
 * 
 * @author Martin Dvorak
 */
public class MultilinedNode extends Node {
    /**
     * The character separator.
     */
    protected char separator = '\u0000';

    /**
     * The label array String.
     */
    protected String[] labels;

    /**
     * The max width.
     */
    protected int maxWidth;

    /**
     * The max width label pos.
     */
    protected int maxWidthLabelPos;

    /**
     * Minimal constructor which will generate an ID value from Java's Date
     * class. Defaults will be used for type and color. The label will be taken
     * from the ID value.
     */
    public MultilinedNode() {
    }

    /**
     * Constructor with the required ID <tt>id</tt>, using defaults for type
     * (rectangle), color (a static variable from TGPanel). The Node's label
     * will be taken from the ID value.
     * 
     * @param id
     *            the id String
     */
    public MultilinedNode(String id) {
        super(id);
    }

    /**
     * Constructor with Strings for ID <tt>id</tt> and <tt>label</tt>,
     * using defaults for type (rectangle) and color (a static variable from
     * TGPanel). If the label is null, it will be taken from the ID value.
     * 
     * @param id
     *            the id String
     * @param label
     *            the label String
     */
    public MultilinedNode(String id, String label) {
        super(id, label);
    }

    /**
     * Constructor with Strings for ID <tt>id</tt> and <tt>label</tt>,
     * using defaults for type (rectangle) and color (a static variable from
     * TGPanel). If the label is null, it will be taken from the ID value.
     * 
     * @param id
     *            node identifier
     * @param label
     *            node label
     * @param separator
     *            used for multiline labels, name will be split according to
     *            separator
     */
    public MultilinedNode(String id, String label, char separator) {
        super(id, label);
        this.separator = separator;
    }

    /**
     * Constructor with a String ID <tt>id</tt>, an int <tt>type</tt>,
     * Background Color <tt>bgColor</tt>, and a String <tt>label</tt>. If
     * the label is null, it will be taken from the ID value.
     * 
     * @param id
     *            the String id
     * @param type
     *            the type value
     * @param color
     *            the Color
     * @param label
     *            the label String
     * @param separator
     *            the character separator
     * @see #TYPE_RECTANGLE
     * @see #TYPE_ROUNDRECT
     */
    public MultilinedNode(String id, int type, Color color, String label,
            char separator) {
        super(id, type, color, label);
        this.separator = separator;
    }

    /**
     * Sets label separator.
     * 
     * @param separator
     *            label separator
     */
    public void setSeparator(char separator) {
        this.separator = separator;
        initLabels();
    }

    /**
     * Gets label separator.
     * 
     * @return label separator
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * Init labels.
     */
    protected void initLabels() {
        maxWidth = 0;
        maxWidthLabelPos = 0;
        int pos = lbl.indexOf(separator, lbl.indexOf(separator) + 1);
        if (pos > 0) {
            // @todo finish array list
            ArrayList<String> result = new ArrayList<String>();

            // / labels = new String[2];
            int labelsPos = 0;
            int lastPos = 0;
            while (pos > 0) {
                /*
                 * if (labelsPos == labels.length){ //expand String[] newLabels =
                 * new String[labelsPos+1];
                 * System.arraycopy(labels,0,newLabels,0,labelsPos); labels =
                 * newLabels; }
                 */
                if (lastPos < pos) {
                    // labels[labelsPos]= lbl.substring(lastPos,pos);
                    result.add(lbl.substring(lastPos, pos));
                } else {
                    // @todo uber linu
                    // /labels[labelsPos]=" ";
                    break;
                }
                // @todo break too long line without separators
                /*
                 * if (labels[labelsPos].length()>maxWidth){ maxWidthLabelPos =
                 * labelsPos; maxWidth = labels[labelsPos].length(); }
                 */
                if (pos == lbl.length()) {
                    break;
                }
                labelsPos++;
                lastPos = pos + 1;
                pos = lbl.indexOf(separator,
                        lbl.indexOf(separator, lastPos) + 1);
                if (pos == -1) {
                    pos = lbl.length();
                }
            }
            if (result.size() > 3) {
                // TODO go through array and create logest space one
                result.add(0, "                                       ");
                result.add(" ");
            }
            labels = (String[]) result.toArray(new String[result.size()]);
        } else {
            labels = new String[1];
            labels[0] = lbl;
            maxWidth = lbl.length();
        }
    }

    /**
     * Return the label of this Node as a String.
     * 
     * @return the label
     */
    public String getLabel() {
        return super.getLabel();
    }

    /**
     * @see com.touchgraph.graphlayout.Node#setLabel(java.lang.String)
     */
    public void setLabel(String label) {
        labels = null;
        super.setLabel(label);
        initLabels();
    }

    /**
     * @see com.touchgraph.graphlayout.Node#getWidth()
     */
    public int getWidth() {
        if (labels == null) {
            initLabels();
        }
        if (fontMetrics != null && labels[maxWidthLabelPos] != null) {
            return fontMetrics.stringWidth(labels[maxWidthLabelPos]) + 12;
        }
        return 10;
    }

    /**
     * @see com.touchgraph.graphlayout.Node#getHeight()
     */
    public int getHeight() {
        if (labels == null) {
            initLabels();
        }
        if (fontMetrics != null) {
            return (fontMetrics.getHeight()) * labels.length + 6;
        }
        return 6;
    }

    /**
     * Return the height of label element.
     * 
     * @return Returns the height value
     */
    public int getElemHeight() {
        if (labels == null) {
            initLabels();
        }
        if (fontMetrics != null) {
            return fontMetrics.getHeight();
        }
        return 0;
    }

    /**
     * @see com.touchgraph.graphlayout.Node#paintNodeBody(java.awt.Graphics,
     *      com.touchgraph.graphlayout.TGPanel)
     */
    public void paintNodeBody(Graphics g, TGPanel tgPanel) {
        g.setFont(font);
        fontMetrics = g.getFontMetrics();

        int ix = (int) drawx;
        int iy = (int) drawy;
        int h = getHeight();
        int w = getWidth();
        int r = h / 2 + 1; // arc radius

        Color borderCol = getPaintBorderColor(tgPanel);
        g.setColor(borderCol);

        switch (typ) {
        case TYPE_ROUNDRECT:
            g.fillRoundRect(ix - w / 2, iy - h / 2, w, h, r, r);
            break;
        case TYPE_ELLIPSE:
            g.fillOval(ix - w / 2, iy - h / 2, w, h);
            break;
        case TYPE_CIRCLE:
            // just use width for both dimensions
            g.fillOval(ix - w / 2, iy - w / 2, w, w);
            break;
        default: // case TYPE_RECTANGLE :
            g.fillRect(ix - w / 2, iy - h / 2, w, h);
            break;
        }

        Color backCol = getPaintBackColor(tgPanel);
        g.setColor(backCol);

        switch (typ) {
        case TYPE_ROUNDRECT:
            g.fillRoundRect(ix - w / 2 + 2, iy - h / 2 + 2, w - 4, h - 4, r, r);
            break;
        case TYPE_ELLIPSE:
            g.fillOval(ix - w / 2 + 2, iy - h / 2 + 2, w - 4, h - 4);
            break;
        case TYPE_CIRCLE:
            g.fillOval(ix - w / 2 + 2, iy - w / 2 + 2, w - 4, w - 4);
            break;
        default: // case TYPE_RECTANGLE :
            g.fillRect(ix - w / 2 + 2, iy - h / 2 + 2, w - 4, h - 4);
            break;
        }

        Color textCol = getPaintTextColor(tgPanel);
        g.setColor(textCol);
        // this one needs to be customized
        if (labels.length == 1) {
            // original
            g.drawString(lbl, ix - fontMetrics.stringWidth(lbl) / 2, iy
                    + fontMetrics.getDescent() + 1);
        } else {
            // multi line (y+30 ... go to south, x +30 goto east
            int elemHeight = getElemHeight();
            int lineFact = -(elemHeight * (labels.length - 1)) / 2;
            for (int i = 0; i < labels.length; i++) {
                String label = labels[i];
                g.drawString(label, ix - fontMetrics.stringWidth(label) / 2, iy
                        + fontMetrics.getDescent() + 1 + lineFact);
                lineFact += elemHeight;
            }
        }
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that "textually
     * represents" this object. The result should be a concise but informative
     * representation that is easy for a person to read. It is recommended that
     * all subclasses override this method.
     * <p>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the object
     * is an instance, the at-sign character `<code>@</code>', and the
     * unsigned hexadecimal representation of the hash code of the object. In
     * other words, this method returns a string equal to the value of:
     * <blockquote>
     * 
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre>
     * 
     * </blockquote>
     * @return a string representation of the object.
     */
    public String toString() {
        return getClass().getName() + "[id=" + getID() + ",label=" + getLabel()
                + ",type" + getType() + "]";
    }
}
