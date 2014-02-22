package GUI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import Server.Data;

public class GuiModel {
    private static Data database;
    private static InetAddress ip;

    public static void submitInfo(String user, String testNum) {
        database.setTestInfo(user, testNum);
    }

    public static void updateAxes(Gui gui, Float time, Float x, Float y) {
        gui.tableModel1.addRow(new Object[] { time, x, y });
    }

    public static void updateFader(Gui gui, Float time, Float bar, Float value) {
        gui.tableModel2.addRow(new Object[] { time, bar, value });
    }

    public static void setDatabase(Data data) {
        database = data;
    }

    public static void setIPAddress(String ipAddress) {
        try {
            ip = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "THERE WAS AN IP ERROR");
        }
    }

    public static void turnOnLED(boolean axes) {
        OSCPortOut sender;
        try {
            sender = new OSCPortOut(ip, 9000);
            Object[] arg = new Object[1];
            arg[0] = new Integer(1);
            OSCMessage msg;
            if (axes)
                msg = new OSCMessage("/1/led1", arg);
            else
                msg = new OSCMessage("/2/led1", arg);
            sender.send(msg);
        } catch (SocketException e1) {
            JOptionPane.showMessageDialog(null,
                    "THERE WAS AN ERROR WITH THE OUTGOING PORT");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "THERE WAS AN ERROR WITH SENDING THE LED MESSAGE");
        }
    }
    
    public static void turnOffLED(boolean axes) {
        OSCPortOut sender;
        try {
            sender = new OSCPortOut(ip, 9000);
            Object[] arg = new Object[1];
            arg[0] = new Integer(0);
            OSCMessage msg;
            if (axes)
                msg = new OSCMessage("/1/led1", arg);
            else
                msg = new OSCMessage("/2/led1", arg);
            sender.send(msg);
        } catch (SocketException e1) {
            JOptionPane.showMessageDialog(null,
                    "THERE WAS AN ERROR WITH THE OUTGOING PORT");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "THERE WAS AN ERROR WITH SENDING THE LED MESSAGE");
        }
    }
    
    public static void export(String fileLocation) {
        try {
            database.exportCSV(fileLocation);
        } catch (IOException e) {
        	e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "THERE WAS AN ERROR EXPORTING THE CSV");
        }
    }
    
    public static void enableButtons(Gui gui, boolean axes) {
        if (axes) {
            gui.tab1button1.setEnabled(true);
            gui.tab1button2.setEnabled(true);
            gui.tab1text1.setEnabled(true);
            gui.tab1text2.setEnabled(true);
            gui.tab1text3.setEnabled(true);
        } else {
            gui.tab2button1.setEnabled(true);
            gui.tab2button2.setEnabled(true);
            gui.tab2text1.setEnabled(true);
            gui.tab2text2.setEnabled(true);
            gui.tab2text3.setEnabled(true);
        }
    }
}
