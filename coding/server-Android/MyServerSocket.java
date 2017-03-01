import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class MyServerSocket {

	public static void main(String[] args) {
		new MyServerListener().start();
		
	}

}
