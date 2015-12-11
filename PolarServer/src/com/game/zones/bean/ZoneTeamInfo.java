package com.game.zones.bean;


import com.game.message.Bean;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 多人副本展示信息
 */
public class ZoneTeamInfo extends Bean {

	//副本编号
	private int zoneid;
	
	//上一次进副本的时间
	private long last_time;

	public long getLast_time() {
		return last_time;
	}

	public void setLast_time(long last_time) {
		this.last_time = last_time;
	}

	//副本冷却时长
	private long wait_time;
	
	//次数用完后通关状态，1已参与，2已通关
	private byte clearancestatus;
	
	//进入次数
	private int enternum;
	
	//允许进入的最大次数
	private int enterMaxnum;

	//0关闭，1开启 3.
	private byte isopen;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//副本编号
		writeInt(buf, this.zoneid);
		//次数用完后通关状态，1已参与，2已通关
		writeByte(buf, this.clearancestatus);
		//进入次数
		writeInt(buf, this.enternum);
		//0关闭，1开启
		writeByte(buf, this.isopen);
		//上一次进入的时间
		writeLong(buf, this.last_time);
		//副本冷却时长
		writeLong(buf, this.wait_time);
		//允许进入的最大次数
		writeInt(buf, this.enterMaxnum);
		
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//副本编号
		this.zoneid = readInt(buf);
		//次数用完后通关状态，1已参与，2已通关
		this.clearancestatus = readByte(buf);
		//进入次数
		this.enternum = readInt(buf);
		//0关闭，1开启
		this.isopen = readByte(buf);
		//0关闭，1开启

		//0关闭，1开启
		this.wait_time = readLong(buf);
		this.enterMaxnum = readInt(buf);
		
		return true;
	}
	
	
	public int getEnterMaxnum() {
		return enterMaxnum;
	}

	public void setEnterMaxnum(int enterMaxnum) {
		this.enterMaxnum = enterMaxnum;
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
	
	/**
	 * get 次数用完后通关状态，1已参与，2已通关
	 * @return 
	 */
	public byte getClearancestatus(){
		return clearancestatus;
	}
	
	/**
	 * set 次数用完后通关状态，1已参与，2已通关
	 */
	public void setClearancestatus(byte clearancestatus){
		this.clearancestatus = clearancestatus;
	}
	
	/**
	 * get 进入次数
	 * @return 
	 */
	public int getEnternum(){
		return enternum;
	}
	
	/**
	 * set 进入次数
	 */
	public void setEnternum(int enternum){
		this.enternum = enternum;
	}
	
	/**
	 * get 0关闭，1开启
	 * @return 
	 */
	public byte getIsopen(){
		return isopen;
	}
	
	/**
	 * set 0关闭，1开启
	 */
	public void setIsopen(byte isopen){
		this.isopen = isopen;
	}


	public long getWait_time() {
		return wait_time;
	}

	public void setWait_time(long wait_time) {
		this.wait_time = wait_time;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//副本编号
		buf.append("zoneid:" + zoneid +",");
		//次数用完后通关状态，1已参与，2已通关
		buf.append("clearancestatus:" + clearancestatus +",");
		//进入次数
		buf.append("enternum:" + enternum +",");
		//0关闭，1开启
		buf.append("isopen:" + isopen +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}