package wetsch.jbcsserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;

import wetsch.jbcsclient.BarCodeData;
import wetsch.jbcsclient.ServerClientCommand;


/*
 * Last modified on 1/23/2016
 * Changes:
 *Added support for the server to send console messages.
 */
/**
 * This class extends Thread.  It uses this thread to listen for incoming connections from the Wireless barcode scanner Android app. 
 * The server receives the barcode data transmitted by the app, and makes it available via the BarcodeServerDataListener. 
 * The server does throws a socket closed exception when the server is stopped.
 *The barcode information that is received is listed below.
 *<li> Barcode Type</li>
 *<li> Barcode</li>  
 * @author kevin
 * @version 1.1
 */
public class WirelessBarcodeScannerServer extends Thread{
	private DebugPrinter debugPrinter = null;
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
		debugPrinter = new DebugPrinter();
		this.hostAddress = hostAddress;
		this.port = port;
	}
	
	/**
	 * Returns an array of available IPV4 addresses on the system.
	 * @return String[]
	 * @throws SocketException
	 */
	public static String[] getAvailableIPV4Addresses() throws SocketException{
		ArrayList<String> v4Addresses = new ArrayList<String>();
		Enumeration<NetworkInterface> nif = NetworkInterface.getNetworkInterfaces();
		for(NetworkInterface netint : Collections.list(nif)){
			Enumeration<InetAddress> addresses = netint.getInetAddresses();
			for(InetAddress addr : Collections.list(addresses)){
				if(addr instanceof Inet4Address)
					v4Addresses.add(addr.getHostAddress());
			}
		}
				return v4Addresses.toArray(new String[v4Addresses.size()]);
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
			sendMessageToConsole(getDateTime() + ": JBCS server is running");
			while(running){
				if(connection == null){
					ListenForConnections();
					sendMessageToConsole(getDateTime() + ": Device connected with IP Address" + connection.getInetAddress().toString());
					//sleep(100);
				}else{
					setupStreams();
					handelData();
				}
			}
		}catch(Exception e){
			try {
				sendMessageToConsole(getDateTime() + ": Server shutdown successfully");
				debugPrinter.sendDebugToFile(e);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		super.run();
	}
	
	//Handles the data received by connected client devices. 
	private void handelData() throws ClassNotFoundException, IOException{
		String clientInetAddress = connection.getInetAddress().toString();
		Object obj = in.readObject();
		if(obj instanceof BarCodeData){
			BarCodeData data = (BarCodeData) obj;
			if(listeners != null){
			for(BarcodeServerDataListener l : listeners)
				l.barcodeServerDatareceived(new BarcodeReceiverEvent(this, data, clientInetAddress));
			}
			out.writeObject(ServerClientCommand.dataReceived);
			out.flush();
			sendMessageToConsole(getDateTime() + ": Barcode data receieved by client with IP address" + clientInetAddress);

		}else if(obj instanceof ServerClientCommand){
			ServerClientCommand request = (ServerClientCommand) obj;
			if(request == ServerClientCommand.connectionOK){
				out.writeObject(request);
				out.flush();
				sendMessageToConsole(getDateTime() + ": Connection check from device with IP address" + clientInetAddress);

			}
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
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	/**
	 * Removes the listener from the object.
	 * @param listener
	 */
	public void removeServerDatareceivedListener(BarcodeServerDataListener listener){
		if(listeners.contains(listener))
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
	
	//Get current date and time.
	private String getDateTime(){
		DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return df.format(cal.getTime());
	}
	
	//Send message to console.
	private void sendMessageToConsole(String message){
		if(listeners != null){
		for(BarcodeServerDataListener l : listeners)
			l.barcodeServerConsole(message);
		}

	}
}
