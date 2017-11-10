package mvc;

import java.awt.List;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.SwingUtilities;

import mvc.View.CenterImageComponent;

public class Model {
	// Vector in which every component is saved, to loop through and
	// look for selected ones to display them in the center
	private Vector<MyImage> m_VecAllMyImages;
	public Point m_StartPoint = new Point(-1, -1);
	public Point m_EndPoint = new Point(-1, -1);
	private MyImage m_WorkingLayerMyImage;
	private MyImage m_CenterMyImg;
	private boolean m_readyToMerge = false;
	private volatile boolean m_randomColors;
	private int m_currentCircleRadius;
	private int m_bT = 0;
	private int m_Color1;
	private int m_Color2;
	private double m_currentRotation;
	public static int MOVE_TOP = 1, MOVE_RIGHT = 2, MOVE_BOTTOM = 3, MOVE_LEFT = 4, SHEARX = 5, SHEARY = 6,
			SCALE_BIGGER = 7, SCALE_SMALLER = 8, ROTATE_LEFT = 9, ROTATE_RIGHT = 10;
	public int m_currentGradient;
	public static int LEFT_TO_RIGHT_GRADIENT = 0, MIDDLE_TO_OUTSIDE_GRADIENT = 1;
	public Model() {
		
		m_VecAllMyImages = new Vector<MyImage>(5, 0);
		m_currentGradient = MIDDLE_TO_OUTSIDE_GRADIENT;
		m_Color1 = 0x00FF33;
		m_Color2 = 0x33234C;
	}

	public void addImage(MyImage img) {
		m_VecAllMyImages.addElement(img);
	}

	public void manageIconAction(int action, MouseEvent e) {
		if (Mode.currentMode == Mode.SELECT && m_EndPoint.x != -1) {
			if (!isMergeReady()) {
				cutOut(m_CenterMyImg);
			}
			if (action == MOVE_TOP) {
				translateSelection(0, -50);
			} else if (action == MOVE_RIGHT) {
				translateSelection(20, 0);
			} else if (action == MOVE_BOTTOM) {
				translateSelection(0, 20);
			} else if (action == MOVE_LEFT) {
				translateSelection(-20, 0);
			} else if (action == SCALE_BIGGER) {
				scaleSelection(1.1, 1.1);
			} else if (action == SCALE_SMALLER) {
				scaleSelection(0.9, 0.9);
			} else if (action == SHEARX) {
				if (SwingUtilities.isRightMouseButton(e))
					shearXSelection(-0.05);
				else
					shearXSelection(0.05);
			} else if (action == SHEARY) {
				if (SwingUtilities.isRightMouseButton(e))
					shearYSelection(-0.05);
				else
					shearYSelection(0.05);
			} else if (action == ROTATE_LEFT) {
				rotateSelection(3);
			} else if (action == ROTATE_RIGHT) {
				rotateSelection(-3);
			}
		} else {
			clearWorkingLayerAndPoints();
			if (action == MOVE_TOP) {
				translate(m_CenterMyImg, 0, -20);
			} else if (action == MOVE_RIGHT) {
				translate(m_CenterMyImg, 20, 0);
			} else if (action == MOVE_BOTTOM) {
				translate(m_CenterMyImg, 0, 20);
			} else if (action == MOVE_LEFT) {
				translate(m_CenterMyImg, -20, 0);
			} else if (action == SCALE_BIGGER) {
				if(m_CenterMyImg == null) System.out.println("leeer");
				scale(m_CenterMyImg, 1.1, 1.1);
			} else if (action == SCALE_SMALLER) {
				scale(m_CenterMyImg, 0.9, 0.9);
			} else if (action == SHEARX) {
				if (SwingUtilities.isRightMouseButton(e))
					shearX(m_CenterMyImg, -0.05);
				else
					shearX(m_CenterMyImg, 0.05);
			} else if (action == SHEARY) {
				if (SwingUtilities.isRightMouseButton(e))
					shearY(m_CenterMyImg, -0.05);
				else
					shearY(m_CenterMyImg, 0.05);
			} else if (action == ROTATE_LEFT) {
				rotate(m_CenterMyImg, 3, true);
			} else if (action == ROTATE_RIGHT) {
				rotate(m_CenterMyImg, -3, true);
			}
		}
	
	}

