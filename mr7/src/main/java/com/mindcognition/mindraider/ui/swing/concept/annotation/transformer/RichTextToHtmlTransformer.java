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
package com.mindcognition.mindraider.ui.swing.concept.annotation.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emental.mindraider.core.MindRaider;
import com.mindcognition.mindraider.application.model.note.annotation.NoteInterlinking;
import com.mindcognition.mindraider.commons.representation.twiki.TwikiToHtml;
import com.mindcognition.mindraider.ui.swing.Gfx;

public class RichTextToHtmlTransformer implements AnnotationToHtmlTransformer {

    private static final Log logger = LogFactory.getLog(RichTextToHtmlTransformer.class); // {{debug}}
        
    public RichTextToHtmlTransformer() {
    }
    
    public String toHtml(String editorText) {
        // MR interlinking
        editorText = NoteInterlinking.translate(editorText);

        // break it to lines
        ArrayList<String> text = new ArrayList<String>(Arrays.asList(editorText.split("\n")));
        // here comes additonal processing
        text = TwikiToHtml.linksProcess(text);
        text = TwikiToHtml.variablesProcess(text);
        // text=TwikiToHtml.replaceProcess(text,"[ ]", "&nbsp;");

        // concatenate lines as PARAGRAPHS (BR tag is buggy in Sun implementation and doesn't work)
        StringBuffer buffer = new StringBuffer();
        buffer.append(getHtmlHead());
        String bullets;
        for (int i = 0; i < text.size(); ++i) {
            // colorize todo lines: o ! # x
            String line=htmlColorizeToDoLine(text.get(i));
            
            // replace leading spaces with &nbsp;
            bullets="";
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == ' ') {
                    bullets += "&nbsp;";
                } else {
                    bullets += line.substring(j);
                    break;
                }
            }

            // since document.getText() is buggy an only possible roundtrip is
            // usage of paragraph
            buffer.append("<p align='left'>" + bullets + "</p>\n");
        }
        buffer.append(getHtmlTail());

        logger.debug("<-= text2html:\n"+buffer.toString());
        
        return buffer.toString();
    }

    /**
     * Get HTML head.
     */
    private String getHtmlHead() {
        return 
        "<html>" +
        " <head>" +
        "   <style type='text/css'>" +
        "     p {"
        + "         margin-top: 0px;" + "         margin-bottom: 0px;" + "         margin-left: 0px;"
        + "     }" + 
        "     body {" + 
        "         color: #"+Gfx.getColorHexString(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getTextColor())+"; " + 
        "         background: #"+Gfx.getColorHexString(MindRaider.annotationColorProfileRegistry.getCurrentProfile().getBackroundColor())+"; " + 
        "         font-family: monospace; " + 
        "         font-size: small;"
        + "     }" +
        "   </style>" +
        " </head>" +
        " <body>";
    }
    
    private String getHtmlTail() {
         return " </body></html>";
    }
    
    /**
     * Content based colorization of the annotation - mainly todos:
     * <ul>
     *  <li>o normal
     *  <li>x done (gray)
     *  <li>! important (red)
     *  <li>? question (blue)
     * <ul>
     * 
     * @param annotation
     * @return
     */
    private String htmlColorizeToDoLine(String line) {
        if (line == null) {
            return null;
        } else {            
            int idx;
            String prefix;
            
            final int EXTRA_INDENTATION=1; // for twiki it is 3
            if (Pattern.matches("^[ ]+o .*", line)) {
                idx = line.indexOf('o');
                line = line.substring(idx + 1);
                prefix = "";
                for (int i = 0; i < idx * EXTRA_INDENTATION; i++) {
                    prefix += " ";
                }
                line = prefix + "o" + line;
            } else {
                if (Pattern.matches("^[ ]+! .*", line)) {
                    prefix = "";
                    while (line.startsWith(" ")) {
                        prefix += " ";
                        line = line.substring(1);
                    }
                    line = prefix + "! <b><font color='#dd6060'>" + line.substring(1) + "</font></b>";
                } else {
                    if (Pattern.matches("^[ ]+x .*", line)) {
                        idx = line.indexOf('x');
                        line = line.substring(idx + 1);
                        prefix = "";
                        for (int i = 0; i < idx * EXTRA_INDENTATION; i++) {
                            prefix += " ";
                        }
                        line = prefix + "x <i><font color='gray'>" + line + "</font></i>";
                    } else {
                        if (Pattern.matches("^[ ]+\\? .*", line)) {
                            idx = line.indexOf('?');
                            line = line.substring(idx + 1);
                            prefix = "";
                            for (int i = 0; i < idx * EXTRA_INDENTATION; i++) {
                                prefix += " ";
                            }
                            line = prefix + "? <b><font color='blue'>" + line + "</font></b>";
                        }
                    }
                }
            }
            
            return line+"\n";
        }
    }
}
