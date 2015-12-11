package com.game.player.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.game.arrow.manager.ArrowAttributeCalculator;
import com.game.attribute.ActivateAttributeCalculator;
import com.game.backpack.manager.BackpackAttributeCalculator;
import com.game.backpack.structs.Equip;
import com.game.buff.manager.BuffAttributeCalculator;
import com.game.collect.manager.CollectAttributeCalculator;
import com.game.data.bean.Q_hiddenweapon_skillBean;
import com.game.data.bean.Q_itemBean;
import com.game.data.bean.Q_skill_modelBean;
import com.game.data.manager.DataManager;
import com.game.equip.manager.EquipAttributeCalculator;
import com.game.fight.structs.FighterState;
import com.game.fightpower.manager.FightPowerManager;
import com.game.gem.manager.GemAttributeCalculator;
import com.game.guild.manager.GuildBannerAttributeCalculator;
import com.game.hiddenweapon.manager.HiddenWeaponAttributeCalculator;
import com.game.hiddenweapon.manager.HiddenWeaponManager;
import com.game.hiddenweapon.structs.HiddenWeapon;
import com.game.horse.manager.HorseAttributeCalculator;
import com.game.horse.manager.HorseEquipAttributeCalculator;
import com.game.horseweapon.manager.HorseWeaponAttributeCalculator;
import com.game.longyuan.manager.LongYuanAttributeCalculator;
import com.game.lostskills.struts.LostSkillAttributeCalculator;
import com.game.manager.ManagerPool;
import com.game.marriage.manager.MarriageAttributeCalculator;
import com.game.pet.attribute.PetAttributeCaluclator;
import com.game.player.bean.PlayerAttributeItem;
import com.game.player.message.ResChangeOneAttributeMessage;
import com.game.player.message.ResPlayerAttributeChangeMessage;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttribute;
import com.game.player.structs.PlayerAttributeCalculator;
import com.game.player.structs.PlayerAttributeType;
import com.game.rank.manager.RankAttributeCalculator;
import com.game.realm.manager.RealmAttributeCalculator;
import com.game.skill.structs.Skill;
import com.game.store.manager.StoreAttributeCalculator;
import com.game.structs.Attributes;
import com.game.task.manager.ChapterAdditionAttributeCalculator;
import com.game.team.manager.TeamAttributeCalculator;
import com.game.toplist.manager.TopTitleAttributeCalculator;
import com.game.utils.Global;
import com.game.utils.MessageUtil;
import com.game.vip.manager.VIPAttributeCalculator;

public class PlayerAttributeManager {

	protected Logger log = Logger.getLogger(PlayerAttributeManager.class);
	private static Object obj = new Object();
	//玩家管理类实例
	private static PlayerAttributeManager manager;
	//属性计算器
	private static HashMap<Integer, PlayerAttributeCalculator> calculators = new HashMap<Integer, PlayerAttributeCalculator>();

	private PlayerAttributeManager() {
		//注册基本属性计算器
		registerAttributeCalculator(new BaseAttributeCalculator());
		//注册装备属性计算器
		registerAttributeCalculator(new EquipAttributeCalculator());
		//注册buff属性计算器
		registerAttributeCalculator(new BuffAttributeCalculator());
		//注册背包属性计算器
		registerAttributeCalculator(new BackpackAttributeCalculator());
		//注册仓库属性计算器  
		registerAttributeCalculator(new StoreAttributeCalculator());
		//注册天元心法属性计算器   (6)
		registerAttributeCalculator(new LongYuanAttributeCalculator());
		//注册军衔属性计算器   (7)
		registerAttributeCalculator(new RankAttributeCalculator());
		//坐骑属性计算器   （8）
		registerAttributeCalculator(new HorseAttributeCalculator());
		//注册组队属性计算器   (10)
		registerAttributeCalculator(new TeamAttributeCalculator());
		//注册坐骑装备属性计算器   (11)
		registerAttributeCalculator(new HorseEquipAttributeCalculator());
		//注册宝石属性计算器   (13)
		registerAttributeCalculator(new GemAttributeCalculator());
		//注册盟旗属性计算器   (14)
		registerAttributeCalculator(new GuildBannerAttributeCalculator());
		//注册称号属性计算器   (15)
		registerAttributeCalculator(new TopTitleAttributeCalculator());
		//注册弓箭属性计算器   (16)
		registerAttributeCalculator(new ArrowAttributeCalculator());
		//注册骑战兵器属性计算器   (17)
		registerAttributeCalculator(new HorseWeaponAttributeCalculator());
		//注册境界属性计算器   (18)
		registerAttributeCalculator(new RealmAttributeCalculator());
		//大秦典藏加成  (19)
		registerAttributeCalculator(new CollectAttributeCalculator());
		//注册骑战兵器属性计算器   (20)
		registerAttributeCalculator(new HiddenWeaponAttributeCalculator());
		registerAttributeCalculator(new ChapterAdditionAttributeCalculator());
		//结婚属性计算器（20）
		registerAttributeCalculator(new MarriageAttributeCalculator());
		//注册其他属性计算器   (100)
		registerAttributeCalculator(new OtherAttributeCalculator());
		//美人合体属性
		registerAttributeCalculator(new PetAttributeCaluclator());
		//套装计算器

		// vip属性计算
		registerAttributeCalculator(new VIPAttributeCalculator());
		
		//遗落技能属性计算
		registerAttributeCalculator(new LostSkillAttributeCalculator());
		
		//卓越激活属性
		registerAttributeCalculator(new ActivateAttributeCalculator());
	}

	private void registerAttributeCalculator(PlayerAttributeCalculator calculator) {
		calculators.put(calculator.getType(), calculator);
	}

