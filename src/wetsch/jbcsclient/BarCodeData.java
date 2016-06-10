package wetsch.jbcsclient;

import java.io.Serializable;

/*
 * Last modified: 1/5/2016
 */

/**
 * This class holds the data collected by the JBCS-Client.
 * @author kevin
 * @version 1.0
 *
 */
public class BarCodeData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String barcodeType = null; //Stores the barcode type.
	private String barcodeValue = null;//Store the barcode value.
	
	/**
	 * This constructor takes two strings that set the barcode type and data.
	 * @param barcodeType Type of barcode.
	 * @param barcodeValue Data stored in barcode.
	 */
	public BarCodeData(String barcodeType, String barcodeValue) {
		super();
		this.barcodeType = barcodeType;
		this.barcodeValue = barcodeValue;
	}
	
	/**
	 * Returns the type of barcode.
	 * @return String
	 */
	public String getBarcodeType() {
		return barcodeType;
	}
	
	/**
	 * Returns the barcode Data.
	 * @return String.
	 */
	public String getBarcodeValue() {
		return barcodeValue;
	}
}