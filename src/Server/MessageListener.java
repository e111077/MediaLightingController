package Server;

import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

public class MessageListener implements OSCListener {
    
    public MessageListener(Server server) {
        System.out.println("HOLLA DOLLA BILLS YALL");
    }

    @Override
    public void acceptMessage(Date date, OSCMessage message) {
        String messageString = message.getArguments().toString();
        System.out.println(messageString);
        System.out.println("BITCHES");
    }

}
