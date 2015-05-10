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

package com.touchgraph.graphlayout;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JPanel;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.ui.graph.spiders.SpidersGraph;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.touchgraph.graphlayout.graphelements.GraphEltSet;
import com.touchgraph.graphlayout.graphelements.ImmutableGraphEltSet;
import com.touchgraph.graphlayout.graphelements.TGForEachEdge;
import com.touchgraph.graphlayout.graphelements.TGForEachNode;
import com.touchgraph.graphlayout.graphelements.VisibleLocality;
import com.touchgraph.graphlayout.interaction.GLEditUI;
import com.touchgraph.graphlayout.interaction.TGAbstractClickUI;

/* Java 1.5 Deadlock hunting (by MartinDvorak <mindraider@users.sourceforge.net>

Problem description:
  Synchronization of paint() and repaintAfterMove() - check also stacktraces below.

Solution
 o repaintAfterMove() method doesn't have to be sychronized
   (since all the methods called from there (and need synchronization)
   are already synchronized themselves). An only one that is repaint()
   (which in fact doesn't need synchronization) and it participates in deadlock
 o I have also unfolded content of paint() method (in order to isolate the problem)

---

Full thread dump Java HotSpot(TM) Client VM (1.5.0_04-b05 mixed mode, sharing):

"DestroyJavaVM" prio=5 tid=0x053bc9d8 nid=0xc74 waiting on condition [0x00000000..0x0007fae8]

"TimerQueue" daemon prio=5 tid=0x054e0310 nid=0xbfc in Object.wait() [0x0561f000..0x0561fb68]
        at java.lang.Object.wait(Native Method)
        - waiting on <0x233302b8> (a javax.swing.TimerQueue)
        at javax.swing.TimerQueue.run(TimerQueue.java:233)
        - locked <0x233302b8> (a javax.swing.TimerQueue)
        at java.lang.Thread.run(Thread.java:595)

"Thread-2" prio=5 tid=0x02eacae8 nid=0xe90 waiting for monitor entry [0x055cf000..0x055cfc68]
        at java.awt.Component.reshape(Component.java:1858)
        - waiting to lock <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.reshape(JComponent.java:3940)
        at java.awt.Component.setBounds(Component.java:1847)
        at javax.swing.plaf.basic.BasicScrollBarUI.layoutHScrollbar(BasicScrollBarUI.java:732)
        at javax.swing.plaf.basic.BasicScrollBarUI.layoutContainer(BasicScrollBarUI.java:775)
        at javax.swing.plaf.basic.BasicScrollBarUI$ModelListener.stateChanged(BasicScrollBarUI.java:935)
        at javax.swing.DefaultBoundedRangeModel.fireStateChanged(DefaultBoundedRangeModel.java:348)
        at javax.swing.DefaultBoundedRangeModel.setRangeProperties(DefaultBoundedRangeModel.java:285)
        at javax.swing.DefaultBoundedRangeModel.setValue(DefaultBoundedRangeModel.java:151)
        at javax.swing.JScrollBar.setValue(JScrollBar.java:441)
        at com.touchgraph.graphlayout.interaction.HVScroll$DScrollbar.setIValue(HVScroll.java:339)
        at com.touchgraph.graphlayout.interaction.HVScroll$DScrollbar.setDValue(HVScroll.java:349)
        at com.touchgraph.graphlayout.interaction.HVScroll.graphMoved(HVScroll.java:240)
        at com.touchgraph.graphlayout.TGPanel.fireMovedEvent(TGPanel.java:681)
        at com.touchgraph.graphlayout.TGPanel.repaintAfterMove(TGPanel.java:1078)
        - locked <0x231ceb20> (a com.touchgraph.graphlayout.TGPanel)
        at com.touchgraph.graphlayout.TGLayout.relax(TGLayout.java:473)
        - locked <0x231cead0> (a com.touchgraph.graphlayout.TGLayout)
        at com.touchgraph.graphlayout.TGLayout.run(TGLayout.java:496)
        at java.lang.Thread.run(Thread.java:595)

"AWT-EventQueue-0" prio=7 tid=0x02e64d88 nid=0xf40 waiting for monitor entry [0x0513e000..0x0513fd68]
        at com.touchgraph.graphlayout.TGPanel.paint(TGPanel.java:1139)
        - waiting to lock <0x231ceb20> (a com.touchgraph.graphlayout.TGPanel)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JSplitPane.paintChildren(JSplitPane.java:1021)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JSplitPane.paintChildren(JSplitPane.java:1021)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JSplitPane.paintChildren(JSplitPane.java:1021)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JLayeredPane.paint(JLayeredPane.java:559)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.paintWithOffscreenBuffer(JComponent.java:4970)
        at javax.swing.JComponent.paintDoubleBuffered(JComponent.java:4916)
        at javax.swing.JComponent.paint(JComponent.java:995)
        at java.awt.GraphicsCallback$PaintCallback.run(GraphicsCallback.java:21)
        at sun.awt.SunGraphicsCallback.runOneComponent(SunGraphicsCallback.java:60)
        at sun.awt.SunGraphicsCallback.runComponents(SunGraphicsCallback.java:97)
        at java.awt.Container.paint(Container.java:1709)
        at sun.awt.RepaintArea.paintComponent(RepaintArea.java:248)
        at sun.awt.RepaintArea.paint(RepaintArea.java:224)
        at sun.awt.windows.WComponentPeer.handleEvent(WComponentPeer.java:254)
        at java.awt.Component.dispatchEventImpl(Component.java:4031)
        at java.awt.Container.dispatchEventImpl(Container.java:2024)
        at java.awt.Window.dispatchEventImpl(Window.java:1774)
        at java.awt.Component.dispatchEvent(Component.java:3803)
        at java.awt.EventQueue.dispatchEvent(EventQueue.java:463)
        at java.awt.EventDispatchThread.pumpOneEventForHierarchy(EventDispatchThread.java:242)
        at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:163)
        at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:157)
        at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:149)
        at java.awt.EventDispatchThread.run(EventDispatchThread.java:110)

"AWT-Windows" daemon prio=7 tid=0x02e3cfc0 nid=0x3ac runnable [0x0500f000..0x0500fae8]
        at sun.awt.windows.WToolkit.eventLoop(Native Method)
        at sun.awt.windows.WToolkit.run(WToolkit.java:269)
        at java.lang.Thread.run(Thread.java:595)

"AWT-Shutdown" prio=5 tid=0x02e476b8 nid=0xf34 in Object.wait() [0x04fcf000..0x04fcfb68]
        at java.lang.Object.wait(Native Method)
        - waiting on <0x23004ad0> (a java.lang.Object)
        at java.lang.Object.wait(Object.java:474)
        at sun.awt.AWTAutoShutdown.run(AWTAutoShutdown.java:259)
        - locked <0x23004ad0> (a java.lang.Object)
        at java.lang.Thread.run(Thread.java:595)

"Java2D Disposer" daemon prio=10 tid=0x02e5ba50 nid=0xf68 in Object.wait() [0x04f8f000..0x04f8fbe8]
        at java.lang.Object.wait(Native Method)
        - waiting on <0x23004b58> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:116)
        - locked <0x23004b58> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:132)
        at sun.java2d.Disposer.run(Disposer.java:107)
        at java.lang.Thread.run(Thread.java:595)

"Low Memory Detector" daemon prio=5 tid=0x00a7f498 nid=0x944 runnable [0x00000000..0x00000000]

"CompilerThread0" daemon prio=10 tid=0x00a7e070 nid=0xe74 waiting on condition [0x00000000..0x02c2f6cc]

"Signal Dispatcher" daemon prio=10 tid=0x00a7d448 nid=0xe24 waiting on condition [0x00000000..0x00000000]

"Finalizer" daemon prio=9 tid=0x00a782f8 nid=0xef4 in Object.wait() [0x02baf000..0x02bafa68]
        at java.lang.Object.wait(Native Method)
        - waiting on <0x22fd0f00> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:116)
        - locked <0x22fd0f00> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:132)
        at java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:159)

"Reference Handler" daemon prio=10 tid=0x00a76e18 nid=0x420 in Object.wait() [0x02b6f000..0x02b6fae8]
        at java.lang.Object.wait(Native Method)
        - waiting on <0x22fd0f80> (a java.lang.ref.Reference$Lock)
        at java.lang.Object.wait(Object.java:474)
        at java.lang.ref.Reference$ReferenceHandler.run(Reference.java:116)
        - locked <0x22fd0f80> (a java.lang.ref.Reference$Lock)

"VM Thread" prio=10 tid=0x00a74578 nid=0xe94 runnable

"VM Periodic Task Thread" prio=10 tid=0x00a806e0 nid=0xce4 waiting on condition


Found one Java-level deadlock:
=============================
"Thread-2":
  waiting to lock monitor 0x00a77bcc (object 0x22fd2290, a java.awt.Component$AWTTreeLock),
  which is held by "AWT-EventQueue-0"
"AWT-EventQueue-0":
  waiting to lock monitor 0x00a77b0c (object 0x231ceb20, a com.touchgraph.graphlayout.TGPanel),
  which is held by "Thread-2"

Java stack information for the threads listed above:
===================================================
"Thread-2":
        at java.awt.Component.reshape(Component.java:1858)
        - waiting to lock <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.reshape(JComponent.java:3940)
        at java.awt.Component.setBounds(Component.java:1847)
        at javax.swing.plaf.basic.BasicScrollBarUI.layoutHScrollbar(BasicScrollBarUI.java:732)
        at javax.swing.plaf.basic.BasicScrollBarUI.layoutContainer(BasicScrollBarUI.java:775)
        at javax.swing.plaf.basic.BasicScrollBarUI$ModelListener.stateChanged(BasicScrollBarUI.java:935)
        at javax.swing.DefaultBoundedRangeModel.fireStateChanged(DefaultBoundedRangeModel.java:348)
        at javax.swing.DefaultBoundedRangeModel.setRangeProperties(DefaultBoundedRangeModel.java:285)
        at javax.swing.DefaultBoundedRangeModel.setValue(DefaultBoundedRangeModel.java:151)
        at javax.swing.JScrollBar.setValue(JScrollBar.java:441)
        at com.touchgraph.graphlayout.interaction.HVScroll$DScrollbar.setIValue(HVScroll.java:339)
        at com.touchgraph.graphlayout.interaction.HVScroll$DScrollbar.setDValue(HVScroll.java:349)
        at com.touchgraph.graphlayout.interaction.HVScroll.graphMoved(HVScroll.java:240)
        at com.touchgraph.graphlayout.TGPanel.fireMovedEvent(TGPanel.java:681)
        at com.touchgraph.graphlayout.TGPanel.repaintAfterMove(TGPanel.java:1078)
        - locked <0x231ceb20> (a com.touchgraph.graphlayout.TGPanel)
        at com.touchgraph.graphlayout.TGLayout.relax(TGLayout.java:473)
        - locked <0x231cead0> (a com.touchgraph.graphlayout.TGLayout)
        at com.touchgraph.graphlayout.TGLayout.run(TGLayout.java:496)
        at java.lang.Thread.run(Thread.java:595)
"AWT-EventQueue-0":
        at com.touchgraph.graphlayout.TGPanel.paint(TGPanel.java:1139)
        - waiting to lock <0x231ceb20> (a com.touchgraph.graphlayout.TGPanel)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JSplitPane.paintChildren(JSplitPane.java:1021)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JSplitPane.paintChildren(JSplitPane.java:1021)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JSplitPane.paintChildren(JSplitPane.java:1021)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.paint(JComponent.java:1014)
        at javax.swing.JLayeredPane.paint(JLayeredPane.java:559)
        at javax.swing.JComponent.paintChildren(JComponent.java:842)
        - locked <0x22fd2290> (a java.awt.Component$AWTTreeLock)
        at javax.swing.JComponent.paintWithOffscreenBuffer(JComponent.java:4970)
        at javax.swing.JComponent.paintDoubleBuffered(JComponent.java:4916)
        at javax.swing.JComponent.paint(JComponent.java:995)
        at java.awt.GraphicsCallback$PaintCallback.run(GraphicsCallback.java:21)
        at sun.awt.SunGraphicsCallback.runOneComponent(SunGraphicsCallback.java:60)
        at sun.awt.SunGraphicsCallback.runComponents(SunGraphicsCallback.java:97)
        at java.awt.Container.paint(Container.java:1709)
        at sun.awt.RepaintArea.paintComponent(RepaintArea.java:248)
        at sun.awt.RepaintArea.paint(RepaintArea.java:224)
        at sun.awt.windows.WComponentPeer.handleEvent(WComponentPeer.java:254)
        at java.awt.Component.dispatchEventImpl(Component.java:4031)
        at java.awt.Container.dispatchEventImpl(Container.java:2024)
        at java.awt.Window.dispatchEventImpl(Window.java:1774)
        at java.awt.Component.dispatchEvent(Component.java:3803)
        at java.awt.EventQueue.dispatchEvent(EventQueue.java:463)
        at java.awt.EventDispatchThread.pumpOneEventForHierarchy(EventDispatchThread.java:242)
        at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:163)
        at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:157)
        at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:149)
        at java.awt.EventDispatchThread.run(EventDispatchThread.java:110)

Found 1 deadlock.
 */

