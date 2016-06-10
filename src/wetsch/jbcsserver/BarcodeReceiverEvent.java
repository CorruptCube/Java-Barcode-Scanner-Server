package wetsch.jbcsserver;

import wetsch.jbcsclient.BarCodeData;

/*
 * Last modified on 1/5/2016
 *Changes:
 *Changed the event to take in a barcode data object.
 */

/**
 * This event class contains the data received by the server.  
 * Below is the following object attributes.
 * <li>Barcode format type</li>
 * <li>Barcode data</li>
 * <li>Client inet address triggering the event</li>
 * <li>Source object triggering the event</li> 
 * @author kevin
 * @version 1.1
 *
 */
public class BarcodeReceiverEvent {
	private String clientInetAddress = null;//Client devices inet address.
	private String barcode = null;//Barcode data. 
	private String barcodeType = null;//Barcode format type.
	private Object source = null;//Object that triggered the event.
/**
 * This constructor takes the source, data and client inet address triggering the event.
 * @param source The source object that triggers the event.
 * @param data Barcode data received by server.
 * @param clientInetAddress client inet address.
 */
	public BarcodeReceiverEvent(Object source, BarCodeData data, String clientInetAddress){
		this.source = source;
		this.barcode = data.getBarcodeValue();
		this.barcodeType = data.getBarcodeType();
		this.clientInetAddress = clientInetAddress;
	}
	
	/**
	 * Returns the source object that triggered the event.
	 * @return Object
	 */
	public Object getSource(){
		return source;
	}
	
	/**
	 * Returns the IPv4 address of the client that triggered the event.
	 * @return String
	 */
	public String getClientInetAddress(){
		return clientInetAddress;
	}
	
	/**
	 * Returns a string representation of the barcode.
	 * @return String
	 */
	public String getBarcode(){
		return barcode;
	}
	
	/**
	 * Returns a string representation of the barcode format type.
	 * @return String
	 */
	public String getBarcodeType(){
		return barcodeType;
	}
}