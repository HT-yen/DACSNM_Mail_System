/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.sun.glass.ui.Screen;
import com.sun.javafx.font.FontStrike;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.javafx.sg.prism.NGLightBase;
import com.sun.javafx.sg.prism.NodePath;
import com.sun.prism.BasicStroke;
import com.sun.prism.CompositeMode;
import com.sun.prism.Graphics;
import com.sun.prism.RTTexture;
import com.sun.prism.RenderTarget;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.Paint;

import client.auth.CheckAuth;
import client.auth.CreateAuth;
import client.connetion.ConnectionSocket;
import client.smtp.SendMailSMTP;
import sun.java2d.pipe.DrawImage;

/**
 *
 * @author DungVan
 */
public class Register extends javax.swing.JFrame implements ActionListener{
	JButton ok, reset, cancel;
	JLabel lb1, lb2, lb3, lb4,lb5, lb6, lb7;
	JTextField tf1, tf2,tf3;
	JPanel pn1, pn2, pn3, pn;
	public Register() {
		setTitle("Register account screen");
		setLocation(500, 200);
		setVisible(true);
		setResizable(true);

		lb1 = new JLabel("REGISTER ACCOUNT SCREEN ");
		lb2 = new JLabel("Email: ");
		lb3 = new JLabel("Phone number:");
		lb4 = new JLabel("Password");
		lb5 = new JLabel("");
		lb6 = new JLabel("");
		lb7 = new JLabel("");

		tf1 = new JTextField();
		tf2 = new JTextField();
		tf3=new JTextField();

		ok = new JButton("Sign up");
		reset = new JButton("Reset");
		cancel = new JButton("Cancel");

		ok.addActionListener(this);
		cancel.addActionListener(this);
		reset.addActionListener(this);

		pn1 = new JPanel(new FlowLayout());
		pn2 = new JPanel(new GridLayout(4, 3));
		pn3 = new JPanel(new FlowLayout());
		pn = new JPanel(new GridLayout(4, 1));
		pn1.add(lb1);
		pn2.add(lb2);
		pn2.add(tf1);
		pn2.add(lb5);
		pn2.add(lb3);
		pn2.add(tf2);
		pn2.add(lb6);
		pn2.add(lb4);
		pn2.add(tf3);
		pn2.add(lb7);
		pn3.add(ok);
		pn3.add(reset);
		pn3.add(cancel);
		pn.add(pn1);
		pn.add(pn2);
		pn.add(pn3);
		add(pn);
		setSize(400, 300);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == ok) {
				String user=tf1.getText().toString();
				String sdt=tf2.getText().toString();
				String pass=tf3.getText().toString();
				boolean checkInfomation=true;
				if(user.length()==0) {
					lb5.setText("You must fill in!");
					tf1.requestFocus();
					checkInfomation=false;
					}
				else lb5.setText("");
				if(sdt.length()==0) {
					lb6.setText("You must fill in!");
					tf2.requestFocus();
					checkInfomation=false;
					}
				else lb6.setText("");
				if(pass.length()==0) {
					lb7.setText("You must fill in!");
					tf3.requestFocus();
					checkInfomation=false;
					}
				else lb7.setText("");
				if(checkInfomation){
					try {
						CreateAuth au=new CreateAuth();
						au.connect("localhost", 32);
						if(au.command(user, pass)) {
							JOptionPane.showMessageDialog(null, "register success!");
							dispose();
							new MailBox(user,pass);
						}
						else 
						{							
							tf1.setText("");
							tf2.setText("");
							tf3.setText("");
							JOptionPane.showMessageDialog(null,"Cannot create this account!");
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "null error");
		}
		if (e.getSource() == reset) {
			tf1.setText("");
			tf2.setText("");
			tf3.setText("");
		}
		if (e.getSource() == cancel)
			setVisible(false);

	}
}
