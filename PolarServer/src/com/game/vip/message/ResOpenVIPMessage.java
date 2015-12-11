/**
 * 
 */
package com.game.vip.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 * 
 * @date 2013年12月28日 下午5:47:20
 * 
 *       开通结果
 */
public class ResOpenVIPMessage extends Message {

	private byte result;// 0失败，1成功

	@Override
	public int getId() {
		return 147106;
	}
	@Override
	public boolean read(IoBuffer buff) {
		result = readByte(buff);
		return true;
	}

	@Override
	public boolean write(IoBuffer buff) {
		writeByte(buff, result);
		return true;
	}

	public byte getResult() {
		return result;
	}

	public void setResult(byte result) {
		this.result = result;
	}

	public void succeed() {
		this.result = 1;
	}

	@Override
	public String getQueue() {
		// TODO Auto-generated constructor stub
		return null;
	}
	@Override
	public String getServer() {
		// TODO Auto-generated constructor stub
		return null;
	}
}
