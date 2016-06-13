package wetsch.jbcsserver.tools;

import java.awt.AWTException;
import java.awt.event.KeyEvent;

/*
 * Last modified: 9/27.2015
 * Changes:
 * Fixed bug that prevented typing of alpha characters and most commonly used special characters.
 * Added support to print stack-trace to debug output file.
 */

/**
 * This class extends Robot from the java.awt package.  
 * The class will create a thread and simulate typing on the keyboard.  
 * <br>
 * <b>Note:</b> Make sure to have a cursor placed where you want the robot to 
 * type, or you may experience unexpected behavior. 
 *   
 * <b>Note:</b> 
 * @author kevin
 *@version 1.0
 */
public class Robot extends java.awt.Robot {
	private char[] specialCharacters = null;// special characters that need shift key.
	private DebugPrinter debugPrinter = null;// print the debug output to a file.
	public Robot() throws AWTException {
		super();
		debugPrinter = new DebugPrinter();
		specialCharacters = new char[]{'!', '@', '#', '$', '^', '%', '&', '*', '(', ')', '_', '+', '{', '}', '|', ':', '"', '<', '>', '?'};
	}
	
	/**
	 * The method uses a thread to type out the characters that are in the passed in string.
	 * <br>
	 * <b>Note:</b> Make sure to have a cursor placed where you want the robot to 
	 * type, or you may experience unexpected behavior.
	 * @param s The string that the robot will type.
	 */
	public void typeString(final String s){
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					for(char c : s.toCharArray()){
						if(new String(specialCharacters).indexOf(c)  >= 0){
							keyPress(KeyEvent.VK_SHIFT);
							keyPress((int) KeyBoard.getSpecialCharictor(c));
							keyRelease((int) KeyBoard.getSpecialCharictor(c));
							keyRelease(KeyEvent.VK_SHIFT);
						}else if(Character.isUpperCase(c)){
							keyPress(KeyEvent.VK_SHIFT);
							keyPress(Character.toUpperCase(c));
							keyRelease(Character.toUpperCase(c));
							keyRelease(KeyEvent.VK_SHIFT);
						}else{
							keyPress(Character.toUpperCase(c));
							keyRelease(Character.toUpperCase(c));
						}
					}
				}catch(Exception e){
					debugPrinter.sendDebugToFile(e);
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
}