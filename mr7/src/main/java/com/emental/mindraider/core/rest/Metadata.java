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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import com.emental.mindraider.core.MindRaider;
import com.mindcognition.mindraider.utils.PullParsing;

/**
 * Resource metadata.
 */
public class Metadata {

    /**
     * The resource uri.
     */
    public URI uri;

    /**
     * The FOAF author.
     */
    private URI author;

    /**
     * The created timestamp.
     */
    private long created;

    /**
     * The revision number.
     */
    private long revision;

    /**
     * The revision timestamp.
     */
    private long timestamp;

    /**
     * The discarded flag.
     */
    private boolean discarded;

    /**
     * The type.
     */
    private String type;

    /**
     * The MindRaider version.
     */
    private String mindRaiderVersion;

    /**
     * The element metadata constant.
     */
    public static final String ELEMENT_METADATA = "metadata";

    /**
     * The element uri constant.
     */
    public static final String ELEMENT_URI = "uri";

    /**
     * The element author constant.
     */
    public static final String ELEMENT_AUTHOR = "author";

    /**
     * The element created constant.
     */
    public static final String ELEMENT_CREATED = "created";

    /**
     * The element revision constant.
     */
    public static final String ELEMENT_REVISION = "revision";

    /**
     * The element timestamp constant.
     */
    public static final String ELEMENT_TIMESTAMP = "timestamp";

    /**
     * The element discarded constant.
     */
    public static final String ELEMENT_DISCARDED = "discarded";

    /**
     * The element resource type constant.
     */
    public static final String ELEMENT_RESOURCE_TYPE = "type";

    /**
     * The element MindRaider version.
     */
    public static final String ELEMENT_MIND_RAIDER_VERSION = "mr";

    /**
     * Parse metadata from XML.
     *
     * @param xpp
     *            the xml pull parser
     * @throws XmlPullParserException
     *             the xml pull parser exception
     * @throws IOException
     *             the I/O exception
     * @throws URISyntaxException
     *             the URI syntax exception
     */
    void fromXml(XmlPullParser xpp) throws XmlPullParserException, IOException,
            URISyntaxException {
        PullParsing.startElement(xpp, ELEMENT_METADATA);

        PullParsing.startElement(xpp, ELEMENT_URI);
        uri = new URI(xpp.nextText());

        PullParsing.startElement(xpp, ELEMENT_AUTHOR);
        author = new URI(xpp.nextText());

        PullParsing.startElement(xpp, ELEMENT_CREATED);
        created = Long.parseLong(xpp.nextText());

        PullParsing.startElement(xpp, ELEMENT_REVISION);
        revision = Long.parseLong(xpp.nextText());

        PullParsing.startElement(xpp, ELEMENT_TIMESTAMP);
        timestamp = Long.parseLong(xpp.nextText());

        // optional discarded elements OR end
        while (xpp.nextTag() == XmlPullParser.START_TAG) {
            // try element discarded
            if (ELEMENT_DISCARDED.equals(xpp.getName())) {
                xpp.require(XmlPullParser.START_TAG, "", ELEMENT_DISCARDED);
                discarded = Boolean.valueOf(xpp.nextText()).booleanValue();
                continue;
            }
            // try element type
            if (ELEMENT_RESOURCE_TYPE.equals(xpp.getName())) {
                xpp.require(XmlPullParser.START_TAG, "", ELEMENT_RESOURCE_TYPE);
                type = xpp.nextText();
                continue;
            }
            // try element type
            if (ELEMENT_MIND_RAIDER_VERSION.equals(xpp.getName())) {
                xpp.require(XmlPullParser.START_TAG, "",
                        ELEMENT_MIND_RAIDER_VERSION);
                mindRaiderVersion = xpp.nextText();
                continue;
            }
        }
    }

