package mvc;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class View extends JFrame{
	private Model m_Model;
	private CenterImageComponent m_CenterImageComponent;	

	// how much percent of the center does the big image fill
	private int bigImagePercent = 90;
	
	private MyMenuBar m_MyMenuBar;
	private ImageBarPanel m_ImageBar;
	
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
	
	
	class ImageBarPanel extends JPanel{
		private JScrollPane m_Scrollpane;
		public ImageBarPanel(JFrame owner){
			setLayout(new FlowLayout());
			m_Scrollpane = new JScrollPane(this);
			
			// smallImageHeight + 30 to take the scrollbar into account
			m_Scrollpane.setPreferredSize(new Dimension(MyImage.WIDTH, MyImage.HEIGHT + 30));
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
