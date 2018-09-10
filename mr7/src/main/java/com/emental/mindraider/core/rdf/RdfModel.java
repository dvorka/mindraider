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

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.ui.graph.spiders.SpidersGraph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.mindcognition.mindraider.install.Installer;
import com.mindcognition.mindraider.tools.Checker;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;
import com.mindcognition.mindraider.utils.Utils;

/**
 * RDF Model. 
 */
public class RdfModel {

    /*
      TODO refactoring needed - this class should hold/be connected to all the RDF
        stuff inherit from vocabulary, contain helper methods, etc. Now its code is
        spread through.
     */
    
    // TODO 1.5 model type enum
    
    /**
     * The generated model type const.
     */
    public static final int GENERATED_MODEL_TYPE = 1;

    /**
     * The final model type const.
     */
    public static final int FILE_MODEL_TYPE = 2;

    /**
     * The url model type const.
     */
    public static final int URL_MODEL_TYPE = 3;

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(RdfModel.class);

    /**
     * The RDF model.
     */
    private Model model;

    /**
     * model name or filename.
     */
    private String filename;

    /**
     * The type property.
     */
    private int type;

    /**
     * Create model and auto detect its type.
     * 
     * @param uri
     *            the filaname
     * @throws Exception
     *             an exception
     */
    public RdfModel(String uri) throws Exception {
        this.filename = uri;

        if (uri != null) {
            if (uri.startsWith("http://")) {
                model = downloadModel(uri);
                type = URL_MODEL_TYPE;
                return;
            }

            // else try to load it from local file system
            model = loadModel(uri);
            type = FILE_MODEL_TYPE;
        }
    }

    /**
     * Constructor. Create model and specify its type.
     * 
     * @param name
     *            the model name
     * @param type
     *            the model type
     */
    public RdfModel(String name, int type) {
        this.filename = name;
        this.type = type;
        switch (type) {
        case GENERATED_MODEL_TYPE:
            model = ModelFactory.createDefaultModel();
            break;
        case FILE_MODEL_TYPE:
            model = loadModel(name);
            break;
        case URL_MODEL_TYPE:
            model = downloadModel(name);
            break;
        default:
            // throw Exception?
            break;
        }
    }

    /**
     * Constructor. Create RDF model from existing model and assign a name.
     * 
     * @param name
     *            the RDF model name
     * @param model
     *            the Model
     */
    public RdfModel(String name, Model model) {
        this.filename = name;
        this.model = model;
        type = GENERATED_MODEL_TYPE;
    }

    /**
     * Return the model.
     * 
     * @return the model
     */
    public Model getModel() {
        return model;
    }

    /**
     * Return the model name.
     * 
     * @return the model name
     */
    public String getModelName() {
        return filename;
    }

    /**
     * Load the model.
     * 
     * @return return true if model is loaded, otherwise false
     */
    public boolean load() {
        if ((model = loadModel(filename)) != null) {
            return true;
        }
        return false;
    }

    /**
     * Save the model.
     * 
     * @return return true if model is saved, otherwise false.
     */
    public boolean save() {
        return saveModel(model, filename);
    }

    /**
     * Save model with name.
     * 
     * @param filename
     *            the name to save
     * @return true if model is saved, otherwise false
     */
    public boolean saveAs(String filename) {
        this.filename = filename;
        return saveModel(model, filename);
    }

    /**
     * Write model to standard out.
     */
    public void show() {
        if (model != null) {
            model.write(System.out);
        }
    }

    public static Model loadModel(String filename) {
       return loadModel(filename, true); 
    }
    
    /**
     * Load model.
     * 
     * @param filename
     *            the filename
     * @return the model
     * @throws Exception
     */
    public static Model loadModel(String filename, boolean checkAndFixOnError) {
        try {
            // create an empty model
            Model model = ModelFactory.createDefaultModel();

            File file = new File(filename);
            InputStreamReader in = null;
            try {
                if (file != null && file.exists()) {
                    in = new InputStreamReader(new BufferedInputStream(
                            new FileInputStream(file)), "UTF-8");
                } else {
                    System.err.println("File: " + filename + " not found");
                    return null;
                }

                // read the RDF/XML file
                // predefined languages: "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE"
                model.read(in, null, "RDF/XML");
            } finally {
                if (in != null) {
                    in.close();
                }
            }

            return model;
        } catch (Exception e) {
            logger.debug("Unable to load model!", e);   
            // something is wrong (model file is probably broken) -> run check & fix
            if(checkAndFixOnError) {
                Installer.backupRepositoryAsZip();
                Checker.checkAndFixRepository();
            }
            return null;
        }
    }

