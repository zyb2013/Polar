package com.game.goldraffle.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 客户端请求积分兑换消息
 */
public class ReqFractionToServerMessage extends Message{

	//物品编号
	private int fractionID;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//物品编号
		writeInt(buf, this.fractionID);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//物品编号
		this.fractionID = readInt(buf);
		return true;
	}

	/**
	 * get 物品编号
	 * @return
	 */
	public int getFractionID() {
		return fractionID;
	}

	/**
	 * set 物品编号
	 */
	public void setFractionID(int fractionID) {
		this.fractionID = fractionID;
	}
	

	@Override
	public int getId() {
		return 528205;
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
		//物品编号分
		buf.append("fractionID:" + fractionID +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}