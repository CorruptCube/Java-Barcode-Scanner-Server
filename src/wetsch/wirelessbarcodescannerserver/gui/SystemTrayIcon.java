package wetsch.wirelessbarcodescannerserver.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;

/*
 * Last modified on 9/4/2015
 * Has not been tested on Max OSX yet.
 */

/**
 *Place a system tray icon to notification area. 
 * @author kevin
 *@version 1.0
 */
public class SystemTrayIcon extends TrayIcon {
	private SystemTray tray = null; //Notification system tray.
	private ActionListener listener = null;//Menu item listener
	private PopupMenu menu = new PopupMenu();//Pop-up menu on tray icon.
	private MenuItem itemOpenInterface = new MenuItem("Hide interface");//Show hide interface menu item.
	private MenuItem itemStartStopServer = new MenuItem("Start Server");//Start stop server menu item.
	private MenuItem itemStartStopRobot = new MenuItem("Turn robot on");//Start stop robot menu item
	private MenuItem itemExit = new MenuItem("Exit");//Exit program menu item.

	/**
	 * Takes an image and listener.
	 * @param img Icon image.
	 * @param listener Menu item listener.
	 * @throws AWTException
	 */
	public SystemTrayIcon(Image img, ActionListener listener) throws AWTException {
		super(img);
		this.listener = listener;
		setupTrayIcon();
		setupPopupMenu();
	}
	
	//Setup tray icon.
	private void setupTrayIcon() throws AWTException{
		tray = SystemTray.getSystemTray();
		setImageAutoSize(true);
		tray.add(this);
	}
	
	//Setup pop-up menu for tray icon.
	private void setupPopupMenu(){
		
		menu.add(itemOpenInterface);
		menu.add(itemStartStopServer);
		menu.add(itemStartStopRobot);
		menu.add(itemExit);

		for(int i = 0; i < menu.getItemCount(); i++){
			menu.getItem(i).addActionListener(listener);
		}
		setPopupMenu(menu);
	}
	/**
	 * Returns the open interface menu item.
	 * @return MenuItem
	 */
	public MenuItem getMenuItemOpenInterface(){
		return itemOpenInterface;
	}
	
	/**
	 * Returns the start stop server menu item.
	 * @return MenuItem
	 */
	public MenuItem getMenuItemStartStopServer(){
		return itemStartStopServer;
	}
	
	/**
	 * Returns the start stop robot menu item.
	 * @return MenuItem
	 */
	public MenuItem getMenuItemStartStopRobot(){
		return itemStartStopRobot;
	}
	
	/**
	 * Returns the exit server menu item.
	 * @return MenuItem
	 */
	public MenuItem getMenuItemExit(){
		return itemExit;
	}
}