    /**
     * Load model from an URL.
     * 
     * @param url
     *            the url from which download
     * @return the model
     */
    public static Model downloadModel(String url) {
        Model model = ModelFactory.createDefaultModel();
        try {
            URLConnection connection = new URL(url).openConnection();

            if (connection instanceof HttpURLConnection) {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestProperty("Accept",
                        "application/rdf+xml, */*;q=.1");
                httpConnection.setRequestProperty("Accept-Language", "en");
            }
            InputStream in = connection.getInputStream();
            model.read(in, url);
            in.close();
            return model;
        } catch (MalformedURLException e) {
            logger.debug("Unable to download model from " + url, e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.debug("Unable to download model from " + url, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Store model in one of the following encodings: "RDF/XML",
     * "RDF/XML-ABBREV", "N-TRIPLE" or "N3".
     * 
     * @param model
     *            the model
     * @param filename
     *            the filaname
     * @return <code>true</code> if successfuly saved.
     */
    public static boolean saveModel(Model model, String filename) {
        OutputStreamWriter out = null;
        try {
            if (filename != null) {
                if (SpidersGraph.MINDRAIDER_NEW_MODEL.equals(filename)) {
                    JFileChooser fc = new JFileChooser();
                    fc.setCurrentDirectory(new File(MindRaider.rdfCustodian
                            .getModelsDirectory()));
                    fc.setDialogTitle("Save Model As...");
                    int returnVal = fc.showSaveDialog(MindRaider.mainJFrame);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        filename = file.getPath();
                        MindRaider.masterToolBar.setModelLocation(filename);
                    }
                }
                StatusBar.show(" Saving model " + filename);
                File f = Utils.renewFile(filename);

                out = new OutputStreamWriter(new BufferedOutputStream(
                        new FileOutputStream(f)), "UTF-8");

                // predefined languages: "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE"
                model.write(out, "RDF/XML");
                StatusBar.show("Model " + filename + " saved.");
            }
        } catch (Exception e) {
            logger.debug("Unable to save model!", e);
            StatusBar.show("Failed to save model " + filename, Color.red);
            return false;
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e1) {
                    logger.debug("Unable to close stream!", e1);
                }
            }
        }
        return true;
    }

    /**
     * Create a statement.
     * 
     * @param subjectUri
     *            the subject uri
     * @param predicateUri
     *            the predicate uri
     * @param objectUri
     *            the object uri
     * @param literal
     *            the literal flag
     * @return the Statement object
     */
    public Statement createStatement(String subjectUri, String predicateUri,
            String objectUri, boolean literal) {
        return createStatement((Resource) newResource(subjectUri, false),
                predicateUri, objectUri, literal);
    }

    /**
     * Create a statement.
     * 
     * @param subject
     *            the resource
     * @param predicateUri
     *            the predicate url
     * @param objectUri
     *            the object url
     * @param literal
     *            the literal flag
     * @return a Statement object
     */
    public Statement createStatement(Resource subject, String predicateUri,
            String objectUri, boolean literal) {
        RDFNode objectResource;
        Property predicateResource = model.createProperty(predicateUri);

        if (literal) {
            objectResource = model.createLiteral(objectUri);
        } else {
            objectResource = model.createResource(objectUri);
        }
        subject.addProperty(predicateResource, objectResource);

        StmtIterator i = model.listStatements(subject, predicateResource,
                objectResource);
        if (i.hasNext()) {
            return i.nextStatement();
        }
        JOptionPane.showMessageDialog(MindRaider.mainJFrame,
                "Unable to fetch statement.", "Error",
                JOptionPane.ERROR_MESSAGE);
        return null;
    }

    /**
     * Return the resource object from given uri.
     * 
     * @param uri
     *            the uri string
     * @return the Resource
     */
    public Resource getResource(String uri) {
        return (model!=null?model.getResource(uri):null);
    }

    /**
     * Return the RDFNode from given uri.
     * 
     * @param uri
     *            the uri string
     * @param literal
     *            the literary flag
     * @return the RDFNode
     */
    public RDFNode newResource(String uri, boolean literal) {
        if (literal) {
            return model.createLiteral(uri);
        }
        return model.createResource(uri);
    }

    /**
     * Set the filename.
     * 
     * @param filename
     *            the filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Set the type.
     * 
     * @param type
     *            the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Delete statement.
     * 
     * @param subject
     *            the RDFNode object
     * @param property
     *            the Property object
     * @param object
     *            the RDFNode object
     */
    public void deleteStatement(RDFNode subject, Property property,RDFNode object) {
        StmtIterator i = model.listStatements((Resource) subject, property, object);
        if (i.hasNext()) {
            model.remove(i.nextStatement());
        }
    }

    /**
     * Get statement by predicate.
     * 
     * @param model
     *            the Model object
     * @param property
     *            the Property object
     * @return a Statement
     */
    public static Statement getStatementByPredicate(Model model,Property property) {
        StmtIterator i = model.listStatements((Resource) null, property,
                (RDFNode) null);
        if (i.hasNext()) {
            return i.nextStatement();
        }
        return null;
    }

    /**
     * Get statement by predicate and literal object
     * 
     * @param model
     *            the Model object
     * @param property
     *            the Property object
     * @param literal
     *            literal string
     * @return a Statement
     */
    public static Statement getStatementByPredicateAndLiteral(Model model,Property property, String literal) {
        StmtIterator i = model.listStatements((Resource) null, property,
                literal);
        if (i.hasNext()) {
            return i.nextStatement();
        }
        return null;
    }

    /**
     * Delete statement by predicate.
     * 
     * @param model
     *            the Model
     * @param property
     *            the Property
     */
    public static void deleteStatementByPredicate(Model model, Property property) {
        StmtIterator i = model.listStatements((Resource) null, property,
                (RDFNode) null);
        if (i.hasNext()) {
            model.remove(i.nextStatement());
        }
    }

    /**
     * Delete statement by object.
     * 
     * @param model
     *            the Model
     * @param object
     *            the RDFNode
     */
    public static void deleteStatementByObject(Model model, RDFNode object) {
        StmtIterator i = model.listStatements((Resource) null, null, object);
        if (i.hasNext()) {
            model.remove(i.nextStatement());
        }
    }

    /**
     * Move up.
     * 
     * @param parentResource
     *            the Resource object
     * @param resourceUri
     *            the resource uri String
     * @return <code>true</code> if moved.
     */
    public boolean upInSequence(Resource parentResource, String resourceUri) {
        Seq seq = model.getSeq(parentResource);
        Resource movedResource = model.getResource(resourceUri);
        int movedResourceIndex = seq.indexOf(movedResource);
        logger
                .debug("  UP: concept index in sequence is "
                        + movedResourceIndex);
        if (movedResourceIndex > 1) {
            // switch previous resource with the actual one
            com.hp.hpl.jena.rdf.model.Resource auxResource = seq
                    .getResource(movedResourceIndex - 1);
            seq.set(movedResourceIndex, auxResource);
            seq.set(movedResourceIndex - 1, movedResource);
            save();
            logger.debug("  UP: after up index: " + seq.indexOf(movedResource));
            return true;
        }
        return false;
    }

    /**
     * Move down.
     * 
     * @param parentResource
     *            the Resource
     * @param resourceUri
     *            the resource uri
     * @return <code>true</code> if moved.
     */
    public boolean downInSequence(Resource parentResource, String resourceUri) {
        Seq seq = model.getSeq(parentResource);
        Resource movedResource = model.getResource(resourceUri);
        int movedResourceIndex = seq.indexOf(movedResource);
        logger.debug("  DOWN: concept index in sequence is "
                + movedResourceIndex);
        if (movedResourceIndex < seq.size()) {
            // switch previous resource with the actual one
            com.hp.hpl.jena.rdf.model.Resource auxResource = seq
                    .getResource(movedResourceIndex + 1);
            seq.set(movedResourceIndex, auxResource);
            seq.set(movedResourceIndex + 1, movedResource);
            save();
            logger.debug("  DOWN: after down index: "
                    + seq.indexOf(movedResource));
            return true;
        }
        return false;
    }
    
    /**
     * Get outline's/concept's label from the RDF model.
     */
    public static String getLabel(Model model, Resource resource) {
        return getStringLiteral(model, resource, RDFS.label);
    }
    
    /**
     * Get outline's/concept's timestamp from the RDF model.
     */
    public static long getTimestamp(Model model, Resource resource) {
        return getLongLiteral(model, resource, DC.date);
    }
    
    public static String getHref(Model model, Resource resource) {
        return getStringLiteral(model, resource, MindRaiderVocabulary.xlinkHref);
    }
    
    public static void setHref(Resource resource, String href) {
        resource.addProperty(
                MindRaiderVocabulary.xlinkHref, 
                href);
    }
    
    /**
     * Get outline's/concept's comment from the RDF model.
     */
    public static String getComment(Model model, Resource resource) {
        return getStringLiteral(model, resource, RDFS.comment);
    }
    
    public static String getStringLiteral(Model model, Resource resource, Property property) {
        String result=null;
        StmtIterator listStatements = model.listStatements(new SimpleSelector(resource,property,(String)null));
        if(listStatements.hasNext()) {
            result=((Statement)listStatements.next()).getLiteral().getString();
        }
        logger.debug("String literal: "+result);
        return result;
    }
    
    public static long getLongLiteral(Model model, Resource resource, Property property) {
        long result=0;
        StmtIterator listStatements = model.listStatements(new SimpleSelector(resource,property,(Long)null));
        if(listStatements.hasNext()) {
            result=((Statement)listStatements.next()).getLiteral().getLong();
        }
        logger.debug("Long literal: "+result);
        return result;
    }
    
    
//    /**
//     * Get concept's attachment (URLs) from the RDF model.
//     */
//    public static String getAttachments(Model model, Resource resource) {
//    }
}
