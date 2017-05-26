/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import client.smtp.SendMailSMTP;

public class SendMail_GUI extends JFrame implements ActionListener {
	JButton send, reset, cancel;
	JLabel lb1, lb2, lb3, lb4;
	JTextField tf1, tf2;
	JTextArea ta;
	JPanel pn1, pn2, pn3, pn4, pn;
	String user_mail;

	public SendMail_GUI(String user) {
		this.user_mail = user;
		initComponents();
	}

	private void initComponents() {
		setTitle("Send mail screen");
		setLocation(500, 200);
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		lb1 = new JLabel("SEND MAIL SCREEN ");
		lb2 = new JLabel("To: ");
		lb3 = new JLabel("Subject:");
		lb4 = new JLabel("Data:");
		tf1 = new JTextField();
		tf2 = new JTextField();
		ta = new JTextArea(50, 30);
		send = new JButton("Send");
		reset = new JButton("Reset");
		cancel = new JButton("Cancel");
		send.addActionListener(this);
		cancel.addActionListener(this);
		reset.addActionListener(this);
		pn1 = new JPanel(new BorderLayout());
		pn2 = new JPanel(new GridLayout(3, 2));
		pn3 = new JPanel(new BorderLayout());
		pn4 = new JPanel(new FlowLayout());
		pn = new JPanel(new GridLayout(4, 1));
		pn1.add(lb1, BorderLayout.CENTER);
		pn2.add(lb2);
		pn2.add(tf1);
		pn2.add(lb3);
		pn2.add(tf2);
		pn3.add(lb4, BorderLayout.WEST);
		pn3.add(ta);
		pn4.add(send);
		pn4.add(reset);
		pn4.add(cancel);
		pn.add(pn1);
		pn.add(pn2);
		pn.add(pn3);
		pn.add(pn4);
		add(pn);
		setSize(400, 300);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == send) {
				SendMailSMTP sm = new SendMailSMTP();
				sm.connect("localhost", 25);
				if (sm.command(user_mail, tf1.getText().toString(), tf2.getText().toString(), ta.getText().toString()))
					if (sm.getSendAlready())
						JOptionPane.showMessageDialog(null, "sent successful");
					else {
						JOptionPane.showMessageDialog(null, "receiver not exist in servermail");
					}
				dispose();
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "null error");
		}
		if (e.getSource() == reset) {
			tf1.setText("");
			tf2.setText("");
			ta.setText("");
		}
		if (e.getSource() == cancel)
			setVisible(false);
	}
}
