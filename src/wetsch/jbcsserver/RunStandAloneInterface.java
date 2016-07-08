package wetsch.jbcsserver;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import wetsch.jbcsserver.gui.serverinterface.MainPanel;
import wetsch.jbcsserver.server.registrationsystem.RegisteredDevices;
import wetsch.jbcsserver.tools.DebugPrinter;
import wetsch.jbcsserver.tools.Tools;

/*
 * This is the main class.
 * This Static void main method starts
 * the user interface for the server.
 * This class also loads in the registered devices data.
 * If a problem occurs while reading this data, an exception
 * is cought and a message will pop up indicating the data is
 * corrupt and the file storing this data was deleted.
 */
public class RunStandAloneInterface {
	public static void main(String[] srgs) {
		try {
			RegisteredDevices.readInRegisteredDevices();
		} catch (ClassNotFoundException | IOException e) {
			StringBuilder message = new StringBuilder();
			message.append("The registered devices data is corrupt, and the file has been removed.\n");
			message.append("A new one will be created when registered devices has been configured.");
			File f = new File(Tools.getRegisteredDevicesFilePath());
			f.delete();
			JOptionPane.showMessageDialog(null, message.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			new DebugPrinter().sendDebugToFile(e);
		}
		new MainPanel(); // Start the UI interface.

	}

}