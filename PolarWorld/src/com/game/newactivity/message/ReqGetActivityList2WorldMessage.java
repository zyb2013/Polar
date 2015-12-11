package com.game.newactivity.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 *
 * @date   2014年3月4日 下午9:22:09
 */
public class ReqGetActivityList2WorldMessage extends Message {

	@Override
	public int getId() {
		return 511310;
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
	public boolean read(IoBuffer buff) {
		return false;
	}

	@Override
	public boolean write(IoBuffer buff) {
		return false;
	}

}
