package com.game.guild.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 盟主修改自动同意申请加入设置消息
 */
public class ReqGuildAutoGuildArgeeAddToServerMessage extends Message{

	//战盟Id
	private long guildId;
	
	//自动同意加入战盟的申请
	private byte autoGuildAgreeAdd;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//战盟Id
		writeLong(buf, this.guildId);
		//自动同意加入战盟的申请
		writeByte(buf, this.autoGuildAgreeAdd);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//战盟Id
		this.guildId = readLong(buf);
		//自动同意加入战盟的申请
		this.autoGuildAgreeAdd = readByte(buf);
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
	 * get 自动同意加入战盟的申请
	 * @return 
	 */
	public byte getAutoGuildAgreeAdd(){
		return autoGuildAgreeAdd;
	}
	
	/**
	 * set 自动同意加入战盟的申请
	 */
	public void setAutoGuildAgreeAdd(byte autoGuildAgreeAdd){
		this.autoGuildAgreeAdd = autoGuildAgreeAdd;
	}
	
	
	@Override
	public int getId() {
		return 121212;
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
		//自动同意加入战盟的申请
		buf.append("autoGuildAgreeAdd:" + autoGuildAgreeAdd +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}