/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umn.msi.gx.ontology_lookup_client;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import uk.ac.ebi.demo.ols.business.OLSClient;

/**
 *
 * @author James E Johnson jj@umn.edu
 * @version
 */
public class OntologyLookUp {

    private OLSClient client;
    List<String> ontologyList = null;
    private Collection<String> termIDs;
    private Collection<String> names;
    private String ontology;
    private PrintStream out;
    public enum Filter {
        ALL, MATCH, LEAF
    }
    Filter filter = Filter.LEAF;
    

    public OntologyLookUp() {
        out = System.out;
        client = new OLSClient();
        ontologyList = client.getOntologies();
    }

    public OntologyLookUp(String[] args) {
        this();
        parseOtions(args);
    }

    private void parseOtions(String[] args) {
        Parser parser = new BasicParser();
        String ontologyOpt = "ontology";
        String termOpt = "term";
        String nameOpt = "name";
        String filterOpt = "filter";
        String outputOpt = "output";

        Options options = new Options();
        options.addOption("O", ontologyOpt, true, "Ontology e.g. BTO");
        options.addOption("t", termOpt, true, "TermID e.g. BTO:0000759");
        options.addOption("n", nameOpt, true, "Entry names must include this");
        options.addOption("f", filterOpt, true, "include: ALL,MATCH,LEAF");
        options.addOption("o", outputOpt, true, "output file");
        
        try {
            // parse the command line arguments
            CommandLine cli = parser.parse(options, args);
            if (cli.hasOption(ontologyOpt)) {
                ontology = cli.getOptionValue(ontologyOpt);
                
            } else {
                System.err.printf("Failed: ontology option required. Available onologies: %s\n", ontologyList);
                System.exit(1);
            }
            if (cli.hasOption(termOpt)) {
                String terms = cli.getOptionValue(termOpt);
                termIDs = Arrays.asList(terms.split(",\\s*"));
            }
            if (cli.hasOption(nameOpt)) {
                String terms = cli.getOptionValue(nameOpt);
                names = Arrays.asList(terms.split(",\\s*"));
            }

            if (cli.hasOption(filterOpt)) {
                String filt = cli.getOptionValue(filterOpt);
                switch (filt) {
                    case "LEAF":
                        filter = Filter.LEAF;
                        break;
                    case "ALL":
                        filter = Filter.ALL;
                        break;
                    case "MATCH":
                        filter = Filter.MATCH;
                        break;
                }
            }
            if (cli.hasOption(outputOpt)) {
                String outputFile = cli.getOptionValue(outputOpt);
                out = new PrintStream(outputFile);
            }
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OntologyLookUp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<OntologyEntry> addChildren(OntologyEntry ontologyEntry) {
        Map<String, String> childTerms = client.getTermChildren(ontologyEntry.getId(), ontologyEntry.getOntology());
        if (childTerms != null && childTerms.size() > 0) {
            List<OntologyEntry> children = new ArrayList<OntologyEntry>();
            for (String termId : childTerms.keySet()) {
                children.add(getOntologyEntry(ontologyEntry.getOntology(), termId, childTerms.get(termId), ontologyEntry));
            }
            ontologyEntry.setChildren(children);
        }
        return ontologyEntry.getChildren();
    }

    public void addDescendants(List<OntologyEntry> ontologyEntries) {
        if (ontologyEntries != null && ontologyEntries.size() > 0) {
            for (OntologyEntry ontologyEntry : ontologyEntries) {
                addDescendants(addChildren(ontologyEntry));
            }
        }
    }
    
    public OntologyEntry getOntologyEntry(String ontology, String termId, String name, OntologyEntry parent) {    
        OntologyEntry ontologyEntry = new OntologyEntry(ontology,  termId, name,parent,null);
        if (name == null) {
            ontologyEntry.setName(client.getTermById(termId, ontology));
        }
//        printOntologyEntry(ontologyEntry);
        return ontologyEntry;        
    }

    public List<OntologyEntry> getOntologyEntries(String ontology, Collection<String> termIds, Filter filter) {
        List<OntologyEntry> ontologyEntries = new ArrayList<OntologyEntry>();
        if ((termIds != null && termIds.size() > 0) || names != null && names.size() > 0) {
            if (termIds != null && termIds.size() > 0) {
                for (String termId : termIds) {
                    ontologyEntries.add(getOntologyEntry(ontology, termId, null, null));
                }
            }
            if (names != null && names.size() > 0) {
                for (String name : names) {
                    Map<String, String> terms = client.getTermsByName(name, ontology);
                    if (terms != null && terms.size() > 0) {
                        List<OntologyEntry> children = new ArrayList<OntologyEntry>();
                        for (String termName : terms.keySet()) {
                            ontologyEntries.add(getOntologyEntry(ontology, terms.get(termName), termName, null));
                        }
                    }
                }
            }
       } else {
            Map<String, String> rootTerms = client.getOntologyRoots(ontology);
            if (rootTerms != null && rootTerms.size() > 0) {
                List<OntologyEntry> children = new ArrayList<OntologyEntry>();
                for (String termId : rootTerms.keySet()) {
                    ontologyEntries.add(getOntologyEntry(ontology, termId, rootTerms.get(termId), null));
                }
            }
        }
        if (filter != Filter.MATCH) {
            addDescendants(ontologyEntries);
        }
        return ontologyEntries;
    }
    public void printOntologyEntries() {
        printOntologyEntries(getOntologyEntries( ontology, termIDs, filter),!filter.equals(Filter.MATCH));
    }
    public void printOntologyEntries(List<OntologyEntry> ontologyEntries, boolean all) {    
        TreeSet<OntologyEntry> ontologySet = new TreeSet<OntologyEntry>(ontologyEntries);
        for (OntologyEntry ontologyEntry : ontologySet) {
            if (all || !ontologyEntry.hasChildren()) {
                printOntologyEntry(ontologyEntry);
            } 
            if (ontologyEntry.hasChildren()) {
                printOntologyEntries(ontologyEntry.getChildren(),all);
            }
        }
    }
    public void printOntologyEntry(OntologyEntry ontologyEntry) {
        out.printf("%s\t[ %s, %s, %s, ]\t%s %s\n", ontologyEntry.getOntology(), ontologyEntry.getOntology(), ontologyEntry.getId(), ontologyEntry.getName(), ontologyEntry.getId(), ontologyEntry.getAncestorNames());
    }

    public static void main(String[] args) { // ontology, id/name, leaf?
        OntologyLookUp ontologyLookUp = new OntologyLookUp(args);
        ontologyLookUp.printOntologyEntries();
    }

}
