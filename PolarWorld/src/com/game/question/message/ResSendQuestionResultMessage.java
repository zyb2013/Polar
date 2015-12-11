package com.game.question.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 答题结果消息
 */
public class ResSendQuestionResultMessage extends Message{

	//0:正确 1:错误
	private byte isRight;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//0:正确 1:错误
		writeByte(buf, this.isRight);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//0:正确 1:错误
		this.isRight = readByte(buf);
		return true;
	}
	
	/**
	 * get 0:正确 1:错误
	 * @return 
	 */
	public byte getIsRight(){
		return isRight;
	}
	
	/**
	 * set 0:正确 1:错误
	 */
	public void setIsRight(byte isRight){
		this.isRight = isRight;
	}
	
	
	@Override
	public int getId() {
		return 158102;
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
		//0:正确 1:错误
		buf.append("isRight:" + isRight +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}