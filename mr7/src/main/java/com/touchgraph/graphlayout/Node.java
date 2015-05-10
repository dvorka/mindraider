/*
 * TouchGraph LLC. Apache-Style Software License
 *
 *
 * Copyright (c) 2001-2002 Alexander Shapiro. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        TouchGraph LLC (http://www.touchgraph.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "TouchGraph" or "TouchGraph LLC" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission.  For written permission, please contact
 *    alex@touchgraph.com
 *
 * 5. Products derived from this software may not be called "TouchGraph",
 *    nor may "TouchGraph" appear in their name, without prior written
 *    permission of alex@touchgraph.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL TOUCHGRAPH OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 *
 */

package com.touchgraph.graphlayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.Vector;

import com.emental.mindraider.ui.graph.spiders.SpidersGraph;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * The Node class.
 * 
 * @author Alexander Shapiro
 * @author Murray Altheim
 */
public class Node {

    /**
     * The text font constant.
     */
    public static Font TEXT_FONT = new Font("Verdana", Font.PLAIN, 10);

    /**
     * The default type.
     */
    public static int DEFAULT_TYPE = 1;

    /**
     * This Node's type is a Rectangle.
     */
    public static final int TYPE_RECTANGLE = 1;

    /**
     * This Node's type is a Round Rectangle.
     */
    public static final int TYPE_ROUNDRECT = 2;

    /**
     * This Node's type is an Ellipse.
     */
    public static final int TYPE_ELLIPSE = 3;

    /**
     * This Node's type is a Circle.
     */
    public static final int TYPE_CIRCLE = 4;

