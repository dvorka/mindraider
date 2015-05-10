/*
 ===========================================================================
   Copyright 2002-2011 Martin Dvorak

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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.emental.mindraider.core.MindRaider;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;
import com.mindcognition.mindraider.utils.Launcher;

@SuppressWarnings("serial")
public class MindForgerCredentialsDialog extends ProgramIconJDialog {
    
    public static enum Type {
        UPLOAD,
        DOWNLOAD
    }

    private JTextField usernameTextField;
    private JPasswordField passwordTextField;

    public MindForgerCredentialsDialog(Type type) {
        super();    
        
        switch (type) {
        case UPLOAD:
            setTitle(Messages.getString("MindForgerJCredentialsDialog.titleUpload"));            
            break;
        case DOWNLOAD:
            setTitle(Messages.getString("MindForgerJCredentialsDialog.titleDownload"));                        
            break;
        }
        
        JPanel dialogPanel = new JPanel();
        dialogPanel.setBorder(new EmptyBorder(5, 10, 0, 10));
        dialogPanel.setLayout(new BorderLayout());
        
        JPanel contentAndButtons = new JPanel(new GridLayout(4,1));

        // 1
        JLabel description;
        switch (type) {
        case UPLOAD:
            description = new JLabel(Messages.getString("MindForgerJCredentialsDialog.descriptionUpload"));
            break;
        default:
            description = new JLabel(Messages.getString("MindForgerJCredentialsDialog.descriptionDownload"));
            break;
        }
        description.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Launcher.launchInBrowser("http://mindraider.sourceforge.net/mindforger.html");                
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }            
        });
        contentAndButtons.add(description);
        // 2a
        JPanel rowPanel = new JPanel();
        rowPanel.add(new JLabel(Messages.getString("MindForgerJCredentialsDialog.username")));
        // 2b
        usernameTextField = new JTextField((MindRaider.mindForgerUsername!=null?MindRaider.mindForgerUsername:""),30);
        usernameTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // submit
            }
        });
        rowPanel.add(usernameTextField);
        contentAndButtons.add(rowPanel);
        // 3a
        rowPanel = new JPanel();
        rowPanel.add(new JLabel(Messages.getString("MindForgerJCredentialsDialog.password")));
        // 3b
        passwordTextField = new JPasswordField((MindRaider.mindForgerPassword!=null?MindRaider.mindForgerPassword:""),30);
        passwordTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // submit
            }
        });
        rowPanel.add(passwordTextField);
        contentAndButtons.add(rowPanel);
        
        // left
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 5));
        JButton okButton;
        switch (type) {
        case UPLOAD:
            okButton = new JButton(Messages.getString("MindForgerJCredentialsDialog.okUpload"));
            break;
        default:
            okButton = new JButton(Messages.getString("MindForgerJCredentialsDialog.okDownload"));
            break;
        }
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveCredentials();
            }
        });
        p.add(okButton);
        // right
        JButton cancelButton = new JButton(Messages.getString("MindForgerJCredentialsDialog.cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelled=true;
                dispose();
            }
        });
        p.add(cancelButton);
        // 4
        contentAndButtons.add(p);
        
        dialogPanel.add(contentAndButtons,BorderLayout.CENTER);

        getContentPane().add(dialogPanel, BorderLayout.CENTER);

        // show
        pack();
        Dimension ddww = getSize();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(new Point((screen.width - ddww.width) / 2,
                (screen.height - ddww.height) / 2));
        setVisible(true);
    }
    
    public boolean cancelled=true;
    
    private void saveCredentials() {
        if((usernameTextField==null || "".equals(usernameTextField.getText()))
                && (passwordTextField==null || "".equals(passwordTextField.getPassword()))) {
            JOptionPane.showMessageDialog(
                    this,
                    "Credentials Error", 
                    "Credentials cannot be empty!",
                    JOptionPane.ERROR_MESSAGE);
            return; 
        }
        cancelled=false;
        
        MindRaider.mindForgerUsername=usernameTextField.getText();
        MindRaider.mindForgerPassword=new String(passwordTextField.getPassword());
        
        dispose();
    }
}
