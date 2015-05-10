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
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.mindcognition.mindraider.ui.swing.Gfx;

/**
 * Progress dialog.
 */
public class ProgressDialogJFrame extends JFrame {

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The constant max.
     */
    private static int MAX = 50;

    /**
     * The progress label.
     */
    private JLabel progressJLabel;

    /**
     * The action label.
     */
    private JLabel actionJLabel;

    /**
     * The progress bar.
     */
    private JProgressBar progressBar;

    /**
     * Constructor.
     * 
     * @param title
     *            The title
     * @param action
     *            The action
     */
    public ProgressDialogJFrame(String title, String action) {
        super(title);
        setIconImage(IconsRegistry.getImage("programIcon.gif"));
        getContentPane().setLayout(new BorderLayout());

        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        actionJLabel = new JLabel(action);
        p.add(actionJLabel, BorderLayout.WEST);
        progressJLabel = new JLabel("");
        progressJLabel.setPreferredSize(new Dimension(390, progressJLabel
                .getHeight()));
        p.add(progressJLabel, BorderLayout.CENTER);
        getContentPane().add(p, BorderLayout.CENTER);

        // progress bar
        JPanel southPanel = new JPanel();
        progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        southPanel.add(progressBar, BorderLayout.SOUTH);
        getContentPane().add(southPanel, BorderLayout.SOUTH);

        // show
        pack();
        Gfx.centerAndShowWindow(this);
    }

    /**
     * Setter for action message.
     * 
     * @param message
     *            The action message to set for <code>actionJLabel</code>
     */
    public void setActionMessage(String message) {
        actionJLabel.setText(message);
    }

    /**
     * Setter for progress message.
     * 
     * @param message
     *            The message to set
     */
    public void setProgressMessage(String message) {
        if (message != null) {
            if (message.length() > MAX) {
                message = "..." + message.substring(message.length() - MAX);
            }
            progressJLabel.setText(message);
        }
    }
}
