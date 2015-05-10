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

import java.util.Comparator;


/**
 * Compare tags by name.
 */
public class TagEntryNameComparator implements Comparator<TagEntry> {

    /*
     * (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(TagEntry tagEntry1, TagEntry tagEntry2) {
        return tagEntry1.getTagLabel().toLowerCase().compareTo(tagEntry2.getTagLabel().toLowerCase());
    }

}
