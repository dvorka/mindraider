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
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;

import javax.swing.JScrollBar;

import com.touchgraph.graphlayout.GraphListener;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGAbstractLens;
import com.touchgraph.graphlayout.TGLensSet;
import com.touchgraph.graphlayout.TGPanel;
import com.touchgraph.graphlayout.TGPoint2D;


// import javax.swing.*;

/**
 * HVScroll: Allows for scrolling horizontaly+vertically. This can be done in all sorts of ways, for instance by using
 * the scrollbars, or by dragging.
 * <p>
 * This code is more complex then it would seem it should be, because scrolling has to be independent of the screen
 * being warped by lenses. HVScroll needs to use the tgLensSet object because the offset is recorded in real
 * coordinates, while the user interacts with the drawn coordinates.
 * @author Alexander Shapiro
 * @version 1.22-jre1.1 $Id: HVScroll.java,v 1.4 2011/12/03 17:49:44 mindraider Exp $
 */
public class HVScroll implements GraphListener {

    private DScrollbar horizontalSB;

    private DScrollbar verticalSB;

    HVLens hvLens;

    HVDragUI hvDragUI;

    HVScrollToCenterUI hvScrollToCenterUI;

    public boolean scrolling;

    private boolean adjustmentIsInternal;

    private TGPanel tgPanel;

    private TGLensSet tgLensSet;

    TGPoint2D offset;

    // ............

    /**
     * Constructor with a TGPanel <tt>tgp</tt> and TGLensSet <tt>tgls</tt>.
     */
    public HVScroll(TGPanel tgp, TGLensSet tgls)
    {
        tgPanel = tgp;
        tgLensSet = tgls;

        offset = new TGPoint2D(0, 0);
        scrolling = false;
        adjustmentIsInternal = false;

        horizontalSB = new DScrollbar(Scrollbar.HORIZONTAL, 0, 100, -1000, 1100);
        horizontalSB.setBlockIncrement(100);
        horizontalSB.setUnitIncrement(20);

        horizontalSB.addAdjustmentListener(new horizAdjustmentListener());

        verticalSB = new DScrollbar(Scrollbar.VERTICAL, 0, 100, -1000, 1100);
        verticalSB.setBlockIncrement(100);
        verticalSB.setUnitIncrement(20);

        verticalSB.addAdjustmentListener(new vertAdjustmentListener());

        hvLens = new HVLens();
        hvDragUI = new HVDragUI(); // Hopefully this approach won't eat too much memory
        hvScrollToCenterUI = new HVScrollToCenterUI();

        tgPanel.addGraphListener(this);
    }

    public JScrollBar getHorizontalSB()
    {
        return horizontalSB;
    }

    public JScrollBar getVerticalSB()
    {
        return verticalSB;
    }

    public HVDragUI getHVDragUI()
    {
        return hvDragUI;
    }

    /**
     * Returns the <code>hvLens</code>.
     * @return the lens
     */
    public HVLens getLens()
    {
        return hvLens;
    }

    /**
     * Returns the <code>hvScrollToCenterUI</code>.
     * @return the hv scroll to center ui.
     */
    public TGAbstractClickUI getHVScrollToCenterUI()
    {
        return hvScrollToCenterUI;
    }

    /**
     * Returns the top left draw point.
     * @return Returns the <code>TGPoint2D</code>.
     */
    public TGPoint2D getTopLeftDraw()
    {
        TGPoint2D tld = tgPanel.getTopLeftDraw();
        tld.setLocation(tld.x - tgPanel.getSize().width / 4, tld.y - tgPanel.getSize().height / 4);
        return tld;
    }

    /**
     * Returns the bottom right draw point.
     * @return Returns the <code>TGPoint2D</code>.
     */
    public TGPoint2D getBottomRightDraw()
    {
        TGPoint2D brd = tgPanel.getBottomRightDraw();
        brd.setLocation(brd.x + tgPanel.getSize().width / 4, brd.y + tgPanel.getSize().height / 4);
        return brd;
    }

    /**
     * Returns the draw center point.
     * @return Returns the <code>TGPoint2D</code>.
     */
    public TGPoint2D getDrawCenter()
    { // Should probably be called from tgPanel
        return new TGPoint2D(tgPanel.getSize().width / 2, tgPanel.getSize().height / 2);
    }

