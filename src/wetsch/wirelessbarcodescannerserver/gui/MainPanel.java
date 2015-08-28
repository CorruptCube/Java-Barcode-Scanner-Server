package wetsch.wirelessbarcodescannerserver.gui;

import java.awt.Toolkit;	
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import wetsch.wirelessbarcodescannerserver.BarcodeReceiverEvent;
import wetsch.wirelessbarcodescannerserver.BarcodeServerDataListener;
import wetsch.wirelessbarcodescannerserver.WirelessBarcodeScannerServer;

/*
** Last modified on 8/28/2015
*Fixed message in catch error if server can not be shutdown.
 */

/**
 * This class extends MainPanelLayout.  This class holds the code to interact with the JFrame.
 * @author kevin
 *@version 1.0
 */
public class MainPanel  extends MainPanelLayout implements BarcodeServerDataListener, ActionListener{
	private static final long serialVersionUID = 1L;
	
	
	private WirelessBarcodeScannerServer server = null;

	public MainPanel() {
		setupActionListeners();
	}
	
	//Set up listeners.
	private void setupActionListeners(){
		btnStartServer.addActionListener(this);
		btnStopserver.addActionListener(this);
		btnCopyBarcodeToClipboard.addActionListener(this);
		btnExit.addActionListener(this);
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
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Could not shut down server");
			}
	}
	
	/*
	 * Listener method for start server button.
	 * if the server thread is equal to null,
	 * a new thread is created and the server is started.
	 * 
	 */
	private void btnStartServerListener(){
		String address = (String) jcbInterfaces.getSelectedItem();
		int port = Integer.parseInt(jtfPort.getText());
		if(server == null){
			server = new WirelessBarcodeScannerServer(address, port);
			server.addServerDatareceivedListener(this);
			server.start();
		}
		lblServerStatus.setText("Running");
		lblServerAddress.setText(address);
		lblServerPort.setText(Integer.toString(port));
	}
	
	//Listener method for the stop server button.
	private void btnStopServerListener(){
		stopServer();
		lblServerStatus.setText("Not Running");
		lblServerAddress.setText("N/A");
		lblServerPort.setText("N/A");

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
	
	//Listener method for the exit button.
	private void btnExitListener(){
		stopServer();
		System.exit(0);
	}
	

	
	//Listeners

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnStartServer){
			btnStartServerListener();
		}else if(e.getSource() == btnStopserver){
			btnStopServerListener();
		}else if(e.getSource() == btnCopyBarcodeToClipboard){
			btnCopyToClipboardListener();
		}else if(e.getSource() == btnExit){
			btnExitListener();
		}
	}

	//Handle the barcode data when received by server.
	@Override
	public void barcodeServerDatareceived(BarcodeReceiverEvent e) {
		DefaultTableModel model = (DefaultTableModel) jtbcTable.getModel();
		model.addRow(new String[]{e.getBarcodeType(), e.getBarcode()});
		DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		lblMessages.setText("Last barcode received from " + e.getClientInetAddress()+ " at " + df.format(cal.getTime()) + ".");
	}
}
