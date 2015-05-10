package com.mindcognition.mindraider.integration.mindforger;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import javax.swing.JOptionPane;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.ui.dialogs.ProgressDialogJFrame;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.google.gdata.client.GoogleAuthTokenFactory;
import com.google.gson.Gson;
import com.mindcognition.mindraider.MindRaiderException;
import com.mindcognition.mindraider.export.Atomizer;
import com.mindcognition.mindraider.integration.mindforger.beans.RestOutlinesListBean;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;
import com.mindcognition.mindraider.utils.Utils;

public class MindForgerClient {
    private static final Logger logger = Logger.getLogger(MindForgerClient.class);

    private static final boolean DEVELOPMENT_MODE=false;
    
    private String APPENGINE_INSTANCE="mind-forger.appspot.com";

    private static final String CONTENT_TYPE_ATOM= "application/atom+xml; charset=utf8";
    private static final String ENCODING_UTF_8 = "utf8";

    private String baseUrl;
    private String authenticationCookie;
    
    public MindForgerClient() {
        final String gaeBaseUrlProperty = System.getProperty("gaeBaseUrl");
        if(gaeBaseUrlProperty!=null) {
            APPENGINE_INSTANCE=gaeBaseUrlProperty;
        }
    }

    private void authenticateAtLocalhost() throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

        HttpPost httpPost = new HttpPost("http://localhost:8888/_ah/login");
        httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
        String email = URLEncoder.encode("test@example.com", "UTF-8");
        String redirectUrl = URLEncoder.encode("http://localhost:8888", "UTF-8");
        httpPost.setEntity(new StringEntity("email=" + email + "&continue=" + redirectUrl + "&isAdmin=on"));
        HttpResponse response = httpClient.execute(httpPost);

        authenticationCookie = response.getFirstHeader("Set-Cookie").getValue();
        httpClient.getConnectionManager().shutdown();

