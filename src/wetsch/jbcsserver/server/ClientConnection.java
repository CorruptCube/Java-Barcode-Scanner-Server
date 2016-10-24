package wetsch.jbcsserver.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;

import wetsch.jbcsclient.BarCodeData;
import wetsch.jbcsserver.server.listeners.BarCoderEvent;
import wetsch.jbcsserver.server.listeners.DeviceRegistrationRequestListener;
import wetsch.jbcsserver.server.listeners.JbcsServerListener;
import wetsch.jbcsserver.server.registrationsystem.Device;
import wetsch.jbcsserver.server.registrationsystem.RegisteredDevices;
import wetsch.jbcsserver.tools.DebugPrinter;

/*
 * Last modified: 7/17/2016
 * Added device Registration listener.
 */

/**
 * This clas extends Thread.  It is responsible for handling connections accepted 
 * by the server.
 * @author kevin
 * @version 1.0
 */
public class ClientConnection extends Thread {
	private HashSet<JbcsServerListener> listeners = null;//Hold listeners that listen for updates.
	private HashSet<DeviceRegistrationRequestListener> regListeners = null;//Hold listeners that listen for updates.
	private RegisteredDevices registeredDevices = RegisteredDevices.getInstance();//Instance to the registration system.
	
	private Socket connection = null;//The connection to the client.
	private BufferedReader in = null;//Read the input-stream from the client.
	private PrintWriter out = null;//The output-stream back to the client.
	private boolean registrationActive = false;//Determine if a new device can register with the system or not.

	/**
	 * This constructor take the client connection object, and any listeners from the server.
	 * @param connection The accepted  connection from the server
	 * @param listeners Listeners that are from the server.
	 */
	public ClientConnection(Socket connection, HashSet<JbcsServerListener> listeners, boolean registrationActive) {
		this.listeners = listeners;
		this.connection = connection;
		this.registrationActive = registrationActive;
	}
	
	/**
	 * Adds the Listener object to the thread that handles the accepted connections.
	 * @param l Listener object for storing the classes listening for registration requests.
	 */
	public void setRegistrationListeners(HashSet<DeviceRegistrationRequestListener> l){
		regListeners = l;
	}
	
	/*The run method sets up the input/output streams and handles the data received.
	 * If an exception is thrown, a message is sent to the server console, and the
	 * finally block will try to close down the connection.
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try{
			setupStreams();
			handelData();
		}catch(Exception e){
			if(e instanceof SocketTimeoutException)
				sendMessageToConsole("Connection timed out for client with IP address " + connection.getInetAddress());
			new DebugPrinter().sendDebugToFile(e);
			e.printStackTrace();
		}finally{
			try {
				closeConnection();
			} catch (IOException e) {
				new DebugPrinter().sendDebugToFile(e);
				e.printStackTrace();
			}
		}
		super.run();
	}
	
	/*
	 * This method sets up the input/output stream readers for the connection.
	 * Also, this method sets the connection So timeout to 60 seconds.
	 * If the input-stream does not receive any data in 60 seconds, the
	 * connection will time out and a SocketTimeoutException will be raised.
	 * the catch block in the run method will handle it and then finally try
	 *  to close the connection. 
	 */
	private void setupStreams() throws IOException{
		if(connection != null){
			connection.setSoTimeout(60000);
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			out = new PrintWriter(connection.getOutputStream());
		}
	}
	
