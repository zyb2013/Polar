package com.game.guild.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 通知世界服务器战盟通知消息
 */
public class ReqInnerGuildNotifyToWorldMessage extends Message{

	//角色Id
	private long playerId;
	
	//通知类型
	private int notifytype;
	
	//战盟通知内容
	private String guildNotify;
	
	//! 通知类型
	private byte notify;
	
	//! 引导类型
	private int subType;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//角色Id
		writeLong(buf, this.playerId);
		//通知类型
		writeInt(buf, this.notifytype);
		//战盟通知内容
		writeString(buf, this.guildNotify);
		//! 战盟通知类型
		writeByte(buf, this.notify);
		//! 引导类型
		writeInt(buf, this.subType);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//角色Id
		this.playerId = readLong(buf);
		//通知类型
		this.notifytype = readInt(buf);
		//战盟通知内容
		this.guildNotify = readString(buf);
		//! 战盟通知类型
		this.notify = readByte(buf);
		//! 引导类型
		this.subType = readInt(buf);
		return true;
	}
	
	/**
	 * get 角色Id
	 * @return 
	 */
	public long getPlayerId(){
		return playerId;
	}
	
	/**
	 * set 角色Id
	 */
	public void setPlayerId(long playerId){
		this.playerId = playerId;
	}
	
	/**
	 * get 通知类型
	 * @return 
	 */
	public int getNotifytype(){
		return notifytype;
	}
	
	/**
	 * set 通知类型
	 */
	public void setNotifytype(int notifytype){
		this.notifytype = notifytype;
	}
	
	/**
	 * get 战盟通知内容
	 * @return 
	 */
	public String getGuildNotify(){
		return guildNotify;
	}
	
	/**
	 * set 战盟通知内容
	 */
	public void setGuildNotify(String guildNotify){
		this.guildNotify = guildNotify;
	}
	
	/**
	 * get 通知类型
	 */
	public byte getNotify(){
		return this.notify;
	}
	
	/**
	 * set 通知类型
	 */
	public void setNotify(byte notify){
		this.notify = notify;
	}
	
	/**
	 * get 引导类型
	 */
	public int getSubType(){
		return this.subType;
	}
	
	/**
	 * set 引导类型
	 */
	public void setSubType(int subType){
		this.subType = subType;
	}
	
	
	@Override
	public int getId() {
		return 121322;
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
		//角色Id
		buf.append("playerId:" + playerId +",");
		//通知类型
		buf.append("notifytype:" + notifytype +",");
		//战盟通知内容
		if(this.guildNotify!=null) buf.append("guildNotify:" + guildNotify.toString() +",");
		buf.append("notify:"+notify+",");
		buf.append("subType:"+subType+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}