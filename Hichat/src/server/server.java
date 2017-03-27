package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import connect.message;

public class server extends JFrame {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private ArrayList<message> loginList = new ArrayList<message>();
	private ServerSocket serversocket;
	private Map<Socket,message> socTolog = new ConcurrentHashMap<Socket,message>();
	//ALLOCATE PORT NUMBER BETWEEN CLIENTS
	//用来分配客户端之间进行通信的端口号
	private static int port = 3000;
	
	JTextArea t1 = new JTextArea();
	JTextArea t2 = new JTextArea();
	
	public server(){
		
		setTitle("HICHAT SERVER");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 10, 414, 242);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel userInfo = new JLabel("USER LIST");
		userInfo.setBounds(20, 8, 150, 15);
		panel.add(userInfo);
		
		JLabel linkInfo = new JLabel("LOGIN/LOGOUT INFORMATION");
		linkInfo.setBounds(170, 8, 200, 15);
		panel.add(linkInfo);
		
		JScrollPane sp1 = new JScrollPane();
		sp1.setBounds(20, 33, 104, 199);
		panel.add(sp1);
		
		t1.setEditable(false);
		t1.setLineWrap(true);
		sp1.setViewportView(t1);
		
		JScrollPane sp2 = new JScrollPane();
		sp2.setBounds(169, 33, 193, 199);
		panel.add(sp2);
		
		t2.setEditable(false);
		t2.setLineWrap(true);
		sp2.setViewportView(t2);
		
		this.setVisible(true);
		
		new Thread(new handleCon()).start();
	}
	
	class handleCon implements Runnable{
		@Override
		public void run() {
			try {
				//START MONITOR IN THE SERVER SIDE
				//开启服务器端监听
				serversocket = new ServerSocket(2020);
				while(true){
					//START A THREAD TO READ THE DATA FROM CLIENTS WHEN CONNECTED
					//当有客户端连接上时，运行线程来读客户端发送过来的信息
					Socket s = serversocket.accept();
					new Thread(new readClientInfo(s)).start();
				}
			}catch(Exception e){
				System.out.println("THE PORT NUMBER HAS BEEN USED.");
				System.exit(1);
			}
		}
	}
	
	class readClientInfo implements Runnable{
		private Socket s;
		public readClientInfo(Socket s){
			this.s = s;
		}
		@Override
		public void run() {
			while(true){
				try {
					Object obj = new ObjectInputStream(s.getInputStream()).readObject();
					//IF THE INFORMATION RECEIVED IS THE INSTANCE OF CLASS MESSAGE
					//当接收到的消息是message类的实例化
					if(obj instanceof message){
						message mes = (message)obj;
						int k=0;
						//WHETHER THE USERNAME IS BEEN USED
						//判断用户名是否已被使用
						for(k = 0;k<loginList.size();k++){
							if(loginList.get(k).getName().equals(mes.getName())){
								//IF THE USERNAME IS BEEN USED, SEND A MESSAGE TO THE CLIENT SIDE AND BREAK THE CONNECTION
								//当用户名被使用时，给客户端返回一个通知信息并断开连接
								new ObjectOutputStream(s.getOutputStream()).writeObject("THE USERNAME HAS BEEN USED.");
								socTolog.remove(s);
								break;
							}
						}
						//THE USERNAME IS AVAILABLE
						//用户名没有被使用
						if(k==loginList.size()){
							mes.setPort(port++);
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
							Date d = new Date();
							String str = df.format(d);
							t2.append("User "+mes.getName()+" logs in at "+str);
							t2.append("\n");
							loginList.add(mes);
							t1.setText("");
							for(message m:loginList){
								t1.append("Username: "+m.getName()+"\n");
							}
							loginList.add(mes);
							socTolog.put(s, mes);
							allocateMsg();
						}
					}else if(obj instanceof String){       
						//IF THE INPUT MESSAGE IS THE INSTANCE OF String, IT MEANS THE CONNECTION BETWEEN CLIENTS IS CLOSED
						/*输入消息是String的实例化说明客户端断开了连接*/
						String str = (String)obj;
						if(str.equals("DISCONNECTED")){
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
							Date d = new Date();
							String date = df.format(d);
							t2.append("User "+socTolog.get(s).getName()+" logs out at "+date);
							t2.append("\n");
							socTolog.remove(s);
							t1.setText("");
							allocateMsg();
							for(message m:loginList){
								t1.append("Username: "+m.getName()+"\n");
							}
							
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					break;
				}
			}
		}
		
	}
	
	//allocate user list 
	//分发用户列表消息
	public void allocateMsg(){
		loginList.clear();
		for(message m:socTolog.values()){
			loginList.add(m);
		}
		for(Socket tempS:socTolog.keySet()){
			try {
				new ObjectOutputStream(tempS.getOutputStream()).writeObject(loginList);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public static void main(String[] args){
		server s = new server();
	}
}

