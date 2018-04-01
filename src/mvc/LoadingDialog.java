package mvc;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoadingDialog extends JDialog {
	JProgressBar m_progressBar;
	
	public LoadingDialog(JFrame owner, String title, String text, int max) {
		super(owner, title, false);
		setLayout(new GridLayout(1,2));
		JLabel textLabel = new JLabel(text, SwingConstants.CENTER);
		textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		textLabel.setBorder(new EmptyBorder(30, 0, 30, 0));
		add(textLabel);
		m_progressBar = new JProgressBar(0,max);
		m_progressBar.setPreferredSize(new Dimension(300, 40));
		m_progressBar.setBorder(new EmptyBorder(20, 0,20, 20));
		m_progressBar.setStringPainted(true);
		add(m_progressBar);
		Point p = owner.getLocation();
		
		pack();
		setVisible(true);
		if(owner.getWidth() > 200) {
			setLocation(p.x + owner.getWidth() / 2 - this.getWidth() / 2,
			p.y + owner.getHeight() / 2 - this.getHeight() / 2);			
	}else {
		setLocationRelativeTo(null);
//			setLocation(getToolkit().getScreenSize().width/2 - 200, getToolkit().getScreenSize().height/2 -200);
	}
	}
	
	public void addProgress() {
		m_progressBar.setValue(m_progressBar.getValue() + 1);
	}
}
