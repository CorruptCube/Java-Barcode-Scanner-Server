package wetsch.wirelessbarcodescannerserver.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;	
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import wetsch.wirelessbarcodescannerserver.BarcodeReceiverEvent;
import wetsch.wirelessbarcodescannerserver.BarcodeServerDataListener;
import wetsch.wirelessbarcodescannerserver.DebugPrinter;
import wetsch.wirelessbarcodescannerserver.Robot;
import wetsch.wirelessbarcodescannerserver.WirelessBarcodeScannerServer;

/*
** Last modified on 9/27/2015
 * Added support to print stack-trace to debug output file.
 */

/**
 * This class extends MainPanelLayout.  This class holds the code to interact with the JFrame.
 * @author kevin
 *@version 1.0
 */
public class MainPanel  extends MainPanelLayout implements BarcodeServerDataListener, ActionListener, Listener{
	private static final long serialVersionUID = 1L;
	private DebugPrinter debugPrinter = null;
	private boolean useRowbot = false;
	private WirelessBarcodeScannerServer server = null;
	private LinuxTrayIcon ltIcon = null;
	private SystemTrayIcon trayIcon = null;
	
	public MainPanel() {
		debugPrinter = new DebugPrinter("JBCS-server-debug-report.txt");
		if(SystemTray.isSupported() && !System.getProperty("os.name").equals("Linux")){
			setupSystemTrayIcon();
		}else{
			ltIcon = new LinuxTrayIcon(this);
			ltIcon.showIcon();
		}
		setupActionListeners();
		
	}
	
	//Set up listeners.
	private void setupActionListeners(){
		btnStartStopServer.addActionListener(this);
		btnCopyBarcodeToClipboard.addActionListener(this);
		btnRobot.addActionListener(this);
		btnExit.addActionListener(this);
		btnCloseToTray.addActionListener(this);
	}
	
	//Setup system tray icon
	private void setupSystemTrayIcon(){
		try {
			URL url = System.class.getResource("/tray_icon-16x16.png");
			Image img = Toolkit.getDefaultToolkit().getImage(url);
			trayIcon = new SystemTrayIcon(img, new TrayIconActionListener());
		} catch (AWTException e) {
			try {
				debugPrinter.sendDebugToFile(e);
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, e1.getMessage());

			}
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage() + "\nyou can find out more in the debug output file stored at " + debugPrinter.getDebugReportFilePath());
		}
	}
	
	/*
	 * If server is not = to null, shut down the server thread.
	 * If the server is = null, the server is allready dead and
	 * the method finishes.
	 */
	private void stopServer(){
		try {
			if(server != null){
				server.shutDownServer();
				server.join();
				server = null;
				lblMessages.setText("Server shutdown successfuly.");
			}
			}catch (InterruptedException | IOException e) {
				try {
					debugPrinter.sendDebugToFile(e);
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, e1.getMessage());
				}
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.getMessage() + "\nyou can find out more in the debug output file stored at " + debugPrinter.getDebugReportFilePath());
			}
	}
	
	private void openCloseInterface(){
		if(isVisible()){
			setVisible(false);
			if(trayIcon != null)
				trayIcon.getMenuItemOpenInterface().setLabel("Show interface");
			else if(ltIcon != null)
				ltIcon.changeMenuItemLabel("Show interface", ltIcon.getItemShowHideInterface());
		}else{
			setVisible(true);
			if(trayIcon != null)
				trayIcon.getMenuItemOpenInterface().setLabel("Hide interface");
			else if(ltIcon != null)
				ltIcon.changeMenuItemLabel("Hide interface", ltIcon.getItemShowHideInterface());
		}
	}
	
	/*
	 * Listener method for start server button.
	 * if the server thread is equal to null,
	 * a new thread is created and the server is started.
	 * 
	 */
	private void btnStartStopServerListener(){
		if(server == null){
			String address = (String) jcbInterfaces.getSelectedItem();
			int port = Integer.parseInt(jtfPort.getText());
			server = new WirelessBarcodeScannerServer(address, port);
			server.addServerDatareceivedListener(this);
			server.start();
			btnStartStopServer.setText("Stop Server");
			lblServerStatus.setText("Running");
			lblServerAddress.setText(address);
			lblServerPort.setText(Integer.toString(port));
			if(trayIcon != null)
				trayIcon.getMenuItemStartStopServer().setLabel("Stop server");
			else if(ltIcon != null)
				ltIcon.changeMenuItemLabel("Stop server", ltIcon.getItemStartServer());
		}else if(server!= null){
			stopServer();
			btnStartStopServer.setText("Start server");
			lblServerStatus.setText("Not Running");
			lblServerAddress.setText("N/A");
			lblServerPort.setText("N/A");
			if(trayIcon != null)
				trayIcon.getMenuItemStartStopServer().setLabel("Start server");
			else if(ltIcon != null)
				ltIcon.changeMenuItemLabel("Start server", ltIcon.getItemStartServer());
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
		if(useRowbot){
			useRowbot = false;
			btnRobot.setText("Turn robot on");
			if(trayIcon != null)
				trayIcon.getMenuItemStartStopRobot().setLabel("Turn robot on");
			else if(ltIcon != null)
				ltIcon.changeMenuItemLabel("Turn robot on", ltIcon.getItemStartStopRobot());
		}else{
			useRowbot = true;
			btnRobot.setText("Turn robot off");
			if(trayIcon != null)
				trayIcon.getMenuItemStartStopRobot().setLabel("Turn robot off");
			else if(ltIcon != null)
				ltIcon.changeMenuItemLabel("Turn robot off", ltIcon.getItemStartStopRobot());
		}
	}
	
	//Listener method for the exit button.
	private void btnExitListener(){
		stopServer();
		System.exit(0);
	}
	
	//Listeners

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
			}else if(e.getSource() == btnExit){
				btnExitListener();
		}
	}
	
	//Linux system tray icon menu items listener.
	@Override
	public void handleEvent(Event event) {
		if(event.widget == ltIcon.getItemShowHideInterface()){
			openCloseInterface();
		}else if(event.widget == ltIcon.getItemStartServer()){
			btnStartStopServerListener();
		}else if(event.widget == ltIcon.getItemStartStopRobot()){
			btnRobotListener();
		}else if(event.widget == ltIcon.getItemExit()){
			btnExitListener();
		}
	}

	//Handle the barcode data when received by server.
	@Override
	public void barcodeServerDatareceived(BarcodeReceiverEvent e) {
		
		DefaultTableModel model = (DefaultTableModel) jtbcTable.getModel();
		try{
		model.addRow(new String[]{e.getBarcodeType(), e.getBarcode()});
		/*
		 * Enable the robot to use the keyboard.
		 * This is what allows the barcode to be
		 * inputed outside the program.
		 */
		if(useRowbot){
			Robot robot = new Robot();
			robot.typeString(e.getBarcode());
		}
		DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		lblMessages.setText("Last barcode received from " + e.getClientInetAddress()+ " at " + df.format(cal.getTime()) + ".");
		}catch(Exception ex){
			try {
				debugPrinter.sendDebugToFile(ex);
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, ex.getMessage());
			}
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex.getMessage() + "\n you can find out more in the debug output file stored at " + debugPrinter.getDebugReportFilePath());

		}
	}
	
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
}
