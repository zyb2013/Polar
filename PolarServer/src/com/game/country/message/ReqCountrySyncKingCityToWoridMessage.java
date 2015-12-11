package com.game.country.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 同步到世界服务器单个国家王城战盟消息
 */
public class ReqCountrySyncKingCityToWoridMessage extends Message{

	//国家ID
	private int countryid;
	
	//王城战盟ID
	private long guildid;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//国家ID
		writeInt(buf, this.countryid);
		//王城战盟ID
		writeLong(buf, this.guildid);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//国家ID
		this.countryid = readInt(buf);
		//王城战盟ID
		this.guildid = readLong(buf);
		return true;
	}
	
	/**
	 * get 国家ID
	 * @return 
	 */
	public int getCountryid(){
		return countryid;
	}
	
	/**
	 * set 国家ID
	 */
	public void setCountryid(int countryid){
		this.countryid = countryid;
	}
	
	/**
	 * get 王城战盟ID
	 * @return 
	 */
	public long getGuildid(){
		return guildid;
	}
	
	/**
	 * set 王城战盟ID
	 */
	public void setGuildid(long guildid){
		this.guildid = guildid;
	}
	
	
	@Override
	public int getId() {
		return 146301;
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
		//国家ID
		buf.append("countryid:" + countryid +",");
		//王城战盟ID
		buf.append("guildid:" + guildid +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}