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

import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.Node;


// import java.util.Collection;
// import java.util.Iterator;

/**
 * ImmutableGraphEltSet provides access to the elements of GraphElementSet that does not allow for addition or deletion
 * of nodes or edges.
 * @author Alexander Shapiro
 * @version 1.22-jre1.1 $Id: ImmutableGraphEltSet.java,v 1.1 2007/11/10 23:36:18 mindraider Exp $
 */
public interface ImmutableGraphEltSet
{

    /**
     * Return the number of Nodes in the cumulative Vector.
     * @return the number of nodes.
     */
    public int nodeCount();

    /**
     * Return the current Node count.
     * @return the number of current node count.
     * @deprecated this method has been replaced by the <tt>nodeCount()</tt> method.
     */
    public int nodeNum();

    /*
     * Return an iterator over the Nodes in the cumulative Vector, null if it is empty. public Iterator getNodes();
     */

    /**
     * Return the number of Edges in the cumulative Vector.
     * @return the edge count.
     */
    public int edgeCount();

    /**
     * Return the current Edge count.
     * @return the count of current edge.
     * @deprecated this method has been replaced by the <tt>edgeCount()</tt> method.
     */
    public int edgeNum();

    /*
     * Return an iterator over the Edges in the cumulative Vector, null if it is empty. public Iterator getEdges();
     */

    /**
     * Return the Node whose ID matches the String <tt>id</tt>, null if no match is found.
     * @param id the string to find.
     */
    public Node findNode(String id);

    /*
     * Return a Collection of all Nodes whose label matches the String <tt>label</tt>, null if no match is found.
     * public Collection findNodesByLabel( String label );
     */

    /**
     * Return the first Nodes whose label contains the String <tt>substring</tt>, null if no match is found.
     * @param the substring to check.
     * @return the first node if found, otherwise <code>null</code>.
     */
    public Node findNodeLabelContaining(String substring);

    /**
     * Return an Edge spanning Node <tt>from</tt> to Node <tt>to</tt>.
     * @param from the start node.
     * @param to the end node.
     * @return the edge between nodes.
     */
    public Edge findEdge(Node from, Node to);

    /**
     * Returns a random node, or null if none exist (for making random graphs).
     * @return a random node
     */
    public Node getRandomNode();

    /**
     * Return the first Node
     * @return Returns the first <code>node</code>, otherwise <code>null</code>.
     */
    public Node getFirstNode();

    /**
     * Iterates through all the nodes.
     * @param fen the TGForEachNode.
     */
    public void forAllNodes(TGForEachNode fen);

    /**
     * Iterates through pairs of Nodes.
     * @param fenp TGForEachNodePair.
     */
    public void forAllNodePairs(TGForEachNodePair fenp);

    /**
     * Iterates through Edges.
     * @param fee the TGForEachEdge.
     */
    public void forAllEdges(TGForEachEdge fee);
}