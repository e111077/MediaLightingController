package Server;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import GUI.Gui;
import GUI.GuiModel;

import com.illposed.osc.OSCPortIn;

/**
 * This is the main execution class. This begins the listening threads and runs
 * the front-end of the GUI associated with it.
 * 
 * @author marquez
 * 
 */
public class Main {

	public static void main(String[] args) throws IOException {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

	        public void run() {
	            // Do what you want when the application is stopping
	        	MessageListener listener;
				try {
					listener = new MessageListener(null);
					listener.sendMessage("/set_inactive", null);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }));
		// displays info
		JOptionPane.showMessageDialog(null,
				"Select the directory that includes "
						+ "the coordinate and intensity CSVs.");
		String path = directoryChooser();
		// sets the port in which data is collected
		int PORT = 5555;

		// creates and runs the gui
		Gui gui = new Gui();

		// Instantiates the receiver on the specific port
		OSCPortIn receiver = new OSCPortIn(PORT);
		// Instantiates the database
		Data database = new Data(gui, path);
		GuiModel.setDatabase(database);
		// Instantiates the listener which calls the database object
		MessageListener listener = new MessageListener(database);
		GuiModel.setListener(listener);

		// Adds addresses from the mobile client to the listener
		receiver.addListener("/1/xy1", listener);
		receiver.addListener("/2/multifader1/1", listener);
		receiver.addListener("/2/multifader1/2", listener);
		receiver.addListener("/2/multifader1/3", listener);
		receiver.addListener("/2/multifader1/4", listener);
		receiver.addListener("/2/multifader1/5", listener);
		receiver.addListener("/2/multifader1/6", listener);
		receiver.addListener("/2/multifader1/7", listener);
		receiver.addListener("/2/multifader1/8", listener);
		receiver.addListener("/1/toggle1", listener);
		receiver.addListener("/2/toggle1", listener);
		receiver.addListener("/2/warm", listener);
		receiver.addListener("/2/cool", listener);
		receiver.addListener("/3/push1", listener);
		receiver.addListener("/3/push2", listener);
		receiver.addListener("/3/push3", listener);
		receiver.addListener("/3/push4", listener);
		receiver.addListener("/3/push5", listener);
		receiver.addListener("/3/push6", listener);
		receiver.addListener("/3/push7", listener);
		receiver.addListener("/3/push8", listener);

		// begins listening
		receiver.startListening();
		// creates a thread pointing to the receiver's runnable file
		Thread listenThread = new Thread(receiver);
		// starts the listening thread
		listenThread.start();

		SwingUtilities.invokeLater(gui);
	}

	private static String directoryChooser() {
		// Creates the File Chooser
		JFileChooser chooser = new JFileChooser();

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// Displays the chooser
		int returnedInt = chooser.showOpenDialog(null);

		// If a file is selected, return the string of the filepath
		if (returnedInt == JFileChooser.APPROVE_OPTION) {
			// finds the absolute path of the location
			String path = chooser.getSelectedFile().getAbsolutePath();
			
			if (path.charAt(path.length()-1) != '/') {
				path = path + "/";
			}
			
			// returns the path
			return path;
		}

		// returns null if exited
		return null;
	}
}
