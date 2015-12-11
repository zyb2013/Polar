package com.game.buff.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.game.buff.structs.AttributeBuff;
import com.game.buff.structs.Buff;
import com.game.buff.structs.BuffType;
import com.game.data.bean.Q_buffBean;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttribute;
import com.game.player.structs.PlayerAttributeCalculator;
import com.game.player.structs.PlayerAttributeType;
import com.game.utils.Global;

public class BuffAttributeCalculator implements PlayerAttributeCalculator {
/*
 * 注意暂时不加物理攻击力上限，下限
 */
	@Override
	public int getType() {
		return PlayerAttributeType.BUFF;
	}

	@Override
	public PlayerAttribute getPlayerAttribute(Player player) {
		PlayerAttribute att = new PlayerAttribute();
		// 计算战斗力用的
		PlayerAttribute calAttr = new PlayerAttribute();
		
		//Buff加成
		int attackBufValue = 0;
		int attackBufPercent = 0;
		int equipAttackPercent = 0;
		int defenseBufValue = 0;
		int defenseBufPercent = 0;
		int equipDefensePercent = 0;
		int critBufValue = 0;
		int critBufPercent = 0;
		int dodgeBufValue = 0;
		int dodgeBufPercent = 0;
		int maxHpBufValue = 0;
		int maxHpBufPercent = 0;
		int maxMpBufValue = 0;
		int maxMpBufPercent = 0;
		int maxSpBufValue = 0;
		int maxSpBufPercent = 0;
		int attackSpeedBufValue = 0;
		int attackSpeedBufPercent = 0;
		int totalAttackPercent = 0;
		int totalDefensePercent = 0;
		int totalCritPercent = 0;
		int totalDodgePercent = 0;
		int totalLuckPercent = 0;
		int totalSpeedPercent = 0;
		int totalAttackSpeedPercent = 0;
		int totolMaxHpPercent = 0;
		int totolMaxMpPercent = 0;
		int totolMaxSpPercent = 0;
		int speedBufValue = 0;
		int speedBufPercent = 0;
		int luckBufValue = 0;
		int luckBufPercent = 0;
		int expPercent = 0;
		int zhenQiPercent=0;
		
		//panic
		double add_injure= 1;
		double reduce_injure = 1;
		
		//xuliang
		int strength = 0;
		int intelligence = 0;
		int vitality = 0;
		int agil = 0;
		
		// luminghua
		int perfectAttack = 0;
		double knowingAttackPercent = 1.0;// 会心一击
		double ignoreAttackPercent = 1.0;// 无视一击
		int perfectAttackAddition = 0;// 触发卓越一击后伤害加成
		int knowingAttackAddition = 0;// 触发会心一击后伤害加成

		HashMap<Integer, Integer> skills = new HashMap<Integer, Integer>();
		for (int i = 0; i < player.getBuffs().size(); i++) {
			Buff buff = player.getBuffs().get(i);
			if(buff instanceof AttributeBuff){
				AttributeBuff aBuff = (AttributeBuff)buff;
				if (aBuff.getFightFlag()) { // 需要计算战斗力
					calAttr.setAttack(calAttr.getAttack() + aBuff.getAttack() * aBuff.getOverlay());
					calAttr.setAttackPercent(calAttr.getAttackPercent() + aBuff.getAttackPercent() * aBuff.getOverlay());
					calAttr.setEquipAttackPercent(calAttr.getEquipAttackPercent() + aBuff.getEquipAttackPercent() * aBuff.getOverlay());
					calAttr.setDefense(calAttr.getDefense() + aBuff.getDefense() * aBuff.getOverlay());
					calAttr.setDefensePercent(calAttr.getDefensePercent() + aBuff.getDefensePercent() * aBuff.getOverlay());
					calAttr.setEquipDefensePercent(calAttr.getEquipDefensePercent() + aBuff.getEquipDefensePercent() * aBuff.getOverlay());
					calAttr.setCrit(calAttr.getCrit() + aBuff.getCrit() * aBuff.getOverlay());
					calAttr.setCritPercent(calAttr.getCritPercent() + aBuff.getCritPercent() * aBuff.getOverlay());
					calAttr.setDodge(calAttr.getDodge() + aBuff.getDodge() * aBuff.getOverlay());
					calAttr.setDodgePercent(calAttr.getDodgePercent() + aBuff.getDodgePercent() * aBuff.getOverlay());
					calAttr.setMaxHp(calAttr.getMaxHp() + aBuff.getMaxHp() * aBuff.getOverlay());
					calAttr.setMaxHpPercent(calAttr.getMaxHpPercent() + aBuff.getMaxHpPercent() * aBuff.getOverlay());
					calAttr.setMaxMp(calAttr.getMaxMp() + aBuff.getMaxMp() * aBuff.getOverlay());
					calAttr.setMaxMpPercent(calAttr.getMaxMpPercent() + aBuff.getMaxMpPercent() * aBuff.getOverlay());
					calAttr.setMaxSp(calAttr.getMaxSp() + aBuff.getMaxSp() * aBuff.getOverlay());
					calAttr.setMaxSpPercent(calAttr.getMaxSpPercent() + aBuff.getMaxSpPercent() * aBuff.getOverlay());
					calAttr.setAttackSpeed(calAttr.getAttackSpeed() + aBuff.getAttackSpeed() * aBuff.getOverlay());
					calAttr.setAttackSpeedPercent(calAttr.getAttackSpeedPercent() + aBuff.getAttackSpeedPercent() * aBuff.getOverlay());
					calAttr.setAttackTotalPercent(calAttr.getAttackSpeedTotalPercent() + aBuff.getTotalAttackPercent() * aBuff.getOverlay());
					calAttr.setDefenseTotalPercent(calAttr.getDefenseTotalPercent() + aBuff.getTotalDefensePercent() * aBuff.getOverlay());
					calAttr.setCritTotalPercent(calAttr.getCritTotalPercent() + aBuff.getTotalCritPercent() * aBuff.getOverlay());
					calAttr.setDodgeTotalPercent(calAttr.getDodgeTotalPercent() + aBuff.getTotalDodgePercent() * aBuff.getOverlay());
					calAttr.setLuckTotalPercent(calAttr.getLuckTotalPercent() + aBuff.getTotalLuckPercent() * aBuff.getOverlay());
					calAttr.setSpeedTotalPercent(calAttr.getSpeedTotalPercent() + aBuff.getTotalSpeedPercent() * aBuff.getOverlay());
					calAttr.setAttackSpeedTotalPercent(calAttr.getAttackSpeedTotalPercent() + aBuff.getTotalAttackSpeedPercent() * aBuff.getOverlay());
					calAttr.setMaxHpTotalPercent(calAttr.getMaxHpTotalPercent() + aBuff.getTotalMaxHpPercent() * aBuff.getOverlay());
					calAttr.setMaxMpTotalPercent(calAttr.getMaxMpTotalPercent() + aBuff.getTotalMaxMpPercent() * aBuff.getOverlay());
					calAttr.setMaxSpTotalPercent(calAttr.getMaxSpTotalPercent() + aBuff.getTotalMaxSpPercent() * aBuff.getOverlay());
					calAttr.setSpeed(calAttr.getSpeed() + aBuff.getSpeed() * aBuff.getOverlay());
					calAttr.setSpeedPercent(calAttr.getSpeedPercent() + aBuff.getSpeedPercent() * aBuff.getOverlay());
					calAttr.setLuck(calAttr.getLuck() + aBuff.getLuck() * aBuff.getOverlay());
					calAttr.setLuckPercent(calAttr.getLuckPercent() + aBuff.getLuckPercent() * aBuff.getOverlay());
					calAttr.setExpPercent(calAttr.getExpPercent() + aBuff.getExpPercent() * aBuff.getOverlay());
					calAttr.setZhenQiPercent(calAttr.getZhenQiPercent() + aBuff.getZhenqiPercent() * aBuff.getOverlay());
					//xuliang add
					calAttr.setStrength(calAttr.getStrength() + aBuff.getStrength() * aBuff.getOverlay());
					calAttr.setIntelligence(calAttr.getIntelligence() + aBuff.getIntelligence() * aBuff.getOverlay());
					calAttr.setVitality(calAttr.getVitality() + aBuff.getVitality() * aBuff.getOverlay());
					calAttr.setAgil(calAttr.getAgil() + aBuff.getAgil() * aBuff.getOverlay());
				}
				attackBufValue += aBuff.getAttack() * aBuff.getOverlay();
				attackBufPercent += aBuff.getAttackPercent() * aBuff.getOverlay();
				equipAttackPercent += aBuff.getEquipAttackPercent() * aBuff.getOverlay();
				defenseBufValue += aBuff.getDefense() * aBuff.getOverlay();
				defenseBufPercent += aBuff.getDefensePercent() * aBuff.getOverlay();
				equipDefensePercent += aBuff.getEquipDefensePercent() * aBuff.getOverlay();
				critBufValue += aBuff.getCrit() * aBuff.getOverlay();
				critBufPercent += aBuff.getCritPercent() * aBuff.getOverlay();
				dodgeBufValue += aBuff.getDodge() * aBuff.getOverlay();
				dodgeBufPercent += aBuff.getDodgePercent() * aBuff.getOverlay();
				maxHpBufValue += aBuff.getMaxHp() * aBuff.getOverlay();
				maxHpBufPercent += aBuff.getMaxHpPercent() * aBuff.getOverlay();
				maxMpBufValue += aBuff.getMaxMp() * aBuff.getOverlay();
				maxMpBufPercent += aBuff.getMaxMpPercent() * aBuff.getOverlay();
				maxSpBufValue += aBuff.getMaxSp() * aBuff.getOverlay();
				maxSpBufPercent += aBuff.getMaxSpPercent() * aBuff.getOverlay();
				attackSpeedBufValue += aBuff.getAttackSpeed() * aBuff.getOverlay();
				attackSpeedBufPercent += aBuff.getAttackSpeedPercent() * aBuff.getOverlay();
				totalAttackPercent += aBuff.getTotalAttackPercent() * aBuff.getOverlay();;
				totalDefensePercent += aBuff.getTotalDefensePercent() * aBuff.getOverlay();;
				totalCritPercent += aBuff.getTotalCritPercent() * aBuff.getOverlay();;
				totalDodgePercent += aBuff.getTotalDodgePercent() * aBuff.getOverlay();;
				totalLuckPercent += aBuff.getTotalLuckPercent() * aBuff.getOverlay();
				totalSpeedPercent += aBuff.getTotalSpeedPercent() * aBuff.getOverlay();
				totalAttackSpeedPercent += aBuff.getTotalAttackSpeedPercent() * aBuff.getOverlay();
				totolMaxHpPercent += aBuff.getTotalMaxHpPercent() * aBuff.getOverlay();
				totolMaxMpPercent += aBuff.getTotalMaxMpPercent() * aBuff.getOverlay();
				totolMaxSpPercent += aBuff.getTotalMaxSpPercent() * aBuff.getOverlay();
				speedBufValue += aBuff.getSpeed() * aBuff.getOverlay();
				speedBufPercent += aBuff.getSpeedPercent() * aBuff.getOverlay();
				luckBufValue += aBuff.getLuck() * aBuff.getOverlay();
				luckBufPercent += aBuff.getLuckPercent() * aBuff.getOverlay();
				expPercent += aBuff.getExpPercent() * aBuff.getOverlay();
				zhenQiPercent+=aBuff.getZhenqiPercent() * aBuff.getOverlay();
				Iterator<Entry<Integer, Integer>> iter = aBuff.getSkillLevelUp().entrySet().iterator();
				//panic add
				reduce_injure *= (double) (1 - (double) aBuff.getreduce_injure()* aBuff.getOverlay()/Global.MAX_PROBABILITY);
				add_injure *= (double) (1 - (double) aBuff.getadd_injure()* aBuff.getOverlay()/Global.MAX_PROBABILITY);
				//xuliang add
				strength += aBuff.getStrength() * aBuff.getOverlay();
				intelligence += aBuff.getIntelligence() * aBuff.getOverlay();
				vitality += aBuff.getVitality() * aBuff.getOverlay();
				agil += aBuff.getAgil() * aBuff.getOverlay();
				// luminghua
				// 卓越一击
				perfectAttack += aBuff.getPerfectAttack();
				perfectAttackAddition += aBuff.getPerfectAttackAddition();
				// 会心一击
				knowingAttackPercent *= (double) (1 - (double) aBuff.getKnowingAttack() / Global.MAX_PROBABILITY);
				knowingAttackAddition += aBuff.getKnowingAttackAddition();
				// 无视一击
				ignoreAttackPercent *= (double) (1 - (double) aBuff.getIgnoreAttack() / Global.MAX_PROBABILITY);
				
//				//! 特殊处理 add by xuliang
//				//! 生命上限比例特殊处理
//				if (aBuff.getActionType() == BuffType.MAXHP_PLUS){
//					Q_buffBean buffModel = ManagerPool.dataManager.q_buffContainer.getMap().get(aBuff.getModelId());
//					maxHpBufPercent += buffModel.getQ_effect_ratio() + player.getAttibute_one()[3] * 5;
//				}
//				//! 吸收伤害比例特殊处理
//				if (aBuff.getActionType() == BuffType.GUARDIANSPIRIT_PLUS){
//					Q_buffBean buffModel = ManagerPool.dataManager.q_buffContainer.getMap().get(aBuff.getModelId());
//					reduce_injure *= (double) (1 - (double)(buffModel.getQ_effect_ratio() + player.getAttibute_one()[2])* aBuff.getOverlay()/Global.MAX_PROBABILITY);
//				}
//				//! 攻击力增加特殊处理
//				if (aBuff.getActionType() == BuffType.ATTACK_PLUS){
//					Q_buffBean buffModel = ManagerPool.dataManager.q_buffContainer.getMap().get(aBuff.getModelId());
//					attackBufValue += buffModel.getQ_effect_value() + player.getAttibute_one()[3] * 3000 / Global.MAX_PROBABILITY;
//				}
//				//! 防御力增加特殊处理
//				if (aBuff.getActionType() == BuffType.DEFENSE_PLUS){
//					Q_buffBean buffModel = ManagerPool.dataManager.q_buffContainer.getMap().get(aBuff.getModelId());
//					defenseBufValue += buffModel.getQ_effect_value() + player.getAttibute_one()[3] * 320 / Global.MAX_PROBABILITY;
//				}
				
				while (iter.hasNext()) {
					Map.Entry<java.lang.Integer, java.lang.Integer> entry = (Map.Entry<java.lang.Integer, java.lang.Integer>) iter
							.next();
					if(skills.containsKey(entry.getKey())){
						skills.put(entry.getKey(), skills.get(entry.getKey()) + entry.getValue() * aBuff.getOverlay());
					}else{
						skills.put(entry.getKey(), entry.getValue() * aBuff.getOverlay());
					}
				}
			}
		}
		
		att.setAttack(attackBufValue);
		att.setAttackPercent(attackBufPercent);
		att.setEquipAttackPercent(equipAttackPercent);
		att.setDefense(defenseBufValue);
		att.setDefensePercent(defenseBufPercent);
		att.setEquipDefensePercent(equipDefensePercent);
		att.setCrit(critBufValue);
		att.setCritPercent(critBufPercent);
		att.setDodge(dodgeBufValue);
		att.setDodgePercent(dodgeBufPercent);
		att.setMaxHp(maxHpBufValue);
		att.setMaxHpPercent(maxHpBufPercent);
		att.setMaxMp(maxMpBufValue);
		att.setMaxMpPercent(maxMpBufPercent);
		att.setMaxSp(maxSpBufValue);
		att.setMaxSpPercent(maxSpBufPercent);
		att.setAttackSpeed(attackSpeedBufValue);
		att.setAttackSpeedPercent(attackSpeedBufPercent);
		att.setAttackTotalPercent(totalAttackPercent);
		att.setDefenseTotalPercent(totalDefensePercent);
		att.setCritTotalPercent(totalCritPercent);
		att.setDodgeTotalPercent(totalDodgePercent);
		att.setLuckTotalPercent(totalLuckPercent);
		att.setSpeedTotalPercent(totalSpeedPercent);
		att.setAttackSpeedTotalPercent(totalAttackSpeedPercent);
		att.setMaxHpTotalPercent(totolMaxHpPercent);
		att.setMaxMpTotalPercent(totolMaxMpPercent);
		att.setMaxSpTotalPercent(totolMaxSpPercent);
		att.setSpeed(speedBufValue);
		att.setSpeedPercent(speedBufPercent);
		att.setLuck(luckBufValue);
		att.setLuckPercent(luckBufPercent);
		att.setSkillLevelUp(skills);
		att.setExpPercent(expPercent);
		att.setZhenQiPercent(zhenQiPercent);
		player.setBuffCalAttr(calAttr);
		//
		att.setReduce_injure(reduce_injure);
		att.setAdd_injure(add_injure);
		//xuliang add
		att.setStrength(strength);
		att.setAgil(agil);
		att.setVitality(vitality);
		att.setIntelligence(intelligence);
		// luminghua
		att.setPerfect_attack(perfectAttack);
		att.setPerfectatk_addpercent(perfectAttackAddition);
		att.setIgnore_attackPercent(ignoreAttackPercent);
		att.setKnowing_attackPercent(knowingAttackPercent);
		att.setKnowingatk_addpercent(knowingAttackAddition);
		return att;
	}

}
