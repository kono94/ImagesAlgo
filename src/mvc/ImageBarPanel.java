package mvc;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
@SuppressWarnings("serial")
public class ImageBarPanel extends JPanel{
	private JScrollPane m_Scrollpane;
	public ImageBarPanel(JFrame owner){
		setLayout(new FlowLayout());
		m_Scrollpane = new JScrollPane(this);
		
		// smallImageHeight + 30 to take the scrollbar into account
		m_Scrollpane.setPreferredSize(new Dimension(SmallImage.WIDTH, SmallImage.HEIGHT + 30));
	}
	public JScrollPane getScrollPane(){
		return m_Scrollpane;
	}
}
