package mvc;

import javax.swing.*;

import java.awt.*;
import java.awt.image.MemoryImageSource;

@SuppressWarnings("serial")
public class View extends JFrame{
	private Model m_Model;
	private CenterImageComponent m_CenterImageComponent;	

	// how much percent of the center does the big image fill
	private int bigImagePercent = 90;
	
	private MyMenuBar m_MyMenuBar;
	private ImageBarPanel m_ImageBar;
	private UtilityBar m_UtilityBar;
	
	public View(Model model) {
		m_Model = model;
		m_ImageBar = new ImageBarPanel(this);	
		m_CenterImageComponent = new CenterImageComponent();		
		m_MyMenuBar = new MyMenuBar(this);
		setJMenuBar(m_MyMenuBar);
		
		
		// set the Layout to BorderLayout.
		// CENTER: big image will be display here % of its width/height
		// SOUTH: Scrollpane with FlowLayout in which all the
		// small-image-components are placed		
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, m_CenterImageComponent);
		add(BorderLayout.SOUTH, m_ImageBar.getScrollPane());
		add(BorderLayout.WEST, new UtilityBar(this));
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);		
	}	
		
	
	class CenterImageComponent extends JComponent {
		private MyImage m_MyImage;
		private CenterPopupMenu m_Popup;
		private MyImage m_TempMyImageForFading;
		private Image m_Img;
		
		public CenterImageComponent() {
			m_TempMyImageForFading = new MyImage(this);
			m_Popup = new CenterPopupMenu();
			add(m_Popup);
			enableEvents(AWTEvent.MOUSE_EVENT_MASK);
			setPreferredSize(new Dimension(1280, 720));
			addMouseListener(m_Popup.new PopupMouseListener());
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
		
		public CenterPopupMenu getPopup() {
			return m_Popup;
		}
		
		public MyImage getMyImage() {
			return m_MyImage;
		}
		public void setMyImage(MyImage myImage) {
			m_MyImage = myImage;
			m_Img = myImage.getImage();
			repaint();
		}
		public void setImg(Image img) {
			m_Img = img;
		}
		 
		public void setTempMyImageWithPixelArr(int[] a) {
			m_TempMyImageForFading.setCurrentPix(a);
			m_Img = m_TempMyImageForFading.getImage();
			repaint();
		}
		
	} // end CenterImageComponent class
	
	
	class ImageBarPanel extends JPanel{
		private JScrollPane m_Scrollpane;
		public ImageBarPanel(JFrame owner){
			setLayout(new FlowLayout());
			m_Scrollpane = new JScrollPane(this);
			
			// smallImageHeight + 30 to take the scrollbar into account
			m_Scrollpane.setPreferredSize(new Dimension(MyImage.COMP_WIDTH, MyImage.COMP_HEIGHT + 30));
		}
		public JScrollPane getScrollPane(){
			return m_Scrollpane;
		}
	}


	
	public MyMenuBar getMyMenuBar() {
		return m_MyMenuBar;
	}
	
	public CenterImageComponent getCenterImageComponent() {
		return m_CenterImageComponent;
	}
	public ImageBarPanel getImageBarPanel() {
		return m_ImageBar;
	}

}
