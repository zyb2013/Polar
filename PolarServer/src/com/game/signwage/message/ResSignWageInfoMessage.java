package com.game.signwage.message;

import com.game.signwage.bean.SignInfo;
import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 签到消息消息
 */
public class ResSignWageInfoMessage extends Message{

	//签到消息
	private SignInfo signInfo;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//签到消息
//		writeBean(buf, this.signInfo);
		//当前年
		writeInt(buf, signInfo.getYear());
		//当前月
		writeInt(buf, signInfo.getMonth());
		//当前天
		writeInt(buf, signInfo.getDay());
		//签到日期
		writeShort(buf, signInfo.getDaylist().size());
		for (int i = 0; i < signInfo.getDaylist().size(); i++) {
			writeInt(buf, signInfo.getDaylist().get(i));
		}
		//签到奖励列表
		writeShort(buf, signInfo.getAward().size());
		for (int i = 0; i < signInfo.getAward().size(); i++) {
			writeInt(buf, signInfo.getAward().get(i));
		}
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//签到消息
//		this.signInfo = (SignInfo)readBean(buf, SignInfo.class);
		
		this.signInfo = new SignInfo();
		
		//当前年
		this.signInfo.setYear(readInt(buf));
		//当前月
		this.signInfo.setMonth(readInt(buf));
		//当前天
		this.signInfo.setDay(readInt(buf));
		//签到日期
		int daylist_length = readShort(buf);
		for (int i = 0; i < daylist_length; i++) {
			signInfo.getDaylist().add(readInt(buf));
		}
		//签到奖励列表
		int award_length = readShort(buf);
		for (int i = 0; i < award_length; i++) {
			signInfo.getDaylist().add(readInt(buf));
		}
		return true;
	}
	
	/**
	 * get 签到消息
	 * @return 
	 */
	public SignInfo getSignInfo(){
		return signInfo;
	}
	
	/**
	 * set 签到消息
	 */
	public void setSignInfo(SignInfo signInfo){
		this.signInfo = signInfo;
	}
	
	
	@Override
	public int getId() {
		return 152101;
	}

	@Override
	public String getQueue() {
		return null;
	}
	
	@Override
	public String getServer() {
		return null;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//签到消息
		if(this.signInfo!=null) buf.append("signInfo:" + signInfo.toString() +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}