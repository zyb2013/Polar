package com.game.goldraffle.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 使用钻石抽奖宝箱物品后发送到客户端消息
 */
public class ResGoldRaffleBoxUseItemToClientMessage extends Message{

	//格子编号
	private int grididx;
	
	//操作钻石抽奖宝箱物品类型, 1表示使用物品或者装备;2表示把物品放入背包
	private int type;
	
	//1成功 0失败
	private int isSuccess;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//格子编号
		writeInt(buf, this.grididx);
		//操作钻石抽奖宝箱物品类型, 1表示使用物品或者装备;2表示把物品放入背包
		writeInt(buf, this.type);
		//1成功 0失败
		writeInt(buf, this.isSuccess);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//格子编号
		this.grididx = readInt(buf);
		//操作钻石抽奖宝箱物品类型, 1表示使用物品或者装备;2表示把物品放入背包
		this.type = readInt(buf);
		//1成功 0失败
		this.isSuccess = readInt(buf);
		return true;
	}
	
	/**
	 * get 格子编号
	 */
	public int getGrididx() {
		return grididx;
	}

	/**
	 * set 格子编号
	 */
	public void setGrididx(int grididx) {
		this.grididx = grididx;
	}

	/**
	 * get 操作钻石抽奖宝箱物品类型, 1表示使用物品或者装备;2表示把物品放入背包
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * set 操作钻石抽奖宝箱物品类型, 1表示使用物品或者装备;2表示把物品放入背包
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * get 1成功 0失败
	 */
	public int getIsSuccess() {
		return isSuccess;
	}

	/**
	 * set 1成功 0失败
	 */
	public void setIsSuccess(int isSuccess) {
		this.isSuccess = isSuccess;
	}

	
	@Override
	public int getId() {
		return 528105;
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
		//格子编号
		buf.append("grididx:" + grididx +",");
		//操作钻石抽奖宝箱物品类型, 1表示使用物品或者装备;2表示把物品放入背包
		buf.append("type:" + type +",");
		//1成功 0失败
		buf.append("isSuccess:" + isSuccess +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}