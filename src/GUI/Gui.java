package GUI;

import static javax.swing.GroupLayout.Alignment.BASELINE;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Group;
import javax.swing.table.DefaultTableModel;

public class Gui extends JPanel{
	private final JButton button1;
	private final JTextField text1;
	private final JTextField text2;

	private final DefaultTableModel tableModel1;
	private final DefaultTableModel tableModel2;
	private final JTable dataTable1;
	private final JTable dataTable2;

	//The following are used for organizing the layout.
	private final Group horizontal1;
	private final Group vertical1;
	private final Group horizontal2;
	private final Group vertical2;

	private final Group tab1row1;
	private final Group tab1vert1;
	
	private final Group tab2row1;
	private final Group tab2row2;
	private final Group tab2vert1;
	private final Group tab2vert2;

	public Gui(){
		super(new GridLayout(1, 1));//not too sure what this does...
		JTabbedPane tabbedPane = new JTabbedPane();
	
		//-----------------------------1st Tab-----------------------------------
		//Instantiate components
		dataTable1 = new JTable(0,3);
		dataTable1.setName("dataTable1");
		tableModel1 = (DefaultTableModel) dataTable1.getModel();

		//Set up layout
		JComponent panel1 = makeTextPanel("Panel #1");
		GroupLayout layout1 = new GroupLayout(panel1);
		panel1.setLayout(layout1);
		layout1.setAutoCreateGaps(true);
		layout1.setAutoCreateContainerGaps(true);
		
		//Arrange horizontal
		tab1row1 = layout1.createSequentialGroup();
		tab1row1.addComponent(dataTable1);

		horizontal1 = layout1.createParallelGroup();
		horizontal1.addGroup(tab1row1);
		layout1.setHorizontalGroup(horizontal1);

		//Arrange vertical
		tab1vert1 = layout1.createParallelGroup(BASELINE);
		tab1vert1.addComponent(dataTable1);

		vertical1 = layout1.createSequentialGroup();
		vertical1.addGroup(tab1vert1);
		layout1.setVerticalGroup(vertical1);

		//Add Title Row to Table
		String [] rowData = new String[3];
		rowData[0] = "Time";
		rowData[1] = "Person";
		rowData[2] = "More info";
		tableModel1.addRow(rowData);
		
		//-----------------------------2nd Tab-----------------------------------
		//instantiating components
		button1 = new JButton();
		button1.setName("button1");
		button1.setText("Generic Button");
		text1 = new JTextField(60);
		text1.setName("text1");
		text2 = new JTextField(60);
		text2.setName("text2");
		dataTable2 = new JTable(0,3);
		dataTable2.setName("dataTable2");
		tableModel2 = (DefaultTableModel) dataTable2.getModel();
		
		//Set up layout
		JComponent panel2 = makeTextPanel("Panel #2");
		GroupLayout layout2 = new GroupLayout(panel2);
		panel2.setLayout(layout2);
		layout2.setAutoCreateGaps(true);
		layout2.setAutoCreateContainerGaps(true);
		
		//Arrange horizontal
		tab2row1 = layout2.createSequentialGroup();
		tab2row1.addComponent(text1);
		tab2row1.addComponent(text2);
		tab2row1.addComponent(button1);
		
		tab2row2 = layout2.createSequentialGroup();
		tab2row2.addComponent(dataTable2);

		horizontal2 = layout2.createParallelGroup();
		horizontal2.addGroup(tab2row1);
		horizontal2.addGroup(tab2row2);
		layout2.setHorizontalGroup(horizontal2);

		//Arrange vertical
		tab2vert1 = layout2.createParallelGroup(BASELINE);
		tab2vert1.addComponent(text1);
		tab2vert1.addComponent(text2);
		tab2vert1.addComponent(button1);
		
		tab2vert2 = layout2.createParallelGroup(BASELINE);
		tab2vert2.addComponent(dataTable2);

		vertical2 = layout2.createSequentialGroup();
		vertical2.addGroup(tab2vert1);
		vertical2.addGroup(tab2vert2);
		layout2.setVerticalGroup(vertical2);

		//Add Title Row to Table
		String [] rowData2 = new String[3];
		rowData[0] = "Time";
		rowData[1] = "Person";
		rowData[2] = "More info";
		tableModel2.addRow(rowData);
		
		/////////////////////////////
        
        tabbedPane.addTab("Tab 1", panel1);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
       
        tabbedPane.addTab("Tab 2", panel2);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);        
        
        //Add the tabbed pane to this panel.
        add(tabbedPane);
        
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}
	
	protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from
     * the event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("GUI Window Name Here");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Add content to the window.
        frame.add(new Gui(), BorderLayout.CENTER);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
