package com.game.goldraffle.structs;

import java.util.ArrayList;
import java.util.List;

import com.game.object.GameObject;

/**
 * 钻石抽奖宝箱数据
 *
 * @author xiaozhuoming 
 */
public class GoldRaffleBoxData extends GameObject{
	
	private static final long serialVersionUID = 7164222563413472118L;
	
	//开启次数
	private int opennum;
	
	//宝箱的物品
	private List<GoldRaffleGridData> goldRaffleGridDataList = new ArrayList<GoldRaffleGridData>();
	
	public int getOpennum() {
		return opennum;
	}

	public void setOpennum(int opennum) {
		this.opennum = opennum;
	}

	public List<GoldRaffleGridData> getGoldRaffleGridDataList() {
		return goldRaffleGridDataList;
	}

	public void setGoldRaffleGridDataList(List<GoldRaffleGridData> goldRaffleGridDataList) {
		this.goldRaffleGridDataList = goldRaffleGridDataList;
	}

}
