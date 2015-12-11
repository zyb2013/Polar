package com.game.guild.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 更换盟旗造型消息
 */
public class ReqGuildChangeBannerIconToServerMessage extends Message{

	//战盟Id
	private long guildId;
	
	//旗帜造型
	private int bannerIcon;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//战盟Id
		writeLong(buf, this.guildId);
		//旗帜造型
		writeInt(buf, this.bannerIcon);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//战盟Id
		this.guildId = readLong(buf);
		//旗帜造型
		this.bannerIcon = readInt(buf);
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
	 * get 旗帜造型
	 * @return 
	 */
	public int getBannerIcon(){
		return bannerIcon;
	}
	
	/**
	 * set 旗帜造型
	 */
	public void setBannerIcon(int bannerIcon){
		this.bannerIcon = bannerIcon;
	}
	
	
	@Override
	public int getId() {
		return 121215;
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
		//旗帜造型
		buf.append("bannerIcon:" + bannerIcon +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}