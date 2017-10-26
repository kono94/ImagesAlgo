package mvc;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

@SuppressWarnings("serial")
public class View extends JFrame{
	private CenterImageComponent m_CenterImageComponent;
	
	// preferred size of the small images in the scrollpane
	private final int smallImageWidth = 160, smallImageHeight = 90;

	// how much percent of the center does the big image fill
	private int bigImagePercent = 90;
	// Vector in which every component is saved, to loop through and
	// look for selected ones to display them in the center
	private Vector<SmallImage> m_AllSmallImages = new Vector<SmallImage>(5, 0);

	private MyMenuBar m_MenuBar;
	ImageBarPanel m_ImageBar;
	public View() {
		// set the Layout to BorderLayout.
		// CENTER: big image will be display here % of its width/height
		// SOUTH: Scrollpane with FlowLayout in which all the
		// small-image-components are placed
		setLayout(new BorderLayout());

		m_ImageBar = new ImageBarPanel(this);
		
		m_MenuBar = new MyMenuBar(this);
		setJMenuBar(m_MenuBar);
		
		m_CenterImageComponent = new CenterImageComponent();
		new MyFileChooser(m_AllSmallImages, m_CenterImageComponent, m_ImageBar, this);
		
		
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	
	
	
	
	
	
	
	
	

	class CenterImageComponent extends JComponent {
		private Image m_Img;

		public CenterImageComponent() {
			setPreferredSize(new Dimension(500, 500));
		}

		@Override
		public void paintComponent(Graphics g) {
			if (m_Img != null) {
				// scales the image to fill "bigImagePercent" percent of the
				// CENTER
				int x = getWidth() / 2 - getWidth() / 2 * bigImagePercent / 100;
				int y = getHeight() / 2 - getHeight() / 2 * bigImagePercent / 100;
				int w = getWidth() * bigImagePercent / 100;
				int h = getHeight() * bigImagePercent / 100;

				// draw the image
				g.drawImage(m_Img, x, y, w, h, this);
			}
		}

		public void setImage(Image img) {
			m_Img = img;
			repaint();
		}
	} // end CenterImageComponent class


}