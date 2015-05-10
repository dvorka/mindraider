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

package com.touchgraph.graphlayout.interaction;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import com.emental.mindraider.ui.graph.spiders.GraphPopUpBuilder;
import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGException;
import com.touchgraph.graphlayout.TGPanel;


// import javax.swing.*;
// import javax.swing.event.*;

/**
 * GLNavigateUI. User interface for moving around the graph, as opposed to editing.
 * @author Alexander Shapiro
 * @author Murray Altheim (abstracted GLPanel to TGScrollPane interface)
 * @version 1.22-jre1.1 $Id: GLNavigateUI.java,v 1.2 2011/12/03 17:49:44 mindraider Exp $
 */
public class GLNavigateUI extends TGUserInterface
{

    /**
     * The GL panel.
     */
    GLPanel glPanel;

    /**
     * The TG panel.
     */
    TGPanel tgPanel;

    /**
     * The mouse listener.
     */
    GLNavigateMouseListener ml;

    /**
     * The hv drag ui.
     */
    TGAbstractDragUI hvDragUI;

    /**
     * The rotate drag ui.
     */
    TGAbstractDragUI rotateDragUI;

    /**
     * The drag node ui.
     */
    DragNodeUI dragNodeUI;

    /**
     * The locality scroll.
     */
    LocalityScroll localityScroll;

    /**
     * The node JPopup.
     */
    JPopupMenu nodePopup;

    /**
     * The edge JPopup.
     */
    JPopupMenu edgePopup;

    /**
     * The popup node.
     */
    private Node popupNode;

    /**
     * The popup edge.
     */
    private Edge popupEdge;

    /**
     * Constructor.
     * @param glp the GL panel.
     */
    public GLNavigateUI(GLPanel glp)
    {
        glPanel = glp;
        tgPanel = glPanel.getTGPanel();

        localityScroll = glPanel.getLocalityScroll();
        hvDragUI = glPanel.getHVScroll().getHVDragUI();
        rotateDragUI = glPanel.getRotateScroll().getRotateDragUI();
        // hvRotateDragUI = new HVRotateDragUI(tgPanel,
        // glPanel.getHVScroll(), glPanel.getRotateScroll());
        dragNodeUI = new DragNodeUI(tgPanel);

        ml = new GLNavigateMouseListener();
        // TODO setUpNodePopup(glp);
        // TODO setUpEdgePopup(glp);
        setUpNodePopup();
        setUpEdgePopup();
    }

    /**
     * @see com.touchgraph.graphlayout.interaction.TGUserInterface#activate()
     */
    public void activate()
    {
        tgPanel.addMouseListener(ml);
    }

    /**
     * @see com.touchgraph.graphlayout.interaction.TGUserInterface#deactivate()
     */
    public void deactivate()
    {
        tgPanel.removeMouseListener(ml);
    }

    /**
     * The GLNavigateMouseListener class.
     * @author Alexander Shapiro
     * @version $Revision: 1.2 $ ($Author: mindraider $)
     */
    class GLNavigateMouseListener extends MouseAdapter
    {

        /**
         * Logger for this class.
         */
        private final Logger logger = Logger.getLogger(GLNavigateMouseListener.class);

        /**
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent e)
        {
            Node mouseOverN = tgPanel.getMouseOverN();

            if (e.getModifiers() == MouseEvent.BUTTON1_MASK)
            {
                if (mouseOverN == null)
                {
                    hvDragUI.activate(e);
                }
                else
                {
                    dragNodeUI.activate(e);
                }
            }
        }

        /**
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e)
        {
            Node mouseOverN = tgPanel.getMouseOverN();
            if (e.getModifiers() == MouseEvent.BUTTON1_MASK)
            {
                if (mouseOverN != null)
                {
                    if (e.getClickCount() == 1)
                    {
                        tgPanel.setSelect(mouseOverN);
                    }
                    else
                    {
                        tgPanel.setDoubleSelect(mouseOverN);
                    }
                    glPanel.getHVScroll().slowScrollToCenter(mouseOverN);

                    try
                    {
                        tgPanel.setLocale(mouseOverN, localityScroll.getLocalityRadius());
                    }
                    catch (TGException ex)
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("mouseClicked() - Error setting locale");
                        }
                        logger.error("mouseClicked(MouseEvent)", ex);
                    }
                }
            }
        }

        /**
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent e)
        {
            if (e.isPopupTrigger())
            {
                popupNode = tgPanel.getMouseOverN();
                popupEdge = tgPanel.getMouseOverE();
                if (popupNode != null)
                {
                    tgPanel.setMaintainMouseOver(true);
                    // nodePopup.show(e.getComponent(), e.getX(), e.getY());
                    nodePopup.show(tgPanel, e.getX(), e.getY());
                }
                else if (popupEdge != null)
                {
                    tgPanel.setMaintainMouseOver(true);
                    // edgePopup.show(e.getComponent(), e.getX(), e.getY());
                    edgePopup.show(tgPanel, e.getX(), e.getY());
                }
                else
                {
                    // glPanel.glPopup.show(e.getComponent(), e.getX(), e.getY());
                    glPanel.glPopup.show(tgPanel, e.getX(), e.getY());
                }
            }
            else
            {
                tgPanel.setMaintainMouseOver(false);
            }
        }

    }

    /**
     * Setup the node popup.
     */
    private void setUpNodePopup()
    {
        nodePopup = GraphPopUpBuilder.buildNavigationNodePopup(tgPanel, this);
    }

    /**
     * Setup the edge popup.
     */
    private void setUpEdgePopup()
    {
        edgePopup = GraphPopUpBuilder.buildNavigationEdgePopup(tgPanel, this);
    }


    /**
     * Getter for <code>popupEdge</code>.
     * @return Returns the popupEdge.
     */
    public Edge getPopupEdge()
    {
        return this.popupEdge;
    }


    /**
     * Setter for <code>popupEdge</code>.
     * @param popupEdge The popupEdge to set.
     */
    public void setPopupEdge(Edge popupEdge)
    {
        this.popupEdge = popupEdge;
    }


    /**
     * Getter for <code>popupNode</code>.
     * @return Returns the popupNode.
     */
    public Node getPopupNode()
    {
        return this.popupNode;
    }


    /**
     * Setter for <code>popupNode</code>.
     * @param popupNode The popupNode to set.
     */
    public void setPopupNode(Node popupNode)
    {
        this.popupNode = popupNode;
    }
}
