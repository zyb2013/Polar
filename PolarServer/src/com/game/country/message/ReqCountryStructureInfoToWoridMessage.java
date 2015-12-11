package com.game.country.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 请求王城结构信息to世界消息
 */
public class ReqCountryStructureInfoToWoridMessage extends Message{

	//玩家ID
	private long playerid;
	
	//王城战盟ID
	private long guildid;
	
	//攻城时间
	private String Siegetime;
	
	
	//连斩天数
	private int day;
		
	//是否有奖励
	private String reward;
		
	
	
	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家ID
		writeLong(buf, this.playerid);
		//王城战盟ID
		writeLong(buf, this.guildid);
		//攻城时间
		writeString(buf, this.Siegetime);
		
		writeInt(buf, day);
		writeString(buf, reward);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//玩家ID
		this.playerid = readLong(buf);
		//王城战盟ID
		this.guildid = readLong(buf);
		//攻城时间
		this.Siegetime = readString(buf);
		
		this.day= readInt(buf);
		
		this.reward = readString(buf);
		
		
		return true;
	}
	
	/**
	 * get 玩家ID
	 * @return 
	 */
	public long getPlayerid(){
		return playerid;
	}
	
	/**
	 * set 玩家ID
	 */
	public void setPlayerid(long playerid){
		this.playerid = playerid;
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
	
	/**
	 * get 攻城时间
	 * @return 
	 */
	public String getSiegetime(){
		return Siegetime;
	}
	
	/**
	 * set 攻城时间
	 */
	public void setSiegetime(String Siegetime){
		this.Siegetime = Siegetime;
	}
	
	
	@Override
	public int getId() {
		return 146300;
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
		//玩家ID
		buf.append("playerid:" + playerid +",");
		//王城战盟ID
		buf.append("guildid:" + guildid +",");
		//攻城时间
		if(this.Siegetime!=null) buf.append("Siegetime:" + Siegetime.toString() +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}