package com.game.guild.bean;


import com.game.message.Bean;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 查看其他战盟信息
 */
public class OtherGuildInfo extends Bean {

	//战盟旗帜
	private BannerInfo guildBanner;
	
	//战盟职位等级
	private byte guildPowerLevel;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//战盟旗帜
		writeBean(buf, this.guildBanner);
		//战盟职位等级
		writeByte(buf, this.guildPowerLevel);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//战盟旗帜
		this.guildBanner = (BannerInfo)readBean(buf, BannerInfo.class);
		//战盟职位等级
		this.guildPowerLevel = readByte(buf);
		return true;
	}
	
	/**
	 * get 战盟旗帜
	 * @return 
	 */
	public BannerInfo getGuildBanner(){
		return guildBanner;
	}
	
	/**
	 * set 战盟旗帜
	 */
	public void setGuildBanner(BannerInfo guildBanner){
		this.guildBanner = guildBanner;
	}
	
	/**
	 * get 战盟职位等级
	 * @return 
	 */
	public byte getGuildPowerLevel(){
		return guildPowerLevel;
	}
	
	/**
	 * set 战盟职位等级
	 */
	public void setGuildPowerLevel(byte guildPowerLevel){
		this.guildPowerLevel = guildPowerLevel;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//战盟旗帜
		if(this.guildBanner!=null) buf.append("guildBanner:" + guildBanner.toString() +",");
		//战盟职位等级
		buf.append("guildPowerLevel:" + guildPowerLevel +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}