package com.game.liveness.message;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.liveness.structs.EventInfo;
import com.game.message.Message;

/**
 * 活跃度进度列表返回
 * @author hongxiao.z
 * @date   2013-12-26  下午3:27:57
 */
public class ResLivenessEventsToClientMessage extends Message
{
	/**
	 * 活跃事件列表
	 */
	private List<EventInfo> events;
	
	public ResLivenessEventsToClientMessage(List<EventInfo> events)
	{
		this.events = events;
	}
	
	@Override
	public int getId() 
	{
		return 600103;
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
	public boolean read(IoBuffer arg0) 
	{
		int size = readShort(arg0);
		
		if(size == 0)
		{
			return true;
		}
		
		this.events = new ArrayList<EventInfo>();
		
		for (int i = 0; i < size; i++) 
		{
			EventInfo event = new EventInfo();
			event.setEventid(readShort(arg0));
			event.setCount(readShort(arg0));
			events.add(event);
		}
		
		return true;
	}

	@Override
	public boolean write(IoBuffer buf) 
	{
		//将活跃度写入流
		writeShort(buf, events.size());
		for (EventInfo event : this.events) 
		{
			writeShort(buf,event.getEventid());
			writeShort(buf,event.getCount());
			//writeBean(buf, event);
		}
		
		return true;
	}

	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}
