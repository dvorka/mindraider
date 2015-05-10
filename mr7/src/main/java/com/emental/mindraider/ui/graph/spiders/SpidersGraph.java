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
package com.emental.mindraider.ui.graph.spiders;

import java.awt.Color;
import java.awt.Image;

import javax.swing.JOptionPane;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.emental.mindraider.core.MindRaider;
import com.emental.mindraider.core.facet.BriefFacet;
import com.emental.mindraider.core.facet.Facet;
import com.emental.mindraider.core.facet.FacetCustodian;
import com.emental.mindraider.core.kernel.NodeDescriptor;
import com.emental.mindraider.core.rdf.MindRaiderVocabulary;
import com.emental.mindraider.core.rdf.RdfModel;
import com.emental.mindraider.core.rest.resource.ConceptResource;
import com.emental.mindraider.ui.gfx.IconsRegistry;
import com.emental.mindraider.ui.graph.spiders.color.SpidersColorProfile;
import com.emental.mindraider.ui.outline.OutlineJPanel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.mindcognition.mindraider.commons.MindRaiderConstants;
import com.mindcognition.mindraider.ui.swing.main.StatusBar;
import com.mindcognition.mindraider.utils.FileLoader;
import com.mindcognition.mindraider.utils.Launcher;
import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGException;
import com.touchgraph.graphlayout.TGPanel;
import com.touchgraph.graphlayout.interaction.GLPanel;

/**
 * Spiders graph - it is singleton class that is able to render arbitrary RDF
 * model.
 * <br>
 * <br>
 * There are multiple parameters that may influence the way in which is graph
 * rendered:
 * <ul>
 * <li>Color profile ... TODO going to be reimplemented - will be based on
 * node/edge templates that will be cloned.</li>
 * <li>Facet ... used to filter out statements/resource to be shown.</li>
 * <li>Mode ... RDF/URIQA/etc. that determines the way in which are generated
 * launchable URLs in nodes.</li>
 * <li>Other options ... like show URIs as labels, multiline labels, show
 * predicates, etc.</li>
 * </ul>
 *
 * TODO creation of resources - generate URIQA URIs TODO handleDoubleSelect -
 * load it from remote and perform warp in all circumstances TODO union/new
 * model
 */
public class SpidersGraph {

    /**
     * The MindRaider new model String constant.
     */
    public static final String MINDRAIDER_NEW_MODEL = "MindRaider:NewModel";

    /**
     * Enable/disable hyperbolic graphs.
     */
    public static boolean hyperbolic;

    /**
     * Enable/disable antialiasing.
     */
    public static boolean antialiased;

    /**
     * Show frames per second statistics.
     */
    public static boolean fps;

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(SpidersGraph.class);

    /**
     * The active model.
     */
    private com.emental.mindraider.core.rdf.RdfModel rdfModel;

    /**
     * The rendering profile containing colors and node types.
     */
    private SpidersColorProfile render;

    /**
     * The triplets counter.
     */
    private int renderedTriplets;

    /**
     * The show local name or URI property.
     */
    private boolean uriLabels;

    /**
     * The render long names in multiline mode property.
     */
    private boolean multilineNodes = true;

    /**
     * The hide predicate nodes property.
     */
    private boolean hidePredicates = true;

    /**
     * The node label max width, or when to break label line.
     */
    private int nodeLabelMaxWidth = 30;

    /**
     * The facet to be used for model filtering.
     */
    private Facet facet;

    /**
     * The glpanel.
     */
    private GLPanel glPanel;

    /**
     * The tgpanel.
     */
    private TGPanel tgPanel;

    /**
     * Constructor.
     */
    public SpidersGraph() {
        glPanel = new GLPanel();
        glPanel.initialize();
        tgPanel = glPanel.getTGPanel();

        facet = FacetCustodian.getInstance().getFacet(BriefFacet.LABEL);

        if (MindRaider.profile != null) {
            hidePredicates = MindRaider.profile.isGraphHidePredicates();
            multilineNodes = MindRaider.profile.isGraphMultilineLabels();
            uriLabels = MindRaider.profile.isGraphShowLabelsAsUris();
        }
    }