	public void morph(Matrix m, MyImage myImg) {
		
		m = Matrix.multiply(myImg.getMatrix(),m);
		myImg.setMatrix(m);

		for (int x = 0; x < MyImage.IMG_WIDTH; ++x) {
			for (int y = 0; y < MyImage.IMG_HEIGHT; ++y) {
				ThreeDimVector vXY = new ThreeDimVector(x, y);
				ThreeDimVector vSrc = Matrix.multiplyWithVector(m, vXY);
				int posInOriginal = vSrc.getY() * MyImage.IMG_WIDTH + vSrc.getX();
				if (0 <= vSrc.getY() && vSrc.getY() < MyImage.IMG_HEIGHT && 0 <= vSrc.getX()
						&& vSrc.getX() < MyImage.IMG_WIDTH) {
					myImg.getCurrentPix()[y * MyImage.IMG_WIDTH + x] = myImg.getOriginalPix()[posInOriginal];

				} else {

					myImg.getCurrentPix()[y * MyImage.IMG_WIDTH + x] = 0xffffffff;
					// m_VecAllMyImages.get(0)
					// .getOriginalPix()[y * MyImage.IMG_WIDTH + x];
				}
			}
		}
		myImg.newPixels();

	}

	public void morphSelection(Matrix m) {
		int[] purePoints = calcPurePointsSelection();
		int x1 = purePoints[0];
		int y1 = purePoints[1];
		int x2 = purePoints[2];
		int y2 = purePoints[3];

		m = Matrix.multiply(m, m_WorkingLayerMyImage.getMatrix());
		m_WorkingLayerMyImage.setMatrix(m);

		for (int x = 0; x < MyImage.IMG_WIDTH; ++x) {
			for (int y = 0; y < MyImage.IMG_HEIGHT; ++y) {
				ThreeDimVector vXY = new ThreeDimVector(x, y);
				ThreeDimVector vSrc = Matrix.multiplyWithVector(m, vXY);

				int posInOriginal = vSrc.getY() * MyImage.IMG_WIDTH + vSrc.getX();

				if (vSrc.getX() >= x1 - m_bT && vSrc.getX() <= x2 + m_bT && vSrc.getY() >= y1 - m_bT
						&& vSrc.getY() <= y2 + m_bT) {
					m_WorkingLayerMyImage.getCurrentPix()[y * MyImage.IMG_WIDTH + x] = m_WorkingLayerMyImage
							.getOriginalPix()[posInOriginal];

				} else {
					m_WorkingLayerMyImage.getCurrentPix()[y * MyImage.IMG_WIDTH + x] = 0;
				}
			}
		}
		m_WorkingLayerMyImage.newPixels();

	}

	public void cutOut(MyImage cImg) {
		m_readyToMerge = true;
		System.out.println("cut");
		int[] purePoints = calcPurePointsSelection();
		int x1 = purePoints[0];
		int y1 = purePoints[1];
		int x2 = purePoints[2];
		int y2 = purePoints[3];

		if (m_StartPoint.x != -1 && m_EndPoint.x != -1) {
			for (int x = 0; x < MyImage.IMG_WIDTH; ++x) {
				for (int y = 0; y < MyImage.IMG_HEIGHT; ++y) {
					if (x >= x1 && x <= x2 && y >= y1 && y <= y2) {
						m_WorkingLayerMyImage.getOriginalPix()[y * MyImage.IMG_WIDTH + x] = cImg
								.getCurrentPix()[y * MyImage.IMG_WIDTH + x];
						cImg.getOriginalPix()[y * MyImage.IMG_WIDTH + x] = 0;
						cImg.getCurrentPix()[y * MyImage.IMG_WIDTH + x] = 0xff000000;
					} else if ((x >= x1 - m_bT && x < x2 + m_bT && y >= y1 - m_bT && y < y1)
							|| (((x >= x1 - m_bT && x < x1) || (x > x2 && x <= x2 + m_bT)) && (y >= y1 && y < y2))
							|| (x >= x1 - m_bT && x < x2 + m_bT && y <= y2 + m_bT && y > y2)) {
						m_WorkingLayerMyImage.getOriginalPix()[y * MyImage.IMG_WIDTH + x] = 0xff882288;
					} else {
						m_WorkingLayerMyImage.getOriginalPix()[y * MyImage.IMG_WIDTH + x] = 0;

					}

				}
			}
			m_WorkingLayerMyImage.setCurrentPix(m_WorkingLayerMyImage.getOriginalPix());
			;
			m_WorkingLayerMyImage.newPixels();
			cImg.newPixels();
		} else {
			System.out.println("cutOut failed");
		}

	}

	public void cutOut() {
		cutOut(m_CenterMyImg);
	}

	public void translate(MyImage myImg, int h, int v) {
		myImg.changeCenterPoint(new Point(h,v));
		Matrix transM = Matrix.inverseTranslation(h,v);
		// Matrix transM = Matrix.inverseXShearing(-3.4);
		morph(transM, myImg);
	}

