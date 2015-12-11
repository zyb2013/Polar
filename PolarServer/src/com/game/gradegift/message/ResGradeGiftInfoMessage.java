package com.game.gradegift.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
import com.game.gradegift.bean.GradeGiftInfo;
/**
  * 全服等级礼包信息推送
 */
public class ResGradeGiftInfoMessage extends Message{

	//礼包领取记录
	private GradeGiftInfo info;
	/**
 	 *set 礼包领取记录
	 *@return
	 */
	public void setInfo(GradeGiftInfo info){
		this.info = info;
	}

	/**
 	 *get 礼包领取记录
	 *@return
	 */
	public GradeGiftInfo getInfo(){
		return this.info;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//礼包领取记录
		writeBean(buf,this.info);
		return true;
	}

	@Override
	public int getId() {
		return 600005;
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
		//礼包领取记录
		this.info = (GradeGiftInfo)readBean(buf,GradeGiftInfo.class);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//礼包领取记录
		if(this.info!=null) buf.append("info:"+info.toString()+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}