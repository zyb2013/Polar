package com.game.fight.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 广播攻击动作消息(只针对 postion的范围攻击)
 */
public class ResFightPostionBroadcastMessage extends Message{

	//战斗Id
	private long fightId;
	
	//角色Id
	private long personId;
	
	//攻击朝向
	private int fightDirection;
	
	//攻击类型
	private int fightType;
	//int mapModelId, int line,
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
		//战斗Id
		writeLong(buf, this.fightId);
		//角色Id
		writeLong(buf, this.personId);
		//攻击朝向
		writeInt(buf, this.fightDirection);
		//攻击类型
		writeInt(buf, this.fightType);
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
		//战斗Id
		this.fightId = readLong(buf);
		//角色Id
		this.personId = readLong(buf);
		//攻击朝向
		this.fightDirection = readInt(buf);
		//攻击类型
		this.fightType = readInt(buf);
		//攻击目标
		this.mapModelId = readInt(buf);
		this.line = readInt(buf);
		this.x = readShort(buf);
		this.y = readShort(buf);
		return true;
	}
	
	/**
	 * get 战斗Id
	 * @return 
	 */
	public long getFightId(){
		return fightId;
	}
	
	/**
	 * set 战斗Id
	 */
	public void setFightId(long fightId){
		this.fightId = fightId;
	}
	
	/**
	 * get 角色Id
	 * @return 
	 */
	public long getPersonId(){
		return personId;
	}
	
	/**
	 * set 角色Id
	 */
	public void setPersonId(long personId){
		this.personId = personId;
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
		
	@Override
	public int getId() {
		return 502101;
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
		//战斗Id
		buf.append("fightId:" + fightId +",");
		//角色Id
		buf.append("personId:" + personId +",");
		//攻击朝向
		buf.append("fightDirection:" + fightDirection +",");
		//攻击类型
		buf.append("fightType:" + fightType +",");
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