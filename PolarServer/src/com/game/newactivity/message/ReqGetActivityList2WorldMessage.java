package com.game.newactivity.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;
import com.game.message.pool.ServerProtocol;

/**
 * @author luminghua
 *
 * @date   2014年3月4日 下午9:22:09
 */
public class ReqGetActivityList2WorldMessage extends Message {

	@Override
	public int getId() {
		return ServerProtocol.ReqGetActivityList2WorldMessage;
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
