package Server;

import java.net.SocketException;
import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortIn;

public class OSCServer implements Runnable {
    public final OSCPortIn receiver;
    
    public OSCServer(OSCPortIn receiver) {
        this.receiver = receiver;
    }

    @Override
    public void run() {
        receiver.startListening();
        receiver.run();
    }
}
