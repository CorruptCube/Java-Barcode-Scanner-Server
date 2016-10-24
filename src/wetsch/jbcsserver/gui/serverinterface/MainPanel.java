package wetsch.jbcsserver.gui.serverinterface;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;	
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import wetsch.jbcsserver.gui.SystemTrayIcon;
import wetsch.jbcsserver.gui.debugreporting.DebugReportPanel;
import wetsch.jbcsserver.gui.registereddevices.RegisteredDeicesMainPanel;
import wetsch.jbcsserver.gui.swt.SWATWidgets;
import wetsch.jbcsserver.server.JbcsServer;
import wetsch.jbcsserver.server.listeners.BarCoderEvent;
import wetsch.jbcsserver.server.listeners.JbcsServerListener;
import wetsch.jbcsserver.server.listeners.ServerEvent;
import wetsch.jbcsserver.tools.CsvFileWritter;
import wetsch.jbcsserver.tools.DebugPrinter;
import wetsch.jbcsserver.tools.Robot;
import wetsch.jbcsserver.tools.Tools;

/*
* Last modified on 7/12/2016
 *Changes:
 *Add listener for refreshing the interfaces menu item.
 */

/**
 * This class extends MainPanelLayout.  This class holds the code to interact with the JFrame.
 * @author kevin
 *@version 1.0
 */
public class MainPanel  extends MainPanelLayout implements ActionListener{
	private static final long serialVersionUID = 1L;

	private boolean useRobot = false;//Determine if robot is on/off.
	private JbcsServer server = null;//Barcode scanner server object.
	private SWATWidgets swtWidgets = null;//SWT widgets object.
	private SystemTrayIcon trayIcon = null;//Windows system tray icon
	private RegisteredDeicesMainPanel registeredDeicesMainPanel = null;//Instance to the registration system main panel controls.

	public MainPanel() {
		setupActionListeners();
		if(SystemTray.isSupported() && !System.getProperty("os.name").equals("Linux")){
			setupSystemTrayIcon();
		}else
			/*
			* Due to nome shell 3, SWT libs are used for the system trat icon.
			* When testing this on java 1.9, it loooks as though the awt API may have been fixed.
			* Until this is confirmed, the SWT libs will be used in linux.
			*/
			setupSWATWidgets();
	}
	
	//Set up listeners.
	private void setupActionListeners(){
		btnStartStopServer.addActionListener(this);
		btnCopyBarcodeToClipboard.addActionListener(this);
		btnRobot.addActionListener(this);
		btnCloseToTray.addActionListener(this);
		btnConsoleClear.addActionListener(this);
		btnSaveConsole.addActionListener(this);
		btnSaveCsvFile.addActionListener(this);
		
		//Menu bar listeners
		menuBar.jmiExit.addActionListener(new MenuBarListener());
		menuBar.jmiRegisteredDevices.addActionListener(new MenuBarListener());
		menuBar.jmiRefreshInterfaces.addActionListener(new MenuBarListener());
		menuBar.jmiDebugReport.addActionListener(new MenuBarListener());
	}
	
	/*
	 * Setup SWT widgets.
	 * These libraries are used to load the system tray icon for Linux.
	 * The main Java API does not load the system tray icon  correctly for Nome-shell.
	 * The SWT libraries are also used with saving CSV and console to file because JFileChooser 
	 * clashes with the SWT thread. 
	 */
	private void setupSWATWidgets(){
		swtWidgets = new SWATWidgets(new LinuxSystemTrayListener());
		swtWidgets.showIcon();
	}
	
