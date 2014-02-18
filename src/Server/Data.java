package Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import GUI.Gui;
import GUI.GuiModel;

/**
 * This class acts as a database that stores the points and data and time. It
 * also has the ability to export all of the data into a CSV.
 * 
 * @author marquez
 * 
 */
public class Data {
    // Recording boolean
    private boolean recording = false;
    // Live data arrays from fader and axes
    private ArrayList<Object[]> faderData = new ArrayList<Object[]>();
    private ArrayList<Object[]> axesData = new ArrayList<Object[]>();
    // Array list of older datasets
    private ArrayList<ArrayList<Object[]>> previousAxes = new ArrayList<ArrayList<Object[]>>();
    private ArrayList<ArrayList<Object[]>> previousFader = new ArrayList<ArrayList<Object[]>>();
    // Initial time in seconds since record button was pressed.
    private Long initTime = null;
    // Gui to be edited
    private final Gui gui;

    private String user = "";
    private String testNumber = "";
    
    private int counter = 0;

    public Data(Gui gui) {
        this.gui = gui;
    }

    /**
     * Begins storing data into the arrays
     */
    public void start() {
        // Starts recording
        recording = true;
    }

    /**
     * Stops storing stata into the arrays This also stores the data for
     * exporting and clears the current, live data.
     * 
     * @param axes
     *            this value should be set to true if the data being recorded is
     *            coming from the axes. False if it is coming from the fader.
     */
    public void stop(boolean axes) {
        // Stops recording
        recording = false;

        // Initializes the clone
        ArrayList<Object[]> clone = new ArrayList<Object[]>();

        // Clones the axes data and adds it to the previous collections
        // ArrayList
        if (axes) {
            try {
                clone = (ArrayList<Object[]>) axesData.clone();
                exportCSV(gui.defaultSave, true, clone);
            } catch (Exception e) {
                System.out.println("There was an error in cloning");
                e.printStackTrace();
            }

            previousAxes.add(clone);
            // Clears the axes data array
            axesData = new ArrayList<Object[]>();

            // Clones the fader data and adds it to the previous collections
            // ArrayList
        } else {
            try {
                clone = (ArrayList<Object[]>) faderData.clone();
                exportCSV(gui.defaultSave, true, clone);
            } catch (Exception e) {
                System.out.println("There was an error in cloning");
                e.printStackTrace();
            }

            previousFader.add(clone);
            // Clears the fader data array
            faderData = new ArrayList<Object[]>();

        }

        GuiModel.turnOffLED(axes);
        GuiModel.enableButtons(this.gui, axes);

        // Resets the initTime
        initTime = null;

        // TODO: REMOVE AFTER DEBUGGING IS FINISHED
        // try {
        // exportCSV();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }

    /**
     * Adds a point and appropriate data to the axes data array.
     * 
     * @param data
     *            Array representing a coordinate such as (1,2) would be
     *            represented as {1.0, 2.0} only two dimensions supported
     */
    public void addPoint(Float x, Float y) {
        // Closes if not recording
        if (!recording)
            return;

        // Sets the elapsed time
        float elapsedTime = firstCheck();

        // Adds the appropriate data into the axes ArrayList
        axesData.add(new Object[] { elapsedTime, x, y, user, testNumber });

        // adds to the row
        GuiModel.updateAxes(this.gui, elapsedTime, x, y);
    }

    /**
     * Stores a data entry into the appropriate data array.
     * 
     * @param faderNumber
     *            This is the number of the fader which is typically in the OSC
     *            address as: /pageNumber/faderName/faderNumber
     * 
     * @param faderValue
     *            This is the value of the fader as a Float value
     */
    public void addFaderDatum(float faderNumber, Float faderValue) {
        // Closes if not recording
        if (!recording)
            return;

        // Sets the elapsed time
        float elapsedTime = firstCheck();

        // Adds the appropriate data into the fader ArrayList
        faderData.add(new Object[] { elapsedTime, faderNumber, faderValue,
                user, testNumber });

        GuiModel.updateFader(this.gui, elapsedTime, faderNumber, faderValue);
    }

    /**
     * Checks the initial time
     * 
     * @return The elapsed time
     */
    private float firstCheck() {
        // Sets the initial time if null
        if (this.initTime == null) {
            this.initTime = System.currentTimeMillis();
        }

        // finds the elapsed time in seconds
        float elapsed = ((float) ((System.currentTimeMillis() % 1000000 - this.initTime % 1000000)) / 1000);

        // returns the correct elapsed time if it clocks over 1000000ms
        return elapsed > 0 ? elapsed : elapsed + 1000;
    }

    /**
     * Exports all of the data stored and exports them into .csv files delimited
     * only by commas (As opposed to spaces and semicolons). Separates each test
     * into its own file with a label of what type of test it was and what
     * number of that type of test it is.
     * 
     * @throws IOException
     *             Throws due to an IOException from the FileWriter
     */
    public void exportCSV(String fileLocation, boolean autoSave, ArrayList<Object[]> lastDataset ) throws IOException {
        // gets the prefix of the save location
        String fileLocationPrefix = fileLocation.substring(0,
                fileLocation.length() - 4);
        
        if (!autoSave) {
	        // iterates through the axes
	        for (int i = 0; i < previousAxes.size(); i++) {
	            // breaks if there are no previous axes
	            if (previousAxes.size() == 0)
	                break;
	
	            // opens a file writer and appends the word "axis" the number of the
	            // test and ".csv"
	            FileWriter writer = new FileWriter(fileLocationPrefix + "Axis" + i
	                    + ".csv");
	
	            // creates the column titles
	            writer.append("Time (seconds),X Coordinate,Y Coordinate,Test Subject,Test Number\n");
	
	            // gets the indexed axis
	            ArrayList<Object[]> previousAxis = previousAxes.get(i);
	
	            // iterates through each value in each test
	            for (int j = 1; j < previousAxis.size(); j++) {
	                // gets each attribute in the stored data
	                Float time = (Float) previousAxis.get(j)[0];
	                Float xAxis = (Float) previousAxis.get(j)[1];
	                Float yAxis = (Float) previousAxis.get(j)[2];
	                String username = (String) previousAxis.get(j)[3];
	                String testNumb = (String) previousAxis.get(j)[4];
	
	                // writes in csv format
	                writer.append("" + time + "," + xAxis + "," + yAxis + ","
	                        + username + "," + testNumb + "\n");
	            }
	
	            // cleans up after the writer
	            writer.flush();
	            writer.close();
	        }
	
	        // iterates through the axes
	        for (int i = 0; i < previousFader.size(); i++) {
	            // breaks if there are no previous faders
	            if (previousFader.size() == 0)
	                break;
	
	            // opens a file writer and appends the word "fader" the number of
	            // the
	            // test and ".csv"
	            FileWriter writer = new FileWriter(fileLocationPrefix + "Fader" + i
	                    + ".csv");
	
	            // creates the column titles
	            writer.append("Time (seconds),Fader,Value,Test Subject,Test Number\n");
	
	            // gets the indexed fader
	            ArrayList<Object[]> previousAxis = previousFader.get(i);
	
	            // iterates through each value in each test
	            for (int j = 1; j < previousAxis.size(); j++) {
	                // gets each attribute in the stored data
	                Float time = (Float) previousAxis.get(j)[0];
	                Float fader = (Float) previousAxis.get(j)[1];
	                Float value = (Float) previousAxis.get(j)[2];
	                String username = (String) previousAxis.get(j)[3];
	                String testNumb = (String) previousAxis.get(j)[4];
	
	                // writes in csv format
	                writer.append("" + time + "," + fader + "," + value + ","
	                        + username + "," + testNumb + "\n");
	            }
	
	            // cleans up after the writer
	            writer.flush();
	            writer.close();
	        }
        } else {
        	// opens a file writer and appends the word "fader" the number of
            // the
            // test and ".csv"
            FileWriter writer = new FileWriter(fileLocationPrefix + counter
                    + ".csv");
            counter++;

            // creates the column titles
            writer.append("Time (seconds),Val1,Val2,Test Subject,Test Number\n");

            // gets the indexed fader
            ArrayList<Object[]> previousAxis = lastDataset;

            // iterates through each value in each test
            for (int j = 1; j < previousAxis.size(); j++) {
                // gets each attribute in the stored data
                Float time = (Float) previousAxis.get(j)[0];
                Float fader = (Float) previousAxis.get(j)[1];
                Float value = (Float) previousAxis.get(j)[2];
                String username = (String) previousAxis.get(j)[3];
                String testNumb = (String) previousAxis.get(j)[4];

                // writes in csv format
                writer.append("" + time + "," + fader + "," + value + ","
                        + username + "," + testNumb + "\n");
            }

            // cleans up after the writer
            writer.flush();
            writer.close();
        }
    }
    
    public void exportCSV(String fileLocation) throws IOException {
    	exportCSV(fileLocation, false, null);
    }

    public void setTestInfo(String username, String testNum) {
        this.user = username;
        this.testNumber = testNum;
    }
}