    /**
     * The no repaint thread.
     */
    Thread noRepaintThread;

    /**
     *
     * @see com.touchgraph.graphlayout.GraphListener#graphMoved()
     */
    public void graphMoved()
    { // From GraphListener interface
        if (tgPanel.getDragNode() == null && tgPanel.getSize().height > 0)
        {
            TGPoint2D drawCenter = getDrawCenter();

            TGPoint2D tld = getTopLeftDraw();
            TGPoint2D brd = getBottomRightDraw();

            double newH = (-(tld.x - drawCenter.x) / (brd.x - tld.x) * 2000 - 1000);
            double newV = (-(tld.y - drawCenter.y) / (brd.y - tld.y) * 2000 - 1000);

            boolean beyondBorder;
            beyondBorder = true;

            if (newH < horizontalSB.getMaximum()
                && newH > horizontalSB.getMinimum()
                && newV < verticalSB.getMaximum()
                && newV > verticalSB.getMinimum())
            {
                beyondBorder = false;
            }

            adjustmentIsInternal = true;
            horizontalSB.setDValue(newH);
            verticalSB.setDValue(newV);
            adjustmentIsInternal = false;

            if (beyondBorder)
            {
                adjustHOffset();
                adjustVOffset();
                tgPanel.repaint();
            }
        }

        if (noRepaintThread != null && noRepaintThread.isAlive())
        {
            noRepaintThread.interrupt();
        }
        noRepaintThread = new Thread()
        {

            public void run()
            {
                try
                {
                    Thread.sleep(40); // Wait 40 milliseconds before repainting
                }
                catch (InterruptedException ex)
                {
                    //
                }
            }
        };
        noRepaintThread.start();

    }

    /**
     * @see com.touchgraph.graphlayout.GraphListener#graphReset()
     */
    public void graphReset()
    { // From GraphListener interface
        horizontalSB.setDValue(0);
        verticalSB.setDValue(0);

        adjustHOffset();
        adjustVOffset();
    }

    /**
     * The DScrollbar.
     * @author Alexander Shapiro
     * @version $Revision: 1.4 $ ($Author: mindraider $)
     */
    class DScrollbar extends JScrollBar {

        /**
         * The serial version uid for serialization.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The double value.
         */
        private double doubleValue;

        /**
         * Constructor.
         * @param orient the orientation.
         * @param val the val.
         * @param vis the vis.
         * @param min the min.
         * @param max the max.
         */
        DScrollbar(int orient, int val, int vis, int min, int max)
        {
            super(orient, val, vis, min, max);
            doubleValue = val;
        }

        /**
         * @see java.awt.Adjustable#setValue(int)
         */
        public void setValue(int v)
        {
            doubleValue = v;
            super.setValue(v);
        }

        /**
         * Setter for the int value.
         * @param v the value to set
         */
        public void setIValue(int v)
        {
            super.setValue(v);
        }

        /**
         * Setter for the <code>doubleValue</code>.
         * @param v the double value to set.
         */
        public void setDValue(double v)
        {
            doubleValue = Math.max(getMinimum(), Math.min(getMaximum(), v));
            setIValue((int) v);
        }

        /**
         * Getter for the <code>doubleValue</code>.
         * @return Returns double value.
         */
        public double getDValue()
        {
            return doubleValue;
        }
    }

    /**
     * Adjust horizontal? offset.
     */
    protected void adjustHOffset()
    { // The inverse of the "graphMoved" function.
        for (int iterate = 0; iterate < 3; iterate++)
        { // Iteration needed to yeild cerrect results depite warping lenses
            TGPoint2D center = tgPanel.getCenter();
            TGPoint2D tld = getTopLeftDraw();
            TGPoint2D brd = getBottomRightDraw();

            double newx = ((horizontalSB.getDValue() + 1000.0) / 2000) * (brd.x - tld.x) + tld.x;
            double newy = tgPanel.getSize().height / 2;
            TGPoint2D newCenter = tgLensSet.convDrawToReal(newx, newy);

            offset.setX(offset.x + (newCenter.x - center.x));
            offset.setY(offset.y + (newCenter.y - center.y));

            tgPanel.processGraphMove();
        }
    }

