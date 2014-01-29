package Server;

import java.util.ArrayList;

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
    private ArrayList<Float[]> faderData = new ArrayList<Float[]>();
    private ArrayList<Float[]> axesData = new ArrayList<Float[]>();
    // Array list of older datasets
    private ArrayList<ArrayList<Float[]>> previousCollections = new ArrayList<ArrayList<Float[]>>();
    // Initial time in seconds since record button was pressed.
    private Long initTime = null;

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
        ArrayList<Float[]> clone = new ArrayList<Float[]>();

        // Clones the axes data and adds it to the previous collections
        // ArrayList
        if (axes) {
            try {
                clone = (ArrayList<Float[]>) axesData.clone();
            } catch (Exception e) {
                System.out.println("There was an error in cloning");
                e.printStackTrace();
            }

            previousCollections.add(clone);
            // Clears the axes data array
            axesData = new ArrayList<Float[]>();

            // Clones the fader data and adds it to the previous collections
            // ArrayList
        } else {
            try {
                clone = (ArrayList<Float[]>) faderData.clone();
            } catch (Exception e) {
                System.out.println("There was an error in cloning");
                e.printStackTrace();
            }

            previousCollections.add(clone);
            // Clears the fader data array
            faderData = new ArrayList<Float[]>();
        }

        // Prints everything for debugging
        // TODO: REMOVE AFTER DEBUGGING IS FINISHED
        printall();
        // Resets the initTime
        initTime = null;
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
        axesData.add(new Float[] { elapsedTime, x, y });
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
        faderData.add(new Float[] { elapsedTime, faderNumber, faderValue });
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

    // PRINTS EVERYTHING
    // TODO: REMOVE AFTER DEBUGGING
    private void printall() {
        for (int i = 0; i < previousCollections.size(); i++) {
            ArrayList<Float[]> collection = previousCollections.get(i);
            if (collection.size() == 0)
                break;

            for (int j = 0; j < collection.size(); j++) {
                Float[] data = collection.get(j);
                String output = "";

                for (int k = 0; k < data.length; k++) {
                    output += data[k] + "\t";
                }
                System.out.println(output += "\n");
            }
        }
    }

    public void exportCSV() {
        // TODO: Export all data to CSV
    }
}
