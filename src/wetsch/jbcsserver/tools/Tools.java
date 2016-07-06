package wetsch.jbcsserver.tools;

import java.io.File;

public abstract class Tools {
	private static final String applicationDirectory = System.getProperty("user.home") + "/JBCS";//Stores the path to the users application home directory.
	private static final String registeredDevicesFileName = applicationDirectory +  "/Registered_devices.jdb";
	private static final String debugPrinteFilePath = applicationDirectory + "/JBCS-server-debug-report.txt";
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
	
	/**
	 * Returns the path to to he file where the registered devices are written.
	 * @return String
	 */
	public static String getRegisteredDevicesFilePath(){
		return registeredDevicesFileName;
	}
	
	/**
	 * Returns the path to the file whare the debug printer writes the stack-trace.
	 * @return String
	 */
	public static String getDebugPrinterFilePath(){
		return debugPrinteFilePath;
	}
}
