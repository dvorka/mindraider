/*
 ===========================================================================
   Copyright 2002-2011 Martin Dvorak

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
package com.mindcognition.mindraider;

import java.io.File;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.kernel.Commander;
import com.emental.mindraider.ui.frames.MindRaiderMainWindow;
import com.mindcognition.mindraider.commons.MindRaiderConstants;

/**
 * Mind Raider.
 * 
 *  Usage: mindraider [-h] [-d] [-p <url>] [-t <twiki file to import>]
 *  Options:
 *   h ... help
 *   t ... remote import TWiki file (MR must be already running)
 *   p ... directory where to search for profile
 *   d ... print out debug logs
 *  Example:
 *   mindraider -h
 *   mindraider -d
 *   mindraider -p /home/dvorka/mr/development
 *   
 *  System properties:
 *   gaeBaseUrl ... GAE instance to be used for upload/download
 *                  example: -DgaeBaseUrl=web.mindforger.com
 */
public class MindRaiderApplication extends javax.swing.JPanel {

    /**
     * The cr constant.
     */
    private static final String cr = "\n";

    /**
     * The blank char constant.
     */
    private static final String blank = " ";

    /**
     * The tab constant.
     */
    private static final String tab = "     ";

    /**
     * The TWiki import command.
     */
    public static final String COMMAND_TWIKI_IMPORT = "TWIKI_IMPORT "; //$NON-NLS-1$

    /**
     * The help argument.
     */
    public static final String ARG_HELP = "-h"; //$NON-NLS-1$

    /**
     * Debug argument.
     */
    public static final String ARG_DEBUG = "-d"; //$NON-NLS-1$

    /**
     * The profile argument.
     */
    public static final String ARG_PROFILE = "-p"; //$NON-NLS-1$

    /**
     * The TWiki import argument.
     */
    public static final String ARG_TWIKI_IMPORT = "-t"; //$NON-NLS-1$

    /**
     * serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * MindRaider application uses its own bundle, because profile is not loaded
     * yet and therefore user-specific settings can not be applied.
     */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle("com.mindcognition.mindraider.MindRaiderApplication");

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(MindRaiderApplication.class);

    /**
     * The main procedure.
     * 
     * @param args
     *            program arguments.
     */
    public static void main(String[] args) {
        boolean help = false;
        boolean debugMode = false;
        
        // analyze arguments
        if (args != null) {
            // while there are some arguments try to consume them
            int i = 0;
            while (i < args.length) {
                if (ARG_HELP.equals(args[i]) || "--help".equals(args[i]) //$NON-NLS-1$
                        || "/h".equals(args[i]) || "/?".equals(args[i])) { //$NON-NLS-1$ //$NON-NLS-2$
                    help = true;
                } else {
                    if (ARG_PROFILE.equals(args[i])) {
                        // take next arg - there is path to the profile
                        i++;
                        MindRaider.profilesDirectory = args[i];
                    } else {
                        if (ARG_TWIKI_IMPORT.equals(args[i])) {
                            // take next arg - there is path to the twiki file
                            // to be imported
                            i++;
                            String command = args[i];
                            if (StringUtils.isNotEmpty(command)) {
                                new Commander(COMMAND_TWIKI_IMPORT + command);
                            } else {
                                System.err
                                        .println(MindRaiderApplication
                                                .getString("MindRaiderApplication.0") //$NON-NLS-1$
                                                + command
                                                + MindRaiderApplication
                                                        .getString("MindRaiderApplication.1")); //$NON-NLS-1$
                            }
                            System.exit(0);
                        } else {
                            if (ARG_DEBUG.equals(args[i])) {
                                debugMode = true;
                            } else {
                                System.out
                                        .println(MindRaiderApplication
                                                .getString("MindRaiderApplication.11") + args[i]); //$NON-NLS-1$
                                help = true;
                            }
                        }
                    }
                }
                i++;
            }

            // process arguments
            if (help) {
                help();
                return;
            }
        }

        // reset log4j configuration
        try {
            BasicConfigurator.resetConfiguration();
            if (debugMode) {
                PropertyConfigurator.configure(System
                        .getProperty("log4j.configuration.debug")); //$NON-NLS-1$
            } else {
                String log4jconfig = System
                        .getProperty("log4j.configuration");
                PropertyConfigurator.configure(log4jconfig); //$NON-NLS-1$
            }
        } catch (Throwable e) {
            logger.debug(getString(MindRaiderApplication
                    .getString("MindRaiderApplication.2"))); //$NON-NLS-1$
            BasicConfigurator.resetConfiguration();
        }

        logger.debug(MindRaider.getTitle());

        String javaVersion;
        try {
            javaVersion = System.getProperty("java.version"); //$NON-NLS-1$
        } catch (NullPointerException e) {
            javaVersion = ""; //$NON-NLS-1$
        }

        logger.debug(getString("MindRaiderApplication.javaVersion",
                new Object[] { javaVersion, System.getProperty("java.vendor"),
                        System.getProperty("java.home") }));
        if (javaVersion.compareTo("1.1.2") < 0) {
            logger.debug(getString("MindRaiderApplication.swing112version"));
            return;
        }

        // initialization
        if(MindRaiderConstants.EARLY_ACCESS) {
            MindRaider.setUser(System.getProperty("user.name"), System
                    .getProperty("user.home")+File.separator+MindRaiderConstants.MR+"-eap"); //$NON-NLS-1$
        } else { 
            MindRaider.setUser(System.getProperty("user.name"), System //$NON-NLS-1$
                    .getProperty("user.home")); //$NON-NLS-1$
        }
        
        MindRaider.setInstallationDirectory(System.getProperty("user.dir")); //$NON-NLS-1$

        logger.debug(getString("MindRaiderApplication.installationDirectory",
                MindRaider.installationDirectory));
        logger.debug(getString("MindRaiderApplication.profileName",
                MindRaider.user.getName()));
        logger.debug(getString("MindRaiderApplication.userHome",
                MindRaider.user.getHome()));

        MindRaider.eapProfilesDirectory 
            = System.getProperty("user.home")+File.separator+MindRaiderConstants.MR+"-eap"+
              File.separator+".mindraider.profile.eap";
        
        // set profile
        if (MindRaider.profilesDirectory == null) {
            if(MindRaiderConstants.EARLY_ACCESS) {
                MindRaider.profilesDirectory = MindRaider.eapProfilesDirectory;
            } else {
                MindRaider.profilesDirectory = MindRaider.user.getHome()
                + File.separator + ".mindraider.profile"; //$NON-NLS-1$
            }
        }
        MindRaider.setMainJFrame(MindRaiderMainWindow.getInstance());
    }

