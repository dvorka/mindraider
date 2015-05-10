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
package com.emental.mindraider.core.rdf;

import com.emental.mindraider.core.MindRaider;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.mindcognition.mindraider.commons.MindRaiderConstants;

/**
 * Mind Raider vocabulary. <br>
 * <br>
 * Guidelines:
 * <ul>
 * <li>Classes must start with upper case letter.</li>
 * <li>Properties must start with lower case letter.</li>
 * <li>Individuals can be in lower case.</li>
 * </ul>
 */
public class MindRaiderVocabulary {

    /**
     * The attachment property.
     */
    public static Property attachment;

    /**
     * The reference property.
     */
    public static Property reference;

    /**
     * The active notebook property.
     */
    public static Property activeNotebook;

    /**
     * The active model property.
     */
    public static Property activeModel;

    /**
     * The notebooks directory property.
     */
    public static Property notebooksDirectory;

    /**
     * The folders directory property.
     */
    public static Property foldersDirectory;

    /**
     * The home directory property.
     */
    public static Property homeDirectory;

    /**
     * The home notebook property.
     */
    public static Property homeNotebook;

    /**
     * The has child concept property.
     */
    public static Property hasChildConcept;

    /**
     * The hostname property.
     */
    public static Property hostname;

    /**
     * The MindRaider version property.
     */
    public static Property mrVersion;

    /**
     * The graph render label uri property.
     */
    public static Property graphRenderLabelUris;

    /**
     * The graph multiline labels property.
     */
    public static Property graphMultilineLabels;

    /**
     * The graph hide predicates property.
     */
    public static Property graphHidePredicates;

    /**
     * The laucher local property.
     */
    public static Property launcherLocal;

    /**
     * The launcher web property.
     */
    public static Property launcherWeb;

    /**
     * The launcher property.
     */
    public static Property launcher;

    /**
     * The xlink href property.
     */
    public static Property xlinkHref;

    /**
     * The is discarded property.
     */
    public static Property isDiscarded;

    /**
     * The flag category property.
     */
    public static Property flagProperty;

    /**
     * The true literal property.
     */
    public static Literal trueLiteral;

    /**
     * The false literal property.
     */
    public static Literal falseLiteral;

    /**
     * UI perspective property.
     */
    public static Property uiPerspective;

    /**
     * Look&feel property.
     */
    public static Property lookAndFeel;

    /**
     * Enable command daemon.
     */
    public static Property enableCommandDaemon;

    /**
     * Override system locale.
     */
    public static Property overrideSystemLocale;

    /**
     * If override system locale is enabled, use this custom locale.
     */
    public static Property customLocale;

    /**
     * The web resource constant.
     */
    private static final String WEB_RESOURCE = "webResource";

    /**
     * The local resource constant.
     */
    private static final String LOCAL_RESOURCE = "localResource";

    /**
     * The use launcher constant.
     */
    private static final String USE_LAUNCHER = "useLauncher";

    /**
     * The hide predicates in graph constant.
     */
    private static final String HIDE_PREDICATES_IN_GRAPH = "hidePredicatesInGraph";

    /**
     * The allow multiline labels in graph constant.
     */
    private static final String ALLOW_MULTILINE_LABELS_IN_GRAPH = "allowMultilineLabelsInGraph";

    /**
     * The render label uri in graph constant.
     */
    private static final String RENDER_LABEL_URIS_IN_GRAPH = "renderLabelUrisInGraph";

    /**
     * The active model constant.
     */
    private static final String ACTIVE_MODEL = "activeModel";

    /**
     * The active notebook constant.
     */
    private static final String ACTIVE_NOTEBOOK = "activeNotebook";

    /**
     * The home notebook constant.
     */
    private static final String HOME_NOTEBOOK = "homeNotebook";

    /**
     * The folders directory constant.
     */
    private static final String FOLDERS_DIRECTORY = "foldersDirectory";

    /**
     * The notebooks directory constant.
     */
    private static final String NOTEBOOKS_DIRECTORY = "notebooksDirectory";

