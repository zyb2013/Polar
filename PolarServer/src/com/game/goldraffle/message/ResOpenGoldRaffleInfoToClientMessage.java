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
 * 打开钻石抽奖界面发送到客户端消息
 */
public class ResOpenGoldRaffleInfoToClientMessage extends Message{

	//优惠券
	private int coupon;

	//积分
	private int fraction;
	
	//钻石抽奖宝箱的当前容量
	private int capacity;

	//钻石抽奖日志列表
	private List<GoldRaffleEventInfo> goldRaffleEventInfoList = new ArrayList<GoldRaffleEventInfo>();


	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//优惠券
		writeInt(buf, this.coupon);
		//积分
		writeInt(buf, this.fraction);
		//钻石抽奖宝箱的当前容量
		writeInt(buf, this.capacity);
		//钻石抽奖日志列表
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
		//优惠券
		this.coupon = readInt(buf);
		//积分
		this.fraction = readInt(buf);
		//钻石抽奖宝箱的当前容量
		this.capacity = readInt(buf);
		//钻石抽奖日志列表
		int eventList_length = readShort(buf);
		for (int i = 0; i < eventList_length; i++) {
			goldRaffleEventInfoList.add((GoldRaffleEventInfo)readBean(buf, GoldRaffleEventInfo.class));
		}		
		return true;
	}

	/**
	 * get 优惠券
	 * @return
	 */
	public int getCoupon() {
		return coupon;
	}

	/**
	 * set 优惠券
	 */
	public void setCoupon(int coupon) {
		this.coupon = coupon;
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
	 * get 钻石抽奖宝箱的当前容量
	 * @return
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * set 钻石抽奖宝箱的当前容量
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * get 钻石抽奖日志列表
	 * @return
	 */
	public List<GoldRaffleEventInfo> getGoldRaffleEventInfoList() {
		return goldRaffleEventInfoList;
	}

	/**
	 * set 钻石抽奖日志列表
	 */
	public void setGoldRaffleEventInfoList(List<GoldRaffleEventInfo> goldRaffleEventInfoList) {
		this.goldRaffleEventInfoList = goldRaffleEventInfoList;
	}

	@Override
	public int getId() {
		return 528101;
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
		//优惠券
		buf.append("coupon:" + coupon +",");
		//积分
		buf.append("fraction:" + fraction +",");
		//钻石抽奖宝箱的当前容量
		buf.append("capacity:" + capacity +",");
		//钻石抽奖日志列表
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