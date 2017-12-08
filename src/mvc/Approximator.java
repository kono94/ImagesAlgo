package mvc;

import java.util.HashMap;
import java.util.Map;

/*
 * Info: Bildgröße können Sie in der MyImage-Klasse ändern durch die Konstanten:
 * 		 IMG_WIDTH
 * 		 IMG_HEIGHT
 * 
 * Vorgehen:
 * Map erstellen: diff color -> quantity
 * Diese Map in zwei array übertragen: diff colors in m_colors speichern
 * 									   quantity in m_quantities speichern
 * Jetzt m_quantities mit quicksort sortieren und jeden swap parallel in m_colors ausführen
 * Grenze angeben, wieviele Farben erhalten bleiben sollen und diese in drei array (R, G, B) speichern
 * R nach Rot sortieren (gleiche Rotanteile dann nach Grün und dann noch nach Blau)
 * G nach Grün sortieren -II-
 * B nach Blau sortieren -II-
 * 
 * neue Map erstellen, die alle unterschiedlichen Farben auf die neuen Farben mappen soll
 * links von der Grenze bleiben alle gleich
 * für jede Farbe rechts der Grenze, die näheste Farbe erreichnen, indem:
 * binäre suche aller Farbanteile der alten Farbe in den jeweiligen R-,G-,B- arrays ->startIndexR,G,B
 * Abstand berechnen
 * in jedem Array nach links und rechts laufen, bis der Abstand zum Farbanteil größer wird,
 * als der jetzige Gesamtabstand (Trick: erst wurzelberechnen, wenn das unter der Wurzel (a²+b²+c²) kleiner ist, als vom
 * vorrigen Gesamtabstand)
 * spart sehr viele Wurzelberechnungen (z.B. Brute Force: 2,3 Milliarden, Jetzt: 700k)
 * 
 * Originalbild jeden Pixel durchgehen und mappen, Ergebnis ins angezeigte Bild speicher (currentImgPix)
 * Fertig!
 * 
 * Annahme/Anmerkung:  
 * 			  (ganz viele 50 Werte für Rotanteil)
 * 			  Eindeutige Sortierung bringt keine Optimieren, da der Abstand immer mindestens
 * 			  1 beträgt, d.h. man würde zwar einen guten Einstiegspunkt finden in allen
 * 			  50ern, aber müsste ja trotzdem durch alle durchgehen, da man sich mindestens
 * 		      die Werte von 49 bis 51 anschauen muss. 	
 */
public class Approximator {

	private Map<Integer, Integer> m_colorMap;
	private int[] m_colors;
	private int[] m_quantities;
	private int[] R;
	private int[] G;
	private int[] B;
	private int m_totalDiffColors;
	private MyImage m_myImg;
	private int roots = 0;

	public Approximator() {}
	
	public void prepareReduction(MyImage myImg) {
			m_myImg = myImg;
			m_myImg.CurrentToOriginal();
			// Colors -> Quantity
			createColorMap();
			m_totalDiffColors = m_colorMap.size();
			m_colors = new int[m_totalDiffColors];
			m_quantities = new int[m_totalDiffColors];			
			representMapWithArrays();
			/* 
			 * sort arrays by color quantity, mirror number-array changes to
			 * color array		 	
			 */
			quickSort(m_quantities, 0, m_quantities.length - 1);
	}

	// returns number of root calculations
	public int reduceColors(int percent) {
		int border = percent*m_totalDiffColors/100;
		fillColorArrays(border);
		sortColorArrays();
		roots = 0;
		/*
		 * replaceMap maps all different colors to
		 * the colors they will be replaced by
		 * color in original picture -> color in new picture
		 * (can be the same ofc.)
		 */
		HashMap<Integer, Integer> replaceMap = new HashMap<Integer, Integer>();
		/*
		 * m_colors is sorted in descending order. 
		 * Most common color at [0]...
		 * Colors left of the border-index remain the same
		 */
		for (int i = 0; i < border; i++) {
			replaceMap.put(m_colors[i], m_colors[i]);
		}
		/*
		 * Colors right of the border-index are replaced
		 * by their "closest" color of remaining colors
		 */
		for (int i = border; i < m_totalDiffColors; i++) {
			replaceMap.put(m_colors[i], getClosestColor(m_colors[i]));
		}
		/*
		 * use replaceMap for every pixel in original
		 * picture and save new pixels in displayed picture
		 */
		for (int i = 0; i < m_myImg.getCurrentPix().length; i++) {
			m_myImg.getCurrentPix()[i] = replaceMap.get(m_myImg.getOriginalPix()[i]);
		}
		m_myImg.newPixels();
		return roots;
	}

