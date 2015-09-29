package wetsch.wirelessbarcodescannerserver;

import java.awt.event.KeyEvent;

/**
 * This class is meant to be to get the key code for a given char.  
 * 
 * @author kevin
 *
 */
public abstract class KeyBoard {

	/**
	 * Returns the key event for the special char that is passed in. For example, 
	 * !, @, #, $, %, ^, &, *. (. ). ), _, +., <, >, ? <br>
	 * If useing the robot class, you must have the robot press shift first before 
	 * calling this method.  Do not forget to have the robot release the shift key 
	 * at the end.
	 * @param c The char that is associated with a key event.
	 * @return
	 */
	public static Object getSpecialCharictor(char c){

		switch (c) {
		case '!':
			return KeyEvent.VK_1;
		case '@':
			return KeyEvent.VK_AT;
		case '#':
			return KeyEvent.VK_NUMBER_SIGN;
		case '$':
			return KeyEvent.VK_DOLLAR;
		case '^':
			return KeyEvent.VK_CIRCUMFLEX;
		case '%':
			return KeyEvent.VK_5;
		case '&':
			return KeyEvent.VK_AMPERSAND;
		case '*':
			return KeyEvent.VK_ASTERISK;
		case '(':
			return KeyEvent.VK_LEFT_PARENTHESIS;
		case ')':
			return KeyEvent.VK_RIGHT_PARENTHESIS;
		case '_':
			return KeyEvent.VK_UNDERSCORE;
		case '+':
			return KeyEvent.VK_PLUS;
		case '|':
			return KeyEvent.VK_BACK_SLASH;
		case '{':
			return KeyEvent.VK_OPEN_BRACKET;
		case '}':
			return KeyEvent.VK_CLOSE_BRACKET;
		case ':':
			return KeyEvent.VK_COLON;
		case '"':
			return KeyEvent.VK_QUOTE;
		case '<':
			return KeyEvent.VK_COMMA;
		case '>':
			return KeyEvent.VK_PERIOD;
		case '?':
			return KeyEvent.VK_SLASH;
		}
		return 0;
	}
}
