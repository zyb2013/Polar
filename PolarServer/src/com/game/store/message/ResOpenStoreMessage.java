/**
 * 
 */
package com.game.store.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 *
 * @date   2013年12月30日 上午9:53:54
 */
public class ResOpenStoreMessage extends Message {

	private byte result;// 0失败 1成功

	@Override
	public int getId() {
		return 112209;
	}

	@Override
	public boolean read(IoBuffer buff) {
		this.result = readByte(buff);
		return true;
	}

	@Override
	public boolean write(IoBuffer buff) {
		writeByte(buff, result);
		return false;
	}

	public void succeed() {
		result = 1;
	}

	@Override
	public String getQueue() {
		// TODO Auto-generated constructor stub
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}

}
