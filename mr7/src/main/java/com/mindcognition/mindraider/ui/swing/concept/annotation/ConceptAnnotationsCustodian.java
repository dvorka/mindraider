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
package com.mindcognition.mindraider.ui.swing.concept.annotation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.mindcognition.mindraider.ui.swing.concept.annotation.renderer.AbstractAnnotationRenderer;
import com.mindcognition.mindraider.ui.swing.concept.annotation.renderer.HelpAnnotationRenderer;

/**
 * Custodian of concept annotations:
 * <ul>
 *   <li>a place where are all the annotation renderers registered
 *   <li>a widget that allows editation/view of the annotations
 * </ul>
 * Custodian is a tabbed pane, renderer is a tab. Renderer might do what it needs within the tab
 * in order to enable editation and view of the annotation. Other UI components interact with the
 * custodian only - they do not work with renderers direclty. The custodian passes commands / notifies 
 * components itself.
 * <br><br>
 * By default custodian provides a help tab.
 * <br><br> 
 * Concept resource contains an annotation. Annotation content type is stored as 
 * an OWL class. Human friendly label, background and foreground colors are associated 
 * to the OWL class within the custodian - custodian knows what to do with the resource 
 * having the particular content type.
 * <br><br> 
 * Annotation renderers ask the custodian for the default foreground/background color
 * prefered by the user. Note that there is typically different file/g configuration for
 * the edit and view modes.
 */
public class ConceptAnnotationsCustodian extends JTabbedPane {
    private static final Logger logger = Logger.getLogger(ConceptAnnotationsCustodian.class);
        
    /**
     * Ordered array of renderers (important for UI usability).
     */
    private ArrayList<AbstractAnnotationRenderer> orderedRegistry;
    
    /**
     * OWL class to annotation type renderer registry
     */
    private HashMap<String,AbstractAnnotationRenderer> registry;

    /**
     * labels to OWL class map
     */
    private HashMap<String,String> comboLabel2OwlClassMap;

    /**
     * class to combo mapping
     */
    private HashMap<String,String> owlClass2ComboLabelMap;

    /**
     * foreground color map
     */
    private HashMap<String,Color> owlClass2BgColorMap;

    /**
     * background color map
     */
    private HashMap<String,Color> owlClass2FgColorMap;
    
    /**
     * Constructor.
     *
     */
    public ConceptAnnotationsCustodian() {
        super();
        
        setTabLayoutPolicy(SwingConstants.TOP);
        
        registry = new HashMap<String,AbstractAnnotationRenderer>();
        orderedRegistry = new ArrayList<AbstractAnnotationRenderer>();
        
        comboLabel2OwlClassMap = new HashMap<String,String>();
        owlClass2ComboLabelMap = new HashMap<String,String>();
        owlClass2FgColorMap = new HashMap<String,Color>();
        owlClass2BgColorMap = new HashMap<String,Color>();
        
        helpAnnotationRenderer = new HelpAnnotationRenderer();
        addTab(helpAnnotationRenderer.getAnnotationTypeLabel(), helpAnnotationRenderer);
    }
    
    /*
     * (non-Javadoc)
     * @see javax.swing.JTabbedPane#removeAll()
     */
    @Override
    public void removeAll() {
        super.removeAll();
    }

    /**
     * Register new type of concept annotation.
     * 
     * @param annotationRenderer
     */
    public void registerRenderer(AbstractAnnotationRenderer annotationRenderer) {
        if (annotationRenderer != null) {
            logger.debug("Registering concept annotation: "+ annotationRenderer.getAnnotationTypeLabel() + " ("+annotationRenderer.getAnnotationTypeOwlClass()+")"); // {{debug}}
            registry.put(annotationRenderer.getAnnotationTypeOwlClass(), annotationRenderer);
            orderedRegistry.add(annotationRenderer);

            comboLabel2OwlClassMap.put(annotationRenderer.getAnnotationTypeLabel(),
                    annotationRenderer.getAnnotationTypeOwlClass());
            owlClass2ComboLabelMap.put(annotationRenderer.getAnnotationTypeOwlClass(),
                    annotationRenderer.getAnnotationTypeLabel());
            owlClass2FgColorMap.put(annotationRenderer.getAnnotationTypeOwlClass(),
                    annotationRenderer.getForegroundColor());
            owlClass2BgColorMap.put(annotationRenderer.getAnnotationTypeOwlClass(),
                    annotationRenderer.getBackgroundColor());
        }
    }
    
    /**
     * Set annotation renderers.
     * 
     * @param conceptAnnotationRenderers
     */
    public void setAnnotationRenderers(AbstractAnnotationRenderer[] conceptAnnotationRenderers) {
        if(conceptAnnotationRenderers!=null) {
            logger.debug("Annotations to be registered: "+conceptAnnotationRenderers.length); // {{debug}}
            for(AbstractAnnotationRenderer renderer:conceptAnnotationRenderers) {
                registerRenderer(renderer);
            }
        }
    }