    /**
     * Constructor.
     *
     * @param renderingProfile
     *            the color profile spiders graph to render
     */
    public SpidersGraph(SpidersColorProfile renderingProfile) {
        this();
        setRenderingProfile(renderingProfile);
    }

    /**
     * Set rendering profile.
     *
     * @param render
     *            the color profile spiders graph to render
     */
    public void setRenderingProfile(SpidersColorProfile render) {
        this.render = render;
        TGPanel.BACK_COLOR = render.getBackroundColor();
    }

    /**
     * Clear graph.
     */
    public void clear() {
        clearGraph();
        rdfModel = new RdfModel(MINDRAIDER_NEW_MODEL, RdfModel.GENERATED_MODEL_TYPE);
    }

    /**
     * Clear graph.
     */
    public void clearGraph() {
        glPanel.getTGPanel().clearSelect();
        glPanel.getTGPanel().clearAll();
        glPanel.getTGPanel().repaint();
    }

    /**
     * This class kernel method - render active model. <br/>Remarks: o Label vs
     * ID vs URI o label is just human readable annotation, used just for human
     * searches o it is system level identification of the node - ID must be
     * unique (used in touchgraph hashes) o URI is annotation of mine resource,
     * it is additional information; MR uses URIs to search for resources (note
     * that URIs are independent from IDs - ID is system level; URI is MR logic
     * level) Icons o all nodes that are clickable are marked with network icon
     *
     * @todo o performance o on selection of notebook in explorer panel, it is
     *       rendered twice o set zoom and lookahead to proper value in order to
     *       improve performance of the graph rendering o prepare rendering
     *       profile for MR - colors of resource types (notebook, attachment,
     *       etc.) o prepare renderNg() method (old rename to renderOld() and
     *       encapsulate them to render() where will be just switch) o it's
     *       therefore rendering profiles will require rendering by statement
     *       (which is already implemented) o loop (getStatement();
     *       selectSubject(); createStatement(already exists));
     */
    public void renderModel() {
        if(OutlineJPanel.getInstance().spidersAndTagsTabs.getSelectedIndex()==1) {
            return;
        }
        
        if (rdfModel == null || rdfModel.getModel() == null) {
            return;
        }

        // detect whether there is some node selected, if so, remember its ID
        Node selectedNode;
        String selectedNodeId;

        // try to remember selected node id to select it after rendering
        try {
            selectedNode = tgPanel.getSelect();
            // @todo solve literals ID selection - invent some stable ID
            // generation algorithm
            selectedNodeId = selectedNode.getID();
        } catch (NullPointerException e) {
            selectedNode = null;
            selectedNodeId = null;
        }

        // clean up visuals
        clearGraph();
        
        // render it again

        // make initial TG setup
        // @todo set this according to zoom and whether predicate is/is not
        // rendered
        Edge.setEdgeDefaultLength(80);
        Edge.setEdgeDefaultColor(render.getEdgeDefaultColor());

        // now fill in the TG representation
        try {
            Node firstNode = null, subjectNode, objectNode, predicateNode = null;
            NodeDescriptor nodeDescriptor;
            Statement statement;
            String id, label;

            StmtIterator i = rdfModel.getModel().listStatements();
            while (i.hasNext()) {
                statement = i.nextStatement();

                /*
                 * facet
                 */

                if (!facet.showThisStatement(statement)) {
                    logger.debug("  Facet: skipping " + statement);
                    continue;
                }

                renderedTriplets++;

                /*
                 * rendering
                 */

                // subject node
                nodeDescriptor = getNodeDescriptor(statement.getSubject());

                // @todo find node by URL, if it fail, then it must be some
                // noname node - so use ID instead

                if ((subjectNode = tgPanel.findNode(nodeDescriptor.getId())) == null) {
                    subjectNode = createSubjectNode(nodeDescriptor);
                    subjectNode.rdfNode = statement.getSubject();
                    tgPanel.addNode(subjectNode);
                }

                // predicate node
                if (!hidePredicates) {
                    // default predicate has empty label
                    String ns = statement.getPredicate().getNameSpace();
                    if (StringUtils.equals(MindRaiderConstants.MR_RDF_PREDICATE_NS, ns)
                            && StringUtils.equals(MindRaiderConstants.MR_RDF_PREDICATE, ns)) {
                        label = "";
                    } else {
                        label = uriLabels ? statement.getPredicate().getURI() : statement.getPredicate().getLocalName();
                    }
                    // for predicate, ID is always unique
                    id = statement.getPredicate().getURI() + "_" + MindRaiderConstants.MR + renderedTriplets;
                    predicateNode = createPredicateNode(id, label);
                    tgPanel.addNode(predicateNode);

                    // the first half of the edge
                    createSubject2PredicateEdge(subjectNode, predicateNode);
                }

                // object node
                if (statement.getObject() instanceof Resource) {
                    nodeDescriptor = getNodeDescriptor((Resource) statement.getObject());
                    if ((objectNode = tgPanel.findNode(nodeDescriptor.getId())) == null) {
                        objectNode = createObjectNode(nodeDescriptor);
                        tgPanel.addNode(objectNode);
                    }
                } else {
                    // literal
                    objectNode = createLiteralNode("literal_" + MindRaiderConstants.MR + renderedTriplets, statement
                            .getObject().toString());
                    tgPanel.addNode(objectNode);
                }
                objectNode.rdfNode = statement.getObject();

                if (!hidePredicates) {
                    // the second half of the edge
                    createPredicate2ObjectEdge(predicateNode, objectNode);
                } else {
                    // hide predicates
                    createPredicate2ObjectEdgePredicateHidden(
                            subjectNode, 
                            objectNode,
                            statement.getPredicate().getURI());
                }
            }

            if (firstNode == null) {
                firstNode = tgPanel.getGES().getFirstNode();
                tgPanel.setLocale(firstNode, 0);
                tgPanel.setSelect(firstNode);
            }

            // now try to previously selected node AND set locality
            if (selectedNodeId != null) {
                selectedNode = tgPanel.findNode(selectedNodeId);
            } else {
                tgPanel.selectFirstNode();
                selectedNode = getSelectedNode();
            }
            tgPanel.setSelect(selectedNode);
            tgPanel.setLocale(selectedNode, glPanel.localityScroll.getLocalityRadius());

            logger.debug("  Total triplets: " + renderedTriplets);
        } catch (Exception e) {
            logger.error("Unable to enumerate statements: " + e.getMessage(), e);
        }

    }

