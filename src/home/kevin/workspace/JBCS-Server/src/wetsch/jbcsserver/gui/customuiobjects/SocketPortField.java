package wetsch.jbcsserver.gui.customuiobjects;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

/*
 * Last Modified: 7/1/2016
 */

/**
 * This class extends JTextField.
 * It only allows numbers between 0 and 65535.
 * Moreover, the setText() method has been overridden to except a string 
 * representation of a number.  This method will default to 0 if the 
 * string value is < 0 or > 65535.
 * A key listener is added by default to the object to restrict the field to numbers, and
 * lock the range of the number that can be stored in this field.
  * @author kevin
 *
 */
public class SocketPortField extends JTextField {
	private static final long serialVersionUID = 1L;

	/**
	 * this constructor will default the port number to 0;
	 */
	public SocketPortField() {
		super();
		setText("0");
		addKeyListener(new CustomKeyListener());
	}

	/**
	 * The port number for the socket as an int value.
	 * If you enter a port number < 0  or > 65535, the port 
	 * value will be defaulted to 0;
	 * @param The sockets port number.
	 */
	public SocketPortField(int portNumber) {
		super();
		if(portNumber >= 0 && portNumber <= 65535)
			setText(Integer.toString(portNumber));
		else
			setText("0");
		addKeyListener(new CustomKeyListener());
	}
	
	/**
	 * This method takes a string representation of the port number.
	 * If the port number can not be persed into an integer, it will be 
	 * defaulted to 0. Moreover, if the number is < 0 or > 65535 it will 
	 * be defaulted to 0;
	 * @param text
	 */
	public void setText(String text){
		try{
			int port = Integer.parseInt(text);
			if(port < 0 || port > 65535)
				text = "0";
		}catch(Exception e){
			text = "0";
			e.printStackTrace();
		}
		super.setText(text);
	}

	/*
	 * This custom key listener checks if the char typed is a number.
	 * Moreover, it also cheks to make sure the number stored is between 0 and 65535.
	 */
	private class CustomKeyListener extends KeyAdapter{
		@Override
		public void keyTyped(KeyEvent e) {
			char ch = e.getKeyChar();
			if(getText().toString().startsWith("0", 0))
				e.consume();
			if(!isNumber(ch) || !isPortRange(ch))
				e.consume();
			
			super.keyTyped(e);
		}
		
		/*
		 * Check if char is a number.
		 */
		private boolean isNumber(char ch){
			return ch >='0' && ch<='9';
		}
		
		/*
		 * Check to make sure the number stored in the field stays between 0 and 65535.
		 */
		private boolean isPortRange(char ch){
			if(isNumber(ch)){
				if(getText().length() == 0)
					return true;
				String value = getText().toString() + ch;
				int port = Integer.parseInt(value);
				if(port > 65535)
					return false;
			}
			return true;
		}
	}
}