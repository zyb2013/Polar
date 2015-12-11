package com.game.challenge.struct;

import com.game.backpack.bean.ItemInfo;
import com.game.backpack.structs.Equip;
import com.game.backpack.structs.Item;
import com.game.object.GameObject;
import com.game.player.structs.Player;

/**
 * @author xuliang
 * 
 * @version 1.0.0
 * 
 * @since 2014-03-25
 * 
 * 物品信息转换类
 * 将Item或Equip转换成Json字符串存入数据库
 */

public class ItemTrans extends GameObject {

	private static final long serialVersionUID = 935641347303684282L;
	
	//! 1：普通物品；2：装备
	byte type;
	
	//! 装备
	Equip equip = null;
	
	//! 普通道具
	Item item = null;
	public ItemTrans(){
		
	}
	
	public ItemTrans(Item item){
		if (item instanceof Equip){
			this.equip = (Equip)item;
			type = 2;
		}else{
			this.item = item;
			type = 1;
		}
	}
	
	public byte getType(){
		return this.type;
	}
	
	public void setType(byte type){
		this.type = type;
	}
	
	public Equip getEquip(){
		return this.equip;
	}
	
	public void setEquip(Equip equip){
		this.equip = equip;
	}
	
	public Item getItem(){
		return this.item;
	}
	
	public void setItem(Item item){
		this.item = item;
	}
	
	public ItemInfo getItemInfo(){
		if(type == 1){
			return item.buildItemInfo();
		}else{
			return equip.buildItemInfo();
		}
	}
}
