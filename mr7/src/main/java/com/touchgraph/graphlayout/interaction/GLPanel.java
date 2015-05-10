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

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Point;
import java.awt.Scrollbar;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

import com.emental.mindraider.ui.graph.spiders.GraphPopUpBuilder;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;
import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGException;
import com.touchgraph.graphlayout.TGLensSet;
import com.touchgraph.graphlayout.TGPanel;
import com.touchgraph.graphlayout.graphelements.TGForEachNode;

/**
 * GLPanel contains code for adding scrollbars and interfaces to the TGPanel The
 * "GL" prefix indicates that this class is GraphLayout specific, and will
 * probably need to be rewritten for other applications.
 * 
 * @author Alexander Shapiro
 */
public class GLPanel extends JPanel {

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The label for zoom menu item.
     */
    public String zoomLabel = " Zoom ";

    /**
     * The label for rotate menu item.
     */
    public String rotateLabel = " Rotate ";

    /**
     * The label for locality menu item.
     */
    public String localityLabel = " Look ahead ";

    /**
     * The label for Hyper menu item.
     */
    public String hyperLabel = " Hyperbolic ";

    /**
     * The HVScroll.
     */
    public HVScroll hvScroll;

    /**
     * The zoom scroll.
     */
    public ZoomScroll zoomScroll;

    /**
     * The hyper scroll. This is unused!.
     */
    public HyperScroll hyperScroll;

    /**
     * The rotate scroll.
     */
    public RotateScroll rotateScroll;

    /**
     * The locality scroll.
     */
    public LocalityScroll localityScroll;

    /**
     * The gl popup.
     */
    public JPopupMenu glPopup;

    /**
     * The scroll bar hash table.
     */
    public Hashtable scrollBarHash;

    /**
     * The TGUI manager.
     */
    public TGUIManager tgUIManager;

    /**
     * The array to hold images. Added by Brendan.
     */
    public Image[] imgList;

    /**
     * The TG panel.
     */
    protected TGPanel tgPanel;

    /**
     * The TG lens set.
     */
    protected TGLensSet tgLensSet;

    /**
     * The current scrollbar.
     */
    private Scrollbar currentSB;

    /**
     * The controls visible flag.
     */
    private boolean controlsVisible = true;

    /**
     * The default background color.
     */
    private Color defaultBackColor = new Color(0x01, 0x11, 0x44);

    /**
     * The default border background color.
     */
    private Color defaultBorderBackColor = new Color(0x02, 0x35, 0x81);

    /**
     * The default foreground color.
     */
    private Color defaultForeColor = new Color((float) 0.95, (float) 0.85,
            (float) 0.55);

    /**
     * Default constructor.
     */
    public GLPanel() {
        this.setBackground(defaultBorderBackColor);
        this.setForeground(defaultForeColor);
        scrollBarHash = new Hashtable();
        tgLensSet = new TGLensSet();
        tgPanel = new TGPanel();
        tgPanel.setBackColor(defaultBackColor);
        hvScroll = new HVScroll(tgPanel, tgLensSet);
        zoomScroll = new ZoomScroll(tgPanel);
        hyperScroll = new HyperScroll(tgPanel);
        rotateScroll = new RotateScroll(tgPanel);
        localityScroll = new LocalityScroll(tgPanel);

        // moved by Brendan to allow Image list setting
        // initialize();
    }

    /**
     * Setter for the image list.
     * 
     * @param imgList
     *            The image list to set.
     */
    public void setImageList(Image[] imgList) {
        this.imgList = imgList;
    }

    /**
     * Initialize panel, lens, and establish a random graph as a demonstration.
     */
    public void initialize() {
        ementalityBuildPanel();
        // buildPanel();
        buildLens();
        tgPanel.setLensSet(tgLensSet);
        addUIs();
        setVisible(true);
    }

    /**
     * Return the TGPanel used with this GLPanel.
     * 
     * @return Returns the TG panel.
     */
    public TGPanel getTGPanel() {
        return tgPanel;
    }

