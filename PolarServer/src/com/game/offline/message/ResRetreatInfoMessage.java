package com.game.offline.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 *          返回离线信息消息
 */
public class ResRetreatInfoMessage extends Message{

	// 是普通返回还是特效返回，0普通1特效
	private int notifyType;
	
	// 当前离线追加值
	private int offlineCount;
	
	// 当前可领取离线经验
	private int curExp;
	
	// 双倍时需要消耗的钻石
	private int costGold;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		writeInt(buf, notifyType);
		writeInt(buf, offlineCount);
		writeInt(buf, curExp);
		writeInt(buf, costGold);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		this.notifyType = readInt(buf);
		this.offlineCount = readInt(buf);
		this.curExp = readInt(buf);
		this.costGold = readInt(buf);
		return true;
	}

	public int getCostGold() {
		return costGold;
	}

	public void setCostGold(int costGold) {
		this.costGold = costGold;
	}

	public int getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(int notifyType) {
		this.notifyType = notifyType;
	}

	public int getOfflineCount() {
		return offlineCount;
	}

	public void setOfflineCount(int offlineCount) {
		this.offlineCount = offlineCount;
	}

	public int getCurExp() {
		return curExp;
	}

	public void setCurExp(int curExp) {
		this.curExp = curExp;
	}

	@Override
	public int getId() {
		return 150203;
	}

	/*
	 * @see com.game.message.Message#getQueue()
	 */
	@Override
	public String getQueue() {
		// TODO Auto-generated constructor stub
		return null;
	}

	/*
	 * @see com.game.message.Message#getServer()
	 */
	@Override
	public String getServer() {
		// TODO Auto-generated constructor stub
		return null;
	}

}