package com.game.vip.bean;


import com.game.message.Bean;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 玩家VIP信息
 */
public class VipInfo extends Bean {

	// 0无vip，1体验卡，2月卡，3季度卡，4半年年卡
	private int stage;

	// 真实vip等级
	private int realLevel;

	// VIPLevel
	private int level;
	
	//VIP剩余持续时间 单位:秒 
	private int remain;
	
	// vip经验值
	private int exp;

	//是否可领取，0-不可领取， 1-可领取
	// private int status;
	
	//是否展示 至尊VIP的广告 0-不展示 1-展示
	// private byte showad;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		writeInt(buf, this.stage);
		writeInt(buf, this.realLevel);
		writeInt(buf, this.level);
		writeInt(buf, this.remain);
		writeInt(buf, this.exp);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		this.stage = readInt(buf);
		this.realLevel = readInt(buf);
		this.level = readInt(buf);
		this.remain = readInt(buf);
		this.exp = readInt(buf);
		return true;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public int getRealLevel() {
		return realLevel;
	}

	public void setRealLevel(int realLevel) {
		this.realLevel = realLevel;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getRemain() {
		return remain;
	}

	public void setRemain(int remain) {
		this.remain = remain;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}
	
}