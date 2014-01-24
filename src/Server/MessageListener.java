package Server;

import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

public class MessageListener implements OSCListener {

    public void acceptMessage(Date date, OSCMessage message) {
        Object[] args = message.getArguments();
        for (Object arg : args) {
            System.out.println(arg.toString());
        }
    }

}
