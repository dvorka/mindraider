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

package com.touchgraph.graphlayout.graphelements;

import org.apache.log4j.Logger;

import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGException;

/**
 * VisibleLocality: Extends Locality to spefically handle the Nodes + Edges that
 * are visible on screen. The visible attribute of the nodes + edges is set to
 * true when they appear on screen, and false when they are removed from screen.
 * Locality is used in conjunction with LocalityUtils, which handle locality
 * shift animations.
 * 
 * @author Alexander Shapiro
 */
public class VisibleLocality extends Locality {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger
            .getLogger(VisibleLocality.class);

    /**
     * Constructor.
     * 
     * @param ges
     *            the graph elt set.
     */
    public VisibleLocality(GraphEltSet ges) {
        super(ges);
    }

    /**
     * @see com.touchgraph.graphlayout.graphelements.GraphEltSet#addNode(com.touchgraph.graphlayout.Node)
     */
    public synchronized void addNode(Node node) throws TGException {
        super.addNode(node);
        node.setVisible(true);
    }

    /**
     * @see com.touchgraph.graphlayout.graphelements.GraphEltSet#addEdge(com.touchgraph.graphlayout.Edge)
     */
    public void addEdge(Edge edge) {
        if (!contains(edge)) {
            super.addEdge(edge);
            Node from = edge.getFrom();
            edge.setFrom(from);
            from.visibleEdgeCnt++;

            Node to = edge.getTo();
            to.visibleEdgeCnt++;
            edge.setTo(to);
        }
    }

    /**
     * @see com.touchgraph.graphlayout.graphelements.Locality#removeEdge(com.touchgraph.graphlayout.Edge)
     */
    public boolean removeEdge(Edge edge) {
        boolean removed = super.removeEdge(edge);
        if (removed) {
            edge.setVisible(false);
            Node from = edge.getFrom();
            from.visibleEdgeCnt--;
            edge.setFrom(from);

            Node to = edge.getTo();
            to.visibleEdgeCnt--;
            edge.setTo(to);
        }
        return removed;
    }

    /**
     * @see com.touchgraph.graphlayout.graphelements.Locality#removeNode(com.touchgraph.graphlayout.Node)
     */
    public boolean removeNode(Node node) {
        boolean removed = super.removeNode(node);
        if (removed) {
            node.setVisible(false);
        }
        return removed;
    }

    /**
     * @see com.touchgraph.graphlayout.graphelements.Locality#removeAll()
     */
    public synchronized void removeAll() {
        for (int i = 0; i < nodeCount(); i++) {
            nodeAt(i).setVisible(false);
        }
        for (int i = 0; i < edgeCount(); i++) {
            edgeAt(i).setVisible(false);
        }
        super.removeAll();
    }

    /**
     * Update locality from visibility.
     * 
     * @throws TGException
     *             the a TG Exception
     */
    public void updateLocalityFromVisibility() throws TGException {
        // for (int i = 0 ; i < completeEltSet.nodeCount(); i++) {
        // Node n = nodeAt(i);
        TGForEachNode fen = new TGForEachNode() {

            public void forEachNode(Node node) {
                try {
                    if (node.isVisible() && !contains(node)) {
                        addNode(node);
                    } else if (!node.isVisible() && contains(node)) {
                        removeNode(node);
                    }
                } catch (TGException ex) {
                    logger.error("forEachNode(Node)", ex);
                }
            }
        };
        completeEltSet.forAllNodes(fen);

        // for (int i = 0 ; i < edgeCount(); i++) {
        // Edge e = edgeAt(i);
        TGForEachEdge fee = new TGForEachEdge() {

            public void forEachEdge(Edge edge) {
                if (edge.isVisible() && !contains(edge)) {
                    addEdge(edge);
                } else if (!edge.isVisible() && contains(edge)) {
                    removeEdge(edge);
                }
            }
        };
        completeEltSet.forAllEdges(fee);
    }
}