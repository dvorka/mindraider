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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.emental.mindraider.core.MindRaider;
import com.mindcognition.mindraider.l10n.Messages;

/**
 * Preferences: directories JPanel.
 */
public class DirectoriesJPanel extends JPanel {

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public DirectoriesJPanel() {
        // both of the fields below should be set to the profile and then
        // propagated.

        setLayout(new BorderLayout());

        JPanel topBorderPanel = new JPanel();
        topBorderPanel.setLayout(new BorderLayout());
        topBorderPanel.add(new JLabel("   "), BorderLayout.NORTH);

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 2));
        p.setBorder(new TitledBorder(Messages
                .getString("DirectoriesJPanel.title")));

        p.add(new JLabel(Messages.getString("DirectoriesJPanel.profiles")));
        JTextField profiles = new JTextField(
                MindRaider.profileRegistry.profilesNest);
        profiles.setEnabled(false);
        p.add(profiles);

        p.add(new JLabel(Messages.getString("DirectoriesJPanel.repositories")));
        JTextField yourRepository = new JTextField(MindRaider.profile
                .getHomeDirectory());
        yourRepository.setEnabled(false);
        p.add(yourRepository);

        topBorderPanel.add(p, BorderLayout.CENTER);

        add(topBorderPanel, BorderLayout.NORTH);
    }
}