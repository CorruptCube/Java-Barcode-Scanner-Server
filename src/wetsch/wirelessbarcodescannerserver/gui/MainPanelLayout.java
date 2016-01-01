package wetsch.wirelessbarcodescannerserver.gui;

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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import wetsch.wirelessbarcodescannerserver.WirelessBarcodeScannerServer;

/*
 * Last modified on 12/30/2015
 * Changes:
 * Rewrote the method that populates the combo box of available interfaces. 
 */

/**
 * This class holds all the objects and layout for the JFrame.
 * @author kevin
 *@version 1.0
 */
public abstract class MainPanelLayout extends JFrame{
	private static final long serialVersionUID = 1L;
	private static Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	private GridBagConstraints jplc = new GridBagConstraints();
	
	private JPanel jpMainPanel = new JPanel(new GridBagLayout());
	private JPanel jpServerStatus = new JPanel(new GridBagLayout());
	private JPanel jpButtons = new JPanel(new GridBagLayout());
	private JPanel jpResultData = new JPanel(new GridLayout(0,1));
	private JPanel jpConnectionConfig = new JPanel(new GridBagLayout());
	private JPanel jpStatusBar = new JPanel(new BorderLayout());
	
	protected JLabel lblServerAddress = new JLabel("N/A");
	protected JLabel lblServerPort = new JLabel("N/A");
	protected JLabel lblServerStatus = new JLabel("Not Running");
	protected JLabel lblMessages = new JLabel("Ready");
	
	private JScrollPane jspbcdPane = null;

	protected JTable jtbcTable = new JTable();
	
	protected JComboBox<String> jcbInterfaces = new JComboBox<String>();

	protected JTextField jtfPort = new JTextField("9800");
	
	protected JButton btnStartStopServer = new JButton("Start server");
	protected JButton btnCopyBarcodeToClipboard = new JButton("Copy barcode to clipboard");
	protected JButton btnRobot = new JButton("Turn robot on");
	protected JButton btnCloseToTray = new JButton("Minimize to tray");
	protected JButton btnExit = new JButton("Exit");

	
	public MainPanelLayout(){
		super("Wirless barcode Scanner Server Interface");
		setSize(screen.width/2, screen.height/2);
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
		addComp(jpMainPanel, jpButtons, 1, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0.2, 0.5);
		jpButtonsSetup();

		jplc.insets = new Insets(1, 1, 1, 1);
		addComp(jpMainPanel, jpResultData, 2, 2, 1, 2, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0.8, 0.5);
		jpResultDataSetup();
		
		addComp(jpMainPanel, jpConnectionConfig, 1, 3, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0.2, 0);
		jpConnectionConfigSetup();
		
		jpStatusBar.setBorder(BorderFactory.createLineBorder(Color.gray));
		jpStatusBar.add(lblMessages, BorderLayout.WEST);
		addComp(jpMainPanel, jpStatusBar, 1, 4, 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0, 0.1);
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
		addComp(jpButtons, btnStartStopServer, 1, 1, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 0.5, 0);

		jplc.insets = new Insets(15, 0, 0, 0);
		addComp(jpButtons, btnCopyBarcodeToClipboard, 1, 2, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 0.5, 0);

		addComp(jpButtons, btnRobot, 1, 3, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 0.5, 0);

		addComp(jpButtons, btnCloseToTray, 1, 4, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);

		addComp(jpButtons, btnExit, 1, 5, 1, 1, GridBagConstraints.LAST_LINE_START, GridBagConstraints.HORIZONTAL, 1, 1);

	}
	
	//Setup the Table that holds the data received by the server.
	private void jpResultDataSetup(){
		String[] columnNames = new String[]{"Barcode Type","Barcode"};
		DefaultTableModel model = new DefaultTableModel(columnNames,0);
		jtbcTable.setModel(model);
		jspbcdPane = new JScrollPane(jtbcTable);
		addComp(jpResultData, jspbcdPane, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 1, 1);
		jtbcTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	//Setup the panel that holds the GUI objects to setup the servers configuration.
	private void jpConnectionConfigSetup(){
		
		JLabel l1 = new JLabel("Interfaces:");
		addComp(jpConnectionConfig, l1, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, 0, 0);

		addComp(jpConnectionConfig, jcbInterfaces, 2, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);

		JLabel l2 = new JLabel("Port:");
		addComp(jpConnectionConfig, l2, 1, 2, 1, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, 0, 0);

		addComp(jpConnectionConfig, jtfPort, 2, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 1, 1);
	}

	//Populating the combo box that holds the available IPV4 addresses on the system.
	private void populatejcbInterfaces(){
		try {
			if(WirelessBarcodeScannerServer.getAvailableIPV4Addresses().length > 0)
				jcbInterfaces.setModel(new DefaultComboBoxModel<String>(WirelessBarcodeScannerServer.getAvailableIPV4Addresses()));
		} catch (SocketException e) {
			e.printStackTrace();
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
