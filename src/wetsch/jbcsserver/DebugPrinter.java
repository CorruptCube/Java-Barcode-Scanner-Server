package wetsch.jbcsserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DebugPrinter {
	
	private final String homeDir = System.getProperty("user.home");
	private final File applicationDirectory = new File(homeDir + "/JBCS");
	private final String filename;
	
	/**
	 * This constructor takes a string that represents the file name 
	 * that the debug output is save to. This file is saved to the home 
	 * directory of the current user.
	 * @param filename The filename to write the output to.
	 */
	public DebugPrinter(){
		this.filename  = "JBCS-server-debug-report.txt";
	}
	
	/**
	 * This method writs the passed in exception to file.  
	 * The file is saved in the current user's home directory.
	 * If the file does not exist, it is created automatically.
	 * Each method call appends to the end of the file with a 
	 * time stamp and the exception that was passed in.
	 * @param e Exception to be written to file.
	 * @throws IOException
	 */
	public void sendDebugToFile(Exception e) throws IOException{
		File report = new File(applicationDirectory+"/"+filename);
		if(!applicationDirectory.exists())
			applicationDirectory.mkdirs();
		 if(!report.exists())
			report.createNewFile();
		FileWriter fw = new FileWriter(report, true);
		PrintWriter pw = new PrintWriter(fw);
		pw.println(getCurrentDateTime());
		e.printStackTrace(pw);
		pw.flush();
		pw.close();
		fw.close();
	}
	/**
	 * Returns the absolute path to the debug output file.
	 * @return String
	 */
	public String getDebugReportFilePath(){
		return homeDir + "/" + filename;
	}
	//Returns the current date and time to timestam the debug output.
	private static String getCurrentDateTime(){
		DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return df.format(cal.getTime());
	}
}
