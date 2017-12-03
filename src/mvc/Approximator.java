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
	 * Abstand speichern! Ureinstiegspunkt merken für jeden Vektor, (r, g , b
	 * wert)
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
	 * R[] - alle farben sortiert nach rotanteil G[] - nach grünanteil B[] -
	 * nach blauanteil
	 * 
	 * und jetzt? jeden pixel durchgehen, farbe als key in hashmap einsetzen =>
	 * position im array ^ unnötigt, einfach binary search im colors-array? JOA
	 * 
	 * nicht drin, dann vorlesungsmethode ! Die da wäre:
	 * 
	 */
	public void reduceColors(int percent, MyImage myImg) {
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

		int grenze = (m_gesamt * percent) / 100;

		fillColorArrays(grenze);
		sortColorArrays();

		HashMap<Integer, Integer> replaceMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < grenze; i++) {
			replaceMap.put(m_colors[i], m_colors[i]);
		}
		replaceMap.put(0, 0);

		for (int i = grenze; i < m_gesamt; i++) {
			
			replaceMap.put(m_colors[i], getClosestColor(m_colors[i]));
		}

		for (int i = 0; i < myImg.getCurrentPix().length; i++) {
			int colorInPix = myImg.getOriginalPix()[i];
			if (replaceMap.get(colorInPix) != null)
				myImg.getCurrentPix()[i] = replaceMap.get(colorInPix);

		}

		myImg.newPixels();
		return;

	}

	private int getClosestColor(int oldC) {
		int minAbstand = Integer.MAX_VALUE;
		int newC = 0;
	
			int colorInPix = oldC;

			int[] leftRightPoints;
			int tmpAbs = minAbstand;
			leftRightPoints = binSearch(R, colorInPix, Model.SHIFT_RED);
			int startPointRed = leftRightPoints[0];
			tmpAbs = calcAbstand(R[leftRightPoints[0]], colorInPix);
			if (tmpAbs < minAbstand) {
				minAbstand = tmpAbs;
				newC = R[leftRightPoints[0]];
			}
			tmpAbs = calcAbstand(R[leftRightPoints[1]], colorInPix);
			if (tmpAbs < minAbstand) {
				minAbstand = tmpAbs;
				newC = R[leftRightPoints[1]];
			}

			leftRightPoints = binSearch(G, colorInPix, Model.SHIFT_GREEN);
			int startPointGreen = leftRightPoints[0];
			tmpAbs = calcAbstand(G[leftRightPoints[0]], colorInPix);
			if (tmpAbs < minAbstand) {
				minAbstand = tmpAbs;
				newC = G[leftRightPoints[0]];
			}
			tmpAbs = calcAbstand(G[leftRightPoints[1]], colorInPix);
			if (tmpAbs < minAbstand) {
				minAbstand = tmpAbs;
				newC = G[leftRightPoints[1]];
			}
			leftRightPoints = binSearch(B, colorInPix, Model.SHIFT_BLUE);
			int startPointBlue = leftRightPoints[0];
			tmpAbs = calcAbstand(B[leftRightPoints[0]], colorInPix);
			if (tmpAbs < minAbstand) {
				minAbstand = tmpAbs;
				newC = B[leftRightPoints[0]];
			}
			tmpAbs = calcAbstand(B[leftRightPoints[1]], colorInPix);
			if (tmpAbs < minAbstand) {
				minAbstand = tmpAbs;
				newC = B[leftRightPoints[1]];
			}		

			int minR = ((colorInPix >> Model.SHIFT_RED) & 0xff) - minAbstand;
			int maxR = ((colorInPix >> Model.SHIFT_RED) & 0xff) + minAbstand;			

			// Suchraum weiter einschränken;
			// ein nach links und rechts in jedem vektor, ++ nach jedem
			// iterationsschritt
			int spannweite = 1;
			boolean leftDone, rightDone;
			leftDone = rightDone = false;
			// RED
			while (!leftDone && !rightDone) {
				if ( rightDone && startPointRed + spannweite < R.length && ((R[startPointRed + spannweite] >> Model.SHIFT_RED) & 0xff) < maxR) {
					tmpAbs = calcAbstand(R[startPointRed + spannweite], colorInPix);
					if (tmpAbs < minAbstand) {
						minAbstand = tmpAbs;
						newC = R[startPointRed + spannweite];
					}
				}else{
					rightDone = true;
				}
				
				if (!leftDone && startPointRed - spannweite > -1 && ((R[startPointRed - spannweite] >> Model.SHIFT_RED) & 0xff) > minR) {
					tmpAbs = calcAbstand(R[startPointRed - spannweite], colorInPix);
					if (tmpAbs < minAbstand) {
						minAbstand = tmpAbs;
						newC = R[startPointRed - spannweite];
					}
				}else{
					leftDone = true;
				}
				++spannweite;
			}

			
			int minG = ((colorInPix >> Model.SHIFT_GREEN) & 0xff) - minAbstand;
			int maxG = ((colorInPix >> Model.SHIFT_GREEN) & 0xff) + minAbstand;
			leftDone = rightDone = false;

			// GREEN
			while (!leftDone && !rightDone) {
				if ( rightDone && startPointGreen + spannweite < G.length && ((G[startPointRed + spannweite] >> Model.SHIFT_GREEN) & 0xff) < maxG) {
					tmpAbs = calcAbstand(G[startPointGreen + spannweite], colorInPix);
					if (tmpAbs < minAbstand) {
						minAbstand = tmpAbs;
						newC = G[startPointRed + spannweite];
					}
				}else{
					rightDone = true;
				}
				
				if (!leftDone && startPointGreen - spannweite > -1 && ((G[startPointGreen - spannweite] >> Model.SHIFT_GREEN) & 0xff) > minG) {
					tmpAbs = calcAbstand(G[startPointGreen - spannweite], colorInPix);
					if (tmpAbs < minAbstand) {
						minAbstand = tmpAbs;
						newC = G[startPointGreen - spannweite];
					}
				}else{
					leftDone = true;
				}
				++spannweite;
			}
			
			
			int minB = ((colorInPix >> Model.SHIFT_BLUE) & 0xff) - minAbstand;
			int maxB = ((colorInPix >> Model.SHIFT_BLUE) & 0xff) + minAbstand;
			leftDone = rightDone = false;

			// BLUE
			while (!leftDone && !rightDone) {
				if ( rightDone && startPointBlue + spannweite < B.length && ((G[startPointBlue + spannweite] >> Model.SHIFT_BLUE) & 0xff) < maxB) {
					tmpAbs = calcAbstand(B[startPointBlue + spannweite], colorInPix);
					if (tmpAbs < minAbstand) {
						minAbstand = tmpAbs;
						newC = B[startPointBlue + spannweite];
					}
				}else{
					rightDone = true;
				}
				
				if (!leftDone && startPointBlue - spannweite > -1 && ((B[startPointBlue - spannweite] >> Model.SHIFT_BLUE) & 0xff) > minB) {
					tmpAbs = calcAbstand(B[startPointBlue - spannweite], colorInPix);
					if (tmpAbs < minAbstand) {
						minAbstand = tmpAbs;
						newC = B[startPointBlue - spannweite];
					}
				}else{
					leftDone = true;
				}
				++spannweite;
			}
			
		
		return newC;
	}

	private int calcAbstand(int lrPoint, int col) {
		int a = ((lrPoint >> Model.SHIFT_RED) & 0xff) - ((col >> Model.SHIFT_RED) & 0xff);
		int b = ((lrPoint >> Model.SHIFT_GREEN) & 0xff) - ((col >> Model.SHIFT_GREEN) & 0xff);
		int c = ((lrPoint >> Model.SHIFT_BLUE) & 0xff) - ((col >> Model.SHIFT_BLUE) & 0xff);
		return (int) Math.sqrt(a * a + b * b + c * c);
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
