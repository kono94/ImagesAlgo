package firstTasks;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

@SuppressWarnings("serial")
public class DisplayImage extends JFrame{
	private JPanel imageBar;
	private CenterImageComponent centerImageComponent;
	
	// preferred size of the small images in the scrollpane
	private final int smallImageWidth = 160, smallImageHeight=90;
	
	// how much percent of the center does the big image fill 
	private int bigImagePercent = 90;
	
	
	
	public DisplayImage(){	
		// set the Layout to BorderLayout.
		// CENTER: big image will be display here % of its width/height
		// SOUTH: Scrollpane with FlowLayout in which all the small-image-components are placed
		setLayout(new BorderLayout());
		centerImageComponent = new CenterImageComponent();
		add(BorderLayout.CENTER, centerImageComponent);		
		
		imageBar = new JPanel();
		imageBar.setLayout(new FlowLayout());
		JScrollPane scrollbar = new JScrollPane(imageBar);		
		// smallImageHeight + 30 to take the scrollbar into account
		scrollbar.setPreferredSize(new Dimension(smallImageWidth	,smallImageHeight + 30));
		
		// adding all .jpg's of the current working directory to the FlowLayout
		loadImages();
		
		add(BorderLayout.SOUTH, scrollbar);
		pack();
		setVisible(true);		
	}
	
	private void loadImages(){		
		try {			
			 // . means current working directory
			File directory = new File(".");		
			  File[] f = directory.listFiles();
		        for (File file : f) {
		        	 // if file ends with .jpg
		        	if(file != null && file.getName().toLowerCase().endsWith(".jpg")){
		        		// add it to the bottom imageBar (small images)
		        		imageBar.add(new SmallImage(ImageIO.read(file)));	
		        	}
		        }		     
		} catch (Exception e) {
			
		}
	}
	class CenterImageComponent extends JComponent{
		private Image m_Img;
		public CenterImageComponent(){
			setPreferredSize(new Dimension(500,500));
		}
		@Override
		public void paintComponent(Graphics g){
			if(m_Img != null){
				// scales the image to fill "bigImagePercent" percent of the CENTER
				int x = getWidth()/2 - getWidth()/2 * bigImagePercent / 100; 
				int y = getHeight()/2 - getHeight()/2 * bigImagePercent / 100;
				int w = getWidth() * bigImagePercent / 100;
				int h = getHeight() * bigImagePercent/100;
				
				//draw the image
				g.drawImage(m_Img, x, y, w, h, this);
			}
		}
		
		public void setImage(Image img){
			m_Img = img;
			repaint();
		}
	}
	
	class SmallImage extends JComponent{
		private Image m_Img;
		public SmallImage(Image img){
			m_Img = img;
			setPreferredSize(new Dimension(smallImageWidth, smallImageHeight));
			
			// clicking on the small images "sends" them to the centerImage class to be
			// displayed in the center
			addMouseListener(new MouseAdapter(){
				@Override
				public void mousePressed(MouseEvent e){
					centerImageComponent.setImage(img);
				}
			});
		}
		@Override
		public void paintComponent(Graphics g){
			g.drawImage(m_Img, 0, 0, getWidth(), getHeight(), this);
		}
	}
	
	
	public static void main(String[] args) {
		new DisplayImage();
	}
}