    /**
     * Adjust vertical offset.
     */
    protected void adjustVOffset()
    { // The inverse of the "graphMoved" function.
        for (int iterate = 0; iterate < 10; iterate++)
        { // Iteration needed to yeild cerrect results depite warping lenses
            TGPoint2D center = tgPanel.getCenter();
            TGPoint2D tld = getTopLeftDraw();
            TGPoint2D brd = getBottomRightDraw();

            double newx = tgPanel.getSize().width / 2;
            double newy = ((verticalSB.getDValue() + 1000.0) / 2000) * (brd.y - tld.y) + tld.y;

            TGPoint2D newCenter = tgLensSet.convDrawToReal(newx, newy);

            offset.setX(offset.x + (newCenter.x - center.x));
            offset.setY(offset.y + (newCenter.y - center.y));

            tgPanel.processGraphMove();
        }
    }

    /**
     * The horizAdjustmentListener class.
     * @author Alexander Shapiro
     * @version $Revision: 1.4 $ ($Author: mindraider $)
     */
    private class horizAdjustmentListener implements AdjustmentListener {

        /**
         * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
         */
        public void adjustmentValueChanged(AdjustmentEvent e)
        {
            if (!adjustmentIsInternal)
            {
                adjustHOffset();
                tgPanel.repaintAfterMove();
            }
        }
    }

    /**
     * The vertAdjustmentListener class.
     * @author Alexander Shapiro
     * @version $Revision: 1.4 $ ($Author: mindraider $)
     */
    private class vertAdjustmentListener implements AdjustmentListener {

        /**
         * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
         */
        public void adjustmentValueChanged(AdjustmentEvent e)
        {
            if (!adjustmentIsInternal)
            {
                adjustVOffset();
                tgPanel.repaintAfterMove();
            }
        }
    }

    /**
     * The HVLens class.
     * @author Alexander Shapiro
     * @version $Revision: 1.4 $ ($Author: mindraider $)
     */
    class HVLens extends TGAbstractLens {


        /**
         * @see com.touchgraph.graphlayout.TGAbstractLens#applyLens(com.touchgraph.graphlayout.TGPoint2D)
         */
        protected void applyLens(TGPoint2D p)
        {
            p.x = p.x - offset.x;
            p.y = p.y - offset.y;
        }

        /**
         * @see com.touchgraph.graphlayout.TGAbstractLens#undoLens(com.touchgraph.graphlayout.TGPoint2D)
         */
        protected void undoLens(TGPoint2D p)
        {
            p.x = p.x + offset.x;
            p.y = p.y + offset.y;
        }
    }

    /**
     * Set the offset point.
     * @param p the point to set
     */
    public void setOffset(Point p)
    {
        offset.setLocation(p.x, p.y);
        tgPanel.processGraphMove(); // Adjust draw coordinates to include new offset
        graphMoved(); // adjusts scrollbars to fit draw coordinates
    }

    /**
     * Returns the offset.
     * @return Returns <code>Point</code>.
     */
    public Point getOffset()
    {
        return new Point((int) offset.x, (int) offset.y);
    }

    /**
     * Scroll a node towards another.
     * @param drawFrom the start node.
     * @param drawTo the end node.
     */
    public void scrollAtoB(TGPoint2D drawFrom, TGPoint2D drawTo)
    {
        TGPoint2D from = tgLensSet.convDrawToReal(drawFrom);
        TGPoint2D to = tgLensSet.convDrawToReal(drawTo);
        offset.setX(offset.x + (from.x - to.x));
        offset.setY(offset.y + (from.y - to.y));
    }

    /**
     * The scroll thread.
     */
    Thread scrollThread;

