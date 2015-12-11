package com.game.guild.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 玩家加入或者退出战盟广播的消息
 */
public class ResGuildChangeBroadcastMessage extends Message{

	//角色Id
	private long personId;
	
	//战盟Id
	private long guildId;
	
	//战盟名字
	private String guildName;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//角色Id
		writeLong(buf, this.personId);
		//战盟Id
		writeLong(buf, this.guildId);
		//战盟名字
		writeString(buf, this.guildName);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//角色Id
		this.personId = readLong(buf);
		//战盟Id
		this.guildId = readLong(buf);
		//战盟名字
		this.guildName = readString(buf);
		return true;
	}
	
	/**
	 * get 角色Id
	 * @return 
	 */
	public long getPersonId(){
		return personId;
	}
	
	/**
	 * set 角色Id
	 */
	public void setPersonId(long personId){
		this.personId = personId;
	}
	
	/**
	 * get 战盟Id
	 * @return
	 */
	public long getGuildId() {
		return guildId;
	}

	/**
	 * set 战盟Id
	 */
	public void setGuildId(long guildId) {
		this.guildId = guildId;
	}

	/**
	 * get 战盟名字
	 * @return
	 */
	public String getGuildName() {
		return guildName;
	}

	/**
	 * set 战盟名字
	 */
	public void setGuildName(String guildName) {
		this.guildName = guildName;
	}

	@Override
	public int getId() {
		return 121130;
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
		buf.append("personId:" + personId +",");
		//战盟Id
		buf.append("guildId:" + guildId +",");
		//战盟名字
		buf.append("guildName:" + guildName +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}