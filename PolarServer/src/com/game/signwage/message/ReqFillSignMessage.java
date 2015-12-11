package com.game.signwage.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 补签
 * @author hongxiao.z
 * @date   2013-12-30  下午4:18:26
 */
public class ReqFillSignMessage extends Message{
	
	private int day;
	
	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		writeInt(buf, day);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		this.day = readInt(buf);
		return true;
	}
	
	
	@Override
	public int getId() {
		return 601000;
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
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}