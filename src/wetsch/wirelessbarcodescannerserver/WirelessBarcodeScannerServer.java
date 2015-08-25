package wetsch.wirelessbarcodescannerserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

/*
 * Last modified on 8/23/2015
 * Changes:
 * Added client inet address to receiver event.
 */
/**
 * This class extends Thread.  It uses this thread to listen for incoming connections from the Wireless bar-code scanner Android app.. 
 * The server receives the barcode data transmitted by the app, and makes it available via the BarcodeServerDataListener. 
 *The bar-code information that is received is listed below.
 *<li> Bar-code Type</li>
 *<li> Bar-code</li>  
 * @author kevin
 * @version 1.0
 * 
 *
 */
public class WirelessBarcodeScannerServer extends Thread{
	
	private boolean running = false;//Control the thread loop.
	private ServerSocket server = null;//creates the server socket.
	private Socket connection = null;//Creates the connection between server and client.
	private ObjectInputStream in = null;//Outbound stream for the connection.
	private ObjectOutputStream out = null;//Inbound stream for the connection.
	private HashSet<BarcodeServerDataListener> listeners = null;//Hold listeners that listen for updates.
	private String hostAddress = null;// Servers listening address.
	private int port = 0;//The port number the server socket listens on.
	
	/**
	 * The constructor takes in a string and integer that represents the host  port number to listen on.
	 * @param hostAddress listening interface.
	 * @param port listening port number.
	 */
	public WirelessBarcodeScannerServer(String hostAddress, int port) {
		this.hostAddress = hostAddress;
		this.port = port;
	}
/*
 * The runnable contends to loop until the running boolean is set to false.
 * Each cycle checks for a incoming connection.  If a connection is made, 
 * The loop sets up the streams and receives the data. Once the data is
 * Handled, the connection is closed and set to null. The loop goes back
 * to waiting for an incoming connection.
 * (non-Javadoc)
 * @see java.lang.Thread#run()
 */
	@Override
	public void run() {
		try{
			server = new ServerSocket();
			server.bind(new InetSocketAddress(hostAddress, port));
			running = true;
			System.out.println("WBS Server is running.");
			while(running){
				if(connection == null){
					ListenForConnections();
					System.out.println("Device connected");
					//sleep(100);
				}else{
					setupStreams();
					handelData();
				}
			}
			System.out.println("Server stopped.");

		}catch(Exception e){
			e.printStackTrace();
		}
		super.run();
	}
	
	//Handles the data received by connected client devices. 
	private void handelData() throws ClassNotFoundException, IOException{
		Object obj = in.readObject();
		String[] data = (String[]) obj;
		String clientInetAddress = connection.getInetAddress().toString();
		if(listeners != null){
		for(BarcodeServerDataListener l : listeners)
			l.barcodeServerDatareceived(new BarcodeReceiverEvent(this, data, clientInetAddress));
			
		}
		closeConnection();
	}
	
	//Set up the streams used for the socket connection.
	private void setupStreams() throws IOException{
		if(connection != null){
			in = new ObjectInputStream(connection.getInputStream());
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
		}
	}
	
	//Listen for incoming connections.
	private void ListenForConnections() throws IOException{
		if(connection == null && running){
			connection = server.accept();
		}
	}
	//Closes the streams and connection.
	private void closeConnection() throws IOException{
		out.writeObject("Data received by server.");
		out.flush();
		in.close();
		in = null;
		out.close();
		out = null;
		connection.close();
		connection = null;
	}
	
	/**
	 * Signals the thread to finish and shut down the server.
	 * @throws IOException 
	 */
	public void shutDownServer() throws IOException{
		server.close();
		running = false;
	}
	/**
	 * Returns the port number the server socket is listening on.
	 * @return int
	 */
	public int getServerListeningPort(){
		return port;
	}
	
	/**
	 * Add a data reveived listener to the object.
	 * @param listener
	 */
	public void addServerDatareceivedListener(BarcodeServerDataListener listener){
		if(listeners == null)
			listeners = new HashSet<BarcodeServerDataListener>();
		listeners.add(listener);
	}
	
	/**
	 * Removes the listener from the object.
	 * @param listener
	 */
	public void removeServerDatareceivedListener(BarcodeServerDataListener listener){
		listeners.remove(listener);
		if(listeners.size() == 0)
			listeners = null;
	}
	
	/**
	 * Removes all listeners from the object.
	 */
	public void removeAllServerDatareceivedListener(){
		listeners.clear();
		listeners = null;
	}

}
