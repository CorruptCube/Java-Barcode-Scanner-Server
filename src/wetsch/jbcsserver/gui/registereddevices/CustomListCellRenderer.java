package wetsch.jbcsserver.gui.registereddevices;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/*
 * Last Modified 6/23/2016
 */

/**
 * Custom Cell Renderer to include image on JLabel.
 * @author kevin
 * @param <Device>
 */
@SuppressWarnings("rawtypes")
public class CustomListCellRenderer extends JLabel implements ListCellRenderer{
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		setOpaque(true);
		setIcon(new ImageIcon(getClass().getResource("/phone_icon.png")));
		setText(value.toString());
		if(isSelected){
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}else{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			
		}
		return this;
	}

}