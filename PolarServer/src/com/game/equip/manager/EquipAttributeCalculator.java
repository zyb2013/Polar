package com.game.equip.manager;

import com.game.backpack.structs.Equip;
import com.game.data.bean.Q_itemBean;
import com.game.data.manager.DataManager;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttribute;
import com.game.player.structs.PlayerAttributeCalculator;
import com.game.player.structs.PlayerAttributeType;
import com.game.structs.Attributes;
import com.game.utils.Global;

public class EquipAttributeCalculator implements PlayerAttributeCalculator {

	@Override
	public int getType() {
		return PlayerAttributeType.EQUIP;
	}

	@Override
	public PlayerAttribute getPlayerAttribute(Player player) {
		PlayerAttribute att = new PlayerAttribute();
		
		//装备加成
		int defenseEquipValue = 0;
		int dodgeEquipValue = 0;// 防御成功率
		int dodgeEquipPercent = 0;// 防御成功率万分比加成
		int maxHpEquipValue = 0;
		int maxHpEquipPercent = 0;// 增加血量上限万分比
		int maxMpEquipValue = 0;
		int maxMpEquipPercent = 0;// 增加法力上限万分比
		int attackSpeedEquipValue = 0;
		int speedEquipValue = 0;
		int luckEquipValue = 0;
		
		//add start
		int physicAttackupperEquipValue = 0;
		int physicAttacklowerEquipValue = 0;
		int magicAttackupperEquipValue = 0;
		int magicAttacklowerEquipValue = 0;
		int physicAttackPercent = 0;
		int magicAttackPercent = 0;
		int hitEquipValue = 0;
		double knowingAttackPercent = 1.0;// 会心一击,出现会心一击的几率=(1-(1-第一件几率)*(1-第二件几率))……
		int perfectAttack = 0;// 卓越一击
		int perfectAttackpercent=0;//卓越一击
		double ignoreAttackPercent = 1.0;// 无视一击,出现无视一击的几率=(1-(1-第一件几率)*(1-第二件几率))……
		
		int iceDef = 0;
		int rayDef = 0;
		int poisonDef = 0;

		double addInjure = 1.0;// 增加伤害
		double reduceInjure = 1.0;// 吸收伤害

		// 生命回复 +% 几率
		int hp_recover = 0;
		// 伤害反射 -% 几率
		int rebound_damage = 0;

		// 杀怪掉出的金币增加 +x% 几率
		int addmoney_whenkill = 0;
		// 杀怪时得到的生命值增加 + 生命/x
		int addhp_whenkill = 0;
		// 杀怪时得到的魔法值增加 + 魔法/x
		int addmp_whenkill = 0;
		//add end

		for (int i = 0; i < player.getEquips().length; i++) {
			Equip equip = player.getEquips()[i];
			if (equip == null || !equip.isCanUse())
				continue;
			
			/* （1）若副手位置为盾牌，则主手和副手位置的属性都是以100%加到人物属性上
			         （2）若主副手位置均为武器，则以主手的100%属性和副手的30%属性加到人物属性上
			*/
			if (i == 1) {
				Equip leftEquip = player.getEquips()[0];
				if (leftEquip != null && leftEquip.isCanUse()) {
					Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(equip.getItemModelId());
					if (q_itemBean.getQ_kind() != 2) {// 不包括盾
						/*
						physicAttackupperEquipValue *= 0.55d;
						physicAttacklowerEquipValue *= 0.55d;
						magicAttackupperEquipValue *= 0.55d;
						magicAttacklowerEquipValue *= 0.55d;
						*/
						// 因为下面将会加上第二件武器的100%，所以这里要减掉70%，即剩下30%
						physicAttackupperEquipValue -= (equip.getPhysicAttackUpper(player.getLevel()) * 0.7d);
						physicAttacklowerEquipValue -= (equip.getPhysicAttackLower(player.getLevel()) * 0.7d);
						magicAttackupperEquipValue -= (equip.getMagicAttackUpper(player.getLevel()) * 0.7d);
						magicAttacklowerEquipValue -= (equip.getMagicAttackLower(player.getLevel()) * 0.7d);
					}
				}
			}
			physicAttackupperEquipValue += equip.getPhysicAttackUpper(player.getLevel());
			physicAttacklowerEquipValue += equip.getPhysicAttackLower(player.getLevel());
			physicAttackPercent += equip.getAttributeByType(Attributes.Q_PHYSICATTACK_PERCENT.getValue());
			physicAttackPercent += equip.getAttributeByType(Attributes.Q_ATTACK_PERCENT.getValue());
			magicAttackupperEquipValue += equip.getMagicAttackUpper(player.getLevel());
			magicAttacklowerEquipValue += equip.getMagicAttackLower(player.getLevel());
			magicAttackPercent += equip.getAttributeByType(Attributes.Q_MAGICATTACK_PERCENT.getValue());
			magicAttackPercent += equip.getAttributeByType(Attributes.Q_ATTACK_PERCENT.getValue());
			hitEquipValue+=equip.getHitValue();
			// maxDamageHitEquipValue+=equip.getMaxDamageHitValue();
			// 会心一击
			knowingAttackPercent *= (double) (1 - (double) equip.getKnowingAttackValue() / Global.MAX_PROBABILITY);
			// 无视一击
			ignoreAttackPercent *= (double) (1 - (double) equip.getIgnoreAttackValue() / Global.MAX_PROBABILITY);
			// 卓越一击
			perfectAttack += equip.getCrit();
			perfectAttackpercent += equip.getAttributeByType(Attributes.PERFECT_ATTACKPERCENT.getValue());
			// 毒、冰、电   1,11;2,31,3,61 

			iceDef+=equip.getIceDefence();
			rayDef+=equip.getRayDefence();
			poisonDef+=equip.getPoisonDefence();
			defenseEquipValue += equip.getDefense();
			dodgeEquipValue += equip.getDodge();//基础防御成功率
			dodgeEquipPercent += equip.getAttributeByType(Attributes.DODGE.getValue());
			maxHpEquipValue += equip.getMaxHp();
			maxMpEquipValue += equip.getMaxMp();
			attackSpeedEquipValue += equip.getAttackSpeed();
			speedEquipValue += equip.getSpeed();
			luckEquipValue += equip.getLuck();

			addInjure *= (double) (1 + (double) equip.getAddInjureValue() / Global.MAX_PROBABILITY);
			reduceInjure *= (double) (1 - (double) equip.getReduceInjureValue() / Global.MAX_PROBABILITY);
			reduceInjure *= (double) (1 - (double) equip.getAttributeByType(Attributes.Q_REDUCE_DAMAGE.getValue()) / Global.MAX_PROBABILITY);
			hp_recover += equip.getAttributeByType(Attributes.Q_HP_RECOVER.getValue());
			maxMpEquipPercent += equip.getAttributeByType(Attributes.Q_ADD_MPLIMIT.getValue());
			maxHpEquipPercent += equip.getAttributeByType(Attributes.Q_ADD_HP_LIMIT.getValue());
			rebound_damage += equip.getAttributeByType(Attributes.Q_REBOUND_DAMAGE.getValue());
			addmoney_whenkill += equip.getAttributeByType(Attributes.Q_ADD_MONEY.getValue());
			int hpWhenKillAtt = equip.getAttributeByType(Attributes.Q_ADDHP_WHENKILL.getValue());
			if (hpWhenKillAtt != 0) {
				addhp_whenkill += (player.getMaxHp() / hpWhenKillAtt);// 不需要计算最终血量上限
			}
			int mpWhenKillAtt = equip.getAttributeByType(Attributes.Q_ADDMP_WHENKILL.getValue());
			if (mpWhenKillAtt != 0) {
				addmp_whenkill += (player.getMaxMp() / mpWhenKillAtt);
			}
		}
		att.setDefense(defenseEquipValue);
		att.setDodge(dodgeEquipValue);
		att.setDodgePercent(dodgeEquipPercent);
		att.setMaxHp(maxHpEquipValue);
		att.setMaxMp(maxMpEquipValue);
		att.setAttackSpeed(attackSpeedEquipValue);
		att.setSpeed(speedEquipValue);
		att.setLuck(luckEquipValue);
		
		//add start
		att.setPhysic_attackupper(physicAttackupperEquipValue);
		att.setPhysic_attacklower(physicAttacklowerEquipValue);
		att.setPhysic_attackPercent(physicAttackPercent);
		att.setMagic_attackupper(magicAttackupperEquipValue);
		att.setMagic_attacklower(magicAttacklowerEquipValue);
		att.setMagic_attackPercent(magicAttackPercent);
		att.setIce_def(iceDef);
		att.setRay_def(rayDef);
		att.setPoison_def(poisonDef);
		att.setHit(hitEquipValue);
		att.setKnowing_attackPercent(knowingAttackPercent);
		att.setPerfect_attack(perfectAttack);
		att.setPerfect_attackPercent(perfectAttackpercent);
		att.setIgnore_attackPercent(ignoreAttackPercent);

		att.setAdd_injure(addInjure);
		att.setReduce_injure(reduceInjure);

		att.setHp_recover(hp_recover);
		att.setMaxMpPercent(maxMpEquipPercent);
		att.setMaxHpPercent(maxHpEquipPercent);
		att.setRebound_damage(rebound_damage);
		att.setAddmoney_whenkill(addmoney_whenkill);
		att.setAddhp_whenkill(addhp_whenkill);
		att.setAddmp_whenkill(addmp_whenkill);
		//add end
		
		return att;
	}

}
