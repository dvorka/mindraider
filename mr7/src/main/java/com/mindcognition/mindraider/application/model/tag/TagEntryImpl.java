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
package com.mindcognition.mindraider.application.model.tag;

import java.util.Hashtable;


/**
 * Tag entry in the tag navigator hash.
 */
public class TagEntryImpl implements TagEntry {

    /**
     * number of references to tag
     */
    int cardinality;
    
    /**
     * tag label
     */
    String label;
    
    /**
     * tag URI
     */
    String uri;

    /**
     * tagged resources
     */
    private Hashtable<String,TaggedResourceEntry> resources;
    
    /**
     * Constructor.
     */
    public TagEntryImpl() {
    }
    
    /**
     * Constructor.
     * 
     * @param uri
     * @param label
     * @param cardinality
     */
    public TagEntryImpl(String uri, String label, int cardinality) {
        resources=new Hashtable<String,TaggedResourceEntry>();
        
        setTagUri(uri);
        setTagLabel(label);
        this.cardinality=cardinality;
    }
    
    /*
     * (non-Javadoc)
     * @see com.emental.tag.widget.TagEntry#getCardinality()
     */
    public int getCardinality() {
        return cardinality;
    }

    /*
     * (non-Javadoc)
     * @see com.emental.tag.widget.TagEntry#getTagLabel()
     */
    public String getTagLabel() {
        return label;
    }

    /*
     * (non-Javadoc)
     * @see com.emental.tag.widget.TagEntry#getTagUri()
     */
    public String getTagUri() {
        return uri;
    }
    
    /*
     * (non-Javadoc)
     * @see com.emental.tag.widget.TagEntry#setCardinality()
     */
    public void setCardinality(int cardinality) {
        this.cardinality=cardinality;
    }
    
    /*
     * (non-Javadoc)
     * @see com.emental.tag.widget.TagEntry#setTagLabel()
     */
    public void setTagLabel(String label) {
        this.label=label.toLowerCase();
    }
    
    /*
     * (non-Javadoc)
     * @see com.emental.tag.widget.TagEntry#setTagUri(java.lang.String)
     */
    public void setTagUri(String uri) {
        this.uri=uri;
    }

    /*
     * (non-Javadoc)
     * @see com.emental.tag.widget.TagEntry#inc()
     */
    public void inc() {
       cardinality++;
    }
    
    /*
     * (non-Javadoc)
     * @see com.emental.tag.widget.TagEntry#addResource(com.emental.tag.widget.TaggedResourceEntry)
     */
    public void addResource(TaggedResourceEntry taggedResource) {
        // avoid duplicities
        if(taggedResource!=null) {
            if(resources.get(taggedResource.conceptUri)==null) {
                resources.put(taggedResource.conceptUri,taggedResource);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.emental.tag.widget.TagEntry#getResources()
     */
    public TaggedResourceEntry[] getResources() {
        if(resources.size()==0) {
            return null;
        } else {
            TaggedResourceEntry[] resourceArray = resources.values().toArray(new TaggedResourceEntry[resources.size()]);
            return resourceArray;
        }
    }

    public void dec() {
        cardinality--;
    }

    public boolean isDead() {
        return cardinality<=0;
    }

    public String getTagLabelAsHtml() {
        return label.replaceAll("" , "&nbsp;");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TagEntryImpl other = (TagEntryImpl) obj;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        return true;
    }
}
