package com.spike.jena.arq;

import static com.spike.jena.Constants.BOUNDARY;
import static com.spike.jena.Constants.NEWLINE;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

/**
 * SPARQL SELECT Query using remote service(SPARQL endpoint)
 * 
 * @author zhoujiagen<br/>
 *         Jul 5, 2015 6:31:30 PM
 */
public class SelectQueryUsingRemoteService {

	private static final String SERVICE_URL = "http://www.sparql.org/books/sparql";

	public static void main(String[] args) {
		// populate SPARQL SELECT Query string
		StringBuilder sb = new StringBuilder();
		sb.append("PREFIX books:   <http://example.org/book/>").append(NEWLINE);
		sb.append("PREFIX dc:      <http://purl.org/dc/elements/1.1/>").append(NEWLINE);
		sb.append("SELECT ?book ?title").append(NEWLINE);
		sb.append("WHERE {").append(NEWLINE);
		sb.append("		?book dc:title ?title").append(NEWLINE);
		sb.append("}").append(NEWLINE);

		// query from remote service
		QueryExecution qexec = QueryExecutionFactory.sparqlService(SERVICE_URL, sb.toString());

		System.out.println("Plan to run remote SPARQL query: ");
		System.out.println(BOUNDARY);
		System.out.println(sb.toString());
		System.out.println(BOUNDARY);

		ResultSet rs = qexec.execSelect();

		// use result set formatter
		ResultSetFormatter.out(rs);

		qexec.close();
	}
}
