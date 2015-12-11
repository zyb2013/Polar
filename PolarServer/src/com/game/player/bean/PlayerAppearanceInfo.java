package com.game.player.bean;


import java.util.ArrayList;
import java.util.List;
import com.game.message.Bean;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 玩家外观展示信息
 */
public class PlayerAppearanceInfo extends Bean {

	//玩家性别
	private byte sex;
	
	//衣服模版ID
	private int clothingmodid;
	
	//武器模版ID
	private int weaponmodid;
	
	//武器强化等级
	private byte weaponStreng;
	
	//坐骑模版ID
	private int horsemodid;
	
	//坐骑模版ID
	private int horseweaponmodid;
	
	//头像模板ID
	private int avatarid;
	
	//弓箭模板ID
	private int arrowid;
	
//	//暗器模版ID
//	private int hiddenweaponmodid;
//	
//	//坐骑锻骨草使用数量
//	private short horseduangu;
	
	//装备列表信息
	private List<com.game.equip.bean.EquipInfo> equips = new ArrayList<com.game.equip.bean.EquipInfo>();
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//玩家性别
		writeByte(buf, this.sex);
		//衣服模版ID
		writeInt(buf, this.clothingmodid);
		//武器模版ID
		writeInt(buf, this.weaponmodid);
		//武器强化等级
		writeByte(buf, this.weaponStreng);
		//坐骑模版ID
		writeInt(buf, this.horsemodid);
		//坐骑模版ID
		writeInt(buf, this.horseweaponmodid);
		//头像模板ID
		writeInt(buf, this.avatarid);
		//弓箭模板ID
		writeInt(buf, this.arrowid);
//		//暗器模版ID
//		writeInt(buf, this.hiddenweaponmodid);
//		//坐骑锻骨草使用数量
//		writeShort(buf, this.horseduangu);
		//装备列表信息
		writeShort(buf, equips.size());
		for (int i = 0; i < equips.size(); i++) {
			writeBean(buf, equips.get(i));
		}
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//玩家性别
		this.sex = readByte(buf);
		//衣服模版ID
		this.clothingmodid = readInt(buf);
		//武器模版ID
		this.weaponmodid = readInt(buf);
		//武器强化等级
		this.weaponStreng = readByte(buf);
		//坐骑模版ID
		this.horsemodid = readInt(buf);
		//坐骑模版ID
		this.horseweaponmodid = readInt(buf);
		//头像模板ID
		this.avatarid = readInt(buf);
		//弓箭模板ID
		this.arrowid = readInt(buf);
//		//暗器模版ID
//		this.hiddenweaponmodid = readInt(buf);
//		//坐骑锻骨草使用数量
//		this.horseduangu = readShort(buf);
		//装备列表信息
		int equips_length = readShort(buf);
		for (int i = 0; i < equips_length; i++) {
			equips.add((com.game.equip.bean.EquipInfo)readBean(buf, com.game.equip.bean.EquipInfo.class));
		}
		return true;
	}
	
	/**
	 * get 玩家性别
	 * @return 
	 */
	public byte getSex(){
		return sex;
	}
	
	/**
	 * set 玩家性别
	 */
	public void setSex(byte sex){
		this.sex = sex;
	}
	
	/**
	 * get 衣服模版ID
	 * @return 
	 */
	public int getClothingmodid(){
		return clothingmodid;
	}
	
	/**
	 * set 衣服模版ID
	 */
	public void setClothingmodid(int clothingmodid){
		this.clothingmodid = clothingmodid;
	}
	
	/**
	 * get 武器模版ID
	 * @return 
	 */
	public int getWeaponmodid(){
		return weaponmodid;
	}
	
	/**
	 * set 武器模版ID
	 */
	public void setWeaponmodid(int weaponmodid){
		this.weaponmodid = weaponmodid;
	}
	
	/**
	 * get 武器强化等级
	 * @return 
	 */
	public byte getWeaponStreng(){
		return weaponStreng;
	}
	
	/**
	 * set 武器强化等级
	 */
	public void setWeaponStreng(byte weaponStreng){
		this.weaponStreng = weaponStreng;
	}
	
	/**
	 * get 坐骑模版ID
	 * @return 
	 */
	public int getHorsemodid(){
		return horsemodid;
	}
	
	/**
	 * set 坐骑模版ID
	 */
	public void setHorsemodid(int horsemodid){
		this.horsemodid = horsemodid;
	}
	
	/**
	 * get 坐骑模版ID
	 * @return 
	 */
	public int getHorseweaponmodid(){
		return horseweaponmodid;
	}
	
	/**
	 * set 坐骑模版ID
	 */
	public void setHorseweaponmodid(int horseweaponmodid){
		this.horseweaponmodid = horseweaponmodid;
	}
	
	/**
	 * get 头像模板ID
	 * @return 
	 */
	public int getAvatarid(){
		return avatarid;
	}
	
	/**
	 * set 头像模板ID
	 */
	public void setAvatarid(int avatarid){
		this.avatarid = avatarid;
	}
	
	/**
	 * get 弓箭模板ID
	 * @return 
	 */
	public int getArrowid(){
		return arrowid;
	}
	
	/**
	 * set 弓箭模板ID
	 */
	public void setArrowid(int arrowid){
		this.arrowid = arrowid;
	}
	
//	/**
//	 * get 暗器模版ID
//	 * @return 
//	 */
//	public int getHiddenweaponmodid(){
//		return hiddenweaponmodid;
//	}
//	
//	/**
//	 * set 暗器模版ID
//	 */
//	public void setHiddenweaponmodid(int hiddenweaponmodid){
//		this.hiddenweaponmodid = hiddenweaponmodid;
//	}
//	
//	/**
//	 * get 坐骑锻骨草使用数量
//	 * @return 
//	 */
//	public short getHorseduangu(){
//		return horseduangu;
//	}
//	
//	/**
//	 * set 坐骑锻骨草使用数量
//	 */
//	public void setHorseduangu(short horseduangu){
//		this.horseduangu = horseduangu;
//	}
	
	/**
	 * get 装备列表信息
	 * @return 
	 */
	public List<com.game.equip.bean.EquipInfo> getEquips(){
		return equips;
	}
	
	/**
	 * set 装备列表信息
	 */
	public void setEquips(List<com.game.equip.bean.EquipInfo> equips){
		this.equips = equips;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//玩家性别
		buf.append("sex:" + sex +",");
		//衣服模版ID
		buf.append("clothingmodid:" + clothingmodid +",");
		//武器模版ID
		buf.append("weaponmodid:" + weaponmodid +",");
		//武器强化等级
		buf.append("weaponStreng:" + weaponStreng +",");
		//坐骑模版ID
		buf.append("horsemodid:" + horsemodid +",");
		//坐骑模版ID
		buf.append("horseweaponmodid:" + horseweaponmodid +",");
		//头像模板ID
		buf.append("avatarid:" + avatarid +",");
		//弓箭模板ID
		buf.append("arrowid:" + arrowid +",");
//		//暗器模版ID
//		buf.append("hiddenweaponmodid:" + hiddenweaponmodid +",");
//		//坐骑锻骨草使用数量
//		buf.append("horseduangu:" + horseduangu +",");
		//装备列表信息
		buf.append("equips:{");
		for (int i = 0; i < equips.size(); i++) {
			buf.append(equips.get(i).toString() +",");
		}
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("},");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}