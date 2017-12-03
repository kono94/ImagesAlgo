package mvc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JSlider;

import mvc.Model.Matrix;
import mvc.UtilityBar.Icon;

@SuppressWarnings("serial")
public class Controller {
	private View m_View;
	private Model m_Model;
	private int m_SwitchingDelay = 50;

	public Controller() {
		m_Model = new Model();
		m_View = new View(m_Model);
		
		MyImage tmp = new MyImage(m_View.createImage(MyImage.IMG_WIDTH, MyImage.IMG_HEIGHT),m_View.getCenterImageComponent());
		for (int j = 0; j < tmp.getCurrentPix().length; j++) {
			tmp.getCurrentPix()[j] = 0xffffffff;
		}
		tmp.CurrentToOriginal();
		tmp.newPixels();		
		m_View.getImageBarPanel().add(tmp);
		m_Model.addImage(tmp);
		setStartingImage();
		m_View.getImageBarPanel().revalidate();
		
		// proceedJFileChooserInput(new MyFileChooser(m_View).getInput());
		loadAllImagesFromDirectory();
		applyKeyListeners();
		applyMenuListeners();
		applyPopupListeners();
		applyUtilityListeners();
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
					File directory = new File("./pictures");
					File[] f = directory.listFiles();
					LoadingDialog loadingDialog = new LoadingDialog(m_View, "Loading Images", "Loading Images...", f.length);

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
			if(m_Model.getMyImageVector() != null && m_Model.getMyImageVector().size() > 0)
				m_View.getCenterImageComponent().setMyImage(m_Model.getMyImageVector().get(0));		
	}
	public void stopFading() {
		m_View.getUtilityBar().getIconByModus(Mode.SELECT).activate();

		m_Model.changeMode(Mode.SELECT);
		m_View.getMyMenuBar().getMIfadingSwitcher().setText("START fading");		
	}
	public void startFading() {
		m_Model.changeMode(Mode.FADING);
		m_View.getMyMenuBar().getMIfadingSwitcher().setText("STOP fading");
		if (m_View.getCenterImageComponent().getMyImage() == null)
			new Fader(-1);
		else {
			new Fader(m_Model.getMyImageVector().indexOf(m_View.getCenterImageComponent().getMyImage()));
		}
	}
	
