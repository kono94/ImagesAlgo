package mvc;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class MyMenuBar extends JMenuBar{
	private JMenu m_MfileMenu;
	private JMenuItem m_MIopen;
	private JMenuItem m_MIloadAllImagesItem;
	private JMenu m_Mmenu;
	private JMenuItem m_MIslow;
	private JMenuItem m_MImedium;
	private JMenuItem m_MIveryFast;
		public MyMenuBar(JFrame owner){		
			m_MfileMenu = new JMenu("Files");
			m_MIopen = new JMenuItem("open");
			m_MIloadAllImagesItem = new JMenuItem("load all images in current directory");		
			m_MfileMenu.add(m_MIopen);
			m_MfileMenu.add(m_MIloadAllImagesItem);
			m_Mmenu = new JMenu("time");
			m_MIslow = new JMenuItem("slow - 1000ms");
			m_MImedium = new JMenuItem("medium - 500ms");
			m_MIveryFast = new JMenuItem("ultra fast - 30ms");	
			
			m_Mmenu.add(m_MIslow);
			m_Mmenu.add(m_MImedium);
			m_Mmenu.add(m_MIveryFast);
			this.add(m_MfileMenu);
			this.add(m_Mmenu);
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
			return m_Mmenu;
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
}
