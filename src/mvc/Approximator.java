package mvc;

import java.util.HashMap;
import java.util.Map;

public class Approximator {

	private Map<Integer, Integer> m_colorMap;
	private int[] m_colors;
	private int[] m_numbers;
	private int[] R;
	private int[] G;
	private int[] B;
	private int m_gesamt;
	private volatile boolean m_histoDone = false;
	private MyImage m_myImg;

	public Approximator() {

	}
	/*
	 * Vorgehen: Binäre Suche in allen Farbvektoren; Kleinester Abstand, für die
	 * Einstiegsstelle in den drei Vektoren (links und rechts prüfen) Kleinsten
	 * Abstand speichern! Ureinstiegspunkt merken für jeden Vektor, (r, g , b wert)
	 * 
	 * Loopen bis ureinstiegswert r +- stellenwert größer sind als max Abstand:
	 * Trennung zwischen ARRAYINDEX (Stellen) und ABSTAND r +- 2 stellen vom
	 * Einstiegspunkt g +-2 Stellen vom Einstiegspunkt b +- 2 Stellen vom
	 * Einstiegspunkt
	 * 
	 * für alle neue Abstandsberechnungen; rgb +- 3 Stellen wieder all Abstände
	 * berechnen, irgendwann hat man den kleinsten Abstand!
	 * 
	 * 
	 */

	/*
	 * hashmap: Color - array position
	 * 
	 * int[] colors - farben, position in numbers-array einsetzen, um anzahl
	 * herauszufinden int[] numbers - wie oft kommt eine farbe vor absteigend
	 * 
	 * R[] - alle farben sortiert nach rotanteil G[] - nach grünanteil B[] - nach
	 * blauanteil
	 * 
	 * und jetzt? jeden pixel durchgehen, farbe als key in hashmap einsetzen =>
	 * position im array ^ unnötigt, einfach binary search im colors-array? JOA
	 * 
	 * nicht drin, dann vorlesungsmethode ! Die da wäre:
	 * 
	 */
	public void prepareReduction(MyImage myImg) {
		if (myImg != m_myImg) {
			m_histoDone = false;
			m_myImg = myImg;
		}
		if (!m_histoDone) {
			m_myImg = myImg;
			// Colors -> Quantity
			createColorMap();

			m_gesamt = m_colorMap.size();
			m_colors = new int[m_gesamt];
			m_numbers = new int[m_gesamt];

			representMapWithArrays();
			// sort arrays by color quantity, mirror number-array changes to
			// color array
			quickSort(m_numbers, 0, m_numbers.length - 1);

			// recycles color map, to new map: color -> index in color-array for
			// faster searching (maybe just use binary search here?!)
			colorMapToPositionMap();
			m_histoDone = true;
		}		
	}
	public int getColorCount() {
		return m_gesamt;
	}
	
	public void reduceColors(int grenze) {

		fillColorArrays(grenze);
		sortColorArrays();
	
		HashMap<Integer, Integer> replaceMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < grenze; i++) {
			replaceMap.put(m_colors[i], m_colors[i]);
		}


		for (int i = grenze; i < m_gesamt; i++) {
			replaceMap.put(m_colors[i], getClosestColor(m_colors[i]));
//			System.out.println(Integer.toHexString(m_colors[i]) + " : " +  Integer.toHexString(getClosestColor(m_colors[i])));
		}
//		
//		System.out.println("REPLACEMAP");
//		for (Map.Entry<Integer, Integer> entry : replaceMap.entrySet()) {
//		    System.out.println(Integer.toHexString(entry.getKey()) + " : " + 
//		    Integer.toHexString(entry.getValue()));		    
//		}

		for (int i = 0; i < m_myImg.getCurrentPix().length; i++) {			
			m_myImg.getCurrentPix()[i] = replaceMap.get(m_myImg.getOriginalPix()[i]);		
		}

