package mvc;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
@SuppressWarnings("serial")
public class ImageBarPanel extends JPanel{
	
	public ImageBarPanel(JFrame owner){
		setLayout(new FlowLayout());
		JScrollPane scrollbar = new JScrollPane(this);
		
		// smallImageHeight + 30 to take the scrollbar into account
		scrollbar.setPreferredSize(new Dimension(SmallImage.WIDTH, SmallImage.HEIGHT + 30));
	}
}
