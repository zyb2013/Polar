package com.game.attribute;

import java.util.HashMap;
import java.util.Map;

import com.game.backpack.structs.Attribute;
import com.game.backpack.structs.Equip;
import com.game.data.bean.Q_globalBean;
import com.game.manager.ManagerPool;
import com.game.utils.Symbol;

/**
 * @author luminghua.ko@gmail.com
 *
 * @date   2014年3月18日 上午11:36:51
 */
public class ActivateAttributeManager {
	
	private static ActivateAttributeManager instance;
	private static final Object  o = new Object();
	private Map<Integer,Integer> type2map;
	
	public ActivateAttributeManager() {
		
	}
	
	public static ActivateAttributeManager getInstance() {
		if(instance == null) {
			synchronized (o) {
				if(instance == null) {
					instance = new ActivateAttributeManager();
				}
			}
		}
		return instance;
	}
	
	public void init() {
		Q_globalBean globalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(213);
		String[] split = globalBean.getQ_string_value().split(Symbol.DOUHAO_REG);
		Map<Integer,Integer> type2map = new HashMap<Integer,Integer>();
		for(String str:split) {
			String[] split2 = str.split("_");
			type2map.put(Integer.parseInt(split2[0]), Integer.parseInt(split2[1]));
		}
		this.type2map = type2map;
	}
	
	
	
	public int checkEquip(Equip equip) {
		int count = 0;
		for(Attribute att:equip.getAttributes()) {
			if(type2map.containsKey(att.getType())) {
				count++;
			}
		}
		return count;
	}
	
	public int calAttributePower(Equip equip) {
		int power = 0;
		for(Attribute att:equip.getAttributes()) {
			Integer integer = type2map.get(att.getType());
			if(integer != null) {
				power += integer;
			}
		}
		return power;
	}
	
	
}
