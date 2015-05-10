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
package com.mindcognition.mindraider.ui.swing.concept;

import java.util.HashMap;

import com.mindcognition.mindraider.commons.MindRaiderConstants;

/**
 * Classifier flag renderer - OWL taxonomy 2 UI captions/colors transposition.
 */
public class ClassifierFlagRenderer {

	/**
	 * The comboLabels array String.
	 */
	public static String[] comboLabels;

	/**
	 * The combo label Hashmap.
	 */
	public static HashMap<String,String> comboLabel2OwlClassMap;

	/**
	 * The color HashMap.
	 */
	public static HashMap<String,String> owlClass2ColorMap;

	/**
	 * The text color HashMap.
	 */
	public static HashMap<String,String> owlClass2TextColorMap;

	static {
		// combo labels
		comboLabels = new String[] { "None", "Important", "Cool", "ToDo",
				"Personal", "Later", "Obsolete", "Problem" };

		// combo 2 OWL class map
		comboLabel2OwlClassMap = new HashMap<String,String>();
		comboLabel2OwlClassMap.put(comboLabels[0], null);
		comboLabel2OwlClassMap.put(comboLabels[1],
				MindRaiderConstants.MR_OWL_FLAG_IMPORTANT);
		comboLabel2OwlClassMap.put(comboLabels[2],
				MindRaiderConstants.MR_OWL_FLAG_COOL);
		comboLabel2OwlClassMap.put(comboLabels[3],
				MindRaiderConstants.MR_OWL_FLAG_TODO);
		comboLabel2OwlClassMap.put(comboLabels[4],
				MindRaiderConstants.MR_OWL_FLAG_PERSONAL);
		comboLabel2OwlClassMap.put(comboLabels[5],
				MindRaiderConstants.MR_OWL_FLAG_LATER);
		comboLabel2OwlClassMap.put(comboLabels[6],
				MindRaiderConstants.MR_OWL_FLAG_OBSOLETE);
		comboLabel2OwlClassMap.put(comboLabels[7],
				MindRaiderConstants.MR_OWL_FLAG_PROBLEM);

		// OWL class 2 color map
		owlClass2ColorMap = new HashMap<String,String>();
		owlClass2ColorMap.put(MindRaiderConstants.MR_OWL_FLAG_IMPORTANT,
				"#ff8181");
		owlClass2ColorMap.put(MindRaiderConstants.MR_OWL_FLAG_COOL, "#99b1ff");
		owlClass2ColorMap.put(MindRaiderConstants.MR_OWL_FLAG_TODO, "#ffcd8d");
		owlClass2ColorMap.put(MindRaiderConstants.MR_OWL_FLAG_PERSONAL,
				"#7fc074");
		owlClass2ColorMap.put(MindRaiderConstants.MR_OWL_FLAG_LATER, "#c79ec9");
		owlClass2ColorMap
				.put(MindRaiderConstants.MR_OWL_FLAG_OBSOLETE, "white");
		owlClass2ColorMap.put(MindRaiderConstants.MR_OWL_FLAG_PROBLEM, "black");

		// OWL class 2 text color map
		owlClass2TextColorMap = new HashMap<String,String>();
		owlClass2TextColorMap.put(MindRaiderConstants.MR_OWL_FLAG_IMPORTANT,
				"white");
		owlClass2TextColorMap
				.put(MindRaiderConstants.MR_OWL_FLAG_COOL, "white");
		owlClass2TextColorMap
				.put(MindRaiderConstants.MR_OWL_FLAG_TODO, "white");
		owlClass2TextColorMap.put(MindRaiderConstants.MR_OWL_FLAG_PERSONAL,
				"white");
		owlClass2TextColorMap.put(MindRaiderConstants.MR_OWL_FLAG_LATER,
				"white");
		owlClass2TextColorMap.put(MindRaiderConstants.MR_OWL_FLAG_OBSOLETE,
				"#bbbbbb");
		owlClass2TextColorMap.put(MindRaiderConstants.MR_OWL_FLAG_PROBLEM,
				"white");
	}

	/**
	 * Return the HTML color for given categoryUri.
	 * 
	 * @param categoryUri
	 *            the category URI to check
	 * @return the HTML String color
	 */
	public static String getHtmlColorForUri(String categoryUri) {
		if (categoryUri != null) {
			String color = (String) owlClass2ColorMap.get(categoryUri);
			String textColor = (String) owlClass2TextColorMap.get(categoryUri);
			if (color != null) {
				return " bgColor='" + color + "' text='" + textColor + "' ";
			}
		}
		return "";
	}
}
