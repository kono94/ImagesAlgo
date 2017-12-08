package mvc;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class MyMenuBar extends JMenuBar {
	private JMenu m_MfileMenu;
	private JMenuItem m_MIopen;
	private JMenuItem m_MIloadAllImagesItem;
	private JMenu m_MfadingMenu;
	private JMenuItem m_MIslow;
	private JMenuItem m_MImedium;
	private JMenuItem m_MIveryFast;
	private JMenuItem m_MIfadeSwitcher;
	private JMenu m_MhistoMenu;
	private JMenuItem m_MIcreateHisto;
	private JMenu m_MselMenu;
	private JMenuItem m_MIputIn;
	private JMenuItem m_MIrandomColor;
	private JMenu m_MsettingsMenu;
	private JMenu m_MGradientMenu;
	private JMenuItem m_MIleftToRight;
	private JMenuItem m_MIMidToOut;
	private JMenuItem m_MIpicFromHisto;
	private JMenuItem m_MI3D;
	private JMenuItem m_MI3Dback;
	private JMenu m_MapproxMenu;
	private JMenuItem m_MIapprox;

	public MyMenuBar(JFrame owner) {
		Font f = new Font("Comic Sans MS", Font.PLAIN, 18);

		UIManager.put("Menu.font", f);
		UIManager.put("MenuItem.font" , f);
		m_MfileMenu = new JMenu("Files");
		m_MIopen = new JMenuItem("Open");
		m_MIloadAllImagesItem = new JMenuItem("Load all pictures from working directory");
		m_MfileMenu.add(m_MIopen);
		m_MfileMenu.add(m_MIloadAllImagesItem);
		m_MfadingMenu = new JMenu("Fading");
		m_MIslow = new JMenuItem("Slow - 100ms 3%");
		m_MImedium = new JMenuItem("Medium - 50ms 3%");
		m_MIveryFast = new JMenuItem("Max speed - 0ms 3%");
		m_MIfadeSwitcher = new JMenuItem("START fading");

		m_MfadingMenu.add(m_MIfadeSwitcher);
		m_MfadingMenu.addSeparator();
		m_MfadingMenu.add(m_MIslow);
		m_MfadingMenu.add(m_MImedium);
		m_MfadingMenu.add(m_MIveryFast);
		
		m_MhistoMenu = new JMenu("Histogram");
		m_MIcreateHisto = new JMenuItem("Create file with histogram");
		m_MIpicFromHisto = new JMenuItem("Convert histogram to image");
		m_MhistoMenu.add(m_MIcreateHisto);
		m_MhistoMenu.add(m_MIpicFromHisto);
		
		
		m_MselMenu = new JMenu("Selection");
		m_MIputIn = new JMenuItem("Cut");
		m_MselMenu.add(m_MIputIn);
		
		m_MsettingsMenu = new JMenu("Settings");
		m_MIrandomColor = new JMenuItem("START using random colors");
		m_MGradientMenu = new JMenu("Color gradient");
		m_MIleftToRight = new JMenuItem("Left -> Right");
		m_MIMidToOut	= new JMenuItem("Center -> Outside");
		m_MI3D = new JMenuItem("Enable '3D' Mode");
		m_MI3Dback = new JMenuItem("Disable '3D' Mode");
		m_MsettingsMenu.add(m_MIrandomColor);
		m_MsettingsMenu.add(m_MI3D);
		m_MsettingsMenu.add(m_MI3Dback);
		
		m_MGradientMenu.add(m_MIMidToOut);
		m_MGradientMenu.add(m_MIleftToRight);		
		m_MsettingsMenu.add(m_MGradientMenu);
		
		m_MapproxMenu = new JMenu("Approximation");
		m_MIapprox = new JMenuItem("Color substitution");
		m_MapproxMenu.add(m_MIapprox);
		
		this.add(m_MfileMenu);
		this.add(m_MfadingMenu);
		this.add(m_MhistoMenu);
		this.add(m_MselMenu);
		this.add(m_MsettingsMenu);
		this.add(m_MapproxMenu);
	}

	public JMenu getMfileMenu() {
		return m_MfileMenu;
	}

	public JMenuItem getMIopen() {
		return m_MIopen;
	}

	public JMenuItem getMILoadAllImagesItem() {
		return m_MIloadAllImagesItem;
	}

	public JMenu getMmenu() {
		return m_MfadingMenu;
	}

	public JMenuItem getMIslow() {
		return m_MIslow;
	}

	public JMenuItem getMImedium() {
		return m_MImedium;
	}

	public JMenuItem getMIveryFast() {
		return m_MIveryFast;
	}
	
	public JMenuItem getMIfadingSwitcher() {
		return m_MIfadeSwitcher;
	}
	public JMenuItem getMIcreateHisto() {
		return m_MIcreateHisto;
	}
	public JMenuItem getMIputIn() {
		return m_MIputIn;
	}
	
	public JMenuItem getMIrandomColor() {
		return m_MIrandomColor;
	}
	public JMenuItem getMIleftToRight() {
		return m_MIleftToRight;
	}
	public JMenuItem getMIMiddleToOut() {
		return m_MIMidToOut;
	}
	public JMenuItem getMIpicFromHisto() {
		return m_MIpicFromHisto;
	}
	public JMenuItem getMI3D() {
		return m_MI3D;
	}
	public JMenuItem getMI3Dback() {
		return m_MI3Dback;
	}
	public JMenuItem getMIapprox() {
		return m_MIapprox;
	}
}
