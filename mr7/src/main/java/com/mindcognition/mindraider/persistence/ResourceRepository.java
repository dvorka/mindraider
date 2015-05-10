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
package com.mindcognition.mindraider.persistence;

/**
 * Interface of the repository which is responsible for storing all resources.
 * It stores notebooks and concepts (former folders are replaced by GMail-like tags).
 * It is global (i.e. concepts are visible across notebooks e.g. when a tag based
 * search is performed). Tag is a key concept which is used to put notebooks to folders
 * (folder ~ tag) and concepts to notebooks (notebook ~ tag). Thus notebook might be
 * in several folders and concepts in several notebooks.
 * 
 * Hierarchy of the concepts is realized using tags as well - thus particular concept
 * might present more then once in the particular notebook, but also in multiple
 * notebooks. Hierarchy are just references, to remove notebook, no reference must 
 * exist (user gets warning describing whether he is going to unlink or purge the notebook).
 */
public interface ResourceRepository {

}
