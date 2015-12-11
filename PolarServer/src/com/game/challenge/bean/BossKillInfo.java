package com.game.challenge.bean;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Bean;
import java.util.List;
import java.util.ArrayList;
import com.game.challenge.bean.BossEventInfo;
/**
  * BOSS 信息
 */
public class BossKillInfo extends Bean{

	//BossID
	private int bossId;
	//BOSS击杀事件
	private List<BossKillEventInfo> events = new ArrayList<BossKillEventInfo>();
	//Boss状态 1-死亡 0-正常
	private byte isDead;
	/**
 	 *set BossID
	 *@return
	 */
	public void setBossId(int bossId){
		this.bossId = bossId;
	}

	/**
 	 *get BossID
	 *@return
	 */
	public int getBossId(){
		return this.bossId;
	}

	/**
 	 *set BOSS击杀事件
	 *@return
	 */
	public void setEvents(List<BossKillEventInfo> events){
		this.events = events;
	}

	/**
 	 *get BOSS击杀事件
	 *@return
	 */
	public List<BossKillEventInfo> getEvents(){
		return this.events;
	}

	/**
 	 *set Boss状态 1-死亡 0-正常
	 *@return
	 */
	public void setIsDead(byte isDead){
		this.isDead = isDead;
	}

	/**
 	 *get Boss状态 1-死亡 0-正常
	 *@return
	 */
	public byte getIsDead(){
		return this.isDead;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//BossID
		writeInt(buf,this.bossId);
		//BOSS击杀事件
		writeShort(buf,events.size());
		for(int i = 0;i < events.size();i++){
			writeBean(buf,events.get(i));
		}
		//Boss状态 1-死亡 0-正常
		writeByte(buf,this.isDead);
		return true;
	}

	/**
 	 *读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//BossID
		this.bossId = readInt(buf);
		//BOSS击杀事件
		int events_length = readShort(buf);
		for(int i = 0;i < events_length;i++){
			events.add((BossKillEventInfo)readBean(buf,BossKillEventInfo.class));
		}
		//Boss状态 1-死亡 0-正常
		this.isDead = readByte(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//BossID
		buf.append("bossId:"+bossId+",");
		//BOSS击杀事件
		buf.append("events:{");
		for(int i=0;i<events.size();i++){
			buf.append(events.get(i).toString()+",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		//Boss状态 1-死亡 0-正常
		buf.append("isDead:"+isDead+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}