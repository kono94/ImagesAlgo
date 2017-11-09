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

	public MyMenuBar(JFrame owner) {
		Font f = new Font("sans-serif", Font.PLAIN, 18);
		UIManager.put("Menu.font", f);
		UIManager.put("MenuItem.font" , f);
		m_MfileMenu = new JMenu("Files");
		m_MIopen = new JMenuItem("open");
		m_MIloadAllImagesItem = new JMenuItem("load all images in current directory");
		m_MfileMenu.add(m_MIopen);
		m_MfileMenu.add(m_MIloadAllImagesItem);
		m_MfadingMenu = new JMenu("Fading");
		m_MIslow = new JMenuItem("slow - 100ms 3%");
		m_MImedium = new JMenuItem("medium - 50ms 3%");
		m_MIveryFast = new JMenuItem("maximaler Speed - 0ms 3%");
		m_MIfadeSwitcher = new JMenuItem("start fading");

		m_MfadingMenu.add(m_MIfadeSwitcher);
		m_MfadingMenu.addSeparator();
		m_MfadingMenu.add(m_MIslow);
		m_MfadingMenu.add(m_MImedium);
		m_MfadingMenu.add(m_MIveryFast);
		
		m_MhistoMenu = new JMenu("Histogramm");
		m_MIcreateHisto = new JMenuItem("Erstelle Datei mit Historgramm");
		m_MhistoMenu.add(m_MIcreateHisto);
		
		m_MselMenu = new JMenu("Selection");
		m_MIputIn = new JMenuItem("Paste In");
		m_MselMenu.add(m_MIputIn);
		
		m_MsettingsMenu = new JMenu("Settings");
		m_MIrandomColor = new JMenuItem("start using random colors");
		
		m_MsettingsMenu.add(m_MIrandomColor);
		
		this.add(m_MfileMenu);
		this.add(m_MfadingMenu);
		this.add(m_MhistoMenu);
		this.add(m_MselMenu);
		this.add(m_MsettingsMenu);
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
	
}
