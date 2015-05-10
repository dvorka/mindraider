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
package com.emental.mindraider.core.rdf;

import java.io.File;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.mindcognition.mindraider.MindRaiderException;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.utils.Utils;

/**
 * This class is custodian of RDF models and OWL ontologies.
 * 
 * It allows registering of models to be synchronized and fetched for 
 * off-line processing/reading etc. It also includes helpers and auxiliary
 * methods.
 * 
 * @author Martin.Dvorak
 * @see {@link RdfModel} and {@link OwlOntology}
 * @version $Revision: 1.4 $ ($Author: mindraider $)
 */
public class RdfCustodian {
    
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(RdfCustodian.class);

    /*
     * well known ontologies (file names)
     */

    /**
     * tags ontology
     */
    public static final String FILENAME_TAGS_ONTOLOGY = "tags.owl.xml";

    /**
     * MR resources ontology
     */
    public static final String FILENAME_MR_ONTOLOGY = "mindraider.owl.xml";
    
    /*
     * custodian
     */

    /**
     * Location of RDF models.
     */
    private String modelsNest;

    /**
     * Location of OWL ontologies.
     */
    private String ontologiesNest;

	/**
	 * Constructor.
	 */
	public RdfCustodian() {
		// models directory
		this.modelsNest = Utils.normalizePath(MindRaider.profile
				.getHomeDirectory()
				+ File.separator + MindRaiderConstants.MR_DIR_MODELS);
		Utils.createDirectory(modelsNest);
		// off-line models directory
		this.ontologiesNest = Utils.normalizePath(MindRaider.profile
				.getHomeDirectory()
				+ File.separator + MindRaiderConstants.MR_DIR_ONTOLOGIES);
		Utils.createDirectory(ontologiesNest);

        // check whether important models and ontologies exist - if not, create them

        // tag ontology
        if(!existsOntology(FILENAME_TAGS_ONTOLOGY)) {
            logger.debug("Creating initial tag ontology...");
            OwlOntology tagOntology = createTagOntology();
            try {
                saveOntology(FILENAME_TAGS_ONTOLOGY, tagOntology);
            } catch (MindRaiderException e) {
                logger.error("Unable to save tag ontology!",e);
            }
        }
        
        // MR ontology (types of resources)
        if(!existsOntology(FILENAME_MR_ONTOLOGY)) {
            logger.debug("Creating MR ontology...");
            OwlOntology mrOntology=createMindRaiderOntology();
            try {
                saveOntology(FILENAME_MR_ONTOLOGY, mrOntology);
            } catch (MindRaiderException e) {
                logger.error("Unable to save MindRaider ontology!", e);
            }
        }
	}

    /**
     * Save RDF model with the given filename.
     *  
     * @param filename  name of the model file (not the path)
     */
    public void saveModel(String filename, RdfModel model) {
        
    }
    
    /**
     * Save ontology with the given filename.
     *  
     * @param filename  name of the ontology file (not the path)
     * @throws MindRaiderException 
     */
    public void saveOntology(String filename, OwlOntology ontology) throws MindRaiderException {
        ontology.setFilename(ontologiesNest+File.separator+filename);
        ontology.save();
    }
    
    /**
     * Load ontology from the file with given filename.
     * 
     * @param filename
     * @return
     */
    public OwlOntology loadOntology(String filename) {
        OwlOntology owlOntology = new OwlOntology();
        final String ontologyFile = ontologiesNest+File.separator+filename;
        logger.debug("Loading ontology: "+ontologyFile); // {{debug}}
        owlOntology.setFilename(ontologyFile);
        owlOntology.load();
        
        return owlOntology;
    }
    
    /**
     * Check whether model exists.
     * 
     * @param filename
     * @return
     */
    public boolean existsModel(String filename) {
        File target=new File(modelsNest+File.separator+filename);
        return target.exists();
    }
    
    /**
     * Check whether ontology exists.
     * 
     * @param filename
     * @return
     */
    public boolean existsOntology(String filename) {
        File target=new File(ontologiesNest+File.separator+filename);
        return target.exists();
    }
    
    
    /**
     * Get MR models directory.
     *
     * @return the MR models directory
     */
    public String getModelsDirectory() {
        return modelsNest;
    }
    
    /**
     * Get MR ontology directory.
     *
     * @return the MR ontology directory
     */
    public String getOntologiesDirectory() {
        return ontologiesNest;
    }
    
    /*
     * Off-line models caching
     */
    
	/**
	 * Synchronize all registered resources - remote notebooks, RDFs (FOAF), etc.
	 */
	public void synchronize() {
        // TODO to be implemented
	}

