/* ===========================================================================
   Mind Raider - Semantic Web outliner
   Copyright 2010 Martin Dvorak

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
package com.emental.mindraider.core;

import java.awt.Color;

import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import com.emental.mindraider.core.history.History;
import com.emental.mindraider.core.kernel.RemoteCommandDaemon;
import com.emental.mindraider.core.rdf.RdfCustodian;
import com.emental.mindraider.core.search.SearchCommander;
import com.emental.mindraider.ui.editors.color.AnnotationColorProfileRegistry;
import com.emental.mindraider.ui.frames.MindRaiderMainWindow;
import com.emental.mindraider.ui.graph.spiders.SpidersGraph;
import com.emental.mindraider.ui.graph.spiders.color.SpidersColorProfileRegistry;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.emental.mindraider.ui.outline.treetable.OutlineTreeInstance;
import com.mindcognition.mindraider.application.model.label.LabelCustodian;
import com.mindcognition.mindraider.application.model.note.NoteCustodian;
import com.mindcognition.mindraider.application.model.outline.OutlineCustodian;
import com.mindcognition.mindraider.application.model.tag.TagCustodian;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.commons.profile.Profile;
import com.mindcognition.mindraider.commons.profile.ProfileCustodian;
import com.mindcognition.mindraider.install.Installer;
import com.mindcognition.mindraider.ui.swing.LookAndFeel;
import com.mindcognition.mindraider.ui.swing.concept.ConceptJPanel;
import com.mindcognition.mindraider.ui.swing.main.MasterToolBar;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;
import com.mindcognition.mindraider.ui.swing.recent.RecentConceptsTree;

/**
 * MindRaider kernel holds the state of application.
 */
public class MindRaider {
    private static final Logger cat = Logger.getLogger(MindRaider.class);

    /*
     * Look & Feels
     */

    // native
    public static final String LF_NATIVE = "native";

    // java: default
    public static final String LF_JAVA_DEFAULT = "default java";

    /**
     * The user profile.
     */
    public static User user;

    /**
     * The installation directory.
     */
    public static String installationDirectory;

    /**
     * The profile registry.
     */
    public static ProfileCustodian profileRegistry;

    /**
     * The profile.
     */
    public static Profile profile;

    /**
     * The profiles home.
     */
    public static String profilesDirectory;
    public static String eapProfilesDirectory;

    /**
     * The spiders graph.
     */
    public static SpidersGraph spidersGraph;

    /**
     * The application JFrame.
     */
    public static MindRaiderMainWindow mainJFrame;

    /**
     * The master tool bar.
     */
    public static MasterToolBar masterToolBar;

    /**
     * The folder custodian.
     */
    public static LabelCustodian labelCustodian;

    /**
     * The notebook custodian.
     */
    public static OutlineCustodian outlineCustodian;

    /**
     * The concept custodian.
     */
    public static NoteCustodian noteCustodian;

    /**
     * The model custodian.
     */
    public static RdfCustodian rdfCustodian;

    /**
     * The RDF/XML notebook editor area.
     */
    public static JTextArea notebookRdfXmlViewer;

    /**
     * The RDF/XML folders editor area.
     */
    public static JTextArea foldersRdfXmlViewer;

    /**
     * The show discarded resources.
     */
    public static boolean showDiscardedResources;

    /**
     * The notebooks history.
     */
    public static History history;

    /**
     * <code>true</code> if running on Windows operating system (launcher need
     * to know that).
     */
    public static boolean windowsOs;
    
    /** 
     * MindForder credentials that are kept just in memory and lost on application exit.
     */
    public static String mindForgerUsername;
    public static String mindForgerPassword;
    
    public static RecentConceptsTree recentConcepts;
    
    /*
     * color profiles
     */
    
    public static AnnotationColorProfileRegistry annotationColorProfileRegistry
        =new AnnotationColorProfileRegistry();

    public static SpidersColorProfileRegistry spidersColorProfileRegistry
        =new SpidersColorProfileRegistry();

    public static TagCustodian tagCustodian;

    /**
     * Basic initialization.
     */
    public static void preSetProfiles() {
        showDiscardedResources = false;
                
        tagCustodian=new TagCustodian();

        // providers
        profileRegistry = new ProfileCustodian(profilesDirectory);
        StatusBar.getStatusBar();
        profile = MindRaider.profileRegistry.getProfile(user.name);
        
        // start remote command daemon
        if (profile.isEnableCommandDaemon()) {
            new RemoteCommandDaemon();
        }

        MindRaider.profile.setGraphShowLabelsAsUris(false);
        MindRaider.profile.setGraphHidePredicates(true);
        MindRaider.profile.setGraphMultilineLabels(false);
        
        // set UI according to profile preferences
        LookAndFeel.setUpUI();
        // reinitialize status bar according to UI preferences
        StatusBar.reinitializeStatusBar();

        spidersGraph = new SpidersGraph(spidersColorProfileRegistry.getCurrentProfile());
    }

