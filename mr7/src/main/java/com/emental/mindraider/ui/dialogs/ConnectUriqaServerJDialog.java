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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.mindcognition.mindraider.integration.uriqa.UriqaClient;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;

/**
 * Connect to URIQA server.
 * 
 * @author Martin.Dvorak
 * @version $Revision: 1.7 $ ($Author: mindraider $)
 */
public class ConnectUriqaServerJDialog extends ProgramIconJDialog {

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The text areas width constant.
     */
    private static final int TEXT_AREAS_WIDTH = 200;

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger
            .getLogger(ConnectUriqaServerJDialog.class);

    /**
     * The uriqa server url property.
     */
    private JTextField uriqaServerUrl;

    /**
     * The resource uri property.
     */
    private JComboBox resourceUri;

    /**
     * The addInfered property.
     */
    private JCheckBox addInfered;

    /**
     * Constructor.
     */
    public ConnectUriqaServerJDialog() {
        super(Messages.getString("ConnectUriqaServerJDialog.title"));
        getContentPane().setLayout(new BorderLayout());

        // north
        JPanel north = new JPanel();
        north.add(new JLabel(Messages
                .getString("ConnectUriqaServerJDialog.serverUrl")));

        uriqaServerUrl = new JTextField("http://sw.nokia.com/uriqa/", 31);
        uriqaServerUrl.setMinimumSize(new Dimension(TEXT_AREAS_WIDTH,
                uriqaServerUrl.getPreferredSize().height));
        uriqaServerUrl.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    connect();
                }
            }

            public void keyReleased(KeyEvent keyEvent) {
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        north.add(uriqaServerUrl);
        getContentPane().add(north, BorderLayout.NORTH);

        // center
        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());

        JPanel centerTop = new JPanel();
        centerTop.add(new JLabel(Messages
                .getString("ConnectUriqaServerJDialog.resource")));
        String[] knowUriqaUris = new String[] {
                "http://sw.nokia.com/VOC-1/term",
                "http://sw.nokia.com/MARS-3/Rank",
                "http://sw.nokia.com/MARS-3",
                "http://sw.nokia.com/schemas/nokia/MARS-3.1.rdf" };
        resourceUri = new JComboBox(knowUriqaUris);
        resourceUri.setMinimumSize(new Dimension(TEXT_AREAS_WIDTH, resourceUri
                .getPreferredSize().height));
        resourceUri.setEditable(true);
        centerTop.add(resourceUri);

        JPanel centerCenter = new JPanel();
        centerCenter.setLayout(new FlowLayout(FlowLayout.LEFT));
        centerCenter.setToolTipText(Messages
                .getString("ConnectUriqaServerJDialog.loadOverToolTip"));
        centerCenter.add(new JLabel(Messages
                .getString("ConnectUriqaServerJDialog.loadOver")));
        JCheckBox loadOver = new JCheckBox();
        loadOver.setSelected(true);
        loadOver.setEnabled(false);
        centerCenter.add(loadOver);

        JPanel centerBottom = new JPanel();
        centerBottom.setLayout(new FlowLayout(FlowLayout.LEFT));
        centerBottom.setToolTipText(Messages
                .getString("ConnectUriqaServerJDialog.addInferedToolTip"));
        centerBottom.add(new JLabel(Messages
                .getString("ConnectUriqaServerJDialog.addInfered")));
        addInfered = new JCheckBox();
        addInfered.setSelected(true);
        centerBottom.add(addInfered);

        center.add(centerTop, BorderLayout.NORTH);
        center.add(centerCenter, BorderLayout.CENTER);
        center.add(centerBottom, BorderLayout.SOUTH);
        getContentPane().add(center, BorderLayout.CENTER);

        // south
        JPanel south = new JPanel();
        JButton uploadButton = new JButton(Messages
                .getString("ConnectUriqaServerJDialog.connect"));
        uploadButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                connect();
            }
        });
        south.add(uploadButton);

        JButton cancelButton = new JButton(Messages
                .getString("ConnectUriqaServerJDialog.cancel"));
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        south.add(cancelButton);
        getContentPane().add(south, BorderLayout.SOUTH);

        // show
        pack();
        Gfx.centerAndShowWindow(this);
    }

    /**
     * Connect.
     */
    protected void connect() {
        String modelUrl = uriqaServerUrl.getText();
        if (StringUtils.isEmpty(modelUrl)) {
            StatusBar
                    .show(
                            Messages
                                    .getString("ConnectUriqaServerJDialog.invalidModelLocation"),
                            Color.RED);
        } else {
            try {
                logger.debug(addInfered.isSelected());

                // call uriqa URI builder; load model from that URI (and than
                // look in click handler)
                UriqaClient.establishConnection(uriqaServerUrl.getText(),
                        addInfered.isSelected());
                MindRaider.spidersGraph.load(UriqaClient
                        .getResourceRepresentationUrl((String) resourceUri
                                .getSelectedItem()));
            } catch (Exception e) {
                logger.debug(Messages.getString(
                        "ConnectUriqaServerJDialog.unableToConnect", e
                                .getMessage()));
                dispose();
                JOptionPane
                        .showMessageDialog(
                                this,
                                Messages
                                        .getString(
                                                "ConnectUriqaServerJDialog.unableToConnect",
                                                e.getMessage()),
                                Messages
                                        .getString("ConnectUriqaServerJDialog.uricaServerError"),
                                JOptionPane.ERROR_MESSAGE);
                return;
            }
            StatusBar.show(Messages.getString(
                    "ConnectUriqaServerJDialog.modelDownloaded", modelUrl));
        }
        dispose();
    }
}