    /**
     * Return the HVScroll used with this GLPanel.
     * 
     * @return Returns the hvscroll.
     */
    public HVScroll getHVScroll() {
        return hvScroll;
    }

    /**
     * Return the HyperScroll used with this GLPanel.
     * 
     * @return Returns the hyper scroll.
     */
    public HyperScroll getHyperScroll() {
        return hyperScroll;
    }

    /**
     * Setter for the horizontal offset.
     * 
     * @param p
     *            the point offset.
     */
    public void setOffset(Point p) {
        hvScroll.setOffset(p);
    }

    /**
     * Getter for the horizontal and vertical offset position as a Point.
     * 
     * @return the point offset.
     */
    public Point getOffset() {
        return hvScroll.getOffset();
    }

    /**
     * Return the RotateScroll used with this GLPanel.
     * 
     * @return the rotate scroll.
     */
    public RotateScroll getRotateScroll() {
        return rotateScroll;
    }

    /**
     * Setter for rotation angle. Allowed values from 0 to 359.
     * 
     * @param angle
     *            the rotation value to set.
     */
    public void setRotationAngle(int angle) {
        rotateScroll.setRotationAngle(angle);
    }

    /**
     * Return the rotation angle of this GLPanel.
     * 
     * @return the rotate scroll value.
     */
    public int getRotationAngle() {
        return rotateScroll.getRotationAngle();
    }

    /**
     * Return the LocalityScroll used with this GLPanel.
     * 
     * @return the locality scroll.
     */
    public LocalityScroll getLocalityScroll() {
        return localityScroll;
    }

    /**
     * Setter for locality radius. Allowed values between 0 to 4, or
     * <code>LocalityUtils.INFINITE_LOCALITY_RADIUS</code>.
     * 
     * @param radius
     *            the local radius value.
     */
    public void setLocalityRadius(int radius) {
        localityScroll.setLocalityRadius(radius);
    }

    /**
     * Getter for the <code>localityScroll</code> locality radius.
     * 
     * @return Returns the locality radius value.
     */
    public int getLocalityRadius() {
        return localityScroll.getLocalityRadius();
    }

    /**
     * Getter for <code>zoomScroll</code> zoom scrool.
     * 
     * @return Returns the zoom scroll.
     */
    public ZoomScroll getZoomScroll() {
        return zoomScroll;
    }

    /**
     * Setter for the <code>zoomScroll</code> zoom value. Allowable values
     * between -100 to 100).
     * 
     * @param zoomValue
     *            the zoom value.
     */
    public void setZoomValue(int zoomValue) {
        zoomScroll.setZoomValue(zoomValue);
    }

    /**
     * Getter for the <code>zoomScroll</code> zoom scrool.
     * 
     * @return Returns the zoom value.
     */
    public int getZoomValue() {
        return zoomScroll.getZoomValue();
    }

    /**
     * Getter for <code>glPopup</code> popup.
     * 
     * @return Returns the popup.
     */
    public JPopupMenu getGLPopup() {
        return glPopup;
    }

    /**
     * Build lens.
     */
    public void buildLens() {
        tgLensSet.addLens(hvScroll.getLens());
        tgLensSet.addLens(zoomScroll.getLens());
        tgLensSet.addLens(hyperScroll.getLens());
        tgLensSet.addLens(rotateScroll.getLens());
        tgLensSet.addLens(tgPanel.getAdjustOriginLens());
    }

