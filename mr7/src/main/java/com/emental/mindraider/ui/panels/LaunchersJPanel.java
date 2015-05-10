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
package com.emental.mindraider.ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.emental.mindraider.core.MindRaider;
import com.mindcognition.mindraider.utils.BrowserLauncher;
import com.mindcognition.mindraider.utils.Launcher;

/**
 * Launcher panel.
 *
 * @author Martin.Dvorak
 */
public class LaunchersJPanel extends JPanel {

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The web resource launcher.
     */
    private String webResourceLauncher;

    /**
     * The local resource launcher.
     */
    private String localResourceLaucher;

    /**
     * The web text field.
     */
    public JTextField web;

    /**
     * The local text field.
     */
    public JTextField local;

    /**
     * Enable command daemon;
     */
    public JCheckBox enableCommandDaemon;

    /**
     * The constructor.
     */
    public LaunchersJPanel() {
        // both of the fields below should be set to the profile and then
        // propagated.
        setWebResourceLauncher(BrowserLauncher.preferredBrowserPath);
        setLocalResourceLaucher(Launcher.getLocalCommand());

        setLayout(new BorderLayout());

        // launchers
        JPanel topBorderPanel = new JPanel();
        topBorderPanel.setLayout(new BorderLayout());
        topBorderPanel.add(new JLabel("   "), BorderLayout.NORTH);

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 2));
        p.setBorder(new TitledBorder(" Resource Launchers "));
        p.add(new JLabel(" Web (Linux only):"));
        web = new JTextField(webResourceLauncher);
        p.add(web);
        p.add(new JLabel(" Local:"));
        local = new JTextField(localResourceLaucher);
        p.add(local);

        topBorderPanel.add(p, BorderLayout.CENTER);

        // daemons
        p = new JPanel();
        p.setLayout(new GridLayout(1, 2));
        p.setBorder(new TitledBorder(" Enable Daemons "));
        p.add(new JLabel(" Command (TWiki import/export):"));
        enableCommandDaemon=new JCheckBox(" (restart needed)");
        enableCommandDaemon.setSelected(MindRaider.profile.isEnableCommandDaemon());
        p.add(enableCommandDaemon);

        topBorderPanel.add(p, BorderLayout.SOUTH);

        add(topBorderPanel, BorderLayout.NORTH);
    }

    /**
     * Getter for <code>localResourceLaucher</code>.
     *
     * @return Returns the localResourceLaucher.
     */
    public String getLocalResourceLaucher() {
        return this.localResourceLaucher;
    }

    /**
     * Setter for <code>localResourceLaucher</code>.
     *
     * @param localResourceLaucher
     *            The localResourceLaucher to set.
     */
    public void setLocalResourceLaucher(String localResourceLaucher) {
        this.localResourceLaucher = localResourceLaucher;
    }

    /**
     * Getter for <code>webResourceLauncher</code>.
     *
     * @return Returns the webResourceLauncher.
     */
    public String getWebResourceLauncher() {
        return this.webResourceLauncher;
    }

    /**
     * Setter for <code>webResourceLauncher</code>.
     *
     * @param webResourceLauncher
     *            The webResourceLauncher to set.
     */
    public void setWebResourceLauncher(String webResourceLauncher) {
        this.webResourceLauncher = webResourceLauncher;
    }

    /**
     * Is common daemon enabled?
     *
     * @return <code>true</code> if the daemon is enabled.
     */
    public boolean isCommandDaemonEnabled() {
        return this.enableCommandDaemon.isSelected();
    }

    /**
     * Enable or disable command daemon.
     *
     * @param state enable or disable.
     */
    public void setCommandDaemonEnabled(boolean state) {
        this.enableCommandDaemon.setSelected(state);
    }
}

