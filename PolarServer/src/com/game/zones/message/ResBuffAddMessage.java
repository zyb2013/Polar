package com.game.zones.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 翻牌-副本通关奖励信息消息
 */
public class ResBuffAddMessage extends Message{

	
	
	//BUFF层数
	private byte num;
	//BUFF  id
	private int buffId;

	public int getBuffId() {
		return buffId;
	}

	public void setBuffId(int buffId) {
		this.buffId = buffId;
	}

	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		
		//选择次数
		writeByte(buf, this.num);
		writeInt(buf, this.buffId);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//选择次数
		this.num = readByte(buf);
		this.buffId = readInt(buf);
		return true;
	}
	
	/**
	 * get 选择次数
	 * @return 
	 */
	public byte getNum(){
		return num;
	}
	
	/**
	 * set 选择次数
	 */
	public void setNum(byte num){
		this.num = num;
	}
	
	
	
	@Override
	public int getId() {
		return 528317;
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
		buf.append("num:" + num +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}