	/*
	 * Set up the system tray icon for Windows.
	 * The SWT libraries are not loaded and used 
	 * if the Operating-system is detected as Windows.
	 * The JfileChooser is also used instead of the SWT 
	 * widget for choosing a file to save CSV and console.
	 */
	private void setupSystemTrayIcon(){
		try {
			URL url = System.class.getResource("/tray_icon.png");
			Image img = Toolkit.getDefaultToolkit().getImage(url);
			trayIcon = new SystemTrayIcon(img, new TrayIconActionListener());
		} catch (AWTException e) {
			new DebugPrinter().sendDebugToFile(e);
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	/*
	 * This method opens a file dialog to save a file.
	 * If swtWidgets is not equal to null, the SWT
	 * file dialog widget is used.
	 * Otherwise, JFileChooser is used to get the file path. 
	 */
	private String getFileDialog(){
		try{
			if(swtWidgets != null){
				return swtWidgets.getSWTFileDialog();
			}else{
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Save file to?");
				fc.setCurrentDirectory(new File(Tools.getApplicationDir()));
				int selection = fc.showSaveDialog(this);
				if(selection != JFileChooser.APPROVE_OPTION)
					return null;
				return fc.getSelectedFile().getAbsolutePath();
			}

		}catch(Exception e){
			new DebugPrinter().sendDebugToFile(e);
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * If server is not = to null, shut down the server thread.
	 * If the server is = null, the server is allready dead and
	 * the method finishes.
	 */
	
	//Get the data from the JTable holding the barcode data.
	private String[][] getBarcodeTableData(){
		if(jtbcTable.getRowCount() == 0)
			return null;
		String[][] data = new String[jtbcTable.getRowCount()+1][jtbcTable.getColumnCount()];
		for(int c = 0; c < jtbcTable.getColumnCount(); c++)
			data[0][c] = jtbcTable.getColumnModel().getColumn(c).getHeaderValue().toString();
		for(int r = 1; r < data.length; r++){
			for(int c = 0; c < data[r].length; c++){
				data[r][c] = jtbcTable.getValueAt(r-1, c).toString();
			}
		}
		return data;
	}
	
	/*
	 * Listener method for start/stop server button.
	 * if the server thread is equal to null,
	 * a new thread is created and the server is started.
	 * Otherwise, the server is stopped and the server object is set back to null.
	 * If the loop back address is used, a message is displayed to warn the user
	 * that the server will not be able to receieve information from the client.
	 * 
	 */
	private void btnStartStopServerListener(){
		try{
		String address = null;
		int port = 0;
		int poolLimit = 0;
		if(server == null){
			address = (String) jcbInterfaces.getSelectedItem();
			port = Integer.parseInt(jtfPort.getText());
			poolLimit = Integer.parseInt(jtfConnectionLimit.getText().toString());
			server = new JbcsServer(address, port, poolLimit);
			server.addListener(new JbcsServerListenerAdapter());
			if(registeredDeicesMainPanel != null)
				server.addListener(registeredDeicesMainPanel.serverListener);
			server.start();
		}else if(server!= null)
			server.shutDownServer();
		}catch(Exception e){
			new DebugPrinter().sendDebugToFile(e);
		}
	}
	
	//Listener method for the copy barcode to clip-board button.
	private void btnCopyToClipboardListener(){
		Toolkit tools = Toolkit.getDefaultToolkit();
		Clipboard clb = tools.getSystemClipboard();
		int selectedBarcode = jtbcTable.getSelectedRow();
		if(selectedBarcode != -1){
			String barcode = jtbcTable.getValueAt(selectedBarcode, 1).toString();
			clb.setContents(new StringSelection(barcode), null);
			lblMessages.setText("Barcode value copyed to clipboard.");
		}else
			lblMessages.setText("YOu do not have a barcode selected.");
	}
	
	//Listener method for the btnRobot button.
	private void btnRobotListener(){
		if(useRobot){
			useRobot = false;
			btnRobot.setText("Turn Robot On");
			if(trayIcon != null)
				trayIcon.getMenuItemStartStopRobot().setLabel("Turn Robot On");
			else if(swtWidgets != null)
				swtWidgets.changeMenuItemLabel("Turn Robot On", swtWidgets.getItemStartStopRobot());
		}else{
			useRobot = true;
			btnRobot.setText("Turn Robot Off");
			if(trayIcon != null)
				trayIcon.getMenuItemStartStopRobot().setLabel("Turn Robot Off");
			else if(swtWidgets != null)
				swtWidgets.changeMenuItemLabel("Turn Robot Off", swtWidgets.getItemStartStopRobot());
		}
	}
	
	/*
	 * Listener method for show or hide the interface window.
	 * The interface can also be shown or hiden from the 
	 * system tray icon.
	 */
	private void openCloseInterface(){
		if(isVisible()){
			setVisible(false);
			if(trayIcon != null)
				trayIcon.getMenuItemOpenInterface().setLabel("Show interface");
			else if(swtWidgets != null){
				setVisible(false);
				swtWidgets.changeMenuItemLabel("Show Interface", swtWidgets.getItemShowHideInterface());
			}
		}else{
			setVisible(true);
			if(trayIcon != null){
				trayIcon.getMenuItemOpenInterface().setLabel("Hide Interface");
			}else if(swtWidgets != null){
				swtWidgets.changeMenuItemLabel("Hide Interface", swtWidgets.getItemShowHideInterface());
			}
		}
	}

	//Listener method for the exit button.
	private void btnExitListener(){
		try{
		if(server != null)
			server.shutDownServer();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			System.exit(0);
		}
	}
	
	//Listener method for console clear button.
	private void clearConsole(){
		jtaServerConsole.setText("");
		lblMessages.setText("Console cleared.");
	}
	
	/*
	 * Listener method for save console to file button.
	 */
	private void saveConsoleToFile(){
		String fileName = 	getFileDialog();
		if(fileName == null)
			return;
		try{
			File f = new File(fileName);
			if(!f.exists())
				f.createNewFile();
			FileWriter fw = new FileWriter(f);
			fw.write(jtaServerConsole.getText().toString());
			fw.close();
			lblMessages.setText("File saved successfully to " + fileName);
		}catch(Exception e){
			new DebugPrinter().sendDebugToFile(e);
			lblMessages.setText("Failed to write file " + fileName + ".");
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	/*Listener method for saving bar-code table data to CSV file.
	 */
	private void saveBarcodeDataTableAsCsvFile(){
		JTextField delim = new JTextField();
		Component[] dialog =  new Component[]{
			new JLabel("Enter a delimiter."),
			delim
		};
		if(jtbcTable.getRowCount() == 0){
			JOptionPane.showMessageDialog(this, "There is no barcode data to write.");
			return;
		}
			String fileName = getFileDialog();
		if(fileName == null)
			return;
		
		try{
			JOptionPane.showMessageDialog(this, dialog, "What is the delimter?",JOptionPane.QUESTION_MESSAGE);
			CsvFileWritter cfw = new CsvFileWritter(getBarcodeTableData(), delim.getText().toString());
			cfw.writeCsvFile(fileName);
			lblMessages.setText("File saved successfully to " + fileName);
		}catch(Exception e){
			new DebugPrinter().sendDebugToFile(e);
			lblMessages.setText("Failed to write file " + fileName + ".");
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	//Implemented Listeners
	
	//Main action listener method for the server interface main panel.
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnStartStopServer){
			btnStartStopServerListener();
		}else if(e.getSource() == btnCopyBarcodeToClipboard){
			btnCopyToClipboardListener();
		}if(e.getSource() == btnRobot){
			btnRobotListener();
		}else if(e.getSource() == btnCloseToTray){
			openCloseInterface();
		}else if(e.getSource() == btnConsoleClear){
			clearConsole();;
		}else if(e.getSource() == btnSaveConsole){
			saveConsoleToFile();
		}else if(e.getSource() == btnSaveCsvFile){
			saveBarcodeDataTableAsCsvFile();
		}
	}
	
	//Linux system tray icon menu items listener.
	private class LinuxSystemTrayListener implements Listener {
		@Override
		public void handleEvent(Event event) {
			if(event.widget == swtWidgets.getItemShowHideInterface()){
				openCloseInterface();
			}else if(event.widget == swtWidgets.getItemStartServer()){
				btnStartStopServerListener();
			}else if(event.widget == swtWidgets.getItemStartStopRobot()){
				btnRobotListener();
			}else if(event.widget == swtWidgets.getItemExit()){
				btnExitListener();
			}
		}
	}
	
	//This class is used for the menu bar actionn listener.
	private class MenuBarListener implements ActionListener{
		//Method to open device registration frame.
		private void openRegisteredDevices(){
			registeredDeicesMainPanel = new RegisteredDeicesMainPanel(server);
			registeredDeicesMainPanel.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					registeredDeicesMainPanel = null;
					super.windowClosed(e);
				}
			});
		}
		// Implemented method for click actions on the main menu bar.
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == menuBar.jmiExit){
				btnExitListener();
			}else if(e.getSource() == menuBar.jmiRegisteredDevices){
				openRegisteredDevices();
			}else if(e.getSource() == menuBar.jmiRefreshInterfaces){
				populatejcbInterfaces();
			}else if(e.getSource() == menuBar.jmiDebugReport){
				new DebugReportPanel();
			}
		}
	}

	//This class is used for the system tray icon for Windows and OS X based systems.
	private class TrayIconActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == trayIcon.getMenuItemOpenInterface()){
				openCloseInterface();
			}else if(e.getSource() == trayIcon.getMenuItemStartStopServer()){
				btnStartStopServerListener();
			}else if(e.getSource() == trayIcon.getMenuItemStartStopRobot()){
				btnRobotListener();
			}else if(e.getSource() == trayIcon.getMenuItemExit()){
				btnExitListener();
			}
		}
	}
	
	//This class is used for the JBCS server listener.
	private class JbcsServerListenerAdapter implements JbcsServerListener{

		//Called when the ServerStarted method is called from the JBCS server.
		private void serverStarted(JbcsServer s){
			btnStartStopServer.setText("Stop Server");
			lblServerStatus.setText("Running");
			lblServerAddress.setText(s.getListeningInetAddress());
			lblServerPort.setText(Integer.toString(s.getServerListeningPort()));
			if(trayIcon != null)
				trayIcon.getMenuItemStartStopServer().setLabel("Stop Server");
			else if(swtWidgets != null)
				swtWidgets.changeMenuItemLabel("Stop Server", swtWidgets.getItemStartServer());
			if(s.getListeningInetAddress().equals("127.0.0.1"))
				JOptionPane.showMessageDialog(null, "The server is listening on the loopback address. This will prevent the server from receieving information from the client.", "Warning", JOptionPane.WARNING_MESSAGE);
		}
		
		//Called when the ServerStopped method is called from the JBCS server.
		private void serverStopped(){
			btnStartStopServer.setText("Start Server");
			lblServerStatus.setText("Not Running");
			lblServerAddress.setText("N/A");
			lblServerPort.setText("N/A");
			if(trayIcon != null)
				trayIcon.getMenuItemStartStopServer().setLabel("Start Server");
			else if(swtWidgets != null)
				swtWidgets.changeMenuItemLabel("Start Server", swtWidgets.getItemStartServer());
		}
	
		//Handle the barcode data when received by server.
		@Override
		public void barcodeServerDatareceived(BarCoderEvent e) {
			DefaultTableModel model = (DefaultTableModel) jtbcTable.getModel();
			try{
				model.addRow(new String[]{e.getBarcodeType(), e.getBarcode()});
				/*
				 * Enable the robot to use the keyboard.
				 * This is what allows the barcode to be
				 * inputed outside the program.
				 */
				if(useRobot){
					Robot robot = new Robot();
					robot.typeString(e.getBarcode());
				}
				DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				lblMessages.setText("Last barcode received from " + e.getClientInetAddress()+ " at " + df.format(cal.getTime()) + ".");
			}catch(Exception ex){
				new DebugPrinter().sendDebugToFile(ex);
				JOptionPane.showMessageDialog(null, ex.getMessage());
			}
		}
		//Set UI objects when the server is started.
		@Override
		public void serverStarted(ServerEvent e) {
			serverStarted((JbcsServer) e.getSource());
		}
	
		//Set UI objects when the server is stopped.
		@Override
		public void ServerStopped(ServerEvent e) {
			serverStopped();
			server = null;
		}

		//Console messages sent from server.
		@Override
		public void serverConsole(String message) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try{
						jtaServerConsole.append(message + "\n");
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		}
	}
}