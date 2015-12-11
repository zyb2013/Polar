package com.game.player.structs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.game.data.bean.Q_buffBean;
import com.game.json.JSONserializable;
import com.game.object.GameObject;
import com.game.utils.CommonString;
import com.game.utils.StringUtil;

/**
 * 玩家属性
 * 
 */
public class PlayerAttribute extends GameObject {
	/*
	 * 卓越模块
	 * 1.元素抗性判断
	 * 2.技能效果* > 无视一击* > 命中判定 > 会心一击*|卓越一击* > 伤害结算 
	 * 3.	伤害=((基本攻击力*(100%+x)+y+技能攻击力)-目标防御力)*(技能伤害200%
			基本攻击力是从最小攻击力和最大攻击力中随机出的数值
			x为附带的攻击力的提升百分比
			y为附带的攻击力的提升固定值
	   4.卓越逻辑判断	
	 */
	/*
	 * 技能效果*:指代游戏中技能命中目标后带来的电击、冰冻、毒素等技能附带效果 
	 */

	private static final long serialVersionUID = 8232665477417300232L;

	// 最大血量
	private int maxHp = 0;
	// 最大魔法
	private int maxMp = 0;

	// 最大血量
	private int maxHpPercent = 0;
	// 最大魔法
	private int maxMpPercent = 0;
	
	
	// 攻击速度
	private int attackSpeed = 0;
	//移动速度
	private int speed = 0;
	// 攻击速度
	private int attackSpeedPercent = 0;
	// 移动速度
	private int speedPercent = 0;
	
	//4个一级属性
	//一级初始化属性【力量，体力，敏捷，智力，预留属性】
	private int strength = 0;
	private int vitality = 0;
	private int agil = 0;
	private int intelligence = 0;
	
	//6个元素属性 冰。雷，毒，攻击和防御率 Ice. Ray, poison
	private int ice_attack = 0;
	private int ray_attack = 0;
	private int poison_attack = 0;	
	private int ice_def = 0;
	private int ray_def = 0;
	private int poison_def = 0;

	

	// 命中判定（缺少命中）//攻击成功率
	private int hit=0;
	private int hitPercent = 0;// 废用
	// 闪避值//防御成功率
	private int dodge = 0;
	// 闪避
	private int dodgePercent = 0;// 废用
	
	//暴击(人物暂时不用，用卓越属性代替)
	private int crit = 0;
	//暴击
	private int critPercent = 0;
	 
	//会心一击*:触发时攻击力取值为最大攻击  (除以万分比)
	private double Knowing_attackPercent = 0.0;
	//无视一击*:触发时无视目标的防御力 概率
	private double ignore_attackPercent = 0.0;
	//卓越一击 *暴击 // 暂时按照卓越一击*:触发时攻击伤害加成值120%，请查看主角属性文档 (除以万分比)
	private int perfect_attackPercent = 0;
	private int perfect_attack = 0;
	
	//加成卓越一击 *暴击 伤害加成值120% +%
	private int perfectatk_addpercent = 0;
	// 加成会心一击 = 100%+x%
	private int knowingatk_addpercent = 0;
	
	//进入  忽视防御百分比 的概率的机会
	private int ignore_defendPercent = 0;
	//进入  忽视防御百分比 的概率的后， 忽视防御百分比的值
	private int ignore_defend_add_Percent = 0;



	
	//*攻击  值//物理攻击，魔法攻击 // 最小，最大
	private int attack = 0; 
	private int physic_attackupper = 0;
	private int physic_attacklower = 0;
	private int magic_attackupper = 0;
	private int magic_attacklower = 0;
	
	
	//攻击 百分比//物理攻击，魔法攻击
	private int attackPercent = 0;
	private int physic_attackPercent = 0;
	private int magic_attackPercent = 0;	
	//*防御
	private int defense = 0;
	//防御百分比
	private int defensePercent = 0;
	//幸运 
	private int luck = 0; //暂时不用
	
	//无视防御 暂时用不到
	private int negDefence;
	
	// 增加伤害
	private double add_injure;
	// 吸收伤害
	private double reduce_injure;
	
	// 生命回复 +x%
	private int hp_recover;
	// 伤害反射 -x%
	private int rebound_damage;

	// 杀怪掉出的金币增加 +x%
	private int addmoney_whenkill;
	// 杀怪时得到的生命值增加 + 生命/x
	private int addhp_whenkill;
	// 杀怪时得到的魔法值增加 + 魔法/x
	private int addmp_whenkill;