/**
 * TGPanel contains code for drawing the graph, and storing which nodes are
 * selected, and which ones the mouse is over. It houses methods to activate
 * TGLayout, which performs dynamic layout. Whenever the graph is moved, or
 * repainted, TGPanel fires listner methods on associated objects.
 * <p>
 * <b> Parts of this code build upon Sun's Graph Layout example.
 * http://java.sun.com/applets/jdk/1.1/demo/GraphLayout/Graph.java </b>
 * </p>
 *
 * @author Alexander Shapiro
 * @author Murray Altheim (2001-11-06; 2002-01-14 cleanup)
 */
public class TGPanel extends JPanel {

    /**
     * The back color constant.
     */
    public static Color BACK_COLOR = Color.white;

    /**
     * The serial version uid for serialization constant.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The TGLayout property.
     */
    public TGLayout tgLayout;

    /**
     * The basic mouse motion listener property.
     */
    protected BasicMouseMotionListener basicMML;

    /**
     * The mouseOverE is the edge the mouse is over.
     */
    protected Edge mouseOverE;

    /**
     * The mouseOverN is the node the mouse is over.
     */
    protected Node mouseOverN;

    /**
     * The maintain mouse over. If <code>true</code>, then don't change
     * mouseOverN or mouseOverE
     */
    protected boolean maintainMouseOver;

