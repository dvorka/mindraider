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
package com.emental.mindraider.core.rest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.emental.mindraider.core.rest.properties.ResourceProperty;
import com.mindcognition.mindraider.MindRaiderException;
import com.mindcognition.mindraider.utils.PullParsing;

/**
 * REST resource:
 *
 * <pre>
 *    &lt;resource&gt;
 *      &lt;metadata&gt;
 *        ...
 *      &lt;/metadata&gt;
 *      &lt;data&gt;
 *        &lt;labelProperty&gt;
 *        &lt;/labelProperty&gt;
 *        &lt;annotationProperty&gt;
 *        &lt;/annotationProperty&gt;
 *        ...
 *        &lt;propertyGroup uri=&quot;[group type]&quot; label=&quot;[human readable name]&quot;&gt;
 *          ... e.g. categories, descriptions, etc.
 *        &lt;/propertyGroup&gt;
 *      &lt;/data&gt;
 *    &lt;/resource&gt;
 * </pre>
 *
 * Remarks:
 * <ul>
 * <li>REST resource may have multiple representations - e.g. XML, XHTML etc.
 * For the serialization/deserialization of the resource to/from these
 * representations are prepared corresponding methods.</li>
 * <li>XML representation: property groups cann't be nested</li>
 * <li>Extensibility: there are two axes of resource extensibility - properties
 * and typed resources. You always get representation of an resource using this
 * object. If you want to introduce new resource type (note that resource type
 * is expressed using proper categorization on the XML level) that need new
 * types of properties, you just have to create property parser and register it
 * into property property registry (it will be activated automatically). You may
 * build typed resource representation on top of this class - that are typed
 * resources.</li>
 * <li>URIs and URLs: resource representation contains no URLs i.e. pointers to
 * other resources in terms of their location - such relationships are captured
 * by RDF Model(s) associated with the resource. Resource (XML) representation
 * contains just URIs of associated resources.</li>
 * <li>Namespaces: I avoided usage of namespaces in order to enable parsing of
 * small devices (like mobiles).</li>
 * </ul>
 */
public class Resource {

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(Resource.class);

    /**
     * Resource metadata.
     */
    public Metadata metadata;

    /**
     * Resource data.
     */
    public Data data;

    /**
     * Constructor.
     */
    public Resource() {
        metadata = new Metadata();
        data = new Data();
    }

    /**
     * Constructor.
     *
     * @param author
     *            author.
     * @param created
     *            created.
     * @param revision
     *            revision.
     * @param timestamp
     *            timestamp.
     * @param uri
     *            resource URI.
     * @throws Exception
     *             thrown on error.
     */
    public Resource(String author, long created, long revision, long timestamp,
            String uri) throws Exception {
        this();
        metadata.setCreated(created);
        metadata.setRevision(revision);
        metadata.setTimestamp(timestamp);
        metadata.setUri(new URI(uri));
        metadata.setAuthor(new URI(author));
    }

    /**
     * Constructor.
     *
     * @param string
     *            resource string representation
     */
    public Resource(String filename) throws Exception {
        this();
        logger.debug("Parsing: " + filename);
        fromXml(filename);
    }

    /**
     * The element resource constant.
     */
    public static final String ELEMENT_RESOURCE = "resource";

    /**
     * Add a resource property to data section.
     *
     * @param property
     *            the resource property.
     * @throws Exception
     *             a generic exception
     */
    public void addProperty(ResourceProperty property) throws RuntimeException {
        data.addProperty(property);
    }

    /**
     * Parse XML representation of the resource.
     *
     * @param filename
     *            the filename
     * @throws Exception
     *             a generic exception
     */
    public void fromXml(String filename) throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        BufferedInputStream in = null;
        try {
            // if(xpp.getFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL)) {
            // logger.debug("XML DOCDECL processing is not supported!");
            // }

            in = new BufferedInputStream(new FileInputStream(filename));
            xpp.setInput(in, "UTF-8");

            int eventType = xpp.getEventType();
            // parse root element and then pass control to metadata and data
            if (eventType == XmlPullParser.START_DOCUMENT) {
                logger.debug("Start document");

                PullParsing.startElement(xpp, ELEMENT_RESOURCE);
                // parse metadata
                metadata.fromXml(xpp);
                // parse data
                data.fromXml(xpp);
                PullParsing.endElement(xpp, ELEMENT_RESOURCE);
            }

        } catch (Exception e) {
            logger.error("Unable to load resource from "+filename, e);
            throw new MindRaiderException("Unable to load resource from: "+filename, e);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * To XML.
     *
     * @param outputStream
     *            the output stream.
     * @throws Exception
     *             a generic exception.
     */
    public void toXml(OutputStream outputStream) throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlSerializer xs = factory.newSerializer();
        xs
                .setProperty(
                        "http://xmlpull.org/v1/doc/properties.html#serializer-indentation",
                        "  ");

        xs.setOutput(outputStream, "UTF-8");

        // first write XML declaration
        xs.startDocument("UTF-8", null);
        // add some empty lines before first start tag
        xs.ignorableWhitespace("\n");

        toXml(xs);

        xs.endDocument();
    }

    /**
     * Save resource to file.
     *
     * @param filename
     *            target file.
     */
    public void toXmlFile(String filename) {
        // TODO URI driven write through cache

        BufferedOutputStream bufferedWriter = null;
        FileOutputStream fileWriter = null;
        try {
            fileWriter = new FileOutputStream(new File(filename));
            bufferedWriter = new BufferedOutputStream(fileWriter);
            logger.debug("Going to write: " + filename);
            toXml(bufferedWriter);
            logger.debug("...successfuly written!");
        } catch (Exception e) {
            logger.debug("Unable to save resource: " + filename, e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.flush();
                    bufferedWriter.close();
                } catch (IOException e1) {
                    logger.debug("Unable to flush/close " + filename, e1);
                }
            }
        }
    }

    /**
     * Serialize resource using given serializer.
     *
     * @param xs
     *            the XML serializer.
     * @throws IOException
     *             a I/O exception
     * @throws Exception
     *             a generic exception
     */
    public void toXml(XmlSerializer xs) throws IOException, Exception {
        xs.startTag("", ELEMENT_RESOURCE);
        metadata.toXml(xs);
        data.toXml(xs);

        xs.endTag("", ELEMENT_RESOURCE);
        xs.ignorableWhitespace("\n");
    }

    /**
     * Main.
     */
    public static void main(String args[]) throws Exception {
        logger
                .debug("- Going to parse resource... ---------------------------------");
        // String
        // schemaLocation=Resource.class.getResource("resource.xml").getFile().substring(1);
        // Resource resource=new Resource(schemaLocation);
        logger
                .debug("- Resource parsed! -------------------------------------------");
        logger
                .debug("- Going to serialize resource... -----------------------------");
        // resource.toXml(new PrintWriter(System.out));
        logger
                .debug("- Resource serialized ----------------------------------------");
    }

    /**
     * Getter for <code>data</code>.
     *
     * @return Returns the data.
     */
    public Data getData() {
        return this.data;
    }

    /**
     * Setter for <code>data</code>.
     *
     * @param data
     *            The data to set.
     */
    public void setData(Data data) {
        this.data = data;
    }

    /**
     * Getter for <code>metadata</code>.
     *
     * @return Returns the metadata.
     */
    public Metadata getMetadata() {
        return this.metadata;
    }

    /**
     * Setter for <code>metadata</code>.
     *
     * @param metadata
     *            The metadata to set.
     */
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
