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
import java.awt.Graphics;

/**
 * Edge.
 *
 * @author Alexander Shapiro
 * @version $Revision: 1.1 $ ($Author: mindraider $)
 */
public class Edge {

    /**
     * The edge type default constant .
     */
    public static final int EDGE_TYPE_DEFAULT = 1;

    /**
     * The edge type line constant.
     */
    public static final int EDGE_TYPE_LINE = 2;

    /**
     * The default color.
     */
    public static Color DEFAULT_COLOR = Color.decode("#006090");

    /**
     * The mouse over color.
     */
    public static Color MOUSE_OVER_COLOR = Color.decode("#ccddff");

    /**
     * The default length.
     */
    public static int DEFAULT_LENGTH = 40;

    /**
     * The node from.
     */
    private Node from;

    /**
     * The node to.
     */
    private Node to;

    /**
     * The color.
     */
    protected Color col;

    /**
     * The length.
     */
    protected int length;

    /**
     * The visible flag.
     */
    protected boolean visible;

    /**
     * The id.
     */
    protected String id;

    /**
     * The type.
     */
    private int type = EDGE_TYPE_DEFAULT;

    /**
     * Constructor.
     *
     * @param from
     *            the from node
     * @param to
     *            the to node
     * @param length
     *            the length
     */
    public Edge(Node from, Node to, int length) {
        this.from = from;
        this.to = to;
        this.length = length;
        col = DEFAULT_COLOR;
        visible = false;
    }

    /**
     * Constructor.
     *
     * @param from
     *            the from node
     * @param to
     *            the to node
     * @param length
     *            the length
     * @param type
     *            the type
     */
    public Edge(Node from, Node to, int length, int type) {
        this(from, to, length);
        this.type = type;
    }

    /**
     * Constructor. Uses the default length.
     *
     * @param from
     *            the from node
     * @param to
     *            the to node
     */
    public Edge(Node from, Node to) {
        this(from, to, DEFAULT_LENGTH);
    }

    /**
     * Setter for <code>DEFAULT_COLOR</code>.
     *
     * @param color
     *            the color to set.
     */
    public static void setEdgeDefaultColor(Color color) {
        DEFAULT_COLOR = color;
    }

    /**
     * Setter for <code>MOUSE_OVER_COLOR</code>.
     *
     * @param color
     *            the color to set.
     */
    public static void setEdgeMouseOverColor(Color color) {
        MOUSE_OVER_COLOR = color;
    }

    /**
     * Setter for <code>DEFAULT_LENGTH</code>.
     *
     * @param length
     *            the length to set.
     */
    public static void setEdgeDefaultLength(int length) {
        DEFAULT_LENGTH = length;
    }

    /**
     * Returns the starting <code>from</code> node of this edge as Node.
     *
     * @return Returns the node.
     */
    public Node getFrom() {
        return from;
    }

    /**
     * Returns the terminating <code>to</code> node of this edge as Node.
     *
     * @return Returns the node.
     */
    public Node getTo() {
        return to;
    }

    /**
     * Getter for the color <code>col</code> of this edge as Color.
     *
     * @return Returns the color.
     */
    public Color getColor() {
        return col;
    }

    /**
     * Setter for the color <code>col</code>.
     *
     * @param color
     *            the color to set.
     */
    public void setColor(Color color) {
        col = color;
    }

    /**
     * Returns the <code>id</code>..
     *
     * @return Returns the id.
     */
    public String getID() {
        return id;
    }

    /**
     * Setter for the <code>id</code>.
     *
     * @param id
     *            the string to set.
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Getter for <code>length</code> value.
     *
     * @return the length value.
     */
    public int getLength() {
        return length;
    }

    /**
     * Setter for <code>length</code> value.
     *
     * @param length
     *            the length to set.
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Setter for the <code>visible</code> value.
     *
     * @param visible
     *            the value to set.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Check the visibility of the Edge.
     *
     * @return Returns <code>true</code> or <code>false</code> according to
     *         the visible value.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Returns, given a point, the other.
     *
     * @param n
     *            the node. Could be <code>from</code> or <code>to</code>.
     * @return Returns the other node.
     */
    public Node getOtherEndpt(Node n) { // yields false results if Node n is not
                                        // an endpoint
        if (to != n) {
            return to;
        }
        return from;
    }

    /**
     * Switches the endpoints of the edge.
     */
    public void reverse() {
        Node temp = to;
        to = from;
        from = temp;
    }

    /**
     * Check if edge intersect the given dimension.
     *
     * @param d
     *            the dimension to check
     * @return Returns <code>true</code> if intersects, otherwise
     *         <code>false</code>.
     */
    public boolean intersects(Dimension d) {
        int x1 = (int) from.drawx;
        int y1 = (int) from.drawy;
        int x2 = (int) to.drawx;
        int y2 = (int) to.drawy;

        return (((x1 > 0 || x2 > 0) && (x1 < d.width || x2 < d.width)) && ((y1 > 0 || y2 > 0) && (y1 < d.height || y2 < d.height)));

    }

