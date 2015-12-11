package com.game.challenge.bean;
import org.apache.mina.core.buffer.IoBuffer;

import com.game.json.JSONserializable;
import com.game.message.Bean;
import com.game.utils.VersionUpdateUtil;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.game.backpack.bean.ItemInfo;
import com.game.backpack.structs.Item;
import com.game.challenge.struct.ItemTrans;
import com.game.db.bean.BossEventBean;
/**
  * BOSS 掉落信息
 */
public class BossEventInfo extends Bean{

	//事件ID
	private long eventId;
	//玩家名
	private String playerName;
	//玩家ID
	private long playerId;
	//BOSS地图ID
	private int bossMapId;
	//BOSS ID
	private int bossId;
	//物品信息
	private ItemInfo itemInfo;
	//事件类型：1-普通掉落信息;2-置顶掉落信息;3-BOSS击杀信息
	private byte type;
	//事件发生时间
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
 	 *set BOSS地图ID
	 *@return
	 */
	public void setBossMapId(int bossMapId){
		this.bossMapId = bossMapId;
	}

	/**
 	 *get BOSS地图ID
	 *@return
	 */
	public int getBossMapId(){
		return this.bossMapId;
	}

	/**
 	 *set BOSS ID
	 *@return
	 */
	public void setBossId(int bossId){
		this.bossId = bossId;
	}

	/**
 	 *get BOSS ID
	 *@return
	 */
	public int getBossId(){
		return this.bossId;
	}

	/**
 	 *set 物品信息
	 *@return
	 */
	public void setItemInfo(ItemInfo itemInfo){
		this.itemInfo = itemInfo;
	}

	/**
 	 *get 物品信息
	 *@return
	 */
	public ItemInfo getItemInfo(){
		return this.itemInfo;
	}

	/**
 	 *set 事件类型：1-普通掉落信息;2-置顶掉落信息;3-BOSS击杀信息
	 *@return
	 */
	public void setType(byte type){
		this.type = type;
	}

	/**
 	 *get 事件类型：1-普通掉落信息;2-置顶掉落信息;3-BOSS击杀信息
	 *@return
	 */
	public byte getType(){
		return this.type;
	}

	/**
 	 *set 事件发生时间
	 *@return
	 */
	public void setTouchTime(long touchTime){
		this.touchTime = touchTime;
	}

	/**
 	 *get 事件发生时间
	 *@return
	 */
	public long getTouchTime(){
		return this.touchTime;
	}
	
	public BossEventBean getBossEventBean(Item item){
		BossEventBean bean = new BossEventBean();
		
		bean.setEventId(this.getEventId());
		bean.setBossId(this.getBossId());
		bean.setBossMapId(this.getBossMapId());
		bean.setPlayerId(this.getPlayerId());
		bean.setPlayerName(this.getPlayerName());
		bean.setTime(this.getTouchTime());
		bean.setType(this.getType());
		bean.setItem(VersionUpdateUtil.dateSave(JSONserializable.toString(new ItemTrans(item))));
		
		return bean;
	}
	
	public void setBossEventInfo(BossEventBean bean){
		this.setEventId(bean.getEventId());
		this.setBossId(bean.getBossId());
		this.setBossMapId(bean.getBossMapId());
		this.setPlayerId(bean.getPlayerId());
		this.setPlayerName(bean.getPlayerName());
		this.setTouchTime(bean.getTime());
		this.setType(bean.getType());
		try {
		ItemTrans itemTrans = (ItemTrans) JSONserializable.toObject(VersionUpdateUtil.dateLoad(bean.getItem()),ItemTrans.class);
			this.setItemInfo(itemTrans.getItemInfo());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		//BOSS地图ID
		writeInt(buf,this.bossMapId);
		//BOSS ID
		writeInt(buf,this.bossId);
		//物品信息
		writeBean(buf,this.itemInfo);
		//是否置顶
		writeByte(buf,this.type);
		//事件发生时间
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
		//BOSS地图ID
		this.bossMapId = readInt(buf);
		//BOSS ID
		this.bossId = readInt(buf);
		//物品信息
		this.itemInfo = (ItemInfo)readBean(buf, ItemInfo.class);
		//是否置顶
		this.type = readByte(buf);
		//事件发生时间
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
		//BOSS地图ID
		buf.append("bossMapId:"+bossMapId+",");
		//BOSS ID
		buf.append("bossId:"+bossId+",");
		//物品信息
		buf.append("itemInfo:"+itemInfo.toString()+",");
		//是否置顶
		buf.append("type:"+type+",");
		//事件发生时间
		buf.append("touchTime:"+touchTime+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}