	public static PlayerAttributeManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new PlayerAttributeManager();
			}
		}
		return manager;
	}

	/**
	 * 初始化玩家属性
	 *
	 * @param player
	 */
	public void initPlayerAttribute(Player player) {
		Iterator<PlayerAttributeCalculator> iter = calculators.values().iterator();
		while (iter.hasNext()) {
			PlayerAttributeCalculator playerAttributeCalculator = (PlayerAttributeCalculator) iter.next();
			player.getAttributes().put(playerAttributeCalculator.getType(), playerAttributeCalculator.getPlayerAttribute(player));
		}
		//showAttributes(player);
		PlayerInfo info = new PlayerInfo();
		info.copyPlayer(player);
		setAttribute(player, true);
		info.comparePlayer(player, 0, 0);
	}

	/**
	 * 计算玩家属性
	 *
	 * @param player
	 * @param type 类型
	 */
	public void countPlayerAttribute(Player player, int type) {
		countPlayerAttribute(player, type, 0);
	}

	/**
	 * 计算玩家属性
	 *
	 * @param player
	 * @param type 类型
	 */
	public void countPlayerAttribute(Player player, int type, int modelId) {	
		PlayerAttributeCalculator playerAttributeCalculator = calculators.get(type);
		if (!player.getAttributes().containsKey(playerAttributeCalculator.getType())) {
			return;
		}
		if (type == PlayerAttributeType.BASE) {
			// 四大属性,需要先把总的四大属性计算完毕才能计算base模块
			player.getAttibute_one()[0] = countAttributeStrength(player);
			player.getAttibute_one()[1] = countAttributeVitality(player);
			player.getAttibute_one()[2] = countAttributeAgil(player);
			player.getAttibute_one()[3] = countAttributeIntelligence(player);
			player.getAttibute_one()[4] = player.getAttibute_one_base()[4];
			ResChangeOneAttributeMessage endmsg = new ResChangeOneAttributeMessage();
			endmsg.setEndValue(1);
			endmsg.setStrength(player.getAttibute_one()[0]);
			endmsg.setVitality(player.getAttibute_one()[1]);
			endmsg.setAgile(player.getAttibute_one()[2]);
			endmsg.setIntelligence(player.getAttibute_one()[3]);
			endmsg.setRestPlusPoint(player.getAttibute_one()[4]);
			MessageUtil.tell_player_message(player, endmsg);
			// 如果改变的是四大属性，那么要检测玩家身上的装备是否还满足穿戴条件
			checkEquipCondition(player);
		}
		player.getAttributes().put(playerAttributeCalculator.getType(), playerAttributeCalculator.getPlayerAttribute(player));
		
		if (type == PlayerAttributeType.BUFF) {
			// 四大属性，buff会影响四大属性，所以要通知base重新计算
			boolean isChange = false;
			int strength = countAttributeStrength(player);
			if(player.getAttibute_one()[0] != strength) {
				isChange = true;
				player.getAttibute_one()[0] = strength;
			}
			int vitality = countAttributeVitality(player);
			if(player.getAttibute_one()[1] != vitality) {
				isChange = true;
				player.getAttibute_one()[1] = vitality;
			}
			int agil = countAttributeAgil(player);
			if(player.getAttibute_one()[2] != agil) {
				isChange = true;
				player.getAttibute_one()[2] = agil;
			}
			int intelligence = countAttributeIntelligence(player);
			if(player.getAttibute_one()[3] != intelligence) {
				isChange = true;
				player.getAttibute_one()[3] = intelligence;
			}
			if(isChange) {
				player.getAttibute_one()[4] = player.getAttibute_one_base()[4];
				countPlayerAttribute(player, PlayerAttributeType.BASE, 0);
				return;
			}
		}
		//showAttributes(player);
		PlayerInfo info = new PlayerInfo();
		info.copyPlayer(player);
		//modify by luminghua
		//现在buff也会影响战斗力了
//		setAttribute(player, (type != PlayerAttributeType.BUFF) ? true : false);
		setAttribute(player, true);
		info.comparePlayer(player, type, modelId);
	}

	private void checkEquipCondition(Player player) {
		boolean change = false;
		int length = player.getAttibute_one()[0];
		int vitality = player.getAttibute_one()[1];
		int agile = player.getAttibute_one()[2];
		int intelligence = player.getAttibute_one()[3];
		for (int i = 0; i < player.getEquips().length; i++) {
			Equip equip = player.getEquips()[i];
			if (equip != null) {
				Q_itemBean model = DataManager.getInstance().q_itemContainer.getMap().get(equip.getItemModelId());
				if (equip.isCanUse()) {
					// 本身可用变为不可用
					if (length < model.getQ_str_limit() || vitality < model.getQ_vit_limit() || agile < model.getQ_dex_limit() || intelligence < model.getQ_int_limit()) {
						equip.setCanUse(false);
						change = true;
					}
				} else {
					// 不可用变为可用
					if (length >= model.getQ_str_limit() && vitality >= model.getQ_vit_limit() && agile >= model.getQ_dex_limit() && intelligence >= model.getQ_int_limit()) {
						equip.setCanUse(true);
						change = true;
					}
				}
			}
		}
		if (change) {
			// 如果装备是否满足穿戴条件的状态改变了，重新计算装备加成
			this.countPlayerAttribute(player, PlayerAttributeType.EQUIP);
		}
	}
	
	/**
	 * 计算玩家属性(上马的特殊处理)
	 *
	 * @param player
	 * @param type 类型
	 */
	public void countPlayerSpecailAttribute(Player player, int type, int modelId) {
		PlayerInfo info = new PlayerInfo();
		info.copyPlayer(player);
		player.setSpeed(countSpeed(player));
		//
		List<PlayerAttributeItem> list = new ArrayList<PlayerAttributeItem>();
		PlayerAttributeItem attribute = new PlayerAttributeItem();
		attribute.setType(Attributes.SPEED.getValue());
		attribute.setValue(player.getSpeed());
		list.add(attribute);
		ResPlayerAttributeChangeMessage msg = new ResPlayerAttributeChangeMessage();
		msg.setAttributeChangeReason(type);
		msg.setModelId(modelId);
		msg.setAttributeChangeList(list);
		MessageUtil.tell_player_message(player, msg);
	}
	
	
	/**
	 * 获取玩家属性
	 *
	 * @param player
	 * @param type 类型
	 */
	private PlayerAttribute getAttribute(Player player, int type) {
		return player.getAttributes().get(type);
	}

	/**
	 * 设置玩家属性
	 *
	 * @param player
	 */
	private void setAttribute(Player player, boolean bosendFightPower) {
		//player.setAttack(countAttack(player));
		//
		player.setPhysic_attackupper(countPhysicMagicAttack(player, 1));
		player.setPhysic_attacklower(countPhysicMagicAttack(player, 2));
		player.setMagic_attackupper(countPhysicMagicAttack(player, 3));
		player.setMagic_attacklower(countPhysicMagicAttack(player, 4));
		//
		player.setDefense(countDefense(player));
		//player.setCrit(countCrit(player));
		//
		player.setHit(countHit(player));
		
		player.setKnowing_attackPercent(countZhuoYue(player, Global.KNOWING_ATTACKPERCENT));
		player.setIgnore_attackPercent(countZhuoYue(player, Global.IGNORE_ATTACKPERCENT));
		player.setPerfect_attackPercent(countZhuoYue(player, Global.PERFECT_ATTACKPERCENT));
		player.setPerfect_addattackPercent(countPerfect_addAttackPercent(player));
		player.setKnowing_addattackPercent(countKnowing_addAttackPercent(player));

		player.setIce_def(counticelayposion(player, Global.Ice_def));
		player.setRay_def(counticelayposion(player, Global.Ray_def));
		player.setPoison_def(counticelayposion(player, Global.Poison_def));
		
		player.setignore_defendPercent(countIgnore_defendPercent(player));
		player.setignore_defend_add_Percent(countIgnore_defend_add_Percent(player));

		// 增加伤害
		player.setAddInjure(countAddInjure(player));
		// 吸收伤害
		player.setReduceInjure(countReduceInjure(player));
		
		//
		player.setDodge(countDodge(player));
		player.setMaxHp(countMaxHp(player));
		player.setMaxMp(countMaxMp(player));
		player.setMaxSp(countMaxSp(player));
		player.setAttackSpeed(countAttackSpeed(player));
		player.setSpeed(countSpeed(player));
		player.setLuck(countLuck(player));
		player.setExpMultiple(countExpMultiple(player));
		player.setSkillLevelUp(countSkillLevelUp(player));
		player.setZhenQiMultiple(countZhenQiMultiple(player));
		player.setNegDefence(countnegDefence(player));
		player.setArrowProbability(countarrowProbability(player));
		// 回血
		player.setHp_recover(countHpRecover(player));
		// 伤害反射
		player.setRebound_damage(countReboundDamage(player));
		// 杀怪增加金币
		player.setAddmoney_whenkill(countAddMoneyWhenKill(player));
		// 杀怪加血
		player.setAddhp_whenkill(countAddHpWhenKill(player));
		// 杀怪加魔
		player.setAddmp_whenkill(countAddMpWhenKill(player));
		/*
		//TODO oyk edit for test 
		player.setIce_attack(88);
		player.setIce_def(99);
		player.setPoison_def(89);
		player.setPhysic_attacklower(100);
		player.setPhysic_attackupper(200);
		*/
		if (bosendFightPower) {
			FightPowerManager.getInstance().Update(player);
		}
	}

	/**
	 * 计算忽视对方 defen(1-%)的概率
	 *
	 * @param player
	 * @return
	 */
	public int countIgnore_defendPercent(Player player) {
		double Value = 0;
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		
		//遗落 技能
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		
		Value = (double)(horse.getIgnore_defendPercent() + lostSkill.getIgnore_defendPercent());
		return  (int)(Value);
	}
	
	/**
	 * 计算忽视对方 defen(1-%)的概率
	 *
	 * @param player
	 * @return
	 */
	public int countIgnore_defend_add_Percent(Player player) {
		double Value = 0;
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		
		//遗落技能
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		
		Value = (double)(horse.getIgnore_defend_add_Percent() + lostSkill.getIgnore_defend_add_Percent());
		return  (int)(Value);
	}
	
	/**
	 * 计算冰雷毒的防
	 *
	 * @param player
	 * @return
	 */
	public int counticelayposion(Player player, int type) {
		
		//获取基本加成
//		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
//		//Buff加成
//		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//遗落技能
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		
//		//坐骑加成
//		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
//		//其他加成 比如药水
//		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		//double baseValue = 0;
		double equipValue = 0;
		//double otherValue = 0;
		if(type == Global.Ice_def){ //
			 equipValue = (double)(equip.getIce_def() + lostSkill.getIce_def());
			 return  (int)(equipValue);
		}else if(type == Global.Ray_def){ //
			 equipValue = (double)(equip.getRay_def() + lostSkill.getRay_def());
			 return  (int)(equipValue);
		}
		else if(type == Global.Poison_def){//
			 equipValue = (double)(equip.getPoison_def() + lostSkill.getPoison_def());
			 return  (int)(equipValue);
		}else{
			return 0;
		}
		
		
	}
	
	/*
	 * 
	 */
	public int countPerfect_addAttackPercent(Player player) {
		//获取基本加成
		// PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//装备加成
		// PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//其他加成 比如药水
		// PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		//
		// double baseValue = 0;
		// double equipValue = 0;
		// double otherValue = 0;
		
		//遗落技能
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);

		return horse.getPerfectatk_addpercent() + buff.getPerfectatk_addpercent() + lostSkill.getPerfectatk_addpercent();

		// baseValue = (double)base.getPerfectatk_addpercent() + buff.getPerfectatk_addpercent();
		// equipValue = (double)equip.getPerfectatk_addpercent();
		// otherValue = horse.getPerfectatk_addpercent();
		// return (int)((baseValue + equipValue + otherValue) * (10000 + equip.getPerfectatk_addpercent() +buff.getPerfectatk_addpercent()) / 10000);

		//buff.getAttackPercent() + taskChapter.getAttackPercent() + banner.getAttackPercent()
		//buff.getEquipAttackPercent() + taskChapter.getEquipAttackPercent()
		//buff.getAttackTotalPercent()
	}

	public int countKnowing_addAttackPercent(Player player) {
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//遗落技能
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		
		return buff.getKnowingatk_addpercent() + lostSkill.getKnowingatk_addpercent();

	}
	/**
	 * 计算卓越属性
	 *
	 * @param player
	 * @return
	 */
	public int countZhuoYue(Player player, int type) {
		
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
//		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
//		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//遗落技能
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
//		//其他加成 比如药水
//		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);

		if (type == Global.PERFECT_ATTACKPERCENT) { // 卓越一击
			return (int) ((base.getPerfect_attack() + buff.getPerfect_attack() + equip.getPerfect_attack() + horse.getPerfect_attack() + lostSkill.getPerfect_attack())
					* (Global.MAX_PROBABILITY + equip.getPerfect_attackPercent()) / Global.MAX_PROBABILITY);
		} else if (type == Global.KNOWING_ATTACKPERCENT) { // 会心一击，如果有多个会心一击触发几率,则使用相乘运算
			int knowingAttackValue = (int) ((double) (1 - (equip.getKnowing_attackPercent() * buff.getKnowing_attackPercent() * lostSkill.getKnowing_attackPercent())) * Global.MAX_PROBABILITY);
			return knowingAttackValue;
		} else if (type == Global.IGNORE_ATTACKPERCENT) {// 无视一击，如果有多个无视一击触发几率,则使用相乘运算
			int ignoreAttackValue = (int) ((double) (1 - (equip.getIgnore_attackPercent() * buff.getIgnore_attackPercent() * lostSkill.getIgnore_attackPercent())) * Global.MAX_PROBABILITY);
			return ignoreAttackValue;
		}else{
			return 0;
		}
		
		//原有
//		if (type == Global.PERFECT_ATTACKPERCENT) { // 卓越一击
//			return (int) ((base.getPerfect_attack() + buff.getPerfect_attack() + equip.getPerfect_attack() + horse.getPerfect_attack())
//					* (Global.MAX_PROBABILITY + equip.getPerfect_attackPercent()) / Global.MAX_PROBABILITY);
//		} else if (type == Global.KNOWING_ATTACKPERCENT) { // 会心一击，如果有多个会心一击触发几率,则使用相乘运算
//			int knowingAttackValue = (int) ((double) (1 - (equip.getKnowing_attackPercent() * buff.getKnowing_attackPercent())) * Global.MAX_PROBABILITY);
//			return knowingAttackValue;
//		} else if (type == Global.IGNORE_ATTACKPERCENT) {// 无视一击，如果有多个无视一击触发几率,则使用相乘运算
//			int ignoreAttackValue = (int) ((double) (1 - (equip.getIgnore_attackPercent() * buff.getIgnore_attackPercent())) * Global.MAX_PROBABILITY);
//			return ignoreAttackValue;
//		}else{
//			return 0;
//		}
	}

	public int countAddInjure(Player player) {

		// 获取基本加成
		// PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		// //Buff加成
		// PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		// 装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		// //坐骑加成
		// PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		// //其他加成 比如药水
		// PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		return (int) (equip.getAdd_injure()* Global.MAX_PROBABILITY);//不能去掉括号
	}

	public int countReduceInjure(Player player) {

		// 获取基本加成
		// PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
	    //Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		// 装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		// //坐骑加成
		// PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		// //其他加成 比如药水
		// PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		return (int) (equip.getReduce_injure() * buff.getReduce_injure() * Global.MAX_PROBABILITY );
	}
	/**
	 * 攻击力计算
	 *
	 * @param player
	 * @param skill 技能
	 * @return
	 */
	public int countAttack(Player player, Skill skill) {
		//攻击清0 Buff
		if (FighterState.GONGJIWEILING.compare(player.getFightState())) {
			return 0;
		}
		
		//暗器基本技能,只计算暗器伤害
		Q_hiddenweapon_skillBean hiddenweaponSkillBean = ManagerPool.dataManager.q_hiddenweapon_skillContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
		if (hiddenweaponSkillBean != null) {
			HiddenWeapon hiddenWeapon = HiddenWeaponManager.getInstance().getHiddenWeapon(player);
			if (hiddenWeapon == null) {
				return 0;
			}
			PlayerAttribute attr = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
			return attr.getAttack();
		}
		
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//大秦典藏加成
		PlayerAttribute collect = getAttribute(player, PlayerAttributeType.COLLECT);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//天元心法加成
		PlayerAttribute tianyuan = getAttribute(player, PlayerAttributeType.LONGYUAN);
		//军衔属性加成
		PlayerAttribute rank = getAttribute(player, PlayerAttributeType.RANK);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//坐骑装备加成
		PlayerAttribute horseequip = getAttribute(player, PlayerAttributeType.HORSE_EQUIP);

		//宝石属性加成
		PlayerAttribute gem = getAttribute(player, PlayerAttributeType.GEM);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		//弓箭属性加成
		PlayerAttribute arrow = getAttribute(player, PlayerAttributeType.ARROW);

		PlayerAttribute taskChapter = getAttribute(player, PlayerAttributeType.TASK_CHATPER);
		//其他加成
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		PlayerAttribute pet = getAttribute(player, PlayerAttributeType.PET);
		//境界加成
		PlayerAttribute realm = getAttribute(player, PlayerAttributeType.REALM);
		PlayerAttribute horseWeapon = getAttribute(player, PlayerAttributeType.HORSE_WEAPON);
		PlayerAttribute hiddenWeapon = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
		//结婚属性加成
		PlayerAttribute marriageatt = getAttribute(player, PlayerAttributeType.MARRIAGE);
		//遗落技能属性加成
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		
		
		//技能加成
		int skillValue = 0;
		if (skill != null) {
			Q_skill_modelBean skillModel = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getRealLevel(player));
//			Q_skill_modelBean skillModel= ManagerPool.dataManager.q_skill_modelContainer.getMap().get(SkillManager.skillKey(player, skill.getSkillModelId()));
			skillValue = skillModel.getQ_attack_addition();
			//武功境界加成
			/*
			 * TODO 武功境界加成效果暂时取消 Q_skill_realmBean realm =
			 * ManagerPool.skillManager.getSkillRealm(player);
			 * if(realm!=null){ skillValue = skillValue *
			 * (Global.MAX_PROBABILITY + realm.getQ_jiache_ratio())
			 * / Global.MAX_PROBABILITY; }
			 */
		}

		double baseValue = ((double)base.getAttack() + skillValue + buff.getAttack() + taskChapter.getAttack() + tianyuan.getAttack() + banner.getAttack())
				* (10000 + buff.getAttackPercent() + taskChapter.getAttackPercent() + banner.getAttackPercent()) / 10000;
		double equipValue = ((double)equip.getAttack() + horseequip.getAttack() + gem.getAttack())
				* (10000 + buff.getEquipAttackPercent() + taskChapter.getEquipAttackPercent()) / 10000;
		double otherValue = lostSkill.getAttack() + rank.getAttack() + other.getAttack() + horse.getAttack() + pet.getAttack() + arrow.getAttack() + horseWeapon.getAttack() + realm.getAttack() + collect.getAttack() + marriageatt.getAttack() + hiddenWeapon.getAttack();
		return (int)((baseValue + equipValue + otherValue) * (10000 + buff.getAttackTotalPercent()) / 10000);
	}

	/**
	 * 攻击力计算 PhysicMagicAttack
	 *
	 * @param player
	 * @param skill 技能
	 * @return
	 */
	public int countPhysicMagicAttack(Player player, int attacktype) {
		//攻击清0 Buff
		if (FighterState.GONGJIWEILING.compare(player.getFightState())) {
			return 0;
		}
		
		/*
		//暗器基本技能,只计算暗器伤害
		Q_hiddenweapon_skillBean hiddenweaponSkillBean = ManagerPool.dataManager.q_hiddenweapon_skillContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
		if (hiddenweaponSkillBean != null) {
			HiddenWeapon hiddenWeapon = HiddenWeaponManager.getInstance().getHiddenWeapon(player);
			if (hiddenWeapon == null) {
				return 0;
			}
			PlayerAttribute attr = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
			return attr.getAttack();
		}
		*/
		
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//其他加成 比如药水
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		// vip加成
		PlayerAttribute vip = getAttribute(player, PlayerAttributeType.VIP);
		// 遗落技能夹层
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		// 激活卓越属性加成
		PlayerAttribute activation = getAttribute(player, PlayerAttributeType.ACTIVATION);
		/*
		//大秦典藏加成
		PlayerAttribute collect = getAttribute(player, PlayerAttributeType.COLLECT);
		//天元心法加成
		PlayerAttribute tianyuan = getAttribute(player, PlayerAttributeType.LONGYUAN);
		//军衔属性加成
		PlayerAttribute rank = getAttribute(player, PlayerAttributeType.RANK);
		//坐骑装备加成
		PlayerAttribute horseequip = getAttribute(player, PlayerAttributeType.HORSE_EQUIP);
		//宝石属性加成
		PlayerAttribute gem = getAttribute(player, PlayerAttributeType.GEM);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		//弓箭属性加成
		PlayerAttribute arrow = getAttribute(player, PlayerAttributeType.ARROW);
		PlayerAttribute taskChapter = getAttribute(player, PlayerAttributeType.TASK_CHATPER);
		
		PlayerAttribute pet = getAttribute(player, PlayerAttributeType.PET);
		//境界加成
		PlayerAttribute realm = getAttribute(player, PlayerAttributeType.REALM);
		PlayerAttribute horseWeapon = getAttribute(player, PlayerAttributeType.HORSE_WEAPON);
		PlayerAttribute hiddenWeapon = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
		//结婚属性加成
		PlayerAttribute marriageatt = getAttribute(player, PlayerAttributeType.MARRIAGE);
		*/
		double baseValue = 0;
		double equipValue = 0;
		double otherValue = 0;
		if(attacktype == 1){
			baseValue = (double) base.getPhysic_attackupper() + buff.getAttack() + banner.getPhysic_attackupper() + vip.getPhysic_attackupper() + lostSkill.getPhysic_attackupper()+activation.getPhysic_attackupper();
			 equipValue = (double)equip.getPhysic_attackupper();
			 otherValue = horse.getPhysic_attackupper()+other.getPhysic_attackupper();
			return (int) ((baseValue + equipValue + otherValue)
					* (10000 + equip.getPhysic_attackPercent() + buff.getAttackPercent() + banner.getPhysic_attackPercent() + vip.getPhysic_attackPercent()) + lostSkill.getPhysic_attackPercent()+activation.getPhysic_attackPercent()) / 10000;
		}else if(attacktype == 2){
			baseValue = (double) base.getPhysic_attacklower() + buff.getAttack() + banner.getPhysic_attacklower() + vip.getPhysic_attacklower() + lostSkill.getPhysic_attacklower()+activation.getPhysic_attacklower();
			 equipValue = (double)equip.getPhysic_attacklower();
			 otherValue =horse.getPhysic_attacklower()+other.getPhysic_attacklower();
			return (int) ((baseValue + equipValue + otherValue)
					* (10000 + equip.getPhysic_attackPercent() + buff.getAttackPercent() + banner.getPhysic_attackPercent() + vip.getPhysic_attackPercent()) + lostSkill.getPhysic_attackPercent()+activation.getPhysic_attackPercent()) / 10000;
		}else if(attacktype == 3){
			baseValue = (double) base.getMagic_attackupper() + buff.getAttack() + banner.getMagic_attackupper() + vip.getMagic_attackupper() + lostSkill.getMagic_attackupper()+activation.getMagic_attackupper();
			 equipValue = (double)equip.getMagic_attackupper();
			 otherValue =  horse.getMagic_attackupper()+other.getMagic_attackupper();
			return (int) ((baseValue + equipValue + otherValue)
					* (10000 + equip.getMagic_attackPercent() + buff.getAttackPercent() + banner.getMagic_attackPercent() + vip.getMagic_attackPercent() + lostSkill.getMagic_attackPercent()+activation.getMagic_attackPercent()) / 10000);
		}else{
			baseValue = (double) base.getMagic_attacklower() + buff.getAttack() + banner.getMagic_attacklower() + vip.getMagic_attacklower() + lostSkill.getMagic_attacklower()+activation.getMagic_attacklower();
			 equipValue = (double)equip.getMagic_attacklower();
			 otherValue =  horse.getMagic_attacklower()+other.getMagic_attacklower();
			return (int) ((baseValue + equipValue + otherValue)
					* (10000 + equip.getMagic_attackPercent() + buff.getAttackPercent() + banner.getMagic_attackPercent() + vip.getMagic_attackPercent() + lostSkill.getMagic_attackPercent()+activation.getMagic_attackPercent()) / 10000);
		}
		
		//buff.getAttackPercent() + taskChapter.getAttackPercent() + banner.getAttackPercent()
		//buff.getEquipAttackPercent() + taskChapter.getEquipAttackPercent()
		//buff.getAttackTotalPercent()
		
	}
	
	public int countHit(Player player) {
		//攻击清0 Buff
//		if (FighterState.GONGJIWEILING.compare(player.getFightState())) {
//			return 0;
//		}
//		
		/*
		//暗器基本技能,只计算暗器伤害
		Q_hiddenweapon_skillBean hiddenweaponSkillBean = ManagerPool.dataManager.q_hiddenweapon_skillContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
		if (hiddenweaponSkillBean != null) {
			HiddenWeapon hiddenWeapon = HiddenWeaponManager.getInstance().getHiddenWeapon(player);
			if (hiddenWeapon == null) {
				return 0;
			}
			PlayerAttribute attr = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
			return attr.getAttack();
		}
		*/
		
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//其他加成 比如药水
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		//遗落技能加成
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		/*
		//大秦典藏加成
		PlayerAttribute collect = getAttribute(player, PlayerAttributeType.COLLECT);
		//天元心法加成
		PlayerAttribute tianyuan = getAttribute(player, PlayerAttributeType.LONGYUAN);
		//军衔属性加成
		PlayerAttribute rank = getAttribute(player, PlayerAttributeType.RANK);
		//坐骑装备加成
		PlayerAttribute horseequip = getAttribute(player, PlayerAttributeType.HORSE_EQUIP);
		//宝石属性加成
		PlayerAttribute gem = getAttribute(player, PlayerAttributeType.GEM);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		//弓箭属性加成
		PlayerAttribute arrow = getAttribute(player, PlayerAttributeType.ARROW);
		PlayerAttribute taskChapter = getAttribute(player, PlayerAttributeType.TASK_CHATPER);
		
		PlayerAttribute pet = getAttribute(player, PlayerAttributeType.PET);
		//境界加成
		PlayerAttribute realm = getAttribute(player, PlayerAttributeType.REALM);
		PlayerAttribute horseWeapon = getAttribute(player, PlayerAttributeType.HORSE_WEAPON);
		PlayerAttribute hiddenWeapon = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
		//结婚属性加成
		PlayerAttribute marriageatt = getAttribute(player, PlayerAttributeType.MARRIAGE);
		*/
		double baseValue = 0;
		double equipValue = 0;
		double otherValue = 0;
		baseValue = (double)base.getHit() + buff.getHit();
		equipValue = (double)equip.getHit() + lostSkill.getHit();
		//otherValue = other.getAttack() + horse.getAttack();

		 return   (int)((baseValue + equipValue + otherValue) * (10000 + equip.gethitPercent() +buff.gethitPercent()) / 10000);

		//buff.getAttackPercent() + taskChapter.getAttackPercent() + banner.getAttackPercent()
		//buff.getEquipAttackPercent() + taskChapter.getEquipAttackPercent()
		//buff.getAttackTotalPercent()
		
	}
	/**
	 * 攻击力计算
	 *
	 * @param player
	 * @return
	 */
	private int countAttack(Player player) {
		//攻击清0 Buff
		if (FighterState.GONGJIWEILING.compare(player.getFightState())) {
			return 0;
		}

		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//大秦典藏加成
		PlayerAttribute collect = getAttribute(player, PlayerAttributeType.COLLECT);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//天元心法加成
		PlayerAttribute tianyuan = getAttribute(player, PlayerAttributeType.LONGYUAN);
		//军衔属性加成
		PlayerAttribute rank = getAttribute(player, PlayerAttributeType.RANK);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//坐骑装备加成
		PlayerAttribute horseequip = getAttribute(player, PlayerAttributeType.HORSE_EQUIP);
		//宝石属性加成
		PlayerAttribute gem = getAttribute(player, PlayerAttributeType.GEM);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		//弓箭属性加成
		PlayerAttribute arrow = getAttribute(player, PlayerAttributeType.ARROW);
		//其他加成
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);

		PlayerAttribute taskChapter = getAttribute(player, PlayerAttributeType.TASK_CHATPER);

		PlayerAttribute pet = getAttribute(player, PlayerAttributeType.PET);
		//境界加成
		PlayerAttribute realm = getAttribute(player, PlayerAttributeType.REALM);
		PlayerAttribute horseWeapon = getAttribute(player, PlayerAttributeType.HORSE_WEAPON);
		PlayerAttribute hiddenWeapon = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
		//结婚属性加成
		PlayerAttribute marriageatt = getAttribute(player, PlayerAttributeType.MARRIAGE);
		
		//遗落技能属性加成
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		
		//计算战斗力用
		player.setCalattack(
				(
				((collect.getAttack() + base.getAttack() + taskChapter.getAttack() + tianyuan.getAttack() + banner.getAttack() + player.getBuffCalAttr().getAttack())
				* (10000 + taskChapter.getAttackPercent() + banner.getAttackPercent() +  player.getBuffCalAttr().getAttackPercent()) / 10000 + (equip.getAttack() + horseequip.getAttack() + gem.getAttack()) * (10000 + taskChapter.getEquipAttackPercent()) / 10000
				+ lostSkill.getAttack() + rank.getAttack() + other.getAttack() + horse.getAttack() + pet.getAttack() + arrow.getAttack() + horseWeapon.getAttack() + realm.getAttack() + marriageatt.getAttack() + hiddenWeapon.getAttack())  * (10000) / 10000
				) * ( player.getBuffCalAttr().getAttackTotalPercent()) / 10000
			);

