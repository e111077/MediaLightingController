package Server;

import java.awt.List;
import java.net.SocketException;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCPortIn;

import Client.*;

public class Main {
    
    public static void main(String[] args) throws SocketException {
        OSCPortIn receiver;
        int PORT = 5555;
        OSCListener listener;
        
        
        Server server = new Server();
        
        Thread listenThread;
        receiver = new OSCPortIn(PORT);
        listener = new MessageListener(server);
        receiver.addListener("OSCServer", listener);
        
        listenThread = new Thread(new OSCServer(receiver));
        listenThread.start();
        System.out.println("Hello");
    }
}
