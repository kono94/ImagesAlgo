package mvc;

import java.awt.Image;
import java.awt.MediaTracker;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import mvc.View.CenterImageComponent;


@SuppressWarnings("serial")
//Opens a JFileChooser and loads all selected images into 
//the a private image[], controller will call "getInput()" 
//to get the array and pass the img-data to the model and view
//(create "MyImages")
class MyFileChooser extends JFileChooser {
	private Image[] images;
	public MyFileChooser(JFrame view) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg","gif");
		setCurrentDirectory(new File(System.getProperty("user.dir")));
		setFileFilter(filter);
		setMultiSelectionEnabled(true);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		super.updateUI();
		showOpenDialog(view);
		File[] files = getSelectedFiles();			
		images = new Image[files.length];
		try {
			MediaTracker mt = new MediaTracker(this);
			for (int i = 0; i < files.length; i++) {
				// add it to the bottom imageBar (small images)
				Image tmpImg = ImageIO.read(files[i]);
				mt.addImage(tmpImg, 0);
				mt.waitForAll();
				images[i] = tmpImg;			
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Image[] getInput(){		
		return images;
	}
}