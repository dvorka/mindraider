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

public interface AnnotationToHtmlTransformer {

    String HTML_HEAD=
        "<html>" +
        " <head>" +
        "   <style type='text/css'>" +
        "     ul, ol {" +
        "         margin-top: 0px;" +
        "         margin-bottom: 3px;" +
        "         margin-left: 25px;" +
        "     }" +
        "     body {" +
        "         font-family: arial, helvetica, sans-serif; " +
        "         font-size: small;" +
        "     }"+
        "   </style>" +
        " </head>" +
        "<body>";
    
    String HTML_TAIL=
        "</body>"+
        "</html>";
    
    String toHtml(String editorText);
}