//		return ((base.getAttack() + buff.getAttack() + taskChapter.getAttack() + tianyuan.getAttack() + banner.getAttack())
//			* (10000 + buff.getAttackPercent() + taskChapter.getAttackPercent() + banner.getAttackPercent()) / 10000 + (equip.getAttack() + horseequip.getAttack() + gem.getAttack()) * (10000 + buff.getEquipAttackPercent() + taskChapter.getEquipAttackPercent()) / 10000
//			+ rank.getAttack() + other.getAttack() + horse.getAttack() + pet.getAttack() + arrow.getAttack())  * (10000 + buff.getAttackTotalPercent()) / 10000;
		double baseValue = ((double)base.getAttack() + buff.getAttack() + taskChapter.getAttack() + tianyuan.getAttack() + banner.getAttack())
				* (10000 + buff.getAttackPercent() + taskChapter.getAttackPercent() + banner.getAttackPercent()) / 10000;
		double equipValue = ((double)equip.getAttack() + horseequip.getAttack() + gem.getAttack())
				* (10000 + buff.getEquipAttackPercent() + taskChapter.getEquipAttackPercent()) / 10000;
		double otherValue = lostSkill.getAttack() + rank.getAttack() + other.getAttack() + horse.getAttack() + pet.getAttack() + arrow.getAttack() + horseWeapon.getAttack() + realm.getAttack() + collect.getAttack() + marriageatt.getAttack() + hiddenWeapon.getAttack();
		return (int)((baseValue + equipValue + otherValue) * (10000 + buff.getAttackTotalPercent()) / 10000);
	}

	/**
	 * 防御力计算
	 *
	 * @param player
	 * @return
	 */
	private int countDefense(Player player) {
		//防御清0 Buff
		if (FighterState.FANGYUWEILING.compare(player.getFightState())) {
			return 0;
		}
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//其他加成 比如药水
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		// vip加成
		PlayerAttribute vip = getAttribute(player, PlayerAttributeType.VIP);
		// 遗落技能加成
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		// 激活卓越属性加成
		PlayerAttribute activation = getAttribute(player, PlayerAttributeType.ACTIVATION);

		double baseValue = 0;
		double equipValue = 0;
		double otherValue = 0;
		baseValue = (double) base.getDefense() + buff.getDefense() + banner.getDefense() + vip.getDefense() + + lostSkill.getDefense()+activation.getDefense();
		equipValue = (double)equip.getDefense();
		otherValue = other.getDefense() + horse.getDefense();
		return (int) ((baseValue + equipValue + otherValue) * (10000 + equip.getDefensePercent() + buff.getDefensePercent() + banner.getDefensePercent() + vip.getDefensePercent() + lostSkill.getDefensePercent()+activation.getDefensePercent()) / 10000);

		
		
		
		/*panic add
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//大秦典藏加成
		PlayerAttribute collect = getAttribute(player, PlayerAttributeType.COLLECT);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//天元心法加成
		PlayerAttribute tianyuan = getAttribute(player, PlayerAttributeType.LONGYUAN);
		//军衔属性加成
		PlayerAttribute rank = getAttribute(player, PlayerAttributeType.RANK);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//坐骑装备加成
		PlayerAttribute horseequip = getAttribute(player, PlayerAttributeType.HORSE_EQUIP);
		//宝石属性加成
		PlayerAttribute gem = getAttribute(player, PlayerAttributeType.GEM);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		//弓箭属性加成
		PlayerAttribute arrow = getAttribute(player, PlayerAttributeType.ARROW);
		//任务章节加成
		PlayerAttribute taskChapter = getAttribute(player, PlayerAttributeType.TASK_CHATPER);
		//其他加成
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);

		PlayerAttribute pet = getAttribute(player, PlayerAttributeType.PET);
		//境界加成
		PlayerAttribute realm = getAttribute(player, PlayerAttributeType.REALM);
		PlayerAttribute horseWeapon = getAttribute(player, PlayerAttributeType.HORSE_WEAPON);
		PlayerAttribute hiddenWeapon = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
		//结婚属性加成
		PlayerAttribute marriageatt = getAttribute(player, PlayerAttributeType.MARRIAGE);
		
		//计算战斗力用
		player.setCaldefense(
				(
				((base.getDefense() +  player.getBuffCalAttr().getDefense() + collect.getDefense() + taskChapter.getDefense() + tianyuan.getDefense() + banner.getDefense())
				* (10000 + taskChapter.getDefensePercent() + banner.getDefensePercent() +  player.getBuffCalAttr().getDefensePercent()) / 10000 + (equip.getDefense() + horseequip.getDefense() + gem.getDefense()) * (10000 + taskChapter.getEquipDefensePercent()) / 10000
				+ rank.getDefense() + other.getDefense() + horse.getDefense() + pet.getDefense() + arrow.getDefense() + horseWeapon.getDefense() + realm.getDefense() +  marriageatt.getDefense() + hiddenWeapon.getDefense())  * (10000) / 10000
				) * ( player.getBuffCalAttr().getDefenseTotalPercent() + 10000) / 10000
				);
		
		double baseValue = ((double)base.getDefense() + buff.getDefense() + taskChapter.getDefense() + tianyuan.getDefense() + banner.getDefense())
				* (10000 + buff.getDefensePercent() + taskChapter.getDefensePercent() + banner.getDefensePercent()) / 10000;
		double equipValue = ((double)equip.getDefense() + horseequip.getDefense() + gem.getDefense())
				* (10000 + buff.getEquipDefensePercent() + taskChapter.getEquipDefensePercent()) / 10000;
		double otherValue = rank.getDefense() + other.getDefense() + horse.getDefense() + pet.getDefense() + arrow.getDefense() + horseWeapon.getDefense() + realm.getDefense() + collect.getDefense()+ marriageatt.getDefense() + hiddenWeapon.getDefense();
		return (int)((baseValue + equipValue + otherValue) * (10000 + buff.getDefenseTotalPercent()) / 10000);
		*/
	}

	/**
	 * 暴击计算（人物暂时不用）
	 *
	 * @param player
	 * @return
	 */
	private int countCrit(Player player) {
		//暴击清0 Buff
		if (FighterState.BAOJIWEILING.compare(player.getFightState())) {
			return 0;
		}
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//其他加成 比如药水
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		// vip加成
		PlayerAttribute vip = getAttribute(player, PlayerAttributeType.VIP);
		// 遗落技能加成
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		// 激活卓越属性加成
		PlayerAttribute activation = getAttribute(player, PlayerAttributeType.ACTIVATION);

		double baseValue = 0;
		double equipValue = 0;
		double otherValue = 0;
		baseValue = (double) base.getCrit() + buff.getCrit() + banner.getCrit() + vip.getCrit() + lostSkill.getCrit()+activation.getCrit();
		equipValue = (double)equip.getCrit();
		otherValue = other.getCrit() + horse.getCrit();
		return (int) ((baseValue + equipValue + otherValue) * (10000 + equip.getCritPercent() + buff.getCritTotalPercent() + banner.getCritPercent() + vip.getCritPercent() + lostSkill.getCritPercent()+activation.getCritPercent()) / 10000);

		/* panic god
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//大秦典藏加成
		PlayerAttribute collect = getAttribute(player, PlayerAttributeType.COLLECT);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//天元心法加成
		PlayerAttribute tianyuan = getAttribute(player, PlayerAttributeType.LONGYUAN);
		//军衔属性加成
		PlayerAttribute rank = getAttribute(player, PlayerAttributeType.RANK);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//坐骑装备加成
		PlayerAttribute horseequip = getAttribute(player, PlayerAttributeType.HORSE_EQUIP);
		//宝石属性加成
		PlayerAttribute gem = getAttribute(player, PlayerAttributeType.GEM);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		//弓箭属性加成
		PlayerAttribute arrow = getAttribute(player, PlayerAttributeType.ARROW);
		//任务章节加成
		PlayerAttribute taskChapter = getAttribute(player, PlayerAttributeType.TASK_CHATPER);
		//其他加成
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);

		PlayerAttribute pet = getAttribute(player, PlayerAttributeType.PET);
		//境界加成
		PlayerAttribute realm = getAttribute(player, PlayerAttributeType.REALM);
		PlayerAttribute horseWeapon = getAttribute(player, PlayerAttributeType.HORSE_WEAPON);
		PlayerAttribute hiddenWeapon = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
		//结婚属性加成
		PlayerAttribute marriageatt = getAttribute(player, PlayerAttributeType.MARRIAGE);
		
		//计算战斗力用
		player.setCalcrit(
				(
				((base.getCrit() + taskChapter.getCrit() + tianyuan.getCrit() + banner.getCrit() +  player.getBuffCalAttr().getCrit())
				* (10000 + taskChapter.getCrit() + banner.getCritPercent() +  player.getBuffCalAttr().getCritPercent()) / 10000 + equip.getCrit() + rank.getCrit() + other.getCrit()
				+ horse.getCrit() + horseequip.getCrit() + collect.getCrit() + gem.getCrit() + pet.getCrit() + arrow.getCrit() + horseWeapon.getCrit() + realm.getCrit() + marriageatt.getCrit() + hiddenWeapon.getCrit()) * (10000) / 10000
				) * (10000 +  player.getBuffCalAttr().getCritTotalPercent()) / 10000
				);

		double baseValue = ((double)base.getCrit() + buff.getCrit() + taskChapter.getCrit() + tianyuan.getCrit() + banner.getCrit())
				* (10000 + buff.getCritPercent() + taskChapter.getCritPercent() + banner.getCritPercent()) / 10000;
		double equipValue = ((double)equip.getCrit() + horseequip.getCrit() + gem.getCrit());
		double otherValue = rank.getCrit() + other.getCrit() + horse.getCrit() + pet.getCrit() + arrow.getCrit() + horseWeapon.getCrit() + realm.getCrit() + collect.getCrit() + marriageatt.getCrit() + hiddenWeapon.getCrit();
		return (int)((baseValue + equipValue + otherValue) * (10000 + buff.getCritTotalPercent()) / 10000);
		*/
//		return ((base.getCrit() + buff.getCrit() + taskChapter.getDefense() + tianyuan.getCrit() + banner.getCrit())
//			* (10000 + buff.getCritPercent() + taskChapter.getDefense() + banner.getCritPercent()) / 10000 + equip.getCrit() + rank.getCrit() + other.getCrit()
//			+ horse.getCrit() + horseequip.getCrit() + gem.getCrit() + pet.getCrit() + arrow.getCrit()) * (10000 + buff.getCritTotalPercent()) / 10000;
	
	}

	/**
	 * 闪避计算
	 *
	 * @param player
	 * @return
	 */
	private int countDodge(Player player) {
		//闪避清0 Buff
		if (FighterState.SHANBIWEILING.compare(player.getFightState())) {
			return 0;
		}
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//其他加成 比如药水
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		// vip加成
		PlayerAttribute vip = getAttribute(player, PlayerAttributeType.VIP);
		//遗落技能加成
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		// 激活卓越属性加成
		PlayerAttribute activation = getAttribute(player, PlayerAttributeType.ACTIVATION);

		int baseValue = 0;
		int equipValue = 0;
		int otherValue = 0;
		baseValue = base.getDodge() + buff.getDodge() + banner.getDodge() + vip.getDodge() + lostSkill.getDodge()+activation.getDodge();
		equipValue = equip.getDodge();
		otherValue = other.getDodge() + horse.getDodge();
		return ((baseValue + equipValue + otherValue) * (10000 + equip.getDodgePercent() + buff.getDodgePercent() + banner.getDodgePercent() + vip.getDodgePercent() + lostSkill.getDodgePercent()+activation.getDodgePercent()) / 10000);


		/* panic god
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//大秦典藏加成
		PlayerAttribute collect = getAttribute(player, PlayerAttributeType.COLLECT);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//龙元心法加成
		PlayerAttribute tianyuan = getAttribute(player, PlayerAttributeType.LONGYUAN);
		//军衔属性加成
		PlayerAttribute rank = getAttribute(player, PlayerAttributeType.RANK);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//坐骑装备加成
		PlayerAttribute horseequip = getAttribute(player, PlayerAttributeType.HORSE_EQUIP);
		//宝石属性加成
		PlayerAttribute gem = getAttribute(player, PlayerAttributeType.GEM);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		//弓箭属性加成
		PlayerAttribute arrow = getAttribute(player, PlayerAttributeType.ARROW);
		//任务章节加成
		PlayerAttribute taskChapter = getAttribute(player, PlayerAttributeType.TASK_CHATPER);
		//其他加成
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);

		PlayerAttribute pet = getAttribute(player, PlayerAttributeType.PET);
		//境界加成
		PlayerAttribute realm = getAttribute(player, PlayerAttributeType.REALM);
		PlayerAttribute horseWeapon = getAttribute(player, PlayerAttributeType.HORSE_WEAPON);
		PlayerAttribute hiddenWeapon = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
		//结婚属性加成
		PlayerAttribute marriageatt = getAttribute(player, PlayerAttributeType.MARRIAGE);
		
		
		//计算战斗力用
		player.setCaldodge(
				(
				((base.getDodge() + taskChapter.getDodge() + tianyuan.getDodge() + banner.getDodge() +  player.getBuffCalAttr().getDodge())
				* (10000 + taskChapter.getDodgePercent() + banner.getDodgePercent() +  player.getBuffCalAttr().getDodgePercent()) / 10000 + equip.getDodge() + rank.getDodge()
				+ horse.getDodge() + other.getDodge() + horseequip.getDodge() + gem.getDodge() + pet.getDodge() + arrow.getDodge() + horseWeapon.getDodge() + realm.getDodge() + collect.getDodge() + marriageatt.getDodge() + hiddenWeapon.getDodge()) * (10000) / 10000
				) * (10000 +  player.getBuffCalAttr().getDodgeTotalPercent()) / 10000
				);

		double baseValue = ((double)base.getDodge() + buff.getDodge() + taskChapter.getDodge() + tianyuan.getDodge() + banner.getDodge())
				* (10000 + buff.getDodgePercent() + taskChapter.getDodgePercent() + banner.getDodgePercent()) / 10000;
		double equipValue = ((double)equip.getDodge() + horseequip.getDodge() + gem.getDodge());
		double otherValue = rank.getDodge() + other.getDodge() + horse.getDodge() + pet.getDodge() + arrow.getDodge() + horseWeapon.getDodge() + realm.getDodge() + collect.getDodge() + marriageatt.getDodge() + hiddenWeapon.getDodge();
		return (int)((baseValue + equipValue + otherValue) * (10000 + buff.getDodgeTotalPercent()) / 10000);
		*/
//		return ((base.getDodge() + buff.getDodge() + taskChapter.getDodge() + tianyuan.getDodge() + banner.getDodge())
//			* (10000 + buff.getDodgePercent() + taskChapter.getDodgePercent() + banner.getDodgePercent()) / 10000 + equip.getDodge() + rank.getDodge()
//			+ horse.getDodge() + other.getDodge() + horseequip.getDodge() + gem.getDodge() + pet.getDodge() + arrow.getDodge()) * (10000 + buff.getDodgeTotalPercent()) / 10000;
	}

	private int countMaxHp(Player player) {
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//其他加成 比如药水
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		// vip加成
		PlayerAttribute vip = getAttribute(player, PlayerAttributeType.VIP);
		//遗落技能加成
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		// 激活卓越属性加成
		PlayerAttribute activation = getAttribute(player, PlayerAttributeType.ACTIVATION);
		
		double baseValue = 0;
		double equipValue = 0;
		double otherValue = 0;
		baseValue = (double) base.getMaxHp() + buff.getMaxHp() + banner.getMaxHp() + vip.getMaxHp() + lostSkill.getMaxHp()+activation.getMaxHp();
		equipValue = (double)equip.getMaxHp();
		otherValue = other.getMaxHp() + horse.getMaxHp();
		int maxhp = (int) ((baseValue + equipValue + otherValue) * (10000 + equip.getMaxHpPercent() + buff.getMaxHpPercent() + banner.getMaxHpPercent() + vip.getMaxHpPercent() + lostSkill.getMaxHpPercent()+activation.getMaxHpPercent()) / 10000);
		if(maxhp <= 0) maxhp = 1;
		return maxhp;
		/* panic god
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//大秦典藏加成
		PlayerAttribute collect = getAttribute(player, PlayerAttributeType.COLLECT);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//包裹加成
		PlayerAttribute backpack = getAttribute(player, PlayerAttributeType.BACKPACK);
		//仓库加成
		PlayerAttribute store = getAttribute(player, PlayerAttributeType.STORE);
		//龙元心法加成
		PlayerAttribute tianyuan = getAttribute(player, PlayerAttributeType.LONGYUAN);
		//组队加成
		PlayerAttribute team = getAttribute(player, PlayerAttributeType.TEAM);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//坐骑装备加成
		PlayerAttribute horseequip = getAttribute(player, PlayerAttributeType.HORSE_EQUIP);
		//宝石属性加成
		PlayerAttribute gem = getAttribute(player, PlayerAttributeType.GEM);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		//弓箭属性加成
		PlayerAttribute arrow = getAttribute(player, PlayerAttributeType.ARROW);
		//任务章节加成
		PlayerAttribute taskChapter = getAttribute(player, PlayerAttributeType.TASK_CHATPER);
		//其他加成
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		//军衔属性加成
		PlayerAttribute rank = getAttribute(player, PlayerAttributeType.RANK);
		
		PlayerAttribute pet = getAttribute(player, PlayerAttributeType.PET);
		//境界加成
		PlayerAttribute realm = getAttribute(player, PlayerAttributeType.REALM);
		PlayerAttribute horseWeapon = getAttribute(player, PlayerAttributeType.HORSE_WEAPON);
		PlayerAttribute hiddenWeapon = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
		//结婚属性加成
		PlayerAttribute marriageatt = getAttribute(player, PlayerAttributeType.MARRIAGE);
		
		//计算战斗力用
		player.setCalmaxHp(
				(
				(base.getMaxHp() + taskChapter.getMaxHp() + tianyuan.getMaxHp() + banner.getMaxHp() +  player.getBuffCalAttr().getMaxHp())
				* (10000 + taskChapter.getMaxHpPercent() + banner.getMaxHpPercent() +  player.getBuffCalAttr().getMaxHpPercent()) / 10000 + equip.getMaxHp() + backpack.getMaxHp() + store.getMaxHp()
				+ horse.getMaxHp() + other.getMaxHp() + collect.getMaxHp() + horseequip.getMaxHp() + gem.getMaxHp() + pet.getMaxHp() + arrow.getMaxHp() + rank.getMaxHp() + horseWeapon.getMaxHp() + realm.getMaxHp() + marriageatt.getMaxHp() + hiddenWeapon.getMaxHp()
				)
				 * (10000 +  player.getBuffCalAttr().getMaxHpTotalPercent()) / 10000
				);
		
		double baseValue = ((double)base.getMaxHp() + buff.getMaxHp() + taskChapter.getMaxHp() + tianyuan.getMaxHp() + banner.getMaxHp())
				* (10000 + buff.getMaxHpPercent() + taskChapter.getMaxHpPercent() + banner.getMaxHpPercent()) / 10000;
		double equipValue = ((double)equip.getMaxHp() + horseequip.getMaxHp() + gem.getMaxHp());
		double otherValue = rank.getMaxHp() + other.getMaxHp() + horse.getMaxHp() + pet.getMaxHp() + arrow.getMaxHp() + backpack.getMaxHp() + store.getMaxHp() + team.getMaxHp() + horseWeapon.getMaxHp() + realm.getMaxHp() + collect.getMaxHp() + marriageatt.getMaxHp() + hiddenWeapon.getMaxHp();
		int maxhp =  (int)((baseValue + equipValue + otherValue) * (10000 + buff.getMaxHpTotalPercent()) / 10000);
		if(maxhp <= 0) maxhp = 1;
		return maxhp;
		
		*/
//		return (base.getMaxHp() + buff.getMaxHp() + taskChapter.getMaxHp() + tianyuan.getMaxHp() + banner.getMaxHp())
//			* (10000 + buff.getMaxHpPercent() + taskChapter.getMaxHpPercent() + banner.getMaxHpPercent()) / 10000 + equip.getMaxHp() + backpack.getMaxHp() + store.getMaxHp()
//			+ team.getMaxHp() + horse.getMaxHp() + other.getMaxHp() + horseequip.getMaxHp() + gem.getMaxHp() + pet.getMaxHp() + arrow.getMaxHp()+ rank.getMaxHp();
	}

	private int countMaxMp(Player player) {
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//其他加成 比如药水
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		// vip加成
		PlayerAttribute vip = getAttribute(player, PlayerAttributeType.VIP);
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		// 激活卓越属性加成
		PlayerAttribute activation = getAttribute(player, PlayerAttributeType.ACTIVATION);

		double baseValue = 0;
		double equipValue = 0;
		double otherValue = 0;
		baseValue = (double) base.getMaxMp() + buff.getMaxMp() + banner.getMaxMp() + vip.getMaxMp() + lostSkill.getMaxMp()+activation.getMaxMp();
		equipValue = (double)equip.getMaxMp();
		otherValue = other.getMaxMp() + horse.getMaxMp();
		return (int) ((baseValue + equipValue + otherValue) * (10000 + equip.getMaxMpPercent() + buff.getMaxMpPercent() + banner.getMaxMpPercent() + vip.getMaxMpPercent() + lostSkill.getMaxMpPercent()+activation.getMaxMpPercent()) / 10000);

		/*
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//大秦典藏加成
		PlayerAttribute collect = getAttribute(player, PlayerAttributeType.COLLECT);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//龙元心法加成
		PlayerAttribute tianyuan = getAttribute(player, PlayerAttributeType.LONGYUAN);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//坐骑装备加成
		PlayerAttribute horseequip = getAttribute(player, PlayerAttributeType.HORSE_EQUIP);
		//宝石属性加成
		PlayerAttribute gem = getAttribute(player, PlayerAttributeType.GEM);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		//弓箭属性加成
		PlayerAttribute arrow = getAttribute(player, PlayerAttributeType.ARROW);
		//任务章节加成
		PlayerAttribute taskChapter = getAttribute(player, PlayerAttributeType.TASK_CHATPER);
		
		//军衔属性加成
		PlayerAttribute rank = getAttribute(player, PlayerAttributeType.RANK);
		
		//其他加成
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);

		PlayerAttribute pet = getAttribute(player, PlayerAttributeType.PET);
		//境界加成
		PlayerAttribute realm = getAttribute(player, PlayerAttributeType.REALM);
		PlayerAttribute horseWeapon = getAttribute(player, PlayerAttributeType.HORSE_WEAPON);
		PlayerAttribute hiddenWeapon = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
		//结婚属性加成
		PlayerAttribute marriageatt = getAttribute(player, PlayerAttributeType.MARRIAGE);
		
		//计算战斗力用
		player.setCalmaxMp(
				(
				(base.getMaxMp() + taskChapter.getMaxMp() + tianyuan.getMaxMp() + banner.getMaxMp() +  player.getBuffCalAttr().getMaxMp())
				* (10000 + banner.getMaxMpPercent() +  player.getBuffCalAttr().getMaxMpPercent()) / 10000 + equip.getMaxMp() + other.getMaxMp()
				+ horse.getMaxMp() + horseequip.getMaxMp() + gem.getMaxMp() + collect.getMaxMp() + pet.getMaxMp() + arrow.getMaxMp() + rank.getMaxMp() + horseWeapon.getMaxMp() + realm.getMaxMp()  + marriageatt.getMaxMp() + hiddenWeapon.getMaxMp()
				) * (10000 +  player.getBuffCalAttr().getMaxMpTotalPercent()) / 10000
				);
		
		double baseValue = ((double)base.getMaxMp() + buff.getMaxMp() + taskChapter.getMaxMp() + tianyuan.getMaxMp() + banner.getMaxMp())
				* (10000 + buff.getMaxMpPercent() + taskChapter.getMaxMpPercent() + banner.getMaxMpPercent()) / 10000;
		double equipValue = ((double)equip.getMaxMp() + horseequip.getMaxMp() + gem.getMaxMp());
		double otherValue = rank.getMaxMp() + other.getMaxMp() + horse.getMaxMp() + pet.getMaxMp() + arrow.getMaxMp() + horseWeapon.getMaxMp() + realm.getMaxMp() + collect.getMaxMp() + marriageatt.getMaxMp() + hiddenWeapon.getMaxMp();
		return (int)((baseValue + equipValue + otherValue) * (10000 + buff.getMaxMpTotalPercent()) / 10000);
		*/
//		return (base.getMaxMp() + buff.getMaxMp() + taskChapter.getMaxMp() + tianyuan.getMaxMp() + banner.getMaxMp())
//			* (10000 + buff.getMaxMpPercent() + banner.getMaxMpPercent()) / 10000 + equip.getMaxMp() + other.getMaxMp()
//			+ horse.getMaxMp() + horseequip.getMaxMp() + gem.getMaxMp() + pet.getMaxMp() + arrow.getMaxMp() + rank.getMaxMp();
	}
