package com.spike.jena;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.spike.jena.util.ModelUtils;
import com.spike.jena.util.SPARQLUtils;

/**
 * Jena's TDB HelloWorld
 * 
 * @author zhoujiagen
 *
 */
public class TDBManipulation {
	private static final Logger logger = Logger.getLogger(TDBManipulation.class);

	private static final String TDB_DIR = "tdb";

	private static final String ONTOLOGY_DIR = "D:/sts-workspace/semanticWebTutorialUsingJena/ontology/";
	private static final String FOAF_BASE_URI = "http://xmlns.com/foaf/0.1/";
	private static final String FOAF_SCHEMA_FilePath = ONTOLOGY_DIR + "foafSchema.rdf";

	public static void main(String[] args) {
		// clean the TDB directory
		// ModelUtils.cleanDirectory(TDB_DIR);

		demoOfUsingADirectory();

		// demoOfUsingAnAssemblerFile();
	}

	/**
	 * see <a href="http://jena.apache.org/documentation/tdb/assembler.html">TDB
	 * Assembler</a> for concrete details
	 */
	static void demoOfUsingAnAssemblerFile() {
		// Assembler way: Make a TDB-back Jena model in the named directory.
		// This way, you can change the model being used without changing the
		// code.
		// The assembler file is a configuration file.
		// The same assembler description will work in Fuseki.
		String assemblerFile = "Store/tdb-assembler.ttl";
		Dataset dataset = TDBFactory.assembleDataset(assemblerFile); // ...

		// read something
		logger.debug("read tx start!!!");
		demoOfReadTransaction(dataset);
		logger.debug("read tx end!!!");

		// write something
		logger.debug("write tx start!!!");
		demoOfWriteTransaction(dataset);
		logger.debug("write tx end!!!");

		// read again
		logger.debug("read tx start!!!");
		demoOfReadTransaction(dataset);
		logger.debug("read tx end!!!");

		dataset.close();
	}

	static void demoOfUsingADirectory() {
		// Make a TDB-backed dataset
		String directory = TDB_DIR;

		// read something
		Dataset dataset = TDBFactory.createDataset(directory);
		logger.debug("read tx start!!!");
		demoOfReadTransaction(dataset);
		logger.debug("read tx end!!!");
		dataset.close();

		// write something
		dataset = TDBFactory.createDataset(directory);
		logger.debug("write tx start!!!");
		demoOfWriteTransaction(dataset);
		logger.debug("write tx end!!!");
		dataset.close();

		// read again
		dataset = TDBFactory.createDataset(directory);
		logger.debug("read tx start!!!");
		demoOfReadTransaction(dataset);
		logger.debug("read tx end!!!");
		dataset.close();
	}

	private static void demoOfReadTransaction(Dataset dataset) {
		dataset.begin(ReadWrite.READ);

		// Get model inside the transaction
		Model model = dataset.getDefaultModel();

		// query the inserted facts
		StringBuilder query = SPARQLUtils.getRegualrSPARQLPREFIX();
		query.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>").append(Constants.NEWLINE);
		query.append("SELECT DISTINCT ?person WHERE {?person rdf:type foaf:Person}");
		SPARQLUtils.query(model, query.toString(), "?person");

		model.close();// closing the model to flush

		dataset.end();
	}

	private static void demoOfWriteTransaction(Dataset dataset) {
		dataset.begin(ReadWrite.WRITE);

		Model model = dataset.getDefaultModel();

		ModelUtils.fillModel(model, FOAF_BASE_URI, FOAF_SCHEMA_FilePath);

		// insert foaf:me rdf:type foaf:Person
		Resource me = model.createResource(FOAF_BASE_URI + "me");
		Property rdfType = model.getProperty(Constants.RDF_TYPE_URL);
		Resource FOAFPersonClass = model.getResource(FOAF_BASE_URI + "Person");
		model.add(me, rdfType, FOAFPersonClass);
		// model.write(System.out);// for debug

		model.close();// closing the model to flush

		dataset.commit();

		dataset.end();
	}
}
