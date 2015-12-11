package com.game.newactivity.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

/**
 * @author luminghua
 *
 * @date   2014年2月27日 下午5:12:16
 */
public class ReqGetRankInfo2WorldMessage extends Message {

	@Override
	public int getId() {
		return 511308;
	}

	@Override
	public String getQueue() {
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}

	private int serverId;//服务器id
	private byte type;//排行榜类型
	private int count;//排行前几名
	private long playerId;//查询的玩家id
	
	@Override
	public boolean read(IoBuffer buff) {
		this.serverId = readInt(buff);
		this.type = readByte(buff);
		this.count = readInt(buff);
		this.playerId = readLong(buff);
		return true;
	}

	@Override
	public boolean write(IoBuffer buff) {
		writeInt(buff,serverId);
		writeByte(buff,type);
		writeInt(buff,count);
		writeLong(buff,playerId);
		return true;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	
}
