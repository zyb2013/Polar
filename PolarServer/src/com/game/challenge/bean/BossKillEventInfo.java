package com.game.challenge.bean;
import org.apache.mina.core.buffer.IoBuffer;

import com.game.db.bean.BossEventBean;
import com.game.json.JSONserializable;
import com.game.message.Bean;
import com.game.utils.VersionUpdateUtil;
/**
  * BOSS 信息
 */
public class BossKillEventInfo extends Bean{

	//事件ID
	private long eventId;
	//玩家名
	private String playerName;
	//玩家ID
	private long playerId;
	//Boss ID
	private int bossId;
	//事件类型
	private byte type;
	//事件时间
	private long touchTime;
	/**
 	 *set 事件ID
	 *@return
	 */
	public void setEventId(long eventId){
		this.eventId = eventId;
	}

	/**
 	 *get 事件ID
	 *@return
	 */
	public long getEventId(){
		return this.eventId;
	}

	/**
 	 *set 玩家名
	 *@return
	 */
	public void setPlayerName(String playerName){
		this.playerName = playerName;
	}

	/**
 	 *get 玩家名
	 *@return
	 */
	public String getPlayerName(){
		return this.playerName;
	}

	/**
 	 *set 玩家ID
	 *@return
	 */
	public void setPlayerId(long playerId){
		this.playerId = playerId;
	}

	/**
 	 *get 玩家ID
	 *@return
	 */
	public long getPlayerId(){
		return this.playerId;
	}

	/**
 	 *set Boss ID
	 *@return
	 */
	public void setBossId(int bossId){
		this.bossId = bossId;
	}

	/**
 	 *get Boss ID
	 *@return
	 */
	public int getBossId(){
		return this.bossId;
	}

	/**
 	 *set 事件类型
	 *@return
	 */
	public void setType(byte type){
		this.type = type;
	}

	/**
 	 *get 事件类型
	 *@return
	 */
	public byte getType(){
		return this.type;
	}

	/**
 	 *set 事件时间
	 *@return
	 */
	public void setTouchTime(long time){
		this.touchTime = time;
	}

	/**
 	 *get 事件时间
	 *@return
	 */
	public long getTouchTime(){
		return this.touchTime;
	}
	
	public BossEventBean getBossEventBean(){
		BossEventBean bean = new BossEventBean();
		
		bean.setEventId(this.getEventId());
		bean.setBossId(this.getBossId());
		bean.setBossMapId(0);
		bean.setPlayerId(this.getPlayerId());
		bean.setPlayerName(this.getPlayerName());
		bean.setTime(this.getTouchTime());
		bean.setType(this.getType());
		bean.setItem(null);
		
		return bean;
	}
	
	public void setBossKillEventInfo(BossEventBean bean){
		this.setBossId(bean.getBossId());
		this.setEventId(bean.getEventId());
		this.setPlayerId(bean.getPlayerId());
		this.setPlayerName(bean.getPlayerName());
		this.setTouchTime(bean.getTime());
		this.setType(bean.getType());
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//事件ID
		writeLong(buf,this.eventId);
		//玩家名
		writeString(buf,this.playerName);
		//玩家ID
		writeLong(buf,this.playerId);
		//Boss ID
		writeInt(buf,this.bossId);
		//事件类型
		writeByte(buf,this.type);
		//事件时间
		writeLong(buf,this.touchTime);
		return true;
	}

	/**
 	 *读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//事件ID
		this.eventId = readLong(buf);
		//玩家名
		this.playerName = readString(buf);
		//玩家ID
		this.playerId = readLong(buf);
		//Boss ID
		this.bossId = readInt(buf);
		//事件类型
		this.type = readByte(buf);
		//事件时间
		this.touchTime = readLong(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//事件ID
		buf.append("eventId:"+eventId+",");
		//玩家名
		buf.append("playerName:"+playerName+",");
		//玩家ID
		buf.append("playerId:"+playerId+",");
		//Boss ID
		buf.append("bossId:"+bossId+",");
		//事件类型
		buf.append("type:"+type+",");
		//事件时间
		buf.append("touchTime:"+touchTime+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}