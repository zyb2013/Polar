package com.game.attribute.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;
import com.game.message.pool.ServerProtocol;

/**
 * @author luminghua.ko@gmail.com
 *
 * @date   2014年3月18日 下午3:40:55
 */
public class ResAttributeChangeMessage extends Message {

	private byte type;
	private String param;
	
	
	@Override
	public int getId() {
		return ServerProtocol.ResAttributeChangeMessage;
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
	public boolean read(IoBuffer buf) {
		this.type = readByte(buf);
		this.param = readString(buf);
		return true;
	}

	@Override
	public boolean write(IoBuffer buf) {
		writeByte(buf,type);
		writeString(buf,param);
		return true;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}
	
}
