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
package com.mindcognition.mindraider.commons.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.emental.mindraider.core.MindRaider;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;

public class PreferencesJDialog extends ProgramIconJDialog {

    public PreferencesJDialog() {
        super(Messages.getString("PreferencesJDialog.title"));

        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BorderLayout());
        getContentPane().add(jpanel);
        jpanel.add(new ButtonBarMain(), BorderLayout.CENTER);

        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton(Messages.getString("PreferencesJDialog.add"));
        p.add(addButton);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // save settings
                MindRaider.profile.setLocalLauncherCommand(ButtonBarMain.launchersJPanel.local.getText());
                MindRaider.profile.setWebLauncherPath(ButtonBarMain.launchersJPanel.web.getText());
                MindRaider.profile.setEnableCommandDaemon(ButtonBarMain.launchersJPanel.isCommandDaemonEnabled());

                MindRaider.profile.setOverrideSystemLocale(ButtonBarMain.profileJPanel.forceLocale.isSelected());
                if(MindRaider.profile.isOverrideSystemLocale()) {
                    MindRaider.profile.setCustomLocale(ButtonBarMain.profileJPanel.locales.getSelectedItem().toString());
                }

                MindRaider.profile.save();
                PreferencesJDialog.this.dispose();
            }
        });

        JButton cancelButton = new JButton(Messages.getString("PreferencesJDialog.cancel"));
        p.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PreferencesJDialog.this.dispose();
            }
        });
        jpanel.add(p, BorderLayout.SOUTH);

        // show
        pack();
        Dimension ddww = MindRaider.mainJFrame.getSize();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(new Point((screen.width - ddww.width) / 2 + 250, (screen.height - ddww.height) / 2 + 150));
        setVisible(true);
    }

    private static final long serialVersionUID = -7602471210790069785L;
}
