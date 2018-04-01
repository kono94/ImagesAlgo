package mvc;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class InfoDialog extends JDialog{
		private JLabel m_infoLabel;
		private JButton m_okButton;
		private boolean m_closeOnFocusLost;
		
		public InfoDialog(JFrame owner, String title, boolean modal, String text, boolean closeOnFocusLost) {
			super(owner, title, modal);
			m_closeOnFocusLost = closeOnFocusLost;
			setLayout(new BorderLayout());			
			addKeyListener(new KeyAdapter() {				
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						m_okButton.doClick();
					}
					
				}
			});		
			if(m_closeOnFocusLost) {
				addWindowListener(new WindowAdapter() {				
					@Override
					public void windowDeactivated(WindowEvent arg0) {
						m_okButton.doClick();
						
					}
				});
			}


			Font f = new Font("Arial", Font.PLAIN, 18);
			m_infoLabel = new JLabel(text, SwingConstants.CENTER);
			m_infoLabel.setFont(f);
			m_infoLabel.setBorder(new EmptyBorder(25, 25, 50, 25));
			m_okButton = new JButton("	ok	");
			m_okButton.setFont(f);
			m_okButton.setPreferredSize(new Dimension(80,30));
			m_okButton.addActionListener(e -> dispose());
			
			add(BorderLayout.CENTER, m_infoLabel);
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.add(m_okButton);
			add(BorderLayout.SOUTH, buttonPanel);
			pack();
			Point p = owner.getLocation();
			if(owner.getWidth() > 200) {
					setLocation(p.x + owner.getWidth() / 2 - this.getWidth() / 2,
					p.y + owner.getHeight() / 2 - this.getHeight() / 2);			
			}else {
				setLocationRelativeTo(null);
//					setLocation(getToolkit().getScreenSize().width/2 - 200, getToolkit().getScreenSize().height/2 -200);
			}
		
			setResizable(false);
			setVisible(true);
			requestFocus();
		}
		public InfoDialog(JFrame owner, String title, String text) {
			this(owner, title, false, text, true);
		}
		
}