    /**
     * Generate spiders graph for RDF model. <br>
     * UUID either URI or UUID provided by model (it is available only for
     * anonymous resources.
     *
     * @param name
     *            the name of model.
     * @param model
     *            RDF model to be rendered.
     */
    public void renderModel(String name, Model model) {
        rdfModel = new RdfModel(name, model);
        MindRaider.masterToolBar.setModelLocation(name);
        renderModel();
    }

    /**
     * Load and show an RDF model.
     *
     * @param url
     *            the url String
     * @throws Exception
     *             a generic exception
     */
    public void load(String url) throws Exception {
        if (url != null) {
            try {
                rdfModel = new RdfModel(url);
                MindRaider.masterToolBar.setModelLocation(url);
                renderedTriplets = 0;
                MindRaider.profile.setActiveModel(url);

                // clear selection
                tgPanel.clearSelect();
                renderModel();

                // load file to viewer
                FileLoader.loadFile(url, MindRaider.notebookRdfXmlViewer);
            } catch (Exception e) {
                logger.debug("Unable to load model: " + e.getMessage(), e);
                throw e;
            }
        }
    }

    /**
     * Add and show and RDF model.
     *
     * @param url
     *            the url String
     */
    public void addModel(String url) {
        if (url == null) {
            return;
        }

        try {
            RdfModel newModel = new RdfModel(url);
            rdfModel.getModel().union(newModel.getModel());
            MindRaider.masterToolBar.setModelLocation(url);
            renderModel();
            MindRaider.profile.setActiveModel(url);
        } catch (Exception e) {
            logger.error("addModel(String)", e);
        }
    }

