package com.spike.jena;

import java.io.IOException;
import java.io.InputStream;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;
import static com.spike.jena.Constants.*;

/**
 * <ul>
 * <li>Author: zhoujg | Date: 2014-3-23 下午12:56:51</li>
 * <li>Description: Jena语义Web编程之HelloWorld</li>
 * </ul>
 */
class HelloSemanticWeb {
	private static final String ONTOLOGY_DIR = "D:/sts-workspace/semanticWebTutorialUsingJena/ontology/";

	// FOAF命名空间
	private static final String FOAF_NS = "http://xmlns.com/foaf/0.1/";
	// FOAF文件绝对路径
	private static final String FOAF_SCHEMA_FN = ONTOLOGY_DIR + "foafSchema.rdf";

	// myfoaf命名空间
	private static final String MYFOAF_NS = "http://blog.sina.com.cn/zhoujiagenontology/helloworld.owl";
	// myfoaf文件绝对路径
	private static final String MYFOAF_DATA_FN = ONTOLOGY_DIR + "foafData.rdf";

	// poeple命名空间
	private static final String PEOPLE_NS = "http://www.people.com";
	// people文件的绝对路径
	private static final String PEOPLE_SCHEMA_FN = ONTOLOGY_DIR + "peopleSchema.rdf";
	private static final String PEOPLE_DATA_FN = ONTOLOGY_DIR + "peopleData.rdf";

	// Jena的RDF模型
	private static Model friendsModel = null;
	// 所有本体的Schema模型
	private static Model schema = null;

	// 推理后模型
	private static InfModel inferredModel = null;

	public static void main(String[] args) throws IOException {
		version3();
	}

	/** version 1: rdf navigate using sparql query */
	public static void version1() throws IOException {
		System.out.println("Load my FOAF friends");
		friendsModel = populateMyFOAFFriends(MYFOAF_DATA_FN);

		System.out.println("Say Hello to myself");
		sayHelloToMyself(friendsModel);

		System.out.println("Say Hello to my friends");
		sayHelloToMyFriends(friendsModel);
	}

	/** version 2: ontology integration using alignment */
	public static void version2() throws IOException {
		System.out.println("Load the data");
		loadABox();

		System.out.println("Generate the schema to contain all ontology's tbox");
		loadTBox();

		// 本体对准
		alignmentInTBox();

		// 绑定到推理机
		bindReasoner();

		// 执行查询
		sayHelloToMyself(inferredModel);
		sayHelloToMyFriends(inferredModel);
	}

	/** version 3: using jena rules */
	public static void version3() throws IOException {
		System.out.println("Load the data");
		loadABox();

		System.out.println("Generate the schema to contain all ontology's tbox");
		loadTBox();

		// 本体对准
		alignmentInTBox();

		// 绑定到规则推理机
		bindJenaReasoner();

		// 执行查询
		sayHelloToGmailFriends(inferredModel);
	}

	private static void bindJenaReasoner() {
		final String rule = "[gmailFriend: (?person <http://xmlns.com/foaf/0.1/mbox_sha1sum> ?email), strConcat(?email, ?lit), regex(?lit, '(.*gmail.com)')"
				+ "-> (?person " + RDF_TYPE + " <http://www.people.com#GmailPerson>)]";
		Reasoner ruleReasoner = new GenericRuleReasoner(Rule.parseRules(rule));
		ruleReasoner = ruleReasoner.bindSchema(schema);
		inferredModel = ModelFactory.createInfModel(ruleReasoner, friendsModel);
	}

	/** 加载所有本体的ABox */
	private static void loadABox() {
		friendsModel = ModelFactory.createDefaultModel();
		InputStream is = FileManager.get().open(MYFOAF_DATA_FN);// MyFOAF的data
		friendsModel.read(is, MYFOAF_NS);

		is = FileManager.get().open(PEOPLE_DATA_FN);// people的data
		friendsModel.read(is, PEOPLE_NS);
	}

	/** 加载所有本体的TBox */
	private static void loadTBox() {
		schema = ModelFactory.createDefaultModel();
		InputStream is = FileManager.get().open(FOAF_SCHEMA_FN);// FOAF的Schema
		schema.read(is, FOAF_NS);

		is = FileManager.get().open(PEOPLE_SCHEMA_FN);// people的Schema
		schema.read(is, PEOPLE_NS);
	}

