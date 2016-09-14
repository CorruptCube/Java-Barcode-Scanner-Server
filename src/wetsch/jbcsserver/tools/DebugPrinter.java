package wetsch.jbcsserver.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * Last modified 7/6/2016
 */

/**
 * This class extends Thread.  
 * This class is used to write debug output to a file.  
 * If an exception is thrown during runtime, The exceptions 
 * can be written to a file.  
 * There is no need to call the start() method sence it is 
 * called by the sendDebugToFile() method.
 * @author kevin
 *@version 1.0
 */
public class DebugPrinter extends Thread{
	
	private final String filename;//The file name to save the debug reports.
	private Exception e = null;//the exception to be written.
	
	/**
	 * This constructor takes a string that represents the file name 
	 * that the debug output is save to. This file is saved to the home 
	 * directory of the current user.
	 * @param filename The filename to write the output to.
	 */
	public DebugPrinter(){
		this.filename  = Tools.getDebugPrinterFilePath();
	}
	
	
	/*
	 * This method is the method the thread will exacute at the start of the thread.
	 * The method will take the exception and write it to a text file.
	 * The method also appends to tehe bottom of the file keeping prevous exceptions in place.
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			File report = new File(filename);
			if(!report.exists())
				report.createNewFile();
			FileWriter fw = new FileWriter(report, true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(getCurrentDateTime());
			e.printStackTrace(pw);
			pw.flush();
			pw.close();
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		super.run();
	}


	/**
	 * This method sets the Exception to write to a file.
	 * @param e Exception to write.
	 */
	public void sendDebugToFile(Exception e){
		this.e = e;
		start();
	}
	//Returns the current date and time to time-stamp the debug output.
	private static String getCurrentDateTime(){
		DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return df.format(cal.getTime());
	}
}