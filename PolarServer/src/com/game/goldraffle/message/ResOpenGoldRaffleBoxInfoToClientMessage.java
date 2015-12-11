package com.game.goldraffle.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.goldraffle.bean.GoldRaffleBoxInfo;
import com.game.message.Message;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 钻石抽奖宝箱信息发送到客户端消息
 */
public class ResOpenGoldRaffleBoxInfoToClientMessage extends Message{

	//钻石抽奖宝箱信息
	private GoldRaffleBoxInfo goldRaffleBoxInfo;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//钻石抽奖宝箱信息
		writeBean(buf, this.goldRaffleBoxInfo);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//钻石抽奖宝箱信息
		this.goldRaffleBoxInfo = (GoldRaffleBoxInfo)readBean(buf, GoldRaffleBoxInfo.class);
		return true;
	}
	
	/**
	 * get 钻石抽奖宝箱信息
	 * @return
	 */
	public GoldRaffleBoxInfo getGoldRaffleBoxInfo() {
		return goldRaffleBoxInfo;
	}

	/**
	 * set 钻石抽奖宝箱信息
	 */
	public void setGoldRaffleBoxInfo(GoldRaffleBoxInfo goldRaffleBoxInfo) {
		this.goldRaffleBoxInfo = goldRaffleBoxInfo;
	}

	@Override
	public int getId() {
		return 528104;
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
		//钻石抽奖宝箱信息
		if(this.goldRaffleBoxInfo!=null) buf.append("goldRaffleBoxInfo:" + goldRaffleBoxInfo.toString() +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}