	public void rotate(MyImage myImg, double alpha, boolean spinAroundMiddle) {
		alpha = Math.toRadians(alpha);
		Matrix rotateM;
		if (!spinAroundMiddle) {
			rotateM = Matrix.inverseRotation(alpha);
		} else {
			Matrix toTopLeftM = Matrix.inverseTranslation(myImg.getCenterPoint().x, myImg.getCenterPoint().y);
			Matrix spinM = Matrix.inverseRotation(alpha);
			Matrix backM = Matrix.inverseTranslation(-myImg.getCenterPoint().x, -myImg.getCenterPoint().y);
			rotateM = Matrix.multiply(backM, (Matrix.multiply(spinM, toTopLeftM)));
		}
	
		morph(rotateM, myImg);
	}

	public void shearX(MyImage myImg, double shX) {
		Matrix shearM = Matrix.inverseXShearing(shX);
		morph(shearM, myImg);
	}

	public void shearY(MyImage myImg, double shY) {
		Matrix shearM = Matrix.inverseYShearing(shY);
		morph(shearM, myImg);
	}

	public void scale(MyImage myImg, double scaleX, double scaleY) {
		Matrix toTopLeftM = Matrix.inverseTranslation(-MyImage.IMG_WIDTH / 2, -MyImage.IMG_HEIGHT / 2);
		Matrix backM = Matrix.inverseTranslation(MyImage.IMG_WIDTH / 2, MyImage.IMG_HEIGHT / 2);
		Matrix scaleM = Matrix.inverserScaling(scaleX, scaleY);
		Matrix all = Matrix.multiply((Matrix.multiply(toTopLeftM, scaleM)), backM);
		morph(all, myImg);
		
		
		//
		
		
		
		// BILD AUS HISTORGRAMM ERSTELLEN IN FARBREIHENFOLGE
		
		
		
		//
	}

	public void shearXY(MyImage myImg, double shX, double shY) {
		Matrix shearXY = Matrix.multiply(Matrix.inverseXShearing(shX), Matrix.inverseYShearing(shY));
		morph(shearXY, myImg);
	}

	public void translateSelection(int h, int v) {
			
			Matrix transM = Matrix.inverseTranslation(h, v);
	//		ThreeDimVector oldSP = new ThreeDimVector(m_StartPoint.x, m_StartPoint.y);
	//		ThreeDimVector newSP = new ThreeDimVector(m_EndPoint.x, m_EndPoint.y);
	//		ThreeDimVector tmp1 = Matrix.multiplyWithVector(transM, oldSP);
	//		ThreeDimVector tmp2 = Matrix.multiplyWithVector(transM, newSP);
	//		m_StartPoint.x = tmp1.getX();
	//		m_StartPoint.x = tmp1.getY();
	//		m_EndPoint.x = tmp2.getX();
	//		m_EndPoint.y = tmp2.getY();
			morphSelection(transM);
		}

	public void scaleSelection(double xF, double yF) {
		int[] purePoints = calcPurePointsSelection();
		int x1 = purePoints[0];
		int y1 = purePoints[1];
		int x2 = purePoints[2];
		int y2 = purePoints[3];
	
		Matrix toTopLeftM = Matrix.inverseTranslation(-(x1 + (x2 - x1) / 2), -(y1 + (y2 - y1) / 2));
		Matrix scale = Matrix.inverserScaling(xF, yF);
		Matrix backM = Matrix.inverseTranslation((x1 + (x2 - x1) / 2), (y1 + (y2 - y1) / 2));
		Matrix all = Matrix.multiply(Matrix.multiply(toTopLeftM, scale), backM);
		morphSelection(all);
	}

	public void rotateSelection(double alpha) {
		alpha = Math.toRadians(alpha);
		m_currentRotation = (m_currentRotation + alpha)%(Math.PI*2);
		int[] purePoints = calcPurePointsSelection();
		int x1 = purePoints[0];
		int y1 = purePoints[1];
		int x2 = purePoints[2];
		int y2 = purePoints[3];
	
		Matrix toTopLeftM = Matrix.inverseTranslation(+(x1 + (x2 - x1) / 2), +(y1 + (y2 - y1) / 2));
		Matrix spinM = Matrix.inverseRotation(alpha);
		Matrix backM = Matrix.inverseTranslation(-(x1 + (x2 - x1) / 2), -(y1 + (y2 - y1) / 2));
		Matrix rotateM = Matrix.multiply(backM , (Matrix.multiply(spinM, toTopLeftM)));
		morphSelection(rotateM);
	}

