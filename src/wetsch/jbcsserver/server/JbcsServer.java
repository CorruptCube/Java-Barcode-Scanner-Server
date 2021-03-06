package wetsch.jbcsserver.server;

import java.io.IOException;
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

import wetsch.jbcsserver.server.listeners.DeviceRegistrationRequestListener;
import wetsch.jbcsserver.server.listeners.JbcsServerListener;
import wetsch.jbcsserver.server.listeners.ServerEvent;
import wetsch.jbcsserver.tools.DebugPrinter;

/*
 * Last modified on 7/17/2016
 * Changes:
 *Added calls to the start and stop method of the JbcsServerListener.
 *Added method to add a registration request listener.
 *Updated java documentation.
 */
/**
 * This class extends Thread.  It uses this thread to listen for incoming connections from the Wireless barcode scanner Android app. 
 * The server receives the barcode data transmitted by the app, and makes it available via the BarcodeServerDataListener. 
 * The server does throws a socket closed exception when the server is stopped.
 *The barcode information that is received is listed below.
 *<li> Barcode Type</li>
 *<li> Barcode</li>  
 * @author kevin
 * @version 2.1
 */
public class JbcsServer extends Thread{

	private boolean running = false;//Control the thread loop.
	private ServerSocket server = null;//creates the server socket.
	private Socket connection = null;//Creates the connection between server and client.
	private HashSet<JbcsServerListener> listeners = null;//Hold listeners that listen for updates.
	private HashSet<DeviceRegistrationRequestListener> regListeners = null;//Hold listeners that listen for updates.
	private String hostAddress = null;// Servers listening address.
	private int port = 0;//The port number the server socket listens on.
	private int poolLimit = 0;//Holes the number of connections allowed in server pool.
	private boolean registrationActive = false;//Boolean to determine if the registration system is accepting new registration requests.
	 
	/**
	 * The constructor takes in a string and integer that represents the host  port number to listen on.
	 * @param hostAddress listening interface.
	 * @param port listening port number.
	 * @param poolLimit The back log of connections that have not yet been accepted.
	 */
	public JbcsServer(String hostAddress, int port, int poolLimit) {
		this.hostAddress = hostAddress;
		this.port = port;
		this.poolLimit = poolLimit;
		
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
 * (non-Java-doc)
 * @see java.lang.Thread#run()
 */
	@Override
	public void run() {
		try{
			server = new ServerSocket();
			server.bind(new InetSocketAddress(hostAddress, port),poolLimit);
			running = true;
			if(listeners != null){
				for(JbcsServerListener l : listeners)
					l.serverStarted(new ServerEvent(this));
			}
			sendMessageToConsole("JBCS server is running");
			while(running){
				ListenForConnections();
			}
		}catch(Exception e){
			sendMessageToConsole(e.getMessage());
			if(listeners != null){
				for(JbcsServerListener l : listeners)
					l.ServerStopped(new ServerEvent(this));
			}
			new DebugPrinter().sendDebugToFile(e);
			e.printStackTrace();
		}
		super.run();
	}
	
	/**
	 * Signals the thread to finish and shut down the server.  
	 * Any thread currently blocked in accept() will throw a SocketException.  
	 * If the server is shutdown by calling this method, ignore the socket 
	 * closed exceptions in the debug report logs.
	 * @throws IOException 
	 */
	public void shutDownServer() throws IOException{
		running = false;
		server.close();
		sendMessageToConsole("Server shutdown successfully");
	}
	
	/**
	 * Returns the listening host address of the server.
	 * @return String
	 */
	public String getListeningInetAddress(){
		return hostAddress;
	}
	
	/**
	 * Returns the port number the server socket is listening on.
	 * @return int
	 */
	public int getServerListeningPort(){
		return port;
	}
	
	/**
	 * Returns true if server is running, otherwise returns false.
	 * @return boolean
	 */
	public boolean isServerRunning(){
		return running;
	}
	
	/**
	 * Add a registration request listener.<br>
	 * <b>Note:</b> This listener should only contain one class implementing this listener at a time.
	 * If more then one class that implements this listener is found in the listener, and the server 
	 * loops through multiple classes, it can result in a false accept or reject.
	 * The listener will not be added if it already exists in the list of listeners.
	 * @param l The class that implements this listener.
	 */
	public void addRegistrationRequestListener(DeviceRegistrationRequestListener l){
		if(regListeners == null)
			regListeners = new HashSet<DeviceRegistrationRequestListener>();
		if(!regListeners.contains(l))
			regListeners.add(l);
	}
	
	/**
	 * Removes the registration request listener.
	 * If the listener does not exist in the list, it will not be removed.
	 * @param l The listener to be removed.
	 */
	public void removeRegistrationRequestListener(DeviceRegistrationRequestListener l){
		if(regListeners != null){
			if(regListeners.contains(l))
				regListeners.remove(l);
			if(regListeners.size() == 0)
				regListeners = null;
		}
	}
	/**
	 * Add a JBCS server listener. 
	 * If the listener exists in the list, it will not be added.
	 * 
	 * @param listener to be added.
	 */
	public void addListener(JbcsServerListener listener){
		if(listeners == null)
			listeners = new HashSet<JbcsServerListener>();
		if(!listeners.contains(listener))
			listeners.add(listener);
	}

	/**
	 * Removes a JBCS server listener. 
	 * If the listener does not exist in the list, it will not be removed.
	 * @param listener Listener to be removed.
	 */
	public void removeListener(JbcsServerListener listener){
		if(listeners.contains(listener))
			listeners.remove(listener);
		if(listeners.size() == 0)
			listeners = null;
	}
	
	/**
	 * Removes all JBCS server listeners associated with this object.
	 */
	public void removeAlListeners(){
		if(listeners != null){
			listeners.clear();
			listeners = null;
		}if(regListeners != null){
			regListeners.clear();
			regListeners = null;
		}
	}
	
	/**
	 * Set if the registration system is or is not accepting connections.
	 * @param active
	 */
	public void deviceRegistrationEnabled(boolean active){
		registrationActive = active;
	}
	
	//Listen for incoming connections.
	private void ListenForConnections() throws IOException{
		if(connection == null && running){
			connection = server.accept();
			sendMessageToConsole("Device connected with IP address " + connection.getInetAddress().toString());
			ClientConnection client = new ClientConnection(connection, listeners, registrationActive);
			client.setRegistrationListeners(regListeners);
			client.start();
			connection = null;
		}
	}
	
	/*
	 * If a class implements this listener, it will send console output back to those classes.
	 * This is called when a client connects and sends messages to the server.
	 */
	private void sendMessageToConsole(String message){
		if(listeners != null){
		for(JbcsServerListener l : listeners)
			l.serverConsole(getDateTime() +": " + message);
		}
	}
	
	/*
	 * This method gets the current date and time when called.
	 * Used by the console to set the date and time on a console message.
	 */
	private String getDateTime(){
		DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return df.format(cal.getTime());
	}
}