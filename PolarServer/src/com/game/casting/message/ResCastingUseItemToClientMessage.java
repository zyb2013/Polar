package com.game.casting.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 使用铸造奖励仓库物品后发送到客户端消息
 */
public class ResCastingUseItemToClientMessage extends Message{

	//操作铸造奖励仓库物品的类型,1表示取出;2表示出售;3表示分解
	private int type;
	
	//1成功 0失败
	private int isSuccess;
	
	//格子编号,>=0表示格子编号
	private int grididx;
	
	//type=2时表示出售该格子物品获得的金币数量;type=3时表示分解该格子物品获得的工艺数量
	private int value;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//操作铸造奖励仓库物品的类型,1表示取出;2表示出售;3表示分解
		writeInt(buf, this.type);
		//1成功 0失败
		writeInt(buf, this.isSuccess);
		//格子编号,>=0表示格子编号
		writeInt(buf, this.grididx);
		//type=2时表示出售该格子物品获得的金币数量;type=3时表示分解该格子物品获得的工艺数量
		writeInt(buf, this.value);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//操作铸造奖励仓库物品的类型,1表示取出;2表示出售;3表示分解
		this.type = readInt(buf);
		//1成功 0失败
		this.isSuccess = readInt(buf);
		//格子编号,>=0表示格子编号
		this.isSuccess = readInt(buf);
		//type=2时表示出售该格子物品获得的金币数量;type=3时表示分解该格子物品获得的工艺数量
		this.isSuccess = readInt(buf);
		return true;
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
	 * get type=2时表示出售该格子物品获得的金币数量;type=3时表示分解该格子物品获得的工艺数量
	 * @return
	 */
	public int getValue() {
		return value;
	}

	/**
	 * set type=2时表示出售该格子物品获得的金币数量;type=3时表示分解该格子物品获得的工艺数量
	 */
	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public int getId() {
		return 529103;
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
		//操作铸造奖励仓库物品的类型,1表示取出;2表示出售;3表示分解
		buf.append("type:" + type +",");
		//1成功 0失败
		buf.append("isSuccess:" + isSuccess +",");
		//格子编号,>=0表示格子编号
		buf.append("grididx:" + grididx +",");
		//type=2时表示出售该格子物品获得的金币数量;type=3时表示分解该格子物品获得的工艺数量
		buf.append("value:" + value +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}