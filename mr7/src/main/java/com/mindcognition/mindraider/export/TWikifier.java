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

import org.apache.log4j.Logger;

import com.mindcognition.mindraider.application.model.note.annotation.AnnotationTransformer;

public final class TWikifier implements AnnotationTransformer {

    /*
     * TODO Twikify issues 
     *  o %BR% not translated 
     *  o %ENDCOLOR% 2 </font> 
     *  o %BLACK% ... (basic colors) to <font color='...'> 
     *  o multiline bullets note translated (when the line is broken, it must be appended to previous
     *    one
     */
    
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(TWikifier.class);

    /**
     * Singleton.
     */
    private static TWikifier singleton;

    /**
     * Constructor of TWikifier. It is private because we want a singleton of
     * this class.
     */
    private TWikifier() {
    }

    /**
     * Get singleton of this class.
     * 
     * @return singleton of this class.
     */
    public static synchronized TWikifier getInstance() {
        if (singleton == null) {
            singleton = new TWikifier();
        }
        return singleton;
    }

    /**
     * TWikify annotation.
     * <ul>
     * <li>Unordered list - replaces ' o' with ' *' (and similarly for the
     * other quick bullets).</li>
     * <li>Ordered list - replaces ' #' with ' 1' (and similarly for the other
     * quick bullets).</li>
     * </ul>
     * 
     * @param annotation
     *            The annotation to transform.
     * @return String Returns the wikified annotation.
     */
    public String transform(String annotation) {
        if (annotation == null) {
            return null;
        }

        StringBuffer result = new StringBuffer();
        try {
            // read line by line and twikify
            BufferedReader bufferedReader = new BufferedReader(
                    new StringReader(annotation));

            String line, prefix;
            int idx;
            while ((line = bufferedReader.readLine()) != null) {
                if (Pattern.matches("^[ ]+o .*", line)) {
                    idx = line.indexOf('o');
                    line = line.substring(idx + 1);
                    prefix = "";
                    for (int i = 0; i < idx * 3; i++) {
                        prefix += " ";
                    }
                    line = prefix + "*" + line;
                } else {
                    if (Pattern.matches("^[ ]+# .*", line)) {
                        prefix = "";
                        while (line.startsWith(" ")) {
                            prefix += "   ";
                            line = line.substring(1);
                        }
                        line = prefix + "1" + line.substring(1);
                    } else {
                        if (Pattern.matches("^[ ]+x .*", line)) {
                            idx = line.indexOf('x');
                            line = line.substring(idx + 1);
                            prefix = "";
                            for (int i = 0; i < idx * 3; i++) {
                                prefix += " ";
                            }
                            line = prefix + "* _%GRAY%" + line + "%ENDCOLOR%_";
                        }
                    }
                }
                result.append(line);
                result.append("\n");
            }
        } catch (Exception e) {
            return annotation;
        }

        // used for export
        result.append("\n");

        return result.toString();
    }

    /**
     * The main procedure.
     * 
     * @param args
     *            arguments The array of arguments.
     */
    public static void main(String[] args) {
        TWikifier twiki = new TWikifier();
        logger.debug("\n"
                + twiki.transform("aaaa" + "\n o yo " + "Blah "
                        + "\n # one yoyo" + "\n  # two yoyo" + "\n nenene"));
    }

}
