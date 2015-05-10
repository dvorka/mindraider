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
package com.emental.mindraider.ui.gfx;

import java.awt.Component;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

/**
 * Icons and images registry.
 *
 * @author Martin.Dvorak
 * @version $Revision: 1.2 $ ($Author: mindraider $)
 */
public final class IconsRegistry extends Component {

    /**
     * The const for icon directory.
     */
    public static final String DIRECTORY_ICONS = "images/";

    /**
     * The directory image.
     */
    public static final String DIRECTORY_IMAGES = "icons/";

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(IconsRegistry.class);

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    private IconsRegistry() {
    }

    /**
     * Get an image.
     *
     * @param name
     *            the image file name
     * @return the Image object
     */
    public static Image getImage(String name) {
        logger.debug("Loading image: "+name+" from "+DIRECTORY_IMAGES + name); // {{debug}}
        Image img = null;
        try {
            URL fileLoc = ClassLoader.getSystemClassLoader().getResource(
                    DIRECTORY_IMAGES + name);
            ImageIcon imgIcon = new ImageIcon(fileLoc, "Image");
            img = imgIcon.getImage();
        } catch (Exception e) {
            logger.debug("error loading image resource: " + name,e);
        }
        return img;
    }

    /**
     * Return the ImageIcon object.
     *
     * @param name
     *            the icon file name
     * @return the ImageIcon object
     */
    public static ImageIcon getImageIcon(String name) {
        //logger.debug("Loading icon: "+name+" from "+DIRECTORY_IMAGES + name); // {{debug}}
        ImageIcon imgIcon = null;
        try {
            URL fileLoc = ClassLoader.getSystemClassLoader().getResource(
                    DIRECTORY_ICONS + name);
            imgIcon = new ImageIcon(fileLoc, "Icon");
        } catch (Exception e) {
            logger.debug("error loading image icon resource: " + name,e);
        }
        return imgIcon;
    }
}
