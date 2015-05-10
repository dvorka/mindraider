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
package com.emental.mindraider.core.rest.properties;

import java.util.ArrayList;

import com.emental.mindraider.core.rest.Resource;

/**
 * Tree node.
 */
public class ConceptTreeNode {

    /**
     * concept resource itself
     */
    public Resource concept;

    /**
     * children of this concept
     */
    public ArrayList<ConceptTreeNode> children;

    /**
     * Children getter.
     * 
     * @return Returns the children.
     */
    public ArrayList<ConceptTreeNode> getChildren() {
        return children;
    }

    /**
     * Children setter.
     * 
     * @param children The children to set.
     */
    public void setChildren(ArrayList<ConceptTreeNode> children) {
        this.children = children;
    }

    /**
     * Constructor.
     */
    public ConceptTreeNode() {
        children=new ArrayList<ConceptTreeNode>();
    }

    /**
     * Constructor.
     *
     * @param concept       concept.
     * @param children      children.
     */
    public ConceptTreeNode(Resource concept, ArrayList<ConceptTreeNode> children) {
        this.concept=concept;
        this.children=children;
    }

    /**
     * Concept getter.
     * 
     * @return Returns the concept.
     */
    public Resource getConcept() {
        return concept;
    }

    /**
     * Concept setter.
     * 
     * @param concept The concept to set.
     */
    public void setConcept(Resource concept) {
        this.concept = concept;
    }
}