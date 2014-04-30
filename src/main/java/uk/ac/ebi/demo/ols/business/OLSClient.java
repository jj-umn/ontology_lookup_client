package uk.ac.ebi.demo.ols.business;

import uk.ac.ebi.demo.ols.soap.Query;
import uk.ac.ebi.demo.ols.soap.QueryServiceLocator;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.*;

/**
 * User: rcote
 * Date: 18-May-2007
 * Time: 12:21:47
 * $Id: $
 *
 * This is the main business class that will communicate with the OLS
 * webservice and serve data to the GUI classes.
 */
public class OLSClient {

    /**
     * calls OLS webserver and gets map of all ontologies. It will then
     * return a list of all ontology labels.
     * @return List of all ontology labels.
     * List should not be null.
     */
    public List<String> getOntologies(){

        List retval = new ArrayList<String>();
        QueryServiceLocator locator = new QueryServiceLocator();
        try {
            Query service = locator.getOntologyQuery();
            HashMap ontologies = service.getOntologyNames();
            if (ontologies != null){
                retval.addAll(ontologies.keySet());
                Collections.sort(retval);
            }

        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return retval;
    }

    /**
     * calls OLS webserver and gets root terms of an ontology
     * @return Map of root terms - key is termId, value is termName.
     * Map should not be null.
     */
    public Map<String, String> getOntologyRoots(String ontology) {

        Map retval = new HashMap<String, String>();
        QueryServiceLocator locator = new QueryServiceLocator();
        try {
            Query service = locator.getOntologyQuery();
            HashMap roots = service.getRootTerms(ontology);
            if (roots != null){
                retval.putAll(roots);
            }

        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return retval;

    }

    /**
     * calls OLS webserver and gets child terms for a termId
     * @return Map of child terms - key is termId, value is termName.
     * Map should not be null.
     */
    public Map<String, String> getTermChildren(String ontology, String termId) {

        Map retval = new HashMap<String, String>();
        QueryServiceLocator locator = new QueryServiceLocator();
        try {
            Query service = locator.getOntologyQuery();
            HashMap terms = service.getTermChildren(ontology, termId, 1, null);
            if (terms != null){
                retval.putAll(terms);
            } 
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return retval;

    }

    /**
     * calls OLS webserver and gets metadata for a termId
     * @return Map of metadata - key is data type, value is data value.
     * Map should not be null.
     */
    public Map<String, String> getTermMetadata(String termId, String ontology) {

        Map retval = new HashMap<String, String>();
        QueryServiceLocator locator = new QueryServiceLocator();
        try {
            Query service = locator.getOntologyQuery();
            HashMap terms = service.getTermMetadata(termId, ontology);
            if (terms != null){
                retval.putAll(terms);
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return retval;

    }

    /**
     * calls OLS webserver and gets suggestions of terms for a given query
     * @return Map of suggested terms - key is termId, value is termName.
     * Map should not be null.
     */
    public Map<String, String> getTermsByName(String text, String ontology) {

        Map retval = new HashMap<String, String>();
        QueryServiceLocator locator = new QueryServiceLocator();
        try {
            Query service = locator.getOntologyQuery();
            HashMap terms = service.getTermsByName(text, ontology, true);
            if (terms != null){
                retval.putAll(terms);
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return retval;

    }
    
 
    /**
     * calls OLS webserver and gets name of term for a given termId
     * @return Map of suggested terms - key is termId, value is termName.
     * Map should not be null.
     */
    public String getTermById(String termId, String ontology) {
        String name = null;
        QueryServiceLocator locator = new QueryServiceLocator();
        try {
            Query service = locator.getOntologyQuery();
            name = service.getTermById(termId, ontology);
         } catch (ServiceException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return name;

    }
/**
     * calls OLS webserver and gets root terms of an ontology
     * @return Map of root terms - key is termId, value is termName.
     * Map should not be null.
     */
    public Map<String, String> getAllTermsFromOntology(String ontology) {

        Map retval = new HashMap<String, String>();
        QueryServiceLocator locator = new QueryServiceLocator();
        try {
            Query service = locator.getOntologyQuery();
            HashMap roots = service.getAllTermsFromOntology(ontology);
            if (roots != null){
                retval.putAll(roots);
            }

        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return retval;

    }

}
