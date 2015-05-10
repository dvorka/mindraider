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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;

public class AboutJDialog extends ProgramIconJDialog {

    public AboutJDialog() {
        super(Messages.getString("AboutJDialog.about",
                MindRaiderConstants.MR_TITLE));

        getContentPane().setLayout(new BorderLayout());

        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        JButton button = new JButton("", IconsRegistry
                .getImageIcon("programIcon.gif"));
        button.setContentAreaFilled(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        p.add(button);

        getContentPane().add(p, BorderLayout.NORTH);

        DecimalFormat formatter = new DecimalFormat("#,###,###");
        
        JPanel gp = new JPanel();
        gp.add(new JLabel(
                "<html>" +
                "<br><center><b>" +
                MindRaiderConstants.MR_TITLE+"</b>"+
                "<br><a href='http://mindraider.sourceforge.net'>http://mindraider.sourceforge.net</a>"+
                "<br>by" +
                "<br>Martin Dvorak" +
                "<br><br>Contributors:"+
                "<br>Alain Goyé" +
                "<br>Francesco Tinti" +
                "<br>Reto Bachmann-Gmuer"+
                "<br><br>&nbsp;&nbsp;&nbsp;Check also <code>readme.txt</code> for 3rd party code and graphics.&nbsp;&nbsp;&nbsp;"+
                "<br>"+
                "<br>JRE: "+System.getProperty("java.version")+", "+System.getProperty("java.vendor")+
                "<br>Memory: "+
                formatter.format(Runtime.getRuntime().freeMemory())+"/"+
                formatter.format(Runtime.getRuntime().totalMemory())+"/"+
                formatter.format(Runtime.getRuntime().maxMemory())+" (free/total/max)"+
                "</center>"+
                "</html>"
        ));
        getContentPane().add(gp, BorderLayout.SOUTH);

        // show
        pack();
        Dimension ddww = getSize();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(new Point((screen.width - ddww.width) / 2,
                (screen.height - ddww.height) / 2));
        setVisible(true);
    }

    private static final long serialVersionUID = 1L;
}
