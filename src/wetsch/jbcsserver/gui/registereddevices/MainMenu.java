package wetsch.jbcsserver.gui.registereddevices;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainMenu extends JMenuBar{
	private static final long serialVersionUID = 1L;

	private JMenu jmActions = new JMenu("Actions");
	
	public final JMenuItem jmiRemoveDatabase = new JMenuItem("Remove Database");
	

	public MainMenu(){
		setupMenu();
	}

	//Setup Menu
	private void setupMenu(){
		add(jmActions);
		jmActions.add(jmiRemoveDatabase);
		
		}

}
