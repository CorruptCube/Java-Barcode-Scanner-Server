package wetsch.jbcsserver.server.registrationsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;

import wetsch.jbcsserver.tools.Tools;

/*
 * Last modified 7/18/2016
 */

/**
 * This class extends LinkedHashMap.
 * The key is the devices registration ID, and the object 
 * is the representation of the registered device.
 * This class holds the registered devices for the server.
 */

public class RegisteredDevices extends LinkedHashMap<String, Device> implements Serializable{
	private static final long serialVersionUID = 1L;

	private static RegisteredDevices devices = new RegisteredDevices();//The instance of he registration system.
	private boolean systemEnabled = false;//The enabled status of the registration system.
	public static void readInRegisteredDevices() throws FileNotFoundException, IOException, ClassNotFoundException{
		File f = new File(Tools.getApplicationDir() + "/Registered_devices.jdb");
		if(!f.exists()){
			return;
		}
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		devices =  (RegisteredDevices) ois.readObject();
		ois.close();
	}
	
	public static void writeRegisteredDevices() throws FileNotFoundException, IOException{
		File f = new File(Tools.getApplicationDir() + "/Registered_devices.jdb");
		if(!f.exists())
			f.createNewFile();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
		oos.writeObject(devices);
		oos.close();
	}
	
	/**
	 * Returns an Instance of the registration system.
	 * @return RegisteredDevices Instance
	 */
	public static RegisteredDevices getInstance(){
		return devices;
	}
	
	/**
	 * Returns the enabled status of the registration system.
	 * If true is returned, the system is enabled.
	 * if false is returned, the system is disabled.
	 * @return boolean
	 */
	public boolean isSystemEnabled(){
		return systemEnabled;
	}
	
	/**
	 * Sets the registration system enabled status.
	 * @param status set true to enable, otherwise set false to disable.
	 */
	public void setSystemEnabled(boolean status){
		systemEnabled = status;
	}

}

