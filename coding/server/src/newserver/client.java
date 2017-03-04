package newserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class client {
	public static void main(String[] arguments) throws UnknownHostException, IOException{
		Socket c=new Socket("localhost",9898 ); 
		new Thread(new send(c)).start();
		new Thread(new receive(c)).start();
	}
}
