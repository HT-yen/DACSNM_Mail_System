/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.netbeans.lib.awtextra.AbsoluteConstraints;

import client.pop3.GetMailPOP3;

public class MailBox extends javax.swing.JFrame implements ActionListener {

	private static String USER_EMAIL;
	private static String PASS_EMAIL;
	JButton TDN_POP3, logout, send,TDN_IMAP;
	JTextArea ta;
	JPanel pn, pn1,pn2;
	DefaultListModel<String> model;
	JList<String> listmail;

	public static String getUSER_EMAIL() {
		return USER_EMAIL;
	}

	public static void setUSER_EMAIL(String user) {
		USER_EMAIL = user;
	}

	public MailBox(String user, String pass) {
		this.USER_EMAIL = user;
		this.PASS_EMAIL = pass;
		setTitle("YOUR MAILBOX");
		setLocation(300, 100);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TDN_POP3 = new JButton("Thư đã nhận_POP3");
		TDN_IMAP = new JButton("Thư đã nhận_IMAP");
		logout = new JButton("Đăng xuất");
		send = new JButton("Send");
		send.setPreferredSize(new Dimension(100, 30));
		logout.setPreferredSize(new Dimension(100, 30));
		TDN_POP3.setPreferredSize(new Dimension(150, 30));
		TDN_POP3.addActionListener(this);
		TDN_IMAP.setPreferredSize(new Dimension(150, 30));
		TDN_IMAP.addActionListener(this);
		logout.addActionListener(this);
		send.addActionListener(this);
		listmail=new JList<>();
		listmail.setBackground(Color.WHITE);
		model = new DefaultListModel<String>();
		listmail.setModel(model);
		listmail.setForeground(new Color(255, 0, 0));
		listmail.setFont(new Font("Consolas", 0, 17));
		listmail.setBackground(new Color(203, 241, 241));
		listmail.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		listmail.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String nameMail = listmail.getSelectedValue();
				File file = new File("Mailbox/" + USER_EMAIL.split("@")[0].trim());
				file.mkdir();
				if (file.listFiles() == null)
					return;
				else {
					String contentTa = "";
					for (File f : file.listFiles())
						if (f.getName().equals(nameMail)) {
							try {
								BufferedReader dis = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
								// Đọc dữ liệu
								String line;
								while ((line = dis.readLine()) != null) {
									contentTa += line + "\n";
								}
								dis.close();
								ta.setText(contentTa);
							} catch (Exception ex) {
								Logger.getLogger(MailBox.class.getName()).log(Level.SEVERE, null, ex);
								return;
							}
						}
				}
			}
		});
		Border loweredBevel = BorderFactory.createLoweredBevelBorder();
		listmail.setBorder(loweredBevel);
		pn1 = new JPanel() {
			public void paintComponent(Graphics g) {
				ImageIcon icon = new ImageIcon("image/nen3.jpg");
				Dimension d = getSize();
				g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);
				setOpaque(false);
				super.paintComponent(g);
			}

		};
		pn1.setLayout(new GridLayout(4,1));
		pn1.add(FlowAddButton(send));
		pn1.add(FlowAddButton(TDN_POP3));
		pn1.add(FlowAddButton(TDN_IMAP));
		pn1.add(FlowAddButton(logout));
		ta = new JTextArea(6, 15);
		pn2=new JPanel(new GridLayout(2,1));
		pn2.add(pn1);
		pn2.add(ta);
		ta.setBorder(BorderFactory.createLineBorder(Color.RED));
		pn = new JPanel(new GridLayout(1, 2));
		pn.add(pn2);
		pn.add(listmail);
		add(pn);
		setSize(700, 500);
		setResizable(false);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			if (e.getSource() == send) {
				Thread t = new Thread() {
					public void run() {
						new SendMail_GUI(USER_EMAIL).setVisible(true);
					}
				};
				t.start();
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "error");
		}
		if (e.getSource() == TDN_POP3) {
			try {
				GetMailPOP3 pop3 = new GetMailPOP3();
				pop3.connect("localhost", 110);
				pop3.command(USER_EMAIL, PASS_EMAIL);
				ArrayList<String> allMail = pop3.getAllMail(USER_EMAIL);
				if (!model.isEmpty()){
					model.removeAllElements();
					}
					
				for (int i = 0; i < allMail.size(); i++) {
					model.addElement(allMail.get(i));
					System.out.println(model.getElementAt(i));
				}
//			listmail.setModel(model);
			}
		        catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (e.getSource() == TDN_IMAP) {
			try {
				
			}
		        catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (e.getSource() == logout) {
			dispose();
			new Login("Login Screen");

		}
	}
	public JPanel FlowAddButton(JButton bt) {
		JPanel pn=new JPanel(new FlowLayout());
		pn.add(bt);
		return pn;
		
	}
		
}
