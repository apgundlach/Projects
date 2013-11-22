/**
 * Name:  		Adelle Paulette Rivera
 * Class:  		CS212
 * Assignment:  Project 3
 **/

/**
 * The SearchResults class is used to combine query search results together and
 * compare each file based on frequency and first position using Comparable.
 * 
 * @author Adelle Rivera
 * 
 */
public class SearchResults implements Comparable<SearchResults> {

	private String fileName;
	private int frequency;
	private int firstPos;

	// Constructor
	public SearchResults(String fileName) {
		this.fileName = fileName;
		frequency = 0;
		firstPos = Integer.MAX_VALUE;
	}

	/**
	 * This updateResults method is used to add information for each file name
	 * with updated frequency and first position.
	 * 
	 * @param frequency		The frequency of the query word
	 * @param firstPosition	The first position for the query word
	 */
	public void updateResults(int frequency, int firstPosition) {
		this.frequency = frequency + this.frequency;
		if (this.firstPos > firstPosition) {
			this.firstPos = firstPosition;
		}
	}

	/**
	 * This compareTo method compares the frequencies to determine where
	 * information is inserted and if frequencies are equal the first position
	 * is looked at to determine where information is inserted next.
	 */
	@Override
	public int compareTo(SearchResults other) {

		if (this.frequency > other.frequency) {
			return -1;
		} else if (this.frequency == other.frequency) {
			if (this.firstPos < other.firstPos) {
				return -1;
			} else if (this.firstPos == other.firstPos) {
				if (this.fileName.length() > other.fileName.length()) {
					return -1;
				} else {
					return 1;
				}
			} else {
				return 1;
			}
		} else
			return 1;

	}

	/**
	 * This toString method is used for convenience of printing the output in
	 * desired format.
	 * 
	 */
	public String toString() {
		return "\"" + fileName + "\"" + ", " + frequency + ", " + firstPos;
	}

}