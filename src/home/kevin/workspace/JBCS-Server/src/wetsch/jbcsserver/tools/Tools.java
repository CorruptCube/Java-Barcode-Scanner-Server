package wetsch.jbcsserver.tools;

import java.io.File;

public abstract class Tools {
	private static final String applicationDirectory = System.getProperty("user.home") + "/JBCS";//Stores the path to the users application home directory.
	
	/**
	 * Returns the path to the home directory of the application.
	 * @return String
	 */
	public static String getApplicationDir(){
		File dir = new File(applicationDirectory);
		if(!dir.exists())
			dir.mkdir();
		return applicationDirectory;
	}
}
