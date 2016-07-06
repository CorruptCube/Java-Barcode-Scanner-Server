package wetsch.jbcsserver.gui.debugreporting;

import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public abstract class DebugReportPanelLayout extends JFrame{
	private static final long serialVersionUID = 1L;
	private final Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();//Hold the screen size.

	private GridBagConstraints jplc = new GridBagConstraints();//Layout manager to use.

	protected JTextArea jtaDebugReport = new JTextArea();
	
	private JPanel container = new JPanel(new GridBagLayout());
	
	protected JButton btnClearLog = new JButton("Clear Log File.");
	protected JButton btnClose = new JButton("Close");
	
	public DebugReportPanelLayout() {
		super("Debug Reporting");
		setSize(screen.width/2, screen.height/2);
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(new GridLayout(1,1));
		setLayout(new GridLayout(1,1));
		layoutSetup();
		setVisible(true);

	}
	
	//Setup GUI layout.
	private void layoutSetup() {
		add(container);
		
		jtaDebugReport.setEditable(false);
		JScrollPane scroolPane = new JScrollPane(jtaDebugReport);
		addComp(container, scroolPane, 1, 1, 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0, 0.9);
		addComp(container, btnClearLog, 1, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 0, 0.1);
		addComp(container, btnClose, 2, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 1, 0.1);
	}


	//Handles the adding of objects for the grid-bag layout.
	 private void addComp(JPanel thePanel, JComponent comp, int xPos, int yPos, int compWidth, int compHeight, int place, int stretch, double weightx, double weighty){
		 jplc.gridx = xPos;
		 jplc.gridy = yPos;
		 jplc.gridwidth = compWidth;
		 jplc.gridheight = compHeight;
		 jplc.weightx = weightx;
		 jplc.weighty = weighty;
		 jplc.anchor = place;
		 jplc.fill = stretch;
	     thePanel.add(comp, jplc);
	 }


}