/*
 * 暂时不用
 */
	private int countMaxSp(Player player) {
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//大秦典藏加成
		PlayerAttribute collect = getAttribute(player, PlayerAttributeType.COLLECT);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//天元心法加成
		PlayerAttribute tianyuan = getAttribute(player, PlayerAttributeType.LONGYUAN);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//坐骑装备加成
		PlayerAttribute horseequip = getAttribute(player, PlayerAttributeType.HORSE_EQUIP);
		//宝石属性加成
		PlayerAttribute gem = getAttribute(player, PlayerAttributeType.GEM);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		//弓箭属性加成
		PlayerAttribute arrow = getAttribute(player, PlayerAttributeType.ARROW);
		//军衔属性加成
		PlayerAttribute rank = getAttribute(player, PlayerAttributeType.RANK);
		//任务章节加成
		PlayerAttribute taskChapter = getAttribute(player, PlayerAttributeType.TASK_CHATPER);
		//其他加成
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);

		PlayerAttribute pet = getAttribute(player, PlayerAttributeType.PET);
		//境界加成
		PlayerAttribute realm = getAttribute(player, PlayerAttributeType.REALM);
		PlayerAttribute horseWeapon = getAttribute(player, PlayerAttributeType.HORSE_WEAPON);
		PlayerAttribute hiddenWeapon = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
		//结婚属性加成
		PlayerAttribute marriageatt = getAttribute(player, PlayerAttributeType.MARRIAGE);
		//遗落技能属性加成
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		
		//计算战斗力用
		player.setCalmaxSp(
				(
				(base.getMaxSp() + taskChapter.getMaxSp() + tianyuan.getMaxSp() + banner.getMaxSp() +  player.getBuffCalAttr().getMaxSp())
				* (10000 + taskChapter.getMaxSpPercent() + banner.getMaxSpPercent() +  player.getBuffCalAttr().getMaxSpPercent()) / 10000 + equip.getMaxSp() + other.getMaxSp()
				+ horse.getMaxSp() + horseequip.getMaxSp() + collect.getMaxSp() + gem.getMaxSp() + pet.getMaxSp() + arrow.getMaxSp() + rank.getMaxSp() + horseWeapon.getMaxSp() + realm.getMaxSp() + marriageatt.getMaxSp() + hiddenWeapon.getMaxSp() + lostSkill.getMaxSp()
				) * ( player.getBuffCalAttr().getMaxSpTotalPercent() + 10000) / 10000
				);

		double baseValue = ((double)base.getMaxSp() + buff.getMaxSp() + taskChapter.getMaxSp() + tianyuan.getMaxSp() + banner.getMaxSp())
				* (10000 + buff.getMaxSpPercent() + taskChapter.getMaxSpPercent() + banner.getMaxSpPercent()) / 10000;
		double equipValue = ((double)equip.getMaxSp() + horseequip.getMaxSp() + gem.getMaxSp());
		double otherValue = rank.getMaxSp() + other.getMaxSp() + horse.getMaxSp() + pet.getMaxSp() + arrow.getMaxSp() + horseWeapon.getMaxSp() + realm.getMaxSp() + collect.getMaxSp() + marriageatt.getMaxSp() + hiddenWeapon.getMaxSp() + lostSkill.getMaxSp();
		return (int)((baseValue + equipValue + otherValue) * (10000 + buff.getMaxSpTotalPercent()) / 10000);
		
