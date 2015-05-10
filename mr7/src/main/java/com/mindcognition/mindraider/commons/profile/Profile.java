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
import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.core.rdf.RdfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.mindcognition.mindraider.application.model.outline.OutlineCustodian;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.utils.BrowserLauncher;
import com.mindcognition.mindraider.utils.Launcher;

/**
 * Profile holds configuration informations for particular user. It is identified using her/his login name. It is formed
 * by:
 * 
 * <ul>
 * <li>filesystem location of user outlines
 * <li>last opened outline
 * <li>model color profile for graphs (Spiders)
 * <li>... and more
 * </ul>
 * 
 * To initialize the profile either create a brand new profile using the constructor OR specify the profile filesystem
 * location and then load() it.
 * 
 * Once the profile is loaded, it is kept in the memory and setters/getters are used to modify the profile values (profile
 * class is manipulated as POJO bean). After modifying the fields, save() should be called to persistently store the profile.
 */
public class Profile {
    private static final Logger cat = Logger.getLogger(Profile.class);

    private static final String LAUNCHERS = "launchers";

    private Model model;
    private String activeModel;

    // configuration
    private String profileName;
    private String hostname;
    private String foldersDirectory;
    private String notebooksDirectory;
    private URI activeNotebookUri;
    public String version;
    private String profileLocation;
    private String homeDirectory;
    private String homeNotebook;
    private String laucherLocal;
    private String launcherWeb;
    private String lookAndFeel;
    private String uiPerspective;
    private boolean enableCommandDaemon = false;
    private boolean graphShowLabelsAsUris;
    private boolean graphMultilineLabels;
    private boolean graphHidePredicates = true;
    private boolean enableGnowsisSupport = false;
    private boolean enableSpiders = true;
    private boolean overrideSystemLocale = false;
    private String customLocale;
    
    private int graphLookAhead;
    private int graphZoom;
    private int graphRotate;
    private int graphHyperbolic;
    private String graphColorScheme;
    
    /**
     * Create brand new profile.
     *
     * @param profileLocation
     * @param profileName
     * @param homeDirectory
     * @param hostname
     */
    public Profile(String profileLocation, String profileName, String homeDirectory, String hostname) {
        Resource resource;
        model = ModelFactory.createDefaultModel();

        this.profileLocation = profileLocation;
        this.profileName = profileName;

        // profile name has type of e:profile
        // TODO this is wrong - it wont be unique, here must be mangled also
        // hostname to make profile resource unique
        resource = model.createResource(MindRaiderConstants.MR_RDF_NS + profileName);
        resource.addProperty(RDF.type, model.createResource(MindRaiderConstants.MR_OWL_CLASS_PROFILE));

        // mind raider version
        version = MindRaiderConstants.majorVersion + "." + MindRaiderConstants.minorVersion;
        resource.addProperty(MindRaiderVocabulary.mrVersion, model.createLiteral(version));

        // resource directory
        resource.addProperty(MindRaiderVocabulary.hostname, model.createLiteral(hostname));
        // resource directory
        resource.addProperty(MindRaiderVocabulary.homeDirectory, model.createLiteral(homeDirectory
            + MindRaiderConstants.MR_DIRECTORY));
        // folders directory
        resource.addProperty(MindRaiderVocabulary.foldersDirectory, model.createLiteral(homeDirectory
            + MindRaiderConstants.MR_DIR_FOLDERS));
        // notebooks directory
        resource.addProperty(MindRaiderVocabulary.notebooksDirectory, model.createLiteral(homeDirectory
            + MindRaiderConstants.MR_DIR_NOTEBOOKS));

        // home notebook
        if (MindRaider.outlineCustodian != null) {
            try {
                homeNotebook = MindRaider.outlineCustodian
                    .getOutlineUriByLocalName(OutlineCustodian.MR_DOC_NOTEBOOK_DOCUMENTATION_LOCAL_NAME);
                resource.addProperty(MindRaiderVocabulary.homeNotebook, model.createLiteral(homeNotebook));
            }
            catch (Exception e) {
                cat.debug("Unable to determine help notebook uri: ", e);
            }
        }

        /*
         * graph settings
         */
        resource.addProperty(MindRaiderVocabulary.graphRenderLabelUris, MindRaiderVocabulary.falseLiteral);
        resource.addProperty(MindRaiderVocabulary.graphHidePredicates, MindRaiderVocabulary.trueLiteral);
        resource.addProperty(MindRaiderVocabulary.graphMultilineLabels, MindRaiderVocabulary.trueLiteral);

        /*
         * daemons
         */
        resource.addProperty(MindRaiderVocabulary.enableCommandDaemon, MindRaiderVocabulary.falseLiteral);
        resource.addProperty(MindRaiderVocabulary.overrideSystemLocale, MindRaiderVocabulary.falseLiteral);
    }

