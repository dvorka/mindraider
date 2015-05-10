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
package com.emental.mindraider.core.facet;

import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.mindcognition.mindraider.application.model.note.NoteCustodian;

/**
 * Normal facet.
 */
public class NormalFacet implements Facet {

    public static final String LABEL = "Normal";

    private Model model;

    /**
     * Constructor.
     */
    public NormalFacet() {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.emental.mindraider.facet.Facet#getLabel()
     */
    public String getLabel() {
        return LABEL;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.emental.mindraider.facet.Facet#setModel(com.hp.hpl.jena.rdf.model.Model)
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Get model.
     *
     * @return model.
     */
    public Model getModel() {
        return model;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.emental.mindraider.facet.Facet#showThisStatement(com.hp.hpl.jena.rdf.model.Statement)
     */
    public boolean showThisStatement(Statement statement) {
        Property predicate = statement.getPredicate();
        if (predicate.equals(RDF.type) || predicate.equals(MindRaiderVocabulary.isDiscarded)
                || predicate.equals(MindRaiderVocabulary.flagProperty)) {
            return false;
        }
        // do not show trash content
        if (statement.getSubject().getURI().endsWith(NoteCustodian.NOTEBOOK_TRASH_LOCAL_NAME)) {
            return false;
        }

        return true;
    }

}