    /**
     * New model.
     *
     * @param name
     *            the model name
     */
    public void newModel(String name) {
        rdfModel = new RdfModel(name, RdfModel.GENERATED_MODEL_TYPE);
    }

    /**
     * Get glpanel.
     *
     * @return the GLPanel property.
     */
    public GLPanel getPanel() {
        return glPanel;
    }

    /**
     * Get glpanel.
     *
     * @return the TGPanel property.
     */
    public TGPanel getTGPanel() {
        return tgPanel;
    }

    /**
     * Returns the selected node label.
     *
     * @return the node label String
     */
    public String getSelectedNodeLabel() {
        if (tgPanel.getSelect() == null) {
            logger.debug("Select is null!");
            return null;
        }
        return tgPanel.getSelect().getLabel();
    }

    /**
     * Return the selected node.
     *
     * @return Returns the node
     */
    public Node getSelectedNode() {
        return tgPanel.getSelect();
    }

    /**
     * Select node by URI of the resource.
     *
     * @param uri
     *            the uri String
     */
    public void selectNodeByUri(String uri) {
        logger.debug("selectNodeByUri: " + uri);
        selectNode(tgPanel.findNodeByUri(uri));
    }

    /**
     * Select node by Java reference.
     *
     * @param node
     *            the node to selected
     */
    public void selectNode(Node node) {
        if (node != null) {
            try {
                tgPanel.setSelect(node);
                tgPanel.setLocale(node, getLookahead());
                // tgPanel.expandNode(node);
            } catch (TGException e) {
                logger.debug("Unable to set lookahed!", e);
            }
        }
    }

    /**
     * Clear node selection.
     */
    public void clearNodeSelection() {
        tgPanel.clearSelect();
    }

    /**
     * Render model with labels as URIs or just local names.
     *
     * @param uriLabels
     *            the uri labels
     */
    public void showUriLables(boolean uriLabels) {
        this.uriLabels = uriLabels;
        renderModel();
    }

    /**
     * Handle select.
     *
     * @param node
     *            the selected node
     * @return <code>true</code>
     * @todo application of MEP plugins using their selectors
     */
    public boolean handleSelect(Node node) {
        return true;
    }

    /**
     * This method is called on double select of an node. If it is resource,
     * annotation is opened. If it is literal, corresponding operation is
     * launched (http://, *.rdf.xml, *.txt, etc.).
     *
     * @param node
     *            node to be used as source for URL
     * @return boolean value
     */
    public boolean handleDoubleSelect(Node node) {
        logger.debug("=-> handeDoubleSelect: " + node.getURL());
        // TODO fix this method - it's terrible; windows specific and extensions
        // limited
        // TODO warp only in some cases
        // TODO extension handling - extensions must be lowercased
        // TODO omezeni na literal! - rdfs: seeAlso! - invent generic approach
        // of warping to such nodes

        if (node.getURL() != null) {
            // only URL matters
            String url = node.getURL();
            String lowerUrl = url.toLowerCase();

            // TODO Warp - user should be allowd to choose between Warp and
            // Launch (ask him)
            if (lowerUrl.endsWith(".rdf") || lowerUrl.endsWith(".rdf.xml")) {
                // gfx
                tgPanel.warpStart("Warp!");
                gfxNavigatorMessageFade();
                try {
                    // warp
                    load(url);
                } catch (Exception e) {
                    //
                }
                return true;
            }

            // remote launch
            // TODO expose for resistration of extensions later (will be used on
            // defalt: section of BrowserLauncher)
            if (lowerUrl.startsWith("http")) {
                // filter out my notebooks/concepts for now
                if (!MindRaiderVocabulary.isMindRaiderResourceUri(lowerUrl)) {
                    // gfx
                    tgPanel.warpStart("Launch!");
                    gfxNavigatorMessageFade();
                    // start browser
                    Launcher.launchInBrowser(url);
                    return true;
                }
            }

            // TODO local launch (start)
            // TODO test EXE
            // TODO test directories
            if (url.charAt(1) == ':' || url.charAt(1) == '/' || url.startsWith("file:")) {
                // gfx
                tgPanel.warpStart("Local start!");
                gfxNavigatorMessageFade();

                // start
                // TODO OS independent
                Launcher.launchViaStart(url);
                return true;
            }
        }

        logger.debug("Node URL is null!");

        return false;
    }

