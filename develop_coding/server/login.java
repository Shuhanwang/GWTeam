package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import message.message;
import client.clientTrans;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class login extends JFrame {

	private static String myName;
	private Socket socket;
	private JPanel contentPane;
	private String localIp = "";
	private JTextField textField;

	public login() {
		setTitle("P2P登录端");
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			localIp = addr.getHostAddress();
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(230, 230, 250));
		panel.setBounds(0, 0, 434, 262);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel userName = new JLabel("   用户名");
		userName.setBackground(Color.WHITE);
		userName.setBounds(107, 73, 75, 30);
		panel.add(userName);
		
		textField = new JTextField();
		textField.setBounds(209, 74, 109, 30);
		panel.add(textField);
		textField.setColumns(10);
		
		//给按钮添加监听器，当点击按钮时发送一个message消息给服务器端以便确认是否能够连接
		JButton btnOk = new JButton("登录");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new Socket(localIp,2020);
					myName = textField.getText();
					if(myName.equals("")){
						JOptionPane.showMessageDialog(null, "请输入用户名");
						return;
					}
					new ObjectOutputStream(socket.getOutputStream()).writeObject(new message(myName,localIp));
					login.this.dispose();
					clientTrans ct = new clientTrans(socket,myName);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "服务器未开启");
					System.exit(1);
				}
				
			}
		});
		btnOk.setBounds(107, 156, 75, 23);
		panel.add(btnOk);
		
		JButton btnCancle = new JButton("取消");
		btnCancle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});
		btnCancle.setBounds(242, 156, 75, 23);
		panel.add(btnCancle);
		
		this.setVisible(true);
		
	}
	


	
	public static void main(String[] args){
		login lg  = new login();
	}
}
