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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * Facet is a class used to "filter" a RDF graph to be more "readable".
 */
public interface Facet {

	/**
	 * Label is used in UI and also as identifier - must be unique!
	 * 
	 * @return Returns the label.
	 */
	String getLabel();

	/**
	 * Set the RDF model to be used for statement evaluation.
	 * 
	 * @param model
	 *            the model.
	 */
	void setModel(Model model);

	/**
	 * Method used to determine whether to show particular statement in this
	 * facet.
	 * 
	 * @param statement
	 *            the statement to check.
	 * @return Returns <code>true</code> if statement is shown, otherwise
	 *         <code>false</code>.
	 */
	boolean showThisStatement(Statement statement);
}