	private int getClosestColor(int oldC) {
		ApproxElement appo = new ApproxElement(oldC);

		// set entry points in every R-,G-,B- array
		appo.setStartIndexRed(binSearch(R, oldC, Model.SHIFT_RED));
		appo.setStartIndexGreen(binSearch(G, oldC, Model.SHIFT_GREEN));
		appo.setStartIndexBlue(binSearch(B, oldC, Model.SHIFT_BLUE));
		/*
		 * iterates through all R-,G-,B- arrays at the same time
		 *  -1 R_start +1, -1 G_start +1, -1 B_start +1
		 *  -2 R_start +2, -2 G_start +2, -2 B_start +2
		 *  ...
		 *  testDistance() returns 1, if the current (R) part
		 *  is still in range, return 0 if not
		 */
		int checks = 1;
		int offs = 0;
		while (checks > 0) {
			checks = appo.testDistance(R, appo.getRedStartIndex() - offs, Model.SHIFT_RED)
					+ appo.testDistance(R, appo.getRedStartIndex() + offs, Model.SHIFT_RED)
					+ appo.testDistance(G, appo.getGreenStartIndex() - offs, Model.SHIFT_GREEN)
					+ appo.testDistance(G, appo.getGreenStartIndex() + offs, Model.SHIFT_GREEN)
					+ appo.testDistance(B, appo.getBlueStartIndex() - offs, Model.SHIFT_BLUE)
					+ appo.testDistance(B, appo.getBlueStartIndex() + offs, Model.SHIFT_BLUE);
			offs++;
		}
		return appo.getNewColor();
	}

	// helper class
	class ApproxElement {
		private int m_colorToBeReplaced;
		private int m_newColor;
		private int m_startIndexRed;
		private int m_startIndexGreen;
		private int m_startIndexBlue;
		private int m_distance;
		/*
		 * trick to only operate a square calculation
		 * if the distance is smaller before the square root (a²+b²+c²)
		 */
		private int m_distanceBeforeSqrt;
		public static final int RED = 0, GREEN = 1, BLUE = 2;

		public ApproxElement(int color) {
			m_colorToBeReplaced = color;
			m_newColor = color;
			m_distance = Integer.MAX_VALUE;
			m_distanceBeforeSqrt = Integer.MAX_VALUE;
			m_startIndexRed = -1;
			m_startIndexGreen = -1;
			m_startIndexBlue = -1;
		}

		/*
		 * parameter "index" is correctly called
		 * by getStartIndexRed() +- offset
		 */
		public int testDistance(int[] arr, int index, int shift) {
			if (index > 0 && index < arr.length && isInDistance(arr[index], shift)) {
				checkForNewDistance(arr[index], m_colorToBeReplaced);			
				return 1;
			} else {
				return 0;
			}
		}
		
		public boolean isInDistance(int checkColor, int shift) {
			return Math.abs(((checkColor >> shift) & 0xff) - ((m_colorToBeReplaced >> shift) & 0xff)) <= m_distance;
		}
		
		private void checkForNewDistance(int checkC, int col) {
			int a = ((checkC >> Model.SHIFT_RED) & 0xff) - ((col >> Model.SHIFT_RED) & 0xff);
			int b = ((checkC >> Model.SHIFT_GREEN) & 0xff) - ((col >> Model.SHIFT_GREEN) & 0xff);
			int c = ((checkC >> Model.SHIFT_BLUE) & 0xff) - ((col >> Model.SHIFT_BLUE) & 0xff);
			int beforeSqrt = a * a + b * b + c * c;
			if(beforeSqrt < m_distanceBeforeSqrt) {
				m_distance = (int) Math.ceil(Math.sqrt(beforeSqrt));;
				m_distanceBeforeSqrt = beforeSqrt;
				m_newColor = checkC;
				roots++;			
			}
		}
		// getter and setter of indexes
		public void setStartIndexRed(int index) {
			m_startIndexRed = index;
		}
		public void setStartIndexGreen(int index) {
			m_startIndexGreen = index;
		}
		public void setStartIndexBlue(int index) {
			m_startIndexBlue = index;
		}
		public int getRedStartIndex() {
			return m_startIndexRed;
		}
		public int getGreenStartIndex() {
			return m_startIndexGreen;
		}
		public int getBlueStartIndex() {
			return m_startIndexBlue;
		}
		public int getNewColor() {
			return m_newColor;
		}
		public int getDistance() {
			return m_distance;
		}
	}

	private void createColorMap() {
		m_colorMap = new HashMap<Integer, Integer>();
		int[] pix = m_myImg.getOriginalPix();
		for (int i = 0; i < pix.length; ++i) {
			if (!m_colorMap.containsKey(pix[i])) {
				m_colorMap.put(pix[i], 1);
			} else {
				m_colorMap.put(pix[i], (int) m_colorMap.get(pix[i]) + 1);
			}
		}
	}