    /**
     * Slow scroll to center.
     * @param n the node
     */
    public void slowScrollToCenter(final Node n)
    {
        // final TGPoint2D drawFrom =new TGPoint2D(n.drawx,n.drawy);
        // final TGPoint2D drawTo = getDrawCenter();
        scrolling = true;
        if (scrollThread != null && scrollThread.isAlive())
        {
            scrollThread.interrupt();
        }
        scrollThread = new Thread()
        {

            /**
             * @see java.lang.Runnable#run()
             */
            public void run()
            {
                double nx = -999;
                double ny = -999;
                double cx;
                double cy;
                double distFromCenter;
                boolean keepScrolling = true;
                // int stopScrollingAttempt = 0;
                int scrollSteps = 0;

                while (keepScrolling && scrollSteps++ < 250)
                {
                    nx = n.drawx;
                    ny = n.drawy;
                    cx = getDrawCenter().x;
                    cy = getDrawCenter().y;

                    distFromCenter = Math.sqrt((nx - cx) * (nx - cx) + (ny - cy) * (ny - cy));

                    double newx, newy;
                    if (distFromCenter > 5)
                    {
                        newx = cx + (nx - cx) * ((distFromCenter - 5) / distFromCenter);
                        newy = cy + (ny - cy) * ((distFromCenter - 5) / distFromCenter);
                    }
                    else
                    {
                        newx = cx;
                        newy = cy;
                    }

                    scrollAtoB(new TGPoint2D(nx, ny), new TGPoint2D(newx, newy));

                    if (noRepaintThread == null || !noRepaintThread.isAlive())
                    {
                        tgPanel.repaintAfterMove(); // only repaint if 40 milliseconds have not ellapsed since last
                        // repaint
                    }
                    else
                    {
                        tgPanel.processGraphMove(); // otherwise, register scroll internally
                    }

                    try
                    {
                        Thread.sleep(20);
                    }
                    catch (InterruptedException ex)
                    {
                        keepScrolling = false;
                    }

                    if (distFromCenter < 3)
                    {
                        try
                        {
                            Thread.sleep(200); // Wait a little to make sure
                        }
                        catch (InterruptedException ex)
                        {
                            keepScrolling = false;
                        }
                        nx = n.drawx;
                        ny = n.drawy;
                        cx = getDrawCenter().x;
                        cy = getDrawCenter().y;
                        distFromCenter = Math.sqrt((nx - cx) * (nx - cx) + (ny - cy) * (ny - cy));
                        if (distFromCenter < 3)
                        {
                            keepScrolling = false;
                        }

                    }
                }
                scrollAtoB(new TGPoint2D(n.drawx, n.drawy), getDrawCenter()); // for good measure
                tgPanel.repaintAfterMove();
                HVScroll.this.scrolling = false;
            }
        };
        scrollThread.setDaemon(true);
        scrollThread.start();
    }

    /**
     * The HVScrollToCenterUI class.
     * @author Alexander Shapiro
     */
    class HVScrollToCenterUI extends TGAbstractClickUI {
        /**
         * @see com.touchgraph.graphlayout.interaction.TGAbstractClickUI#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e)
        {
            /*
             * Node mouseOverN=tgPanel.getMouseOverN(); if(!scrolling && mouseOverN!=null)
             * slowScrollToCenter(mouseOverN);
             */}
    }

    /**
     * The HVDragUI class.
     * @author Alexander Shapiro
     * @version $Revision: 1.4 $ ($Author: mindraider $)
     */
    class HVDragUI extends TGAbstractDragUI {

        /**
         * The last mouse position.
         */
        TGPoint2D lastMousePos;

        /**
         * Constructor.
         */
        HVDragUI()
        {
            super(HVScroll.this.tgPanel);
        }

        /**
         * @see com.touchgraph.graphlayout.interaction.TGAbstractDragUI#preActivate()
         */
        public void preActivate()
        {
        }

        /**
         * @see com.touchgraph.graphlayout.interaction.TGAbstractDragUI#preDeactivate()
         */
        public void preDeactivate()
        {
        }

        /**
         * @see com.touchgraph.graphlayout.interaction.TGAbstractDragUI#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent e)
        {
            lastMousePos = new TGPoint2D(e.getX(), e.getY());
        }

        /**
         * @see com.touchgraph.graphlayout.interaction.TGAbstractDragUI#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent e)
        {
        }

        /**
         * @see com.touchgraph.graphlayout.interaction.TGAbstractDragUI#mouseDragged(java.awt.event.MouseEvent)
         */
        public void mouseDragged(MouseEvent e)
        {
            if (!scrolling)
            {
                scrollAtoB(lastMousePos, new TGPoint2D(e.getX(), e.getY()));
            }
            lastMousePos.setLocation(e.getX(), e.getY());
            this.tgPanel.repaintAfterMove();
        }
    }

} // end com.touchgraph.graphlayout.interaction.HVScroll
