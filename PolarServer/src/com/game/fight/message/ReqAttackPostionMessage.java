package com.game.fight.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 攻击玩家请求消息
 */
public class ReqAttackPostionMessage extends Message{

	//攻击类型
	private int fightType;
	
	//攻击朝向
	private int fightDirection;
	private int  mapModelId;
	private int  line;
	
	//攻击目标x
	private short x;
	
	//攻击目标x
	private short y;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//攻击类型
		writeInt(buf, this.fightType);
		//攻击朝向
		writeInt(buf, this.fightDirection);
		//攻击目标
		writeInt(buf, this.mapModelId);
		writeInt(buf, this.line);
		writeShort(buf, this.x);
		writeShort(buf, this.y);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//攻击类型
		this.fightType = readInt(buf);
		//攻击朝向
		this.fightDirection = readInt(buf);
		//攻击目标
		this.mapModelId = readInt(buf);
		this.line = readInt(buf);
		this.x = readShort(buf);
		this.y = readShort(buf);
		return true;
	}
	
	/**
	 * get 攻击类型
	 * @return 
	 */
	public int getFightType(){
		return fightType;
	}
	
	/**
	 * set 攻击类型
	 */
	public void setFightType(int fightType){
		this.fightType = fightType;
	}
	
	/**
	 * get 攻击朝向
	 * @return 
	 */
	public int getFightDirection(){
		return fightDirection;
	}
	
	/**
	 * set 攻击朝向
	 */
	public void setFightDirection(int fightDirection){
		this.fightDirection = fightDirection;
	}
	
	
	
	@Override
	public int getId() {
		return 502202;
	}

	@Override
	public String getQueue() {
		return null;
	}
	
	@Override
	public String getServer() {
		return null;
	}
	public short getx() {
		return x;
	}
	

	public short gety() {
		return y;
	}

	public void setx(short x) {
		this.x = x;
	}
	
	public void sety(short y) {
		this.y = y;
	}
	
	public int getmapModelId() {
		return mapModelId;
	}

	public void setmapModelId(int mapModelId) {
		this.mapModelId = mapModelId;
	}
	
	public int getline() {
		return line;
	}

	public void setline(int line) {
		this.line = line;
	}
	
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//攻击类型
		buf.append("fightType:" + fightType +",");
		//攻击朝向
		buf.append("fightDirection:" + fightDirection +",");
		//攻击目标
		buf.append("mapModelId:" + mapModelId +",");
		buf.append("line:" + line +",");
		buf.append("x:" + x +",");
		buf.append("y:" + y +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}