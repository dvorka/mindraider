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
package com.mindcognition.mindraider.application.model.note.annotation;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.RdfModel;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

public class NoteInterlinking {

    public static final String MINDRAIDER_LOCAL_RESOURCE_HOSTNAME = "local.mindraider.net";

    public static final String CONCEPT_URI_PARAMETER = "concept";

    public static final String NOTEBOOK_URI_PARAMETER = "notebook";

    public final static String LINK_PREFIX = "[[";

    public final static String LINK_SUFIX = "]]";

    // Examples:
    
    // Link to concept
    // [[folder name.notebook name#concept name]]
    // [[notebook name#concept name]]
    // [[concept name]]
    
    // Link to notebook (note . prefix)
    // [[folder name.notebook name]]
    // [[.notebook name]]

    // Created links (# is replaced with ~ because of URL spec):
    // Link to concept
    // http://local-resource.mindraider.net?concept=htpp://blah
    // http://local-resource.mindraider.net?concept=htpp://blah&notebook=http://blah
    // Link to notebook
    // http://local-resource.mindraider.net?notebook=htpp://blah

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(NoteInterlinking.class);

    /**
     * Process MindRaider links in the annotation.
     *
     * @param annotation
     *            input annotation.
     * @return processed links.
     */
    public static String translate(String annotation) {
        if (annotation == null) {
            return null;
        }

        Pattern p = Pattern.compile("((?<= |^)(<nop>){0}([A-Z]+[a-z]+\\.)*[A-Z]+)([a-z]+)([A-Z][^ $\\.\\?!\\<]*)");

        // get names of all concepts in the current notebook
        ResourceDescriptor[] noteDescriptors = MindRaider.outlineCustodian.getAllNoteDescriptors();
        if (noteDescriptors == null) {
            // nothing to highlight
            return annotation;
        }

        // get names of all notebooks
        ResourceDescriptor[] outlineDescriptors = MindRaider.labelCustodian.getNotebookDescriptors();

        // process annotation
        StringBuffer result = new StringBuffer();
        try {
            // read line by line
            BufferedReader bufferedReader = new BufferedReader(new StringReader(annotation));

            // TODO initial implementation is simple: just first link occurence
            // is replaced
            String line, newLine;
            while ((line = bufferedReader.readLine()) != null) {
                newLine = "";
                int start = line.indexOf(LINK_PREFIX);
                int end = line.indexOf(LINK_SUFIX, (start >= 0 ? start + 2 : start));

                String concept = null, notebook = null;
                if (start < end) {
                    newLine += line.substring(0, start);
                    String innards = line.substring(start + 2, end);
                    if (innards == null || "".equals(innards)) {
                        continue;
                    }

                    // try to parse innards...
                    int hashIdx;
                    if (innards.startsWith(".")) {
                        // it is either 'notebook' or 'concept in notebook' link
                        if ((hashIdx = innards.indexOf("#")) > 0) {
                            // concept in notebook link
                            notebook = innards.substring(1, hashIdx);
                            concept = innards.substring(hashIdx + 1);
                        } else {
                            // notebook link
                            notebook = innards.substring(1);
                        }
                    } else {
                        // detect notebook#concept links
                        if(!innards.startsWith("#") && !innards.endsWith("#") && (hashIdx = innards.indexOf("#")) > 0) {
                            notebook = innards.substring(0,hashIdx);
                            concept = innards.substring(hashIdx + 1);
                        }
                    }

                    logger.debug(" Parsed interlink - notebook: '" + notebook + "' concept: '" + concept + "'");

                    // if there is notebook specification, check whether notebook exists
                    boolean found = false;
                    if (notebook != null) {
                        String outlineUri = null, conceptUri = null;
                        for (ResourceDescriptor resource : outlineDescriptors) {
                            if (notebook.equals(resource.getLabel())) {
                                outlineUri = resource.getUri();
                                found = true;
                                break;
                            }
                        }

                        if (outlineUri != null) {
                            // immediately build link to concept
                            String href = "http://" + MINDRAIDER_LOCAL_RESOURCE_HOSTNAME + "/resource?"
                                    + NOTEBOOK_URI_PARAMETER + "=" + outlineUri.replace('#', '~');

                            if (concept != null) {
                                // link to concept - determine whether it
                                // exists:
                                // 1) load notebook RDF model
                                // 2) get URI for label from it

                                Model notebookModel = MindRaider.outlineCustodian.getModel(outlineUri);
                                // get URI of label
                                Statement statementByPredicateAndLiteral = RdfModel.getStatementByPredicateAndLiteral(
                                        notebookModel, RDFS.label, concept);
                                logger.debug(" Model - found concept label statement: "+statementByPredicateAndLiteral);
                                if(statementByPredicateAndLiteral!=null) {
                                    conceptUri=statementByPredicateAndLiteral.getSubject().getURI();
                                    logger.debug("  Concept URI is: "+conceptUri);
                                    href+="&"+CONCEPT_URI_PARAMETER+"="+conceptUri.replace('#', '~');
                                    found=true;
                                } else {
                                    found=false;
                                }
                            }

                            innards = "%GREEN%<b><a href='" + href + "'>" + innards + "</a></b>%ENDCOLOR%";
                            newLine += innards;
                            newLine += line.substring(end + 2);
                        }
                        if (found) {
                            // TODO mark link as invalid
                            line = newLine;
                        }
                    } else {
                        // TODO determine whether it is valid concept name
                        for (ResourceDescriptor resource : noteDescriptors) {
                            if (innards.equals(resource.getLabel())) {
                                found = true;
                                String href = "http://" + MINDRAIDER_LOCAL_RESOURCE_HOSTNAME + "/concepts?"
                                        + CONCEPT_URI_PARAMETER + "=" + resource.getUri().replace('#', '~');
                                innards = "%GREEN%<b><a href='" + href + "'>" + innards + "</a></b>%ENDCOLOR%";

                                newLine += innards;
                                newLine += line.substring(end + 2);
                                break;
                            }
                        }
                        if (found) {
                            line = newLine;
                        } else {
                            // detect TWiki word
                            Matcher m = p.matcher(innards);
                            boolean isTWikiWord = m.find();
                            if (innards == null || innards.startsWith("http://") || innards.startsWith("https://")
                                    || isTWikiWord) {
                                // ok link
                            } else {
                                // there is INVALID link - avoid translation of
                                // this
                                // link by TWiki
                                newLine += "%RED%<b>{" + innards + "}</b>%ENDCOLOR%";
                                newLine += line.substring(end + 2);
                                line = newLine;
                            }
                        }
                    }
                }

                // assemble line back
                result.append(line);
                result.append("\n");
            }
        } catch (Exception e) {
            return annotation;
        }

        // used for export
        result.append("\n");

        return result.toString();
    }
    
    /**
     * Detect MR link.
     * 
     * @param hyperlink     MindRaider hyperlink.
     * @return return status.
     */
    public static boolean isMindRaiderLink(String hyperlink) {
        logger.debug("isMindRaiderLink: "+hyperlink);
        if(hyperlink!=null && hyperlink.indexOf(MINDRAIDER_LOCAL_RESOURCE_HOSTNAME)>=0) {
            return true;
        }
        return false;
    }
}
