package wetsch.jbcsserver.gui.serverinterface;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class ServerInterfaceMainMenu extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	private JMenu jmFile = new JMenu("File");//File menu
	private JMenu jmServer = new JMenu("Server");//Server menu
	private JMenu jmHelp = new JMenu("Help");//Help Menu
	
	public JMenuItem jmiRegisteredDevices = new JMenuItem("Registered Devices");//Menu item to open registered devices frame.
	public JMenuItem jmiExit = new JMenuItem("Exit");//Menu item to exit application.
	public JMenuItem jmiDebugReport = new JMenuItem("Debug Reporting");//Menu item to open debug report.
	public JMenuItem jmiRefreshInterfaces = new JMenuItem("Refresh Interfaces");//Refresh available interfaces.
	
	public ServerInterfaceMainMenu() {
		setupMenu();
	}

	private void setupMenu() {
		add(jmFile);
		jmFile.add(jmiRegisteredDevices);
		jmFile.add(jmiExit);
		add(jmServer);
		jmServer.add(jmiRefreshInterfaces);
		add(jmHelp);
		jmHelp.add(jmiDebugReport);
		
	}

}
