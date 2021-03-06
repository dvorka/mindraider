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
package com.mindcognition.mindraider;


/**
 * This class is a template for the source code and summarization of patterns and guidelines.
 */
public class CodeStyle {

    /*
     * Rules:
     *  o must: Apache 2.0 header (remove YEARS from it)
     *  
     *  o no: Created 25. 8 2007
     *  o no: @author
     *  o no: CVS version header
     *  
     * Remarks:
     *  o make code short and concise
     *  o do not write obvious javadoc (like "Constructor")
     *  o do not copy inherited javadoc (IDE autogenerated)
     *  o make visibility as restricted as possible (public is exceptional)
     *  o do not localize logger.debug and logger.error messages
     *  
     * Patterns
     *  o methods should not return null - empty array, "", should be rather returned,
     *    in order to avoid checks for null everywhere 
     *  
     */
    
    /**
     * field javadoc starts in lowercase
     */
    int field;    
    
    /*
     * Where to continue:
     * 
     *  o ESC closes dialog (add this functionality to programicon dialog)
     *  o notebook tree: double click notebook in the tree to get its properties (update dialog)
     *  o menu structure
     *  o editor key bindings: ctrl-k, -a, -e       
     * 
     *  o contribute: spellchecker
     */
}
