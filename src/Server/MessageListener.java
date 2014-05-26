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
    private final Float[] warmWall = {255f, 192f, 110f};
    private final Float[] warmCent = {255f, 192f, 110f};
    private final Float[] coolWall = {255f, 248f, 254f};
    private final Float[] coolCent = {255f, 248f, 254f};
    private Float[] currentWallTemp = warmWall;
    private Float[] currentCentTemp = warmCent;

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
		this.serverIp = InetAddress.getByName("lights.media.mit.edu");
		this.serverPort = 10002;
        sendMessage("/set_active", null); //controls if we can actually do stuff in the room
    }

    /**
     * This runs every time the OSCport receives an OSCMessage and it stores
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
        	// catch bugs and flashes and malformed packets
			if (!((data.length >= 2) &&
					(data[1] != null) &&
					!((Float) data[1] < 0f) &&
					!((Float) data[0] < 0f) &&
					!((Float) data[1] > 1f) &&
					!((Float) data[0] > 1f) &&
					!((Float) data[1]).equals((Float) 1.611492E-10f) &&
					!((Float) data[1]).equals((Float) 7.1E-4f) &&
					!((Float) data[0]).equals((Float) 2.5534332E-9f) &&
					!((Float) data[1]).equals((Float) 2.5534332E-9f) &&
					!((Float) data[1]).equals((Float) 1.862655E-9f) &&
					!((Float) data[1]).equals((Float) 3.2741587E-12f) &&
					!((Float) data[1]).equals((Float) 5.7072E-39f))) {
				return;
			}
			
			// filters the x and y to the correct axes
			Float filteredX = this.database.filterVal((Float) data[0], 100000, true);
			Float filteredY = this.database.filterVal((Float) data[1], 100000, false);
			
			// checks if a nessage should or not be sent
			if (filteredX == null || filteredY == null) {
				return;
			}
			
            this.database.addPoint(filteredX, filteredY);

            // gets the light intensity
 			Float[] intensity = this.database
 					.lightIntensity(filteredX, filteredY);
 			
 			Float wallR = intensity[0];
 			Float wallG = intensity[1];
 			Float wallB = intensity[2];
 			Float downR = intensity[3];
 			Float downG = intensity[4];
 			Float downB = intensity[5];
// 			if (wallR <.1 || wallG < .1 || wallB < .1) {
// 				System.out.println("x: " + data[0] + " y: " + data[1]);
// 				System.out.println("x': " + filteredX + " y': " + filteredY);
// 			}
 			
 			// sends the message to all 20 lights
 			Float[] messageValues =
 				  { wallR, wallG, wallB, // 1st
 					wallR, wallG, wallB,
 					wallR, wallG, wallB, // 2nd
 					wallR, wallG, wallB,
 					wallR, wallG, wallB, // 3rd
 					wallR, wallG, wallB,
 					wallR, wallG, wallB, // 4th
 					wallR, wallG, wallB,
 					wallR, wallG, wallB, // 5th
 					wallR, wallG, wallB,
 					wallR, wallG, wallB, // 6th
 					wallR, wallG, wallB,
 					downR, downG, downB, // 1st
 					downR, downG, downB,
 					downR, downG, downB,
 					downR, downG, downB,
 					downR, downG, downB, // 2nd
 					downR, downG, downB,
 					downR, downG, downB,
 					downR, downG, downB };
 			sendMessage("/all", messageValues);

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
            
            this.database.storeFaderValue(faderNum, (Float) data[0]);
            
            Float[] currentSliders = this.database.getFaderValues();
            
            Float group1 = currentSliders[0];
            Float group2 = currentSliders[1];
            Float group3 = currentSliders[2];
            Float group7 = currentSliders[3];
            Float group8 = currentSliders[4];
            Float group4 = currentSliders[5];
            Float group5 = currentSliders[6];
            Float group6 = currentSliders[7];

            Float wallR = this.currentWallTemp[0];
 			Float wallG = this.currentWallTemp[1];
 			Float wallB = this.currentWallTemp[2];
 			Float downR = this.currentCentTemp[0];
 			Float downG = this.currentCentTemp[1];
 			Float downB = this.currentCentTemp[2];
 			
 			// sends the message to all 20 lights
 			Float[] messageValues =
 				  { wallR * group1, wallG * group1, wallB * group1, // 1st
 					wallR * group1, wallG * group1, wallB * group1,
 					wallR * group2, wallG * group2, wallB * group2, // 2nd
 					wallR * group2, wallG * group2, wallB * group2,
 					wallR * group3, wallG * group3, wallB * group3, // 3rd
 					wallR * group3, wallG * group3, wallB * group3,
 					wallR * group4, wallG * group4, wallB * group4, // 4th
 					wallR * group4, wallG * group4, wallB * group4,
 					wallR * group5, wallG * group5, wallB * group5, // 5th
 					wallR * group5, wallG * group5, wallB * group5,
 					wallR * group6, wallG * group6, wallB * group6, // 6th
 					wallR * group6, wallG * group6, wallB * group6,
 					downR * group7, downG * group7, downB * group7, // 1st
 					downR * group7, downG * group7, downB * group7,
 					downR * group7, downG * group7, downB * group7,
 					downR * group7, downG * group7, downB * group7,
 					downR * group8, downG * group8, downB * group8, // 2nd
 					downR * group8, downG * group8, downB * group8,
 					downR * group8, downG * group8, downB * group8,
 					downR * group8, downG * group8, downB  * group8};
 			sendMessage("/all", messageValues);

            // starts / stops the database collection for the axes
        } else if (address.equals("/2/warm")) {
        	this.currentWallTemp = this.warmWall;
        	this.currentCentTemp = this.warmCent;
        	this.database.addFaderDatum(-1, 0f);
        	
Float[] currentSliders = this.database.getFaderValues();
            
            Float group1 = currentSliders[0];
            Float group2 = currentSliders[1];
            Float group3 = currentSliders[2];
            Float group7 = currentSliders[3];
            Float group8 = currentSliders[4];
            Float group4 = currentSliders[5];
            Float group5 = currentSliders[6];
            Float group6 = currentSliders[7];

            Float wallR = this.currentWallTemp[0];
 			Float wallG = this.currentWallTemp[1];
 			Float wallB = this.currentWallTemp[2];
 			Float downR = this.currentCentTemp[0];
 			Float downG = this.currentCentTemp[1];
 			Float downB = this.currentCentTemp[2];
 			
 			// sends the message to all 20 lights
 			Float[] messageValues =
 				  { wallR * group1, wallG * group1, wallB * group1, // 1st
 					wallR * group1, wallG * group1, wallB * group1,
 					wallR * group2, wallG * group2, wallB * group2, // 2nd
 					wallR * group2, wallG * group2, wallB * group2,
 					wallR * group3, wallG * group3, wallB * group3, // 3rd
 					wallR * group3, wallG * group3, wallB * group3,
 					wallR * group4, wallG * group4, wallB * group4, // 4th
 					wallR * group4, wallG * group4, wallB * group4,
 					wallR * group5, wallG * group5, wallB * group5, // 5th
 					wallR * group5, wallG * group5, wallB * group5,
 					wallR * group6, wallG * group6, wallB * group6, // 6th
 					wallR * group6, wallG * group6, wallB * group6,
 					downR * group7, downG * group7, downB * group7, // 1st
 					downR * group7, downG * group7, downB * group7,
 					downR * group7, downG * group7, downB * group7,
 					downR * group7, downG * group7, downB * group7,
 					downR * group8, downG * group8, downB * group8, // 2nd
 					downR * group8, downG * group8, downB * group8,
 					downR * group8, downG * group8, downB * group8,
 					downR * group8, downG * group8, downB  * group8};
 			sendMessage("/all", messageValues);
        	
        } else if (address.equals("/2/cool")) {
        	this.currentWallTemp = this.coolWall;
        	this.currentCentTemp = this.coolCent;
        	this.database.addFaderDatum(-2, 0f);
        	
Float[] currentSliders = this.database.getFaderValues();
            
            Float group1 = currentSliders[0];
            Float group2 = currentSliders[1];
            Float group3 = currentSliders[2];
            Float group7 = currentSliders[3];
            Float group8 = currentSliders[4];
            Float group4 = currentSliders[5];
            Float group5 = currentSliders[6];
            Float group6 = currentSliders[7];

            Float wallR = this.currentWallTemp[0];
 			Float wallG = this.currentWallTemp[1];
 			Float wallB = this.currentWallTemp[2];
 			Float downR = this.currentCentTemp[0];
 			Float downG = this.currentCentTemp[1];
 			Float downB = this.currentCentTemp[2];
 			
 			// sends the message to all 20 lights
 			Float[] messageValues =
 				  { wallR * group1, wallG * group1, wallB * group1, // 1st
 					wallR * group1, wallG * group1, wallB * group1,
 					wallR * group2, wallG * group2, wallB * group2, // 2nd
 					wallR * group2, wallG * group2, wallB * group2,
 					wallR * group3, wallG * group3, wallB * group3, // 3rd
 					wallR * group3, wallG * group3, wallB * group3,
 					wallR * group4, wallG * group4, wallB * group4, // 4th
 					wallR * group4, wallG * group4, wallB * group4,
 					wallR * group5, wallG * group5, wallB * group5, // 5th
 					wallR * group5, wallG * group5, wallB * group5,
 					wallR * group6, wallG * group6, wallB * group6, // 6th
 					wallR * group6, wallG * group6, wallB * group6,
 					downR * group7, downG * group7, downB * group7, // 1st
 					downR * group7, downG * group7, downB * group7,
 					downR * group7, downG * group7, downB * group7,
 					downR * group7, downG * group7, downB * group7,
 					downR * group8, downG * group8, downB * group8, // 2nd
 					downR * group8, downG * group8, downB * group8,
 					downR * group8, downG * group8, downB * group8,
 					downR * group8, downG * group8, downB  * group8};
 			sendMessage("/all", messageValues);
        	
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
    public void sendMessage(String identifier, Float[] values) {
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
            
            sender.close();
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
