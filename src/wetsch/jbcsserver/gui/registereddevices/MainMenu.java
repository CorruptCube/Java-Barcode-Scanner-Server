package wetsch.jbcsserver.gui.registereddevices;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/*
 * Last modified 9/26/2016.
 */

/**
 * This class stores the main menu bar for the registration system UI panel.
 * @author kevinwetsch
 *
 */
public class MainMenu extends JMenuBar{
	private static final long serialVersionUID = 1L;

	private JMenu jmActions = new JMenu("Actions");// Action menu
	
	public final JMenuItem jmiRemoveDatabase = new JMenuItem("Remove Database");// Remove registration system database.
	
	public MainMenu(){
		setupMenu();
	}

	//Setup Menu
	private void setupMenu(){
		add(jmActions);
		jmActions.add(jmiRemoveDatabase);
		
		}

}
