package com.game.challenge.message;
import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Message;

import java.util.List;
import java.util.ArrayList;

import com.game.challenge.bean.BossEventInfo;
import com.game.challenge.bean.BossKillEventInfo;
/**
  * BOSS 掉落信息
 */
public class ResBossEventMessage extends Message{

	//消息类型
	private byte type;
	//BOSS事件
	private List<BossEventInfo> events = new ArrayList<BossEventInfo>();
	/**
 	 *set 消息类型
	 *@return
	 */
	public void setType(byte type){
		this.type = type;
	}

	/**
 	 *get 消息类型
	 *@return
	 */
	public byte getType(){
		return this.type;
	}

	/**
 	 *set BOSS事件
	 *@return
	 */
	public void setEvents(List<BossEventInfo> events){
		this.events = events;
	}

	/**
 	 *get BOSS事件
	 *@return
	 */
	public List<BossEventInfo> getEvents(){
		return this.events;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//消息类型
		writeByte(buf,this.type);
		//BOSS事件
		writeShort(buf,events.size());
		for(int i = 0;i < events.size();i++){
			writeBean(buf,events.get(i));
		}
		return true;
	}

	@Override
	public int getId() {
		return 510020;
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
		//消息类型
		this.type = readByte(buf);
		//BOSS事件
		int events_length = readShort(buf);
		for(int i = 0;i < events_length;i++){
			events.add((BossEventInfo)readBean(buf,BossEventInfo.class));
		}
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//消息类型
		buf.append("type:"+type+",");
		//BOSS事件
		buf.append("events:{");
		for(int i=0;i<events.size();i++){
			buf.append(events.get(i).toString()+",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}