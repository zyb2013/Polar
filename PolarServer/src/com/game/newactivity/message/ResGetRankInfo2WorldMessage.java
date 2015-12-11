package com.game.newactivity.message;

import java.util.LinkedList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;
import com.game.newactivity.model.SimpleRankInfo;

/**
 * @author luminghua
 *
 * @date   2014年2月27日 下午5:13:53
 */
public class ResGetRankInfo2WorldMessage extends Message {

	@Override
	public int getId() {
		return 511309;
	}

	@Override
	public String getQueue() {
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}

	private byte type;//排行榜类型
	private long playerId;//请求的玩家id
	private List<SimpleRankInfo> infoList;//排行榜数据

	@Override
	public boolean read(IoBuffer buff) {
		this.type = readByte(buff);
		this.playerId = readLong(buff);
		short size = readShort(buff);
		infoList = new LinkedList<SimpleRankInfo>();
		for(int i=0;i<size;i++) {
			infoList.add(new SimpleRankInfo(readLong(buff),readString(buff),readByte(buff),readShort(buff),readString(buff)));
		}
		return true;
	}

	@Override
	public boolean write(IoBuffer buff) {
		writeByte(buff,type);
		writeLong(buff,playerId);
		if(infoList == null) {
			writeShort(buff,0);
		}else {
			writeShort(buff,infoList.size());
			for(SimpleRankInfo info:infoList) {
				writeLong(buff,info==null?0:info.getPlayerId());
				writeString(buff,info==null?null:info.getName());
				writeByte(buff,info==null?0:(byte)info.getJob());
				writeShort(buff,info==null?0:info.getRank());
				writeString(buff,info==null?null:info.getData());
			}
		}
		return true;
	}

	public List<SimpleRankInfo> getInfoList() {
		return infoList;
	}

	public void setInfoList(List<SimpleRankInfo> infoList) {
		this.infoList = infoList;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	
}
