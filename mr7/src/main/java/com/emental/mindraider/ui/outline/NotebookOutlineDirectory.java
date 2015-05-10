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
package com.emental.mindraider.ui.outline;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * Directory represents a directory containing other directory's as well as entry's. It adds a name and created property
 * to DefaultMutableTreeNode.
 * 
 * @author Martin Dvorak
 */
public class NotebookOutlineDirectory extends DefaultMutableTreeNode implements OutlineNode {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // not shown - used for building of the tree
    public String uri;

    private String created;

    private String annotation;

    public NotebookOutlineDirectory(String uri, String label, String annotation, String created)
    {
        super(label);
        this.annotation = annotation;
        this.created = created;
        this.uri = uri;
    }

    public void setLabel(String label)
    {
        setUserObject(label);
    }

    public String getLabel()
    {
        return (String) getUserObject();
    }

    public void setAnnotation(String annotation)
    {
        this.annotation = annotation;
    }

    public String getAnnotation()
    {
        return annotation;
    }

    public void setCreated(String created)
    {
        this.created = created;
    }

    public String getCreated()
    {
        return created;
    }

    public String getUri()
    {
        return uri;
    }
}