    protected Node select;

    Node dragNode; // Node currently being dragged

    protected Point mousePos; // Mouse location, updated in the
                                // mouseMotionListener

    /**
     * The complete graph elt set property.
     */
    private GraphEltSet completeEltSet;

    /**
     * The visible locality property.
     */
    private VisibleLocality visibleLocality;

    /**
     * The locality utils property.
     */
    private LocalityUtils localityUtils;

    /**
     * The offscreen Image property.
     */
    Image offscreen;

    /**
     * The offscreen dimension property.
     */
    Dimension offscreensize;

    /**
     * The off graphics property.
     */
    Graphics offgraphics;

    /**
     * The graph listeners vector.
     */
    private Vector graphListeners;

    /**
     * The paint listeners vector.
     */
    private Vector paintListeners;

    /**
     * Converts between a nodes visual position (drawx, drawy).
     */
    TGLensSet tgLensSet;

    /**
     * Converts between the visual position and absolute position (x,y).
     */
    AdjustOriginLens adjustOriginLens;

    /**
     * The switch select ui property.
     */
    SwitchSelectUI switchSelectUI;

    /**
     * Default constructor.
     */
    public TGPanel() {
        setLayout(null);

        setGraphEltSet(new GraphEltSet());
        addMouseListener(new BasicMouseListener());
        basicMML = new BasicMouseMotionListener();
        addMouseMotionListener(basicMML);

        graphListeners = new Vector();
        paintListeners = new Vector();

        adjustOriginLens = new AdjustOriginLens();
        switchSelectUI = new SwitchSelectUI();

        TGLayout tgLayout = new TGLayout(this);
        setTGLayout(tgLayout);
        tgLayout.start();
        setGraphEltSet(new GraphEltSet());

    }

    /**
     * Setter for TGLensSet.
     *
     * @param lensSet
     *            the lensSet to set.
     */
    public void setLensSet(TGLensSet lensSet) {
        tgLensSet = lensSet;
    }

    /**
     * Setter for TGLayout.
     *
     * @param tgl
     *            the TGLayout to set.
     */
    public void setTGLayout(TGLayout tgl) {
        tgLayout = tgl;
    }

    /**
     * Setter for GraphEltSet.
     *
     * @param ges
     *            the GraphEltSet to set.
     */
    public void setGraphEltSet(GraphEltSet ges) {
        completeEltSet = ges;
        visibleLocality = new VisibleLocality(completeEltSet);
        localityUtils = new LocalityUtils(visibleLocality, this);
    }

