package com.spike.jena.arq;

import static com.spike.jena.Constants.BOUNDARY;
import static com.spike.jena.Constants.NEWLINE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.spike.jena.Constants;
import com.spike.jena.util.ModelUtils;

/**
 * SPARQL SELECT
 * 
 * @author zhoujiagen<br/>
 *         Jul 5, 2015 5:16:11 PM
 */
public class SelectQueryUsingModel {

	// rdf data directory
	private static final String DATA_DIR = "ontology/";

	// namespace of myfoaf
	private static final String NAMESPACE = "http://blog.sina.com.cn/zhoujiagenontology/helloworld.owl";
	// absolute file path of myfoaf RDF file
	private static final String FILE_PATH = DATA_DIR + "foafData.rdf";

	// the model
	private static Model model = null;

	@BeforeClass
	public static void setUpBeforeClass() {
		// fill the model
		model = ModelUtils.fillEmptyModel(NAMESPACE, FILE_PATH);

		assertNotNull(model);
	}

	@AfterClass
	public static void setUpAfterClass() {
		if (model != null) {
			model.close();
		}
	}

	@Test
	public void queryWithSingleVariable() {

		// populate SPARQL SELECT Query string
		StringBuilder sb = new StringBuilder();
		sb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>").append(NEWLINE);
		sb.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>").append(NEWLINE);
		sb.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>").append(NEWLINE);
		sb.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>").append(NEWLINE);
		sb.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>").append(NEWLINE);
		sb.append("PREFIX myfoaf: <http://blog.sina.com.cn/zhoujiagenontology/helloworld.owl#>").append(NEWLINE);
		sb.append("PREFIX people: <http://www.people.com#>").append(NEWLINE);
		sb.append("SELECT DISTINCT ?name").append(NEWLINE);
		sb.append("WHERE { myfoaf:me foaf:name ?name}").append(NEWLINE);

		// generate Query
		Query query = QueryFactory.create(sb.toString());

		// the binding variable
		String field = "?name";

		// the query result
		String result = null;

		// execute Query
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		System.out.println("Plan to run SPARQL query: ");
		System.out.println(BOUNDARY);
		System.out.println(query);
		System.out.println(BOUNDARY);
		ResultSet rs = qexec.execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.nextSolution();
			RDFNode name = qs.get(field);
			if (name != null) {
				System.out.println(name);
				result = name.toString();
			} else {
				System.out.println("No result!");
			}
		}
		qexec.close();

		// assertion
		assertEquals("Semantic Web^^http://www.w3.org/2001/XMLSchema#string", result);
	}

	@Test
	public void queryWithMultipleVariable() {

		// populate SPARQL SELECT Query string
		StringBuilder sb = new StringBuilder();
		sb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>").append(NEWLINE);
		sb.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>").append(NEWLINE);
		sb.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>").append(NEWLINE);
		sb.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>").append(NEWLINE);
		sb.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>").append(NEWLINE);
		sb.append("PREFIX myfoaf: <http://blog.sina.com.cn/zhoujiagenontology/helloworld.owl#>").append(NEWLINE);
		sb.append("PREFIX people: <http://www.people.com#>").append(NEWLINE);
		sb.append("SELECT DISTINCT ?prop ?obj").append(NEWLINE);
		sb.append("WHERE { myfoaf:me ?prop ?obj}").append(NEWLINE);

		// generate Query
		Query query = QueryFactory.create(sb.toString());

		// the binding variable
		String prop = "?prop";
		String obj = "?obj";

		int result = 0;

		// execute Query
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		System.out.println("Plan to run SPARQL query: ");
		System.out.println(BOUNDARY);
		System.out.println(query);
		System.out.println(BOUNDARY);
		ResultSet rs = qexec.execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.nextSolution();
			RDFNode propRDFNode = qs.get(prop);
			RDFNode objRDFNode = qs.get(obj);

			System.out.println(propRDFNode + Constants.TAB + objRDFNode);
			result++;
		}
		qexec.close();

		// assertion
		assertTrue(result > 0);
	}

}
