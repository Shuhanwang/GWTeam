package connect;

import java.io.Serializable;

public class message implements Serializable{
	private static final long serialVersionUID = -5170616131030479811L;
	private String name;
	private String ip;
	private int port;
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public message(String name,String ip){
		this.name = name;
		this.ip = ip;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}

}
