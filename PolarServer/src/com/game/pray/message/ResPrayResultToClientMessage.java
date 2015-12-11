package com.game.pray.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 祈愿是否成功发送到客户端消息
 */
public class ResPrayResultToClientMessage extends Message{

	//祈愿的类型,1-祈愿金币;2-祈愿经验
	private byte type;
	
	//1成功 0失败
	private byte isSuccess;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//祈愿的类型,1-祈愿金币;2-祈愿经验
		writeByte(buf, this.type);
		//1成功 0失败
		writeByte(buf, this.isSuccess);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//祈愿的类型,1-祈愿金币;2-祈愿经验
		this.type = readByte(buf);
		//1成功 0失败
		this.isSuccess = readByte(buf);
		return true;
	}
	
	/**
	 * get 祈愿的类型,1-祈愿金币;2-祈愿经验
	 * @return
	 */
	public byte getType() {
		return type;
	}

	/**
	 * set 祈愿的类型,1-祈愿金币;2-祈愿经验
	 */
	public void setType(byte type) {
		this.type = type;
	}
	
	/**
	 * get 1成功 0失败
	 */
	public byte getIsSuccess() {
		return isSuccess;
	}

	/**
	 * set 1成功 0失败
	 */
	public void setIsSuccess(byte isSuccess) {
		this.isSuccess = isSuccess;
	}

	@Override
	public int getId() {
		return 528333;
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
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}