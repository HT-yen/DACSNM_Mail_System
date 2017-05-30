/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

import client.auth.CheckAuth;
import client.connetion.ConnectionSocket;
import client.pop3.GetMailPOP3;
import mailObject.mail;

public class Login extends JFrame implements ActionListener{
	private JLabel lb1,lb2,lb3,lb4;
	   private JButton login,reset,exit,register;
	   private JTextField tf1;
	   private JPasswordField tf2;
	   private JPanel pn1,pn2,pn3,pn;
	   public String host;
     public Login(String host){
     	setTitle("Login screen");
     	this.host=host;
     	setLocation(500,200);
     	setVisible(true);
     	setResizable(false);
     	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     	 lb1=new JLabel("LOGIN SCREEN ");
         lb2=new JLabel("Email: ");
         lb3=new JLabel( "Password");
         
         tf1=new JTextField("@abc");
         tf2=new JPasswordField();
         ImageIcon icon= new ImageIcon("image/button.png");
         login=new JButton("",new ImageIcon("image/login.png"));
         login.setPreferredSize( new Dimension(icon.getIconWidth(), icon.getIconHeight()));
//         login.setText("Login");
         reset=new JButton("",new ImageIcon("image/reset.png"));
         reset.setPreferredSize( new Dimension(icon.getIconWidth(), icon.getIconHeight()));
         exit=new JButton("",new ImageIcon("image/exit.png"));
         exit.setPreferredSize( new Dimension(icon.getIconWidth(), icon.getIconHeight()));
         register=new JButton("",new ImageIcon("image/register.png"));
         register.setPreferredSize( new Dimension(icon.getIconWidth(), icon.getIconHeight()));
         
         login.addActionListener(this);
         exit.addActionListener(this);
         reset.addActionListener(this);
         register.addActionListener(this);
         pn1=new JPanel(new FlowLayout());
         pn2=new JPanel(new GridLayout(3,2));
         pn3=new JPanel(new FlowLayout());
         pn=new JPanel(new GridLayout(3,1));
         pn1.setBackground(Color.CYAN);
         pn2.setBackground(Color.CYAN);
         pn3.setBackground(Color.CYAN);
         
         pn1.add(lb1);
         pn2.add(lb2);
         pn2.add(tf1);
         pn2.add(lb3);
         pn2.add(tf2);
        
         pn3.add(login);
         pn3.add(reset);
         pn3.add(exit);
         pn3.add(register);
         pn.add(pn1);
         pn.add(pn2);
         pn.add(pn3);
         tf1.requestFocus();
         add(pn);
         setSize(400,300);
         
     }
		@Override
		public void actionPerformed(ActionEvent e) {
			try{
			if(e.getSource()==login){
				String  user=tf1.getText().toString().trim();
				String  pass=tf2.getText().toString().trim();
				try {
					CheckAuth au=new CheckAuth();
					au.connect(host, 32);
					if(au.command(user, pass)) {
						dispose();
						new MailBox(user,pass,host);
					}
					else 
					{
						
						tf1.setText("@abc");
						tf2.setText("");
						JOptionPane.showMessageDialog(null,"No see you account!");
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			}
			catch(Exception e1){
				JOptionPane.showMessageDialog(null,"null error");
			}
			if(e.getSource()==register){
				setVisible(false);
				new Register(host);
			}
			if(e.getSource()==reset){
				tf1.setText("@abc");
				tf2.setText("");
			}
			if(e.getSource()==exit) System.exit(0);
			
			
		}
	}

