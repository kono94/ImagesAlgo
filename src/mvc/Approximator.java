package mvc;

import java.util.HashMap;
import java.util.Map;

public class Approximator {
	
	private Map<Integer, Integer> colorMap;
	private int[] colors;
	private int[] numbers;
	private int[] R;
	private int[] G;
	private int[] B;
	private int gesamt;
	private volatile boolean histoDone = false;
	private MyImage m_myImg;
	
	public Approximator() {
		
	}
	
	
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
	public void reduceColors(int percent, MyImage myImg) {
		if(myImg != m_myImg) {
			histoDone = false;
			m_myImg = myImg;
		}
		if (!histoDone) {
			colorMap = new HashMap<Integer, Integer>();

			int[] pix = myImg.getCurrentPix();
			for (int i = 0; i < pix.length; ++i) {
				if (!colorMap.containsKey(pix[i])) {
					colorMap.put(pix[i], 1);
				} else {
					colorMap.put(pix[i], (int) colorMap.get(pix[i]) + 1);
				}
			}

			gesamt = colorMap.size();
			colors = new int[gesamt];
			numbers = new int[gesamt];

			int counter = 0;
			System.err.println(gesamt);
			for (Integer color : colorMap.keySet()) {
				System.out.println(counter + " von " + gesamt);
				colors[counter] = color;
				numbers[counter] = (Integer) colorMap.get(color);
				counter++;
			}
			System.out.println("map erstellt.. quicksorting");
			quickSort(numbers, 0, numbers.length - 1, true, 0);
			System.out.println("ready");

			System.out.println("quicksort done, save new colorArray position in hashmap");
			for (int i = 0; i < colors.length; i++) {
				colorMap.put(colors[i], i);
			}
			System.out.println("done");
			histoDone = true;

		}
		//System.out.println("KEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEeeee");
		//System.out.println("Checke, welche farbe zu den TOP " + percent + " % gehören");
		int grenze = (gesamt * percent) / 100;
		R = new int[grenze];
		G = new int[grenze];
		B = new int[grenze];

		int counter = 0;
		for (int i = 0; i < grenze; ++i) {
			R[counter] = colors[counter];
			G[counter] = colors[counter];
			B[counter] = colors[counter];
			++counter;
		}

		quickSort(R, 0, R.length - 1, false, Model.SHIFT_RED);
		quickSort(G, 0, G.length - 1, false, Model.SHIFT_GREEN);
		quickSort(B, 0, B.length - 1, false, Model.SHIFT_BLUE);


		//System.out.println("GRENZE: " + grenze);
		int drin = 0;
		int raus = 0;
		double minAbstand;
		// m_CenterMyImg.getCurrentPix().length
		
		HashMap<Integer, Integer> replaceMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < grenze; i++) {
			replaceMap.put(colors[i], colors[i]);			
		}
		replaceMap.put(0, 0);
		for (int i = grenze; i < gesamt; i++) {			
			int replacingColor =0;
			replaceMap.put(colors[i], replacingColor);
		}
		for (int i = 0; i < myImg.getCurrentPix().length; i++) {
			int colorInPix = myImg.getOriginalPix()[i];			
				myImg.getCurrentPix()[i] = replaceMap.get(colorInPix);
			
		}
		myImg.newPixels();
		return;
		
//		for (int i = 0; i < m_CenterMyImg.getCurrentPix().length; i++) {
//			int colorInPix = m_CenterMyImg.getCurrentPix()[i];
//			if (colorMap.get(colorInPix) < grenze)
//				drin++;
//			else {
//				minAbstand = 2000000000;
//				int[] leftRightPoints;
//				double tmpAbs = 0;
//				leftRightPoints = binSearch(R, colorInPix, SHIFT_RED);
//				tmpAbs = calcAbstand(R[leftRightPoints[0]], colorInPix);
//				if (tmpAbs < minAbstand)
//					minAbstand = tmpAbs;
//
//				tmpAbs = calcAbstand(R[leftRightPoints[1]], colorInPix);
//				if (tmpAbs < minAbstand)
//					minAbstand = tmpAbs;
//
//				leftRightPoints = binSearch(G, colorInPix, SHIFT_GREEN);
//				tmpAbs = calcAbstand(G[leftRightPoints[0]], colorInPix);
//				if (tmpAbs < minAbstand)
//					minAbstand = tmpAbs;
//
//				tmpAbs = calcAbstand(G[leftRightPoints[1]], colorInPix);
//				if (tmpAbs < minAbstand)
//					minAbstand = tmpAbs;
//
//				leftRightPoints = binSearch(B, colorInPix, SHIFT_BLUE);
//				tmpAbs = calcAbstand(B[leftRightPoints[0]], colorInPix);
//				if (tmpAbs < minAbstand)
//					minAbstand = tmpAbs;
//
//				tmpAbs = calcAbstand(B[leftRightPoints[1]], colorInPix);
//				if (tmpAbs < minAbstand)
//					minAbstand = tmpAbs;
//
//				System.out.println(minAbstand);
//				raus++;
//
//			}
//		}
//		System.out.println("drin " + drin);
//		System.out.println("raus " + raus);

	}

	private double calcAbstand(int lrPoint, int col) {
		int a = ((lrPoint >> Model.SHIFT_RED) & 0xff) - ((col >> Model.SHIFT_RED) & 0xff);
		int b = ((lrPoint >> Model.SHIFT_GREEN) & 0xff) - ((col >> Model.SHIFT_GREEN) & 0xff);
		int c = ((lrPoint >> Model.SHIFT_BLUE) & 0xff) - ((col >> Model.SHIFT_BLUE) & 0xff);
		return Math.sqrt(a * a + b * b + c * c);
	}

	private int[] binSearch(int[] arr, int val, int shift) {
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
				return new int[] { MIDDLE, MIDDLE };
			else
				return new int[] { (MIDDLE - 1), MIDDLE };
		} else {
			if (MIDDLE == arr.length - 1)
				return new int[] { MIDDLE, MIDDLE };
			else
				return new int[] { MIDDLE, MIDDLE + 1 };
		}

		// closest?
		// ist x nicht in der Menge vorhanden, dann
		// 1. hört die Suche bei einem Index i auf, an dem x
		// gestanden hätte, wenn es in M vorhanden gewesen
		// wäre
		// 2. wenn x < v[i] ist, dann vergleiche x mit v[i] und v[i-1]
		// 3. wenn x > v[i] ist, dann vergleiche x mit v[i] und v[i+1]
		// System.out.println(MIDDLE);
		// if(MIDDLE == 0 || MIDDLE == arr.length -1)
		// return MIDDLE;
		//
		// if(val < arr[MIDDLE]) {
		// if((arr[MIDDLE] - val) > ( val - arr[MIDDLE -1]))
		// return MIDDLE - 1;
		// }else if(val > arr[MIDDLE]) {
		// if((val - arr[MIDDLE]) > ( arr[MIDDLE +1] - val))
		// return MIDDLE + 1;
		// }

	}

	private void quickSort(int[] arr, int low, int high, boolean reflect, int shift) {
		if (arr == null || arr.length == 0)
			return;

		if (low >= high)
			return;

		// pick the pivot
		int middle = low + (high - low) / 2;
		int pivot;
		if (reflect)
			pivot = arr[middle];
		else
			pivot = (arr[middle] >> shift) & 0xff;

		// make left < pivot and right > pivot
		int i = low, j = high;
		while (i <= j) {
			if (reflect) {
				while (arr[i] > pivot) {
					i++;
				}

				while (arr[j] < pivot) {
					j--;
				}
			} else {
				while (((arr[i] >> shift) & 0xff) > pivot) {
					i++;
				}

				while (((arr[j] >> shift) & 0xff) < pivot) {
					j--;
				}
			}

			if (i <= j) {
				int temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
				if (reflect) {
					temp = colors[i];
					colors[i] = colors[j];
					colors[j] = temp;
				}

				i++;
				j--;
			}
		}

		// recursively sort two sub parts
		if (low < j)
			quickSort(arr, low, j, reflect, shift);

		if (high > i)
			quickSort(arr, i, high, reflect, shift);
	}
	
	public void clearHisto() {
		histoDone = false;
	}
}
