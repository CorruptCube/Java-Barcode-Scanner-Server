package wetsch.jbcsserver.gui.registereddevices;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import wetsch.jbcsserver.server.JbcsServer;
import wetsch.jbcsserver.server.listeners.DeviceRegistrationRequestListener;
import wetsch.jbcsserver.server.listeners.JbcsServerListenerAdapter;
import wetsch.jbcsserver.server.listeners.ServerEvent;
import wetsch.jbcsserver.server.registrationsystem.Device;
import wetsch.jbcsserver.server.registrationsystem.RegisteredDevices;
import wetsch.jbcsserver.tools.DebugPrinter;

/*
 * Last modified 9/9/2016
 */

/**
 * This class is contains all the undergone code with the UI.
 * @author kevin
 * @version 2.1
 *
 */
public class RegisteredDeicesMainPanel extends RegisteredDevicesMainPanelLayout implements ListSelectionListener, ActionListener{
	private static final long serialVersionUID = 1L;
	
	private int serverRegistrationRequest = 0;//Action for registerDevice() method when server receives a registration request.
	private int ManualDeviceRegistration = 1;//Action for registerDevice() method to manually add device.
	private JbcsServer server = null;
	private RegisteredDevices registeredDevices = RegisteredDevices.getInstance();

	public serverListener serverListener = new serverListener();//Listener object for the JBCS server.
	@SuppressWarnings("unchecked")
	public RegisteredDeicesMainPanel(JbcsServer server) {
		this.server = server;
		SetupListeners();
		if(server != null){
			server.addListener(serverListener);
			server.deviceRegistrationEnabled(true);
			server.addRegistrationRequestListener(serverListener);
		
		}
		jlDevieList.setCellRenderer(new CustomListCellRenderer());
		populateRegistredDevices();
		tbtenEnableSystem.setSelected(registeredDevices.isSystemEnabled());
		setupServrStatusMessage();
	}
	
	//Set up the server status message.
	private void setupServrStatusMessage(){
		if(server == null || !server.isServerRunning()){
			lblStatusMesssage.setIcon(new ImageIcon(getClass().getResource("/warning-icon.png")));
			lblStatusMesssage.setText(getServerStatusMessage(serverStatusNotRunningMessage));
		}else{
			lblStatusMesssage.setIcon(new ImageIcon(getClass().getResource("/info-icon.png")));
			lblStatusMesssage.setText(getServerStatusMessage(serverStatusRunningMessage));
		}
	}
	
	//Setup listeners.
	private void SetupListeners(){
		jlDevieList.addListSelectionListener(this);
		btnAddDevice.addActionListener(this);
		btnRemoveDevice.addActionListener(this);
		btnUpdateDevice.addActionListener(this);
		btnClose.addActionListener(this);
		tbtenEnableSystem.addActionListener(this);
	}
	
	/*
	 * Write the devices to file.
	 */
	private void SaveDevices(){
		try {
			RegisteredDevices.writeRegisteredDevices();
			showMessageDialog("Data saved sucessfully.");
		} catch (IOException e) {
			new DebugPrinter().sendDebugToFile(e);
			e.printStackTrace();
			showMessageDialog(e.getMessage());
		}
	}
	
	//Populates the device list with the registered device objects.
	private void populateRegistredDevices(){
		if(registeredDevices != null){
			DefaultListModel< Device> m = new DefaultListModel<Device>();
			for(Entry<String, Device> d : registeredDevices.entrySet()){
				m.addElement(d.getValue());
			}
			jlDevieList.setModel(m);
		}
	}
	