	/** 本体对准ontology alignment */
	private static void alignmentInTBox() {
		// [1]people:Individual = foaf:Person
		Resource resource = schema.createResource(PEOPLE_NS + "#Individual");
		Property property = schema.createProperty(OWL_URL + "equivalentClass");
		Resource object = schema.createResource(FOAF_URL + "Person");
		schema.add(resource, property, object);

		// [2]people:hasName = foaf:name
		resource = schema.createResource(PEOPLE_NS + "#hasName");
		property = schema.createProperty(OWL_URL + "equivalentProperty");
		object = schema.createResource(FOAF_URL + "name");
		schema.add(resource, property, object);

		// [3]people:hasFriend < foaf:knows
		resource = schema.createResource(PEOPLE_NS + "#hasFriend");
		property = schema.createProperty(RDFS_URL + "subPropertyOf");
		object = schema.createResource(FOAF_URL + "knows");
		schema.add(resource, property, object);

		// [4]myfoaf:me = people:individual_5
		resource = schema.createResource(MYFOAF_NS + "#me");
		property = schema.createProperty(OWL_URL + "sameAs");
		object = schema.createResource(PEOPLE_NS + "#individual_5");
		schema.add(resource, property, object);
	}

	private static void bindReasoner() {
		Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
		reasoner = reasoner.bindSchema(schema);// tbox
		inferredModel = ModelFactory.createInfModel(reasoner, friendsModel);// abox
	}

	/** 填充模型 */
	private static Model fillModel(String base, String filePath) throws IOException {
		Model model = ModelFactory.createDefaultModel();
		InputStream is = FileManager.get().open(filePath);
		model.read(is, base);
		is.close();
		return model;
	}

	/** MyFOAF填充模型 */
	private static Model populateMyFOAFFriends(String filePath) throws IOException {
		return fillModel(MYFOAF_NS, filePath);
	}

	/** RDF模型导航：SPARQL查询 - 查询自己name */
	private static void sayHelloToMyself(Model model) {
		String query = generateMyselfSPARQLQuery();
		sparql(model, query, "?name");
	}

	private static void sayHelloToGmailFriends(Model model) {
		String query = generateGmailFriendsSPARQLQuery();
		sparql(model, query, "?name");
	}

	/** RDF模型导航：SPARQL查询 - 查询朋友name */
	private static void sayHelloToMyFriends(Model model) {
		String query = generateFriendsSPARQLQuery();
		sparql(model, query, "?name");
	}

	/** 在RDF模型中执行SPARQL查询 */
	private static void sparql(Model model, String query, String field) {
		Query q = QueryFactory.create(query);
		QueryExecution qexec = QueryExecutionFactory.create(q, model);
		System.out.println("Plan to run SPARQL query: ");
		System.out.println(BOUNDARY);
		System.out.println(query);
		System.out.println(BOUNDARY);
		ResultSet rs = qexec.execSelect();
		while (rs.hasNext()) {
			QuerySolution qs = rs.nextSolution();
			RDFNode name = qs.get(field);// 暂用RDFNode
			if (name != null) {
				System.out.println("Hello to " + name);
			} else {
				System.out.println("No friends found!");
			}
		}
		qexec.close();
	}

	private static String generateMyselfSPARQLQuery() {
		StringBuilder sb = generateSPARQLPREFIX();
		// 添加查询语句
		sb.append("SELECT DISTINCT ?name").append(NEWLINE).append("WHERE { myfoaf:me foaf:name ?name}").append(NEWLINE);
		return sb.toString();
	}

	private static String generateGmailFriendsSPARQLQuery() {
		StringBuilder sb = generateSPARQLPREFIX();
		sb.append("SELECT DISTINCT ?name WHERE {?name rdf:type people:GmailPerson}");
		return sb.toString();
	}

	private static String generateFriendsSPARQLQuery() {
		StringBuilder sb = generateSPARQLPREFIX();
		sb.append("SELECT DISTINCT ?name").append(NEWLINE).append("WHERE { myfoaf:me foaf:knows ?friend. ")
				.append("?friend foaf:name ?name}").append(NEWLINE);
		return sb.toString();
	}

	/** 添加SPARQL查询前缀PREFIX */
	private static StringBuilder generateSPARQLPREFIX() {
		StringBuilder sb = new StringBuilder();
		sb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>").append(NEWLINE)
				.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>").append(NEWLINE)
				.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>").append(NEWLINE)
				.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>").append(NEWLINE)
				.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>").append(NEWLINE)
				.append("PREFIX myfoaf: <http://blog.sina.com.cn/zhoujiagenontology/helloworld.owl#>").append(NEWLINE)
				.append("PREFIX people: <http://www.people.com#>").append(NEWLINE);
		return sb;
	}
}
