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
package javax.swing;

import java.awt.Component;

import javax.swing.tree.DefaultTreeCellRenderer;

import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.emental.mindraider.ui.outline.treetable.NotebookOutlineEntry;
import com.mindcognition.mindraider.commons.MindRaiderConstants;

/**
 * Outline tree cell renderer.
 */
public class OutlineTreeCellRenderer extends DefaultTreeCellRenderer {

    /**
     * The serial version uid for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The concept icon.
     */
    private Icon conceptIcon;
    private Icon notebookIcon;
    private ImageIcon twikiIcon;
    private ImageIcon htmlIcon;
    private ImageIcon richTextIcon;
    private ImageIcon plainTextIcon;
    private ImageIcon sketchIcon;

    /**
     * Constructor.
     * 
     * @param notebookIcon
     *            The notebook icon.
     * @param conceptIcon
     *            The concept icon.
     */
    public OutlineTreeCellRenderer(Icon notebookIcon, Icon conceptIcon) {
        this.conceptIcon = conceptIcon;
        this.notebookIcon = notebookIcon;
        this.plainTextIcon=IconsRegistry.getImageIcon("mimePlainText.png");
        this.richTextIcon=IconsRegistry.getImageIcon("mimeRichText.png");
        this.htmlIcon=IconsRegistry.getImageIcon("mimeHtml.png");
        this.twikiIcon=IconsRegistry.getImageIcon("mimeTWiki.png");
        this.sketchIcon=IconsRegistry.getImageIcon("mimeSketch.png");
    }

    /**
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree,
     *      java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                row, hasFocus);
        
        // you may refine this condition
        if (row == 0) {
            setIcon(notebookIcon);
            setToolTipText("This is resource.");
        } else {
            if(value!=null && value instanceof NotebookOutlineEntry) {
                final String annotationType = ((NotebookOutlineEntry)value).getAnnotationType();
                if(MindRaiderConstants.MR_OWL_CONTENT_TYPE_PLAIN_TEXT.equals(annotationType)) {
                    setIcon(plainTextIcon);
                } else {
                    if(MindRaiderConstants.MR_OWL_CONTENT_TYPE_RICH_TEXT.equals(annotationType)) {
                        setIcon(richTextIcon);
                    }
                    else {
                        if(MindRaiderConstants.MR_OWL_CONTENT_TYPE_TWIKI.equals(annotationType)) {
                            setIcon(twikiIcon);
                        }
                        else {
                            if(MindRaiderConstants.MR_OWL_CONTENT_TYPE_HTML.equals(annotationType)) {
                                setIcon(htmlIcon);
                            }
                            else {
                                if(MindRaiderConstants.MR_OWL_CONTENT_TYPE_JARNAL.equals(annotationType)) {
                                    setIcon(sketchIcon);
                                }
                                else {
                                    setIcon(conceptIcon);
                                }
                            }
                        }
                    }
                }
            } else {
                setIcon(conceptIcon);
                setToolTipText(null); // no tool tip
            }
        }
        return this;
    }
}