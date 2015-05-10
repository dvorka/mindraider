/*
 ===========================================================================
   Copyright 2002-2010 Martin Dvorak

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ===========================================================================
*/
package com.emental.mindraider.ui.frames;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;

import com.emental.mindraider.ui.gfx.IconsRegistry;

/**
 * Splash screen.
 * 
 * @author Martin.Dvorak
 */
public class SplashScreen {

    private boolean isApplet;

    /**
     * splash screen
     */
    private JWindow splashScreen;

    /**
     * parent jframe
     */
    private JFrame parent;

    /**
     * splash label
     */
    private JLabel splashLabel;

    /**
     * Constructor.
     * 
     * @param parent
     * @param isApplet
     */
    public SplashScreen(JFrame parent, boolean isApplet) {
        this.isApplet = isApplet;

        splashLabel = new JLabel(IconsRegistry.getImageIcon("splash.gif"));

        if (!isApplet) {
            splashScreen = new JWindow(parent);
            splashScreen.getContentPane().add(splashLabel);
            splashScreen.pack();
            Rectangle screenRect = parent.getGraphicsConfiguration()
                    .getBounds();
            splashScreen
                    .setLocation(screenRect.x + screenRect.width / 2
                            - splashScreen.getSize().width / 2, screenRect.y
                            + screenRect.height / 2
                            - splashScreen.getSize().height / 2);
        }
    }

    /**
     * Show splash screen.
     */
    public void showSplashScreen() {
        if (!isApplet) {
            splashScreen.setVisible(true);
        } else {
            parent.add(splashLabel, BorderLayout.CENTER);
            parent.validate();
            parent.repaint();
        }
    }

    /**
     * Pop down the spash screen.
     */
    public void hideSplash() {
        if (!isApplet) {
            splashScreen.setVisible(false);
            splashScreen.dispose();
            splashScreen = null;
            splashLabel = null;
        }
    }
}
