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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.netbeans.lib.awtextra.AbsoluteConstraints;

import client.imap.GetMailIMAP;
import client.pop3.GetMailPOP3;
import client.gui.*;;

public class MailBox extends javax.swing.JFrame implements ActionListener {

	private static String USER_EMAIL;
	private static String PASS_EMAIL;
	JButton TDN_POP3, logout, send, TDN_IMAP, add, copy, paste, delete, ok, cancel;
	JTextArea ta, namMB;
	JPanel pn, pn1, pn2, pn3, pn4;
	DefaultListModel<String> model, model1;
	JList<String> listmail, listmailbox;
	JScrollPane Jscroll;
	int IMAP_or_POP3 = 0;// 0: default 1: POP3 2: IMAP
	GetMailIMAP imap;
	GetMailPOP3 pop3;
	confirmDialog cd;
	String NameMailCopy,NameMailBoxPaste,contentmail;
	boolean iscopy=false;
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
		namMB = new JTextArea(3, 20);
		namMB.setText("Nhập tên hộp thư cần tạo");
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
		listmail = new JList<>();
		listmailbox = new JList<>();
		listmail.setBackground(Color.WHITE);
		model = new DefaultListModel<String>();
		model1 = new DefaultListModel<String>();
		listmail.setModel(model);
		listmail.setForeground(new Color(255, 0, 0));
		listmail.setFont(new Font("Consolas", 0, 17));
		listmail.setBackground(new Color(203, 241, 241));
		listmail.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		listmail.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (!model.isEmpty()) {
					if (IMAP_or_POP3 == 1) // POP3
					{
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

										BufferedReader dis = new BufferedReader(
												new InputStreamReader(new FileInputStream(f), "UTF-8"));
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
					} else if (IMAP_or_POP3 == 2) {
						copy.setEnabled(true);
						String nameMail = listmail.getSelectedValue();
						String[] contentmail = imap.getMessageContent(nameMail).split("\\.");
						ta.setText(contentmail[0] + "\n" + contentmail[1] + "\n" + contentmail[2] + "\n"
								+ contentmail[3] + "\n" + contentmail[4]);
					}
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
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
		pn1.setLayout(new GridLayout(4, 1));
		pn1.add(FlowAddButton(send));
		pn1.add(FlowAddButton(TDN_POP3));
		pn1.add(FlowAddButton(TDN_IMAP));
		pn1.add(FlowAddButton(logout));
		ta = new JTextArea(6, 15);
		pn2 = new JPanel(new GridLayout(2, 1));
		pn2.add(pn1);
		pn2.add(ta);
		ta.setBorder(BorderFactory.createLineBorder(Color.RED));
		pn = new JPanel(new GridLayout(1, 2));
		pn.add(pn2);
		Jscroll = new JScrollPane(listmail);
		pn3 = new JPanel(new GridLayout(2, 1));
		add = new JButton("add");
		delete = new JButton("del");
		copy = new JButton("copy");
		paste = new JButton("paste");
		ok = new JButton("OK");
		cancel = new JButton("CANCEL");
		add.setEnabled(false);
		delete.setEnabled(false);
		copy.setEnabled(false);
		paste.setEnabled(false);
		ok.setEnabled(false);
		cancel.setEnabled(false);
		add.addActionListener(this);
		delete.addActionListener(this);
		copy.addActionListener(this);
		paste.addActionListener(this);
		ok.addActionListener(this);
		cancel.addActionListener(this);
		pn3.add(Jscroll);
		JPanel pn4 = new JPanel(new FlowLayout());
		pn4.add(listmailbox);
		pn4.add(add);
		pn4.add(delete);
		pn4.add(copy);
		pn4.add(paste);
		pn4.add(ok);
		pn4.add(cancel);
		pn4.add(namMB);
		listmailbox.setVisible(false);
		namMB.setVisible(false);
		listmailbox.setModel(model1);
		model1.addElement("11");
		model1.addElement("12");
		listmailbox.setFont(new Font("Consolas", 0, 14));
		listmailbox.setBackground(new Color(213, 200, 200));
		listmailbox.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		listmailbox.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				delete.setEnabled(true);
				if (!model.isEmpty()) {
					model.removeAllElements();
				}
				if (!model1.isEmpty()) {
					ta.setText("");
					String nameMailbox = listmailbox.getSelectedValue();
					if (imap != null)
						imap.selectMailbox(nameMailbox);
					ArrayList<String> allMail = imap.getAllMail();
					for (int i = 0; i < allMail.size(); i++) {
						model.addElement(allMail.get(i));
					}
				}
				if(copy.isEnabled()&&(iscopy))
					if(NameMailCopy!=null) paste.setEnabled(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		listmailbox.setVisible(false);
		pn3.add(pn4);
		pn.add(pn3);
		add(pn);
		setSize(700, 500);
		setResizable(false);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == ok) {
				if (imap != null) {
					if (imap.addMailbox(namMB.getText().toString().trim()))
						JOptionPane.showMessageDialog(null, "Create successfuly!");
					else
						JOptionPane.showMessageDialog(null, "Create failure!");
					namMB.setText("");
				} else {
					JOptionPane.showMessageDialog(null, "IMAP not ready! try later!");
				}
			}
			if (e.getSource() == cancel) {
				namMB.setText("");
				namMB.setVisible(false);
				ok.setEnabled(false);
				cancel.setEnabled(false);
			}
			if (e.getSource() == add) {
				namMB.setVisible(true);
				ok.setEnabled(true);
				cancel.setEnabled(true);
			}
			if (e.getSource() == delete) {
				String selectedMalbox = listmailbox.getSelectedValue();
				cd = new confirmDialog(selectedMalbox);
				cd.getDEL().addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (imap != null)
							if (imap.delMailbox(selectedMalbox))
								JOptionPane.showMessageDialog(null, "delete successfuly!");
							else {
								JOptionPane.showMessageDialog(null, "delete failture!");
							}
						cd.dispose();
						delete.setEnabled(false);
					}
				});
				cd.getCancel().addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						cd.dispose();
						delete.setEnabled(false);
					}
				});
			}

			if (e.getSource() == copy) {
				iscopy=true;
				NameMailCopy=listmail.getSelectedValue();
			    contentmail = imap.getMessageContent(NameMailCopy);
			    if(contentmail.equals("")) JOptionPane.showMessageDialog(null, "Choice message to copy!");
			}
			if (e.getSource() == paste) {
				NameMailBoxPaste=listmailbox.getSelectedValue();
				if((NameMailBoxPaste!=null)&&((NameMailCopy!=null))&&(imap!=null))
					if(imap.copyMail(NameMailCopy, NameMailBoxPaste, contentmail))JOptionPane.showMessageDialog(null, "Copy successfully!");
					else JOptionPane.showMessageDialog(null, "Copy failure!");
				copy.setEnabled(false);
				paste.setEnabled(false);
				iscopy=false;
			}
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
				add.setEnabled(false);
				delete.setEnabled(false);
				copy.setEnabled(false);
				paste.setEnabled(false);
				ok.setEnabled(false);
				cancel.setEnabled(false);
				namMB.setVisible(false);
				listmailbox.setVisible(false);
				if (!model.isEmpty()) {
					model.removeAllElements();
				}
				if (!model1.isEmpty()) {
					model1.removeAllElements();
				}
				ta.setText("");
				if (imap != null)
					imap.closeConnect();
				// cần close imap thì nó luôn online còn pop3 connect rồi tự
				// động đóng connect rồi
				IMAP_or_POP3 = 1;
				pop3 = new GetMailPOP3();
				pop3.connect("localhost", 110);
				pop3.command(USER_EMAIL, PASS_EMAIL);
				ArrayList<String> allMail = pop3.getAllMail(USER_EMAIL);
				for (int i = 0; i < allMail.size(); i++) {
					model.addElement(allMail.get(i));
					System.out.println(model.getElementAt(i));
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if (e.getSource() == TDN_IMAP) {
			try {
				iscopy=false;
				add.setEnabled(true);
				paste.setEnabled(false);
				copy.setEnabled(false);
				delete.setEnabled(false);
				ok.setEnabled(false);
				cancel.setEnabled(false);
				listmailbox.setVisible(true);
				if (!model.isEmpty()) {
					model.removeAllElements();
				}
				if (!model1.isEmpty()) {
					model1.removeAllElements();
				}
				ta.setText("");
				if (imap != null)
					imap.closeConnect();
				IMAP_or_POP3 = 2;
				imap = new GetMailIMAP();
				imap.connect("localhost", 143);
				if (imap.command(USER_EMAIL, PASS_EMAIL)) {
					ArrayList<String> allMailBox = imap.getAllMailBox();
					for (int i = 0; i < allMailBox.size(); i++) {
						model1.addElement(allMailBox.get(i));
					}
				}
			} catch (Exception e1) {
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
		JPanel pn = new JPanel(new FlowLayout());
		pn.add(bt);
		return pn;

	}

}