	/**
	 * Register model for off-line caching.
	 * 
	 * @param url
	 *            the url to register.
	 * @param name
	 *            the name to which register
	 * @todo to be implemented
	 */
	public void register(String url, String name) {
	    // TODO to be implemented
	}

	/**
	 * Unregister model.
	 * 
	 * @param url
	 *            the url to unregister.
	 * @todo to be implemented
	 */
	public void unregister(String url) {
        // TODO to be implemented
	}

    /*
     * ontology builders
     */

    /**
     * Create initial (empty) version of tags ontology.
     */
    private OwlOntology createTagOntology() {
        OwlOntology tagOntology = new OwlOntology();
        OntModel ontology = tagOntology.getOntology();

        // merge in former flags ontology and build tags hierarchy in parallel
        String flagUris[]={
                MindRaiderConstants.MR_OWL_FLAG_IMPORTANT,
                MindRaiderConstants.MR_OWL_FLAG_COOL,
                MindRaiderConstants.MR_OWL_FLAG_LATER,
                MindRaiderConstants.MR_OWL_FLAG_OBSOLETE,
                MindRaiderConstants.MR_OWL_FLAG_PROBLEM,
                MindRaiderConstants.MR_OWL_FLAG_PERSONAL,
                MindRaiderConstants.MR_OWL_FLAG_TODO
        };
        // set flags as subclass of tag, flags and set label the same as local name (it is safe)
        OntClass tag= ontology.createClass(MindRaiderConstants.MR_OWL_TAG);
        OntClass flag = ontology.createClass(MindRaiderConstants.MR_OWL_FLAG);
        for(String flagUri: flagUris) {
            //OntClass flagClass = ontology.createClass(MindRaiderConstants.MR_OWL_TAG_NS+flagUri);
            OntClass flagClass = ontology.createClass(flagUri);
            flagClass.addLabel(flagUri, "en");
            flag.addSubClass(flagClass);
            tag.addSubClass(flagClass);
        }
        
        // properties
        ObjectProperty flagProperty = ontology.createObjectProperty(MindRaiderConstants.MR_OWL_PROPERTY_FLAG);
        flagProperty.addRange(flag);
        
        return tagOntology;
    }

    /**
     * Build MR's OWL ontology.
     *
     * @param ontology
     *            the OntModel
     */
    private OwlOntology createMindRaiderOntology() {
        OwlOntology mrOntology = new OwlOntology();
        OntModel ontology = mrOntology.getOntology();

        // MR resource type classes
        OntClass mrResource = ontology.createClass(MindRaiderConstants.MR_OWL_CLASS_RESOURCE);
        OntClass profile = ontology.createClass(MindRaiderConstants.MR_OWL_CLASS_PROFILE);
        OntClass mindMap = ontology.createClass(MindRaiderConstants.MR_OWL_CLASS_MINDMAP);
        OntClass folder = ontology.createClass(MindRaiderConstants.MR_OWL_CLASS_FOLDER);
        OntClass notebook = ontology.createClass(MindRaiderConstants.MR_OWL_CLASS_NOTEBOOK);
        OntClass concept = ontology.createClass(MindRaiderConstants.MR_OWL_CLASS_CONCEPT);
        OntClass attachment = ontology.createClass(MindRaiderConstants.MR_OWL_CLASS_ATTACHMENT);
        OntClass localAttachment = ontology.createClass(MindRaiderConstants.MR_OWL_CLASS_LOCAL_ATTACHMENT);
        OntClass webAttachment = ontology.createClass(MindRaiderConstants.MR_OWL_CLASS_WEB_ATTACHMENT);
        OntClass mrAttachment = ontology.createClass(MindRaiderConstants.MR_OWL_CLASS_MR_ATTACHMENT);

        // taxonomy
        mrResource.addSubClass(mindMap);
        mrResource.addSubClass(profile);
        mindMap.addSubClass(folder);
        mindMap.addSubClass(notebook);
        mindMap.addSubClass(concept);
        mindMap.addSubClass(attachment);
        attachment.addSubClass(localAttachment);
        attachment.addSubClass(webAttachment);
        attachment.addSubClass(mrAttachment);

        // properties
        ObjectProperty hasAttachment = ontology.createObjectProperty(MindRaiderConstants.MR_OWL_PROPERTY_HAS_ATTACH);
        hasAttachment.addDomain(concept);
        hasAttachment.addRange(attachment);

        ontology.createObjectProperty(MindRaiderConstants.MR_OWL_PROPERTY_IS_DISCARDED);
        
        return mrOntology;
    }
}
