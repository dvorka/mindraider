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

import javax.naming.OperationNotSupportedException;
import javax.xml.namespace.QName;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/**
 * Tree of concept resources property.
 */
public class ConceptTreeProperty implements ResourceProperty {

    /*
     * <conceptTreeProperty>
     *   <concept>
     *     <resource>...</resource>     {1}   <!-- concept data -->
     *     <concept>...</concept>       *     <!-- children (recursion) -->
     *   </concept>
     * </conceptTreeProperty>
     */

    /**
     * The label property.
     */
    public static String label;

    /**
     * The QName property.
     */
    public static QName qname;

    /**
     * The element concept tree constant.
     */
    public static final String ELEMENT_CONCEPT_TREE = "conceptTreeProperty";

    static {
        qname = new QName("", ELEMENT_CONCEPT_TREE);
        label = "Concept tree property";
    }

    /**
     * The root node of concept tree.
     */
    private ConceptTreeNode root;

    /**
     * Constructor.
     */
    public ConceptTreeProperty() {
    }

    /**
     * Constructor.
     */
    public ConceptTreeProperty(ConceptTreeNode root) {
        this.root = root;
    }

    /**
     * @see com.emental.mindraider.core.rest.properties.ResourceProperty#getQName()
     */
    public QName getQName() {
        return qname;
    }

    /**
     * @see com.emental.mindraider.core.rest.properties.ResourceProperty#getLabel()
     */
    public String getLabel() {
        return label;
    }

    /**
     * @see com.emental.mindraider.core.rest.properties.ResourceProperty#fromXml(org.xmlpull.v1.XmlPullParser)
     */
    public void fromXml(XmlPullParser xpp) throws Exception {
        throw new OperationNotSupportedException("Deserialization not implemented!");

    }

    /**
     * @see com.emental.mindraider.core.rest.properties.ResourceProperty#toXml(org.xmlpull.v1.XmlSerializer)
     */
    public void toXml(XmlSerializer xs) throws Exception {
        // traverse the tree and write it to the stream
        toXml(root, xs);
    }

    /**
     * Serialize concept recursively.
     *
     * @param conceptNode
     *            the concept node.
     * @param xs
     *            the XML serializer object.
     * @throws Exception
     *             a generic Exception.
     */
    private void toXml(ConceptTreeNode conceptNode, XmlSerializer xs) throws Exception {
        if (conceptNode != null) {
            xs.startTag("", ELEMENT_CONCEPT_TREE);

            // serialize resource
            conceptNode.getConcept().toXml(xs);
            // serialize children
            for (int i = 0; i < conceptNode.getChildren().size(); i++) {
                toXml(((ConceptTreeNode) conceptNode.getChildren().get(i)), xs);
            }

            xs.endTag("", ELEMENT_CONCEPT_TREE);
        }
    }

    /**
     * Getter for <code>root</code>.
     *
     * @return Returns the root.
     */
    public ConceptTreeNode getRoot() {
        return this.root;
    }

    /**
     * Setter for <code>root</code>.
     *
     * @param root
     *            The root to set.
     */
    public void setRoot(ConceptTreeNode root) {
        this.root = root;
    }
}
