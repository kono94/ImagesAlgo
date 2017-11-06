package mvc;

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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class UtilityBar extends JComponent {
	private Icon m_pfeil;
	private Icon m_linie;
	private Icon m_kreis;
	private Icon[] m_allIcons = new Icon[3];
	public UtilityBar(JFrame owner) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		if(loadIcons()) {
			add(m_pfeil);
			m_allIcons[Mode.SELECT] = m_pfeil; 
			add(m_linie);
			m_allIcons[Mode.LINE] = m_linie; 
			add(m_kreis);
			m_allIcons[Mode.CIRCLE] = m_kreis; 
			
			for (int i = 0; i < m_allIcons.length; i++) {
				if(Mode.currentMode == i) {
					m_allIcons[i].activate();
				}else {
					m_allIcons[i].deactive();
				}
			}
		}else {
			new InfoDialog(owner, "Error", true, "Konnte die Icons nicht einladen", false);
		}
		
		
	}
	public boolean loadIcons() {
		try {
			m_pfeil = new Icon(ImageIO.read(new File("icons/pfeil.png")), ImageIO.read(new File("icons/pfeil_active.png")), Mode.SELECT);
			m_linie = new Icon(ImageIO.read(new File("icons/line.png")), ImageIO.read(new File("icons/line_active.png")), Mode.LINE);
			m_kreis = new Icon(ImageIO.read(new File("icons/kreis.png")), ImageIO.read(new File("icons/kreis_active.png")), Mode.CIRCLE);

			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	class Icon extends JComponent{
		private Image m_imgN;
		private Image m_imgActive;
		private boolean m_isActive;
		private int m_representedMode;
		
		public Icon(Image imgN, Image imgActive, int representedMode) {
			m_representedMode = representedMode;
			m_isActive = false;
			m_imgN = imgN;
			m_imgActive = imgActive;
			setPreferredSize(new Dimension(40,40));
			setMaximumSize(new Dimension(40,40));
			setMinimumSize(new Dimension(40,40));
			
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(!m_isActive)
						activate();
				}
			});
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			if(m_isActive) {
				g.fillRect(0, 0, getWidth(), getHeight());
				g.drawImage(m_imgActive, 2, 2, getWidth()-4, getHeight()-4, this);
			}else {
				g.fillRect(0, 0, getWidth(), getHeight());
				g.drawImage(m_imgN, 2, 2, getWidth()-4, getHeight()-4, this);
			}
		}
		
		public boolean isActive() {
			return m_isActive;
		}
		public void activate() {
			Mode.currentMode = m_representedMode;
			for (int i = 0; i < m_allIcons.length; i++) {
				if(i == m_representedMode)
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
}
