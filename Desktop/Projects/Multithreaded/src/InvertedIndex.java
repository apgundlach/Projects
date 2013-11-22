/**
 * Name:  		Adelle Paulette Rivera
 * Class:  		CS212
 * Assignment:  Project 3
 * 
 **/

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * InvertedIndex class builds up the Inverted Index of files
 * 
 * @author Adelle Rivera
 * 
 */
public class InvertedIndex {

	private static Logger log = Logger.getLogger(InvertedIndex.class.getName());

	private TreeMap<String, TreeMap<String, ArrayList<Integer>>> index;

	private MultiReaderLock lock;

	// Constructor
	public InvertedIndex() {
		index = new TreeMap<String, TreeMap<String, ArrayList<Integer>>>();
		lock = new MultiReaderLock();
		log.info("Creating inverted index.");
	}

	/**
	 * This method will add the word in the inverted index if it doesn't exist,
	 * add the filename associated with word if it exists or if doesn't exist
	 * add the new filename or add the location of the word in that particular
	 * file
	 * 
	 * @param word
	 *            The word in the file
	 * @param fileName
	 *            The file name the word is in
	 * @param location
	 *            The location of the word in the file
	 */
	public void addInfo(String word, String fileName, int location) {

		lock.acquireWriteLock();
		log.debug("Adding " + word + " to index.");
		if (index.get(word) == null) {
			index.put(word, new TreeMap<String, ArrayList<Integer>>());
		}

		TreeMap<String, ArrayList<Integer>> fileToIndex = index.get(word);

		if (fileToIndex.get(fileName) == null) {
			fileToIndex.put(fileName, new ArrayList<Integer>());
		}

		ArrayList<Integer> positions = fileToIndex.get(fileName);
		positions.add(location);
		lock.releaseWriteLock();

	}

	public void addAllInfo(InvertedIndex subindex) {

		lock.acquireWriteLock();
		for (String word : subindex.index.keySet()) {
			if (index.get(word) == null) {
				index.put(word, new TreeMap<String, ArrayList<Integer>>());
			}

			TreeMap<String, ArrayList<Integer>> fileToIndex = index.get(word);
			TreeMap<String, ArrayList<Integer>> subfile = subindex.index
					.get(word);

			for (String file : subfile.keySet()) {

				if (fileToIndex.get(file) == null) {
					fileToIndex.put(file, new ArrayList<Integer>());
				}

				ArrayList<Integer> subpositions = subfile.get(file);
				ArrayList<Integer> positions = fileToIndex.get(file);
				positions.addAll(subpositions);
			}

		}
		lock.releaseWriteLock();
	}

	/**
	 * This method will create the index file that gets printed out
	 * 
	 * @param indexFile
	 *            The file name provided for the index file that will be printed
	 *            out
	 * 
	 * @throws IOException
	 *             This method throws this exception in case an error occurs
	 *             with the file
	 **/

	public void printFile(String indexFile) throws IOException {

		lock.acquireReadLock();

		PrintWriter pw = new PrintWriter(new FileWriter(indexFile));

		for (String word : index.keySet()) {
			pw.println(word);
			TreeMap<String, ArrayList<Integer>> instanceMap = index.get(word);

			for (String fileName : instanceMap.keySet()) {
				pw.print("\"" + fileName + "\"");
				ArrayList<Integer> positions = instanceMap.get(fileName);

				for (Integer x : positions) {
					pw.print(", " + x);
				}

				pw.println();
			}

			pw.println();
		}

		pw.close();
		lock.releaseReadLock();
	}

	/**
	 * This method will get the keys (file names of the map associated with the
	 * query word in inverted index
	 * 
	 * @param word
	 *            The word in the index that is being searched for.
	 * 
	 * @return files Returns the files associated with the given word
	 */
	private Set<String> getFileNameSet(String word) {
		TreeMap<String, ArrayList<Integer>> fileToIndex = index.get(word);
		Set<String> files = fileToIndex.keySet();
		return files;
	}

	/**
	 * This method gives the number of elements in the file list.
	 * 
	 * @param word
	 *            The word from inverted index
	 * @param fileName
	 *            The file name where the word is located
	 * 
	 * @return frequency Returns the size of the array associated with the word
	 */
	public Integer getWordFrequency(String word, String fileName) {
		lock.acquireReadLock();
		Integer frequency = index.get(word).get(fileName).size();
		lock.releaseReadLock();
		return frequency;
	}

	/**
	 * This method gives the first instance/location of the word in the file
	 * 
	 * @param word
	 *            The word from the inverted index
	 * @param fileName
	 *            The file name where word is found
	 * 
	 * @return firstInst Returns the first instance (location) of word in array
	 *         list
	 */
	public Integer firstInstance(String word, String fileName) {
		lock.acquireReadLock();
		Integer firstInst = index.get(word).get(fileName).get(0);
		lock.releaseReadLock();
		return firstInst;
	}

	public HashMap<String, SearchResults> queryResult(
			ArrayList<String> queryWords) {

		lock.acquireReadLock();
		HashMap<String, SearchResults> qResults = new HashMap<String, SearchResults>();

		/*
		 * Based on the first character of query word, this will search through
		 * the TreeSet of the inverted index keys to look for exact or partial
		 * matches to the query word.
		 */
		for (String query : queryWords) {

			for (String cur = index.ceilingKey(query); cur != null; cur = index
					.higherKey(cur)) {
				if (cur.startsWith(query)) {
					for (String file : getFileNameSet(cur)) {
						if (!qResults.containsKey(file)) {
							SearchResults sr = new SearchResults(file);
							qResults.put(file, sr);
						}
						qResults.get(file).updateResults(
								getWordFrequency(cur, file),
								firstInstance(cur, file));
					}
				} else {
					break;
				}
			}
			

		}
		lock.releaseReadLock();
		return qResults;
		

	}
}