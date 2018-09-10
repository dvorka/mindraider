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
package com.mindcognition.mindraider.install;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.core.rdf.RdfModel;
import com.emental.mindraider.core.rest.ResourceDescriptor;
import com.emental.mindraider.core.rest.properties.ResourcePropertyGroup;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.emental.mindraider.core.rest.resource.FolderResource;
import com.emental.mindraider.core.rest.resource.OutlineResource;
import com.emental.mindraider.core.search.SearchCommander;
import com.emental.mindraider.ui.outline.treetable.OutlineTreeInstance;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.mindcognition.mindraider.application.model.outline.OutlineCustodian;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.l10n.Messages;
import com.mindcognition.mindraider.tools.Checker;
import com.mindcognition.mindraider.ui.swing.explorer.ExplorerJPanel;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;
import com.mindcognition.mindraider.utils.Utils;
import com.mindcognition.mindraider.utils.Zipper;

/**
 * This class is responsible for installation and upgrades of the prepared notebooks, XSLs,
 * CSSs, etc.
 */
public class Installer {

    /**
     * The profile hostname.
     */
    private static String profileHostname = "";

    /**
     * The profile username.
     */
    private static String profileUsername = "";

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Installer.class);

    /**
     * The XML document declaration constant.
     */
    private static final String XML_DOCDECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    /**
     * The directory distribution skeleton constant.
     */
    private static final String DIR_DISTRIBUTION_SKELETON = "/install";

    /**
     * The home directory of folders, notebooks, etc.
     */
    private String resourceDirectoryHome;

    /**
     * Constructor.
     *
     * @param resourceDirectoryHome
     *            the resource directory home
     */
    public Installer(String resourceDirectoryHome) {
        this.resourceDirectoryHome = resourceDirectoryHome;
    }

    /**
     * Install everything :-) .
     *
     * @param hostname
     *            the hostname
     * @param username
     *            the username
     */
    public void install(String hostname, String username) {
        profileHostname = hostname;
        profileUsername = username;

        try {
            // try to find the distribution skeleton - either there is regular MR installation
            // or development MR checkout from CVS. check whats's available
            String repositorySkeleton = MindRaider.installationDirectory + DIR_DISTRIBUTION_SKELETON;
            if(!new File(repositorySkeleton).exists()) {
                // try the development location... just a try ;-)
                repositorySkeleton=MindRaider.installationDirectory+"/../mr7-release/src/main/distribution"+DIR_DISTRIBUTION_SKELETON;
            }

            LOGGER.debug("Installing from: "+repositorySkeleton);
            LOGGER.debug("Installing to: "+resourceDirectoryHome);

            // check whether the repository is already initialized - if so, don't initialize it
            File folders = new File(resourceDirectoryHome + "/Notebooks");
            if (folders.exists()) {
                LOGGER.debug(Messages.getString("Installer.repositoryAlreadyInitialized"));
                return;
            }

            // source repository skeleton
            LOGGER.debug(Messages.getString("Installer.installingRepository") + " <");
            gatherDirectoryFiles(new File(repositorySkeleton), resourceDirectoryHome, repositorySkeleton.length());
            LOGGER.debug(">");
        } catch (Exception e) {
            LOGGER.error(Messages.getString("Installer.unableToInitializeRepository"), e);

        }
    }

    /**
     * Process only files under dir.
     *
     * @param dir
     *            the File
     * @param destinationDirectory
     *            the destination directory
     * @param prefixLng
     *            the prefix length
     * @throws Exception
     *             a generic exception
     */
    public static void gatherDirectoryFiles(File dir, String destinationDirectory, int prefixLng) throws Exception {
        String fromFile = dir.getPath();
        if (dir.isDirectory()) {
            String toFile = destinationDirectory + (fromFile.substring(prefixLng));

            LOGGER.debug("Dir: " + fromFile);
            LOGGER.debug(" =-> " + toFile);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("gatherDirectoryFiles() - :");
            }
            Utils.createDirectory(toFile);

            String[] children = dir.list();
            for (String filename : children) {
                gatherDirectoryFiles(new File(dir, filename), destinationDirectory, prefixLng);
            }
        } else {
            String toFile = destinationDirectory + (fromFile.substring(prefixLng));

            LOGGER.debug("File: " + fromFile);
            LOGGER.debug(" =-> " + toFile);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("gatherDirectoryFiles() - .");
            }
            FileUtils.copyFile(new File(fromFile), new File(toFile));

            // now URIs must be fixed - nasty hack
            // from http://dvorka and http://savant must be created http://[user
            // hostname]
            fixUris(toFile);
        }
    }

    /**
     * Fix URIs - remove dvorka & savant in order to replace it with user's
     * hostname.
     *
     * @param filename
     *            the filename
     * @todo replace reading/closing file with commons-io functions
     */
    public static void fixUris(String filename) {
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader in = null;
        try {

            // try to start reading
            in = new BufferedReader(new FileReader(new File(filename)));
            String line;
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
        } catch (IOException e) {
            LOGGER.debug(Messages.getString("Installer.unableToReadFile", filename), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    LOGGER.debug(Messages.getString("Installer.unableToCloseReader"));
                }
            }
        }

        // file successfuly loaded - now replace strings
        String old = stringBuffer.toString();
        if (old != null) {
            String replacement = "http://" + profileHostname + "/e-mentality/mindmap#" + profileUsername;
            old = old.replaceAll("http://dvorka/e-mentality/mindmap#dvorka", replacement);
            old = old.replaceAll("http://dvorka/", "http://" + profileHostname + "/");
            old = old.replaceAll("http://savant/", "http://" + profileHostname + "/");
            // logger.debug(old+"\n");
        }

        // write it back
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(filename));
            out.write(old);
        } catch (Exception e) {
            LOGGER.debug(Messages.getString("Installer.unableToWriteFixedFile", filename), e);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e1) {
                    LOGGER.debug(Messages.getString("Installer.unableToCloseFile", filename), e1);
                }
            }
        }
    }

    /*
     * upgrade
     */

    /**
     * Called in case of existing repository in order to perform update. There
     * are two phases - current version detection and upgrade itself.
     */
    public static void upgrade() {
        profileHostname = MindRaider.profile.getHostname();
        profileUsername = MindRaider.user.getName();

        String upgradeInfo =
            "Upgrade check: " +  profileUsername+"@"+profileHostname+" # "+
            MindRaider.getVersion()+" -> "+ MindRaider.profile.getVersion();
        LOGGER.debug(upgradeInfo);

        boolean doUpgrade = false;

        int profileMajor = 1024;
        int profileMinor = 1024;
        if (MindRaider.profile.getVersion() != null && MindRaider.profile.getVersion().length() >= 3) {
            int dotIdx = MindRaider.profile.getVersion().indexOf('.');
            profileMajor = NumberUtils.createInteger(MindRaider.profile.getVersion().substring(0, dotIdx));
            profileMinor = NumberUtils.createInteger(MindRaider.profile.getVersion().substring(dotIdx + 1));

            LOGGER.debug("  Parsed profile version: "+profileMajor+" # "+profileMinor); // {{debug}}
            if ((profileMajor < MindRaiderConstants.majorVersion) ||
                (profileMajor == MindRaiderConstants.majorVersion) && (profileMinor < MindRaiderConstants.minorVersion)) {
                doUpgrade = true;
            }
        } else {
            doUpgrade = true;
        }

        if (!doUpgrade) {
            return;
        }

        LOGGER.debug(Messages.getString("Installer.goingToUpgrade"));

        // upgrade to more recent minor version
        if (profileMajor == 0) {
            if (profileMinor < 506) {
                upgradeTo0506();
            }
            if (profileMinor < 507) {
                upgradeTo0507();
            }
            if (profileMinor < 511) {
                upgradeTo0511();
            }
            if (profileMinor < 512) {
                upgradeTo0512();
            }
        }

        if(profileMajor <= 7) {
            if(profileMinor < 1) {
                upgradeTo71();
            }
            if(profileMinor < 2) {
                upgradeTo72();
            }
            if(profileMinor < 3) {
                upgradeTo73();
            }
            if(profileMinor < 5) {
                upgradeTo75();
            }
            if(profileMinor < 6) {
                upgradeTo76();
            }
            if(profileMinor < 7) {
                upgradeTo80();
            }
        }
    }

    /**
     * upgrade to 0.506 re-save all: 1. notebook models; 2. notebook resources;
     * 3. concept resources
     */
    private static void upgradeTo0506() {
        LOGGER.debug(Messages.getString("Installer.upgradingTo", "0.506"));

        try {
            // 1. repair notebook models
            // for each folder get notebook descriptors
            ResourceDescriptor[] folders = MindRaider.labelCustodian.getLabelDescriptors();
            if (folders != null) {
                for (int i = 0; i < folders.length; i++) {
                    String uri = folders[i].getUri();
                    StatusBar.setText("", uri,70);

                    try {
                        // resave folder resource
                        new FolderResource(MindRaider.labelCustodian.get(uri)).save();
                    } catch (Exception e2) {
                        LOGGER.debug(Messages.getString("Installer.unableToResaveFolder", uri), e2);
                    }

                    ResourceDescriptor[] notebooks = MindRaider.labelCustodian.getOutlineDescriptors(uri);

                    if (notebooks != null) {
                        for (int j = 0; j < notebooks.length; j++) {
                            String notebookUri = notebooks[j].getUri();
                            StatusBar.setText("", notebookUri,70);

                            // upgrade model
                            String notebookModelFilename = MindRaider.outlineCustodian
                                    .getModelFilenameByDirectory(MindRaider.outlineCustodian
                                            .getOutlineDirectory(notebookUri));
                            Model oldModel = RdfModel.loadModel(notebookModelFilename);

                            // fix notebook
                            Resource notebookRdf = oldModel.getResource(notebookUri);
                            // * rdfs:bag -> rdfs:seq
                            notebookRdf.removeAll(RDF.type);
                            OutlineCustodian.createOutlineRdfResource(notebooks[j], oldModel, notebookRdf);

                            // * fix every concept in the model
                            com.emental.mindraider.core.rest.Resource notebookR = MindRaider.outlineCustodian
                                    .get(notebookUri);
                            OutlineResource notebookResource = new OutlineResource(notebookR);
                            try {
                                // save resource to update its properties
                                notebookResource.save();
                            } catch (Exception e1) {
                                LOGGER.error("Unable to save notebook!", e1);
                            }
                            String[] conceptUris = notebookResource.getConceptUris();
                            if (!ArrayUtils.isEmpty(conceptUris)) {
                                for (String conceptUri : conceptUris) {
                                    try {
                                        Resource conceptRdf = oldModel.getResource(conceptUri);
                                        ConceptResource conceptResource = MindRaider.noteCustodian.get(notebookUri,
                                                conceptUri);
                                        // add attachments to the resource
                                        // empty attachemnts group (detect,
                                        // whether group presents and if it
                                        // doesn't, then create it)
                                        if (!conceptResource.attachmentsExist()) {
                                            conceptResource.resource.getData().addPropertyGroup(
                                                    new ResourcePropertyGroup(
                                                            ConceptResource.PROPERTY_GROUP_LABEL_ATTACHMENTS, new URI(
                                                                    ConceptResource.PROPERTY_GROUP_URI_ATTACHMENTS)));
                                            // add attachments there (if
                                            // exists)
                                            StmtIterator a = oldModel.listStatements(conceptRdf,
                                                    MindRaiderVocabulary.attachment, (RDFNode) null);
                                            while (a.hasNext()) {
                                                String url = a.nextStatement().getObject().toString();

                                                LOGGER.debug(Messages.getString("Installer.attachmentUrl", url));
                                                conceptResource.addAttachment(null, url);
                                            }
                                        }

                                        // save resource to update its
                                        // properties
                                        // MindRaider.conceptCustodian.save(noteResource,oldModel);
                                        conceptResource.save();

                                        // * rdfs:seq
                                        conceptRdf.removeAll(RDF.type);
                                        conceptRdf.addProperty(RDF.type, RDF.Seq);
                                        // * MR type
                                        conceptRdf.addProperty(RDF.type, oldModel
                                                .createResource(MindRaiderConstants.MR_OWL_CLASS_CONCEPT));
                                        // * rdfs:label
                                        conceptRdf.addProperty(RDFS.label, oldModel.createLiteral(conceptResource
                                                .getLabel()));
                                        // * dc:created
                                        conceptRdf.addProperty(DC.date, oldModel.createLiteral(conceptResource.resource
                                                .getMetadata().getCreated()));
                                        // * rdfs:comment (annotation
                                        // snippet)
                                        conceptRdf.addProperty(RDFS.comment, oldModel
                                                .createLiteral(OutlineTreeInstance
                                                        .getAnnotationCite(conceptResource.getAnnotation())));
                                        // * xlink:href
                                        conceptRdf.addProperty(MindRaiderVocabulary.xlinkHref, MindRaider.profile
                                                .getRelativePath(MindRaider.noteCustodian
                                                        .getConceptResourceFilename(notebookUri, conceptUri)));
                                    } catch (Exception e) {
                                        LOGGER.error(Messages.getString("Installer.unableToUpgradeConcept"), e);
                                    }
                                }
                            }

                            // result overview
                            // StringWriter result=new StringWriter();
                            // oldModel.write(result);
                            // logger.debug(result.toString());

                            // write model
                            RdfModel.saveModel(oldModel, notebookModelFilename);
                        }
                    }
                }
            }

            // update version in the profile
            MindRaider.profile.setVersion(MindRaider.getVersion());
            MindRaider.profile.save();
        } finally {
        }
    }

    /**
     * upgrade to 0.507 version of MR.
     */
    private static void upgradeTo0507() {
        LOGGER.debug(Messages.getString("Installer.upgradingTo", "0.507"));

        // update type of the resource in the profile
        Resource resource = MindRaider.profile.getModel().getResource(
                MindRaiderConstants.MR_RDF_NS + MindRaider.profile.getProfileName());
        resource.removeAll(RDF.type);
        resource.addProperty(RDF.type, MindRaider.profile.getModel().createResource(
                MindRaiderConstants.MR_OWL_CLASS_PROFILE));

        // update version in the profile
        MindRaider.profile.setVersion(MindRaider.getVersion());
        MindRaider.profile.save();

        // @todo there is profile issue - user QName is not unique :-Z (taken
        // from MR namespace)
    }

    /**
     * upgrade to 0.511 version of MR.
     */
    private static void upgradeTo0511() {
        LOGGER.debug(Messages.getString("Installer.upgradingTo", "0.511"));

        // *) make zip archive of the old repository
        backupRepositoryAsZip();

        // *) documentation upgrade - delete old documentation, copy new
        // documentation there
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("upgradeTo0511() -      Upgrading documentation... <");
        }
        // o determine whether documentation folder
        // o determine whether documentation notebooks exists (notebook
        // custodian)
        // o delete directories on the file system
        // o copy directories there using standard installation routines -
        // but only these
        // tree notebooks

        // delete what exists
        try {
            String mrFolderUri = MindRaiderVocabulary.getFolderUri(OutlineCustodian.MR_DOC_FOLDER_LOCAL_NAME);
            if (!MindRaider.labelCustodian.exists(mrFolderUri)) {
                LOGGER.debug(Messages.getString("Installer.creatingMindRaiderFolder"));
                MindRaider.labelCustodian.create("MR", mrFolderUri);
            }
            String introNotebookUri = MindRaiderVocabulary
                    .getNotebookUri(OutlineCustodian.MR_DOC_NOTEBOOK_INTRODUCTION_LOCAL_NAME);
            if (!MindRaider.outlineCustodian.exists(introNotebookUri)) {
                LOGGER.debug(Messages.getString("Installer.creatingIntroductionNotebook"));
                MindRaider.outlineCustodian.create("Introduction", introNotebookUri, "MR Introduction", false);
                MindRaider.labelCustodian.addOutline(mrFolderUri, introNotebookUri);
            }
            String docNotebookUri = MindRaiderVocabulary
                    .getNotebookUri(OutlineCustodian.MR_DOC_NOTEBOOK_DOCUMENTATION_LOCAL_NAME);
            if (!MindRaider.outlineCustodian.exists(docNotebookUri)) {
                LOGGER.debug(Messages.getString("Installer.creatingDocumentationNotebook"));
                MindRaider.outlineCustodian.create("Documentation", docNotebookUri, "MR Documentation", false);
                MindRaider.labelCustodian.addOutline(mrFolderUri, docNotebookUri);
            }
            String developersNotebookUri = MindRaiderVocabulary
                    .getNotebookUri(OutlineCustodian.MR_DOC_NOTEBOOK_FOR_DEVELOPERS_LOCAL_NAME);
            if (!MindRaider.outlineCustodian.exists(developersNotebookUri)) {
                LOGGER.debug(Messages.getString("Installer.creatingForDevelopersNotebook"));
                MindRaider.outlineCustodian.create("For Developers", developersNotebookUri, "For Developers", false);
                MindRaider.labelCustodian.addOutline(mrFolderUri, developersNotebookUri);
            }

            // HACK :-Z renew directories on the file system
            upgradeDocumentationNotebook(OutlineCustodian.MR_DOC_NOTEBOOK_INTRODUCTION_LOCAL_NAME);
            upgradeDocumentationNotebook(OutlineCustodian.MR_DOC_NOTEBOOK_DOCUMENTATION_LOCAL_NAME);
            upgradeDocumentationNotebook(OutlineCustodian.MR_DOC_NOTEBOOK_FOR_DEVELOPERS_LOCAL_NAME);

            ExplorerJPanel.getInstance().refresh();
        } catch (Exception e) {
            LOGGER.debug("upgradeTo0511(): unable to upgrade documentation!");
        }
        LOGGER.debug(">\n" + Messages.getString("Installer.documentationUpgraded"));

        // *) replace categories file - copy there just single file
        try {
            String categoriesOntologySuffix = File.separator + MindRaiderConstants.MR_DIR_CATEGORIES_DIR
                    + File.separator + "notebook.rdf.xml";

            String target = MindRaider.profile.getHomeDirectory() + categoriesOntologySuffix;
            File file = new File(target);
            file.getParentFile().mkdirs();

            FileUtils.copyFile(new File(MindRaider.installationDirectory + DIR_DISTRIBUTION_SKELETON
                    + categoriesOntologySuffix), new File(target));
        } catch (Exception e2) {
            LOGGER.error(Messages.getString("Installer.unableToCopyNotebooksCategoriesOntology"), e2);
            LOGGER.error("upgradeTo0511()", e2);
        }

        // *) internationalization
        // o all *.xml will be processed
        // o implemented using hack - it just reads file content and
        // replaces the first line if it starts with
        // <?xml
        // with
        // <?xml version="1.0" encoding="UTF-8"?>
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Messages.getString("Installer.internationalization") + " <");
        }

        try {
            internationalizationUpgradeTo511(new File(MindRaider.profile.getHomeDirectory()));
        } catch (Exception e) {
            LOGGER.debug(Messages.getString("Installer.unableToInternationalize"), e);
        } finally {
            LOGGER.debug(">\n" + Messages.getString("Installer.internationalizationUpgradeFinished"));
        }

        // *) internationalization - profile: encoding changed automatically

        // *) update search index
        SearchCommander.rebuildSearchAndTagIndices();

        // update version in the profile
        MindRaider.profile.setVersion(MindRaider.getVersion());
        MindRaider.profile.save();
    }

    /**
     * upgrade to 0.512. there are no changes in the model - just rebuild search
     * index and upgrade content type properties are set automatically, backup
     * repository and update version in the profile.
     */
    private static void upgradeTo0512() {
        SearchCommander.rebuildSearchAndTagIndices();
        Installer.backupRepositoryAsZipAsync();

        MindRaider.profile.version = MindRaider.getVersion();
        MindRaider.profile.save();
    }

    /**
     * upgrade to 0.601 - the tag release: tag index is build.
     */
    private static void upgradeTo71() {
        // refresh stylesheets/css/javascript/...
        String xslSkeleton = MindRaider.installationDirectory + DIR_DISTRIBUTION_SKELETON + File.separator + "lib";
        // if distribution directory doesn't exist, try development one
        if(!new File(xslSkeleton).exists()) {
            xslSkeleton = MindRaider.installationDirectory + "/../mr7-release/src/main/distribution" + DIR_DISTRIBUTION_SKELETON + File.separator + "lib";
        }
        String targetDirectory = MindRaider.profile.getHomeDirectory()+File.separator+"lib";
        LOGGER.debug("Upgrade to 7.1: "+xslSkeleton+" # "+targetDirectory);
        try {
            gatherDirectoryFiles(
                    new File(xslSkeleton),
                    targetDirectory,
                    xslSkeleton.length());
        } catch (Exception e) {
            LOGGER.error("Unable to copy XSL/JS/CSS resources to the repository.",e); // {{debug}}
        }

        SearchCommander.rebuildSearchAndTagIndices();
        Installer.backupRepositoryAsZipAsync();

        MindRaider.profile.version = MindRaider.getVersion();
        MindRaider.profile.save();
    }

    private static void upgradeTo72() {
        upgradeSanityCheck();
    }

    private static void upgradeTo73() {
        upgradeSanityCheck();
    }

    private static void upgradeTo75() {
        upgradeSanityCheck();
    }

    private static void upgradeTo76() {
        upgradeSanityCheck();
    }

    private static void upgradeTo80() {
        upgradeSanityCheck();
    }

    /**
     * Asynchronous repository backup ensuring dialog refreshing.
     */
    public static void backupRepositoryAsZipAsync() {
        Thread thread = new Thread() {
            @Override
            public void run() {

                String targetFile;
                if ((targetFile = backupRepositoryAsZip()) == null) {
                    JOptionPane.showMessageDialog(MindRaider.mainJFrame, Messages
                            .getString("Installer.UnableToBackupRepository"), Messages
                            .getString("Installer.backupError"), JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(MindRaider.mainJFrame, Messages.getString(
                            "Installer.repositoryBackupStoredTo", targetFile), Messages
                            .getString("Installer.backupResult"), JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Backup repository.
     *
     * @return location of the directory where was the directory backup.
     */
    public static String backupRepositoryAsZip() {
        LOGGER.debug(Messages.getString("Installer.makingZipBackupRepository", MindRaider.profile.getHomeDirectory()));

        try {
            // use book & make helper
            String zipFileName = new File(MindRaider.profile.getHomeDirectory()).getParent() + File.separator +
                    "MindRaider-" +
                    MindRaiderConstants.majorVersion+
                    "."+
                    MindRaiderConstants.minorVersion+
                    "-backup-"+Utils.getCurrentDataTimeAsPrettyString()+".zip";

            Zipper.zip(zipFileName, MindRaider.profile.getHomeDirectory());
            LOGGER.debug(Messages.getString("Installer.backupCreated", zipFileName));

            return zipFileName;
        } catch (Exception e1) {
            LOGGER.error(Messages.getString("Installer.unableToBackupRepositoryDirectory"), e1);
        }

        return null;
    }

    public static void backupRepositoryAsTWikiDirectoryAsync() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                String targetFile;
                if ((targetFile = backupRepositoryAsTWikiDirectory()) == null) {
                    JOptionPane.showMessageDialog(MindRaider.mainJFrame, Messages
                            .getString("Installer.UnableToBackupRepository"), Messages
                            .getString("Installer.backupError"), JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(MindRaider.mainJFrame, Messages.getString(
                            "Installer.repositoryBackupStoredTo", targetFile), Messages
                            .getString("Installer.backupResult"), JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public static String backupRepositoryAsTWikiDirectory() {
        LOGGER.debug(Messages.getString("Installer.makingTWikiBackupRepository", MindRaider.profile.getHomeDirectory()));

        try {
            String twikiDirFileName = new File(MindRaider.profile.getHomeDirectory()).getParent() + File.separator +
                    "MindRaider-" +
                    MindRaiderConstants.majorVersion+
                    "."+
                    MindRaiderConstants.minorVersion+
                    "-twiki-backup-"+Utils.getCurrentDataTimeAsPrettyString();

            File targetDir = new File(twikiDirFileName);
            if(!targetDir.exists()) {
                if(targetDir.mkdir()) {
                    ResourceDescriptor[] labelDescriptors
                    = MindRaider.labelCustodian.getLabelDescriptors();
                    if(!ArrayUtils.isEmpty(labelDescriptors)) {
                        for (ResourceDescriptor labelDescriptor : labelDescriptors) {
                            String labelUri = labelDescriptor.getUri();
                            ResourceDescriptor[] outlineDescriptors
                            = MindRaider.labelCustodian.getOutlineDescriptors(labelUri);
                            if (outlineDescriptors != null) {
                                for (ResourceDescriptor outlineDescriptor : outlineDescriptors) {
                                    // load outline to be exported
                                    if(MindRaider.outlineCustodian.loadOutline(new URI(outlineDescriptor.getUri()))) {
                                        String dstFileName =
                                                MindRaider.outlineCustodian.getActiveNotebookNcName()
                                                + "-"
                                                + Utils.getCurrentDataTimeAsPrettyString()
                                                + ".twiki";
                                        LOGGER.debug(Messages.getString("MindRaiderJFrame.exportingToFile", dstFileName));
                                        StatusBar.setText("Exporting Outline: "+MindRaider.outlineCustodian.getActiveNotebookLabel()); // TODO l10n
                                        MindRaider.outlineCustodian.exportOutline(OutlineCustodian.FORMAT_TWIKI, twikiDirFileName+File.separator+dstFileName);
                                    } else {
                                        LOGGER.error("Unable to load outline: "+outlineDescriptor.getUri());
                                        // skip it and continue
                                    }
                                }
                            }
                        }
                    }
                    return twikiDirFileName;
                } else {
                    LOGGER.error("Unable to create target dir: "+targetDir.getAbsolutePath());
                    return null;
                }
            } else {
                LOGGER.error("Target directory already exists: "+targetDir.getAbsolutePath());
                return null;
            }
        } catch (Exception e1) {
            LOGGER.error(Messages.getString("Installer.unableToBackupRepositoryDirectory"), e1);
        }

        return null;
    }

    /**
     * Upgrade documentation notebook.
     *
     * @param notebookLocalName
     *            the notebook local name
     * @throws Exception
     *             a generic exception
     */
    private static void upgradeDocumentationNotebook(String notebookLocalName) throws Exception {
        String relativePath = File.separator + MindRaiderConstants.MR_DIR_NOTEBOOKS_DIR + File.separator
                + notebookLocalName;
        File file = new File(MindRaider.profile.getHomeDirectory() + relativePath);

        LOGGER.debug(Messages.getString("Installer.checkingNotebookExistence", file.getAbsolutePath()));
        if (file.exists()) {

            LOGGER.debug(Messages.getString("Installer.renewing", file.getAbsolutePath()));
            Utils.deleteSubtree(file);
            file.mkdirs();
            String sourceSkeleton = MindRaider.installationDirectory + DIR_DISTRIBUTION_SKELETON + relativePath;
            File sourceSkeletonFile = new File(sourceSkeleton);
            gatherDirectoryFiles(sourceSkeletonFile, file.getAbsolutePath(), sourceSkeleton.length());
        }
    }

    /**
     * Process only files under dir.
     *
     * @param dir
     *            the file
     * @throws Exception
     *             a generic exception
     */
    public static void internationalizationUpgradeTo511(File dir) throws Exception {
        String fromFile = dir.getPath();
        if (dir.isDirectory()) {
            LOGGER.debug("Dir: " + fromFile);
            StatusBar.setText(" Directory: ",fromFile,70);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("internationalizationUpgradeTo511()");
            }

            String[] children = dir.list();
            for (String child : children) {
                internationalizationUpgradeTo511(new File(dir, child));
            }
        } else {
            LOGGER.debug("File: " + fromFile);
            StatusBar.setText(" File: ",fromFile,70);
            if (fromFile.endsWith(".xml")) {
                internationalizationUpgradeTo511(fromFile);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("internationalizationUpgradeTo511()");
            }
        }
    }

    /**
     * Change XML Decl to <?xml version="1.0" encoding="UTF-8"?>.
     *
     * @param filename
     *            the filename
     */
    public static void internationalizationUpgradeTo511(String filename) {
        String content = null;

        try {
            content = FileUtils.readFileToString(new File(filename), "UTF-8");
        } catch (IOException e) {
            LOGGER.debug(Messages.getString("Installer.unableToReadFile", filename), e);
        }

        String s;
        // file successfully loaded - now replace strings
        if (content.indexOf("<?xml") == 0) {
            // replace the first line
            // logger.debug(stringBuffer.indexOf("\n"));
            s = content.substring(content.indexOf("\n") + 1);
            s = XML_DOCDECL + "\n" + s;
        } else {
            // prepend the declaration
            s = (XML_DOCDECL + "\n").toString();
        }

        try {
            s = new String(s.getBytes(), "UTF-8");
            // logger.debug("#"+s+"#");
        } catch (Exception e) {
            LOGGER.debug(Messages.getString("Installer.unableToReencode"), e);
            return;
        }

        try {
            FileUtils.writeStringToFile(new File(filename), s, "UTF-8");
        } catch (Exception e) {
            LOGGER.debug(Messages.getString("Installer.unableToWriteFixedFile", filename), e);
        }
    }

    private static void upgradeSanityCheck() {
        Installer.backupRepositoryAsZipAsync();
        Checker.checkAndFixRepository();
        SearchCommander.rebuildSearchAndTagIndices();

        MindRaider.profile.version = MindRaider.getVersion();
        MindRaider.profile.save();
    }

}