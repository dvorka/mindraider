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
package com.mindcognition.mindraider.ui.swing.explorer;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.emental.mindraider.ui.gfx.IconsRegistry;

/**
 * Label explorer tree cell renderer.
 */
public class LabelsTreeCellRenderer extends DefaultTreeCellRenderer {

	private Icon labelsIcon;
	private Icon labelIcon;

	public LabelsTreeCellRenderer() {
		this.labelsIcon = IconsRegistry.getImageIcon("labels.png");
		this.labelIcon = IconsRegistry.getImageIcon("label.png");
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);

		// you may refine this condition
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

		if (node == node.getRoot()) {
			setIcon(labelsIcon);
			setToolTipText("Notebooks' Labels");
		} else {
			if (node.getRoot().getIndex(node) >= 0) {
				// root child is label
				setIcon(labelIcon);
				setToolTipText("Label '" + value+"'");
			}
		}
		return this;
	}
	
    private static final long serialVersionUID = 8402799372867565861L;
}
