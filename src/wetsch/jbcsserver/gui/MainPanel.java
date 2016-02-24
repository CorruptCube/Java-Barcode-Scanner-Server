package wetsch.jbcsserver.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;	
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;

import wetsch.jbcsserver.BarcodeReceiverEvent;
import wetsch.jbcsserver.BarcodeServerDataListener;
import wetsch.jbcsserver.CsvFileWritter;
import wetsch.jbcsserver.DebugPrinter;
import wetsch.jbcsserver.Robot;
import wetsch.jbcsserver.WirelessBarcodeScannerServer;

/*
** Last modified on 2/22/2016
 * Added some debugging lines.
 */

/**
 * This class extends MainPanelLayout.  This class holds the code to interact with the JFrame.
 * @author kevin
 *@version 1.0
 */
public class MainPanel  extends MainPanelLayout implements BarcodeServerDataListener, ActionListener, Listener{
	private static final long serialVersionUID = 1L;
	private DebugPrinter debugPrinter = null;//Object to write debug output to file.
	private boolean useRowbot = false;//Determine if to use robot.
	private WirelessBarcodeScannerServer server = null;//Barcode scanner server object.
	private SWATWidgets swtWidgets = null;//SWT widgets object.
	private SystemTrayIcon trayIcon = null;//Windows system tray icon
	
	public MainPanel() {
		debugPrinter = new DebugPrinter();
		if(SystemTray.isSupported() && !System.getProperty("os.name").equals("Linux")){
			setupSystemTrayIcon();
		}else
			setupSWATWidgets();
		setupActionListeners();
		
	}
	
	//Set up listeners.
	private void setupActionListeners(){
		btnStartStopServer.addActionListener(this);
		btnCopyBarcodeToClipboard.addActionListener(this);
		btnRobot.addActionListener(this);
		btnExit.addActionListener(this);
		btnCloseToTray.addActionListener(this);
		btnConsoleClear.addActionListener(this);
		btnSaveConsole.addActionListener(this);
		btnSaveCsvFile.addActionListener(this);
	}
	
