package wetsch.jbcsserver.gui.debugreporting;

import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public abstract class DebugReportPanelLayout extends JFrame{
	private static final long serialVersionUID = 1L;
	private final Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();//Hold the screen size.

	private GridBagConstraints jplc = new GridBagConstraints();//Layout manager to use.

	protected JTextArea jtaDebugReport = new JTextArea();//Holds the text stored in the debug report.
	
	private JPanel container = new JPanel(new GridBagLayout());//Main panel to hold components.
	
	protected JLabel lblFilePath = new JLabel();//Holds the string representation of the file path to the debug report.
	
	protected JButton btnClearLog = new JButton("Clear Log File.");//Clears the debug report.
	protected JButton btnClose = new JButton("Close");//Closes the window.
	
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
		
		addComp(container, lblFilePath, 1, 1, 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, 0, 0);
		jtaDebugReport.setEditable(false);
		JScrollPane scroolPane = new JScrollPane(jtaDebugReport);
		addComp(container, scroolPane, 1, 2, 2, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, 0, 0.9);
		btnClearLog.setToolTipText("Clear the log file.");
		addComp(container, btnClearLog, 1, 3, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 0, 0.1);
		btnClose.setToolTipText("Close the window.");
		addComp(container, btnClose, 2, 3, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, 1, 0.1);
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