package Server;

import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

public class MessageListener implements OSCListener {
    private final Data database;

    public MessageListener(Data database) {
        this.database = database;
    }

    public void acceptMessage(Date executionTime, OSCMessage message) {
        String address = message.getAddress();
        Object[] data = message.getArguments();

        String[] addressComponents = address.split("/");

        if (address.equals("/1/xy1")) {
            System.out.println(data.getClass());
            this.database.addPoint((Double[]) data);

        } else if (address.equals("/2/multifader1")) {
            int faderNum = 0; 
            try {
                faderNum = Integer.parseInt((String) addressComponents[2]);
            } catch (Exception e) {
                System.out.println("There was an error parsing the multifader");
                e.printStackTrace();
            }
            this.database.addFaderDatum(faderNum, (double) data[0]);

        } else if (address.equals("/1/toggle1")) {
            if ((Integer) data[0] == 1) {
                this.database.start();

            } else {
                this.database.stop(true);

            }
        } else if (address.equals("/1/toggle1")) {
            if ((Integer) data[0] == 1) {
                this.database.start();

            } else {
                this.database.stop(false);
            }
        }
    }

}
