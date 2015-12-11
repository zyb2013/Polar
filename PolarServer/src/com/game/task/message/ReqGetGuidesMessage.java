/**
 * 
 */
package com.game.task.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 *
 * @date   2013年12月30日 下午8:11:57
 */
public class ReqGetGuidesMessage extends Message {

	@Override
	public int getId() {
		return 120218;
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
	public boolean read(IoBuffer arg0) {
		return true;
	}

	@Override
	public boolean write(IoBuffer arg0) {
		return true;
	}

}
