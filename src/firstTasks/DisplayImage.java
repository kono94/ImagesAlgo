package firstTasks;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Vector;

@SuppressWarnings("serial")
public class DisplayImage extends JFrame{
	private JPanel imageBar;
	private CenterImageComponent m_CenterImageComponent;
	
	// preferred size of the small images in the scrollpane
	private final int smallImageWidth = 160, smallImageHeight=90;
	
	// Vector in which every component is saved, to loop through and
	// look for selected ones to display them in the center
	private Vector<SmallImage> m_AllSmallImages = new Vector<SmallImage>(5, 0);
	
	// how much percent of the center does the big image fill 
	private int bigImagePercent = 90;
	
	// time the thread sleeps after switching an image
	private int m_SwitchingDelay = 500;
	
	
	
	public DisplayImage(){	
		// set the Layout to BorderLayout.
		// CENTER: big image will be display here % of its width/height
		// SOUTH: Scrollpane with FlowLayout in which all the small-image-components are placed
		setLayout(new BorderLayout());
		m_CenterImageComponent = new CenterImageComponent();
		add(BorderLayout.CENTER, m_CenterImageComponent);		
		
		imageBar = new JPanel();
		imageBar.setLayout(new FlowLayout());
		JScrollPane scrollbar = new JScrollPane(imageBar);		
		// smallImageHeight + 30 to take the scrollbar into account
		scrollbar.setPreferredSize(new Dimension(smallImageWidth	,smallImageHeight + 30));
		
		// adding all .jpg's of the current working directory to the FlowLayout
		loadImages();
		
		add(BorderLayout.SOUTH, scrollbar);
		
		//menu to set the time between two images switching
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("time");
		JMenuItem slow = new JMenuItem("slow - 1000ms");
		JMenuItem medium = new JMenuItem("medium - 500ms");
		JMenuItem fast = new JMenuItem("fast - 100ms");
		JMenuItem veryFast = new JMenuItem("very fast - 20ms");
		slow.addActionListener(e->{
			m_SwitchingDelay = 1000;
		});
		medium.addActionListener(e->{
			m_SwitchingDelay = 500;
		});
		fast.addActionListener(e->{
			m_SwitchingDelay = 100;
		});
		veryFast.addActionListener(e->{
			m_SwitchingDelay = 20;
		});
		menu.add(slow); menu.add(medium); menu.add(fast); menu.add(veryFast);
		menuBar.add(menu);
		setJMenuBar(menuBar);
		
		
		 
		setDefaultCloseOperation(EXIT_ON_CLOSE);;
		pack();
		setVisible(true);	
		
		// starting the background thread 
		new AnimateSwitching();
	} // end constructor
	
	private void loadImages(){		
		try {			
			 // . means current working directory
			File directory = new File(".");		
			File[] f = directory.listFiles();
		        for (File file : f) {
		        	 // if file ends with .jpg
		        	if(file != null && file.getName().toLowerCase().endsWith(".jpg")){
		        		// add it to the bottom imageBar (small images)
		        		SmallImage tmp = new SmallImage(ImageIO.read(file));
		        		imageBar.add(tmp);
		        		
		        		// add Component to vector
		        		m_AllSmallImages.addElement(tmp);
		        	}
		        }		     
		} catch (Exception e) {
			System.out.println("File error");
		}
	} // end loadImages() method
	
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
	} // end CenterImageComponent class
	
	class SmallImage extends JComponent{
		private Image m_Img;
		
		// if selected: adds a red border to the image
		// runnable-class loops through the vector and checks if selected == true
		private boolean selected;
		private int borderWidth = 2;
		public SmallImage(Image img){
			m_Img = img;
			setPreferredSize(new Dimension(smallImageWidth, smallImageHeight));
			
			// clicking on the small images "sends" them to the centerImage class to be
			// displayed in the center
			addMouseListener(new MouseAdapter(){
				@Override
				public void mousePressed(MouseEvent e){
					// left click display the images directly no matter what
					// right clicks adds the component to the iteration of diplaying them 
					// one after another
					if(SwingUtilities.isLeftMouseButton(e))
						m_CenterImageComponent.setImage(img);
				
					else if(SwingUtilities.isRightMouseButton(e)){
						selected = selected == true ? false : true;
						repaint();
					}						
				}
			});			
		
		} // end constructor
		
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			
			// adds a red border
			if(selected){
				g.setColor(Color.RED);
				g.fillRect(0, 0, getWidth(), getHeight());
			}	
			
			// draws the image; getWidth() - borderWidth*2 { one borderWidth on each side}
			g.drawImage(m_Img, 0 + borderWidth, 0 + borderWidth, getWidth() - borderWidth*2, getHeight()- borderWidth*2, this);
		}
	} // end SmallImage class
	
	
	
	class AnimateSwitching implements Runnable{
		
		public AnimateSwitching(){
			Thread t = new Thread(this);
			t.start();
		}
		
		@Override
		public void run(){
			while(true){
				for(SmallImage smallComp: m_AllSmallImages){
					if(smallComp.selected){
						// pass the small image to the center component
						m_CenterImageComponent.setImage(smallComp.m_Img);
						try {
							// pause the thread
							Thread.sleep(m_SwitchingDelay);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} // end for-loop
			} // end while(true)
		} // end run()
	} // end Animation class
	
	public static void main(String[] args) {
		// create new JFrame
		new DisplayImage();
	}
}
