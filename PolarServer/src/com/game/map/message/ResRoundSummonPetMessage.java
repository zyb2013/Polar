package com.game.map.message;

import com.game.map.bean.SummonPetInfo;
import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 周围召唤怪消息
 */
public class ResRoundSummonPetMessage extends Message{

	//周围召唤怪信息
	private SummonPetInfo pet;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//周围召唤怪信息
		writeBean(buf, this.pet);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//周围召唤怪信息
		this.pet = (SummonPetInfo)readBean(buf, SummonPetInfo.class);
		return true;
	}
	
	/**
	 * get 周围召唤怪信息
	 * @return 
	 */
	public SummonPetInfo getPet(){
		return pet;
	}
	
	/**
	 * set 周围召唤怪信息
	 */
	public void setPet(SummonPetInfo pet){
		this.pet = pet;
	}
	
	
	@Override
	public int getId() {
		return 501104;
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
		//周围召唤怪信息
		if(this.pet!=null) buf.append("pet:" + pet.toString() +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}