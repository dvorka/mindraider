/*
 * Created on Nov 10, 2011
 */
package com.mindcognition.mindraider.integration.mindforger;

public class MindForgerCredentials {
    public String username;
    public String password;
    
    public MindForgerCredentials(String username, String password) {
        this.username=username;
        this.password=password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
