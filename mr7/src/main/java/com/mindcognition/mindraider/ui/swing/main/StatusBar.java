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
package com.mindcognition.mindraider.ui.swing.main;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTextField;

public final class StatusBar {

    private static JTextField statusBar;
    private static JPanel p;
    private static StatusBar singleton;

    private static boolean enabled = true;

    private StatusBar() {
        singleton = this;

        p = new JPanel();
        p.setLayout(new BorderLayout());
        statusBar = new JTextField("");
        statusBar.setEditable(false);
        p.add(statusBar, BorderLayout.CENTER);
    }

    public static JPanel getStatusBar() {
        if (singleton == null) {
            singleton = new StatusBar();
        }
        return p;
    }

    public static StatusBar getInstance() {
        return singleton;
    }

    public static void reinitializeStatusBar() {
        singleton = new StatusBar();
    }

    public static void clear() {
        if (enabled) {
            statusBar.setText("");
        }
    }

    public static void show(String message) {
        if (enabled) {
            statusBar.setText(message);
        }
    }

    public static void show(String message, Color color) {
        if (enabled) {
            statusBar.setForeground(color);
            statusBar.setText(message);
            statusBar.setForeground(Color.BLACK);
        }
    }

    public static void setEnabled(boolean e) {
        enabled = e;
    }

    public static void setText(String message) {
        show(message);
    }
    
    public static void setText(String prefix, String message, int maxLength) {
        if (message != null) {
            if (message.length() > maxLength) {
                message = "..." + message.substring(message.length() - maxLength);
            }
            show(prefix+message);
        }
    }
    
}
