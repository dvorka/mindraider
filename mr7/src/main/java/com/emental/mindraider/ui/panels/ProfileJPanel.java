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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.emental.mindraider.core.MindRaider;

/**
 * Preferences: profile panel.
 *
 * @author Martin.Dvorak
 */
public class ProfileJPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public JCheckBox forceLocale;

    public static final String CZECH="czech";
    public static final String ENGLISH="english";
    public static final String ITALIAN="italian";
    public static final String FRENCH="french";
    public static final String[] localeLabels = new String[] { CZECH, ENGLISH, FRENCH, ITALIAN};

    public final JComboBox locales = new JComboBox(localeLabels);

    /**
     * Constructor.
     */
    public ProfileJPanel() {
        // both of the fields below should be set to the profile and then
        // propagated.

        setLayout(new BorderLayout());

        JPanel topBorderPanel = new JPanel();
        topBorderPanel.setLayout(new BorderLayout());
        topBorderPanel.add(new JLabel("   "), BorderLayout.NORTH);

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 2));
        p.setBorder(new TitledBorder(" Your Personal Info "));

        // TODO must be FOAF

        p.add(new JLabel(" Name:"));
        JTextField username = new JTextField();
        username.setEnabled(false);
        p.add(username);

        p.add(new JLabel(" FOAF URL:"));
        JTextField foaf = new JTextField("");
        foaf.setEnabled(false);
        p.add(foaf);

        topBorderPanel.add(p, BorderLayout.CENTER);

        JPanel systemAndLanguagePanel = new JPanel();
        systemAndLanguagePanel.setLayout(new BorderLayout());

        p = new JPanel();
        p.setLayout(new GridLayout(2, 2));
        p.setBorder(new TitledBorder(" Language "));
        // force
        p.add(new JLabel(" Force locale:"));
        forceLocale = new JCheckBox("(restart needed)");
        forceLocale.setSelected(MindRaider.profile.isOverrideSystemLocale());
        p.add(forceLocale);
        forceLocale.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    locales.setEnabled(true);
                } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                    locales.setEnabled(false);
                }
            }
        });
        // lang
        p.add(new JLabel());
        locales.setEnabled(forceLocale.isSelected());
        int index=0;
        for (int i = 0; i < localeLabels.length; i++) {
            if(localeLabels[i].equals(MindRaider.profile.getCustomLocale())) {
                index=i;
                break;
            }
        }
        locales.setSelectedIndex(index);
        p.add(locales);
        systemAndLanguagePanel.add(p, BorderLayout.NORTH);

        p = new JPanel();
        p.setLayout(new GridLayout(2, 2));
        p.setBorder(new TitledBorder(" System Info "));
        // login
        p.add(new JLabel(" Login:"));
        JTextField systemLogin = new JTextField(MindRaider.user.getName());
        systemLogin.setEnabled(false);
        p.add(systemLogin);
        // home
        p.add(new JLabel(" Home:"));
        JTextField home = new JTextField(MindRaider.user.getHome());
        home.setEnabled(false);
        p.add(home);
        systemAndLanguagePanel.add(p, BorderLayout.SOUTH);

        topBorderPanel.add(systemAndLanguagePanel, BorderLayout.SOUTH);

        add(topBorderPanel, BorderLayout.NORTH);
    }

}
