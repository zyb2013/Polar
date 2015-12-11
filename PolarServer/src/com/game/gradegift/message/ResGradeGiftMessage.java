package com.game.gradegift.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
import java.util.List;
import java.util.ArrayList;
import com.game.gradegift.bean.GradeGiftInfo;
import com.game.gradegift.bean.GainGradeGiftInfo;
/**
  * 等级礼包信息推送
 */
public class ResGradeGiftMessage extends Message{

	//活动开始时间
	private long startTime;
	//活动结束时间
	private long endTime;
	//剩余时间（秒）
	private int surplusTime;
	//活动奖励列表信息
	private List<GradeGiftInfo> awardInfo = new ArrayList<GradeGiftInfo>();
	//礼包领取的次数信息
	private List<GainGradeGiftInfo> gainAwardInfo = new ArrayList<GainGradeGiftInfo>();
	/**
 	 *set 活动开始时间
	 *@return
	 */
	public void setStartTime(long startTime){
		this.startTime = startTime;
	}

	/**
 	 *get 活动开始时间
	 *@return
	 */
	public long getStartTime(){
		return this.startTime;
	}

	/**
 	 *set 活动结束时间
	 *@return
	 */
	public void setEndTime(long endTime){
		this.endTime = endTime;
	}

	/**
 	 *get 活动结束时间
	 *@return
	 */
	public long getEndTime(){
		return this.endTime;
	}

	/**
 	 *set 剩余时间（秒）
	 *@return
	 */
	public void setSurplusTime(int surplusTime){
		this.surplusTime = surplusTime;
	}

	/**
 	 *get 剩余时间（秒）
	 *@return
	 */
	public int getSurplusTime(){
		return this.surplusTime;
	}

	/**
 	 *set 活动奖励列表信息
	 *@return
	 */
	public void setAwardInfo(List<GradeGiftInfo> awardInfo){
		this.awardInfo = awardInfo;
	}

	/**
 	 *get 活动奖励列表信息
	 *@return
	 */
	public List<GradeGiftInfo> getAwardInfo(){
		return this.awardInfo;
	}

	/**
 	 *set 礼包领取的次数信息
	 *@return
	 */
	public void setGainAwardInfo(List<GainGradeGiftInfo> gainAwardInfo){
		this.gainAwardInfo = gainAwardInfo;
	}

	/**
 	 *get 礼包领取的次数信息
	 *@return
	 */
	public List<GainGradeGiftInfo> getGainAwardInfo(){
		return this.gainAwardInfo;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//活动开始时间
		writeLong(buf,this.startTime);
		//活动结束时间
		writeLong(buf,this.endTime);
		//剩余时间（秒）
		writeInt(buf,this.surplusTime);
		//活动奖励列表信息
		writeShort(buf,awardInfo.size());
		for(int i = 0;i < awardInfo.size();i++){
			writeBean(buf,awardInfo.get(i));
		}
		//礼包领取的次数信息
		writeShort(buf,gainAwardInfo.size());
		for(int i = 0;i < gainAwardInfo.size();i++){
			writeBean(buf,gainAwardInfo.get(i));
		}
		return true;
	}

	@Override
	public int getId() {
		return 600004;
	}
	@Override
	public String getQueue() {
		return null;
	}
	@Override
	public String getServer(){
		return null;
	} 
	/**
 	 *读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//活动开始时间
		this.startTime = readLong(buf);
		//活动结束时间
		this.endTime = readLong(buf);
		//剩余时间（秒）
		this.surplusTime = readInt(buf);
		//活动奖励列表信息
		int awardInfo_length = readShort(buf);
		for(int i = 0;i < awardInfo_length;i++){
			awardInfo.add((GradeGiftInfo)readBean(buf,GradeGiftInfo.class));
		}
		//礼包领取的次数信息
		int gainAwardInfo_length = readShort(buf);
		for(int i = 0;i < gainAwardInfo_length;i++){
			gainAwardInfo.add((GainGradeGiftInfo)readBean(buf,GainGradeGiftInfo.class));
		}
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//活动开始时间
		buf.append("startTime:"+startTime+",");
		//活动结束时间
		buf.append("endTime:"+endTime+",");
		//剩余时间（秒）
		buf.append("surplusTime:"+surplusTime+",");
		//活动奖励列表信息
		buf.append("awardInfo:{");
		for(int i=0;i<awardInfo.size();i++){
			buf.append(awardInfo.get(i).toString()+",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		//礼包领取的次数信息
		buf.append("gainAwardInfo:{");
		for(int i=0;i<gainAwardInfo.size();i++){
			buf.append(gainAwardInfo.get(i).toString()+",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}