    /**
     * The home directory constant.
     */
    private static final String HOME_DIRECTORY = "homeDirectory";

    /**
     * The hostname constant.
     */
    private static final String HOSTNAME = "hostname";

    /**
     * The has children concept constant.
     */
    private static final String HAS_CHILD_CONCEPT = "hasChildConcept";

    /**
     * The attachment constant.
     */
    private static final String ATTACHMENT = "attachment";

    /**
     * The MindRaider version constant.
     */
    private static final String MINDRAIDER_VERSION = "mindRaiderVersion";

    /**
     * UI perspective constant.
     */
    private static final String UI_PERSPECTIVE = "uiPerspective";

    /**
     * Look&feel constant.
     */
    private static final String LOOK_AND_FEEL = "lookAndFeel";

    /**
     * Enable common daemon.
     */
    private static final String ENABLE_COMMAND_DAEMON = "enableCommandDaemon";

    /**
     * Override system locale.
     */
    private static final String OVERRIDE_SYSTEM_LOCALE = "overrideSystemLocale";

    /**
     * Custom locale.
     */
    private static final String CUSTOM_LOCALE = "customLocale";

    static {
        Model model = ModelFactory.createDefaultModel();

        reference = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                MindRaiderConstants.MR_RDF_PREDICATE);

        mrVersion = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                MINDRAIDER_VERSION);

        attachment = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                ATTACHMENT);
        hasChildConcept = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                HAS_CHILD_CONCEPT);

        hostname = model
                .createProperty(MindRaiderConstants.MR_RDF_NS, HOSTNAME);

        homeDirectory = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                HOME_DIRECTORY);
        notebooksDirectory = model.createProperty(
                MindRaiderConstants.MR_RDF_NS, NOTEBOOKS_DIRECTORY);
        foldersDirectory = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                FOLDERS_DIRECTORY);

        homeNotebook = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                HOME_NOTEBOOK);

        activeNotebook = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                ACTIVE_NOTEBOOK);
        activeModel = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                ACTIVE_MODEL);

        // graph properties
        graphRenderLabelUris = model.createProperty(
                MindRaiderConstants.MR_RDF_NS, RENDER_LABEL_URIS_IN_GRAPH);
        graphMultilineLabels = model.createProperty(
                MindRaiderConstants.MR_RDF_NS, ALLOW_MULTILINE_LABELS_IN_GRAPH);
        graphHidePredicates = model.createProperty(
                MindRaiderConstants.MR_RDF_NS, HIDE_PREDICATES_IN_GRAPH);

        // lauchers
        launcher = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                USE_LAUNCHER);
        launcherLocal = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                LOCAL_RESOURCE);
        launcherWeb = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                WEB_RESOURCE);

        // system
        xlinkHref = model.createProperty(MindRaiderConstants.XLINK_NAMESPACE,
                MindRaiderConstants.XLINK_LOCAL_NAME_HREF);
        isDiscarded = model.createProperty(
                MindRaiderConstants.MR_OWL_PROPERTY_NS,
                MindRaiderConstants.MR_OWL_PROPERTY_IS_DISCARDED_LOCAL_NAME);

        // categories
        flagProperty = model.createProperty(MindRaiderConstants.MR_OWL_FLAG_NS,
                MindRaiderConstants.MR_OWL_FLAG_PROPERTY_LOCAL_NAME);

        // ui
        uiPerspective = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                UI_PERSPECTIVE);
        lookAndFeel = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                LOOK_AND_FEEL);

        /*
         * literals
         */
        trueLiteral = model.createLiteral(true);
        falseLiteral = model.createLiteral(false);

        // various preferences
        enableCommandDaemon = model.createProperty(
                MindRaiderConstants.MR_RDF_NS, ENABLE_COMMAND_DAEMON);
        overrideSystemLocale = model.createProperty(
                MindRaiderConstants.MR_RDF_NS, OVERRIDE_SYSTEM_LOCALE);
        customLocale = model.createProperty(MindRaiderConstants.MR_RDF_NS,
                CUSTOM_LOCALE);
    }

    /**
     * Get URI of the current user mind map like
     * <code>http://[hostname]/e-mentality/mindmap#[user name]</code>.
     *
     * @return the Uri String
     */
    public static String getMindMapUri() {
        return "http://" + MindRaider.profile.getHostname()
                + "/e-mentality/mindmap#" + MindRaider.user.getName();
    }

    /**
     * Get folder URI skeleton like:
     * <code>http://[hostname]/e-mentality/folder#</code>.
     *
     * @return the Uri folder skeleton String
     */
    public static String getFolderUriSkeleton() {
        return "http://" + MindRaider.profile.getHostname()
                + "/e-mentality/folder#";
    }

    /**
     * Get folder URI.
     *
     * @param folderNcName
     *            the folder name
     * @return the Uri folder String
     */
    public static String getFolderUri(String folderNcName) {
        return getFolderUriSkeleton() + folderNcName;
    }

    /**
     * Get notebook URI skeleton like:
     * <code>http://[hostname]/e-mentality/notebook#</code>.
     *
     * @return the notebook Uri skeleton String
     */
    public static String getNotebookUriSkeleton() {
        return "http://" + MindRaider.profile.getHostname()
                + "/e-mentality/notebook#";
    }

    /**
     * Get notebook URI.
     *
     * @param notebookNcName
     *            the notebook name
     * @return the notebook Uri String
     */
    public static String getNotebookUri(String notebookNcName) {
        return getNotebookUriSkeleton() + notebookNcName;
    }

    /**
     * Note that this method is wrong - URIs should be opaque.
     *
     * @param conceptUri
     *            the concept Uri String
     * @return return <code>true</code> if the given conceptUri is, otherwise
     *         <code>false</code>
     */
    public static boolean isConceptUri(String conceptUri) {
        // hostnames may differ, so some hack is needed
        if (conceptUri != null && conceptUri.startsWith("http://")
                && conceptUri.indexOf("/e-mentality/concept/") >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Note that this method is wrong - URIs should be opaque.
     *
     * @param notebookUri
     *            the notebook Uri
     * @return return <code>true</code> if given notebookUri is, otherwise
     *         <code>false</code>
     */
    public static boolean isNotebookUri(String notebookUri) {
        // hostnames may differ, so some hack is needed
        if (notebookUri != null && notebookUri.startsWith("http://")
                && notebookUri.indexOf("/e-mentality/notebook") >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Note that this method is wrong - URIs should be opaque.
     *
     * @param uri
     *            the Uri String
     * @return return <code>true</code> if uri is MindRaider Uri, otherwise
     *         <code>false</code>
     */
    public static boolean isMindRaiderResourceUri(String uri) {
        return isNotebookUri(uri)
                || isConceptUri(uri)
                || (uri != null && uri
                        .startsWith(MindRaiderConstants.MR_RDF_NS));
    }

    /**
     * Get concept URI skeleton like:
     * <code>http://[hostname]/e-mentality/concept/[notebook NCName]#.
     * @param notebookNcName the notebook name
     * @return the concept Uri skeleton String
     */
    public static String getConceptUriSkeleton(String notebookNcName) {
        return "http://" + MindRaider.profile.getHostname()
                + "/e-mentality/concept/" + notebookNcName + "#";
    }

    /**
     * Get concept URI.
     *
     * @param notebookNcName
     *            the notebook name
     * @param conceptNcName
     *            the concept name
     * @return the concept uri
     */
    public static String getConceptUri(String notebookNcName,
            String conceptNcName) {
        return getConceptUriSkeleton(notebookNcName) + conceptNcName;
    }

    /**
     * Resource URI.
     *
     * @return resource URI.
     */
    public static String getResourceUriSkeleton() {
        return "http://" + MindRaider.profile.getHostname() + "/e-mentality/";
    }

    /**
     * Get taxonomy URI skeleton like:
     * <code>http://[hostname]/e-mentality/taxonomy#.
     * 
     * @return the taxonomy URI skeleton string
     */
    public static String getTaxonomyUriSkeleton() {
        return "http://" + MindRaider.profile.getHostname()
                + "/e-mentality/taxonomy#";
    }
}
