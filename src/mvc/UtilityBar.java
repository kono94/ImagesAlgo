package mvc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

public class UtilityBar extends JComponent {
	private Icon m_pfeil;
	private Icon m_linie;
	private Icon m_kreis;
	private Icon m_filledCircle;
	private Icon[] m_allIcons = new Icon[4]; // Those who change Mode
	private Model m_Model;
	private View m_View;
	private Icon m_moveTop;
	private Icon m_moveRight;
	private Icon m_moveBottom;
	private Icon m_moveLeft;
	private Icon m_rotateLeft;
	private Icon m_rotateRight;
	private Icon m_scaleSmaller;
	private Icon m_scaleBigger;
	private Icon m_shearX;
	private Icon m_shearY;
	private Icon m_swirl;
	private JButton m_color1Button;
	private JButton m_color2Button;

	public UtilityBar(View view, Model m) {
		m_Model = m;
		m_View = view;
		setBackground(new Color(0x00A5C5));
		JPanel colorSelection = new JPanel();
		colorSelection.setLayout(new FlowLayout());
		m_color1Button = new JButton();
		m_color1Button.setPreferredSize(new Dimension(18, 18));
		m_color1Button.setBackground(new Color(m_Model.getColor1()));
		m_color1Button.addActionListener(e -> {
			Color newColor = JColorChooser.showDialog(m_View, "Choose Right Color", new Color(m_Model.getColor1()));
			if (newColor != null) {
				m_Model.setColor1(newColor.getRGB());
				m_color1Button.setBackground(newColor);
			}

		});
		m_color2Button = new JButton();
		m_color2Button.setPreferredSize(new Dimension(18, 18));
		m_color2Button.setBackground(new Color(m_Model.getColor2()));
		m_color2Button.addActionListener(e -> {
			Color newColor = JColorChooser.showDialog(m_View, "Choose Right Color", new Color(m_Model.getColor2()));
			if (newColor != null) {
				m_Model.setColor2(newColor.getRGB());
				m_color2Button.setBackground(newColor);
			}

		});
		colorSelection.add(m_color1Button);
		colorSelection.add(m_color2Button);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		if (loadIcons()) {
			add(m_pfeil);
			m_allIcons[Mode.SELECT] = m_pfeil;
			add(m_linie);
			m_allIcons[Mode.LINE] = m_linie;
			add(m_kreis);
			m_allIcons[Mode.CIRCLE] = m_kreis;
			add(m_filledCircle);
			m_allIcons[Mode.FILLED_CIRCLE] = m_filledCircle;
			add(m_moveTop);
			add(m_moveRight);
			add(m_moveBottom);
			add(m_moveLeft);
			add(m_scaleBigger);
			add(m_scaleSmaller);
			add(m_rotateLeft);
			add(m_rotateRight);
			add(m_shearX);
			add(m_shearY);
			add(m_swirl);

			add(colorSelection);
			for (int i = 0; i < m_allIcons.length; i++) {
				if (Mode.currentMode == i) {
					m_allIcons[i].activate();
				} else {
					m_allIcons[i].deactive();
				}
			}
		} else {
			new InfoDialog((JFrame) view, "Error", true, "Konnte die Icons nicht einladen", false);
		}
	}

	public Icon getMoveTop() {
		return m_moveTop;
	}

	public Icon getMoveRight() {
		return m_moveRight;
	}

	public Icon getMoveBottom() {
		return m_moveBottom;
	}

	public Icon getMoveLeft() {
		return m_moveLeft;
	}

	public Icon getShearX() {
		return m_shearX;
	}

	public Icon getShearY() {
		return m_shearY;
	}

