package com.game.gradegift.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
import com.game.gradegift.bean.GainGradeGiftInfo;
/**
  * 角色等级礼包次数推送（单个）
 */
public class ResGainGradeGiftInfoMessage extends Message{

	//礼包领取的次数信息
	private GainGradeGiftInfo awardInfo;
	/**
 	 *set 礼包领取的次数信息
	 *@return
	 */
	public void setAwardInfo(GainGradeGiftInfo awardInfo){
		this.awardInfo = awardInfo;
	}

	/**
 	 *get 礼包领取的次数信息
	 *@return
	 */
	public GainGradeGiftInfo getAwardInfo(){
		return this.awardInfo;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//礼包领取的次数信息
		writeBean(buf,this.awardInfo);
		return true;
	}

	@Override
	public int getId() {
		return 600006;
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
		//礼包领取的次数信息
		this.awardInfo = (GainGradeGiftInfo)readBean(buf,GainGradeGiftInfo.class);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//礼包领取的次数信息
		if(this.awardInfo!=null) buf.append("awardInfo:"+awardInfo.toString()+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}