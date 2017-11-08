package mvc;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.MemoryImageSource;

@SuppressWarnings("serial")
public class View extends JFrame {
	private Model m_Model;
	private CenterImageComponent m_CenterImageComponent;

	// how much percent of the center does the big image fill
	private int bigImagePercent = 90;

	private MyMenuBar m_MyMenuBar;
	private ImageBarPanel m_ImageBar;
	private UtilityBar m_UtilityBar;

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
		m_UtilityBar = new UtilityBar(this, m_Model);
		add(BorderLayout.WEST, m_UtilityBar);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	class CenterImageComponent extends JComponent {
		private MyImage m_MyImage;
		private CenterPopupMenu m_Popup;
		private Image m_Img;
		private Image m_workImg;
		private boolean m_StartInComp;

		public CenterImageComponent() {
			m_Model.createWorkingImage(this);
			m_workImg = m_Model.getWorkingMyImage().getImage();

			m_Popup = new CenterPopupMenu();
			add(m_Popup);
			enableEvents(AWTEvent.MOUSE_EVENT_MASK);
			setPreferredSize(new Dimension(1280, 720));
			addMouseListener(m_Popup.new PopupMouseListener());
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						m_Model.setEndPoint(manageEndP(e));
						if (Mode.currentMode == Mode.SELECT) {
							m_Model.drawSelection();
						}else if(Mode.currentMode == Mode.LINE) {
							m_Model.drawLine();
						}else if(Mode.currentMode == Mode.CIRCLE) {
							m_Model.drawCircle(false);
						}
						repaint();
					}

				}
			});
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e) && Mode.currentMode != Mode.FADING) {
						m_Model.clearWorkingLayerAndPoints();
						m_Model.setStartPoint(new Point((int) (e.getX() * getScalingFactorX()),
								(int) (e.getY() * getScalingFactorY())));
						m_StartInComp = true;
					}
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.isPopupTrigger()) {

					} else {
						if(Mode.currentMode != Mode.FADING)
							m_Model.clearWorkingLayerAndPoints();
						System.out.println("left");
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						m_Model.setEndPoint(manageEndP(e));

						if (Mode.currentMode == Mode.SELECT) {
							m_Model.drawSelection();
						} else if (Mode.currentMode == Mode.LINE) {
							m_Model.drawLine();
						}else if(Mode.currentMode == Mode.CIRCLE) {
							m_Model.drawCircle(false);
						}

					}
					m_StartInComp = false;
				}

			});
		}

		@Override
		public void paintComponent(Graphics g) {
			if (m_Img != null) {
				// scales the image to fill "bigImagePercent" percent of the
				// CENTER
				// int x = getWidth() / 2 - getWidth() / 2 * bigImagePercent / 100;
				// int y = getHeight() / 2 - getHeight() / 2 * bigImagePercent / 100;
				int x = 0;
				int y = 0;
				int w = getWidth();
				int h = getHeight();

				// draw the image

				g.drawImage(m_Img, x, y, w, h, this);
				g.drawImage(m_workImg, x, y, w, h, this);
			}
		}

		public Point manageEndP(MouseEvent e) {
			int x, y;
			x = e.getX();
			y = e.getY();
			if (e.getX() < 0) {
				x = 0;
			}
			if (e.getX() > getWidth()) {
				x = getWidth()-1;
			}
			if (e.getY() < 0) {
				y = 0;
			}
			if (e.getY() > getHeight()-1) {
				y = getHeight()-1;
			}
			return new Point((int) (x * getScalingFactorX()), (int) (y * getScalingFactorY()));
		}

		public double getScalingFactorX() {
			return (double) MyImage.IMG_WIDTH / (double) getWidth();
		}

		public double getScalingFactorY() {
			return (double) MyImage.IMG_HEIGHT / (double) getHeight();
		}

		public CenterPopupMenu getPopup() {
			return m_Popup;
		}

		public MyImage getMyImage() {
			return m_MyImage;
		}

		public void setMyImage(MyImage myImage) {
			m_MyImage = myImage;
			m_Model.setCenterMyImage(m_MyImage);
			m_Img = myImage.getImage();
			if (!m_Model.isAlreadyCutOut())
				m_Model.clearWorkingLayerAndPoints();
			repaint();
		}

		public void setImg(Image img) {
			m_Img = img;
		}

		public void setWorkingLayerMyImageWithPixelArr(int[] a) {
			m_Model.getWorkingMyImage().setCurrentPix(a);
			repaint();
		}

	} // end CenterImageComponent class

	class ImageBarPanel extends JPanel {
		private JScrollPane m_Scrollpane;

		public ImageBarPanel(JFrame owner) {
			setLayout(new FlowLayout());
			m_Scrollpane = new JScrollPane(this);

			// smallImageHeight + 30 to take the scrollbar into account
			m_Scrollpane.setPreferredSize(new Dimension(MyImage.COMP_WIDTH, MyImage.COMP_HEIGHT + 30));
		}

		public JScrollPane getScrollPane() {
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

	public UtilityBar getUtilityBar() {
		return m_UtilityBar;
	}

}
