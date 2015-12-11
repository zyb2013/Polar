package com.game.zones.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 爬塔面板信息返回
 * @author hongxiao.z
 * @date   2014-1-2  上午9:54:17
 */
public class ResTowerInfoMessage extends Message
{
	/*副本id*/
	private int zonesid;
	
	/*历史通关时间,0未通关过*/
	private int historyTime;
	
	/*今日还可扫荡的次数*/
	private int dayCount;
	
	/*最佳通关成绩*/
	private int optimumTime;
	
	/*最佳通关角色名*/
	private String optimumName;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf)
	{
		writeInt(buf, zonesid);
		writeInt(buf, historyTime);
		writeInt(buf, dayCount);
		writeInt(buf, optimumTime);
		writeString(buf, optimumName);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		this.zonesid = readInt(buf);
		this.historyTime = readInt(buf);
		this.dayCount = readInt(buf);
		this.optimumTime = readInt(buf);
		this.optimumName = readString(buf);
		return true;
	}
	
	
	
	@Override
	public int getId() {
		return 610001;
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
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}