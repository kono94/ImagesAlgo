package mvc;

import java.awt.Image;
import java.awt.MediaTracker;
import java.io.File;

import javax.imageio.ImageIO;

import mvc.Model.Matrix;

@SuppressWarnings("serial")
public class Controller {
	private View m_View;
	private Model m_Model;
	private int m_SwitchingDelay;

	public Controller() {
		m_Model = new Model();
		m_View = new View(m_Model);
		proceedJFileChooserInput(new MyFileChooser(m_View).getInput());
		applyMenuListeners();

		double[][] a = { { 3, 2, 1 }, { 2, 4, 2 }, { 3, 4, 4 } };
		Matrix am = new Matrix(a);
		System.out.println(Matrix.multiply(am, am).toString());

	}

	public void proceedJFileChooserInput(Image[] imgs) {
		for (int i = 0; i < imgs.length; i++) {
			MyImage tmp = new MyImage(imgs[i], m_View.getCenterImageComponent());
			// add it to the bottom imageBar (small images)
			m_View.getImageBarPanel().add(tmp);
			// add Component to vector
			m_Model.addImage(tmp);
		}

		m_View.getImageBarPanel().revalidate();
	}

	public void loadAllImagesFromDirectory() {
		try {
			// . means current working directory
			File directory = new File(".");
			File[] f = directory.listFiles();
			for (File file : f) {
				// if file ends with .jpg or .gif
				if (file != null && (file.getName().toLowerCase().endsWith(".jpg")
						|| file.getName().toLowerCase().endsWith(".gif"))) {
					
					MediaTracker mt = new MediaTracker(m_View);					
					Image tmpImg = ImageIO.read(file);
					mt.addImage(tmpImg, 0);
					mt.waitForAll();
					
					// add it to the bottom imageBar (small images)
					MyImage tmp = new MyImage(tmpImg, m_View.getCenterImageComponent());
					// add it to the bottom imageBar (small images)
					m_View.getImageBarPanel().add(tmp);

					// add Component to vector
					m_Model.addImage(tmp);
				}
			}
		} catch (Exception e) {
			System.out.println("File error");
		}
		m_View.getImageBarPanel().revalidate();
	}

	public void applyMenuListeners() {
		// load in a single image
		m_View.getMyMenuBar().getMIopen().addActionListener(e -> {
			proceedJFileChooserInput(new MyFileChooser(m_View).getInput());
		});

		// load all images from working directory
		m_View.getMyMenuBar().getMILoadAllImagesItem().addActionListener(e -> {
			loadAllImagesFromDirectory();
		});

		// switch to slow transition speed
		m_View.getMyMenuBar().getMIslow().addActionListener(e -> {
			m_SwitchingDelay = 1000;
		});

		// switch to medium transition speed
		m_View.getMyMenuBar().getMImedium().addActionListener(e -> {
			m_SwitchingDelay = 500;
		});

		// switch to ultra transition speed
		m_View.getMyMenuBar().getMIveryFast().addActionListener(e -> {
			m_SwitchingDelay = 20;
		});
	}
}
