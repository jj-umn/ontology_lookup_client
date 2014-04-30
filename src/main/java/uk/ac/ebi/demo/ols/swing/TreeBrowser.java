package uk.ac.ebi.demo.ols.swing;

/*
 * This code is based on an example provided by Richard Stanford,
 * a tutorial reader.
 */

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

public class TreeBrowser extends JPanel implements TreeSelectionListener, TreeModelListener {

    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;
    private OntologyBrowserDemo parent;

    /**
     * constructor that accepts a parent container. the parent will have
     * methods that are required to update the interface by communicating
     * with the OLS webservice.
     */
    public TreeBrowser(OntologyBrowserDemo parent) {

        super(new GridLayout(1, 0));

        this.parent = parent;

        tree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode(new TermNode("Load Ontology to Browse", null))));
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.addTreeSelectionListener(this);

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);
    }

    /**
     * sets the root node to the ontology label
     * @param ontologyName
     */
    public void initialize(String ontologyName) {
        rootNode = new DefaultMutableTreeNode(new TermNode(ontologyName, null));
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(this);
        tree.setModel(treeModel);
    }

    /**
     * Remove all nodes except the root node.
     */
    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    /**
     * Add child to the currently selected node, or the root node if no selection
     */
    public DefaultMutableTreeNode addNode(Object termId, Object termName) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode)
                    (parentPath.getLastPathComponent());
        }

        return addNode(parentNode, termId, termName, true);
    }

    /**
     * Add child to a specified node, or the root node if no node specified.
     */
    public DefaultMutableTreeNode addNode(DefaultMutableTreeNode parent,
                                          Object termId,
                                          Object termName,
                                          boolean shouldBeVisible) {

        DefaultMutableTreeNode childNode =
                new DefaultMutableTreeNode(new TermNode(termName.toString(), termId.toString()));

        if (parent == null) {
            parent = rootNode;
        }

        treeModel.insertNodeInto(childNode, parent,
                parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    //required by TreeModelListener interface
    public void treeNodesChanged(TreeModelEvent e) {
    }

    //required by TreeModelListener interface
    public void treeNodesInserted(TreeModelEvent e) {
    }

    //required by TreeModelListener interface
    public void treeNodesRemoved(TreeModelEvent e) {
    }

    //required by TreeModelListener interface
    public void treeStructureChanged(TreeModelEvent e) {
    }

    /**
     * This method will be called when a user selects a node in the tree.
     * Selecting a node will active two behaviours:
     * 1. it will load the children of that term
     * 2. it will load the metadata for that term
     */
    //required by TreeSelectionListener interface
    public void valueChanged(TreeSelectionEvent e) {

        //get selected node
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                e.getPath().getLastPathComponent();

        if (node == null) return;

        //get node data object
        TermNode nodeInfo = (TermNode) node.getUserObject();
        //load children only for leaf nodes and those that have
        //not been marked as processed.
        if (node.isLeaf() && node.getAllowsChildren()) {
            System.out.println("will load children for: " + nodeInfo);
            //load children. if no children, set allowsChildren to false
            if (!parent.loadChildren(node, nodeInfo.getTermId())) {
                node.setAllowsChildren(false);
            }
        }
        //loadmetadata
        System.out.println("will load metadata for: " + nodeInfo.getTermId());
        //call method on parent container
        parent.loadMetaData(nodeInfo.getTermId());

    }

    /**
     * object class that will represent a node in the tree. it contains
     * a term name and term id as members.
     */
    private class TermNode {

        private String termName;
        private String termId;

        public TermNode(String termName, String termId) {
            this.termName = termName;
            this.termId = termId;
        }

        public String getTermName() {
            return termName;
        }

        public void setTermName(String termName) {
            this.termName = termName;
        }

        public String getTermId() {
            return termId;
        }

        public void setTermId(String termId) {
            this.termId = termId;
        }

        public String toString() {
            return ((termId == null) ? "" : "[" + termId + "] ") + termName;
        }
    }

}
