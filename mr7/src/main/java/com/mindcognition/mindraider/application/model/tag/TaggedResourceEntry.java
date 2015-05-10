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
 * Descriptor of the resource being tagged.
 */
public class TaggedResourceEntry {

    public String conceptUri;
    public String conceptLabel;
    public long conceptTimestamp;
    
    public String notebookUri;
    public String notebookLabel;
    public String conceptPath;
    
    /**
     * Constructor.
     * 
     * @param conceptUri
     * @param conceptLabel
     * @param conceptTimestamp
     */
    public TaggedResourceEntry(
            String notebookUri, String notebookLabel,
            String conceptUri, String conceptLabel, long conceptTimestamp, String conceptPath) {
        this.notebookUri=notebookUri;
        this.notebookLabel=notebookLabel;
        this.conceptUri=conceptUri;
        this.conceptLabel=conceptLabel;
        this.conceptTimestamp=conceptTimestamp;
        this.conceptPath=conceptPath;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TaggedResourceEntry other = (TaggedResourceEntry) obj;
        if (conceptUri == null) {
            if (other.conceptUri != null)
                return false;
        } else if (!conceptUri.equals(other.conceptUri))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return conceptUri;
    }
    
}
