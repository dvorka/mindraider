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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.ui.dialogs.AttachmentJDialog;
import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.emental.mindraider.ui.table.AttachmentsTableModel;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.concept.ConceptJPanel;
import com.touchgraph.graphlayout.Node;

/**
 * Panel of attachments.
 */
public class ConceptAttachmentsJPanel extends JPanel {

    /**
     * logger
     */
    private static final Log logger = LogFactory.getLog(ConceptAttachmentsJPanel.class); // {{debug}}
    
    private ConceptJPanel conceptJPanel;
    private JTable attachments;
    private AttachmentsTableModel attachmentsTableModel;
    
    public ConceptAttachmentsJPanel() {
    }

    public void init() {
        JPanel pp;
        setBorder(new TitledBorder(Messages.getString("ConceptJPanel.attachments")));
        setLayout(new BorderLayout());

        // button
        pp = new JPanel();
        pp.setLayout(new GridLayout(3, 1));
        JButton jbutton = new JButton("", IconsRegistry.getImageIcon("attach.png"));
        jbutton.setToolTipText(Messages.getString("ConceptJPanel.attachLocalResourceToConcept"));
        jbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // some concept must be selected - select graph node according
                // to the selected concept =-> touchgraph
                // method needed
                if (conceptJPanel.getConceptResource() == null) {
                    JOptionPane.showMessageDialog(MindRaider.mainJFrame, Messages
                            .getString("ConceptJPanel.attachResourceWarning"), Messages
                            .getString("ConceptJPanel.attachmentError"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                new AttachmentJDialog(conceptJPanel.getConceptResource());
            }
        });
        pp.add(jbutton);
        // @todo edit resource description
        // jbutton=new JButton("",IconsRegistry.getImageIcon("attachLink.png"));
        // jbutton.setToolTipText("Attach web resource to concept");
        // jbutton.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // }
        // });
        // pp.add(jbutton);
        // attach another concept/folder/resource... - perhaps extra frame with
        // radio
        jbutton = new JButton("", IconsRegistry.getImageIcon("launch.png"));
        jbutton.setToolTipText(Messages.getString("ConceptJPanel.showAttachment"));
        jbutton.addActionListener(new ActionListener() {            
            public void actionPerformed(ActionEvent e) {
                if (attachmentsTableModel.attachments != null) {
                    if (attachments.getSelectedRow() == -1) {
                        JOptionPane.showMessageDialog(MindRaider.mainJFrame, Messages
                                .getString("ConceptJPanel.selectAttachmentToLaunch"), Messages
                                .getString("ConceptJPanel.attachmentError"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String uri = attachmentsTableModel.attachments[attachments.getSelectedRow()].getUrl();
                    if (uri != null) {
                        Node node = new Node();
                        node.setLabel(uri);
                        node.setURL(uri);
                        node.setType(MindRaider.spidersColorProfileRegistry.getCurrentProfile().getLiteralNodeType());
                        MindRaider.spidersGraph.handleDoubleSelect(node);
                    }
                }
            }
        });
        pp.add(jbutton);
        jbutton = new JButton("", IconsRegistry.getImageIcon("explorerDiscardSmall.png"));
        jbutton.setToolTipText(Messages.getString("ConceptJPanel.removeAttachment"));
        pp.add(jbutton);
        jbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (attachmentsTableModel.attachments != null) {
                    if (getAttachments().getSelectedRow() != -1) {
                        logger.debug(Messages.getString("ConceptJPanel.removingAttachment", getAttachments()
                                .getSelectedRow()));
                        String attachUrl = getAttachmentsTableModel().attachments[getAttachments().getSelectedRow()]
                                .getUrl();
                        logger.debug(Messages.getString("ConceptJPanel.removingAttachmentUrl", attachUrl));
                        if (conceptJPanel.getConceptResource() != null) {
                            // check, that selected node is type of literal
                            // (helper on spiders graph)
                            MindRaider.noteCustodian.removeAttachment(MindRaider.profile.getActiveOutlineUri()
                                    .toString(), conceptJPanel.getConceptResource(), attachUrl);
                            conceptJPanel.refresh();
                            MindRaider.spidersGraph.renderModel();
                            return;
                        }
                    }
                    JOptionPane.showMessageDialog(MindRaider.mainJFrame, Messages
                            .getString("ConceptJPanel.selectAttachmentToRemove"), Messages
                            .getString("ConceptJPanel.attachmentError"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(pp, BorderLayout.EAST);

        // table
        attachmentsTableModel = new AttachmentsTableModel();
        attachments = new JTable(attachmentsTableModel) {

            private static final long serialVersionUID = 1L;

            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);

                AttachmentsTableModel model = (AttachmentsTableModel) getModel();
                tip = Messages.getString("ConceptJPanel.attachmentUrl") + model.attachments[rowIndex].getUrl();
                // You can omit this part if you know you don't
                // have any renderers that supply their own tool
                // tips.
                // tip = super.getToolTipText(e);
                return tip;
            }
        };
        TableColumn column = attachments.getColumnModel().getColumn(0);
        column.setMaxWidth(50);
        attachments.setRowHeight(20);
        attachments.setAutoscrolls(true);
        attachments.setPreferredScrollableViewportSize(new Dimension(500, 70));
        JScrollPane scrollPane = new JScrollPane(attachments);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void setConceptJPanel(ConceptJPanel conceptJPanel) {
        this.conceptJPanel=conceptJPanel;
    }
    
    /**
     * Getter for <code>attachments</code>.
     *
     * @return Returns the attachments.
     */
    public JTable getAttachments() {
        return this.attachments;
    }

    /**
     * Setter for <code>attachments</code>.
     *
     * @param attachments
     *            The attachments to set.
     */
    public void setAttachments(JTable attachments) {
        this.attachments = attachments;
    }

    /**
     * Getter for <code>attachmentsTableModel</code>.
     *
     * @return Returns the attachmentsTableModel.
     */
    public AttachmentsTableModel getAttachmentsTableModel() {
        return this.attachmentsTableModel;
    }

    /**
     * Setter for <code>attachmentsTableModel</code>.
     *
     * @param attachmentsTableModel
     *            The attachmentsTableModel to set.
     */
    public void setAttachmentsTableModel(AttachmentsTableModel attachmentsTableModel) {
        this.attachmentsTableModel = attachmentsTableModel;
    }
    
    private static final long serialVersionUID = 1L;
}