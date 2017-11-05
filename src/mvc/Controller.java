package mvc;

import java.awt.Image;
import java.awt.MediaTracker;
import java.io.File;
import java.util.Vector;

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
		applyPopupListeners();

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
		new Thread() {
			@Override
			public void run() {
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
		}.start();
		
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
	
	public void applyPopupListeners() {		
		m_View.getCenterImageComponent().getPopup().getMITranslateRandom().addActionListener(e->{
			int h = (int)(Math.random()*(150 + 150) - 150);
			int v = (int)(Math.random()*(200 + 200) - 200);
			m_Model.translate(m_View.getCenterImageComponent().getMyImage(), h, v);
		});
		
		m_View.getCenterImageComponent().getPopup().getMITranslateValue().addActionListener(e->{
			PopupDialog dialog = new PopupDialog(m_View, "Custom Translation", "Verschiebung nach rechts:", "Verschiebung nach unten:", "Ganze Zalen von -2000 bis 2000 eingeben");
			if(!dialog.quitDialog()) {
				int h = dialog.getIntValue1();
				int v = dialog.getIntValue2();
				m_Model.translate(m_View.getCenterImageComponent().getMyImage(), h, v);
			}			
		});
		
		m_View.getCenterImageComponent().getPopup().getMIRotateRandom().addActionListener(e->{
			double alpha = Math.random() * (0.99 + 0.99) - 0.99;
			m_Model.rotate(m_View.getCenterImageComponent().getMyImage(), alpha, false);
		});
		m_View.getCenterImageComponent().getPopup().getMIRotateValue().addActionListener(e->{
			PopupDialog dialog = new PopupDialog(m_View, "Custom Rotation", "Rotation: ", "Sinnvoll sind Double-Wert von -1 bis 1", true);
			if(!dialog.quitDialog()) {
				double alpha = dialog.getDoubleValue();
				boolean spinAroundMiddle = dialog.getSpinAroundMid();
				m_Model.rotate(m_View.getCenterImageComponent().getMyImage(), alpha, spinAroundMiddle);
			}		
		});
		
		m_View.getCenterImageComponent().getPopup().getMIShearingXRandom().addActionListener(e->{
			int amount = (int)(Math.random()*(200+200) - 200);
			m_Model.shearX(m_View.getCenterImageComponent().getMyImage(), amount);
		});
		
		m_View.getCenterImageComponent().getPopup().getMIShearingYRandom().addActionListener(e->{
			int amount = (int)(Math.random()*(200+200) - 200);
			m_Model.shearY(m_View.getCenterImageComponent().getMyImage(), amount);
		});
		
		m_View.getCenterImageComponent().getPopup().getMIShearingValue().addActionListener(e->{
			PopupDialog dialog = new PopupDialog(m_View, "Custom Shearing", "X-Shearing",  "Y-Shearing", "Ganze Zahlen eingeben");
			if(!dialog.quitDialog()) {
				int shearX = dialog.getIntValue1();
				int shearY = dialog.getIntValue2();
				m_Model.shearXY(m_View.getCenterImageComponent().getMyImage(), shearX, shearY);
			}
		});
		
		m_View.getCenterImageComponent().getPopup().getMIScalingRandom().addActionListener(e->{
			
		});
		m_View.getCenterImageComponent().getPopup().getMIScalingValue().addActionListener(e->{
			
		});
	}
}
