package com.game.guild.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 修改公告消息
 */
public class ReqGuildChangeBulletinToServerMessage extends Message{

	//战盟Id
	private long guildId;
	
	//战盟公告
	private String guildBulletin;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//战盟Id
		writeLong(buf, this.guildId);
		//战盟公告
		writeString(buf, this.guildBulletin);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//战盟Id
		this.guildId = readLong(buf);
		//战盟公告
		this.guildBulletin = readString(buf);
		return true;
	}
	
	/**
	 * get 战盟Id
	 * @return 
	 */
	public long getGuildId(){
		return guildId;
	}
	
	/**
	 * set 战盟Id
	 */
	public void setGuildId(long guildId){
		this.guildId = guildId;
	}
	
	/**
	 * get 战盟公告
	 * @return 
	 */
	public String getGuildBulletin(){
		return guildBulletin;
	}
	
	/**
	 * set 战盟公告
	 */
	public void setGuildBulletin(String guildBulletin){
		this.guildBulletin = guildBulletin;
	}
	
	
	@Override
	public int getId() {
		return 121213;
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
		//战盟Id
		buf.append("guildId:" + guildId +",");
		//战盟公告
		if(this.guildBulletin!=null) buf.append("guildBulletin:" + guildBulletin.toString() +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}