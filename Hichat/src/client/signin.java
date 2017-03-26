package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import connect.DBConnect;

public class signin extends JFrame {
	private JPanel contentPane;
	private JTextField utf;
	private JTextField ptf;
	private Connection conn = null;

	public signin() {
		conn = DBConnect.dbConn("root", "19930805");

		setTitle("HICHAT REGISTER");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(700, 380, 450, 300);
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
		
		JLabel logo = new JLabel();
		logo.setIcon(new ImageIcon("pic/logo.PNG"));
		logo.setBounds(200,15,40,40);
		panel.add(logo);
		
		JLabel userName = new JLabel("USERNAME");
		userName.setBackground(Color.WHITE);
		userName.setBounds(107, 70, 90, 30);
		panel.add(userName);
		JLabel passwd = new JLabel("PASSWORD");
		passwd.setBackground(Color.WHITE);
		passwd.setBounds(107, 110, 90, 30);
		panel.add(passwd);
		
		utf = new JTextField();
		utf.setBounds(209, 70, 109, 30);
		panel.add(utf);
		utf.setColumns(12);
		
		ptf = new JTextField();
		ptf.setBounds(209, 110, 109, 30);
		panel.add(ptf);
		ptf.setColumns(12);

		//ADD A LISTENER FOR THE BUTTON, SEND A MESSAGE TO THE SERVER TO CHECK WHETHER IT COULD BE CONNECTED WHEN CLICK THE BUTTON
		//给按钮添加监听器，当点击按钮时发送一个message消息给服务器端以便确认是否能够连接
		JButton btnOk = new JButton("REGISTER");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = utf.getText();
				String pass = ptf.getText();
				try {
					conn.setAutoCommit(false);
					PreparedStatement ps = conn.prepareStatement("insert into user(name,pass) values(?,?)");
					ps.setString(1, name);
					ps.setString(2, pass);
					ps.execute();
					conn.commit();
					JOptionPane.showMessageDialog(null, "WELCOME TO HICHAT! PLEASE REMEMBER YOUR USERNAME AND PASSWORD.");
					signin.this.dispose();
					login login = new login();
				} catch (Exception e1) {
					try {
							conn.rollback();
						JOptionPane.showMessageDialog(null, "THIS USERNAME IS EXISTED! PLEASE CHANGE ANOTHER ONE.");
					} catch (SQLException e2) {
						e2.printStackTrace();
					}
					e1.printStackTrace();
				} finally {
					try {
						conn.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		btnOk.setBounds(107, 166, 100, 23);
		panel.add(btnOk);
		
		JButton btnCancle = new JButton("CANCEL");
		btnCancle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});
		btnCancle.setBounds(242, 166, 100, 23);
		panel.add(btnCancle);
		
		this.setVisible(true);
		
	}

}
