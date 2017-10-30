package com.vstack.beans;

public class OpenstackConnection {
	private String server = null;
	//private String port = null;
	private String username = null;
	private String password = null;
	
	private String arg1 = null;
	private String arg2 = null;

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	/*public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
*/
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	public String getArg1() {
		return arg1;
	}

	public void setArg1(String arg1) {
		this.arg1 = arg1;
	}

	public String getArg2() {
		return arg2;
	}

	public void setArg2(String arg2) {
		this.arg2 = arg2;
	}

	@Override
	public String toString() {
		return String.format("{server:%s}", server);
	}
}
