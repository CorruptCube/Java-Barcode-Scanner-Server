package wetsch.jbcsserver;

import java.io.IOException;

import wetsch.jbcsserver.gui.serverinterface.MainPanel;
import wetsch.jbcsserver.server.registrationsystem.RegisteredDevices;
/*
 * This is the main class.
 * This Static void main method starts
 * the user interface for the server.
 */
public class RunStandAloneInterface{
	public static void main(String[] srgs){
		try {
			RegisteredDevices.readInRegisteredDevices();
			new MainPanel(); //Start the UI interface.
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
}