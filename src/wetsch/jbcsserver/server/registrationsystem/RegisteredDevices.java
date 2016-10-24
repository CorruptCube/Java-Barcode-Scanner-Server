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
 * Last modified 6/22/2016
 */

/**
 * This class extends LinkedHashMap.
 * The key is the devices registration ID, and the object 
 * is the representation of the registered device.
 * This class holds the registered devices for the server.
 */

public class RegisteredDevices extends LinkedHashMap<String, Device> implements Serializable{
	private static final long serialVersionUID = 1L;

	private static RegisteredDevices devices = null;//The instance of he registration system.
	private boolean systemEnabled = false;//The enabled status of the registration system.

	/**
	 * Reads in the registration system object from the file on disk.
	 * @throws FileNotFoundException Thrown if the file is not found on the disk.
	 * @throws IOException Thrown if an IO problem occurs.
	 * @throws ClassNotFoundException Thrown if the object can not be casted.
	 */
	public static void readInRegisteredDevices() throws FileNotFoundException, IOException, ClassNotFoundException{
		File f = new File(Tools.getApplicationDir() + "/Registered_devices.jdb");
		if(!f.exists()){
			devices = new RegisteredDevices();
			return;
		}
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		devices =  (RegisteredDevices) ois.readObject();
		ois.close();
	}
	
	/**
	 * Writes the registration system object to a file.
	 * @throws FileNotFoundException Thrown if file is not found on disk.
	 * @throws IOException Thrown if an IO Problem occurs.  
	 */
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