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
import wetsch.jbcsserver.server.listeners.JbcsServerListener;
import wetsch.jbcsserver.tools.DebugPrinter;

/*
 * Last modified: 6/14/2016
 */

/**
 * This clas extends Thread.  It is responsible for handling connections accepted 
 * by the server.
 * @author kevin
 * @version 1.0
 */
public class ClientConnection extends Thread {
	private HashSet<JbcsServerListener> listeners = null;//Hold listeners that listen for updates.

	private Socket connection = null;//The connection to the client.
	private BufferedReader in = null;//Read the input-stream from the client.
	private PrintWriter out = null;//The output-stream back to the client.

	/**
	 * This constructor take the client connection object, and any listeners from the server.
	 * @param connection The accepted  connection from the server
	 * @param listeners Listeners that are from the server.
	 */
	public ClientConnection(Socket connection, HashSet<JbcsServerListener> listeners) {
		this.listeners = listeners;
		this.connection = connection;
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
		String clientInetAddress = connection.getInetAddress().toString();
		String message = in.readLine();

		switch (message) {
			case "SEND_BARCODE_DATA":
				String bType = in.readLine();
				String bData =in.readLine();
				out.println("Data received by server.");
				out.flush();
				BarCodeData data = new BarCodeData(bType, bData);
				if(listeners != null){
					for(JbcsServerListener l : listeners)
						l.barcodeServerDatareceived(new BarCoderEvent(this, data, clientInetAddress));
				}
				sendMessageToConsole("Data received from client with IP address " + clientInetAddress);
				break;
			case "CHECK_CONNECTION":
				out.println("Connection to server is ok.");
				out.flush();
				sendMessageToConsole("Connection check from client with IP address " + clientInetAddress);
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
}