//		return (base.getMaxSp() + buff.getMaxSp() + taskChapter.getMaxSp() + tianyuan.getMaxSp() + banner.getMaxSp())
//			* (10000 + buff.getMaxSpPercent() + taskChapter.getMaxSpPercent() + banner.getMaxSpPercent()) / 10000 + equip.getMaxSp() + other.getMaxSp()
//			+ horse.getMaxSp() + horseequip.getMaxSp() + gem.getMaxSp() + pet.getMaxSp() + arrow.getMaxSp() + rank.getMaxSp();
	}

	private int countAttackSpeed(Player player) {
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//其他加成 比如药水
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);				
		// vip加成
		PlayerAttribute vip = getAttribute(player, PlayerAttributeType.VIP);
		//遗落技能加成
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		// 激活卓越属性加成
		PlayerAttribute activation = getAttribute(player, PlayerAttributeType.ACTIVATION);
		double baseValue = 0;
		double equipValue = 0;
		double otherValue = 0;
		baseValue = (double) base.getAttackSpeed() + buff.getAttackSpeed() + banner.getAttackSpeed() + vip.getAttackSpeed() + lostSkill.getAttackSpeed()+activation.getAttackSpeed();
		equipValue = (double)equip.getAttackSpeed();
		otherValue = other.getAttackSpeed() + horse.getAttackSpeed();
		return (int) ((baseValue + equipValue + otherValue)
				* (10000 + equip.getAttackSpeedPercent() + buff.getAttackSpeedPercent() + banner.getAttackSpeedPercent() + vip.getAttackSpeedPercent() + lostSkill.getAttackSpeedPercent()+activation.getAttackSpeedPercent()) / 10000);
		/* panic add
		//获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//大秦典藏加成
		PlayerAttribute collect= getAttribute(player, PlayerAttributeType.COLLECT);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//天元心法加成
		PlayerAttribute tianyuan = getAttribute(player, PlayerAttributeType.LONGYUAN);
		//军衔属性加成
		PlayerAttribute rank = getAttribute(player, PlayerAttributeType.RANK);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//坐骑装备加成
		PlayerAttribute horseequip = getAttribute(player, PlayerAttributeType.HORSE_EQUIP);
		//宝石属性加成
		PlayerAttribute gem = getAttribute(player, PlayerAttributeType.GEM);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		//弓箭属性加成
		PlayerAttribute arrow = getAttribute(player, PlayerAttributeType.ARROW);
		//任务章节加成
		PlayerAttribute taskChapter = getAttribute(player, PlayerAttributeType.TASK_CHATPER);
		//其他加成
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);

		PlayerAttribute pet = getAttribute(player, PlayerAttributeType.PET);
		//境界加成
		PlayerAttribute realm = getAttribute(player, PlayerAttributeType.REALM);
		PlayerAttribute horseWeapon = getAttribute(player, PlayerAttributeType.HORSE_WEAPON);
		PlayerAttribute hiddenWeapon = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
		//结婚属性加成
		PlayerAttribute marriageatt = getAttribute(player, PlayerAttributeType.MARRIAGE);
		
		//计算战斗力用
		player.setCalattackSpeed(
				(
				((base.getAttackSpeed() + taskChapter.getAttackSpeed() + tianyuan.getAttackSpeed() + banner.getAttackSpeed() +  player.getBuffCalAttr().getAttackSpeed())
				* (10000 + taskChapter.getAttackSpeedPercent() + banner.getAttackSpeedPercent() +  player.getBuffCalAttr().getAttackSpeedPercent()) / 10000 + (equip.getAttackSpeed() + horseequip.getAttackSpeed() + gem.getAttackSpeed())) * (10000) / 10000
				+ rank.getAttackSpeed() + other.getAttackSpeed() + collect.getAttackSpeed() + horse.getAttackSpeed() + pet.getAttackSpeed() + arrow.getAttackSpeed() + horseWeapon.getAttackSpeed()+ realm.getAttackSpeed() + marriageatt.getAttackSpeed() + hiddenWeapon.getAttackSpeed()
				) * ( player.getBuffCalAttr().getAttackSpeedTotalPercent()) / 10000
				);

		double baseValue = ((double)base.getAttackSpeed() + buff.getAttackSpeed() + taskChapter.getAttackSpeed() + tianyuan.getAttackSpeed() + banner.getAttackSpeed())
				* (10000 + buff.getAttackSpeedPercent() + taskChapter.getAttackSpeedPercent() + banner.getAttackSpeedPercent()) / 10000;
		double equipValue = ((double)equip.getAttackSpeed() + horseequip.getAttackSpeed() + gem.getAttackSpeed());
		double otherValue = rank.getAttackSpeed() + other.getAttackSpeed() + horse.getAttackSpeed() + pet.getAttackSpeed() + arrow.getAttackSpeed() + horseWeapon.getAttackSpeed() + realm.getAttackSpeed() + collect.getAttackSpeed() + marriageatt.getAttackSpeed() + hiddenWeapon.getAttackSpeed();
		return (int)((baseValue + equipValue + otherValue) * (10000 + buff.getAttackSpeedTotalPercent()) / 10000);
		
		*/
