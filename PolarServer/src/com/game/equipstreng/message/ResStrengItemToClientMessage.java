package com.game.equipstreng.message;

import com.game.message.Message;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * @author 周江华
 * 
 * @version 1.0.0
 * 
 * 发送强化结果消息
 */
public class ResStrengItemToClientMessage extends Message{

	//装备详细信息
	private com.game.equip.bean.EquipInfo equipInfo;
	
	//是否成功：0失败，1成功
	private byte issuccess;
	
	//当前的强化等级
	private byte itemlevel;
	
	//物品追加等级
	private byte addAttributLevel;
	
	
	
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//装备详细信息
		writeBean(buf, this.equipInfo);
		//是否成功：0失败，1成功
		writeByte(buf, this.issuccess);
		//当前的强化等级
		writeByte(buf, this.itemlevel);
		
		writeByte(buf, this.addAttributLevel);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//装备详细信息
		this.equipInfo = (com.game.equip.bean.EquipInfo)readBean(buf, com.game.equip.bean.EquipInfo.class);
		//是否成功：0失败，1成功
		this.issuccess = readByte(buf);
		//当前的强化等级
		this.itemlevel = readByte(buf);
		
		this.addAttributLevel=readByte(buf);
		return true;
	}
	
	public byte getAddAttributLevel() {
		return addAttributLevel;
	}

	public void setAddAttributLevel(byte addAttributLevel) {
		this.addAttributLevel = addAttributLevel;
	}

	/**
	 * get 装备详细信息
	 * @return 
	 */
	public com.game.equip.bean.EquipInfo getEquipInfo(){
		return equipInfo;
	}
	
	/**
	 * set 装备详细信息
	 */
	public void setEquipInfo(com.game.equip.bean.EquipInfo equipInfo){
		this.equipInfo = equipInfo;
	}
	
	/**
	 * get 是否成功：0失败，1成功
	 * @return 
	 */
	public byte getIssuccess(){
		return issuccess;
	}
	
	/**
	 * set 是否成功：0失败，1成功
	 */
	public void setIssuccess(byte issuccess){
		this.issuccess = issuccess;
	}
	
	/**
	 * get 当前的强化等级
	 * @return 
	 */
	public byte getItemlevel(){
		return itemlevel;
	}
	
	/**
	 * set 当前的强化等级
	 */
	public void setItemlevel(byte itemlevel){
		this.itemlevel = itemlevel;
	}
	
	
	@Override
	public int getId() {
		return 130101;
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
		//装备详细信息
		if(this.equipInfo!=null) buf.append("equipInfo:" + equipInfo.toString() +",");
		//是否成功：0失败，1成功
		buf.append("issuccess:" + issuccess +",");
		//当前的强化等级
		buf.append("itemlevel:" + itemlevel +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}