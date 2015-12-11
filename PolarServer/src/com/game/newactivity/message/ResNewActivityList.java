package com.game.newactivity.message;

import java.util.List;import org.apache.mina.core.buffer.IoBuffer;import com.game.message.Message;import com.game.newactivity.model.SimpleActivityInfo;

/**
* @author luminghua,by python
*
* @date 2014-02-25 10:39
**推送活动列表
*/
public class ResNewActivityList extends Message{


	@Override
	public int getId() {
		return 511000;
	}

	@Override
	public String getQueue() {
		return null;
	}
	
	@Override
	public String getServer() {
		return null;
	}

	private List<SimpleActivityInfo> activities;

	public List<SimpleActivityInfo> getActivities(){
		return activities;
	}
	public void setActivities(List<SimpleActivityInfo> activities){
		this.activities=activities;	}
	@Override
	public boolean read(IoBuffer buff) {
		return true;
	}

	@Override
	public boolean write(IoBuffer buff) {		if(activities != null) {			writeShort(buff,activities.size());			for(SimpleActivityInfo info : activities) {				writeInt(buff,info.getActivityId());				writeByte(buff,info.getCount());			}		}else {			writeShort(buff,0);		}
		return true;
	}
}