    /**
     * Getter for adjustOriginLens.
     *
     * @return Returns the set AdjustOriginLens.
     */
    public AdjustOriginLens getAdjustOriginLens() {
        return adjustOriginLens;
    }

    /**
     * Getter for switchSelectUI.
     *
     * @return Returns the switchSelectUI.
     */
    public SwitchSelectUI getSwitchSelectUI() {
        return switchSelectUI;
    }

    /**
     * Setter for color.
     *
     * @param color
     *            The Color to set.
     */
    public void setBackColor(Color color) {
        BACK_COLOR = color;
    }

    /**
     * Returns an Iterator over all nodes in the complete graph. public Iterator
     * getAllNodes() { return completeEltSet.getNodes(); }
     */

    /**
     * Return the current visible locality.
     *
     * @return ImmutableGraphEltSet visible locality.
     */
    public ImmutableGraphEltSet getGES() {
        return visibleLocality;
    }

    /**
     * Returns the current node count.
     *
     * @return the node count.
     */
    public int getNodeCount() {
        return completeEltSet.nodeCount();
    }

    /**
     * Returns the current node count within the VisibleLocality.
     *
     * @return the number of node of visibileLocality.
     * @deprecated this method has been replaced by the
     *             <tt>visibleNodeCount()</tt> method.
     */
    public int nodeNum() {
        return visibleLocality.nodeCount();
    }

    /**
     * Returns the current node count within the VisibleLocality.
     *
     * @return the number of visible nodes.
     */
    public int visibleNodeCount() {
        return visibleLocality.nodeCount();
    }

    /**
     * Return the Node whose ID matches the String <tt>id</tt>, null if no
     * match is found.
     *
     * @param id
     *            The ID identifier used as a query.
     * @return The Node whose ID matches the provided 'id', null if no match is
     *         found.
     */
    public Node findNode(String id) {
        if (id == null) {
            return null;
        }
        return completeEltSet.findNode(id);
    }

    /**
     * Return the Node whose URL matches the String <tt>strURL</tt>, null if
     * no match is found.
     *
     * @param strURL
     *            The URL identifier used as a query.
     * @return The Node whose URL matches the provided 'URL', null if no match
     *         is found.
     */
    public Node findNodeByUri(String strURL) {
        if (strURL == null) {
            return null;
        }
        return completeEltSet.findNodeByURL(strURL);
    }

    /**
     * Return a Collection of all Nodes whose label matches the String
     * <tt>label</tt>, null if no match is found.
     */
    /*
     * public Collection findNodesByLabel( String label ) { if ( label == null )
     * return null; // ignore return completeEltSet.findNodesByLabel(label); }
     */

    /**
     * Return the first Nodes whose label contains the String <tt>substring</tt>,
     * null if no match is found.
     *
     * @param substring
     *            The Substring used as a query.
     * @return the first node.
     */
    public Node findNodeLabelContaining(String substring) {
        if (substring == null) {
            return null;
        }
        return completeEltSet.findNodeLabelContaining(substring);
    }

    /**
     * Adds a Node, with its ID and label being the current node count plus 1.
     *
     * @see com.touchgraph.graphlayout.Node
     */
    public Node addNode() throws TGException {
        String id = String.valueOf(getNodeCount() + 1);
        return addNode(id, null);
    }

    /**
     * Adds a Node, provided its label. The node is assigned a unique ID.
     *
     * @see com.touchgraph.graphlayout.graphelements.GraphEltSet
     */
    public Node addNode(String label) throws TGException {
        return addNode(null, label);
    }

    /**
     * Adds a Node, provided its ID and label.
     *
     * @see com.touchgraph.graphlayout.Node
     */
    public Node addNode(String id, String label) throws TGException {
        Node node;
        if (label == null) {
            node = new Node(id);
        } else {
            node = new Node(id, label);
        }
        updateDrawPos(node); // The addNode() call should probably take a
                                // position, this just sets it at 0,0
        addNode(node);
        return node;
    }

    /**
     * Add the Node <tt>node</tt> to the visibleLocality, checking for ID
     * uniqueness.
     *
     * @param node
     *            the Node.
     * @throws TGException
     *             a TGException.
     */
    public void addNode(final Node node) throws TGException {
        synchronized (localityUtils) {
            visibleLocality.addNode(node);
            resetDamper();
        }
    }

    /**
     * Remove the Node object matching the ID <code>id</code>, returning true
     * if the deletion occurred, false if a Node matching the ID does not exist
     * (or if the ID value was null).
     *
     * @param id
     *            The ID identifier used as a query.
     * @return true if the deletion occurred.
     */
    public boolean deleteNodeById(String id) {
        if (id == null) {
            return false; // ignore
        }
        Node node = findNode(id);
        if (node == null) {
            return false;
        }
        return deleteNode(node);
    }

    /**
     * Delete a node.
     *
     * @param node
     *            the node to delete.
     * @return Returns <code>true</code> if node is deleted, otherwise
     *         <code>false</code>.
     */
    public boolean deleteNode(Node node) {
        synchronized (localityUtils) {
            if (visibleLocality.deleteNode(node)) { // delete from
                                                    // visibleLocality, *AND
                                                    // completeEltSet
                if (node == select) {
                    clearSelect();
                }
                resetDamper();
                return true;
            }
            return false;
        }
    }

    /**
     * Clear all.
     */
    public void clearAll() {
        synchronized (localityUtils) {
            visibleLocality.clearAll();
        }
    }

    /**
     * Return the selected node.
     *
     * @return Returns the node.
     */
    public Node getSelect() {
        return select;
    }

    /**
     * Return the node selected by mouse.
     *
     * @return Returns node
     */
    public Node getMouseOverN() {
        return mouseOverN;
    }

