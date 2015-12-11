package com.game.newactivity.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 *
 * @date   2014年2月27日 下午4:47:59
 * 
 * 活动的动态减少或增加
 */
public class ResNewActivityListChange extends Message {

	@Override
	public int getId() {
		return 511005;
	}

	@Override
	public String getQueue() {
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}

	private byte type;//1是增加，2是减少
	private int[] ids;

	@Override
	public boolean read(IoBuffer buff) {
		this.type = readByte(buff);
		short size = readShort(buff);
		ids = new int[size];
		for(int i=0;i<size;i++) {
			ids[i] = readInt(buff);
		}
		return true;
	}

	@Override
	public boolean write(IoBuffer buff) {
		writeByte(buff,type);
		writeShort(buff,ids.length);
		for(int id:ids) {
			writeInt(buff,id);
		}
		return true;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int[] getIds() {
		return ids;
	}

	public void setIds(int[] ids) {
		this.ids = ids;
	}
	
}
