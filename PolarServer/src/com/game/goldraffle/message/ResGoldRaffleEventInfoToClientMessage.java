package com.game.goldraffle.message;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.goldraffle.bean.GoldRaffleEventInfo;
import com.game.message.Message;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 钻石抽奖后增加的抽奖日志返回客户端消息
 */
public class ResGoldRaffleEventInfoToClientMessage extends Message{

	//钻石抽奖后增加的抽奖日志
	private List<GoldRaffleEventInfo> goldRaffleEventInfoList = new ArrayList<GoldRaffleEventInfo>();
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//钻石抽奖后增加的抽奖日志
		writeShort(buf, goldRaffleEventInfoList.size());
		for (int i = 0; i < goldRaffleEventInfoList.size(); i++) {
			writeBean(buf, goldRaffleEventInfoList.get(i));
		}
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//钻石抽奖后增加的抽奖日志
		int eventList_length = readShort(buf);
		for (int i = 0; i < eventList_length; i++) {
			goldRaffleEventInfoList.add((GoldRaffleEventInfo)readBean(buf, GoldRaffleEventInfo.class));
		}		
		return true;
	}
	

	/**
	 * get 钻石抽奖后增加的抽奖日志
	 * @return
	 */
	public List<GoldRaffleEventInfo> getGoldRaffleEventInfoList() {
		return goldRaffleEventInfoList;
	}

	/**
	 * set 钻石抽奖后增加的抽奖日志
	 */
	public void setGoldRaffleEventInfoList(List<GoldRaffleEventInfo> goldRaffleEventInfoList) {
		this.goldRaffleEventInfoList = goldRaffleEventInfoList;
	}
	

	@Override
	public int getId() {
		return 528103;
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
		//钻石抽奖后增加的抽奖日志
		buf.append("goldRaffleEventList:{");
		for (int i = 0; i < goldRaffleEventInfoList.size(); i++) {
			buf.append(goldRaffleEventInfoList.get(i).toString() +",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		buf.append("]");
		return buf.toString();
	}
}