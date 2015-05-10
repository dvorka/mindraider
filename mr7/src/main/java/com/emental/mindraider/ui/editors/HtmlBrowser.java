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
package com.emental.mindraider.ui.editors;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.log4j.Logger;

/**
 * HtmlBrowser.java
 * 
 * @author Martin.Dvorak
 */
public class HtmlBrowser extends JPanel {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(HtmlBrowser.class);

	/**
	 * The serial version uid for serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The wiki text pane.
	 */
	final JTextPane tpWikiText;

	/**
	 * The location text field
	 */
	final JTextField location;

	/**
	 * Constructor.
	 */
	public HtmlBrowser() {
		setLayout(new BorderLayout());

		JPanel p = new JPanel();
		p.setBorder(new EtchedBorder());
		p.add(new JLabel("Model: "));
		location = new JTextField("", 50);
		location.setEditable(true);
		location.setToolTipText("RDF Model location");
		location.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						tpWikiText
								.setPage(new File(location.getText()).toURL());
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						logger.error("keyPressed(KeyEvent)", e);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error("keyPressed(KeyEvent)", e);
					}
				}
			}

			public void keyReleased(KeyEvent keyEvent) {
			}

			public void keyTyped(KeyEvent keyEvent) {
			}
		});
		p.add(location);
		add(p, BorderLayout.NORTH);

		tpWikiText = new JTextPane();
		tpWikiText.setEditable(false);

		tpWikiText.addHyperlinkListener(new HyperlinkListener() {

			public void hyperlinkUpdate(HyperlinkEvent e) {
			}
		});
		tpWikiText.setEditorKit(new HTMLEditorKit());
		add(tpWikiText);

		try {
			tpWikiText.setPage(new File("Help.html").toURL());
		} catch (MalformedURLException e1) {
			logger.error("HtmlBrowser()", e1);
		} catch (IOException e1) {
			logger.error("HtmlBrowser()", e1);
		}
	}
}