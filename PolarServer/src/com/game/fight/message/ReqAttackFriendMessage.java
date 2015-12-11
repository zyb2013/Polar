package com.game.fight.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 给友好目标释放技能请求消息
 */
public class ReqAttackFriendMessage extends Message{

	//攻击类型
	private int fightType;
	
	//攻击朝向
	private int fightDirection;
	
	//友好目标释放技能
	private long fightTarget;
	
	//友好目标类型
	private byte TargetType;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//攻击类型
		writeInt(buf, this.fightType);
		//攻击朝向
		writeInt(buf, this.fightDirection);
		//友好目标释放技能
		writeLong(buf, this.fightTarget);
		writeByte(buf, this.TargetType);
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
		//友好目标释放技能
		this.fightTarget = readLong(buf);
		this.TargetType= readByte(buf);
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
	
	/**
	 * get 攻击目标
	 * @return 
	 */
	public long getFightTarget(){
		return fightTarget;
	}
	
	/**
	 * set 攻击目标
	 */
	public void setFightTarget(long fightTarget){
		this.fightTarget = fightTarget;
	}
	
	
	@Override
	public int getId() {
		return 502201;
	}

	@Override
	public String getQueue() {
		return null;
	}
	
	@Override
	public String getServer() {
		return null;
	}
	
	public byte getTargetType() {
		return TargetType;
	}

	public void setTargetType(byte targetType) {
		TargetType = targetType;
	}

	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//攻击类型
		buf.append("fightType:" + fightType +",");
		//攻击朝向
		buf.append("fightDirection:" + fightDirection +",");
		//攻击目标
		buf.append("fightTarget:" + fightTarget +",");
		buf.append("TargetType:" + TargetType +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}