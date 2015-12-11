package com.game.newactivity.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 *
 * @date   2014年2月27日 下午5:12:16
 * 
 */
public class ReqGetRankInfoMessage extends Message {

	@Override
	public int getId() {
		return 511006;
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
		this.activityId = readInt(buff);
		return true;
	}

	@Override
	public boolean write(IoBuffer buff) {
		return false;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}


}
