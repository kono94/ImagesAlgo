package mvc;

import java.util.Vector;

public class Model {
		// Vector in which every component is saved, to loop through and
		// look for selected ones to display them in the center
		private Vector<MyImage> m_VecAllMyImages;
		
		public Model() {
			m_VecAllMyImages = new Vector<MyImage>(5, 0);			
		}
		
		
		public Vector<MyImage> getMyImageVector(){
			return m_VecAllMyImages;
		}
		
		public void addImage(MyImage img) {
			m_VecAllMyImages.addElement(img);
		}

		
		class ThreeDimVector{
			int[] m_Data;
			
			public ThreeDimVector(int x, int y) {
				m_Data = new int[3];
				m_Data[0] = x;
				m_Data[1] = y;
				m_Data[2] = 1;
			}
			
			public int getX() {
				return m_Data[0];
			}
			
			public int getY() {
				return m_Data[1];
			}
		}
		
		static class Matrix{
			double[][] m_Data;
			
			public Matrix(double[][] data) {
				m_Data = data;
			}
			
			public Matrix() {
				this(new double[3][3]);
			}
			
			public static Matrix translation(int x, int y) {
				double[][] tmp = {{1,0,x}, {0,1,y},{0,0,1}};
				return new Matrix(tmp);
			}
			
			public static Matrix inverseTranslation(int x, int y) {
				double[][] tmp = {{1,0,-x}, {0,1,-y},{0,0,1}};
				return new Matrix(tmp);
			}
		}
}
