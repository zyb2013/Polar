package com.game.player.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;
import com.game.message.pool.ServerProtocol;

/**
 * @author luminghua.ko@gmail.com
 *
 * @date   2014年3月20日 下午4:22:46
 */
public class ResRoundChangeJobMessage extends Message {

	@Override
	public int getId() {
		return ServerProtocol.ResRoundChangeJobMessage;
	}

	@Override
	public String getQueue() {
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}

	private long playerId;
	private byte job;
	
	@Override
	public boolean read(IoBuffer buf) {
		this.playerId = readLong(buf);
		this.job = readByte(buf);
		return true;
	}

	@Override
	public boolean write(IoBuffer buf) {
		writeLong(buf,playerId);
		writeByte(buf,job);
		return true;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public byte getJob() {
		return job;
	}

	public void setJob(byte job) {
		this.job = job;
	}

}
