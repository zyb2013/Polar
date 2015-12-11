package com.game.summonpet.message;


import com.game.summonpet.bean.SummonPetAttribute;
import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 召唤怪属性变更消息
 */
public class ResSummonPetAttributeChangeMessage extends Message{

	//召唤怪Id
	private long summonpetId;
	
	//变更的属性
	private SummonPetAttribute attributeChange;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//召唤怪Id
		writeLong(buf, this.summonpetId);
		//变更的属性
		writeBean(buf, this.attributeChange);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//召唤怪Id
		this.summonpetId = readLong(buf);
		//变更的属性
		this.attributeChange = (SummonPetAttribute)readBean(buf, SummonPetAttribute.class);
		return true;
	}
	
	/**
	 * get 召唤怪Id
	 * @return 
	 */
	public long getSummonPetId(){
		return summonpetId;
	}
	
	/**
	 * set 召唤怪Id
	 */
	public void setSummonPetId(long summonpetId){
		this.summonpetId = summonpetId;
	}
	
	/**
	 * get 变更的属性
	 * @return 
	 */
	public SummonPetAttribute getAttributeChange(){
		return attributeChange;
	}
	
	/**
	 * set 变更的属性
	 */
	public void setAttributeChange(SummonPetAttribute attributeChange){
		this.attributeChange = attributeChange;
	}
	
	
	@Override
	public int getId() {
		return 510105;
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
		//召唤怪Id
		buf.append("petId:" + summonpetId +",");
		//变更的属性
		if(this.attributeChange!=null) buf.append("attributeChange:" + attributeChange.toString() +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}