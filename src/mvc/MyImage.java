package mvc;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import mvc.View.CenterImageComponent;

//TODO speration of logic and view
@SuppressWarnings("serial")
public class MyImage extends JComponent {	
	// preferred size of the small images in the scrollpane
	public static final int WIDTH = 160;
	public static final int HEIGHT = 90;
	
	private Image m_Img;
	private int[] m_Pix = new int[WIDTH * HEIGHT];
	private int[] m_OriginalPixels = new int[WIDTH * HEIGHT];
	private MemoryImageSource m_ImgSrc;
	private PixelGrabber m_PixelGrabber;
	// if selected: adds a red border to the image
	// runnable-class loops through the vector and checks if selected ==
	// true
	private boolean selected;
	private int borderWidth = 2;

	public MyImage(Image img, CenterImageComponent centerImageComponent) {
	
		m_Img = img;
		m_ImgSrc = new MemoryImageSource(WIDTH, HEIGHT, m_Pix, 0, WIDTH);
		m_ImgSrc.setAnimated(true);
		m_PixelGrabber = new PixelGrabber(m_Img, 0, 0, WIDTH, HEIGHT, m_OriginalPixels, 0, WIDTH);
		try {
			m_PixelGrabber.grabPixels();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		hardCopyArray(m_Pix, m_OriginalPixels);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));		
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
				// diplaying them
				// one after another
				if (SwingUtilities.isLeftMouseButton(e))
					centerImageComponent.setImage(m_Img);

				else if (SwingUtilities.isRightMouseButton(e)) {
					selected = selected == true ? false : true;
					repaint();
				}
			}
		});

	} // end constructor

	// values from b[] are written into a[]
	private void hardCopyArray(int[] a, int[] b){
		for(int i=0; i < a.length; i++){
			a[i] = b[i];
		}
	}
	
	
	public void random(){
		for (int i = 0; i < m_Pix.length; i++) {
			m_Pix[i] /= 2;
		}
		m_ImgSrc.newPixels();
		repaint();
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// adds a red border
		if (selected) {
			g.setColor(Color.RED);
			g.fillRect(0, 0, getWidth(), getHeight());
		}

		// draws the image; getWidth() - borderWidth*2 { one borderWidth on
		// each side}
		g.drawImage(m_Img, 0 + borderWidth, 0 + borderWidth, getWidth() - borderWidth * 2,
				getHeight() - borderWidth * 2, this);
	}
} // end SmallImage class