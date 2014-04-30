package uk.ac.ebi.demo.ols.swing;

import uk.ac.ebi.demo.ols.business.OLSClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.Arrays;
import java.util.Comparator;

/**
 * User: rcote
 * Date: 17-May-2007
 * Time: 11:32:03
 * $Id: $
 */
public class OntologyNameLookupDemo extends JPanel {

    //UI components
    private String[] columns = new String[]{"Key", "Value"};
    private JTable tableData;
    private JComboBox ontologies;
    private JTextField queryTerm;
    private JComboBox termNames;
    private static final String CLICK_TO_LOAD_STR = "Click button to load";
    private static final String ENTER_TEXT_STR = "Enter query term";

    //OLS SOAP handler
    private OLSClient client = new OLSClient();
    private Map<String, String> termCache;

    /**
     * constructor that sets up UI components
     */
    public OntologyNameLookupDemo() {

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        add(Box.createVerticalStrut(5));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        ontologies = new JComboBox();
        ontologies.setModel(new DefaultComboBoxModel(new String[]{CLICK_TO_LOAD_STR}));
        ontologies.setEditable(false);
        ontologies.setMaximumSize(ontologies.getPreferredSize());

        //load ontologies from OLS
        JButton loadOntologies = new JButton("Load Ontologies");
        loadOntologies.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                java.util.List<String> ontologyList = client.getOntologies();
                ontologies.setModel(new DefaultComboBoxModel(ontologyList.toArray()));
                System.out.println("ontologies loaded");
            }
        });
        topPanel.add(ontologies);
        topPanel.add(loadOntologies);

        add(topPanel);
        add(Box.createHorizontalStrut(5));

        //query string
        JPanel midPanel = new JPanel();
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.X_AXIS));
        queryTerm = new JTextField(ENTER_TEXT_STR);
        queryTerm.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                //clear last value on focus
                queryTerm.setText("");
            }

            public void focusLost(FocusEvent e) {
            }
        });
        midPanel.add(queryTerm);

        //perform SOAP call to OLS
        JButton lookup = new JButton("Suggest terms");
        lookup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                //clear metadata
                clearMetadataTable();

                //query OLS
                if (ontologies.getSelectedItem().toString().equals(CLICK_TO_LOAD_STR)) {
                    JOptionPane.showMessageDialog(null, "Please select an ontology to query");
                    return;
                }
                if (queryTerm.getText().equals(ENTER_TEXT_STR)) {
                    JOptionPane.showMessageDialog(null, "Please enter a query string");
                    return;
                }
                termCache = client.getTermsByName(queryTerm.getText(), ontologies.getSelectedItem().toString());

                //update termNames

                if (!termCache.isEmpty()) {
                    String[] values = termCache.keySet().toArray(new String[0]);
                    //sort on string length
                    Arrays.sort(values, new Comparator() {
                        public int compare(Object o1, Object o2) {
                            String s1 = o1.toString();
                            String s2 = o2.toString();
                            if (s1.length() == s2.length()) {
                                return s1.compareTo(s2);
                            } else {
                                if (s1.length() > s2.length()) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            }
                        }
                    });
                    //add item to head
                    String[] finalValues = new String[values.length + 1];
                    finalValues[0] = "Select a term from the list below";
                    System.arraycopy(values, 0, finalValues, 1, values.length);
                    //update combobox
                    termNames.setModel(new DefaultComboBoxModel(finalValues));
                } else {
                    termNames.setModel(new DefaultComboBoxModel(new String[]{"No terms found"}));
                }

            }
        });
        midPanel.add(lookup);
        add(midPanel);
        add(Box.createHorizontalStrut(5));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        termNames = new JComboBox(new DefaultComboBoxModel(new String[]{"Suggestions will appear here"}));
        termNames.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {

                    //get term id
                    String termId = termCache.get(termNames.getSelectedItem().toString());
                    if (termId != null) {

                        //query OLS
                        Map<String, String> metadata = client.getTermMetadata(termId, ontologies.getSelectedItem().toString());
                        if (!metadata.isEmpty()) {

                            Object[][] data = new Object[metadata.size()][2];
                            int i = 0;

                            for (String key : metadata.keySet()) {
                                String value = metadata.get(key);
                                data[i][0] = key;
                                data[i][1] = value;
                                i++;
                            }

                            //refresh
                            tableData.setModel(new DefaultTableModel(data, columns));

                            System.out.println("update done");

                        }
                    }
                }
            }
        });
        bottomPanel.add(termNames);
        add(bottomPanel);

        tableData = new JTable(new Object[0][0], columns);
        tableData.setShowGrid(true);
        JScrollPane scrollPane = new JScrollPane(tableData);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        add(scrollPane);

        add(Box.createHorizontalStrut(5));
        add(Box.createVerticalGlue());

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
        JFrame frame = new JFrame("Ontology Term Lookup Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new OntologyNameLookupDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
