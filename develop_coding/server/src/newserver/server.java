package newserver;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class server {

	public static void main(String[] arguments) throws IOException{
		ServerSocket s=new ServerSocket(9898);
		while(true){
		    Socket socket=s.accept();
		    DataInputStream dis=new DataInputStream(socket.getInputStream());
		    DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
		    
		    while(true){ 
		        String msg=dis.readUTF();
		        System.out.println(msg);
		        dos.writeUTF("server-->"+msg);
		        dos.flush();
		        }
		}
	}
}
