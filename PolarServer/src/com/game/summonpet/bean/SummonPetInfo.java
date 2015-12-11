package com.game.summonpet.bean;

import java.util.List;
import java.util.ArrayList;

import com.game.message.Bean;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 召唤怪信息
 */
public class SummonPetInfo extends Bean {

	//召唤怪Id
	private long petId;
	
	//召唤怪模板Id
	private int petModelId;
	
	//所有者ID 
	private long ownerId;
	
	//所有者名称
	private String ownerName;
	
	//召唤怪等级
	private int level;
	
	//召唤怪所在地图
	private int mapId;
	
	//召唤怪所在X
	private short x;
	
	//召唤怪所在Y
	private short y;
	
	//召唤怪HP
	private int hp;
	
	//召唤怪最大HP
	private int maxHp;
	
	//召唤怪MP
	private int mp;
	
	//召唤怪最大MP
	private int maxMp;
	
	//召唤怪SP
	private int sp;
	
	//召唤怪最大SP
	private int maxSp;
	
	//召唤怪速度
	private int speed;
	
	//召唤怪面对方向
	private byte dir;
	
	//跑步坐标集合
	private List<Byte> positions = new ArrayList<Byte>();
	//buff集合
	private List<com.game.buff.bean.BuffInfo> buffs = new ArrayList<com.game.buff.bean.BuffInfo>();
	//双修状态 1与玩家双修 2玩家与玩家双修并出战召唤怪
	private byte sxstate;
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//召唤怪Id
		writeLong(buf, this.petId);
		//召唤怪模板Id
		writeInt(buf, this.petModelId);
		//所有者ID 
		writeLong(buf, this.ownerId);
		//所有者名称
		writeString(buf, this.ownerName);
		//召唤怪等级
		writeInt(buf, this.level);
		//召唤怪所在地图
		writeInt(buf, this.mapId);
		//召唤怪所在X
		writeShort(buf, this.x);
		//召唤怪所在Y
		writeShort(buf, this.y);
		//召唤怪HP
		writeInt(buf, this.hp);
		//召唤怪最大HP
		writeInt(buf, this.maxHp);
		//召唤怪MP
		writeInt(buf, this.mp);
		//召唤怪最大MP
		writeInt(buf, this.maxMp);
		//召唤怪SP
		writeInt(buf, this.sp);
		//召唤怪最大SP
		writeInt(buf, this.maxSp);
		//召唤怪速度
		writeInt(buf, this.speed);
		//召唤怪面对方向
		writeByte(buf, this.dir);
		//跑步坐标集合
		writeShort(buf, positions.size());
		for (int i = 0; i < positions.size(); i++) {
			writeByte(buf, positions.get(i));
		}
		//buff集合
		writeShort(buf, buffs.size());
		for (int i = 0; i < buffs.size(); i++) {
			writeBean(buf, buffs.get(i));
		}
		//双修状态 1与玩家双修 2玩家与玩家双修并出战召唤怪
		writeByte(buf, this.sxstate);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//召唤怪Id
		this.petId = readLong(buf);
		//召唤怪模板Id
		this.petModelId = readInt(buf);
		//所有者ID 
		this.ownerId = readLong(buf);
		//所有者名称
		this.ownerName = readString(buf);
		//召唤怪等级
		this.level = readInt(buf);
		//召唤怪所在地图
		this.mapId = readInt(buf);
		//召唤怪所在X
		this.x = readShort(buf);
		//召唤怪所在Y
		this.y = readShort(buf);
		//召唤怪HP
		this.hp = readInt(buf);
		//召唤怪最大HP
		this.maxHp = readInt(buf);
		//召唤怪MP
		this.mp = readInt(buf);
		//召唤怪最大MP
		this.maxMp = readInt(buf);
		//召唤怪SP
		this.sp = readInt(buf);
		//召唤怪最大SP
		this.maxSp = readInt(buf);
		//召唤怪速度
		this.speed = readInt(buf);
		//召唤怪面对方向
		this.dir = readByte(buf);
		//跑步坐标集合
		int positions_length = readShort(buf);
		for (int i = 0; i < positions_length; i++) {
			positions.add(readByte(buf));
		}
		//buff集合
		int buffs_length = readShort(buf);
		for (int i = 0; i < buffs_length; i++) {
			buffs.add((com.game.buff.bean.BuffInfo)readBean(buf, com.game.buff.bean.BuffInfo.class));
		}
		//双修状态 1与玩家双修 2玩家与玩家双修并出战召唤怪
		this.sxstate = readByte(buf);
		return true;
	}
	
	/**
	 * get 召唤怪Id
	 * @return 
	 */
	public long getPetId(){
		return petId;
	}
	
	/**
	 * set 召唤怪Id
	 */
	public void setPetId(long petId){
		this.petId = petId;
	}
	
	/**
	 * get 召唤怪模板Id
	 * @return 
	 */
	public int getPetModelId(){
		return petModelId;
	}
	
	/**
	 * set 召唤怪模板Id
	 */
	public void setPetModelId(int petModelId){
		this.petModelId = petModelId;
	}
	
	/**
	 * get 所有者ID 
	 * @return 
	 */
	public long getOwnerId(){
		return ownerId;
	}
	
	/**
	 * set 所有者ID 
	 */
	public void setOwnerId(long ownerId){
		this.ownerId = ownerId;
	}
	
	/**
	 * get 所有者名称
	 * @return 
	 */
	public String getOwnerName(){
		return ownerName;
	}
	
	/**
	 * set 所有者名称
	 */
	public void setOwnerName(String ownerName){
		this.ownerName = ownerName;
	}
	
	/**
	 * get 召唤怪等级
	 * @return 
	 */
	public int getLevel(){
		return level;
	}
	
	/**
	 * set 召唤怪等级
	 */
	public void setLevel(int level){
		this.level = level;
	}
	
	/**
	 * get 召唤怪所在地图
	 * @return 
	 */
	public int getMapId(){
		return mapId;
	}
	
	/**
	 * set 召唤怪所在地图
	 */
	public void setMapId(int mapId){
		this.mapId = mapId;
	}
	
	/**
	 * get 召唤怪所在X
	 * @return 
	 */
	public short getX(){
		return x;
	}
	
	/**
	 * set 召唤怪所在X
	 */
	public void setX(short x){
		this.x = x;
	}
	
	/**
	 * get 召唤怪所在Y
	 * @return 
	 */
	public short getY(){
		return y;
	}
	
	/**
	 * set 召唤怪所在Y
	 */
	public void setY(short y){
		this.y = y;
	}
	
	/**
	 * get 召唤怪HP
	 * @return 
	 */
	public int getHp(){
		return hp;
	}
	
	/**
	 * set 召唤怪HP
	 */
	public void setHp(int hp){
		this.hp = hp;
	}
	
	/**
	 * get 召唤怪最大HP
	 * @return 
	 */
	public int getMaxHp(){
		return maxHp;
	}
	
	/**
	 * set 召唤怪最大HP
	 */
	public void setMaxHp(int maxHp){
		this.maxHp = maxHp;
	}
	
	/**
	 * get 召唤怪MP
	 * @return 
	 */
	public int getMp(){
		return mp;
	}
	
	/**
	 * set 召唤怪MP
	 */
	public void setMp(int mp){
		this.mp = mp;
	}
	
	/**
	 * get 召唤怪最大MP
	 * @return 
	 */
	public int getMaxMp(){
		return maxMp;
	}
	
	/**
	 * set 召唤怪最大MP
	 */
	public void setMaxMp(int maxMp){
		this.maxMp = maxMp;
	}
	
	/**
	 * get 召唤怪SP
	 * @return 
	 */
	public int getSp(){
		return sp;
	}
	
	/**
	 * set 召唤怪SP
	 */
	public void setSp(int sp){
		this.sp = sp;
	}
	
	/**
	 * get 召唤怪最大SP
	 * @return 
	 */
	public int getMaxSp(){
		return maxSp;
	}
	
	/**
	 * set 召唤怪最大SP
	 */
	public void setMaxSp(int maxSp){
		this.maxSp = maxSp;
	}
	
	/**
	 * get 召唤怪速度
	 * @return 
	 */
	public int getSpeed(){
		return speed;
	}
	
	/**
	 * set 召唤怪速度
	 */
	public void setSpeed(int speed){
		this.speed = speed;
	}
	
	/**
	 * get 召唤怪面对方向
	 * @return 
	 */
	public byte getDir(){
		return dir;
	}
	
	/**
	 * set 召唤怪面对方向
	 */
	public void setDir(byte dir){
		this.dir = dir;
	}
	
	/**
	 * get 跑步坐标集合
	 * @return 
	 */
	public List<Byte> getPositions(){
		return positions;
	}
	
	/**
	 * set 跑步坐标集合
	 */
	public void setPositions(List<Byte> positions){
		this.positions = positions;
	}
	
	/**
	 * get buff集合
	 * @return 
	 */
	public List<com.game.buff.bean.BuffInfo> getBuffs(){
		return buffs;
	}
	
	/**
	 * set buff集合
	 */
	public void setBuffs(List<com.game.buff.bean.BuffInfo> buffs){
		this.buffs = buffs;
	}
	
	/**
	 * get 双修状态 1与玩家双修 2玩家与玩家双修并出战召唤怪
	 * @return 
	 */
	public byte getSxstate(){
		return sxstate;
	}
	
	/**
	 * set 双修状态 1与玩家双修 2玩家与玩家双修并出战召唤怪
	 */
	public void setSxstate(byte sxstate){
		this.sxstate = sxstate;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//召唤怪Id
		buf.append("petId:" + petId +",");
		//召唤怪模板Id
		buf.append("petModelId:" + petModelId +",");
		//所有者ID 
		buf.append("ownerId:" + ownerId +",");
		//所有者名称
		if(this.ownerName!=null) buf.append("ownerName:" + ownerName.toString() +",");
		//召唤怪等级
		buf.append("level:" + level +",");
		//召唤怪所在地图
		buf.append("mapId:" + mapId +",");
		//召唤怪所在X
		buf.append("x:" + x +",");
		//召唤怪所在Y
		buf.append("y:" + y +",");
		//召唤怪HP
		buf.append("hp:" + hp +",");
		//召唤怪最大HP
		buf.append("maxHp:" + maxHp +",");
		//召唤怪MP
		buf.append("mp:" + mp +",");
		//召唤怪最大MP
		buf.append("maxMp:" + maxMp +",");
		//召唤怪SP
		buf.append("sp:" + sp +",");
		//召唤怪最大SP
		buf.append("maxSp:" + maxSp +",");
		//召唤怪速度
		buf.append("speed:" + speed +",");
		//召唤怪面对方向
		buf.append("dir:" + dir +",");
		//跑步坐标集合
		buf.append("positions:{");
		for (int i = 0; i < positions.size(); i++) {
			buf.append(positions.get(i) +",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		//buff集合
		buf.append("buffs:{");
		for (int i = 0; i < buffs.size(); i++) {
			buf.append(buffs.get(i).toString() +",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		//双修状态 1与玩家双修 2玩家与玩家双修并出战召唤怪
		buf.append("sxstate:" + sxstate +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}