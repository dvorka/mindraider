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
package com.mindcognition.mindraider.ui.swing.concept;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Timestamp;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rest.Metadata;
import com.emental.mindraider.core.rest.properties.CategoryProperty;
import com.emental.mindraider.core.rest.resource.AttachmentResource;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.emental.mindraider.ui.listeners.ClassificationFlagComboActionListener;
import com.emental.mindraider.ui.listeners.ContentTypeComboActionListener;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.emental.mindraider.ui.panels.ConceptAttachmentsJPanel;
import com.mindcognition.mindraider.application.model.tag.TagEntry;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.concept.annotation.ConceptAnnotationsCustodian;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;

public class ConceptJPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(ConceptJPanel.class);

    // TODO add done button to force storing and tree refresh
    // TODO add HTML button to fire rendered whole topic with included images, links, statements, etc.
    // TODO add RDF Logo based button to show RDF source - perhaps should be in navigator pane
    
    public static final Font TEXTAREA_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    public static final int TEXTFIELD_WIDTH = 30;

    private ConceptResource previousConceptResource;
    
    private JPanel notebookPanel;
    private JPanel metadataPanel;
    private ConceptAttachmentsJPanel attachmentsPanel;
    private JEditorPane previewPane;

    private JTextField titleTextField;
    private JTextField uriTextField;
    private JTextField tagsTextField;
    private JTextField createdTextField;
    private JTextField revisionTextField;

    private JComboBox categoryCombo;
    private ClassificationFlagComboActionListener categoriesComboListener;

    private JComboBox contentTypeCombo;
    private DefaultComboBoxModel contentTypeComboModel;
    private ContentTypeComboActionListener contentTypeComboListener;
    
    private ConceptAnnotationsCustodian annotationsCustodian;

    private boolean dirty;
    
    public ConceptJPanel() {
    }

    public void init() {
        setLayout(new BorderLayout());

        /*
         * concept metadata (title, timestamps, tags...)
         */
        
        notebookPanel = new JPanel();
        notebookPanel.setLayout(new BorderLayout());
        notebookPanel.setBorder(new TitledBorder(" Note "));

        metadataPanel = new JPanel();
        metadataPanel.setLayout(new BorderLayout());

        // NORTH
        JPanel northJPanel = new JPanel();
        northJPanel.setLayout(new GridLayout(3, 1));

        // *) title
        JPanel metaEntryJPanel = new JPanel();
        metaEntryJPanel.setLayout(new BorderLayout());
        final JCheckBox jcheckbox = new JCheckBox(Messages.getString("ConceptJPanel.label"));
        jcheckbox.setIcon(IconsRegistry.getImageIcon("expander.png"));
        jcheckbox.setSelectedIcon(IconsRegistry.getImageIcon("expanderMinus.png"));
        Dimension metaEntryDimension = new Dimension(90, 18);
        jcheckbox.setPreferredSize(metaEntryDimension);
        metaEntryJPanel.add(jcheckbox, BorderLayout.WEST);
        titleTextField = new JTextField();
        titleTextField.addKeyListener(new TitleTextFieldKeyListener(this));
        titleTextField.addFocusListener(new TitleTextFieldFocusListener(this));
        metaEntryJPanel.add(titleTextField, BorderLayout.CENTER);
        northJPanel.add(metaEntryJPanel);

        // *) tags
        JPanel tagsJPanel=new JPanel();
        tagsJPanel.setLayout(new BorderLayout());
        JLabel jLabel = new JLabel(Messages.getString("ConceptJPanel.tags"));
        jLabel.setIcon(IconsRegistry.getImageIcon("expanderTransparent.png"));
        jLabel.setPreferredSize(metaEntryDimension);
        tagsJPanel.add(jLabel, BorderLayout.WEST);
        tagsTextField = new JTextField();
        tagsTextField.setEnabled(true);
        tagsTextField.setToolTipText(Messages.getString("ConceptJPanel.tagsTooltip"));
        tagsJPanel.add(tagsTextField, BorderLayout.CENTER);
        northJPanel.add(tagsJPanel);
        
        // *) category / color
        JPanel categoryJPanel = new JPanel();
        categoryJPanel.setLayout(new BorderLayout());
        jLabel = new JLabel(Messages.getString("ConceptJPanel.flag"));
        jLabel.setIcon(IconsRegistry.getImageIcon("expanderTransparent.png"));
        jLabel.setPreferredSize(metaEntryDimension);
        categoryJPanel.add(jLabel, BorderLayout.WEST);
        categoryCombo = new JComboBox(ClassifierFlagRenderer.comboLabels);
        categoryCombo.setSelectedIndex(0);
        categoryCombo.setEnabled(false);
        categoriesComboListener = new ClassificationFlagComboActionListener(this);
        categoryCombo.addActionListener(categoriesComboListener);
        categoryJPanel.add(categoryCombo, BorderLayout.CENTER);
        northJPanel.add(categoryJPanel);
        
        
        metadataPanel.add(northJPanel, BorderLayout.NORTH);

        final JPanel southJPanel = new JPanel();
        southJPanel.setLayout(new GridLayout(3, 1));
        
        // *) content type
        metaEntryJPanel=new JPanel();
        metaEntryJPanel.setLayout(new BorderLayout());
        jLabel=new JLabel(Messages.getString("ConceptJPanel.contentType"));
        jLabel.setIcon(IconsRegistry.getImageIcon("expanderTransparent.png"));
        jLabel.setPreferredSize(metaEntryDimension);
        metaEntryJPanel.add(jLabel, BorderLayout.WEST);
        contentTypeComboModel = new DefaultComboBoxModel();
        contentTypeCombo = new JComboBox(contentTypeComboModel);
        contentTypeCombo.setEnabled(false);
        contentTypeComboListener=new ContentTypeComboActionListener(this);
        // pull down of annotation types - user might change the annotation type
        for(String label: annotationsCustodian.getAnnotationTypeLabels()) {
            contentTypeComboModel.addElement(label);
        }
        contentTypeCombo.setSelectedIndex(0);
        contentTypeCombo.addActionListener(contentTypeComboListener);
        metaEntryJPanel.add(contentTypeCombo,BorderLayout.CENTER);                
        southJPanel.add(metaEntryJPanel);
        
        // *) created
        metaEntryJPanel = new JPanel();
        metaEntryJPanel.setLayout(new BorderLayout());
        jLabel = new JLabel(Messages.getString("ConceptJPanel.created"));
        jLabel.setIcon(IconsRegistry.getImageIcon("expanderTransparent.png"));
        jLabel.setPreferredSize(metaEntryDimension);
        metaEntryJPanel.add(jLabel, BorderLayout.WEST);
        createdTextField = new JTextField();
        createdTextField.setEnabled(false);
        metaEntryJPanel.add(createdTextField, BorderLayout.CENTER);
        southJPanel.add(metaEntryJPanel);
        
        // *) revision
        metaEntryJPanel = new JPanel();
        metaEntryJPanel.setLayout(new BorderLayout());
        jLabel = new JLabel(Messages.getString("ConceptJPanel.revision"));
        jLabel.setIcon(IconsRegistry.getImageIcon("expanderTransparent.png"));
        jLabel.setPreferredSize(metaEntryDimension);
        metaEntryJPanel.add(jLabel, BorderLayout.WEST);
        revisionTextField = new JTextField();
        revisionTextField.setEnabled(false);
        metaEntryJPanel.add(revisionTextField, BorderLayout.CENTER);
        southJPanel.add(metaEntryJPanel);
        
        // *) uri
        metaEntryJPanel = new JPanel();
        metaEntryJPanel.setLayout(new BorderLayout());
        jLabel = new JLabel(Messages.getString("ConceptJPanel.uri"));
        jLabel.setIcon(IconsRegistry.getImageIcon("expanderTransparent.png"));
        jLabel.setPreferredSize(metaEntryDimension);
        metaEntryJPanel.add(jLabel, BorderLayout.WEST);
        uriTextField = new JTextField();
        uriTextField.setEnabled(false);
        metaEntryJPanel.add(uriTextField, BorderLayout.CENTER);
        
        southJPanel.setVisible(false);

        metadataPanel.add(southJPanel, BorderLayout.CENTER);
        notebookPanel.add(metadataPanel, BorderLayout.NORTH);
        add(notebookPanel, BorderLayout.NORTH);

        // hiding
        jcheckbox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                // determine state & hide/show south panel
                if (jcheckbox.isSelected()) {
                    southJPanel.setVisible(true);
                } else {
                    southJPanel.setVisible(false);
                }
                southJPanel.updateUI();
            }
        });

        /*
         * concept's annotation rendering
         */
        
        add(annotationsCustodian, BorderLayout.CENTER);
               
        /*
         * attachments
         */

        JPanel catAndAttachJPanel = new JPanel();
        catAndAttachJPanel.setLayout(new BorderLayout());
        catAndAttachJPanel.add(attachmentsPanel, BorderLayout.SOUTH);

        add(catAndAttachJPanel, BorderLayout.SOUTH);

        /*
         * shutdown hook
         */

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                // non-daemon: concept must be saved
                logger.debug(Messages.getString("ConceptJPanel.savingLastConcept"));
                StatusBar.setEnabled(false);
                //System.out.println("MR shutdown hook started");
                OutlineJPanel.getInstance().conceptJPanel.save();
                //System.out.println("MR shutdown hook finished");
            }
        });
    }

    public void setAnnotationsCustodian(ConceptAnnotationsCustodian annotationsCustodian) {
        this.annotationsCustodian=annotationsCustodian;
    }

    public ConceptAnnotationsCustodian getAnnotationsCustodian() {
        return annotationsCustodian;
    }
        
    public ConceptResource getConceptResource() {
        return previousConceptResource;
    }

    /**
     * Refresh panel content - set up new concept and save the previous one.
     *
     * @param noteResource
     *            the concept resource.
     */
    public void open(ConceptResource conceptResource) {
        open(conceptResource,true);
    }
    
    /**
     * Refresh panel content - set up new concept and save the previous one.
     *
     * @param noteResource
     *            new concept resource.
     * @param savePreviousResource 
     *            save resource before refresh
     */
    public void open(ConceptResource conceptResource, boolean savePreviousResource) {
        // set annotation content type before save
        checkAndsetDefaultAnnotationContentType(conceptResource);
        // save previous resource
        if(savePreviousResource) {
            save();
            annotationsCustodian.closeConceptAnnotation();
        }
        
        // remember last concept to save it on exit or change
        previousConceptResource = conceptResource;

        // refresh current resource
        refresh();
        
        //System.out.println("PROFILER save+refresh concept: "+(System.currentTimeMillis()-profileStart)+"(ms)");        
    }

    /**
     * Refresh concept panel using previous resource.
     */
    public void refresh() {
        logger.debug("refresh concept: resource => UI");
        if (previousConceptResource != null) {
            Metadata meta = previousConceptResource.resource.getMetadata();
            // set new resource to panel
            titleTextField.setText(previousConceptResource.getLabel());
            uriTextField.setText(meta.getUri().toASCIIString());
            createdTextField.setText(new Timestamp(meta.getCreated()).toString());
            revisionTextField.setText("" + meta.getRevision());
            
            // tags (list)
            tagsTextField.setText("");
            CategoryProperty[] tagsAndFlag = previousConceptResource.getCategories();
            if(tagsAndFlag!=null) {
                StringBuffer tagBuffer=new StringBuffer();
                for(CategoryProperty tagOrFlag: tagsAndFlag) {
                    // add only tags - not flag
                    if(tagOrFlag.getCategoryValue()!=null && tagOrFlag.getCategoryValue().length()>0) {
                        if(!tagOrFlag.getCategoryValue().startsWith(MindRaiderConstants.MR_OWL_FLAG_NS)) {
                            tagBuffer.append(tagOrFlag.getCategoryCaption());
                            tagBuffer.append(", ");
                        }
                    }
                }
                if(tagBuffer.length()>0) {
                    tagsTextField.setText(tagBuffer.toString().substring(0,tagBuffer.length()-2));
                }
            }

            // attachments
            attachmentsPanel.getAttachmentsTableModel().removeAllAttachments();
            AttachmentResource[] conceptAttachments = MindRaider.noteCustodian.getAttachments(previousConceptResource);
            attachmentsPanel.getAttachmentsTableModel().attachments = conceptAttachments;
            if (conceptAttachments != null && conceptAttachments.length>0) {
                for (int i = 0; i < conceptAttachments.length; i++) {
                    // TODO hashtable of icons (extension based)
                    if (conceptAttachments[i] != null
                            && (conceptAttachments[i].getUrl().startsWith("http://") || conceptAttachments[i].getUrl()
                                    .startsWith("https://"))) {
                        attachmentsPanel.getAttachmentsTableModel().addAttachment(IconsRegistry.getImageIcon("webAttachment.png"),
                                conceptAttachments[i].getDescription());
                    } else {
                        attachmentsPanel.getAttachmentsTableModel().addAttachment(IconsRegistry.getImageIcon("localAttachment.png"),
                                conceptAttachments[i].getDescription());
                    }
                }
                attachmentsPanel.setVisible(true);
            } else {
                attachmentsPanel.setVisible(false);
            }

            // category
            categoryCombo.setEnabled(true);
            refreshCategoryCombo(previousConceptResource);

            // content type
            contentTypeCombo.setEnabled(true);
            refreshContentTypeCombo(previousConceptResource);

            // shuffle annotation editation wysiwyg tabs
            annotationsRefreshEditorsTabbedPane();
            annotationsCustodian.openConceptAnnotation(previousConceptResource);

            StatusBar.show(Messages.getString("ConceptJPanel.selectedConcept", previousConceptResource.getLabel()));         
        }
    }

    /**
     * Save currrent concept resource.
     */
    public void save() {
        logger.debug("save concept: UI => resource...");
        if (previousConceptResource != null) {
            // save previsious resource
            logger.debug(Messages.getString("ConceptJPanel.messageSave", previousConceptResource.getLabel()));

            // *) label
            previousConceptResource.getLabelProperty().setLabelContent(titleTextField.getText());
            
            // *) annotation
            // flush the annotation to the concept
            annotationsCustodian.flushToResource();
                        
            // *) tags + categories
            //   - there are tags and categories - there might be at most one category (pulldown)
            //     and several tags
            //   - category and tags differ in the namespace (prefix) - see tag explorer class for more details
            
            
            // *) remove tags and flag
            // parse tags by label, if there is a new tag with unknown label, then register it as new
            // update both tag model and the resource itself
            //   before removing categories, remove all the previous tags from the index (they will be added immediately
            CategoryProperty[] oldTags = previousConceptResource.getCategories();
            if(oldTags!=null && oldTags.length>0) {
                for (CategoryProperty property : oldTags) {
                    logger.debug("Tag to remove: "+property.getCategoryValue());
                    MindRaider.tagCustodian.decOrRemoveByLabel(property.getCategoryCaption());
                }
            }            
            // now it is safe to remove tags (+ flag) from the concept resource
            // *) add flag
            // add flag back
            MindRaider.noteCustodian.addCategoryToCategoryProperties(categoryCombo.getSelectedItem().toString(), previousConceptResource);
            // *) add tags
            // add TAGS back
            TagEntry[] registerCategories = MindRaider.tagCustodian.registerCategories(
                    tagsTextField.getText(), 
                    previousConceptResource.getNotebookUri(), 
                    previousConceptResource.getUri(), 
                    previousConceptResource.getLabel(), 
                    previousConceptResource.resource.metadata.getTimestamp());
            if(registerCategories!=null && registerCategories.length>0) {
                for (int i = 0; i < registerCategories.length; i++) {
                    previousConceptResource.addCategory(
                            registerCategories[i].getTagLabel(), 
                            registerCategories[i].getTagUri());
                }
            }
            
            // *) save
            try {
                MindRaider.noteCustodian.save(previousConceptResource);
                // redraw always - cardinality changes
                MindRaider.tagCustodian.redraw();
                MindRaider.tagCustodian.toRdf();
            } catch (Exception e) {
                logger.error("save()", e);
                JOptionPane.showMessageDialog(MindRaider.mainJFrame, Messages.getString(
                        "ConceptJPanel.cannotSaveConcept", e.getMessage()), Messages
                        .getString("ConceptJPanel.saveConceptError"), JOptionPane.ERROR_MESSAGE);
            }
            
        }
    }

    /**
     * Set annotation content type on the resource due to combo.
     */
    public void annotationSetResourceContentType() {
        logger.debug("SET annotation content type..."); // {{debug}}
        logger.debug(" OLD annotation content type: "+previousConceptResource.getAnnotationContentType()); // {{debug}}
        if(contentTypeCombo.getSelectedItem().getClass().getName() instanceof String) {
            logger.debug("  IN IF"); // {{debug}}
            try {
                final String actionString = (String)contentTypeCombo.getSelectedItem();
                logger.debug("    Action string: "+actionString); // {{debug}}
                Object owlClass = annotationsCustodian.getOwlClassForLabel(actionString);
                logger.debug("    OWL - new content type: "+owlClass); // {{debug}}
                previousConceptResource.setAnnotationContentType(owlClass.toString());
            } catch (Exception e) {
                logger.debug("Unable to set concept annotation type!",e);
            }
        }
    }

    /**
     * Clear content pane.
     */
    public void clear() {
        save();
        
        annotationsCustodian.removeAll();
        previousConceptResource = null;
        titleTextField.setText("");
        uriTextField.setText("");
        createdTextField.setText("");
        revisionTextField.setText("");
        attachmentsPanel.getAttachmentsTableModel().removeAllAttachments();
        tagsTextField.setText("");
    }


    /**
     * Getter for <code>dirty</code>.
     *
     * @return Returns the dirty.
     */
    public boolean isDirty() {
        return this.dirty;
    }

    /**
     * Setter for <code>dirty</code>.
     *
     * @param dirty
     *            The dirty to set.
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Getter for <code>twikiPane</code>.
     *
     * @return Returns the twikiPane.
     */
    public JEditorPane getTwikiPane() {
        return this.previewPane;
    }

    /**
     * Setter for <code>twikiPane</code>.
     *
     * @param twikiPane
     *            The twikiPane to set.
     */
    public void setTwikiPane(JEditorPane twikiPane) {
        this.previewPane = twikiPane;
    }


    /**
     * Refresh content of the preview pane - there are different previews according
     * to the content type.
     */
    public void refreshPreviewPane() {
        if(previousConceptResource!=null) {
            annotationsCustodian.refreshPreview(
                    previousConceptResource.getAnnotationContentType(),
                    titleTextField.getText());
        } else {
            logger.debug("Error: previous concept resource is null!"); // {{debug}}
        }
    }

    /**
     * Refresh WYSIWYG tabbed pane according to the content type combo. Tabs are shown/hidden according to
     * the type of the content type that is currently selected.
     */
    public void annotationsRefreshEditorsTabbedPane() {
        // note: if the type is unknown or combo is disabled, then hide all the tabs
        if(contentTypeCombo.isEnabled()) {
            String contentType=previousConceptResource.getAnnotationContentType();
            if(contentType!=null) {
                annotationsCustodian.openConceptAnnotation(previousConceptResource);
            } else {
                logger.debug("Unknown annotation content type: "+contentType); // {{debug}}
            }
        } else {
            annotationsCustodian.removeAll();
        }
    }

    /**
     * Refresh categories combo.
     *
     * @param noteResource
     */
    private void refreshCategoryCombo(ConceptResource conceptResource) {
        categoryCombo.removeActionListener(categoriesComboListener);

        // category properties are formed by tags and one category, therefore
        // category must be found first and then set
        CategoryProperty[] properties=conceptResource.getCategories();
        if(properties!=null && properties.length>0) {
            // iterate concept's category properties and find the category between tags
            for (int i = 0; i < properties.length; i++) {
                if(properties[i].getCategoryValue()!=null && properties[i].getCategoryValue().startsWith(MindRaiderConstants.MR_OWL_FLAG_NS)) {
                    // ;-)
                    String caption=properties[i].categoryCaption;
                    if(caption!=null) {
                        // find it in the array
                        for (int j = 0; j < ClassifierFlagRenderer.comboLabels.length; j++) {
                            if(caption.equals(ClassifierFlagRenderer.comboLabels[j])) {
                                categoryCombo.setSelectedIndex(j);
                                categoryCombo.addActionListener(categoriesComboListener);
                                return;
                            }
                        }
                    }
                }
            }
        }

        // set none
        categoryCombo.setSelectedIndex(0);
        categoryCombo.addActionListener(categoriesComboListener);
    }

    /**
     * Refresh annotation content type combo.
     *
     * @param noteResource
     */
    public void refreshContentTypeCombo(ConceptResource conceptResource) {
        contentTypeCombo.removeActionListener(contentTypeComboListener);

        String contentType=checkAndsetDefaultAnnotationContentType(conceptResource);

        String label=annotationsCustodian.getLabelForOwlClass(contentType);
        if(label!=null) {
            final String[] labels = annotationsCustodian.getAnnotationTypeLabels();
            for (int i = 0; i < labels.length; i++) {
                if(label.equals(labels[i])) {
                    contentTypeCombo.setSelectedIndex(i);
                    contentTypeCombo.addActionListener(contentTypeComboListener);
                    return;
                }
            }
        }

        // try set text as default...
        logger.warn("Unknown annotation content type: "+label);
        // force known default
        try {
            conceptResource.setAnnotationContentType(MindRaiderConstants.MR_OWL_CONTENT_TYPE_TWIKI);
        } catch (Exception e) {
        }
        contentTypeCombo.setSelectedIndex(annotationsCustodian.getLabelIndex(MindRaiderConstants.MR_OWL_CONTENT_TYPE_TWIKI));
        contentTypeCombo.addActionListener(contentTypeComboListener);
    }

    /**
     * Default annotation content type.
     * 
     * @param noteResource
     * @return default annotation type.
     */
    private String checkAndsetDefaultAnnotationContentType(ConceptResource conceptResource) {
        String contentType=conceptResource.getAnnotationContentType();
        if(contentType==null || "".equals(contentType)) {
            logger.debug("Setting default content type to text!");
            try {
                conceptResource.setAnnotationContentType(MindRaiderConstants.MR_OWL_CONTENT_TYPE_RICH_TEXT);
                contentType=conceptResource.getAnnotationContentType();
            } catch (Exception e) {
                logger.debug("Unable to set annotation content type!",e);
            }
        }
        return contentType;
    }
    
    public void setAttachmentsJPanel(ConceptAttachmentsJPanel attachmentsJPanel) {
        this.attachmentsPanel=attachmentsJPanel;
    }

    private static final long serialVersionUID = 4823926933079007565L;
}