    /**
     * Help.
     */
    public static void help() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(MindRaider.getTitle() + cr); //$NON-NLS-1$
        buffer.append(
                MindRaiderApplication.getString("MindRaiderApplication.3") + 
                " " + 
                MindRaiderConstants.MR + 
                " " + 
                MindRaiderApplication.getString("MindRaiderApplication.4") + cr); //$NON-NLS-1$
        buffer.append(MindRaiderApplication.getString("MindRaiderApplication.5") + cr); //$NON-NLS-1$
        buffer.append(
                blank+ 
                MindRaiderApplication.getString("MindRaiderApplication.6") + cr); //$NON-NLS-1$
        buffer.append(
                blank+ 
                MindRaiderApplication.getString("MindRaiderApplication.7") + cr); //$NON-NLS-1$
        buffer.append(
                blank+ 
                MindRaiderApplication.getString("MindRaiderApplication.8") + cr); //$NON-NLS-1$
        buffer.append(
                blank+
                MindRaiderApplication.getString("MindRaiderApplication.debug") + cr); //$NON-NLS-1$
        buffer.append(
                MindRaiderApplication.getString("MindRaiderApplication.9") + cr); //$NON-NLS-1$
        buffer.append(
                tab + 
                MindRaiderConstants.MR + " -h" + cr); //$NON-NLS-1$
        buffer.append(
                tab + 
                MindRaiderConstants.MR + " -d" + cr); //$NON-NLS-1$
        buffer.append(
                tab + MindRaiderConstants.MR + " -p /home/dvorka/mr/development" + cr); //$NON-NLS-1$
        buffer.append(""); //$NON-NLS-1$
        System.out.println(buffer.toString());
    }

    /**
     * Debug.
     */
    public static void debug() {
        Properties properties = System.getProperties();
        Enumeration<Object> propertyNames = properties.keys();

        if (properties.size() > 0) {
            logger.debug(" Properties debug (" + properties.size() + "):"); //$NON-NLS-1$ //$NON-NLS-2$
            String element = null;
            while (propertyNames.hasMoreElements()) {
                element = (String) propertyNames.nextElement();
                if (element.startsWith(MindRaiderConstants.MR)) {
                    logger.debug("  " + element + ": " //$NON-NLS-1$ //$NON-NLS-2$
                            + System.getProperty(element));
                }
            }
            logger.debug(" - done -"); //$NON-NLS-1$
        }
    }

    /**
     * Returns the message string for given key.
     * 
     * @param key
     *            the key.
     * @return Returns the message.
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * Returns the message string for given key and parameters.
     * 
     * @param key
     *            the key.
     * @param params
     *            the parameters array.
     * @return Returns the message.
     */
    public static String getString(String key, Object[] params) {
        try {
            return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * Returns the message string for given key and parameter.
     * 
     * @param key
     *            the key.
     * @param param
     *            the parameter
     * @return Returns the message.
     */
    public static String getString(String key, Object param) {
        return getString(key, new Object[] { param });
    }
}