    /**
     * Return the distance from point.
     *
     * @param px
     *            the point x.
     * @param py
     *            the point y.
     * @return Returns the distance value.
     */
    public double distFromPoint(double px, double py) {
        double x1 = from.drawx;
        double y1 = from.drawy;
        double x2 = to.drawx;
        double y2 = to.drawy;

        if (px < Math.min(x1, x2) - 8 || px > Math.max(x1, x2) + 8 || py < Math.min(y1, y2) - 8
                || py > Math.max(y1, y2) + 8) {
            return 1000;
        }

        double dist = 1000;
        if (x1 - x2 != 0) {
            dist = Math.abs((y2 - y1) / (x2 - x1) * (px - x1) + (y1 - py));
        }
        if (y1 - y2 != 0) {
            dist = Math.min(dist, Math.abs((x2 - x1) / (y2 - y1) * (py - y1) + (x1 - px)));
        }

        return dist;
    }

    /**
     * Check if edge contains the given point.
     *
     * @param px
     *            the point x.
     * @param py
     *            the point y.
     * @return Returns <code>code</code> if point is contained, otherwise
     *         <code>false</code>.
     */
    public boolean containsPoint(double px, double py) {
        return distFromPoint(px, py) < 10;
    }

    /**
     * Paint arrow.
     *
     * @param g
     *            the graphics.
     * @param x1
     *            the from x.
     * @param y1
     *            the from y.
     * @param x2
     *            the to x.
     * @param y2
     *            the to y.
     * @param c
     *            the color.
     */
    public static void paintArrow(Graphics g, int x1, int y1, int x2, int y2, Color c) {
        paintArrow(g, x1, y1, x2, y2, c);
    }

    /**
     * Paint arrow.
     *
     * @param g
     *            the graphics.
     * @param x1
     *            the from x.
     * @param y1
     *            the from y.
     * @param x2
     *            the to x.
     * @param y2
     *            the to y.
     * @param c
     *            the color.
     * @param type
     *            the type.
     */
    public static void paintArrow(Graphics g, int x1, int y1, int x2, int y2, Color c, int type) {
        // Forget hyperbolic bending for now

        g.setColor(c);

        int x3 = x1;
        int y3 = y1;

        double dist = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        if (dist > 10) {
            double adjustDistRatio = (dist - 10) / dist;
            x3 = (int) (x1 + (x2 - x1) * adjustDistRatio);
            y3 = (int) (y1 + (y2 - y1) * adjustDistRatio);
        }

        x3 = (int) ((x3 * 4 + x1) / 5.0);
        y3 = (int) ((y3 * 4 + y1) / 5.0);

        switch (type) {
        case Edge.EDGE_TYPE_LINE:
            g.drawLine(x1, y1, x2, y2);
            break;
        default:
            g.drawLine(x3, y3, x2, y2);
            g.drawLine(x1, y1, x3, y3);
            g.drawLine(x1 + 1, y1, x3, y3);
            g.drawLine(x1 + 2, y1, x3, y3);
            g.drawLine(x1 + 3, y1, x3, y3);
            g.drawLine(x1 + 4, y1, x3, y3);
            g.drawLine(x1 - 1, y1, x3, y3);
            g.drawLine(x1 - 2, y1, x3, y3);
            g.drawLine(x1 - 3, y1, x3, y3);
            g.drawLine(x1 - 4, y1, x3, y3);
            g.drawLine(x1, y1 + 1, x3, y3);
            g.drawLine(x1, y1 + 2, x3, y3);
            g.drawLine(x1, y1 + 3, x3, y3);
            g.drawLine(x1, y1 + 4, x3, y3);
            g.drawLine(x1, y1 - 1, x3, y3);
            g.drawLine(x1, y1 - 2, x3, y3);
            g.drawLine(x1, y1 - 3, x3, y3);
            g.drawLine(x1, y1 - 4, x3, y3);
        }
    }

    /**
     * Paint.
     *
     * @param g
     *            the graphic
     * @param tgPanel
     *            the TG panel.
     */
    public void paint(Graphics g, TGPanel tgPanel) {
        Color c = (tgPanel.getMouseOverE() == this) ? MOUSE_OVER_COLOR : col;

        int x1 = (int) from.drawx;
        int y1 = (int) from.drawy;
        int x2 = (int) to.drawx;
        int y2 = (int) to.drawy;
        if (intersects(tgPanel.getSize())) {
            paintArrow(g, x1, y1, x2, y2, c, type);
        }
    }

    /**
     * Setter for <code>from</code>.
     *
     * @param from
     *            The from to set.
     */
    public void setFrom(Node from) {
        this.from = from;
    }

    /**
     * Setter for <code>to</code>.
     *
     * @param to
     *            The to to set.
     */
    public void setTo(Node to) {
        this.to = to;
    }
}
