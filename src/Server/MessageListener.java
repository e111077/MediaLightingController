package Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.JOptionPane;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

/**
 * This is the listener to the messages and sends messages to the light server
 * and stores info in the database
 * 
 * @author marquez
 * 
 */
public class MessageListener implements OSCListener {
    // This is the database to be accessed
    private final Data database;
    private final InetAddress serverIp;
    private final int serverPort;

    /**
     * Constructs the object and uses the given database
     * 
     * @param database
     *            This is the database to be accessed by the listener.
     * @throws UnknownHostException
     *             This is the ip error for the light osc server
     */
    public MessageListener(Data database) throws UnknownHostException {
        this.database = database;
        this.serverIp = InetAddress.getByName("18.85.59.119");
        this.serverPort = 10002;
    }

    /**
     * This is ran every time the OSCport receives an OSCMessage and it stores
     * data into the database given an address of "/1/xy1", "/2/multifader1/#",
     * "/1/toggle1", and "/2/toggle1"
     */
    public void acceptMessage(Date executionTime, OSCMessage message) {
        // Address of the message (aka component name)
        String address = message.getAddress();
        // Data in an object array
        Object[] data = message.getArguments();

        // returns if the data is empty or null
        if (!(data.length != 0 && data[0] != null)) {
            return;
        }

        // adds a point to the database
        if (address.equals("/1/xy1")) {
            this.database.addPoint((Float) data[0], (Float) data[1]);

            Float[] messageValues = { (Float) data[1], (Float) data[1],
                    (Float) data[1] };
            for (int i = 1; i < 21; i++) {
                sendMessage("/sr" + i + "/rgb", messageValues);
            }

            // adds the value to the database
        } else if (address.contains("/2/multifader1")) {
            // instantiates the fader number
            float faderNum = 0;

            try {
                // finds the bar number
                String barNumber = address.split("/")[3];
                faderNum = Float.parseFloat(barNumber);

            } catch (Exception e) {
                System.out.println("There was an error parsing the multifader");
                e.printStackTrace();
            }

            // adds the value to the database
            this.database.addFaderDatum(faderNum, (Float) data[0]);

            Float[] messageValues = { (Float) data[0], (Float) data[0],
                    (Float) data[0] };
            for (float i = (faderNum - 1) * 5 + 1; i < faderNum * 5 + 1; i++) {
                String istr = i + "";
                istr = istr.split("\\.")[0];
                sendMessage("/sr" + istr + "/rgb", messageValues);
            }

            // starts / stops the database collection for the axes
        } else if (address.equals("/1/toggle1")) {
            if ((Float) data[0] == 1f) {
                this.database.start();

            } else {
                this.database.stop(true);

            }
            // starts / stops the database collection for the faders
        } else if (address.equals("/2/toggle1")) {
            if ((Float) data[0] == 1f) {
                this.database.start();

            } else {
                this.database.stop(false);
            }
        }
    }

    /**
     * This method sends OSC messages to the server at the ip addess and port
     * specified in this object's constructor
     * 
     * @param identifier
     *            This is the identifier and command of the light which is
     *            identified as /sr##/COMMAND
     * @param values
     *            this is a float array of the values sent to the server in the
     *            form of a float array (one value for red, green, blue, and
     *            three values for rgb
     */
    private void sendMessage(String identifier, Float[] values) {
        // creates the sender
        OSCPortOut sender;

        // tries to send the message and displays an error if something goes
        // wrong
        try {
            // sets a vlaue to the sender
            sender = new OSCPortOut(this.serverIp, this.serverPort);
            // instantiates the message
            OSCMessage msg;

            // assigns the message with the identifer and the values
            msg = new OSCMessage(identifier, values);

            // sends the message
            sender.send(msg);
        } catch (SocketException e1) {
            // displays an error
            JOptionPane.showMessageDialog(null,
                    "THERE WAS AN ERROR WITH THE OUTGOING PORT");
        } catch (IOException e) {
            // displays an error
            JOptionPane
                    .showMessageDialog(null,
                            "THERE WAS AN ERROR WITH SENDING THE MESSAGE TO THE LIGHT SERVER");
        }
    }
}
