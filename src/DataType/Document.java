package DataType;

/**
 * Stores the data from a CSV as a Float array of Float arrays
 * 
 * @author marquez
 * 
 */
public class Document {
	// Float arrays of the csv data
	private final Float[][] data;
	// initializations
	private final char axis;
	private final int rows;
	private final int cols;
	public final float max = 2f;

	/**
	 * Constructor for a document also performs initializations and flipping
	 * when necessary
	 * 
	 * @param data
	 *            this is the data within a CSV stored as an array of arrays.
	 * @param axis
	 *            char representing which axis this Document represents 'x' 'y'
	 *            or 'z'
	 */
	public Document(Float[][] data, char axis) {
		// gets the axis
		this.axis = axis;

		// flips the y array because the logic of this object must be read as
		// rows not columns
		if (this.axis == 'y') {
			this.data = this.flipShit(data);
		} else {
			this.data = data;
		}

		// finds the rows and the columns
		this.rows = this.data.length;
		this.cols = this.data[0].length;
	}

	/**
	 * Accessor for rows
	 * 
	 * @return returns the number of rows (columns if y)
	 */
	public int getNumRows() {
		return this.rows;
	}

	/**
	 * Accessor for columns
	 * 
	 * @return returns the number of columns (rows if y)
	 */
	public int getNumCols() {
		return this.cols;
	}

	/**
	 * One Dimensional binary Search
	 * 
	 * @param inputValue
	 *            row / column we are reading from
	 * @param target
	 *            value in row/column we are looking for
	 * @param start
	 *            start of the subsection in binary search
	 * @param end
	 *            end of subsection in binary search
	 * @return interpolated index between the two values target is between and
	 *         their respective indices
	 */
	public Float oneDimensionSearch(int inputValue, Float target, int start,
			int end) {
		if (start <= end) {
			// calculates the new mid
			int mid = (start + end) / 2;
			// TODO: remove after testing
//			 System.out.println("Start: " + start + " End: " + end + " Mid: "
//			 + mid + " Target: " + target);

			// edge cases
			// above max
			if (target > this.data[inputValue][this.cols - 1]) {
				return new Float(this.cols - 1);
				// below min
			} else if (target < this.data[inputValue][0]) {
				return new Float(0);
				// exact value
			} else if (this.data[inputValue][mid].equals(target)) {
				return new Float(mid);
			}

			// terminating condition.
			if (end - start <= 0) {
				// checks for between last two values
				if (mid == this.cols - 1) {
					mid--;
				}
				// gets the lower value
				Float first = this.data[inputValue][mid];
				// gets the upper value
				Float second = this.data[inputValue][mid + 1];
				// calculates the final index
				Float outIndex = this.interpolate(first, mid, second, mid + 1,
						target);

				// calculates the final index if not within that range
				if (target > second) {
					// recalulates the new upper and lower values
					first = this.data[inputValue][mid + 1];
					second = this.data[inputValue][mid + 2];
					// calculates the corrected final index
					outIndex = this.interpolate(first, mid - 1, second, mid,
							target);
				}
				return outIndex;

				// searches left or right of the mid depending on binary search
				// conditions
			} else if (target - this.data[inputValue][mid] < 0) {
				return oneDimensionSearch(inputValue, target, start, mid - 1);
			} else {
				return oneDimensionSearch(inputValue, target, mid + 1, end);
			}
		}

		// should never actually get here lol
		Float first = this.data[inputValue][end];
		Float second = this.data[inputValue][start];
//		TODO: Remove after testing
//		System.out.println("Start: " + start + " End: " + end + " Target: " + target);
		return this.interpolate(first, end, second, start, target);
	}

	/**
	 * Does a two dimensional interpolation of a double binary search
	 * 
	 * @param target
	 *            target value we received from the filter
	 * @param inputValue
	 *            column value that is between two columns
	 * @return returns the interpolated index of the column
	 */
	public Float twoDimensionSearch(Float target, Float inputValue) {
		// gets the columns the input is between
		int firstCol = (int) Math.floor((double) inputValue);
		int secondCol = (int) Math.ceil((double) inputValue);

		// finds the indices of the searches
		Float output1 = this.oneDimensionSearch(firstCol, target, 0,
				this.cols - 1);
		Float output2 = this.oneDimensionSearch(secondCol, target, 0,
				this.cols - 1);

		// interpolates between the interpolated indicies
		Float output = interpolate(1f * firstCol, output1, 1f * secondCol,
				output2, inputValue);

		// returns
		return output;
	}

