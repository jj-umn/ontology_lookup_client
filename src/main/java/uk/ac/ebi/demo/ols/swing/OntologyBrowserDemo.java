package uk.ac.ebi.demo.ols.swing;

import uk.ac.ebi.demo.ols.business.OLSClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

/**
 * User: rcote
 * Date: 17-May-2007
 * Time: 11:32:03
 * $Id: $
 */
public class OntologyBrowserDemo extends JPanel implements ActionListener, ItemListener {

    //UI components
    private String[] columns = new String[]{"Key", "Value"};
    private JTable tableData;
    private JComboBox ontologies;
    private TreeBrowser treeBrowser;

    //SOAP client class
    private OLSClient client = new OLSClient();

    /**
     * constructor that creates UI components
     */
    public OntologyBrowserDemo() {

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        add(Box.createVerticalStrut(5));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        ontologies = new JComboBox();
        ontologies.setModel(new DefaultComboBoxModel(new String[]{"Click button to load"}));
        ontologies.setEditable(false);
        ontologies.setMaximumSize(ontologies.getPreferredSize());
        ontologies.addItemListener(this);

        JButton loadOntologies = new JButton("Load Ontologies");
        loadOntologies.addActionListener(this);
        topPanel.add(ontologies);
        topPanel.add(loadOntologies);

        add(topPanel);
        add(Box.createHorizontalStrut(5));

        treeBrowser = new TreeBrowser(this);
        treeBrowser.setMinimumSize(new Dimension(400, 300));
        treeBrowser.setPreferredSize(new Dimension(400, 300));
        add(treeBrowser);
        add(Box.createHorizontalStrut(5));

        tableData = new JTable(new Object[0][0], columns);
        tableData.setShowGrid(true);
        JScrollPane scrollPane = new JScrollPane(tableData);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        add(scrollPane);

        add(Box.createHorizontalStrut(5));
        add(Box.createVerticalGlue());

    }

    /**
     * action listener for load ontologies button
     */
    public void actionPerformed(ActionEvent e) {
        java.util.List<String> ontologyList = client.getOntologies();
        ontologies.setModel(new DefaultComboBoxModel(ontologyList.toArray()));
        System.out.println("ontologies loaded");
    }

    /**
     * listener for ontology list selection. this will load the roots of the ontology
     */
    public void itemStateChanged(ItemEvent e) {

        if (e.getStateChange() == ItemEvent.SELECTED) {

            //get selected ontology
            String ontology = ontologies.getSelectedItem().toString();
            treeBrowser.initialize(ontology);

            //load root terms
            Map<String, String> rootTerms = client.getOntologyRoots(ontology);
            for (String termId : rootTerms.keySet()) {
                String termName = rootTerms.get(termId);
                //update tree
                treeBrowser.addNode(termId, termName);
            }
            System.out.println("updated roots");

        }
    }

    /**
     * load the children of a term
     * @param parent - the tree node where to load the terms
     * @param termId - the term id to query on
     * @return
     */
    public boolean loadChildren(TreeNode parent, String termId) {

        if (termId == null){
            return false;
        }

        //get children from OLS
        Map<String, String> childTerms = client.getTermChildren(termId, ontologies.getSelectedItem().toString());
        if (!childTerms.isEmpty()) {
            for (String tId : childTerms.keySet()) {
                String termName = childTerms.get(tId);
                //update tree
                treeBrowser.addNode(tId, termName);
            }
            return true;
        } else {
            System.out.println("no children returned for " + termId);
            return false;
        }
    }

    /**
     * load metadata for a given termid
     * @param termId
     */
    public void loadMetaData(String termId) {

        if (termId == null){
            //clear table
            clearMetadataTable();
            return;
        }

        //query OLS
        Map<String, String> metadata = client.getTermMetadata(termId, ontologies.getSelectedItem().toString());
        if (!metadata.isEmpty()) {

            Object[][] data = new Object[metadata.size()][2];
            int i = 0;

            for (String key : metadata.keySet()) {
                String value = metadata.get(key);
                data[i][0] = key;
                data[i][1] = value;
                System.out.println("kv= " + key + " -> " + value);
                i++;
            }

            //refresh
            tableData.setModel(new DefaultTableModel(data, columns));

            System.out.println("update done");

        } else {
            clearMetadataTable();
        }

    }

    //empties data table
    private void clearMetadataTable() {
        tableData.setModel(new DefaultTableModel(new Object[0][0], columns));
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Ontology Browser Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new OntologyBrowserDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