	// 弓箭几率
	private int arrowProbability;
	// 幸运
	private int luckPercent = 0;

	// 攻击
	private int attackTotalPercent = 0;
	// 防御
	private int defenseTotalPercent = 0;
	// 暴击
	private int critTotalPercent = 0;
	// 命中
	private int hitTotalPercent = 0;
	// 闪避
	private int dodgeTotalPercent = 0;
	// 最大血量
	private int maxHpTotalPercent = 0;
	// 最大魔法
	private int maxMpTotalPercent = 0;
	// 最大体力
	private int maxSpTotalPercent = 0;
	// 攻击速度
	private int attackSpeedTotalPercent = 0;
	// 速度
	private int speedTotalPercent = 0;
	// 幸运
	private int luckTotalPercent = 0;
	// 装备攻击
	private int equipAttackPercent = 0;
	// 装备防御
	private int equipDefensePercent = 0;
	// 技能等级加成
	private HashMap<Integer, Integer> skillLevelUp = new HashMap<Integer, Integer>();
	// 装备防御
	private int expPercent = 0;
	// 真气
	private int zhenQiPercent = 0;
	// 最大体力 暂时用不到
	private int maxSp = 0;
	// 最大体力 暂时用不到
	private int maxSpPercent = 0;

	/**
	 * @return the perfect_attack
	 */
	public int getPerfect_attack() {
		return perfect_attack;
	}

	/**
	 * @param perfect_attack
	 *            the perfect_attack to set
	 */
	public void setPerfect_attack(int perfect_attack) {
		this.perfect_attack = perfect_attack;
	}

	public int getHitPercent() {
		return hitPercent;
	}

	public void setHitPercent(int hitPercent) {
		this.hitPercent = hitPercent;
	}

	public int getPerfect_attackPercent() {
		return perfect_attackPercent;
	}

	public void setPerfect_attackPercent(int perfect_attackPercent) {
		this.perfect_attackPercent = perfect_attackPercent;
	}

	public int getPerfectatk_addpercent() {
		return perfectatk_addpercent;
	}

	public void setPerfectatk_addpercent(int perfectatk_addpercent) {
		this.perfectatk_addpercent = perfectatk_addpercent;
	}

	public int getKnowingatk_addpercent() {
		return knowingatk_addpercent;
	}

	public void setKnowingatk_addpercent(int knowingatk_addpercent) {
		this.knowingatk_addpercent = knowingatk_addpercent;
	}

	public int getIgnore_defend_add_Percent() {
		return ignore_defend_add_Percent;
	}

	public void setIgnore_defend_add_Percent(int ignore_defend_add_Percent) {
		this.ignore_defend_add_Percent = ignore_defend_add_Percent;
	}
	
	
	public int getHitTotalPercent() {
		return hitTotalPercent;
	}