	/*
	 * Run the JOptionPane.shoeMessageDialog on the UI thread.
	 * This will keep any other thread from being hung up waiting for the box to close.
	 */
	private void showMessageDialog(final String message){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try{
					JOptionPane.showMessageDialog(null, message);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Registers the device with the server.
	 * If the server receives a request, the device and action are passed in 
	 * and a dialog asking to confirm will pop up.
	 * If a devices is being registered manually, the action is passed in and 
	 * device should be set to null. 
	 * @param action action to take.
	 * @param device the device to register.
	 * @return
	 */
	private boolean registerDevice(int action, Device device){
		switch (action) {
		case 0:
			int selection = JOptionPane.showConfirmDialog(null, "Registration request received by device with ID:" + device.getDeviceId() + " , would you like to register this device?", "Device request", JOptionPane.YES_NO_OPTION);
			if(selection == JOptionPane.YES_OPTION){
				String deviceName = JOptionPane.showInputDialog("What would you like to name this device?");
				if(deviceName.equals(""))
					device.setDeviceName("UNKNOWN");
				else
					device.setDeviceName(deviceName);
				registeredDevices.put(device.getDeviceId(), device);
				SaveDevices();
				DefaultListModel<Device> m = (DefaultListModel<Device>) jlDevieList.getModel();
				m.addElement(device);
				return true;
			}else{
				return false;
			}
		case 1:
			JTextField JtfDeviceName = new JTextField();
			JTextField jtfDeviceId = new JTextField();
			JComponent[] imputs = new JComponent[]{
				new JLabel("Device Name"),
				JtfDeviceName,
				new JLabel("Device ID"),
				jtfDeviceId
			};
			JOptionPane.showMessageDialog(null,imputs, "Add device", JOptionPane.PLAIN_MESSAGE);
			if(JtfDeviceName.getText().isEmpty() || jtfDeviceId.getText().isEmpty()){
				JOptionPane.showMessageDialog(this, "The name or ID for the device was missing.  The operation was canceled.");
				return false;
			}else if(registeredDevices.containsKey(jtfDeviceId.getText().toString())){
				JOptionPane.showMessageDialog(this, "Device is already registered.");
				return false;
			}
			DefaultListModel<Device> m = (DefaultListModel<Device>) jlDevieList.getModel();
			Device d = new Device(jtfDeviceId.getText().toString(), JtfDeviceName.getText().toString(), Calendar.getInstance().getTime());
			registeredDevices.put(jtfDeviceId.getText().toString(), d);
			SaveDevices();
			m.addElement(d);
			break;
		}
		return false;
	}
	
	@Override
	public void dispose(){
		closeWindowAction();
		super.dispose();
	}
	
	//Handlers
	
	private void triggerSystemenabledState(){
		registeredDevices.setSystemEnabled(tbtenEnableSystem.isSelected());
		SaveDevices();
	}
	
	/*
	 * Close the window.
	 */
	private void closeWindowAction(){
		if(server != null){
			server.deviceRegistrationEnabled(false);
			server.removeListener(serverListener);
			server.removeRegistrationRequestListener(serverListener);
		}
	}
	
	/*
	 * Method to add devices manually to the device registration system.
	 */
	private void AddDeviceAction(){
		registerDevice(ManualDeviceRegistration, null);
	}
	
	/*
	 * Remove the device from the registered devices.
	 * Be sure to save the changes, or the changes will 
	 * not take effect on application launch.
	 */
	private void removeDeviceAction(){
		Device d = jlDevieList.getSelectedValue();
		DefaultListModel<Device> m = (DefaultListModel<Device>) jlDevieList.getModel();
		if(registeredDevices.containsKey(d.getDeviceId())){
			int selection = JOptionPane.showConfirmDialog(this, "Are you sure you like to remove this devie?","Remove Device",JOptionPane.YES_NO_OPTION);
			if(selection == JOptionPane.YES_OPTION){
				m.removeElement(d);
				registeredDevices.remove(d.getDeviceId());
				jlDevieList.updateUI();
				jtfDeviceName.setText("");
				jtfDeviceAddDate.setText("");
				jtfDeviceRegistrationId.setText("");
				SaveDevices();
			}else{
				JOptionPane.showMessageDialog(this, "The operation was canceled.");
			}
		}
	}
	
	/*
	 * Update the changes to te device.
	 * Be sure to save the changes, or the changes will 
	 * not take effect on application launch.
	 */
	private void updateDeviceAction(){
		if(jlDevieList.getSelectedIndex() != -1){
			jlDevieList.getSelectedValue().setDeviceName(jtfDeviceName.getText().toString());
			jlDevieList.updateUI();
			SaveDevices();
		}else
			JOptionPane.showMessageDialog(this, "You must select a device.");
	}
	
	//Listeners
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()){
			/*
			 * Check to make sure the selected index is not -1.
			 * If the index is -1, there is no selected item in
			 *  the list, and the method will return.
			 */
			if(jlDevieList.getSelectedIndex() == -1)
				return;
			Device d = jlDevieList.getSelectedValue();
			jtfDeviceName.setText(d.getDeviceName());
			jtfDeviceAddDate.setText(d.getDate());
			jtfDeviceRegistrationId.setText(d.getDeviceId());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnAddDevice){
			AddDeviceAction();
		}else if(e.getSource() == btnRemoveDevice){
			removeDeviceAction();
		}else if(e.getSource() == btnUpdateDevice){
			updateDeviceAction();
		}else if(e.getSource() == btnClose){
			dispose();
		}else if(e.getSource() == tbtenEnableSystem){
			triggerSystemenabledState();
		}
	}

	/*
	 * class is used for the server listener to accept device registration requests.
	 */
	private class serverListener extends JbcsServerListenerAdapter implements DeviceRegistrationRequestListener{

		@Override
		public void serverStarted(ServerEvent e) {
			JbcsServer s = (JbcsServer) e.getSource();
			s.addRegistrationRequestListener(this);
			s.deviceRegistrationEnabled(true);
			server = s;
			setupServrStatusMessage();
		}
		
		@Override
		public void ServerStopped(ServerEvent e) {
			setupServrStatusMessage();
		}


		@Override
		public boolean deviceRegistrationRequest(Device device) {
			return registerDevice(serverRegistrationRequest, device);
		}
	}
}
