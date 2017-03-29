package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import connect.message;

public class clientTrans extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel panel = new JPanel();
	private JPanel panel_1 = new JPanel();
	private JPanel panel_2 = new JPanel();
	private JPanel panel_3 = new JPanel();
	JTextArea rsgMes = new JTextArea();
	JTextArea sendMes = new JTextArea();
	JTextArea transFile = new JTextArea();
	private JTextField textField;
	private ArrayList<message> loginList;
	private Socket fromServer;
	private Socket toClient;
	private String desIp;
	private int desPort;
	String myName;
	private int myPort;
	private String tempName;

	private File f;

	public clientTrans(Socket fromServer, String myName) {
		
		this.myName = myName;
		this.fromServer = fromServer;
		loginList = new ArrayList<message>();
		setTitle(myName);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 641, 400);
		// Add a monitor for window, window closing means that the client logs out, then a message should be sent to server to notice that there is a client logging out.
		//给窗口增加监听器，当关闭窗口时代表客户端退出登录，此时需要给服务器端发送一个消息以便通知服务器端有客户端退出
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					new ObjectOutputStream(fromServer.getOutputStream())
							.writeObject("DISCONNECTED");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		this.setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		panel.setBackground(Color.LIGHT_GRAY);
		panel.setForeground(SystemColor.activeCaption);
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		JLabel userList = new JLabel("USER LIST");
		userList.setBounds(20, 10, 80, 15);
		panel.add(userList);

		JLabel session = new JLabel("MESSAGES");
		session.setBounds(120, 10, 100, 15);
		panel.add(session);

		panel_1.setForeground(Color.BLACK);
		panel_1.setBackground(Color.CYAN);
		panel_1.setBounds(120, 35, 248, 287);
		panel.add(panel_1);
		panel_1.setLayout(null);

		rsgMes.setLineWrap(true);
		rsgMes.setEditable(false);
		rsgMes.setBounds(10, 10, 228, 129);

		JScrollPane rsgScroll = new JScrollPane();
		rsgScroll.setBounds(10, 10, 228, 129);
		rsgScroll.setViewportView(rsgMes);
		panel_1.add(rsgScroll);

		sendMes.setLineWrap(true);
		sendMes.setBounds(10, 167, 228, 57);
		panel_1.add(sendMes);

		JLabel sendLabel = new JLabel("SENDING MESSAGES");
		sendLabel.setBounds(10, 142, 200, 15);
		panel_1.add(sendLabel);

		JButton btnSend = new JButton("SEND");
		// Add a monitor for button of sending messages. When clicked, server extracts messages from box and transmit them
		// 给文字消息发送按钮增加监听器，当点击按钮时从输入框中提取消息并进行传输
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* send */
				// Judge whether an object has been chose before sending messages
				// 判断消息发送之前是否选择了通信对象
				if (desIp == null) {
					JOptionPane.showMessageDialog(null, "PLEASE CHOOSE A USER!");
					return;
				}
				String mes = sendMes.getText().toString();
				String mes1 = sendMes.getText().toString();
				DateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date d = new Date();
				String date = df.format(d);
				mes = mes + "#%" + myName;
				sendMes.setText("");
				rsgMes.append("ME: " + "(" + date + ")" + "\n" +  mes1 + "\n" );
				try {
					new ObjectOutputStream(toClient.getOutputStream())
							.writeObject(mes);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnSend.setBounds(20, 242, 80, 23);
		panel_1.add(btnSend);

		JButton btnClear = new JButton("CLEAR");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* clear */
				sendMes.setText("");
			}
		});
		btnClear.setBounds(145, 242, 80, 23);
		panel_1.add(btnClear);

		JLabel fileTrans = new JLabel("FILE TRANSFER");
		fileTrans.setBounds(376, 10, 150, 15);
		panel.add(fileTrans);

		panel_2.setBackground(Color.ORANGE);
		panel_2.setBounds(376, 35, 210, 287);
		panel.add(panel_2);
		panel_2.setLayout(null);

		textField = new JTextField();
		textField.setEditable(false);
		textField.setBounds(10, 27, 108, 21);
		panel_2.add(textField);
		textField.setColumns(10);

		JButton btnChoose = new JButton("OPEN");
		// click to pop-up a box to choose files the user wants to transmit
		// 点击文件按钮会弹出一个文件选择对话框，进行传输文件的选择
		btnChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(clientTrans.this);
				f = chooser.getSelectedFile();
				if (f != null) {
					String path = f.getPath();
					textField.setText(path);
				}
			}
		});
		btnChoose.setBounds(126, 26, 70, 23);
		panel_2.add(btnChoose);

		JLabel filePath = new JLabel("FILE PATH");
		filePath.setBounds(10, 2, 150, 15);
		panel_2.add(filePath);

		JButton btnTrans = new JButton("SEND");
		// Add a monitor for button of file transfer
		// 给文件传输按钮增加监听器
		btnTrans.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//judge whether an object has been chose
					// 文件传输之前需要判断是否选择了通信对象
					if (desIp == null) {
						JOptionPane.showMessageDialog(null, "PLEASE CHOOSE A USER!");
						return;
					}
					if (f != null) {
						Socket tempS = new Socket(desIp, desPort);
						FileInputStream fis = new FileInputStream(f);
						DataOutputStream dos = new DataOutputStream(tempS
								.getOutputStream());
						System.out.println();
						// Length and name of the file and receiver's user name
						// 传输文件名称和长度以及发送端用户名
						new ObjectOutputStream(tempS.getOutputStream())
								.writeObject(f.length());
						dos.writeUTF(f.getName().toString());//写入磁盘文件数据
						dos.writeUTF(myName);
						// transmit
						// 传输文件
						byte[] sendBytes = new byte[1024];

						while (fis.read(sendBytes) > 0) {
							dos.write(sendBytes);
							dos.flush();
						}
						DateFormat df = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date d = new Date();
						String date = df.format(d);
						transFile.append(date + "\n" + "FILE HAS BEEN SENT SUCCESSFULLY" + "\n");

						fis.close();
						dos.close();
						tempS.close();

					}
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnTrans.setBounds(10, 64, 93, 23);
		panel_2.add(btnTrans);

		JLabel recieveList = new JLabel("FILE LIST");
		recieveList.setBounds(10, 109, 93, 15);
		panel_2.add(recieveList);

		transFile.setLineWrap(true);
		transFile.setBounds(10, 134, 190, 143);
		panel_2.add(transFile);

		JScrollPane fileScroll = new JScrollPane();
		fileScroll.setBounds(10, 134, 190, 143);
		fileScroll.setViewportView(transFile);
		panel_2.add(fileScroll);

		// User list panel
		// 用户列表panel
		panel_3.setLayout(new GridLayout(10, 1));
		panel_3.setBounds(20, 35, 90, 287);
		panel.add(panel_3);

		this.setVisible(true);

		new Thread(new readloginList()).start();
	}

	// Used to receive real-time user list sent from server
	// 用于接收服务器端实时发送过来的用户列表
	class readloginList implements Runnable {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) {
				try {
					Object obj = new ObjectInputStream(
							fromServer.getInputStream()).readObject();
					// When server sends the instantiation of Class ArrayList, it means the received message is user list
					// 当服务器端发送的是ArrayList类的实例化时，说明接收到的消息是用户列表
					if (obj instanceof ArrayList) {
						loginList = (ArrayList<message>) obj;
						panel_3.removeAll();
						for (message mes : loginList) {
							if (mes.getName().equals(myName)) {
								if (myPort == 0) {
									myPort = mes.getPort();
									new Thread(new readInfo()).start();
								}
							}
							JButton btnTemp = new JButton(mes.getName());
							btnTemp.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									tempName = ((JButton) e.getSource())
											.getText();
									//rsgMes.setText("");
									rsgMes.append("START COMMUNICATION WITH " + tempName + "\n");
									for (message m : loginList) {
										if (m.getName().equals(tempName)) {
											desIp = m.getIp();
											desPort = m.getPort();
											try {
												toClient = new Socket(desIp,
														desPort);
											} catch (UnknownHostException e1) {
												e1.printStackTrace();
											} catch (IOException e1) {
												e1.printStackTrace();
											}
										}
									}
								}

							});
							panel_3.add(btnTemp);
						}
						panel_3.updateUI();
					}
					// When an instantiation of Class String is received, it means this registering user name has been used
					// 当接收到的消息是String类的实例化时，说明该用户注册的名称已被占用
					if (obj instanceof String) {
						JOptionPane.showMessageDialog(null, "THIS ACCOUNT IS LOGGED IN!");
						dispose();
						System.exit(1);
					}
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
					System.out.println("aaoq");
					break;
				}
			}
		}
	}
	
	// Read messages from client
	// 读取客户端发送过来的消息
	class readInfo implements Runnable {
		ServerSocket serverSocket1;

		public readInfo() {
		}

		@Override
		public void run() {
			try {
				serverSocket1 = new ServerSocket(myPort);
				while (true) {
					Socket socket1 = serverSocket1.accept();
					getInfo gti = new getInfo(socket1);
					new Thread(gti).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class getInfo implements Runnable {
		private Socket s;

		public getInfo(Socket s) {
			this.s = s;
		}

		@Override
		public void run() {
			while (true) {
				try {
					Object obj = null;
					try {
						obj = new ObjectInputStream(s.getInputStream())
								.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					// When an instantiation of String is received it means the object is text message
					// 如果接收到的消息是String类的实例化，则说明接收到的消息是普通文字消息
					if (obj instanceof String) {
						DateFormat df = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date d = new Date();
						String date = df.format(d);
						String str = (String) obj;
						String[] getMes = str.split("#%"); 
						rsgMes.append("MESSAGE FROM " + getMes[1] + "\n" + "(" + date + ")" + "\n");
						rsgMes.append(getMes[0] + "\n");
					}
					// It means the object is file
					// 如果不是，则说明接收到的是文件
					else {
						Long len = (Long) obj;
						String name = new DataInputStream(s.getInputStream())
								.readUTF();
						String userName = new DataInputStream(
								s.getInputStream()).readUTF();
						FileOutputStream fos = new FileOutputStream("E:\\"
								+ name);
						byte[] readBytes = new byte[1024];
						DataInputStream read = new DataInputStream(
								s.getInputStream());
						long lenReceived = 0;
						while (lenReceived < len) {
							int rcvn = read.read(readBytes);
							lenReceived += rcvn;
							fos.write(readBytes,0,rcvn);
							fos.flush();
						}
						DateFormat df = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date d = new Date();
						String date = df.format(d);
						transFile.append(date + " FILE FROM " + userName
								+ " HAS BEEN SAVED IN E:\\" + "\n");
						read.close();
						fos.close();
						IOException e = new IOException("SSS");
						throw e;
					}
				} catch (Exception e) {
					if (e.getMessage().equals("SSS")) {
						break;
					}
				}
			}
		}
	}
}
