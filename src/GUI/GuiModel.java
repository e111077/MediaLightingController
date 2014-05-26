package GUI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import Server.Data;
import Server.MessageListener;

/**
 * This is the main gui backend that is used to send data and commands between
 * the database and the Gui
 * 
 * @author marquez
 * 
 */
public class GuiModel {
	// static vars used in the class
	private static Data database;
	private static MessageListener listener;
	private static InetAddress ip;

	/**
	 * Sets the user and the test number in the database.
	 * 
	 * @param user
	 *            Username of the test subject
	 * @param testNum
	 *            Test number / sequence
	 */
	public static void submitInfo(String user, String testNum) {
		database.setTestInfo(user, testNum);
	}

	/**
	 * This updates the axes tab of the gui by adding a row with the appropriate
	 * data
	 * 
	 * @param gui
	 *            Gui object that we are trying to edit
	 * @param time
	 *            Time elapsed since the beginning of the test
	 * @param x
	 *            X coordinate passed into the table
	 * @param y
	 *            Y coordinate passed into the table
	 */
	public static void updateAxes(Gui gui, Float time, Float x, Float y) {
		gui.axesTableModel.addRow(new Object[] { time, x, y });
	}

	/**
	 * This updates the fader tab of the gui by adding a row with the
	 * appropriate data
	 * 
	 * @param gui
	 *            Gui object that we are trying to edit
	 * @param time
	 *            Time elapsed since the beginning of the test
	 * @param bar
	 *            Bar number being edited
	 * @param value
	 *            Y coordinate passed into the table
	 */
	public static void updateFader(Gui gui, Float time, Float bar, Float value) {
		if (!bar.equals(new Float(-1)) && !bar.equals(new Float(-2))) {
			gui.faderTableModel.addRow(new Object[] { time, bar, value });
		} else if (bar.equals(new Float(-1))) {
			gui.faderTableModel.addRow(new Object[] { time, "Warm Button",
					"Pressed" });
		} else if (bar.equals(new Float(-2))) {
			gui.faderTableModel.addRow(new Object[] { time, "Cool Button",
					"Pressed" });
		}
	}

	/**
	 * This sets the database of the Gui Model
	 * 
	 * @param data
	 *            Data object being edited
	 */
	public static void setDatabase(Data data) {
		database = data;
	}

	public static void setListener(MessageListener listen) {
		listener = listen;
	}

	/**
	 * This sets the IP address of the phone we are trying to contact
	 * 
	 * @param ipAddress
	 *            String representing the IP address of the phone in the form of
	 *            "w.x.y.z" or a hostname such as "machine.domain.domain"
	 */
	public static void setIPAddress(String ipAddress) {
		try {
			ip = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "THERE WAS AN IP ERROR");
		}
	}

	/**
	 * Turns the LED on the Axes or Fader screen on or off depending on the
	 * given value
	 * 
	 * @param axes
	 *            set to true if we are changing the led on the axes false if we
	 *            are changing the led on the fader
	 * @param value
	 *            set to 1 if you want the LED to turn on 0 if you want the LED
	 *            to turn off other values will give a swing error message and
	 *            return
	 */
	public static void toggleLED(boolean axes, int value) {
		// creates the sender
		OSCPortOut sender;

		// givs an error message if the value is not 0 or 1
		if (value != 0 && value != 1) {
			JOptionPane.showMessageDialog(null, value
					+ " is not an acceptable input for the LED value"
					+ " (INTERNAL ERROR)");
			return;
		}

		// tries to send a message and gives proper error message if needed
		try {
			// sets the sender to the phones ip at port 9000
			sender = new OSCPortOut(ip, 9000);
			// instantiates the data being sent to the phone
			Object[] arg = new Object[1];
			// assigns a value to the data being sent to the phone
			arg[0] = new Integer(value);

			// declares the OSC message
			OSCMessage msg;

			// assigns the OSC message
			if (axes)
				msg = new OSCMessage("/1/led1", arg);
			else
				msg = new OSCMessage("/2/led1", arg);

			// sends the message to the phone
			sender.send(msg);

			// displays an error message for a socket error
		} catch (SocketException e1) {
			JOptionPane.showMessageDialog(null,
					"THERE WAS AN ERROR WITH THE OUTGOING PORT");

			// displays an error message when sending a message to the phone
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"THERE WAS AN ERROR WITH SENDING THE LED MESSAGE");
		}
	}

	/**
	 * Exports all stored test as many csvs
	 * 
	 * @param fileLocation
	 *            Location where we will export files
	 */
	public static void export(String fileLocation) {
		try {
			database.exportCSV(fileLocation);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"THERE WAS AN ERROR EXPORTING THE CSV");
		}
	}

	/**
	 * Enables the buttons disabled by the submit button on the gui on the given
	 * gui and tab
	 * 
	 * @param gui
	 *            Gui object we want to enable buttons on
	 * @param axes
	 *            true if this is the axes buttons we want to enable and false
	 *            if the faders
	 */
	public static void enableButtons(Gui gui, boolean axes) {
		if (axes) {
			gui.axesSubmitButton.setEnabled(true);
			gui.axesExportButton.setEnabled(true);
			gui.axesTestSubText.setEnabled(true);
			gui.axesTestNumText.setEnabled(true);
			gui.axesIPText.setEnabled(true);
		} else {
			gui.faderSubmitButton.setEnabled(true);
			gui.faderExportButton.setEnabled(true);
			gui.faderTestSubText.setEnabled(true);
			gui.faderTestNumText.setEnabled(true);
			gui.faderIPText.setEnabled(true);
		}
	}

	/**
	 * Stops collecting data from the user.
	 * 
	 * @param axes
	 *            , to indicate axes or faders
	 */
	public static void stopRecording(boolean axes) {
		// creates the sender
		OSCPortOut sender;

		// tries to send a message and gives proper error message if needed
		try {
			// sets the sender to the phones ip at port 9000
			sender = new OSCPortOut(ip, 9000);
			// instantiates the data being sent to the phone
			Object[] arg = new Object[1];
			// assigns a value to the data being sent to the phone
			arg[0] = new Integer(0);

			// declares the OSC message
			OSCMessage msg;

			// assigns the OSC message
			if (axes)
				msg = new OSCMessage("/1/toggle1", arg);
			else
				msg = new OSCMessage("/2/toggle1", arg);

			// sends the message to the phone
			sender.send(msg);

			// displays an error message for a socket error
		} catch (SocketException e1) {
			JOptionPane.showMessageDialog(null,
					"THERE WAS AN ERROR WITH THE OUTGOING PORT");

			// displays an error message when sending a message to the phone
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"THERE WAS AN ERROR WITH SENDING THE LED MESSAGE");
		}

		database.stop(axes);
	}

	/**
	 * Stops collecting data from the user AND resets the lights.
	 * 
	 * @param axes
	 *            , to indicate axes or faders
	 */
	public static void resetLights(boolean axes) {
		stopRecording(axes);
		listener.sendMessage("/set_inactive", null);
	}

	public static void enableLights() {
		listener.sendMessage("/set_active", null);
	}
}