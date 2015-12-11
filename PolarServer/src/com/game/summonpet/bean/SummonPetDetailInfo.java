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
 * 召唤怪祥细信息类
 */
public class SummonPetDetailInfo extends Bean {

	//召唤怪Id
	private long petId;
	
	//召唤怪模板Id
	private int petModelId;
	
	//召唤怪等级
	private int level;
	
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
	
//	//出战状态,1出战 0不出战
//	private byte showState;
//	
//	//死亡时间 如果出战状态且未死亡则返回0 否则返回秒级时间
//	private int dieTime;
//	
//	//合体次数
//	private int htCount;
//	
//	//今日合体次数
//	private int dayCount;
//	
//	//合体冷确时间
//	private int htCoolDownTime;
	
	//技能列表
	private List<com.game.skill.bean.SkillInfo> skillInfos = new ArrayList<com.game.skill.bean.SkillInfo>();
	//合体加成
//	private List<com.game.player.bean.PlayerAttributeItem> htAddition = new ArrayList<com.game.player.bean.PlayerAttributeItem>();
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//召唤怪Id
		writeLong(buf, this.petId);
		//召唤怪模板Id
		writeInt(buf, this.petModelId);
		//召唤怪等级
		writeInt(buf, this.level);
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

		//技能列表
		writeShort(buf, skillInfos.size());
		for (int i = 0; i < skillInfos.size(); i++) {
			writeBean(buf, skillInfos.get(i));
		}

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
		//召唤怪等级
		this.level = readInt(buf);
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
	
		//技能列表
		int skillInfos_length = readShort(buf);
		for (int i = 0; i < skillInfos_length; i++) {
			skillInfos.add((com.game.skill.bean.SkillInfo)readBean(buf, com.game.skill.bean.SkillInfo.class));
		}

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
	 * get 技能列表
	 * @return 
	 */
	public List<com.game.skill.bean.SkillInfo> getSkillInfos(){
		return skillInfos;
	}
	
	/**
	 * set 技能列表
	 */
	public void setSkillInfos(List<com.game.skill.bean.SkillInfo> skillInfos){
		this.skillInfos = skillInfos;
	}
	
	
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//召唤怪Id
		buf.append("petId:" + petId +",");
		//召唤怪模板Id
		buf.append("petModelId:" + petModelId +",");
		//召唤怪等级
		buf.append("level:" + level +",");
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
		
		//技能列表
		buf.append("skillInfos:{");
		for (int i = 0; i < skillInfos.size(); i++) {
			buf.append(skillInfos.get(i).toString() +",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");

		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}