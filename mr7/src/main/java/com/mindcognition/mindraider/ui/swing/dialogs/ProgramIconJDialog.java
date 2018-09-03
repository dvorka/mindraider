/*
 ===========================================================================
   Copyright 2002-2018 Martin Dvorak

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
package com.mindcognition.mindraider.ui.swing.dialogs;

import javax.swing.JDialog;

import com.emental.mindraider.ui.frames.MindRaiderMainWindow;

/**
 * Predecessor of all dialogs.
 */
public class ProgramIconJDialog extends JDialog {

    public ProgramIconJDialog() {
        super(MindRaiderMainWindow.getInstance(), true);                  
    }
    
    public ProgramIconJDialog(String title) {
        super(MindRaiderMainWindow.getInstance(), title, true);          
    }
        
    private static final long serialVersionUID = -1710564059678966249L;
}