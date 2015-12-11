package com.game.backpack.structs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.game.data.bean.Q_itemBean;
import com.game.data.manager.DataManager;
import com.game.languageres.manager.ResManager;

public class Gift extends Item {

	/**
	 *
	 */
	private static final long serialVersionUID = -8680091763726841301L;
	private List<Item> ownItems = new ArrayList<Item>();	//礼包所有的物品列表
	private int money;					//金币 -1
	private int gold;					//钻石 -2
	private int zhenqi;					//真气 -3
	private int exp;					//经验 -4
	private int bindgold;					//绑钻 -5
	private int fightspirit;				//战魂 -6
	private int rank;					//军功 -7

	public List<Item> getOwnItems() {
		return ownItems;
	}

	public void setOwnItems(List<Item> ownItems) {
		this.ownItems = ownItems;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getZhenqi() {
		return zhenqi;
	}

	public void setZhenqi(int zhenqi) {
		this.zhenqi = zhenqi;
	}

	public int getBindgold() {
		return bindgold;
	}

	public void setBindgold(int bindgold) {
		this.bindgold = bindgold;
	}

	public int getFightspirit() {
		return fightspirit;
	}

	public void setFightspirit(int fightspirit) {
		this.fightspirit = fightspirit;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}


	public void pushItemStringMap(HashMap<Integer, Integer> itemstrMap, int itemid, int itemnum) {
		if (itemstrMap != null) {
			if (itemstrMap.containsKey(itemid)) {
				int oldvalue = itemstrMap.get(itemid);
				oldvalue = oldvalue + itemnum;
				itemstrMap.put(itemid, oldvalue);
			} else {
				itemstrMap.put(itemid, itemnum);
			}
		}
	}

	public String getItemString(HashMap<Integer, Integer> itemstrMap) {
		String itemString = "";
		if (itemstrMap != null) {
			Iterator<Entry<Integer, Integer>> iterator = itemstrMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Integer, Integer> entry = iterator.next();
				int itemid = entry.getKey();
				int itemnum = entry.getValue();
				Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(itemid);
				if (q_itemBean != null) {
					if (itemString.equalsIgnoreCase("")) {
						itemString = String.format("%s*%d", q_itemBean.getQ_name(), itemnum);
					} else {
						if (itemString.length() < 64) {
							itemString = itemString + String.format(",%s*%d", q_itemBean.getQ_name(), itemnum);
							if (itemString.length() >= 64) {
								itemString = itemString + ResManager.getInstance().getString("等物品");
							}
						}
					}
				}
			}
		}
		return itemString;
	}

	public boolean isEmpty() {
		if (getOwnItems().isEmpty() && getGold() == 0 && getMoney() == 0 && getExp() == 0 && getZhenqi() == 0 && getBindgold() == 0 && getFightspirit() == 0 && getRank() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public void clear() {
		getOwnItems().clear();
		setGold(0);
		setMoney(0);
		setExp(0);
		setZhenqi(0);
		setBindgold(0);
		setFightspirit(0);
		setRank(0);
	}
}
