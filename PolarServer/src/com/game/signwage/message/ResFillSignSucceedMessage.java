package com.game.signwage.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 补签成功返回
 * @author hongxiao.z
 * @date   2013-12-30  下午4:27:24
 */
public class ResFillSignSucceedMessage extends Message
{
	private int day;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf)
	{
		writeInt(buf, day);
		return true;
	}
	
	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf)
	{
		this.day = readInt(buf);
		return true;
	}
	
	
	@Override
	public int getId() {
		return 601001;
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