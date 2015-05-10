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

import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.emental.mindraider.ui.gfx.IconsRegistry;

/**
 * Search type combo renderer.
 * 
 * @author Martin.Dvorak
 */
public class SearchTypeComboRenderer extends JLabel implements ListCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Font uhOhFont;

    private ImageIcon[] images;

    /**
     * Constructor.
     */
    public SearchTypeComboRenderer() {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);

        // prepare search images
        images = new ImageIcon[] {
                IconsRegistry.getImageIcon("searchLabels.png"),
                IconsRegistry.getImageIcon("searchConcept.png"),
                IconsRegistry.getImageIcon("searchForConcept.png"),
                IconsRegistry.getImageIcon("searchForNotebook.png") };
    }

    /*
     * This method finds the image and text corresponding to the selected value
     * and returns the label, set up to display the text and image.
     */
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        // Get the selected index. (The index param isn't
        // always valid, so just use the value.)
        int selectedIndex = ((Integer) value).intValue();

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        // set the icon and text. If icon was null, say so.
        ImageIcon icon = images[selectedIndex];
        String pet = "";
        setIcon(icon);
        if (icon != null) {
            setText(pet);
            setFont(list.getFont());
        } else {
            setUhOhText(pet + " (no image available)", list.getFont());
        }

        return this;
    }

    // Set the font and text when no image was found.
    protected void setUhOhText(String uhOhText, Font normalFont) {
        if (uhOhFont == null) { // lazily create this font
            uhOhFont = normalFont.deriveFont(Font.ITALIC);
        }
        setFont(uhOhFont);
        setText(uhOhText);
    }
}