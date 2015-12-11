package com.game.map.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;
import com.game.message.pool.ServerProtocol;

/**
 * @author luminghua.ko@gmail.com
 *
 * @date   2014年3月13日 下午3:50:03
 */
public class ResEquipTimeLostMessage extends Message {

	private long equipId;
	
	@Override
	public int getId() {
		return ServerProtocol.EquipTimeLost;
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
		this.equipId = readLong(buf);
		return true;
	}

	@Override
	public boolean write(IoBuffer buf) {
		writeLong(buf,equipId);
		return true;
	}

	public long getEquipId() {
		return equipId;
	}

	public void setEquipId(long equipId) {
		this.equipId = equipId;
	}

	
}
