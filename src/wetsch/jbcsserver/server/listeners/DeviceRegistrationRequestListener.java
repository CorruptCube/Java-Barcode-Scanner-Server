package wetsch.jbcsserver.server.listeners;

import wetsch.jbcsserver.server.registrationsystem.Device;

/*
 * Last Modified: 6/27/2016
 */

/**
 * Listener interface to detect when the server receives a registration request.
 * It is recommended that this interface be implemented by only one class in a project.
 * Multiple instances of this listener interface found by the server can result in 
 * unexpected behavior.
 * @author kevin
 */
public interface DeviceRegistrationRequestListener {
	
	/**
	 * Called by the server when the server receives a device registration request.
	 * Return true if the registration request is going to be accepted, otherwise 
	 * return false.<br>
	 * <b>Note:</b> This method should only be implemented by one class in your project.
	 * If more then one class that implements this method is found in the listener, and the server 
	 * loops through multiple classes, it can result in a false accept or reject.
	 * @param A Device object. The name of the device is set to null by default.
	 * Call setDeviceName on the object to set it's name.
	 * @return Returns false by default.
	 */
	public boolean deviceRegistrationRequest(Device device);

}
