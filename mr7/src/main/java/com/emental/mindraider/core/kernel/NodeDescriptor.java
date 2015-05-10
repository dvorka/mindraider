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
package com.emental.mindraider.core.kernel;

/**
 * Node descriptor. Check SpidersGraph.render(); for details on id/label/uri
 * differences.
 */
public class NodeDescriptor {

	/**
	 * The id String property.
	 */
	private String id;

	/**
	 * The label String property.
	 */
	private String label;

	/**
	 * The uri String property.
	 */
	private String uri;

	/**
	 * Constructor.
	 */
	public NodeDescriptor() {
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            the id String.
	 * @param label
	 *            the label String.
	 * @param uri
	 *            the uri String.
	 */
	public NodeDescriptor(String id, String label, String uri) {
		this.id = id;
		this.label = label;
		this.uri = uri;
	}

	/**
	 * Getter for <code>id</code>.
	 * 
	 * @return Returns the id.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Setter for <code>id</code>.
	 * 
	 * @param id
	 *            The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Getter for <code>label</code>.
	 * 
	 * @return Returns the label.
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Setter for <code>label</code>.
	 * 
	 * @param label
	 *            The label to set.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Getter for <code>uri</code>.
	 * 
	 * @return Returns the uri.
	 */
	public String getUri() {
		return this.uri;
	}

	/**
	 * Setter for <code>uri</code>.
	 * 
	 * @param uri
	 *            The uri to set.
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
}