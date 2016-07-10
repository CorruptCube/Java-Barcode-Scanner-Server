package wetsch.jbcsserver.gui.debugreporting;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.JOptionPane;

import wetsch.jbcsserver.tools.Tools;

/*
 * Last Modified: 7/7/2016
 */

public class DebugReportPanel extends DebugReportPanelLayout implements ActionListener{
	private static final long serialVersionUID = 1L;

	private File debugReportFile = new File(Tools.getDebugPrinterFilePath());//instance of debug report text file.
	public DebugReportPanel() {
		addListeners();
		loadReport();
	}
	
	//Add listeners to buttons.	
	private void addListeners(){
		btnClearLog.addActionListener(this);
		btnClose.addActionListener(this);
	}
	
	/*
	 * Load the log file.
	 */
	private void loadReport(){
		if(!debugReportFile.exists())
			return;
		try {
			lblFilePath.setText("File:" + debugReportFile.getAbsolutePath());
			byte[] data = new byte[(int) debugReportFile.length()];
			RandomAccessFile raf = new RandomAccessFile(debugReportFile, "r");
			raf.read(data);
			raf.close();
			jtaDebugReport.setText(new String(data,"utf-8"));
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	
	//Action methods
	
	private void clearLogFileAction(){
		if(!debugReportFile.exists())
			return;
		int action = JOptionPane.showConfirmDialog(this, "Are you sure you like to clear the log", "Confirm", JOptionPane.YES_NO_OPTION);
		if(action == JOptionPane.YES_OPTION){
			jtaDebugReport.setText(null);
			debugReportFile.delete();
		}
	}	
	

	//Listeners
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnClearLog){
			clearLogFileAction();
		}else if(e.getSource() == btnClose){
			dispose();
		}
	}

}
