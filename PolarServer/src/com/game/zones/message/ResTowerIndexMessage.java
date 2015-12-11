package com.game.zones.message;
import org.apache.mina.core.buffer.IoBuffer;
import com.game.message.Message;
/**
  * 返回当前爬塔位置信息
 */
public class ResTowerIndexMessage extends Message{

	//副本ID
	private int zoneId;
	//当前层
	private int level;
	//当前关卡(门的关卡默认是0)
	private int stage;
	//最佳通关者(角色全名)
	private String bestName;
	//最佳时间(秒)
	private int bestTime;
	//最近通关者
	private String latelyName;
	//最近通关者职业
	private byte latelyJob;
	//最近通关者战斗力
	private int latelyFightPower;
	/**
 	 *set 副本ID
	 *@return
	 */
	public void setZoneId(int zoneId){
		this.zoneId = zoneId;
	}

	/**
 	 *get 副本ID
	 *@return
	 */
	public int getZoneId(){
		return this.zoneId;
	}

	/**
 	 *set 当前层
	 *@return
	 */
	public void setLevel(int level){
		this.level = level;
	}

	/**
 	 *get 当前层
	 *@return
	 */
	public int getLevel(){
		return this.level;
	}

	/**
 	 *set 当前关卡(门的关卡默认是0)
	 *@return
	 */
	public void setStage(int stage){
		this.stage = stage;
	}

	/**
 	 *get 当前关卡(门的关卡默认是0)
	 *@return
	 */
	public int getStage(){
		return this.stage;
	}

	/**
 	 *set 最佳通关者(角色全名)
	 *@return
	 */
	public void setBestName(String bestName){
		this.bestName = bestName;
	}

	/**
 	 *get 最佳通关者(角色全名)
	 *@return
	 */
	public String getBestName(){
		return this.bestName;
	}

	/**
 	 *set 最佳时间(秒)
	 *@return
	 */
	public void setBestTime(int bestTime){
		this.bestTime = bestTime;
	}

	/**
 	 *get 最佳时间(秒)
	 *@return
	 */
	public int getBestTime(){
		return this.bestTime;
	}

	/**
 	 *set 最近通关者
	 *@return
	 */
	public void setLatelyName(String latelyName){
		this.latelyName = latelyName;
	}

	/**
 	 *get 最近通关者
	 *@return
	 */
	public String getLatelyName(){
		return this.latelyName;
	}

	/**
 	 *set 最近通关者职业
	 *@return
	 */
	public void setLatelyJob(byte latelyJob){
		this.latelyJob = latelyJob;
	}

	/**
 	 *get 最近通关者职业
	 *@return
	 */
	public byte getLatelyJob(){
		return this.latelyJob;
	}

	/**
 	 *set 最近通关者战斗力
	 *@return
	 */
	public void setLatelyFightPower(int latelyFightPower){
		this.latelyFightPower = latelyFightPower;
	}

	/**
 	 *get 最近通关者战斗力
	 *@return
	 */
	public int getLatelyFightPower(){
		return this.latelyFightPower;
	}

	/**
 	 *写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//副本ID
		writeInt(buf,this.zoneId);
		//当前层
		writeInt(buf,this.level);
		//当前关卡(门的关卡默认是0)
		writeInt(buf,this.stage);
		//最佳通关者(角色全名)
		writeString(buf,this.bestName);
		//最佳时间(秒)
		writeInt(buf,this.bestTime);
		//最近通关者
		writeString(buf,this.latelyName);
		//最近通关者职业
		writeByte(buf,this.latelyJob);
		//最近通关者战斗力
		writeInt(buf,this.latelyFightPower);
		return true;
	}

	@Override
	public int getId() {
		return 600001;
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
		//副本ID
		this.zoneId = readInt(buf);
		//当前层
		this.level = readInt(buf);
		//当前关卡(门的关卡默认是0)
		this.stage = readInt(buf);
		//最佳通关者(角色全名)
		this.bestName = readString(buf);
		//最佳时间(秒)
		this.bestTime = readInt(buf);
		//最近通关者
		this.latelyName = readString(buf);
		//最近通关者职业
		this.latelyJob = readByte(buf);
		//最近通关者战斗力
		this.latelyFightPower = readInt(buf);
		return true;
	}
 
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//副本ID
		buf.append("zoneId:"+zoneId+",");
		//当前层
		buf.append("level:"+level+",");
		//当前关卡(门的关卡默认是0)
		buf.append("stage:"+stage+",");
		//最佳通关者(角色全名)
		buf.append("bestName:"+bestName+",");
		//最佳时间(秒)
		buf.append("bestTime:"+bestTime+",");
		//最近通关者
		buf.append("latelyName:"+latelyName+",");
		//最近通关者职业
		buf.append("latelyJob:"+latelyJob+",");
		//最近通关者战斗力
		buf.append("latelyFightPower:"+latelyFightPower+",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}