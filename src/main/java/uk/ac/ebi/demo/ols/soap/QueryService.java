/**
 * QueryService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.ac.ebi.demo.ols.soap;

public interface QueryService extends javax.xml.rpc.Service {
    public java.lang.String getOntologyQueryAddress();

    public uk.ac.ebi.demo.ols.soap.Query getOntologyQuery() throws javax.xml.rpc.ServiceException;

    public uk.ac.ebi.demo.ols.soap.Query getOntologyQuery(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