    /**
     * Save metadata to XML. Note that this method also increments revision and
     * changes modification timestamp.
     *
     * @param xs
     *            the xml serializer
     * @throws Exception
     *             a generic exception
     */
    public void toXml(XmlSerializer xs) throws Exception {
        xs.startTag("", ELEMENT_METADATA);

        PullParsing.serializeTextElement(xs, ELEMENT_URI, uri.toASCIIString());
        PullParsing.serializeTextElement(xs, ELEMENT_AUTHOR, author
                .toASCIIString());
        PullParsing.serializeTextElement(xs, ELEMENT_CREATED, Long
                .toString(created));
        revision++;
        PullParsing.serializeTextElement(xs, ELEMENT_REVISION, Long
                .toString(revision));
        timestamp = System.currentTimeMillis();
        PullParsing.serializeTextElement(xs, ELEMENT_TIMESTAMP, Long
                .toString(timestamp));
        if (discarded) {
            PullParsing.serializeTextElement(xs, ELEMENT_DISCARDED, Boolean
                    .toString(discarded));
        }
        if (type != null) {
            PullParsing.serializeTextElement(xs, ELEMENT_RESOURCE_TYPE, type);
        }
        if (mindRaiderVersion == null) {
            mindRaiderVersion = MindRaider.getVersion();
        }
        PullParsing.serializeTextElement(xs, ELEMENT_MIND_RAIDER_VERSION,
                mindRaiderVersion);

        xs.endTag("", ELEMENT_METADATA);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return uri.toASCIIString() + " # " + author.toASCIIString() + " # "
                + created + " # " + revision + " # " + timestamp + " # ";
    }

    /**
     * Getter for <code>author</code>.
     *
     * @return Returns the author.
     */
    public URI getAuthor() {
        return this.author;
    }

    /**
     * Setter for <code>author</code>.
     *
     * @param author
     *            The author to set.
     */
    public void setAuthor(URI author) {
        this.author = author;
    }

    /**
     * Getter for <code>created</code>.
     *
     * @return Returns the created.
     */
    public long getCreated() {
        return this.created;
    }

    /**
     * Setter for <code>created</code>.
     *
     * @param created
     *            The created to set.
     */
    public void setCreated(long created) {
        this.created = created;
    }

    /**
     * Getter for <code>discarded</code>.
     *
     * @return Returns the discarded.
     */
    public boolean isDiscarded() {
        return this.discarded;
    }

    /**
     * Setter for <code>discarded</code>.
     *
     * @param discarded
     *            The discarded to set.
     */
    public void setDiscarded(boolean discarded) {
        this.discarded = discarded;
    }

    /**
     * Getter for <code>mindRaiderVersion</code>.
     *
     * @return Returns the mindRaiderVersion.
     */
    public String getMindRaiderVersion() {
        return this.mindRaiderVersion;
    }

    /**
     * Setter for <code>mindRaiderVersion</code>.
     *
     * @param mindRaiderVersion
     *            The mindRaiderVersion to set.
     */
    public void setMindRaiderVersion(String mindRaiderVersion) {
        this.mindRaiderVersion = mindRaiderVersion;
    }

    /**
     * Getter for <code>revision</code>.
     *
     * @return Returns the revision.
     */
    public long getRevision() {
        return this.revision;
    }

    /**
     * Setter for <code>revision</code>.
     *
     * @param revision
     *            The revision to set.
     */
    public void setRevision(long revision) {
        this.revision = revision;
    }

    /**
     * Getter for <code>timestamp</code>.
     *
     * @return Returns the timestamp.
     */
    public long getTimestamp() {
        return this.timestamp;
    }

    /**
     * Setter for <code>timestamp</code>.
     *
     * @param timestamp
     *            The timestamp to set.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Getter for <code>type</code>.
     *
     * @return Returns the type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Setter for <code>type</code>.
     *
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for <code>uri</code>.
     *
     * @return Returns the uri.
     */
    public URI getUri() {
        return this.uri;
    }

    /**
     * Setter for <code>uri</code>.
     *
     * @param uri
     *            The uri to set.
     */
    public void setUri(URI uri) {
        this.uri = uri;
    }
}
