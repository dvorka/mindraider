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

import java.awt.Dimension;

import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.touchgraph.graphlayout.GraphListener;
import com.touchgraph.graphlayout.TGAbstractLens;
import com.touchgraph.graphlayout.TGPanel;
import com.touchgraph.graphlayout.TGPoint2D;

/**
 * ZoomScroll: Contains code for enlarging the graph by zooming in.
 * 
 * @author Alexander Shapiro
 */
public class ZoomScroll implements GraphListener {

    /**
     * The zoom lens.
     */
    protected ZoomLens zoomLens;

    /**
     * The zoomSB JSlider.
     */
    private JSlider zoomSB;

    /**
     * The TGPanel.
     */
    private TGPanel tgPanel;

    /**
     * Constant for extend value.
     */
    private static final int EXTEND = 4;

    /**
     * Constructor.
     * 
     * @param tgp
     *            the TGPanel.
     */
    public ZoomScroll(TGPanel tgp) {
        tgPanel = tgp;
        // zoomSB = new JScroll(JScroll.HORIZONTAL, -10, 4, -31, 19);
        zoomSB = new JSlider(SwingConstants.HORIZONTAL, -31, 19, -10);
        zoomSB.setPreferredSize(new Dimension(100,
                zoomSB.getPreferredSize().height));
        zoomSB.addChangeListener(new zoomAdjustmentListener());
        zoomLens = new ZoomLens();
        tgPanel.addGraphListener(this);
    }

    /**
     * Returns the JSlider for zoom.
     * 
     * @return Returns <code>zoomSB</code>.
     */
    public JSlider getZoomSB() {
        return zoomSB;
    }

    /**
     * Return the zoom lens.
     * 
     * @return Returns the <code>zoomLens</code>.
     */
    public ZoomLens getLens() {
        return zoomLens;
    }

    /**
     * @see com.touchgraph.graphlayout.GraphListener#graphMoved()
     */
    public void graphMoved() {
    }

    /**
     * @see com.touchgraph.graphlayout.GraphListener#graphReset()
     */
    public void graphReset() {
        zoomSB.setValue(-10);
    }

    /**
     * Return the zoom value.
     * 
     * @return Returns the value.
     */
    public int getZoomValue() {
        double orientedValue = zoomSB.getValue() - zoomSB.getMinimum();
        // double range =
        // zoomSB.getMaximum()-zoomSB.getMinimum()-zoomSB.getVisibleAmount();
        double range = zoomSB.getMaximum() - zoomSB.getMinimum() - EXTEND;
        return (int) ((orientedValue / range) * 200 - 100);
    }

    /**
     * Set the zoom value.
     * 
     * @param value
     *            the value to set
     */
    public void setZoomValue(int value) {
        // double range =
        // zoomSB.getMaximum()-zoomSB.getMinimum()-zoomSB.getVisibleAmount();
        double range = zoomSB.getMaximum() - zoomSB.getMinimum() - EXTEND;
        zoomSB.setValue((int) ((value + 100) / 200.0 * range + 0.5)
                + zoomSB.getMinimum());
    }

    /**
     * The zoomAdjustmentListener class.
     * 
     * @author Alexander Shapiro
     */
    private class zoomAdjustmentListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            getTgPanel().repaintAfterMove();
        }
    }

    /**
     * The ZoomLens class.
     * 
     * @author Alexander Shapiro
     */
    class ZoomLens extends TGAbstractLens {

        /**
         * @see com.touchgraph.graphlayout.TGAbstractLens#applyLens(com.touchgraph.graphlayout.TGPoint2D)
         */
        protected void applyLens(TGPoint2D p) {
            JSlider js = getZoomSB();
            p.x = p.x * Math.pow(2, js.getValue() / 10.0);
            p.y = p.y * Math.pow(2, js.getValue() / 10.0);

        }

        /**
         * @see com.touchgraph.graphlayout.TGAbstractLens#undoLens(com.touchgraph.graphlayout.TGPoint2D)
         */
        protected void undoLens(TGPoint2D p) {
            JSlider js = getZoomSB();
            p.x = p.x / Math.pow(2, js.getValue() / 10.0);
            p.y = p.y / Math.pow(2, js.getValue() / 10.0);
        }
    }

    /**
     * Getter for <code>tgPanel</code>.
     * 
     * @return Returns the tgPanel.
     */
    public TGPanel getTgPanel() {
        return this.tgPanel;
    }

    /**
     * Setter for <code>tgPanel</code>.
     * 
     * @param tgPanel
     *            The tgPanel to set.
     */
    public void setTgPanel(TGPanel tgPanel) {
        this.tgPanel = tgPanel;
    }
}