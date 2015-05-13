package com.spike.jena.util;

import static com.spike.jena.Constants.NEWLINE;

import org.junit.Test;

public class SPARQLUtilsTest {
	/**
	 * Caution: the service is site on a Fuseki running on local tomcat7.0
	 */
	@Test
	public void queryRemote() {
		final String service = "http://localhost:3030/fuseki/foaf/query";

		StringBuilder query = new StringBuilder();

		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
		query.append(NEWLINE);
		query.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>");
		query.append(NEWLINE);
		query.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>");
		query.append(NEWLINE);
		query.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
		query.append(NEWLINE);
		query.append("PREFIX hw: <http://blog.sina.com.cn/zhoujiagenontology/helloworld.owl#>");
		query.append(NEWLINE);
		query.append("SELECT ?predicate ?object ");
		query.append(NEWLINE);
		query.append("WHERE {   hw:me ?predicate ?object }");
		query.append(NEWLINE);
		query.append("LIMIT 25");

		SPARQLUtils.queryRemote(service, query.toString(), "?predicate", "?object");
	}
}