class TitleTextFieldKeyListener implements KeyListener {
    private ConceptJPanel conceptJPanel;
    
    public TitleTextFieldKeyListener(ConceptJPanel conceptJPanel) {
        this.conceptJPanel=conceptJPanel;
    }
    
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            conceptJPanel.save();
            // @todo inefficient: just its annotation can be refreshed
            OutlineJPanel.getInstance().refresh();
        }
        if (keyEvent.getKeyCode() == KeyEvent.VK_S && keyEvent.isControlDown()) {
            conceptJPanel.save();
            StatusBar.show(Messages.getString("ConceptJPanel.conceptSaved"));
            // @todo inefficient: just its annotation can be refreshed
            OutlineJPanel.getInstance().refresh();
        }
    }
    public void keyReleased(KeyEvent keyEvent) {
    }
    public void keyTyped(KeyEvent keyEvent) {
    }
}

class TitleTextFieldFocusListener implements FocusListener {
    private ConceptJPanel conceptJPanel;

    public TitleTextFieldFocusListener(ConceptJPanel conceptJPanel) {
        this.conceptJPanel=conceptJPanel;
    }
    
    public void focusLost(FocusEvent focusEvent) {
        // save annotation
        conceptJPanel.save();
        // @todo inefficient: just its annotation can be refreshed
        OutlineJPanel.getInstance().refresh();
    }
    public void focusGained(FocusEvent focusEvent) {
    }
}