package newserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public  class send implements Runnable{
    private BufferedReader console;    //inputStream
    private DataOutputStream dos;     //outputStream
    private boolean isRunning=true;     //flag
    public send(){
    	console=new BufferedReader(new InputStreamReader(System.in));
    }
    public send(Socket c){
    	this();
    	try {
			dos=new DataOutputStream(c.getOutputStream());
		} catch (IOException e) {
			isRunning=false;
			closeUtility.closeAll(dos,console);
		}
    }
    
    private String getMsgFromConsole(){
    	try {
			return console.readLine();
		} catch (IOException e) {
			//e.printStackTrace();
		}
    	return "";
    }
    
    public void Send(){
    	String msg=getMsgFromConsole();
    	try {
			if(null!=msg && !msg.equals("")){
				dos.writeUTF(msg);
				dos.flush();
			}
		} catch (IOException e) {
			//e.printStackTrace();
			isRunning=false;
			closeUtility.closeAll(dos,console);
		}
    }
	@Override
	public void run() {
		while(isRunning){
			Send();
		}
	}

}
