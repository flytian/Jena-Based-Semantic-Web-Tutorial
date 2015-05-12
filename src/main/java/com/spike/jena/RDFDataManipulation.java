package com.spike.jena;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.spike.jena.util.ModelUtils;
import com.spike.jena.util.SPARQLUtils;

/**
 * RDF data manipulation demostration<br/>
 * 
 * Data: FOAF, see <a href="http://xmlns.com/foaf/spec/">FOAF Specification</a>
 * for concrete details
 * 
 * @author zhoujiagen
 *
 */
public class RDFDataManipulation {
	private static final String ONTOLOGY_DIR = "ontology/";

	private static final String FOAF_BASE_URI = "http://xmlns.com/foaf/0.1/";
	private static final String FOAF_SCHEMA_FilePath = ONTOLOGY_DIR + "foafSchema.rdf";

	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel();
		ModelUtils.fillModel(model, FOAF_BASE_URI, FOAF_SCHEMA_FilePath);

		// renderer all namespaces
		System.out.println(model.getNsPrefixMap());

		// insert foaf:me rdf:type foaf:Person
		Resource me = model.createResource(FOAF_BASE_URI + "me");
		Property rdfType = model.getProperty(Constants.RDF_TYPE_URL);
		Resource FOAFPersonClass = model.getResource(FOAF_BASE_URI + "Person");
		model.add(me, rdfType, FOAFPersonClass);

		// query the inserted facts
		StringBuilder query = SPARQLUtils.getRegualrSPARQLPREFIX();
		query.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>").append(Constants.NEWLINE);
		query.append("SELECT DISTINCT ?person WHERE {?person rdf:type foaf:Person}");
		SPARQLUtils.query(model, query.toString(), "?person");
	}
}
