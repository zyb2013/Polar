package com.game.casting.bean;

import org.apache.mina.core.buffer.IoBuffer;

import com.game.message.Bean;

/** 
 * @author Commuication Auto Maker
 * 
 * @version 1.0.0
 * 
 * 铸造工厂奖励仓库格子信息
 */
public class CastingGridInfo extends Bean {

	//格子编号
	private int grididx;
	
	//当前格子中的物品信息
	private com.game.backpack.bean.ItemInfo iteminfo;
	
	/**
	 * 写入字节缓存
	 */
	public boolean write(IoBuffer buf){
		//格子编号
		writeInt(buf, this.grididx);
		//当前格子中的物品信息
		writeBean(buf, this.iteminfo);
		return true;
	}
	
	/**
	 * 读取字节缓存
	 */
	public boolean read(IoBuffer buf){
		//格子编号
		this.grididx = readInt(buf);
		//当前格子中的物品信息
		this.iteminfo = (com.game.backpack.bean.ItemInfo)readBean(buf, com.game.backpack.bean.ItemInfo.class);
		return true;
	}
	
	/**
	 * get 格子编号
	 * @return 
	 */
	public int getGrididx(){
		return grididx;
	}
	
	/**
	 * set 格子编号
	 */
	public void setGrididx(int grididx){
		this.grididx = grididx;
	}
	
	/**
	 * get 当前格子中的物品信息
	 * @return 
	 */
	public com.game.backpack.bean.ItemInfo getIteminfo(){
		return iteminfo;
	}
	
	/**
	 * set 当前格子中的物品信息
	 */
	public void setIteminfo(com.game.backpack.bean.ItemInfo iteminfo){
		this.iteminfo = iteminfo;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer("[");
		//格子编号
		buf.append("grididx:" + grididx +",");
		//当前格子中的物品信息
		if(this.iteminfo!=null) buf.append("iteminfo:" + iteminfo.toString() +",");
		if(buf.charAt(buf.length()-1)==',') buf.deleteCharAt(buf.length()-1);
		buf.append("]");
		return buf.toString();
	}
}