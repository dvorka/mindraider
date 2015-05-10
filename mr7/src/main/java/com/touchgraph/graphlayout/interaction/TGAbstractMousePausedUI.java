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

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;

import com.touchgraph.graphlayout.TGPanel;


/**
 * TGAbstractMousePausedUI allows one to handle MousePaused events.
 * @author Alexander Shapiro
 * @version $Revision: 1.3 $ ($Author: mindraider $)
 */

public abstract class TGAbstractMousePausedUI extends TGUserInterface {
    /**
     * The mouse motion listener.
     */
    private AMPUIMouseMotionListener mml;

    /**
     * The mouse user interface listener.
     */
    private AMPUIMouseListener ml;

    /**
     * The TG panel.
     */
    protected TGPanel tgPanel;

    /**
     * The mouse position.
     */
    Point mousePos;

    /**
     * The pause thread.
     */
    PauseThread pauseThread;

    /**
     * Constructor with TGPanel <tt>tgp</tt>.
     * @param tgp the TG pane.
     */
    public TGAbstractMousePausedUI(TGPanel tgp)
    { // Instantiate this way to keep listening
        // for clicks until deactivate is called
        tgPanel = tgp;
        ml = new AMPUIMouseListener();
        mml = new AMPUIMouseMotionListener();
    }

    /**
     * @see com.touchgraph.graphlayout.interaction.TGUserInterface#activate()
     */
    public final void activate()
    {
        preActivate();
        tgPanel.addMouseMotionListener(mml);
        tgPanel.addMouseListener(ml);
    }

    /**
     * @see com.touchgraph.graphlayout.interaction.TGUserInterface#deactivate()
     */
    public final void deactivate()
    {
        tgPanel.removeMouseMotionListener(mml);
        tgPanel.removeMouseListener(ml);
        postDeactivate();
        super.deactivate(); // To activate parentUI from TGUserInterface
    }

    /**
     * The preactivate method.
     */
    public void preActivate()
    {
    }

    /**
     * The post deactivate method.
     */
    public void postDeactivate()
    {
    }

    /**
     * The mouse paused abstract method.
     * @param e the mouse event.
     */
    public abstract void mousePaused(MouseEvent e);

    /**
     * The mouse moved abstract method.
     * @param e the mouse event.
     */
    public abstract void mouseMoved(MouseEvent e);

    /**
     * The mouse dragged abstract method.
     * @param e the mouse event.
     */
    public abstract void mouseDragged(MouseEvent e);

    /**
     * The PauseThread class. Alexander Shapiro
     * @version $Revision: 1.3 $ ($Author: mindraider $)
     */
    class PauseThread extends Thread
    {

        /**
         * Logger for this class
         */
        private final Logger logger = Logger.getLogger(PauseThread.class);

        /**
         * The reset sleep flag.
         */
        boolean resetSleep;

        /**
         * The cancelled flag.
         */
        boolean cancelled;

        /**
         * Constructor.
         */
        PauseThread()
        {
            setDaemon(true);
            cancelled = false;
            start();
        }

        /**
         * Reset method.
         */
        void reset()
        {
            resetSleep = true;
            cancelled = false;
        }

        /**
         * Cancel method.
         */
        void cancel()
        {
            cancelled = true;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            try
            {
                do
                {
                    resetSleep = false;
                    sleep(250);
                }
                while (resetSleep);
                if (!cancelled)
                {
                    MouseEvent pausedEvent = new MouseEvent(
                        tgPanel,
                        MouseEvent.MOUSE_ENTERED,
                        0,
                        0,
                        mousePos.x,
                        mousePos.y,
                        0,
                        false);
                    mousePaused(pausedEvent);
                }
            }
            catch (Exception e)
            {
                logger.error("run()", e);
            }
        }
    }

    /**
     * Reset pause.
     */
    public void resetPause()
    {
        if (pauseThread != null && pauseThread.isAlive())
        {
            pauseThread.reset();
        }
        else
        {
            pauseThread = new PauseThread();
        }
    }

    /**
     * Cancel pause.
     */
    public void cancelPause()
    {
        if (pauseThread != null && pauseThread.isAlive())
        {
            pauseThread.cancel();
        }
    }

    /**
     * The AMPUIMouseMotionListener class.
     * @author Alexander Shapiro
     * @version $Revision: 1.3 $ ($Author: mindraider $)
     */
    private class AMPUIMouseMotionListener implements MouseMotionListener {
        /**
         * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
         */
        public void mouseMoved(MouseEvent e)
        {
            mousePos = e.getPoint();
            resetPause();
            TGAbstractMousePausedUI.this.mouseMoved(e);
        }

        /**
         * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
         */
        public void mouseDragged(MouseEvent e)
        {
            mousePos = e.getPoint();
            resetPause();
            TGAbstractMousePausedUI.this.mouseDragged(e);
        }
    }

    /**
     * The AMPUIMouseListener class.
     * @author Alexander Shapiro
     * @version $Revision: 1.3 $ ($Author: mindraider $)
     */
    private class AMPUIMouseListener extends MouseAdapter {

        /**
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent e)
        {
            cancelPause();
        }

        /**
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent e)
        {
            cancelPause();
        }

        /**
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        public void mouseExited(MouseEvent e)
        {
            // cancelPause();
        }
    }
}