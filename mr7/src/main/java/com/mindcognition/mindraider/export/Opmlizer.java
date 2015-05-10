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
package com.mindcognition.mindraider.export;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.regex.Pattern;

import com.mindcognition.mindraider.application.model.note.annotation.AnnotationTransformer;

public final class Opmlizer implements AnnotationTransformer {

    private static Opmlizer singleton;
    public static synchronized Opmlizer getInstance() {
        if (singleton == null) {
            singleton = new Opmlizer();
        }
        return singleton;
    }

    private Opmlizer() {
    }

    public String transform(String annotation) {
        // break line by line using <br/>
        if (annotation == null) {
            return null;
        }

        StringBuffer result = new StringBuffer();
        try {
            // read line by line and twikify
            BufferedReader bufferedReader = new BufferedReader(
                    new StringReader(annotation));

            String line, prefix;
            while ((line = bufferedReader.readLine()) != null) {
                if (Pattern.matches("^[ ]+[^ ].*", line)) {
                    prefix = "";
                    while (line.startsWith(" ")) {
                        prefix += "&nbsp;";
                        line = line.substring(1);
                    }
                    line = prefix + line;
                }
                result.append(line);
                result.append("<br/>\n");
            }
        } catch (Exception e) {
            return annotation;
        }
        return result.toString();
    }
}