    private void gfxNavigatorMessageFade() {
        new Thread() {
            public void run() {
                setDaemon(true);
                
                TGPanel p = getTgPanel();
                while (p.warpEnabled) {
                    try {
                        sleep(70);
                    } catch (InterruptedException e) {
                    }
                    p.repaint();
                }
            }
        }.start();
    }

    /**
     * Create new resource.
     *
     * @param uri
     *            the uri String
     * @param literal
     *            the literal flag
     */
    public void newResource(String uri, boolean literal) {
        if (uri == null || "".equals(uri)) {
            uri = MindRaiderConstants.MR_RDF_NS + "anonymousResource";
        }

        RDFNode rdfNode = rdfModel.newResource(uri, literal);

        NodeDescriptor nodeDescriptor;
        if (literal) {
            // TODO get triplet id generator method
            nodeDescriptor = new NodeDescriptor("literal_" + MindRaiderConstants.MR + ++renderedTriplets, uri, uri);
        } else {
            nodeDescriptor = getNodeDescriptor((Resource) rdfNode);
        }

        try {
            Node node = createSubjectNode(nodeDescriptor);
            node.rdfNode = rdfNode;
            tgPanel.addNode(node);
            tgPanel.setSelect(node);
        } catch (TGException e) {
            logger.error("newResource(String, boolean)", e);
        }
    }

    /**
     * Create a subject node from a given node descriptor.
     *
     * @param nodeDescriptor
     *            the node descriptor
     * @return the created object node
     */
    public Node createSubjectNode(NodeDescriptor nodeDescriptor) {
        Image icon = null;
        String nodeUri = nodeDescriptor.getUri();
        if (StringUtils.isNotEmpty(nodeUri) && !MindRaiderVocabulary.isMindRaiderResourceUri(nodeUri)) {
            // forbid openning of local resources for now
            icon = IconsRegistry.getImage("launch.png");
        }

        Node node = new Node(
                nodeDescriptor.getId(), 
                render.getSubjectNodeType(), 
                render.getSubjectBackColor(),
                nodeDescriptor.getLabel(), icon);
        // TODO set resource URI if available
        node.setURL(nodeUri);

        node.setTextColor(render.getSubjectTextColor());
        node.setNodeBackSelectColor(render.getSubjectSelectBackColor());
        node.setNodeBackHilightColor(render.getSubjectHilightBackColor());
        node.setNodeBorderInactiveColor(render.getNodeInactiveBorder());

        return node;
    }

