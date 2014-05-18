package Server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import DataType.Document;
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
	// last input value. This is used when checking whether to send another
	// value or not
	private Float lastX;
	private Float lastY;
	private boolean sendX;
	// documents representing the light intensity CSVs
	private final Document x1;
	private final Document y1;
	private final Document zdb;
	private final Document zdg;
	private final Document zdr;
	private final Document zwb;
	private final Document zwg;
	private final Document zwr;

	private final String path;

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

	public int counter = 0;

	public Data(Gui gui, String path) {
		this.lastX = 2f;
		this.lastY = 2f;
		this.path = path;
		this.x1 = readCSV("X", 'x');
		this.y1 = readCSV("Y", 'y');
		this.zdb = readCSV("ZD_B", 'z');
		this.zdg = readCSV("ZD_G", 'z');
		this.zdr = readCSV("ZD_R", 'z');
		this.zwb = readCSV("ZW_B", 'z');
		this.zwg = readCSV("ZW_G", 'z');
		this.zwr = readCSV("ZW_R", 'z');

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
	 * Stops storing data into the arrays This also stores the data for
	 * exporting and clears the current, live data.
	 * 
	 * @param axes
	 *            this value should be set to true if the data being recorded is
	 *            coming from the axes. False if it is coming from the fader.
	 */
	@SuppressWarnings("unchecked")
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

		GuiModel.toggleLED(axes, 0);
		GuiModel.enableButtons(this.gui, axes);

		// Resets the initTime
		initTime = null;
	}

	/**
	 * Calculates the filtered value, which is the value rounded off to 3
	 * decimal places. Returns null if this resulting value is the same as the
	 * last value. Otherwise, the value is returned as normal.
	 * 
	 * @param initVal
	 *            value to be filtered
	 * @param resolution
	 *            must be a number 10^x where x is the order of magnitude of
	 *            resolution
	 * @param isX
	 *            true if you are filtering an x value. false if you are
	 *            filtering a y value.
	 * @return returns a value interpolated between the min and max X or Y
	 *         values given statically. Returns null if a message should not be
	 *         sent (value input more than once)
	 */
	public Float filterVal(Float initVal, float resolution, boolean isX) {
		// return in case of null
		if (initVal == null) {
			return null;
		}

		// initialization
		boolean shouldSend = false;

		// sets the resolution of the number by slicing off numbers at the end
		// of the 15 point float
		Float filteredVal = 0f;
		filteredVal = initVal * resolution;

		filteredVal = (float) Math.floor(filteredVal);

		filteredVal = filteredVal / resolution;

		// checks if X has changed and y has changed. If not return null
		shouldSend = shouldISendAMessage(filteredVal, isX);
		
//		if (!isX) {
//			System.out.println("Send: " + shouldSend);
//			System.out.println("X: " + this.sendX);
//		}
		if (!shouldSend && !isX && !this.sendX) {
			filteredVal = null;
		}

		return filteredVal;
	}

	/**
	 * Calculates the final light intensity to be output
	 * 
	 * @param x
	 *            filtered x value
	 * @param y
	 *            filtered y value
	 * @return interpolated light intensity from the z csv given
	 */
	public Float[] lightIntensity(Float x, Float y) {
		Float xOut = x1.oneDimensionSearch(0, x, 0, x1.getNumCols() - 1);
		Float yOut = y1.twoDimensionSearch(y, xOut);

		Float zWallR = zwr.zSearch(xOut, yOut);
		Float zWallG = zwg.zSearch(xOut, yOut);
		Float zWallB = zwb.zSearch(xOut, yOut);
		Float zDownR = zdr.zSearch(xOut, yOut);
		Float zDownG = zdg.zSearch(xOut, yOut);
		Float zDownB = zdb.zSearch(xOut, yOut);

		Float[] output = { zWallR, zWallG, zWallB, zDownR, zDownG, zDownB };
		return output;
	}

	/**
	 * Checks whether a mesage should be sent to the OSC server based on the
	 * last message received from the user
	 * 
	 * @param messageVal
	 *            value of the user's message
	 * @return returns true if a message should be sent to the osc server
	 */
	private boolean shouldISendAMessage(Float messageVal, boolean isX) {
		// checks if it is x or y
		if (isX) {
			// returns true if x is different false otherwise
			if (!this.lastX.equals(messageVal)) {
				this.lastX = messageVal;
				this.sendX = true;
				return true;
			} else {
				this.sendX = false;
				return false;
			}
		} else {
			// returns true if x is different false otherwise
			if (!this.lastY.equals(messageVal)) {
				this.lastY = messageVal;
				return true;
			} else {
				return false;
			}
		}
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
		if (elapsedTime != 1000) {
			axesData.add(new Object[] { elapsedTime, x, y, user, testNumber });
			// adds to the row
			GuiModel.updateAxes(this.gui, elapsedTime, x, y);
		}
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
		if (elapsedTime != 1000)
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
	 * @param fileLocation
	 *            this is the location where to save the file(s)
	 * @param autoSave
	 *            this is true if this method is being called by an autosave or
	 *            not
	 * @param lastDataset
	 *            this is the last dataset that was entered. Set this to null if
	 *            this is not an autosave
	 * 
	 * @throws IOException
	 *             Throws due to an IOException from the FileWriter
	 */
	public void exportCSV(String fileLocation, boolean autoSave,
			ArrayList<Object[]> lastDataset) throws IOException {
		// gets the prefix of the save location
		String fileLocationPrefix = fileLocation.substring(0,
				fileLocation.length() - 4);

		// exports everything saved in the previos axes arraylists if this is
		// not an autosave
		if (!autoSave) {
			// iterates through the axes
			for (int i = 0; i < previousAxes.size(); i++) {
				// breaks if there are no previous axes
				if (previousAxes.size() == 0)
					break;

				// opens a file writer and appends the word "axis" the number of
				// the
				// test and ".csv"
				FileWriter writer = new FileWriter(fileLocationPrefix + "Axis"
						+ i + ".csv");

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

				// opens a file writer and appends the word "fader" the number
				// of
				// the
				// test and ".csv"
				FileWriter writer = new FileWriter(fileLocationPrefix + "Fader"
						+ i + ".csv");

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

			// exports last dataset if this is an autosave
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

	/**
	 * This exports all datasets to the given file location
	 * 
	 * @param fileLocation
	 *            this is the save location
	 * @throws IOException
	 *             Throws due to an IOException from the FileWriter
	 */
	public void exportCSV(String fileLocation) throws IOException {
		exportCSV(fileLocation, false, null);
	}

	public void setTestInfo(String username, String testNum) {
		this.user = username;
		this.testNumber = testNum;
	}

	/**
	 * Reads the CSV given and returns it as a Document object
	 * 
	 * @param dataset
	 *            this is the CSV file name. If the file is named "filename.csv"
	 *            then dataset would be "filename" without the quotes.
	 * @param axis
	 *            this is a char representing the axis we are reading from. If
	 *            we are reading from the x axis data then this would be 'x', if
	 *            y then 'y', and if z then 'z'. No other values are permitted.
	 * @return A document object that represents the CSV as data
	 */
	private Document readCSV(String dataset, char axis) {
		try {
			// count the number of lines
			int numberOfLines = 0;
			FileReader fr2 = new FileReader(this.path + dataset + ".csv");
			BufferedReader br2 = new BufferedReader(fr2);
			String counterRead = br2.readLine();

			// loop that counts the lines
			while (counterRead != null) {
				counterRead = br2.readLine();
				numberOfLines++;
			}

			// closes the reader
			br2.close();
			fr2.close();

			// initializations for parsing the lines
			FileReader fr = new FileReader(this.path + dataset + ".csv");
			BufferedReader br = new BufferedReader(fr);
			String stringRead = br.readLine();

			// finds the horizontal dimension
			int columns = stringRead.split(",").length;

			// initilaizes the output array
			Float[][] docInfo = new Float[numberOfLines][columns];

			// counter for the lines
			int lineCounter = 0;

			// splits and parses every line and adds it to the float array
			while (stringRead != null) {
				String[] stringNums = stringRead.split(",");

				Float[] values = new Float[stringNums.length];

				for (int i = 0; i < stringNums.length; i++) {
					String stringNum = stringNums[i];
					Float number = 0f;

					// if it is not a number set it to zero
					if (!stringNum.equals("NaN")) {
						number = Float.parseFloat(stringNum);
					}

					values[i] = number;
				}
				docInfo[lineCounter] = values;

				// read the next line
				stringRead = br.readLine();
				lineCounter++;
			}

			// closes the reader
			br.close();
			fr.close();

			// returns the output document
			return new Document(docInfo, axis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}