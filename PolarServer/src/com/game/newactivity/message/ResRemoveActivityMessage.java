package com.game.newactivity.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 *
 * @date   2014年3月3日 下午5:13:24
 */
public class ResRemoveActivityMessage extends Message {

	@Override
	public int getId() {
		return 511009;
	}

	@Override
	public String getQueue() {
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}

	private int activityId;
	
	@Override
	public boolean read(IoBuffer buff) {
		return false;
	}

	@Override
	public boolean write(IoBuffer buff) {
		writeInt(buff,activityId);
		return true;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

}
