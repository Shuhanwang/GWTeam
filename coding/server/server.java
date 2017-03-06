package server0306;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class server {
	private List<MyChannel> all=new ArrayList<MyChannel>();
	public static void main(String[] arguments) throws IOException{
		new server().start();
	}
	
	public void start() throws IOException{
		ServerSocket s=new ServerSocket(9898);
		while(true){
		    Socket c=s.accept();
		    MyChannel channel=new MyChannel(c);
		    all.add(channel);
		    new Thread(channel).start();
		}
	}
	private class MyChannel implements Runnable{
		private DataInputStream dis;
		private DataOutputStream dos;
		private boolean isRunning=true;
		private String name;
		public MyChannel(Socket c){
			try{
			     dis=new DataInputStream(c.getInputStream());
			     dos=new DataOutputStream(c.getOutputStream());
			     this.name=dis.readUTF();
			     this.send("Welcome! Dear "+ this.name);
			     sendOthers(this.name + " has logged in the chatroom");
			   }catch(IOException e){
				   closeUtility.closeAll(dis,dos);
				   isRunning=false;
			   }
		}
		
		private String receive(){
			String msg="";
			try {
				msg=dis.readUTF();
			} catch (IOException e) {
				//e.printStackTrace();
				   closeUtility.closeAll(dis);
				   isRunning=false;
				   all.remove(this);
			}
			return msg;
		}
		
		private void send(String msg){
			if(null==msg||msg.equals("")){
				return;
			}
			try {
				dos.writeUTF(msg);
				dos.flush();
			} catch (IOException e) {
				//e.printStackTrace();
				   closeUtility.closeAll(dos);
				   isRunning=false;
				   all.remove(this);
			}
		}
		private void sendOthers(String msg){
			System.out.println(name+": "+msg+"--------");
			if(msg.startsWith("@")&&msg.indexOf(":")>-1){
				String name=msg.substring(1, msg.indexOf(":"));
				String content=msg.substring(msg.indexOf(":")+1);
				for(MyChannel other:all){
					if(other.name.equals(this.name)){
						other.send(this.name+" talked secretly to you: "+ content);
					}
				}
			}else{
			  for(MyChannel other:all){
				  if(other==this){
					  continue;
				  }  
					  other.send(this.name+" talked to everyone: "+msg);
			  }
			}
		}
		public void run(){
			while(isRunning){
				sendOthers(receive());
			}
		}
	}
}