		m_myImg.newPixels();
		return;
	}


	class ApproxElement {
		private int m_colorToBeReplaced;
		private int m_newColor;
		private int m_startIndexRed;
		private int m_startIndexGreen;
		private int m_startIndexBlue;
		private int m_distance;
		public static final int RED = 0, GREEN = 1, BLUE = 2;

		public ApproxElement(int color) {
			m_colorToBeReplaced = color;
			m_newColor = color;
			m_distance = Integer.MAX_VALUE;
			m_startIndexRed = -1;
			m_startIndexGreen = -1;
			m_startIndexBlue = -1;
		}

		// index wird aufgerufen mit
		// testDistance(R, getStartIndexRed + offset);
		public int testDistance(int[] arr, int index, int shift) {			
			if (index > 0 && index < arr.length && isInDistance(arr[index], shift)) {				
				int tmpD = calcAbstand(arr[index], m_colorToBeReplaced);
				if (tmpD < m_distance) {
					m_distance = tmpD;
					m_newColor = arr[index];
				}
				return 1;
			}else {
				return 0;
			}
		}

		public boolean isInDistance(int checkColor, int shift) {
			return Math.abs(((checkColor >> shift) & 0xff) - ((m_colorToBeReplaced >> shift) & 0xff)) <= m_distance;
		}

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

		private int calcAbstand(int lrPoint, int col) {
			int a = ((lrPoint >> Model.SHIFT_RED) & 0xff) - ((col >> Model.SHIFT_RED) & 0xff);
			int b = ((lrPoint >> Model.SHIFT_GREEN) & 0xff) - ((col >> Model.SHIFT_GREEN) & 0xff);
			int c = ((lrPoint >> Model.SHIFT_BLUE) & 0xff) - ((col >> Model.SHIFT_BLUE) & 0xff);
			return (int) Math.sqrt(a * a + b * b + c * c);
		}

		public int getNewColor() {
			return m_newColor;
		}

	}

	private int getClosestColor(int oldC) {
		ApproxElement appo = new ApproxElement(oldC);

		// Setzte Startpunkte
		appo.setStartIndexRed(binSearch(R, oldC, Model.SHIFT_RED));
		appo.setStartIndexGreen(binSearch(G, oldC, Model.SHIFT_GREEN));
		appo.setStartIndexBlue(binSearch(B, oldC, Model.SHIFT_BLUE));

		int checks = 1;
		int offs = 0;
		while (checks > 0) {			
			checks = appo.testDistance(R, appo.getRedStartIndex() - offs, Model.SHIFT_RED) +
			appo.testDistance(R, appo.getRedStartIndex() + offs+1, Model.SHIFT_RED) +
			appo.testDistance(G, appo.getGreenStartIndex() - offs, Model.SHIFT_GREEN) +
			appo.testDistance(G, appo.getGreenStartIndex() + offs +1, Model.SHIFT_GREEN) +
			appo.testDistance(B, appo.getBlueStartIndex() - offs, Model.SHIFT_BLUE) +
			appo.testDistance(B, appo.getBlueStartIndex() + offs+1, Model.SHIFT_BLUE);
			offs++;			
		}
		return appo.getNewColor();
	}

	private int binSearch(int[] arr, int val, int shift) {
		int iL = 0;
		int iR = arr.length - 1;
		int MIDDLE = -1;
		while (iL <= iR) {
			MIDDLE = (iL + iR) / 2;
			final int RES = ((arr[MIDDLE] >> shift) & 0xff) - ((val >> shift) & 0xff);
			if (RES == 0) {
				break;

			} else if (RES < 0)
				iL = MIDDLE + 1;
			else
				iR = MIDDLE - 1;
		}

		if (MIDDLE < (arr.length - 1) / 2) {
			if (MIDDLE == 0)
				return MIDDLE;
			else
				return MIDDLE - 1;
		} else {
			if (MIDDLE == arr.length - 1)
				return MIDDLE - 1;
			else
				return MIDDLE;
		}

		// closest?
		// ist x nicht in der Menge vorhanden, dann
		// 1. hört die Suche bei einem Index i auf, an dem x
		// gestanden hätte, wenn es in M vorhanden gewesen
		// wäre
		// 2. wenn x < v[i] ist, dann vergleiche x mit v[i] und v[i-1]
		// 3. wenn x > v[i] ist, dann vergleiche x mit v[i] und v[i+1]
		// System.out.println(MIDDLE);
		// if (MIDDLE == 0 || MIDDLE == arr.length - 1)
		// return MIDDLE;
		//
		// if (val < arr[MIDDLE]) {
		// if ((arr[MIDDLE] - val) > (val - arr[MIDDLE - 1]))
		// return MIDDLE - 1;
		// } else if (val > arr[MIDDLE]) {
		// if ((val - arr[MIDDLE]) > (arr[MIDDLE + 1] - val))
		// return MIDDLE + 1;
		// }
		//
		// return MIDDLE;
	}

	private void createColorMap() {
		m_colorMap = new HashMap<Integer, Integer>();

		int[] pix = m_myImg.getCurrentPix();
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
			m_numbers[counter] = (Integer) m_colorMap.get(color);
			counter++;
		}
	}

	private void colorMapToPositionMap() {
		for (int i = 0; i < m_colors.length; i++) {
			m_colorMap.put(m_colors[i], i);
		}
	}

	private void fillColorArrays(int grenze) {
		R = new int[grenze];
		G = new int[grenze];
		B = new int[grenze];

		int counter = 0;
		for (int i = 0; i < grenze; ++i) {
			R[counter] = m_colors[counter];
			G[counter] = m_colors[counter];
			B[counter] = m_colors[counter];
			++counter;
		}
	}

	private void sortColorArrays() {
		quickSortColorArray(R, 0, R.length - 1, Model.SHIFT_RED);
		quickSortColorArray(G, 0, G.length - 1, Model.SHIFT_GREEN);
		quickSortColorArray(B, 0, B.length - 1, Model.SHIFT_BLUE);
	}

	public void clearHisto() {
		m_histoDone = false;
	}

	private void quickSort(int[] arr, int low, int high) {
		if (arr == null || arr.length == 0)
			return;

		if (low >= high)
			return;

		// pick the pivot
		int middle = low + (high - low) / 2;
		int pivot;

		pivot = arr[middle];

		// make left < pivot and right > pivot
		int i = low, j = high;
		while (i <= j) {

			while (arr[i] > pivot) {
				i++;
			}

			while (arr[j] < pivot) {
				j--;
			}

			if (i <= j) {
				int temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;

				temp = m_colors[i];
				m_colors[i] = m_colors[j];
				m_colors[j] = temp;

				i++;
				j--;
			}
		}

		// recursively sort two sub parts
		if (low < j)
			quickSort(arr, low, j);

		if (high > i)
			quickSort(arr, i, high);
	}

	private void quickSortColorArray(int[] arr, int low, int high, int shift) {
		if (arr == null || arr.length == 0)
			return;

		if (low >= high)
			return;

		// pick the pivot
		int middle = low + (high - low) / 2;
		int pivot;
		pivot = (arr[middle] >> shift) & 0xff;

		// make left < pivot and right > pivot
		int i = low, j = high;
		while (i <= j) {
			while (((arr[i] >> shift) & 0xff) > pivot) {
				i++;
			}

			while (((arr[j] >> shift) & 0xff) < pivot) {
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

		// recursively sort two sub parts
		if (low < j)
			quickSortColorArray(arr, low, j, shift);

		if (high > i)
			quickSortColorArray(arr, i, high, shift);
	}
}
