package Server;

import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;

public class MessageListener implements OSCListener {
    // This is the database to be accessed
    private final Data database;

    /**
     * Constructs the object and uses the given database
     * 
     * @param database
     *            This is the database to be accessed by the listener.
     */
    public MessageListener(Data database) {
        this.database = database;
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

}
