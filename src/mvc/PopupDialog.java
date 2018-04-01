package mvc;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class PopupDialog extends JDialog {
	private int m_input1;
	private int m_input2;
	private double m_doubleInput1;
	private double m_doubleInput2;
	private boolean m_returnTwoValues;
	private boolean m_returnAsDouble;
	private boolean m_quit;
	private boolean m_spinAroundMiddle;
	private String m_labelText1;
	private String m_labelText2;
	private String m_infoText;
	private JButton okButton;
	

	public PopupDialog(JFrame owner, String title, String labelText1, String labelText2, String infoText, boolean returnAsDouble) {
		super(owner, title, true);
		m_returnTwoValues = true;
		m_returnAsDouble = returnAsDouble;
		m_labelText1 = labelText1;
		m_labelText2 = labelText2;
		m_infoText = infoText;
		buildLayout(owner);
	}

	public PopupDialog(JFrame owner, String title, String labelText1, String infoText, boolean returnAsDouble) {
		super(owner, title, true);
		m_returnTwoValues = false;
		m_returnAsDouble = returnAsDouble;
		m_labelText1 = labelText1;
		m_infoText = infoText;
		buildLayout(owner);
	}

	private void buildLayout(JFrame owner) {
		m_quit = false;
		setResizable(false);
		setLayout(new BorderLayout());
		
		JPanel centerPanel = new JPanel();
		JLabel label1;
		JLabel label2;
		JTextField field1;
		JTextField field2;
		okButton = new JButton("OK");
		// DecimalFormat format = new DecimalFormat("0.000");
		// NumberFormat format = NumberFormat.getInstance();
		// NumberFormatter formatter = new NumberFormatter(format);
		// formatter.setAllowsInvalid(false);
		JLabel errorLabel = new JLabel();
		errorLabel.setForeground(Color.RED);

		if (m_returnTwoValues) {
			centerPanel.setLayout(new GridLayout(2, 2, 20, 20));
			label1 = new JLabel(m_labelText1, SwingConstants.CENTER);
			centerPanel.add(label1);
			field1 = new JTextField();
			field1.addActionListener(new EnterListener());
			centerPanel.add(field1);
			label2 = new JLabel(m_labelText2, SwingConstants.CENTER);
			centerPanel.add(label2);
			field2 = new JTextField();
			field2.addActionListener(new EnterListener());
			centerPanel.add(field2);
			okButton.addActionListener(e -> {
				try {
					m_quit = false;
					if(!m_returnAsDouble) {
						m_input1 = Integer.parseInt(field1.getText());
						m_input2 = Integer.parseInt(field2.getText());
					}else {
						m_doubleInput1 = Double.parseDouble(field1.getText());
						m_doubleInput2 = Double.parseDouble(field2.getText());
					}					
					dispose();
				} catch (Exception ex) {
					errorLabel.setText("Wrong input");
					pack();
				}
			});

		} else {
			JCheckBox spinMiddleBox = new JCheckBox();
			centerPanel.setLayout(new GridLayout(2, 2, 20, 20));
			label1 = new JLabel(m_labelText1, SwingConstants.CENTER);
			centerPanel.add(label1);
			field1 = new JFormattedTextField();
			field1.addActionListener(new EnterListener());
			centerPanel.add(field1);
			if (m_returnAsDouble) {
				centerPanel.add(new JLabel("Spin around middle", SwingConstants.CENTER));
				centerPanel.add(spinMiddleBox);
			}

			okButton.addActionListener(e -> {
				try {
					m_quit = false;
					if (m_returnAsDouble) {
						m_doubleInput1 = Double.parseDouble(field1.getText());
						if(spinMiddleBox.isSelected()) {
							m_spinAroundMiddle = true;
						}						
					} else {
						m_input1 = Integer.parseInt(field1.getText());
					}
					dispose();
				} catch (Exception ex) {
					errorLabel.setText("Wrong input");
					pack();
				}
			});
		}
		add(BorderLayout.CENTER, centerPanel);
		JLabel infoLabel = new JLabel(m_infoText, SwingConstants.CENTER);
		infoLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(BorderLayout.NORTH, infoLabel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
		buttonPanel.setLayout(new FlowLayout());
		JButton quitButton = new JButton("CANCEL");
		quitButton.addActionListener(e -> {
			m_quit = true;
			dispose();
		});
		buttonPanel.add(okButton);
		buttonPanel.add(quitButton);
		buttonPanel.add(errorLabel);
		add(BorderLayout.SOUTH, buttonPanel);
		pack();
		
		Point p = owner.getLocation();
		setLocation(p.x + owner.getWidth() / 2 - this.getWidth() / 2,
				p.y + owner.getHeight() / 2 - this.getHeight() / 2);
		
		setVisible(true);
	}

	class EnterListener implements ActionListener {
		public EnterListener() {}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			okButton.doClick();
		}
	}

	public int getIntValue1() {
		return m_input1;
	}

	public int getIntValue2() {
		return m_input2;
	}

	public double getDoubleValue1() {
		return m_doubleInput1;
	}
	
	public double getDoubleValue2() {
		return m_doubleInput2;
	}

	public boolean getSpinAroundMid() {
		return m_spinAroundMiddle;
	}

	public boolean quitDialog() {
		return m_quit;
	}

}
