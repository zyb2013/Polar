package com.game.guild.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 发送战斗力给世界服消息
 */
public class ReqInnerGuildUpdateFightPowerToWorld extends Message{

	//角色Id
	private long playerId;
		
	//总战斗力
	private int fightPower;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//角色Id
		writeLong(buf, this.playerId);
		//总战斗力
		writeInt(buf, this.fightPower);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//角色Id
		this.playerId = readLong(buf);
		//总战斗力
		this.fightPower = readInt(buf);
		return true;
	}
	
	/**
	 * get 角色Id
	 * @return
	 */
	public long getPlayerId() {
		return playerId;
	}

	/**
	 * set 角色Id 
	 */
	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	/**
	 * get 总战斗力
	 * @return 
	 */
	public int getFightPower(){
		return fightPower;
	}
	
	/**
	 * set 总战斗力
	 */
	public void setFightPower(int fightPower){
		this.fightPower = fightPower;
	}
	
	
	@Override
	public int getId() {
		return 121327;
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
		//总战斗力
		buf.append("fightPower:" + fightPower +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}