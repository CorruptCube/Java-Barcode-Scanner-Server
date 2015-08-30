package wetsch.wirelessbarcodescannerserver;

import java.awt.AWTException;

/*
 * Last modified: 8/30.2015
 */

/**
 * This class extends Robot from the java.awt package.  
 * This class has a method that simulates typing on the keyboard
 * @author kevin
 *@version 1.0
 */
public class Robot extends java.awt.Robot {


	public Robot() throws AWTException {
		super();
	}
	
	/**
	 * The method uses a thread to type out the characters that are in the passed in string.
	 */
	public void typeString(String s){
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					for(char c : s.toCharArray()){
						keyPress(c);
						keyRelease(c);
					}

				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
}
