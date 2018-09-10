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
package com.mindcognition.mindraider.commons;

import java.io.File;

/**
 * Constants - RDF is about URIs.
 */
public interface MindRaiderConstants {

    /**
     * The MindRaider title constant.
     */
    public static final String MR_TITLE = "MindRaider";

    /**
     * The MindRaider String.
     */
    public static final String MR = "mindraider";

    /**
     * The codename.
     */
    public static final String CODENAME = "MR @ Tags";

    /**
     * The major version identifies MR generations; incremented rarely.
     */
    public static final int majorVersion = 16;

    /**
     * Minor version is incremented with every release.
     */
    public static final int minorVersion = 0;

    /**
     * Early access flag.
     */
    public static final boolean EARLY_ACCESS=false;
    
    /**
     * The RDF name space constant.
     */
    public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";

    /**
     * The RDF syntax constant.
     */
    public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /**
     * The MindRaider RDF name space.
     */
    public static final String MR_RDF_NS = "http://e-mental.com/" + MR + "/rdf#";

    /**
     * The MindRaider RDF schema.
     */
    public static final String MR_RDFS_NS = "http://e-mental.com/" + MR + "/rdf/schema#";

    /**
     * The MindRaider predicate name space.
     */
    public static final String MR_RDF_PREDICATE_NS = MR_RDF_NS;

    /**
     * The MindRaider RDF predicate.
     */
    public static final String MR_RDF_PREDICATE = "reference";

    /**
     * The MindRaider ontology name space.
     */
    public static final String MR_OWL_CLASS_NS = "http://e-mental.com/" + MR + "/owl/class#";

    /**
     * The MindRaider ontology property name space.
     */
    public static final String MR_OWL_PROPERTY_NS = "http://e-mental.com/" + MR + "/owl/property#";

    /**
     * The MindRaider ontology Resource class.
     */
    public static final String MR_OWL_CLASS_RESOURCE = MR_OWL_CLASS_NS + "Resource";

    /**
     * The MindRaider ontology Profile class.
     */
    public static final String MR_OWL_CLASS_PROFILE = MR_OWL_CLASS_NS + "Profile";

    /**
     * The MindRaider ontology MindMap class.
     */
    public static final String MR_OWL_CLASS_MINDMAP = MR_OWL_CLASS_NS + "MindMap";

    /**
     * The MindRaider ontology Folder class.
     */
    public static final String MR_OWL_CLASS_FOLDER = MR_OWL_CLASS_NS + "Folder";

    /**
     * The MindRaider ontology Notebook class.
     */
    public static final String MR_OWL_CLASS_NOTEBOOK = MR_OWL_CLASS_NS + "Notebook";

    /**
     * The MindRaider ontology Concept class.
     */
    public static final String MR_OWL_CLASS_CONCEPT = MR_OWL_CLASS_NS + "Concept";

    /**
     * The MindRaider ontology Attachment class.
     */
    public static final String MR_OWL_CLASS_ATTACHMENT = MR_OWL_CLASS_NS + "Attachment";

    /**
     * The MindRaider ontology LocalAttachment class.
     */
    public static final String MR_OWL_CLASS_LOCAL_ATTACHMENT = MR_OWL_CLASS_NS + "LocalAttachment";

    /**
     * The MindRaider ontology WebAttachment class.
     */
    public static final String MR_OWL_CLASS_WEB_ATTACHMENT = MR_OWL_CLASS_NS + "WebAttachment";

    /**
     * The MindRaider ontology MrAttachment class.
     */
    public static final String MR_OWL_CLASS_MR_ATTACHMENT = MR_OWL_CLASS_NS + "MrAttachment";

    /**
     * The MindRaider ontology hasAttachment property.
     */
    public static final String MR_OWL_PROPERTY_HAS_ATTACH = MR_OWL_PROPERTY_NS + "hasAttachment";

    /**
     * The MindRaider parent notebook of the concept
     */
    public static final String MR_OWL_PROPERTY_IN_NOTEBOOK = MR_OWL_PROPERTY_NS + "inNotebook";

    /**
     * The MindRaider ontology isDiscarded property.
     */
    public static final String MR_OWL_PROPERTY_IS_DISCARDED_LOCAL_NAME = "isDiscarded";

    /**
     * The MindRaider ontology isDiscarded local name property.
     */
    public static final String MR_OWL_PROPERTY_IS_DISCARDED = MR_OWL_PROPERTY_NS
            + MR_OWL_PROPERTY_IS_DISCARDED_LOCAL_NAME;

    /**
     * The MindRaider ontology name space tag.
     */
    public static final String MR_OWL_TAG_NS = "http://e-mental.com/" + MR + "/owl/tag#";

    /**
     * The MindRaider ontology name space flag.
     */
    public static final String MR_OWL_FLAG_NS = "http://e-mental.com/" + MR + "/owl/flag#";

    /**
     * The MindRaider ontology Flag flag.
     */
    public static final String MR_OWL_FLAG = MR_OWL_FLAG_NS + "Flag";

    /**
     * The MindRaider ontology Tag.
     */
    public static final String MR_OWL_TAG = MR_OWL_TAG_NS + "Tag";

    /**
     * The MindRaider ontology Important flag.
     */
    public static final String MR_OWL_FLAG_IMPORTANT = MR_OWL_FLAG_NS + "Important";

    /**
     * The MindRaider ontology Cool flag.
     */
    public static final String MR_OWL_FLAG_COOL = MR_OWL_FLAG_NS + "Cool";