	public void applyKeyListeners() {
		m_View.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println("dd");
			if(e.getKeyCode() > KeyEvent.VK_0)
				System.out.println("dd");
			}
		});
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
			if (Mode.currentMode != Mode.FADING) {
				startFading();

			} else {
				stopFading();
			}
		});

		m_View.getMyMenuBar().getMIcreateHisto().addActionListener(e -> {
			if (m_View.getCenterImageComponent().getMyImage() == null) {
				new InfoDialog(m_View, "Histogramm", "No picture selected!");

			} else {
				if (m_Model.createHistogramm(m_View.getCenterImageComponent().getMyImage())) {
					new InfoDialog(m_View, "Histogramm",
							"Histogram successfully written into 'Histogramm.txt' file");
				} else {
					new InfoDialog(m_View, "Histogramm", "There was an error creating your histogram!");

				}
			}
		});
		m_View.getMyMenuBar().getMIpicFromHisto().addActionListener(e->{
			m_Model.ImageFromHisto();
			m_View.getCenterImageComponent().repaint();
		});
		
		m_View.getMyMenuBar().getMIputIn().addActionListener(e->{
			m_Model.mergeWorkingLayer(m_View.getCenterImageComponent().getMyImage());	
		});
		
		m_View.getMyMenuBar().getMIrandomColor().addActionListener(e->{
			if(m_Model.isUsingRandomColors()) {
				m_Model.useRandomColors(false);
				m_View.getMyMenuBar().getMIrandomColor().setText("START using random colors");
			}else {
				m_Model.useRandomColors(true);
				m_Model.generateRandomColors();
				m_View.getUtilityBar().getColor1Button().setBackground(new Color(m_Model.getColor1()));
				m_View.getUtilityBar().getColor2Button().setBackground(new Color(m_Model.getColor2()));
				m_View.getMyMenuBar().getMIrandomColor().setText("STOP using random colors");
			}
			
		});
		
		m_View.getMyMenuBar().getMIleftToRight().addActionListener(e->{
			m_Model.setGradient(Model.LEFT_TO_RIGHT_GRADIENT);
		});
		m_View.getMyMenuBar().getMIMiddleToOut().addActionListener(e->{
			m_Model.setGradient(Model.MIDDLE_TO_OUTSIDE_GRADIENT);
		});
		m_View.getMyMenuBar().getMI3D().addActionListener(e->{
			m_Model.set3D(true);
		});
		m_View.getMyMenuBar().getMI3Dback().addActionListener(e->{
			m_Model.set3D(false);
		});
	}

	public void applyPopupListeners() {
		m_View.getCenterImageComponent().getPopup().getMICutOut().addActionListener(e->{
			if(!m_Model.isMergeReady())
			m_Model.cutOut();
			else {
				new InfoDialog(m_View, "Info", false, "No effect", true);
			}
		});
		m_View.getCenterImageComponent().getPopup().getMITranslateRandom().addActionListener(e -> {
			// [0] = horizontal, [1] = vertical
			int[] random = new int[2];
			random = m_Model.getRandomValuesForTranslation();
			if(m_Model.getEndPoint().x == -1) {
				m_Model.translate(m_View.getCenterImageComponent().getMyImage(), random[0], random[1]);
			}else {
				if(!m_Model.isMergeReady()) {
					m_Model.cutOut(m_View.getCenterImageComponent().getMyImage());
				}
				m_Model.translateSelection(random[0], random[1]);		
			}
			
			
		});

		m_View.getCenterImageComponent().getPopup().getMITranslateValue().addActionListener(e -> {
			PopupDialog dialog = new PopupDialog(m_View, "Custom Translation", "Move right:",
					"Move down:", "Integers from -2000 to 2000 are reasonable", false);
			if (!dialog.quitDialog()) {
				int h = dialog.getIntValue1();
				int v = dialog.getIntValue2();
				if(m_Model.getEndPoint().x == -1) {
					m_Model.translate(m_View.getCenterImageComponent().getMyImage(), h, v);
				}else {
					if(!m_Model.isMergeReady()) {
						m_Model.cutOut(m_View.getCenterImageComponent().getMyImage());
					}
					m_Model.translateSelection(h, v);		
				}
			}
		});

		m_View.getCenterImageComponent().getPopup().getMIRotateRandom().addActionListener(e -> {			
			double alpha = m_Model.getRandomValueForRotation();
			if(m_Model.getEndPoint().x == -1) {
				m_Model.rotate(m_View.getCenterImageComponent().getMyImage(), alpha, false);
			}else {
				if(!m_Model.isMergeReady()) {
					m_Model.cutOut(m_View.getCenterImageComponent().getMyImage());
				}
				m_Model.rotateSelection(alpha);		
			}
		});
		m_View.getCenterImageComponent().getPopup().getMIRotateValue().addActionListener(e -> {
			PopupDialog dialog = new PopupDialog(m_View, "Custom Rotation", "Rotation: ",
					"Values in degree", true);
			if (!dialog.quitDialog()) {
				double alpha = dialog.getDoubleValue1();
				boolean spinAroundMiddle = dialog.getSpinAroundMid();
				if(m_Model.getEndPoint().x == -1) {
					m_Model.rotate(m_View.getCenterImageComponent().getMyImage(), alpha, spinAroundMiddle);
				}else {
					if(!m_Model.isMergeReady()) {
						m_Model.cutOut(m_View.getCenterImageComponent().getMyImage());
					}
					m_Model.rotateSelection(alpha);		
				}	
				
			}
		});

		m_View.getCenterImageComponent().getPopup().getMIShearingXRandom().addActionListener(e -> {
			double amount = m_Model.getRandomValueForXShearing();
			
			if(m_Model.getEndPoint().x == -1) {
				m_Model.shearX(m_View.getCenterImageComponent().getMyImage(), amount);
			}else {
				if(!m_Model.isMergeReady()) {
					m_Model.cutOut(m_View.getCenterImageComponent().getMyImage());
				}

				m_Model.shearXSelection(amount);
			}
		});

		m_View.getCenterImageComponent().getPopup().getMIShearingYRandom().addActionListener(e -> {
			double amount = m_Model.getRandomValueForYShearing();
			if(m_Model.getEndPoint().x == -1) {
				m_Model.shearY(m_View.getCenterImageComponent().getMyImage(), amount);
			}else {
				if(!m_Model.isMergeReady()) {
					m_Model.cutOut(m_View.getCenterImageComponent().getMyImage());
				}

				m_Model.shearYSelection(amount);
			}
		});

		m_View.getCenterImageComponent().getPopup().getMIShearingValue().addActionListener(e -> {
			PopupDialog dialog = new PopupDialog(m_View, "Custom Shearing", "X-Shearing", "Y-Shearing",
					"Double values from (-) 0.1 to 0.8 are reasonable", true);
			if (!dialog.quitDialog()) {
				double shearX = dialog.getDoubleValue1();
				double shearY = dialog.getDoubleValue2();
				m_Model.shearXY(m_View.getCenterImageComponent().getMyImage(), shearX, shearY);
				if(m_Model.getEndPoint().x == -1) {
					m_Model.shearXY(m_View.getCenterImageComponent().getMyImage(), shearX, shearY);
				}else {
					if(!m_Model.isMergeReady()) {
						m_Model.cutOut(m_View.getCenterImageComponent().getMyImage());
					}

					m_Model.shearXYSelection(shearX, shearY);
				}
			}
		});

		m_View.getCenterImageComponent().getPopup().getMIScalingRandom().addActionListener(e -> {
			double scaleFactor = m_Model.getRandomValueForScaling();
			
			if(m_Model.getEndPoint().x == -1) {
				m_Model.scale(m_View.getCenterImageComponent().getMyImage(), scaleFactor, scaleFactor);
			}else {
				if(!m_Model.isMergeReady()) {
					m_Model.cutOut(m_View.getCenterImageComponent().getMyImage());
				}

				m_Model.scaleSelection(scaleFactor, scaleFactor);
			}
			
		});
		m_View.getCenterImageComponent().getPopup().getMIScalingValue().addActionListener(e -> {
			PopupDialog dialog = new PopupDialog(m_View, "Custom Scaling", "X-Scaling", "Y-Scaling",
					"Double values from 0.3 to 1.7 are reasonable", true);
			if (!dialog.quitDialog()) {
				double scaleX = dialog.getDoubleValue1();
				double scaleY = dialog.getDoubleValue2();
				if(m_Model.getEndPoint().x == -1) {
					m_Model.scale(m_View.getCenterImageComponent().getMyImage(), scaleX, scaleY);
				}else {
					if(!m_Model.isMergeReady()) {
						m_Model.cutOut(m_View.getCenterImageComponent().getMyImage());
					}

					m_Model.scaleSelection(scaleX, scaleY);
				}
			}
		});
	}

	
	public void applyUtilityListeners() {
//		m_View.getUtilityBar().getMoveTop().addMouseListener(new ControlMouseAdapter(Model.MOVE_TOP));	
//		m_View.getUtilityBar().getMoveRight().addMouseListener(new ControlMouseAdapter(Model.MOVE_RIGHT));
//		m_View.getUtilityBar().getMoveBottom().addMouseListener(new ControlMouseAdapter(Model.MOVE_BOTTOM));		
//		m_View.getUtilityBar().getMoveLeft().addMouseListener(new ControlMouseAdapter(Model.MOVE_LEFT));
	
		m_View.getUtilityBar().getReduceColors().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JDialog dialog = new JDialog(m_View, "Reduce Color", true) {
					{
						setLocationRelativeTo(null);
						setLayout(new BorderLayout());
						
						
						JSlider slider = new JSlider(1,100, 100);
						slider.setMinorTickSpacing(5);
						slider.setMajorTickSpacing(5);
						slider.setSnapToTicks(true);
						slider.setPaintTicks(true);
						slider.setPaintLabels(true);
						
						slider.addChangeListener(e->{
							//System.out.println(slider.getValue());
							m_Model.reduceColors(slider.getValue());
						});
						setSize(400, 200);
						slider.setMinimumSize(new Dimension(400,200));
						add(BorderLayout.CENTER, slider);
						setVisible(true);
					}
				};
			}
		});
		
	}
	class Fader implements Runnable {
		private Thread fadingThread;
		private int m_endingMyImagePos;

		public Fader(int currentMyImagePos) {			
			m_endingMyImagePos = currentMyImagePos;
			this.fadingThread = new Thread(this);		
			this.fadingThread.start();
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

			while (Mode.currentMode == Mode.FADING) {
				for (int i = 0; i < vecSize; ++i) {
					if (imgVec.get((loopStart + i) % vecSize).isSelected()) {
						posImg1 = (loopStart + i) % vecSize;
						break;
					}
				}
				if (posImg1 == -1) {
					new InfoDialog(m_View, "Fading Error", "No picture selected! (right click the small images in the bottom panel to select)");					
					break;
				}

				for (int i = 1; i < vecSize; ++i) {
					if (imgVec.get((posImg1 + i) % vecSize).isSelected()) {
						posImg2 = loopStart = m_endingMyImagePos = (posImg1 + i) % vecSize;
						break;
					}
				}
				if (posImg2 == -1) {
					new InfoDialog(m_View, "Fading Error", "Only one picture selected, need at least two");					
					break;
				}

				for (int k = 0; k <= 100 && (Mode.currentMode == Mode.FADING); k = k + 3) {
					try {
						Thread.sleep(m_SwitchingDelay);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					m_Model.shuffle(imgVec.get(posImg1).getCurrentPix(), imgVec.get(posImg2).getCurrentPix(), k);
				}
				

			}
			
			stopFading();
			if (m_endingMyImagePos != -1)
				m_View.getCenterImageComponent().setMyImage(m_Model.getMyImageVector().get(m_endingMyImagePos));
		}
	}
}
