package com.game.newactivity.message;

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;
import com.game.newactivity.model.SimpleRankInfo;

/**
 * @author luminghua
 *
 * @date   2014年2月27日 下午5:13:53
 */
public class ResGetRankInfoMessage extends Message {

	@Override
	public int getId() {
		return 511007;
	}

	@Override
	public String getQueue() {
		return null;
	}

	@Override
	public String getServer() {
		return null;
	}
	
	private int activityId;
	private List<SimpleRankInfo> infoList;//排行榜数据

	@Override
	public boolean read(IoBuffer buff) {
		return false;
	}

	@Override
	public boolean write(IoBuffer buff) {
		writeInt(buff,activityId);
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

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public List<SimpleRankInfo> getInfoList() {
		return infoList;
	}

	public void setInfoList(List<SimpleRankInfo> infoList) {
		this.infoList = infoList;
	}
}