    /**
     * Initialize profiles.
     */
    public static void setProfiles() {
        cat.debug("Initializing profile...");

        // notebooks RDF Model viewer
        notebookRdfXmlViewer = new JTextArea();
        notebookRdfXmlViewer.setEnabled(false);
        notebookRdfXmlViewer.setBackground(Color.BLACK);
        notebookRdfXmlViewer.setForeground(Color.WHITE);
        notebookRdfXmlViewer.setFont(ConceptJPanel.TEXTAREA_FONT);
        notebookRdfXmlViewer.setCaretColor(Color.RED);
        notebookRdfXmlViewer.setToolTipText("Save with Ctrl-S");
        // folders RDF Model viewer
        foldersRdfXmlViewer = new JTextArea();
        foldersRdfXmlViewer.setEnabled(false);
        foldersRdfXmlViewer.setBackground(Color.BLACK);
        foldersRdfXmlViewer.setForeground(Color.WHITE);
        foldersRdfXmlViewer.setFont(ConceptJPanel.TEXTAREA_FONT);
        foldersRdfXmlViewer.setCaretColor(Color.RED);
        foldersRdfXmlViewer.setToolTipText("Save with Ctrl-S");

        // objects initialization
        labelCustodian = new LabelCustodian(profile.getFoldersDirectory());
        outlineCustodian = new OutlineCustodian(profile
                .getNotebooksDirectory());
        noteCustodian = new NoteCustodian(profile.getNotebooksDirectory());

        rdfCustodian = new RdfCustodian();
        tagCustodian.fromRdf();

        noteCustodian.subscribe(OutlineTreeInstance.getInstance());

        labelCustodian.initialize();

        history = new History();
        masterToolBar.refreshHistory();
    }

    /**
     * Post initialization.
     */
    public static void postSetProfiles() {
        // do UPGRADE if it is desired
        Installer.upgrade();
        // rebuild search index (if desired)
        SearchCommander.initialize();
        
        // try to load previous notebook
        boolean loaded;
        try {
            loaded=outlineCustodian.loadOutline(
                    MindRaider.profile.getActiveOutline());
        } catch (Exception e) {
            cat.warn("Unable to load previous notebook!", e);
            loaded=false;
        }
        
        // TODO if not notebook, try to load model, etc.
        
        if(!loaded) {
            profile.setActiveOutline(null);
            profile.setActiveOutlineUri(null);
        } else {
            OutlineJPanel.getInstance().refresh();
        }
    }

    /**
     * Initialize user descriptor.
     *
     * @param userName
     *            the user name
     * @param userHome
     *            the user home
     */
    public static void setUser(String userName, String userHome) {
        user = new User(userName, userHome);
    }

    /**
     * Set the installation directory.
     *
     * @param installationDirectory
     *            the installation directory
     */
    public static void setInstallationDirectory(String installationDirectory) {
        MindRaider.installationDirectory = installationDirectory;
    }

    /**
     * Setter for <code>mainJFrame</code>.
     *
     * @param mainJFrame
     *            the main JFrame
     */
    public static void setMainJFrame(MindRaiderMainWindow mainJFrame) {
        MindRaider.mainJFrame = mainJFrame;
    }

    /**
     * Setter for <code>masterToolBar</code>
     *
     * @param masterToolBar
     *            the master tool bar
     */
    public static void setMasterToolBar(MasterToolBar masterToolBar) {
        MindRaider.masterToolBar = masterToolBar;
    }

    /**
     * The User class.
     */
    public static class User {

        /**
         * The name property.
         */
        private String name;

        /**
         * The home propery
         */
        private String home;

        /**
         * Constructor.
         *
         * @param name
         *            the user name
         * @param home
         *            the user home
         */
        public User(String name, String home) {
            this.name = name;
            this.home = home;
        }

        /**
         * Getter for <code>home</code>.
         *
         * @return Returns the home.
         */
        public String getHome() {
            return this.home;
        }

        /**
         * Setter for <code>home</code>.
         *
         * @param home
         *            The home to set.
         */
        public void setHome(String home) {
            this.home = home;
        }

        /**
         * Getter for <code>name</code>.
         *
         * @return Returns the name.
         */
        public String getName() {
            return this.name;
        }

        /**
         * Setter for <code>name</code>.
         *
         * @param name
         *            The name to set.
         */
        public void setName(String name) {
            this.name = name;
        }

    }

    public static String getTitle() {
        return MindRaiderConstants.MR_TITLE + 
               " "+
               MindRaiderConstants.majorVersion + "." +
               MindRaiderConstants.minorVersion + " - Personal Notebook and Outliner";
    }

    /**
     * Get version.
     *
     * @return string representation of the version.
     */
    public static String getVersion() {
        return MindRaiderConstants.majorVersion + "."
                + MindRaiderConstants.minorVersion;
    }
}
