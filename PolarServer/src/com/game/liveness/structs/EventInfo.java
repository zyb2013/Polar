package com.game.liveness.structs;

import com.game.object.GameObject;

/**
 * 事件单元记录实体
 * @author hongxiao.z
 * @date   2013-12-27  下午7:26:06
 */
public class EventInfo extends GameObject
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5206577415952441910L;

	//事件ID
	private short eventid;
	
	//事件次数
	private short count;
	
	//是否触发
	private boolean isTrigger;
	
	public boolean isTrigger() {
		return isTrigger;
	}

	public void setTrigger(boolean isTrigger) {
		this.isTrigger = isTrigger;
	}

	public short getEventid() {
		return eventid;
	}

	public void setEventid(short eventid) {
		this.eventid = eventid;
	}

	public short getCount() {
		return count;
	}

	public void setCount(short count) {
		this.count = count;
	}
	
	
}
