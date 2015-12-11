package com.game.summonpet.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 召唤怪死亡广播消息
 */
public class ResSummonPetDieBroadcastMessage extends Message{

	//死亡召唤怪ID
	private long petId;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//死亡召唤怪ID
		writeLong(buf, this.petId);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//死亡召唤怪ID
		this.petId = readLong(buf);
		return true;
	}
	
	/**
	 * get 死亡召唤怪ID
	 * @return 
	 */
	public long getPetId(){
		return petId;
	}
	
	/**
	 * set 死亡召唤怪ID
	 */
	public void setPetId(long petId){
		this.petId = petId;
	}
	
	
	@Override
	public int getId() {
		return 510111;
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
		//死亡召唤怪ID
		buf.append("petId:" + petId +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}