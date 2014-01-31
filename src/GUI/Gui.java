package GUI;

import static javax.swing.GroupLayout.Alignment.BASELINE;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Group;
import javax.swing.table.DefaultTableModel;

import Server.Data;

public class Gui extends JPanel implements Runnable {
    public final JButton tab1button1;
    public final JButton tab1button2;
    public final JTextField tab1text1; // Test subject
    public final JTextField tab1text2; // Test number
    public final JTextField tab1text3; // IP Address

    public final JButton tab2button1;
    public final JButton tab2button2;
    public final JTextField tab2text1; // Test subject
    public final JTextField tab2text2; // Test number
    public final JTextField tab2text3; // IP Address

    private final JLabel tab1label1; // Test subject
    private final JLabel tab1label2; // Test number
    private final JLabel tab1label3; // IP Address

    private final JLabel tab2label1; // Test subject
    private final JLabel tab2label2; // Test number
    private final JLabel tab2label3; // IP Address

    public final DefaultTableModel tableModel1; // X,Y table
    public final DefaultTableModel tableModel2; // Bar table
    private final JTable dataTable1;
    private final JTable dataTable2;

    // The following are used for organizing the layout.
    private final Group horizontal1;
    private final Group vertical1;
    private final Group horizontal2;
    private final Group vertical2;

    private final Group tab1row1;
    private final Group tab1row2;
    private final Group tab1vert1;
    private final Group tab1vert2;

    private final Group tab2row1;
    private final Group tab2row2;
    private final Group tab2vert1;
    private final Group tab2vert2;

