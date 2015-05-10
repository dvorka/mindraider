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
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URI;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.Resource;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.emental.mindraider.core.search.SearchResultEntry;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.dialogs.ProgramIconJDialog;

public class SearchResultsJDialog extends ProgramIconJDialog implements
        ListSelectionListener {

    /*
     * @todo add "as you write" filter textarea
     * @todo show concepts only
     * @todo load resource and get label from it
     * @todo notebook cache: url 2 label
     */
    
    private static final Logger logger = Logger.getLogger(SearchResultsJDialog.class);

    private ArrayList<SearchResultEntry> resourceUris;
    private ArrayList<SearchResultEntry> visibleResourceUris;
    private DefaultListModel defaultListModel;
    private JList list;
    private int TEXTFIELD_WIDTH=30;

    public SearchResultsJDialog(String title, final ArrayList<SearchResultEntry> resultUris) {
        super(title==null?Messages.getString("SearchResultNotebooksJDialog.title"):title);

        getContentPane().setLayout(new BorderLayout());
        JPanel dialogPanel = new JPanel();
        dialogPanel.setBorder(new EmptyBorder(5, 10, 0, 10));
        dialogPanel.setLayout(new BorderLayout());

        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.add(new JLabel(Messages.getString("SearchResultsJDialog.filter")),BorderLayout.NORTH);
        final JTextField filterTextField = new JTextField(TEXTFIELD_WIDTH);
        filterTextField.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(visibleResourceUris.size()>0) {
                        if(list.getSelectedIndex()<0) {
                            list.setSelectedIndex(0);                                
                        }
                    }
                    if(loadConcept()) {
                        SearchResultsJDialog.this.dispose();                        
                    }
                }
            }

            public void keyReleased(KeyEvent keyEvent) {
                boolean showAll=false;
                if (filterTextField.getText().length() == 0) {
                    showAll=true;
                }
                
                getListModel().clear();
                visibleResourceUris.clear();

                for (SearchResultEntry resource : resourceUris) {
                    if (showAll ||
                        resource.getConceptLabel().toLowerCase().startsWith(filterTextField.getText().toLowerCase())) {
                        getListModel().addElement(getListResourceEntryLabel(resource));
                        visibleResourceUris.add(resource);
                    }
                }
            }

            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        filterPanel.add(filterTextField,BorderLayout.CENTER);
        filterPanel.add(new JLabel(Messages.getString("SearchResultsJDialog.resources")),BorderLayout.SOUTH);
        
        // filter out notebooks - uri must contain /concepts/ (hack)
        logger.debug("URLs to show: " + resultUris);
        this.resourceUris = resultUris;
        visibleResourceUris=new ArrayList<SearchResultEntry>(resourceUris);

        // strip prefix
        // int prefixLng=MindRaider.profile.getNotebooksDirectory().length();
        defaultListModel = new DefaultListModel();
        
        for (SearchResultEntry resultEntry: resultUris) {
            defaultListModel.addElement(getListResourceEntryLabel(resultEntry));
        }

        list = new JList(defaultListModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(15);
        list.setSelectionForeground(Color.white);
        list.setSelectionBackground(Color.black);
        JScrollPane listScrollPane = new JScrollPane(list);                
        
        // key listener - close on enter
        list.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    SearchResultsJDialog.this.dispose();
                }
            }
            public void keyReleased(KeyEvent keyEvent) {
            }
            public void keyTyped(KeyEvent keyEvent) {
            }
        });

        // create a panel that uses BoxLayout
        JPanel buttonPane = new JPanel();
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton closeButton = new JButton(Messages
                .getString("SearchResultNotebooksJDialog.closeButton"));
        buttonPane.add(closeButton);
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SearchResultsJDialog.this.dispose();
            }
        });

        dialogPanel.add(filterPanel, BorderLayout.NORTH);
        dialogPanel.add(listScrollPane, BorderLayout.CENTER);
        dialogPanel.add(buttonPane, BorderLayout.PAGE_END);

        getContentPane().add(dialogPanel, BorderLayout.CENTER);

        // if there is exactly one result, open that notebook
        if (resultUris.size() == 1) {
            list.setSelectedIndex(0);
            loadConcept();
            dispose();
            return;
        }

        // show
        pack();
        // place it asymetricaly to the lefo to make concept visible
        // now put the frame to the center of the total screen
        Dimension ddww = getSize(); // get size of frame after pack
        // get size of screen
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        // move window to the center of global screen
        Point point = MindRaider.mainJFrame.getLocation();
        setLocation(new Point(point.x + 20, (screen.height - ddww.height) / 2));

        setVisible(true);
    }

    private String getListResourceEntryLabel(SearchResultEntry resultEntry) {
        return " " + resultEntry.getConceptLabel()
                + " (" + resultEntry.getNotebookLabel() + ")  ";
    }

    public DefaultListModel getListModel() {
        return defaultListModel;
    }
    
    /**
     * The LauncherListener class.
     */
    class LaunchListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            loadConcept();
        }
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent event) {

        logger.debug(Messages
                .getString("SearchResultNotebooksJDialog.listValueChanged"));
        loadConcept();
    }

    /**
     * Load concept.
     */
    protected boolean loadConcept() {
        Object[] values = list.getSelectedValues();
        if (values != null && values.length == 1 && list.getSelectedValues()[0] instanceof String) {
            Resource resource;
            try {
                resource = new Resource(((SearchResultEntry) visibleResourceUris.get(list.getSelectedIndex())).getPath());
                ConceptResource conceptResource = new ConceptResource(resource);                                
                
                MindRaider.outlineCustodian.loadOutline(new URI(
                        conceptResource.getNotebookUri()));
                OutlineJPanel.getInstance().refresh();
                OutlineJPanel.getInstance().conceptJPanel
                        .open(MindRaider.noteCustodian.get(
                                MindRaider.profile.getActiveOutlineUri()
                                        .toString(), resource.getMetadata()
                                        .getUri().toString()));
                // TODO add concept annotation search in her
                
                // recent
                MindRaider.recentConcepts.addRecentConcept(
                        conceptResource.getLabel(),
                        conceptResource.getUri(),
                        MindRaider.outlineCustodian.getActiveNotebookLabel(),
                        MindRaider.outlineCustodian.getActiveOutlineResource().getUri());
                
            } catch (Exception e1) {
                logger.error(Messages.getString("SearchResultNotebooksJDialog.unableToShowSearchResults"),e1);
            }
            return true;
        } else {
            JOptionPane.showMessageDialog(MindRaider.mainJFrame, "Please select a Note to be opened!");
            return false;
        }
    }
    
    private static final long serialVersionUID = 1L;
}
