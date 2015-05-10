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
package com.emental.mindraider.ui.editors.color;

import java.util.Collections;
import java.util.Hashtable;

public class AnnotationColorProfileRegistry {

    /**
     * Color profiles.
     */
    private Hashtable<String,AnnotationColorProfile> colorProfiles=new Hashtable<String,AnnotationColorProfile>();
    
    /**
     * Default profile.
     */
    private AnnotationColorProfile defaultProfile;

    /**
     * Current profile.
     */
    private AnnotationColorProfile currentProfile;

    /**
     * Constructor.
     */
    public  AnnotationColorProfileRegistry() {
        AnnotationColorProfile white=new WhiteAnnotationColorProfile();
        register(white);
        BlackAnnotationColorProfile black = new BlackAnnotationColorProfile();
        register(black);
        register(new BlueAnnotationColorProfile());
        register(new YellowAnnotationColorProfile());
        
        defaultProfile=currentProfile=white;
    }
    
    /**
     * Register profile.
     * 
     * @param annotationColorProfile
     */
    public void register(AnnotationColorProfile annotationColorProfile) {
        colorProfiles.put(annotationColorProfile.getUri(),annotationColorProfile);
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
    public AnnotationColorProfile getColorProfileByUri(String uri) {
        return (AnnotationColorProfile)colorProfiles.get(uri);
    }

    /**
     * Set current profile.
     * 
     * @param uri
     */
    public void setCurrentProfile(String uri) {
        currentProfile=(AnnotationColorProfile)colorProfiles.get(uri);
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
    public AnnotationColorProfile getCurrentProfile() {
        if(currentProfile==null) {
            return defaultProfile;
        } else {
            return currentProfile;
        }
    }
}
