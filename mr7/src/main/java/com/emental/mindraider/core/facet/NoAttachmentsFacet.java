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

/**
 * No attachments facet.
 */
public class NoAttachmentsFacet implements Facet {

    public static final String LABEL = "No attachments";

    private Model model;

    private BriefFacet briefFacet;

    /**
     * Constructor.
     */
    public NoAttachmentsFacet() {
        briefFacet = new BriefFacet();
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
        if (briefFacet.showThisStatement(statement)) {
            Property predicate = statement.getPredicate();
            if (!predicate.equals(MindRaiderVocabulary.attachment)) {
                return true;
            }
        }

        return false;
    }

}
