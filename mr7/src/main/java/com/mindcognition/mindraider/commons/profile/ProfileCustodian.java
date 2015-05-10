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
package com.mindcognition.mindraider.commons.profile;

import java.io.File;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.install.Installer;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;
import com.mindcognition.mindraider.utils.Utils;

/**
 * Registry of user profiles. For the profile identification is used OS login name.
 */
public class ProfileCustodian {
    private static final Logger logger = Logger.getLogger(ProfileCustodian.class);

    public String profilesNest;

    public ProfileCustodian(String profilesNest) {
        this.profilesNest = profilesNest;
        Utils.createDirectory(profilesNest);
    }

    /**
     * Get profile, if it doesn't exist, create new one and install/upgrade.
     * 
     * @param profileName the profile name.
     * @return Returns the profile.
     */
    public Profile getProfile(String profileName) {
        // try to open profile model
                
        String profileFileName 
            = Utils.normalizePath(profilesNest + File.separator + profileName + ".rdf.xml");
        boolean regularProfileExists=new File(profileFileName).exists();
        String eapProfileFileName 
            = Utils.normalizePath(MindRaider.eapProfilesDirectory + File.separator + profileName + ".rdf.xml");
        boolean eapProfileExists=new File(eapProfileFileName).exists();
        
        logger.debug("getProfile() " + profileFileName + " # "+eapProfileFileName);
        logger.debug("Profiles: regular - "+regularProfileExists+", eap - "+eapProfileExists); // {{debug}}
        
        // there are two branches of MR: regular and EAP:
        //  ?) test whether exist regular profile
        //    yes) 
        //      ?) is in actuall MR version?
        //        yes) BOOT
        //        no) 
        //          ? test whether exists EAP profile
        //            yes) ASK USER WHICH ONE TO choose 
        //               a) regular =-> UPGRADE from regular - END
        //               b) eap =-> UPGRADE from eap = END
        //            no) UPGRADE from regular profile - END
        //
        //    no) 
        //      ?) is there eap profile
        //        yes) UPGRADE from EAP profile - END 
        //        no) INSTALL new version of MR - END
        
        Profile profile;
        if(regularProfileExists) {
            logger.debug("  Regular profile exists..."); // {{debug}}
            profile = new Profile(profileFileName);
            profile.load();
            
            if(MindRaider.getVersion().equals(profile.getVersion())) {
                // everything upgraded and up to date - booting MR
                logger.debug("   This MR version profile found! Loading...");
                StatusBar.show("Loading profile " + profileFileName);

                profile = loadRegularProfile(profileFileName);
            } else {
                // the profile is obsolete - check whether there is eap profile, if so, then let user to choose
                if(eapProfileExists) {
                    logger.debug("    Both regular & EAP profiles exist..."); // {{debug}}
                    String[] options={"EAP","Regular release"};
                    int showConfirmDialog = JOptionPane.showOptionDialog(
                            MindRaider.mainJFrame, 
                            "Profile for both Early Access Program version of MindRaider\n" +
                            "and regular release exist. Choose Yes to upgrade from EAP or\n" +
                            "No to upgrade from regular version.",
                            "Choose Upgrade Path",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);
                    logger.debug("YES/NO # "+JOptionPane.YES_OPTION+"/"+JOptionPane.NO_OPTION+" # "+showConfirmDialog); // {{debug}}
                    
                    if(showConfirmDialog==JOptionPane.YES_OPTION) {
                        // upgrade from the EAP - load the profile and determine repository location. then set it to the regular profile
                        logger.debug("  Upgrading from EAP..."); // {{debug}}
                        // load eap profile
                        profile = upgradeFromEapProfile(profileName, profileFileName, eapProfileFileName);
                    } else {
                        // upgrade just normal profile
                        logger.debug("  Upgrading from regular profile..."); // {{debug}}
                        profile=loadRegularProfile(profileFileName);
                    }
                } else {
                    logger.debug("  Upgrading regular profile..."); // {{debug}}
                    profile=loadRegularProfile(profileFileName);
                }
            }
            
        } else {
            if(eapProfileExists) {
                logger.debug("  Upgrading from EAP..."); // {{debug}}
                profile = upgradeFromEapProfile(profileName, profileFileName, eapProfileFileName);
            } else {
                // neither regular nor eap profile exists - install a new version of MR
                logger.debug("  Creating NEW profile...");
                StatusBar.show("Creating profile " + profileFileName);
                // interactive installation is no longer supported
                //NewProfileJDialog profileJFrame = new NewProfileJDialog();
                // location of notebooks and concepts
                String repositoryHome=Utils.normalizePath(MindRaider.user.getHome());
                // hostname
                String hostname=Utils.getHostname();
                String escapedHostname = escapeHostname(hostname);
                // create the profile
                profile = new Profile(
                        profileFileName,
                        profileName,
                        repositoryHome,
                        escapedHostname);
                profile.save();
                profile.load();

                // initialize new home repository by copying default folders/notebooks/concepts/...
                Installer installer = new Installer(profile.getHomeDirectory());
                installer.install(escapedHostname, MindRaider.user.getName());
            }
        }
        return profile;
    }

    private Profile upgradeFromEapProfile(String profileName,
            String profileFileName,
            String eapProfileFileName) {
        Profile profile;
        Profile eapProfile = new Profile(eapProfileFileName);
        eapProfile.load();
        // create brand new profile and set paths to the EAP repository
        String hostname=Utils.getHostname();
        String escapedHostname = escapeHostname(hostname);
        profile = new Profile(
                profileFileName,
                profileName,
                eapProfile.getHomeDirectory().substring(0,eapProfile.getHomeDirectory().length()-(MindRaiderConstants.MR_TITLE.length()+1)), // kill /mindraider from path
                escapedHostname);
        profile.save();
        profile.load();
        return profile;
    }

    private String escapeHostname(String hostname) {
        String escapedHostname;
        if (hostname == null || hostname.length() == 0) {
            escapedHostname = "localhost";
        } else {
            // quote hostname
            escapedHostname = Utils.toNcName(hostname, '-');
            logger.debug(Messages.getString("NewProfileJDialog.quotedHostname",escapedHostname));
        }
        return escapedHostname;
    }

    private Profile loadRegularProfile(String profileFileName) {
        Profile profile = new Profile(profileFileName);
        profile.load();
        logger.debug("PROFILE: active notebook: " + profile.getActiveOutlineUri());
        return profile;
    }
}
