package com.game.newactivity.message;

import java.util.List;import org.apache.mina.core.buffer.IoBuffer;import com.game.message.Message;

/**
* @author luminghua,by python
*
* @date 2014-02-25 10:39* * 返回是否成功领取奖励信息
*
*/
public class ResGetNewActivityAward extends Message{


	@Override
	public int getId() {
		return 511004;
	}

	@Override
	public String getQueue() {
		return null;
	}
	
	@Override
	public String getServer() {
		return null;
	}

	private byte result;	private int activityId;	private List<Integer> items;
	public byte getResult(){
		return result;
	}
	public void setResult(byte result){
		this.result=result;
	}

	@Override
	public boolean read(IoBuffer buff) {
		result = readByte(buff);		return true;
	}

	@Override
	public boolean write(IoBuffer buff) {
		writeByte(buff,result);		writeInt(buff,activityId);		if(items == null) {			writeShort(buff,0);		}else {			writeShort(buff,items.size());			for(Integer id:items) {				writeInt(buff,id);			}		}		return true;
	}	public int getActivityId() {		return activityId;	}	public void setActivityId(int activityId) {		this.activityId = activityId;	}	public List<Integer> getItems() {		return items;	}	public void setItems(List<Integer> items) {		this.items = items;	}		
}