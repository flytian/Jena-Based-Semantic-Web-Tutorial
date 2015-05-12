package com.spike.jena;

/**
 * Application Constant
 * 
 * @author zhoujiagen
 *
 */
public class Constants {
	public static final String NEWLINE = System.getProperty("line.separator");
	public static final String TAB = System.getProperty("\t");

	public static final String BOUNDARY = "-----------------------------------------------------------------------";

	// default namespace
	public static final String RDF_URL = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDFS_URL = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String OWL_URL = "http://www.w3.org/2002/07/owl#";
	public static final String XSD_URL = "http://www.w3.org/2001/XMLSchema#";
	public static final String FOAF_URL = "http://xmlns.com/foaf/0.1/";

	public static final String RDF_TYPE_INSPARQL = "<" + RDF_URL + "type>";
	public static final String RDF_TYPE_URL = RDF_URL + "type";

}