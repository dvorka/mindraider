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

public class ResourceDescriptor {

    // basic properties
	private String label;
	private String uri;
	private long created;
	private String annotationContentType;
	private String annotationCite;
	
	// extra properties
	private long modified;
	private long revision;
	private String category;
	
	private String mindForgerId;
	
	public ResourceDescriptor() {
	}

	public ResourceDescriptor(String label, String uri) {
		this.label = label;
		this.uri = uri;
	}

	public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAnnotationCite() {
		return annotationCite;
	}

	public void setAnnotationCite(String annotationCite) {
		this.annotationCite = annotationCite;
	}

	public long getCreated() {
		return this.created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUri() {
		return this.uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

    public String getAnnotationContentType() {
        return annotationContentType;
    }

    public void setAnnotationContentType(String annotationContentType) {
        this.annotationContentType = annotationContentType;
    }
    
    public String getMindForgerId() {
        return mindForgerId;
    }

    public void setMindForgerId(String mindForgerId) {
        this.mindForgerId = mindForgerId;
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
        final ResourceDescriptor other = (ResourceDescriptor) obj;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        return true;
    }
}