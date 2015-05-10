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
package com.emental.mindraider.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.ui.frames.MindRaiderMainWindow;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;
import com.mindcognition.mindraider.utils.Utils;

/**
 * New profile JFrame - opened on MR first boot.
 * 
 * @version $Revision: 1.8 $ ($Author: mindraider $)
 * @deprecated interactive installation is no longer used
 */
@Deprecated
public class NewProfileJDialog extends ProgramIconJDialog {

    /**
     * The text field width constant.
     */
    public static final int TEXTFIELD_WIDTH = 39;

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger
            .getLogger(NewProfileJDialog.class);

    /**
     * The escaped host name.
     */
    public String escapedHostname = "localhost";

    /**
     * The Notebooks directory text field.
     */
    public JTextField ementalityHome;

    /**
     * The host name text field.
     */
    private JTextField hostname;

    /**
     * Constructor.
     * 
     * @throws java.awt.HeadlessException
     *             The headless exception.
     */
    public NewProfileJDialog() throws HeadlessException {
        super("Welcome to " + MindRaiderConstants.MR_TITLE + "!");

        Messages.getString("NewProfileJDialog.title");

        JPanel framePanel = new JPanel();
        framePanel.setLayout(new BorderLayout());

        JPanel welcomeJPanel = new JPanel();
        if(MindRaiderConstants.EARLY_ACCESS) {
            welcomeJPanel.setLayout(new GridLayout(8, 1));
        } else {
            welcomeJPanel.setLayout(new GridLayout(4, 1));
        }
        welcomeJPanel.add(new JPanel());
        welcomeJPanel.add(new JLabel("<html>&nbsp;&nbsp;"
                + Messages.getString("NewProfileJDialog.introduction1")
                + "</html>"));
        welcomeJPanel.add(new JLabel("<html>&nbsp;&nbsp;"
                + Messages.getString("NewProfileJDialog.introduction2")
                + "</html>"));
        welcomeJPanel.add(new JPanel());
        
        if(MindRaiderConstants.EARLY_ACCESS) {
            // early access
            welcomeJPanel.add(new JLabel("<html>&nbsp;&nbsp;<b><font color='red'>IMPORTANT</font></b>:</html>"));
            welcomeJPanel.add(new JLabel("<html>&nbsp;&nbsp;This is EARLY ACCESS release of <a href='http://mindraider.sourceforge.net'>MindRaider</a> just for your review. You may try it,</html>"));
            welcomeJPanel.add(new JLabel("<html>&nbsp;&nbsp;but do not use it for your important personal data!</html>"));
            welcomeJPanel.add(new JLabel("<html>&nbsp;</html>"));
        }

        framePanel.add(welcomeJPanel, BorderLayout.NORTH);

        JPanel p = new JPanel();
        p.setBorder(new TitledBorder(Messages
                .getString("NewProfileJDialog.basicConfiguration")));
        p.setLayout(new GridLayout(2, 1));

        JPanel pp = new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pp.add(new JLabel(Messages
                .getString("NewProfileJDialog.resourceDirectory")));
        ementalityHome = new JTextField(TEXTFIELD_WIDTH);
        String notebookPath = Utils.normalizePath(MindRaider.user.getHome());
        ementalityHome.setText(notebookPath);
        ementalityHome.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    checkNotebooksLocation();
                }
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        pp.add(ementalityHome);
        p.add(pp);

        pp = new JPanel();
        pp.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pp.add(new JLabel(Messages.getString("NewProfileJDialog.hostname")));
        hostname = new JTextField(TEXTFIELD_WIDTH);
        hostname.setText(Utils.getHostname());
        hostname.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        pp.add(hostname);
        p.add(pp);

        framePanel.add(p, BorderLayout.CENTER);

        p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton(Messages
                .getString("NewProfileJDialog.letMRRule"));
        p.add(addButton);
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                checkNotebooksLocation();
            }
        });
        addButton = new JButton(Messages.getString("NewProfileJDialog.cancel"));
        p.add(addButton);
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        framePanel.add(p, BorderLayout.SOUTH);

        getContentPane().add(framePanel, BorderLayout.CENTER);

        p = new JPanel();
        p.setBorder(new TitledBorder(Messages
                .getString("NewProfileJDialog.identitySettings")));
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        p.add(new JLabel(Messages.getString("NewProfileJDialog.foafLocation")));
        JTextField foafTextfield = new JTextField(TEXTFIELD_WIDTH);
        foafTextfield.setEnabled(false);
        p.add(foafTextfield);
        
        getContentPane().add(p, BorderLayout.SOUTH);

        // @todo add hostname specification there - to create resource URIs
        // @todo add color profile specification

        // show
        pack();
        Gfx.centerAndShowWindow(this);
    }

    /**
     * Check notebook locations.
     */
    protected void checkNotebooksLocation() {
        if ("".equals(ementalityHome.getText())) {
            JOptionPane
                    .showMessageDialog(
                            MindRaiderMainWindow.getFrames()[0],
                            Messages
                                    .getString("NewProfileJDialog.notebookLocationCannotBeEmpty"),
                            Messages
                                    .getString("NewProfileJDialog.profileCreationError"),
                            JOptionPane.WARNING_MESSAGE);
            return;
        }

        // check hostname
        if (hostname.getText() == null || hostname.getText().length() == 0) {
            escapedHostname = "localhost";
        } else {
            // quote hostname
            escapedHostname = Utils.toNcName(hostname.getText(), '-');
            logger.debug(Messages.getString("NewProfileJDialog.quotedHostname",
                    escapedHostname));
        }

        NewProfileJDialog.this.dispose();
    }
}
