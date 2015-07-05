package com.spike.jena.arq;

import static com.spike.jena.Constants.BOUNDARY;
import static com.spike.jena.Constants.NEWLINE;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

/**
 * SPARQL SELECT Query using a dataset which contains multiple models
 * 
 * @author zhoujiagen<br/>
 *         Jul 5, 2015 6:00:56 PM
 */
public class SelectQueryUsingDataset {

	private static Dataset dataset = null;

	@BeforeClass
	public static void setUpBeforeClass() {
		String dftGraphURI = "http://www.w3.org/People/Berners-Lee/card#";
		List<String> namedGraphURIs = new ArrayList<String>();
		namedGraphURIs.add("http://www.koalie.net/foaf.rdf");
		// error
		// namedGraphURIs.add("http://heddley.com/edd/foaf.rdf");
		// 404
		// namedGraphURIs.add("http://www.cs.umd.edu/~hendler/2003/foaf.rdf");
		namedGraphURIs.add("http://www.dajobe.org/foaf.rdf");
		namedGraphURIs.add("http://www.isi.edu/~gil/foaf.rdf");
		namedGraphURIs.add("http://www.ivan-herman.net/foaf.rdf");
		namedGraphURIs.add("http://www.kjetil.kjernsmo.net/foaf");
		namedGraphURIs.add("http://www.lassila.org/ora.rdf");
		// no response
		// namedGraphURIs.add("http://www.mindswap.org/2004/owl/mindswappers");

		dataset = DatasetFactory.create(dftGraphURI, namedGraphURIs);

		assertNotNull(dataset);
	}

	@Test
	public void query() {
		// populate SPARQL SELECT Query string
		StringBuilder sb = new StringBuilder();
		sb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>").append(NEWLINE);
		sb.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>").append(NEWLINE);
		sb.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>").append(NEWLINE);
		sb.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>").append(NEWLINE);
		sb.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>").append(NEWLINE);
		sb.append("PREFIX myfoaf: <http://blog.sina.com.cn/zhoujiagenontology/helloworld.owl#>").append(NEWLINE);
		sb.append("PREFIX people: <http://www.people.com#>").append(NEWLINE);
		sb.append("SELECT *").append(NEWLINE);
		sb.append("WHERE {").append(NEWLINE);
		sb.append("		GRAPH ?originGraph {").append(NEWLINE);
		sb.append(" 			_:blank1 foaf:knows _:blank2.").append(NEWLINE);
		sb.append("				_:blank2 rdf:type foaf:Person;").append(NEWLINE);
		sb.append("							foaf:nick ?nickname;").append(NEWLINE);
		sb.append("							foaf:name ?realname").append(NEWLINE);
		sb.append("		}").append(NEWLINE);
		sb.append("}").append(NEWLINE);

		// generate Query
		Query query = QueryFactory.create(sb.toString());

		// the binding variable
		// String originGraph = "?originGraph";
		// String nickname = "?nickname";
		// String realname = "?realname";

		// the query result
		// int result = 0;

		// execute Query using dataset
		QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
		System.out.println("Plan to run SPARQL query: ");
		System.out.println(BOUNDARY);
		System.out.println(query);
		System.out.println(BOUNDARY);
		ResultSet rs = qexec.execSelect();

		// while (rs.hasNext()) {
		// QuerySolution qs = rs.nextSolution();
		// RDFNode originGraphNode = qs.get(originGraph);
		// RDFNode nicknameNode = qs.get(nickname);
		// RDFNode realnameNode = qs.get(realname);
		//
		// System.out.println(originGraphNode + TAB + nicknameNode + TAB +
		// realnameNode);
		//
		// }

		// use result set formatter
		ResultSetFormatter.out(rs, query);

		qexec.close();
	}
}
