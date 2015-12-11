package com.game.zones.bean;


import com.game.message.Bean;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 4v4战场个人信息
 */
public class BfStructsInfo extends Bean {

	//玩家id
	private long playerid;
	
	//玩家名字
	private String playername;
	
	//玩家等级
	private int playerlevel;
	
	//玩家阵营
	private int camp;
	
	//死亡次数
	private int deathnum;
	
	//杀敌次数
	private int killnum;
	
	//累计得到经验
	private int totalexp;
	
	//累计得到真气
	private int totalzhenqi;
	
	//夺旗次数
	private int seizeflag;
	
	//是否在线，1表示离线
	private byte online;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家id
		writeLong(buf, this.playerid);
		//玩家名字
		writeString(buf, this.playername);
		//玩家等级
		writeInt(buf, this.playerlevel);
		//玩家阵营
		writeInt(buf, this.camp);
		//死亡次数
		writeInt(buf, this.deathnum);
		//杀敌次数
		writeInt(buf, this.killnum);
		//累计得到经验
		writeInt(buf, this.totalexp);
		//累计得到真气
		writeInt(buf, this.totalzhenqi);
		//夺旗次数
		writeInt(buf, this.seizeflag);
		//是否在线，1表示离线
		writeByte(buf, this.online);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//玩家id
		this.playerid = readLong(buf);
		//玩家名字
		this.playername = readString(buf);
		//玩家等级
		this.playerlevel = readInt(buf);
		//玩家阵营
		this.camp = readInt(buf);
		//死亡次数
		this.deathnum = readInt(buf);
		//杀敌次数
		this.killnum = readInt(buf);
		//累计得到经验
		this.totalexp = readInt(buf);
		//累计得到真气
		this.totalzhenqi = readInt(buf);
		//夺旗次数
		this.seizeflag = readInt(buf);
		//是否在线，1表示离线
		this.online = readByte(buf);
		return true;
	}
	
	/**
	 * get 玩家id
	 * @return 
	 */
	public long getPlayerid(){
		return playerid;
	}
	
	/**
	 * set 玩家id
	 */
	public void setPlayerid(long playerid){
		this.playerid = playerid;
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
	 * get 玩家等级
	 * @return 
	 */
	public int getPlayerlevel(){
		return playerlevel;
	}
	
	/**
	 * set 玩家等级
	 */
	public void setPlayerlevel(int playerlevel){
		this.playerlevel = playerlevel;
	}
	
	/**
	 * get 玩家阵营
	 * @return 
	 */
	public int getCamp(){
		return camp;
	}
	
	/**
	 * set 玩家阵营
	 */
	public void setCamp(int camp){
		this.camp = camp;
	}
	
	/**
	 * get 死亡次数
	 * @return 
	 */
	public int getDeathnum(){
		return deathnum;
	}
	
	/**
	 * set 死亡次数
	 */
	public void setDeathnum(int deathnum){
		this.deathnum = deathnum;
	}
	
	/**
	 * get 杀敌次数
	 * @return 
	 */
	public int getKillnum(){
		return killnum;
	}
	
	/**
	 * set 杀敌次数
	 */
	public void setKillnum(int killnum){
		this.killnum = killnum;
	}
	
	/**
	 * get 累计得到经验
	 * @return 
	 */
	public int getTotalexp(){
		return totalexp;
	}
	
	/**
	 * set 累计得到经验
	 */
	public void setTotalexp(int totalexp){
		this.totalexp = totalexp;
	}
	
	/**
	 * get 累计得到真气
	 * @return 
	 */
	public int getTotalzhenqi(){
		return totalzhenqi;
	}
	
	/**
	 * set 累计得到真气
	 */
	public void setTotalzhenqi(int totalzhenqi){
		this.totalzhenqi = totalzhenqi;
	}
	
	/**
	 * get 夺旗次数
	 * @return 
	 */
	public int getSeizeflag(){
		return seizeflag;
	}
	
	/**
	 * set 夺旗次数
	 */
	public void setSeizeflag(int seizeflag){
		this.seizeflag = seizeflag;
	}
	
	/**
	 * get 是否在线，1表示离线
	 * @return 
	 */
	public byte getOnline(){
		return online;
	}
	
	/**
	 * set 是否在线，1表示离线
	 */
	public void setOnline(byte online){
		this.online = online;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//玩家id
		buf.append("playerid:" + playerid +",");
		//玩家名字
		if(this.playername!=null) buf.append("playername:" + playername.toString() +",");
		//玩家等级
		buf.append("playerlevel:" + playerlevel +",");
		//玩家阵营
		buf.append("camp:" + camp +",");
		//死亡次数
		buf.append("deathnum:" + deathnum +",");
		//杀敌次数
		buf.append("killnum:" + killnum +",");
		//累计得到经验
		buf.append("totalexp:" + totalexp +",");
		//累计得到真气
		buf.append("totalzhenqi:" + totalzhenqi +",");
		//夺旗次数
		buf.append("seizeflag:" + seizeflag +",");
		//是否在线，1表示离线
		buf.append("online:" + online +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}