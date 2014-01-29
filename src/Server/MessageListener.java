package Server;

import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;

public class MessageListener implements OSCListener {
    private final Data database;

    public MessageListener(Data database) {
        this.database = database;
    }

    public void acceptMessage(Date executionTime, OSCMessage message) {
        String address = message.getAddress();
        Object[] data = message.getArguments();

        String[] addressComponents = address.split("/");
        
        if (!(data.length != 0 && data[0] != null)) {
            return;
        }

        if (address.equals("/1/xy1")) {
            this.database.addPoint((Float) data[0], (Float) data [1]);

        } else if (address.contains("/2/multifader1")) {
            float faderNum = 0; 
            try {
                faderNum = Float.parseFloat((String) addressComponents[3]);
            } catch (Exception e) {
                System.out.println("There was an error parsing the multifader");
                e.printStackTrace();
            }
            this.database.addFaderDatum(faderNum, (Float) data[0]);

        } else if (address.equals("/1/toggle1")) {
            if ((Float) data[0] == 1f) {
                this.database.start();

            } else {
                this.database.stop(true);

            }
        } else if (address.equals("/2/toggle1")) {
            if ((Float) data[0] == 1f) {
                this.database.start();

            } else {
                this.database.stop(false);
            }
        }
    }

}