    /**
     * Create a predicate node.
     *
     * @param id
     *            the id String
     * @param label
     *            the label String
     * @return Returns the predicate Node
     */
    public Node createPredicateNode(String id, String label) {
        Node node = new Node(id, label);

        node.setType(render.getPredicateNodeType());
        node.setBackColor(render.getPredicateBackColor());
        node.setTextColor(render.getPredicateTextColor());

        // TODO change - just experiment of node drawing
        node.BACK_HILIGHT_COLOR = Color.RED;
        node.BACK_SELECT_COLOR = SpidersColorProfile.darkBlueColor;
        node.BORDER_MOUSE_OVER_COLOR = Color.WHITE;
        node.BORDER_INACTIVE_COLOR = new Color(0x99, 0x99, 0x99);

        return node;
    }

    /**
     * Create an object node from the given node descriptor.
     *
     * @param nodeDescriptor
     *            the node descriptor
     * @return the Node object
     */
    public Node createObjectNode(NodeDescriptor nodeDescriptor) {
        Image icon = null;
        if (StringUtils.isNotEmpty(nodeDescriptor.getUri())
                && !MindRaiderVocabulary.isMindRaiderResourceUri(nodeDescriptor.getUri())) {
            // forbid openning of local resources for now
            icon = IconsRegistry.getImage("launch.png");
        }

        Node node = new Node(nodeDescriptor.getId(), render.getObjectNodeType(), render.getObjectBackColor(),
                nodeDescriptor.getLabel(), icon);
        node.setURL(nodeDescriptor.getUri());

        node.setTextColor(render.getObjectTextColor());
        node.setNodeBackSelectColor(render.getObjectSelectBackColor());
        node.setNodeBorderInactiveColor(render.getNodeInactiveBorder());

        node.setNodeBackHilightColor(render.getObjectHilightBackColor());

        // TODO change - just experiment of node drawing
        node.BORDER_INACTIVE_COLOR = Color.DARK_GRAY;
        node.BORDER_MOUSE_OVER_COLOR = Color.WHITE;

        return node;
    }

    /**
     * Create a literal node.
     *
     * @param id
     *            the String id
     * @param label
     *            the label String
     * @return Returns the literal node object
     */
    public Node createLiteralNode(String id, String label) {
        Node node;

        // multiline node: use it only if the node label is longer than some
        // fixed
        // value and contains space
        if (multilineNodes && label.length() > nodeLabelMaxWidth && !label.startsWith("http://")
                && !(label.charAt(1) == ':')) {
            node = new MultilinedNode(id, label, ' ');
        } else {
            node = new Node(id, label);
            if (label != null
                    && (label.startsWith("http") || (label.length() > 1 && label.charAt(1) == ':') || (label.length() > 0 && label
                            .charAt(0) == '/'))) {
                node.setURL(label);
                node.setImage(IconsRegistry.getImage("launch.png"));
            }
        }

        node.setType(render.getLiteralNodeType());
        node.setBackColor(render.getLiteralBackColor());
        node.setTextColor(render.getLiteralTextColor());
        node.setNodeBackHilightColor(render.getLiteralActiveBorderColor());
        node.setNodeBorderInactiveColor(render.getLiteralInactiveBorderColor());
        
        node.setNodeBackSelectColor(render.getObjectSelectBackColor());

        return node;
    }

    /**
     * Create subject to predicate edge.
     *
     * @param subjectNode
     *            the subject node
     * @param predicateNode
     *            the predicate node
     */
    public void createSubject2PredicateEdge(Node subjectNode, Node predicateNode) {
        final Edge edge = new Edge(subjectNode, predicateNode, Edge.DEFAULT_LENGTH, Edge.EDGE_TYPE_LINE);
        edge.setColor(render.getEdgeDefaultColor());
        tgPanel.addEdge(edge);
    }

    /**
     * Create predicate to object edge.
     *
     * @param predicateNode
     *            the predicate node
     * @param objectNode
     *            the node object
     */
    public void createPredicate2ObjectEdge(Node predicateNode, Node objectNode) {
        tgPanel.addEdge(predicateNode, objectNode, Edge.DEFAULT_LENGTH);        
    }

