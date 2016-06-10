package wetsch.jbcsserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import javax.naming.directory.InvalidAttributesException;

/*
 *Last modified 2/8/2016
 */

/**
 * This class writes data to a CSV file.  
 * The data is red in though a multidimensional string array.  
 * The data needs to be organized in a table format of rows and columns.
 * @author kevin
 *
 */
public class CsvFileWritter {

	private String[][] data = null;//Data to write to CSV file.
	private String delimiter = null;//CSV delimiter.
	private String[] invalidStringValues = null;//Invalid string values.
	
	/**
	 * This constructor takes in the data array and delimiter to be used to generate the CSV file.
	 * @param data Data to be used.
	 * @param delimiter The delimiter to separate the data.
	 * @throws InvalidAttributesException Thrown if the data array or delimiter are equal to null or invalid.
	 */
	public CsvFileWritter(String[][] data, String delimiter) throws InvalidAttributesException {
		invalidStringValues = new String[]{null, ""};
		if(data == null)
			throw new InvalidAttributesException("The data array can not be null;");
		if(Arrays.asList(invalidStringValues).contains(delimiter))
			throw new InvalidAttributesException("The delimiter is invalid.");
		this.data = data;
		this.delimiter = delimiter;
	}

	/**
	 * Sets the delimiter that will be used to separate the data.<br>
	 * The fallowing examples can be used as delimiters.<br>
	 * <li>|
	 * 	<li>,<br>
	 * Note that a , is not a good delimiter in the following example.  
	 *For an entry ("John,Smith") the , does not work well in this example.  Using a 
	 * | would be a better delimiter.
	 * 
	 * @param delimiter
	 * @throws InvalidAttributesException
	 */
	public void setDelimiter(String delimiter) throws InvalidAttributesException{
		if(Arrays.asList(invalidStringValues).contains(delimiter))
			throw new InvalidAttributesException("The delimiter is invalid.");
		this.delimiter = delimiter;
	}
	
	/**
	 * Returns the delimiter that is used to separate the data.
	 * @return String
	 */
	public String getDelimiter(){
		return delimiter;
	}
	
	/**
	 * Returns the data array that is to be used to generate the CSV file.
	 * @return
	 * String[][]
	 */
	public String[][] getData(){
		return data;
	}
	
	/**
	 * Sets the data that will be used to generate the CSV file.
	 * @param data Data to store in the CSV file.
	 * @throws InvalidAttributesException Throws exception if parameter is equal to null.
	 */
	public void setData(String[][] data) throws InvalidAttributesException{
		if(data == null)
				throw new InvalidAttributesException("The data array can not be null;");
		this.data = data;
	}
	
	/**
	 * Writes the data passed in from the array to a CSV file.  
	 * The method will throw an exception if the data array 
	 *  equals null, the file name is improperly formated, or 
	 *  the delimiter is null or invalid.
	 * @param fileName The absolute path of the file name.
	 * @throws InvalidAttributesException Thrown if file name is null or an empty string.
	 * @throws IOException Thrown if the file can not be written.
	 */
	public void writeCsvFile(String fileName) throws InvalidAttributesException, IOException{
		StringBuilder stBuilder = new StringBuilder();
		if(Arrays.asList(invalidStringValues).contains(fileName) || Arrays.asList(invalidStringValues).contains(delimiter))
			throw new InvalidAttributesException("The filename or delimiter are invalid.");
		if(data == null)
			throw new InvalidAttributesException("The data array can not be null");
		for(int r = 0; r < data.length; r ++){
			for(int c = 0; c <data[r].length; c++){
				if(c < data[r].length-1)
					stBuilder.append(data[r][c] + delimiter);
				else
					stBuilder.append(data[r][c] + "\n");
			}
		}
		File f = new File(fileName);
		if(!f.exists())
			f.createNewFile();
		FileWriter fw = new FileWriter(f);
		fw.write(stBuilder.toString());
		fw.close();
	}
}