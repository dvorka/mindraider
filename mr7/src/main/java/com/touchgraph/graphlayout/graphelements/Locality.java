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

import java.util.Vector;

import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGException;

/**
 * Locality: A way of representing a subset of a larger set of nodes. Allows for
 * both manipulation of the subset, and manipulation of the larger set. For
 * instance, one can call removeNode to delete it from the subset, or deleteNode
 * to remove it from the larger set. Locality is used in conjunction with
 * LocalityUtils, which handle locality shift animations. More synchronization
 * will almost definitely be required.
 * 
 * @author Alexander Shapiro
 */
public class Locality extends GraphEltSet {

    /**
     * The complete elt set.
     */
    protected GraphEltSet completeEltSet;

    /**
     * Constructor.
     * 
     * @param ges
     *            the graphic elt set.
     */
    public Locality(GraphEltSet ges) {
        super();
        completeEltSet = ges;
    }

    /**
     * Getter for the complete graphic elt set.
     * 
     * @return Returns the <code>completeEltSet</code>.
     */
    public GraphEltSet getCompleteEltSet() {
        return completeEltSet;
    }

    /**
     * @see com.touchgraph.graphlayout.graphelements.GraphEltSet#addNode(com.touchgraph.graphlayout.Node)
     */
    public synchronized void addNode(Node n) throws TGException {
        if (!contains(n)) {
            super.addNode(n);
            // If a new Node is created, and then added to Locality, then add
            // the new edge
            // to completeEltSet as well.
            if (!completeEltSet.contains(n)) {
                completeEltSet.addNode(n);
            }
        }
    }

    /**
     * @see com.touchgraph.graphlayout.graphelements.GraphEltSet#addEdge(com.touchgraph.graphlayout.Edge)
     */
    public void addEdge(Edge e) {
        if (!contains(e)) {
            edges.addElement(e);
            // If a new Edge is created, and then added to Locality, then add
            // the new edge
            // to completeEltSet as well.
            if (!completeEltSet.contains(e)) {
                completeEltSet.addEdge(e);
            }
        }
    }

    /**
     * Add a node with edges.
     * 
     * @param n
     *            the node to add.
     * @throws TGException
     *             a TG exception.
     */
    public synchronized void addNodeWithEdges(Node n) throws TGException {
        addNode(n);
        for (int i = 0; i < n.edgeCount(); i++) {
            Edge e = n.edgeAt(i);
            if (contains(e.getOtherEndpt(n))) {
                addEdge(e);
            }
        }

    }

    /**
     * Add all nodes.
     * 
     * @throws TGException
     *             a TG exception.
     */
    public synchronized void addAll() throws TGException {
        synchronized (completeEltSet) {
            for (int i = 0; i < completeEltSet.nodeCount(); i++) {
                addNode(completeEltSet.nodeAt(i));
            }
            for (int i = 0; i < completeEltSet.edgeCount(); i++) {
                addEdge(completeEltSet.edgeAt(i));
            }
        }
    }

    /**
     * @see com.touchgraph.graphlayout.graphelements.ImmutableGraphEltSet#findEdge(com.touchgraph.graphlayout.Node,
     *      com.touchgraph.graphlayout.Node)
     */
    public Edge findEdge(Node from, Node to) {
        Edge foundEdge = super.findEdge(from, to);
        if (foundEdge != null && edges.contains(foundEdge)) {
            return foundEdge;
        }
        return null;
    }

    /**
     * @see com.touchgraph.graphlayout.graphelements.GraphEltSet#deleteEdge(com.touchgraph.graphlayout.Edge)
     */
    public boolean deleteEdge(Edge e) {
        if (e == null) {
            return false;
        }
        removeEdge(e);
        return completeEltSet.deleteEdge(e);
    }

    /**
     * @see com.touchgraph.graphlayout.graphelements.GraphEltSet#deleteEdges(java.util.Vector)
     */
    public synchronized void deleteEdges(Vector edgesToDelete) {
        removeEdges(edgesToDelete);
        completeEltSet.deleteEdges(edgesToDelete);
    }

    /**
     * Remove edge.
     * 
     * @param e
     *            the edge to remove.
     * @return Returns <code>true</code> if is removed, otherwise
     *         <code>false</code>.
     */
    public boolean removeEdge(Edge e) {
        if (e == null) {
            return false;
        }
        if (edges.removeElement(e)) {
            return true;
        }
        return false;
    }

    /**
     * Remove a vector of edges.
     * 
     * @param edgesToRemove
     *            the <code>Vector</code> of edges to remove.
     */
    public synchronized void removeEdges(Vector edgesToRemove) {
        for (int i = 0; i < edgesToRemove.size(); i++) {
            removeEdge((Edge) edgesToRemove.elementAt(i));
        }
    }

    /**
     * @see com.touchgraph.graphlayout.graphelements.GraphEltSet#deleteNode(com.touchgraph.graphlayout.Node)
     */
    public boolean deleteNode(Node node) {
        if (node == null) {
            return false;
        }
        removeNode(node);
        return completeEltSet.deleteNode(node);
    }

    /**
     * @see com.touchgraph.graphlayout.graphelements.GraphEltSet#deleteNodes(java.util.Vector)
     */
    public synchronized void deleteNodes(Vector nodesToDelete) {
        removeNodes(nodesToDelete);
        completeEltSet.deleteNodes(nodesToDelete);
    }

    /**
     * Remove the node.
     * 
     * @param node
     *            the node to remove.
     * @return Returns <code>true</code> if node is removed, otherwise
     *         <code>false</code>.
     */
    public boolean removeNode(Node node) {
        if (node == null) {
            return false;
        }
        if (!nodes.removeElement(node)) {
            return false;
        }

        String id = node.getID();
        if (id != null) {
            nodeIDRegistry.remove(id); // remove from registry
        }

        for (int i = 0; i < node.edgeCount(); i++) {
            removeEdge(node.edgeAt(i));
        }
        return true;
    }

    /**
     * Remove a vector of nodes.
     * 
     * @param nodesToRemove
     *            The <code>Vector</code> of nodes to remove.
     */
    public synchronized void removeNodes(Vector nodesToRemove) {
        for (int i = 0; i < nodesToRemove.size(); i++) {
            removeNode((Node) nodesToRemove.elementAt(i));
        }
    }

    /**
     * Remove all nodes.
     */
    public synchronized void removeAll() {
        super.clearAll();
    }

    /**
     * @see com.touchgraph.graphlayout.graphelements.GraphEltSet#clearAll()
     */
    public synchronized void clearAll() {
        removeAll();
        completeEltSet.clearAll();
    }
}
