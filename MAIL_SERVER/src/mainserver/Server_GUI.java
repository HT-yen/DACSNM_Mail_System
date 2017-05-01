package mainserver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Server_GUI extends JFrame implements ActionListener {
	JTextArea ta;
	JPanel pn, pn1, pn2;
	JButton on, off,exit;
	public static DefaultListModel<String> model = new DefaultListModel<String>();;
	public static JList<String> command;
	JScrollPane Jscroll;
	MainServer ms=new MainServer();

	public Server_GUI() {
		setTitle("YOUR MAILBOX");
		setLocation(300, 100);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		on = new JButton("ON");
		off = new JButton("OFF");
		exit = new JButton("EXIT");
		on.addActionListener(this);
		off.addActionListener(this);
		exit.addActionListener(this);
		off.setEnabled(false);
		command = new JList<>();
		command.setBackground(Color.WHITE);
		command.setModel(model);
		command.setForeground(new Color(255, 0, 0));
		command.setFont(new Font("Consolas", 0, 17));
		command.setBackground(new Color(203, 241, 241));
		command.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		Border loweredBevel = BorderFactory.createLoweredBevelBorder();
		command.setBorder(loweredBevel);
		pn1 = new JPanel(new FlowLayout());
		Jscroll=new JScrollPane(command);
		pn1.add(on);
		pn1.add(off);
		pn1.add(exit);
		pn = new JPanel(new GridLayout(1, 2));
		pn.add(pn1);
		pn.add(Jscroll);
		add(pn);
		setSize(500, 300);
//		setResizable(false);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			if (e.getSource() == on) {
				off.setEnabled(true);
				on.setEnabled(false);
				ms.start(25, 110, 143, 32);;
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "error");
		}
		if (e.getSource() == off) {
			try {
				on.setEnabled(true);
				off.setEnabled(false);
				if(ms.offServer()) JOptionPane.showMessageDialog(null, "turn off server success!");
				else JOptionPane.showMessageDialog(null, "cannot turn off server!");
				model.removeAllElements();
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "error");
		}
		}
		if (e.getSource() == exit) {
			System.exit(0);
			}
	}
	public static void addElementModel(String element) {
		model.addElement(element);
	}
	public static void main(String []args)
	{
		new Server_GUI();
	}
}