    /**
     * Get all registered annotation types.
     * 
     * @return all annotation types.
     */
    public AbstractAnnotationRenderer[] getAllRenderers() {
        return (AbstractAnnotationRenderer[]) orderedRegistry.toArray(new AbstractAnnotationRenderer[orderedRegistry.size()]);
    }
    
    private void showRenderersRegistry() {
        AbstractAnnotationRenderer[] allRenderers = getAllRenderers();
        logger.debug("Renderers: "+allRenderers.length);
        for(AbstractAnnotationRenderer r:allRenderers) {
            logger.debug("  "+r.getAnnotationTypeOwlClass()+" # "+r);
        }
    }

    /**
     * Get OWL class for label.
     * 
     * @param label
     *            label.
     * @return OWL class.
     */
    public String getOwlClassForLabel(String label) {
        if (label != null) {
            return (String) comboLabel2OwlClassMap.get(label);
        }
        return null;
    }

    /**
     * Get label for owl class.
     * 
     * @param owlClass
     *            OWL class.
     * @return label.
     */
    public String getLabelForOwlClass(String owlClass) {
        if (owlClass != null) {
            return (String) owlClass2ComboLabelMap.get(owlClass);
        }
        return null;
    }

    /**
     * Get all labels.
     * 
     * @return all labels.
     */
    public String[] getAnnotationTypeLabels() {
        String[] result = new String[orderedRegistry.size()];
        AbstractAnnotationRenderer[] conceptAnnotationTypes = getAllRenderers();
        for (int i = 0; i < conceptAnnotationTypes.length; i++) {
            result[i] = orderedRegistry.get(i).getAnnotationTypeLabel();
        }
        return result;
    }
    
    /**
     * Get label index.
     * 
     * @param label     label to be found.
     * @return label index.
     */
    public int getLabelIndex(String label) {
        if(label!=null) {
            String[] labels = getAnnotationTypeLabels();
            for (int i = 0; i < labels.length; i++) {
                if(label.equals(labels[i])) {
                    return i;
                }
            }
        }
        // TODO commented for now throw new RuntimeException("Unknown content type label: "+label);
        return 0;
    }

    /**
     * Get foreground color for OWL class.
     * 
     * @return foreground color.
     */
    public Color getForegroundColorForOwlClass(String owlClass) {
        if (owlClass != null) {
            return (Color) owlClass2FgColorMap.get(owlClass);
        }
        return null;
    }

    /**
     * Get background color for OWL class.
     * 
     * @return background color.
     */
    public Color getBackgroundColorForOwlClass(String owlClass) {
        if (owlClass != null) {
            return (Color) owlClass2BgColorMap.get(owlClass);
        }
        return null;
    }

    public void openConceptAnnotation(ConceptResource conceptResource) {
        openConceptAnnotation(conceptResource, conceptResource.getAnnotationContentType());
    }    

    private AbstractAnnotationRenderer activeRenderer=null;

    private HelpAnnotationRenderer helpAnnotationRenderer;
    
    /**
     * Render concept annotation pane.
     * 
     * @param selectedAnnotationType
     *            OWL class of the content type
     * @param annotationEditorsTabbedPane
     *            pane to be rendered.
     */
    public void openConceptAnnotation(ConceptResource conceptResource, String selectedAnnotationType) {
        logger.debug("Openning concept: "+conceptResource+" with annotation type: "+selectedAnnotationType);
        showRenderersRegistry();
        if (selectedAnnotationType != null) {
            activeRenderer = registry.get(selectedAnnotationType);
            logger.debug("Active renderer "+activeRenderer);
            if (activeRenderer != null) {
                removeAll();
                addTab(activeRenderer.getAnnotationTypeLabel(), activeRenderer);
                activeRenderer.openConceptAnnotation(conceptResource);
            } else {
                logger.debug("Concept annotation type is null: "+selectedAnnotationType); // {{debug}}
            }
        }
    }

    public void flushToResource() {
        logger.debug("flush() "+activeRenderer); // {{debug}}
        if(activeRenderer!=null) {
            activeRenderer.flushToResource();
        } else {
            logger.warn("Resource not flushed - active renderer is null!"); // {{debug}}
        }
    }
    
    public ConceptResource closeConceptAnnotation() {
        logger.debug("close()"); // {{debug}}
        if(activeRenderer!=null) {
            return activeRenderer.closeConceptAnnotation();
        } else {
            logger.warn("Resource not closed - active renderer is null!"); // {{debug}}
            return null;
        }
    }
    
    /**
     * Refresh preview.
     * 
     * @param selectedAnnotationType
     * @param label
     * @return
     */
    public void refreshPreview(String selectedAnnotationType, String label) {
        if (selectedAnnotationType != null) {
            AbstractAnnotationRenderer conceptAnnotationType 
                = registry.get(selectedAnnotationType);
            
            if (conceptAnnotationType != null) {
                conceptAnnotationType.flushToResource();
            }
        }
    }
    
    private static final long serialVersionUID = -6329982303421983498L;
}
