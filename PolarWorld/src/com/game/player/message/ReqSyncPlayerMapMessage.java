package com.game.player.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 同步世界服务器玩家地图消息
 */
public class ReqSyncPlayerMapMessage extends Message{

	//角色id
	private long playerId;
	
	//角色所在线
	private int line;
	
	//角色所在地图
	private int mapId;
	
	//角色所在地图唯一ID
	private long maponlyId;
	
	//角色所在X
	private short x;
	
	//角色所在Y
	private short y;
	
	//玩家名字
	private String name;

	//角色等级
	private int level;

	//职业 1:剑士,2:魔法师,3:弓箭手
	private byte job;
	

	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//角色id
		writeLong(buf, this.playerId);
		//角色所在线
		writeInt(buf, this.line);
		//角色所在地图
		writeInt(buf, this.mapId);
		//角色所在地图唯一ID
		writeLong(buf, this.maponlyId);
		//角色所在X
		writeShort(buf, this.x);
		//角色所在Y
		writeShort(buf, this.y);
		//玩家名字
		writeString(buf, this.getName());
		//角色等级
		writeInt(buf, this.level);	
		//职业 1:剑士,2:魔法师,3:弓箭手
		writeByte(buf, this.job);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//角色id
		this.playerId = readLong(buf);
		//角色所在线
		this.line = readInt(buf);
		//角色所在地图
		this.mapId = readInt(buf);
		//角色所在地图唯一ID
		this.maponlyId = readLong(buf);
		//角色所在X
		this.x = readShort(buf);
		//角色所在Y
		this.y = readShort(buf);
		//角色名字
		this.name = readString(buf);
		//角色等级
		this.level = readInt(buf);
		//职业 1:剑士,2:魔法师,3:弓箭手
		this.job = readByte(buf);
		return true;
	}
	
	/**
	 * get 角色id
	 * @return 
	 */
	public long getPlayerId(){
		return playerId;
	}
	
	/**
	 * set 角色id
	 */
	public void setPlayerId(long playerId){
		this.playerId = playerId;
	}
	
	/**
	 * get 角色所在线
	 * @return 
	 */
	public int getLine(){
		return line;
	}
	
	/**
	 * set 角色所在线
	 */
	public void setLine(int line){
		this.line = line;
	}
	
	/**
	 * get 角色所在地图
	 * @return 
	 */
	public int getMapId(){
		return mapId;
	}
	
	/**
	 * set 角色所在地图
	 */
	public void setMapId(int mapId){
		this.mapId = mapId;
	}
	
	/**
	 * get 角色所在地图唯一ID
	 * @return 
	 */
	public long getMaponlyId(){
		return maponlyId;
	}
	
	/**
	 * set 角色所在地图唯一ID
	 */
	public void setMaponlyId(long maponlyId){
		this.maponlyId = maponlyId;
	}
	
	/**
	 * get 角色所在X
	 * @return 
	 */
	public short getX(){
		return x;
	}
	
	/**
	 * set 角色所在X
	 */
	public void setX(short x){
		this.x = x;
	}
	
	/**
	 * get 角色所在Y
	 * @return 
	 */
	public short getY(){
		return y;
	}
	
	/**
	 * set 角色所在Y
	 */
	public void setY(short y){
		this.y = y;
	}
	
	/**
	 * get 玩家名字
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * set 玩家名字
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * get 角色等级
	 * @return 
	 */
	public int getLevel(){
		return level;
	}
	
	/**
	 * set 角色等级
	 */
	public void setLevel(int level){
		this.level = level;
	}
	
	/**
	 * get 职业 1:剑士,2:魔法师,3:弓箭手
	 * @return
	 */
	public byte getJob() {
		return job;
	}

	/**
	 * set 职业 1:剑士,2:魔法师,3:弓箭手
	 */
	public void setJob(byte job) {
		this.job = job;
	}

	
	@Override
	public int getId() {
		return 103304;
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
		//角色id
		buf.append("playerId:" + playerId +",");
		//角色所在线
		buf.append("line:" + line +",");
		//角色所在地图
		buf.append("mapId:" + mapId +",");
		//角色所在地图唯一ID
		buf.append("maponlyId:" + maponlyId +",");
		//角色所在X
		buf.append("x:" + x +",");
		//角色所在Y
		buf.append("y:" + y +",");
		//角色名字
		buf.append("name:" + name +",");
		//角色等级
		buf.append("level:" + level +",");
		//职业 1:剑士,2:魔法师,3:弓箭手
		buf.append("job:" + job +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}