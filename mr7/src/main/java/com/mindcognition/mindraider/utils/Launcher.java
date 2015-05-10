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
package com.mindcognition.mindraider.utils;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;

public class Launcher {
	private static final Logger logger = Logger.getLogger(Launcher.class);

	private static String localCommand = "explorer ";

	/**
	 * Return the local command.
	 * 
	 * @param localCommand
	 *            the local command String
	 */
	public static void setLocalCommand(String localCommand) {
		if (localCommand != null && localCommand.length() > 0) {
			Launcher.localCommand = localCommand;
		}
	}

	/**
	 * Return the local command.
	 * 
	 * @return the local command String
	 */
	public static String getLocalCommand() {
		return localCommand;
	}

	/**
	 * Try to launch URL in the local browser.
	 * 
	 * @param url
	 *            the url String
	 */
	public static void launchInBrowser(String url) {
		if (url != null) {
			logger.debug("launchInBrowser: " + url);
			try {
				BrowserLauncher.openURL(url);
			} catch (IOException e) {
				logger.debug("Unable to launch in browser!", e);
			}
		}
	}

	/**
	 * Launch via Google.
	 * 
	 * @param label
	 *            the label to search
	 */
	public static void launchViaGoogle(String label) {
		if (label != null) {
			launchInBrowser("http://www.google.com/search?q=" + label);
		}
	}

	/**
	 * Normalize selected node label.
	 * 
	 * @return the normalize selected node label
	 * @deprecated don't use it
	 */
	private static String normalizeSelectedNodeLabel() {
		String command = MindRaider.spidersGraph.getSelectedNodeLabel();
		return normalizeCommand(command);
	}

	/**
	 * Normalize command.
	 * 
	 * @param command
	 *            the command String to normalize
	 * @return the normalized command
	 */
	private static String normalizeCommand(String command) {
		if (command == null) {
			return null;
		}
		if (!command.startsWith("http")) {
			return Utils.normalizePath(command);
		}
		return command;
	}

	/**
	 * Launch via start - local file associations.
	 * 
	 * @param command
	 *            the command String.
	 */
	public static void launchViaStart(String command) {
		logger.debug("launchViaStart: " + command);
		command = normalizeCommand(command);
		logger.debug("  normalized: " + command);
		if (command == null) {
			return;
		}
		command = localCommand + " \"" + command + "\"";

		try {
			logger.debug("  completed : " + command);
			StatusBar.show("Starting process for command: '" + command + "'");
			Runtime.getRuntime().exec(command);
		} catch (Exception e) {
			logger.error("Unable to perform local exec: ", e);
		}
	}

	/**
	 * Launch in IE.
	 * 
	 * @deprecated don't use it
	 */
	public static void launchInExplorer() {
		String command = normalizeSelectedNodeLabel();

		// launchInExplorer(command);
		logger.debug("COMMAND IS: " + command);
		try {
			BrowserLauncher.openURL("http://localhost");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			logger.error("launchInExplorer()", e1);
		}
	}

	/**
	 * Launch via start.
	 * 
	 * @deprecated don't use
	 */
	public static void launchViaStart() {
		String command = normalizeSelectedNodeLabel();
		launchViaStart(command);
	}

	/**
	 * Launch as channel.
	 * 
	 * @deprecated don't use
	 */
	public static void launchAsChannel() {
		String channel = normalizeSelectedNodeLabel();
		if (channel == null) {
			return;
		}
		StatusBar.show("Launching channel " + channel + "...");

		// MindRaider.spidersGraph.load(channel);
	}
}
