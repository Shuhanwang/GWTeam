package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import connect.DBConnect;
import connect.message;



public class login extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String myName;
	private static String myPass;
	private Socket socket;
	private JPanel contentPane;
	private String localIp = "192.168.191.1";
	private JTextField utf;
	private JTextField ptf;
	private Connection conn = null;

	public login() {
		conn = DBConnect.dbConn("root", "19930805");

		setTitle("HICHAT LOGIN");
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			//localIp = addr.getHostAddress();
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}
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
		userName.setBounds(107, 70, 75, 30);
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
		JButton btnOk = new JButton("LOGIN");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new Socket(localIp,2020);
					myName = utf.getText();
					myPass = ptf.getText();
					if(myName.equals("")){
						JOptionPane.showMessageDialog(null, "PLEASE ENTER YOUR USERNAME.");
						return;
					}
					if (myPass.equals("")) {
						JOptionPane.showMessageDialog(null, "PLEASE ENTER YOUR PASSWORD.");
						return;
					}
					String sql = "select * from user where name = '" + myName + "'";
					ResultSet rs = execSql(sql);
					if (rs.next()) {
						String ts = rs.getString("pass");
						if (myPass.equals(ts)) {
							new ObjectOutputStream(socket.getOutputStream()).writeObject(new message(myName, localIp));
							login.this.dispose();
							new clientTrans(socket, myName);
						} else {
							JOptionPane.showMessageDialog(null, "WRONG PASSWORD!");
							return;
						}
					} else {
						JOptionPane.showMessageDialog(null, "YOU HAVEN'T REGISTERED TO HICHAT.");
						login.this.dispose();
						new signin();
					}
				} catch (IOException | SQLException e1) {
					JOptionPane.showMessageDialog(null, "THE SERVER IS CLOSED.");
					System.exit(1);
				}
				
			}
		});
		btnOk.setBounds(107, 166, 85, 23);
		panel.add(btnOk);
		
		JButton btnCancle = new JButton("CANCEL");
		btnCancle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});
		btnCancle.setBounds(242, 166, 85, 23);
		panel.add(btnCancle);
		
		this.setVisible(true);
		
	}
	
	public ResultSet execSql(String sql) {
		PreparedStatement ps;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
			rs = ps.getResultSet();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static void main(String[] args){
		Locale.setDefault(Locale.ENGLISH); 
		new login();
	}
}
