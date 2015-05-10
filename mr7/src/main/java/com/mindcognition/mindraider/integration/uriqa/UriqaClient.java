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
package com.mindcognition.mindraider.integration.uriqa;

import org.apache.log4j.Logger;

/**
 * URIQA connection custodian used in "URIQA mode". <br>
 * <br>
 * URIQA mode is set in {@link com.mindcognition.mindraider.ui.swing.main.StatusBar}.
 * In model rendering are graph URLs generated using this helper (see method for
 * getting resource representation URL from URI). Double click handler in the
 * RDF Navigator is then enriched to detect URIQA mode and union/load over
 * option in order to build graph on the client side.
 */
public final class UriqaClient {
	private static final Logger cat = Logger.getLogger(UriqaClient.class);

	/**
	 * The server url.
	 */
	private String serverUrl;

	/**
	 * The include infered option.
	 */
	private boolean includeInfered;

	/**
	 * Constructor.
	 * 
	 * @param serverUrl
	 *            the server url
	 * @param includeInfered
	 *            include infered option
	 */
	private UriqaClient(String serverUrl, boolean includeInfered) {
		this.serverUrl = serverUrl;
		this.includeInfered = includeInfered;
	}

	/**
	 * URIQA connection.
	 */
	public static UriqaClient session;

	/**
	 * Establish connection to URIQA server.
	 * 
	 * @param serverUrl
	 *            the server url
	 * @param includeInfered
	 *            include infered
	 */
	public static void establishConnection(String serverUrl,
			boolean includeInfered) {
		session = new UriqaClient(serverUrl, includeInfered);
	}

	/**
	 * Disconnect from URIQA server.
	 */
	public static void disconnect() {
		session = null;
	}

	/**
	 * URI to URL mapping for resource representations.
	 * 
	 * @param resourceUri
	 *            the resource uri string
	 * @return the URI string
	 */
	public static String getResourceRepresentationUrl(String resourceUri) {
		if (session != null) {
			StringBuffer url = new StringBuffer(session.serverUrl).append(
					"?uri=").append(resourceUri).append(
					"&application%2Frdf%2Bxml&naming=uri&inference=");
			if (session.includeInfered) {
				url.append("include");
			} else {
				url.append("exclude");
			}
			cat.debug("URIQA resource URL: " + url.toString());
			return url.toString();
		}
		cat.debug("NO URIQA SESSION OPENED!");
		return null;
	}
}
