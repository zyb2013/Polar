package com.game.question.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 问题信息消息
 */
public class ResSendQuestionInfoMessage extends Message{

	//题目序号
	private short index;
	
	//题目id,即题目在数据库里面的编号
	private int q_id;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//题目序号
		writeShort(buf, this.index);
		//题目id,即题目在数据库里面的编号
		writeInt(buf, this.q_id);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//题目序号
		this.index = readShort(buf);
		//题目id,即题目在数据库里面的编号
		this.q_id = readInt(buf);
		return true;
	}
	
	/**
	 * get 题目序号
	 * @return 
	 */
	public short getIndex(){
		return index;
	}
	
	/**
	 * set 题目序号
	 */
	public void setIndex(short index){
		this.index = index;
	}
	
	/**
	 * get 题目id,即题目在数据库里面的编号
	 * @return 
	 */
	public int getQ_id(){
		return q_id;
	}
	
	/**
	 * set 题目id,即题目在数据库里面的编号
	 */
	public void setQ_id(int q_id){
		this.q_id = q_id;
	}
	
	
	@Override
	public int getId() {
		return 158101;
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
		//题目序号
		buf.append("index:" + index +",");
		//题目id,即题目在数据库里面的编号
		buf.append("q_id:" + q_id +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}