    /**
     * Set the node where mouse is over.
     *
     * @param node
     *            The node to select
     */
    // TODO FIXME synchronized removed (deadlocks)
    public void setMouseOverN(Node node) {
        if (dragNode != null || maintainMouseOver) {
            return; // So you don't accidentally switch nodes while dragging
        }
        if (mouseOverN != node) {
            // Node oldMouseOverN = mouseOverN;
            mouseOverN = node;
        }

        if (mouseOverN == null) {
            setCursor(new Cursor(Cursor.MOVE_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    /*
     * Returns an Iterator over all edges in the complete graph public Iterator
     * getAllEdges() { return completeEltSet.getEdges(); }
     */

    /**
     * Delete edgde.
     *
     * @param edge
     *            Edge.
     */
    public void deleteEdge(Edge edge) {
        synchronized (localityUtils) {
            visibleLocality.deleteEdge(edge);
            resetDamper();
        }
    }

    /**
     * Delete edge from a Node to another.
     *
     * @param from
     *            the start node.
     * @param to
     *            the end node.
     */
    public void deleteEdge(Node from, Node to) {
        synchronized (localityUtils) {
            visibleLocality.deleteEdge(from, to);
        }
    }

    /**
     * Returns the current edge count in the complete graph.
     *
     * @return Returns the edge count.
     */
    public int getEdgeCount() {
        return completeEltSet.edgeCount();
    }

    /**
     * Return the number of Edges in the Locality.
     *
     * @return the edge number.
     * @deprecated this method has been replaced by the
     *             <tt>visibleEdgeCount()</tt> method.
     */
    public int edgeNum() {
        return visibleLocality.edgeCount();
    }

    /**
     * Return the number of Edges in the Locality.
     *
     * @return the number of Edges.
     */
    public int visibleEdgeCount() {
        return visibleLocality.edgeCount();
    }

    /**
     * Find edge from nodes file and t.
     *
     * @param file
     *            the Node
     * @param t
     *            the Node
     * @return the Edge
     */
    public Edge findEdge(Node f, Node t) {
        return visibleLocality.findEdge(f, t);
    }

    /**
     * Add an Edge.
     *
     * @param e
     *            the Edge to add.
     */
    public void addEdge(Edge e) {
        synchronized (localityUtils) {
            visibleLocality.addEdge(e);
            resetDamper();
        }
    }

    /**
     * Add a Edge from node to another.
     *
     * @param file
     *            a start Node
     * @param t
     *            a end Node
     * @param tens
     * @return
     */
    public Edge addEdge(Node f, Node t, int tens) {
        synchronized (localityUtils) {
            return visibleLocality.addEdge(f, t, tens);
        }
    }
    
    public Edge getMouseOverE() {
        return mouseOverE;
    }

    // TODO FIXME synchronized removed (deadlocks)
    public void setMouseOverE(Edge edge) {
        if (dragNode != null || maintainMouseOver) {
            return; // No funny business while dragging
        }
        if (mouseOverE != edge) {
            // Edge oldMouseOverE = mouseOverE;
            mouseOverE = edge;
        }
    }

    // miscellany ..................................

    protected class AdjustOriginLens extends TGAbstractLens {

        protected void applyLens(TGPoint2D p) {
            p.x = p.x + TGPanel.this.getSize().width / 2;
            p.y = p.y + TGPanel.this.getSize().height / 2;
        }

        protected void undoLens(TGPoint2D p) {
            p.x = p.x - TGPanel.this.getSize().width / 2;
            p.y = p.y - TGPanel.this.getSize().height / 2;
        }
    }

    public class SwitchSelectUI extends TGAbstractClickUI {

        public void mouseClicked(MouseEvent e) {
            if (mouseOverN != null) {
                if (mouseOverN != select) {
                    setSelect(mouseOverN);
                } else {
                    clearSelect();
                }
            }
        }
    }

    void fireMovedEvent() {
        Vector listeners;

        // TODO FIXME deadlocks: this -> graphlisteners
        synchronized (graphListeners) {
            listeners = (Vector) graphListeners.clone();
        }

        for (int i = 0; i < listeners.size(); i++) {
            GraphListener gl = (GraphListener) listeners.elementAt(i);
            gl.graphMoved();
        }
    }

    public void fireResetEvent() {
        Vector listeners;

        // TODO FIXME deadlocks: this -> graphlisteners
        synchronized (graphListeners) {
            listeners = (Vector) graphListeners.clone();
        }

        for (int i = 0; i < listeners.size(); i++) {
            GraphListener gl = (GraphListener) listeners.elementAt(i);
            gl.graphReset();
        }
    }

    public synchronized void addGraphListener(GraphListener gl) {
        graphListeners.addElement(gl);
    }

    public synchronized void removeGraphListener(GraphListener gl) {
        graphListeners.removeElement(gl);
    }

    public synchronized void addPaintListener(TGPaintListener pl) {
        paintListeners.addElement(pl);
    }

    public synchronized void removePaintListener(TGPaintListener pl) {
        paintListeners.removeElement(pl);
    }

//    private void redraw() {
//        resetDamper();
//    }

    public void setMaintainMouseOver(boolean maintain) {
        maintainMouseOver = maintain;
    }

    public void clearSelect() {
        if (select != null) {
            select = null;
            repaint();
        }
    }

    /**
     * A convenience method that selects the first node of a graph, so that
     * hiding works.
     */
    public void selectFirstNode() {
        setSelect(getGES().getFirstNode());
    }

    public void setSelect(Node node) {
        if (node != null) {
            // try to select concept in the table tree
            OutlineJPanel.getInstance().setSelectedTreeNodeConcept(node.getURL());

            select = node;
            repaint();
        } else {
            clearSelect();
        }
    }

    /**
     * Node was double clicked - node is going to be launched. (added by
     * MindRaider@users.sourceforge.net).
     *
     * @param node
     */
    public void setDoubleSelect(Node node) {
        MindRaider.spidersGraph.handleDoubleSelect(node);
    }

    public void multiSelect(TGPoint2D from, TGPoint2D to) {
        final double minX, minY, maxX, maxY;

        if (from.x > to.x) {
            maxX = from.x;
            minX = to.x;
        } else {
            minX = from.x;
            maxX = to.x;
        }
        if (from.y > to.y) {
            maxY = from.y;
            minY = to.y;
        } else {
            minY = from.y;
            maxY = to.y;
        }

        final Vector selectedNodes = new Vector();

        TGForEachNode fen = new TGForEachNode() {

            public void forEachNode(Node node) {
                double x = node.drawx;
                double y = node.drawy;
                if (x > minX && x < maxX && y > minY && y < maxY) {
                    selectedNodes.addElement(node);
                }
            }
        };

        visibleLocality.forAllNodes(fen);

        if (selectedNodes.size() > 0) {
            int r = (int) (Math.random() * selectedNodes.size());
            setSelect((Node) selectedNodes.elementAt(r));
        } else {
            clearSelect();
        }
    }

    public void updateLocalityFromVisibility() throws TGException {
        visibleLocality.updateLocalityFromVisibility();
    }

    public void setLocale(Node node, int radius, int maxAddEdgeCount, int maxExpandEdgeCount, boolean unidirectional)
            throws TGException {
        localityUtils.setLocale(node, radius, maxAddEdgeCount, maxExpandEdgeCount, unidirectional);
    }

    public void fastFinishAnimation() { // Quickly wraps up the add node
                                        // animation
        localityUtils.fastFinishAnimation();
    }

    public void setLocale(Node node, int radius) throws TGException {
        localityUtils.setLocale(node, radius);
    }

    public void expandNode(Node node) {
        localityUtils.expandNode(node);
    }

    public void hideNode(Node hideNode) {
        localityUtils.hideNode(hideNode);
    }

    public void collapseNode(Node collapseNode) {
        localityUtils.collapseNode(collapseNode);
    }

    public void hideEdge(Edge hideEdge) {
        visibleLocality.removeEdge(hideEdge);
        if (mouseOverE == hideEdge) {
            setMouseOverE(null);
        }
        resetDamper();
    }

    public void setDragNode(Node node) {
        dragNode = node;
        tgLayout.setDragNode(node);
    }

    public Node getDragNode() {
        return dragNode;
    }

    void setMousePos(Point p) {
        mousePos = p;
    }

    public Point getMousePos() {
        return mousePos;
    }

    /** Start and stop the damper. Should be placed in the TGPanel too. */
    public void startDamper() {
        if (tgLayout != null) {
            tgLayout.startDamper();
        }
    }

    public void stopDamper() {
        if (tgLayout != null) {
            tgLayout.stopDamper();
        }
    }

    /** Makes the graph mobile, and slowly slows it down. */
    public void resetDamper() {
        if (tgLayout != null) {
            tgLayout.resetDamper();
        }
    }

    /** Gently stops the graph from moving */
    public void stopMotion() {
        if (tgLayout != null) {
            tgLayout.stopMotion();
        }
    }

    class BasicMouseListener extends MouseAdapter {

        public void mouseEntered(MouseEvent e) {
            addMouseMotionListener(basicMML);
        }

        public void mouseExited(MouseEvent e) {
            removeMouseMotionListener(basicMML);
            mousePos = null;
            setMouseOverN(null);
            setMouseOverE(null);
            repaint();
        }
    }

    class BasicMouseMotionListener implements MouseMotionListener {

        public void mouseDragged(MouseEvent e) {
            mousePos = e.getPoint();
            findMouseOver();
            try {
                Thread.sleep(6); // An attempt to make the cursor flicker
                                    // less
            } catch (InterruptedException ex) {
                // break;
            }
        }

        public void mouseMoved(MouseEvent e) {
            mousePos = e.getPoint();
            synchronized (this) {
                Edge oldMouseOverE = mouseOverE;
                Node oldMouseOverN = mouseOverN;
                findMouseOver();
                if (oldMouseOverE != mouseOverE || oldMouseOverN != mouseOverN) {
                    repaint();
                }
                // Replace the above lines with the commented portion below to
                // prevent whole graph
                // from being repainted simply to highlight a node On mouseOver.
                // This causes some annoying flickering though.
                /*
                 * if(oldMouseOverE!=mouseOverE) { if (oldMouseOverE!=null) {
                 * synchronized(oldMouseOverE) {
                 * oldMouseOverE.paint(TGPanel.this.getGraphics(),TGPanel.this);
                 * oldMouseOverE.from.paint(TGPanel.this.getGraphics(),TGPanel.this);
                 * oldMouseOverE.to.paint(TGPanel.this.getGraphics(),TGPanel.this); } }
                 * if (mouseOverE!=null) { synchronized(mouseOverE) {
                 * mouseOverE.paint(TGPanel.this.getGraphics(),TGPanel.this);
                 * mouseOverE.from.paint(TGPanel.this.getGraphics(),TGPanel.this);
                 * mouseOverE.to.paint(TGPanel.this.getGraphics(),TGPanel.this); } } }
                 * if(oldMouseOverN!=mouseOverN) { if (oldMouseOverN!=null)
                 * oldMouseOverN.paint(TGPanel.this.getGraphics(),TGPanel.this);
                 * if (mouseOverN!=null)
                 * mouseOverN.paint(TGPanel.this.getGraphics(),TGPanel.this); }
                 */
            }
        }
    }

    /**
     * Find mouse over.
     *
     */
    // TODO FIXME synchronized removed
    protected void findMouseOver() {

        if (mousePos == null) {
            setMouseOverN(null);
            setMouseOverE(null);
            return;
        }

        final int mpx = mousePos.x;
        final int mpy = mousePos.y;

        final Node[] monA = new Node[1];
        final Edge[] moeA = new Edge[1];

        TGForEachNode fen = new TGForEachNode() {

            double minoverdist = 100; // Kind of a hack (see second if
                                        // statement)

            // Nodes can be as wide as 200 (=2*100)
            public void forEachNode(Node node) {
                double x = node.drawx;
                double y = node.drawy;

                double dist = Math.sqrt((mpx - x) * (mpx - x) + (mpy - y) * (mpy - y));

                if ((dist < minoverdist) && node.containsPoint(mpx, mpy)) {
                    minoverdist = dist;
                    monA[0] = node;
                }
            }
        };
        visibleLocality.forAllNodes(fen);

        TGForEachEdge fee = new TGForEachEdge() {

            double minDist = 8; // Tangential distance to the edge

            double minFromDist = 1000; // Distance to the edge's "from" node

            public void forEachEdge(Edge edge) {
                double x = edge.getFrom().drawx;
                double y = edge.getFrom().drawy;
                double dist = edge.distFromPoint(mpx, mpy);
                if (dist < minDist) { // Set the over edge to the edge with
                                        // the minimun tangential distance
                    minDist = dist;
                    minFromDist = Math.sqrt((mpx - x) * (mpx - x) + (mpy - y) * (mpy - y));
                    moeA[0] = edge;
                } else if (dist == minDist) { // If tangential distances are
                                                // identical, chose
                    // the edge whose "from" node is closest.
                    double fromDist = Math.sqrt((mpx - x) * (mpx - x) + (mpy - y) * (mpy - y));
                    if (fromDist < minFromDist) {
                        minFromDist = fromDist;
                        moeA[0] = edge;
                    }
                }
            }
        };
        visibleLocality.forAllEdges(fee);

        setMouseOverN(monA[0]);
        if (monA[0] == null) {
            setMouseOverE(moeA[0]);
        } else {
            setMouseOverE(null);
        }
    }

    TGPoint2D topLeftDraw;

    TGPoint2D bottomRightDraw;

    public TGPoint2D getTopLeftDraw() {
        return new TGPoint2D(topLeftDraw);
    }

    public TGPoint2D getBottomRightDraw() {
        return new TGPoint2D(bottomRightDraw);
    }

    public TGPoint2D getCenter() {
        return tgLensSet.convDrawToReal(getSize().width / 2, getSize().height / 2);
    }

    public TGPoint2D getDrawCenter() {
        return new TGPoint2D(getSize().width / 2, getSize().height / 2);
    }

    public void updateGraphSize() {
        if (topLeftDraw == null) {
            topLeftDraw = new TGPoint2D(0, 0);
        }
        if (bottomRightDraw == null) {
            bottomRightDraw = new TGPoint2D(0, 0);
        }

        TGForEachNode fen = new TGForEachNode() {

            boolean firstNode = true;

            public void forEachNode(Node node) {
                if (firstNode) { // initialize topRight + bottomLeft
                    topLeftDraw.setLocation(node.drawx, node.drawy);
                    bottomRightDraw.setLocation(node.drawx, node.drawy);
                    firstNode = false;
                } else { // Standard max and min finding
                    topLeftDraw.setLocation(Math.min(node.drawx, topLeftDraw.x), Math.min(node.drawy, topLeftDraw.y));
                    bottomRightDraw.setLocation(Math.max(node.drawx, bottomRightDraw.x), Math.max(node.drawy,
                            bottomRightDraw.y));
                }
            }
        };

        visibleLocality.forAllNodes(fen);
    }

    /**
     * Process graph move.
     */
    // TODO FIXME synchronized removed (deadlocks)
    public void processGraphMove() {
        updateDrawPositions();
        updateGraphSize();
    }

    /**
     * Update draw position of a node.
     *
     * @param node
     *            the node position to repaint.
     */
    public void updateDrawPos(Node node) {
        TGPoint2D p = tgLensSet.convRealToDraw(node.x, node.y);
        node.drawx = p.x;
        node.drawy = p.y;
    }

    /**
     * Update position from draw.
     *
     * @param node
     *            the node position to repaint.
     */
    public void updatePosFromDraw(Node node) {
        TGPoint2D p = tgLensSet.convDrawToReal(node.drawx, node.drawy);
        node.x = p.x;
        node.y = p.y;
    }

    /**
     * Update draw positions.
     */
    public void updateDrawPositions() {
        TGForEachNode fen = new TGForEachNode() {

            public void forEachNode(Node node) {
                updateDrawPos(node);
            }
        };
        visibleLocality.forAllNodes(fen);
    }

    /**
     * Returns the brighter <code>Color</code> of given color.
     *
     * @param c
     *            the color to process
     * @return Returns the brighter color.
     */
    Color myBrighter(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        r = Math.min(r + 96, 255);
        g = Math.min(g + 96, 255);
        b = Math.min(b + 96, 255);

        return new Color(r, g, b);
    }

    /**
     * Repaint after move.
     */
    public void repaintAfterMove() {
        processGraphMove();
        findMouseOver();
        fireMovedEvent();
        repaint();
    }

    /*
     * (non-Javadoc)
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    // TODO FIXME synchronized removed (vector operation encapsulate -> change to sync vector and remove also that lock)
    public void paint(Graphics g) {
        long start = System.currentTimeMillis();

        Dimension d = getSize();
        if ((offscreen == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)) {
            offscreen = createImage(d.width, d.height);
            offscreensize = d;
            offgraphics = offscreen.getGraphics();

            processGraphMove();
            findMouseOver();
            fireMovedEvent();
        }

        offgraphics.setColor(BACK_COLOR);
        offgraphics.fillRect(0, 0, d.width, d.height);

        // TODO FIXME synchronized changed: this -> paintListeners
        synchronized (paintListeners) {
            paintListeners = (Vector) paintListeners.clone();
        }

        for (int i = 0; i < paintListeners.size(); i++) {
            TGPaintListener pl = (TGPaintListener) paintListeners.elementAt(i);
            pl.paintFirst(offgraphics);
        }

        TGForEachEdge fee = new TGForEachEdge() {

            public void forEachEdge(Edge edge) {
                edge.paint(offgraphics, TGPanel.this);
            }
        };

        visibleLocality.forAllEdges(fee);

        for (int i = 0; i < paintListeners.size(); i++) {
            TGPaintListener pl = (TGPaintListener) paintListeners.elementAt(i);
            pl.paintAfterEdges(offgraphics);
        }

        TGForEachNode fen = new TGForEachNode() {

            public void forEachNode(Node node) {
                node.paint(offgraphics, TGPanel.this);
            }
        };

        visibleLocality.forAllNodes(fen);

        if (mouseOverE != null) { // Make the edge the mouse is over appear on
                                    // top.
            mouseOverE.paint(offgraphics, this);
            mouseOverE.getFrom().paint(offgraphics, this);
            mouseOverE.getTo().paint(offgraphics, this);
        }

        if (select != null) { // Make the selected node appear on top.
            select.paint(offgraphics, this);
        }

        if (mouseOverN != null) { // Make the node the mouse is over appear on
                                    // top.
            mouseOverN.paint(offgraphics, this);
        }

        for (int i = 0; i < paintListeners.size(); i++) {
            TGPaintListener pl = (TGPaintListener) paintListeners.elementAt(i);
            pl.paintLast(offgraphics);
        }

        Graphics2D g2 = (Graphics2D) offgraphics;

        int x, y, w;
        // animate warp
        if (warpEnabled) {
            float ac;
            alphaChannel += 0.05f;
            if (alphaChannel > 1f) {
                if (alphaChannel > 2f) {
                    warpEnabled = false;
                    return;
                }
                ac = 2f - alphaChannel;
            } else {
                ac = alphaChannel;
            }
            // TODO get the right color (white/black profile)
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ac));
            if (SpidersGraph.antialiased) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            } else {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            }
            g2.setFont(new Font("Times New Roman", Font.PLAIN, 75));

            FontMetrics fontMetrics = g2.getFontMetrics();
            w = fontMetrics.stringWidth(warpMessage);
            x = d.width / 2 - (w / 2);
            y = fontMetrics.getHeight();
            g2.setPaint(Color.LIGHT_GRAY);
            g2.fillRoundRect(x - 20, 10, w + 40, fontMetrics.getHeight() + 20, 30, 30);
            g2.setPaint(Color.BLACK);
            g2.drawString(warpMessage, x, y);
            g2.setFont(new Font("Times New Roman", Font.ITALIC, 24));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        if (MindRaider.profile.getActiveOutlineUri() != null && MindRaider.outlineCustodian != null
                && MindRaider.outlineCustodian.getActiveOutlineResource() != null) {
            String notebookLabel = MindRaider.outlineCustodian.getActiveOutlineResource().getLabel();
            g2.setFont(new Font("Times New Roman", Font.ITALIC, 24));
            FontMetrics fontMetrics = g2.getFontMetrics();

            x = 15;
            y = d.height - 50;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .6f));
            if (SpidersGraph.antialiased) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            } else {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            }
            g2.setPaint(Color.LIGHT_GRAY);
            g2.fillRoundRect(x - 35, y, x + 35 + 20 + fontMetrics.stringWidth("Notebook: " + notebookLabel),
                    fontMetrics.getHeight() + 10, 30, 30);
            g2.setPaint(Color.BLACK);
            y += fontMetrics.getHeight();
            g2.drawString("Notebook:  ", x + 10, y);
            g2.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            g2.drawString(notebookLabel, x + 10 + fontMetrics.stringWidth("Notebook: "), y);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        /*
         * y+=50; g2.drawString("Model:", x, y); g2.setFont(new Font("Times New
         * Roman", Font.PLAIN, 12)); y+=22;
         * g2.drawString(MindRaider.profile.activeModel, 50, y);
         */

        paintComponents(offgraphics); // Paint any components that have been
                                        // added to this panel

        /*
         * FPS
         */

        if (SpidersGraph.fps) {
            long delta = System.currentTimeMillis() - start;

            g2.setFont(new Font("Verdana", Font.BOLD, 10));
            g2.setPaint(Color.YELLOW);
            g2.drawString("" + (delta == 0 ? 1000 : 1000l / delta) + " FPS/" + visibleLocality.nodeCount() + " nodes",
                    10, 15);
        }

        g.drawImage(offscreen, 0, 0, null);
    }

    /**
     * The warp enabled flag.
     */
    public boolean warpEnabled;

    /**
     * The alpha channel value.
     */
    float alphaChannel;

    /**
     * The warp message string.
     */
    String warpMessage;

    /**
     * Start warp.
     *
     * @param message
     *            the message.
     */
    public synchronized void warpStart(String message) {
        warpEnabled = true;
        alphaChannel = 0.0f;
        warpMessage = message;
    }

    public static void main(String[] args) {

        Frame frame;
        frame = new Frame("TGPanel");
        TGPanel tgPanel = new TGPanel();

        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        TGLensSet tgls = new TGLensSet();
        tgls.addLens(tgPanel.getAdjustOriginLens());
        tgPanel.setLensSet(tgls);
        try {
            tgPanel.addNode(); // Add a starting node.
        } catch (TGException tge) {
            System.err.println(tge.getMessage());
        }
        tgPanel.setVisible(true);
        new GLEditUI(tgPanel).activate();
        frame.add("Center", tgPanel);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
}