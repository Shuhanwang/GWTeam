import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class ChatSocket extends Thread {
 Socket socket;
 public ChatSocket(Socket s) {
	// TODO Auto-generated constructor stub
	 this.socket = s;
}
  public void out(String out) {
	  try {
		socket.getOutputStream().write(out.getBytes("UTF-8"));
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
 @Override
 public void run() {
//	 try {
//		
//		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//		int count = 0;
//		while (true) {
//			bw.write("loop" + count);
//			sleep(1000);
//			
//		}
//		
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (InterruptedException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	 int count = 0;
	 try {
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		String line = null;  
		while((line = br.readLine()) != null) {
			ChatManager
			.getChatManager().publish(this, line);
//			  count++;
//			  out("loop" + count);
//			  try {
//				sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			  
		  }
		br.close();
		
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 

	 
 }
 
}
