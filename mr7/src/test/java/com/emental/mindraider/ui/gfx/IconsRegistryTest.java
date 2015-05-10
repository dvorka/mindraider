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

import java.awt.Image;

import javax.swing.ImageIcon;

import junit.framework.TestCase;

/**
 * IconsRegistry test class.
 */
public class IconsRegistryTest extends TestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * getImage test case.
     *
     */
    public void testGetImage() {
        String imageName = "jarnal/filesave.png";
        Image image = IconsRegistry.getImage(imageName);
        assertNotNull("image is null", image);
    }

    /**
     * getImageIcon test case.
     *
     */
    public void testGetImageIcon() {
        String imageName = "jarnal/blank.png";
        ImageIcon image = IconsRegistry.getImageIcon(imageName);
        assertNotNull("icon is null", image);
    }
}