    /**
     * The small tag font constant.
     */
    public static final Font SMALL_TAG_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 9);

    /**
     * The back fixed color.
     */
    public Color BACK_FIXED_COLOR = new Color(255, 32, 20);

    /**
     * The back select color.
     */
    public Color BACK_SELECT_COLOR = new Color(225, 164, 0);

    /**
     * The back default color.
     */
    public Color BACK_DEFAULT_COLOR = Color.decode("#4080A0");

    /**
     * The back hilight color.
     */
    public Color BACK_HILIGHT_COLOR = new Color(205, 192, 166);

    /**
     * The back mrf color.
     */
    public Color BACK_MRF_COLOR = new Color(2, 35, 81);

    /**
     * The back jml color.
     */
    public Color BACK_JML_COLOR = new Color(58, 176, 255);

    /**
     * The border drag color.
     */
    public Color BORDER_DRAG_COLOR = new Color(130, 130, 180);

    /**
     * The border mouse over color.
     */
    public Color BORDER_MOUSE_OVER_COLOR = new Color(160, 160, 180);

    /**
     * The border inactive color.
     */
    public Color BORDER_INACTIVE_COLOR = new Color(30, 50, 160);

    /**
     * The text color.
     */
    public Color TEXT_COLOR = Color.white;

    /**
     * an int indicating the Node type.
     * 
     * @see TYPE_RECTANGLE
     * @see TYPE_ROUNDRECT
     * @see TYPE_ELLIPSE
     */
    protected int typ = TYPE_RECTANGLE;

    /**
     * The id.
     */
    private String id;

    /**
     * The drawx.
     */
    public double drawx;

    /**
     * The drawy.
     */
    public double drawy;

    /**
     * The font metrics.
     */
    protected FontMetrics fontMetrics;

    /**
     * The font.
     */
    protected Font font;

    /**
     * The label?.
     */
    protected String lbl;

    /**
     * The background color.
     */
    protected Color backColor = BACK_DEFAULT_COLOR;

    /**
     * The text color.
     */
    protected Color textColor = TEXT_COLOR;

    /**
     * The x position.
     */
    public double x;

    /**
     * The y position.
     */
    public double y;

    /**
     * The massfade.
     */
    public double massfade = 1; // Used by layout

    /**
     * The dx.
     */
    protected double dx; // Used by layout

    /**
     * The dy.
     */
    protected double dy; // Used by layout

    /**
     * The fixed flag.
     */
    protected boolean fixed;

    /**
     * The repulsion value.
     */
    protected int repulsion; // Used by layout

    /**
     * The just made local option.
     */
    public boolean justMadeLocal;

    /**
     * The marked for removal option.
     */
    public boolean markedForRemoval;

    /**
     * The visible edge content? Should only be modified by
     * graphelements.VisibleLocality.
     */
    public int visibleEdgeCnt;

    /**
     * The visible flag.
     */
    protected boolean visible;

    /**
     * The vector of edges.
     */
    private Vector edges;

    /**
     * The icon reference. Modification by Brendan.
     */
    private Image iconRef;

    /**
     * The string url. Modification by Lutz.
     */
    private String strUrl;

    /**
     * The rdf node. Modification by dvorka
     */
    public RDFNode rdfNode;

    /**
     * Minimal constructor which will generate an ID value from Java's Date
     * class. Defaults will be used for type and color. The label will be taken
     * from the ID value.
     */
    public Node() {
        initialize(null);
        lbl = id;
    }

    /**
     * Constructor with the required ID <tt>id</tt>, using defaults for type
     * (rectangle), color (a static variable from TGPanel). The Node's label
     * will be taken from the ID value.
     */
    public Node(String id) {
        initialize(id);
        lbl = id;
    }

    /**
     * Constructor with Strings for ID <tt>id</tt> and <tt>label</tt>,
     * using defaults for type (rectangle) and color (a static variable from
     * TGPanel). If the label is null, it will be taken from the ID value.
     */
    public Node(String id, String label) {
        initialize(id);
        if (label == null) {
            lbl = id;
        } else {
            lbl = label;
        }
    }

    /**
     * Constructor with a String ID <tt>id</tt>, an int <tt>type</tt>,
     * Background Color <tt>bgColor</tt>, and a String <tt>label</tt>. If
     * the label is null, it will be taken from the ID value.
     * 
     * @see TYPE_RECTANGLE
     * @see TYPE_ROUNDRECT
     */
    public Node(String id, int type, Color color, String label) {
        initialize(id);
        typ = type;
        backColor = color;
        if (label == null) {
            lbl = id;
        } else {
            lbl = label;
        }
    }

    // Added by Brendan to allow image setting
    /**
     * Constructor with a String ID <tt>id</tt>, an int <tt>type</tt>,
     * Background Color <tt>bgColor</tt>, String <tt>label</tt> and an
     * Image to display. If the label is null, it will be taken from the ID
     * value.
     * 
     * @see TYPE_RECTANGLE
     * @see TYPE_ROUNDRECT
     */
    public Node(String id, int type, Color color, String label, Image iconRef) {
        initialize(id);
        typ = type;
        backColor = color;
        if (label == null) {
            lbl = id;
        } else {
            lbl = label;
        }

        this.iconRef = iconRef;
    }

    /**
     * Initialize the node.
     * 
     * @param identifier
     *            the node identifier.
     */
    private void initialize(String identifier) {
        this.id = identifier;
        edges = new Vector();
        x = Math.random() * 2 - 1; // If multiple nodes are added without
        // repositioning,
        y = Math.random() * 2 - 1; // randomizing starting location causes them
        // to spread out nicely.
        repulsion = 100;
        font = TEXT_FONT;
        fixed = false;
        typ = DEFAULT_TYPE;
        visibleEdgeCnt = 0;
        visible = false;
    }

    /**
     * Setter for <code>BACK_FIXED_COLOR</code>.
     * 
     * @param color
     *            The color to set.
     */
    public void setNodeBackFixedColor(Color color) {
        BACK_FIXED_COLOR = color;
    }

    /**
     * Setter for <code>BACK_SELECT_COLOR</code>.
     * 
     * @param color
     *            The color to set.
     */
    public void setNodeBackSelectColor(Color color) {
        BACK_SELECT_COLOR = color;
    }

    /**
     * Setter for <code>BACK_DEFAULT_COLOR</code>.
     * 
     * @param color
     *            The color to set.
     */
    public void setNodeBackDefaultColor(Color color) {
        BACK_DEFAULT_COLOR = color;
    }

    /**
     * Setter for <code>BACK_HILIGHT_COLOR</code>.
     * 
     * @param color
     *            The color to set.
     */
    public void setNodeBackHilightColor(Color color) {
        BACK_HILIGHT_COLOR = color;
    }

    /**
     * Setter for <code>BORDER_DRAG_COLOR</code>.
     * 
     * @param color
     *            The color to set.
     */
    public void setNodeBorderDragColor(Color color) {
        BORDER_DRAG_COLOR = color;
    }

    /**
     * Setter for <code></code>.
     * 
     * @param color
     *            The color to set.
     */
    /**
     * Setter for <code>BORDER_MOUSE_OVER_COLOR</code>.
     * 
     * @param color
     *            The color to set.
     */
    public void setNodeBorderMouseOverColor(Color color) {
        BORDER_MOUSE_OVER_COLOR = color;
    }

    /**
     * Setter for <code>BORDER_INACTIVE_COLOR</code>.
     * 
     * @param color
     *            The color to set.
     */
    public void setNodeBorderInactiveColor(Color color) {
        BORDER_INACTIVE_COLOR = color;
    }

    /**
     * Setter for <code>TEXT_COLOR</code>.
     * 
     * @param color
     *            The color to set.
     */
    public void setNodeTextColor(Color color) {
        TEXT_COLOR = color;
    }

    /**
     * Setter for <code>TEXT_FONT</code>.
     * 
     * @param font
     *            The font to set.
     */
    public void setNodeTextFont(Font font) {
        TEXT_FONT = font;
    }

    /**
     * Setter for <code>DEFAULT_TYPE</code>.
     * 
     * @param type
     *            The type to set.
     */
    public void setNodeType(int type) {
        DEFAULT_TYPE = type;
    }

    /**
     * Setter for <code>id</code>.
     * 
     * @param id
     *            The id to set.
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Getter for <code>id</code>.
     * 
     * @return Returns the id
     */
    public String getID() {
        return id;
    }

    /**
     * Setter for the location of this Node.
     * 
     * @param p
     *            The location point to set.
     */
    public void setLocation(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    /**
     * Getter for the location of this Node.
     * 
     * @return Returns the location as <code>Point</code>.
     */
    public Point getLocation() {
        return new Point((int) x, (int) y);
    }

    /**
     * Setter for <code>visible</code>.
     * 
     * @param visible
     *            The value to set.
     */

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Getter for <code>visible</code>.
     * 
     * @return Returns <code>true</code> if node is visible, otherwise
     *         <code>false</code>.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set the type of this Node to the int <tt>type</tt>.
     * 
     * @see TYPE_RECTANGLE
     * @see TYPE_ROUNDRECT
     * @see TYPE_ELLIPSE
     * @see TYPE_CIRCLE
     */

    /**
     * Setter for type.
     * 
     * @param type
     *            The type to set.
     */
    public void setType(int type) {
        typ = type;
    }

    /**
     * Return the <code>typ</code> of this Node.
     * 
     * @return Returns the <code>typ</code>.
     * @see TYPE_RECTANGLE
     * @see TYPE_ROUNDRECT
     * @see TYPE_ELLIPSE
     * @see TYPE_CIRCLE
     */
    public int getType() {
        return typ;
    }

    /** Set the font of this Node to the Font <tt>font</tt>. */
    public void setFont(Font font) {
        this.font = font;
    }

    /** Returns the font of this Node as a Font */
    public Font getFont() {
        return font;
    }

    /** Set the background color of this Node to the Color <tt>bgColor</tt>. */
    public void setBackColor(Color bgColor) {
        backColor = bgColor;
    }

    /**
     * Return the background color of this Node as a Color.
     */
    public Color getBackColor() {
        return backColor;
    }

    /** Set the text color of this Node to the Color <tt>txtColor</tt>. */
    public void setTextColor(Color txtColor) {
        textColor = txtColor;
    }

    /**
     * Return the text color of this Node as a Color.
     */
    public Color getTextColor() {
        return textColor;
    }

    /** Set the label of this Node to the String <tt>label</tt>. */
    public void setLabel(String label) {
        lbl = label;
    }

    /**
     * Return the label of this Node as a String.
     */
    public String getLabel() {
        return lbl;
    }

    /** Set the fixed status of this Node to the boolean <tt>fixed</tt>. */
    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    /**
     * Returns true if this Node is fixed (in place).
     */
    public boolean getFixed() {
        return fixed;
    }

    // Added by Brendan to allow image to be set
    public void setImage(Image iconRef) {
        this.iconRef = iconRef;
    }

    // ....

    /**
     * Return the number of Edges in the cumulative Vector.
     * 
     * @deprecated this method has been replaced by the <tt>edgeCount()</tt>
     *             method.
     */
    public int edgeNum() {
        return edges.size();
    }

    /** Return the number of Edges in the cumulative Vector. */
    public int edgeCount() {
        return edges.size();
    }

    /**
     * Return an iterator over the Edges in the cumulative Vector, null if it is
     * empty.
     */
    /*
     * public Iterator getEdges() { if ( edges.size() == 0 ) return null; else
     * return edges.iterator(); }
     */

    /** Returns the local Edge count. */
    public int visibleEdgeCount() {
        return visibleEdgeCnt;
    }

    /** Return the Edge at int <tt>index</tt>. */
    public Edge edgeAt(int index) {
        return (Edge) edges.elementAt(index);
    }

    /** Add the Edge <tt>edge</tt> to the graph. */
    public void addEdge(Edge edge) {
        if (edge == null) {
            return;
        }
        edges.addElement(edge);
    }

    /** Remove the Edge <tt>edge</tt> from the graph. */
    public void removeEdge(Edge edge) {
        edges.removeElement(edge);
    }

    /** Return the width of this Node. */
    public int getWidth() {
        if (fontMetrics != null && lbl != null) {
            return fontMetrics.stringWidth(lbl) + 12;
        }
        return 10;
    }

    /** Return the height of this Node. */
    public int getHeight() {
        if (fontMetrics != null) {
            return fontMetrics.getHeight() + 6;
        }
        return 6;
    }

    /** Returns true if this Node intersects Dimension <tt>d</tt>. */
    public boolean intersects(Dimension d) {
        return (drawx > 0 && drawx < d.width && drawy > 0 && drawy < d.height);
    }

    /** Returns true if this Node contains the Point <tt>px,py</tt>. */
    public boolean containsPoint(double px, double py) {
        return ((px > drawx - getWidth() / 2) && (px < drawx + getWidth() / 2)
                && (py > drawy - getHeight() / 2) && (py < drawy + getHeight()
                / 2));
    }

    /** Returns true if this Node contains the Point <tt>p</tt>. */
    public boolean containsPoint(Point p) {
        return ((p.x > drawx - getWidth() / 2)
                && (p.x < drawx + getWidth() / 2)
                && (p.y > drawy - getHeight() / 2) && (p.y < drawy
                + getHeight() / 2));
    }

    /**
     * Paints the Node.
     * 
     * @param g
     *            The graphics.
     * @param tgPanel
     *            The TG panel.
     */
    public void paint(Graphics g, TGPanel tgPanel) {
        if (!intersects(tgPanel.getSize())) {
            return;
        }
        paintNodeBody(g, tgPanel);

        if (visibleEdgeCount() < edgeCount()) {
            int ix = (int) drawx;
            int iy = (int) drawy;
            int h = getHeight();
            int w = getWidth();
            int tagX = ix + (w - 7) / 2 - 2 + w % 2;
            int tagY = iy - h / 2 - 2;
            char character;
            int hiddenEdgeCount = edgeCount() - visibleEdgeCount();
            character = (hiddenEdgeCount < 9) ? (char) ('0' + hiddenEdgeCount)
                    : '*';
            paintSmallTag(g, tgPanel, tagX, tagY, Color.red, Color.white,
                    character);
        }
    }

    /**
     * Returns the paint border color.
     * 
     * @param tgPanel
     *            The TG panel.
     * @return Returns the color.
     */
    public Color getPaintBorderColor(TGPanel tgPanel) {
        if (this == tgPanel.getDragNode()) {
            return BORDER_DRAG_COLOR;
        } else if (this == tgPanel.getMouseOverN()) {
            return BORDER_MOUSE_OVER_COLOR;
        }
        return BORDER_INACTIVE_COLOR;
    }

    /**
     * Returns the paint background color for the given TG panel..
     * 
     * @param tgPanel
     *            the TG panel.
     * @return Returns the color.
     */
    public Color getPaintBackColor(TGPanel tgPanel) {
        if (this == tgPanel.getSelect()) {
            return BACK_SELECT_COLOR;
        }

        if (fixed) {
            return BACK_FIXED_COLOR;
        }
        if (markedForRemoval) {
            return BACK_MRF_COLOR;
        }
        if (justMadeLocal) {
            return BACK_JML_COLOR;
        }
        return backColor;
    }

    /**
     * Returns the paint text color for the given TG panel.
     * 
     * @param tgPanel
     *            The panel.
     * @return Returns <code>textColor</code> otherwise
     *         <code>Color.WHITE</code>.
     */
    public Color getPaintTextColor(TGPanel tgPanel) {
        if (this == tgPanel.getSelect()) {
            // @todo driven by color profiles
            // @todo add this property to the node (each node may have different
            // text color)
            // return Color.BLACK;
            return Color.WHITE;
        }
        return textColor;
    }

    /***************************************************************************
     * Paints the background of the node, along with its label public void
     * paintNodeBody( Graphics g, TGPanel tgPanel) { g.setFont(font);
     * fontMetrics = g.getFontMetrics(); int ix = (int)drawx; int iy =
     * (int)drawy; int h = getHeight(); int w = getWidth(); int r = h/2+1; //
     * arc radius Color borderCol = getPaintBorderColor(tgPanel);
     * g.setColor(borderCol); if ( typ == TYPE_ROUNDRECT ) { g.fillRoundRect(ix -
     * w/2, iy - h / 2, w, h, r, r); } else if ( typ == TYPE_ELLIPSE ) {
     * g.fillOval(ix - w/2, iy - h / 2, w, h ); } else if ( typ == TYPE_CIRCLE ) { //
     * just use width for both dimensions g.fillOval(ix - w/2, iy - w / 2, w, w ); }
     * else { // TYPE_RECTANGLE g.fillRect(ix - w/2, iy - h / 2, w, h); } Color
     * backCol = getPaintBackColor(tgPanel); g.setColor(backCol); if ( typ ==
     * TYPE_ROUNDRECT ) { g.fillRoundRect(ix - w/2+2, iy - h / 2+2, w-4, h-4, r,
     * r ); } else if ( typ == TYPE_ELLIPSE ) { g.fillOval(ix - w/2+2, iy - h /
     * 2+2, w-4, h-4 ); } else if ( typ == TYPE_CIRCLE ) { g.fillOval(ix -
     * w/2+2, iy - w / 2+2, w-4, w-4 ); } else { // TYPE_RECTANGLE g.fillRect(ix -
     * w/2+2, iy - h / 2+2, w-4, h-4); } Color textCol =
     * getPaintTextColor(tgPanel); g.setColor(textCol); g.drawString(lbl, ix -
     * fontMetrics.stringWidth(lbl)/2, iy + fontMetrics.getDescent() +1); }
     */

    /**
     * Paints the background of the node, along with its label. Modified by
     * Brendan to draw a 16x16 icon on the Node.
     * 
     * @param g
     *            The graphic.
     * @param tgPanel
     *            the TGPanel.
     */
    public void paintNodeBody(Graphics g, TGPanel tgPanel) {
        Graphics2D g2 = (Graphics2D) g;
        if (SpidersGraph.antialiased) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        g2.setFont(font);
        fontMetrics = g2.getFontMetrics();

        int ix = (int) drawx;
        int iy = (int) drawy;
        int h = getHeight();

        // Modified by Brendan
        int w = getWidth() + 16;

        int r = h / 2 + 1; // arc radius

        Color borderCol = getPaintBorderColor(tgPanel);
        g2.setColor(borderCol);

        if (typ == TYPE_ROUNDRECT) {
            g2.fillRoundRect(ix - w / 2, iy - h / 2, w, h, r, r);
        } else if (typ == TYPE_ELLIPSE) {
            g2.fillOval(ix - w / 2, iy - h / 2, w, h);
        } else if (typ == TYPE_CIRCLE) { // just use width for both
            // dimensions
            g2.fillOval(ix - w / 2, iy - w / 2, w, w);
        } else { // TYPE_RECTANGLE
            g2.fillRect(ix - w / 2, iy - h / 2, w, h);
        }

        Color backCol = getPaintBackColor(tgPanel);
        g2.setColor(backCol);

        if (typ == TYPE_ROUNDRECT) {
            g2
                    .fillRoundRect(ix - w / 2 + 2, iy - h / 2 + 2, w - 4,
                            h - 4, r, r);
        } else if (typ == TYPE_ELLIPSE) {
            g2.fillOval(ix - w / 2 + 2, iy - h / 2 + 2, w - 4, h - 4);
        } else if (typ == TYPE_CIRCLE) {
            g2.fillOval(ix - w / 2 + 2, iy - w / 2 + 2, w - 4, w - 4);
        } else { // TYPE_RECTANGLE
            g2.fillRect(ix - w / 2 + 2, iy - h / 2 + 2, w - 4, h - 4);
        }

        Color textCol = getPaintTextColor(tgPanel);
        g2.setColor(textCol);

        // added by Brendan
        if (iconRef != null) {
            g2.drawImage(iconRef, ix - w / 2 + 4, iy - h / 2 + 2, tgPanel);
            g2.drawString(lbl, (ix - fontMetrics.stringWidth(lbl) / 2) + 8, iy
                    + fontMetrics.getDescent() + 1);
        } else {
            g2.drawString(lbl, (ix - fontMetrics.stringWidth(lbl) / 2), iy
                    + fontMetrics.getDescent() + 1);
        }
    }

    /**
     * Paints a tag with containing a character in a small font.
     * 
     * @param g
     *            the graphic.
     * @param tgPanel
     *            the TG panel.
     * @param tagX
     *            the tag x.
     * @param tagY
     *            the tag y.
     * @param backCol
     *            the background color.
     * @param textCol
     *            the text color.
     * @param character
     *            the character.
     */
    public void paintSmallTag(Graphics g, TGPanel tgPanel, int tagX, int tagY,
            Color backCol, Color textCol, char character) {
        Graphics2D g2 = (Graphics2D) g;
        if (SpidersGraph.antialiased) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        g2.setColor(backCol);
        g2.fillRect(tagX, tagY, 8, 8);
        g2.setColor(textCol);
        g2.setFont(SMALL_TAG_FONT);
        g2.drawString("" + character, tagX + 2, tagY + 7);
    }

    /**
     * Getter for <code>strUrl</code>. Modifications by Lutz Dornbusch
     * 
     * @return Returns the string url.
     */
    public String getURL() {
        return strUrl;
    }

    /**
     * Setter for <code>strUrl</code>.
     * 
     * @param strUrl
     *            The string url to set.
     */
    public void setURL(String strUrl) {
        this.strUrl = strUrl;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "label=" + lbl + "; uri=" + strUrl + "; id=" + id;
    }

    /**
     * Getter for <code>x</code>.
     * 
     * @return Returns the x.
     */
    public double getX() {
        return this.x;
    }

    /**
     * Setter for <code>x</code>.
     * 
     * @param x
     *            The x to set.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Getter for <code>y</code>.
     * 
     * @return Returns the y.
     */
    public double getY() {
        return this.y;
    }

    /**
     * Setter for <code>y</code>.
     * 
     * @param y
     *            The y to set.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Getter for <code>dx</code>.
     * 
     * @return Returns the dx.
     */
    public double getDx() {
        return this.dx;
    }

    /**
     * Setter for <code>dx</code>.
     * 
     * @param dx
     *            The dx to set.
     */
    public void setDx(double dx) {
        this.dx = dx;
    }

    /**
     * Getter for <code>dy</code>.
     * 
     * @return Returns the dy.
     */
    public double getDy() {
        return this.dy;
    }

    /**
     * Setter for <code>dy</code>.
     * 
     * @param dy
     *            The dy to set.
     */
    public void setDy(double dy) {
        this.dy = dy;
    }

} // end com.touchgraph.graphlayout.Node