    /**
     * The MindRaider ontology ToDo flag.
     */
    public static final String MR_OWL_FLAG_TODO = MR_OWL_FLAG_NS + "ToDo";

    /**
     * The MindRaider ontology Personal flag.
     */
    public static final String MR_OWL_FLAG_PERSONAL = MR_OWL_FLAG_NS + "Personal";

    /**
     * The MindRaider ontology Later flag.
     */
    public static final String MR_OWL_FLAG_LATER = MR_OWL_FLAG_NS + "Later";

    /**
     * The MindRaider ontology Obsolete flag.
     */
    public static final String MR_OWL_FLAG_OBSOLETE = MR_OWL_FLAG_NS + "Obsolete";

    /**
     * The MindRaider ontology Problem flag.
     */
    public static final String MR_OWL_FLAG_PROBLEM = MR_OWL_FLAG_NS + "Problem";

    /**
     * The MindRaider ontology flag local name flag.
     */
    public static final String MR_OWL_FLAG_PROPERTY_LOCAL_NAME = "flag";

    /**
     * The MindRaider ontology flag local name tag.
     */
    public static final String MR_OWL_TAG_PROPERTY_LOCAL_NAME = "tag";

    /**
     * The MindRaider ontology property flag.
     */
    public static final String MR_OWL_PROPERTY_FLAG = MR_OWL_FLAG_NS + MR_OWL_FLAG_PROPERTY_LOCAL_NAME;

    /**
     * The MindRaider ontology property tag.
     */
    public static final String MR_OWL_PROPERTY_TAG = MR_OWL_TAG_NS + MR_OWL_TAG_PROPERTY_LOCAL_NAME;

    /**
     * Content type ontology namespace.
     */
    public static final String MR_OWL_CONTENT_TYPE_NS = "http://e-mental.com/" + MR + "/owl/contentType#";

    /**
     * Content type ontology name.
     */
    public static final String MR_OWL_CONTENT_TYPE = MR_OWL_CONTENT_TYPE_NS + "ContentType";

    /**
     * Text content type local name.
     */
    public static final String MR_OWL_CONTENT_TYPE_PLAIN_TEXT_LOCAL_NAME = "PlainText";

    /**
     * Text content type local name.
     */
    public static final String MR_OWL_CONTENT_TYPE_RICH_TEXT_LOCAL_NAME = "RichText";

    /**
     * Plain Text content type.
     */
    public static final String MR_OWL_CONTENT_TYPE_PLAIN_TEXT = MR_OWL_CONTENT_TYPE_NS + MR_OWL_CONTENT_TYPE_PLAIN_TEXT_LOCAL_NAME;
    
    /**
     * Rich Text content type.
     */
    public static final String MR_OWL_CONTENT_TYPE_RICH_TEXT = MR_OWL_CONTENT_TYPE_NS + MR_OWL_CONTENT_TYPE_RICH_TEXT_LOCAL_NAME;

    /**
     * Jarnal content type.
     */
    public static final String MR_OWL_CONTENT_TYPE_JARNAL = MR_OWL_CONTENT_TYPE_NS + "Jarnal";

    /**
     * TWiki content type.
     */
    public static final String MR_OWL_CONTENT_TYPE_TWIKI = MR_OWL_CONTENT_TYPE_NS + "TWiki";

    /**
     * HTML content type.
     */
    public static final String MR_OWL_CONTENT_TYPE_HTML = MR_OWL_CONTENT_TYPE_NS + "Html";

    /**
     * The MindRaider directory Categories directory.
     */
    public static final String MR_DIR_CATEGORIES_DIR = "Categories";

    /**
     * The MindRaider directory Notebooks directory.
     */
    public static final String MR_DIR_NOTEBOOKS_DIR = "Notebooks";

    /**
     * The MindRaider directory.
     */
    public static final String MR_DIRECTORY = File.separator + MR_TITLE;

    /**
     * The MindRaider directory.
     */
    public static final String MR_DIR = MR_DIRECTORY + File.separator;

    /**
     * The MindRaider notebooks directory.
     */
    public static final String MR_DIR_NOTEBOOKS = MR_DIR + MR_DIR_NOTEBOOKS_DIR;

    /**
     * The MindRaider Folders directory.
     */
    public static final String MR_DIR_FOLDERS = MR_DIR + "Folders";

    /**
     * The MindRaider Taxonomies directory.
     */
    public static final String MR_DIR_TAXONOMIES = MR_DIR + "Taxonomies";

    /**
     * The MindRaider OWL ontologies directory.
     */
    public static final String MR_DIR_ONTOLOGIES= "Ontologies";

    /**
     * The MindRaider Models directory.
     */
    public static final String MR_DIR_MODELS = "Models";

    /**
     * The resource already exists.
     */
    public static final String EXISTS = "EXISTS";

    /**
     * The XLink namespace constant.
     */
    public static final String XLINK_NAMESPACE = "http://www.w3.org/1999/xlink#";

    /**
     * The XLink local name href constant.
     */
    public static final String XLINK_LOCAL_NAME_HREF = "href";
    
    /*
     * URN
     */
    
    public static final String MR_RDF_URN="urn:com:e-mental:"+MR;
    
    public static final String MR_ATOM="urn:mindcognition:mindraider";
    public static final String MR_ATOM_TAXONOMY=MR_ATOM+":taxonomy";
}