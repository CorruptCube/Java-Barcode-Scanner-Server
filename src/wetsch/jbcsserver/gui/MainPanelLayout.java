package wetsch.jbcsserver.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.net.SocketException;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import wetsch.jbcsserver.server.JbcsServer;
import wetsch.jbcsserver.tools.DebugPrinter;

/*
 * Last modified on 2/8/2016
 * Changes:
 * Added button to save barcode data table to a CSV file.
 */

/**
 * This class holds all the objects and layout for the JFrame.
 * @author kevin
 *@version 1.0
 */
public abstract class MainPanelLayout extends JFrame{
	private static final long serialVersionUID = 1L;
	private final Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();//Hold the screen size.
	private GridBagConstraints jplc = new GridBagConstraints();//Layout manager to use.
	
	private JPanel jpMainPanel = new JPanel(new GridBagLayout());//The main panel to hold the widgets.
	private JPanel jpServerStatus = new JPanel(new GridBagLayout());//The panel to hold the widgets for the server status.
	private JPanel jpButtons = new JPanel(new GridBagLayout());//The panel to hold the widget buttons.
	private JPanel jpResultData = new JPanel(new GridLayout(0,1));//The panel that hold the table result data.
	private JPanel jpConnectionConfig = new JPanel(new GridBagLayout());//The panel to hold the widgets that configure the server connection.
	private JPanel jpStatusBar = new JPanel(new BorderLayout());//The panel to hold the status bar.
	private JPanel jpServerConsoleButtons = new JPanel(new GridLayout(1,2));//the panel to hold the server console buttons.
	private JPanel jpServerConsole = new JPanel(new GridLayout(1, 1));//The panel to hold the server console.
	
	protected JTextArea jtaServerConsole = null;//The widget that store console output.
	
	protected JLabel lblServerAddress = new JLabel("N/A");//Label that holds the listening server address.
	protected JLabel lblServerPort = new JLabel("N/A");//Label to hold the listening server port.
	protected JLabel lblServerStatus = new JLabel("Not Running");//Label to hold the server status.
	protected JLabel lblMessages = new JLabel("Ready");//Label that holds status-bar messages.
	
	private JScrollPane jspbcdPane = null;//The scroll-pane for the barcode data JTable.
	private JScrollPane jspServerConsole = null;//The scroll-pane for the server console messages.
	
	protected JTable jtbcTable = new JTable();//The table to hold barcode data.
	
	protected JComboBox<String> jcbInterfaces = new JComboBox<String>();//Holds available listening interface IP addresses.

	protected JTextField jtfPort = new JTextField("9800");//Holds the listening port for the server.  Default port is 9800.
	
	protected JButton btnStartStopServer = new JButton("Start server");//Button to start/stop the server.
	protected JButton btnCopyBarcodeToClipboard = new JButton("Copy barcode to clipboard");//Button to copy barcode value to the clip-board.
	protected JButton btnRobot = new JButton("Turn robot on");//Button to turn on/off robot.
	protected JButton btnCloseToTray = new JButton("Minimize to tray");//Button to minimize to system tray.
	protected JButton btnExit = new JButton("Exit");//Button to exit program.
	protected JButton btnConsoleClear = new JButton(new ImageIcon(getClass().getResource("/console-clear-btn.png")));//Button to clear the console.
	protected JButton btnSaveConsole = new JButton(new ImageIcon(getClass().getResource("/console-save-btn.png")));//Button to save console messages to file.
	protected JButton btnSaveCsvFile = new JButton(new ImageIcon(getClass().getResource("/save-csv-file-btn.png")));//Button to save barcode table data to CSV file.

