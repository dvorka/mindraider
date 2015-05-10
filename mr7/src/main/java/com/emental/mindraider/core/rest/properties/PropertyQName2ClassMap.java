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

import java.util.Hashtable;

import javax.xml.namespace.QName;

public class PropertyQName2ClassMap {

    /**
     * Map from QNames to class names.
     */
    private static Hashtable<QName,String> map;

    static {
        map = new Hashtable<QName,String>();
        map.put(new QName("", AnnotationProperty.ELEMENT_ANNOTATION), AnnotationProperty.class.getName());
        map.put(new QName("", AttachmentProperty.ELEMENT_ATTACHMENT), AttachmentProperty.class.getName());
        map.put(new QName("", LabelProperty.ELEMENT_LABEL), LabelProperty.class.getName());
        map.put(new QName("", CategoryProperty.ELEMENT_CATEGORY), CategoryProperty.class.getName());
        map.put(new QName("", NotebookProperty.ELEMENT_NOTEBOOK), NotebookProperty.class.getName());
        map.put(new QName("", ConceptProperty.ELEMENT_CONCEPT), ConceptProperty.class.getName());
        map.put(new QName("", SourceTwikiFileProperty.ELEMENT_SOURCE_TWIKI_FILE), SourceTwikiFileProperty.class.getName());
        map.put(new QName("", OriginProperty.ELEMENT_SOURCE_TWIKI_FILE), OriginProperty.class.getName());
        map.put(new QName("", AnnotationContentTypeProperty.ELEMENT_ANNOTATION_CONTENT_TYPE_URI),AnnotationContentTypeProperty.class.getName());

    }

    /**
     * Get instance corresponding to the element QName.
     * 
     * @param elementQName the QName object
     * @return the ResourceProperty
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws ClassNotFoundException the class not found exception
     */
    public static ResourceProperty getInstance(QName elementQName) throws InstantiationException,
        IllegalAccessException, ClassNotFoundException {
        String className = (String) map.get(elementQName);
        if(className==null) {
            return null;
        } else {
            return (ResourceProperty)Class.forName(className).newInstance();            
        }
    }

    /**
     * Register class that is able to handle particular property.
     * 
     * @param propertyElementQName the property element QName
     * @param associatedClassName the associated class name
     */
    public static void register(QName propertyElementQName, String associatedClassName) {
        map.put(propertyElementQName, associatedClassName);
    }
}
