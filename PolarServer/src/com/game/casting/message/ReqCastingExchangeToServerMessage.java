package com.game.casting.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 客户端请求铸造兑换消息
 */
public class ReqCastingExchangeToServerMessage extends Message{

	//物品编号
	private int exchangeID;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//物品编号
		writeInt(buf, this.exchangeID);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//物品编号
		this.exchangeID = readInt(buf);
		return true;
	}

	/**
	 * get 物品编号
	 * @return
	 */
	public int getExchangeID() {
		return exchangeID;
	}

	/**
	 * set 物品编号
	 */
	public void setExchangeID(int exchangeID) {
		this.exchangeID = exchangeID;
	}

	@Override
	public int getId() {
		return 529206;
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
		buf.append("exchangeID:" + exchangeID +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}