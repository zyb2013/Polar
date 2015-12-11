package com.game.summonpet.message;

import com.game.summonpet.bean.SummonPetDetailInfo;
import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 召唤怪死亡消息
 */
public class ResSummonPetDieMessage extends Message{

	//召唤怪信息
	private SummonPetDetailInfo pet;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//召唤怪信息
		writeBean(buf, this.pet);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//召唤怪信息
		this.pet = (SummonPetDetailInfo)readBean(buf, SummonPetDetailInfo.class);
		return true;
	}
	
	/**
	 * get 召唤怪信息
	 * @return 
	 */
	public SummonPetDetailInfo getPet(){
		return pet;
	}
	
	/**
	 * set 召唤怪信息
	 */
	public void setPet(SummonPetDetailInfo pet){
		this.pet = pet;
	}
	
	
	@Override
	public int getId() {
		return 510106;
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
		//召唤怪信息
		if(this.pet!=null) buf.append("pet:" + pet.toString() +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}