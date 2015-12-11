package com.game.guild.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 通知世界服务器更换盟旗名字消息
 */
public class ReqInnerGuildChangeBannerNameToWorldMessage extends Message{

	//角色Id
	private long playerId;
	
	//战盟Id
	private long guildId;
	
	//旗帜名字
	private String bannerName;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//角色Id
		writeLong(buf, this.playerId);
		//战盟Id
		writeLong(buf, this.guildId);
		//旗帜名字
		writeString(buf, this.bannerName);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//角色Id
		this.playerId = readLong(buf);
		//战盟Id
		this.guildId = readLong(buf);
		//旗帜名字
		this.bannerName = readString(buf);
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
	 * get 旗帜名字
	 * @return 
	 */
	public String getBannerName(){
		return bannerName;
	}
	
	/**
	 * set 旗帜名字
	 */
	public void setBannerName(String bannerName){
		this.bannerName = bannerName;
	}
	
	
	@Override
	public int getId() {
		return 121316;
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
		//战盟Id
		buf.append("guildId:" + guildId +",");
		//旗帜名字
		if(this.bannerName!=null) buf.append("bannerName:" + bannerName.toString() +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}