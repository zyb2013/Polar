package com.game.equipstreng.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 *          发送合成结果消息
 */
public class ResComposeItemToClientMessage extends Message{

	private byte result;// 0失败，1成功
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		writeByte(buf, result);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		this.result = readByte(buf);
		return true;
	}
	
	
	public byte getResult() {
		return result;
	}

	public void setResult(byte result) {
		this.result = result;
	}

	@Override
	public int getId() {
		return 103894;
	}

	@Override
	public String getQueue() {
		return null;
	}
	
	@Override
	public String getServer() {
		return null;
	}
}