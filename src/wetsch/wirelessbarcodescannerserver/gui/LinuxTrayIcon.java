package wetsch.wirelessbarcodescannerserver.gui;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import wetsch.wirelessbarcodescannerserver.DebugPrinter;

/*
 * Last moified 9/27/2015
 * Changes:
 * Added support to print stack-trace to debug output file.

 */

/**
 * Tray icon for Linux systems. this icon is loaded using SWT.
 * @author kevin
 *@version 1.0
 */
public class LinuxTrayIcon {
	private DebugPrinter debugPrinter = null;
	private Thread trayIconThread = null;
	private Listener listener = null;
	private Menu menu;
	private Shell shell;
	private Display display;
	private Image image = null;
	private MenuItem itemShowHideInterface;
	private MenuItem itemStartServer;
	private MenuItem itemStartStopRobot;
	private MenuItem itemExit;
	
	/**
	 * Takes a event listener.
	 * @param listener Listener for menu items.
	 */
	public LinuxTrayIcon(Listener listener) {
		this.listener = listener;
		debugPrinter = new DebugPrinter("JBCS-server-debug-report.txt");
		setupThread();
	}
	
	//Setup thread for icon to run on.
	private void setupThread(){
		trayIconThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					display = new Display();
					shell = new Shell(display);
					image = new Image(display, getClass().getResourceAsStream("/tray_icon.png"));

					menu = new Menu(shell, SWT.POP_UP);
					itemShowHideInterface = new MenuItem(menu, SWT.PUSH);
					itemShowHideInterface.setText("Hide interface");
					itemStartServer = new MenuItem(menu, SWT.PUSH);
					itemStartServer.setText("Start Server");
					itemStartStopRobot = new MenuItem(menu, SWT.POP_UP);
					itemStartStopRobot.setText("Turn robot on");
					itemExit = new MenuItem(menu, SWT.PUSH);
					itemExit.setText("Exit");
					Tray tray = display.getSystemTray();
					if (tray == null) {
						System.out.println("The system tray is not available");
					} else {
						TrayItem item = new TrayItem(tray, SWT.NONE);
						item.addListener(SWT.MenuDetect, new Listener() {
							public void handleEvent(Event event) {
								menu.setVisible(true);
							}
						});
						item.setImage(image);
					}
					setupItemListeners();
					
					while(!shell.isDisposed()){
						if(!display.readAndDispatch())
							display.sleep();
					}
					image.dispose();
					display.dispose();
	
				}catch(Exception e){
					try {
						debugPrinter.sendDebugToFile(e);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		});
	}
	
	
	private void setupItemListeners(){
		for(int i = 0; i < menu.getItemCount(); i++)
			menu.getItem(i).addListener(SWT.Selection, listener);
	}
	
	/**
	 * Change menu items on tray icon.  
	 * This method executes the code on the SWT UI thread. 
	 * @param labelText New menu item text
	 * @param item Menu item widget
	 */
	public void changeMenuItemLabel(String labelText, MenuItem item){
		display.asyncExec(new Runnable() {
			
			@Override
			public void run() {
				try{
					item.setText(labelText);
				}catch(Exception e){
					try {
						debugPrinter.sendDebugToFile(e);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Show the icon on the system tray notification area.
	 */
	public void showIcon(){
		trayIconThread.start();
	}
	/**
	 * Returns the open close interface menu item.
	 * @return MenuItem
	 */
	public MenuItem getItemShowHideInterface(){
		return itemShowHideInterface;
	}
	
	/**
	 * Returns the start stop server menu item.
	 * @return MenuItem
	 */
	public MenuItem getItemStartServer(){
		return itemStartServer;
	}
	
	/**
	 * Returns the start stop robot menu item.
	 * @return MenuItem
	 */
	public MenuItem getItemStartStopRobot(){
		return itemStartStopRobot;
	}
	
	/**
	 * Returns the exit program menu item.
	 * @return MenuItem
	 */
	public MenuItem getItemExit(){
		return itemExit;
	}
}