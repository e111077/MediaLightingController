package Server;

import java.io.IOException;

import com.illposed.osc.*;


public class Main {
    
    public static void main(String[] args) throws IOException {
        int PORT = 5555;
        
        OSCPortIn receiver = new OSCPortIn(PORT);
        MessageListener listener = new MessageListener();
        receiver.addListener("/1/xy1", listener);
        receiver.addListener("/2/multifader1", listener);
        
        receiver.startListening();
        Thread listenThread = new Thread(receiver);
        listenThread.start();
        System.out.println("Hello");
    }
}
