package newserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class receive implements Runnable{
    private  DataInputStream dis;    //inputStream
    private boolean isRunning=true;    //flag
	public receive(){
		
	}
	public receive(Socket c){
		try {
			dis=new DataInputStream(c.getInputStream());
		} catch (IOException e) {
			//e.printStackTrace();
			isRunning=false;
			closeUtility.closeAll(dis);
		}
	}
	public String Receive(){
		String msg="";
		try {
			msg=dis.readUTF();
		} catch (IOException e) {
			//e.printStackTrace();
			isRunning=false;
			closeUtility.closeAll(dis);
		}
		return msg;
	}
   @Override
	public void run() {
		while(isRunning){
			System.out.println(Receive());
		}
	}

}
