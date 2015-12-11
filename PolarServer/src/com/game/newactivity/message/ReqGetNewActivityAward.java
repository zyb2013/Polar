package com.game.newactivity.message;

import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;

/**
* @author luminghua,by python
*
* @date 2014-02-25 10:39* * 获取活动奖励
*
*/
public class ReqGetNewActivityAward extends Message{


	@Override
	public int getId() {
		return 511003;
	}

	@Override
	public String getQueue() {
		return null;
	}
	
	@Override
	public String getServer() {
		return null;
	}
	//id
	private int activityId;
	//序号

	private byte order;

	public int getActivityId(){
		return activityId;
	}
	public void setActivityId(int activityId){
		this.activityId=activityId;
	}
	public int getOrder(){
		return order;
	}
	public void setCond(int order){
		this.order=(byte) order;
	}
	@Override
	public boolean read(IoBuffer buff) {
		activityId = readInt(buff);		order = readByte(buff);		return true;
	}


	@Override
	public boolean write(IoBuffer buff) {
		writeInt(buff,activityId);		writeByte(buff,order);		return true;
	}
}