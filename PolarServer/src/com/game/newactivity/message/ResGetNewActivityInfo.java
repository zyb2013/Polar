package com.game.newactivity.message;

import java.util.List;import org.apache.mina.core.buffer.IoBuffer;import com.game.message.Message;import com.game.newactivity.model.DetailActivityInfo;import com.game.newactivity.model.DetailActivityInfo.Row;

/**
* @author luminghua,by python
*
* @date 2014-02-25 10:39* * 返回具体活动信息
*
*/
public class ResGetNewActivityInfo extends Message{


	@Override
	public int getId() {
		return 511002;
	}

	@Override
	public String getQueue() {
		return null;
	}
	
	@Override
	public String getServer() {
		return null;
	}
	private DetailActivityInfo detailInfo;	private List<Integer> canGet;
	public DetailActivityInfo getDetailInfo(){
		return detailInfo;
	}
	public void setDetailInfo(DetailActivityInfo detailInfo){
		this.detailInfo=detailInfo;
	}
	public List<Integer> getCanGet() {		return canGet;	}	public void setCanGet(List<Integer> canGet) {		this.canGet = canGet;	}	@Override
	public boolean read(IoBuffer buff) {
		return true;
	}


	@Override
	public boolean write(IoBuffer buff) {		writeInt(buff,detailInfo.getId());		writeInt(buff,detailInfo.getStartTime());		writeInt(buff,detailInfo.getEndTime());		writeString(buff,detailInfo.getCondDesc());		List<Row> rows = detailInfo.getRows();		if(rows == null) {			writeShort(buff,0);		}else {			writeShort(buff,rows.size());			for(int i=0;i<rows.size();i++) {				Row row = rows.get(i);				writeString(buff,row.getCond());				writeString(buff,row.getAward());				writeByte(buff,canGet.get(i).byteValue());			}		}
		return true;
	}
}