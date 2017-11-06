package mvc;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class Model {
	// Vector in which every component is saved, to loop through and
	// look for selected ones to display them in the center
	private Vector<MyImage> m_VecAllMyImages;
	private boolean sw = false;

	public Model() {
		m_VecAllMyImages = new Vector<MyImage>(5, 0);
	}

	public Vector<MyImage> getMyImageVector() {
		return m_VecAllMyImages;
	}

	public void addImage(MyImage img) {
		m_VecAllMyImages.addElement(img);
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
			for (Integer color : colorMap.keySet()){
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

	public void translate(MyImage myImg, int h, int v) {
		// Matrix transM = Matrix.inverseTranslation(h, v);
		Matrix transM = Matrix.inverseXShearing(-3.4);
		morph(transM, myImg);
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

		m = Matrix.multiply(m, myImg.getMatrix());
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

					myImg.getCurrentPix()[y * MyImage.IMG_WIDTH + x] = m_VecAllMyImages.get(0)
							.getOriginalPix()[y * MyImage.IMG_WIDTH + x];
				}
			}
		}
		myImg.updateImgSrcPixelArrayTo(myImg.getCurrentPix());

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
