package com.spike.jena.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

/**
 * {@link Model} utilities
 * 
 * @author zhoujiagen
 *
 */
public class ModelUtils {
	private static final Logger logger = Logger.getLogger(ModelUtils.class);

	/**
	 * Fill model using a file
	 * 
	 * @param model
	 *            the filled model1
	 * @param base
	 *            the base uri
	 * @param filePath
	 *            the file's absolute path
	 * @throws IOException
	 */
	public static final void fillModel(Model model, String base, String filePath) {
		try (InputStream is = FileManager.get().open(filePath)) {
			model.read(is, base);
		} catch (Exception e) {
			logger.error("fillModel failed, refer", e);
		}
	}

	/**
	 * empty a directory
	 * 
	 * @param dirPath
	 */
	public static final void cleanDirectory(String dirPath) {
		Path dir = Paths.get(dirPath);
		File[] childFiles = dir.toFile().listFiles();
		if (childFiles != null) {
			for (File child : childFiles) {
				child.delete();
			}
			logger.debug("cleaned " + childFiles.length + " files");
		}
	}
}
