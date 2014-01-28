package Server;

import java.util.ArrayList;

public class Data {
    private boolean recording = false;
    private ArrayList<double[]> faderData = new ArrayList<double[]>();
    private ArrayList<double[]> axesData = new ArrayList<double[]>();
    private ArrayList<ArrayList<double[]>> previousCollections = new ArrayList<ArrayList<double[]>>();
    private Double initTime = null;

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

        ArrayList<double[]> clone = new ArrayList<double[]>();

        if (axes) {
            try {
                clone = (ArrayList<double[]>) axesData.clone();
            } catch (Exception e) {
                System.out.println("There was an error in cloning");
                e.printStackTrace();
            }

            previousCollections.add(clone);
            axesData = new ArrayList<double[]>();
        } else {
            try {
                clone = (ArrayList<double[]>) faderData.clone();
            } catch (Exception e) {
                System.out.println("There was an error in cloning");
                e.printStackTrace();
            }

            previousCollections.add(clone);
            faderData = new ArrayList<double[]>();
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
    public void addPoint(Double[] data) {
        if (!recording)
            return;

        double elapsedTime = firstCheck();

        axesData.add(new double[] { elapsedTime, data[0], data[1] });
    }

    /**
     * Stores a data entry into the appropriate data array.
     * 
     * @param faderNumber
     *            This is the number of the fader which is typically in the OSC
     *            address as: /pageNumber/faderName/faderNumber
     * 
     * @param faderValue
     *            This is the value of the fader as a double value
     */
    public void addFaderDatum(int faderNumber, double faderValue) {
        if (!recording)
            return;

        double elapsedTime = firstCheck();

        faderData.add(new double[] { elapsedTime, faderNumber, faderValue });
    }

    /**
     * Checks the initial time
     * 
     * @return The elapsed time
     */
    private double firstCheck() {
        if (initTime == null) {
            initTime = (double) System.currentTimeMillis();
        }

        return (double) (System.currentTimeMillis() - initTime);
    }

    private void printall() {
        for (int i = 0; i < previousCollections.size(); i++) {
            ArrayList<double[]> collection = previousCollections.get(i);
            if (collection.size() == 0)
                break;
            
            for (int j = 0; j < collection.size(); j++) {
                double[] data = collection.get(j);
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
