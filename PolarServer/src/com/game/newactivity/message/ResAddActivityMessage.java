package com.game.newactivity.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;
import com.game.newactivity.model.SimpleActivityInfo;

/**
 * @author luminghua
 *
 * @date   2014年3月3日 下午5:11:55
 */
public class ResAddActivityMessage extends Message {

	@Override
	public int getId() {
		return 511008;
	}

	@Override
	public String getQueue() {
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}

	private SimpleActivityInfo info;
	
	@Override
	public boolean read(IoBuffer buff) {
		return false;
	}

	@Override
	public boolean write(IoBuffer buff) {
		writeInt(buff,info.getActivityId());
		writeByte(buff,info.getCount());
		return true;
	}

	public SimpleActivityInfo getInfo() {
		return info;
	}

	public void setInfo(SimpleActivityInfo info) {
		this.info = info;
	}

	
}