        baseUrl="http://localhost:8888/rest";       
    }
            
    private void authenticateAtAppEngine(MindForgerCredentials credentials) throws Exception {
        logger.debug("GAE instance: "+APPENGINE_INSTANCE);
        String serviceName = "ah";

        GoogleAuthTokenFactory factory = new GoogleAuthTokenFactory(serviceName, "", null);
        // obtain authentication token from Google Accounts
        String authenticationToken = factory.getAuthToken(credentials.username, credentials.password, null, null, serviceName, "");

        String serviceUrl = "https://" +
                APPENGINE_INSTANCE +
                "/index.jsp";
        String loginUrl = "https://" +
                APPENGINE_INSTANCE +
                "/_ah/login?continue=" +
                URLEncoder.encode(serviceUrl, "UTF-8") + "&auth=" + authenticationToken;

        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
        HttpGet httpget = new HttpGet(loginUrl);
        HttpResponse response = httpclient.execute(httpget);

        // process response if needed (I just needed to login)

        // When the login service sends a redirect after successful login, 
        // it also returns a cookie that allows the client to finally access 
        // the protected App Engine service at https://example.appspot.com/example. 
        // The redirect and cookie handling is done by the httpclient automatically. 
        // For the duration of the session the protected App Engine service can be 
        // accessed with that cookie.

        authenticationCookie = response.getFirstHeader("Set-Cookie").getValue();
        if(authenticationCookie==null || "".equals(authenticationCookie)) {
            throw new RuntimeException("Error: Unable to obtain authentication cookie from App Engine");
        } else {
            logger.debug("Authenticated: "+authenticationCookie);
            baseUrl="https://" +
                    APPENGINE_INSTANCE +
                    "/rest";        
        }

        httpclient.getConnectionManager().shutdown();
    }

    private void importMindRaiderAtomFeeds(String feedDirectoryString, String fileMask) throws Exception {
        logger.debug("Importing Atom feeds from: "+feedDirectoryString);
        File feedDirectory=new File(feedDirectoryString);
        String importUrl=baseUrl+"/user/outlines";         
        logger.debug("    from: "+feedDirectory.getAbsolutePath()+" to "+importUrl);
        if(feedDirectory.exists()) {
            String[] atomFeeds = feedDirectory.list();
            if(atomFeeds!=null && atomFeeds.length>0) {
                for (int j = 0; j < atomFeeds.length; j++) {
                    if(atomFeeds[j]!=null && atomFeeds[j].endsWith("atom.xml")) {
                        if(fileMask!=null && !atomFeeds[j].startsWith(fileMask)) {
                            StatusBar.show("Skipping "+atomFeeds[j]);
                            continue;
                        }
                        String stringPage = feedDirectory+File.separator+atomFeeds[j];
                        logger.debug("      "+stringPage);
                        logger.debug(" POST");
                        StatusBar.show("Uploading Outline...");
                        HttpResponse response;
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost post = new HttpPost(importUrl);
                        post.addHeader("Cookie", authenticationCookie);
                        // OK stream-based version
                        File file = new File(stringPage);
                        FileInputStream fileInputStream = new FileInputStream(file);
                        // set file length to avoid chunked encoding (use -1 to enforce it) > GAE doens't like -1
                        InputStreamEntity inputStreamEntity = new InputStreamEntity(fileInputStream, file.length());
                        inputStreamEntity.setContentEncoding(ENCODING_UTF_8);
                        inputStreamEntity.setContentType(CONTENT_TYPE_ATOM);
                        post.setEntity(inputStreamEntity);

                        response = httpClient.execute(post);
                        StatusLine statusLine = response.getStatusLine();
                        StatusBar.show("Outline uploaded! (finished with "+statusLine+")");
                        if(statusLine!=null && statusLine.getStatusCode()==409) {
                            JOptionPane.showMessageDialog(OutlineJPanel.getInstance().getComponent(0),
                                    "Outline already exists in MindForger. Delete it there before upload please.",
                                    "Conflict",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        if(fileMask!=null) {
                            return;
                        }
                    } else {
                        StatusBar.show("Skipping '"+atomFeeds[j]+"' - wrong extension.");
                    }
                }                   
            } else {
                StatusBar.show("      No pages!");
            }
        } else {
            throw new RuntimeException(feedDirectory.getAbsolutePath()+" doesn't exist!");
        }
        StatusBar.show("Import finished!");
    }

    public void uploadOutline(String directory, String file, String username, String password, Component parentUiComponent) throws Exception {
        if(DEVELOPMENT_MODE) {
            authenticateAtLocalhost();
        } else {
            authenticateAtAppEngine(new MindForgerCredentials(username, password));            
        }
        
        importMindRaiderAtomFeeds(directory, file);
    }

    public ResourceDescriptor[] downloadOutlinesList(String username, String password, Component parentUiComponent) throws Exception {
        if(DEVELOPMENT_MODE) {
            authenticateAtLocalhost();            
        } else {
            authenticateAtAppEngine(new MindForgerCredentials(username, password));            
        }

        String importUrl=baseUrl+"/user/outlines";         
        
        StatusBar.show("Downloading list of your outlines from MindForger - "+importUrl);
        HttpResponse response;
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(importUrl);
        get.addHeader("Cookie", authenticationCookie);
        
        response = httpClient.execute(get);
        
        // show what's downloaded
        //System.out.println(Utils.inputStreamToString(response.getEntity().getContent()));
        
        InputStreamReader inputStreamReader = new InputStreamReader(response.getEntity().getContent(),"UTF-8");                        

        StatusLine statusLine = response.getStatusLine();
        StatusBar.show("Finished with "+statusLine);
        if(statusLine!=null && statusLine.getStatusCode()==409) {
            JOptionPane.showMessageDialog(OutlineJPanel.getInstance().getComponent(0),
                    "Outline already exists in MindForger. Delete it there before upload please.",
                    "Conflict",
                    JOptionPane.ERROR_MESSAGE);
        }
        
        Gson gson=new Gson();
        RestOutlinesListBean restOutlinesListBean = gson.fromJson(inputStreamReader, RestOutlinesListBean.class);
                
        if(restOutlinesListBean!=null && restOutlinesListBean.getOutlines()!=null) {
            ResourceDescriptor[] result=new ResourceDescriptor[restOutlinesListBean.getOutlines().length];
            for (int i = 0; i < restOutlinesListBean.getOutlines().length; i++) {
                result[i]=new ResourceDescriptor(restOutlinesListBean.getOutlines()[i].getTitle(), restOutlinesListBean.getOutlines()[i].getKey());
            }
            return result;
        } else {
            return new ResourceDescriptor[0];
        }
    }

    public String downloadOutline(
            String mindForgerOutlineKey,
            String mindForgerUsername,
            String mindForgerPassword,
            ProgressDialogJFrame parentUiComponent) {

        if(mindForgerOutlineKey==null) {
            throw new MindRaiderException("Unable to determine Outline's MindForger ID!");
        }

        try {
            if(DEVELOPMENT_MODE) {
                authenticateAtLocalhost();                
            } else {
                authenticateAtAppEngine(new MindForgerCredentials(mindForgerUsername, mindForgerPassword));                
            }

            String importUrl=baseUrl+"/user/outlines/"+mindForgerOutlineKey+"?alt=application/atom%2Bxml";         

            StatusBar.show("Downloading outline from MindForger...");
            HttpResponse response;
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(importUrl);
            get.addHeader("Cookie", authenticationCookie);

            response = httpClient.execute(get);
            InputStreamReader inputStreamReader = new InputStreamReader(response.getEntity().getContent(),"UTF-8");                        
            StatusLine statusLine = response.getStatusLine();
            StatusBar.show("Outline downloaded! (finished with "+statusLine+")");
            //        if(statusLine!=null && statusLine.getStatusCode()==409) {
            //            JOptionPane.showMessageDialog(OutlineJPanel.getInstance().getComponent(0),
            //                    "Outline already exists in MindForger. Delete it there before upload please.",
            //                    "Conflict",
            //                    JOptionPane.ERROR_MESSAGE);
            //        }

            return Atomizer.from(inputStreamReader, parentUiComponent);
        } catch(Exception e) {
            logger.error("Unable to download outline", e);
            return null;
        }
    }   
}

