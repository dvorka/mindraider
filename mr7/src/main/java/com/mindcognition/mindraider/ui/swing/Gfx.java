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
package com.mindcognition.mindraider.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

/**
 * GFX utils.
 */
public class Gfx {

    public Toolkit toolkit = Toolkit.getDefaultToolkit();
    
    /**
     * Center and show the window.
     * 
     * @param window
     *            the window to show.
     * @param width
     *            the width.
     * @param height
     *            the height.
     */
    public static void centerAndShowWindow(Window window, int width, int height) {
        window.setSize(width, height);
        centerAndShowWindow(window);
    }

    /**
     * Center and show the window.
     * 
     * @param window
     *            The window to show.
     */
    public static void centerAndShowWindow(Window window) {
        // now put the frame to the center of the total screen
        Dimension ddww = window.getSize(); // get size of frame after pack
        // get size of screen
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        // move window to the center of global screen
        window.setLocation(new Point((screen.width - ddww.width) / 2,
                (screen.height - ddww.height) / 2));

        window.setVisible(true);
    }

    /**
     * Switch from/to fullscreen.
     * 
     * @param window
     *            The window to switch.
     */
    public static void toggleFullScreen(Window window) {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment
                .getDefaultScreenDevice();
        graphicsDevice.setFullScreenWindow(window);
        if (window != null) {
            window.requestFocus();
        }
    }

    /**
     * Get graphics configuration.
     * 
     * @return graphics configuration.
     */
    public static GraphicsConfiguration getGraphicsConfiguration() {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment
                .getDefaultScreenDevice();
        return graphicsDevice.getDefaultConfiguration();
    }
    
    
    /**
     * Get string representation.
     * 
     * @param color
     * @return
     */
    public static String getColorHexString(Color color) {
        return Integer.toHexString(color.getRed())+
        Integer.toHexString(color.getGreen())+
        Integer.toHexString(color.getBlue());
        
    }
}