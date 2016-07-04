package wetsch.jbcsserver.gui.serverinterface;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class ServerInterfaceMainMenu extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	private JMenu jmFile = new JMenu("File");
	
	public JMenuItem jmiRegisteredDevices = new JMenuItem("Registered Devices");
	public JMenuItem jmiExit = new JMenuItem("Exit");
	
	public ServerInterfaceMainMenu() {
		setupMenu();
	}

	private void setupMenu() {
		add(jmFile);
		jmFile.add(jmiRegisteredDevices);
		jmFile.add(jmiExit);
	}

}
