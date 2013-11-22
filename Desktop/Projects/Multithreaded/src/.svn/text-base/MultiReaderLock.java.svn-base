/**
 * Name: Adelle Paulette Rivera 
 * Class: CS212 
 * Assignment: Project 3
 * 
 * This class is a custom lock class used for multithreading of project
 * 
 * @author Adelle
 * 
 */

public class MultiReaderLock {

	private int numReader;
	private int numWriter;

	public MultiReaderLock() {
		numReader = 0;
		numWriter = 0;
	}

	protected synchronized void acquireReadLock() {

		while (numWriter > 0)
			try {
				this.wait();
			} catch (InterruptedException ex) {
			}
		numReader++;
	}

	protected synchronized void releaseReadLock() {
		numReader--;
		this.notifyAll();
	}

	protected synchronized void acquireWriteLock() {

		while (numReader > 0 || numWriter > 0)
			try {
				this.wait();
			} catch (InterruptedException ex) {
			}
		numWriter++;
	}

	protected synchronized void releaseWriteLock() {
		numWriter--;
		this.notifyAll();
	}

}