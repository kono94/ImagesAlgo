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
class MyFileChooser extends JFileChooser {
	public MyFileChooser(Model model, View view) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "png", "gif");
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
		try {
			MediaTracker mt = new MediaTracker(this);
			for (int i = 0; i < files.length; i++) {
				// add it to the bottom imageBar (small images)
				Image tmpImg = ImageIO.read(files[i]);
				mt.addImage(tmpImg, 0);
				MyImage tmp = new MyImage(tmpImg, view.getCenterImageComponent());
				
				view.getImageBarPanel().add(tmp);

				// add Component to vector
				model.addImage(tmp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}