	/*
	 * Handles the data received by connected client devices.
	 * Commands passed:
	 * SEND_BARCODE_DATA:
	 * The first value is the barcode type.
	 * The second value is the value stored in the barcode.
	 * CHECK_CONNECTION:
	 * If the command is received, the server will responds
	 * back with a message indicating that the connection is okay.
	 * Any unrecognized commands will send a message back to the client
	 * the server responded with invalid command.
	 * Once the server finishes, it will close the current connection.
	 */
	private void handelData() throws ClassNotFoundException, IOException{
		String id = null;
		String clientInetAddress = connection.getInetAddress().toString();
		String message = in.readLine();
		switch (message) {
			case ServerCommands.sendBarcodeData:
				if(registeredDevices.isSystemEnabled()){
					out.println(ServerCommands.drsEnabled);
					out.flush();
					id = in.readLine();
					if(!registeredDevices.containsKey(id)){
						out.println(ServerCommands.unregistered);
						out.flush();
						sendMessageToConsole("Unregistered device detected with IP address "+ clientInetAddress);
					}else{
						out.println(ServerCommands.registered);
						out.flush();
						handleBarcodeData(clientInetAddress);
					}
				}else{
					out.println(ServerCommands.drsDisabled);
					out.flush();
					handleBarcodeData(clientInetAddress);
				}
				break;
			case ServerCommands.checkConnection:
				out.println(ServerCommands.connectionOk);
				out.flush();
				sendMessageToConsole("Connection check from client with IP address " + clientInetAddress);
				break;
			case ServerCommands.registerDevice:
				id = in.readLine();
					if(!registrationActive){
						out.println(ServerCommands.registrationRequestInactive);
					}else{
						boolean registrationAccepted = false;
						if(!registeredDevices.containsKey(id)){
							sendMessageToConsole("Device registration ID  received by client with IP address " + clientInetAddress);
							if(regListeners != null){
								for(DeviceRegistrationRequestListener l : regListeners)
									registrationAccepted = l.deviceRegistrationRequest(new Device(id, null, Calendar.getInstance().getTime()));
							}
							if(registrationAccepted){
								out.println(ServerCommands.deviceRegistered);
							}else{
								out.println(ServerCommands.deviceRejected);
							}
						}else{
							out.println(ServerCommands.registered);
							sendMessageToConsole("Device already registered with client IP address " + clientInetAddress);
						}
					}
					out.flush();
				break;
			default:
				out.println("Server responded with invalid command");
				out.flush();
				sendMessageToConsole("Invalid message from client with IP address " + clientInetAddress);
				break;
			}
	}

	//Closes the streams and connection.
	private void closeConnection() throws IOException{
		in.close();
		in = null;
		out.close();
		out = null;
		connection.close();
		sendMessageToConsole("Connection closed for client with IP address " + connection.getInetAddress());
		connection = null;
	}
	
	//Handles the barcode data when a client sends the barcode data to the server.
	private void handleBarcodeData(String clientIP) throws IOException{
		String bType = in.readLine();
		String bData =in.readLine();
		out.println(ServerCommands.dataReceived);
		out.flush();
		BarCodeData data = new BarCodeData(bType, bData);
		if(listeners != null){
			for(JbcsServerListener l : listeners)
				l.barcodeServerDatareceived(new BarCoderEvent(this, data, clientIP));
		}
		sendMessageToConsole("Data received from client with IP address " + clientIP);

	}

	
	/*
	 * If a class implements this listener, it will send console output back to those classes.
	 * This is called when a client connects and sends messages to the server.
	 * The listeners are passed by the server class that created this thread.
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
	
	/**
	 * This class holds static references to the server commands and responces.
	 * @author kevin
	 */
	public static class ServerCommands{
		/**
		 * Command to check the server connection.
		 */
		public static final String checkConnection = "CHECK_CONNECTION";
		/**
		 * Command to register a new device.
		 */
		public static final String registerDevice = "REGISTER_DEVICE";
		/**
		 * Server response to indicate the connection is OK.
		 */
		public static final String connectionOk = "CONNECTION_OK";
		/**
		 * Command to send the barcode data.
		 */
		public static final String sendBarcodeData = "SEND_BARCODE_DATA";
		/**
		 * Server response to indicate the barcode data was received.
		 */
		public static final String dataReceived = "BC_DATA_RECEIVED";
		/**
		 * Server response to indicate the device is not registered with the server.
		 */
		public static final String unregistered = "UNREGISTERED";
		/**
		 * Server response to indicate the device is registered with the server.
		 */
		public static final String registered = "REGISTERED";
		/**
		 * Server response to indicate the device registration request was accepted by the server.
		 */
		public static final String deviceRegistered = "DEVICE_REGISTERED";
		/**
		 * Server response to indicate the device registration request was rejected by the server.
		 */
		public static final String deviceRejected = "DEVICE_REJECTED";
		/**
		 * Server response to indicate the Device registration system in enforced.
		 */
		public static final String drsEnabled = "DRS_ENABLED"; 
		/**
		 * Server response to indicate the device registration system is not enforced.
		 */
		public static final String drsDisabled = "DRS_DISABLED";
		/**
		 * Server response to indicate the server is not accepting registration requests.
		 */
		public static final String registrationRequestInactive = "REGISTRATION_REQUEST_INACTIVE";

	}
}