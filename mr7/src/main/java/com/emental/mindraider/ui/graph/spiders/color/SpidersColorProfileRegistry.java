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
package com.emental.mindraider.ui.graph.spiders.color;

import java.util.Collections;
import java.util.Hashtable;

public class SpidersColorProfileRegistry {

    /**
     * Color profiles.
     */
    private Hashtable<String,SpidersColorProfile> colorProfiles=new Hashtable<String,SpidersColorProfile>();
    
    /**
     * Default profile.
     */
    private SpidersColorProfile defaultProfile;

    /**
     * Current profile.
     */
    private SpidersColorProfile currentProfile;

    /**
     * Constructor.
     */
    public  SpidersColorProfileRegistry() {
        SpidersColorProfile white=new WhiteSpidersColorProfile();
        register(white);
        BlackSpidersColorProfile black = new BlackSpidersColorProfile();
        register(black);
        BlackAndBlueSpidersColorProfile blackAndBlue = new BlackAndBlueSpidersColorProfile();
        register(blackAndBlue);
        register(new BlackAndGreenSpidersColorProfile());
        register(new BlackAndRedSpidersColorProfile());
        register(new BlackAndOrangeSpidersColorProfile());
        
        defaultProfile=currentProfile=blackAndBlue;
    }
    
    /**
     * Register profile.
     * 
     * @param SpidersColorProfile
     */
    public void register(SpidersColorProfile SpidersColorProfile) {
        colorProfiles.put(SpidersColorProfile.getUri(),SpidersColorProfile);
    }
    
    /**
     * Get all profiles URIs.
     * 
     * @return
     */
    public String[] getAllProfilesUris() {
        return (String[])(Collections.list(colorProfiles.keys()).toArray(new String[colorProfiles.size()]));
    }
    
    /**
     * Get color profile by URI.
     * 
     * @param uri
     * @return
     */
    public SpidersColorProfile getColorProfileByUri(String uri) {
        return (SpidersColorProfile)colorProfiles.get(uri);
    }

    /**
     * Set current profile.
     * 
     * @param uri
     */
    public void setCurrentProfile(String uri) {
        currentProfile=(SpidersColorProfile)colorProfiles.get(uri);
        if(uri!=null && currentProfile!=null) {
            return;
        } else {
            currentProfile=defaultProfile;
        }
    }
    
    /**
     * Get current color profile.
     * 
     * @return
     */
    public SpidersColorProfile getCurrentProfile() {
        if(currentProfile==null) {
            return defaultProfile;
        } else {
            return currentProfile;
        }
    }
}
