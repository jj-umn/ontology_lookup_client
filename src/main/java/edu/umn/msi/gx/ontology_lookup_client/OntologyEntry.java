/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umn.msi.gx.ontology_lookup_client;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author James E Johnson <jj@umn.edu>
 * @version 
 */
public class OntologyEntry implements Comparable<OntologyEntry> {
    String ontology;
    String id;
    String name;
    OntologyEntry parent;
    List<OntologyEntry> children;

    public OntologyEntry() {
    }

    public OntologyEntry(String ontology, String id, String name) {
        this.ontology = ontology;
        this.id = id;
        this.name = name;
    }

    public OntologyEntry(String ontology, String id, String name, OntologyEntry parent, List<OntologyEntry> children) {
        this.ontology = ontology;
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.children = children;
    }

    public String getOntology() {
        return ontology;
    }

    public void setOntology(String ontology) {
        this.ontology = ontology;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OntologyEntry getParent() {
        return parent;
    }

    public void setParent(OntologyEntry parent) {
        this.parent = parent;
    }

    public List<OntologyEntry> getChildren() {
        return children;
    }

    public void setChildren(List<OntologyEntry> children) {
        this.children = children;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public boolean hasChildren() {
        return children != null && children.size() > 0;
    }
    
    public List<String> getAncestryNames() {
        List<String> ancestors = new ArrayList<String>();
        for (OntologyEntry entry = this; entry != null; entry = entry.getParent()) {
            ancestors.add(0,entry.getName());            
        }
        return ancestors;        
     }

    public String getAncestorNames() {
        List<String> ancestors = getAncestryNames();
        StringBuilder sb = new StringBuilder();
        for (String name : ancestors) {
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(name);
        }
        return sb.toString();
    }

    @Override
    public int compareTo(OntologyEntry o) {
        return this.getId().compareTo(o.getId());
    }
}