	public void shearXSelection(double shX) {
		int[] purePoints = calcPurePointsSelection();
		int x1 = purePoints[0];
		int y1 = purePoints[1];
		int x2 = purePoints[2];
		int y2 = purePoints[3];
	
		// Pulls in both directions
		// Matrix toTopLeftM = Matrix.inverseTranslation(-(x1 + (x2 - x1) / 2), -(y1 +
		// (y2 - y1) / 2));
		// Matrix shearX = Matrix.inverseXShearing(shX);
		// Matrix backM = Matrix.inverseTranslation((x1 + (x2 - x1) / 2), (y1 + (y2 -
		// y1) / 2));
		//
		Matrix toTopLeftM = Matrix.inverseTranslation(-x1, -y1);
		Matrix shearX = Matrix.inverseXShearing(shX);
		Matrix backM = Matrix.inverseTranslation(x1, y1);
		Matrix shearM = Matrix.multiply((Matrix.multiply(toTopLeftM, shearX)), backM);
		morphSelection(shearM);
	}

	public void shearYSelection(double shY) {
		int[] purePoints = calcPurePointsSelection();
		int x1 = purePoints[0];
		int y1 = purePoints[1];
		int x2 = purePoints[2];
		int y2 = purePoints[3];
	
		Matrix toTopLeftM = Matrix.inverseTranslation((x1 + (x2 - x1) / 2), (y1 + (y2 - y1) / 2));
		Matrix shearY = Matrix.inverseYShearing(shY);
		Matrix backM = Matrix.inverseTranslation(-(x1 + (x2 - x1) / 2), -(y1 + (y2 - y1) / 2));
		Matrix shearM = Matrix.multiply(backM, (Matrix.multiply(shearY,toTopLeftM)));
		morphSelection(shearM);
	}

	public void scaleOnPoint(Point p, double factor) {
		if (Mode.currentMode == Mode.SELECT && m_EndPoint.x != -1) {
			if (!isMergeReady()) {
				cutOut(m_CenterMyImg);
			}
		}
		Matrix move = Matrix.inverseTranslation(-p.x, -p.y);
		Matrix scale = Matrix.inverserScaling(factor, factor);
		Matrix back = Matrix.inverseTranslation(p.x, p.y);
		Matrix all = Matrix.multiply(Matrix.multiply(move, scale), back);
		morph(all, m_CenterMyImg);
		// morph(all, m_WorkingLayerMyImage);
	}

	public void setCenterMyImage(MyImage image) {
		m_CenterMyImg = image;
	}

	public void useRandomColors(boolean b) {
		m_randomColors = b;
	}

	public void clearWorkingLayerAndPoints() {
		System.out.println("resetet");
		if (m_readyToMerge) {
			mergeWorkingLayer(m_CenterMyImg);
		}
		m_readyToMerge = false;
		m_StartPoint.x = -1;
		m_EndPoint.x = -1;
		clearAllPix(m_WorkingLayerMyImage.getCurrentPix());
		m_WorkingLayerMyImage.fullReset();
		m_WorkingLayerMyImage.newPixels();
	}

	public void mergeWorkingLayer(MyImage image) {
		System.err.println("merge");
		for (int i = 0; i < image.getCurrentPix().length; i++) {
			if (m_WorkingLayerMyImage.getCurrentPix()[i] != 0) {
				image.getCurrentPix()[i] = m_WorkingLayerMyImage.getCurrentPix()[i];
				// image.getOriginalPix()[i] = m_WorkingLayerMyImage.getCurrentPix()[i];
			}
		}
		image.CurrentToOriginal();
		image.newPixels();
		image.resetHistoryMatrix();
	}

	public int[] calcPurePointsSelection() {
		
		int[] purePoints = new int[4];
		purePoints[0] = m_StartPoint.x < m_EndPoint.x ? m_StartPoint.x : m_EndPoint.x; // x1
		purePoints[1] = m_StartPoint.y < m_EndPoint.y ? m_StartPoint.y : m_EndPoint.y; // y1
		purePoints[2] = purePoints[0] + Math.abs(m_EndPoint.x - m_StartPoint.x); // x2
		purePoints[3] = purePoints[1] + Math.abs(m_EndPoint.y - m_StartPoint.y); // y2

		return purePoints;
	}
	public void drawSelection() {
		int[] purePoints = calcPurePointsSelection();
		int x1 = purePoints[0];
		int y1 = purePoints[1];
		int x2 = purePoints[2];
		int y2 = purePoints[3];

		clearAllPix(m_WorkingLayerMyImage.getCurrentPix());

		for (int x = x1; x < x2 + 1; ++x) {
			for (int y = y1; y < y2 + 1; ++y) {
				if (x < x1 || y < y1 || x > x2 || y > y2)
					m_WorkingLayerMyImage.getCurrentPix()[y * MyImage.IMG_WIDTH + x] = 0xff882288;
				else
					m_WorkingLayerMyImage.getCurrentPix()[y * MyImage.IMG_WIDTH + x] = 0x99222299;
			}
		}
		m_WorkingLayerMyImage.newPixels();
	}

