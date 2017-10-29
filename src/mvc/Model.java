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
			private int[] m_Data;
			
			
			public ThreeDimVector(int x, int y) {
				m_Data = new int[3];
				m_Data[0] = x;
				m_Data[1] = y;
				m_Data[2] = 1;
			}
			
			public ThreeDimVector(){
				this(0,0);
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
		
		
		static class Matrix{			
			
			private double[][] m_Data;
			
			public Matrix(double[][] data) {
				if(data.length != 3 || data[0].length != 3)
					throw new RuntimeException("Matrix class only suitable for 3x3 matrix");			
				//kelb
				m_Data = data;	
				
				//Robert Sedgewick
//				for (int i = 0; i < 3; i++)
//			            for (int j = 0; j < 3; j++)
//			                    this.m_Data[i][j] = data[i][j];
			}
			
			public Matrix() {
				this(new double[3][3]);
			}
			
			public static Matrix translation(int horizontal, int vertical) {
				double[][] tmp = {{1, 0, horizontal}, {0, 1, vertical},{0, 0, 1}};
				return new Matrix(tmp);
			}
			
			public static Matrix inverseTranslation(int horizontal, int vertical) {
				double[][] tmp = {{1, 0, -horizontal}, {0, 1, -vertical},{0, 0, 1}};			
				return new Matrix(tmp);
			}
			
			public static Matrix rotation(double alpha) {
				double[][] tmp = {{Math.cos(alpha), -Math.sin(alpha), 0}, {Math.sin(alpha), Math.cos(alpha), 0},{0,0,1}};
				return new Matrix(tmp);
			}
			
			public static Matrix inverseRotation(double alpha) {
				double[][] tmp = {{-Math.cos(alpha), Math.sin(alpha), 0}, {-Math.sin(alpha), -Math.cos(alpha), 0},{0,0,1}};	
				return new Matrix(tmp);
			}
			
			public static Matrix scaling(int sX, int sY) {
				double[][] tmp = {{sX, 0, 0}, {0, sY, 0},{0,0,1}};
				return new Matrix(tmp);
			}
			
			public static Matrix inverserScaling(int sX, int sY) {
				double[][] tmp = {{1/sX, 0, 0}, {0, 1/sY, 0},{0,0,1}};
				return new Matrix(tmp);
			}
			//TODO inverse ?! -shX or 1/shX ??
			// "X-Scherung"
			public static Matrix xShearing(int shX) {
				double[][] tmp = {{1, shX, 0}, {0, 1, 0},{0,0,1}};
				return new Matrix(tmp);
			}
			
			public static Matrix inverseXShearing(int shX) {
				double[][] tmp = {{1, -shX, 0}, {0, 1, 0},{0,0,1}};	
				return new Matrix(tmp);
			}
			
			// "Y-Scherung"
			public static Matrix yShearing(int shY) {
				double[][] tmp = {{1, 0, 0}, {shY, 1, 0},{0,0,1}};
				return new Matrix(tmp);
			}
			
			public static Matrix inverseYShearing(int shY) {
				double[][] tmp = {{1, 0, 0}, {-shY, 1, 0},{0,0,1}};	
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
