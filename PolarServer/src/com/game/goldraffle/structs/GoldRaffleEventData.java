package com.game.goldraffle.structs;

import com.game.object.GameObject;

/** 
 * 钻石抽奖事件信息
 * 
 * @author xiaozhuoming 
 */
public class GoldRaffleEventData extends GameObject {

	private static final long serialVersionUID = 6920264503159139067L;

	//事件id
	private long eventId;
	
	//消息时间
	private int messageTime;
	
	//消息类型
	private String messageType;
	
	//消息内容
	private String message;

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public int getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(int messageTime) {
		this.messageTime = messageTime;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "eventId = " + this.eventId + ", messageTime = " + messageTime + ", messageType = " + messageType + ", message = " + message;
	}
}