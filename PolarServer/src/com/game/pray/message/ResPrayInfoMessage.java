package com.game.pray.message;

import com.game.pray.bean.PrayInfo;
import com.game.message.Message;
import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 发送祈愿界面内容消息
 */
public class ResPrayInfoMessage extends Message{

	//祈愿界面内容
	private PrayInfo prayInfo;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//祈愿界面内容
		writeBean(buf, this.prayInfo);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//祈愿界面内容
		this.prayInfo = (PrayInfo)readBean(buf, PrayInfo.class);
		return true;
	}
	
	/**
	 * get 祈愿界面内容
	 */
	public PrayInfo getPrayInfo() {
		return prayInfo;
	}

	/**
	 * set 祈愿界面内容
	 * @param prayInfo
	 */
	public void setPrayInfo(PrayInfo prayInfo) {
		this.prayInfo = prayInfo;
	}
	

	@Override
	public int getId() {
		return 528332;
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
		//祈愿界面内容
		if(this.prayInfo!=null) buf.append("prayInfo:" + prayInfo.toString() +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}