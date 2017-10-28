package mvc;

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
	public MyFileChooser(Vector<MyImage> myImageVector, CenterImageComponent centerImageComponent, ImageBarPanel imageBar, JFrame owner) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "png");
		setCurrentDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator")
				+ "GitWorkspace" + System.getProperty("file.separator") + "ImageAlgo"));
		setFileFilter(filter);
		setMultiSelectionEnabled(true);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		super.updateUI();
		showOpenDialog(owner);
		File[] files = getSelectedFiles();
		try {
			for (int i = 0; i < files.length; i++) {
				// add it to the bottom imageBar (small images)
				MyImage tmp = new MyImage(ImageIO.read(files[i]), centerImageComponent, owner);
				imageBar.add(tmp);

				// add Component to vector
				myImageVector.addElement(tmp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}