/**
 * Name:  		Adelle Paulette Rivera
 * Class:  		CS212
 * Assignment:  Project 3
 **/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import org.apache.log4j.Logger;

/**
 * The QuerySearcher class has multithreaded capabilities. Each thread will do a
 * search of the query word in the inverted index. It will have methods to do an
 * exact search of the word and a partial search of the word and add it the
 * collection
 * 
 * @author Adelle Rivera
 * 
 */
public class QuerySearcher {

	private static Logger log = Logger.getLogger(QuerySearcher.class.getName());

	private TreeMap<String, HashMap<String, SearchResults>> results;
	private ArrayList<String> fullQueryList;
	private WorkQueue workers;
	private MultiReaderLock lock;
	private int pending;

	public QuerySearcher(int threads) {

		results = new TreeMap<String, HashMap<String, SearchResults>>();
		fullQueryList = new ArrayList<String>();
		workers = new WorkQueue(threads);
		lock = new MultiReaderLock();
		pending = 0;

		log.debug("Maximum of " + threads + " worker threads.");
	}

	private synchronized int getPending() {
		return pending;
	}

	private synchronized void updatePending(int amount) {
		pending += amount;

		if (pending <= 0) {
			notifyAll();
		}
	}

	/**
	 * This method gets the query words, checks the inverted index if there is
	 * an exact and partial match.
	 * 
	 * @param queryFileArg
	 *            The query file given by user.
	 * @param queryResultsFile
	 *            The results file name.
	 * 
	 * @throws IOException
	 *             Exception thrown because
	 */

	public void queryFileSearch(InvertedIndex iIndex, String queryFileArg)
			throws IOException {

		File fileQuery = new File(queryFileArg);

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileQuery));

			try {
				String line = null;

				// read line from query file
				while ((line = br.readLine()) != null) {

					log.debug("Updating results for " + line + ".");
					lock.acquireWriteLock();
					results.put(line, new HashMap<String, SearchResults>());
					
					fullQueryList.add(line);
					lock.releaseWriteLock();

					workers.execute(new QueryWorker(line, iIndex));

				}

				while (getPending() > 0) {

					log.info("Still working...");

					synchronized (this) {
						try {
							wait(1000);
						} catch (InterruptedException ex) {
							log.warn("Interrupted while waiting.", ex);
						}

					}
				}

				br.close();

			} catch (IOException e) {
				System.out.println("An error has occurred.");
				log.error("An error occurred searching for query words.");
			}

		} catch (FileNotFoundException e) {
			System.out
					.println("Query file not found.  Please enter a valid query file.");
			log.error("Query file was not found.");
		}

	}

	private class QueryWorker implements Runnable {

		String queryLine;
		InvertedIndex index;

		public QueryWorker(String queryLine, InvertedIndex index) {
			this.queryLine = queryLine;
			this.index = index;
			updatePending(1);
		}

		@Override
		public void run() {
			log.debug("Starting work on " + queryLine + ".");

			ArrayList<String> qWords = new ArrayList<String>();

			for (String queryWord : queryLine.split(" ")) {

				queryWord = queryWord.replaceAll("-", "");
				queryWord = queryWord.toLowerCase();
				qWords.add(queryWord);

			}

			HashMap<String, SearchResults> subResults = index
					.queryResult(qWords);

			lock.acquireWriteLock();
			results.put(queryLine, subResults);
			lock.releaseWriteLock();

			updatePending(-1);
			log.debug("Finished work on " + queryLine + ".");

		}
	}

	public void shutdown() {
		workers.shutdown();
	}

	/**
	 * This method will print out query results.
	 * 
	 * @param resultsFile
	 *            The results file name given by user.
	 */
	public void printQueryResults(String resultsFile) throws IOException {

		lock.acquireReadLock();
		
		PrintWriter pw = new PrintWriter(new FileWriter(resultsFile));

		for (String query : fullQueryList) {

			pw.println(query);
			log.debug("Printing " + query);

			HashMap<String, SearchResults> queryResults = results.get(query);
			ArrayList<SearchResults> searchResults = new ArrayList<SearchResults>(
					queryResults.values());
			Collections.sort(searchResults);

			for (SearchResults result : searchResults) {
				pw.println(result);
			}
			pw.println();
		}

		pw.flush();
		pw.close();
		lock.releaseReadLock();

	}

}