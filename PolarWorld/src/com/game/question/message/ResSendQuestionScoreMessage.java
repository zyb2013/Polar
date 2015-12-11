package com.game.question.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 最终得分消息
 */
public class ResSendQuestionScoreMessage extends Message{

	//答题分数
	private int score;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//答题分数
		writeInt(buf, this.score);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//答题分数
		this.score = readInt(buf);
		return true;
	}
	
	/**
	 * get 答题分数
	 * @return 
	 */
	public int getScore(){
		return score;
	}
	
	/**
	 * set 答题分数
	 */
	public void setScore(int score){
		this.score = score;
	}
	
	
	@Override
	public int getId() {
		return 158103;
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
		//答题分数
		buf.append("score:" + score +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}