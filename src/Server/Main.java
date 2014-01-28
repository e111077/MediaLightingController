package Server;

import java.io.IOException;

import com.illposed.osc.*;


public class Main {
    
    public static void main(String[] args) throws IOException {
        int PORT = 5555;
        
        Data database = new Data();
        OSCPortIn receiver = new OSCPortIn(PORT);
        MessageListener listener = new MessageListener(database);
        receiver.addListener("/1/xy1", listener);
        receiver.addListener("/2/multifader1/1", listener);
        receiver.addListener("/2/multifader1/2", listener);
        receiver.addListener("/2/multifader1/3", listener);
        receiver.addListener("/2/multifader1/4", listener);
        receiver.addListener("/1/toggle1", listener);
        receiver.addListener("/2/toggle1", listener);
        
        receiver.startListening();
        Thread listenThread = new Thread(receiver);
        listenThread.start();
    }
}