    /**
     * Create predicate to object edge when predicates are hidden
     *
     * @param predicateNode
     *            the predicate node
     * @param objectNode
     *            the node object
     */
    public void createPredicate2ObjectEdgePredicateHidden(Node predicateNode, Node objectNode, String predicateUri) {
        Edge edge=tgPanel.addEdge(predicateNode, objectNode, Edge.DEFAULT_LENGTH);
        
        if(predicateUri!=null && !predicateUri.startsWith(MindRaiderConstants.RDF_NS)) {
            // non-standard predicate
            edge.setColor(render.getEdgeExtraColor());
        }
    }

    /**
     * As subject is used selected node. TODO FUUUUJ - forget about QNames and
     * change to URNs!
     *
     * @param predicate
     *            the predicate
     * @param object
     *            the object
     * @param literal
     *            the literal flag
     * @return the Statement
     */
    public Statement createStatement(QName predicate, QName object, boolean literal) {
        Resource subject=null;
        ConceptResource conceptResource = OutlineJPanel.getInstance().conceptJPanel.getConceptResource();
        if(conceptResource==null) {
            JOptionPane.showMessageDialog(MindRaider.mainJFrame, "No not selected - RDF triplet cannot be created!",
                    "Triplet Creation Error", JOptionPane.ERROR_MESSAGE);            
        } else {
            subject=rdfModel.getResource(conceptResource.getUri());
            if(subject==null) {
                JOptionPane.showMessageDialog(MindRaider.mainJFrame, "Unable to find RDF node with ID '"+conceptResource.getUri()+"'",
                        "Triplet Creation Error", JOptionPane.ERROR_MESSAGE);                                        
            }
        }

        Statement statement 
            = rdfModel.createStatement(
                    subject,
                    (predicate.getNamespaceURI()==null?MindRaiderConstants.MR_RDF_NS:predicate.getNamespaceURI())+predicate.getLocalPart(), 
                    (object.getNamespaceURI()==null?object.getLocalPart():object.getNamespaceURI()+object.getLocalPart()), 
                    literal);

        save();
        renderModel();

        return statement;
    }

    /**
     * Used to connect two existing resources.
     *
     * @param subject
     *            the subject
     * @param predicate
     *            the predicate
     * @param object
     *            the object
     */
    public void createPredicate(QName subject, QName predicate, QName object) {
    }

    /**
     * Save model.
     *
     * @return Returns <code>true</code> if the model is successfully saved,
     *         otherwise <code>false</code>.
     */
    public boolean save() {
        return rdfModel.save();
    }

    /**
     * Save model as given filename.
     *
     * @param fileName
     *            the file name to save model
     * @return Returns <code>true</code> if the model is successfully saved,
     *         otherwise <code>false</code>.
     */
    public boolean saveAs(String fileName) {
        if (fileName == null) {
            return false;
        }
        return rdfModel.saveAs(fileName);
    }

    /**
     * Get RDF model.
     *
     * @return the RDFModel
     */
    public RdfModel getRdfModel() {
        return rdfModel;
    }

    /**
     * Get number of RDF model triplets.
     *
     * @return the number of rendered triplets
     */
    public int getNumberOfTriplets() {
        return renderedTriplets;
    }

    /**
     * Get node descriptor (ID and label) from RDF resource.
     *
     * @param resource
     *            the resource
     * @return Returns the NodeDescription
     */
    public NodeDescriptor getNodeDescriptor(Resource resource) {
        logger.debug(" getNodeDescriptor()");
        NodeDescriptor result = new NodeDescriptor();

        String uri = resource.getURI();
        result.setId(uri);
        if (result.getId() == null || "".equals(result.getId())) {
            // substitute ID from RDF model
            try {
                result.setId(resource.getId().toString());
            } catch (Exception e) {
                logger.debug("Result ID is ." + result.getId() + ".");
                result.setLabel(" ");
                result.setId("");
            }
        }

        // label and URI
        if (uri == null) {
            result.setLabel(" ");
        } else {
            if (uriLabels) {
                result.setLabel(uri);
            } else {
                // check whether there is rdfs:label - if so, use it instead of
                // label
                Statement statement = resource.getProperty(RDFS.label);
                if (statement != null) {
                    result.setLabel(statement.getObject().toString());
                } else {
                    result.setLabel(resource.getLocalName());
                }
            }
            result.setUri(uri);
        }

        logger.debug("  Label: " + result.getLabel());
        logger.debug("  URI  : " + result.getUri());
        logger.debug("  ID   : " + result.getId());

        return result;
    }

