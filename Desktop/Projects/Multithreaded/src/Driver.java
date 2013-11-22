/**
 * Name:  		Adelle Paulette Rivera
 * Class:  		CS212
 * Assignment:  Project 3
 **/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * This is the Driver class that takes the input given by the user, creates the
 * inverted index, takes the query file given by the user and initiates the
 * search of the query words through the inverted index.
 * 
 * @author Adelle Rivera
 * 
 */
public class Driver {

	public static void main(String[] args) {

		Logger log = Logger.getLogger(Driver.class.getName());

		if (new File("log4j.properties").canRead() == false) {
			BasicConfigurator.configure();
		} else {
			PropertyConfigurator.configure("log4j.properties");
		}

		String searchDirectory = ArgumentParser.getFlag(args, "-d");
		String queryFile = ArgumentParser.getFlag(args, "-q");
		String threads = ArgumentParser.getFlag(args, "-t");

		if (searchDirectory != null && queryFile != null && threads != null) {

			try {

				int numThreads = Integer.parseInt(threads);

				InvertedIndex index = new InvertedIndex();
				TextParser tp = new TextParser(numThreads);
				QuerySearcher qs = new QuerySearcher(numThreads);

				if (ArgumentParser.containsFlag(args, "-i") == true) {
					log.info("Printed inverted index flag was given.");
					tp.indexFolder(index, searchDirectory);
					log.info("Creating inverted index for \"" + searchDirectory
							+ "\".");
					index.printFile("invertedindex.txt");
					tp.shutdown();
					qs.queryFileSearch(index, queryFile);
					qs.printQueryResults("searchresults.txt");
					qs.shutdown();

				} else {
					log.info("No inverted index flag was indicated.  Inverted index will not be printed.");
					tp.indexFolder(index, searchDirectory);
					log.info("Creating inverted index for \"" + searchDirectory
							+ "\".");
					tp.shutdown();
					qs.queryFileSearch(index, queryFile);
					qs.printQueryResults("searchresults.txt");
					qs.shutdown();
				}

			} catch (NullPointerException e) {
				log.error("There was an error that occurred.  No directory was entered or incorrect directory.");
				System.out
						.println("There was an error that occurred.  Please enter '-d' and a directory to be searched.");
				System.exit(0);

			} catch (FileNotFoundException e) {
				log.error("Invalid file.  File was not found.");
				System.out
						.println("File was not found.  Please enter a valid file.");
				System.exit(0);

			} catch (IOException e) {
				log.error("An error occurred.");
				System.out.println("An error has occurred.");
				System.exit(0);
			}
		} else {
			log.error("Incorrect flags or files entered.");
			System.out
					.println("An error occurred.  Please enter '-d' and a directory to be searched"
							+ " or enter '-q' and a query file to be used or -t and the number of threads to be used.");
		}

	}

}