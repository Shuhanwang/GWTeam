import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class MyServerListener extends Thread {
	@Override
	public void run() {
		// 1-65535
		try {
			ServerSocket serverSocket = new ServerSocket(12345);
			while(true) {
				//block
				Socket socket = serverSocket.accept();
				//Establish connection
				JOptionPane.showMessageDialog(null, "Connection has been established");
				//socket for new thread
//				new ChatSocket(socket).start();
				ChatSocket cs = new ChatSocket(socket);
				cs.start();
				ChatManager.getChatManager().add(cs);
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
