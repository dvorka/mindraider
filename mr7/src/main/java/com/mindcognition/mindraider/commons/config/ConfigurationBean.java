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

/**
 * ConfigurationBean will be no longer manually written RDF s/d class. Instead it will be 
 * normal Java bean and its serialization will be done automatically (annotations +
 * method which reads these annotations and stores configuration fields to the repository).
 */
public class ConfigurationBean {
	
    // general
    String repositoryBackupDirectory=null;
    String profileDirectory=null;
    String repositoryDirectory=null;
    boolean checkForNewVersionOnStartup=false;
    boolean forceLocaleRestartNeeded=false;
    String locale="English";
    String loginName=null;
    String homeDirectory=null;

    // UI
    boolean showSpidersAndTagSnailPane=true; // used
    boolean defaultTabMindMap=true; // used
    boolean showPerspectiveIconInStatusBar=false; // used
    boolean showModeInInStatusBar=false; // used

    // resource launchers
    String webUrlsLinuxOnly=null;
    String filesystem=null;
    
    // daemons
    boolean enableTwikiCommandDaemonRestartNeeded=false;
    
    public boolean isShowSpidersAndTagSnailPane() {
        return showSpidersAndTagSnailPane;
    }

    public void setShowSpidersAndTagSnailPane(boolean showSpidersAndTagSnailPane) {
        this.showSpidersAndTagSnailPane = showSpidersAndTagSnailPane;
    }
    
    public String getProfileDirectory() {
        return profileDirectory;
    }

    public void setProfileDirectory(String profileDirectory) {
        this.profileDirectory = profileDirectory;
    }

    public String getRepositoryDirectory() {
        return repositoryDirectory;
    }

    public void setRepositoryDirectory(String repositoryDirectory) {
        this.repositoryDirectory = repositoryDirectory;
    }

    public boolean isForceLocaleRestartNeeded() {
        return forceLocaleRestartNeeded;
    }

    public void setForceLocaleRestartNeeded(boolean forceLocaleRestartNeeded) {
        this.forceLocaleRestartNeeded = forceLocaleRestartNeeded;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public void setHomeDirectory(String homeDirectory) {
        this.homeDirectory = homeDirectory;
    }

    public String getWebUrlsLinuxOnly() {
        return webUrlsLinuxOnly;
    }

    public void setWebUrlsLinuxOnly(String webUrlsLinuxOnly) {
        this.webUrlsLinuxOnly = webUrlsLinuxOnly;
    }

    public String getFilesystem() {
        return filesystem;
    }

    public void setFilesystem(String filesystem) {
        this.filesystem = filesystem;
    }

    public boolean isEnableTwikiCommandDaemonRestartNeeded() {
        return enableTwikiCommandDaemonRestartNeeded;
    }

    public void setEnableTwikiCommandDaemonRestartNeeded(boolean enableTwikiCommandDaemonRestartNeeded) {
        this.enableTwikiCommandDaemonRestartNeeded = enableTwikiCommandDaemonRestartNeeded;
    }
    
    public ConfigurationBean() {
    }

    public boolean isShowSpidersTagSnailPane() {
        return showSpidersAndTagSnailPane;
    }

    public void setShowSpidersTagSnailPane(boolean showSpidersTagSnailPane) {
        this.showSpidersAndTagSnailPane = showSpidersTagSnailPane;
    }
    
    public boolean isShowPerspectiveIconInStatusBar() {
        return showPerspectiveIconInStatusBar;
    }

    public void setShowPerspectiveIconInStatusBar(boolean showPerspectiveIconInStatusBar) {
        this.showPerspectiveIconInStatusBar = showPerspectiveIconInStatusBar;
    }

    public boolean isShowModeInInStatusBar() {
        return showModeInInStatusBar;
    }

    public void setShowModeInInStatusBar(boolean showModeInInStatusBar) {
        this.showModeInInStatusBar = showModeInInStatusBar;
    }

    public boolean isDefaultTabMindMap() {
        return defaultTabMindMap;
    }

    public void setDefaultTabMindMap(boolean defaultTabMindMap) {
        this.defaultTabMindMap = defaultTabMindMap;
    }

    public boolean getCheckForNewVersionOnStartup() {
        return checkForNewVersionOnStartup;
    }

    public boolean isCheckForNewVersionOnStartup() {
        return checkForNewVersionOnStartup;
    }

    public void setCheckForNewVersionOnStartup(boolean checkForNewVersionOnStartup) {
        this.checkForNewVersionOnStartup = checkForNewVersionOnStartup;
    }

    public String getRepositoryBackupDirectory() {
        return repositoryBackupDirectory;
    }

    public void setRepositoryBackupDirectory(String repositoryBackupDirectory) {
        this.repositoryBackupDirectory = repositoryBackupDirectory;
    }
}

