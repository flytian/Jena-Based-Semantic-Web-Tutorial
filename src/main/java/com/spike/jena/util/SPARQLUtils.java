package com.spike.jena.util;

import static com.spike.jena.Constants.BOUNDARY;
import static com.spike.jena.Constants.NEWLINE;
import static com.spike.jena.Constants.TAB;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * SPARQL Utilities
 * 
 * @author zhoujiagen
 *
 */
public class SPARQLUtils {
	/**
	 * RDF Navigation using SPARQL Query
	 * 
	 * @param model
	 *            the RDF model
	 * @param query
	 *            SPARQL Query String
	 * @param queryFields
	 *            the placeholder of filed in parameter query(sample: ?name)
	 */
	public static void query(final Model model, final String query, final String... queryFields) {
		Query q = QueryFactory.create(query);
		QueryExecution qexec = QueryExecutionFactory.create(q, model);
		System.out.println("Plan to run SPARQL query: ");
		System.out.println(BOUNDARY);
		System.out.println(query);
		System.out.println(BOUNDARY);

		ResultSet rs = qexec.execSelect();
		rendererResultSet(rs, queryFields);

		System.out.println(BOUNDARY);

		qexec.close();
	}

	/**
	 * RDF Navigation using remote SPARQL Query
	 * 
	 * @param service
	 *            the SAPRQL end point URL
	 * @param query
	 *            SPARQL Query String
	 * @param queryField
	 *            the placeholder of filed in parameter query(sample: ?name)
	 */
	public static void queryRemote(final String service, final String query, String... queryFields) {
		if (queryFields == null || queryFields.length == 0) {
			return;
		}

		QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query);

		System.out.println("Plan to run remote SPARQL query: ");
		System.out.println(BOUNDARY);
		System.out.println(query);
		System.out.println(BOUNDARY);

		ResultSet rs = qexec.execSelect();
		rendererResultSet(rs, queryFields);

		System.out.println(BOUNDARY);

		qexec.close();
	}

	private static void rendererResultSet(ResultSet rs, String... queryFields) {
		System.out.println("Result:");
		int queryFieldSize = queryFields.length;
		for (int i = 0; i < queryFieldSize; i++) {
			System.out.print(queryFields[i] + TAB);
		}
		System.out.println();

		while (rs.hasNext()) {
			QuerySolution qs = rs.nextSolution();
			for (int i = 0; i < queryFieldSize; i++) {
				RDFNode name = qs.get(queryFields[i]);
				if (name != null) {
					System.out.print(name + TAB);
				} else {
					System.out.print("NULL" + TAB);
				}
			}
			System.out.println();
		}
	}

	/**
	 * generate regular SPARQL Query prefixes
	 * 
	 * @return
	 */
	public static StringBuilder getRegualrSPARQLPREFIX() {
		StringBuilder sb = new StringBuilder();
		sb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>").append(NEWLINE)
				.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>").append(NEWLINE)
				.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>").append(NEWLINE)
				.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>").append(NEWLINE)
		// .append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>").append(NEWLINE)
		// .append("PREFIX myfoaf: <http://blog.sina.com.cn/zhoujiagenontology/helloworld.owl#>").append(NEWLINE)
		// .append("PREFIX people: <http://www.people.com#>").append(NEWLINE)
		;
		return sb;
	}
}
