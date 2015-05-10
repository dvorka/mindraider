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
package com.mindcognition.mindraider.application.model.label;

import com.emental.mindraider.core.rest.resource.FolderResource;

public interface LabelCustodianListener {

    /**
     * Invoked by custodian on label creation.
     * 
     * @param newLabel
     *            the new label
     * @return
     */
    void labelCreated(FolderResource newLabel);
}
