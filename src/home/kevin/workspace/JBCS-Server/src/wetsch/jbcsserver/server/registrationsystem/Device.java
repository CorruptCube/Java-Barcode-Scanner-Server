package wetsch.jbcsserver.server.registrationsystem;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Last modified: 6/22/2016
 * Changes:
 * Added set method to set device name.
 */

/**
 * This class represents the registered device object.
 * This object contains a device ID, device name, 
 *  and a device registration date.
 * @author kevin
 *
 */
public class Device implements Serializable{
	private static final long serialVersionUID = 1L;
	private String deviceName = null;//The name of the device.
	private Date date = null;//The date the device is added.
	private String deviceId = null;//The registration ID of the device.
	
	/**
	 * 
	 * @param deviceId The registration ID of the device
	 * @param deviceName The name of the device
	 * @param date The date the devices is added.
	 */
	public Device(String deviceId, String deviceName, Date date){
		this.deviceName = deviceName;
		this.date = date;
		this.deviceId = deviceId;
	}
	
	/**
	 * Returns the registration ID of the device.
	 * @return String
	 */
	public String getDeviceId(){
		return deviceId;
	}
	
	/**
	 * Sets the name of the device
	 * @param deviceName The name to give the device.
	 */
	public void setDeviceName(String deviceName){
		this.deviceName = deviceName;
	}
	
	/**
	 *Returns the device name. 
	 * @return String
	 */
	public String getDeviceName(){
		return deviceName;
	}
	
	/**
	 * Returns the date the deice was added
	 * @return String
	 */
	public String getDate(){
		DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
		return df.format(date.getTime());
	}
	
	/**
	 * Returns the name of the device.
	 */
	@Override
	public String toString(){
		return deviceName;
	}
}

