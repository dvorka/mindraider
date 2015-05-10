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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.search.SearchResultEntry;
import com.mindcognition.mindraider.application.model.tag.TagEntry;
import com.mindcognition.mindraider.application.model.tag.TaggedResourceEntry;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.Gfx;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;

/**
 * Open notebook by tag. This dialog provides all the tags used through the MR.
 * List contains entries like: <code>tag name (tag-cardinality)</code>. By choosing
 * a tag, concept search dialog is opened...
 * 
 * @author Martin.Dvorak
 */
public class OpenConceptByTagJDialog extends ProgramIconJDialog {
    
    /**
     * logger
     */
    private static final Log logger = LogFactory.getLog(OpenConceptByTagJDialog.class); // {{debug}}
    
    /**
     * serial number
     */
    private static final long serialVersionUID = 7224729094740944343L;

    /**
     * The text field width constant.
     */
    private static final int TEXTFIELD_WIDTH = 30;

    /**
     * all tags
     */
    TagEntry[] tags;
    
    /**
     * The shown descriptor array.
     */
    ArrayList<TagEntry> shownTags = new ArrayList<TagEntry>();

    /**
     * The jlist.
     */
    private JList list;

    /**
     * The default model.
     */
    private DefaultListModel defaultListModel;

    /**
     * Constructor.
     *
     * @param title
     */
    public OpenConceptByTagJDialog() {
        this("Find Note by Tag", " Select the tag: ", "Get Tagged Notes", true);
    }

    public OpenConceptByTagJDialog(String dialogTitle, String selectionLabel, String buttonLabel, boolean showCancel) {
        super(dialogTitle);
        
        JPanel framePanel = new JPanel();
        framePanel.setBorder(new EmptyBorder(5, 10, 0, 10));
        framePanel.setLayout(new BorderLayout());

        // NORTH panel
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(new JLabel(selectionLabel), BorderLayout.NORTH);

        final JTextField tagLabel = new JTextField(TEXTFIELD_WIDTH);
        
        // data
        tags = MindRaider.tagCustodian.getAllTags();;
        if (tags == null) {
            tagLabel.setEnabled(false);
        }
        tagLabel.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    logger.debug("Openning selected tag...");
                    openTagSearchDialog();
                }
            }
            public void keyReleased(KeyEvent keyEvent) {
                getListModel().clear();
                shownTags.clear();
                if (tagLabel.getText().length() > 0) {
                    for (TagEntry tag: tags) {
                        if (tag.getTagLabel().toLowerCase().startsWith(tagLabel.getText().toLowerCase())) {
                            getListModel().addElement(tag.getTagLabel()+" ("+tag.getCardinality()+")");
                            shownTags.add(tag);
                        }
                    }
                } else {
                    // show all tags
                    for (TagEntry tag: tags) {
                        getListModel().addElement(tag.getTagLabel()+" ("+tag.getCardinality()+")");
                        shownTags.add(tag);
                    }
                }
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        northPanel.add(tagLabel, BorderLayout.SOUTH);
        framePanel.add(northPanel, BorderLayout.NORTH);

        // CENTER panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(new JLabel(Messages.getString("OpenConceptByTagJDialog.matchingTags")), BorderLayout.NORTH);

        defaultListModel = new DefaultListModel();
        list = new JList(defaultListModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        // list.addListSelectionListener(this);
        list.setVisibleRowCount(15);
        JScrollPane listScrollPane = new JScrollPane(list);
        centerPanel.add(listScrollPane, BorderLayout.SOUTH);
        framePanel.add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton openButton = new JButton(buttonLabel);
        southPanel.add(openButton);
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openTagSearchDialog();
            }
        });

        if (showCancel) {
            JButton cancelButton = new JButton(Messages.getString("OpenNotebookJDialog.cancel"));
            southPanel.add(cancelButton);
            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    OpenConceptByTagJDialog.this.dispose();
                }
            });
        }

        framePanel.add(southPanel, BorderLayout.SOUTH);

        getContentPane().add(framePanel, BorderLayout.CENTER);

        // show
        pack();
        Gfx.centerAndShowWindow(this);
    }
    
    /**
     * Open selected notebook.
     */
    protected void openTagSearchDialog() {
        int selectedIndex = list.getSelectedIndex();
        logger.debug("Selected index: " + selectedIndex);
        
        TagEntry tagToOpen=null;
        
        if (selectedIndex >= 0 && selectedIndex < shownTags.size()) {
            tagToOpen = shownTags.get(selectedIndex);
        } else {
            if (shownTags.size() > 0) {
                tagToOpen=shownTags.get(0);
            }
        }
        
        if (tagToOpen!=null) {
            logger.debug("Openning tag: "+tagToOpen.getTagUri());
            
            TaggedResourceEntry[] taggedResources = MindRaider.tagCustodian.getTaggedResourcesByLabel(tagToOpen.getTagLabel());
            if(taggedResources!=null && taggedResources.length>0) {
                ArrayList<SearchResultEntry> result=new ArrayList<SearchResultEntry>();
                for(TaggedResourceEntry entry: taggedResources) {
                    result.add(
                            new SearchResultEntry(
                                    entry.notebookLabel,
                                    entry.conceptLabel,
                                    entry.conceptPath));
                }
                
                OpenConceptByTagJDialog.this.dispose();
                
                new SearchResultsJDialog(
                        Messages.getString("TagSnailHyperlinkListener.taggedResources"),
                        result);
            }
        }
    }
    
    /**
     * List model helper.
     *
     * @return list model.
     */
    protected DefaultListModel getListModel() {
        return defaultListModel;
    }
}