	/**
	 * Searches the axis to do a 3d interpolation
	 * 
	 * @param xResult
	 *            column gained from the x search
	 * @param yResult
	 *            row gained from the y search
	 * @return returns the interpolated intensity
	 */
	public Float zSearch(Float xResult, Float yResult) {
		// TODO: Remove when finished testing
		// System.out.println("X: " + xResult + " Y: " + yResult);
		// Finds the columns the x is between
		int firstCol = (int) Math.floor((double) xResult);
		int secondCol = (int) Math.ceil((double) xResult);

		// Finds the rows the y is between
		int firstRow = (int) Math.floor((double) yResult);
		int secondRow = (int) Math.ceil((double) yResult);

		// finds the values from the table
		Float firstVal = this.data[firstRow][firstCol];
		Float secondVal = this.data[firstRow][secondCol];
		Float thirdVal = this.data[secondRow][firstCol];
		Float fourthVal = this.data[secondRow][secondCol];

		// 2d Interpolation
		Float interpRowFirst = this.interpolate(1f * firstCol, firstVal,
				1f * secondCol, secondVal, xResult);
		Float interpRowSecond = this.interpolate(1f * firstCol, thirdVal,
				1f * secondCol, fourthVal, xResult);

		// 3d interpolation
		Float outVal = this.interpolate(1f * firstRow, interpRowFirst,
				1f * secondRow, interpRowSecond, yResult);

		// returns the output
		return outVal;
	}

	/**
	 * Generic Linear Interpolation (weighted average) changing a point from
	 * range [x_0, y_0] to range [x_1, y_1]
	 * 
	 * @param firstVal
	 *            initial minimum x_0
	 * @param firstIndex
	 *            new minimum x_1
	 * @param secondVal
	 *            initial maximum y_0
	 * @param secondIndex
	 *            new maximum y_1
	 * @param inputVal
	 *            value we are interpolating
	 * @return returns the linearally interpolated value
	 */
	private Float interpolate(Float firstVal, int firstIndex, Float secondVal,
			int secondIndex, Float inputVal) {
		Float totalDiff = secondVal - firstVal;
		Float firstDiff = inputVal - firstVal;
		Float percent = 1 - (firstDiff / totalDiff);
		Float otherPercent = 1 - percent;

		return firstIndex * percent + secondIndex * otherPercent;
	}

	/**
	 * Generic Linear Interpolation (weighted average) changing a point from
	 * range [x_0, y_0] to range [x_1, y_1]
	 * 
	 * @param originalMin
	 *            initial minimum x_0
	 * @param newMin
	 *            firstIndex new minimum x_1
	 * @param originalMax
	 *            initial maximum y_0
	 * @param newMax
	 *            new maximum y_1
	 * @param inputVal
	 *            value we are interpolating
	 * @return returns the linearally interpolated value
	 */
	private Float interpolate(Float originalMin, Float newMin,
			Float originalMax, Float newMax, Float inputVal) {
		Float totalDiff = originalMax - originalMin;
		Float firstDiff = inputVal - originalMin;

		if (totalDiff == 0) {
			totalDiff = 1f;
		}
		Float percent = 1 - (firstDiff / totalDiff);
		Float otherPercent = 1 - percent;

		return newMin * percent + newMax * otherPercent;
	}

	/**
	 * Flips the array 90 degrees
	 * 
	 * @param shitNeededToBeFlipped
	 *            array that is to be flipped
	 * @return returns the same array flipped 90 degrees
	 */
	private Float[][] flipShit(Float[][] shitNeededToBeFlipped) {
		int numCols = shitNeededToBeFlipped.length;
		int numRows = shitNeededToBeFlipped[0].length;

		Float[][] flippedShit = new Float[numRows][numCols];

		for (int i = 0; i < shitNeededToBeFlipped.length; i++) {
			Float[] rowNeededToBeFlipped = shitNeededToBeFlipped[i];

			for (int j = 0; j < rowNeededToBeFlipped.length; j++) {
				flippedShit[j][i] = rowNeededToBeFlipped[j];
			}
		}

		return flippedShit;
	}
	
//	private void sopl(Object msg) {
//		System.out.println(msg);
//	}
}
