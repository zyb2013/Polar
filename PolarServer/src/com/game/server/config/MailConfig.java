package com.game.server.config;

public class MailConfig {
	//服务器地址
	private String host;
	//端口
	private String port;
	//超时时间（毫秒）
	private String timeout;
	//默认用户
	private String defaultuser;
	//默认发件人
	private String defaultfrom;
	//用户名
	private String username;
	//密码
	private String password;
	//发件人
	private String from;
	
	
	
	
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getTimeout() {
		return timeout;
	}
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
	public String getDefaultuser() {
		return defaultuser;
	}
	public void setDefaultuser(String defaultuser) {
		this.defaultuser = defaultuser;
	}
	public String getDefaultfrom() {
		return defaultfrom;
	}
	public void setDefaultfrom(String defaultfrom) {
		this.defaultfrom = defaultfrom;
	}
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
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
}
