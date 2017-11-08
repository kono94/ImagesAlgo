package mvc;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import mvc.View.CenterImageComponent;

public class Model {
	// Vector in which every component is saved, to loop through and
	// look for selected ones to display them in the center
	private Vector<MyImage> m_VecAllMyImages;
	private boolean sw = false;
	private Point m_SelectStart = new Point(-1, -1);
	private Point m_SelectEnd = new Point(-1, -1);
	private boolean m_alreadyCutOut = false;
	private MyImage m_WorkingLayerMyImage;
	private int m_bT = 0;
	private MyImage m_CenterMyImg;

	public Model() {
		m_VecAllMyImages = new Vector<MyImage>(5, 0);
	}

	public Vector<MyImage> getMyImageVector() {
		return m_VecAllMyImages;
	}

	public void addImage(MyImage img) {
		m_VecAllMyImages.addElement(img);
	}
	public void createWorkingImage(CenterImageComponent center) {
		m_WorkingLayerMyImage = new MyImage(center);
	}
	public MyImage getWorkingMyImage() {
		return m_WorkingLayerMyImage;
	}
	public void setCenterMyImage(MyImage image) {
		m_CenterMyImg = image;
	}
	public boolean createHistogramm(MyImage img) {
		PrintWriter writer;
		try {
			writer = new PrintWriter("Histogramm.txt", "UTF-8");
			HashMap<Integer, Integer> colorMap = new HashMap<Integer, Integer>();

			int[] pix = img.getCurrentPix();
			for (int i = 0; i < pix.length; ++i) {
				if (!colorMap.containsKey(pix[i])) {
					colorMap.put(pix[i], 1);
				} else {
					colorMap.put(pix[i], (int) colorMap.get(pix[i]) + 1);
				}
			}
			for (Integer color : colorMap.keySet()) {
				writer.println(Integer.toHexString(color) + ": " + (Integer) colorMap.get(color));
			}

			writer.close() ;
			return true;
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public void translate(MyImage myImg, int h, int v) {
		 Matrix transM = Matrix.inverseTranslation(h, v);
		//Matrix transM = Matrix.inverseXShearing(-3.4);
		morph(transM, myImg);
	}
	public void translateSelection(int h, int v) {
		Matrix transM = Matrix.inverseTranslation(h, v);
		morphSelection(transM);
	}


	public void rotate(MyImage myImg, double alpha, boolean spinAroundMiddle) {
		Matrix rotateM;
		if (!spinAroundMiddle) {
			rotateM = Matrix.inverseRotation(alpha);
		} else {
			Matrix toTopLeftM = Matrix.inverseTranslation(-MyImage.IMG_WIDTH / 2, -MyImage.IMG_HEIGHT / 2);
			Matrix spinM = Matrix.inverseRotation(alpha);
			Matrix backM = Matrix.inverseTranslation(MyImage.IMG_WIDTH / 2, MyImage.IMG_HEIGHT / 2);
			rotateM = Matrix.multiply((Matrix.multiply(toTopLeftM, spinM)), backM);
		}

		morph(rotateM, myImg);
	}
	public void rotateSelection(double alpha) {
		int x1 = m_SelectStart.x < m_SelectEnd.x ? m_SelectStart.x : m_SelectEnd.x;
		int y1 = m_SelectStart.y < m_SelectEnd.y ? m_SelectStart.y : m_SelectEnd.y;
		int x2 = x1 + Math.abs(m_SelectEnd.x - m_SelectStart.x)-1;
		int y2 = y1 + Math.abs(m_SelectEnd.y - m_SelectStart.y)-1;
		
		Matrix toTopLeftM = Matrix.inverseTranslation(-(x1 + (x2-x1)/ 2), -(y1 + (y2-y1)/ 2));
		Matrix spinM = Matrix.inverseRotation(alpha);
		Matrix backM = Matrix.inverseTranslation((x1 + (x2-x1)/ 2), (y1 + (y2-y1)/ 2));
		Matrix rotateM = Matrix.multiply((Matrix.multiply(toTopLeftM, spinM)), backM);
		//morphSelection(spinM);
		morphSelection(rotateM);
	}

	public void shearX(MyImage myImg, double shX) {
		Matrix shearM = Matrix.inverseXShearing(shX);
		morph(shearM, myImg);
	}

	public void shearY(MyImage myImg, double shY) {
		Matrix shearM = Matrix.inverseYShearing(shY);
		morph(shearM, myImg);
	}

	public void shearXY(MyImage myImg, double shX, double shY) {
		Matrix shearXY = Matrix.multiply(Matrix.inverseXShearing(shX), Matrix.inverseYShearing(shY));
		morph(shearXY, myImg);
	}

	public void scale(MyImage myImg, double scaleX, double scaleY) {
		Matrix scaleM = Matrix.inverserScaling(scaleX, scaleY);
		morph(scaleM, myImg);
	}

	public void morph(Matrix m, MyImage myImg) {
		// System.out.println(m.toString());
		// System.out.println(myImg.getMatrix().toString());

		m = Matrix.multiply(myImg.getMatrix(), m);
		myImg.setMatrix(m);

		System.out.println(m.toString());

		for (int x = 0; x < MyImage.IMG_WIDTH; ++x) {
			for (int y = 0; y < MyImage.IMG_HEIGHT; ++y) {
				ThreeDimVector vXY = new ThreeDimVector(x, y);
				ThreeDimVector vSrc = Matrix.multiplyWithVector(m, vXY);
				// System.out.println(vSrc.toString());
				int posInOriginal = vSrc.getY() * MyImage.IMG_WIDTH + vSrc.getX();
				// System.out.println("X: " + vSrc.getX() + "\t Y: " + vSrc.getY());
				if (0 <= vSrc.getY() && vSrc.getY() < MyImage.IMG_HEIGHT && 0 <= vSrc.getX()
						&& vSrc.getX() < MyImage.IMG_WIDTH) {
					myImg.getCurrentPix()[y * MyImage.IMG_WIDTH + x] = myImg.getOriginalPix()[posInOriginal];

				} else {

					myImg.getCurrentPix()[y * MyImage.IMG_WIDTH + x] = 0xffffffff;
							//m_VecAllMyImages.get(0)
							//.getOriginalPix()[y * MyImage.IMG_WIDTH + x];
				}
			}
		}
		myImg.newPixels();

	}
	public void morphSelection(Matrix m) {
		int x1 = m_SelectStart.x < m_SelectEnd.x ? m_SelectStart.x : m_SelectEnd.x;
		int y1 = m_SelectStart.y < m_SelectEnd.y ? m_SelectStart.y : m_SelectEnd.y;
		int x2 = x1 + Math.abs(m_SelectEnd.x - m_SelectStart.x)-1;
		int y2 = y1 + Math.abs(m_SelectEnd.y - m_SelectStart.y)-1;
		
		m = Matrix.multiply(m_WorkingLayerMyImage.getMatrix(), m);
		m_WorkingLayerMyImage.setMatrix(m);
		
		for (int x = 0; x < MyImage.IMG_WIDTH; ++x) {
			for (int y = 0; y < MyImage.IMG_HEIGHT; ++y) {
				ThreeDimVector vXY = new ThreeDimVector(x, y);
				ThreeDimVector vSrc = Matrix.multiplyWithVector(m, vXY);
				// System.out.println(vSrc.toString());
				int posInOriginal = vSrc.getY() * MyImage.IMG_WIDTH + vSrc.getX();
				// System.out.println("X: " + vSrc.getX() + "\t Y: " + vSrc.getY());
				if (vSrc.getX() >= x1-m_bT && vSrc.getX() <= x2+m_bT && vSrc.getY() >= y1-m_bT && vSrc.getY() <= y2+m_bT){
					m_WorkingLayerMyImage.getCurrentPix()[y * MyImage.IMG_WIDTH + x] = m_WorkingLayerMyImage.getOriginalPix()[posInOriginal];
					
				} else {
					m_WorkingLayerMyImage.getCurrentPix()[y * MyImage.IMG_WIDTH + x] = 0;
							//m_VecAllMyImages.get(0)
							//.getOriginalPix()[y * MyImage.IMG_WIDTH + x];
				}
			}
		}
		m_WorkingLayerMyImage.newPixels();
		
	}
	public void cutOut(MyImage cImg) {
		m_alreadyCutOut = true;
		System.out.println("cut");
		int x1 = m_SelectStart.x < m_SelectEnd.x ? m_SelectStart.x : m_SelectEnd.x;
		int y1 = m_SelectStart.y < m_SelectEnd.y ? m_SelectStart.y : m_SelectEnd.y;
		int x2 = x1 + Math.abs(m_SelectEnd.x - m_SelectStart.x)-1;
		int y2 = y1 + Math.abs(m_SelectEnd.y - m_SelectStart.y)-1;
		
		if(m_SelectStart.x != -1 && m_SelectEnd.x != -1) {
			for(int x= 0; x< MyImage.IMG_WIDTH; ++x) {
				for(int y=0; y < MyImage.IMG_HEIGHT; ++y) {
					if(x >= x1 && x <= x2 && y >= y1 && y <= y2) {
						m_WorkingLayerMyImage.getOriginalPix()[y*MyImage.IMG_WIDTH + x] =  cImg.getCurrentPix()[y*MyImage.IMG_WIDTH + x];
						cImg.getOriginalPix()[y*MyImage.IMG_WIDTH + x] = 0;
						cImg.getCurrentPix()[y*MyImage.IMG_WIDTH + x] = 0xff000000;
					}else if((x >= x1-m_bT && x < x2 + m_bT && y >= y1-m_bT && y < y1) ||
							( ((x >= x1-m_bT && x < x1) || (x > x2 && x <= x2+m_bT) )&& (y >= y1 && y< y2)) ||
							( x >= x1-m_bT && x < x2 + m_bT && y <= y2+m_bT && y > y2)){
						m_WorkingLayerMyImage.getOriginalPix()[y*MyImage.IMG_WIDTH + x] = 0xff882288;
					}else {
//						m_WorkingLayerMyImage.getOriginalPix()[y*MyImage.IMG_WIDTH + x] = m_WorkingLayerMyImage.getCurrentPix()[y*MyImage.IMG_WIDTH + x];
						m_WorkingLayerMyImage.getOriginalPix()[y*MyImage.IMG_WIDTH + x] = 0;

					}
						
				}
			}
			 m_WorkingLayerMyImage.setCurrentPix(m_WorkingLayerMyImage.getOriginalPix());;
			m_WorkingLayerMyImage.newPixels();
			cImg.newPixels();
		}else {
			System.out.println("cutOut failed");
		}
		
	}
	public void cutOut() {
		cutOut(m_CenterMyImg);
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

	public int getRandomValueForXShearing() {
		return generateRandomInt(-100, 200);
	}

	public int getRandomValueForYShearing() {
		return generateRandomInt(-50, 150);
	}

	public double getRandomValueForScaling() {
		// generate Number between 0.5 - 0.9 OR 1.1 - 1.5;
		return generateBoolean() ? 1 - generateRandomDouble(0.1, 0.5) : 1 + generateRandomDouble(0.1, 0.5);
	}

	private int generateRandomInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	private double generateRandomDouble(double min, double max) {
		return ThreadLocalRandom.current().nextDouble(min, max);
	}

	private boolean generateBoolean() {
		return Math.random() > 0.5 ? false : true;
	}

	public void setSelectStart(Point p) {
		m_SelectStart.x = p.x;
		m_SelectStart.y = p.y;
	}

	public void setSelectEnd(Point p) {
		m_SelectEnd.x = p.x;
		m_SelectEnd.y = p.y;
		System.out.println("male");
	}

	public Point getStartPoint() {
		return m_SelectStart;
	}

	public Point getEndPoint() {
		return m_SelectEnd;
	}
	public void resetSelection() {
		System.out.println("resetet");	
		if(m_alreadyCutOut) {
			mergeWorkingLayer(m_CenterMyImg);
		}
		m_SelectStart.x =-1;
		m_SelectEnd.x = -1;
		m_alreadyCutOut = false;
		clearAllPix(m_WorkingLayerMyImage.getCurrentPix());
		m_WorkingLayerMyImage.fullReset();
		m_WorkingLayerMyImage.newPixels();
	}
	public void mergeWorkingLayer(MyImage image) {
		System.err.println("merge");
		for (int i = 0; i < image.getCurrentPix().length; i++) {
			if(m_WorkingLayerMyImage.getCurrentPix()[i] != 0)
			image.getCurrentPix()[i] = m_WorkingLayerMyImage.getCurrentPix()[i];
		}
		image.newPixels();
	}
	public boolean isAlreadyCutOut() {
		return m_alreadyCutOut;
	}

	public void drawSelection(Point startP, Point endP) {

		int x = startP.x < endP.x ? startP.x : endP.x;
		int y = startP.y < endP.y ? startP.y : endP.y;
		int w = Math.abs(endP.x - startP.x)-1;
		int h = Math.abs(endP.y - startP.y)-1;
		clearAllPix(m_WorkingLayerMyImage.getCurrentPix());
//		drawLineInArr(myImage.getCurrentPix(), x, y, x+w, y);
//		drawLineInArr(myImage.getCurrentPix(), x, y, x, y+h);
//		drawLineInArr(myImage.getCurrentPix(), x+w, y, x+w, y+h);
//		drawLineInArr(myImage.getCurrentPix(), x, y+h, x+w, y+h);

		for(int i= x; i< x+w; ++i) {
			for(int j=y; j<y+h; ++j) {
				if(i < x + m_bT || j < y + m_bT || i > x+w - m_bT || j > y+h - m_bT )
					m_WorkingLayerMyImage.getCurrentPix()[j*MyImage.IMG_WIDTH + i] = 0xff882288;
				else
					m_WorkingLayerMyImage.getCurrentPix()[j*MyImage.IMG_WIDTH + i] = 0x99222299;					
			}
		}
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
	
	public void setPixelInArr(int[] pix, int x, int y) {
		pix[y*MyImage.IMG_WIDTH + x] = 0xff000000 ;
	}
	public void changeMode(int i) {
		Mode.currentMode  = i;
		resetSelection();		
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
			double[][] tmp = { { 1, 1 / shX, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
			return new Matrix(tmp);
		}

		public static Matrix inverseXShearing(double shX) {
			double[][] tmp = { { 1, 1 / shX, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
			return new Matrix(tmp);
		}

		// "Y-Scherung"
		public static Matrix yShearing(double shY) {
			double[][] tmp = { { 1, 0, 0 }, { 1 / shY, 1, 0 }, { 0, 0, 1 } };
			return new Matrix(tmp);
		}

		public static Matrix inverseYShearing(double shY) {
			double[][] tmp = { { 1, 0, 0 }, { 1 / shY, 1, 0 }, { 0, 0, 1 } };
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
