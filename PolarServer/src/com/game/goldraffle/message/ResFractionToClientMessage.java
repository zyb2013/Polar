package com.game.goldraffle.message;

import com.game.message.Message;
import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 积分兑换后发送到客户端消息
 */
public class ResFractionToClientMessage extends Message{

	//积分
	private int fraction;
	
	//积分兑换物品的ID
	private int itemID;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//积分
		writeInt(buf, this.fraction);
		//积分兑换物品的ID
		writeInt(buf, this.itemID);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//积分
		this.fraction = readInt(buf);
		//积分兑换物品的ID
		this.itemID = readInt(buf);
		return true;
	}
	
	/**
	 * get 积分
	 * @return
	 */
	public int getFraction() {
		return fraction;
	}

	/**
	 * set 积分
	 */
	public void setFraction(int fraction) {
		this.fraction = fraction;
	}

	/**
	 * get 积分兑换物品的ID
	 * @return
	 */
	public int getItemID() {
		return itemID;
	}

	/**
	 * set 积分兑换物品的ID
	 */
	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
	

	@Override
	public int getId() {
		return 528106;
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
		//积分
		buf.append("fraction:" + fraction +",");
		//积分兑换物品的ID
		buf.append("itemID:" + itemID +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}