    /**
     * Delete statement.
     */
    public void deleteStatement() {
        StatusBar.show("Deleting statement...");

        Node predicate = getSelectedNode();

        if (predicate.edgeCount() < 2) {
            StatusBar.show("Error: Uncomplete statement (missing either subject or object)!");
            return;
        }

        if (predicate.getType() != render.getPredicateNodeType()) {
            JOptionPane.showMessageDialog(MindRaider.mainJFrame, "To delete statement, predicate must be selected!",
                    "Triplet Delete Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Node subject, object;
        Edge e = predicate.edgeAt(0);

        if (predicate == e.getTo()) {
            subject = e.getFrom();
            object = predicate.edgeAt(1).getTo();
        } else {
            subject = e.getTo();
            object = predicate.edgeAt(1).getFrom();
        }

        rdfModel.deleteStatement(subject.rdfNode, (Property) predicate.rdfNode, object.rdfNode);
        save();
        renderModel();
    }

    /**
     * Determine whether predicates are hidden.
     *
     * @return the hide predicateg property
     */
    public boolean getHidePredicates() {
        return hidePredicates;
    }

    /**
     * Hide predicates when model is rendered.
     *
     * @param b
     *            <code>true</code> if predicates should be hidden.
     */
    public void hidePredicates(boolean b) {
        hidePredicates = b;
    }

    /**
     * Get lookahead.
     *
     * @return global lookahed.
     */
    public int getLookahead() {
        return glPanel.getLocalityRadius();
    }

    /**
     * Get zoom value.
     *
     * @return zoom value.
     */
    public int getZoom() {
        return glPanel.getZoomValue();
    }

    /**
     * @return Returns the uriLabels.
     */
    public boolean isUriLabels() {
        return uriLabels;
    }

    /**
     * Set the uriLabels property.
     *
     * @param uriLabels
     *            The uriLabels to set.
     */
    public void setUriLabels(boolean uriLabels) {
        this.uriLabels = uriLabels;
    }

    /**
     * Check if it is a multiline nodes.
     *
     * @return Returns the multilineNodes.
     */
    public boolean isMultilineNodes() {
        return multilineNodes;
    }

    /**
     * Set multiline nodes.
     *
     * @param multilineNodes
     *            The multilineNodes to set.
     */
    public void setMultilineNodes(boolean multilineNodes) {
        this.multilineNodes = multilineNodes;
    }

    /**
     * Get current facet.
     *
     * @return current facet.
     */
    public Facet getFacet() {
        return facet;
    }

    /**
     * Set facet to be used.
     *
     * @param facet
     *            facet to be used.
     */
    public void setFacet(Facet facet) {
        if (facet != null) {
            this.facet = facet;
        }
    }

    /**
     * Getter for <code>glPanel</code>.
     *
     * @return Returns the glPanel.
     */
    public GLPanel getGlPanel() {
        return glPanel;
    }

    /**
     * Getter for <code>tgPanel</code>.
     *
     * @return Returns the tgPanel.
     */
    public TGPanel getTgPanel() {
        return this.tgPanel;
    }

    /**
     * Setter for <code>tgPanel</code>.
     *
     * @param tgPanel
     *            The tgPanel to set.
     */
    public void setTgPanel(TGPanel tgPanel) {
        this.tgPanel = tgPanel;
    }
}
