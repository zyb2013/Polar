package com.game.db.bean;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Bean;
/**
  * BOSS 掉落信息
 */
public class BossEventBean extends Bean{

	//事件ID
	private long eventId;
	//玩家名
	private String playerName;
	//玩家ID
	private long playerId;
	//Boss地图ID
	private int bossMapId;
	//Boss ID
	private int bossId;
	//物品信息
	private String item;
	//事件类型
	private byte type;
	//事件时间
	private long time;
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
 	 *set Boss地图ID
	 *@return
	 */
	public void setBossMapId(int bossMapId){
		this.bossMapId = bossMapId;
	}

	/**
 	 *get Boss地图ID
	 *@return
	 */
	public int getBossMapId(){
		return this.bossMapId;
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
 	 *set 物品信息
	 *@return
	 */
	public void setItem(String item){
		this.item = item;
	}

	/**
 	 *get 物品信息
	 *@return
	 */
	public String getItem(){
		return this.item;
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
	public void setTime(long time){
		this.time = time;
	}

	/**
 	 *get 事件时间
	 *@return
	 */
	public long getTime(){
		return this.time;
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
		//Boss地图ID
		writeInt(buf,this.bossMapId);
		//Boss ID
		writeInt(buf,this.bossId);
		//物品信息
		writeString(buf,this.item);
		//事件类型
		writeByte(buf,this.type);
		//事件时间
		writeLong(buf,this.time);
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
		//Boss地图ID
		this.bossMapId = readInt(buf);
		//Boss ID
		this.bossId = readInt(buf);
		//物品信息
		this.item = readString(buf);
		//事件类型
		this.type = readByte(buf);
		//事件时间
		this.time = readLong(buf);
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
		//Boss地图ID
		buf.append("bossMapId:"+bossMapId+",");
		//Boss ID
		buf.append("bossId:"+bossId+",");
		//物品信息
		buf.append("itemInfo:"+item+",");
		//事件类型
		buf.append("type:"+type+",");
		//事件时间
		buf.append("time:"+time+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}