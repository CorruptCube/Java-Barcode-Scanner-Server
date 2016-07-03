package wetsch.jbcsserver.gui.customuiobjects;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

/**
 * This class JToggleButton.  It replaes the original button look and feel with a custom icon.
 * @author kevin
 */

public class CustomToggleButton extends JToggleButton{

	private static final long serialVersionUID = 1L;

	/**
	 * This constructor sets the label for the toggle button.
	 * @param label A string to use for the label.
	 */
	public CustomToggleButton(String label) {
		setText(label);
		setIcon(new ImageIcon(getClass().getResource("/togglebutton-off.png")));
		setSelectedIcon(new ImageIcon(getClass().getResource("/togglebutton-on.png")));
		setBorderPainted(false);
		setContentAreaFilled(false);
	}
}