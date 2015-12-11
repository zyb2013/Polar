package com.game.chat.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 按角色名查询  支持模糊消息
 */
public class RoleQueryMessage extends Message{

	//页码 从1开始
	private int page;
	
	//角色名
	private String roleLikeName;
	
	//查找类型,1-表示聊天查找玩家;2-表示战盟查找玩家
	private int type;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//页码 从1开始
		writeInt(buf, this.page);
		//角色名
		writeString(buf, this.roleLikeName);
		//查找类型,1-表示聊天查找玩家;2-表示战盟查找玩家
		writeInt(buf, this.type);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//页码 从1开始
		this.page = readInt(buf);
		//角色名
		this.roleLikeName = readString(buf);
		//查找类型,1-表示聊天查找玩家;2-表示战盟查找玩家
		this.type = readInt(buf);
		return true;
	}
	
	/**
	 * get 页码 从1开始
	 * @return 
	 */
	public int getPage(){
		return page;
	}
	
	/**
	 * set 页码 从1开始
	 */
	public void setPage(int page){
		this.page = page;
	}
	
	/**
	 * get 角色名
	 * @return 
	 */
	public String getRoleLikeName(){
		return roleLikeName;
	}
	
	/**
	 * set 角色名
	 */
	public void setRoleLikeName(String roleLikeName){
		this.roleLikeName = roleLikeName;
	}
	
	/**
	 * get 查找类型,1-表示聊天查找玩家;2-表示战盟查找玩家
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * set 查找类型,1-表示聊天查找玩家;2-表示战盟查找玩家
	 */
	public void setType(int type) {
		this.type = type;
	}
	

	@Override
	public int getId() {
		return 111202;
	}

	@Override
	public String getQueue() {
		return null;
	}
	
	@Override
	public String getServer() {
		return null;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//页码 从1开始
		buf.append("page:" + page +",");
		//角色名
		if(this.roleLikeName!=null) buf.append("roleLikeName:" + roleLikeName.toString() +",");
		//查找类型,1-表示聊天查找玩家;2-表示战盟查找玩家
		buf.append("type:" + type +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}