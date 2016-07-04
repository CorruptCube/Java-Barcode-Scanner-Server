package wetsch.jbcsserver.gui.registereddevices;

import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import wetsch.jbcsserver.gui.customuiobjects.CustomToggleButton;
import wetsch.jbcsserver.server.registrationsystem.Device;

public abstract class RegisteredDevicesMainPanelLayout extends JFrame{
	private static final long serialVersionUID = 1L;
	private final Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();//Hold the screen size.
	
	protected final int serverStatusRunningMessage = 0;//Server status message if running
	protected final int serverStatusNotRunningMessage = 1;//Server status message if not running.
	
	private GridBagConstraints jplc = new GridBagConstraints();//Layout manager to use.
	
	private JPanel container = new JPanel(new GridBagLayout());//Main container.
	
	protected JList<Device> jlDevieList = new JList<Device>();//Hold device list.
	
	protected JTextField jtfDeviceName = new JTextField(20);//Hold device name.
	protected JTextField jtfDeviceAddDate = new JTextField(20);//Hold device add date.
	protected JTextField jtfDeviceRegistrationId = new JTextField(20);//Hold device registration D.
	
	protected JButton btnAddDevice = new JButton("Add Device");//Add devie buton.
	protected JButton btnRemoveDevice = new JButton("Remove Device");//Remove device button.
	protected JButton btnUpdateDevice = new JButton("Update Device");
	protected JButton btnClose = new JButton("Close");//Close the window button.
	
	protected CustomToggleButton tbtenEnableSystem = new CustomToggleButton("Enforce");
	
	protected JLabel lblStatusMesssage = new JLabel();
	public RegisteredDevicesMainPanelLayout() {
		super("Registered Devices");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(screen.width/2, screen.height/2);
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(new GridLayout(1,1));
		setLayout(new GridLayout(1,1));
		layoutSetup();
		setVisible(true);
	}

	private void layoutSetup() {
		add(container);
		JScrollPane jsp = new JScrollPane(jlDevieList);
		addComp(container, jsp, 1, 1, 1, 4, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0.2, 0.5);
		
		jplc.insets = new Insets(0, 0, 15, 0);
		addComp(container, new JLabel("Device Name:", SwingConstants.RIGHT), 2, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);
		addComp(container, jtfDeviceName, 3, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0.8, 0);

		addComp(container, new JLabel("Add Date:", SwingConstants.RIGHT), 2, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);
		jtfDeviceAddDate.setEditable(false);
		addComp(container, jtfDeviceAddDate, 3, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0.8, 0);

		addComp(container, new JLabel("Device ID:", SwingConstants.RIGHT), 2, 3, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);
		jtfDeviceRegistrationId.setEditable(false);
		addComp(container, jtfDeviceRegistrationId, 3, 3, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0.8, 0);

		jplc.insets = new Insets(0, 0, 0, 0);
		addComp(container, btnUpdateDevice, 3, 4, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);

		JPanel p1 = new JPanel(new GridLayout(1,2));
		p1.add(btnAddDevice);
		p1.add(btnRemoveDevice);
		addComp(container, p1, 1, 5, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 0, 0);
		
		addComp(container, lblStatusMesssage, 1, 6, 3, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0, 0);

		addComp(container, tbtenEnableSystem, 1, 6, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 0, 0);

		addComp(container, lblStatusMesssage, 1, 7, 3, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0, 0);

		JPanel p2 = new JPanel(new GridLayout(1,2));
		p2.add(btnClose);
		addComp(container, p2, 3, 7, 1, 1, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, 1, 1);

		
	}

	protected String getServerStatusMessage(int status){
		StringBuilder sb = new StringBuilder("<html>");
		if(status == serverStatusRunningMessage){
			sb.append("The server is in the listening state to accept registration requests.  ");
			sb.append("You may send a request from the device at this time.  ");
			sb.append("Once a request is received, a message will pop up in this window asking if you would like to register this device.  ");
			sb.append("You may click yes to accept the registration request, or you may click no or cancel to reject it.");
			sb.append("</html>");
			return sb.toString();
		}else if(status == serverStatusNotRunningMessage){
			sb.append("The server is not runnig.  ");
			sb.append("You must first start the server if you want to receive registration requests from the server, otherwise you wll have to add devices manually."); 
			sb.append("</html>");
			return sb.toString();
		}
		return null;
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
