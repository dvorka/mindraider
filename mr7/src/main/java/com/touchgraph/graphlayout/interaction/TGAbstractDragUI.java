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
import java.awt.event.MouseMotionAdapter;

import com.touchgraph.graphlayout.TGPanel;


/**
 * TGAbstractDragUI allows one to write user interfaces that handle what happends when a mouse is pressed, dragged, and
 * released.
 * @author Alexander Shapiro
 * @version 1.22-jre1.1 $Id: TGAbstractDragUI.java,v 1.1 2007/11/10 23:36:24 mindraider Exp $
 */
public abstract class TGAbstractDragUI extends TGSelfDeactivatingUI
{

    /**
     * Used to differentiate between mouse pressed+dragged, and mouseClicked.
     */
    protected boolean mouseWasDragged;

    /**
     * The TG panel.
     */
    public TGPanel tgPanel;

    /**
     * The mouse listener.
     */
    private ADUIMouseListener ml;

    /**
     * The mouse motion listener.
     */
    private ADUIMouseMotionListener mml;

    // ............

    /**
     * Constructor with TGPanel <tt>tgp</tt>.
     * @param tgp the TG panel.
     */
    public TGAbstractDragUI(TGPanel tgp)
    {
        tgPanel = tgp;
        ml = new ADUIMouseListener();
        mml = new ADUIMouseMotionListener();
    }

    /**
     * @see com.touchgraph.graphlayout.interaction.TGUserInterface#activate()
     */
    public final void activate()
    {
        preActivate();
        tgPanel.addMouseListener(ml);
        tgPanel.addMouseMotionListener(mml);
        mouseWasDragged = false;
    }

    /**
     * The activate method.
     * @param e the mouse event.
     */
    public final void activate(MouseEvent e)
    {
        activate();
        mousePressed(e);
    }

    /**
     * @see com.touchgraph.graphlayout.interaction.TGUserInterface#deactivate()
     */
    public final void deactivate()
    {
        preDeactivate();
        tgPanel.removeMouseListener(ml);
        tgPanel.removeMouseMotionListener(mml);
        super.deactivate(); // To activate parentUI from TGUserInterface
    }

    /**
     * The pre-activate method.
     */
    public abstract void preActivate();

    /**
     * The pre-deactivate method.
     */
    public abstract void preDeactivate();

    /**
     * The mouse pressed abstract method.
     * @param e the mouse event.
     */
    public abstract void mousePressed(MouseEvent e);

    /**
     * The mouse dragged abstract method.
     * @param e the mouse event.
     */
    public abstract void mouseDragged(MouseEvent e);

    /**
     * The mouse released abstract method.
     * @param e the mouse event.
     */
    public abstract void mouseReleased(MouseEvent e);

    /**
     * The ADUIMouseListener class.
     * @author Alexander Shapiro
     * @version $Revision: 1.1 $ ($Author: mindraider $)
     */
    private class ADUIMouseListener extends MouseAdapter
    {

        /**
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent e)
        {
            TGAbstractDragUI.this.mousePressed(e);
        }

        /**
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent e)
        {
            TGAbstractDragUI.this.mouseReleased(e);
            if (selfDeactivate)
            {
                deactivate();
            }
        }
    }

    /**
     * The ADUIMouseMotionListener class.
     * @author Alexander Shapiro
     * @version $Revision: 1.1 $ ($Author: mindraider $)
     */
    private class ADUIMouseMotionListener extends MouseMotionAdapter
    {

        /**
         * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
         */
        public void mouseDragged(MouseEvent e)
        {
            mouseWasDragged = true;
            TGAbstractDragUI.this.mouseDragged(e);
        }
    }


    /**
     * Getter for <code>mouseWasDragged</code>.
     * @return Returns the mouseWasDragged.
     */
    public boolean isMouseWasDragged()
    {
        return this.mouseWasDragged;
    }


    /**
     * Setter for <code>mouseWasDragged</code>.
     * @param mouseWasDragged The mouseWasDragged to set.
     */
    public void setMouseWasDragged(boolean mouseWasDragged)
    {
        this.mouseWasDragged = mouseWasDragged;
    }
}