	public void drawLine() {
		int x1 = m_StartPoint.x;
		int y1 = m_StartPoint.y;
		int x2 = m_EndPoint.x;
		int y2 = m_EndPoint.y;

		clearAllPix(m_WorkingLayerMyImage.getCurrentPix());

		try {
			drawLineInArr(m_WorkingLayerMyImage.getCurrentPix(), x1, y1, x2, y2);
			m_readyToMerge = true;

		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("ERROROROROR");
			System.out.println(m_EndPoint);
		}
		m_WorkingLayerMyImage.newPixels();
	}

	public void generateRandomColors() {
		m_Color1 = generateRandomInt(0x8fffffff, 0x7fffffff);
		m_Color2 = generateRandomInt(0x8fffffff, 0x7fffffff);

	}

	public void drawCircle() {
		m_readyToMerge = true;
		clearAllPix(m_WorkingLayerMyImage.getCurrentPix());
		m_currentCircleRadius = (int) Math.hypot((m_StartPoint.x - m_EndPoint.x), (m_StartPoint.y - m_EndPoint.y));
		drawCircle(m_WorkingLayerMyImage.getCurrentPix(), m_StartPoint.x, m_StartPoint.y, m_currentCircleRadius);
		m_WorkingLayerMyImage.newPixels();
	}

	public void clearAllPix(int[] pix) {
		for (int i = 0; i < pix.length; i++) {
			pix[i] = 0;
		}
	}

	public void drawLineInArr(int[] pix, int x0, int y0, int x1, int y1) {
		final int dx = Math.abs(x0 - x1);
		final int dy = Math.abs(y0 - y1);
		final int sgnDx = x0 < x1 ? 1 : -1;
		final int sgnDy = y0 < y1 ? 1 : -1;
		int shortD, longD, incXshort, incXlong, incYshort, incYlong;
		if (dx > dy) {
			shortD = dy;
			longD = dx;
			incXlong = sgnDx;
			incXshort = 0;
			incYlong = 0;
			incYshort = sgnDy;
		} else {
			shortD = dx;
			longD = dy;
			incXlong = 0;
			incXshort = sgnDx;
			incYlong = sgnDy;
			incYshort = 0;
		}
		int d = longD / 2, x = x0, y = y0;
		for (int i = 0; i <= longD; ++i) {
			setPixelInArr(pix, x, y);
			x += incXlong;
			y += incYlong;
			d += shortD;
			if (d >= longD) {
				d -= longD;
				x += incXshort;
				y += incYshort;
			}
		}
	}

	public void drawCircle(int[] pix, int x0, int y0, int r) {
		int y = 0;
		int x = r;
		int F = -r;
		int dy = 1;
		int dyx = -2 * r + 3;
		while (y <= x) {
			if (Mode.currentMode == Mode.CIRCLE) {
				setPixelInArrCircle(pix, x0, y0, x, y);
			}
			if (Mode.currentMode == Mode.FILLED_CIRCLE) {
				setPixelInArrFilledCircle(pix, x0, y0, x, y);
			}
			++y;
			dy += 2;
			dyx += 2;
			if (F > 0) {
				F += dyx;
				--x;
				dyx += 2;
			} else {
				F += dy;
			}
		}
	}

	public void shuffle(int[] pixelImg1, int[] pixelImg2, int p) {
		for (int i = 0; i < (MyImage.IMG_WIDTH * MyImage.IMG_HEIGHT - 1); ++i) {
			m_WorkingLayerMyImage.getCurrentPix()[i] = compPix(pixelImg1[i], pixelImg2[i], p);
		}
		// m_fadingMIS.newPixels();
		m_WorkingLayerMyImage.newPixels();
	}

	private int compColor(int x1, int x2, int p) {
		return x1 + (x2 - x1) * p / 100;
	}

	private int compPix(int pix1, int pix2, int p) {
		final int RED = compColor((pix1 >> 16) & 0xff, (pix2 >> 16) & 0xff, p);
		final int GREEN = compColor((pix1 >> 8) & 0xff, (pix2 >> 8) & 0xff, p);
		final int BLUE = compColor(pix1 & 0xff, pix2 & 0xff, p);
		return 0xff000000 | (RED << 16) | (GREEN << 8) | BLUE;
	}

