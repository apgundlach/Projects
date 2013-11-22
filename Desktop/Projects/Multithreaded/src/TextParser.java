/**
 * Name:  		Adelle Paulette Rivera
 * Class:  		CS212
 * Assignment:  Project 3
 **/

import java.io.*;

import org.apache.log4j.Logger;

/**
 * This TextParser class is a class with multithreaded capabilities. Each thread
 * is used to parse through a text file or traverse through
 * directory/directories to parse the files within it.
 * 
 * @author Adelle Rivera
 * 
 */
public class TextParser {

	private static Logger log = Logger.getLogger(TextParser.class.getName());

	private WorkQueue workers;
	private int pending;

	public TextParser(int threads) {
		workers = new WorkQueue(threads);
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
	 * This parseFile method will parse through the individual files and place
	 * the word in the file in the inverted index.
	 * 
	 * @param index
	 *            An instance of the inverted index is being passed through
	 * @param fileToIndex
	 *            The file to be indexed is passed though method
	 */
	private static void parseFile(InvertedIndex index, File fileToIndex) {

		log.info("Parsing " + fileToIndex + "...");

		int location = 1;

		InvertedIndex subindex = new InvertedIndex();

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileToIndex));

			String fileName = fileToIndex.getAbsolutePath();

			if (fileName.toLowerCase().endsWith("txt")) {

				String line = null;
				String[] words = null;

				// while the line being read in isn't null
				while ((line = br.readLine()) != null) {

					/* line being read in is being split by space */
					words = line.split("\\s");

					/*
					 * this block of code will check each word in words array to
					 * check if word is in the index, then it adds the file name
					 * and location and increments location Also, it replaces
					 * all non-characters and '_' with spaces and converts words
					 * to lower case
					 */
					for (String word : words) {
						word = word.replaceAll("\\W", "");
						word = word.replace("_", "");
						word = word.toLowerCase();
						if (word.equals("")) {
							continue;
						}
						subindex.addInfo(word, fileName, location);
						location++;
					}

				}
			}
			br.close();
			index.addAllInfo(subindex);

		} catch (FileNotFoundException e) {
			log.error(fileToIndex + " to parse was not found.");
			System.out.println("An error occurred, file was not found.");
			System.exit(0);

		} catch (IOException e) {
			log.error("Error occurred while trying to parse file "
					+ fileToIndex);
			System.out
					.println("An error has occurred while trying to parse file.");
			System.exit(0);

		}
	}

	private class ParserWorker implements Runnable {

		private File dir;
		InvertedIndex index;

		public ParserWorker(File dir, InvertedIndex index) {
			this.dir = dir;
			this.index = index;
			updatePending(1);
		}

		@Override
		public void run() {
			log.debug("Starting work on \"" + dir + "\"");

			parseFile(index, dir);

			updatePending(-1);
			log.debug("Finished work on \"" + dir + "\"");
		}
	}

	public void shutdown() {
		workers.shutdown();
	}

	/**
	 * This method indexes a folder/folders to find files to parse.
	 * 
	 * @param index
	 *            An instance of the inverted index is passed through
	 * @param searchDirectory
	 *            The search directory to start indexing through.
	 */
	private void indexFolder(InvertedIndex index, File searchDirectory) {

		if (!searchDirectory.isFile()) {

			File[] indexFiles = searchDirectory.listFiles();
			for (File f : indexFiles) {

				// This if statement checks to see if the 1st element in the
				// indexFiles array is not a file,
				// and if it isn't it takes that folder and recursively calls
				// the method until files are reach
				if (!f.isFile()) {
					indexFolder(index, f);
				} else {
					workers.execute(new ParserWorker(f, index));
				}
			}
		}

	}

	/**
	 * This method is used as method overloading
	 * 
	 * @param index
	 *            An instance of the inverted index is passed through
	 * @param fileToIndex
	 *            The file to index that will be passed through the method
	 */
	public void indexFolder(InvertedIndex index, String fileToIndex) {

		indexFolder(index, new File(fileToIndex));

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
	}

}