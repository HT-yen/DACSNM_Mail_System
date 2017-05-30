package client.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Connect_GUI extends JFrame {
	JPanel pn = new JPanel(new FlowLayout());
	static String host = "";

	public Connect_GUI() {
		JButton ok = new JButton("OK");
		JTextArea hosttxt = new JTextArea("localhost", 1, 20);
		pn.add(hosttxt);
		pn.add(ok);
		add(pn);
		setSize(300, 100);
		setVisible(true);
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				host = hosttxt.getText().toString().trim();
				if (host.equals(""))
					JOptionPane.showMessageDialog(null, "please enter host before!");
				else {
					dispose();
					new Login(host);
				}
			}
		});
	}

	public static void main(String[] args) {
		new Connect_GUI();
	}
}
