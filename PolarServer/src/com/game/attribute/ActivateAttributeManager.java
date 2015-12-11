package com.game.attribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.attribute.message.ResAttributeChangeMessage;
import com.game.backpack.structs.Attribute;
import com.game.backpack.structs.Equip;
import com.game.data.bean.Q_activation_attributeBean;
import com.game.data.bean.Q_globalBean;
import com.game.data.manager.DataManager;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.utils.CommonConfig;
import com.game.utils.Global;
import com.game.utils.MessageUtil;
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
		Q_globalBean globalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.ACTIVATION_ATTRIBUTE.getValue());
		String[] split = globalBean.getQ_string_value().split(Symbol.DOUHAO_REG);
		Map<Integer,Integer> type2map = new HashMap<Integer,Integer>();
		for(String str:split) {
			String[] split2 = str.split("_");
			type2map.put(Integer.parseInt(split2[0]), Integer.parseInt(split2[1]));
		}
		this.type2map = type2map;
	}
	
	public void login(Player player) {
		int count = 0;
		for(int i=0; i<player.getEquips().length; i++) {
			Equip equip = player.getEquips()[i];
			if(equip != null) {
				count += checkEquip(equip);
			}
		}
		player.setAttributeCount(count);
		sendMessage(player);
	}
	
	public void puton(Player player,Equip equip) {
		int checkEquip = checkEquip(equip);
		player.setAttributeCount(player.getAttributeCount() + checkEquip);
		if(checkEquip != 0) {
			int attributeCount = player.getAttributeCount();
			Q_activation_attributeBean beanByCount = getBeanByCount(attributeCount-checkEquip);
			Q_activation_attributeBean beanByCount2 = getBeanByCount(attributeCount);
			if(beanByCount2 != null && beanByCount2 != beanByCount) {
				if(attributeCount <= 18) {
					MessageUtil.notify_player(player, Notifys.CUTOUT, String.format(ResManager.getInstance().getString("%s激活了%s条卓越属性"),player.getName(),attributeCount));
				}else {
					MessageUtil.notify_All_player(Notifys.CUTOUT, String.format(ResManager.getInstance().getString("%s激活了%s条卓越属性"),player.getName(),attributeCount));
				}
			}
		}
	}
	
	public void takeoff(Player player,Equip equip) {
		player.setAttributeCount(player.getAttributeCount() - checkEquip(equip));
	}
	
	public void sendMessage(Player player) {
		ResAttributeChangeMessage msg = new ResAttributeChangeMessage();
		msg.setType((byte) 1);
		msg.setParam(String.valueOf(player.getAttributeCount()));
		MessageUtil.tell_player_message(player, msg);
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
	
	public Q_activation_attributeBean getBeanByCount(Player player) {
		return getBeanByCount(player.getAttributeCount());
	}
	
	private Q_activation_attributeBean getBeanByCount(int count) {
		Q_activation_attributeBean bean = null;
		List<Q_activation_attributeBean> listByType = DataManager.getInstance().q_activation_attributeContainer.getListByType(1);
		for(Q_activation_attributeBean item : listByType) {
			if(count >= Integer.parseInt(item.getQ_param())) {
				bean = item;
			}else {
				break;
			}
		}
		return bean;
	}
	
}