	public int calcColor(int x, int y) {
		int percent = 0;
		if (Mode.currentMode == Mode.CIRCLE ||   Mode.currentMode == Mode.FILLED_CIRCLE) {
			if (m_currentGradient == LEFT_TO_RIGHT_GRADIENT) {
				double w = m_currentCircleRadius * 2;
				percent = (int) (((x - m_StartPoint.x + m_currentCircleRadius) / w) * 100);
			} else if (m_currentGradient == MIDDLE_TO_OUTSIDE_GRADIENT) {
				int xDiff = x - m_StartPoint.x;
				int yDiff = y - m_StartPoint.y;
				percent = (int) ((Math.hypot(xDiff, yDiff) / m_currentCircleRadius) * 100);
			}
		} else if (Mode.currentMode == Mode.LINE) {

			double w = Math.abs(m_StartPoint.x - m_EndPoint.x);
			percent = (int) ((Math.abs(x - m_StartPoint.x) / w) * 100);
		} else {
			return 0xff000000;
		}
		return compPix(m_Color1, m_Color2, percent);

	}

	public void createWorkingImage(CenterImageComponent center) {
		m_WorkingLayerMyImage = new MyImage(center);
		ImageFromHisto();
	}

	public boolean createHistogramm(MyImage img) {
		PrintWriter writer;
		try {
			writer = new PrintWriter("Histogramm.txt", "UTF-8");
			TreeMap<Integer, Integer> colorMap = new TreeMap<Integer, Integer>();
	
			int[] pix = img.getCurrentPix();
			for (int i = 0; i < pix.length; ++i) {
				if (!colorMap.containsKey(pix[i])) {
					colorMap.put(pix[i], 1);
				} else {
					colorMap.put(pix[i], (int) colorMap.get(pix[i]) + 1);
				}
			}
			//Map<Integer,Integer> = new TreeMap(colorMap);
			for (Integer color : colorMap.keySet()) {
				writer.println(Integer.toHexString(color) + ": " + (Integer) colorMap.get(color));
			}
	
			writer.close();
			return true;
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public void ImageFromHisto() {
		try {
		BufferedReader in = new BufferedReader(new FileReader("./Histogramm.txt"));
		String str;
	
		ArrayList<String> list = new ArrayList<String>();
		while((str = in.readLine()) != null){
		    list.add(str);
		}
		
		String[] stringArr = list.toArray(new String[0]);
		System.out.println(stringArr.length);
		int pos = 0;
		for (int i = 0; i < stringArr.length; i++) {
			//System.out.println(stringArr[i].substring(stringArr[i].indexOf(" ") +1, stringArr[i].length()));
		 int  howOften = Integer.parseInt(stringArr[i].substring(stringArr[i].indexOf(" ")+1, stringArr[i].length()));
	//		int howOften =0;
			for(int k =0; k < howOften; ++k) {
				//System.out.println((int) Long.parseLong(stringArr[i].substring(0, stringArr[i].indexOf(":")),16));
			m_CenterMyImg.getCurrentPix()[pos] = (int) Long.parseLong(stringArr[i].substring(0, stringArr[i].indexOf(":")),16);
				//m_CenterMyImg.getCurrentPix()[pos] = 0xffffffff;
				pos++;
			}
			
			m_CenterMyImg.newPixels();
		}
		}catch(Exception e){
			System.out.println("no file");
			e.printStackTrace();
		}
	}

	public boolean isMergeReady() {
		return m_readyToMerge;
	}

	public boolean isUsingRandomColors() {
		return m_randomColors;
	}

	private int generateRandomInt(int min, int max) {
		return (int) (Math.random() * (max - min) + min);
	}

	private double generateRandomDouble(double min, double max) {
		return ThreadLocalRandom.current().nextDouble(min, max);
	}

	private boolean generateBoolean() {
		return Math.random() > 0.5 ? false : true;
	}

	public MyImage getWorkingMyImage() {
		return m_WorkingLayerMyImage;
	}

	public Vector<MyImage> getMyImageVector() {
		return m_VecAllMyImages;
	}

	public int[] getRandomValuesForTranslation() {
		int h = generateRandomInt(-200, 200);
		int v = generateRandomInt(-150, 150);
		int[] tmp = { h, v };
		return tmp;
	}

	public double getRandomValueForRotation() {
		return generateRandomDouble(-1, 1);
	}

	public double getRandomValueForXShearing() {
		return generateRandomDouble(-1, 1);
	}

	public double getRandomValueForYShearing() {
		return generateRandomDouble(-1, 1);
	}

	public double getRandomValueForScaling() {
		// generate Number between 0.5 - 0.9 OR 1.1 - 1.5;
		return generateBoolean() ? 1 - generateRandomDouble(0.1, 0.5) : 1 + generateRandomDouble(0.1, 0.5);
	}

	public int getColor1() {
		return m_Color1;
	}

	public int getColor2() {
		return m_Color2;
	}

	public Point getStartPoint() {
		return m_StartPoint;
	}

	public Point getEndPoint() {
		return m_EndPoint;
	}

	public Point getMidOfSelection() {
		int[] purePoints = calcPurePointsSelection();
		int x = purePoints[0] + (purePoints[2] -purePoints[0])/2;
		int y = purePoints[1] + (purePoints[3] - purePoints[1]) /2;
		ThreeDimVector oldMP = new ThreeDimVector(x, y);
		ThreeDimVector newMP = Matrix.multiplyWithVector(m_WorkingLayerMyImage.getMatrix(), oldMP);
		
		return new Point(newMP.getX(), newMP.getY());
	}

	public void setStartPoint(Point p) {
		m_readyToMerge = false;
		m_StartPoint.x = p.x;
		m_StartPoint.y = p.y;
	}

	public void setEndPoint(Point p) {
		m_EndPoint.x = p.x;
		m_EndPoint.y = p.y;
	}

	public void setPixelInArr(int[] pix, int x, int y) {
		if (x < MyImage.IMG_WIDTH && x >= 0 && y < MyImage.IMG_HEIGHT && y >= 0) {
			pix[y * MyImage.IMG_WIDTH + x] = calcColor(x, y);
		}
	}

	public void setPixelInArrCircle(int[] pix, int x1, int y1, int x2, int y2) {
		setPixelInArr(pix, (x1 + x2), (y1 + y2));
		setPixelInArr(pix, (x1 - x2), (y1 + y2));
		setPixelInArr(pix, (x1 + x2), (y1 - y2));
		setPixelInArr(pix, (x1 - x2), (y1 - y2));
		setPixelInArr(pix, (x1 + y2), (y1 + x2));
		setPixelInArr(pix, (x1 - y2), (y1 + x2));
		setPixelInArr(pix, (x1 + y2), (y1 - x2));
		setPixelInArr(pix, (x1 - y2), (y1 - x2));
	}

	public void setPixelInArrFilledCircle(int[] pix, int x1, int y1, int x2, int y2) {
		drawLineInArr(pix, (x1 + x2), (y1 + y2), (x1 - x2), (y1 + y2));
		drawLineInArr(pix, (x1 + x2), (y1 - y2), (x1 - x2), (y1 - y2));
		drawLineInArr(pix, (x1 + y2), (y1 + x2), (x1 - y2), (y1 + x2));
		drawLineInArr(pix, (x1 + y2), (y1 - x2), (x1 - y2), (y1 - x2));
	}

	public void changeMode(int i) {
		clearWorkingLayerAndPoints();
		Mode.currentMode = i;

	}

	public void setMergeReady(boolean b) {
		m_readyToMerge = b;
	}

	public void setColor1(int c) {
		m_Color1 = c;
	}

	public void setColor2(int c) {
		m_Color2 = c;
	}

	static class ThreeDimVector {
		private int[] m_Data;

		public ThreeDimVector(int x, int y) {
			m_Data = new int[3];
			m_Data[0] = x;
			m_Data[1] = y;
			m_Data[2] = 1;
		}

		public ThreeDimVector() {
			m_Data = new int[3];
			m_Data[0] = 0;
			m_Data[1] = 0;
			m_Data[2] = 0;
		}

		// will this ever be used?
		public ThreeDimVector plus(ThreeDimVector that) {
			ThreeDimVector c = new ThreeDimVector();
			for (int i = 0; i < 2; i++)
				c.m_Data[i] = this.m_Data[i] + that.m_Data[i];
			return c;
		}

		// will this ever be used?
		public ThreeDimVector minus(ThreeDimVector that) {
			ThreeDimVector c = new ThreeDimVector();
			for (int i = 0; i < 2; i++)
				c.m_Data[i] = this.m_Data[i] - that.m_Data[i];
			return c;
		}

		public String toString() {
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < 3; i++)
				s.append(m_Data[i] + " ");
			return s.toString();
		}

		public int getX() {
			return m_Data[0];
		}

		public int getY() {
			return m_Data[1];
		}

	}

	static class Matrix {

		private double[][] m_Data;

		public Matrix(double[][] data) {
			if (data.length != 3 || data[0].length != 3)
				throw new RuntimeException("Matrix class only suitable for 3x3 matrix");
			// kelb
			m_Data = data;

			// Robert Sedgewick
			// for (int i = 0; i < 3; i++)
			// for (int j = 0; j < 3; j++)
			// this.m_Data[i][j] = data[i][j];
		}

		public Matrix() {
			this(new double[3][3]);
		}

		public static Matrix translation(int horizontal, int vertical) {
			double[][] tmp = { { 1, 0, horizontal }, { 0, 1, vertical }, { 0, 0, 1 } };
			return new Matrix(tmp);
		}

		public static Matrix inverseTranslation(int horizontal, int vertical) {
			double[][] tmp = { { 1, 0, -horizontal }, { 0, 1, -vertical }, { 0, 0, 1 } };
			return new Matrix(tmp);
		}

		public static Matrix rotation(double alpha) {
			double[][] tmp = { { Math.cos(alpha), -Math.sin(alpha), 0 }, { Math.sin(alpha), Math.cos(alpha), 0 },
					{ 0, 0, 1 } };
			return new Matrix(tmp);
		}

		public static Matrix inverseRotation(double alpha) {
			double[][] tmp = { { Math.cos(-alpha), -Math.sin(-alpha), 0 }, { Math.sin(-alpha), Math.cos(-alpha), 0 },
					{ 0, 0, 1 } };
			return new Matrix(tmp);
		}

		public static Matrix scaling(double sX, double sY) {
			double[][] tmp = { { sX, 0, 0 }, { 0, sY, 0 }, { 0, 0, 1 } };
			return new Matrix(tmp);
		}

		public static Matrix inverserScaling(double sX, double sY) {
			double[][] tmp = { { 1 / sX, 0, 0 }, { 0, 1 / sY, 0 }, { 0, 0, 1 } };
			return new Matrix(tmp);
		}

		// TODO inverse ?! -shX or 1/shX ??
		// "X-Scherung"
		public static Matrix xShearing(double shX) {
			double[][] tmp = { { 1, shX, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
			return new Matrix(tmp);
		}

		public static Matrix inverseXShearing(double shX) {
			double[][] tmp = { { 1, -shX, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
			return new Matrix(tmp);
		}

		// "Y-Scherung"
		public static Matrix yShearing(double shY) {
			double[][] tmp = { { 1, 0, 0 }, { shY, 1, 0 }, { 0, 0, 1 } };
			return new Matrix(tmp);
		}

		public static Matrix inverseYShearing(double shY) {
			double[][] tmp = { { 1, 0, 0 }, { -shY, 1, 0 }, { 0, 0, 1 } };
			return new Matrix(tmp);
		}

		// STATIC OPERATIONS
		// return C = A + B
		public static Matrix plus(Matrix A, Matrix B) {
			Matrix C = new Matrix();
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					C.m_Data[i][j] = A.m_Data[i][j] + B.m_Data[i][j];
			return C;
		}

		// return C = A - B
		public static Matrix minus(Matrix A, Matrix B) {
			Matrix C = new Matrix();
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					C.m_Data[i][j] = A.m_Data[i][j] + B.m_Data[i][j];
			return C;
		}

		// return C = A * B
		public static Matrix multiply(Matrix A, Matrix B) {
			Matrix C = new Matrix();
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++)
					for (int k = 0; k < 3; k++)
						C.m_Data[i][j] += (A.m_Data[i][k] * B.m_Data[k][j]);
			return C;
		}

		public static ThreeDimVector multiplyWithVector(Matrix ma, ThreeDimVector vec) {
			ThreeDimVector tmp = new ThreeDimVector();
			// System.out.println(tmp.toString() + " = " + ma.toString() + " * " +
			// vec.toString());
			for (int i = 0; i < ma.m_Data.length; ++i) {
				for (int j = 0; j < vec.m_Data.length; ++j) {
					tmp.m_Data[i] = (int) (tmp.m_Data[i] + ma.m_Data[i][j] * vec.m_Data[j]);
				}
			}
			// System.out.println(tmp.toString());
			return tmp;
		}

		public double[][] getData() {
			return m_Data;
		}

		public int rowCount() {
			return m_Data.length;
		}

		public int coloumnCount() {
			return m_Data[0].length;
		}

		public static double[][] neutralDoubleArr() {
			double[][] neutralDouble = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
			return neutralDouble;
		}

		// print matrix to standard output
		public String toString() {
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++)
					s.append(m_Data[i][j] + "\t");
				s.append("\n");
			}
			return s.toString();
		}
	}
}
