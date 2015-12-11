package com.game.casting.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 铸造兑换后发送到客户端消息
 */
public class ResCastingExchangeToClientMessage extends Message{

	//玩家的工艺度
	private int technologyPoint;
	
	//铸造兑换物品的ID
	private int itemID;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家的工艺度
		writeInt(buf, this.technologyPoint);
		//铸造兑换物品的ID
		writeInt(buf, this.itemID);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//玩家的工艺度
		this.technologyPoint = readInt(buf);
		//铸造兑换物品的ID
		this.itemID = readInt(buf);
		return true;
	}
	
	/**
	 * get 玩家的工艺度
	 * @return
	 */
	public int getTechnologyPoint() {
		return technologyPoint;
	}

	/**
	 * set 铸造兑换物品的ID
	 */
	public void setTechnologyPoint(int technologyPoint) {
		this.technologyPoint = technologyPoint;
	}

	/**
	 * get 玩家的工艺度
	 * @return
	 */
	public int getItemID() {
		return itemID;
	}

	/**
	 * set 铸造兑换物品的ID
	 */
	public void setItemID(int itemID) {
		this.itemID = itemID;
	}
	
	@Override
	public int getId() {
		return 529106;
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
		//玩家的工艺度
		buf.append("technologyPoint:" + technologyPoint +",");
		//铸造兑换物品的ID
		buf.append("itemID:" + itemID +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}