//		return ((base.getAttackSpeed() + buff.getAttackSpeed() + taskChapter.getAttackSpeed() + tianyuan.getAttackSpeed() + banner.getAttackSpeed())
//			* (10000 + buff.getAttackSpeedPercent() + taskChapter.getAttackSpeedPercent() + banner.getAttackSpeedPercent()) / 10000 + (equip.getAttackSpeed() + horseequip.getAttackSpeed() + gem.getAttackSpeed())) * (10000 + buff.getAttackSpeedTotalPercent()) / 10000
//			+ rank.getAttackSpeed() + other.getAttackSpeed() + horse.getAttackSpeed() + pet.getAttackSpeed() + arrow.getAttackSpeed();
	}


	private int countAttributeStrength(Player player) {
		// Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		
		int baseValue = player.getAttibute_one_base()[0] + buff.getStrength();
		return baseValue;
	}

	private int countAttributeVitality(Player player) {
		// Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);

		int baseValue = player.getAttibute_one_base()[1] + buff.getVitality();
		return baseValue;
	}

	private int countAttributeAgil(Player player) {
		// Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);

		int baseValue = player.getAttibute_one_base()[2] + buff.getAgil();
		return baseValue;
	}

	private int countAttributeIntelligence(Player player) {
		// Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);

		int baseValue = player.getAttibute_one_base()[3] + buff.getIntelligence();
		return baseValue;
	}

	private int countSpeed(Player player) {
		// 获取基本加成
		PlayerAttribute base = getAttribute(player, PlayerAttributeType.BASE);
		// Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		// 装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		// 坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		// 其他加成 比如药水
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		// vip加成
		PlayerAttribute vip = getAttribute(player, PlayerAttributeType.VIP);
		//遗落技能加成
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		// 激活卓越属性加成
		PlayerAttribute activation = getAttribute(player, PlayerAttributeType.ACTIVATION);

		double baseValue = 0;
		double equipValue = 0;
		double otherValue = 0;
		baseValue = (double) base.getSpeed() + buff.getSpeed() + banner.getSpeed() + vip.getSpeed() + lostSkill.getSpeed()+activation.getSpeed();
		equipValue = (double) equip.getSpeed();
		
		//查看是否上马：
		otherValue = other.getSpeed();
		if (ManagerPool.horseManager.isRidding(player)){
			otherValue += horse.getSpeed();
		}
		return (int) ((baseValue + equipValue + otherValue) * (10000 + equip.getSpeedPercent() + buff.getSpeedPercent() + banner.getSpeedPercent() + vip.getSpeedPercent()  + lostSkill.getSpeedPercent()+activation.getSpeedPercent()) / 10000);

		/*
		 * //获取基本加成 PlayerAttribute base = getAttribute(player,
		 * PlayerAttributeType.BASE); //Buff加成 PlayerAttribute buff =
		 * getAttribute(player, PlayerAttributeType.BUFF); //大秦典藏加成
		 * PlayerAttribute collect = getAttribute(player,
		 * PlayerAttributeType.COLLECT); //装备加成 PlayerAttribute equip =
		 * getAttribute(player, PlayerAttributeType.EQUIP); //天元心法加成
		 * PlayerAttribute tianyuan = getAttribute(player,
		 * PlayerAttributeType.LONGYUAN); //军衔属性加成 PlayerAttribute rank =
		 * getAttribute(player, PlayerAttributeType.RANK); //坐骑加成
		 * PlayerAttribute horse = getAttribute(player,
		 * PlayerAttributeType.HORSE); //坐骑装备加成 PlayerAttribute horseequip =
		 * getAttribute(player, PlayerAttributeType.HORSE_EQUIP); //宝石属性加成
		 * PlayerAttribute gem = getAttribute(player, PlayerAttributeType.GEM);
		 * //盟旗属性加成 PlayerAttribute banner = getAttribute(player,
		 * PlayerAttributeType.GUILDBANNER); //弓箭属性加成 PlayerAttribute arrow =
		 * getAttribute(player, PlayerAttributeType.ARROW); //任务章节加成
		 * PlayerAttribute taskChapter = getAttribute(player,
		 * PlayerAttributeType.TASK_CHATPER); //其他加成 PlayerAttribute other =
		 * getAttribute(player, PlayerAttributeType.OTHER); //境界加成
		 * PlayerAttribute realm = getAttribute(player,
		 * PlayerAttributeType.REALM); PlayerAttribute pet =
		 * getAttribute(player, PlayerAttributeType.PET);
		 * 
		 * PlayerAttribute horseWeapon = getAttribute(player,
		 * PlayerAttributeType.HORSE_WEAPON); //结婚属性加成 PlayerAttribute
		 * marriageatt = getAttribute(player, PlayerAttributeType.MARRIAGE);
		 * 
		 * 
		 * //计算战斗力用 player.setCalspeed( ( ((base.getSpeed() +
		 * taskChapter.getSpeed() + tianyuan.getSpeed() + banner.getSpeed() +
		 * player.getBuffCalAttr().getSpeed()) (10000 +
		 * taskChapter.getSpeedPercent() + banner.getSpeedPercent() +
		 * player.getBuffCalAttr().getSpeedPercent()) / 10000 +
		 * (equip.getSpeed() + horseequip.getSpeed() + gem.getSpeed())) * (10000
		 * + taskChapter.getSpeedPercent()) / 10000 + other.getSpeed() +
		 * rank.getSpeed() + horse.getSpeed() + collect.getSpeed() +
		 * pet.getSpeed() + arrow.getSpeed() + horseWeapon.getSpeed() +
		 * realm.getSpeed() + marriageatt.getSpeed() ) * (10000 +
		 * player.getBuffCalAttr().getSpeedTotalPercent()) / 10000 );
		 * 
		 * double baseValue = ((double)base.getSpeed() + buff.getSpeed() +
		 * taskChapter.getSpeed() + tianyuan.getSpeed() + banner.getSpeed())
		 * (10000 + buff.getSpeedPercent() + taskChapter.getSpeedPercent() +
		 * banner.getSpeedPercent()) / 10000; double equipValue =
		 * ((double)equip.getSpeed() + horseequip.getSpeed() + gem.getSpeed());
		 * double otherValue = rank.getSpeed() + other.getSpeed() +
		 * horse.getSpeed() + pet.getSpeed() + arrow.getSpeed() +
		 * horseWeapon.getSpeed() + realm.getSpeed() + collect.getSpeed() +
		 * marriageatt.getSpeed(); return (int)((baseValue + equipValue +
		 * otherValue) * (10000 + buff.getSpeedTotalPercent()) / 10000);
		 */
//		return ((base.getSpeed() + buff.getSpeed() + taskChapter.getSpeed() + tianyuan.getSpeed() + banner.getSpeed())
//			* (10000 + buff.getSpeedPercent() + taskChapter.getSpeedPercent() + banner.getSpeedPercent()) / 10000 + (equip.getSpeed() + horseequip.getSpeed() + gem.getSpeed())) * (10000 + buff.getSpeedTotalPercent() + taskChapter.getSpeedPercent()) / 10000 + other.getSpeed()
//			+ rank.getSpeed() + horse.getSpeed() + pet.getSpeed() + arrow.getSpeed();
	}

	/*
	 * 暂时不用
	 */
	private int countLuck(Player player) {
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//大秦典藏加成
		PlayerAttribute collect = getAttribute(player, PlayerAttributeType.COLLECT);
		//装备加成
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		//天元心法加成
		PlayerAttribute tianyuan = getAttribute(player, PlayerAttributeType.LONGYUAN);
		//宝石属性加成
		PlayerAttribute gem = getAttribute(player, PlayerAttributeType.GEM);
		//盟旗属性加成
		PlayerAttribute banner = getAttribute(player, PlayerAttributeType.GUILDBANNER);
		//弓箭属性加成
		PlayerAttribute arrow = getAttribute(player, PlayerAttributeType.ARROW);
		//军衔属性加成
		PlayerAttribute rank = getAttribute(player, PlayerAttributeType.RANK);
		//坐骑加成
		PlayerAttribute horse = getAttribute(player, PlayerAttributeType.HORSE);
		//坐骑装备加成
		PlayerAttribute horseequip = getAttribute(player, PlayerAttributeType.HORSE_EQUIP);
		//任务章节加成
		PlayerAttribute taskChapter = getAttribute(player, PlayerAttributeType.TASK_CHATPER);
		//其他加成
		PlayerAttribute other = getAttribute(player, PlayerAttributeType.OTHER);

		PlayerAttribute pet = getAttribute(player, PlayerAttributeType.PET);
		//境界加成
		PlayerAttribute realm = getAttribute(player, PlayerAttributeType.REALM);
		PlayerAttribute horseWeapon = getAttribute(player, PlayerAttributeType.HORSE_WEAPON);
		PlayerAttribute hiddenWeapon = getAttribute(player, PlayerAttributeType.HIDDEN_WEAPON);
		//结婚属性加成
		PlayerAttribute marriageatt = getAttribute(player, PlayerAttributeType.MARRIAGE);

		//遗落技能属性加成
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		
		//计算战斗力用
		player.setCalluck(
				(
				((taskChapter.getLuck() + tianyuan.getLuck() + banner.getLuck() +  player.getBuffCalAttr().getLuck())
				* (10000 + taskChapter.getLuck() + collect.getLuck() + banner.getLuckPercent() +  player.getBuffCalAttr().getLuckPercent()) / 10000 + equip.getLuck() + other.getLuck() + gem.getLuck() + pet.getLuck() + arrow.getLuck() + rank.getLuck() + horseWeapon.getLuck() + realm.getLuck() + marriageatt.getLuck() + hiddenWeapon.getLuck() + lostSkill.getLuck())  * (10000) / 10000
				) * (10000 +  player.getBuffCalAttr().getLuckTotalPercent()) / 10000
				);

		double baseValue = ((double)buff.getLuck() + taskChapter.getLuck() + tianyuan.getLuck() + banner.getLuck())
				* (10000 + buff.getLuckPercent() + taskChapter.getLuckPercent() + banner.getLuckPercent()) / 10000;
		double equipValue = ((double)equip.getLuck() + horseequip.getLuck() + gem.getLuck());
		double otherValue = rank.getLuck() + other.getLuck() + horse.getLuck() + pet.getLuck() + arrow.getLuck() + horseWeapon.getLuck() + realm.getLuck() + collect.getLuck() + marriageatt.getLuck() + hiddenWeapon.getLuck() + lostSkill.getLuck();
		return (int)((baseValue + equipValue + otherValue) * (10000 + buff.getLuckTotalPercent()) / 10000);
		
//		return ((buff.getLuck() + taskChapter.getLuck() + tianyuan.getLuck() + banner.getLuck())
//			* (10000 + buff.getLuckPercent() + taskChapter.getLuck() + banner.getLuckPercent()) / 10000 + equip.getLuck() + other.getLuck() + gem.getLuck() + pet.getLuck() + arrow.getLuck() + rank.getLuck())  * (10000 + buff.getLuckTotalPercent()) / 10000;
	}

	
	
	/**计算无视防御
	 * 暂时不用
	 * @param player
	 * @return
	 */

	private int countnegDefence(Player player) {

		PlayerAttribute realm = getAttribute(player, PlayerAttributeType.REALM);
		PlayerAttribute lostSkill = getAttribute(player, PlayerAttributeType.LOST_SKILL);
		return realm.getNegDefence() + lostSkill.getNegDefence();
	}
	
	
	/**计算弓箭几率
	 * 暂时不用
	 * @param player
	 * @return
	 */

	private int countarrowProbability(Player player) {

		PlayerAttribute realm = getAttribute(player, PlayerAttributeType.REALM);

		return realm.getArrowProbability();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 技能加成计算
	 *暂时不用
	 * @param player
	 * @return
	 */
	public HashMap<Integer, Integer> countSkillLevelUp(Player player) {
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		//天元心法加成
		PlayerAttribute tianyuan = getAttribute(player, PlayerAttributeType.LONGYUAN);

		HashMap<Integer, Integer> skills = new HashMap<Integer, Integer>();
		skills.putAll(buff.getSkillLevelUp());

		Iterator<Entry<Integer, Integer>> iter = tianyuan.getSkillLevelUp().entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<java.lang.Integer, java.lang.Integer> entry = (Map.Entry<java.lang.Integer, java.lang.Integer>) iter.next();
			if (skills.containsKey(entry.getKey())) {
				skills.put(entry.getKey(), skills.get(entry.getKey()) + entry.getValue());
			} else {
				skills.put(entry.getKey(), entry.getValue());
			}
		}

		return skills;
	}

	/**
	 * 经验加成计算
	 *
	 * @param player
	 * @return
	 */
	private int countExpMultiple(Player player) {
		int value = Global.MAX_PROBABILITY;
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);

		return value + buff.getExpPercent();
	}
   /*
    * 暂时不用
    */
	private int countZhenQiMultiple(Player player) {
		int value = Global.MAX_PROBABILITY;
		//Buff加成
		PlayerAttribute buff = getAttribute(player, PlayerAttributeType.BUFF);
		return value + buff.getZhenQiPercent();
	}

	/**
	 * 回血
	 * 
	 * @param player
	 * @return
	 */
	private int countHpRecover(Player player) {
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		int value = equip.getHp_recover();
		return value;
	}

	/**
	 * 伤害反射
	 * 暂时不用
	 * @param player
	 * @return
	 */
	private int countReboundDamage(Player player) {
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		int value = equip.getRebound_damage();
		return value;
	}

	/**
	 * 杀怪增加金币
	 * 
	 * @param player
	 * @return
	 */
	private int countAddMoneyWhenKill(Player player) {
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		int value = equip.getAddmoney_whenkill();
		return value;
	}

	/**
	 * 杀怪加血
	 * 
	 * @param player
	 * @return
	 */
	private int countAddHpWhenKill(Player player) {
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		int value = equip.getAddhp_whenkill();
		return value;
	}

	/**
	 * 杀怪加魔
	 * 
	 * @param player
	 * @return
	 */
	private int countAddMpWhenKill(Player player) {
		PlayerAttribute equip = getAttribute(player, PlayerAttributeType.EQUIP);
		int value = equip.getAddmp_whenkill();
		return value;
	}