	public void setHitTotalPercent(int hitTotalPercent) {
		this.hitTotalPercent = hitTotalPercent;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the hp_recover
	 */
	public int getHp_recover() {
		return hp_recover;
	}

	/**
	 * @param hp_recover
	 *            the hp_recover to set
	 */
	public void setHp_recover(int hp_recover) {
		this.hp_recover = hp_recover;
	}

	/**
	 * @return the rebound_damage
	 */
	public int getRebound_damage() {
		return rebound_damage;
	}

	/**
	 * @param rebound_damage
	 *            the rebound_damage to set
	 */
	public void setRebound_damage(int rebound_damage) {
		this.rebound_damage = rebound_damage;
	}

	/**
	 * @return the addmoney_whenkill
	 */
	public int getAddmoney_whenkill() {
		return addmoney_whenkill;
	}

	/**
	 * @param addmoney_whenkill
	 *            the addmoney_whenkill to set
	 */
	public void setAddmoney_whenkill(int addmoney_whenkill) {
		this.addmoney_whenkill = addmoney_whenkill;
	}

	/**
	 * @return the addhp_whenkill
	 */
	public int getAddhp_whenkill() {
		return addhp_whenkill;
	}

	/**
	 * @param addhp_whenkill
	 *            the addhp_whenkill to set
	 */
	public void setAddhp_whenkill(int addhp_whenkill) {
		this.addhp_whenkill = addhp_whenkill;
	}

	/**
	 * @return the addmp_whenkill
	 */
	public int getAddmp_whenkill() {
		return addmp_whenkill;
	}

	/**
	 * @param addmp_whenkill
	 *            the addmp_whenkill to set
	 */
	public void setAddmp_whenkill(int addmp_whenkill) {
		this.addmp_whenkill = addmp_whenkill;
	}

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	public int gethitPercent() {
		return hitPercent;
	}

	public void sethitPercent(int hitPercent) {
		this.hitPercent = hitPercent;
	}

	public int getPhysic_attackupper() {
		return physic_attackupper;
	}

	public void setPhysic_attackupper(int physic_attackupper) {
		this.physic_attackupper = physic_attackupper;
	}

	public int getPhysic_attacklower() {
		return physic_attacklower;
	}

	public void setPhysic_attacklower(int physic_attacklower) {
		this.physic_attacklower = physic_attacklower;
	}

	public int getMagic_attackupper() {
		return magic_attackupper;
	}

	public void setMagic_attackupper(int magic_attackupper) {
		this.magic_attackupper = magic_attackupper;
	}

	public int getMagic_attacklower() {
		return magic_attacklower;
	}

	public void setMagic_attacklower(int magic_attacklower) {
		this.magic_attacklower = magic_attacklower;
	}

	public int getPhysic_attackPercent() {
		return physic_attackPercent;
	}

	public void setPhysic_attackPercent(int physic_attackPercent) {
		this.physic_attackPercent = physic_attackPercent;
	}

	public int getMagic_attackPercent() {
		return magic_attackPercent;
	}

	public void setMagic_attackPercent(int magic_attackPercent) {
		this.magic_attackPercent = magic_attackPercent;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getVitality() {
		return vitality;
	}

	public void setVitality(int vitality) {
		this.vitality = vitality;
	}

	public int getAgil() {
		return agil;
	}

	public void setAgil(int agil) {
		this.agil = agil;
	}

	public int getIntelligence() {
		return intelligence;
	}

	public void setIntelligence(int intelligence) {
		this.intelligence = intelligence;
	}

	public int getIce_attack() {
		return ice_attack;
	}

	public void setIce_attack(int ice_attack) {
		this.ice_attack = ice_attack;
	}

	public int getRay_attack() {
		return ray_attack;
	}

	public void setRay_attack(int ray_attack) {
		this.ray_attack = ray_attack;
	}

	public int getPoison_attack() {
		return poison_attack;
	}

	public void setPoison_attack(int poison_attack) {
		this.poison_attack = poison_attack;
	}

	public int getIce_def() {
		return ice_def;
	}

	public void setIce_def(int ice_def) {
		this.ice_def = ice_def;
	}

	public int getRay_def() {
		return ray_def;
	}

	public void setRay_def(int ray_def) {
		this.ray_def = ray_def;
	}

	public int getPoison_def() {
		return poison_def;
	}

	public void setPoison_def(int poison_def) {
		this.poison_def = poison_def;
	}

	/**
	 * @return the knowing_attackPercent
	 */
	public double getKnowing_attackPercent() {
		return Knowing_attackPercent;
	}

	/**
	 * @param knowing_attackPercent
	 *            the knowing_attackPercent to set
	 */
	public void setKnowing_attackPercent(double knowing_attackPercent) {
		Knowing_attackPercent = knowing_attackPercent;
	}

	/**
	 * @return the ignore_attackPercent
	 */
	public double getIgnore_attackPercent() {
		return ignore_attackPercent;
	}

	/**
	 * @param ignore_attackPercent
	 *            the ignore_attackPercent to set
	 */
	public void setIgnore_attackPercent(double ignore_attackPercent) {
		this.ignore_attackPercent = ignore_attackPercent;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public int getCrit() {
		return crit;
	}

	public void setCrit(int crit) {
		this.crit = crit;
	}

	public int getDodge() {
		return dodge;
	}

	public void setDodge(int dodge) {
		this.dodge = dodge;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public int getMaxMp() {
		return maxMp;
	}

	public void setMaxMp(int maxMp) {
		this.maxMp = maxMp;
	}

	public int getMaxSp() {
		return maxSp;
	}

	public void setMaxSp(int maxSp) {
		this.maxSp = maxSp;
	}

	public int getAttackSpeed() {
		return attackSpeed;
	}

	public void setAttackSpeed(int attackSpeed) {
		this.attackSpeed = attackSpeed;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getLuck() {
		return luck;
	}

	public void setLuck(int luck) {
		this.luck = luck;
	}

	public int getAttackPercent() {
		return attackPercent;
	}

	public void setAttackPercent(int attackPercent) {
		this.attackPercent = attackPercent;
	}

	public int getDefensePercent() {
		return defensePercent;
	}

	public void setDefensePercent(int defensePercent) {
		this.defensePercent = defensePercent;
	}

	public int getCritPercent() {
		return critPercent;
	}

	public void setCritPercent(int critPercent) {
		this.critPercent = critPercent;
	}

	public int getDodgePercent() {
		return dodgePercent;
	}

	public void setDodgePercent(int dodgePercent) {
		this.dodgePercent = dodgePercent;
	}

	public int getMaxHpPercent() {
		return maxHpPercent;
	}

	public void setMaxHpPercent(int maxHpPercent) {
		this.maxHpPercent = maxHpPercent;
	}

	public int getMaxMpPercent() {
		return maxMpPercent;
	}

	public void setMaxMpPercent(int maxMpPercent) {
		this.maxMpPercent = maxMpPercent;
	}

	public int getMaxSpPercent() {
		return maxSpPercent;
	}

	public void setMaxSpPercent(int maxSpPercent) {
		this.maxSpPercent = maxSpPercent;
	}

	public int getAttackSpeedPercent() {
		return attackSpeedPercent;
	}

	public void setAttackSpeedPercent(int attackSpeedPercent) {
		this.attackSpeedPercent = attackSpeedPercent;
	}

	public int getSpeedPercent() {
		return speedPercent;
	}

	public void setSpeedPercent(int speedPercent) {
		this.speedPercent = speedPercent;
	}

	public int getLuckPercent() {
		return luckPercent;
	}

	public void setLuckPercent(int luckPercent) {
		this.luckPercent = luckPercent;
	}

	public int getAttackTotalPercent() {
		return attackTotalPercent;
	}

	public void setAttackTotalPercent(int attackTotalPercent) {
		this.attackTotalPercent = attackTotalPercent;
	}

	public int getDefenseTotalPercent() {
		return defenseTotalPercent;
	}

	public void setDefenseTotalPercent(int defenseTotalPercent) {
		this.defenseTotalPercent = defenseTotalPercent;
	}

	public int getCritTotalPercent() {
		return critTotalPercent;
	}

	public void setCritTotalPercent(int critTotalPercent) {
		this.critTotalPercent = critTotalPercent;
	}
    //
	public int gethitTotalPercent() {
		return hitTotalPercent;
	}

	public void sethitTotalPercent(int hitTotalPercent) {
		this.hitTotalPercent = hitTotalPercent;
	}
	
	
	public int getDodgeTotalPercent() {
		return dodgeTotalPercent;
	}

	public void setDodgeTotalPercent(int dodgeTotalPercent) {
		this.dodgeTotalPercent = dodgeTotalPercent;
	}

	public int getMaxHpTotalPercent() {
		return maxHpTotalPercent;
	}

	public void setMaxHpTotalPercent(int maxHpTotalPercent) {
		this.maxHpTotalPercent = maxHpTotalPercent;
	}

	public int getMaxMpTotalPercent() {
		return maxMpTotalPercent;
	}

	public void setMaxMpTotalPercent(int maxMpTotalPercent) {
		this.maxMpTotalPercent = maxMpTotalPercent;
	}

	public int getMaxSpTotalPercent() {
		return maxSpTotalPercent;
	}

	public void setMaxSpTotalPercent(int maxSpTotalPercent) {
		this.maxSpTotalPercent = maxSpTotalPercent;
	}

	public int getAttackSpeedTotalPercent() {
		return attackSpeedTotalPercent;
	}

	public void setAttackSpeedTotalPercent(int attackSpeedTotalPercent) {
		this.attackSpeedTotalPercent = attackSpeedTotalPercent;
	}

	public int getSpeedTotalPercent() {
		return speedTotalPercent;
	}

	public void setSpeedTotalPercent(int speedTotalPercent) {
		this.speedTotalPercent = speedTotalPercent;
	}

	public int getLuckTotalPercent() {
		return luckTotalPercent;
	}

	public void setLuckTotalPercent(int luckTotalPercent) {
		this.luckTotalPercent = luckTotalPercent;
	}

	public int getEquipAttackPercent() {
		return equipAttackPercent;
	}

	public void setEquipAttackPercent(int equipAttackPercent) {
		this.equipAttackPercent = equipAttackPercent;
	}

	public int getEquipDefensePercent() {
		return equipDefensePercent;
	}

	public void setEquipDefensePercent(int equipDefensePercent) {
		this.equipDefensePercent = equipDefensePercent;
	}

	public HashMap<Integer, Integer> getSkillLevelUp() {
		return skillLevelUp;
	}

	public void setSkillLevelUp(HashMap<Integer, Integer> skillLevelUp) {
		this.skillLevelUp = skillLevelUp;
	}

	public int getExpPercent() {
		return expPercent;
	}

	public void setExpPercent(int expPercent) {
		this.expPercent = expPercent;
	}

	public int getZhenQiPercent() {
		return zhenQiPercent;
	}

	public void setZhenQiPercent(int zhenQiPercent) {
		this.zhenQiPercent = zhenQiPercent;
	}

	public int getArrowProbability() {
		return arrowProbability;
	}

	public void setArrowProbability(int arrowProbability) {
		this.arrowProbability = arrowProbability;
	}

	public int getNegDefence() {
		return negDefence;
	}

	public void setNegDefence(int negDefence) {
		this.negDefence = negDefence;
	}

	public int getIgnore_defendPercent() {
		return ignore_defendPercent;
	}

	public void setIgnore_defendPercent(int ignore_defendPercent) {
		this.ignore_defendPercent = ignore_defendPercent;
	}


	/**
	 * @return the add_injure
	 */
	public double getAdd_injure() {
		return add_injure;
	}

	/**
	 * @param add_injure
	 *            the add_injure to set
	 */
	public void setAdd_injure(double add_injure) {
		this.add_injure = add_injure;
	}

	/**
	 * @return the reduce_injure
	 */
	public double getReduce_injure() {
		return reduce_injure;
	}

	/**
	 * @param reduce_injure
	 *            the reduce_injure to set
	 */
	public void setReduce_injure(double reduce_injure) {
		this.reduce_injure = reduce_injure;
	}

	public void add(PlayerAttribute other) {
		this.setAttack(this.getAttack() + other.getAttack());
		this.setDefense(this.getDefense() + other.getDefense());
		this.setCrit(this.getCrit() + other.getCrit());
		this.setDodge(this.getDodge() + other.getDodge());
		this.setMaxHp(this.getMaxHp() + other.getMaxHp());
		this.setMaxMp(this.getMaxMp() + other.getMaxMp());
		this.setMaxSp(this.getMaxSp() + other.getMaxSp());
		this.setAttackSpeed(this.getAttackSpeed() + other.getAttackSpeed());
		this.setSpeed(this.getSpeed() + other.getSpeed());
		this.setLuck(this.getLuck() + other.getLuck());
		this.setAttackPercent(this.getAttackPercent()
				+ other.getAttackPercent());
		this.setDefensePercent(this.getDefensePercent()
				+ other.getDefensePercent());
		this.setCritPercent(this.getCritPercent() + other.getCritPercent());
		this.setDodgePercent(this.getDodgePercent() + other.getDodgePercent());
		this.setMaxHpPercent(this.getMaxHpPercent() + other.getMaxHpPercent());
		this.setMaxMpPercent(this.getMaxMpPercent() + other.getMaxMpPercent());
		this.setMaxSpPercent(this.getMaxSpPercent() + other.getMaxSpPercent());
		this.setAttackSpeedPercent(this.getAttackSpeedPercent()
				+ other.getAttackSpeedPercent());
		this.setSpeedPercent(this.getSpeedPercent() + other.getSpeedPercent());
		this.setLuckPercent(this.getLuckPercent() + other.getLuckPercent());
		this.setAttackTotalPercent(this.getAttackSpeedTotalPercent()
				+ other.getAttackSpeedTotalPercent());
		this.setDefenseTotalPercent(this.getDefenseTotalPercent()
				+ other.getDefenseTotalPercent());
		this.setCritTotalPercent(this.getCritTotalPercent()
				+ other.getCritTotalPercent());
		this.setDodgeTotalPercent(this.getDodgeTotalPercent()
				+ other.getDodgeTotalPercent());
		this.setMaxHpTotalPercent(this.getMaxHpTotalPercent()
				+ other.getMaxHpTotalPercent());
		this.setMaxMpTotalPercent(this.getMaxMpTotalPercent()
				+ other.getMaxMpTotalPercent());
		this.setMaxSpTotalPercent(this.getMaxSpTotalPercent()
				+ other.getMaxSpTotalPercent());
		this.setAttackSpeedTotalPercent(this.getAttackSpeedTotalPercent()
				+ other.getAttackSpeedTotalPercent());
		this.setSpeedTotalPercent(this.getSpeedTotalPercent()
				+ other.getSpeedTotalPercent());
		this.setLuckTotalPercent(this.getLuckTotalPercent()
				+ other.getLuckTotalPercent());
		this.setEquipAttackPercent(this.getEquipAttackPercent()
				+ other.getEquipAttackPercent());
		this.setEquipDefensePercent(this.getEquipDefensePercent()
				+ other.getEquipDefensePercent());
		this.setExpPercent(this.getExpPercent() + other.getExpPercent());
		this.setZhenQiPercent(this.getZhenQiPercent()
				+ other.getZhenQiPercent());
		this.setArrowProbability(this.getArrowProbability()
				+ other.getArrowProbability());
		this.setNegDefence(this.getNegDefence() + other.getNegDefence());

		Iterator<Entry<Integer, Integer>> iter = other.getSkillLevelUp()
				.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<java.lang.Integer, java.lang.Integer> entry = (Map.Entry<java.lang.Integer, java.lang.Integer>) iter
					.next();
			Integer level = getSkillLevelUp().get(entry.getKey());
			if (level == null) {
				getSkillLevelUp().put(entry.getKey(), entry.getValue());
			} else {
				getSkillLevelUp().put(entry.getKey(), entry.getValue() + level);
			}
		}
	}

	public static PlayerAttribute getPlayerAttribute(Q_buffBean buffBean) {
		PlayerAttribute patt = new PlayerAttribute();
		@SuppressWarnings("unchecked")
		HashMap<String, Integer> powers = (HashMap<String, Integer>) JSONserializable
				.toObject(StringUtil.formatToJson(buffBean.getQ_Bonus_skill()),
						HashMap.class);
		if (powers != null) {
			for (Map.Entry<String, Integer> entry : powers.entrySet()) {
				String attString = entry.getKey();
				Integer attInteger = entry.getValue();
				if (attString.equalsIgnoreCase(CommonString.ATTACK)) {
					patt.setAttack(attInteger);
				} else if (attString
						.equalsIgnoreCase(CommonString.ATTACKPERCENT)) {
					patt.setAttackPercent(attInteger);
				} else if (attString.equalsIgnoreCase(CommonString.DEFENSE)) {
					patt.setDefense(attInteger);
				} else if (attString
						.equalsIgnoreCase(CommonString.DEFENSEPERCENT)) {
					patt.setDefensePercent(attInteger);
				} else if (attString.equalsIgnoreCase(CommonString.CRIT)) {
					patt.setCrit(attInteger);
				} else if (attString.equalsIgnoreCase(CommonString.CRITPERCENT)) {
					patt.setCritPercent(attInteger);
				} else if (attString.equalsIgnoreCase(CommonString.DODGE)) {
					patt.setDodge(attInteger);
				} else if (attString
						.equalsIgnoreCase(CommonString.DODGEPERCENT)) {
					patt.setDodgePercent(attInteger);
				} else if (attString.equalsIgnoreCase(CommonString.MAXHP)) {
					patt.setMaxHp(attInteger);
				} else if (attString
						.equalsIgnoreCase(CommonString.MAXHPPERCENT)) {
					patt.setMaxHpPercent(attInteger);
				} else if (attString.equalsIgnoreCase(CommonString.MAXMP)) {
					patt.setMaxMp(attInteger);
				} else if (attString
						.equalsIgnoreCase(CommonString.MAXMPPERCENT)) {
					patt.setMaxMpPercent(attInteger);
				} else if (attString.equalsIgnoreCase(CommonString.MAXSP)) {
					patt.setMaxSp(attInteger);
				} else if (attString
						.equalsIgnoreCase(CommonString.MAXSPPERCENT)) {
					patt.setMaxSpPercent(attInteger);
				} else if (attString.equalsIgnoreCase(CommonString.ATTACKSPEED)) {
					patt.setAttackSpeed(attInteger);
				} else if (attString
						.equalsIgnoreCase(CommonString.ATTACKSPEEDPERCENT)) {
					patt.setAttackSpeedPercent(attInteger);
				} else if (attString.equalsIgnoreCase(CommonString.SPEED)) {
					patt.setSpeed(attInteger);
				} else if (attString
						.equalsIgnoreCase(CommonString.SPEEDPERCENT)) {
					patt.setSpeedPercent(attInteger);
				} else if (attString.equalsIgnoreCase(CommonString.LUCK)) {
					patt.setLuck(attInteger);
				} else if (attString.equalsIgnoreCase(CommonString.LUCKPERCENT)) {
					patt.setLuckPercent(attInteger);
				}
			}
		}
		return patt;
	}
}