	private void representMapWithArrays() {
		int counter = 0;
		for (Integer color : m_colorMap.keySet()) {
			m_colors[counter] = color;
			m_quantities[counter] = (Integer) m_colorMap.get(color);
			counter++;
		}
	}

	private void fillColorArrays(int grenze) {
		R = new int[grenze];
		G = new int[grenze];
		B = new int[grenze];

		int c = 0;
		for (int i = 0; i < grenze; ++i) {
			R[c] = m_colors[c];
			G[c] = m_colors[c];
			B[c] = m_colors[c];
			++c;
		}
	}

	/*
	 * destinct sorting of the R-,G-,B- arrays
	 * (pointless)
	 * R: sort RGB as int
	 * G: sort GRB as int, swap back to RGB
	 * B: sort BGR as int, swap back to RGB
	 */
	private void sortColorArrays() {
		quickSortColorArray(R, 0, R.length - 1);
		switchRedWithGreen(G);
		quickSortColorArray(G, 0, G.length - 1);
		switchRedWithGreen(G);
		switchRedWithBlue(B);
		quickSortColorArray(B, 0, B.length - 1);
		switchRedWithBlue(B);
	}

	private void switchRedWithGreen(int[] arr) {
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = (arr[i] & 0xff000000) | ((arr[i] & 0x0000ff00) << 8) | ((arr[i] & 0x00ff0000) >> 8)
					| ((arr[i] & 0xff));
		}
	}

	private void switchRedWithBlue(int[] arr) {
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = (arr[i] & 0xff000000) | ((arr[i] & 0x000000ff) << 16) | ((arr[i] & 0x00ff0000) >> 16)
					| ((arr[i] & 0x0000ff00));
		}
	}

	private int binSearch(int[] arr, int val, int shift) {
			int iL = 0;
			int iR = arr.length - 1;
			int MIDDLE = -1;
			while (iL <= iR) {
				MIDDLE = (iL + iR) / 2;
				final int RES = ((arr[MIDDLE] >> shift) & 0xff) - ((val >> shift) & 0xff);
				if (RES == 0)
					break;
				else if (RES < 0)
					iL = MIDDLE + 1;
				else
					iR = MIDDLE - 1;
			}
	
		// gets the left part of the two-indexes-solution
		// if uncomment, change:
		// offset = 0;
		// left: start - offset
		// right: start + offset + 1
	//		if (MIDDLE < (arr.length - 1) / 2) {
	//			if (MIDDLE == 0)
	//				return MIDDLE;
	//			else
	//				return MIDDLE - 1;
	//		} else {
	//			if (MIDDLE == arr.length - 1)
	//				return MIDDLE - 1;
	//			else
	//				return MIDDLE;
	//		}
	
			// gets index with the "closest" color
			if (MIDDLE == 0 || MIDDLE == arr.length - 1)
				return MIDDLE;
	
			if (val < arr[MIDDLE]) {
				if ((arr[MIDDLE] - val) > (val - arr[MIDDLE - 1]))
					return MIDDLE - 1;
			} else if (val > arr[MIDDLE]) {
				if ((val - arr[MIDDLE]) > (arr[MIDDLE + 1] - val))
					return MIDDLE + 1;
			}
			return MIDDLE;
		}

	// first quicksort for colors and quantity arrays DESCENDING
	private void quickSort(int[] quants, int low, int high) {
		if (quants == null || quants.length == 0)
			return;

		if (low >= high)
			return;

		int middle = low + (high - low) / 2;
		int pivot;

		pivot = quants[middle];

		int i = low, j = high;
		while (i <= j) {
			while (quants[i] > pivot) {
				i++;
			}
			while (quants[j] < pivot) {
				j--;
			}

			if (i <= j) {
				int temp = quants[i];
				quants[i] = quants[j];
				quants[j] = temp;

				// mirrors swaps in colors array
				temp = m_colors[i];
				m_colors[i] = m_colors[j];
				m_colors[j] = temp;

				i++;
				j--;
			}
		}
		if (low < j)
			quickSort(quants, low, j);
		if (high > i)
			quickSort(quants, i, high);
	}

	// second quicksort to sort R-,G-,B- arrays ASCENDING
	private void quickSortColorArray(int[] arr, int low, int high) {
		if (arr == null || arr.length == 0)
			return;
		if (low >= high)
			return;

		int middle = low + (high - low) / 2;
		int pivot;
		pivot = arr[middle];

		int i = low, j = high;
		while (i <= j) {
			while (arr[i] < pivot) {
				i++;
			}
			while (arr[j] > pivot) {
				j--;
			}
			if (i <= j) {
				int temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
				i++;
				j--;
			}
		}
		if (low < j)
			quickSortColorArray(arr, low, j);
		if (high > i)
			quickSortColorArray(arr, i, high);
	}

	public int getColorCount() {
		return m_totalDiffColors;
	}
}
