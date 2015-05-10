/**
 * Copyright $ L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mindcognition.mindraider.commons.config;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.emental.mindraider.ui.panels.DirectoriesJPanel;
import com.emental.mindraider.ui.panels.LaunchersJPanel;
import com.emental.mindraider.ui.panels.ProfileJPanel;
import com.l2fprod.common.swing.JButtonBar;
import com.l2fprod.common.swing.plaf.blue.BlueishButtonBarUI;
import com.l2fprod.common.util.ResourceManager;

public class ButtonBarMain extends JPanel {

    static ResourceManager RESOURCE = ResourceManager.get(ButtonBarMain.class);

    private static final long serialVersionUID = 1L;

    public static LaunchersJPanel launchersJPanel;

    public static ProfileJPanel profileJPanel;

    public ButtonBarMain() {
        setLayout(new BorderLayout());

        JButtonBar toolbar = new JButtonBar(SwingConstants.VERTICAL);
        toolbar.setUI(new BlueishButtonBarUI());
        add("Center", new ButtonBarPanel(toolbar));
    }

    static class ButtonBarPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private Component currentComponent;

        public ButtonBarPanel(JButtonBar toolbar) {
            setLayout(new BorderLayout());

            add("West", toolbar);

            ButtonGroup group = new ButtonGroup();

            // welcome
            JPanel jpanel = new JPanel();
            jpanel.add(new JLabel("<html><br><br>Set up:<ul>" + "<li>Your personal profile.</li>"
                    + "<li>Attachment launchers.</li>" + "<li>Profile and repository directories.</li></html>"));
            addButton(RESOURCE.getString("Main.welcome"), "programIcon.gif", makePanel(RESOURCE
                    .getString("Main.welcome"), jpanel), toolbar, group);

            profileJPanel = new ProfileJPanel();
            addButton(RESOURCE.getString("Main.profile"), "preferencesUser.png", makePanel(RESOURCE
                    .getString("Main.profile"), profileJPanel), toolbar, group);

            // launchers
            launchersJPanel = new LaunchersJPanel();
            addButton(RESOURCE.getString("Main.launchers"), "browserChannels.png", makePanel(RESOURCE
                    .getString("Main.launchers"), launchersJPanel), toolbar, group);

            // directories
            addButton(RESOURCE.getString("Main.folder"), "preferencesDirectories.png", makePanel(RESOURCE
                    .getString("Main.folder"), new DirectoriesJPanel()), toolbar, group);

            // properties - TODO this is a new generation of settings that will replace all others
//            addButton(
//                    RESOURCE.getString("Main.settings"),
//                    "propertysheet32x32.png", 
//                    makePanel(RESOURCE.getString("Main.settings"), new ConfigurationPropertySheetPage()),
//                    toolbar, 
//                    group);
        }

        private JPanel makePanel(String title, JPanel content) {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel top = new JLabel(title);
            top.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            top.setFont(top.getFont().deriveFont(Font.BOLD));
            top.setOpaque(true);
            top.setBackground(panel.getBackground().brighter());
            panel.add("North", top);
            panel.add("Center", content);
            panel.setPreferredSize(new Dimension(400, 300));
            panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            return panel;
        }

        protected void show(Component component) {
            if (currentComponent != null) {
                remove(currentComponent);
            }
            add("Center", currentComponent = component);
            revalidate();
            repaint();
        }

        private void addButton(String title, String iconUrl, final Component component, JButtonBar bar,
                ButtonGroup group) {
            Action action = new AbstractAction(title, IconsRegistry.getImageIcon(iconUrl)) {

                private static final long serialVersionUID = 1L;

                public void actionPerformed(ActionEvent e) {
                    show(component);
                }
            };

            JToggleButton button = new JToggleButton(action);
            bar.add(button);

            group.add(button);

            if (group.getSelection() == null) {
                button.setSelected(true);
                show(component);
            }
        }
    }
}
