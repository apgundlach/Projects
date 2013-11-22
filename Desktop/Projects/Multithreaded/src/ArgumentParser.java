/**
 * Name: Adelle Paulette Rivera 
 * Class: CS212 
 * Assignment: Project 3
 * 
 * This class will parse the arguments passed through the command line by the
 * user
 * 
 * @author Adelle
 * 
 */
public class ArgumentParser {

	/**
	 * This method gets flag and argument given by user
	 * 
	 * @param args		The arguments passed by the user on the command line
	 * @param flag		The flag indicated by Driver
	 * 
	 * @return arg 		The argument associated with the flag if any
	 */
	public static String getFlag(String[] args, String flag) {

		String arg = null;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(flag)) {
				if ((args.length - 1) == i) {
					return arg;
				}
				i++;
				arg = args[i];
				return arg;
			}
		}
		return arg;

	}

	/**
	 * This containsFlag method will determine if a particular flag is present
	 * in args array from command line user arguments
	 * 
	 * @param args		The array of arguments passed by user on command line
	 * @param arg		The argument being searched for
	 * 
	 * @return flag 	True or false if the argument being searched for exists in
	 *         			array
	 */
	public static boolean containsFlag(String[] args, String arg) {

		boolean flag = false;

		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-") && args[i].equals(arg)) {
				flag = true;
				return flag;
			}
		}
		return flag;
	}

}