    /**
     * Initialize existing profile.
     * 
     * @param profileLocation the profile location String
     */
    public Profile(String profileLocation) {
        this.profileLocation = profileLocation;
    }

    /**
     * Load profile.
     */
    public void load() {
        Statement statement;

        try {
            model = RdfModel.loadModel(profileLocation);

            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.mrVersion);
            if (statement != null) {
                version = statement.getObject().toString();
            }

            // home directory
            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.homeDirectory);

            if (statement != null) {
                homeDirectory = statement.getObject().toString();
            }
            // hostname
            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.hostname);
            if (statement != null) {
                hostname = statement.getObject().toString();
            }

            // folders directory
            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.foldersDirectory);
            if (statement != null) {
                foldersDirectory = statement.getObject().toString();
            }

            // notebooks directory
            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.notebooksDirectory);
            if (statement != null) {
                notebooksDirectory = statement.getObject().toString();
            }

            // profile name TODO probably wrong - should be used for classes
            statement = RdfModel.getStatementByPredicate(model, RDF.type);
            if (statement != null) {
                profileName = statement.getSubject().getLocalName();
            }

            // active notebook TODO
            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.activeNotebook);
            if (statement != null) {
                activeNotebookUri = new URI(statement.getObject().toString());
            }

            // active model
            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.activeModel);
            if (statement != null) {
                activeModel = statement.getObject().toString();
            }

            // home notebook
            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.homeNotebook);
            if (statement != null) {
                homeNotebook = statement.getObject().toString();
            }
            if (homeNotebook != null && "".equals(homeNotebook)) {
                homeNotebook = null;
            }

            /*
             * ui
             */
            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.lookAndFeel);
            if (statement != null)
                lookAndFeel = statement.getObject().toString();

            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.uiPerspective);
            if (statement != null)
                uiPerspective = statement.getObject().toString();

            /*
             * graph
             */
            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.graphHidePredicates);
            if (statement != null) {
                graphHidePredicates = statement.getLiteral().getBoolean();
            }

            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.graphMultilineLabels);
            if (statement != null) {
                graphMultilineLabels = statement.getLiteral().getBoolean();
            }

            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.graphRenderLabelUris);
            if (statement != null) {
                graphShowLabelsAsUris = statement.getLiteral().getBoolean();
            }

            /*
             * launchers - if not set in the RDF profile, load defaults from corresponding classes
             */
            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.launcherLocal);
            if (statement != null) {
                laucherLocal = statement.getLiteral().getString();
                setLocalLauncherCommand(laucherLocal);
            }

            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.launcherWeb);
            if (statement != null) {
                launcherWeb = statement.getLiteral().getString();
                setWebLauncherPath(launcherWeb);
            }

            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.enableCommandDaemon);
            if (statement != null) {
                enableCommandDaemon = statement.getLiteral().getBoolean();
            }

            /*
             * locale
             */

            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.overrideSystemLocale);
            if (statement != null) {
                overrideSystemLocale = statement.getLiteral().getBoolean();
            }

            statement = RdfModel.getStatementByPredicate(model, MindRaiderVocabulary.customLocale);
            if (statement != null) {
                customLocale = statement.getLiteral().getString();
            }

        }
        catch (Exception e) {
            cat.error("Unable to load profile!", e);
        }
    }

    /**
     * Save profile.
     * 
     * @return <code>true</code> if successful.
     */
    public boolean save() {
        setGraphProperties();
        setLauncherProperties();
        setUiProperties();
        return RdfModel.saveModel(model, profileLocation);
    }
    
    /**
     * Set UI properties to RDF model based on the fields of this class.
     */
    private void setUiProperties() {
        // delete predicates
        RdfModel.deleteStatementByPredicate(model, MindRaiderVocabulary.uiPerspective);
        RdfModel.deleteStatementByPredicate(model, MindRaiderVocabulary.lookAndFeel);

        // set actual values
        Resource resource = model.getResource(MindRaiderConstants.MR_RDF_NS + profileName);
        if (resource != null) {
            if(uiPerspective!=null) {
                resource.addProperty(MindRaiderVocabulary.uiPerspective,uiPerspective);
            }
            if(lookAndFeel!=null) {
                resource.addProperty(MindRaiderVocabulary.lookAndFeel,lookAndFeel);
            }
        }
    }

    /**
     * Set graph properties to RDF model based on the fields of this class.
     */
    private void setGraphProperties() {
        // delete all
        RdfModel.deleteStatementByPredicate(model, MindRaiderVocabulary.graphHidePredicates);
        RdfModel.deleteStatementByPredicate(model, MindRaiderVocabulary.graphMultilineLabels);
        RdfModel.deleteStatementByPredicate(model, MindRaiderVocabulary.graphRenderLabelUris);

        RdfModel.deleteStatementByPredicate(model, MindRaiderVocabulary.homeNotebook);

        RdfModel.deleteStatementByPredicate(model, MindRaiderVocabulary.mrVersion);

        // set them to actual values
        Resource resource = model.getResource(MindRaiderConstants.MR_RDF_NS + profileName);
        if (resource != null) {
            resource.addProperty(MindRaiderVocabulary.graphRenderLabelUris, graphShowLabelsAsUris);
            resource.addProperty(MindRaiderVocabulary.graphHidePredicates, graphHidePredicates);
            resource.addProperty(MindRaiderVocabulary.graphMultilineLabels, graphMultilineLabels);

            resource.addProperty(MindRaiderVocabulary.mrVersion, version);

            if (homeNotebook != null && !"".equals(homeNotebook)) {
                resource.addProperty(MindRaiderVocabulary.homeNotebook, homeNotebook);
            }

            // locale settings
            RdfModel.deleteStatementByPredicate(model, MindRaiderVocabulary.overrideSystemLocale);
            resource.addProperty(MindRaiderVocabulary.overrideSystemLocale, overrideSystemLocale);
            if (overrideSystemLocale && customLocale != null) {
                RdfModel.deleteStatementByPredicate(model, MindRaiderVocabulary.customLocale);
                resource.addProperty(MindRaiderVocabulary.customLocale, customLocale);
            }
        }
    }

    /**
     * Set non-default launchers (set via properties).
     */
    private void setLauncherProperties() {
        // create launcher statements anyway
        Resource resource = model.getResource(MindRaiderConstants.MR_RDF_NS + profileName);
        Resource launchers = model.getResource(MindRaiderConstants.MR_RDF_NS + LAUNCHERS);
        resource.addProperty(MindRaiderVocabulary.launcher, launchers);

        // if non-default, set them
        if (laucherLocal != null) {
            RdfModel.deleteStatementByPredicate(model, MindRaiderVocabulary.launcherLocal);
            launchers.addProperty(MindRaiderVocabulary.launcherLocal, laucherLocal);
        }
        if (launcherWeb != null) {
            RdfModel.deleteStatementByPredicate(model, MindRaiderVocabulary.launcherWeb);
            launchers.addProperty(MindRaiderVocabulary.launcherWeb, launcherWeb);
        }

        RdfModel.deleteStatementByPredicate(model, MindRaiderVocabulary.enableCommandDaemon);
        launchers.addProperty(MindRaiderVocabulary.enableCommandDaemon, enableCommandDaemon);
    }

    /*
     * setters and getters
     */
    
    /**
     * Return the host name property.
     * @return host name String
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Return home directory property.
     * @return home directory String
     */
    public String getHomeDirectory() {
        return homeDirectory;
    }

    /**
     * Return folders directory property.
     * @return folders directory String
     */
    public String getFoldersDirectory() {
        return foldersDirectory;
    }

    /**
     * Return notebooks directory property.
     * @return notebooks directory String
     */
    public String getNotebooksDirectory() {
        return notebooksDirectory;
    }

    /**
     * Return the profile location property.
     * @return profile location String
     */
    public String getProfileLocation() {
        return profileLocation;
    }

    /**
     * Get active notebook URI.
     * @return active notebook URI.
     */
    public URI getActiveOutline() {
        return activeNotebookUri;
    }

    /**
     * Set active notebook URI.
     * @param notebookUri the notebookUri URI
     */
    public void setActiveOutline(URI notebookUri) {
        cat.debug("Setting active notebook to: " + notebookUri);
        deleteOutlineNotebook();
        deleteActiveModel();

        this.activeNotebookUri = notebookUri;
        if(notebookUri!=null) {
            model.add(
                    model.createResource(MindRaiderConstants.MR_RDF_NS + profileName),
                    MindRaiderVocabulary.activeNotebook,
                    notebookUri);
        }
        save();
    }

    /**
     * Delete active notebook.
     */
    public void deleteOutlineNotebook() {
        StmtIterator i = model.listStatements((Resource) null, MindRaiderVocabulary.activeNotebook, (RDFNode) null);
        if (i.hasNext()) {
            model.remove(i.nextStatement());
        }
        activeNotebookUri = null;
    }

    /**
     * Delete active model.
     */
    public void deleteActiveModel() {
        StmtIterator i = model.listStatements((Resource) null, MindRaiderVocabulary.activeModel, (RDFNode) null);
        if (i.hasNext()) {
            model.remove(i.nextStatement());
        }
        activeModel = null;
    }

    /**
     * Set the active model.
     * @param modelPath the model path String
     */
    public void setActiveModel(String modelPath) {
        deleteActiveModel();
        deleteOutlineNotebook();

        this.activeModel = modelPath;

        model.add(
            model.createResource(MindRaiderConstants.MR_RDF_NS + profileName),
            MindRaiderVocabulary.activeModel,
            activeModel);
        save();
    }

    /**
     * Get URL of the last opened model.
     * @return the last model opened String
     */
    public String getLastOpenedModel() {
        // decide whether is opened model or notebook, if notebook, then load
        // notebook's model
        if (activeModel == null) {
            if (activeNotebookUri == null) {
                return null;
            }
            return MindRaider.outlineCustodian.getActiveNotebookDirectory()
                + File.pathSeparator
                + OutlineCustodian.FILENAME_RDF_MODEL;
        }
        return activeModel;
    }

    /**
     * Set the home notebook.
     */
    public void setHomeNotebook() {
        if (activeNotebookUri != null) {
            homeNotebook = activeNotebookUri.toASCIIString();
            save();
        }
    }

    /**
     * Get home notebook.
     * @return returns the homeNotebook
     */
    public String getHomeNotebook() {
        return homeNotebook;
    }

    /**
     * Set the local launcher command.
     * @param localLauncherCmd local launcher command String
     */
    public void setLocalLauncherCommand(String localLauncherCmd) {
        if (StringUtils.isNotBlank(localLauncherCmd)) {
            Launcher.setLocalCommand(localLauncherCmd);
            laucherLocal = localLauncherCmd;
        }
    }

    /**
     * SEt the web launcher command.
     * @param webLauncher the web launcher command String
     */
    public void setWebLauncherPath(String webLauncher) {
        if (StringUtils.isNotBlank(webLauncher)) {
            BrowserLauncher.preferredBrowserPath = webLauncher;
            launcherWeb = webLauncher;
        }
    }

    /**
     * Return the local launcher command String.
     * @return the local launcher
     */
    public String getLocalLauncherCommand() {
        return laucherLocal;
    }

    /**
     * Return the web launcher path.
     * @return the web launcher path
     */
    public String getWebLauncherPath() {
        return launcherWeb;
    }

    /**
     * Get relative resource path in the repository.
     * 
     * @param repositoryResourcePath the repository resource path
     * @return the relative path String
     */
    public String getRelativePath(String repositoryResourcePath) {
        if (StringUtils.isNotBlank(repositoryResourcePath)) {
            return repositoryResourcePath.substring(homeDirectory.length() + 1);
        }
        return null;
    }

    /**
     * Get absolute path from the relative path.
     * @param relativePath the relative path String
     * @return the absolute path String
     */
    public String getAbsolutePath(String relativePath) {
        return homeDirectory + File.separator + relativePath;
    }

    /**
     * Getter for <code>graphShowLabelsAsUris</code>.
     * @return Returns the graphShowLabelsAsUris.
     */
    public boolean isGraphShowLabelsAsUris() {
        return this.graphShowLabelsAsUris;
    }

    /**
     * Setter for <code>graphShowLabelsAsUris</code>.
     * @param graphShowLabelsAsUris The graphShowLabelsAsUris to set.
     */
    public void setGraphShowLabelsAsUris(boolean graphShowLabelsAsUris) {
        this.graphShowLabelsAsUris = graphShowLabelsAsUris;
    }

    /**
     * Getter for <code>graphMultilineLabels</code>.
     * @return Returns the graphMultilineLabels.
     */
    public boolean isGraphMultilineLabels() {
        return this.graphMultilineLabels;
    }

    /**
     * Setter for <code>graphMultilineLabels</code>.
     * @param graphMultilineLabels The graphMultilineLabels to set.
     */
    public void setGraphMultilineLabels(boolean graphMultilineLabels) {
        this.graphMultilineLabels = graphMultilineLabels;
    }

    /**
     * Setter for <code>foldersDirectory</code>.
     * @param foldersDirectory The foldersDirectory to set.
     */
    public void setFoldersDirectory(String foldersDirectory) {
        this.foldersDirectory = foldersDirectory;
    }

    /**
     * Getter for <code>graphHidePredicates</code>.
     * @return Returns the graphHidePredicates.
     */
    public boolean isGraphHidePredicates() {
        return this.graphHidePredicates;
    }

    /**
     * Setter for <code>graphHidePredicates</code>.
     * @param graphHidePredicates The graphHidePredicates to set.
     */
    public void setGraphHidePredicates(boolean graphHidePredicates) {
        this.graphHidePredicates = graphHidePredicates;
    }

    /**
     * Getter for <code>activeModel</code>.
     * @return Returns the activeModel.
     */
    public String getActiveModel() {
        return this.activeModel;
    }

    /**
     * Getter for <code>activeNotebookUri</code>.
     * @return Returns the activeNotebookUri.
     */
    public URI getActiveOutlineUri() {
        return this.activeNotebookUri;
    }

    /**
     * Setter for <code>activeNotebookUri</code>.
     * @param activeNotebookUri The activeNotebookUri to set.
     */
    public void setActiveOutlineUri(URI activeNotebookUri) {
        this.activeNotebookUri = activeNotebookUri;
    }

    /**
     * Getter for <code>profileName</code>.
     * @return Returns the profileName.
     */
    public String getProfileName() {
        return this.profileName;
    }

    /**
     * Setter for <code>profileName</code>.
     * @param profileName The profileName to set.
     */
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
    
    /**
     * Getter for <code>version</code>.
     * @return Returns the version.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Setter for <code>version</code>.
     * @param version The version to set.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Setter for <code>notebooksDirectory</code>.
     * @param notebooksDirectory The notebooksDirectory to set.
     */
    public void setNotebooksDirectory(String notebooksDirectory) {
        this.notebooksDirectory = notebooksDirectory;
    }

    /**
     * Setter for <code>hostname</code>.
     * @param hostname The hostname to set.
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Getter for <code>model</code>.
     * @return Returns the model.
     */
    public Model getModel() {
        return this.model;
    }

    /**
     * Setter for <code>model</code>.
     * @param model The model to set.
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Get prefered L&F.
     * @return L&F
     */
    public String getLookAndFeel() {
        if (lookAndFeel == null) {
            lookAndFeel = MindRaider.LF_NATIVE;
        }
        return lookAndFeel;
    }

    public void setLookAndFeel(String lookAndFeel) {
        this.lookAndFeel = lookAndFeel;
    }

    public boolean isEnableCommandDaemon() {
        return enableCommandDaemon;
    }

    public void setEnableCommandDaemon(boolean enableCommandDaemon) {
        this.enableCommandDaemon = enableCommandDaemon;
    }

    public boolean isOverrideSystemLocale() {
        return overrideSystemLocale;
    }

    public void setOverrideSystemLocale(boolean overrideSystemLocale) {
        this.overrideSystemLocale = overrideSystemLocale;
    }

    public String getCustomLocale() {
        return customLocale;
    }

    public void setCustomLocale(String customLocale) {
        this.customLocale = customLocale;
    }

    public void setEnableGnowsisSupport(boolean enableGnowsisSupport) {
        this.enableGnowsisSupport = enableGnowsisSupport;
    }

    public boolean isEnableGnowsisSupport() {
        return enableGnowsisSupport;
    }

    public void setEnableSpiders(boolean enableSpiders) {
        this.enableSpiders = enableSpiders;
    }

    public boolean isEnableSpiders() {
        return enableSpiders;
    }
}