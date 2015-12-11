package com.game.summonpet.message;

import com.game.summonpet.bean.SummonPetDetailInfo;
import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 召唤怪增加消息
 */
public class ResSummonPetAddMessage extends Message{

	//增加的召唤怪
	private SummonPetDetailInfo summonpet;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//增加的召唤怪
		writeBean(buf, this.summonpet);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//增加的召唤怪
		this.summonpet = (SummonPetDetailInfo)readBean(buf, SummonPetDetailInfo.class);
		return true;
	}
	
	/**
	 * get 增加的召唤怪
	 * @return 
	 */
	public SummonPetDetailInfo getSummonPet(){
		return summonpet;
	}
	
	/**
	 * set 增加的召唤怪
	 */
	public void setSummonPet(SummonPetDetailInfo summonpet){
		this.summonpet = summonpet;
	}
	
	
	@Override
	public int getId() {
		return 510102;
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
		//增加的召唤怪
		if(this.summonpet!=null) buf.append("summonpet:" + summonpet.toString() +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}