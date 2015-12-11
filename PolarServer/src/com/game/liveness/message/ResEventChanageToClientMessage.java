package com.game.liveness.message;
import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * 活跃事件改变推送
 * @author hongxiao.z
 * @date   2013-12-26  下午3:27:57
 */
public class ResEventChanageToClientMessage extends Message
{
	/**
	 * 当前活跃事件ID
	 */
	private short eventId;
	
	/**
	 * 已执行的次数
	 */
	private short count;
	
	@Override
	public int getId() 
	{
		return 600009;
	}

	@Override
	public String getQueue() 
	{
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}

	@Override
	public boolean read(IoBuffer buf) 
	{
		this.eventId = readShort(buf);
		this.count = readShort(buf);
		return true;
	}

	@Override
	public boolean write(IoBuffer buf) 
	{
		//写入缓冲
		writeShort(buf, this.eventId);
		writeShort(buf, this.count);
		return true;
	}

	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}

	public void setEventId(short eventId) {
		this.eventId = eventId;
	}

	public void setCount(short count) {
		this.count = count;
	}
}
