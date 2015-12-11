package com.game.guild.bean;


import com.game.message.Bean;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 盟旗信息
 */
public class BannerInfo extends Bean {

	//战盟id
	private long guildId;
	
	//战盟名
	private String guildName;
	
	//战盟旗帜
	private String guildBanner;
	
	//盟主名字
	private String bangZhuName;
	
	//旗帜造型
	private int bannerIcon;
	
	//旗帜等级
	private byte bannerLevel;
	
	//战盟创建时间
	private int createTime;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//战盟id
		writeLong(buf, this.guildId);
		//战盟名
		writeString(buf, this.guildName);
		//战盟旗帜
		writeString(buf, this.guildBanner);
		//盟主名字
		writeString(buf, this.bangZhuName);
		//旗帜造型
		writeInt(buf, this.bannerIcon);
		//旗帜等级
		writeByte(buf, this.bannerLevel);
		//战盟创建时间
		writeInt(buf, this.createTime);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//战盟id
		this.guildId = readLong(buf);
		//战盟名
		this.guildName = readString(buf);
		//战盟旗帜
		this.guildBanner = readString(buf);
		//盟主名字
		this.bangZhuName = readString(buf);
		//旗帜造型
		this.bannerIcon = readInt(buf);
		//旗帜等级
		this.bannerLevel = readByte(buf);
		//战盟创建时间
		this.createTime = readInt(buf);
		return true;
	}
	
	/**
	 * get 战盟id
	 * @return 
	 */
	public long getGuildId(){
		return guildId;
	}
	
	/**
	 * set 战盟id
	 */
	public void setGuildId(long guildId){
		this.guildId = guildId;
	}
	
	/**
	 * get 战盟名
	 * @return 
	 */
	public String getGuildName(){
		return guildName;
	}
	
	/**
	 * set 战盟名
	 */
	public void setGuildName(String guildName){
		this.guildName = guildName;
	}
	
	/**
	 * get 战盟旗帜
	 * @return 
	 */
	public String getGuildBanner(){
		return guildBanner;
	}
	
	/**
	 * set 战盟旗帜
	 */
	public void setGuildBanner(String guildBanner){
		this.guildBanner = guildBanner;
	}
	
	/**
	 * get 盟主名字
	 * @return 
	 */
	public String getBangZhuName(){
		return bangZhuName;
	}
	
	/**
	 * set 盟主名字
	 */
	public void setBangZhuName(String bangZhuName){
		this.bangZhuName = bangZhuName;
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
	
	/**
	 * get 旗帜等级
	 * @return 
	 */
	public byte getBannerLevel(){
		return bannerLevel;
	}
	
	/**
	 * set 旗帜等级
	 */
	public void setBannerLevel(byte bannerLevel){
		this.bannerLevel = bannerLevel;
	}
	
	/**
	 * get 战盟创建时间
	 * @return 
	 */
	public int getCreateTime(){
		return createTime;
	}
	
	/**
	 * set 战盟创建时间
	 */
	public void setCreateTime(int createTime){
		this.createTime = createTime;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//战盟id
		buf.append("guildId:" + guildId +",");
		//战盟名
		if(this.guildName!=null) buf.append("guildName:" + guildName.toString() +",");
		//战盟旗帜
		if(this.guildBanner!=null) buf.append("guildBanner:" + guildBanner.toString() +",");
		//盟主名字
		if(this.bangZhuName!=null) buf.append("bangZhuName:" + bangZhuName.toString() +",");
		//旗帜造型
		buf.append("bannerIcon:" + bannerIcon +",");
		//旗帜等级
		buf.append("bannerLevel:" + bannerLevel +",");
		//战盟创建时间
		buf.append("createTime:" + createTime +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}