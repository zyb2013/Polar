package com.game.newactivity.message;

import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;

/**
* @author luminghua,by python
*
* @date 2014-02-25 10:39* * 获取具体活动信息
*
*/
public class ReqGetNewActivityInfo extends Message{


	@Override
	public int getId() {
		return 511001;
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
	public int getActivityId(){
		return activityId;
	}	public void setActivityId(int activityId){
		this.activityId=activityId;
	}
	@Override
	public boolean read(IoBuffer buff) {
		activityId = readInt(buff);		return true;
	}


	@Override
	public boolean write(IoBuffer buff) {
		writeInt(buff,activityId);		return true;
	}
}