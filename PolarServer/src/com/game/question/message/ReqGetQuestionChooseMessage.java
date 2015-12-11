package com.game.question.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 选择答案消息
 */
public class ReqGetQuestionChooseMessage extends Message{

	//选项
	private byte choose;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//选项
		writeByte(buf, this.choose);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//选项
		this.choose = readByte(buf);
		return true;
	}
	
	/**
	 * get 选项
	 * @return 
	 */
	public byte getChoose(){
		return choose;
	}
	
	/**
	 * set 选项
	 */
	public void setChoose(byte choose){
		this.choose = choose;
	}
	
	
	@Override
	public int getId() {
		return 158202;
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
		//选项
		buf.append("choose:" + choose +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}