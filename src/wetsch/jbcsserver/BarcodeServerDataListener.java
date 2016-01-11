package wetsch.jbcsserver;

/*
 * Last modified on 8/23/2015
 * 
 */

/**
 * This interface is used to access the data received by the server.  
 * Any class that implements this interface is required to implement the 
 * barcodeServerDatareceived method.  If a listener is added, the server 
 * calls upon this method passing the data event as it's parameters.
 * @author kevin
 * @version 1.0
 *
 */
public interface BarcodeServerDataListener {
	
	public void barcodeServerDatareceived(BarcodeReceiverEvent e);


}
