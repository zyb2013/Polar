package com.game.zones.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 计时器显示，副本存在时间消息
 */
public class ResZoneLifeTimeMessage extends Message{

	//剩余时间（秒）
	private int surplustime;
	
	//副本编号
	private int zoneid;
	
	//副本进度   0 1 2 3 4 5 6   副本进行到第几阶段
	private int zoneprocess;
	
	private int playerCount;
	
	
	
	public int getPlayerCount() {
		return playerCount;
	}

	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	public int getZoneprocess() {
		return zoneprocess;
	}

	public void setZoneprocess(int zoneprocess) {
		this.zoneprocess = zoneprocess;
	}

	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//剩余时间（秒）
		writeInt(buf, this.surplustime);
		//副本编号
		writeInt(buf, this.zoneid);
		writeInt(buf, this.zoneprocess);
		writeInt(buf, this.playerCount);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//剩余时间（秒）
		this.surplustime = readInt(buf);
		//副本编号
		this.zoneid = readInt(buf);
		this.zoneprocess = readInt(buf);
		this.playerCount = readInt(buf);
		return true;
	}
	
	/**
	 * get 剩余时间（秒）
	 * @return 
	 */
	public int getSurplustime(){
		return surplustime;
	}
	
	/**
	 * set 剩余时间（秒）
	 */
	public void setSurplustime(int surplustime){
		this.surplustime = surplustime;
	}
	
	/**
	 * get 副本编号
	 * @return 
	 */
	public int getZoneid(){
		return zoneid;
	}
	
	/**
	 * set 副本编号
	 */
	public void setZoneid(int zoneid){
		this.zoneid = zoneid;
	}
	
	
	@Override
	public int getId() {
		return 128110;
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
		//剩余时间（秒）
		buf.append("surplustime:" + surplustime +",");
		//副本编号
		buf.append("zoneid:" + zoneid +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}