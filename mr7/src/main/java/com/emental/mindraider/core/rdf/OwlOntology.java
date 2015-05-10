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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.ProfileRegistry;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.mindcognition.mindraider.MindRaiderException;

/**
 * OWL ontology and helpers.
 *
 * @author Martin.Dvorak
 * @version $Revision: 1.3 $ ($Author: mindraider $)
 */
public class OwlOntology {

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(OwlOntology.class);

    /**
     * The ontology property.
     */
    private OntModel ontology;

    /**
     * Filesystem path to the ontology file.
     */
    private String ontologyFile;

    /**
     * Constructor. 
     */
    public OwlOntology() {
        ontology = ModelFactory.createOntologyModel(ProfileRegistry.OWL_LANG);
    }
    
    /**
     * Create new ontology and specify the filename of the ontology.
     *
     * @param ontologyFile
     *            ontology filename
     */
    public OwlOntology(String ontologyFile) {
        this();
        this.ontologyFile = ontologyFile;
    }

    /**
     * Set path to the ontology file.
     * 
     * @param ontologyFile  ontology filename
     */
    public void setFilename(String ontologyFile) {
        this.ontologyFile=ontologyFile;
    }
    
    /**
     * Get ontology.
     *
     * @return <code>ontology</code> property.
     */
    public OntModel getOntology() {
        return ontology;
    }

    /**
     * Load ontology from the file.
     */
    public void load() {
        File file=new File(ontologyFile);
        
        try {
            InputStreamReader in = null;
            try {
                if (file != null && file.exists()) {
                    in = new InputStreamReader(new BufferedInputStream(
                            new FileInputStream(file)), "UTF-8");
                } else {
                    System.err.println("Ontology file: " + ontologyFile+ " not found");
                    return;
                }

                // read the RDF/XML file
                // predefined languages: "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE"
                ontology.read(in, null, "RDF/XML");
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (Exception e) {
            logger.error("Unable to read ontology from file: " + ontologyFile,e);
        }
    }

    /**
     * Save ontology.
     *
     * @throws MindRaiderException
     *             thrown on error.
     */
    public void save() throws MindRaiderException {
        FileWriter fileWriter = null;
        try {
            if (ontologyFile != null) {
                fileWriter = new FileWriter(new File(ontologyFile));
                // TODO set encoding
                ontology.write(fileWriter, "RDF/XML");
            } else {
                throw new MindRaiderException("Ontology file cann't be null!");
            }
        } catch (IOException e) {
            logger.error("Unable to save ontology to: " + ontologyFile, e);
            throw new MindRaiderException("Unable to save ontology to " + ontologyFile, e);
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException e) {
                logger.error("Unable to save ontology to: " + ontologyFile, e);
                throw new MindRaiderException("Unable to save ontology to " + ontologyFile, e);
            }
        }
    }
}
