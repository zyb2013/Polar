package com.game.goldraffle.structs;

import com.game.backpack.structs.Item;
import com.game.object.GameObject;

/**
 * 钻石抽奖宝箱格子数据
 *
 * @author xiaozhuoming  
 */
public class GoldRaffleGridData extends GameObject implements Comparable<GoldRaffleGridData> {

	private static final long serialVersionUID = 799212350070114731L;
	
//	//格子编号
//	private int grididx;
	
	//物品珍贵度
	private int grade;
	
	//物品的信息
	private Item item;

//	public int getGrididx() {
//		return grididx;
//	}
//
//	public void setGrididx(int grididx) {
//		this.grididx = grididx;
//	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public int compareTo(GoldRaffleGridData goldRaffleGridData) {
		return this.getItem().compareTo(goldRaffleGridData.getItem());
	}

}