	//Setup SWT widgets.
	private void setupSWATWidgets(){
		swtWidgets = new SWATWidgets(this);
		swtWidgets.showIcon();
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
	
	//Show or hide the interface window.
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
				trayIcon.getMenuItemOpenInterface().setLabel("Hide interface");
			}else if(swtWidgets != null){
				swtWidgets.changeMenuItemLabel("Hide Interface", swtWidgets.getItemShowHideInterface());
			}
		}
	}
	
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
			else if(swtWidgets != null)
				swtWidgets.changeMenuItemLabel("Stop server", swtWidgets.getItemStartServer());
		}else if(server!= null){
			stopServer();
			btnStartStopServer.setText("Start server");
			lblServerStatus.setText("Not Running");
			lblServerAddress.setText("N/A");
			lblServerPort.setText("N/A");
			if(trayIcon != null)
				trayIcon.getMenuItemStartStopServer().setLabel("Start server");
			else if(swtWidgets != null)
				swtWidgets.changeMenuItemLabel("Start server", swtWidgets.getItemStartServer());
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
			else if(swtWidgets != null)
				swtWidgets.changeMenuItemLabel("Turn robot on", swtWidgets.getItemStartStopRobot());
		}else{
			useRowbot = true;
			btnRobot.setText("Turn robot off");
			if(trayIcon != null)
				trayIcon.getMenuItemStartStopRobot().setLabel("Turn robot off");
			else if(swtWidgets != null)
				swtWidgets.changeMenuItemLabel("Turn robot off", swtWidgets.getItemStartStopRobot());
		}
	}
	
	//Listener method for the exit button.
	private void btnExitListener(){
		stopServer();
		System.exit(0);
	}
	
	//Listener method for console clear button.
	private void clearConsole(){
		jtaServerConsole.setText("");
		lblMessages.setText("Console cleared.");
	}
	
	/*
	 * Listener method for save console to file button.
	 * This method uses SWT libraries to to handle the file dialog for Linux.
	 * The JVM would crash in Linux using the JFileChooser because of the
	 * system tray icon loaded by SWT.  The method checks if the os is Linux
	 * so it can determine if it should use JFileChooser or SWT FileDialog.
	 */
	private void saveConsoleToFile(){
		if(swtWidgets != null){
			swtWidgets.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					try{
						FileDialog fd = new FileDialog(swtWidgets.getShell(), SWT.SAVE);
						fd.setText("Save console output.");
						fd.open();
						if(fd.getFileName().equals("Untitled"))
							return;
						File f = new File(fd.getFilterPath() + "/" + fd.getFileName());
						if(!f.exists())
							f.createNewFile();
						FileWriter fw = new FileWriter(f, true);
						fw.write(jtaServerConsole.getText().toString());
						fw.close();
						lblMessages.setText("File saved successfully to " + f.getAbsolutePath());
					}catch(Exception e){
						try {
							debugPrinter.sendDebugToFile(e);
							swtWidgets.showMessageBoxError(e.getMessage());
						} catch (IOException e1) {
							e1.printStackTrace();
							swtWidgets.showMessageBoxError(e1.getMessage());
						}
					}
				}
			});
		}else{
			try{
				File f = null;
				JFileChooser fc = new JFileChooser();
				int selection = fc.showSaveDialog(this);
				if(selection != JFileChooser.APPROVE_OPTION)
					return;
				f = new File(fc.getSelectedFile().getAbsolutePath());
				if(!f.exists())
					f.createNewFile();
				FileWriter fw = new FileWriter(f,true);
				fw.write(jtaServerConsole.getText().toString());
				fw.close();
				lblMessages.setText("File saved successfully to " + f.getAbsolutePath());
			}catch(Exception e){
				try {
					debugPrinter.sendDebugToFile(e);
					JOptionPane.showMessageDialog(this, e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, e1.getMessage());
				}
			}
		}
	}
	
	//Listener method for saving barcode table data to CSV file.	
	private void saveBarcodeDataTableAsCsvFile(){
		if(jtbcTable.getRowCount() == 0){
			JOptionPane.showMessageDialog(this, "There are no records in the table.");
			return;
		}
		if(swtWidgets != null){
			swtWidgets.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					try{
						FileDialog fd = new FileDialog(swtWidgets.getShell(), SWT.SAVE);
						fd.setText("Save console output.");
						fd.open();
						if(fd.getFileName().equals("Untitled"))
							return;
						CsvFileWritter cfw = new CsvFileWritter(getBarcodeTableData(), "|");
						cfw.writeCsvFile(fd.getFilterPath() + "/" + fd.getFileName());
						lblMessages.setText("File saved successfully to " + fd.getFilterPath() + "/" + fd.getFileName());
					}catch(Exception e){
						try {
							debugPrinter.sendDebugToFile(e);
							swtWidgets.showMessageBoxError(e.getMessage());
						} catch (IOException e1) {
							e1.printStackTrace();
							swtWidgets.showMessageBoxError(e1.getMessage());
						}
					}
				}
			});
		}else{
			try{
				JFileChooser fc = new JFileChooser();
				int selection = fc.showSaveDialog(this);
				if(selection != JFileChooser.APPROVE_OPTION)
					return;
				CsvFileWritter cfw = new CsvFileWritter(getBarcodeTableData(), "|");
				cfw.writeCsvFile(fc.getSelectedFile().getAbsolutePath());
				lblMessages.setText("File saved successfully to " + fc.getSelectedFile().getAbsolutePath());
			}catch(Exception e){
				try {
					debugPrinter.sendDebugToFile(e);
					JOptionPane.showMessageDialog(this, e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, e1.getMessage());
				}
			}
		}
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
		}else if(e.getSource() == btnConsoleClear){
			clearConsole();;
		}else if(e.getSource() == btnSaveConsole){
			saveConsoleToFile();
		}else if(e.getSource() == btnSaveCsvFile){
			saveBarcodeDataTableAsCsvFile();
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
	
	@Override
	public void barcodeServerConsole(String message) {
		jtaServerConsole.append(message + "\n");
	}
	
	//Linux system tray icon menu items listener.
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
