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

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;

/**
 * Import from TWiki.
 */
public class ImportFromTwikiJDialog extends ProgramIconJDialog {

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The concept name URL label.
     */
    public JLabel conceptNameJLabel;

    /**
     * Constructor.
     */
    public ImportFromTwikiJDialog() {
        super(Messages.getString("ImportFromTwikiJDialog.title"));

        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 3));

        p.add(new JLabel(Messages
                .getString("ImportFromTwikiJDialog.importingConcept")));
        conceptNameJLabel = new JLabel("                            ");
        p.add(conceptNameJLabel);

        getContentPane().add(p, BorderLayout.CENTER);

        // show
        pack();
        Gfx.centerAndShowWindow(this);
    }
}
