package com.game.guildflag.bean;


import com.game.message.Bean;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 战盟领地信息
 */
public class GuildFlagInfo extends Bean {

	//战盟ID
	private long guildid;
	
	//战盟名字
	private String guildname;
	
	//战盟旗帜等级
	private int guildflaglevel;
	
	//占领地图id
	private int mapmodelid;
	
	//血量百分比
	private int hppercentage;
	
	//战盟旗帜id
	private int guildflagid;
	
	//盟主ID
	private long guildheadid;
	
	//盟主名字
	private String guildheadname;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//战盟ID
		writeLong(buf, this.guildid);
		//战盟名字
		writeString(buf, this.guildname);
		//战盟旗帜等级
		writeInt(buf, this.guildflaglevel);
		//占领地图id
		writeInt(buf, this.mapmodelid);
		//血量百分比
		writeInt(buf, this.hppercentage);
		//战盟旗帜id
		writeInt(buf, this.guildflagid);
		//盟主ID
		writeLong(buf, this.guildheadid);
		//盟主名字
		writeString(buf, this.guildheadname);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//战盟ID
		this.guildid = readLong(buf);
		//战盟名字
		this.guildname = readString(buf);
		//战盟旗帜等级
		this.guildflaglevel = readInt(buf);
		//占领地图id
		this.mapmodelid = readInt(buf);
		//血量百分比
		this.hppercentage = readInt(buf);
		//战盟旗帜id
		this.guildflagid = readInt(buf);
		//盟主ID
		this.guildheadid = readLong(buf);
		//盟主名字
		this.guildheadname = readString(buf);
		return true;
	}
	
	/**
	 * get 战盟ID
	 * @return 
	 */
	public long getGuildid(){
		return guildid;
	}
	
	/**
	 * set 战盟ID
	 */
	public void setGuildid(long guildid){
		this.guildid = guildid;
	}
	
	/**
	 * get 战盟名字
	 * @return 
	 */
	public String getGuildname(){
		return guildname;
	}
	
	/**
	 * set 战盟名字
	 */
	public void setGuildname(String guildname){
		this.guildname = guildname;
	}
	
	/**
	 * get 战盟旗帜等级
	 * @return 
	 */
	public int getGuildflaglevel(){
		return guildflaglevel;
	}
	
	/**
	 * set 战盟旗帜等级
	 */
	public void setGuildflaglevel(int guildflaglevel){
		this.guildflaglevel = guildflaglevel;
	}
	
	/**
	 * get 占领地图id
	 * @return 
	 */
	public int getMapmodelid(){
		return mapmodelid;
	}
	
	/**
	 * set 占领地图id
	 */
	public void setMapmodelid(int mapmodelid){
		this.mapmodelid = mapmodelid;
	}
	
	/**
	 * get 血量百分比
	 * @return 
	 */
	public int getHppercentage(){
		return hppercentage;
	}
	
	/**
	 * set 血量百分比
	 */
	public void setHppercentage(int hppercentage){
		this.hppercentage = hppercentage;
	}
	
	/**
	 * get 战盟旗帜id
	 * @return 
	 */
	public int getGuildflagid(){
		return guildflagid;
	}
	
	/**
	 * set 战盟旗帜id
	 */
	public void setGuildflagid(int guildflagid){
		this.guildflagid = guildflagid;
	}
	
	/**
	 * get 盟主ID
	 * @return 
	 */
	public long getGuildheadid(){
		return guildheadid;
	}
	
	/**
	 * set 盟主ID
	 */
	public void setGuildheadid(long guildheadid){
		this.guildheadid = guildheadid;
	}
	
	/**
	 * get 盟主名字
	 * @return 
	 */
	public String getGuildheadname(){
		return guildheadname;
	}
	
	/**
	 * set 盟主名字
	 */
	public void setGuildheadname(String guildheadname){
		this.guildheadname = guildheadname;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//战盟ID
		buf.append("guildid:" + guildid +",");
		//战盟名字
		if(this.guildname!=null) buf.append("guildname:" + guildname.toString() +",");
		//战盟旗帜等级
		buf.append("guildflaglevel:" + guildflaglevel +",");
		//占领地图id
		buf.append("mapmodelid:" + mapmodelid +",");
		//血量百分比
		buf.append("hppercentage:" + hppercentage +",");
		//战盟旗帜id
		buf.append("guildflagid:" + guildflagid +",");
		//盟主ID
		buf.append("guildheadid:" + guildheadid +",");
		//盟主名字
		if(this.guildheadname!=null) buf.append("guildheadname:" + guildheadname.toString() +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}