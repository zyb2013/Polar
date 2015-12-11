package com.game.goldraffle.message;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.goldraffle.bean.GoldRaffleBoxInfo;
import com.game.message.Message;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 钻石抽奖后返回客户端消息
 */
public class ResGoldRaffleToClientMessage extends Message{

	//优惠券
	private int coupon;
	
	//积分
	private int fraction;
	
	//添加到钻石抽奖宝箱的物品ID列表
	private List<Integer> goldRaffleBoxItemList = new ArrayList<Integer>();
	
	//通过邮件发送的物品ID列表
	private List<Integer> mailItemList = new ArrayList<Integer>();
	
	//添加到钻石抽奖宝箱的物品信息
	private GoldRaffleBoxInfo goldRaffleBoxInfo = new GoldRaffleBoxInfo();
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//优惠券
		writeInt(buf, this.coupon);
		//积分
		writeInt(buf, this.fraction);
		//添加到钻石抽奖宝箱的物品ID列表
		writeShort(buf, goldRaffleBoxItemList.size());
		for (int i = 0; i < goldRaffleBoxItemList.size(); i++) {
			writeInt(buf, goldRaffleBoxItemList.get(i));
		}
		//通过邮件发送的物品ID列表
		writeShort(buf, mailItemList.size());
		for (int i = 0; i < mailItemList.size(); i++) {
			writeInt(buf, mailItemList.get(i));
		}
		//添加到钻石抽奖宝箱的物品信息
		writeBean(buf, this.goldRaffleBoxInfo);
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
		//添加到钻石抽奖宝箱的物品ID列表
		int goldRaffleBoxItemList_length = readShort(buf);
		for (int i = 0; i < goldRaffleBoxItemList_length; i++) {
			goldRaffleBoxItemList.add(readInt(buf));
		}
		//通过邮件发送的物品ID列表
		int mailBoxItemList_length = readShort(buf);
		for (int i = 0; i < mailBoxItemList_length; i++) {
			mailItemList.add(readInt(buf));
		}
		//添加到钻石抽奖宝箱的物品信息
		this.goldRaffleBoxInfo = (GoldRaffleBoxInfo)readBean(buf, GoldRaffleBoxInfo.class);
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
	 * get 添加到钻石抽奖宝箱的物品ID列表
	 * @return
	 */
	public List<Integer> getGoldRaffleBoxItemList() {
		return goldRaffleBoxItemList;
	}

	/**
	 * set 添加到钻石抽奖宝箱的物品ID列表
	 */
	public void setGoldRaffleBoxItemList(List<Integer> goldRaffleBoxItemList) {
		this.goldRaffleBoxItemList = goldRaffleBoxItemList;
	}

	/**
	 * get 通过邮件发送的物品ID列表
	 * @return
	 */
	public List<Integer> getMailItemList() {
		return mailItemList;
	}

	/**
	 * set 通过邮件发送的物品ID列表 
	 */
	public void setMailItemList(List<Integer> mailItemList) {
		this.mailItemList = mailItemList;
	}
	
	/**
	 * get 添加到钻石抽奖宝箱的物品信息
	 * @return
	 */
	public GoldRaffleBoxInfo getGoldRaffleBoxInfo() {
		return goldRaffleBoxInfo;
	}

	/**
	 * set 添加到钻石抽奖宝箱的物品信息
	 */
	public void setGoldRaffleBoxInfo(GoldRaffleBoxInfo goldRaffleBoxInfo) {
		this.goldRaffleBoxInfo = goldRaffleBoxInfo;
	}
	

	@Override
	public int getId() {
		return 528102;
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
		//添加到钻石抽奖宝箱的物品ID列表
		buf.append("goldRaffleGridList:{");
		for (int i = 0; i < goldRaffleBoxItemList.size(); i++) {
			buf.append(goldRaffleBoxItemList.get(i).toString() +",");
		}
		buf.append("},");
		//通过邮件发送的物品ID列表
		buf.append("mailItemList:{");
		for (int i = 0; i < mailItemList.size(); i++) {
			buf.append(mailItemList.get(i).toString() +",");
		}
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		//添加到钻石抽奖宝箱的物品信息
		if(this.goldRaffleBoxInfo!=null) buf.append("goldRaffleBoxInfo:" + goldRaffleBoxInfo.toString() +",");
		buf.append("},");
		buf.append("]");
		return buf.toString();
	}
}