package com.game.casting.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 客户端请求使用铸造奖励仓库物品消息
 */
public class ReqCastingUseItemToServerMessage extends Message{

	//格子编号,>=0表示格子编号
	private int grididx;
	
	//操作铸造奖励仓库物品的类型,1表示取出;2表示出售;3表示分解
	private int type;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//格子编号,>=0表示格子编号
		writeInt(buf, this.grididx);
		//操作铸造奖励仓库物品的类型,1表示取出;2表示出售;3表示分解
		writeInt(buf, this.type);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//格子编号,>=0表示格子编号
		this.grididx = readInt(buf);
		//操作铸造奖励仓库物品的类型,1表示取出;2表示出售;3表示分解
		this.type = readInt(buf);
		return true;
	}
	
	/**
	 * get 格子编号,>=0表示格子编号
	 * @return
	 */
	public int getGrididx() {
		return grididx;
	}

	/**
	 * set 格子编号,>=0表示格子编号
	 */
	public void setGrididx(int grididx) {
		this.grididx = grididx;
	}

	/**
	 * get 操作铸造奖励仓库物品的类型,1表示取出;2表示出售;3表示分解
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * set 操作铸造奖励仓库物品的类型,1表示取出;2表示出售;3表示分解
	 */
	public void setType(int type) {
		this.type = type;
	}

	@Override
	public int getId() {
		return 529203;
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
		//格子编号,>=0表示格子编号
		buf.append("grididx:" + grididx +",");
		//操作铸造奖励仓库物品的类型,1表示取出;2表示出售;3表示分解
		buf.append("type:" + type +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}