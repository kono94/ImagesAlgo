package mvc;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CenterPopupMenu extends JPopupMenu {
	private JMenuItem m_TranslateRandom;
	private JMenuItem m_TranslateValue;

	private JMenuItem m_ShearingXRandom;
	private JMenuItem m_ShearingYRandom;
	private JMenuItem m_ShearingValue;

	private JMenuItem m_RotateRandom;
	private JMenuItem m_RotateValue;

	private JMenuItem m_ScalingRandom;
	private JMenuItem m_ScalingValue;
	private JLabel m_SelectionLabel;
	private JMenuItem m_CutOut;

	public CenterPopupMenu() {
		setBorder(new EmptyBorder(5, 20, 5, 0));
		m_SelectionLabel = new JLabel("MORPH SELECTION", SwingConstants.CENTER);
		m_SelectionLabel.setBorder(new EmptyBorder(5, 0, 5, 5));
		m_CutOut = new JMenuItem(" CutOut ");
		m_TranslateRandom = new JMenuItem(" Translate Random ");
		m_TranslateValue = new JMenuItem(" Translate Custom ");
		m_ShearingXRandom = new JMenuItem(" Shearing X Random ");
		m_ShearingYRandom = new JMenuItem(" Shearing Y Random ");
		m_ShearingValue = new JMenuItem(" Shearing Custom ");
		m_RotateRandom = new JMenuItem(" Rotate Random ");
		m_RotateValue = new JMenuItem(" Rotate Custom ");
		m_ScalingRandom = new JMenuItem(" Scaling Random ");
		m_ScalingValue = new JMenuItem(" Scaling Custom ");
		
		add(m_SelectionLabel);
		add(m_CutOut);
		addSeparator();
		add(m_TranslateRandom);
		add(m_TranslateValue);
		addSeparator();
		add(m_ShearingXRandom);
		add(m_ShearingYRandom);
		add(m_ShearingValue);
		addSeparator();
		add(m_RotateRandom);
		add(m_RotateValue);
		addSeparator();
		add(m_ScalingRandom);
		add(m_ScalingValue);
	}

	class PopupMouseListener extends MouseAdapter {
//		public void mousePressed(MouseEvent e) {
//			if (e.isPopupTrigger()) {
//				if(Mode.currentMode == Mode.SELECT) {
//					m_SelectionLabel.setVisible(false);
//				}else {
//					m_SelectionLabel.setVisible(true);
//				}
//				doPop(e);
//			}
//				
//		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger() && Mode.currentMode != Mode.FADING) {
				if(Mode.currentMode == Mode.SELECT) {
					m_SelectionLabel.setVisible(true);
				}else {
					m_SelectionLabel.setVisible(false);
				}
				doPop(e);
			}
				
		}

		private void doPop(MouseEvent e) {
			CenterPopupMenu.this.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public JMenuItem getMITranslateRandom() {
		return m_TranslateRandom;
	}

	public JMenuItem getMITranslateValue() {
		return m_TranslateValue;
	}

	public JMenuItem getMIShearingXRandom() {
		return m_ShearingXRandom;
	}

	public JMenuItem getMIShearingYRandom() {
		return m_ShearingYRandom;
	}

	public JMenuItem getMIShearingValue() {
		return m_ShearingValue;
	}

	public JMenuItem getMIRotateRandom() {
		return m_RotateRandom;
	}

	public JMenuItem getMIRotateValue() {
		return m_RotateValue;
	}

	public JMenuItem getMIScalingRandom() {
		return m_ScalingRandom;
	}

	public JMenuItem getMIScalingValue() {
		return m_ScalingValue;
	}
	public JMenuItem getMICutOut() {
		return m_CutOut;
	}
}
