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
package com.mindcognition.mindraider.integration.gnowsis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

/**
 * Client used to connect to the running Gnowsis hub. It also implements methods
 * for sending resource URIs to the Gnowsis server using "Gnowsis button".
 * <br><br>
 * Gnowsis & MindRaider integration scenarios:
 * <ul>
 *   <li>Send Concept link to Gnowsis.
 *   <li>Browse concept in Gnowsis.
 *   <li>Access Gnowsis URIQA endpoint (not in production now) http://localhost:9998/gnowsis/uriqa?
 *   <li>Search service: http://localhost:9998/gnoJoseki/query.html
 * </ul>
 * 
 * @version $Revision: 1.2 $ ($Author: mindraider $)
 */
public class GnowsisClient {

	/**
	 * The host const.
	 */
	public static final String HOST = "localhost";

	/**
	 * The port const.
	 */
	public static final String PORT = "9998";

	/**
	 * The method send uri for Gnowsis.
	 */
	public static final String METHOD_LINK_URI = "gnowsis_linker.linkResource";

	/**
	 * Send resource URI to Gnowsis.
	 * 
	 * @param url
	 *            the resource url
	 */
	public static void linkResource(String url) {
		call(HOST, PORT, METHOD_LINK_URI, new String[] { url });
	}
    
    /**
     * The method to browse URI in Gnowsis. 
     */
    public static final String METHOD_BROWSE_URI = "gnowsis_browser.browse";
    
    /**
     * Browse resource.
     * 
     * @param uri   resource URI.
     */
    public static void browseResource(String uri) {
        /*     
         <methodCall>
          <methodName>gnowsis.getBoundedDescription</methodName>
          <params>
           <param><value>mozilla:rdf:#$RccdG2</value></param>
          </params>
         </methodCall>
         */
        call(HOST, PORT, METHOD_BROWSE_URI, new String[] { uri });
    }

	/**
	 * Logger for this class.
	 */
	private static final Logger cat = Logger.getLogger(GnowsisClient.class);

	/**
	 * The call method.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param method
	 *            the method
	 * @param params
	 *            the parameters array
	 */
	public static void call(String host, String port, String method,
			String[] params) {
		cat.debug("call (host:" + host + " port:" + port + " method:" + method);

		try {
			// build the parameter string using a StringBuffer to perform
			// quickly.
			String message = null;
			StringBuffer bufMessage = new StringBuffer();
			bufMessage.append("<?xml version='1.0' encoding='ISO-8859-1'?>");
			bufMessage.append("<methodCall>");
			bufMessage.append("<methodName>");
			bufMessage.append(method);
			bufMessage.append("</methodName>");
			bufMessage.append("<params>");

			if (params != null && params.length > 0) {
				// todo: if (aParams instanceof Array)
				for (int i = 0; i < params.length; i++) {
					bufMessage.append("<param><value><![CDATA[" + params[i]
							+ "]]></value></param>");
				}
			}
			bufMessage.append("</params></methodCall>");
			message = bufMessage.toString();
			bufMessage = null;

			/*
			 * send message
			 */
			String stringUrl = "http://" + host + ":" + port + "/RPC2";
			cat.debug("Sending message to: " + stringUrl + "\n" + message);

			URL url = new URL(stringUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.getOutputStream().write(message.getBytes());
			urlConnection.getOutputStream().flush();
            // TODO to be in finally
            urlConnection.getOutputStream().close();

			// get the response
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				cat.debug("#server# " + line);
			}
			bufferedReader.close();
		} catch (Exception e) {
			cat.debug("Unable to send link to Gnowsis!", e);
		}
	}
}
