package com.game.casting.structs;

import com.game.backpack.structs.Item;
import com.game.object.GameObject;

/**
 * 铸造工厂奖励仓库格子数据
 *
 * @author xiaozhuoming  
 */
public class CastingGridData extends GameObject {
	
	private static final long serialVersionUID = 5917491601516097212L;

	//格子编号
	private int grididx;
	
	//物品的信息
	private Item item;

	public int getGrididx() {
		return grididx;
	}

	public void setGrididx(int grididx) {
		this.grididx = grididx;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

}
