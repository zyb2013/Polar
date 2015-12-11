/**
 * 
 */
package com.game.store.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 * 
 * @date 2013年12月30日 上午9:45:25
 * 
 *       打开远程仓库扣钻石
 */
public class ReqOpenStoreMessage extends Message {

	@Override
	public int getId() {
		return 112208;
	}

	@Override
	public boolean read(IoBuffer arg0) {
		return true;
	}

	@Override
	public boolean write(IoBuffer arg0) {
		return true;
	}

	@Override
	public String getQueue() {
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}

}