    /*
     * public void buildPanel() { final Scrollbar horizontalSB =
     * hvScroll.getHorizontalSB(); final Scrollbar verticalSB =
     * hvScroll.getVerticalSB(); final Scrollbar zoomSB =
     * zoomScroll.getZoomSB(); final Scrollbar rotateSB =
     * rotateScroll.getRotateSB(); final Scrollbar localitySB =
     * localityScroll.getLocalitySB(); final Scrollbar hyperSB =
     * hyperScroll.getHyperSB(); setLayout(new BorderLayout()); JPanel
     * scrollPanel = new JPanel(); scrollPanel.setBackground(defaultBackColor);
     * scrollPanel.setForeground(defaultForeColor); scrollPanel.setLayout(new
     * GridBagLayout()); GridBagConstraints c = new GridBagConstraints(); JPanel
     * modeSelectPanel = new JPanel();
     * modeSelectPanel.setBackground(defaultBackColor);
     * modeSelectPanel.setForeground(defaultForeColor);
     * modeSelectPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0,0)); final
     * JPanel topPanel = new JPanel();
     * topPanel.setBackground(defaultBorderBackColor);
     * topPanel.setForeground(defaultForeColor); topPanel.setLayout(new
     * GridBagLayout()); c.gridy=0; c.fill=GridBagConstraints.HORIZONTAL;
     * c.gridx=0;c.weightx=0; c.insets=new Insets(0,0,0,0);
     * c.gridy=0;c.weightx=1; scrollBarHash.put(zoomLabel, zoomSB);
     * scrollBarHash.put(rotateLabel, rotateSB);
     * scrollBarHash.put(localityLabel, localitySB);
     * scrollBarHash.put(hyperLabel, hyperSB);
     */
    // JPanel scrollselect = scrollSelectPanel(new String[] {zoomLabel,
    // rotateLabel /*, localityLabel*/, hyperLabel});
    /*
     * scrollselect.setBackground(defaultBorderBackColor);
     * scrollselect.setForeground(defaultForeColor);
     * topPanel.add(scrollselect,c); add(topPanel, BorderLayout.SOUTH); c.fill =
     * GridBagConstraints.BOTH; c.gridwidth = 1; c.gridx = 0; c.gridy = 1;
     * c.weightx = 1; c.weighty = 1; scrollPanel.add(tgPanel,c); c.gridx = 1;
     * c.gridy = 1; c.weightx = 0; c.weighty = 0; //
     * scrollPanel.add(verticalSB,c); // For WDR We do not need scrollbars
     * c.gridx = 0; c.gridy = 2; // scrollPanel.add(horizontalSB,c); // For WDR
     * We do not need scrollbars add(scrollPanel,BorderLayout.CENTER); glPopup =
     * new PopupMenu(); add(glPopup); // needed by JDK11 Popupmenu.. MenuItem
     * menuItem = new MenuItem("Toggle Controls"); ActionListener
     * toggleControlsAction = new ActionListener() { boolean controlsVisible =
     * true; public void actionPerformed(ActionEvent e) { controlsVisible =
     * !controlsVisible; horizontalSB.setVisible(controlsVisible);
     * verticalSB.setVisible(controlsVisible);
     * topPanel.setVisible(controlsVisible); GLPanel.this.doLayout(); } };
     * menuItem.addActionListener(toggleControlsAction); glPopup.add(menuItem); }
     */

    /**
     * Returns the scroll select panel.
     * 
     * @param scrollBarNames
     *            the scroll bar names array.
     * @return Returns the scrollbar select panel.
     */
    protected JPanel scrollSelectPanel(final String[] scrollBarNames) {
        final JPanel sbp = new JPanel(new GridBagLayout());

        // UI: Scrollbarselector via
        // Radiobuttons.................................

        sbp.setBackground(defaultBorderBackColor);
        sbp.setForeground(defaultForeColor);

        JPanel firstRow = new JPanel(new GridBagLayout());

        final CheckboxGroup bg = new CheckboxGroup();

        int cbNumber = scrollBarNames.length;
        Checkbox checkboxes[] = new Checkbox[cbNumber];

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.gridy = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < cbNumber; i++) {
            checkboxes[i] = new Checkbox(scrollBarNames[i], true, bg);
            c.gridx = i;
            firstRow.add(checkboxes[i], c);
        }
        checkboxes[0].setState(true);

        c.gridx = cbNumber;
        c.weightx = 1;
        Label lbl = new Label(
                "     Right-click nodes and background for more options");
        firstRow.add(lbl, c);

        class radioItemListener implements ItemListener {

            public radioItemListener(String str2Act) {
            }

