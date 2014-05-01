package GUI;

import static javax.swing.GroupLayout.Alignment.BASELINE;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * This is the main gui panel.
 * 
 * @author fcolon
 * @author marquez
 * 
 */
@SuppressWarnings("serial")
public class Gui extends JPanel implements Runnable {
    public final JButton axesSubmitButton;
    public final JButton axesExportButton;
    public final JTextField axesTestSubText; // Test subject
    public final JTextField axesTestNumText; // Test number
    public final JTextField axesIPText; // IP Address

    public final JButton faderSubmitButton;
    public final JButton faderExportButton;
    public final JTextField faderTestSubText; // Test subject
    public final JTextField faderTestNumText; // Test number
    public final JTextField faderIPText; // IP Address
    public final JButton faderStopRecording; //Stop recording data
    public final JButton faderResetLights; //Reset Lights and Stop the user from controlling them

    private final JLabel axesTestSubLabel; // Test subject
    private final JLabel axesTestNumLabel; // Test number
    private final JLabel axesIPLabel; // IP Address
    public final JButton axesStopRecording; //Stop recording data
    public final JButton axesResetLights; //Reset Lights and Stop the user from controlling them
    
    private final JLabel faderTestSubLabel; // Test subject
    private final JLabel faderTestNumLabel; // Test number
    private final JLabel faderIPLabel; // IP Address

    public final DefaultTableModel axesTableModel; // X,Y table
    public final DefaultTableModel faderTableModel; // Bar table
    private final JTable axesDataTable;
    private final JTable faderDataTable;

    // The following are used for organizing the layout.
    private final Group horizontal1;
    private final Group vertical1;
    private final Group horizontal2;
    private final Group vertical2;

    private final Group axesrow1;
    private final Group axesrow2;
    private final Group axesrow3;
    private final Group axesvert1;
    private final Group axesvert2;
    private final Group axesvert3;

    private final Group faderRow1;
    private final Group faderRow2;
    private final Group faderRow3;
    private final Group faderVert1;
    private final Group faderVert2;
    private final Group faderVert3;

    public final String defaultSave;

