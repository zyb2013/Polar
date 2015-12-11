package com.game.backpack.manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.game.backpack.structs.Attribute;
import com.game.backpack.structs.Equip;
import com.game.backpack.structs.Item;
import com.game.data.bean.Q_equip_appendBean;
import com.game.data.manager.DataManager;
import com.game.structs.Attributes;
import com.game.utils.RandomUtils;
import com.game.utils.StringUtil;
import com.game.utils.Symbol;
/**
 * 
 * @author  
 *
 */
public class ItemAppendManager {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ItemAppendManager.class);

	/**
	 * 物品加成管理类实例
	 */
	private static ItemAppendManager manager;

	private ItemAppendManager() {
	}
	private static Object obj = new Object();
	public static ItemAppendManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new ItemAppendManager();
			}
		}
		return manager;
	}
	
	/**
	 * 装备
	 * @param equip
	 * @param count	条数（废弃）
	 * @return
	 */
	// public Item buildAppend(Equip equip,int count){
	// List<Integer> hasAttributeList = new ArrayList<>();
	// Q_equip_appendBean appendModel = DataManager.getInstance().q_equip_appendContainer.getMap().get(equip.getItemModelId());
	// if(appendModel == null) {
	// return equip;
	// }
	// //随机赋予几条
	// count = RandomUtils.random(appendModel.getAttribute_min(), appendModel.getAttribute_max());
	// while (count > 0) {
	// Attribute generateAttribute = generateAttribute(appendModel);
	// if (generateAttribute != null) {
	// if (hasAttributeList.contains(generateAttribute.getType())) {
	// continue;
	// }
	// hasAttributeList.add(generateAttribute.getType());
	// equip.getAttributes().add(generateAttribute);
	// count--;
	// }
	// }
	// return equip;
	// }
	//
	public Item buildAppend(Equip equip) {
		try {
			Q_equip_appendBean appendModel = DataManager.getInstance().q_equip_appendContainer.getMap().get(equip.getItemModelId());
			if (appendModel == null) {
				return equip;
			}
			// 随机赋予几条
			int count = RandomUtils.random(appendModel.getAttribute_min(), appendModel.getAttribute_max());
			if (count > 0) {
				List<Attribute> generateAttribute = generateAttribute(appendModel, count);
				if (generateAttribute != null && generateAttribute.size() > 0) {
					equip.getAttributes().addAll(generateAttribute);
					// System.out.println("append count:" + count + ",size:" + generateAttribute.size() + " :" + generateAttribute);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return equip;
	}

	private List<Attribute> generateAttribute(Q_equip_appendBean appendModel, int count) {
		if(appendModel==null)return null;
		List<WrapAttribute> randomList = new ArrayList<WrapAttribute>();
		int totalPercent = 0;
		if(appendModel.getQ_attack()!=0){
			randomList.add(new WrapAttribute(Attributes.ATTACK, appendModel.getQ_attack()));
			totalPercent += appendModel.getQ_attack();
		}
		// luminghua 暂时不用
		// if(appendModel.getQ_attackspeed()!=0){
		// randomList.add(new WrapAttribute(Attributes.ATTACKSPEED, appendModel.getQ_attackspeed()));
		// totalPercent += appendModel.getQ_attackspeed();
		// }
		if(appendModel.getQ_defence()!=0){
			randomList.add(new WrapAttribute(Attributes.DEFENSE, appendModel.getQ_defence()));
			totalPercent += appendModel.getQ_defence();
		}
		// 暂时不用
		// if(appendModel.getQ_crit()!=0){
		// probs.add(appendModel.getQ_crit());
		// keys.add(3);
		// }

		if(appendModel.getQ_dodge()!=0){
			randomList.add(new WrapAttribute(Attributes.DODGE, appendModel.getQ_dodge()));
			totalPercent += appendModel.getQ_dodge();
		}
		if (appendModel.getQ_speed() != 0) {
			randomList.add(new WrapAttribute(Attributes.SPEED, appendModel.getQ_speed()));
			totalPercent += appendModel.getQ_speed();
		}
		if(appendModel.getQ_hp()!=0){
			randomList.add(new WrapAttribute(Attributes.MAXHP, appendModel.getQ_hp()));
			totalPercent += appendModel.getQ_hp();
		}
		if(appendModel.getQ_mp()!=0){
			randomList.add(new WrapAttribute(Attributes.MAXMP, appendModel.getQ_mp()));
			totalPercent += appendModel.getQ_mp();
		}
		if(appendModel.getQ_sp()!=0){
			randomList.add(new WrapAttribute(Attributes.MAXSP, appendModel.getQ_sp()));
			totalPercent += appendModel.getQ_sp();
		}
		if(appendModel.getQ_luck()!=0){
			randomList.add(new WrapAttribute(Attributes.LUCK, appendModel.getQ_luck()));
			totalPercent += appendModel.getQ_luck();
		}
		//新增属性
		if(appendModel.getQ_hp_recover()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_HP_RECOVER, appendModel.getQ_hp_recover()));
			totalPercent += appendModel.getQ_hp_recover();
		}
		if(appendModel.getQ_add_mplimit()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_ADD_MPLIMIT, appendModel.getQ_add_mplimit()));
			totalPercent += appendModel.getQ_add_mplimit();
		}
		if(appendModel.getQ_add_hp_limit()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_ADD_HP_LIMIT, appendModel.getQ_add_hp_limit()));
			totalPercent += appendModel.getQ_add_hp_limit();
		}
		if(appendModel.getQ_reduce_damage()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_REDUCE_DAMAGE, appendModel.getQ_reduce_damage()));
			totalPercent += appendModel.getQ_reduce_damage();
		}
		if(appendModel.getQ_rebound_damage()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_REBOUND_DAMAGE, appendModel.getQ_rebound_damage()));
			totalPercent += appendModel.getQ_rebound_damage();
		}
		// if(appendModel.getQ_dodge()!=0){
		// probs.add(appendModel.getQ_dodge());
		// keys.add(15);
		// }
		if(appendModel.getQ_add_money()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_ADD_MONEY, appendModel.getQ_add_money()));
			totalPercent += appendModel.getQ_add_money();
		}
		if(appendModel.getQ_remarkble_attack()!=0){
			randomList.add(new WrapAttribute(Attributes.PERFECT_ATTACKPERCENT, appendModel.getQ_remarkble_attack()));
			totalPercent += appendModel.getQ_remarkble_attack();
		}
		if(appendModel.getQ_physicattack_bylevel()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_PHYSICATTACK_BYLEVEL, appendModel.getQ_physicattack_bylevel()));
			totalPercent += appendModel.getQ_physicattack_bylevel();
		}
		if(appendModel.getQ_physicattack_Percent()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_PHYSICATTACK_PERCENT, appendModel.getQ_physicattack_Percent()));
			totalPercent += appendModel.getQ_physicattack_Percent();
		}
		if(appendModel.getQ_magicattack_bylevel()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_MAGICATTACK_BYLEVEL, appendModel.getQ_magicattack_bylevel()));
			totalPercent += appendModel.getQ_magicattack_bylevel();
		}
		if(appendModel.getQ_magicattack_Percent()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_MAGICATTACK_PERCENT, appendModel.getQ_magicattack_Percent()));
			totalPercent += appendModel.getQ_magicattack_Percent();
		}
		if(appendModel.getQ_attackspeed()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_ATTACKSPEED, appendModel.getQ_attackspeed()));
			totalPercent += appendModel.getQ_attackspeed();
		}
		if(appendModel.getQ_addhp_whenkill()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_ADDHP_WHENKILL, appendModel.getQ_addhp_whenkill()));
			totalPercent += appendModel.getQ_addhp_whenkill();
		}
		if(appendModel.getQ_addmp_whenkill()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_ADDMP_WHENKILL, appendModel.getQ_addmp_whenkill()));
			totalPercent += appendModel.getQ_addmp_whenkill();
		}
		if(appendModel.getQ_attack_bylevel()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_ATTACK_BYLEVEL, appendModel.getQ_attack_bylevel()));
			totalPercent += appendModel.getQ_attack_bylevel();
		}
		if(appendModel.getQ_attack_percent()!=0){
			randomList.add(new WrapAttribute(Attributes.Q_ATTACK_PERCENT, appendModel.getQ_attack_percent()));
			totalPercent += appendModel.getQ_attack_percent();
		}
		// 从小到大排序
		java.util.Collections.sort(randomList, new Comparator<WrapAttribute>() {

			@Override
			public int compare(WrapAttribute o1, WrapAttribute o2) {
				return o1.value > o2.value ? 1 : -1;
			}
		});
		List<Attribute> result = new ArrayList<Attribute>();
		for (int i = 0; i < count; i++) {
			if (totalPercent <= 0) {
				break;
			}
			int random = RandomUtils.random(totalPercent);
			WrapAttribute target = null;
			int addUp = 0;
			for (int j = 0; j < randomList.size(); j++) {
				WrapAttribute att = randomList.get(j);
				if (att.value + addUp > random) {
					target = att;
					// 排除已经拿到的属性
					randomList.remove(j);
					totalPercent -= att.value;
					break;
				}
				// 累加
				addUp += att.value;
			}
			if (target == null) {
				logger.error("机率计算出错,random:" + random + ",list:" + randomList);
				break;
			}
			Attribute attribute = new Attribute();
			attribute.setType(target.attributes.getValue());
			switch (target.attributes) {
			case ATTACK:
				attribute.setValue(RandomUtils.random(appendModel.getQ_attack_min(), appendModel.getQ_attack_max()));
				break;
			// case ATTACKSPEED:
			// attribute.setValue(RandomUtils.random(appendModel.getQ_attackspeed_min(), appendModel.getQ_attackspeed_max()));
			// break;
			case DEFENSE:
				attribute.setValue(RandomUtils.random(appendModel.getQ_defence_min(), appendModel.getQ_defence_max()));
				break;
			// panic 暂时不用
			// case 3:
			// attribute.setType(Attributes.PERFECT_ATTACKPERCENT.getValue());
			// attribute.setValue(RandomUtils.random(appendModel.getQ_crit_min(),appendModel.getQ_crit_max()));
			// break;
			case DODGE:
				attribute.setValue(RandomUtils.random(appendModel.getQ_dodge_min(), appendModel.getQ_dodge_max()));
				break;
			case SPEED:
				attribute.setValue(RandomUtils.random(appendModel.getQ_speed_min(), appendModel.getQ_speed_max()));
				break;
			case MAXHP:
				attribute.setValue(RandomUtils.random(appendModel.getQ_hp_min(), appendModel.getQ_hp_max()));
				break;
			case MAXMP:
				attribute.setValue(RandomUtils.random(appendModel.getQ_mp_min(), appendModel.getQ_mp_max()));
				break;
			case MAXSP:
				attribute.setValue(RandomUtils.random(appendModel.getQ_sp_min(), appendModel.getQ_sp_max()));
				break;
			case LUCK:
				attribute.setValue(RandomUtils.random(appendModel.getQ_luck_min(), appendModel.getQ_luck_max()));
				break;
			case Q_HP_RECOVER:
				attribute.setValue(RandomUtils.random(appendModel.getQ_hp_recover_min(), appendModel.getQ_hp_recover_max()));
				break;
			case Q_ADD_MPLIMIT:
				attribute.setValue(RandomUtils.random(appendModel.getQ_add_mplimit_min(), appendModel.getQ_add_mplimit_max()));
				break;
			case Q_ADD_HP_LIMIT:
				attribute.setValue(RandomUtils.random(appendModel.getQ_add_hp_limit_min(), appendModel.getQ_add_hp_limit_max()));
				break;
			case Q_REDUCE_DAMAGE:
				attribute.setValue(RandomUtils.random(appendModel.getQ_reduce_damage_min(), appendModel.getQ_reduce_damage_max()));
				break;
			case Q_REBOUND_DAMAGE:
				attribute.setValue(RandomUtils.random(appendModel.getQ_rebound_damage_min(), appendModel.getQ_rebound_damage_max()));
				break;
			// case 15:
			// attribute.setType(Attributes.Q_DODGE.getValue());
			// attribute.setValue(RandomUtils.random(appendModel.getQ_dodge_min(),appendModel.getQ_dodge_max()));
			// break;
			case Q_ADD_MONEY:
				attribute.setValue(RandomUtils.random(appendModel.getQ_add_money_min(), appendModel.getQ_add_money_max()));
				break;
			case PERFECT_ATTACKPERCENT:
				attribute.setValue(RandomUtils.random(appendModel.getQ_remarkble_attack_min(), appendModel.getQ_reduce_damage_max()));
				break;
			case Q_PHYSICATTACK_BYLEVEL:
				attribute.setValue(RandomUtils.random(appendModel.getQ_physicattack_bylevel_min(), appendModel.getQ_physicattack_bylevel_max()));
				break;
			case Q_PHYSICATTACK_PERCENT:
				attribute.setValue(RandomUtils.random(appendModel.getQ_physicattack_Percent_min(), appendModel.getQ_physicattack_Percent_max()));
				break;
			case Q_MAGICATTACK_BYLEVEL:
				attribute.setValue(RandomUtils.random(appendModel.getQ_magicattack_bylevel_min(), appendModel.getQ_magicattack_bylevel_max()));
				break;
			case Q_MAGICATTACK_PERCENT:
				attribute.setValue(RandomUtils.random(appendModel.getQ_magicattack_Percent_min(), appendModel.getQ_magicattack_Percent_max()));
				break;
			case Q_ATTACKSPEED:
				attribute.setValue(RandomUtils.random(appendModel.getQ_attackspeed_min(), appendModel.getQ_attackspeed_max()));
				break;
			case Q_ADDHP_WHENKILL:
				attribute.setValue(RandomUtils.random(appendModel.getQ_addhp_whenkill_min(), appendModel.getQ_addhp_whenkill_max()));
				break;
			case Q_ADDMP_WHENKILL:
				attribute.setValue(RandomUtils.random(appendModel.getQ_addmp_whenkill_min(), appendModel.getQ_addmp_whenkill_max()));
				break;
			case Q_ATTACK_BYLEVEL:
				attribute.setValue(RandomUtils.random(appendModel.getQ_attack_bylevel_min(), appendModel.getQ_attack_bylevel_max()));
				break;
			case Q_ATTACK_PERCENT:
				attribute.setValue(RandomUtils.random(appendModel.getQ_attack_percent_min(), appendModel.getQ_attack_percent_max()));
				break;
			default:
				break;
			}
			if (attribute.getValue() <= 0) {
				continue;
			}
			result.add(attribute);
		}
		return result;
	}
	
	/**
	 * 
	 * @param equip
	 * @param append 属性类型|属性值;属性类型|属性值;属性类型|属性值;
	 * @return
	 */
	public Item buildAppend(Equip equip,String append){
		try {
			if (append != null && !("").equals(append)) {
				equip.getAttributes().clear();
				String[] attributes = append.split(Symbol.FENHAO_REG);
				for (int i = 0; i < attributes.length; i++) {
					if (!StringUtil.isBlank(attributes[i])) {
						String[] strs = attributes[i].split("\\|");
						Attribute attribute = new Attribute();
						attribute.setType(Integer.parseInt(strs[0]));
						attribute.setValue(Integer.parseInt(strs[1]));
						equip.getAttributes().add(attribute);
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return equip;
	}

	private static class WrapAttribute {
		Attributes attributes;
		int value;

		public WrapAttribute(Attributes attributes, int value) {
			this.attributes = attributes;
			this.value = value;
		}

		public String toString() {
			return "Attributes[" + attributes + "],value[" + value + "]";
		}

	}
	// luminghua

	// public int generateAttribute(int type, Q_equip_appendBean appendModel) {
	// if (type == Attributes.ATTACK.getValue())
	// return RandomUtils.random(appendModel.getQ_attack_min(),appendModel.getQ_attack_max());
	// if (type == Attributes.ATTACKSPEED.getValue())
	// return RandomUtils.random(appendModel.getQ_attackspeed_min(),appendModel.getQ_attackspeed_max());
	// if (type == Attributes.DEFENSE.getValue())
	// return RandomUtils.random(appendModel.getQ_defence_min(),appendModel.getQ_defence_max());
	// if (type == Attributes.PERFECT_ATTACKPERCENT.getValue())
	// return RandomUtils.random(appendModel.getQ_crit_min(),appendModel.getQ_crit_max());
	// if (type == Attributes.DODGE.getValue())
	// return RandomUtils.random(appendModel.getQ_dodge_min(),appendModel.getQ_dodge_max());
	// if (type == Attributes.SPEED.getValue())
	// return RandomUtils.random(appendModel.getQ_speed_min(),appendModel.getQ_speed_max());
	// if (type == Attributes.MAXHP.getValue())
	// return RandomUtils.random(appendModel.getQ_hp_min(),appendModel.getQ_hp_max());
	// if (type == Attributes.MAXMP.getValue())
	// return RandomUtils.random(appendModel.getQ_mp_min(),appendModel.getQ_mp_max());
	// if (type == Attributes.MAXSP.getValue())
	// return RandomUtils.random(appendModel.getQ_sp_min(),appendModel.getQ_sp_max());
	// if (type == Attributes.LUCK.getValue())
	// return RandomUtils.random(appendModel.getQ_luck_min(),appendModel.getQ_luck_max());
	// return 0;
	// }
}