	public JButton getColor1Button() {
		return m_color1Button;
	}
	public JButton getColor2Button() {
		return m_color2Button;
	}
	public boolean loadIcons() {
		try {
			m_pfeil = new Icon(ImageIO.read(new File("icons/pfeil.png")),
					ImageIO.read(new File("icons/pfeil_active.png")), Mode.SELECT);
			m_linie = new Icon(ImageIO.read(new File("icons/line.png")),
					ImageIO.read(new File("icons/line_active.png")), Mode.LINE);
			m_kreis = new Icon(ImageIO.read(new File("icons/kreis.png")),
					ImageIO.read(new File("icons/kreis_active.png")), Mode.CIRCLE);
			m_filledCircle = new Icon(ImageIO.read(new File("icons/filledCircle.png")),
					ImageIO.read(new File("icons/filledCircle_active.png")), Mode.FILLED_CIRCLE);
			m_moveTop = new Icon(ImageIO.read(new File("icons/moveTop.png")),
					ImageIO.read(new File("icons/moveTopPressed.png")), Model.MOVE_TOP);
			m_moveRight = new Icon(ImageIO.read(new File("icons/moveRight.png")),
					ImageIO.read(new File("icons/moveRightPressed.png")), Model.MOVE_RIGHT);
			m_moveBottom = new Icon(ImageIO.read(new File("icons/moveBottom.png")),
					ImageIO.read(new File("icons/moveBottomPressed.png")), Model.MOVE_BOTTOM);
			m_moveLeft = new Icon(ImageIO.read(new File("icons/moveLeft.png")),
					ImageIO.read(new File("icons/moveLeftPressed.png")), Model.MOVE_LEFT);
			m_scaleBigger = new Icon(ImageIO.read(new File("icons/scaleBigger.png")),
					ImageIO.read(new File("icons/scaleBiggerPressed.png")), Model.SCALE_BIGGER);
			m_scaleSmaller = new Icon(ImageIO.read(new File("icons/scaleSmaller.png")),
					ImageIO.read(new File("icons/scaleSmallerPressed.png")), Model.SCALE_SMALLER);
			m_rotateLeft = new Icon(ImageIO.read(new File("icons/rotateLeft.png")),
					ImageIO.read(new File("icons/rotateLeftPressed.png")), Model.ROTATE_LEFT);
			m_rotateRight = new Icon(ImageIO.read(new File("icons/rotateRight.png")),
					ImageIO.read(new File("icons/rotateRightPressed.png")), Model.ROTATE_RIGHT);
			m_shearX = new Icon(ImageIO.read(new File("icons/shearX.png")),
					ImageIO.read(new File("icons/shearXPressed.png")), Model.SHEARX);
			m_shearY = new Icon(ImageIO.read(new File("icons/shearY.png")),
					ImageIO.read(new File("icons/shearYPressed.png")), Model.SHEARY);
			m_swirl = new Icon(ImageIO.read(new File("icons/swirl.png")),
					ImageIO.read(new File("icons/swirlPressed.png")), Model.MOVE_TOP);

			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public Icon getIconByModus(int modus) {
		return m_allIcons[modus];
	}

	class Icon extends JComponent {
		private Image m_imgN;
		private Image m_imgActive;
		private Image m_imgP;
		private boolean m_isActive;
		private boolean m_isPressed;
		private int m_representedMode = -1;

		public Icon(Image imgN, Image imgActive, short representedMode) {
			m_representedMode = representedMode;
			m_isActive = false;
			m_imgN = imgN;
			m_imgActive = imgActive;
			setPreferredSize(new Dimension(40, 40));
			setMaximumSize(new Dimension(40, 40));
			setMinimumSize(new Dimension(40, 40));

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (Mode.currentMode != m_representedMode)
						activate();
				}
			});
		}

		public Icon(Image imgN, Image imgP, int action) {
			m_isActive = false;
			m_imgN = imgN;
			m_imgP = imgP;
			setPreferredSize(new Dimension(40, 40));
			setMaximumSize(new Dimension(40, 40));
			setMinimumSize(new Dimension(40, 40));
			addMouseListener(new MyControlMouseAdapter(this, action));

		}

		@Override
		protected void paintComponent(Graphics g) {
			if (m_isActive) {
				g.fillRect(0, 0, getWidth(), getHeight());
				g.drawImage(m_imgActive, 2, 2, getWidth() - 4, getHeight() - 4, this);
			} else if (m_isPressed) {
				g.fillRect(0, 0, getWidth(), getHeight());
				g.drawImage(m_imgP, 2, 2, getWidth() - 4, getHeight() - 4, this);

			} else {

				g.fillRect(0, 0, getWidth(), getHeight());
				g.drawImage(m_imgN, 2, 2, getWidth() - 4, getHeight() - 4, this);
			}
		}

		public void setPressed(boolean p) {
			m_isPressed = p;
			repaint();
		}

		public boolean isActive() {
			return m_isActive;
		}

		public void activate() {
			m_Model.changeMode(m_representedMode);

			for (int i = 0; i < m_allIcons.length; i++) {
				if (i == m_representedMode)
					m_isActive = true;
				else
					m_allIcons[i].deactive();
			}

			repaint();
		}

		public void deactive() {
			m_isActive = false;
			repaint();
		}
	}

	class MyControlMouseAdapter extends MouseAdapter {
		private int m_action;
		private Icon m_icon;
		private volatile boolean m_mouseDown;

		public MyControlMouseAdapter(Icon icon, int action) {
			m_action = action;
			m_icon = icon;
			m_mouseDown = false;

		}

		@Override
		public void mousePressed(MouseEvent e) {
			m_icon.setPressed(true);
			m_mouseDown = true;
			new PressedThread(e);
			m_Model.manageIconAction(m_action, e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			m_mouseDown = false;
			m_icon.setPressed(false);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			m_mouseDown = false;
			m_icon.setPressed(false);
		}

		class PressedThread implements Runnable {
			MouseEvent m_e;

			public PressedThread(MouseEvent e) {
				Thread t = new Thread(this);
				t.start();
				System.out.println("new");
				m_e = e;
			}

			@Override
			public void run() {
				while (m_mouseDown) {

					m_icon.setPressed(true);
					m_Model.manageIconAction(m_action, m_e);
				}

			}
		}
	}
}
