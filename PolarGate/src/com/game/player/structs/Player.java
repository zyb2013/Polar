package com.game.player.structs;

public class Player {
	//玩家id
	private long id;
	//玩家所在服务器
	private int server;
	//玩家创建服务器
	private int createServer;
	//玩家用户id
	private String userId;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getServer() {
		return server;
	}
	public void setServer(int server) {
		this.server = server;
	}
	public int getCreateServer() {
		return createServer;
	}
	public void setCreateServer(int createServer) {
		this.createServer = createServer;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