	public MainPanelLayout(){
		super("Wirless barcode Scanner Server Interface");
		setSize(screen.width/2, screen.height/2+100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(new GridLayout(1,1));
		setVisible(true);
		add(jpMainPanel);
		setLayout(new GridLayout(1,1));
		
		layoutSetup();
		populatejcbInterfaces();
	}
	
	//Setup the frame layout.
	private void layoutSetup(){
		addComp(jpMainPanel, jpServerStatus, 1, 1, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 0, 0);
		jpServerStatusSetup();

		jplc.insets = new Insets(1, 1, 1, 1);
		addComp(jpMainPanel, jpButtons, 1, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0.2, 0);
		jpButtonsSetup();

		jplc.insets = new Insets(1, 1, 1, 1);
		addComp(jpMainPanel, jpResultData, 2, 2, 1, 2, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0.8, 0);
		jpResultDataSetup();
		
		addComp(jpMainPanel, jpConnectionConfig, 1, 3, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0, 0);
		jpConnectionConfigSetup();
		
		addComp(jpMainPanel, jpServerConsoleButtons, 2, 4 , 1, 1, GridBagConstraints.EAST, GridBagConstraints.NORTHEAST, 0, 0);

		addComp(jpMainPanel, jpServerConsole, 1, 5 , 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 1, 1);
		jpServerConsoleSetup();

		
		jpStatusBar.setBorder(BorderFactory.createLineBorder(Color.gray));
		jpStatusBar.add(lblMessages, BorderLayout.WEST);
		addComp(jpMainPanel, jpStatusBar, 1, 6, 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0, 0.2);
		
	}

	//Setup the panel that holds the GUI objects for the server status.
	private void jpServerStatusSetup(){
		jpServerStatus.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JLabel l1 = new JLabel("Server status:");
		addComp(jpServerStatus, l1, 1, 1, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 0.5, 0);
		
		addComp(jpServerStatus, lblServerStatus, 2, 1, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 0.5, 0);
		jplc.insets = new Insets(10, 0, 0, 0);
		JLabel l2 = new JLabel("Listening Address:");
		addComp(jpServerStatus, l2, 1, 2, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.BOTH, 0.5, 0);

		addComp(jpServerStatus, lblServerAddress, 2, 2, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 0.5, 0);

		JLabel l3 = new JLabel("Listening Port:");
		addComp(jpServerStatus, l3, 1, 3, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 0.5, 0);

		addComp(jpServerStatus, lblServerPort, 2, 3, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 0.5, 0);
	}
	
	//Setup the Panel that holds the buttons.
	private void jpButtonsSetup(){
		btnStartStopServer.setToolTipText("Start/stop the server.");
		addComp(jpButtons, btnStartStopServer, 1, 1, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 0.5, 0);

		jplc.insets = new Insets(15, 0, 0, 0);
		btnCopyBarcodeToClipboard.setToolTipText("Copy selected barcode to clipboard.");
		addComp(jpButtons, btnCopyBarcodeToClipboard, 1, 2, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 0.5, 0);
		btnRobot.setToolTipText("Turn on/off robot.");
		addComp(jpButtons, btnRobot, 1, 3, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 0.5, 0);
		btnCloseToTray.setToolTipText("Minimize to system tray icon.");
		addComp(jpButtons, btnCloseToTray, 1, 4, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);
		
		btnExit.setToolTipText("Shutdown and exit the server interface.");
		addComp(jpButtons, btnExit, 1, 5, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 1, 1);

	}
	
	//Setup the Table that holds the data received by the server.
	private void jpResultDataSetup(){
		String[] columnNames = new String[]{"Barcode Type","Barcode"};
		DefaultTableModel model = new DefaultTableModel(columnNames,0);
		jtbcTable.setBackground(Color.white);
		jtbcTable.setModel(model);
		jspbcdPane = new JScrollPane(jtbcTable);
		addComp(jpResultData, jspbcdPane, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 1, 1);
		jtbcTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	//Setup the panel that holds the GUI objects to setup the servers configuration.
	private void jpConnectionConfigSetup(){
		
		JLabel l1 = new JLabel("Interfaces:");
		addComp(jpConnectionConfig, l1, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, 0, 0);
		jcbInterfaces.setToolTipText("Interface for server to listen on.");
		addComp(jpConnectionConfig, jcbInterfaces, 2, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);

		JLabel l2 = new JLabel("Port:");
		addComp(jpConnectionConfig, l2, 1, 2, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, 0, 0);
		
		jtfPort.setToolTipText("Port number for server to listen on.");
		addComp(jpConnectionConfig, jtfPort, 2, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 1, 1);
	}
	
	//Setup the panel for the server console.
	private void jpServerConsoleSetup(){
		btnSaveCsvFile.setBorderPainted(false);
		btnSaveCsvFile.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		btnSaveCsvFile.setToolTipText("Save barcode data to CSV file.");
		jpServerConsoleButtons.add(btnSaveCsvFile);

		jtaServerConsole = new JTextArea();
		jtaServerConsole.setToolTipText("Server Console.");
		jtaServerConsole.setLineWrap(true);
		jtaServerConsole.setEditable(false);
		jspServerConsole = new JScrollPane(jtaServerConsole);
		jpServerConsole.add(jspServerConsole);
		
		btnConsoleClear.setBorderPainted(false);
		btnConsoleClear.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		btnConsoleClear.setSize(btnConsoleClear.getPreferredSize());
		btnConsoleClear.setToolTipText("Clear console.");
		jpServerConsoleButtons.add(btnConsoleClear);
		
		btnSaveConsole.setBorderPainted(false);
		btnSaveConsole.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		btnSaveConsole.setToolTipText("Save console to file.");
		jpServerConsoleButtons.add(btnSaveConsole);
	}


	//Populating the combo box that holds the available IPV4 addresses on the system.
	private void populatejcbInterfaces(){
		try {
			if(JbcsServer.getAvailableIPV4Addresses().length > 0)
				jcbInterfaces.setModel(new DefaultComboBoxModel<String>(JbcsServer.getAvailableIPV4Addresses()));
		} catch (SocketException e) {
			DebugPrinter printer = new DebugPrinter();
			printer.sendDebugToFile(e);
			JOptionPane.showMessageDialog(this, e.getMessage());
			
		}
	}
	
	//Handles the adding of objects for the grid-bag layout.
	 private void addComp(JPanel thePanel, JComponent comp, int xPos, int yPos, int compWidth, int compHeight, int place, int stretch, double weightx, double weighty){
		 jplc.gridx = xPos;
		 jplc.gridy = yPos;
		 jplc.gridwidth = compWidth;
		 jplc.gridheight = compHeight;
		 jplc.weightx = weightx;
		 jplc.weighty = weighty;
		 jplc.anchor = place;
		 jplc.fill = stretch;
	     thePanel.add(comp, jplc);
	 }
}