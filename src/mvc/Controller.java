package mvc;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;

import mvc.Model.Matrix;
import mvc.UtilityBar.Icon;

@SuppressWarnings("serial")
public class Controller {
	private View m_View;
	private Model m_Model;
	private int m_SwitchingDelay = 50;
	private volatile boolean m_IsFading;

	public Controller() {
		m_Model = new Model();
		m_View = new View(m_Model);
		// proceedJFileChooserInput(new MyFileChooser(m_View).getInput());
		loadAllImagesFromDirectory();
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
		setStartingImage();
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
					LoadingDialog loadingDialog = new LoadingDialog(m_View, "Loading Images", "L�dt neue Bilder ein", f.length);

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
						loadingDialog.addProgress();
					}
					setStartingImage();
					loadingDialog.dispose();
				} catch (Exception e) {
					System.out.println("File error");
				}
				m_View.getImageBarPanel().revalidate();
				
			}
		}.start();

	}
	public void setStartingImage() {
		if(m_View.getCenterImageComponent().getMyImage() == null) {
			if(m_Model.getMyImageVector() != null && m_Model.getMyImageVector().size() > 0)
				m_View.getCenterImageComponent().setMyImage(m_Model.getMyImageVector().get(0));
		}
	}
	public void stopFading() {
		m_IsFading = false;
		m_View.getMyMenuBar().getMIfadingSwitcher().setText("start fading");		
	}
	public void startFading() {
		m_IsFading = true;
		m_View.getMyMenuBar().getMIfadingSwitcher().setText("STOP fading");
		if (m_View.getCenterImageComponent().getMyImage() == null)
			new Fader(-1);
		else {
			new Fader(m_Model.getMyImageVector().indexOf(m_View.getCenterImageComponent().getMyImage()));
		}
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
			m_SwitchingDelay = 100;
		});

		// switch to medium transition speed
		m_View.getMyMenuBar().getMImedium().addActionListener(e -> {
			m_SwitchingDelay = 50;
		});

		// switch to ultra transition speed
		m_View.getMyMenuBar().getMIveryFast().addActionListener(e -> {
			m_SwitchingDelay = 0;
		});

		m_View.getMyMenuBar().getMIfadingSwitcher().addActionListener(e -> {
			if (!m_IsFading) {
				startFading();

			} else {
				stopFading();
			}
		});

		m_View.getMyMenuBar().getMIcreateHisto().addActionListener(e -> {
			if (m_View.getCenterImageComponent().getMyImage() == null) {
				new InfoDialog(m_View, "Histogramm", "Kein Bild ausgew�hlt");

			} else {
				if (m_Model.createHistogramm(m_View.getCenterImageComponent().getMyImage())) {
					new InfoDialog(m_View, "Histogramm",
							"Histogramm wurde erfolgreich in die Datei Histogramm.txt geschrieben");
				} else {
					new InfoDialog(m_View, "Histogramm", "Es gab einen Fehler beim Erstellen des Histogramms!");

				}
			}
		});
	}

	public void applyPopupListeners() {
		m_View.getCenterImageComponent().getPopup().getMITranslateRandom().addActionListener(e -> {
			// [0] = horizontal, [1] = vertical
			int[] random = new int[2];
			random = m_Model.getRandomValuesForTranslation();
			m_Model.translate(m_View.getCenterImageComponent().getMyImage(), random[0], random[1]);
		});

		m_View.getCenterImageComponent().getPopup().getMITranslateValue().addActionListener(e -> {
			PopupDialog dialog = new PopupDialog(m_View, "Custom Translation", "Verschiebung nach rechts:",
					"Verschiebung nach unten:", "Ganze Zalen von --2000 bis 2000 sind sinnvoll", false);
			if (!dialog.quitDialog()) {
				int h = dialog.getIntValue1();
				int v = dialog.getIntValue2();
				m_Model.translate(m_View.getCenterImageComponent().getMyImage(), h, v);
			}
		});

		m_View.getCenterImageComponent().getPopup().getMIRotateRandom().addActionListener(e -> {
			double alpha = m_Model.getRandomValueForRotation();
			m_Model.rotate(m_View.getCenterImageComponent().getMyImage(), alpha, false);
		});
		m_View.getCenterImageComponent().getPopup().getMIRotateValue().addActionListener(e -> {
			PopupDialog dialog = new PopupDialog(m_View, "Custom Rotation", "Rotation: ",
					"Sinnvoll sind Double-Wert von -1 bis 1", true);
			if (!dialog.quitDialog()) {
				double alpha = dialog.getDoubleValue1();
				boolean spinAroundMiddle = dialog.getSpinAroundMid();
				m_Model.rotate(m_View.getCenterImageComponent().getMyImage(), alpha, spinAroundMiddle);
			}
		});

		m_View.getCenterImageComponent().getPopup().getMIShearingXRandom().addActionListener(e -> {
			double amount = m_Model.getRandomValueForXShearing();
			m_Model.shearX(m_View.getCenterImageComponent().getMyImage(), amount);
		});

		m_View.getCenterImageComponent().getPopup().getMIShearingYRandom().addActionListener(e -> {
			double amount = m_Model.getRandomValueForYShearing();
			m_Model.shearY(m_View.getCenterImageComponent().getMyImage(), amount);
		});

		m_View.getCenterImageComponent().getPopup().getMIShearingValue().addActionListener(e -> {
			PopupDialog dialog = new PopupDialog(m_View, "Custom Shearing", "X-Shearing", "Y-Shearing",
					"Double Werte von (-) 0.1 bis 0.8 sind sinnvoll", true);
			if (!dialog.quitDialog()) {
				double shearX = dialog.getDoubleValue1();
				double shearY = dialog.getDoubleValue2();
				m_Model.shearXY(m_View.getCenterImageComponent().getMyImage(), shearX, shearY);
			}
		});

		m_View.getCenterImageComponent().getPopup().getMIScalingRandom().addActionListener(e -> {
			double scaleFactor = m_Model.getRandomValueForScaling();
			m_Model.scale(m_View.getCenterImageComponent().getMyImage(), scaleFactor, scaleFactor);
		});
		m_View.getCenterImageComponent().getPopup().getMIScalingValue().addActionListener(e -> {
			PopupDialog dialog = new PopupDialog(m_View, "Czstom Scaling", "X-Scaling", "Y-Scaling",
					"Double Werte von 0.3 bis 1.7 sind sinnvoll", true);
			if (!dialog.quitDialog()) {
				double scaleX = dialog.getDoubleValue1();
				double scaleY = dialog.getDoubleValue2();
				m_Model.scale(m_View.getCenterImageComponent().getMyImage(), scaleX, scaleY);
			}
		});
	}

	class Fader implements Runnable {
		private Thread fadingThread;
		private int[] m_fadingPixel = new int[MyImage.IMG_WIDTH * MyImage.IMG_HEIGHT];
		private MemoryImageSource m_fadingMIS;
		private int m_endingMyImagePos;

		public Fader(int currentMyImagePos) {
			m_endingMyImagePos = currentMyImagePos;
			m_fadingMIS = new MemoryImageSource(MyImage.IMG_WIDTH, MyImage.IMG_HEIGHT, m_fadingPixel, 0,
					MyImage.IMG_WIDTH);
			m_fadingMIS.setAnimated(true);
			this.fadingThread = new Thread(this);
			System.out.println("start");
			this.fadingThread.start();
		}

		private int compColor(int x1, int x2, int p) {
			return x1 + (x2 - x1) * p / 100;
		}

		private int compPix(int pix1, int pix2, int p) {
			final int RED = compColor((pix1 >> 16) & 0xff, (pix2 >> 16) & 0xff, p);
			final int GREEN = compColor((pix1 >> 8) & 0xff, (pix2 >> 8) & 0xff, p);
			final int BLUE = compColor(pix1 & 0xff, pix2 & 0xff, p);
			return 0xff000000 | (RED << 16) | (GREEN << 8) | BLUE;
		}

		public void shuffle(int[] pixelImg1, int[] pixelImg2, int p) {
			for (int i = 0; i < (MyImage.IMG_WIDTH * MyImage.IMG_HEIGHT - 1); ++i) {
				m_fadingPixel[i] = compPix(pixelImg1[i], pixelImg2[i], p);
			}
			// m_fadingMIS.newPixels();
			m_View.getCenterImageComponent().setWorkingLayerMyImageWithPixelArr(m_fadingPixel);
		}

		@Override
		public void run() {
			Vector<MyImage> imgVec = m_Model.getMyImageVector();
			int vecSize = imgVec.size();

			int loopStart = 0;
			int posImg1 = -1;
			int posImg2 = -1;

			if (m_View.getCenterImageComponent().getMyImage() != null
					&& m_View.getCenterImageComponent().getMyImage().isSelected()) {
				loopStart = m_Model.getMyImageVector().indexOf(m_View.getCenterImageComponent().getMyImage());
			} else {
				for (int i = 0; i < vecSize; ++i) {
					if (imgVec.get(i).isSelected()) {
						m_View.getCenterImageComponent().setMyImage(imgVec.get(i));
						break;
					}
				}
			}

			while (m_IsFading) {
				for (int i = 0; i < vecSize; ++i) {
					if (imgVec.get((loopStart + i) % vecSize).isSelected()) {
						posImg1 = (loopStart + i) % vecSize;
						break;
					}
				}
				if (posImg1 == -1) {
					stopFading();
					new InfoDialog(m_View, "Fading Error", "Es ist kein Bild ausgew�hlt");					
					break;
				}

				for (int i = 1; i < vecSize; ++i) {
					if (imgVec.get((posImg1 + i) % vecSize).isSelected()) {
						posImg2 = loopStart = m_endingMyImagePos = (posImg1 + i) % vecSize;
						break;
					}
				}
				if (posImg2 == -1) {
					stopFading();
					new InfoDialog(m_View, "Fading Error", "Es ist nur ein Bild ausgew�hlt, es werden mindestens zwei ben�tigt!");					
					break;
				}

				for (int k = 0; k <= 100 && m_IsFading; k = k + 3) {
					try {
						Thread.sleep(m_SwitchingDelay);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					shuffle(imgVec.get(posImg1).getCurrentPix(), imgVec.get(posImg2).getCurrentPix(), k);
				}

			}
			if (m_endingMyImagePos != -1)
				m_View.getCenterImageComponent().setMyImage(m_Model.getMyImageVector().get(m_endingMyImagePos));
		}
	}
}
