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

import java.io.BufferedReader;
import java.io.StringReader;

public class PlainTextToHtmlTransformer implements AnnotationToHtmlTransformer {

    private static final String HTML_HEAD=
        "<html>" +
        " <head>" +
        "   <style type='text/css'>" +
        "     ul, ol {" +
        "         margin-top: 2px;" +
        "         margin-bottom: 2px;" +
        "         margin-left: 25px;" +
        "     }" +
        "     body {" +
        "         font-family: monospace; " +
        "         font-size: small;" +
        "     }"+
        "   </style>" +
        " </head>" +
        " <body>" +
        "  <pre>";

    private static final String HTML_TAIL="</pre></body></html>";
    
    private static final String HTML_CONTENT_END=""; // - eof -

    public String toHtml(String editorText) {
        if(editorText!=null && editorText.length()>0) {
            BufferedReader bufferedReader = new BufferedReader(new StringReader(editorText));
            String line;
            StringBuffer result=new StringBuffer();
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    int startIdx, endIdx;
                    if((startIdx=line.indexOf("http://"))==-1) {
                        startIdx=line.indexOf("https://");
                    }                    
                    if(startIdx!=-1) {
                        endIdx=line.indexOf(' ', startIdx);
                        if(endIdx==-1) {
                            endIdx=line.length();
                        }
                        endIdx--;
                        String hyperlink = line.substring(startIdx,endIdx-startIdx+1);
                        line=line.substring(0, startIdx)+
                             "<a href='"+hyperlink+"'>"+hyperlink+"</a>"+
                             (endIdx==line.length()?"":line.substring(endIdx+1));
                    }
                    result.append(line);
                    result.append("<br>");
                }
                return HTML_HEAD+result.toString()+HTML_CONTENT_END+HTML_TAIL;
            } catch(Exception e) {
                // return non-transformed default
            }
        }
        return HTML_HEAD+editorText+HTML_CONTENT_END+HTML_TAIL;
    }
    
}
