package com.game.country.structs;

import com.game.object.GameObject;
import com.game.spirittree.structs.FruitReward;
import java.util.ArrayList;
import java.util.List;

/**
 * 圣盟宝箱
 *
 * @author  
 */
public class KingCityChest extends GameObject{

	private static final long serialVersionUID = 5918264124631597809L;
	//圣盟宝箱奖励表
	private List<FruitReward> kingcitychestlist = new ArrayList<FruitReward>();
	//圣盟宝箱选择3次（位置）
	private List<Integer> kingcitychestidx = new ArrayList<Integer>();

	public List<Integer> getKingcitychestidx() {
		return kingcitychestidx;
	}

	public void setKingcitychestidx(List<Integer> kingcitychestidx) {
		this.kingcitychestidx = kingcitychestidx;
	}

	public List<FruitReward> getKingcitychestlist() {
		return kingcitychestlist;
	}

	public void setKingcitychestlist(List<FruitReward> kingcitychestlist) {
		this.kingcitychestlist = kingcitychestlist;
	}
}