//	private void showAttributes(Player player){
//		Iterator<Integer> atts = player.getAttributes().keySet().iterator();
//		while (atts.hasNext()) {
//			Integer playerAttribute = (Integer) atts.next();
//			log.info(player.getId() + ":" + playerAttribute);
//		}
//	}
	/*
	 * 主要用于客户端属性面板展示用，现在有3个面板
	 */
	private class PlayerInfo {
		//攻击
		private int attack;
		private int physic_attackupper ;//
		private int physic_attacklower ;//
		private int magic_attackupper ;//
		private int magic_attacklower ;//
		
		//
		private int  Perfect_attackPercent;
		private int  Knowing_attackPercent;
		private int  Ignore_attackPercent;
		//	
		private int  Ice_def;
		private int  Poison_def;
		private int  Ray_def;
		
		//忽略防御
		private int ignore_defendPercent;
		//无视防御产生的值（万分比）
		private int ignore_defend_add_Percent;
		//加成卓越一击 *暴击 伤害加成值120% +%
		private int  perfectatk_addpercent;

		//防御
		private int defense;
		//暴击
		private int crit;
		
		//命中判定
		private int hit;//
		//闪避
		private int dodge;
	
		
		//最大血量
		private int maxHp;
		//最大魔法
		private int maxMp;
		//最大体力
		private int maxSp;
		//攻击速度
		private int attackSpeed;
		//速度
		private int speed;
		//幸运
		private int luck;
		//预留属性
		private int[] attribute_one;
		HashMap<Integer, Integer> skillLevelUp = new HashMap<Integer, Integer>();

		

		public int[] getAttribute_one() {
			return attribute_one;
		}

		public void setAttribute_one(int[] attribute_one) {
			this.attribute_one = attribute_one;
		}

		public void copyPlayer(Player player) {
			//this.setAttack(player.getAttack());
			
			this.setPhysic_attackupper(player.getPhysic_attackupper());
			this.setPhysic_attacklower(player.getPhysic_attacklower());
			this.setMagic_attackupper(player.getMagic_attackupper());
			this.setMagic_attacklower(player.getMagic_attacklower());
			this.setKnowing_attackPercent(player.getKnowing_attackPercent());
			this.setIgnore_attackPercent(player.getIgnore_attackPercent());
			this.setPerfect_attackPercent(player.getPerfect_attackPercent());
			this.setIce_def(player.getIce_def());
			this.setRay_def(player.getRay_def());
			this.setPoison_def(player.getPoison_def());
			this.setIgnore_defendPercent(player.getignore_defendPercent());
			this.setPerfectatk_addpercent(player.getPerfect_addattackPercent());
			
			this.setDefense(player.getDefense());
			//this.setCrit(player.getCrit());
			this.setHit(player.getHit());
			this.setDodge(player.getDodge());
			this.setMaxHp(player.getMaxHp());
			this.setMaxMp(player.getMaxMp());
			this.setMaxSp(player.getMaxSp());
			this.setAttackSpeed(player.getAttackSpeed());
			this.setSpeed(player.getSpeed());
			this.setLuck(player.getLuck());
			this.getSkillLevelUp().putAll(player.getSkillLevelUp());
			this.setAttribute_one(player.getAttibute_one());
		}

		public void comparePlayer(Player player, int type, int modelId) {
			List<PlayerAttributeItem> list = new ArrayList<PlayerAttributeItem>();
		
			if (this.getDefense() != player.getDefense()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.DEFENSE.getValue());
				attribute.setValue(player.getDefense());
				list.add(attribute);
				log.debug("Player " + player.getId() + " defense " + this.getDefense() + "-->" + player.getDefense());
			}
			//panic add
			if (this.getPhysic_attackupper() != player.getPhysic_attackupper()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.PHYSIC_ATTACKUPPER.getValue());
				attribute.setValue(player.getPhysic_attackupper());
				list.add(attribute);
				log.debug("Player " + player.getId() + " getPhysic_attackupper " + this.getPhysic_attackupper() + "-->" + player.getPhysic_attackupper());
			}
			if (this.getPhysic_attacklower() != player.getPhysic_attacklower()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.PHYSIC_ATTACKLOWER.getValue());
				attribute.setValue(player.getPhysic_attacklower());
				list.add(attribute);
				log.debug("Player " + player.getId() + " getPhysic_attacklower " + this.getPhysic_attacklower() + "-->" + player.getDefense());
			}
			if (this.getMagic_attackupper() != player.getMagic_attackupper()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.MAGIC_ATTACKUPPER.getValue());
				attribute.setValue(player.getMagic_attackupper());
				list.add(attribute);
				log.debug("Player " + player.getId() + " getMagic_attackupper " + this.getMagic_attackupper() + "-->" + player.getDefense());
			}
			if (this.getMagic_attacklower() != player.getMagic_attacklower()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.MAGIC_ATTACKLOWER.getValue());
				attribute.setValue(player.getMagic_attacklower());
				list.add(attribute);
				log.debug("Player " + player.getId() + " getMagic_attacklower " + this.getMagic_attacklower() + "-->" + player.getMagic_attacklower());
			}
			if (this.getHit() != player.getHit()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.HIT.getValue());
				attribute.setValue(player.getHit());
				list.add(attribute);
				// 前端显示用 攻击成功率
				PlayerAttributeItem attribute2 = new PlayerAttributeItem();
				attribute2.setType(Attributes.ATTACKPERCENT.getValue());
				attribute2.setValue(player.getHit());
				list.add(attribute2);
				log.debug("Player " + player.getId() + " Hit " + this.getDodge() + "-->" + player.getDodge());
			}
			if (this.getPerfect_attackPercent() != player.getPerfect_attackPercent()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.PERFECT_ATTACKPERCENT.getValue());
				attribute.setValue(player.getPerfect_attackPercent());
				list.add(attribute);
				log.debug("Player " + player.getId() + " PERFECT_ATTACKPERCENT " + this.getPerfect_attackPercent() + "-->" + player.getPerfect_attackPercent());
			}
			if (this.getKnowing_attackPercent() != player.getKnowing_attackPercent()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.KNOWING_ATTACKPERCENT.getValue());
				attribute.setValue(player.getKnowing_attackPercent());
				list.add(attribute);
				log.debug("Player " + player.getId() + " KNOWING_ATTACKPERCENT " + this.getKnowing_attackPercent() + "-->" + player.getKnowing_attackPercent());
			}
			if (this.getIgnore_attackPercent() != player.getIgnore_attackPercent()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.IGNORE_ATTACKPERCENT.getValue());
				attribute.setValue(player.getIgnore_attackPercent());
				list.add(attribute);
				log.debug("Player " + player.getId() + " KNOWING_ATTACKPERCENT " + this.getIgnore_attackPercent() + "-->" + player.getIgnore_attackPercent());
			}
			if (this.getIce_def() != player.getIce_def()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.ICE_DEF.getValue());
				attribute.setValue(player.getIce_def());
				list.add(attribute);
				log.debug("Player " + player.getId() + " getIce_def " + this.getIce_def() + "-->" + player.getIce_def());
			}
			if (this.getPoison_def() != player.getPoison_def()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.POISON_DEF.getValue());
				attribute.setValue(player.getPoison_def());
				list.add(attribute);
				log.debug("Player " + player.getId() + " getPoison_def " + this.getPoison_def() + "-->" + player.getPoison_def());
			}
			if (this.getRay_def() != player.getRay_def()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.RAY_DEF.getValue());
				attribute.setValue(player.getRay_def());
				list.add(attribute);
				log.debug("Player " + player.getId() + " RAY_DEF " + this.getRay_def() + "-->" + player.getRay_def());
			}
			if (this.getPerfectatk_addpercent() != player.getPerfect_addattackPercent()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.PERFECTATK_ADDPERCENT.getValue());
				attribute.setValue(player.getPerfect_addattackPercent());
				list.add(attribute);
				log.debug("Player " + player.getId() + " perfect_addattackPercent " + this.getPerfectatk_addpercent() + "-->" + player.getPerfect_addattackPercent());
			}
			if (this.getIgnore_defendPercent() != player.getignore_defendPercent()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.IGNORE_DEFENDPERCENT.getValue());
				attribute.setValue(player.getignore_defendPercent());
				list.add(attribute);
				log.debug("Player " + player.getId() + " ignore_defendPercent " + this.getIgnore_defendPercent() + "-->" + player.getignore_defendPercent());
			}
			if (this.getIgnore_defend_add_Percent() != player.getignore_defend_add_Percent()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.IGNORE_ADD_DEFENSE.getValue());
				attribute.setValue(player.getignore_defend_add_Percent());
				list.add(attribute);
				log.debug("Player " + player.getId() + " ignore_defend_add_Percent " + this.getIgnore_defend_add_Percent() + "-->" + player.getignore_defend_add_Percent());
			}
			if (this.getDodge() != player.getDodge()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.DODGE.getValue());
				attribute.setValue(player.getDodge());
				list.add(attribute);
				// 前端显示用 防御成功率
				PlayerAttributeItem attribute2 = new PlayerAttributeItem();
				attribute2.setType(Attributes.DEFENSEPERCENT.getValue());
				attribute2.setValue(player.getDodge());
				list.add(attribute2);
				log.debug("Player " + player.getId() + " dodge " + this.getDodge() + "-->" + player.getDodge());
			}
			if (this.getMaxHp() != player.getMaxHp()) {
				ManagerPool.playerManager.onMaxHpChange(player);
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.MAXHP.getValue());
				attribute.setValue(player.getMaxHp());
				list.add(attribute);
				log.debug("Player " + player.getId() + " maxHp " + this.getMaxHp() + "-->" + player.getMaxHp());
			}
			if (this.getMaxMp() != player.getMaxMp()) {
				ManagerPool.playerManager.onMaxMpChange(player);
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.MAXMP.getValue());
				attribute.setValue(player.getMaxMp());
				list.add(attribute);
				log.debug("Player " + player.getId() + " maxMp " + this.getMaxMp() + "-->" + player.getMaxMp());
			}
			/*
			if (this.getMaxSp() != player.getMaxSp()) {
				ManagerPool.playerManager.onMaxSpChange(player);
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.MAXSP.getValue());
				attribute.setValue(player.getMaxSp());
				list.add(attribute);
				log.debug("Player " + player.getId() + " maxSp " + this.getMaxSp() + "-->" + player.getMaxSp());
			}*/
			if (this.getAttackSpeed() != player.getAttackSpeed()) {
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.ATTACKSPEED.getValue());
				attribute.setValue(player.getAttackSpeed());
				list.add(attribute);
				log.debug("Player " + player.getId() + " attackSpeed " + this.getAttackSpeed() + "-->" + player.getAttackSpeed());
			}
			if (this.getSpeed() != player.getSpeed()) {
				ManagerPool.playerManager.onSpeedChange(player);
				PlayerAttributeItem attribute = new PlayerAttributeItem();
				attribute.setType(Attributes.SPEED.getValue());
				attribute.setValue(player.getSpeed());
				list.add(attribute);
				log.debug("Player " + player.getId() + " speed " + this.getSpeed() + "-->" + player.getSpeed());
			}
			PlayerAttributeItem attribute = new PlayerAttributeItem();
			attribute.setType(Attributes.ATTRIBUTE_ONE_REST_PLUS_VALUE.getValue());
			attribute.setValue(player.getAttibute_one()[4]);
			list.add(attribute);
			
			
			ResPlayerAttributeChangeMessage msg = new ResPlayerAttributeChangeMessage();
			msg.setAttributeChangeReason(type);
			msg.setModelId(modelId);
			msg.setAttributeChangeList(list);
			MessageUtil.tell_player_message(player, msg);

			//发送技能信息
			HashSet<Integer> keys = new HashSet<Integer>();
			keys.addAll(this.getSkillLevelUp().keySet());
			keys.addAll(player.getSkillLevelUp().keySet());
			Iterator<Integer> iter = keys.iterator();
			while (iter.hasNext()) {
				Integer key = (Integer) iter.next();
				if (key == -1) {
					int level1 = 0;
					if (this.getSkillLevelUp().containsKey(-1)) {
						level1 = this.getSkillLevelUp().get(-1);
					}
					int level2 = 0;
					if (player.getSkillLevelUp().containsKey(-1)) {
						level2 = player.getSkillLevelUp().get(-1);
					}
					if (level1 != level2) {
						ManagerPool.skillManager.sendSkillInfos(player);
						break;
					}
				}
//				else if(key > 100 && key < 1000){
//					continue;
//				}
//				
//				// 初学 默认取第一级
//				Q_skill_modelBean skillModel = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(key + "_" + 1);
//				
				int level1 = 0;
				if (this.getSkillLevelUp().containsKey(-1)) {
					level1 = this.getSkillLevelUp().get(-1);
				}
//				if(this.getSkillLevelUp().containsKey(skillModel.getq_)){
//					level1 += this.getSkillLevelUp().get(key);
//				}
				if (this.getSkillLevelUp().containsKey(key)) {
					level1 += this.getSkillLevelUp().get(key);
				}

				int level2 = 0;
				if (player.getSkillLevelUp().containsKey(-1)) {
					level2 = player.getSkillLevelUp().get(-1);
				}
				if (player.getSkillLevelUp().containsKey(key)) {
					level2 += player.getSkillLevelUp().get(key);
				}

				if (level1 != level2) {
					//ManagerPool.skillManager.sendSkillInfos(player);
					ManagerPool.skillManager.sendSkillInfo(player, key);
				}
			}
		}

		public int getAttack() {
			return attack;
		}

		public void setAttack(int attack) {
			this.attack = attack;
		}

		public int getIgnore_defendPercent() {
			return ignore_defendPercent;
		}

		public int getIgnore_defend_add_Percent() {
			return ignore_defend_add_Percent;
		}

		public void setIgnore_defend_add_Percent(int ignore_defend_add_Percent) {
			this.ignore_defend_add_Percent = ignore_defend_add_Percent;
		}

		public void setIgnore_defendPercent(int ignore_defendPercent) {
			this.ignore_defendPercent = ignore_defendPercent;
		}

		public int getPerfectatk_addpercent() {
			return perfectatk_addpercent;
		}

		public void setPerfectatk_addpercent(int perfectatk_addpercent) {
			this.perfectatk_addpercent = perfectatk_addpercent;
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

		public HashMap<Integer, Integer> getSkillLevelUp() {
			return skillLevelUp;
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

		public int getHit() {
			return hit;
		}

		public void setHit(int hit) {
			this.hit = hit;
		}
		
		/*
		 * private int  Perfect_attackPercent;
		private int  Knowing_attackPercent;
		private int  Ignore_attackPercent;
		
		 */
		public int getPerfect_attackPercent() {
			return Perfect_attackPercent;
		}

		public void setPerfect_attackPercent(int Perfect_attackPercent) {
			this.Perfect_attackPercent = Perfect_attackPercent;
		}
		
		public int getKnowing_attackPercent() {
			return Knowing_attackPercent;
		}

		public void setKnowing_attackPercent(int Knowing_attackPercent) {
			this.Knowing_attackPercent = Knowing_attackPercent;
		}
		
		public int getIgnore_attackPercent() {
			return Ignore_attackPercent;
		}

		public void setIgnore_attackPercent(int Ignore_attackPercent) {
			this.Ignore_attackPercent = Ignore_attackPercent;
		}
		/*
		 * 	private int  Ice_def;
		private int  Poison_def;
		private int  Ray_def;
		 */
		public int getIce_def() {
			return Ice_def;
		}

		public void setIce_def(int Ice_def) {
			this.Ice_def = Ice_def;
		}
		public int getPoison_def() {
			return Poison_def;
		}

		public void setPoison_def(int Poison_def) {
			this.Poison_def = Poison_def;
		}
		public int getRay_def() {
			return Ray_def;
		}

		public void setRay_def(int Ray_def) {
			this.Ray_def = Ray_def;
		}
	}
	
}
