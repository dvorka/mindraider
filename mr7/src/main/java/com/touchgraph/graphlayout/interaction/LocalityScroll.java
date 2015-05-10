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

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.touchgraph.graphlayout.GraphListener;
import com.touchgraph.graphlayout.LocalityUtils;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGException;
import com.touchgraph.graphlayout.TGPanel;


/**
 * LocalityScroll.
 * @author Alexander Shapiro
 * @version 1.21 $Id: LocalityScroll.java,v 1.3 2011/12/03 17:49:44 mindraider Exp $
 */
public class LocalityScroll implements GraphListener {

    private JSlider localitySB;

    private TGPanel tgPanel;

    public TitledBorder tb;

    public JPanel p;

    public LocalityScroll(TGPanel tgp)
    {
        tgPanel = tgp;
        // localitySB = new JSlider(JScrollBar.HORIZONTAL, 2, 1, 0, 7);
        localitySB = new JSlider(SwingConstants.HORIZONTAL, 0, 7, 2);
        // localitySB.setPaintTicks(true);
        localitySB.setPaintTrack(true);
        // localitySB.setBlockIncrement(1);
        // localitySB.setUnitIncrement(1);
        localitySB.setPreferredSize(new Dimension(100, localitySB.getPreferredSize().height));
        localitySB.addChangeListener(new localityAdjustmentListener());
        tgPanel.addGraphListener(this);
    }

    public JSlider getLocalitySB()
    {
        return localitySB;
    }

    public int getLocalityRadius()
    {
        int locVal = localitySB.getValue();
        if (locVal >= 6)
        {
            return LocalityUtils.INFINITE_LOCALITY_RADIUS;
        }
        return locVal;
    }

    public void setLocalityRadius(int radius)
    {
        if (radius <= 0)
        {
            localitySB.setValue(0);
        }
        else if (radius <= 5) // and > 0
        {
            localitySB.setValue(radius);
        }
        else
        {
            // radius > 5
            localitySB.setValue(6);
        }
    }

    public void graphMoved()
    {
    } // From GraphListener interface

    public void graphReset() {
        localitySB.setValue(2);
    } // From GraphListener interface

    private class localityAdjustmentListener implements ChangeListener
    {

        /**
         * Logger for this class
         */
        private final Logger logger = Logger.getLogger(localityAdjustmentListener.class);

        public void stateChanged(ChangeEvent e)
        {
            int value = localitySB.getValue();
            tb.setTitle(" Look ahead: " + value + " ");
            p.repaint();

            Node select = tgPanel.getSelect();
            if (select != null || getLocalityRadius() == LocalityUtils.INFINITE_LOCALITY_RADIUS)
            {
                try
                {
                    tgPanel.setLocale(select, getLocalityRadius());
                }
                catch (TGException ex)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("stateChanged() - Error setting locale");
                    }
                    logger.error("stateChanged(ChangeEvent)", ex);
                }
            }
        }
    }

} // end com.touchgraph.graphlayout.interaction.LocalityScroll
