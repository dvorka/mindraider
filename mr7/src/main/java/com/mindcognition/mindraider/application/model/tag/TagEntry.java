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

/**
 * Tag entry interface.
 */
public interface TagEntry {
    int getCardinality();
    void setCardinality(int cardinality);
 
    void inc();
    void dec();
    
    boolean isDead();
    
    String getTagUri();
    void setTagUri(String uri);
    
    String getTagLabel();
    String getTagLabelAsHtml();
    void setTagLabel(String label);
    
    /**
     * Add resource tagged by this tag.
     * 
     * @param taggedResource
     */
    void addResource(TaggedResourceEntry taggedResource);
    
    /**
     * Get resources tagged with this tag.
     * 
     * @return
     */
    TaggedResourceEntry[] getResources();
}
