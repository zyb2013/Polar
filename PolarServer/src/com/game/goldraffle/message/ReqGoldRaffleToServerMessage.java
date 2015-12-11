package com.game.goldraffle.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 客户端请求钻石抽奖消息
 */
public class ReqGoldRaffleToServerMessage extends Message{

	//抽奖类型,1-表示抽1次;2-表示抽10次;3-表示抽50次
	private byte type;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//抽奖类型,1-表示抽1次;2-表示抽10次;3-表示抽50次
		writeByte(buf, this.type);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//抽奖类型,1-表示抽1次;2-表示抽10次;3-表示抽50次
		this.type = readByte(buf);
		return true;
	}
	
	/**
	 * get 抽奖类型,1-表示抽1次;2-表示抽10次;3-表示抽50次
	 */
	public byte getType() {
		return type;
	}

	/**
	 * set 抽奖类型,1-表示抽1次;2-表示抽10次;3-表示抽50次
	 * @param type
	 */
	public void setType(byte type) {
		this.type = type;
	}
	
	@Override
	public int getId() {
		return 528202;
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
		//抽奖类型,1-表示抽1次;2-表示抽10次;3-表示抽50次
		buf.append("type:" + type +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}