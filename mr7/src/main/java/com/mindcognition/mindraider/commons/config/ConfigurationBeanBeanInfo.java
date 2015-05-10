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
package com.mindcognition.mindraider.commons.config;

import com.l2fprod.common.beans.BaseBeanInfo;

public class ConfigurationBeanBeanInfo extends BaseBeanInfo {
    
    public ConfigurationBeanBeanInfo() {
        super(ConfigurationBean.class);

        addProperty("checkForNewVersionOnStartup").setCategory("General");
        addProperty("repositoryBackupDirectory").setCategory("General");
        addProperty("profileDirectory").setCategory("General");
        addProperty("repositoryDirectory").setCategory("General");
        addProperty("forceLocaleRestartNeeded").setCategory("General");
        addProperty("locale").setCategory("General");
        addProperty("loginName").setCategory("General");
        addProperty("homeDirectory").setCategory("General");
        
        addProperty("webUrlsLinuxOnly").setCategory("Attachment Lauchers");
        addProperty("filesystem").setCategory("Attachment Launchers");
        
        addProperty("showPerspectiveIconInStatusBar").setCategory("UI");
        addProperty("showModeInInStatusBar").setCategory("UI");
        addProperty("defaultTabMindMap").setCategory("UI");
        addProperty("showSpidersAndTagSnailPane").setCategory("UI");
        
        addProperty("enableTwikiCommandDaemonRestartNeeded").setCategory("Deamons");
    }
}
