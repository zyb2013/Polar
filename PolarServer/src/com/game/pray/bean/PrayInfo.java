package com.game.pray.bean;


import com.game.message.Bean;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 祈愿界面内容
 */
public class PrayInfo extends Bean {

	//祈愿可获得金币数量
	private int prayGold;
	
	//祈愿金币花费的钻石数量
	private int prayGoldCost;
	
	//祈愿金币剩余次数
	private int prayGoldTimes;
	
	//祈愿可获得经验数量
	private int prayExp;
	
	//祈愿经验花费的钻石数量
	private int prayExpCost;
	
	//祈愿经验剩余次数
	private int prayExpTimes;
	
	//第一次祈愿,0表示没有第一次祈愿;1表示已经第一次祈愿
	private byte firstPray;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//祈愿可获得金币数量
		writeInt(buf, this.prayGold);
		//祈愿金币花费的钻石数量
		writeInt(buf, this.prayGoldCost);
		//祈愿金币剩余次数
		writeInt(buf, this.prayGoldTimes);
		//祈愿可获得经验数量
		writeInt(buf, this.prayExp);
		//祈愿经验花费的钻石数量
		writeInt(buf, this.prayExpCost);
		//祈愿经验剩余次数
		writeInt(buf, this.prayExpTimes);
		//第一次祈愿,0表示没有第一次祈愿;1表示已经第一次祈愿
		writeByte(buf, this.firstPray);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//祈愿可获得金币数量
		this.prayGold = readInt(buf);
		//祈愿金币花费的钻石数量
		this.prayGoldCost = readInt(buf);
		//祈愿金币剩余次数
		this.prayGoldTimes = readInt(buf);
		//祈愿可获得经验数量
		this.prayExp = readInt(buf);
		//祈愿经验花费的钻石数量
		this.prayExpCost = readInt(buf);
		//祈愿经验剩余次数
		this.prayExpTimes = readInt(buf);
		//第一次祈愿,0表示没有第一次祈愿;1表示已经第一次祈愿
		this.firstPray = readByte(buf);
		return true;
	}
	
	/**
	 * get 祈愿可获得金币数量
	 */
	public int getPrayGold() {
		return prayGold;
	}

	/**
	 * set 祈愿可获得金币数量
	 * @param prayGold
	 */
	public void setPrayGold(int prayGold) {
		this.prayGold = prayGold;
	}

	/**
	 * get 祈愿金币花费的钻石数量
	 */
	public int getPrayGoldCost() {
		return prayGoldCost;
	}

	/**
	 * set 祈愿金币花费的钻石数量
	 * @param prayGoldCost
	 */
	public void setPrayGoldCost(int prayGoldCost) {
		this.prayGoldCost = prayGoldCost;
	}

	/**
	 * get 祈愿金币剩余次数
	 */
	public int getPrayGoldTimes() {
		return prayGoldTimes;
	}

	/**
	 * set 祈愿金币剩余次数
	 * @param prayGoldTimes
	 */
	public void setPrayGoldTimes(int prayGoldTimes) {
		this.prayGoldTimes = prayGoldTimes;
	}

	/**
	 * get 祈愿可获得经验数量
	 */
	public int getPrayExp() {
		return prayExp;
	}

	/**
	 * set 祈愿可获得经验数量
	 * @param prayExp
	 */
	public void setPrayExp(int prayExp) {
		this.prayExp = prayExp;
	}

	/**
	 * get 祈愿经验花费的钻石数量
	 */
	public int getPrayExpCost() {
		return prayExpCost;
	}

	/**
	 * set 祈愿经验花费的钻石数量
	 * @param prayExpCost
	 */
	public void setPrayExpCost(int prayExpCost) {
		this.prayExpCost = prayExpCost;
	}

	/**
	 * get 祈愿经验剩余次数
	 */
	public int getPrayExpTimes() {
		return prayExpTimes;
	}

	/**
	 * set 祈愿经验剩余次数
	 * @param prayExpTimes
	 */
	public void setPrayExpTimes(int prayExpTimes) {
		this.prayExpTimes = prayExpTimes;
	}
	
	/**
	 * get 第一次祈愿,0表示没有第一次祈愿;1表示已经第一次祈愿
	 */
	public byte getFirstPray() {
		return firstPray;
	}

	/**
	 * set 第一次祈愿,0表示没有第一次祈愿;1表示已经第一次祈愿
	 * @param firstPray
	 */
	public void setFirstPray(byte firstPray) {
		this.firstPray = firstPray;
	}

	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//祈愿可获得金币数量
		buf.append("prayGold:" + prayGold +",");
		//祈愿金币花费的钻石数量
		buf.append("prayGoldCost:" + prayGoldCost +",");
		//祈愿金币剩余次数
		buf.append("prayGoldExp:" + prayGoldTimes +",");
		//祈愿可获得经验数量
		buf.append("prayExp:" + prayExp +",");
		//祈愿经验花费的钻石数量
		buf.append("prayExpCost:" + prayExpCost +",");
		//祈愿经验剩余次数
		buf.append("prayExpTimes:" + prayExpTimes +",");
		//第一次祈愿,0表示没有第一次祈愿;1表示已经第一次祈愿
		buf.append("firstPray:" + firstPray +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}