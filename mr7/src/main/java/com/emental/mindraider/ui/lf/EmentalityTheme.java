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
package com.emental.mindraider.ui.lf;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * This class describes a theme using gray colors. 1.7 01/23/03
 */
public class EmentalityTheme extends DefaultMetalTheme {

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getName()
	 */
	public String getName() {
		return "ementality";
	}

	/**
	 * Top rectangles.
	 */
	private final ColorUIResource primary1 = new ColorUIResource(66, 33, 66);

	/**
	 * Controls highlight.
	 */
	private final ColorUIResource primary2 = new ColorUIResource(90, 86, 99);

	/**
	 * Selection color.
	 */
	private final ColorUIResource primary3 = new ColorUIResource(99, 99, 99);

	/**
	 * All controls border.
	 */
	private final ColorUIResource secondary1 = new ColorUIResource(120, 120,
			120);

	/**
	 * Selected tabs.
	 */
	private final ColorUIResource secondary2 = new ColorUIResource(51, 51, 51);

	/**
	 * The background color.
	 */
	private final ColorUIResource secondary3 = new ColorUIResource(102, 102,
			102);

	/**
	 * The foreground text color.
	 */
	private final ColorUIResource black = new ColorUIResource(255, 255, 255);

	/**
	 * The background of edit boxes, trees, etc.
	 */
	private final ColorUIResource white = new ColorUIResource(0, 0, 0);

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getPrimary1()
	 */
	protected ColorUIResource getPrimary1() {
		return primary1;
	}

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getPrimary2()
	 */
	protected ColorUIResource getPrimary2() {
		return primary2;
	}

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getPrimary3()
	 */
	protected ColorUIResource getPrimary3() {
		return primary3;
	}

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getSecondary1()
	 */
	protected ColorUIResource getSecondary1() {
		return secondary1;
	}

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getSecondary2()
	 */
	protected ColorUIResource getSecondary2() {
		return secondary2;
	}

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getSecondary3()
	 */
	protected ColorUIResource getSecondary3() {
		return secondary3;
	}

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getBlack()
	 */
	protected ColorUIResource getBlack() {
		return black;
	}

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getWhite()
	 */
	protected ColorUIResource getWhite() {
		return white;
	}

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getControlTextFont()
	 */
	public FontUIResource getControlTextFont() {
		// TODO Auto-generated method stub
		return super.getControlTextFont();
	}

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getMenuTextFont()
	 */
	public FontUIResource getMenuTextFont() {
		return super.getMenuTextFont();
	}

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getSubTextFont()
	 */
	public FontUIResource getSubTextFont() {
		return super.getSubTextFont();
	}

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getSystemTextFont()
	 */
	public FontUIResource getSystemTextFont() {
		return super.getSystemTextFont();
	}

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getUserTextFont()
	 */
	public FontUIResource getUserTextFont() {
		return super.getUserTextFont();
	}

	/**
	 * @see javax.swing.plaf.metal.MetalTheme#getWindowTitleFont()
	 */
	public FontUIResource getWindowTitleFont() {
		return super.getWindowTitleFont();
	}
}