            public void itemStateChanged(ItemEvent e) {
                Scrollbar selectedSB = (Scrollbar) scrollBarHash.get(bg
                        .getSelectedCheckbox().getLabel());
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    for (int i = 0; i < scrollBarNames.length; i++) {
                        Scrollbar sb = (Scrollbar) scrollBarHash
                                .get(scrollBarNames[i]);
                        sb.setVisible(false);
                    }
                    selectedSB.setBounds(currentSB.getBounds());
                    if (selectedSB != null) {
                        selectedSB.setVisible(true);
                    }
                    currentSB = selectedSB;
                    sbp.invalidate();
                }
            }
        }

        for (int i = 0; i < cbNumber; i++) {
            checkboxes[i].addItemListener(new radioItemListener(
                    scrollBarNames[0]));
        }

        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(1, 5, 1, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 10;
        c.gridwidth = 3; // Radiobutton UI
        c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        sbp.add(firstRow, c);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < scrollBarNames.length; i++) {
            Scrollbar sb = (Scrollbar) scrollBarHash.get(scrollBarNames[i]);
            if (sb == null) {
                continue;
            }
            if (currentSB == null) {
                currentSB = sb;
            }
            sbp.add(sb, c);
        }

        return sbp;
    }

    /**
     * Add user interfaces.
     */
    public void addUIs() {
        tgUIManager = new TGUIManager();
        GLEditUI editUI = new GLEditUI(this);
        GLNavigateUI navigateUI = new GLNavigateUI(this);
        tgUIManager.addUI(editUI, "Edit");
        tgUIManager.addUI(navigateUI, "Navigate");
        tgUIManager.activate("Navigate");
    }

    /**
     * Random graph.
     * 
     * @throws TGException
     *             the TG exception.
     */
    public void randomGraph() throws TGException {
        Node n1 = tgPanel.addNode();
        n1.setType(0);
        for (int i = 0; i < 249; i++) {
            tgPanel.addNode();
        }

        TGForEachNode fen = new TGForEachNode() {

            public void forEachNode(Node n) {
                for (int i = 0; i < 5; i++) {
                    Node r = tgPanel.getGES().getRandomNode();
                    if (r != n && tgPanel.findEdge(r, n) == null) {
                        tgPanel.addEdge(r, n, Edge.DEFAULT_LENGTH);
                    }
                }
            }
        };
        tgPanel.getGES().forAllNodes(fen);

        tgPanel.setLocale(n1, 1);
        tgPanel.setSelect(n1);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            //
        }

        getHVScroll().slowScrollToCenter(n1);
    }

    /**
     * The main procedure.
     * 
     * @param args
     *            The argument list.
     */
    public static void main(String[] args) {
        final Frame frame;
        final GLPanel glPanel = new GLPanel();
        frame = new Frame("TouchGraph GraphLayout");
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                frame.remove(glPanel);
                frame.dispose();
            }
        });
        frame.add("Center", glPanel);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    /**
     * The edit mode checkbox.
     */
    public static final JCheckBox editModeCheckBox = new JCheckBox("edit",
            false);

    /**
     * The top panel.
     */
    JPanel topPanel;

    /**
     * Build the e-mentality panel.
     */
    public void ementalityBuildPanel() {

        /*
         * topPanel: graph controls
         */

        final JScrollBar horizontalSB = hvScroll.getHorizontalSB();
        final JScrollBar verticalSB = hvScroll.getVerticalSB();
        final JSlider zoomSB = zoomScroll.getZoomSB();
        final JSlider rotateSB = rotateScroll.getRotateSB();
        final JSlider localitySB = localityScroll.getLocalitySB();
        final JSlider hyperSB = hyperScroll.getHyperSB();

        setLayout(new BorderLayout());

        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        /*
         * JPanel modeSelectPanel = new JPanel();
         * //modeSelectPanel.setBackground(defaultColor);
         * modeSelectPanel.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
         * modeSelectPanel.setBorder(new TitledBorder(" Mode ")); AbstractAction
         * a = new AbstractAction("Mode") { public void
         * actionPerformed(ActionEvent e) { Enumeration
         * enumeration=EmentalityJFrame.getInstance().modeGroup.getElements();
         * if(editModeCheckBox.isSelected()) { tgUIManager.activate("Edit");
         * while(enumeration.hasMoreElements()) { JRadioButtonMenuItem
         * i=((JRadioButtonMenuItem)enumeration.nextElement());
         * if("Edit".equals(i.getLabel())) {
         * EmentalityJFrame.getInstance().modeGroup.setSelected(i.getModel(),true); } } }
         * else { tgUIManager.activate("Navigate"); JRadioButtonMenuItem
         * i=((JRadioButtonMenuItem)enumeration.nextElement());
         * if("Navigate".equals(i.getLabel())) {
         * EmentalityJFrame.getInstance().modeGroup.setSelected(i.getModel(),true); } } } };
         * editModeCheckBox.addActionListener(a);
         * //editCheckBox.setBackground(defaultColor);
         * modeSelectPanel.add(editModeCheckBox);
         */

        // panel containing controls
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 5, 0, 0));
        // topPanel.setBorder(new TitledBorder(" Controls "));

        JPanel p;

        /*
         * searchTextField= new JTextField(10);
         * searchTextField.addKeyListener(new KeyListener() { public void
         * keyPressed(KeyEvent keyEvent) { if (keyEvent.getKeyCode() ==
         * KeyEvent.VK_ENTER) { search(searchTextField.getText(),true);
         * searchTextField.setText(""); } } public void keyReleased(KeyEvent
         * keyEvent) { } public void keyTyped(KeyEvent keyEvent) { } }); p=new
         * JPanel(); p.setBorder(new TitledBorder(" Search ")); p.setLayout(new
         * FlowLayout(FlowLayout.CENTER,0,0)); p.add(searchTextField);
         * topPanel.add(p);
         */

        scrollBarHash.put(zoomLabel, zoomSB);
        scrollBarHash.put(rotateLabel, rotateSB);
        scrollBarHash.put(localityLabel, localitySB);
        scrollBarHash.put(hyperLabel, hyperSB);

        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        localityScroll.tb = new TitledBorder(localityLabel + ": 2 ");
        localityScroll.p = p;
        p.setBorder(localityScroll.tb);
        p.add(localitySB);
        topPanel.add(p);
        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setBorder(new TitledBorder(zoomLabel));
        p.add(zoomSB);
        topPanel.add(p);
        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setBorder(new TitledBorder(rotateLabel));
        p.add(rotateSB);
        topPanel.add(p);
        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setBorder(new TitledBorder(hyperLabel));
        p.add(hyperSB);
        topPanel.add(p);

        // topPanel.add(modeSelectPanel);

        // add control panel
        add(topPanel, BorderLayout.SOUTH);

        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        scrollPanel.add(tgPanel, c);

        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        scrollPanel.add(verticalSB, c);

        c.gridx = 0;
        c.gridy = 2;
        scrollPanel.add(horizontalSB, c);

        add(scrollPanel, BorderLayout.CENTER);

        // build popup
        glPopup = GraphPopUpBuilder.buildNavigationPopup(horizontalSB,
                verticalSB, topPanel);
    }

    /**
     * Search for node using regexp.
     * 
     * @param search
     *            regexp.
     * @return Returns <code>true</code> if a node occurs, otherwise
     *         <code>false</code>.
     */
    public boolean search(String search) {
        StatusBar.show("Searching for: " + search);

        if (search != null && search.length() > 0) {
            // now lets try to lookup the resource and to select it
            Node resultNode;

            resultNode = tgPanel.findNodeLabelContaining(search);
            if (resultNode != null) {
                try {
                    tgPanel.setLocale(resultNode, getLocalityRadius());
                    tgPanel.setSelect(resultNode);
                    StatusBar.show("Node with label matching '" + search
                            + "' found!");
                    return true;
                } catch (TGException ee) {
                    StatusBar.show("Unable to render node which was found!",
                            Color.red);
                }
            }
        }

        StatusBar.show("Node with label matching '" + search + "' not found!");
        return false;
    }

    /**
     * Toggle the control panel.
     */
    public void toggleControlPanel() {
        controlsVisible = !controlsVisible;
        topPanel.setVisible(controlsVisible);
        GLPanel.this.doLayout();
    }
}
