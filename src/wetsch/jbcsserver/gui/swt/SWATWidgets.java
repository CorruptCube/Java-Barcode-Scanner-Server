package wetsch.jbcsserver.gui.swt;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import wetsch.jbcsserver.tools.DebugPrinter;
import wetsch.jbcsserver.tools.Tools;

/*
 * Last modified 7/9/2016
 * Changes:
 * Added error message dialog box method.

 */

/**
 * Tray icon for Linux systems. this icon is loaded using SWT.
 * @author kevin
 *@version 1.0
 */
public class SWATWidgets {
	private String disposableString;//Disposable string

	private Thread swtThread = null;//Thread for SWT widgets.
	private Listener listener = null;//Listener for the menu items.
	private Menu menu;//System tray incon menu.
	private Shell shell;//SWT shell.
	private Display display;//SWT Display
	private Image image = null;// System tray icon image.
	private MenuItem itemShowHideInterface;//Menu item to show/hide interface.
	private MenuItem itemStartServer;//Menu item to start/stop the server.
	private MenuItem itemStartStopRobot;//Menu item to turn on/off robot.
	private MenuItem itemExit;//Menu item to exit program.
	
	/**
	 * Takes a event listener.
	 * @param listener Listener for menu items.
	 */
	public SWATWidgets(Listener listener) {
		this.listener = listener;
		setupThread();
	}
	
	//Setup thread for icon to run on.
	private void setupThread(){
		swtThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					display = new Display();
					shell = new Shell(display);
					setupsystemTrayIcon();
					while(!shell.isDisposed()){
						if(!display.readAndDispatch())
							display.sleep();
					}
					image.dispose();
					display.dispose();
	
				}catch(Exception e){
					new DebugPrinter().sendDebugToFile(e);
					e.printStackTrace();
				}
			}
		});
	}
	
	//Setup the system system tray icon.
	public void setupsystemTrayIcon(){
		image = new Image(display, getClass().getResourceAsStream("/tray_icon.png"));

		menu = new Menu(shell, SWT.POP_UP);
		itemShowHideInterface = new MenuItem(menu, SWT.PUSH);
		itemShowHideInterface.setText("Hide Interface");
		itemStartServer = new MenuItem(menu, SWT.PUSH);
		itemStartServer.setText("Start Server");
		itemStartStopRobot = new MenuItem(menu, SWT.PUSH);
		itemStartStopRobot.setText("Turn Robot On");
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

	}
	
	//Setup menu item listener for tray icon.
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
	public void changeMenuItemLabel(final String labelText, final MenuItem item){
		display.asyncExec(new Runnable() {
			
			@Override
			public void run() {
				try{
					item.setText(labelText);
				}catch(Exception e){
					new DebugPrinter().sendDebugToFile(e);
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Show the icon on the system tray notification area.
	 */
	public void showIcon(){
		swtThread.start();
	}
	
	/*
	 * Remove the icon from system tray and kill the thread.
	 */
	public void removeIcon(){
		display.asyncExec(new Runnable() {
			
			@Override
			public void run() {
				try {
					shell.dispose();
				} catch (Exception e) {
					new DebugPrinter().sendDebugToFile(e);
					e.printStackTrace();
				}
			}
		});
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
	 * Shows an error message dialog.
	 * @param message Message text.
	 */
	public void SWTShowMessageBoxError(final String message){
		display.asyncExec(new Runnable() {
			
			@Override
			public void run() {
				MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR);
				mb.setText("Error");
				mb.setMessage(message);
				mb.open();
			}
		});
	}
	
	/**
	 * Returns the exit program menu item.
	 * @return MenuItem
	 */
	public MenuItem getItemExit(){
		return itemExit;
	}
	
	/**
	 * Returns the Display object for SWT.
	 * @return Display
	 */
	public Display getDisplay(){
		return display;
	}
	
	/**
	 * This method opens a SWT widget file dialog.  
	 * Once a file has been chosen, a string containing 
	 * the absolute path is returned.
	 * <br>
	 * <b>Note:</b> This method runs on the SWT display thread.  
	 * Once the runnable is exacted, Wait() is called to lock 
	 * the thread that called it.  Once the dialog has been 
	 * closed, notify() is called and control of the lock is 
	 * handed back to the calling thread.
	 * @return String
	 * @throws InterruptedException
	 */
	public String getSWTFileDialog() throws InterruptedException{
		final Object lock = new Object();
		disposableString = null;
		synchronized (lock) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					synchronized (lock) {
						FileDialog fd = new FileDialog(shell, SWT.SAVE);
						fd.setText("Save file to?");
						fd.setFilterPath(Tools.getApplicationDir());
						fd.open();
						if(fd.getFileName().equals("Untitled")){
							lock.notify();
							return;
						}
						disposableString = fd.getFilterPath()  + "/" + fd.getFileName();
						lock.notify();
					}
				}
			});
				lock.wait();
		}
		return disposableString;
	}
	
	/**
	 * Returns the Shell object for SWT.
	 * @return Shell
	 */
	public Shell getShell(){
		return shell;
	}
}