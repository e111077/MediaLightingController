package Server;

import java.util.ArrayList;

public class Data {
    private boolean recording = false;
    private ArrayList<Float[]> faderData = new ArrayList<Float[]>();
    private ArrayList<Float[]> axesData = new ArrayList<Float[]>();
    private ArrayList<ArrayList<Float[]>> previousCollections = new ArrayList<ArrayList<Float[]>>();
    private Float initTime = null;

    /**
     * Begins storing data into the arrays
     */
    public void start() {
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
        recording = false;

        ArrayList<Float[]> clone = new ArrayList<Float[]>();

        if (axes) {
            try {
                clone = (ArrayList<Float[]>) axesData.clone();
            } catch (Exception e) {
                System.out.println("There was an error in cloning");
                e.printStackTrace();
            }

            previousCollections.add(clone);
            axesData = new ArrayList<Float[]>();
        } else {
            try {
                clone = (ArrayList<Float[]>) faderData.clone();
            } catch (Exception e) {
                System.out.println("There was an error in cloning");
                e.printStackTrace();
            }

            previousCollections.add(clone);
            faderData = new ArrayList<Float[]>();
        }
        
        printall();
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
        if (!recording)
            return;

        float elapsedTime = firstCheck();

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
        if (!recording)
            return;

        Float elapsedTime = firstCheck();

        faderData.add(new Float[] { elapsedTime, faderNumber, faderValue });
    }

    /**
     * Checks the initial time
     * 
     * @return The elapsed time
     */
    private float firstCheck() {
        if (initTime == null) {
            initTime = (float) System.currentTimeMillis();
        }

        return (float) (System.currentTimeMillis() - initTime);
    }

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
