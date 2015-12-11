package com.game.team.bean;


import com.game.message.Bean;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 当前地图玩家信息类
 */
public class MapPlayerInfo extends Bean {

	//玩家Id
	private long playerid;
	
	//玩家等级
	private short playerlv;
	
	//组队状态，0未组队，1已组队
	private byte teamstate;
	
	//玩家名字
	private String playername;
	
	//战盟名字
	private String guildname;
	
	//所在线路
	private byte line;
	
	//队员职业
	private byte job;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家Id
		writeLong(buf, this.playerid);
		//玩家等级
		writeShort(buf, this.playerlv);
		//组队状态，0未组队，1已组队
		writeByte(buf, this.teamstate);
		//玩家名字
		writeString(buf, this.playername);
		//战盟名字
		writeString(buf, this.guildname);
		//所在线路
		writeByte(buf, this.line);
		//队员职业
		writeByte(buf, this.job);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//玩家Id
		this.playerid = readLong(buf);
		//玩家等级
		this.playerlv = readShort(buf);
		//组队状态，0未组队，1已组队
		this.teamstate = readByte(buf);
		//玩家名字
		this.playername = readString(buf);
		//战盟名字
		this.guildname = readString(buf);
		//所在线路
		this.line = readByte(buf);
		//队员职业
		this.job = readByte(buf);
		return true;
	}
	
	/**
	 * get 玩家Id
	 * @return 
	 */
	public long getPlayerid(){
		return playerid;
	}
	
	/**
	 * set 玩家Id
	 */
	public void setPlayerid(long playerid){
		this.playerid = playerid;
	}
	
	/**
	 * get 玩家等级
	 * @return 
	 */
	public short getPlayerlv(){
		return playerlv;
	}
	
	/**
	 * set 玩家等级
	 */
	public void setPlayerlv(short playerlv){
		this.playerlv = playerlv;
	}
	
	/**
	 * get 组队状态，0未组队，1已组队
	 * @return 
	 */
	public byte getTeamstate(){
		return teamstate;
	}
	
	/**
	 * set 组队状态，0未组队，1已组队
	 */
	public void setTeamstate(byte teamstate){
		this.teamstate = teamstate;
	}
	
	/**
	 * get 玩家名字
	 * @return 
	 */
	public String getPlayername(){
		return playername;
	}
	
	/**
	 * set 玩家名字
	 */
	public void setPlayername(String playername){
		this.playername = playername;
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
	 * get 所在线路
	 * @return 
	 */
	public byte getLine(){
		return line;
	}
	
	/**
	 * set 所在线路
	 */
	public void setLine(byte line){
		this.line = line;
	}
	
	/**
	 * get 队员职业
	 * @return
	 */
	public byte getJob() {
		return job;
	}

	/**
	 * set 队员职业
	 */
	public void setJob(byte job) {
		this.job = job;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//玩家Id
		buf.append("playerid:" + playerid +",");
		//玩家等级
		buf.append("playerlv:" + playerlv +",");
		//组队状态，0未组队，1已组队
		buf.append("teamstate:" + teamstate +",");
		//玩家名字
		if(this.playername!=null) buf.append("playername:" + playername.toString() +",");
		//战盟名字
		if(this.guildname!=null) buf.append("guildname:" + guildname.toString() +",");
		//所在线路
		buf.append("line:" + line +",");
		//队员职业
		buf.append("job:" + job + ",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}