    public Gui() {
        super(new GridLayout(1, 1));// not too sure what this does...
        JTabbedPane tabbedPane = new JTabbedPane();

        // -----------------------------1st
        // Tab-----------------------------------
        // Instantiate components
        tab1button1 = new JButton();
        tab1button1.setName("tab1button1");
        tab1button1.setText("Submit");
        tab1label1 = new JLabel(); // Note: Labels are reused in second tab
        tab1button2 = new JButton();
        tab1button2.setName("tab1button2");
        tab1button2.setText("Export All Tests");
        tab1label1.setText("Test Subject: ");
        tab1label2 = new JLabel();
        tab1label2.setText("Test Number: ");
        tab1label3 = new JLabel();
        tab1label3.setText("IP Address: ");
        tab1text1 = new JTextField(20); // Test subject
        tab1text1.setName("text1");
        tab1text2 = new JTextField(20); // Test number
        tab1text2.setName("text2");
        tab1text3 = new JTextField(20); // IP Address
        tab1text3.setName("text2");

        Vector<String> table1Columns = new Vector<String>();
        table1Columns.add("Time");
        table1Columns.add("X");
        table1Columns.add("Y");
        tableModel1 = new DefaultTableModel(table1Columns, 0) {
            private static final long serialVersionUID = 2045698881619435427L;

            @Override
            public boolean isCellEditable(int row, int column) {

                // Make the cells not editable
                return false;
            }
        };
        dataTable1 = new JTable(tableModel1);
        final JScrollPane scrollTable1 = new JScrollPane(dataTable1);
        dataTable1.setName("dataTable1");

        // Set up layout
        JComponent panel1 = makeTextPanel("Panel #1");
        GroupLayout layout1 = new GroupLayout(panel1);
        panel1.setLayout(layout1);
        layout1.setAutoCreateGaps(true);
        layout1.setAutoCreateContainerGaps(true);

        // Arrange horizontal
        tab1row1 = layout1.createSequentialGroup();
        tab1row1.addComponent(tab1label1);
        tab1row1.addComponent(tab1text1);
        tab1row1.addComponent(tab1label2);
        tab1row1.addComponent(tab1text2);
        tab1row1.addComponent(tab1label3);
        tab1row1.addComponent(tab1text3);
        tab1row1.addComponent(tab1button1);
        tab1row1.addComponent(tab1button2);

        tab1row2 = layout1.createSequentialGroup();
        tab1row2.addComponent(scrollTable1);

        horizontal1 = layout1.createParallelGroup();
        horizontal1.addGroup(tab1row1);
        horizontal1.addGroup(tab1row2);
        layout1.setHorizontalGroup(horizontal1);

        // Arrange vertical
        tab1vert1 = layout1.createParallelGroup(BASELINE);
        tab1vert1.addComponent(tab1label1);
        tab1vert1.addComponent(tab1text1);
        tab1vert1.addComponent(tab1label2);
        tab1vert1.addComponent(tab1text2);
        tab1vert1.addComponent(tab1label3);
        tab1vert1.addComponent(tab1text3);
        tab1vert1.addComponent(tab1button1);
        tab1vert1.addComponent(tab1button2);

        tab1vert2 = layout1.createParallelGroup(BASELINE);
        tab1vert2.addComponent(scrollTable1);

        vertical1 = layout1.createSequentialGroup();
        vertical1.addGroup(tab1vert1);
        vertical1.addGroup(tab1vert2);
        layout1.setVerticalGroup(vertical1);

        scrollTable1.setColumnHeaderView(dataTable1.getTableHeader());

        tab1button1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GuiModel.submitInfo(tab1text1.getText(), tab1text2.getText());
                GuiModel.setIPAddress(tab1text3.getText());
                GuiModel.turnOnLED(true);
                tab1text1.setEnabled(false);
                tab1text2.setEnabled(false);
                tab1text3.setEnabled(false);
                tab1button1.setEnabled(false);
                tab1button2.setEnabled(false);

                for (int i = tableModel1.getRowCount() - 1; i >= 0; i--) {
                    tableModel1.removeRow(i);
                }
            }
        });

        tab1button2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GuiModel.export();
            }
        });

        // -----------------------------2nd
        // Tab-----------------------------------
        // instantiating components
        tab2button1 = new JButton();
        tab2button1.setName("tab2button1");
        tab2button1.setText("Submit");
        tab2label1 = new JLabel(); // Note: Labels are reused in second tab
        tab2button2 = new JButton();
        tab2button2.setName("tab2button2");
        tab2button2.setText("Export All Tests");
        tab2label1.setText("Test Subject: ");
        tab2label2 = new JLabel();
        tab2label2.setText("Test Number: ");
        tab2label3 = new JLabel();
        tab2label3.setText("IP Address: ");
        tab2text1 = new JTextField(20); // Test subject
        tab2text1.setName("text1");
        tab2text2 = new JTextField(20); // Test number
        tab2text2.setName("text2");
        tab2text3 = new JTextField(20); // IP Address
        tab2text3.setName("text2");

        Vector<String> table2Columns = new Vector<String>();
        table2Columns.add("Time");
        table2Columns.add("Bar Number");
        table2Columns.add("Value");
        tableModel2 = new DefaultTableModel(table2Columns, 0) {
            private static final long serialVersionUID = 2045698881619435427L;

            @Override
            public boolean isCellEditable(int row, int column) {

                // Make the cells not editable
                return false;
            }
        };

        dataTable2 = new JTable(tableModel2);
        final JScrollPane scrollTable2 = new JScrollPane(dataTable2);
        dataTable2.setName("dataTable2");

        // Set up layout
        JComponent panel2 = makeTextPanel("Panel #2");
        GroupLayout layout2 = new GroupLayout(panel2);
        panel2.setLayout(layout2);
        layout2.setAutoCreateGaps(true);
        layout2.setAutoCreateContainerGaps(true);

        // Arrange horizontal
        tab2row1 = layout2.createSequentialGroup();
        tab2row1.addComponent(tab2label1);
        tab2row1.addComponent(tab2text1);
        tab2row1.addComponent(tab2label2);
        tab2row1.addComponent(tab2text2);
        tab2row1.addComponent(tab2label3);
        tab2row1.addComponent(tab2text3);
        tab2row1.addComponent(tab2button1);
        tab2row1.addComponent(tab2button2);

        tab2row2 = layout2.createSequentialGroup();
        tab2row2.addComponent(scrollTable2);

        horizontal2 = layout2.createParallelGroup();
        horizontal2.addGroup(tab2row1);
        horizontal2.addGroup(tab2row2);
        layout2.setHorizontalGroup(horizontal2);

        // Arrange vertical
        tab2vert1 = layout2.createParallelGroup(BASELINE);
        tab2vert1.addComponent(tab2label1);
        tab2vert1.addComponent(tab2text1);
        tab2vert1.addComponent(tab2label2);
        tab2vert1.addComponent(tab2text2);
        tab2vert1.addComponent(tab2label3);
        tab2vert1.addComponent(tab2text3);
        tab2vert1.addComponent(tab2button1);
        tab2vert1.addComponent(tab2button2);

        tab2vert2 = layout2.createParallelGroup(BASELINE);
        tab2vert2.addComponent(scrollTable2);

        vertical2 = layout2.createSequentialGroup();
        vertical2.addGroup(tab2vert1);
        vertical2.addGroup(tab2vert2);
        layout2.setVerticalGroup(vertical2);

        scrollTable2.setColumnHeaderView(dataTable2.getTableHeader());

        tab2button1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GuiModel.submitInfo(tab2text1.getText(), tab2text2.getText());
                GuiModel.setIPAddress(tab2text3.getText());
                GuiModel.turnOnLED(false);
                tab2text1.setEnabled(false);
                tab2text2.setEnabled(false);
                tab2text3.setEnabled(false);
                tab2button1.setEnabled(false);
                tab2button2.setEnabled(false);
                
                for (int i = tableModel2.getRowCount() - 1; i >= 0; i--) {
                    tableModel2.removeRow(i);
                }
            }
        });

        tab2button2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GuiModel.export();
            }
        });
        // ///////////////////////////

        tabbedPane.addTab("Tab 1", panel1);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        tabbedPane.addTab("Tab 2", panel2);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        // Add the tabbed pane to this panel.
        add(tabbedPane);

        // The following line enables to use scrolling tabs.
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
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event dispatch thread.
     */
    private void createAndShowGUI() {
        // Create and set up the window.
        JFrame frame = new JFrame(
                "MIT Media Lab: Responsive Environments Group");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add content to the window.
        frame.add(this, BorderLayout.CENTER);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        createAndShowGUI();
    }
}
