package wetsch.jbcsserver.server.listeners;


/*
 * Last modified on 1/23/2016
 * Added abstract method to listen for messages from the console.
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
public interface JbcsServerListener {
	
	/**
	 * Called by the server when barcode data is received from the client.
	 * @param e Event generated by the server.
	 */
	
	public void barcodeServerDatareceived(BarCoderEvent e);
	
	/**
	 * Called by the server when it sends a console message.
	 * @param message Message sent from the server.
	 */
	public void barcodeServerConsole(String message);
}