    public Gui() {
        super(new GridLayout(1, 1));

        // displays info
        JOptionPane.showMessageDialog(null, "Choose the Autosave location. "
                + "We recommend not selecting a\nprevious autosave location "
                + "and not where you will export all of\nyour data in the end");
        
        // default save location chosen by file chooser
        this.defaultSave = saveFileChooser();
        
        // closes if default save location is not selected
        if (this.defaultSave == null) {
            System.exit(0);
        }

        JTabbedPane tabbedPane = new JTabbedPane();

        // -----------------------------1st
        // Tab-----------------------------------
        // Instantiate components
        axesSubmitButton = new JButton();
        axesSubmitButton.setName("axesSubmitButton");
        axesSubmitButton.setText("Submit");
        axesTestSubLabel = new JLabel(); // Note: Labels are reused in second tab
        axesExportButton = new JButton();
        axesExportButton.setName("axesExportButton");
        axesExportButton.setText("Export All Tests");
        axesTestSubLabel.setText("Test Subject: ");
        axesTestNumLabel = new JLabel();
        axesTestNumLabel.setText("Test Number: ");
        axesIPLabel = new JLabel();
        axesIPLabel.setText("IP Address: ");
        axesTestSubText = new JTextField(20); // Test subject
        axesTestSubText.setName("axesTestSubText");
        axesTestNumText = new JTextField(20); // Test number
        axesTestNumText.setName("axesTestNumText");
        axesIPText = new JTextField(20); // IP Address
        axesIPText.setName("axesIPText");
        axesStopRecording = new JButton();
        axesStopRecording.setName("axesStopRecording");
        axesStopRecording.setText("Stop Recording");
        axesResetLights = new JButton();
        axesResetLights.setName("axesResetLights");
        axesResetLights.setText("Reset Lights");

        Vector<String> axesTableColumns = new Vector<String>();
        axesTableColumns.add("Time");
        axesTableColumns.add("X");
        axesTableColumns.add("Y");
        axesTableModel = new DefaultTableModel(axesTableColumns, 0) {
            private static final long serialVersionUID = 2045698881619435427L;

            @Override
            public boolean isCellEditable(int row, int column) {

                // Make the cells not editable
                return false;
            }
        };
        axesDataTable = new JTable(axesTableModel);
        final JScrollPane scrollTable1 = new JScrollPane(axesDataTable);
        axesDataTable.setName("dataTable1");

        // Set up layout
        JComponent panel1 = makeTextPanel("axesPanel");
        GroupLayout layout1 = new GroupLayout(panel1);
        panel1.setLayout(layout1);
        layout1.setAutoCreateGaps(true);
        layout1.setAutoCreateContainerGaps(true);

        // Arrange horizontal
        axesrow1 = layout1.createSequentialGroup();
        axesrow1.addComponent(axesTestSubLabel);
        axesrow1.addComponent(axesTestSubText);
        axesrow1.addComponent(axesTestNumLabel);
        axesrow1.addComponent(axesTestNumText);
        axesrow1.addComponent(axesIPLabel);
        axesrow1.addComponent(axesIPText);
        axesrow1.addComponent(axesSubmitButton);
        axesrow1.addComponent(axesExportButton);

        axesrow2 = layout1.createSequentialGroup();
        axesrow2.addComponent(axesStopRecording);
        axesrow2.addComponent(axesResetLights);
        
        axesrow3 = layout1.createSequentialGroup();
        axesrow3.addComponent(scrollTable1);

        horizontal1 = layout1.createParallelGroup();
        horizontal1.addGroup(axesrow1);
        horizontal1.addGroup(axesrow2);
        horizontal1.addGroup(axesrow3);
        layout1.setHorizontalGroup(horizontal1);

        // Arrange vertical
        axesvert1 = layout1.createParallelGroup(BASELINE);
        axesvert1.addComponent(axesTestSubLabel);
        axesvert1.addComponent(axesTestSubText);
        axesvert1.addComponent(axesTestNumLabel);
        axesvert1.addComponent(axesTestNumText);
        axesvert1.addComponent(axesIPLabel);
        axesvert1.addComponent(axesIPText);
        axesvert1.addComponent(axesSubmitButton);
        axesvert1.addComponent(axesExportButton);

        axesvert2 = layout1.createParallelGroup(BASELINE);
        axesvert2.addComponent(axesStopRecording);
        axesvert2.addComponent(axesResetLights);
        
        axesvert3 = layout1.createParallelGroup(BASELINE);
        axesvert3.addComponent(scrollTable1);

        vertical1 = layout1.createSequentialGroup();
        vertical1.addGroup(axesvert1);
        vertical1.addGroup(axesvert2);
        vertical1.addGroup(axesvert3);
        layout1.setVerticalGroup(vertical1);

        scrollTable1.setColumnHeaderView(axesDataTable.getTableHeader());

        axesSubmitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GuiModel.submitInfo(axesTestSubText.getText(), axesTestNumText.getText());
                GuiModel.setIPAddress(axesIPText.getText());
                GuiModel.toggleLED(true, 1);
                axesTestSubText.setEnabled(false);
                axesTestNumText.setEnabled(false);
                axesIPText.setEnabled(false);
                axesSubmitButton.setEnabled(false);
                axesExportButton.setEnabled(false);

                for (int i = axesTableModel.getRowCount() - 1; i >= 0; i--) {
                    axesTableModel.removeRow(i);
                }
                
                GuiModel.enableLights();
            }
        });

        axesExportButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GuiModel.export(saveFileChooser());
            }
        });
        
        axesStopRecording.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	GuiModel.toggleLED(true, 0);
            	GuiModel.stopRecording(true);
            }
        });
        
        axesResetLights.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	GuiModel.toggleLED(true, 0);
            	GuiModel.resetLights(true);
            }
        });

        // -----------------------------2nd
        // Tab-----------------------------------
        // instantiating components
        faderSubmitButton = new JButton();
        faderSubmitButton.setName("tab2button1");
        faderSubmitButton.setText("Submit");
        faderTestSubLabel = new JLabel(); // Note: Labels are reused in second tab
        faderExportButton = new JButton();
        faderExportButton.setName("tab2button2");
        faderExportButton.setText("Export All Tests");
        faderTestSubLabel.setText("Test Subject: ");
        faderTestNumLabel = new JLabel();
        faderTestNumLabel.setText("Test Number: ");
        faderIPLabel = new JLabel();
        faderIPLabel.setText("IP Address: ");
        faderTestSubText = new JTextField(20); // Test subject
        faderTestSubText.setName("text1");
        faderTestNumText = new JTextField(20); // Test number
        faderTestNumText.setName("text2");
        faderIPText = new JTextField(20); // IP Address
        faderIPText.setName("text2");
        faderStopRecording = new JButton();
        faderStopRecording.setName("faderStopRecording");
        faderStopRecording.setText("Stop Recording");
        faderResetLights = new JButton();
        faderResetLights.setName("faderResetLights");
        faderResetLights.setText("Reset Lights");

        Vector<String> table2Columns = new Vector<String>();
        table2Columns.add("Time");
        table2Columns.add("Bar Number");
        table2Columns.add("Value");
        faderTableModel = new DefaultTableModel(table2Columns, 0) {
            private static final long serialVersionUID = 2045698881619435427L;

            @Override
            public boolean isCellEditable(int row, int column) {

                // Make the cells not editable
                return false;
            }
        };

        faderDataTable = new JTable(faderTableModel);
        final JScrollPane scrollTable2 = new JScrollPane(faderDataTable);
        faderDataTable.setName("dataTable2");

        // Set up layout
        JComponent panel2 = makeTextPanel("faderPanel");
        GroupLayout layout2 = new GroupLayout(panel2);
        panel2.setLayout(layout2);
        layout2.setAutoCreateGaps(true);
        layout2.setAutoCreateContainerGaps(true);

        // Arrange horizontal
        faderRow1 = layout2.createSequentialGroup();
        faderRow1.addComponent(faderTestSubLabel);
        faderRow1.addComponent(faderTestSubText);
        faderRow1.addComponent(faderTestNumLabel);
        faderRow1.addComponent(faderTestNumText);
        faderRow1.addComponent(faderIPLabel);
        faderRow1.addComponent(faderIPText);
        faderRow1.addComponent(faderSubmitButton);
        faderRow1.addComponent(faderExportButton);

        faderRow2 = layout2.createSequentialGroup();
        faderRow2.addComponent(faderStopRecording);
        faderRow2.addComponent(faderResetLights);
        
        faderRow3 = layout2.createSequentialGroup();
        faderRow3.addComponent(scrollTable2);

        horizontal2 = layout2.createParallelGroup();
        horizontal2.addGroup(faderRow1);
        horizontal2.addGroup(faderRow2);
        horizontal2.addGroup(faderRow3);
        layout2.setHorizontalGroup(horizontal2);

        // Arrange vertical
        faderVert1 = layout2.createParallelGroup(BASELINE);
        faderVert1.addComponent(faderTestSubLabel);
        faderVert1.addComponent(faderTestSubText);
        faderVert1.addComponent(faderTestNumLabel);
        faderVert1.addComponent(faderTestNumText);
        faderVert1.addComponent(faderIPLabel);
        faderVert1.addComponent(faderIPText);
        faderVert1.addComponent(faderSubmitButton);
        faderVert1.addComponent(faderExportButton);

        faderVert2 = layout2.createParallelGroup(BASELINE);
        faderVert2.addComponent(faderStopRecording);
        faderVert2.addComponent(faderResetLights);
        
        faderVert3 = layout2.createParallelGroup(BASELINE);
        faderVert3.addComponent(scrollTable2);

        vertical2 = layout2.createSequentialGroup();
        vertical2.addGroup(faderVert1);
        vertical2.addGroup(faderVert2);
        vertical2.addGroup(faderVert3);
        layout2.setVerticalGroup(vertical2);

        scrollTable2.setColumnHeaderView(faderDataTable.getTableHeader());

        faderSubmitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GuiModel.submitInfo(faderTestSubText.getText(), faderTestNumText.getText());
                GuiModel.setIPAddress(faderIPText.getText());
                GuiModel.toggleLED(false, 1);
                faderTestSubText.setEnabled(false);
                faderTestNumText.setEnabled(false);
                faderIPText.setEnabled(false);
                faderSubmitButton.setEnabled(false);
                faderExportButton.setEnabled(false);

                for (int i = faderTableModel.getRowCount() - 1; i >= 0; i--) {
                    faderTableModel.removeRow(i);
                }
            }
        });

        faderExportButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GuiModel.export(saveFileChooser());
            }
        });
        
        faderStopRecording.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	GuiModel.toggleLED(true, 0);
            	GuiModel.stopRecording(false);
            }
        });
        
        faderResetLights.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	GuiModel.toggleLED(true, 0);
            	GuiModel.resetLights(false);
            }
        });
        
        // ///////////////////////////

        tabbedPane.addTab("Axes Data", panel1);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        tabbedPane.addTab("Fader Data", panel2);
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

    /**
     * This opens a JFileChooser to select the location of where to save the
     * CSV. It also appends .csv to the filename if it is not already present.
     * 
     * @return the filepath in which we want to save the canvas image with
     *         ".csv" appended to the end
     */
    public String saveFileChooser() {
        // Creates the File Chooser
        JFileChooser chooser = new JFileChooser();

        // sets the default file name
        chooser.setSelectedFile(new File("dataExport.csv"));

        // Displays the chooser
        int returnedInt = chooser.showSaveDialog(null);

        // If a file is selected, return the string of the filepath
        if (returnedInt == JFileChooser.APPROVE_OPTION) {
            // finds the absolute path of the location
            String path = chooser.getSelectedFile().getAbsolutePath();

            // if it does not end with .csv, it sets it to end with .csv
            if (!path.endsWith(".csv")) {
                return path + ".csv";
            }

            // returns the path
            return path;
        }

        // returns null if exited
        return null;
    }

    @Override
    public void run() {
        createAndShowGUI();
    }
}