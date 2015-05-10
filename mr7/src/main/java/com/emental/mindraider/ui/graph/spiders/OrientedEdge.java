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

import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGPanel;

/**
 * Extends standard touchgraph edge to provide both oriented and unorineted
 * edge, default edge is oriented.
 */
public class OrientedEdge extends Edge {

    /**
     * oriented state.
     */
    private boolean oriented;

    /**
     * Constructor with two Nodes and a length.
     * 
     * @param file
     *            from node
     * @param t
     *            to node
     * @param len
     *            the len
     * @param oriented
     *            <code>true</code>, if the edge should look like an orinted
     *            edge
     */
    public OrientedEdge(Node f, Node t, int len, boolean oriented) {
        super(f, t, len);
        this.oriented = oriented;
    }

    /**
     * Constructor with two Nodes, which uses a default length, by default
     * oriented will be constructed.
     * 
     * @param file
     *            the Node
     * @param t
     *            the Node
     */
    public OrientedEdge(Node f, Node t) {
        super(f, t);
        oriented = true;
    }

    /**
     * Returns <code>true</code> is this edge is oriented.
     * 
     * @return <code>true</code> means oriented, <code>false</code> means
     *         unoriented
     */
    public boolean isOriented() {
        return oriented;
    }

    /**
     * Sets oriented state of this edge.
     * 
     * @param oriented
     *            <code>true</code> means oriented, <code>false</code> means
     *            unoriented
     */
    public void setOriented(boolean oriented) {
        this.oriented = oriented;
    }

    /**
     * Paints this arrow.
     * 
     * @param g
     *            AWT graphics
     * @param tgPanel
     *            the TGPanel
     */
    public void paint(Graphics g, TGPanel tgPanel) {
        Color c = (tgPanel.getMouseOverE() == this) ? MOUSE_OVER_COLOR : col;

        int x1 = (int) getFrom().drawx;
        int y1 = (int) getFrom().drawy;
        int x2 = (int) getTo().drawx;
        int y2 = (int) getTo().drawy;
        if (intersects(tgPanel.getSize())) {
            paintCustomArrow(g, x1, y1, x2, y2, c);
        }
    }

    /**
     * Paint an arrow between nodes of this edge.
     * 
     * @param g
     *            AWT graphics
     * @param fromX
     *            from node, x axis
     * @param fromY
     *            from node, y axis
     * @param toX
     *            to node, x axis
     * @param toY
     *            to node, y axis
     * @param color
     *            color of arrow
     */
    public void paintCustomArrow(Graphics g, int fromX, int fromY, int toX,
            int toY, Color color) {
        if (oriented) {
            paintArrow(g, fromX, fromY, toX, toY, color);
        } else {
            g.setColor(color);
            g.drawLine(fromX, fromY, toX, toY);
        }
    }

}
