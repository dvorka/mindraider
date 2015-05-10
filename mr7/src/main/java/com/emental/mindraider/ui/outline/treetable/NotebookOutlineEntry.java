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
package com.emental.mindraider.ui.outline.treetable;

import javax.swing.tree.DefaultMutableTreeNode;

import com.emental.mindraider.ui.outline.OutlineNode;

/**
 * Entry represents a resource's outline. It contains a URL, a user definable
 * string, and two dates, one giving the date the URL was last visited and the
 * other giving the date the bookmark was created.
 */
public class NotebookOutlineEntry extends DefaultMutableTreeNode implements OutlineNode {

    // not shown - used for building of the tree
    public String uri;
    private String label;
    private String annotation;
    private String created;
    
    private String annotationType;

    /**
     * Constructor.
     */
    public NotebookOutlineEntry(String uri, String label, String annotation, String annotationType, String created) {
        this.label = label;
        this.annotation = annotation;
        this.annotationType=annotationType;
        this.created = created;
        this.uri = uri;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreated() {
        return created;
    }

    public String toString() {
        return getLabel();
    }

    public String getUri() {
        return uri;
    }
    
    private static final long serialVersionUID = -2356991207029364381L;

    public String getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }    
}    
