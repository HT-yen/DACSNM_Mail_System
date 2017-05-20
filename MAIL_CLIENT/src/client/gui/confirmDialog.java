package client.gui;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class confirmDialog extends JFrame {
	JButton del=new JButton("DEL");
	JButton cancel=new JButton("CANCEL");
	public confirmDialog(String selectedMailbox) {
		JPanel pn=new JPanel(new FlowLayout());
		JLabel lb=new JLabel("      Do you want to delete '"+selectedMailbox+"'      ");
		pn.add(lb);
		pn.add(del);
		pn.add(cancel);
		add(pn);
		setSize(200, 100);
		setResizable(false);
		setVisible(true);
		setLocation(800, 500);
	}
	public JButton getDEL() {
		return del;
	}
	public JButton getCancel() {
		return cancel;
	}
}
