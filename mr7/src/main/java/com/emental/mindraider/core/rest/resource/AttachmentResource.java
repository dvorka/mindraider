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
package com.emental.mindraider.core.rest.resource;

/**
 * The AttachmentResource class.
 */
public class AttachmentResource {

	/**
	 * The description.
	 */
	private String description;

	/**
	 * The url.
	 */
	private String url;

	/**
	 * Default constructor.
	 */
	public AttachmentResource() {
	}

	/**
	 * Constructor with parameters.
	 * 
	 * @param description
	 *            the description
	 * @param url
	 *            the url
	 */
	public AttachmentResource(String description, String url) {
		this.description = description;
		this.url = url;
	}

	/**
	 * Getter for <code>description</code>.
	 * 
	 * @return Returns the description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Setter for <code>description</code>.
	 * 
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Getter for <code>url</code>.
	 * 
	 * @return Returns the url.
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Setter for <code>url</code>.
	 * 
	 * @param url
	 *            The url to set.
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
