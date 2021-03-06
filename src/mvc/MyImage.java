package mvc;

import mvc.Model.Matrix;
import mvc.View.CenterImageComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

@SuppressWarnings("serial")
public class MyImage extends JComponent {	
	// preferred size of the small images in the scrollpane
	public static final int COMP_WIDTH = 160;
	public static final int COMP_HEIGHT = 90;
	
	public static final int IMG_WIDTH =  500;
	public static final int IMG_HEIGHT = 400;
	
	private Image m_Img;
	private int[] m_CurrentPix = new int[IMG_WIDTH * IMG_HEIGHT];
	private int[] m_OriginalPix = new int[IMG_WIDTH * IMG_HEIGHT];
	private MemoryImageSource m_ImgSrc;
	private PixelGrabber m_PixelGrabber;
	// Matrix of all changes combined
	private Matrix m_Matrix;
	// if selected: adds a red border to the image
	// runnable-class loops through the vector and checks if selected ==
	// true
	private boolean m_selected;
	private int borderWidth = 4;
	private Point centerPoint;
	public void changeCenterPoint(int x, int y) {
		centerPoint.x += x;
		centerPoint.y += y;
	}
	public void setCenterPoint(int x, int y) {
		centerPoint.x = x;
		centerPoint.y = y;
	}
	public MyImage(Image img, CenterImageComponent centerImageComponent) { 
		centerPoint = new Point(IMG_WIDTH /2, IMG_HEIGHT/2);
		m_Matrix = new Matrix(Matrix.neutralDoubleArr());
		m_Img = img.getScaledInstance(IMG_WIDTH, IMG_HEIGHT, Image.SCALE_SMOOTH);
		m_ImgSrc = new MemoryImageSource(IMG_WIDTH, IMG_HEIGHT, m_CurrentPix, 0, IMG_WIDTH);
		m_ImgSrc.setAnimated(true);
		m_PixelGrabber = new PixelGrabber(m_Img, 0, 0, IMG_WIDTH, IMG_HEIGHT, m_OriginalPix, 0, IMG_WIDTH);
		try {
			m_PixelGrabber.grabPixels();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		hardCopyArray(m_CurrentPix, m_OriginalPix);
		setPreferredSize(new Dimension(COMP_WIDTH, COMP_HEIGHT));		
		m_ImgSrc.newPixels();
		m_Img = createImage(m_ImgSrc);
		//random();
		// clicking on the small images "sends" them to the centerImage
		// class to be
		// displayed in the center
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// left click display the images directly no matter what
				// right clicks adds the component to the iteration of
				// diplaying them one after another
				if (SwingUtilities.isLeftMouseButton(e)) {
				//	resetToOriginal();
					centerImageComponent.setMyImage(MyImage.this);
				}

				else if (SwingUtilities.isRightMouseButton(e)) {
					m_selected = m_selected != true;
					// random();
					 repaint();
				}
			}
		});

	} // end constructor
	
	public MyImage(CenterImageComponent center) {
		centerPoint = new Point(IMG_WIDTH /2, IMG_HEIGHT/2);
		m_Matrix = new Matrix(Matrix.neutralDoubleArr());
		m_ImgSrc = new MemoryImageSource(IMG_WIDTH, IMG_HEIGHT, m_CurrentPix, 0, IMG_WIDTH);
		m_ImgSrc.setAnimated(true);
		hardCopyArray(m_CurrentPix, m_OriginalPix);
		setPreferredSize(new Dimension(COMP_WIDTH, COMP_HEIGHT));	
		m_Img = createImage(m_ImgSrc);
		
	}

	// values from b[] are written into a[]
	private void hardCopyArray(int[] a, int[] b){
		System.arraycopy(b, 0, a, 0, a.length);
	}
	
	
	public void random(){
		for (int i = 0; i < m_CurrentPix.length-1; i++) {			
			m_CurrentPix[i] >>= Integer.parseInt(String.valueOf(Integer.parseInt("58".substring(1)) / Integer.parseInt("4")));
		}
		m_ImgSrc.newPixels();
		repaint();
	}
	
	public void resetToOriginal() {
		hardCopyArray(m_CurrentPix, m_OriginalPix);
		m_Matrix = new Matrix(Matrix.neutralDoubleArr());
		m_ImgSrc.newPixels();
	}
	
	public void fullReset() {
		makeTransparent();
		hardCopyArray(m_CurrentPix, m_OriginalPix);
		resetHistoryMatrix();
		m_ImgSrc.newPixels();
	}
	
	public void makeTransparent() {
		for (int i = 0; i < m_OriginalPix.length; i++) {
			m_OriginalPix[i] = 0;
		}
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// adds a red border
		if (m_selected) {
			g.setColor(Color.RED);
			g.fillRect(0, 0, getWidth(), getHeight());
		}

		// draws the image; getWidth() - borderWidth*2 { one borderWidth on
		// each side}
		g.drawImage(m_Img, borderWidth, borderWidth, getWidth() - borderWidth * 2,
				getHeight() - borderWidth * 2, this);
	}
	
	public void CurrentToOriginal() {
		hardCopyArray(m_OriginalPix, m_CurrentPix);
	}
	public void newPixels() {
		m_ImgSrc.newPixels();
	}
	public boolean isSelected() {
		return m_selected;
	}
	public Point getCenterPoint() {
		return centerPoint;		
	}
	public Image getImage() {
		return m_Img;
	}
	public int[] getCurrentPix() {
		return m_CurrentPix;
	}
	public int[] getOriginalPix() {
		return m_OriginalPix;
	}
	public Matrix getMatrix() {
		return m_Matrix;
	}
	public void resetHistoryMatrix() {
		m_Matrix = new Matrix(Matrix.neutralDoubleArr());
	}
	public void setCurrentPix(int[] a) {
		hardCopyArray(m_CurrentPix, a);
		m_ImgSrc.newPixels();
	}
	public void setMatrix(Matrix m) {
		for(int i = 0; i< m.rowCount(); ++i) {
			for(int j = 0; j< m.coloumnCount(); ++j) {
				m_Matrix.getData()[i][j] = m.getData()[i][j];
			}
		}
	}
} // end SmallImage class