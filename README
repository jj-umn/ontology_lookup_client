ontology_lookup_client
======================

Client application to retrieve entries from the EBI ontology web service:
http://www.ebi.ac.uk/ontology-lookup/


This uses the OLS SOAP Demo client (java): http://www.ebi.ac.uk/ontology-lookup/ols-client.tar.gz 

The application will retrieves ontology entries and output a 3 column tabular file, e.g. :

BTO	[ BTO, BTO:0000608, liver adenocarcinoma cell ]	BTO:0000608 liver adenocarcinoma cell
BTO	[ BTO, BTO:0000578, hepatoma cell line ]	BTO:0000578 liver adenocarcinoma cell; hepatoma cell line



The columns are:
1.	The ontology ID
2.	A triplet with: [ Ontology, EntryID, description ]
3.	The EntryID followed by semicolon-separated list of descriptions in the hierarchical ontology path to this entry


This output can be used in a Galaxy select parameter from either a dataset or tool-data .loc file 


