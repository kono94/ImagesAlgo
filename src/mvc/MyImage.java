package mvc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import mvc.View.CenterImageComponent;

@SuppressWarnings("serial")
public class MyImage extends JComponent {
	private Image m_Img;
	// preferred size of the small images in the scrollpane
	public static final int WIDTH = 160;
	public static final int HEIGHT = 90;

	// if selected: adds a red border to the image
	// runnable-class loops through the vector and checks if selected ==
	// true
	private boolean selected;
	private int borderWidth = 2;

	public MyImage(Image img, CenterImageComponent centerImageComponent, JFrame owner) {
		m_Img = img;
		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		// clicking on the small images "sends" them to the centerImage
		// class to be
		// displayed in the center
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// left click display the images directly no matter what
				// right clicks adds the component to the iteration of
				// diplaying them
				// one after another
				if (SwingUtilities.isLeftMouseButton(e))
					centerImageComponent.setImage(img);

				else if (SwingUtilities.isRightMouseButton(e)) {
					selected = selected == true ? false : true;
					repaint();
				}
			}
		});

	} // end constructor

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// adds a red border
		if (selected) {
			g.setColor(Color.RED);
			g.fillRect(0, 0, getWidth(), getHeight());
		}

		// draws the image; getWidth() - borderWidth*2 { one borderWidth on
		// each side}
		g.drawImage(m_Img, 0 + borderWidth, 0 + borderWidth, getWidth() - borderWidth * 2,
				getHeight() - borderWidth * 2, this);
	}
} // end SmallImage class