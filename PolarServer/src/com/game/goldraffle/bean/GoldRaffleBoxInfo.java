package com.game.goldraffle.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Bean;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 钻石抽奖宝箱信息
 */
public class GoldRaffleBoxInfo extends Bean {
	
	//钻石抽奖宝箱格子列表
	private List<GoldRaffleGridInfo> goldRaffleGridList = new ArrayList<GoldRaffleGridInfo>();
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//钻石抽奖宝箱格子列表
		writeShort(buf, goldRaffleGridList.size());
		for (int i = 0; i < goldRaffleGridList.size(); i++) {
			writeBean(buf, goldRaffleGridList.get(i));
		}
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//钻石抽奖宝箱格子列表
		int goldRaffleGridList_length = readShort(buf);
		for (int i = 0; i < goldRaffleGridList_length; i++) {
			goldRaffleGridList.add((GoldRaffleGridInfo)readBean(buf, GoldRaffleGridInfo.class));
		}
		return true;
	}
	
	/**
	 * get 钻石抽奖宝箱格子列表
	 * @return
	 */
	public List<GoldRaffleGridInfo> getGoldRaffleGridList() {
		return goldRaffleGridList;
	}

	/**
	 * set 钻石抽奖宝箱格子列表
	 */
	public void setGoldRaffleGridList(List<GoldRaffleGridInfo> goldRaffleGridList) {
		this.goldRaffleGridList = goldRaffleGridList;
	}

	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//钻石抽奖宝箱格子列表
		buf.append("goldRaffleGridList:{");
		for (int i = 0; i < goldRaffleGridList.size(); i++) {
			buf.append(goldRaffleGridList.get(i).toString() +",");
		}
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}