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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.swing.KeyStroke;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.InputMapUIResource;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;

public class LookAndFeel {
	private static final Logger cat = Logger.getLogger(LookAndFeel.class);
    
    public static final String office2003="org.fife.plaf.Office2003.Office2003LookAndFeel";
    public static final String officeXp="org.fife.plaf.OfficeXP.OfficeXPLookAndFeel";
    public static final String visualStudio="org.fife.plaf.VisualStudio2005.VisualStudio2005LookAndFeel";
	public static final String mac = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
	public static final String metal = "javax.swing.plaf.metal.MetalLookAndFeel";
	public static final String motif = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	public static final String windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	public static final String gtk = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
	private static String currentLookAndFeel = windows;

	public static String get() {
		return currentLookAndFeel;
	}

	public static void set(String lookAndFeel) {
		currentLookAndFeel = lookAndFeel;
	}

	public static String getNative() {
		return UIManager.getSystemLookAndFeelClassName();
	}

	public static void apply() {
		try {
			UIManager.setLookAndFeel(currentLookAndFeel);
		} catch (Throwable e) {
			cat.debug("  Unable to set native look and feel!");
		}
	}

	public static void apply(String lookAndFeel) {
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Throwable e) {
			cat.debug("  Unable to set native look and feel!");
		}
	}

    public static void setUpUI() {
        if (MindRaider.LF_NATIVE.equals(MindRaider.profile.getLookAndFeel())) {
            set(getNative());
            apply();
        }   // otherwise there will be java look&feel
                
        // disable ctrl-shift-o
        String[] uiComponentsToFix=new String[]{
                "TextField.focusInputMap", 
                "PasswordField.focusInputMap",
                "TextArea.focusInputMap",
                "TextPane.focusInputMap",
                "EditorPane.focusInputMap"};
        for (String component : uiComponentsToFix) {
            fixCtrlShiftO(component);            
        }
        
        listFocusInputMap("TextArea.focusInputMap");
    }

    private static void fixCtrlShiftO(String component) {
        InputMapUIResource iomap = (InputMapUIResource)UIManager.get(component);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK);
        iomap.put(keyStroke, null);
    }
    
    public static void listFocusInputMap(String component) {
        InputMapUIResource iomap = (InputMapUIResource)UIManager.get(component);
        KeyStroke[] keys = iomap.keys();
        for (KeyStroke keyStroke : keys) {
            cat.debug("Component"+component+":");
            cat.debug("  "+keyStroke+" -> "+iomap.get(keyStroke)+"");
        }
        
    }
    
    public static void listUiDefaults(String args[]) throws Exception {
        UIManager.LookAndFeelInfo looks[] = UIManager.getInstalledLookAndFeels();

        for (UIManager.LookAndFeelInfo info : looks) {
          UIManager.setLookAndFeel(info.getClassName());

          UIDefaults defaults = UIManager.getDefaults();
          Enumeration newKeys = defaults.keys();

          while (newKeys.hasMoreElements()) {
            Object obj = newKeys.nextElement();
            System.out.printf("%50s : %s\n", obj, UIManager.get(obj));
          }
        }
      }    
}
