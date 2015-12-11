package com.game.fight.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.game.buff.structs.Buff;
import com.game.buff.structs.BuffConst;
import com.game.buff.structs.BuffType;
import com.game.config.Config;
import com.game.cooldown.structs.CooldownTypes;
import com.game.country.manager.CountryManager;
import com.game.country.structs.CountryFightStatus;
import com.game.csys.manager.CsysManger;
import com.game.data.bean.Q_buffBean;
import com.game.data.bean.Q_globalBean;
import com.game.data.bean.Q_mapBean;
import com.game.data.bean.Q_monsterBean;
import com.game.data.bean.Q_petinfoBean;
import com.game.data.bean.Q_skill_modelBean;
import com.game.dazuo.manager.PlayerDaZuoManager;
import com.game.fight.bean.AttackResultInfo;
import com.game.fight.message.ResAttackRangeMessage;
import com.game.fight.message.ResAttackResultMessage;
import com.game.fight.message.ResEffectBroadcastMessage;
import com.game.fight.message.ResFightBroadcastMessage;
import com.game.fight.message.ResFightFailedBroadcastMessage;
import com.game.fight.message.ResFightPostionBroadcastMessage;
import com.game.fight.script.IAttackCheckScript;
import com.game.fight.script.IHitDamageScript;
import com.game.fight.structs.FightResult;
import com.game.fight.structs.Fighter;
import com.game.fight.structs.FighterInfo;
import com.game.fight.structs.FighterState;
import com.game.fight.structs.ResultFailType;
import com.game.fight.timer.HitPostionTimer;
import com.game.fight.timer.HitTimer;
import com.game.guild.manager.GuildServerManager;
import com.game.hiddenweapon.message.ResHiddenWeaponSkillTriggerMessage;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.map.manager.MapManager;
import com.game.map.message.ResSetPositionMessage;
import com.game.map.message.ResSetSpecialPositionMessage;
import com.game.map.structs.Area;
import com.game.map.structs.Effect;
import com.game.map.structs.GroundMagic;
import com.game.map.structs.Map;
import com.game.monster.manager.MonsterManager;
import com.game.monster.script.IMonsterAiScript;
import com.game.monster.structs.Hatred;
import com.game.monster.structs.Monster;
import com.game.monster.structs.MonsterState;
import com.game.pet.manager.PetScriptManager;
import com.game.pet.message.script.IPetWasHitScript;
import com.game.pet.struts.Pet;
import com.game.pet.struts.PetJumpState;
import com.game.pet.struts.PetRunState;
import com.game.player.manager.PlayerManager;
import com.game.player.message.ResChangePlayerEnemiesToClientMessage;
import com.game.player.script.IPlayerWasHitScript;
import com.game.player.script.PlayerCheckType;
import com.game.player.structs.GmState;
import com.game.player.structs.Person;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerState;
import com.game.prompt.structs.Notifys;
import com.game.script.structs.ScriptEnum;
import com.game.skill.structs.ISkillScript;
import com.game.skill.structs.Skill;
import com.game.structs.Grid;
import com.game.structs.Position;
import com.game.summonpet.manager.SummonPetOptManager;
import com.game.summonpet.struts.SummonPet;
import com.game.summonpet.struts.SummonPetJumpState;
import com.game.summonpet.struts.SummonPetRunState;
import com.game.team.bean.TeamInfo;
import com.game.team.bean.TeamMemberInfo;
import com.game.team.manager.TeamManager;
import com.game.util.TimerUtil;
import com.game.utils.CommonConfig;
import com.game.utils.Global;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
import com.game.utils.RandomUtils;
import com.game.utils.Symbol;

/**
 * 战斗管理
 *
 * @author
 *
 */
public class FightManager {

	private Logger log = Logger.getLogger(FightManager.class);
	
	private static final int DEFAULT_PK_MIN_LEVEL = 60; //PK最低攻击等级
	
	private static final int DEFAULT_PK_DIFF_LEVEL = 60; //PK允许攻击的等级差
	
	private static Object obj = new Object();
	//管理类实例
	private static FightManager manager;
	//玩家同步坐标
	private static HashSet<Long> syncArea = new HashSet<Long>();

	private FightManager() {
	}

	public static FightManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new FightManager();
			}
		}
		return manager;
	}

	public static HashSet<Long> getSyncArea() {
		return syncArea;
	}

	/**
	 * 玩家攻击怪物
	 *
	 * @param roleId 玩家Id
	 * @param monsterId 怪物Id
	 * @param skillId 攻击技能
	 * @param direction 攻击方向
	 */
	public void playerAttackMonster(Player player, long monsterId, int skillId, int direction) {		
		//停止采集
		ManagerPool.npcManager.playerStopGather(player);

		//冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.PLAYER_ATTACK, null)) {
			log.debug("攻击者（玩家）攻击冷却");
			return;
		}
		
		//冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL, String.valueOf(skillId))) {
			long remain = (long) ManagerPool.cooldownManager.getCooldownTime(player, CooldownTypes.SKILL, String.valueOf(skillId));
			
			ManagerPool.playerManager.playercheck(player, PlayerCheckType.ATTACK_SPEED, remain);
			//技能冷却中
			if (remain > 10 * 1000) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("技能冷却中，请稍后再试，剩余{1}秒"), String.valueOf((int)(remain/1000)));
			}

			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.cooldown, player, monsterId, skillId, direction));
			return;
		}

		//玩家已经死亡
		if (player.isDie()) {
			log.debug("攻击者（玩家）死亡");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.isDie, player, monsterId, skillId, direction));
			return;
		}

		//定身或睡眠中
		if (FighterState.DINGSHEN.compare(player.getFightState()) || FighterState.SHUIMIAN.compare(player.getFightState())) {
			log.debug("攻击者（玩家）定身或睡眠");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.DINGSHENSHUIMIAN, player, monsterId, skillId, direction));
			return;
		}

		//停止格挡
		if (PlayerState.BLOCKPREPARE.compare(player.getState()) || PlayerState.BLOCK.compare(player.getState())) {
			ManagerPool.mapManager.playerStopBlock(player);
		}

		// 游泳中
		if (PlayerState.SWIM.compare(player.getState())) {
			log.debug("攻击者（玩家）游泳中了");
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("游泳区内无法使用技能"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.SWIM, player, monsterId, skillId, direction));
			return;
		}

		//技能判断
		Skill skill = ManagerPool.skillManager.getSkillByModelId(player, skillId);
		//技能是否创建时学会
		boolean defaultStudy = false;
		if (skill == null) {
			skill = new Skill();
			skill.setSkillModelId(skillId);
			skill.setSkillLevel(1);
			defaultStudy = true;
		}

		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getRealLevel(player));
		if (model == null) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.modelnull, player, monsterId, skillId, direction));
			return;
		}

		if (defaultStudy) {
			//不是默认学会技能
			if (model.getQ_default_study() != 1) {
				log.debug("攻击者（玩家）技能不对");
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultstudyno, player, monsterId, skillId, direction));
				return;
			}
		}
		
        //获取玩家身上的变身buff
		List<Buff> shapeChangebuffs = ManagerPool.buffManager.getBuffShapeChange(player);
		//如果没有变身
		if(shapeChangebuffs.size() == 0){
			if(model.getQ_shapechange_buffid() !=0){
				log.debug("攻击者（玩家）变身技能不对");
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultshapechangeno, player, 0, skillId, direction));
				return;			
			 }
		}else{
			//有变身
			boolean defaultShapeChange = false;
			for(Buff per:shapeChangebuffs){
				if(per.getModelId()== model.getQ_shapechange_buffid()){
					defaultShapeChange = true;
					break;
				}
			}
			if(!defaultShapeChange){
				log.debug("攻击者（玩家）变身技能不对");
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultshapechangeno, player, 0, skillId, direction));
				return;		
			}
		}

		//是否主动技能
		if (model.getQ_trigger_type() != 1) {
			log.debug("攻击者（玩家）非主动技能");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.triggertypeno, player, monsterId, skillId, direction));
			return;
		}

		//是否人物技能
		if (model.getQ_skill_user() != 1) {
			log.debug("攻击者（玩家）非人物技能");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.skilluserno, player, monsterId, skillId, direction));
			return;
		}

		//获得怪物
		Monster monster = null;
		
		if (monsterId > 0) {
			monster = ManagerPool.monsterManager.getMonster(player.getServerId(), player.getLine(), player.getMap(), monsterId);
			if (monster != null) {
				if(monster.isDie()){
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.monsterisDie, player, monsterId, skillId, direction));
					return;
				}
				
				IAttackCheckScript script = (IAttackCheckScript) ManagerPool.scriptManager.getScript(ScriptEnum.CHECKATTACK);
				if (script != null) {
					try {
						if(!script.check(player, monster)){
							MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.scriptcheckno, player, monsterId, skillId, direction));
							return;
						}
					} catch (Exception e) {
						log.error(e, e);
					}
				} else {
					log.error("攻击检查脚本不存在！");
				}
			}
		}
		
		//技能目标选择
		ISkillScript script = null;
		if(model.getQ_skill_script() > 0){
			script = (ISkillScript) ManagerPool.scriptManager.getScript(model.getQ_skill_script());
		}
		if(script!=null){
			try{
				if(!script.canUse(player, monster, direction)){
					return;
				}
			}catch(Exception e){
				log.error(e, e);
			}
		}

		//log.error("Player " + player.getId() + " attack monster " + monsterId + ", is null:" + (monster!=null) + (monster==null?"":(" is die:" + monster.isDie())));

		//目标检查 （单体目标且对象不为自己时）
		if (model.getQ_area_shape() == 1 && model.getQ_target() != 1 && (monster == null || monster.isDie())) {
			//无目标错误
			//MessageUtil.notify_player(player, Notifys.ERROR, "请先选定一个释放目标");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.shapetargetno, player, monsterId, skillId, direction));
			return;
		}

		//目标不符，无法释放
		if (model.getQ_target() == 1 || model.getQ_target() == 2) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("目标不符，无法释放"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.targetno, player, monsterId, skillId, direction));
			return;
		}

		if (PlayerState.CHANGEMAP.compare(player.getState())) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.CHANGEMAP, player, monsterId, skillId, direction));
			return;
		}

		Map map = ManagerPool.mapManager.getMap(player);
		if (map == null) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.mapno, player, monsterId, skillId, direction));
			return;
		}
		
		//梅花副本不能使用群攻技能
		if(map.getMapModelid()==42121 && model.getQ_area_shape()!=1){
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("本地图不能使用此技能"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.mapskillno, player, monsterId, skillId, direction));
			return;
		}

		//距离检查
		if (monster != null) {
			Grid playerGrid = MapUtils.getGrid(player.getPosition(), ManagerPool.mapManager.getMapBlocks(map.getMapModelid()));
			Grid monsterGrid = MapUtils.getGrid(monster.getPosition(), ManagerPool.mapManager.getMapBlocks(map.getMapModelid()));
			//计算格子之间距离，放宽一格
			if (model.getQ_range_limit() >= 0 && MapUtils.countDistance(playerGrid, monsterGrid) > model.getQ_range_limit() + 1) {
				//超出攻击距离
				log.debug("超出攻击距离");
				ManagerPool.mapManager.broadcastPlayerForceStop(player);
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.rangelimit, player, monsterId, skillId, direction));
				return;
			}
		}
       //如果是冲锋，地裂斩&旋风斩，检查人是否是可行走路径
		//2是冲锋，3是地裂斩&旋风斩，4是雷
		if (model.getQ_skill_type() == 2 || model.getQ_skill_type() == 3) {
			List<Position> roads = new ArrayList<Position>();
			roads = MapUtils.findRoads(
					ManagerPool.mapManager.getMapBlocks(map.getMapModelid()),
					player.getPosition(), monster.getPosition(), -1, false);
			// 开始走动
			if (roads.size() > 1) {
				Position pos = roads.get(roads.size()-2);
				MapManager.getInstance().changeSpecialPosition(player, pos ,model.getQ_skillID());

			}
			/*else{ panic god暂时屏蔽，和怪物同一个点也释放
				log.debug("无行走路径");
				ManagerPool.mapManager.broadcastPlayerForceStop(player);
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.noroads, player, monsterId, skillId, direction));
				return;
			}*/
		}
		
		//消耗检查
		if (player.getMp() < model.getQ_need_mp()) {
			//魔法值不足
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("魔法值不足，无法释放"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.Mpno, player, monsterId, skillId, direction));
			return;
		}

		//公共冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()))) {
			int remain = (int) ManagerPool.cooldownManager.getCooldownTime(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level())) / 1000;
			//技能公共冷却中
			if (remain > 10) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("技能冷却中，请稍后再试，剩余{1}秒"), String.valueOf(remain));
			}
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.SKILLPUBLICNO, player, monsterId, skillId, direction));
			return;
		}

		//跳跃状态不能攻击
		if (model.getQ_is_Jump_skill() == 0 && (PlayerState.JUMP.compare(player.getState()) || PlayerState.DOUBLEJUMP.compare(player.getState()))) {
			log.debug("跳跃状态不能攻击");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.JUMPNO, player, monsterId, skillId, direction));
			return;
		}
		//攻击时要下马
		if (ManagerPool.horseManager.isRidding(player)){
			ManagerPool.horseManager.unride(player);
		}
	
		
		/*panic god 屏蔽
		PlayerDaZuoManager.getInstacne().breakDaZuo(player);
		*/
		//攻击怪物不进入或延迟战斗状态
		//玩家进入攻击状态
		//player.setState(PlayerState.FIGHT);
		//玩家转向
		player.setDirection((byte) direction);
		//魔法值消耗
		player.setMp(player.getMp() - model.getQ_need_mp());
		ManagerPool.playerManager.onMpChange(player);

		//开始冷却
		double speed = ((double) player.getAttackSpeed()-100)*10;
//		System.out.println("攻击时间：" + System.currentTimeMillis());
//		System.out.println("冷却：" + (long)(model.getQ_cd() ));
//		System.out.println("公共冷却：" + (long)(model.getQ_cd() ));
		//添加技能冷却
		//ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long) (model.getQ_cd() ));
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long) (model.getQ_cd()));
		//添加技能公共冷却
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()), (long) ((model.getQ_public_cd() - speed)>300?(model.getQ_public_cd() - speed):300));

		long fightId = Config.getId();

		MessageUtil.tell_round_message(player, getFightBroadcastMessage(fightId, player, monster, skillId, direction));

		boolean action = false;
		if(script!=null){
			try{
				//技能脚本行为
				action = script.defaultAction(player, monster);
			}catch(Exception e){
				log.error(e, e);
			}
		}
		//加召唤
		if(!action){
			//技能飞行时间计算 // 新的delaytime=   delaytime *( q_public_cd-speed)/q_public_cd
			long fly = (long) (model.getQ_delay_time() *(model.getQ_public_cd() - speed)/model.getQ_public_cd());
			//单体攻击
			if (model.getQ_area_shape() == 1 && monster != null && model.getQ_trajectory_speed()>0) {
				fly += MapUtils.countDistance(player.getPosition(), monster.getPosition()) * 1000 / model.getQ_trajectory_speed();
			}
			//再加一个判断，不能小于50毫秒
			if(fly<= 50){
				fly= 50;
			}
			//log.debug("技能" + model.getQ_skillID_q_grade() + "飞行" + fly + "毫秒,速度" + model.getQ_trajectory_speed() + ",距离" +  MapUtils.countDistance(player.getPosition(), monster.getPosition()));
			if (fly > 0) {
				//延时伤害
				HitTimer timer = new HitTimer(fightId, player, monster, skill, direction, fly, true);
				TimerUtil.addTimerEvent(timer);
			} else {
				//即时伤害
				attack(fightId, player, monster, skill, direction, true);
			}
		}
	}

	/**
	 * 玩家释放友好技能
	 *
	 * @param roleId 玩家Id
	 * @param friendId Id
	 * @param skillId 攻击技能
	 * @param direction 攻击方向
	 */
	public void playerAttackFriend(Player player, long friendId, Byte targetType, int skillId, int direction) {

		//停止采集
		ManagerPool.npcManager.playerStopGather(player);

		//冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.PLAYER_ATTACK, null)) {
			log.debug("攻击者（玩家）攻击冷却");
			return;
		}
		
		//冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL, String.valueOf(skillId))) {
			long remain = (long) ManagerPool.cooldownManager.getCooldownTime(player, CooldownTypes.SKILL, String.valueOf(skillId));
			
			ManagerPool.playerManager.playercheck(player, PlayerCheckType.ATTACK_SPEED, remain);
			//技能冷却中
			if (remain > 10 * 1000) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("技能冷却中，请稍后再试，剩余{1}秒"), String.valueOf((int)(remain/1000)));
			}

			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.cooldown, player, friendId, skillId, direction));
			return;
		}

		//玩家已经死亡
		if (player.isDie()) {
			log.debug("攻击者（玩家）死亡");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.isDie, player, friendId, skillId, direction));
			return;
		}

		//定身或睡眠中
		if (FighterState.DINGSHEN.compare(player.getFightState()) || FighterState.SHUIMIAN.compare(player.getFightState())) {
			log.debug("攻击者（玩家）定身或睡眠");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.DINGSHENSHUIMIAN, player, friendId, skillId, direction));
			return;
		}

		//停止格挡
		if (PlayerState.BLOCKPREPARE.compare(player.getState()) || PlayerState.BLOCK.compare(player.getState())) {
			ManagerPool.mapManager.playerStopBlock(player);
		}

		// 游泳中
		if (PlayerState.SWIM.compare(player.getState())) {
			log.debug("攻击者（玩家）游泳中了");
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("游泳区内无法使用技能"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.SWIM, player, friendId, skillId, direction));
			return;
		}

		//技能判断
				Skill skill = ManagerPool.skillManager.getSkillByModelId(player, skillId);
				//技能是否创建时学会
				boolean defaultStudy = false;
				if (skill == null) {
					skill = new Skill();
					skill.setSkillModelId(skillId);
					skill.setSkillLevel(1);
					defaultStudy = true;
				}

				Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getRealLevel(player));
				if (model == null) {
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.modelnull, player, 0, skillId, direction));
					return;
				}

				if (defaultStudy) {
					//不是默认学会技能
					if (model.getQ_default_study() != 1) {
						log.debug("攻击者（玩家）技能不对");
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultstudyno, player, 0, skillId, direction));
						return;
					}
				}
				
			
				//获取玩家身上的变身buff
				List<Buff> shapeChangebuffs = ManagerPool.buffManager.getBuffShapeChange(player);
				//如果没有变身
				if(shapeChangebuffs.size() == 0){
					if(model.getQ_shapechange_buffid() !=0){
						log.debug("攻击者（玩家）变身技能不对");
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultshapechangeno, player, 0, skillId, direction));
						return;			
					 }
				}else{
					//有变身
					boolean defaultShapeChange = false;
					for(Buff per:shapeChangebuffs){
						if(per.getModelId()== model.getQ_shapechange_buffid()){
							defaultShapeChange = true;
							break;
						}
					}
					if(!defaultShapeChange){
						log.debug("攻击者（玩家）变身技能不对");
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultshapechangeno, player, 0, skillId, direction));
						return;		
					}
				}


		//是否主动技能
		if (model.getQ_trigger_type() != 1) {
			log.debug("攻击者（玩家）非主动技能");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.triggertypeno, player, friendId, skillId, direction));
			return;
		}

		//是否人物技能
		if (model.getQ_skill_user() != 1) {
			log.debug("攻击者（玩家）非人物技能");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.skilluserno, player, friendId, skillId, direction));
			return;
		}
		
		if (PlayerState.CHANGEMAP.compare(player.getState())) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.CHANGEMAP, player, friendId, skillId, direction));
			return;
		}

		
		Map map = ManagerPool.mapManager.getMap(player);		
		if (map == null) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.mapno, player, friendId, skillId, direction));
			return;
		}
		

		
		//获得目标
		Fighter  fighter = null;
		if (friendId > 0) {
		  if(targetType == 1){
			   fighter=map.getPlayers().get(friendId);
		   }else if(targetType == 2){
			   fighter=map.getMonsters().get(friendId);
		   }else if(targetType == 5){
			   fighter=map.getPets().get(friendId);
		   }else if(targetType == 4){
			   fighter=map.getSummonpets().get(friendId);
		   }
		  if (fighter != null) {
				if(fighter.isDie()){
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.monsterisDie, player, friendId, skillId, direction));
					return;
			}
		}
		  
		//距离检查
		if (fighter != null) {
			Grid playerGrid = MapUtils.getGrid(player.getPosition(), ManagerPool.mapManager.getMapBlocks(map.getMapModelid()));
			Grid monsterGrid = MapUtils.getGrid(fighter.getPosition(), ManagerPool.mapManager.getMapBlocks(map.getMapModelid()));
			//计算格子之间距离，放宽一格
			if (model.getQ_range_limit() >= 0 && MapUtils.countDistance(playerGrid, monsterGrid) > model.getQ_range_limit() + 1) {
				//超出攻击距离
				log.debug("超出攻击距离");
				ManagerPool.mapManager.broadcastPlayerForceStop(player);
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.rangelimit, player, friendId, skillId, direction));
				return;
			}
		}
		

		//目标不符，无法释放
		if (model.getQ_target() == 3) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("目标不符，无法释放"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.targetno, player, friendId, skillId, direction));
			return;
		}

		//消耗检查
		if (player.getMp() < model.getQ_need_mp()) {
			//魔法值不足
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("魔法值不足，无法释放"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.Mpno, player, friendId, skillId, direction));
			return;
		}

		//公共冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()))) {
			int remain = (int) ManagerPool.cooldownManager.getCooldownTime(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level())) / 1000;
			//技能公共冷却中
			if (remain > 10) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("技能冷却中，请稍后再试，剩余{1}秒"), String.valueOf(remain));
			}
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.SKILLPUBLICNO, player, friendId, skillId, direction));
			return;
		}
		//攻击时要下马
		if (ManagerPool.horseManager.isRidding(player)){
			ManagerPool.horseManager.unride(player);
		}
		
		
		//玩家转向
		player.setDirection((byte) direction);
		//魔法值消耗
		player.setMp(player.getMp() - model.getQ_need_mp());
		ManagerPool.playerManager.onMpChange(player);

		//开始冷却
		double speed = ((double) player.getAttackSpeed()-100)*10;
//		System.out.println("攻击时间：" + System.currentTimeMillis());
//		System.out.println("冷却：" + (long)(model.getQ_cd() ));
//		System.out.println("公共冷却：" + (long)(model.getQ_cd() ));
		//添加技能冷却
		//ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long) (model.getQ_cd() ));
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long) (model.getQ_cd()));
		//添加技能公共冷却
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()), (long) ((model.getQ_public_cd() - speed)>300?(model.getQ_public_cd() - speed):300));

		long fightId = Config.getId();

		MessageUtil.tell_round_message(player, getFightBroadcastMessage(fightId, player, fighter, skillId, direction));
		/*
		 * 给玩家加增益buffp，让客户端去做延迟毫秒
		 */
		String[] buffs = model.getQ_passive_buff()
				.split(Symbol.FENHAO_REG);
		for (int i = 0; i < buffs.length; i++) {
			// 获取BUFF模型
			Q_buffBean buffModel = ManagerPool.dataManager.q_buffContainer.getMap().get(Integer.parseInt(buffs[i]));
			if (buffModel == null)
				continue;	
			List<Fighter> adders = new ArrayList<Fighter>();
			// 同队队友添加buff
			if (buffModel.getQ_target() == 1) {
				adders.add(player);
			}else if (buffModel.getQ_target() == 4) {
				if (player != null) {
					// 角色有组队 给自己和队员加buff
					if (player.getTeamid() > 0) {
						TeamInfo team = ManagerPool.teamManager.getTeam(player
								.getTeamid());
						if (team != null) {
							/*panic god 屏蔽
							TeamMemberInfo[] teamMember = team.getMemberinfo()
									.toArray(new TeamMemberInfo[0]);
							for (TeamMemberInfo teamMemberInfo : teamMember) {
								Player xplayer = ManagerPool.playerManager
										.getOnLinePlayer(teamMemberInfo
												.getMemberid());
								if (xplayer.getLine() == player.getLine()
										&& xplayer.getMap() == player.getMap()) {
							*/	
							List<Player> mapSameTeam = TeamManager.getInstance().getAreaSameTeam(player);
							for (Player xplayer : mapSameTeam) {
								if (xplayer.getLine() == player.getLine()
										&& xplayer.getMap() == player.getMap()) {
									adders.add(xplayer);
									//对应生命之光，召唤怪也要加
									if(buffModel.getQ_buff_id() == BuffConst.LIGHTOFLIFE){
										SummonPet summonpet = ManagerPool.summonpetOptManager.onwerBuff(xplayer);
										if(summonpet!= null){
											adders.add(summonpet);
										}
									}
								}
							}
						}
					} else {
						// 角色没有组队 只给自己加buff
						adders.add(player);
					}
				}
			} else {
				adders.add(fighter);
			}
			int buffadd= 0;
			int buffaddpercent = 0;
			/* xuliang hide 移置 BuffManager 处理
//			if(buffModel.getQ_buff_id() == BuffConst.LIGHTOFLIFE){
//				//施放后对范围内的所有队员及队员召唤物添加一个增加最大生命上限的buff
//				//增加效果= (10 + 智力/5 )%
//				buffaddpercent = player.getAttibute_one()[3]/5;
//			}else if(buffModel.getQ_buff_id() == BuffConst.GUARDIANSPIRIT){
//				// 增加效果= (10 + 敏捷/7 )% reduce_injure 取数组,到具体取数组的时候，是%
//				buffaddpercent = player.getAttibute_one()[2]/7;
//			}else if(buffModel.getQ_buff_id() == BuffConst.BATTLEFORCE){
//				//增加效果= 20+智力/7
//				buffadd = player.getAttibute_one()[3]/7;
//			}else if(buffModel.getQ_buff_id() == BuffConst.GUARDIANOFLIGHT){
//			    //增加效果= 20+智力/5
//				buffadd = player.getAttibute_one()[3]/5;
//			}
//			else if(buffModel.getQ_buff_id() == BuffConst.HEALINGLIGHT){
//				// 增加效果= 20+智力/10
//				buffadd = player.getAttibute_one()[3]/10;
//			}
//			else if(buffModel.getQ_buff_id() == BuffConst.WINDRUNNER){
//				// 施放后对自己添加一个增加移动速度、闪避率100%的buff
//			}
			buffadd =  player.getAttibute_one()[3]/10;
			*/
			for (int j = 0; j < adders.size(); j++) {
				if (buffModel.getQ_target() == 1 || buffModel.getQ_target() == 4) {
					ManagerPool.buffManager.addBuff( adders.get(j), player, 
							Integer.parseInt(buffs[i]), 0,buffadd, buffaddpercent);
				} else if(buffModel.getQ_target() == 2) {
					ManagerPool.buffManager.addBuff( player, adders.get(j),
							Integer.parseInt(buffs[i]), 0,buffadd, buffaddpercent);
				}
					
					
			}
		}
		}
	}
	/**
	 * 玩家攻击玩家
	 *
	 * @param roleId 玩家Id
	 * @param targetId 怪物Id
	 * @param skillId 攻击技能
	 * @param direction 攻击方向
	 */
	public void playerAttackPlayer(Player player, long targetId, int skillId, int direction) {
		//停止采集
		ManagerPool.npcManager.playerStopGather(player);

		//冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.PLAYER_ATTACK, null)) {
			log.debug("攻击者（玩家）攻击冷却");
			return;
		}
				
		//冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL, String.valueOf(skillId))) {
			long remain = (long) ManagerPool.cooldownManager.getCooldownTime(player, CooldownTypes.SKILL, String.valueOf(skillId));
			
			ManagerPool.playerManager.playercheck(player, PlayerCheckType.ATTACK_SPEED, remain);
			//技能冷却中
			if (remain > 10 * 1000) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("技能冷却中，请稍后再试，剩余{1}秒"), String.valueOf((int)(remain/1000)));
			}

			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//玩家已经死亡
		if (player.isDie()) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//定身或睡眠中
		if (FighterState.DINGSHEN.compare(player.getFightState()) || FighterState.SHUIMIAN.compare(player.getFightState())) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//停止格挡
		if (PlayerState.BLOCKPREPARE.compare(player.getState()) || PlayerState.BLOCK.compare(player.getState())) {
			ManagerPool.mapManager.playerStopBlock(player);
		}

		// 游泳中
		if (PlayerState.SWIM.compare(player.getState())) {
			log.debug("攻击者（玩家）游泳中了");
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("游泳区内无法使用技能"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//技能判断
		Skill skill = ManagerPool.skillManager.getSkillByModelId(player, skillId);
		//技能是否创建时学会
		boolean defaultStudy = false;
		if (skill == null) {
			skill = new Skill();
			skill.setSkillModelId(skillId);
			skill.setSkillLevel(1);
			defaultStudy = true;
		}

		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getRealLevel(player));
		if (model == null) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		if (defaultStudy) {
			//不是默认学会技能
			if (model.getQ_default_study() != 1) {
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
				return;
			}
		}

        //获取玩家身上的变身buff
		List<Buff> shapeChangebuffs = ManagerPool.buffManager.getBuffShapeChange(player);
		//如果没有变身
		if(shapeChangebuffs.size() == 0){
			if(model.getQ_shapechange_buffid() !=0){
				log.debug("攻击者（玩家）变身技能不对");
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultshapechangeno, player, 0, skillId, direction));
				return;			
			 }
		}else{
			//有变身
			boolean defaultShapeChange = false;
			for(Buff per:shapeChangebuffs){
				if(per.getModelId()== model.getQ_shapechange_buffid()){
					defaultShapeChange = true;
					break;
				}
			}
			if(!defaultShapeChange){
				log.debug("攻击者（玩家）变身技能不对");
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultshapechangeno, player, 0, skillId, direction));
				return;		
			}
		}
		
		//是否主动技能 
		if (model.getQ_trigger_type() != 1) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//是否人物技能
		if (model.getQ_skill_user() != 1) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//获得怪物
		Player target = null;
		if (targetId != 0) {
			target = ManagerPool.playerManager.getPlayer(targetId);
		}
		
		//技能目标选择
		ISkillScript script = null;
		if(model.getQ_skill_script() > 0){
			script = (ISkillScript) ManagerPool.scriptManager.getScript(model.getQ_skill_script());
		}
		if(script!=null){
			try{
				if(!script.canUse(player, target, direction)){
					return;
				}
			}catch(Exception e){
				log.error(e, e);
			}
		}
		
		if(target==null){
			log.error("玩家(" + player.getId() + ")对玩家(" + targetId + ")攻击没有目标");
			return;
		}

		if (PlayerState.CHANGEMAP.compare(player.getState())) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		Map map = ManagerPool.mapManager.getMap(player);
		if (map == null) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}
		
		//梅花副本不能使用群攻
		if(map.getMapModelid()==42121 && model.getQ_area_shape()!=1){
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("本地图不能使用此技能"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//目标检查 （单体目标且对象不为自己时）
		if (model.getQ_area_shape() == 1 && model.getQ_target() != 1 && (target == null || target.isDie())) {
			//无目标错误
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("请先选定一个释放目标"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//消耗检查
		if (player.getMp() < model.getQ_need_mp()) {
			//魔法值不足
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("魔法值不足，无法释放"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//公共冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()))) {
			int remain = (int) ManagerPool.cooldownManager.getCooldownTime(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level())) / 1000;
			log.debug("公共冷却中");
			//技能公共冷却中
			if (remain > 10) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("技能冷却中，请稍后再试，剩余{1}秒"), String.valueOf(remain));
			}
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//跳跃状态不能攻击
		if (model.getQ_is_Jump_skill() == 0 && (PlayerState.JUMP.compare(player.getState()) || PlayerState.DOUBLEJUMP.compare(player.getState()))) {
			log.debug("跳跃状态不能攻击");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer.getMap().get(map.getMapModelid());
		
		//! PK值大于10无法攻击
		if (player.getPkValue() >= 10 && mapBean.getQ_map_pkpoint() == 1){
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的杀孽过重，系统将限制您恶意攻击其他玩家的行为"));
			return;
		}
		
		Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());
		if (target != null) {
			if ( model.getQ_target() != 2) {
				//不在pk列表中
				if (!player.getEnemys().containsKey(target.getId()) && target.getPkValue() == 0) {
					//PK状态检查
					if (player.getPkState() == 0) {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("当前PK模式，无法对其造成伤害"));
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						return;
					}else if (player.getPkState() == 1) {
						//同队伍检查
						if (player.getTeamid() == target.getTeamid() && player.getTeamid() != 0) {
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("当前PK模式，无法对其造成伤害"));
							MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
							return;
						}
						
						//同战盟检查
						if ((player.getGuildId() == target.getGuildId()  || GuildServerManager.getInstance().isFriendGuild(player.getGuildId(), target.getGuildId())) && player.getGuildId() != 0) {
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("当前PK模式，无法对其造成伤害"));
							MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
							return;
						}
					}
				}else{
					log.info("玩家(" + player.getId() + ")PK状态为(" + player.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(player.getState()) + ")对玩家(" + target.getId() + ")PK状态为(" + target.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(target.getState()) + ")攻击因为在仇恨列表中");	
				}

				// 游泳中
				if (PlayerState.SWIM.compare(target.getState())) {
					/* xuliang
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("游泳区内无法被攻击"));
					*/
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
					return;
				}

				int pkMinLevel = DEFAULT_PK_MIN_LEVEL, pkDiffLevel = DEFAULT_PK_DIFF_LEVEL;
				Q_globalBean pkMinLevelGolbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PK_MIN_LEVEL.getValue());
				if(pkMinLevelGolbalBean != null) pkMinLevel = pkMinLevelGolbalBean.getQ_int_value();
				Q_globalBean pkDiffLevelGolbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PK_DIFF_LEVEL.getValue());
				if(pkDiffLevelGolbalBean != null) pkDiffLevel = pkDiffLevelGolbalBean.getQ_int_value();
				
				//自己等级检查
				if (player.getLevel() < pkMinLevel && mapBean.getQ_map_pkprotection() == 1) {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您处于"+pkMinLevel+"级以下新手保护期内，无法对其他玩家造成伤害"));
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
					return;
				}

				//敌人等级检查
				if (target.getLevel() < pkMinLevel && mapBean.getQ_map_pkprotection() == 1 ) {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您无法对低于"+pkMinLevel+"级以下处于新手保护期内的玩家造成伤害"));
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
					return;
				}

				if(!FighterState.FORCEPK.compare(target.getFightState())){
					//Pk保护Buff
					if (FighterState.PKBAOHU.compare(target.getFightState())) {
						Buff buff = ManagerPool.buffManager.getBuffByType(target, BuffType.PKPROTECT).get(0);
						long remain = buff.getStart() + buff.getTotalTime() - System.currentTimeMillis();
						if (remain > 0) {
							int sec = (int) (remain / 1000);
							if (remain > 5000) {
								if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.NOTIFY_KILL, null)) {
									MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
									return;
								} else {
									ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.NOTIFY_KILL, null, 10 * 1000);
								}
							}
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("该玩家处于复活后的PK保护时间内，您目前无法对其造成伤害，剩余时间：{1}分{2}秒"), String.valueOf(sec / 60), String.valueOf(sec % 60));
							MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
							
							//玩家进入攻击状态
							player.setState(PlayerState.FIGHT);
							//设置玩家恶意攻击
							player.setMalicious(1);
							
							//主动Pk移除被杀保护Buff
							if (FighterState.PKBAOHU.compare(player.getFightState()) ) {
								ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_FOR_KILLED);
								log.info("玩家(" + player.getId() + ")PK状态为(" + player.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(player.getState()) + ")敌人列表为(" + MessageUtil.castListToString(player.getEnemys().values()) + ")对玩家(" + target.getId() + ")PK状态为(" + target.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(target.getState()) + ")攻击导致和平保护buff消失");	
								MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("您主动发起了对其他玩家的PK，和平保护BUFF消失了"));
							}
							return;
						}
					}
		
					//夜晚挂机和平buff
					if (FighterState.PKBAOHUFORNIGHT.compare(target.getFightState())) {
						Buff buff = ManagerPool.buffManager.getBuffByType(target, BuffType.PROTECTFORNIGHT).get(0);
						long remain = buff.getStart() + buff.getTotalTime() - System.currentTimeMillis();
						if (remain > 0) {
							int sec = (int) (remain / 1000);
							if (remain > 5000) {
								if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.NOTIFY_KILL, null)) {
									MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
									return;
								} else {
									ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.NOTIFY_KILL, null, 10 * 1000);
								}
							}
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("玩家处于夜晚挂机PK保护时间内，您目前无法对其造成伤害，剩余时间：{1}分{2}秒"), String.valueOf(sec / 60), String.valueOf(sec % 60));
							MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
							
							//玩家进入攻击状态
							player.setState(PlayerState.FIGHT);
							//设置玩家夜间恶意攻击
							player.setMalicious(2);
							
							//主动Pk移除夜晚保护Buff
							if (FighterState.PKBAOHUFORNIGHT.compare(player.getFightState()) ) {
								ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_IN_NIGHT);
								log.error("玩家(" + player.getId() + ")PK状态为(" + player.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(player.getState()) + ")敌人列表为(" + MessageUtil.castListToString(player.getEnemys().values()) + ")对玩家(" + target.getId() + ")PK状态为(" + target.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(target.getState()) + ")攻击导致夜晚和平保护buff消失");	
							}
							return;
						}
					}
		
					//等级差检查(地图开启等级差保护)
					if (Math.abs(player.getLevel() - target.getLevel()) > pkDiffLevel && mapBean.getQ_map_pk() == 1) {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("等级差>"+pkDiffLevel+"级，互相之间无法造成伤害"));
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						return;
					}
		
					//自己安全区检查
					if (ManagerPool.mapManager.isSafe(player.getPosition(), player.getMapModelId())) {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，安全区内禁止PK"));
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						return;
					}
		
					//目标安全区检查
					if (ManagerPool.mapManager.isSafe(target.getPosition(), player.getMapModelId())) {
						/* xuliang
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，无法PK处于安全区内的玩家"));
						*/
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						return;
					}
				}
			
			}
			
			//距离检查
			//计算格子之间距离，放宽一格
			Grid playerGrid = MapUtils.getGrid(player.getPosition(), blocks);
			Grid monsterGrid = MapUtils.getGrid(target.getPosition(), blocks);
			if (model.getQ_range_limit() >= 0 && MapUtils.countDistance(playerGrid, monsterGrid) > model.getQ_range_limit() + 1) {
				//超出攻击距离
				log.debug("超出攻击距离");
				ManagerPool.mapManager.broadcastPlayerForceStop(player);
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
				return;
			}
			
			//如果是冲锋，地裂斩&旋风斩，检查人是否是可行走路径		
			if ((model.getQ_skill_type() == 2 || model.getQ_skill_type() == 3)) {
				List<Position> roads = new ArrayList<Position>();
				roads = MapUtils.findRoads(
						ManagerPool.mapManager.getMapBlocks(map.getMapModelid()),
						player.getPosition(), target.getPosition(), -1, false);
				// 开始走动
			if (roads.size() > 1) {
				Position pos = roads.get(roads.size()-2);
				MapManager.getInstance().changeSpecialPosition(player, pos ,model.getQ_skillID());
				}/*else{ panic god暂时屏蔽，和怪物同一个点也释放
					log.debug("无行走路径");
					ManagerPool.mapManager.broadcastPlayerForceStop(player);
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.noroads, player, targetId, skillId, direction));
					return;
				}*/
			}
			
			IAttackCheckScript checkscript = (IAttackCheckScript) ManagerPool.scriptManager.getScript(ScriptEnum.CHECKATTACK);
			if (checkscript != null) {
				try {
					if(!checkscript.check(player, target)){
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						return;
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			} else {
				log.error("攻击检查脚本不存在！");
			}
		}
		PlayerDaZuoManager.getInstacne().breakDaZuo(player);
		if (model.getQ_target() != 2 ) {
			//玩家进入攻击状态
			player.setState(PlayerState.FIGHT);

			//主动Pk移除被杀保护Buff
			if (FighterState.PKBAOHU.compare(player.getFightState()) ) {
				player.setMalicious(1);//设置玩家恶意攻击
				ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_FOR_KILLED);
				log.error("玩家(" + player.getId() + ")PK状态为(" + player.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(player.getState()) + ")敌人列表为(" + MessageUtil.castListToString(player.getEnemys().values()) + ")对玩家(" + target.getId() + ")PK状态为(" + target.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(target.getState()) + ")攻击导致和平保护buff消失");	
				MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("您主动发起了对其他玩家的PK，和平保护BUFF消失了"));
			}

			//主动Pk移除夜晚保护Buff
			if (FighterState.PKBAOHUFORNIGHT.compare(player.getFightState()) ) {
				player.setMalicious(2);//设置玩家夜间恶意攻击
				ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_IN_NIGHT);
				log.error("玩家(" + player.getId() + ")PK状态为(" + player.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(player.getState()) + ")敌人列表为(" + MessageUtil.castListToString(player.getEnemys().values()) + ")对玩家(" + target.getId() + ")PK状态为(" + target.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(target.getState()) + ")攻击导致夜晚和平保护buff消失");	
			}
		}
		

		log.error("玩家(" + player.getId() + ")PK状态为(" + player.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(player.getState()) + ")敌人列表为(" + MessageUtil.castListToString(player.getEnemys().values()) + ")对玩家(" + target.getId() + ")PK状态为(" + target.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(target.getState()) + ")攻击");	
		//攻击时要下马
		if (ManagerPool.horseManager.isRidding(player)){
			ManagerPool.horseManager.unride(player);
		}
		//玩家转向
		player.setDirection((byte) direction);

		//魔法值消耗
		player.setMp(player.getMp() - model.getQ_need_mp());
		ManagerPool.playerManager.onMpChange(player);

		//开始冷却
		double speed = (double) 1;
		//结婚几个技能不受攻击速度影响
		if ( !(model.getQ_skillID()==25005 || model.getQ_skillID()==25006 || model.getQ_skillID()==25007) ) {
			speed = ((double) player.getAttackSpeed()-100)*10;
		}
		//添加技能冷却
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long) (model.getQ_cd()));
		//添加技能公共冷却
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()), (long) ((model.getQ_public_cd() - speed)>300?(model.getQ_public_cd() - speed):300));
		long fightId = Config.getId();

		MessageUtil.tell_round_message(player, getFightBroadcastMessage(fightId, player, target, skillId, direction));

		boolean action = false;
		if(script!=null){
			try{
				//技能脚本行为
				action = script.defaultAction(player, target);
			}catch(Exception e){
				log.error(e, e);
			}
		}
		if(!action){
			//技能飞行时间计算
			//long fly = model.getQ_delay_time();
			long fly = (long) (model.getQ_delay_time() *(model.getQ_public_cd() - speed)/model.getQ_public_cd());
			//单体攻击
			if (model.getQ_area_shape() == 1 && target != null && model.getQ_trajectory_speed()>0) {
				fly += MapUtils.countDistance(player.getPosition(), target.getPosition()) * 1000 / model.getQ_trajectory_speed();
			}
            if(fly <= 50){
            	fly = 50;
            }
			if (fly > 0) {
				//延时伤害
				HitTimer timer = new HitTimer(fightId, player, target, skill, direction, fly, true);
				TimerUtil.addTimerEvent(timer);
			} else {
				//即时伤害
				attack(fightId, player, target, skill, direction, true);
			}
		}
		
	}

	/**
	 * 玩家攻击宠物
	 *
	 * @param roleId 玩家Id
	 * @param ownerId 主人Id
	 * @param targetId 宠物Id
	 * @param skillId 攻击技能
	 * @param direction 攻击方向
	 */
	public void playerAttackPet(Player player, long ownerId, long targetId, int skillId, int direction) {
		//停止采集
		ManagerPool.npcManager.playerStopGather(player);

		//玩家已经死亡
		if (player.isDie()) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//定身或睡眠中
		if (FighterState.DINGSHEN.compare(player.getFightState()) || FighterState.SHUIMIAN.compare(player.getFightState())) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//停止格挡
		if (PlayerState.BLOCKPREPARE.compare(player.getState()) || PlayerState.BLOCK.compare(player.getState())) {
			ManagerPool.mapManager.playerStopBlock(player);
		}

		// 游泳中
		if (PlayerState.SWIM.compare(player.getState())) {
			log.debug("攻击者（玩家）游泳中了");
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("游泳区内无法使用技能"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//技能判断
		Skill skill = ManagerPool.skillManager.getSkillByModelId(player, skillId);
		//技能是否创建时学会
		boolean defaultStudy = false;
		if (skill == null) {
			skill = new Skill();
			skill.setSkillModelId(skillId);
			skill.setSkillLevel(1);
			defaultStudy = true;
		}

		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getRealLevel(player));
		if (model == null) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		if (defaultStudy) {
			//不是默认学会技能
			if (model.getQ_default_study() != 1) {
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
				return;
			}
		}

        //获取玩家身上的变身buff
		List<Buff> shapeChangebuffs = ManagerPool.buffManager.getBuffShapeChange(player);
		//如果没有变身
		if(shapeChangebuffs.size() == 0){
			if(model.getQ_shapechange_buffid() !=0){
				log.debug("攻击者（玩家）变身技能不对");
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultshapechangeno, player, 0, skillId, direction));
				return;			
			 }
		}else{
			//有变身
			boolean defaultShapeChange = false;
			for(Buff per:shapeChangebuffs){
				if(per.getModelId()== model.getQ_shapechange_buffid()){
					defaultShapeChange = true;
					break;
				}
			}
			if(!defaultShapeChange){
				log.debug("攻击者（玩家）变身技能不对");
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultshapechangeno, player, 0, skillId, direction));
				return;		
			}
		}
		
		//是否主动技能 
		if (model.getQ_trigger_type() != 1) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//是否人物技能
		if (model.getQ_skill_user() != 1) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		if (PlayerState.CHANGEMAP.compare(player.getState())) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		Map map = ManagerPool.mapManager.getMap(player);
		if (map == null) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}
		
		//梅花副本不能使用群攻
		if(map.getMapModelid()==42121 && model.getQ_area_shape()!=1){
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("本地图不能使用此技能"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//获得怪物
		Player owner = null;
		Pet target = null;
		if (ownerId == player.getId()) {
			//宠物主人是自己！
			return;
		}

		if (ownerId > 0) {
			owner = ManagerPool.playerManager.getPlayer(ownerId);
			if (owner != null && targetId > 0) {
				target = ManagerPool.petInfoManager.getPet(owner, targetId);
			}
		}
		
		//技能目标选择
		ISkillScript script = null;
		if(model.getQ_skill_script() > 0){
			script = (ISkillScript) ManagerPool.scriptManager.getScript(model.getQ_skill_script());
		}
		if(script!=null){
			try{
				if(!script.canUse(player, target, direction)){
					return;
				}
			}catch(Exception e){
				log.error(e, e);
			}
		}
		
		if(target==null){
			log.error("玩家(" + player.getId() + ")对宠物(" + targetId + ")主人为(" + ownerId + ")攻击没有目标");
			return;
		}

		//目标检查 （单体目标且对象不为自己时）
		if (model.getQ_area_shape() == 1 && model.getQ_target() != 1 && (target == null || target.isDie())) {
			//无目标错误
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("请先选定一个释放目标"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//消耗检查
		if (player.getMp() < model.getQ_need_mp()) {
			//魔法值不足
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("魔法值不足，无法释放"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()))) {
			int remain = (int) ManagerPool.cooldownManager.getCooldownTime(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID())) / 1000;
			log.debug("技能冷却中");
			//技能冷却中
			if (remain > 10) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("技能冷却中，请稍后再试，剩余{1}秒"), String.valueOf(remain));
			}
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//公共冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()))) {
			int remain = (int) ManagerPool.cooldownManager.getCooldownTime(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level())) / 1000;
			log.debug("公共冷却中");
			//技能公共冷却中
			if (remain > 10) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("技能冷却中，请稍后再试，剩余{1}秒"), String.valueOf(remain));
			}
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//跳跃状态不能攻击
		if (model.getQ_is_Jump_skill() == 0 && (PlayerState.JUMP.compare(player.getState()) || PlayerState.DOUBLEJUMP.compare(player.getState()))) {
			log.debug("跳跃状态不能攻击");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer.getMap().get(map.getMapModelid());
		
		if (player.getPkValue() >= 10 && mapBean.getQ_map_pkpoint() == 1){
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的杀孽过重，系统将限制您恶意攻击其他玩家的行为"));
			return;
		}
		
		Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());
		if (target != null && owner != null) {
			if (PetRunState.SWIM == target.getRunState()) {
				/* xuliang
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("游泳区内无法被攻击"));
				*/
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
				return;
			}

			//不在pk列表中
			if (!player.getEnemys().containsKey(owner.getId()) && owner.getPkValue() == 0) {
				//PK状态检查
				if (player.getPkState() == 0) {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("当前PK模式，无法对其造成伤害"));
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
					return;
				} else if (player.getPkState() == 1) {
					//同队伍检查
					if (player.getTeamid() == owner.getTeamid() && player.getTeamid() != 0) {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("当前PK模式，无法对其造成伤害"));
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						return;
					}
					
					//同战盟检查
					if ((player.getGuildId() == owner.getGuildId()  || GuildServerManager.getInstance().isFriendGuild(owner.getGuildId(), player.getGuildId())) && player.getGuildId()!=0) {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("当前PK模式，无法对其造成伤害"));
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						return;
					}
				}
			}

			int pkMinLevel = DEFAULT_PK_MIN_LEVEL, pkDiffLevel = DEFAULT_PK_DIFF_LEVEL;
			Q_globalBean pkMinLevelGolbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PK_MIN_LEVEL.getValue());
			if(pkMinLevelGolbalBean != null) pkMinLevel = pkMinLevelGolbalBean.getQ_int_value();
			Q_globalBean pkDiffLevelGolbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PK_DIFF_LEVEL.getValue());
			if(pkDiffLevelGolbalBean != null) pkDiffLevel = pkDiffLevelGolbalBean.getQ_int_value();
			
			//自己等级检查
			if (player.getLevel() < pkMinLevel && mapBean.getQ_map_pkprotection() == 1) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您处于"+pkMinLevel+"级以下新手保护期内，无法对其他玩家造成伤害"));
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
				return;
			}

			//敌人等级检查
			if (owner.getLevel() < pkMinLevel && mapBean.getQ_map_pkprotection() == 1) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您无法对低于"+pkMinLevel+"级以下处于新手保护期内的玩家造成伤害"));
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
				return;
			}

			if(!FighterState.FORCEPK.compare(owner.getFightState())){
				//Pk保护Buff
				if (FighterState.PKBAOHU.compare(owner.getFightState())) {
					Buff buff = ManagerPool.buffManager.getBuffByType(owner, BuffType.PKPROTECT).get(0);
					long remain = buff.getStart() + buff.getTotalTime() - System.currentTimeMillis();
					if (remain > 0) {
						int sec = (int) (remain / 1000);
						if (remain > 5000) {
							if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.NOTIFY_KILL, null)) {
								MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
								return;
							} else {
								ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.NOTIFY_KILL, null, 10 * 1000);
							}
						}
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("该玩家处于复活后的PK保护时间内，您目前无法对其造成伤害，剩余时间：{1}分{2}秒"), String.valueOf(sec / 60), String.valueOf(sec % 60));
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						
						//玩家进入攻击状态
						player.setState(PlayerState.FIGHT);
						//设置玩家恶意攻击
						player.setMalicious(1);
						
						//主动Pk移除被杀保护Buff
						if (FighterState.PKBAOHU.compare(player.getFightState())) {
							ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_FOR_KILLED);
							log.error("玩家(" + player.getId() + ")PK状态为(" + player.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(player.getState()) + ")敌人列表为(" + MessageUtil.castListToString(player.getEnemys().values()) + ")对宠物(" + target.getId() + ")攻击导致和平保护buff消失");
							MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("您主动发起了对其他玩家的PK，和平保护BUFF消失了"));
						}
						return;
					}
				}
	
				//夜晚挂机和平buff
				if (FighterState.PKBAOHUFORNIGHT.compare(owner.getFightState())) {
					Buff buff = ManagerPool.buffManager.getBuffByType(owner, BuffType.PROTECTFORNIGHT).get(0);
					long remain = buff.getStart() + buff.getTotalTime() - System.currentTimeMillis();
					if (remain > 0) {
						int sec = (int) (remain / 1000);
						if (remain > 5000) {
							if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.NOTIFY_KILL, null)) {
								MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
								return;
							} else {
								ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.NOTIFY_KILL, null, 10 * 1000);
							}
						}
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("玩家处于夜晚挂机PK保护时间内，您目前无法对其造成伤害，剩余时间：{1}分{2}秒"), String.valueOf(sec / 60), String.valueOf(sec % 60));
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						
						//玩家进入攻击状态
						player.setState(PlayerState.FIGHT);
						//设置玩家夜间恶意攻击
						player.setMalicious(2);
						
						//主动Pk移除夜晚保护Buff
						if (FighterState.PKBAOHUFORNIGHT.compare(player.getFightState())) {
							ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_IN_NIGHT);
							log.error("玩家(" + player.getId() + ")PK状态为(" + player.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(player.getState()) + ")敌人列表为(" + MessageUtil.castListToString(player.getEnemys().values()) + ")对宠物(" + target.getId() + ")攻击导致夜晚和平保护buff消失");
						}
						return;
					}
				}
	
				//等级差检查(地图开启等级差保护)
				if (Math.abs(player.getLevel() - owner.getLevel()) > pkDiffLevel && mapBean.getQ_map_pk() == 1) {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("等级差>"+pkDiffLevel+"级，互相之间无法造成伤害"));
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
					return;
				}
	
				//自己安全区检查
				if (ManagerPool.mapManager.isSafe(player.getPosition(), player.getMapModelId())) {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，安全区内禁止PK"));
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
					return;
				}
	
				//目标安全区检查
				if (ManagerPool.mapManager.isSafe(target.getPosition(), player.getMapModelId())) {
					/* xuliang
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，无法PK处于安全区内的美人"));
					*/
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
					return;
				}
			}
			
			//距离检查
			//计算格子之间距离，放宽一格
			Grid playerGrid = MapUtils.getGrid(player.getPosition(), blocks);
			Grid monsterGrid = MapUtils.getGrid(target.getPosition(), blocks);
			if (model.getQ_range_limit() >= 0 && MapUtils.countDistance(playerGrid, monsterGrid) > model.getQ_range_limit() + 1) {
				//超出攻击距离
				log.debug("超出攻击距离");
				ManagerPool.mapManager.broadcastPlayerForceStop(player);
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
				return;
			}
			//如果是冲锋，地裂斩&旋风斩，检查人是否是可行走路径
			if (model.getQ_skill_type() == 2 || model.getQ_skill_type() == 3) {
				List<Position> roads = new ArrayList<Position>();
				roads = MapUtils.findRoads(
						ManagerPool.mapManager.getMapBlocks(map.getMapModelid()),
						player.getPosition(), target.getPosition(), -1, false);
				// 开始走动
			if (roads.size() > 1) {
				Position pos = roads.get(roads.size()-2);
				MapManager.getInstance().changeSpecialPosition(player, pos ,model.getQ_skillID());
				}
			/*else{ panic god暂时屏蔽，和怪物同一个点也释放else{
					log.debug("无行走路径");
					ManagerPool.mapManager.broadcastPlayerForceStop(player);
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.noroads, player, targetId, skillId, direction));
					return;
				}*/
			}
			IAttackCheckScript checkscript = (IAttackCheckScript) ManagerPool.scriptManager.getScript(ScriptEnum.CHECKATTACK);
			if (checkscript != null) {
				try {
					if(!checkscript.check(player, target)){
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						return;
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			} else {
				log.error("攻击检查脚本不存在！");
			}
		}
		PlayerDaZuoManager.getInstacne().breakDaZuo(player);
		//玩家进入攻击状态
		player.setState(PlayerState.FIGHT);

		//主动Pk移除被杀保护Buff
		if (FighterState.PKBAOHU.compare(player.getFightState())) {
			player.setMalicious(1);//设置玩家恶意攻击
			ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_FOR_KILLED);
			log.error("玩家(" + player.getId() + ")PK状态为(" + player.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(player.getState()) + ")敌人列表为(" + MessageUtil.castListToString(player.getEnemys().values()) + ")对宠物(" + target.getId() + ")攻击导致和平保护buff消失");
			MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("您主动发起了对其他玩家的PK，和平保护BUFF消失了"));
		}

		//主动Pk移除夜晚保护Buff
		if (FighterState.PKBAOHUFORNIGHT.compare(player.getFightState())) {
			player.setMalicious(2);//设置玩家夜间恶意攻击
			ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_IN_NIGHT);
			log.error("玩家(" + player.getId() + ")PK状态为(" + player.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(player.getState()) + ")敌人列表为(" + MessageUtil.castListToString(player.getEnemys().values()) + ")对宠物(" + target.getId() + ")攻击导致夜晚和平保护buff消失");
		}
		//攻击时要下马
		if (ManagerPool.horseManager.isRidding(player)){
			ManagerPool.horseManager.unride(player);
		}
		//玩家转向
		player.setDirection((byte) direction);

		//魔法值消耗
		player.setMp(player.getMp() - model.getQ_need_mp());
		ManagerPool.playerManager.onMpChange(player);

		//开始冷却
		double speed = ((double) player.getAttackSpeed()-100)*10;
		//添加技能冷却
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long) (model.getQ_cd()));
		//添加技能公共冷却
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()), (long) ((model.getQ_public_cd() - speed)>300?(model.getQ_public_cd() - speed):300));

		long fightId = Config.getId();

		MessageUtil.tell_round_message(player, getFightBroadcastMessage(fightId, player, target, skillId, direction));
		
		boolean action = false;
		if(script!=null){
			try{
				//技能脚本行为
				action = script.defaultAction(player, target);
			}catch(Exception e){
				log.error(e, e);
			}
		}
		if(!action){
			//技能飞行时间计算
			//long fly = model.getQ_delay_time();
			long fly = (long) (model.getQ_delay_time() *(model.getQ_public_cd() - speed)/model.getQ_public_cd());
			//单体攻击
			if (model.getQ_area_shape() == 1 && target != null && model.getQ_trajectory_speed()>0) {
				fly += MapUtils.countDistance(player.getPosition(), target.getPosition()) * 1000 / model.getQ_trajectory_speed();
			}
	
			if(fly <= 50){
				fly = 50;
			}
			if (fly > 0) {
				//延时伤害
				HitTimer timer = new HitTimer(fightId, player, target, skill, direction, fly, true);
				TimerUtil.addTimerEvent(timer);
			} else {
				//即时伤害
				attack(fightId, player, target, skill, direction, true);
			}
		}
	}
	/**
	 * 玩家攻击召唤怪
	 *
	 * @param roleId 玩家Id
	 * @param ownerId 主人Id
	 * @param targetId 宠物Id
	 * @param skillId 攻击技能
	 * @param direction 攻击方向
	 */
	public void playerAttackSummonPet(Player player, long ownerId, long targetId, int skillId, int direction) {
		//停止采集
		ManagerPool.npcManager.playerStopGather(player);

		//玩家已经死亡
		if (player.isDie()) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//定身或睡眠中
		if (FighterState.DINGSHEN.compare(player.getFightState()) || FighterState.SHUIMIAN.compare(player.getFightState())) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//停止格挡
		if (PlayerState.BLOCKPREPARE.compare(player.getState()) || PlayerState.BLOCK.compare(player.getState())) {
			ManagerPool.mapManager.playerStopBlock(player);
		}

		// 游泳中
		if (PlayerState.SWIM.compare(player.getState())) {
			log.debug("攻击者（玩家）游泳中了");
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("游泳区内无法使用技能"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//技能判断
		Skill skill = ManagerPool.skillManager.getSkillByModelId(player, skillId);
		//技能是否创建时学会
		boolean defaultStudy = false;
		if (skill == null) {
			skill = new Skill();
			skill.setSkillModelId(skillId);
			skill.setSkillLevel(1);
			defaultStudy = true;
		}

		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getRealLevel(player));
		if (model == null) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		if (defaultStudy) {
			//不是默认学会技能
			if (model.getQ_default_study() != 1) {
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
				return;
			}
		}

        //获取玩家身上的变身buff
		List<Buff> shapeChangebuffs = ManagerPool.buffManager.getBuffShapeChange(player);
		//如果没有变身
		if(shapeChangebuffs.size() == 0){
			if(model.getQ_shapechange_buffid() !=0){
				log.debug("攻击者（玩家）变身技能不对");
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultshapechangeno, player, 0, skillId, direction));
				return;			
			 }
		}else{
			//有变身
			boolean defaultShapeChange = false;
			for(Buff per:shapeChangebuffs){
				if(per.getModelId()== model.getQ_shapechange_buffid()){
					defaultShapeChange = true;
					break;
				}
			}
			if(!defaultShapeChange){
				log.debug("攻击者（玩家）变身技能不对");
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultshapechangeno, player, 0, skillId, direction));
				return;		
			}
		}
		
		//是否主动技能 
		if (model.getQ_trigger_type() != 1) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//是否人物技能
		if (model.getQ_skill_user() != 1) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		if (PlayerState.CHANGEMAP.compare(player.getState())) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		Map map = ManagerPool.mapManager.getMap(player);
		if (map == null) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}
		
		//梅花副本不能使用群攻
		if(map.getMapModelid()==42121 && model.getQ_area_shape()!=1){
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("本地图不能使用此技能"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//获得怪物
		Player owner = null;
		SummonPet target = null;
		if (ownerId == player.getId()) {
			//召唤怪主人是自己！
			return;
		}

		if (ownerId > 0) {
			owner = ManagerPool.playerManager.getPlayer(ownerId);
			if (owner != null && targetId > 0) {
				target = ManagerPool.summonpetInfoManager.getSummonPet(owner, targetId);
			}
		}
		
		//技能目标选择
		ISkillScript script = null;
		if(model.getQ_skill_script() > 0){
			script = (ISkillScript) ManagerPool.scriptManager.getScript(model.getQ_skill_script());
		}
		if(script!=null){
			try{
				if(!script.canUse(player, target, direction)){
					return;
				}
			}catch(Exception e){
				log.error(e, e);
			}
		}
		
		if(target==null){
			log.error("玩家(" + player.getId() + ")对宠物(" + targetId + ")主人为(" + ownerId + ")攻击没有目标");
			return;
		}

		//目标检查 （单体目标且对象不为自己时）
		if (model.getQ_area_shape() == 1 && model.getQ_target() != 1 && (target == null || target.isDie())) {
			//无目标错误
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("请先选定一个释放目标"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//消耗检查
		if (player.getMp() < model.getQ_need_mp()) {
			//魔法值不足
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("魔法值不足，无法释放"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()))) {
			int remain = (int) ManagerPool.cooldownManager.getCooldownTime(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID())) / 1000;
			log.debug("技能冷却中");
			//技能冷却中
			if (remain > 10) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("技能冷却中，请稍后再试，剩余{1}秒"), String.valueOf(remain));
			}
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//公共冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()))) {
			int remain = (int) ManagerPool.cooldownManager.getCooldownTime(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level())) / 1000;
			log.debug("公共冷却中");
			//技能公共冷却中
			if (remain > 10) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("技能冷却中，请稍后再试，剩余{1}秒"), String.valueOf(remain));
			}
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		//跳跃状态不能攻击
		if (model.getQ_is_Jump_skill() == 0 && (PlayerState.JUMP.compare(player.getState()) || PlayerState.DOUBLEJUMP.compare(player.getState()))) {
			log.debug("跳跃状态不能攻击");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
			return;
		}

		Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer.getMap().get(map.getMapModelid());
		
		//! PK值大于10无法攻击
		if (player.getPkValue() >= 10 && mapBean.getQ_map_pkpoint() == 1){
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的杀孽过重，系统将限制您恶意攻击其他玩家的行为"));
			return;
		}
		
		Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());
		if (target != null && owner != null) {
			if (SummonPetRunState.SWIM == target.getRunState()) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("游泳区内无法被攻击"));
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
				return;
			}

			//不在pk列表中
			if (!player.getEnemys().containsKey(owner.getId()) && owner.getPkValue() == 0) {
				//PK状态检查
				if (player.getPkState() == 0) {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("当前PK模式，无法对其造成伤害"));
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
					return;
				} else if (player.getPkState() == 1) {
					//同队伍检查
					if (player.getTeamid() == owner.getTeamid() && player.getTeamid() != 0) {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("当前PK模式，无法对其造成伤害"));
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						return;
					}
					
					//同战盟检查
					if ((player.getGuildId() == owner.getGuildId()  || GuildServerManager.getInstance().isFriendGuild(owner.getGuildId(), player.getGuildId())) && player.getGuildId()!=0) {
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("当前PK模式，无法对其造成伤害"));
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						return;
					}
				}
			}

			int pkMinLevel = DEFAULT_PK_MIN_LEVEL, pkDiffLevel = DEFAULT_PK_DIFF_LEVEL;
			Q_globalBean pkMinLevelGolbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PK_MIN_LEVEL.getValue());
			if(pkMinLevelGolbalBean != null) pkMinLevel = pkMinLevelGolbalBean.getQ_int_value();
			Q_globalBean pkDiffLevelGolbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PK_DIFF_LEVEL.getValue());
			if(pkDiffLevelGolbalBean != null) pkDiffLevel = pkDiffLevelGolbalBean.getQ_int_value();
			
			//自己等级检查
			if (player.getLevel() < pkMinLevel && mapBean.getQ_map_pkprotection() == 1) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您处于"+pkMinLevel+"级以下新手保护期内，无法对其他玩家造成伤害"));
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
				return;
			}

			//敌人等级检查
			if (owner.getLevel() < pkMinLevel && mapBean.getQ_map_pkprotection() == 1) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您无法对低于"+pkMinLevel+"级以下处于新手保护期内的玩家造成伤害"));
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
				return;
			}

			if(!FighterState.FORCEPK.compare(owner.getFightState())){
				//Pk保护Buff
				if (FighterState.PKBAOHU.compare(owner.getFightState())) {
					Buff buff = ManagerPool.buffManager.getBuffByType(owner, BuffType.PKPROTECT).get(0);
					long remain = buff.getStart() + buff.getTotalTime() - System.currentTimeMillis();
					if (remain > 0) {
						int sec = (int) (remain / 1000);
						if (remain > 5000) {
							if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.NOTIFY_KILL, null)) {
								MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
								return;
							} else {
								ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.NOTIFY_KILL, null, 10 * 1000);
							}
						}
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("该玩家处于复活后的PK保护时间内，您目前无法对其造成伤害，剩余时间：{1}分{2}秒"), String.valueOf(sec / 60), String.valueOf(sec % 60));
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						
						//玩家进入攻击状态
						player.setState(PlayerState.FIGHT);
						//设置玩家恶意攻击
						player.setMalicious(1);

						//主动Pk移除被杀保护Buff
						if (FighterState.PKBAOHU.compare(player.getFightState())) {
							ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_FOR_KILLED);
							log.error("玩家(" + player.getId() + ")PK状态为(" + player.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(player.getState()) + ")敌人列表为(" + MessageUtil.castListToString(player.getEnemys().values()) + ")对宠物(" + target.getId() + ")攻击导致和平保护buff消失");
							MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("您主动发起了对其他玩家的PK，和平保护BUFF消失了"));
						}
						return;
					}
				}
	
				//夜晚挂机和平buff
				if (FighterState.PKBAOHUFORNIGHT.compare(owner.getFightState())) {
					Buff buff = ManagerPool.buffManager.getBuffByType(owner, BuffType.PROTECTFORNIGHT).get(0);
					long remain = buff.getStart() + buff.getTotalTime() - System.currentTimeMillis();
					if (remain > 0) {
						int sec = (int) (remain / 1000);
						if (remain > 5000) {
							if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.NOTIFY_KILL, null)) {
								MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
								return;
							} else {
								ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.NOTIFY_KILL, null, 10 * 1000);
							}
						}
						MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("玩家处于夜晚挂机PK保护时间内，您目前无法对其造成伤害，剩余时间：{1}分{2}秒"), String.valueOf(sec / 60), String.valueOf(sec % 60));
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						
						//玩家进入攻击状态
						player.setState(PlayerState.FIGHT);
						//设置玩家夜间恶意攻击
						player.setMalicious(2);
						
						//主动Pk移除夜晚保护Buff
						if (FighterState.PKBAOHUFORNIGHT.compare(player.getFightState())) {
							ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_IN_NIGHT);
							log.error("玩家(" + player.getId() + ")PK状态为(" + player.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(player.getState()) + ")敌人列表为(" + MessageUtil.castListToString(player.getEnemys().values()) + ")对宠物(" + target.getId() + ")攻击导致夜晚和平保护buff消失");
						}
						return;
					}
				}
	
				//等级差检查(地图开启等级差保护)
				if (Math.abs(player.getLevel() - owner.getLevel()) > pkDiffLevel && mapBean.getQ_map_pk() == 1) {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("等级差>"+pkDiffLevel+"级，互相之间无法造成伤害"));
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
					return;
				}
	
				//自己安全区检查
				if (ManagerPool.mapManager.isSafe(player.getPosition(), player.getMapModelId())) {
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，安全区内禁止PK"));
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
					return;
				}
	
				//目标安全区检查
				if (ManagerPool.mapManager.isSafe(target.getPosition(), player.getMapModelId())) {
					/* xuliang
					MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉，无法PK处于安全区内的美人"));
					*/
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
					return;
				}
			}
			
			//距离检查
			//计算格子之间距离，放宽一格
			Grid playerGrid = MapUtils.getGrid(player.getPosition(), blocks);
			Grid monsterGrid = MapUtils.getGrid(target.getPosition(), blocks);
			if (model.getQ_range_limit() >= 0 && MapUtils.countDistance(playerGrid, monsterGrid) > model.getQ_range_limit() + 1) {
				//超出攻击距离
				log.debug("超出攻击距离");
				ManagerPool.mapManager.broadcastPlayerForceStop(player);
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
				return;
			}
			
			//如果是冲锋，地裂斩&旋风斩，检查人是否是可行走路径
			if (model.getQ_skill_type() == 2 || model.getQ_skill_type() == 3) {
				List<Position> roads = new ArrayList<Position>();
				roads = MapUtils.findRoads(
						ManagerPool.mapManager.getMapBlocks(map.getMapModelid()),
						player.getPosition(), target.getPosition(), -1, false);
				// 开始走动
			if (roads.size() > 1) {
				Position pos = roads.get(roads.size()-2);
				MapManager.getInstance().changeSpecialPosition(player, pos ,model.getQ_skillID());
				}/*else{ panic god暂时屏蔽，和怪物同一个点也释放
					log.debug("无行走路径");
					ManagerPool.mapManager.broadcastPlayerForceStop(player);
					MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.noroads, player, targetId, skillId, direction));
					return;
				}*/
			}
			IAttackCheckScript checkscript = (IAttackCheckScript) ManagerPool.scriptManager.getScript(ScriptEnum.CHECKATTACK);
			if (checkscript != null) {
				try {
					if(!checkscript.check(player, target)){
						MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, targetId, skillId, direction));
						return;
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			} else {
				log.error("攻击检查脚本不存在！");
			}
		}
		PlayerDaZuoManager.getInstacne().breakDaZuo(player);
		//玩家进入攻击状态
		player.setState(PlayerState.FIGHT);

		//主动Pk移除被杀保护Buff
		if (FighterState.PKBAOHU.compare(player.getFightState())) {
			player.setMalicious(1);//设置玩家恶意攻击
			ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_FOR_KILLED);
			log.error("玩家(" + player.getId() + ")PK状态为(" + player.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(player.getState()) + ")敌人列表为(" + MessageUtil.castListToString(player.getEnemys().values()) + ")对宠物(" + target.getId() + ")攻击导致和平保护buff消失");
			MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("您主动发起了对其他玩家的PK，和平保护BUFF消失了"));
		}

		//主动Pk移除夜晚保护Buff
		if (FighterState.PKBAOHUFORNIGHT.compare(player.getFightState())) {
			player.setMalicious(2);//设置玩家夜间恶意攻击
			ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_IN_NIGHT);
			log.error("玩家(" + player.getId() + ")PK状态为(" + player.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(player.getState()) + ")敌人列表为(" + MessageUtil.castListToString(player.getEnemys().values()) + ")对宠物(" + target.getId() + ")攻击导致夜晚和平保护buff消失");
		}
		//攻击时要下马
		if (ManagerPool.horseManager.isRidding(player)){
			ManagerPool.horseManager.unride(player);
		}
		
		//玩家转向
		player.setDirection((byte) direction);

		//魔法值消耗
		player.setMp(player.getMp() - model.getQ_need_mp());
		ManagerPool.playerManager.onMpChange(player);

		//开始冷却
		double speed = ((double) player.getAttackSpeed()-100)*10;
		//添加技能冷却
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long) (model.getQ_cd()));
		//添加技能公共冷却
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()), (long) ((model.getQ_public_cd() - speed)>300?(model.getQ_public_cd() - speed):300));

		long fightId = Config.getId();

		MessageUtil.tell_round_message(player, getFightBroadcastMessage(fightId, player, target, skillId, direction));
		
		boolean action = false;
		if(script!=null){
			try{
				//技能脚本行为
				action = script.defaultAction(player, target);
			}catch(Exception e){
				log.error(e, e);
			}
		}
		if(!action){
			//技能飞行时间计算
			//long fly = model.getQ_delay_time();
			long fly = (long) (model.getQ_delay_time() *(model.getQ_public_cd() - speed)/model.getQ_public_cd());
			//单体攻击
			if (model.getQ_area_shape() == 1 && target != null && model.getQ_trajectory_speed()>0) {
				fly += MapUtils.countDistance(player.getPosition(), target.getPosition()) * 1000 / model.getQ_trajectory_speed();
			}
	
			if(fly <= 50){
				fly = 50;
			}
			if (fly > 0) {
				//延时伤害
				HitTimer timer = new HitTimer(fightId, player, target, skill, direction, fly, true);
				TimerUtil.addTimerEvent(timer);
			} else {
				//即时伤害
				attack(fightId, player, target, skill, direction, true);
			}
		}
	}

	/**
	 * 怪物攻击玩家
	 *
	 * @param monster 怪物
	 * @param roleId 玩家Id
	 * @param attackType 攻击类型
	 */
	public void monsterAttackPlayer(Monster monster, Player player, Skill skill) {

		/* xuliang hide
		//怪物正在攻击动画播放
		if(ManagerPool.cooldownManager.isCooldowning(monster, CooldownTypes.MONSTER_ACTION, null)){
			return;
		}
		//怪物正在攻击CD
		if(ManagerPool.cooldownManager.isCooldowning(monster, CooldownTypes.MONSTER_ATTACK, null)){
			return;
		}	
		*/

		//panic god 屏蔽掉攻击间隔按默认技能冷却算
		//Q_monsterBean monsterModel = ManagerPool.dataManager.q_monsterContainer.getMap().get(monster.getModelId());
		//Skill defaultSkill = monster.getDefaultSkill(monsterModel);
		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
		if (model == null) {
			return;
		}
		
		// 怪物攻击技能冷却
		if (ManagerPool.cooldownManager.isCooldowning(monster,
				CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()))) {
			return;
		}
		
		if(player!=null){
			if(!monster.canAttack(player)){
				return;
			}
			
			IAttackCheckScript script = (IAttackCheckScript) ManagerPool.scriptManager.getScript(ScriptEnum.CHECKATTACK);
			if (script != null) {
				try {
					if(!script.check(monster, player)){
						return;
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			} else {
				log.error("攻击检查脚本不存在！");
			}
		}

		//开始冷却(speed百分比)
		double speed = ((double) monster.getAttackSpeed()) * 10;
		//添加怪物攻击冷却
		ManagerPool.cooldownManager.addCooldown(monster, CooldownTypes.MONSTER_ATTACK, null, (long) ((model.getQ_public_cd() - speed)>300?(model.getQ_public_cd() - speed):300));

		//添加怪物攻击动画冷却
		ManagerPool.cooldownManager.addCooldown(monster, CooldownTypes.MONSTER_ACTION, null, (long) (model.getQ_delay_time()));
		
		//添加技能冷却
		ManagerPool.cooldownManager.addCooldown(monster, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long) (model.getQ_cd()));

		long fightId = Config.getId();

		log.debug("monster " + (monster == null ? "null" : monster.getId()) + " use skill " + (skill == null ? "null" : skill.getSkillModelId()) + " attack player " + (player == null ? "null" : player.getId()));

		int dir = MapUtils.countDirection(monster.getPosition(), player.getPosition());
		monster.setDirection((byte) dir);

		MessageUtil.tell_round_message(player, getFightBroadcastMessage(fightId, monster, player, skill.getSkillModelId(), dir));

		// 如果是召唤怪技能特殊处理
		// 召唤兽(格式：召唤兽ID_数量_时间（秒）_属性百分比；召唤兽ID_数量_属性百分比_时间（秒）_属性百分比) 
		String q_animals = model.getQ_animal_id();
		if (q_animals != null && q_animals.length() >= 3) {
			ManagerPool.monsterManager.createrMonstersBySkill(q_animals, monster);
			return;
		}
		//技能飞行时间计算
		//long fly = model.getQ_delay_time();
		long fly = (long) (model.getQ_delay_time() *(model.getQ_public_cd() - speed)/model.getQ_public_cd());
		
		if (model.getQ_area_shape() == 1 && player != null && model.getQ_trajectory_speed()>0) {
			fly += MapUtils.countDistance(monster.getPosition(), player.getPosition()) * 1000 / model.getQ_trajectory_speed();
		}

		if(fly <=50){
			fly = 50;
		}
		if (fly > 0) {
			//延时伤害
			HitTimer timer = new HitTimer(fightId, monster, player, skill, dir, fly, true);
			TimerUtil.addTimerEvent(timer);
		} else {
			//即时伤害
			attack(fightId, monster, player, skill, dir, true);
		}
	}
	
	/**
	 * 怪物攻击召唤怪
	 *
	 * @param monster 怪物
	 * @param target 怪物
	 * @param attackType 攻击类型
	 */
	public void monsterAttackPlayer(Monster monster, SummonPet target, Skill skill) {
		/* xuliang hide
		// 怪物正在攻击动画播放
		if (ManagerPool.cooldownManager.isCooldowning(monster,CooldownTypes.MONSTER_ACTION, null)) {
			return;
		}
		// 怪物正在攻击CD
		if (ManagerPool.cooldownManager.isCooldowning(monster,CooldownTypes.MONSTER_ATTACK, null)) {
			return;
		}
		*/
		
		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
		if (model == null) {
			return;
		}
		
		//怪物攻击技能冷却
		if (ManagerPool.cooldownManager.isCooldowning(monster, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()))) {
			return;
		}
		
		if(target!=null){
			if(!monster.canAttack(target)){
				return;
			}
			
			IAttackCheckScript script = (IAttackCheckScript) ManagerPool.scriptManager.getScript(ScriptEnum.CHECKATTACK);
			if (script != null) {
				try {
					if(!script.check(monster, target)){
						return;
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			} else {
				log.error("攻击检查脚本不存在！");
			}
		}

		//开始冷却
		double speed = ((double) monster.getAttackSpeed()) / 1000;
		//添加怪物攻击冷却
		ManagerPool.cooldownManager.addCooldown(monster, CooldownTypes.MONSTER_ATTACK, null, (long) ((model.getQ_public_cd() - speed)>300?(model.getQ_public_cd() - speed):300));

		//添加怪物攻击动画冷却
		ManagerPool.cooldownManager.addCooldown(monster, CooldownTypes.MONSTER_ACTION, null, (long) (model.getQ_delay_time()));
		
		//添加技能冷却
		ManagerPool.cooldownManager.addCooldown(monster, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long) (model.getQ_cd()));

		long fightId = Config.getId();

		log.debug("monster " + (monster == null ? "null" : monster.getId()) + " use skill " + (skill == null ? "null" : skill.getSkillModelId()) + " attack monster " + (target == null ? "null" : target.getId()));

		int dir = MapUtils.countDirection(monster.getPosition(), target.getPosition());
		monster.setDirection((byte) dir);

		MessageUtil.tell_round_message(monster, getFightBroadcastMessage(fightId, monster, target, skill.getSkillModelId(), dir));

		// 如果是召唤怪技能特殊处理
		// 召唤兽(格式：召唤兽ID_数量_时间（秒）_属性百分比；召唤兽ID_数量_属性百分比_时间（秒）_属性百分比) 
		String q_animals = model.getQ_animal_id();
		if (q_animals != null && q_animals.length() >= 3) {
			ManagerPool.monsterManager.createrMonstersBySkill(q_animals, monster);
			return;
		}
		//技能飞行时间计算
		//long fly = model.getQ_delay_time();
		long fly = (long) (model.getQ_delay_time() *(model.getQ_public_cd() - speed)/model.getQ_public_cd());


		if (model.getQ_area_shape() == 1 && target != null && model.getQ_trajectory_speed() >0) {
			fly += MapUtils.countDistance(monster.getPosition(), target.getPosition()) * 1000 / model.getQ_trajectory_speed();
		}


		if(fly <=50){
			fly = 50;
		}
		
		if (fly > 0) {
			//延时伤害
			HitTimer timer = new HitTimer(fightId, monster, target, skill, dir, fly, true);
			TimerUtil.addTimerEvent(timer);
		} else {
			//即时伤害
			attack(fightId, monster, target, skill, dir, true);
		}
	}
	/**
	 * 怪物攻击怪物
	 *
	 * @param monster 怪物
	 * @param target 怪物
	 * @param attackType 攻击类型
	 */
	public void monsterAttackPlayer(Monster monster, Monster target, Skill skill) {
		//攻击间隔按默认技能冷却算
		Q_monsterBean monsterModel = ManagerPool.dataManager.q_monsterContainer.getMap().get(monster.getModelId());
		Skill defaultSkill = monster.getDefaultSkill(monsterModel);
		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(defaultSkill.getSkillModelId() + "_" + defaultSkill.getSkillLevel());
		if (model == null) {
			return;
		}
		
		if(target!=null){
			if(!monster.canAttack(target)){
				return;
			}
			
			IAttackCheckScript script = (IAttackCheckScript) ManagerPool.scriptManager.getScript(ScriptEnum.CHECKATTACK);
			if (script != null) {
				try {
					if(!script.check(monster, target)){
						return;
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			} else {
				log.error("攻击检查脚本不存在！");
			}
		}

		//开始冷却
		double speed = ((double) monster.getAttackSpeed()) / 1000;
		//添加怪物攻击冷却
		ManagerPool.cooldownManager.addCooldown(monster, CooldownTypes.MONSTER_ATTACK, null, (long) ((model.getQ_public_cd() - speed)>300?(model.getQ_public_cd() - speed):300));

		//添加怪物攻击动画冷却
		ManagerPool.cooldownManager.addCooldown(monster, CooldownTypes.MONSTER_ACTION, null, (long) (model.getQ_delay_time()));

		long fightId = Config.getId();

		log.debug("monster " + (monster == null ? "null" : monster.getId()) + " use skill " + (skill == null ? "null" : skill.getSkillModelId()) + " attack monster " + (target == null ? "null" : target.getId()));

		int dir = MapUtils.countDirection(monster.getPosition(), target.getPosition());
		monster.setDirection((byte) dir);

		MessageUtil.tell_round_message(monster, getFightBroadcastMessage(fightId, monster, target, skill.getSkillModelId(), dir));
		// 如果是召唤怪技能特殊处理
		// 召唤兽(格式：召唤兽ID_数量_时间（秒）_属性百分比；召唤兽ID_数量_属性百分比_时间（秒）_属性百分比) 
		String q_animals = model.getQ_animal_id();
		if (q_animals != null && q_animals.length() >= 3) {
			ManagerPool.monsterManager.createrMonstersBySkill(q_animals, monster);
			return;
		}
		//技能飞行时间计算
		//long fly = model.getQ_delay_time();
		long fly = (long) (model.getQ_delay_time() *(model.getQ_public_cd() - speed)/model.getQ_public_cd());


		if (model.getQ_area_shape() == 1 && target != null && model.getQ_trajectory_speed() >0) {
			fly += MapUtils.countDistance(monster.getPosition(), target.getPosition()) * 1000 / model.getQ_trajectory_speed();
		}


		if(fly <=50){
			fly = 50;
		}
		
		if (fly > 0) {
			//延时伤害
			HitTimer timer = new HitTimer(fightId, monster, target, skill, dir, fly, true);
			TimerUtil.addTimerEvent(timer);
		} else {
			//即时伤害
			attack(fightId, monster, target, skill, dir, true);
		}
	}

	/**
	 * 怪物不可以攻击宠物 怪物攻击宠物
	 * @param monster 怪物
	 * @param pet 宠物
	 * @param attackType 攻击类型
	 */
//	public void monsterAttackPet(Monster monster, Pet pet, Skill skill){
//		//攻击间隔按默认技能冷却算
//		Q_monsterBean monsterModel = ManagerPool.dataManager.q_monsterContainer.getMap().get(monster.getModelId());
//		Skill defaultSkill = monster.getDefaultSkill(monsterModel);
//		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(defaultSkill.getSkillModelId() + "_" + defaultSkill.getSkillLevel());
//		if(model==null) return;
//		
//		//开始冷却
//		double speed = ((double)monster.getAttackSpeed()) / 1000;
//		//添加怪物攻击冷却
//		ManagerPool.cooldownManager.addCooldown(monster, CooldownTypes.MONSTER_ATTACK, null, (long)(model.getQ_public_cd() / speed));
//		
//		//添加怪物攻击动画冷却
//		ManagerPool.cooldownManager.addCooldown(monster, CooldownTypes.MONSTER_ACTION, null, (long)(model.getQ_delay_time()));
//		
//		long fightId = Config.getId();
//		
////		log.debug("monster " + (monster==null?"null":monster.getId()) + " use skill " + (skill==null?"null":skill.getSkillModelId()) + " attack player " + (pet==null?"null":pet.getId()));
//		
//		int dir = MapUtils.countDirection(monster.getPosition(), pet.getPosition());
//		monster.setDirection((byte)dir);
//		
//		MessageUtil.tell_round_message(pet, getFightBroadcastMessage(fightId, monster, pet, skill.getSkillModelId(), dir));
//		
//		//技能飞行时间计算
//		long fly = model.getQ_delay_time();
//
//		if(model.getQ_area_shape()==1 && pet!=null){
//			fly += MapUtils.countDistance(monster.getPosition(), pet.getPosition()) * 1000 / model.getQ_trajectory_speed();
//		}
//		
//		if(fly > 0){
//			//延时伤害
//			HitTimer timer = new HitTimer(fightId, monster, pet, skill, dir, fly);
//			TimerUtil.addTimerEvent(timer);
//		}else{
//			//即时伤害
//			attack(fightId, monster, pet, skill, dir);
//		}
//	}
	/**
	 * 宠物攻击怪物
	 *
	 * @param roleId 玩家Id
	 * @param monsterId 怪物Id
	 * @param skillId 攻击技能
	 * @param direction 攻击方向
	 */
	public void petAttackMonster(Pet pet, Monster monster, Skill skill) {
		//宠物主人死亡
		Player owner = ManagerPool.playerManager.getPlayer(pet.getOwnerId());
		if (owner == null || owner.isDie()) {
			return;
		}
		//宠物死亡
		if (pet.isDie()) {
			return;
		}

		//攻击间隔按默认技能冷却算
		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
		if (model == null) {
			return;
		}

		//定身或睡眠中
		if (FighterState.DINGSHEN.compare(pet.getFightState()) || FighterState.SHUIMIAN.compare(pet.getFightState())) {
			return;
		}

		//目标检查 （单体目标且对象不为自己时）
		if (model.getQ_area_shape() == 1 && model.getQ_target() != 1 && (monster == null || monster.isDie())) {
			//无目标错误
			return;
		}

//		//消耗检查
//		if (pet.getMp() < model.getQ_need_mp()) {
//			//魔法值不足
//			return;
//		}

		//攻击冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(pet, CooldownTypes.PET_ATTACK, null)) {
			return;
		}

//		//冷却检查
//		if(ManagerPool.cooldownManager.isCooldowning(pet, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()))){
//			return;
//		}
//		
//		//公共冷却检查
//		if(ManagerPool.cooldownManager.isCooldowning(pet, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()))){
//			return;
//		}

//		//魔法值消耗
//		pet.setMp(pet.getMp() - model.getQ_need_mp());
//		ManagerPool.petInfoManager.onMpChange(pet);

		IAttackCheckScript script = (IAttackCheckScript) ManagerPool.scriptManager.getScript(ScriptEnum.CHECKATTACK);
		if (script != null) {
			try {
				if(!script.check(pet, monster)){
					return;
				}
			} catch (Exception e) {
				log.error(e, e);
			}
		} else {
			log.error("攻击检查脚本不存在！");
		}
		//开始冷却
		double speed = ((double) pet.getAttackSpeed()) / 100;

		//System.out.println("宠物攻击速度：" + speed);
		//添加怪物攻击冷却
		ManagerPool.cooldownManager.addCooldown(pet, CooldownTypes.PET_ATTACK, null, (long) ((model.getQ_public_cd() - speed)>300?(model.getQ_public_cd() - speed):300));

		//添加怪物攻击动画冷却
		ManagerPool.cooldownManager.addCooldown(pet, CooldownTypes.PET_ACTION, null, (long) (model.getQ_delay_time()));

		long fightId = Config.getId();

//		log.debug("pet " + (pet==null?"null":pet.getId()) + " use skill " + (skill==null?"null":skill.getSkillModelId()) + " attack player " + (player==null?"null":player.getId()));

		int dir = MapUtils.countDirection(pet.getPosition(), monster.getPosition());
		pet.setDirection((byte) dir);

		MessageUtil.tell_round_message(pet, getFightBroadcastMessage(fightId, pet, monster, skill.getSkillModelId(), dir));

		//技能飞行时间计算
		//long fly = model.getQ_delay_time();
		long fly = (long) (model.getQ_delay_time() *(model.getQ_public_cd() - speed)/model.getQ_public_cd());


		if (model.getQ_area_shape() == 1 && monster != null && model.getQ_trajectory_speed() >0) {
			fly += MapUtils.countDistance(pet.getPosition(), monster.getPosition()) * 1000 / model.getQ_trajectory_speed();
		}


		if(fly <=50){
			fly = 50;
		}
		
		if (fly > 0) {
			//延时伤害
			HitTimer timer = new HitTimer(fightId, pet, monster, skill, dir, fly, true);
			TimerUtil.addTimerEvent(timer);
		} else {
			//即时伤害
			attack(fightId, pet, monster, skill, dir, true);
		}
		PetScriptManager.getInstance().useSkill(pet, skill);
	}

	/**
	 * 召唤怪攻击怪物
	 *
	 * @param roleId 玩家Id
	 * @param monsterId 怪物Id
	 * @param skillId 攻击技能
	 * @param direction 攻击方向
	 */
	public void summonpetAttackMonster(SummonPet summonpet, Monster monster, Skill skill) {
		//召唤怪主人死亡
		Player owner = ManagerPool.playerManager.getPlayer(summonpet.getOwnerId());
		if (owner == null || owner.isDie()) {
			return;
		}
		//召唤怪死亡
		if (summonpet.isDie()) {
			return;
		}

		//攻击间隔按默认技能冷却算
		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
		if (model == null) {
			return;
		}

		//定身或睡眠中
		if (FighterState.DINGSHEN.compare(summonpet.getFightState()) || FighterState.SHUIMIAN.compare(summonpet.getFightState())) {
			return;
		}

		//目标检查 （单体目标且对象不为自己时）
		if (model.getQ_area_shape() == 1 && model.getQ_target() != 1 && (monster == null || monster.isDie())) {
			//无目标错误
			return;
		}

		//攻击冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(summonpet, CooldownTypes.SUMMONPET_ATTACK, null)) {
			return;
		}
		
		//正在攻击动画播放
		if(ManagerPool.cooldownManager.isCooldowning(summonpet, CooldownTypes.SUMMONPET_ACTION, null)){
			return;
		}

		//冷却检查
		if(ManagerPool.cooldownManager.isCooldowning(summonpet, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()))){
			return;
		}
//		
//		//公共冷却检查
//		if(ManagerPool.cooldownManager.isCooldowning(summonpet, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()))){
//			return;
//		}


		IAttackCheckScript script = (IAttackCheckScript) ManagerPool.scriptManager.getScript(ScriptEnum.CHECKATTACK);
		if (script != null) {
			try {
				if(!script.check(summonpet, monster)){
					return;
				}
			} catch (Exception e) {
				log.error(e, e);
			}
		} else {
			log.error("攻击检查脚本不存在！");
		}
		//开始冷却
		double speed = ((double) summonpet.getAttackSpeed()) / 1000;

		//System.out.println("宠物攻击速度：" + speed);
		//添加怪物攻击冷却
		ManagerPool.cooldownManager.addCooldown(summonpet, CooldownTypes.SUMMONPET_ATTACK, null, (long) ((model.getQ_public_cd() - speed)>300?(model.getQ_public_cd() - speed):300));

		//添加怪物攻击动画冷却
		ManagerPool.cooldownManager.addCooldown(summonpet, CooldownTypes.SUMMONPET_ACTION, null, (long) (model.getQ_delay_time()));
		
		//添加技能冷却
		ManagerPool.cooldownManager.addCooldown(summonpet, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long) (model.getQ_cd()));

		long fightId = Config.getId();

//		log.debug("pet " + (pet==null?"null":pet.getId()) + " use skill " + (skill==null?"null":skill.getSkillModelId()) + " attack player " + (player==null?"null":player.getId()));

		int dir = MapUtils.countDirection(summonpet.getPosition(), monster.getPosition());
		summonpet.setDirection((byte) dir);

		MessageUtil.tell_round_message(summonpet, getFightBroadcastMessage(fightId, summonpet, monster, skill.getSkillModelId(), dir));

		//技能飞行时间计算
		//long fly = model.getQ_delay_time();
		long fly = (long) (model.getQ_delay_time() *(model.getQ_public_cd() - speed)/model.getQ_public_cd());


		if (model.getQ_area_shape() == 1 && monster != null && model.getQ_trajectory_speed() >0) {
			fly += MapUtils.countDistance(summonpet.getPosition(), monster.getPosition()) * 1000 / model.getQ_trajectory_speed();
		}

		if(fly <=50){
			fly = 50;
		}
		if (fly > 0) {
			//延时伤害
			HitTimer timer = new HitTimer(fightId, summonpet, monster, skill, dir, fly, true);
			TimerUtil.addTimerEvent(timer);
		} else {
			//即时伤害
			attack(fightId, summonpet, monster, skill, dir, true);
		}
		/*PANIC 暂时屏蔽
		PetScriptManager.getInstance().useSkill(summonpet, skill);
		*/
	}
	
	/**
	 * 宠物攻击玩家
	 *
	 * @param roleId 玩家Id
	 * @param targetId 怪物Id
	 * @param skillId 攻击技能
	 * @param direction 攻击方向
	 */
	public void petAttackPlayer(Pet pet, Player target, Skill skill) {
		//宠物主人死亡
		Player owner = ManagerPool.playerManager.getPlayer(pet.getOwnerId());
		if (owner == null || owner.isDie()) {
			return;
		}
		//宠物死亡
		if (pet.isDie()) {
			return;
		}

		//玩家已经死亡
		if (target.isDie()) {
			return;
		}

		//定身或睡眠中
		if (FighterState.DINGSHEN.compare(pet.getFightState()) || FighterState.SHUIMIAN.compare(pet.getFightState())) {
			return;
		}


		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
		if (model == null) {
			return;
		}

		//目标检查 （单体目标且对象不为自己时）
		if (model.getQ_area_shape() == 1 && model.getQ_target() != 1 && (target == null || target.isDie())) {
			//无目标错误
			return;
		}

//		//消耗检查
//		if (pet.getMp() < model.getQ_need_mp()) {
//			//魔法值不足
//			return;
//		}

		//攻击冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(pet, CooldownTypes.PET_ATTACK, null)) {
			return;
		}

//		//冷却检查
//		if(ManagerPool.cooldownManager.isCooldowning(pet, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()))){
//			return;
//		}
//		
//		//公共冷却检查
//		if(ManagerPool.cooldownManager.isCooldowning(pet, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()))){
//			return;
//		}

		Map map = ManagerPool.mapManager.getMap(owner.getServerId(), owner.getLine(), owner.getMap());
		Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer.getMap().get(map.getMapModelid());
		Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());
		if (target != null) {
			//不在pk列表中
			if (!owner.getEnemys().containsKey(target.getId())) {
				//PK状态检查
				if (owner.getPkState() == 0) {
					return;
				} else if (owner.getPkState() == 1) {
					//同队伍检查
					if (owner.getTeamid() == target.getTeamid() && owner.getTeamid() != 0) {
						return;
					}

					//同战盟检查
					if ((owner.getGuildId() == target.getGuildId() || GuildServerManager.getInstance().isFriendGuild(owner.getGuildId(), target.getGuildId())) && owner.getGuildId() != 0) {
						return;
					}
				}
			}
			
			int pkMinLevel = DEFAULT_PK_MIN_LEVEL, pkDiffLevel = DEFAULT_PK_DIFF_LEVEL;
			Q_globalBean pkMinLevelGolbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PK_MIN_LEVEL.getValue());
			if(pkMinLevelGolbalBean != null) pkMinLevel = pkMinLevelGolbalBean.getQ_int_value();
			Q_globalBean pkDiffLevelGolbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PK_DIFF_LEVEL.getValue());
			if(pkDiffLevelGolbalBean != null) pkDiffLevel = pkDiffLevelGolbalBean.getQ_int_value();

			//自己等级检查
			if (owner.getLevel() < pkMinLevel && mapBean.getQ_map_pkprotection() == 1) {
				return;
			}

			//敌人等级检查
			if (target.getLevel() < pkMinLevel && mapBean.getQ_map_pkprotection() == 1) {
				return;
			}

			if(!FighterState.FORCEPK.compare(target.getFightState())){
				//Pk保护Buff
				if (FighterState.PKBAOHU.compare(target.getFightState())) {
					Buff buff = ManagerPool.buffManager.getBuffByType(target, BuffType.PKPROTECT).get(0);
					long remain = buff.getStart() + buff.getTotalTime() - System.currentTimeMillis();
					if (remain > 0) {
						return;
					}
				}
	
				//夜晚挂机和平buff
				if (FighterState.PKBAOHUFORNIGHT.compare(target.getFightState())) {
					Buff buff = ManagerPool.buffManager.getBuffByType(target, BuffType.PROTECTFORNIGHT).get(0);
					long remain = buff.getStart() + buff.getTotalTime() - System.currentTimeMillis();
					if (remain > 0) {
						return;
					}
				}
	
				//等级差检查(地图开启等级差保护)
				if (Math.abs(owner.getLevel() - target.getLevel()) > pkDiffLevel && mapBean.getQ_map_pk() == 1) {
					return;
				}
	
				//自己安全区检查	
				if (ManagerPool.mapManager.isSafe(pet.getPosition(), owner.getMapModelId())) {
					return;
				}
	
				//目标安全区检查
	
				if (ManagerPool.mapManager.isSafe(target.getPosition(), owner.getMapModelId())) {
					return;
				}
			}
			//距离检查
			//计算格子之间距离，放宽一格
			Grid playerGrid = MapUtils.getGrid(pet.getPosition(), blocks);
			Grid monsterGrid = MapUtils.getGrid(target.getPosition(), blocks);
			if (model.getQ_range_limit() >= 0 && MapUtils.countDistance(playerGrid, monsterGrid) > model.getQ_range_limit() + 1) {
				//超出攻击距离
				log.debug("超出攻击距离");
				return;
			}
			
			IAttackCheckScript script = (IAttackCheckScript) ManagerPool.scriptManager.getScript(ScriptEnum.CHECKATTACK);
			if (script != null) {
				try {
					if(!script.check(pet, target)){
						return;
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			} else {
				log.error("攻击检查脚本不存在！");
			}
		}

		//玩家进入攻击状态
		owner.setState(PlayerState.FIGHT);

//		//主动Pk移除被杀保护Buff
//		if(FighterState.PKBAOHU.compare(player.getFightState())){
//			ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_FOR_KILLED);
//			MessageUtil.notify_player(player, Notifys.NORMAL, "您主动发起了对其他玩家的PK，和平保护BUFF消失了");
//		}
//		
//		//主动Pk移除夜晚保护Buff
//		if(FighterState.PKBAOHUFORNIGHT.compare(player.getFightState())){
//			ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_IN_NIGHT);
//		}

		int dir = MapUtils.countDirection(pet.getPosition(), target.getPosition());
		//玩家转向
		pet.setDirection((byte) dir);
		pet.setLastFightTime(System.currentTimeMillis());

//		//魔法值消耗
//		pet.setMp(pet.getMp() - model.getQ_need_mp());
//		ManagerPool.petInfoManager.onMpChange(pet);

		//开始冷却
		double speed = ((double) pet.getAttackSpeed()) / 100;
//		//添加技能冷却
//		ManagerPool.cooldownManager.addCooldown(pet, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long)(model.getQ_cd() ));
//		//添加技能公共冷却
//		ManagerPool.cooldownManager.addCooldown(pet, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()), (long)(model.getQ_public_cd() / speed));

		//添加怪物攻击冷却
		ManagerPool.cooldownManager.addCooldown(pet, CooldownTypes.PET_ATTACK, null, (long) ((model.getQ_public_cd() - speed)>300?(model.getQ_public_cd() - speed):300));

		//添加怪物攻击动画冷却
		ManagerPool.cooldownManager.addCooldown(pet, CooldownTypes.PET_ACTION, null, (long) (model.getQ_delay_time()));

		long fightId = Config.getId();

		MessageUtil.tell_round_message(pet, getFightBroadcastMessage(fightId, pet, target, skill.getSkillModelId(), dir));

		//技能飞行时间计算
		//long fly = model.getQ_delay_time();
		long fly = (long) (model.getQ_delay_time() *(model.getQ_public_cd() - speed)/model.getQ_public_cd());

		//单体攻击
		if (model.getQ_area_shape() == 1 && target != null && model.getQ_trajectory_speed() >0) {
			fly += MapUtils.countDistance(pet.getPosition(), target.getPosition()) * 1000 / model.getQ_trajectory_speed();
		}

		if(fly <=50){
			fly = 50;
		}
		if (fly > 0) {
			//延时伤害
			HitTimer timer = new HitTimer(fightId, pet, target, skill, dir, fly, true);
			TimerUtil.addTimerEvent(timer);
		} else {
			//即时伤害
			attack(fightId, pet, target, skill, dir, true);
		}
		PetScriptManager.getInstance().useSkill(pet, skill);
	}

	
	/**
	 * 宠物攻击玩家
	 *
	 * @param roleId 玩家Id
	 * @param targetId 怪物Id
	 * @param skillId 攻击技能
	 * @param direction 攻击方向
	 */
	public void summonpetAttackPlayer(SummonPet summonpet, Player target, Skill skill) {
		//宠物主人死亡
		Player owner = ManagerPool.playerManager.getPlayer(summonpet.getOwnerId());
		if (owner == null || owner.isDie()) {
			return;
		}
		//宠物死亡
		if (summonpet.isDie()) {
			return;
		}

		//玩家已经死亡
		if (target.isDie()) {
			return;
		}

		//定身或睡眠中
		if (FighterState.DINGSHEN.compare(summonpet.getFightState()) || FighterState.SHUIMIAN.compare(summonpet.getFightState())) {
			return;
		}


		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
		if (model == null) {
			return;
		}

		//目标检查 （单体目标且对象不为自己时）
		if (model.getQ_area_shape() == 1 && model.getQ_target() != 1 && (target == null || target.isDie())) {
			//无目标错误
			return;
		}

		//攻击冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(summonpet, CooldownTypes.SUMMONPET_ATTACK, null)) {
			return;
		}

		//怪物正在攻击动画播放
		if(ManagerPool.cooldownManager.isCooldowning(summonpet, CooldownTypes.SUMMONPET_ACTION, null)){
			return;
		}

//		//冷却检查
//		if(ManagerPool.cooldownManager.isCooldowning(pet, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()))){
//			return;
//		}
//		
//		//公共冷却检查
//		if(ManagerPool.cooldownManager.isCooldowning(pet, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()))){
//			return;
//		}

		Map map = ManagerPool.mapManager.getMap(owner.getServerId(), owner.getLine(), owner.getMap());
		Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer.getMap().get(map.getMapModelid());
		Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());
		if (target != null) {
			//不在pk列表中
			if (!owner.getEnemys().containsKey(target.getId())) {
				//PK状态检查
				if (owner.getPkState() == 0) {
					return;
				} else if (owner.getPkState() == 1) {
					//同队伍检查
					if (owner.getTeamid() == target.getTeamid() && owner.getTeamid() != 0) {
						return;
					}
					
					//同战盟检查
					if ((owner.getGuildId() == target.getGuildId() || GuildServerManager.getInstance().isFriendGuild(owner.getGuildId(), target.getGuildId())) && owner.getGuildId() != 0) {
						return;
					}
				}
			}
			
			int pkMinLevel = DEFAULT_PK_MIN_LEVEL, pkDiffLevel = DEFAULT_PK_DIFF_LEVEL;
			Q_globalBean pkMinLevelGolbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PK_MIN_LEVEL.getValue());
			if(pkMinLevelGolbalBean != null) pkMinLevel = pkMinLevelGolbalBean.getQ_int_value();
			Q_globalBean pkDiffLevelGolbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PK_DIFF_LEVEL.getValue());
			if(pkDiffLevelGolbalBean != null) pkDiffLevel = pkDiffLevelGolbalBean.getQ_int_value();

			//自己等级检查
			if (owner.getLevel() < pkMinLevel && mapBean.getQ_map_pkprotection() == 1) {
				return;
			}

			//敌人等级检查
			if (target.getLevel() < pkMinLevel && mapBean.getQ_map_pkprotection() == 1) {
				return;
			}

			if(!FighterState.FORCEPK.compare(target.getFightState())){
				//Pk保护Buff
				if (FighterState.PKBAOHU.compare(target.getFightState())) {
					Buff buff = ManagerPool.buffManager.getBuffByType(target, BuffType.PKPROTECT).get(0);
					long remain = buff.getStart() + buff.getTotalTime() - System.currentTimeMillis();
					if (remain > 0) {
						return;
					}
				}
	
				//夜晚挂机和平buff
				if (FighterState.PKBAOHUFORNIGHT.compare(target.getFightState())) {
					Buff buff = ManagerPool.buffManager.getBuffByType(target, BuffType.PROTECTFORNIGHT).get(0);
					long remain = buff.getStart() + buff.getTotalTime() - System.currentTimeMillis();
					if (remain > 0) {
						return;
					}
				}
	
				//等级差检查(地图开启等级差保护)
				if (Math.abs(owner.getLevel() - target.getLevel()) > pkDiffLevel && mapBean.getQ_map_pk() == 1) {
					return;
				}
	
				//自己安全区检查	
				if (ManagerPool.mapManager.isSafe(summonpet.getPosition(), owner.getMapModelId())) {
					return;
				}
	
				//目标安全区检查
	
				if (ManagerPool.mapManager.isSafe(target.getPosition(), owner.getMapModelId())) {
					return;
				}
			}
			//距离检查
			//计算格子之间距离，放宽一格
			Grid playerGrid = MapUtils.getGrid(summonpet.getPosition(), blocks);
			Grid monsterGrid = MapUtils.getGrid(target.getPosition(), blocks);
			if (model.getQ_range_limit() >= 0 && MapUtils.countDistance(playerGrid, monsterGrid) > model.getQ_range_limit() + 1) {
				//超出攻击距离
				log.debug("超出攻击距离");
				return;
			}
			
			IAttackCheckScript script = (IAttackCheckScript) ManagerPool.scriptManager.getScript(ScriptEnum.CHECKATTACK);
			if (script != null) {
				try {
					if(!script.check(summonpet, target)){
						return;
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			} else {
				log.error("攻击检查脚本不存在！");
			}
		}

		//玩家进入攻击状态
		owner.setState(PlayerState.FIGHT);

//		//主动Pk移除被杀保护Buff
//		if(FighterState.PKBAOHU.compare(player.getFightState())){
//			ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_FOR_KILLED);
//			MessageUtil.notify_player(player, Notifys.NORMAL, "您主动发起了对其他玩家的PK，和平保护BUFF消失了");
//		}
//		
//		//主动Pk移除夜晚保护Buff
//		if(FighterState.PKBAOHUFORNIGHT.compare(player.getFightState())){
//			ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_IN_NIGHT);
//		}

		int dir = MapUtils.countDirection(summonpet.getPosition(), target.getPosition());
		//玩家转向
		summonpet.setDirection((byte) dir);
		summonpet.setLastFightTime(System.currentTimeMillis());

//		//魔法值消耗
//		pet.setMp(pet.getMp() - model.getQ_need_mp());
//		ManagerPool.petInfoManager.onMpChange(pet);

		//开始冷却
		double speed = ((double) summonpet.getAttackSpeed()) / 1000;
//		//添加技能冷却
//		ManagerPool.cooldownManager.addCooldown(pet, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long)(model.getQ_cd() ));
//		//添加技能公共冷却
//		ManagerPool.cooldownManager.addCooldown(pet, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()), (long)(model.getQ_public_cd() / speed));

		//添加怪物攻击冷却
		ManagerPool.cooldownManager.addCooldown(summonpet, CooldownTypes.SUMMONPET_ATTACK, null,(long) ((model.getQ_public_cd() - speed)>300?(model.getQ_public_cd() - speed):300));

		//添加怪物攻击动画冷却
		ManagerPool.cooldownManager.addCooldown(summonpet, CooldownTypes.SUMMONPET_ACTION, null, (long) (model.getQ_delay_time()));

		long fightId = Config.getId();

		MessageUtil.tell_round_message(summonpet, getFightBroadcastMessage(fightId, summonpet, target, skill.getSkillModelId(), dir));

		//技能飞行时间计算
		//long fly = model.getQ_delay_time();
		long fly = (long) (model.getQ_delay_time() *(model.getQ_public_cd() - speed)/model.getQ_public_cd());

		//单体攻击
		if (model.getQ_area_shape() == 1 && target != null && model.getQ_trajectory_speed() >0) {
			fly += MapUtils.countDistance(summonpet.getPosition(), target.getPosition()) * 1000 / model.getQ_trajectory_speed();
		}

		if(fly <=50){
			fly = 50;
		}
		if (fly > 0) {
			//延时伤害
			HitTimer timer = new HitTimer(fightId, summonpet, target, skill, dir, fly, true);
			TimerUtil.addTimerEvent(timer);
		} else {
			//即时伤害
			attack(fightId, summonpet, target, skill, dir, true);
		}
	}

//	/**
//	 * @deprecated 宠物不可以攻击宠物
//	 * 宠物攻击宠物
//	 * @param roleId 玩家Id
//	 * @param targetId 怪物Id
//	 * @param skillId 攻击技能
//	 * @param direction 攻击方向
//	 */
//	public void petAttackPet(Pet pet, Pet targetPet, Skill skill){
//		//宠物主人死亡
//		Player owner = ManagerPool.playerManager.getPlayer(pet.getOwnerId());
//		if(owner==null || owner.isDie()) return;
//		//宠物死亡
//		if(pet.isDie()) return;
//		
//		//玩家已经死亡
//		if(targetPet==null || targetPet.isDie()){
//			return;
//		}
//		
//		//定身或睡眠中
//		if(FighterState.DINGSHEN.compare(pet.getFightState()) || FighterState.SHUIMIAN.compare(pet.getFightState())) return;
//		
//		
//		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
//		if(model==null) return;
//		
//		//目标检查 （单体目标且对象不为自己时）
//		if(model.getQ_area_shape()==1 && model.getQ_target()!=1 && (targetPet==null || targetPet.isDie())){
//			//无目标错误
//			return;
//		}
//		
//		//消耗检查
//		if(pet.getMp() < model.getQ_need_mp()){
//			//魔法值不足
//			return;
//		}
//		
//		//冷却检查
//		if(ManagerPool.cooldownManager.isCooldowning(pet, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()))){
//			return;
//		}
//		
//		//公共冷却检查
//		if(ManagerPool.cooldownManager.isCooldowning(pet, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()))){
//			return;
//		}
//		
//		Map map = ManagerPool.mapManager.getMap(owner.getServerId(), owner.getLine(), owner.getMap());
//		Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer.getMap().get(map.getMapModelid());
//		Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());
//		Player target = ManagerPool.playerManager.getPlayer(targetPet.getOwnerId());
//		if(target!=null){
//			//不在pk列表中
//			if(!owner.getEnemys().containsKey(target.getId())){
//				//PK状态检查
//				if(owner.getPkState()==0){
//					return;
//				}
//			}
//			
//			//自己等级检查
//			if(owner.getLevel() < 30 && mapBean.getQ_map_pkprotection()==1){
//				return;
//			}
//			
//			//敌人等级检查
//			if(target.getLevel() < 30 && mapBean.getQ_map_pkprotection()==1){
//				return;
//			}
//			
//			//Pk保护Buff
//			if(FighterState.PKBAOHU.compare(target.getFightState())){
//				Buff buff = ManagerPool.buffManager.getBuffByType(target, BuffType.PKPROTECT).get(0);
//				long remain = buff.getStart() + buff.getTotalTime() - System.currentTimeMillis();
//				if(remain > 0){
//					return;
//				}
//			}
//			
//			//夜晚挂机和平buff
//			if(FighterState.PKBAOHUFORNIGHT.compare(target.getFightState())){
//				Buff buff = ManagerPool.buffManager.getBuffByType(target, BuffType.PROTECTFORNIGHT).get(0);
//				long remain = buff.getStart() + buff.getTotalTime() - System.currentTimeMillis();
//				if(remain > 0){
//					return;
//				}
//			}
//			
//			//等级差检查(地图开启等级差保护)
//			if(Math.abs(owner.getLevel() - target.getLevel()) > 20 && mapBean.getQ_map_pk()==1){
//				return;
//			}
//			
//			//自己安全区检查
//			Grid playerGrid = MapUtils.getGrid(pet.getPosition(), blocks);
//			if(playerGrid.getSafe()==1){
//				return;
//			}
//			
//			//目标安全区检查
//			Grid monsterGrid = MapUtils.getGrid(targetPet.getPosition(), blocks);
//			if(monsterGrid.getSafe()==1){
//				return;
//			}
//
//			//距离检查
//			//计算格子之间距离，放宽一格
//			if(MapUtils.countDistance(playerGrid, monsterGrid) > model.getQ_range_limit() + 1){
//				//超出攻击距离
//				log.debug("超出攻击距离");
//				return;
//			}
//		}
//
//		//玩家进入攻击状态
//		owner.setState(PlayerState.FIGHT);
//		
////		//主动Pk移除被杀保护Buff
////		if(FighterState.PKBAOHU.compare(player.getFightState())){
////			ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_FOR_KILLED);
////			MessageUtil.notify_player(player, Notifys.NORMAL, "您主动发起了对其他玩家的PK，和平保护BUFF消失了");
////		}
////		
////		//主动Pk移除夜晚保护Buff
////		if(FighterState.PKBAOHUFORNIGHT.compare(player.getFightState())){
////			ManagerPool.buffManager.removeByBuffId(player, Global.PROTECT_IN_NIGHT);
////		}
//		
//		int dir = MapUtils.countDirection(pet.getPosition(), targetPet.getPosition());
//		//玩家转向
//		pet.setDirection((byte)dir);
//		
//		//魔法值消耗
//		pet.setMp(pet.getMp() - model.getQ_need_mp());
//		ManagerPool.petManager.onMpChange(pet);
//		
//		//开始冷却
//		double speed = 1 + ((double)pet.getAttackSpeed()) / 1000;
//		//添加技能冷却
//		ManagerPool.cooldownManager.addCooldown(pet, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long)(model.getQ_cd() ));
//		//添加技能公共冷却
//		ManagerPool.cooldownManager.addCooldown(pet, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()), (long)(model.getQ_public_cd() / speed));
//		
//		long fightId = Config.getId();
//		
//		MessageUtil.tell_round_message(pet, getFightBroadcastMessage(fightId, pet, targetPet, skill.getSkillModelId(), dir));
//		
//		//技能飞行时间计算
//		long fly = model.getQ_delay_time();
//		//单体攻击
//		if(model.getQ_area_shape()==1 && target!=null){
//			fly += MapUtils.countDistance(pet.getPosition(), targetPet.getPosition()) * 1000 / model.getQ_trajectory_speed();
//		}
//		
//		if(fly > 0){
//			//延时伤害
//			HitTimer timer = new HitTimer(fightId, pet, targetPet, skill, dir, fly);
//			TimerUtil.addTimerEvent(timer);
//		}else{
//			//即时伤害
//			attack(fightId, pet, targetPet, skill, dir);
//		}
//	}
	/**
	 * 攻击
	 *
	 * @param attacker 攻击者
	 * @param defender 防御者
	 * @param skill 使用技能
	 * @param direction 方向
	 */
	public void attack(long fightId, Fighter attacker, Fighter defender, Skill skill, int direction, boolean trigger) {
		//死亡检查
		if (attacker.isDie()) {
			return;
		}
		if (defender != null && defender.isDie()) {
			return;
		}
		if (defender != null) {
			if (attacker.getLine() != defender.getLine() || attacker.getMap() != defender.getMap()) {
				return;
			}
		}

		//获得技能模板
		Q_skill_modelBean model = null;
		if (attacker instanceof Player) {
			model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getRealLevel((Player) attacker));
		} else {
			model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
		}
		
		// 走特殊流程
		if (model != null  && model.getQ_skill_type() == 1 && defender != null) {
				this.useGroundMagic((Player) attacker, skill, defender.getMap(), defender.getLine(), defender.getPosition());
				return ;
		}
		
		//技能目标选择
		ISkillScript skillscript = null;
		if(model.getQ_skill_script() > 0){
			skillscript = (ISkillScript) ManagerPool.scriptManager.getScript(model.getQ_skill_script());
		}

		Map map = ManagerPool.mapManager.getMap(attacker.getServerId(), attacker.getLine(), attacker.getMap());
		if (map == null) {
			return;
		}
		Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer.getMap().get(map.getMapModelid());

		Grid[][] grids = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());

		//中心点
		Position center = null;
		if (model.getQ_area_target() == 1) {
			//自身
			Grid grid = MapUtils.getGrid(attacker.getPosition(), grids);
			center = grid.getCenter();
		} else {
			//敌人
			Grid grid = MapUtils.getGrid(defender.getPosition(), grids);
			center = grid.getCenter();
		}

		List<Fighter> fighters = null;

		//设置敌人类型 0-全部, 1-玩家和宠物和召唤怪, 2-怪物
		int type = 0;
		//玩家类型选择 0-和平 1-强制 2-全体
		int playerAttackType = 0;

		if (attacker instanceof Player || attacker instanceof Pet || attacker instanceof SummonPet) {
			Player role = null;
			if (attacker instanceof Player) {
				role = (Player) attacker;
			}else if (attacker instanceof Pet) {
				role = PlayerManager.getInstance().getPlayer(((Pet) attacker).getOwnerId());
			}else if (attacker instanceof SummonPet) {
				role = PlayerManager.getInstance().getPlayer(((SummonPet) attacker).getOwnerId());
			}
			
			//人物pk模式
			playerAttackType = role.getPkState();
			//人物是否站在安全区内
			if (ManagerPool.mapManager.isSafe(role.getPosition(), map.getMapModelid())) {
				playerAttackType = 0;
			}
			
			int pkMinLevel = DEFAULT_PK_MIN_LEVEL;
			Q_globalBean pkMinLevelGolbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PK_MIN_LEVEL.getValue());
			if(pkMinLevelGolbalBean != null) pkMinLevel = pkMinLevelGolbalBean.getQ_int_value();
			
			//自己等级检查
			if (role.getLevel() < pkMinLevel && mapBean.getQ_map_pkprotection() == 1) {
				playerAttackType = 0;
			}
		} else if (attacker instanceof Monster) {
			type = 0;
			playerAttackType = 2;
		}

		if (attacker instanceof Player && defender != null) {
			ManagerPool.petOptManager.onwerAttack((Player) attacker, defender, 0);
			ManagerPool.summonpetOptManager.onwerAttack((Player) attacker, defender, 0);
		}
		
		boolean issingle = false;
		//点
		if (model.getQ_area_shape() == 1) {
			issingle = true;
			fighters = new ArrayList<Fighter>();
			if(checkCanAttack(attacker, defender, skill.getSkillModelId())){
				fighters.add(defender);
			}
		} //线形
		else if (model.getQ_area_shape() == 2) {
			//计算角度
			direction = countDirection(direction);
			//测试用 显示范围
			if (attacker instanceof Player && syncArea.contains(attacker.getId())) {
				showGridInLine((Player) attacker, center, direction, model.getQ_area_width(), model.getQ_area_length(), grids);
			}
			//获取范围内战斗者
			fighters = getFighterInLine(attacker, center, getFighters(center, map, model.getQ_area_length(), type), direction, model.getQ_area_width(), model.getQ_area_length(), playerAttackType, grids, skill.getSkillModelId());
		} //锥形
		else if (model.getQ_area_shape() == 3) {
			//计算角度
			direction = countDirection(direction);
			//测试用 显示范围
			if (attacker instanceof Player && syncArea.contains(attacker.getId())) {
				showGridInSector((Player) attacker, center, direction, model.getQ_sector_start(), model.getQ_sector_radius(), grids);
			}
			//获取范围内战斗者
			fighters = getFighterInSector(attacker, center, getFighters(center, map, model.getQ_sector_radius(), type), direction, model.getQ_sector_start(), model.getQ_sector_radius(), playerAttackType, grids, skill.getSkillModelId());
			//再套一个小圆圈
			fighters.addAll(getFighterInCircle(attacker, center, getFighters(center, map, 100, type), 100, playerAttackType, grids, skill.getSkillModelId()));
		
		} //圆形
		else if (model.getQ_area_shape() == 4) {
			//测试用 显示范围
			if (attacker instanceof Player && syncArea.contains(attacker.getId())) {
				showGridInCircle((Player) attacker, center, model.getQ_circular_radius(), grids);
			}
			fighters = getFighterInCircle(attacker, center, getFighters(center, map, model.getQ_circular_radius(), type), model.getQ_circular_radius(), playerAttackType, grids, skill.getSkillModelId());
		}
		
		if(fighters==null || fighters.size()==0){
			//如果是自爆的技能，就把自爆人血量设为0q_skill_type 5 为自爆人
			if(model.getQ_skill_type() == 5 && attacker instanceof Player){
				attacker.setHp(0);
				ManagerPool.playerManager.die((Player) attacker, null);
			}
			return;
		}
		
		//随机选择战斗者
		fighters = randomSelectFighters(fighters, model.getQ_target_max());

//		//攻击前可触发Skill
//		List<Skill> beforeAttackSkills = null;
		//攻击可触发Skill
		List<Skill> attackSkills = null;
		/*panic god 暂时屏蔽弓箭技能
		List<Skill> bowSkills = null;
		boolean triggerBow = false;
		int triggerNum = 0;
		int maxTriggerNum = 0;
		boolean triggerHorseWeapon = false;
		*/
		//攻击者可触发Buff
		List<Buff> attackBuffs = null;
		if(trigger){
			if (attacker instanceof Player) {
	//			beforeAttackSkills = ManagerPool.skillManager.getSkillTriggerBeforeAttack((Player)attacker);
				attackSkills = ManagerPool.skillManager.getSkillTriggerByAttack((Player) attacker);

				/*panic god 暂时// 暗器触发
				if (fighters.get(0) != null) {
					triggerHiddenWeaponSkill(fightId, (Player) attacker, fighters.get(0), direction);
				}
				*/

			} else if (attacker instanceof Pet) {
				attackSkills = ManagerPool.skillManager.getPetSkillTriggerByAttack((Pet) attacker);
			}
			
			/*panic god 暂时屏蔽弓箭技能
			if (attacker instanceof Player) {
				bowSkills = ManagerPool.arrowManager.tiggerSkillList((Player) attacker);
				maxTriggerNum = ManagerPool.arrowManager.tiggerSkillNum((Player) attacker);
			}
			*/
			attackBuffs = ManagerPool.buffManager.getBuffTriggerByAttack(attacker);
		}
		// 无视一击*  现在改为不用计算闪避，一定命中，并且最后伤害*3
		//卓越一击* 取暴击概率1.2 然后还可以加暴击概率值
		// 会心一击*取伤害上限最大值
		int critMul = Global.MAX_PROBABILITY;
		int isIGNORE_ATTACKPERCENT = 0;
		int isPERFECT_ATTACKPERCENT = 0;
		int isKNOWING_ATTACKPERCENT = 0;
		
		
		if (attacker instanceof Player) {
			// 无视一击
			if (RandomUtils.random(Global.MAX_PROBABILITY) < ((Player) attacker).getIgnore_attackPercent()) {
				isIGNORE_ATTACKPERCENT = Global.IGNORE_ATTACKPERCENT;
			}
			// 卓越一击
			if (RandomUtils.random(Global.MAX_PROBABILITY) < ((Player) attacker).getPerfect_attackPercent()) {
				// 暴击倍率   
				critMul = (int) ((Global.CRIT_ZHUOYUE + (double) ((Player) attacker).getPerfect_addattackPercent() / Global.MAX_PROBABILITY) * Global.MAX_PROBABILITY);
				isPERFECT_ATTACKPERCENT = Global.PERFECT_ATTACKPERCENT;
			}
			//会心一击
			if (RandomUtils.random(Global.MAX_PROBABILITY) < ((Player) attacker).getKnowing_attackPercent()) {
				isKNOWING_ATTACKPERCENT = Global.KNOWING_ATTACKPERCENT;
			}
		}
		FighterInfo attackerinfo = new FighterInfo();
		attackerinfo.copyFighter(attacker);
		
		boolean topHorseFlag = true;
		for (int i = 0; i < fighters.size(); i++) {
			Fighter fighter = fighters.get(i);
			if (fighter instanceof Pet) {
				Player owner = ManagerPool.playerManager.getPlayer(((Pet) fighter).getOwnerId());
				if(owner.isDie()) continue;
			}else if (fighter instanceof SummonPet) {
				Player owner = ManagerPool.playerManager.getPlayer(((SummonPet) fighter).getOwnerId());
				if(owner.isDie()) continue;
			}
			
			//攻击结果0-成功 1-MISS 2-跳闪 4-暴击  8-格挡 6-跳闪+暴击 12-格挡+暴击 16-无敌 32-死亡中被打 64-被秒杀
			FightResult fightResult = new FightResult();
			fightResult.special = 0;
			//无敌
			if (FighterState.WUDI.compare(fighter.getFightState())) {
				fightResult.fightResult = 16;
				MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId, attacker, fighter, skill, fightResult.fightResult, 0, 0, 0, fightResult.special));
				continue;
			}
			if(isPERFECT_ATTACKPERCENT == Global.PERFECT_ATTACKPERCENT){
				fightResult.fightResult = fightResult.fightResult | 4;
				fightResult.special |= 1 << 1;
			}

			//技能效果* > 无视一击* > 卓越一击* > 命中判定 > 会心一击* >
//			10009 冰
//			10010 毒
//			10098 雷
			// 技能元素属性（0=冰 1=雷 2=毒 3=无属性（指定概率））
			if (model.getQ_skill_elementtype() == 0) {//1100 冰
				if (RandomUtils.random(model.getQ_element_ice() + fighter.getIce_def() + 7) > fighter.getIce_def() +7) {
					int result = ManagerPool.buffManager.addBuff(attacker, fighter, Integer.valueOf(model.getQ_passive_buff()),
							1, 0, 0, 0, 0);
					if (result == 0) {
						//MessageUtil.notify_player(fighter, Notifys.ERROR, "使用出错。");
					}
				}
			} else if (model.getQ_skill_elementtype() == 2) {
				if (RandomUtils.random(model.getQ_element_bane() + fighter.getPoison_def() + 7) > fighter.getPoison_def()+7) { //1045  毒
					int result = ManagerPool.buffManager.addBuff(attacker,fighter, Integer.valueOf(model.getQ_passive_buff()), 1, 0, 0, 0, 0);
					if (result == 0) {
						// MessageUtil.notify_player(fighter, Notifys.ERROR,
						// "使用出错。");
					}

				}
			} 

		
			//造成伤害
			if (model.getQ_trigger_figth_hurt() == 1) {
				/* panic god 屏蔽跳
				boolean sidestep = true;
				if(skillscript!=null){
					try{
						//技能是否能被跳闪
						sidestep = skillscript.canJumpSidestep(attacker, fighter);
					}catch(Exception e){
						log.error(e, e);
					}
				}
				if(sidestep){
					if (!ManagerPool.skillManager.isIgnoreJumpMiss(model) && fighter instanceof Player) {
						//TODO 跳跃中闪避
						//if (PlayerState.JUMP.compare(((Player) fighter).getState()) || PlayerState.DOUBLEJUMP.compare(((Player) fighter).getState())) {
						if(((Player) fighter).isJumpProtect()){
							fightResult.fightResult = 2;
						}
						
					} else if (fighter instanceof Pet) {
						//跳跃中闪避
						if (((Pet) fighter).getJumpState() != PetJumpState.NOMAL) {
							fightResult.fightResult = 2;
						}
					}else if (fighter instanceof SummonPet) {
						//跳跃中闪避
						if (((SummonPet) fighter).getJumpState() != SummonPetJumpState.NOMAL) {
							fightResult.fightResult = 2;
						}
					}
				}
               */
				//对象死亡
				if (fighter.isDie()) {
//					fighter.setReduce(0);
					continue;
				}
				/* panic god 屏蔽跳
				//死亡中被打特殊
				if (fighter instanceof Monster && MonsterState.DIEING.compare(((Monster) fighter).getState())) {
					fightResult.fightResult = 32;
//					fighter.setReduce(0);
					MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId, attacker, fighter, skill, fightResult.fightResult, 0, 0, 0));
					ManagerPool.monsterManager.die((Monster) fighter, attacker, skill.getSkillModelId());
					continue;
				}
               */
				//连击秒怪
				if (fighter instanceof Monster && attacker instanceof Player) {
					Monster monster = (Monster) fighter;
					Player player = (Player) attacker;
					if (!monster.isDie() && ManagerPool.batterManager.checkEvencut(player, monster)) {
						fightResult.fightResult = 64;
//						fighter.setReduce(0);
						addHatred(monster, player, 100);
						int hp = monster.getHp();
						monster.getDamages().put(player.getId(), hp);
						monster.setHp(0);
						MonsterManager.getInstance().die(monster, player, skill.getSkillModelId());
						MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId, attacker, fighter, skill, fightResult.fightResult, hp, 0, 0, fightResult.special));
						continue;
					}
				}

				if(!(attacker instanceof Pet || fighter instanceof Pet)){
					//未跳闪
					if (fightResult.fightResult == 0) {
						if(!ManagerPool.skillManager.isIgnoreMiss(model) && FighterState.FORCEDODGE.compare(fighter.getFightState())){
							fightResult.fightResult = 1;
							ManagerPool.buffManager.removeByType(fighter, BuffType.ZIMANGBUFF);
//							fighter.setReduce(0);
							MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId, attacker, fighter, skill, fightResult.fightResult, 0, 0, 0, fightResult.special));
							continue;
						}
	//					//怪物死亡中状态不会闪避
	//					if(!(fighter instanceof Monster && MonsterState.DIEING.compare(((Monster)fighter).getState()))){
						//命中计算
						/*panic god 屏蔽
						int dodge = 0;//(int)(Global.MAX_PROBABILITY * ((double)fighter.getDodge() / (fighter.getDodge() + attacker.getDodge())  * (double)fighter.getLevel() / (fighter.getLevel() + attacker.getLevel())));
						if (fighter.getDodge() == 0) {
							dodge = 0;
						} else {
	//						 (int) (10000 * ((double) 被攻击者闪避 / (被攻击者闪避 + 攻击者闪避) * 被攻击者等级/ (被攻击者等级 + 攻击者等级)));
							dodge = (int) (Global.MAX_PROBABILITY * ((double) fighter.getDodge() / (fighter.getDodge() + attacker.getDodge()) * (double) fighter.getLevel() / (fighter.getLevel() + attacker.getLevel())));
						}
						if (fighter instanceof Monster) {
							dodge = (int) ((double) dodge * 0.2);
						}
						log.debug("闪避值" + dodge + "-->攻击者名字" + attacker.getName() + "攻击者等级" + attacker.getLevel() + "攻击者闪避" + attacker.getDodge() + "防御者名字" + fighter.getName() + "防御者等级" + fighter.getLevel() + "防御者闪避" + fighter.getDodge());
						if (dodge < 500) {
							dodge = 500;
						} else if (dodge > 9500) {
							dodge = 9500;
						}*/
						//闪避  被攻击者的闪避 / 攻击者的命中
						if(isIGNORE_ATTACKPERCENT != Global.IGNORE_ATTACKPERCENT){
							int hit = attacker.getHit();//(int)(Global.MAX_PROBABILITY * ((double)fighter.getDodge() / (fighter.getDodge() + attacker.getDodge())  * (double)fighter.getLevel() / (fighter.getLevel() + attacker.getLevel())));
							int dodge = fighter.getDodge();//(int)(Global.MAX_PROBABILITY * ((double)fighter.getDodge() / (fighter.getDodge() + attacker.getDodge())  * (double)fighter.getLevel() / (fighter.getLevel() + attacker.getLevel()))); 
							//0.95*防御方防率/（攻击方攻率+防御方防率）
	                        int succ = (int)( Global.MAX_PROBABILITY* 0.95 *dodge/(hit+dodge));
							if(dodge > 0){
								if((!ManagerPool.skillManager.isIgnoreMiss(model) && RandomUtils.random(Global.MAX_PROBABILITY) < succ) ){
									fightResult.fightResult = 1;
	//								fighter.setReduce(0);
									MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId, attacker, fighter, skill, fightResult.fightResult, 0, 0, 0, fightResult.special));
									continue;
								}
							}
	//					}
						}
						if (fighter instanceof Player) {
							if (isWudi((Player) fighter)) {
								fightResult.fightResult = 1;
//								fighter.setReduce(0);
								MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId, attacker, fighter, skill, fightResult.fightResult, 0, 0, 0, fightResult.special));
								continue;
							}
							//攻击时要下马,怪攻击人不会下马
							if ( !(attacker instanceof Monster) &&  ManagerPool.horseManager.isRidding((Player) fighter)){
								ManagerPool.horseManager.unride((Player) fighter);
							}
						}
					}
				}


				/* panic god 屏蔽
				//暴击buff
				List<Buff> critBuffs = ManagerPool.buffManager.getBuffByType(attacker, BuffType.MULCRIT);
				if (critBuffs.size() > 0) {
					Buff critBuff = critBuffs.get(0);
					int result = critBuff.action(attacker, attacker);
					if (result == 2) {
						//移除buff
						ManagerPool.buffManager.removeBuff(attacker, critBuff);
					}
					Q_buffBean buffModel = ManagerPool.dataManager.q_buffContainer.getMap().get(critBuff.getModelId());
					critMul = buffModel.getQ_effect_ratio();
					fightResult.fightResult = fightResult.fightResult | 4;
				}

				//皇权buff
				Buff powerBuff = null;
				if ((fightResult.fightResult & 4) == 0) {
					List<Buff> powerBuffs = ManagerPool.buffManager.getBuffByType(attacker, BuffType.BAQUANBUFF);
					if (powerBuffs.size() > 0) {
						powerBuff = powerBuffs.get(0);
						Q_buffBean buffModel = ManagerPool.dataManager.q_buffContainer.getMap().get(powerBuff.getModelId());
						if (RandomUtils.random(Global.MAX_PROBABILITY) < buffModel.getQ_trigger_probability()) {
							//暴击倍率
							critMul = Global.MAX_PROBABILITY * Global.CRIT_MULTIPLE;
							fightResult.fightResult = fightResult.fightResult | 4;
						} else {
							powerBuff = null;
						}
					}
				}
                
				//未暴击过 
				if ((fightResult.fightResult & 4) == 0) {
					//命中计算
					int crit = 0;//(int)(Global.MAX_PROBABILITY * ((double)attacker.getCrit() / (fighter.getCrit() + attacker.getCrit())  * (double)attacker.getLevel() / (fighter.getLevel() + attacker.getLevel())));
					if (fighter.getCrit() == 0) {
						crit = 0;
					} else {
						crit = (int) (Global.MAX_PROBABILITY * ((double) attacker.getCrit() / (fighter.getCrit() + attacker.getCrit()) * (double) attacker.getLevel() / (fighter.getLevel() + attacker.getLevel())));
					}
					if (attacker instanceof Monster) {
						crit = (int) ((double) crit * 0.2);
					}else if(attacker instanceof Player){
						crit = (int) ((double) crit * 0.4);
					}
					log.debug("暴击值" + crit + "-->攻击者名字" + attacker.getName() + "攻击者等级" + attacker.getLevel() + "攻击者暴击" + attacker.getCrit() + "防御者名字" + fighter.getName() + "防御者等级" + fighter.getLevel() + "防御者暴击" + fighter.getCrit());
					if (crit < 500) {
						crit = 500;
					} else if (crit > 5000) {
						crit = 5000;
					}
					if (RandomUtils.random(Global.MAX_PROBABILITY) < crit) {
						//暴击倍率
						critMul = Global.MAX_PROBABILITY * Global.CRIT_MULTIPLE;
						fightResult.fightResult = fightResult.fightResult | 4;
					}
				}
                */
				//计算伤害
				if(attacker instanceof Pet){
					fightResult.damage = attacker.getAttack();
				}else if(attacker instanceof Player){
						// 伤害类型(0=物理 1=魔法)
						if(model.getQ_hurttype() == 0){
							if(isKNOWING_ATTACKPERCENT == Global.KNOWING_ATTACKPERCENT){
								fightResult.damage = ((Player)attacker).getPhysic_attackupper();
								fightResult.damage += fightResult.damage*((Player)attacker).getKnowing_addattackPercent()/Global.MAX_PROBABILITY;
								fightResult.special |= 1;
							}else{
								fightResult.damage = RandomUtils.random(((Player)attacker).getPhysic_attacklower() ,((Player)attacker).getPhysic_attackupper());	
							}
						}else{
							if(isKNOWING_ATTACKPERCENT == Global.KNOWING_ATTACKPERCENT){
								fightResult.damage = ((Player)attacker).getMagic_attackupper();	
								fightResult.damage += fightResult.damage*((Player)attacker).getKnowing_addattackPercent()/Global.MAX_PROBABILITY;
								fightResult.special |= 1;
							}else{
								fightResult.damage = RandomUtils.random(((Player)attacker).getMagic_attacklower() ,((Player)attacker).getMagic_attackupper());
							}
						}
				}else{
					fightResult.damage = attacker.getAttack();
				}
				
				/*xiaozhuoming: 暂时屏蔽
				//在会心一击判定完攻击力之后，乘一个等级压制系数min(int(max(自己等级-对方等级，0)/50 + 1),3)
				fightResult.damage = fightResult.damage * Math.min((int) (Math.max(attacker.getLevel() - fighter.getLevel(), 0) / 50 + 1), 3);*/
				
				//在会心一击判定完攻击力之后，乘一个等级压制系数min(int(max(怪物等级-人物等级，0)/50+1),3), 当怪物攻击玩家时有效
				if(attacker instanceof Monster && fighter instanceof Player) {
					fightResult.damage = fightResult.damage * Math.min((int) (Math.max(attacker.getLevel() - fighter.getLevel(), 0) / 50 + 1), 3);
				}
			    
				if (fightResult.damage < 0) {
					fightResult.damage = 0;
				}
				
				/* panic god  暂时屏蔽，用不到
				fightResult.backDamage += model.getQ_ignore_defence();
				if (attacker instanceof Monster ) {
					//怪物,召唤怪无视防御
					fightResult.backDamage += ((Monster) attacker).getIgnoreDamage();
				} else if(attacker instanceof SummonPet){
					fightResult.backDamage += ((SummonPet) attacker).getIgnoreDamage();
				}else if (attacker instanceof Player) {
					fightResult.backDamage += ((Player) attacker).getNegDefence();//境界属性 无视防御
					if (fighter instanceof Monster) {
						Q_monsterBean q_monsterBean = ManagerPool.dataManager.q_monsterContainer.getMap().get(((Monster) fighter).getModelId());
						if (q_monsterBean != null) {
							if (q_monsterBean.getQ_monster_type()==3) {
								fightResult.hitDamage = ManagerPool.batterManager.getbossBatter(((Player) attacker));
								fightResult.backDamage += fightResult.hitDamage;
							}else if (q_monsterBean.getQ_monster_type()==1){
								fightResult.backDamage += ((Player) attacker).getEvencutatk();
							}
						}
					}
				}
                */
				//随即攻击比例（最大攻击比例和最小攻击比例之间）
				int percent = Global.MAX_PROBABILITY;
				/*
				int max_damage = 12000;
				int min_damage = 8000 + attacker.getLuck() * 35;
				int percent = RandomUtils.random(min_damage, max_damage);
                */
				int defense = fighter.getDefense();
				if (attacker instanceof Player) {
					if(RandomUtils.random(Global.MAX_PROBABILITY) < ((Player) attacker).getignore_defendPercent()){
						defense = defense * (1 - ((Player) attacker).getignore_defend_add_Percent() / Global.MAX_PROBABILITY);
					}
				}
				if (ManagerPool.skillManager.isIgnoreDefense(model)) {
					defense = 0;
				}
//				//计算被攻击者防御减少
//				int defenseMul = Global.MAX_PROBABILITY;
//				//计算被攻击者防御减少buff
//				List<Buff> redefenseBuffs = ManagerPool.buffManager.getBuffByType(fighter, BuffType.MEIHUABUFF);
//				if ((fightResult & 2) == 0 && redefenseBuffs.size() > 0) {
//					for (int j = 0; j < redefenseBuffs.size(); j++) {
//						Buff redefenseBuff = redefenseBuffs.get(j);
//						redefenseBuff.action(fighter, fighter);
//						defenseMul += redefenseBuff.getParameter();
//					}
//				}
//				if(defenseMul < 0){
//					defenseMul = 0;
//				}
//				defense = (int)((double)defense * defenseMul / Global.MAX_PROBABILITY);
				
				if(!(attacker instanceof Pet)){
						fightResult.damage = fightResult.damage - defense;
				}
				//计算伤害加成
				//panic god 暂时屏蔽fightResult.damage = (int) (((double) fightResult.damage) * model.getQ_hurt_correct_factor() / Global.MAX_PROBABILITY);
				if (fightResult.damage <= 0) {
					fightResult.damage = 1;
				}
				//计算卓越一击伤害
				fightResult.damage = (int) (fightResult.damage * ((double) critMul / Global.MAX_PROBABILITY));
				//计算技能加成伤害
				if (skill != null) {
					Q_skill_modelBean skillModel =null;
					if(attacker instanceof Player){
						skillModel =  ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getRealLevel((Player)attacker));
					}else{
						 skillModel= ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
					}
					//! changed by xuliang 先计算除法，以免int溢出
					fightResult.damage = (int)(fightResult.damage * ((double)(Global.MAX_PROBABILITY + skillModel.getQ_skill_attack())/Global.MAX_PROBABILITY)) + skillModel.getQ_attack_addition();		
				}
				//增加伤害比例
				if(attacker instanceof Player){
					fightResult.damage = (int)(fightResult.damage * ((double)((Player) attacker).getAddInjure()/Global.MAX_PROBABILITY));
				}
				//吸收伤害比例
				if(fighter instanceof Player){
					fightResult.damage = fightResult.damage *((Player) fighter).getReduceInjure()/Global.MAX_PROBABILITY;	
				}
				//无视一击结算
				if(attacker instanceof Player && isIGNORE_ATTACKPERCENT !=0){
					fightResult.damage = fightResult.damage *3;
					fightResult.special |= 1 << 2;
				}
				
				//backDamage = backDamage * percent / Global.MAX_PROBABILITY * model.getQ_hurt_correct_factor() / Global.MAX_PROBABILITY * critMul / Global.MAX_PROBABILITY;

				//计算被攻击者伤害加深
				

				int damageMul = Global.MAX_PROBABILITY;
				//计算被攻击者伤害加深buff
				List<Buff> damageBuffs = ManagerPool.buffManager.getBuffByType(attacker, BuffType.DEEPENDAMAGE);
				if ((fightResult.fightResult & 2) == 0 && damageBuffs.size() > 0) {
					for (int j = 0; j < damageBuffs.size(); j++) {
						Buff damageBuff = damageBuffs.get(j);
						damageBuff.action(fighter, fighter);
						damageMul += damageBuff.getParameter();
					}
				}
				//伤害加深
				fightResult.damage = (int) ((double) fightResult.damage * damageMul / Global.MAX_PROBABILITY);
				/*panic god 屏蔽
				//backDamage = backDamage * damageMul / Global.MAX_PROBABILITY ;

				log.debug("attacker " + attacker.getName() + " damage " + fightResult.damage + " backdamage " + fightResult.backDamage);

				if (fightResult.damage < 0) {
					fightResult.damage = 0;
				}

				//伤害减少
				if (fighter instanceof Player) {
					//格挡中 墨子剑法Buff
					if (PlayerState.BLOCK.compare(((Player) fighter).getState())) {
						fightResult.fightResult = fightResult.fightResult | 8;
						//减少伤害
						int reduceDamage = (int) ((double) fightResult.damage * ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.SHIELD_REDUCTION.getValue()).getQ_int_value() / Global.MAX_PROBABILITY);
						fightResult.damage -= reduceDamage;
						//只反弹前3式的
						if (!FighterState.FANTAN.compare(fighter.getFightState())) {
							fightResult.damage += fightResult.backDamage;
							fightResult.backDamage = 0;
						} else {
							boolean rebound = false;
							List<Buff> buffs = ManagerPool.buffManager.getBuffByType(fighter, BuffType.REBOUND);
							for (int j = 0; j < buffs.size(); j++) {
								Buff buff = buffs.get(j);
								Q_buffBean buffModel = ManagerPool.dataManager.q_buffContainer.getMap().get(buff.getModelId());
								if (buffModel == null) {
									continue;
								}
								String[] rebounds = buffModel.getQ_Bonus_skill().split(Symbol.DOUHAO_REG);
								for (int k = 0; k < rebounds.length; k++) {
									if (skill.getSkillModelId() == Integer.parseInt(rebounds[k])) {
										rebound = true;
										break;
									}
								}
								if (rebound) {
									break;
								}
							}
							if (!rebound) {
								fightResult.damage += fightResult.backDamage;
								fightResult.backDamage = 0;
							}
						}
					} else {
						fightResult.damage += fightResult.backDamage;
						fightResult.backDamage = 0;
					}
				} else {
					fightResult.damage += fightResult.backDamage;
					fightResult.backDamage = 0;
				}
                */
				if(skillscript!=null){
					try{
						//技能附加伤害
						skillscript.damage(attacker, fighter, fightResult);
					}catch(Exception e){
						log.error(e, e);
					}
				}
				
				//固定伤害型怪物
				if (fighter instanceof Monster) {
					//获得怪物模型
					Q_monsterBean monsterModel = ManagerPool.dataManager.q_monsterContainer.getMap().get(((Monster) fighter).getModelId());
					//收到固定伤害
					if (monsterModel.getQ_fixed_hurt() == 1) {
						fightResult.damage = monsterModel.getQ_fiexd_value();
					}
				}

				//侍宠固定掉血
				else if(fighter instanceof Pet){
					//获得侍宠模型
					Q_petinfoBean petModel = ManagerPool.dataManager.q_petinfoContainer.getMap().get(((Pet) fighter).getModelId());
					//收到固定伤害
					fightResult.damage = petModel.getQ_fiexd_value();
				}
				
//				if (fighter.getReduce() > 0) {
//					fightResult.damage = fightResult.damage - fighter.getReduce();
//					fighter.setReduce(0);
//				}

				if (fightResult.damage < 0) {
					fightResult.damage = 0;
				}

				//受到伤害脚本触发
				IHitDamageScript script = (IHitDamageScript) ManagerPool.scriptManager.getScript(ScriptEnum.HIT_DAMAGE);
				if (script != null) {
					try {
						script.onDamage(attacker, fighter, fightResult);
					} catch (Exception e) {
						log.error(e, e);
					}
				} else {
					log.error("攻击伤害脚本不存在！");
				}

				//未跳跃闪避
				if ((fightResult.fightResult & 2) == 0) {

					FighterInfo fighterinfo = new FighterInfo();
					fighterinfo.copyFighter(fighter);
					
					// xuliang add
					fightResult.damage = fightResult.damage <= 0 ? 1 : fightResult.damage;
					fighter.setHp(fighter.getHp() - fightResult.damage);
					//反弹伤害,其他反弹伤害的地方暂时屏蔽
					if(fighter instanceof  Player){
						fightResult.backDamage = fightResult.damage * ((Player)fighter).getRebound_damage()/Global.MAX_PROBABILITY;
					}
					if (fighter.getHp() < 0) {
						fighter.setHp(0);
					}

					//技能可造成仇恨
					if (model.getQ_trigger_figth_hurt() > 0 && fightResult.damage > 0) {						
						if (attacker instanceof Player) {
							ManagerPool.petOptManager.onwerDamage((Player) attacker, fighter, fightResult.damage);
							ManagerPool.summonpetOptManager.onwerDamage((Player) attacker, fighter, fightResult.damage);
							
							//赤色要塞中，攻击玩家或怪物均会移除复活保护的buff
							//! add by xuliang
							if (mapBean.getQ_map_id() == ManagerPool.countryManager.SIEGE_MAPID){
								ManagerPool.buffManager.removeByBuffId((Player) attacker, Global.GCZ_PROTECT_FOR_KILLED);
							}else if(mapBean.getQ_map_id() == CsysManger.getInstance().CSYS_MAPID){
								ManagerPool.buffManager.removeByBuffId((Player) attacker, Global.CSYS_PROTECT_FOR_KILLED);
							}
						}

						if (attacker instanceof Player && (fighter instanceof Player || fighter instanceof Pet || fighter instanceof SummonPet)) {
							//主动Pk移除被杀保护Buff
							if (FighterState.PKBAOHU.compare(attacker.getFightState())) {
								ManagerPool.buffManager.removeByBuffId((Player) attacker, Global.PROTECT_FOR_KILLED);
								if(fighter instanceof Player){
									log.error("玩家(" + attacker.getId() + ")PK状态为(" + ((Player)attacker).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)attacker).getState()) + ")敌人列表为(" + MessageUtil.castListToString(((Player)attacker).getEnemys().values()) + ")对玩家(" + fighter.getId() + ")PK状态为(" + ((Player)fighter).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)fighter).getState()) + ")群体攻击导致和平保护buff消失");
								}else{
									log.error("玩家(" + attacker.getId() + ")PK状态为(" + ((Player)attacker).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)attacker).getState()) + ")敌人列表为(" + MessageUtil.castListToString(((Player)attacker).getEnemys().values()) + ")对宠物(" + fighter.getId() + ")群体攻击导致和平保护buff消失");
								}
								MessageUtil.notify_player((Player) attacker, Notifys.NORMAL, "您主动发起了对其他玩家的PK，和平保护BUFF消失了");
							}

							//主动Pk移除夜晚保护Buff
							if (FighterState.PKBAOHUFORNIGHT.compare(attacker.getFightState())) {
								ManagerPool.buffManager.removeByBuffId((Player) attacker, Global.PROTECT_IN_NIGHT);
								if(fighter instanceof Player){
									log.error("玩家(" + attacker.getId() + ")PK状态为(" + ((Player)attacker).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)attacker).getState()) + ")敌人列表为(" + MessageUtil.castListToString(((Player)attacker).getEnemys().values()) + ")对玩家(" + fighter.getId() + ")PK状态为(" + ((Player)fighter).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)fighter).getState()) + ")群体攻击导致夜晚和平保护buff消失");
								}else{
									log.error("玩家(" + attacker.getId() + ")PK状态为(" + ((Player)attacker).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)attacker).getState()) + ")敌人列表为(" + MessageUtil.castListToString(((Player)attacker).getEnemys().values()) + ")对宠物(" + fighter.getId() + ")群体攻击导致夜晚和平保护buff消失");
								}
							}
						}

						if (fighter instanceof Monster) {
							Monster monster = (Monster) fighter;

							Player owner = null;
							//增加敌对对象
							if (attacker instanceof Player) {
								owner = (Player) attacker;
								//增加连击
								/* xuliang 弃用Boss连击
								if (ManagerPool.monsterManager.isBoss(((Monster) fighter).getModelId())) {
									ManagerPool.batterManager.bossBatter(owner);
								}
								*/
							} else if (attacker instanceof Pet) {
								owner = ManagerPool.playerManager.getPlayer(((Pet) attacker).getOwnerId());
							}else if (attacker instanceof SummonPet) {
								owner = ManagerPool.playerManager.getPlayer(((SummonPet) attacker).getOwnerId());
							}
							
							if(owner!=null){
								 if (attacker instanceof SummonPet){
									//增加召唤怪的仇恨
									addHatred(monster, attacker, model.getQ_enmity());
									//增加仇恨，加一半
									addHatred(monster, owner, model.getQ_enmity()/2);
								 }else{
									//增加仇恨
									addHatred(monster, owner, model.getQ_enmity());
								 }

								//增加伤害统计
								if (monster.getDamages().containsKey(owner.getId())) {
									monster.getDamages().put(owner.getId(), monster.getDamages().get(owner.getId()) + fightResult.damage);

									
								} else {
									monster.getDamages().put(owner.getId(), fightResult.damage);
									
									
								}
	
								//怪物受到伤害脚本
								Q_monsterBean q_monsterBean = ManagerPool.dataManager.q_monsterContainer.getMap().get(monster.getModelId());
								if (q_monsterBean != null) {
									if (q_monsterBean.getQ_script_id() > 0) {
										IMonsterAiScript aiScript = (IMonsterAiScript) ManagerPool.scriptManager.getScript(q_monsterBean.getQ_script_id());
										if (aiScript != null) {
											aiScript.wasHit(monster,attacker,fightResult.damage);
										}
									}
								}
							}
						} else if (fighter instanceof Player) {
							checkEnemy(attacker, (Player)fighter, mapBean);
							
							ManagerPool.petOptManager.ownerDefence(attacker, (Player) fighter, fightResult.damage);
							ManagerPool.summonpetOptManager.ownerDefence(attacker, (Player) fighter, fightResult.damage);
							//玩家进入战斗状态
							if (!fighter.isDie() && !(attacker instanceof Monster)) {
								((Player) fighter).setState(PlayerState.FIGHT);
							}
							
							//人物受到伤害脚本
							IPlayerWasHitScript hscript = (IPlayerWasHitScript) ManagerPool.scriptManager.getScript(ScriptEnum.PLAYERWASHIT);
							if (hscript != null) {
								try{
									hscript.wasHit(attacker, (Player) fighter);
								}catch (Exception e) {
									log.error(e, e);
								}
							}
						} else if (fighter instanceof Pet) {
							Player owner = ManagerPool.playerManager.getPlayer(((Pet) fighter).getOwnerId());
							
							checkEnemy(attacker, owner, mapBean);
							//增加敌对对象
							/* xuliang 移除无用代码
							if (attacker instanceof Player) {
								if(!((Player) attacker).getEnemys().containsKey(owner.getId())){
									if(!owner.isDie()) owner.addEnemy((Player) attacker);
								}
								((Player) owner).getHits().add(attacker.getId());
							}else if (attacker instanceof SummonPet) {
								Player aOwner = ManagerPool.playerManager.getPlayer(((SummonPet) attacker).getOwnerId());								
								if(!aOwner.getEnemys().containsKey(owner.getId())){
									if(!owner.isDie()) owner.addEnemy((Player) attacker);
								}
								((Player) owner).getHits().add(aOwner.getId());

							}
							*/
							ManagerPool.petOptManager.petDefence(attacker, (Pet) fighter, fightResult.damage);
							//ManagerPool.summonpetOptManager.summonpetDefence(attacker, (SummonPet) fighter, fightResult.damage);
							//玩家进入战斗状态
							if (!owner.isDie()) {
								owner.setState(PlayerState.FIGHT);
							}
							
							//宠物受到伤害脚本
							IPetWasHitScript hscript = (IPetWasHitScript) ManagerPool.scriptManager.getScript(ScriptEnum.PETWASHIT);
							if (hscript != null) {
								try{
									hscript.wasHit(attacker, (Pet) fighter);
								}catch (Exception e) {
									log.error(e, e);
								}
							}
						}else if (fighter instanceof SummonPet) {
							Player owner = ManagerPool.playerManager.getPlayer(((SummonPet) fighter).getOwnerId());
							
							checkEnemy(attacker, owner, mapBean);
							//增加敌对对象
							/* xuliang 移除无用代码
							if (attacker instanceof Player) {
								if(!((Player) attacker).getEnemys().containsKey(owner.getId())){
									if(!owner.isDie()) owner.addEnemy((Player) attacker);
								}
								((Player) owner).getHits().add(attacker.getId());
							} else if (attacker instanceof Pet) {
								Player aOwner = ManagerPool.playerManager.getPlayer(((Pet) attacker).getOwnerId());								
								if(!aOwner.getEnemys().containsKey(owner.getId())){
									if(!owner.isDie()) owner.addEnemy(aOwner);
								}
								((Player) owner).getHits().add(aOwner.getId());

							}
//							else if (attacker instanceof Pet) {
//								Player aOwner = ManagerPool.playerManager.getPlayer(((Pet) attacker).getOwnerId());
//								owner.addEnemy(aOwner);
//							}
							 */
							ManagerPool.summonpetOptManager.summonpetDefence(attacker, (SummonPet) fighter, fightResult.damage);
							//玩家进入战斗状态
							if (!owner.isDie()&& !(attacker instanceof Monster)) {
								owner.setState(PlayerState.FIGHT);
							}
							
							//受到伤害脚本
							/*
							IPetWasHitScript hscript = (IPetWasHitScript) ManagerPool.scriptManager.getScript(ScriptEnum.PETWASHIT);
							if (hscript != null) {
								try{
									hscript.wasHit(attacker, (Pet) fighter);
								}catch (Exception e) {
									log.error(e, e);
								}
							}
							*/
						}
					}

					//怪物或玩家死亡
					if (fighter.getHp() == 0) {
						if (fighter instanceof Monster) {
							ManagerPool.monsterManager.die((Monster) fighter, attacker, skill.getSkillModelId());
						} else if (fighter instanceof Player) {
							ManagerPool.playerManager.die((Player) fighter, attacker);
						} else if (fighter instanceof Pet) {
							ManagerPool.petOptManager.die((Pet) fighter, attacker);
						}else if (fighter instanceof SummonPet) {
							ManagerPool.summonpetOptManager.die((SummonPet) fighter, attacker);
						}
					}
					//反弹伤害
					if (fightResult.backDamage > 0 && !attacker.isDie()) {
						attacker.setHp(attacker.getHp() - fightResult.backDamage);
						if (attacker.getHp() < 0) {
							attacker.setHp(0);
							fightResult.special = 1;
							MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId,  fighter, attacker, skill, 0, 0, 0, fightResult.backDamage, fightResult.special));
						}

						if (attacker instanceof Monster) {
							Monster monster = (Monster) attacker;
							//增加伤害统计
							if (monster.getDamages().containsKey(fighter.getId())) {
								monster.getDamages().put(fighter.getId(), monster.getDamages().get(fighter.getId()) + fightResult.backDamage);
								
							} else {
								monster.getDamages().put(fighter.getId(), fightResult.backDamage);
								
							}
							
							
						}

						//怪物或玩家死亡
						if (attacker.getHp() == 0) {
							if (attacker instanceof Monster) {
								ManagerPool.monsterManager.die((Monster) attacker, fighter, skill.getSkillModelId());
							} else if (attacker instanceof Player) {
								ManagerPool.playerManager.die((Player) attacker, fighter);
							} else if (attacker instanceof Pet) {
								ManagerPool.petOptManager.die((Pet) attacker, fighter);
							}else if (fighter instanceof SummonPet) {
								ManagerPool.summonpetOptManager.die((SummonPet) fighter, attacker);
							}
						}
					}

					//攻击者未死亡
					if (!attacker.isDie()) {
						//触发进攻技能
						if (skill.getSkillModelId() == 25027) { // 断骨草的群攻触发技能,仅仅生效一次
							if (topHorseFlag) {
								ManagerPool.skillManager.triggerSkill(attacker, fighter, skill, true);
								topHorseFlag = false;
							}
						} else {
							ManagerPool.skillManager.triggerSkill(attacker, fighter, skill, true);
							//触发雷技能
							if (model.getQ_skill_elementtype() == 1) {
								if (RandomUtils.random(model.getQ_element_Lightning() + fighter.getRay_def() +7) > fighter.getRay_def()+7) {
									triggerRaySkill(attacker, fighter, 10098 , issingle);
								}
							} 
						}
						
						if(trigger){
							//弓箭被动
							if(attacker instanceof Player){
	 							if(!ManagerPool.buffManager.isHaveLongyuanBuff(fighter)){
									List<Skill> horseWeaponSkills = ManagerPool.horseWeaponManager.getHorseWeaponSkillTriggerByAttack((Player)attacker);
									for (int j = 0; j < horseWeaponSkills.size() - 1; j++) {
										if(ManagerPool.skillManager.triggerSkill(attacker, fighter, horseWeaponSkills.get(j), true)>0){
											break;
										}
									}
								}
							}
							
							//皇权buff
							/*
							if (powerBuff != null) {
								powerBuff.action(attacker, fighter);
							}
	                       */
							if (attackSkills != null) {
								//计算攻击触发Skill
								for (int j = 0; j < attackSkills.size(); j++) {
									ManagerPool.skillManager.triggerSkill(attacker, fighter, attackSkills.get(j), false);
								}
							}
							
							if (attackBuffs != null) {
								//计算攻击触发Buff
								for (int j = 0; j < attackBuffs.size(); j++) {
									ManagerPool.buffManager.triggerBuff(attacker, fighter, attackBuffs.get(j));
								}
							}
						}
					}

					if (!fighter.isDie() && trigger) {
						//睡眠buff
						List<Buff> sleepBuffs = ManagerPool.buffManager.getBuffByType(fighter, BuffType.SLEEP);
						if (sleepBuffs.size() > 0) {
							//移除睡眠buff
							for (int j = 0; j < sleepBuffs.size(); j++) {
								Buff sleepBuff = sleepBuffs.get(j);
								sleepBuff.remove(fighter);
							}
						}

						//防御者可触发Skill
						List<Skill> defenseSkills = null;
						if (fighter instanceof Player) {
							defenseSkills = ManagerPool.skillManager.getSkillTriggerByDefense((Player) fighter);
						} else if (fighter instanceof Pet) {
							defenseSkills = ManagerPool.skillManager.getPetSkillTriggerByDefense((Pet) fighter);
						}

						//防御者可触发Buff
						List<Buff> defenseBuffs = ManagerPool.buffManager.getBuffTriggerByDefense(fighter);

						if (defenseSkills != null) {
							//计算攻击触发Skill
							for (int j = 0; j < defenseSkills.size(); j++) {
								Skill defenseSkill = defenseSkills.get(j);
								//紫芒技能
								if(defenseSkill.getSkillModelId()==Global.ZIMANG_SKILL){
									//暴击
									if(!(attacker instanceof Monster) && (fightResult.fightResult&4)>0 && fightResult.damage>(fighter.getMaxHp()/2)){
										ManagerPool.skillManager.triggerSkill(fighter, attacker, defenseSkills.get(j), false);
									}
								}else{
									ManagerPool.skillManager.triggerSkill(fighter, attacker, defenseSkills.get(j), false);
								}
							}
						}

						if (defenseBuffs != null) {
							//计算攻击触发Buff
							for (int j = 0; j < defenseBuffs.size(); j++) {
								ManagerPool.buffManager.triggerBuff(fighter, attacker, defenseBuffs.get(j));
							}
						}

						if (fighter instanceof Pet) {
							//美人被攻击 进入战斗状态
							Pet pet = (Pet) fighter;
							pet.setLastFightTime(System.currentTimeMillis());
						}else if (fighter instanceof SummonPet) {
							//召唤怪被攻击 进入战斗状态
							SummonPet summonpet = (SummonPet) fighter;
							summonpet.setLastFightTime(System.currentTimeMillis());
						}
					}
					/*panic god 暂时屏蔽弓箭技能,骑兵武器
					try{
						//攻击者未死亡
						if (!attacker.isDie() && (attacker instanceof Player) && trigger) {
							if (bowSkills != null && !triggerBow) {// && (fighter instanceof Player)
								//计算攻击触发Skill
								for (int j = 0; j < bowSkills.size(); j++) {
									boolean triggerResult = triggerBowSkill((Player)attacker, fighter, bowSkills.get(j),issingle);//ManagerPool.skillManager.triggerSkill(attacker, fighter, bowSkills.get(j), false);
									if(triggerResult){
										triggerBow = true;
										triggerNum++;
										if(triggerNum >= maxTriggerNum) break;
									}
								}
							}
							
							if(!triggerHorseWeapon){
								//长虹贯日
								List<Skill> horseWeaponSkills = ManagerPool.horseWeaponManager.getHorseWeaponSkillTriggerByAttack((Player)attacker);
								if(horseWeaponSkills.size() > 0){
									triggerBowSkill((Player)attacker, fighter, horseWeaponSkills.get(horseWeaponSkills.size() - 1),issingle);
									triggerHorseWeapon = true;
								}
							}
						}
					}catch (Exception e) {
						log.error(e, e);
					}
					*///panic god 暂时屏蔽弓箭技能，骑兵武器
//					if(fighter instanceof Player||attacker instanceof Player){
//						//玩家攻击  或者被攻击时 通知到宠物
//						PetManager.getInstance().playerAttack(attacker, fighter);
//					}
					//比较变化 发送消息
					fighterinfo.compareFighter(fighter);
				}

				MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId, attacker, fighter, skill, fightResult.fightResult, fightResult.damage, fightResult.hitDamage, fightResult.backDamage, fightResult.special));
			}
	    }
		//如果是自爆的技能，就把自爆人血量设为0q_skill_type 5 为自爆人
		if(model.getQ_skill_type() == 5 && attacker instanceof Player){
			attacker.setHp(0);
			ManagerPool.playerManager.die((Player) attacker, null);
		}
		//比较变化 发送消息
	    attackerinfo.compareFighter(attacker);
	}

	private boolean triggerHiddenWeaponSkill(long oldFightId, Player player, Fighter defender, int direction) {
		Skill skill = ManagerPool.hiddenWeaponManager.getHiddenWeaponSkillTriggerByAttack(player);
		if (skill == null) {
			return false;
		}

		//获得技能模板
		Q_skill_modelBean model= ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
		if (model == null) return false;
		
		//技能冷却
		if(ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()))){
			return false;
		}
		//技能公共冷却
		if(ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()))){
			return false;
		}

		//开始冷却
		double speed = ((double) player.getAttackSpeed()-100)*10;
		//添加技能冷却
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long) (model.getQ_cd() ));
		//添加技能公共冷却
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()), (long) ((model.getQ_public_cd() - speed)>300?(model.getQ_public_cd() - speed):300));

		long fightId = Config.getId();
		
		// 通知前端显示特效
		ResHiddenWeaponSkillTriggerMessage msg = new ResHiddenWeaponSkillTriggerMessage();
		msg.setPlayerid(player.getId());
		msg.setSkill(skill.getSkillModelId());
		msg.setLevel(skill.getSkillLevel());
		MessageUtil.tell_round_message(player, msg);
		

		// 如果是对自己施放buff,那就不走攻击流程
		if (model.getQ_target() == 1) {
			if (ManagerPool.skillManager.triggerSkill(player, defender, skill, true) == 1) {
				ResAttackResultMessage retMsg = ManagerPool.fightManager.getAttackResultMessage(oldFightId, player, player, skill, 0, 0, 0, 0, 0);
				MessageUtil.tell_round_message(player, retMsg);
			}
			ManagerPool.hiddenWeaponManager.countNextIco(player);
			return true;
		}
		
		if (!ManagerPool.skillManager.canTrigger(player, defender, skill)) {
			ManagerPool.hiddenWeaponManager.countNextIco(player);
			return false;
		}

		MessageUtil.tell_round_message(player, getFightBroadcastMessage(fightId, player, defender, skill.getSkillModelId(), -1));

		//技能飞行时间计算
		//long fly = model.getQ_delay_time();
		long fly = (long) (model.getQ_delay_time() *(model.getQ_public_cd() - speed)/model.getQ_public_cd());

		
		//单体攻击
		if (model.getQ_area_shape() == 1 && defender != null && model.getQ_trajectory_speed() >0) {
			fly += MapUtils.countDistance(player.getPosition(), defender.getPosition()) * 1000 / model.getQ_trajectory_speed();
		}

		if(fly <=50){
			fly = 50;
		}
		if (fly > 0) {
			//延时伤害
			HitTimer timer = new HitTimer(fightId, player, defender, skill, direction, fly, false);
			TimerUtil.addTimerEvent(timer);
		} else {
			//即时伤害
			attack(fightId, player, defender, skill, direction, false);
		}
		
		ManagerPool.hiddenWeaponManager.countNextIco(player);
		return true;
	}

	/**
	 * 伤害(不会触发任何buff)
	 *
	 * @param attacker 攻击者
	 * @param defender 防御者
	 */
	public void damage(Fighter attacker, Fighter defender, int damage, int hate, int skillModelId) {
		//死亡检查
		if (attacker.isDie()) {
			return;
		}
		if (defender != null && defender.isDie()) {
			return;
		}
		if (defender != null) {
			if (attacker.getLine() != defender.getLine() || attacker.getMap() != defender.getMap()) {
				return;
			}
			
			if(!checkCanAttack(attacker, defender, skillModelId)){
				return;
			}
		}
		
		Map map = ManagerPool.mapManager.getMap(attacker.getServerId(), attacker.getLine(), attacker.getMap());
		if (map == null) {
			return;
		}

		//攻击结果0-成功 1-MISS 2-跳闪 4-暴击  8-格挡 6-跳闪+暴击 12-格挡+暴击 16-无敌
		FightResult fightResult = new FightResult();
		fightResult.damage = damage;
		//无敌
		if (FighterState.WUDI.compare(defender.getFightState())) {
			fightResult.fightResult = 16;
			MessageUtil.tell_round_message(defender, getAttackResultMessage(0, attacker, defender, null, fightResult.fightResult, 0, 0, 0, 0));
			return;
		}

		//减少伤害
		int reduceDamage = 0;

		if (defender instanceof Player) {
			//跳跃中闪避
			if(((Player) defender).isJumpProtect()){
				fightResult.fightResult = 2;
			}
			
		} else if (defender instanceof Pet) {
			//跳跃中闪避
			if (((Pet) defender).getJumpState() != PetJumpState.NOMAL) {
				fightResult.fightResult = 2;
			}
		} else if (defender instanceof SummonPet) {
			//跳跃中闪避
			if (((SummonPet) defender).getJumpState() != SummonPetJumpState.NOMAL) {
				fightResult.fightResult = 2;
			}
		}

		//无敌
		if (FighterState.WUDI.compare(defender.getFightState())) {
			fightResult.fightResult = 16;
			MessageUtil.tell_round_message(defender, getAttackResultMessage(0, attacker, defender, null, fightResult.fightResult, 0, 0, 0, 0));
			return;
		}

		//死亡中被打特殊
		if (defender instanceof Monster && MonsterState.DIEING.compare(((Monster) defender).getState())) {
			fightResult.fightResult = 32;
			MessageUtil.tell_round_message(defender, getAttackResultMessage(0, attacker, defender, null, fightResult.fightResult, 0, 0, 0, 0));
			ManagerPool.monsterManager.die((Monster) defender, attacker, -1);
			return;
		}


		//伤害减少
		if (defender instanceof Player) {
			//格挡中 墨子剑法Buff
			if (PlayerState.BLOCK.compare(((Player) defender).getState())) {
				fightResult.fightResult = fightResult.fightResult | 8;
				//减少伤害
				reduceDamage = (int) ((double) fightResult.damage * ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.SHIELD_REDUCTION.getValue()).getQ_int_value() / Global.MAX_PROBABILITY);
				fightResult.damage -= reduceDamage;
			}
		}

		//固定伤害型怪物
		if (defender instanceof Monster) {
			//获得怪物模型
			Q_monsterBean monsterModel = ManagerPool.dataManager.q_monsterContainer.getMap().get(((Monster) defender).getModelId());
			//收到固定伤害
			if (monsterModel.getQ_fixed_hurt() == 1) {
				fightResult.damage = monsterModel.getQ_fiexd_value();
			}
		}
		
		//侍宠固定掉血
		else if(defender instanceof Pet){
			//获得侍宠模型
			Q_petinfoBean petModel = ManagerPool.dataManager.q_petinfoContainer.getMap().get(((Pet) defender).getModelId());
			//收到固定伤害
			fightResult.damage = petModel.getQ_fiexd_value();
		}
		
		if (fightResult.damage < 0) {
			fightResult.damage = 0;
		}
		
		//受到伤害脚本触发
		IHitDamageScript script = (IHitDamageScript) ManagerPool.scriptManager.getScript(ScriptEnum.HIT_DAMAGE);
		if (script != null) {
			try {
				script.onDamage(attacker, defender, fightResult);
			} catch (Exception e) {
				log.error(e, e);
			}
		} else {
			log.error("攻击伤害脚本不存在！");
		}

		//未跳跃闪避
		if ((fightResult.fightResult & 2) == 0) {

			FighterInfo defenderinfo = new FighterInfo();
			defenderinfo.copyFighter(defender);

			defender.setHp(defender.getHp() - fightResult.damage);
			if (defender.getHp() < 0) {
				defender.setHp(0);
			}

			//技能可造成仇恨
			if (fightResult.damage > 0) {
				if (attacker instanceof Player) {
					ManagerPool.petOptManager.onwerAttack((Player) attacker, defender, fightResult.damage);
					ManagerPool.summonpetOptManager.onwerAttack((Player) attacker, defender, fightResult.damage);
				}
				
				if (attacker instanceof Player && (defender instanceof Player || defender instanceof Pet || defender instanceof SummonPet)) {
					//主动Pk移除被杀保护Buff
					if (FighterState.PKBAOHU.compare(attacker.getFightState())) {
						ManagerPool.buffManager.removeByBuffId((Player) attacker, Global.PROTECT_FOR_KILLED);
						if(defender instanceof Player){
							log.error("玩家(" + attacker.getId() + ")PK状态为(" + ((Player)attacker).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)attacker).getState()) + ")敌人列表为(" + MessageUtil.castListToString(((Player)attacker).getEnemys().values()) + ")对玩家(" + defender.getId() + ")PK状态为(" + ((Player)defender).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)defender).getState()) + ")群体攻击导致和平保护buff消失");
						}else{
							log.error("玩家(" + attacker.getId() + ")PK状态为(" + ((Player)attacker).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)attacker).getState()) + ")敌人列表为(" + MessageUtil.castListToString(((Player)attacker).getEnemys().values()) + ")对宠物(" + defender.getId() + ")群体攻击导致和平保护buff消失");
						}
						MessageUtil.notify_player((Player) attacker, Notifys.NORMAL, "您主动发起了对其他玩家的PK，和平保护BUFF消失了");
					}

					//主动Pk移除夜晚保护Buff
					if (FighterState.PKBAOHUFORNIGHT.compare(attacker.getFightState())) {
						ManagerPool.buffManager.removeByBuffId((Player) attacker, Global.PROTECT_IN_NIGHT);
						if(defender instanceof Player){
							log.error("玩家(" + attacker.getId() + ")PK状态为(" + ((Player)attacker).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)attacker).getState()) + ")敌人列表为(" + MessageUtil.castListToString(((Player)attacker).getEnemys().values()) + ")对玩家(" + defender.getId() + ")PK状态为(" + ((Player)defender).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)defender).getState()) + ")群体攻击导致夜晚和平保护buff消失");
						}else{
							log.error("玩家(" + attacker.getId() + ")PK状态为(" + ((Player)attacker).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)attacker).getState()) + ")敌人列表为(" + MessageUtil.castListToString(((Player)attacker).getEnemys().values()) + ")对宠物(" + defender.getId() + ")群体攻击导致夜晚和平保护buff消失");
						}
					}
				}

				if (defender instanceof Monster) {
					Monster monster = (Monster) defender;
					Player owner = null;
					//增加敌对对象
					if (attacker instanceof Player) {
						owner = (Player) attacker;
					} else if (attacker instanceof Pet) {
						owner = ManagerPool.playerManager.getPlayer(((Pet) attacker).getOwnerId());
					}else if (attacker instanceof SummonPet) {
						owner = ManagerPool.playerManager.getPlayer(((SummonPet) attacker).getOwnerId());
					}
					
					if(owner!=null){
						//增加仇恨
						if (hate != 0) {
							if (attacker instanceof SummonPet) {
								// 增加召唤怪的仇恨
								ManagerPool.fightManager.addHatred(monster,
										attacker, hate);
								// 增加仇恨，加一半
								ManagerPool.fightManager.addHatred(monster,
										owner, hate / 2);
							} else {
								// 增加仇恨
								ManagerPool.fightManager.addHatred(monster,
										owner, hate);
							}
							// ManagerPool.fightManager.addHatred(monster,
							// owner, hate);
						}
						//增加伤害统计
						if (monster.getDamages().containsKey(owner.getId())) {
							monster.getDamages().put(owner.getId(), monster.getDamages().get(owner.getId()) + fightResult.damage);
						} else {
							monster.getDamages().put(owner.getId(), fightResult.damage);
						}
	
						//怪物受到伤害脚本
						Q_monsterBean q_monsterBean = ManagerPool.dataManager.q_monsterContainer.getMap().get(monster.getModelId());
						if (q_monsterBean != null) {
							if (q_monsterBean.getQ_script_id() > 0) {
								IMonsterAiScript aiScript = (IMonsterAiScript) ManagerPool.scriptManager.getScript(q_monsterBean.getQ_script_id());
								if (aiScript != null) {
									aiScript.wasHit(monster,attacker,fightResult.damage);
								}
							}
						}
					}
				} else if (defender instanceof Player) {
					//增加敌对对象
					if (attacker instanceof Player) {
						if(!((Player) attacker).getEnemys().containsKey(defender.getId())){
							((Player) defender).addEnemy((Player) attacker);
						}
						((Player) defender).getHits().add(attacker.getId());
					} else if (attacker instanceof Pet) {
						Player aOwner = ManagerPool.playerManager.getPlayer(((Pet) attacker).getOwnerId());
						if(!aOwner.getEnemys().containsKey(defender.getId())){
							((Player) defender).addEnemy(aOwner);
						}
						((Player) defender).getHits().add(aOwner.getId());
					}else if (attacker instanceof SummonPet) {
						Player aOwner = ManagerPool.playerManager.getPlayer(((SummonPet) attacker).getOwnerId());
						if(!aOwner.getEnemys().containsKey(defender.getId())){
							((Player) defender).addEnemy(aOwner);
						}
						((Player) defender).getHits().add(aOwner.getId());
					}
					
					ManagerPool.petOptManager.ownerDefence(attacker, (Player) defender, fightResult.damage);
					//玩家进入战斗状态
					if (!defender.isDie() && !(attacker instanceof Monster)) {
						((Player) defender).setState(PlayerState.FIGHT);
						if (attacker instanceof Player) {
							((Player) attacker).setState(PlayerState.FIGHT);
						}
					}
					
					//人物受到伤害脚本
					IPlayerWasHitScript hscript = (IPlayerWasHitScript) ManagerPool.scriptManager.getScript(ScriptEnum.PLAYERWASHIT);
					if (hscript != null) {
						try{
							hscript.wasHit(attacker, (Player)defender);
						}catch (Exception e) {
							log.error(e, e);
						}
					}
				} else if (defender instanceof Pet) {
					Player owner = ManagerPool.playerManager.getPlayer(((Pet) defender).getOwnerId());
					//增加敌对对象
					if (attacker instanceof Player) {
						if(!((Player) attacker).getEnemys().containsKey(owner.getId())){
							if(!owner.isDie()) 
								owner.addEnemy((Player) attacker);
						}
						((Player) owner).getHits().add(attacker.getId());
					}else if(attacker instanceof SummonPet) {
						Player aowner = ManagerPool.playerManager.getPlayer(((SummonPet) attacker).getOwnerId());
						if(!aowner.getEnemys().containsKey(defender.getId())){
							if(!owner.isDie()) 
								owner.addEnemy((Player) aowner);
						}
						((Player) owner).getHits().add(aowner.getId());
					} 
					ManagerPool.petOptManager.petDefence(attacker, (Pet) defender, fightResult.damage);
					//玩家进入战斗状态
					if (!owner.isDie()) {
						owner.setState(PlayerState.FIGHT);
						if (attacker instanceof Player) {
							((Player) attacker).setState(PlayerState.FIGHT);
						}
					}
					
					//人物受到伤害脚本
					IPetWasHitScript hscript = (IPetWasHitScript) ManagerPool.scriptManager.getScript(ScriptEnum.PETWASHIT);
					if (hscript != null) {
						try{
							hscript.wasHit(attacker, (Pet)defender);
						}catch (Exception e) {
							log.error(e, e);
						}
					}
				}else if (defender instanceof SummonPet) {
					Player owner = ManagerPool.playerManager.getPlayer(((SummonPet) defender).getOwnerId());
					//增加敌对对象
					if (attacker instanceof Player) {
						if(!((Player) attacker).getEnemys().containsKey(owner.getId())){
							if(!owner.isDie()) 
								owner.addEnemy((Player) attacker);
						}
						((Player) owner).getHits().add(attacker.getId());
					}else if (attacker instanceof Pet) {
						Player aOwner = ManagerPool.playerManager.getPlayer(((Pet) attacker).getOwnerId());					
						if(!(aOwner.getEnemys().containsKey(owner.getId()))){
							if(!owner.isDie()) 
								owner.addEnemy(aOwner);
						}
						((Player) owner).getHits().add(aOwner.getId());
					}
		
					ManagerPool.summonpetOptManager.summonpetDefence(attacker, (SummonPet) defender, fightResult.damage);
					//玩家进入战斗状态
					if (!owner.isDie() && !(attacker instanceof Monster)) {
						owner.setState(PlayerState.FIGHT);
						if (attacker instanceof Player) {
							((Player) attacker).setState(PlayerState.FIGHT);
						}
					}
					
					//人物受到伤害脚本
					IPetWasHitScript hscript = (IPetWasHitScript) ManagerPool.scriptManager.getScript(ScriptEnum.PETWASHIT);
					if (hscript != null) {
						try{
							hscript.wasHit(attacker, (Pet)defender);
						}catch (Exception e) {
							log.error(e, e);
						}
					}
				}
				

				if (!defender.isDie()) {
					//睡眠buff
					List<Buff> sleepBuffs = ManagerPool.buffManager.getBuffByType(defender, BuffType.SLEEP);
					if (sleepBuffs.size() > 0) {
						//移除睡眠buff
						for (int j = 0; j < sleepBuffs.size(); j++) {
							Buff sleepBuff = sleepBuffs.get(j);
							sleepBuff.remove(defender);
						}
					}
				}
			}

			//怪物或玩家死亡
			if (defender.getHp() == 0) {
				if (defender instanceof Monster) {
					ManagerPool.monsterManager.die((Monster) defender, attacker, -1);
				} else if (defender instanceof Player) {
					ManagerPool.playerManager.die((Player) defender, attacker);
				} else if (defender instanceof Pet) {
					ManagerPool.petOptManager.die((Pet) defender, attacker);
				}else if (defender instanceof SummonPet) {
					ManagerPool.summonpetOptManager.die((SummonPet) defender, attacker);
				}
			}

			//比较变化 发送消息
			defenderinfo.compareFighter(defender);
		}
		MessageUtil.tell_round_message(defender, getAttackResultMessage(0, attacker, defender, null, fightResult.fightResult, fightResult.damage, 0, 0, 0));

	}
	
	/**
	 * 使用地面魔法
	 */
	public void useGroundMagic(Player player, Skill skill, int mapModelId, int line, Position pos){
		//获得地图
		Map map = ManagerPool.mapManager.getMap(player);
		//地图不存在
		if(map==null){
			return;
		}
		//地图与要释放的地图不一致
		if(map.getMapModelid()!=mapModelId || map.getLineId()!=line){
			return;
		}
		
		Grid[][] mapBlocks = ManagerPool.mapManager.getMapBlocks(mapModelId);
		Grid center = MapUtils.getGrid(pos, mapBlocks);
		//坐标是否超出范围
		if(center==null){
			return;
		}
		
		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getRealLevel(player));
		//技能模版不存在
		if(model==null){
			return;
		}
		
		List<Grid> roundGrid = MapUtils.getRoundGrid(center, model.getQ_circular_radius(), mapBlocks);
		HashSet<Grid> newgrids = new HashSet<Grid>();
		for (Grid grid : roundGrid) {
			newgrids.add(grid);
		}
		
		Iterator<GroundMagic> iterator = map.getMagics().values().iterator();
		while (iterator.hasNext()) {
			GroundMagic groundMagic = (GroundMagic) iterator.next();
			for (Grid grid : groundMagic.getGrids()) {
				if(newgrids.contains(grid)){
					log.error("与其他地面魔法重合");
					return;
				}
			}
		}

		//地面魔法信息
		GroundMagic magic = new GroundMagic();
		magic.setId(Config.getId());
		magic.setSkill(skill);
		magic.setPosition(pos);
		magic.setMap((int)map.getId());
		magic.setMapmodelid(map.getMapModelid());
		magic.setLine(map.getLineId());
		//释放者id
		magic.setSourceId(player.getId());
		//分布格子
		magic.setGrids(roundGrid);
		//开始时间
		magic.setStartTime(System.currentTimeMillis());
		//持续时间
		magic.setLastTime(model.getQ_skill_time());
		
		map.getMagics().put(magic.getId(), magic);
		
		//地图上效果
		Effect effect = new Effect();
		effect.setId(magic.getId());
		effect.setEffectModelId(skill.getSkillModelId());
		effect.setPosition(pos);
		effect.setMap((int)map.getId());
		effect.setMapmodelid(map.getMapModelid());
		effect.setLine(map.getLineId());
		effect.setSourceId(magic.getId());
		effect.setServerId(player.getServerId());
		effect.setType((byte) 1);
		
		ManagerPool.mapManager.enterMap(effect);
		
		magic.setEffect(effect);
	}
	
	/**
	 * 移除地面魔法
	 * @param magic
	 */
	public void removeGroundMagic(GroundMagic magic){
		if(magic.getEffect()!=null){
			ManagerPool.mapManager.quitMap(magic.getEffect());
		}
	}
	/**
	 * 触发雷技能  使目标向攻击者方向移动1-3格
	 * @param player
	 * @param skill
	 * @return
	 */
	private boolean triggerRaySkill(Fighter source, Fighter target,
			int skillid, boolean issingle) {
		if (target instanceof Monster){
			//木桩式怪物
			if(((Monster) target).getAttackType() == 3) 
				return false;
		}
		// // 获取地图
		Map map = ManagerPool.mapManager.getMap((Person) target);
		// 获取阻挡信息
		Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(map
				.getMapModelid());

		int i = 0;
		/**
		 * 切换地点
		 */
		while (true) {
			i++;
			if (i > 100) {
				break;
			}
			int x = target.getPosition().getX();
			int y = target.getPosition().getY();
			if (RandomUtils.random(100) > 50) {
				x = x + RandomUtils.random(25, 75);
			} else {
				x = x - RandomUtils.random(25, 75);
			}

			if (RandomUtils.random(100) > 50) {
				y = y + RandomUtils.random(25, 75);
			} else {
				y = y - RandomUtils.random(25, 75);
			}

			Position p = new Position((short) x, (short) y);
			Grid startGrid = MapUtils.getGrid(p, blocks);
			if (startGrid != null && startGrid.getBlock() != 0) {
				if (target instanceof Player) {
					ManagerPool.mapManager.changeSpecialPosition((Player) target, p, skillid);
				} else {
					ManagerPool.mapManager.changeSpecialPosition((Person) target, p, skillid);
				}
				break;
			}
		}

		return true;
		// MessageUtil.tell_round_message(player,
		// getFightBroadcastMessage(fightId, player, target,
		// skill.getSkillModelId(), -1));
		
//		if (target instanceof Player) {
//			// 获取地图
//			Map map = ManagerPool.mapManager.getMap((Player) target);
//			// 获取阻挡信息
//			Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(map
//					.getMapModelid());
//
//			/**
//			 * 切换地点
//			 */
//			while (true) {
//				int i=0;
//				i++;
//				if(i>100){
//					break;
//				}
//				int x = target.getPosition().getX();
//				int y = target.getPosition().getY();
//				if (RandomUtils.random(100) > 50) {
//					x = x+ RandomUtils.random(25, 75);
//				} else {
//					x = x- RandomUtils.random(25, 75);
//				}
//
//				if (RandomUtils.random(100) > 50) {
//					y= y + RandomUtils.random(25, 75);
//				} else {
//					y = y- RandomUtils.random(25, 75);
//				}
//
//				Position p = new Position((short) x, (short) y);
//				Grid startGrid = MapUtils.getGrid(p, blocks);
//				if (startGrid != null && startGrid.getBlock() != 0) {
//					ManagerPool.mapManager.setPlayerPosition((Player) target, p);
//					// 广播拉向消息
//					ResSetPositionMessage msg = new ResSetPositionMessage();
//					msg.setPersonId(target.getId());
//					msg.setPosition(p);
//					MessageUtil.tell_round_message((Player) target, msg);
//					break;
//				}
//			}
//		} else if (target instanceof Monster) {
//			// 获取地图
//						Map map = ManagerPool.mapManager.getMap((Monster) target);
//						// 获取阻挡信息
//						Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());
//						/**
//						 * 切换地点
//						 */
//						while (true) {
//							int i=0;
//							i++;
//							if(i>100){
//								break;
//							}
//							int x = target.getPosition().getX();
//							int y = target.getPosition().getY();
//							if (RandomUtils.random(100) > 50) {
//								x = x+ RandomUtils.random(25, 75);
//							} else {
//								x = x- RandomUtils.random(25, 75);
//							}
//
//							if (RandomUtils.random(100) > 50) {
//								y= y + RandomUtils.random(25, 75);
//							} else {
//								y = y- RandomUtils.random(25, 75);
//							}
//
//							Position p = new Position((short) x, (short) y);
//							Grid startGrid = MapUtils.getGrid(p, blocks);
//							if (startGrid != null && startGrid.getBlock() != 0) {
//								// 设置移动起点
//								ManagerPool.mapManager.setMonsterPosition((Player)source, (Monster) target, p);
//								// 广播拉向消息
//								ResSetPositionMessage msg = new ResSetPositionMessage();
//								msg.setPersonId(target.getId());
//								msg.setPosition(p);
//								MessageUtil.tell_round_message((Monster) target, msg);
//								break;
//							}
//						}
//			// 停止走路
//			//ManagerPool.mapManager.monsterStopRun((Monster) target);
//		}

	}
	/**
	 * 触发弓箭技能
	 * @param player
	 * @param skill
	 * @return
	 */
	private boolean triggerBowSkill(Player player, Fighter target, Skill skill,boolean issingle){
		return true;
	}
		/*
		if (!ManagerPool.skillManager.canTrigger(player, target, skill)) {
			return false;
		}
		
		//获得技能模板
		Q_skill_modelBean model= ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getRealLevel(player));
		if(model == null) return false;
		
		//单机技能额外+弓箭几率
		int arrowpro = model.getQ_passive_prob();
		if (issingle) {
			arrowpro += player.getArrowProbability();
		}
		//未成功触发技能
		int prop = RandomUtils.random(Global.MAX_PROBABILITY);
		if(prop >= arrowpro){
			return false;
		}
		
		//技能冷却
		if(ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()))){
			return false;
		}
		//技能公共冷却
		if(ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()))){
			return false;
		}
		
		//开始冷却
		double speed = 1 + ((double) player.getAttackSpeed()) / 1000;
		//添加技能冷却
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long) (model.getQ_cd() ));
		//添加技能公共冷却
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()), (long) (model.getQ_public_cd() / speed));

		long fightId = Config.getId();

		MessageUtil.tell_round_message(player, getFightBroadcastMessage(fightId, player, target, skill.getSkillModelId(), -1));

		//技能飞行时间计算
		long fly = model.getQ_delay_time();
		//单体攻击
		if (model.getQ_area_shape() == 1 && target != null && model.getQ_trajectory_speed()>0) {
			fly += MapUtils.countDistance(player.getPosition(), target.getPosition()) * 1000 / model.getQ_trajectory_speed();
		}

		if (fly > 0) {
			//延时伤害
			HitTimer timer = new HitTimer(fightId, player, target, skill, -1, fly, false);
			TimerUtil.addTimerEvent(timer);
		} else {
			//即时伤害
			attack(fightId, player, target, skill, -1, false);
		}
		
		return true;
	}
	
	
	/**
	 * 获取中心点周围一定范围内指定个数敌对目标
	 *
	 * @param map 所在地图
	 * @param attacker 攻击者
	 * @param center 中心点
	 * @param radius 半径
	 * @param type 类型 0-怪物 1-玩家 2-玩家和怪物
	 * @param playerAttackType 玩家类型选择 0-全部 1-非同队 2-非本战盟
	 * @param max 最大数量
	 * @return
	 */
	public List<Fighter> selectFighters(Map map, Fighter attacker, Position center, int radius, int type, int playerAttackType, int max, Grid[][] grids, int skillModelId) {
		return randomSelectFighters(getFighterInCircle(attacker, center, getFighters(center, map, radius, type), radius, playerAttackType, grids, skillModelId), max);
	}

	/**
	 * 随机选择战斗者
	 *
	 * @param fighters 总战斗者
	 * @param max 最大树立那个
	 * @return
	 */
	public List<Fighter> randomSelectFighters(List<Fighter> fighters, int max) {
		if(fighters==null){
			try{
				throw new Exception();
			}catch (Exception e) {
				log.error(e, e);
			}
			log.error("选择目标为null");
			return new ArrayList<Fighter>();
		}
		//总数量
		int num = fighters.size();
		if (num <= max) {
			return fighters;
		} else {
			//目标集合
			List<Fighter> targets = new ArrayList<Fighter>();

			for (int i = 0; i < max; i++) {
				targets.add(fighters.remove(RandomUtils.random(fighters.size())));
			}

			return targets;
		}
	}

	/**
	 * 获取范围内战斗者集合
	 *
	 * @param center 中心点
	 * @param map 地图
	 * @param radius 半径
	 * @param type 类型 0-玩家和怪物 1-玩家 2-怪物
	 * @return
	 */
	private List<Fighter> getFighters(Position center, Map map, int radius, int type) {
		//战斗人员集合
		List<Fighter> fighters = new ArrayList<Fighter>();
		//获得技能半径以内区域
		int[] rounds = ManagerPool.mapManager.getRoundAreas(center, map, radius);
		for (int i = 0; i < rounds.length; i++) {
			Area area = map.getAreas().get(rounds[i]);
			if (area == null) {
				continue;
			}
			//获取战斗者
			if (type == 0) {
				fighters.addAll(area.getPlayers());
				fighters.addAll(area.getPets());
				fighters.addAll(area.getSummonpets());
				fighters.addAll(area.getMonsters().values());
			} else if (type == 1) {
				fighters.addAll(area.getPlayers());
			} else if (type == 2) {
				fighters.addAll(area.getMonsters().values());
			}
		}

		return fighters;
	}

	/*
	 * 机器人选择攻击怪物
	 */
		public List<Fighter> robotgetFighters(Position center, Map map, int radius, int type) {
			//战斗人员集合
			List<Fighter> fighters = new ArrayList<Fighter>();
			//获得技能半径以内区域
			int[] rounds = ManagerPool.mapManager.getRoundAreas(center, map, radius);
			for (int i = 0; i < rounds.length; i++) {
				Area area = map.getAreas().get(rounds[i]);
				if (area == null) {
					continue;
				}
				//获取战斗者
				if (type == 0) {
					fighters.addAll(area.getPlayers());
					fighters.addAll(area.getPets());
					fighters.addAll(area.getSummonpets());
					fighters.addAll(area.getMonsters().values());
				} else if (type == 1) {
					fighters.addAll(area.getPlayers());
				} else if (type == 2) {
					fighters.addAll(area.getMonsters().values());
				}
			}

			return fighters;
		}

	/**
	 * 计算角度
	 *
	 * @param dir 前台方向1
	 * @return 坐标2相对坐标1的角度 0：→， 1：↗， 2：↑， 3：↖， 4：←， 5：↙， 6：↓， 7：↘
	 */
	private int countDirection(int dir) {
		switch (dir) {
			case 0:
				return 2;
			case 1:
				return 1;
			case 2:
				return 0;
			case 3:
				return 7;
			case 4:
				return 6;
			case 5:
				return 5;
			case 6:
				return 4;
			case 7:
				return 3;
			default:
				log.error("skill directiont error:" + dir);
				return -1;
		}
	}

	/**
	 * 增加仇恨值
	 *
	 * @param monster 怪物
	 * @param fighter 攻击者
	 * @param damage 伤害
	 */
	public void addHatred(Monster monster, Fighter fighter, int hate) {
		Hatred hatred = null;

		//log.debug("怪物" + monster.getId() + "增加对" + fighter.getId() + "的仇恨" + hate);
		//查找仇恨列表中是否已有该角色
		List<Hatred> hatreds = monster.getHatreds();
		for (int i = 0; i < hatreds.size(); i++) {
			if (hatreds.get(i).getTarget().getId() == fighter.getId()) {
				hatred = hatreds.remove(i);
				break;
			}
		}

		if (hatred == null) {
			hatred = ManagerPool.hatredManager.getHatred();
			hatred.setTarget(fighter);
			hatred.setFirstAttack(System.currentTimeMillis());
		}
		//增加仇恨值
		hatred.setHatred(hatred.getHatred() + hate);
		hatred.setLastAttack(System.currentTimeMillis());
		//插入仇恨列表（按仇恨值大小排列）
		for (int i = 0; i < hatreds.size(); i++) {
			if (hatreds.get(i).getHatred() < hatred.getHatred()) {
				hatreds.add(i, hatred);
				return;
			}
		}
		hatreds.add(hatred);
	}

//	/**
//	 * 增加仇恨值
//	 * @param monster 怪物
//	 * @param fighter 攻击者
//	 * @param damage 伤害
//	 */
//	public void addHatred(Pet pet, Fighter fighter, int hate){
//		Hatred hatred = null;
//		
//		//log.debug("怪物" + monster.getId() + "增加对" + fighter.getId() + "的仇恨" + hate);
//		//查找仇恨列表中是否已有该角色
//		List<Hatred> hatreds = pet.getHatreds();
//		for (int i = 0; i < hatreds.size(); i++) {
//			if(hatreds.get(i).getTarget().getId() == fighter.getId()){
//				hatred = hatreds.remove(i);
//				break;
//			}
//		}
//		
//		if(hatred == null){
//			hatred = ManagerPool.hatredManager.getHatred();
//			hatred.setTarget(fighter);
//			hatred.setFirstAttack(System.currentTimeMillis());
//		}
//		//增加仇恨值
//		hatred.setHatred(hatred.getHatred() + hate);
//		hatred.setLastAttack(System.currentTimeMillis());
//		//插入仇恨列表（按仇恨值大小排列）
//		for (int i = 0; i < hatreds.size(); i++) {
//			if(hatreds.get(i).getHatred() < hatred.getHatred()){
//				hatreds.add(i, hatred);
//				return;
//			}
//		}
//		
//		hatreds.add(hatred);
//	}
	/**
	 * 广播攻击动作消息
	 *
	 * @param attacker
	 * @param defender
	 * @param attackType
	 * @return
	 */
	public ResFightBroadcastMessage getFightBroadcastMessage(long fightId, Fighter attacker, Fighter defender, int attackType, int direction) {
		ResFightBroadcastMessage msg = new ResFightBroadcastMessage();
		msg.setFightId(fightId);
		msg.setFightDirection(direction);
		msg.setPersonId(attacker.getId());
		msg.setFightType(attackType);
		if (defender != null) {
			msg.setFightTarget(defender.getId());
		}

		return msg;
	}

	/**
	 * 广播攻击动作消息(位置)
	 *
	 * @param attacker
	 * @param defender
	 * @param attackType
	 * @return
	 */
	public ResFightPostionBroadcastMessage getFightPostionBroadcastMessage(long fightId, Fighter attacker, short x, short y, int attackType, int direction) {
		ResFightPostionBroadcastMessage msg = new ResFightPostionBroadcastMessage();
		msg.setFightId(fightId);
		msg.setFightDirection(direction);
		msg.setPersonId(attacker.getId());
		msg.setFightType(attackType);
		msg.setx(x);
		msg.sety(y);
		return msg;
	}
	/**
	 * 广播攻击动作失败消息
	 *
	 * @param attacker
	 * @param defender
	 * @param attackType
	 * @return
	 */
	public ResFightFailedBroadcastMessage getFightFailedBroadcastMessage(long fightId, Fighter attacker, long targetId, int attackType, int direction) {
		ResFightFailedBroadcastMessage msg = new ResFightFailedBroadcastMessage();
		msg.setFightId(fightId);
		msg.setFightDirection(direction);
		msg.setPersonId(attacker.getId());
		msg.setFightType(attackType);
		msg.setFightTarget(targetId);

		return msg;
	}

	/**
	 * 广播特效消息
	 *
	 * @param attacker
	 * @param defender
	 * @param attackType
	 * @return
	 */
	public ResEffectBroadcastMessage getEffectBroadcastMessage(Fighter attacker, Fighter defender, int attackType, int direction) {
		ResEffectBroadcastMessage msg = new ResEffectBroadcastMessage();
		msg.setFightDirection(direction);
		msg.setPersonId(attacker.getId());
		msg.setFightType(attackType);
		if (defender != null) {
			msg.setFightTarget(defender.getId());
		}

		return msg;
	}

	/**
	 * 攻击结果
	 *
	 * @param fighter 战斗者
	 * @param damage 伤害
	 * @return
	 */
	public ResAttackResultMessage getAttackResultMessage(long fightId, Fighter source, Fighter target, Skill skill, int resultType, int damage, int hitDamage, int backDamage, int specialFight) {
		AttackResultInfo result = new AttackResultInfo();
		result.setEntityId(target.getId());
		result.setSourceId(source.getId());
		if (skill != null) {
			result.setSkillId(skill.getSkillModelId());
			if (source instanceof Player) {
				result.setSkillLevel(skill.getRealLevel((Player) source));
			} else {
				result.setSkillLevel(skill.getSkillLevel());
			}
		}
		result.setFightResult(resultType);
		result.setDamage(damage);
		result.setHit(hitDamage);
		result.setBack(backDamage);
		//panic god add
		result.setentityIdhp(target.getHp());
		result.setFightSpecialRes(specialFight);

		ResAttackResultMessage msg = new ResAttackResultMessage();
		msg.setFightId(fightId);
		msg.setState(result);
		
		return msg;
	}

	/**
	 * 线攻击技能选择
	 *
	 * @param attacker 攻击者
	 * @param center 技能中心
	 * @param fighters 战斗者集合
	 * @param direction 方向
	 * @param width 宽度
	 * @param radius 半径
	 * @param type 玩家类型选择 0-全部 1-非同队 2-非本战盟
	 * @return
	 */
	private List<Fighter> getFighterInLine(Fighter attacker, Position center, List<Fighter> fighters, int direction, int width, int radius, int playerAttackType, Grid[][] grids, int skillModelId) {
		//目标集合
		List<Fighter> targets = new ArrayList<Fighter>();

		int half = width / 2;
		double sin45 = Math.sin(Math.PI / 4);
		//选择目标
		Iterator<Fighter> iter = fighters.iterator();
		while (iter.hasNext()) {
			Fighter fighter = (Fighter) iter.next();
			if (fighter.isDie()) {
				continue;
			}
			if (fighter.getId() == attacker.getId()) {
				continue;
			}

			Grid grid = MapUtils.getGrid(fighter.getPosition(), grids);
			if (grid == null) {
				Map map = ManagerPool.mapManager.getMap((Person)fighter);
				if(map!=null){
					if(fighter instanceof Player){
						log.error("player" + " " + fighter.getId() + "(" + ") position " + fighter.getPosition() + " out map " + map.getMapModelid() + "!");
					}else if(fighter instanceof Monster){
						log.error("monster" + " " + fighter.getId() + "(" + ((Monster)fighter).getModelId() + ") position " + fighter.getPosition() + " out map " + map.getMapModelid() + "!");
					}else if(fighter instanceof Pet){
						log.error("pet" + " " + fighter.getId() + "(" + ((Pet)fighter).getModelId() + ") position " + fighter.getPosition() + " out map " + map.getMapModelid() + "!");
					}
				}else{
					if(fighter instanceof Player){
						log.error("player" + " " + fighter.getId() + "(" + ") position " + fighter.getPosition() + " out map !");
					}else if(fighter instanceof Monster){
						log.error("monster" + " " + fighter.getId() + "(" + ((Monster)fighter).getModelId() + ") position " + fighter.getPosition() + " out map !");
					}else if(fighter instanceof Pet){
						log.error("pet" + " " + fighter.getId() + "(" + ((Pet)fighter).getModelId() + ") position " + fighter.getPosition() + " out map !");
					}
				}
				continue;
			}

			if (!checkCanAttack(attacker, fighter, skillModelId)) {
				continue;
			}

			int x = grid.getCenter().getX() - center.getX();
			int y = -(grid.getCenter().getY() - center.getY());
			switch (direction) {
				case 0:
					if (x >= 0 && x <= radius && Math.abs(y) <= half) {
						targets.add(fighter);
					}
					break;
				case 1:
					if (y + x >= 0 && y + x <= radius / sin45 && Math.abs(y - x) <= half / sin45) {
						targets.add(fighter);
					}
					break;
				case 2:
					if (y >= 0 && y <= radius && Math.abs(x) <= half) {
						targets.add(fighter);
					}
					break;
				case 3:
					if (y - x >= 0 && y - x <= radius / sin45 && Math.abs(y + x) <= half / sin45) {
						targets.add(fighter);
					}
					break;
				case 4:
					if (x <= 0 && x >= -radius && Math.abs(y) <= half) {
						targets.add(fighter);
					}
					break;
				case 5:
					if (y + x <= 0 && y + x >= -radius / sin45 && Math.abs(y - x) <= half / sin45) {
						targets.add(fighter);
					}
					break;
				case 6:
					if (y <= 0 && y >= -radius && Math.abs(x) <= half) {
						targets.add(fighter);
					}
					break;
				case 7:
					if (y - x <= 0 && y - x >= -radius / sin45 && Math.abs(y + x) <= half / sin45) {
						targets.add(fighter);
					}
					break;
				default:
					break;
			}
		}

		return targets;
	}

	/**
	 * 圆攻击技能选择
	 *
	 * @param attacker 攻击者
	 * @param center 技能中心
	 * @param fighters 战斗者集合
	 * @param radius 半径
	 * @param type 玩家类型选择 0-全部 1-非同队 2-非本战盟
	 * @return
	 */
	private List<Fighter> getFighterInCircle(Fighter attacker, Position center, List<Fighter> fighters, int radius, int playerAttackType, Grid[][] grids, int skillModelId) {
		//目标集合
		List<Fighter> targets = new ArrayList<Fighter>();

		//选择目标
		Iterator<Fighter> iter = fighters.iterator();
		while (iter.hasNext()) {
			Fighter fighter = (Fighter) iter.next();
			if (fighter.isDie()) {
				continue;
			}
			if (fighter.getId() == attacker.getId()) {
				continue;
			}

			Grid grid = MapUtils.getGrid(fighter.getPosition(), grids);
			if (grid == null) {
				Map map = ManagerPool.mapManager.getMap((Person)fighter);
				if(map!=null){
					if(fighter instanceof Player){
						log.error("player" + " " + fighter.getId() + "(" + ") position " + fighter.getPosition() + " out map " + map.getMapModelid() + "!");
					}else if(fighter instanceof Monster){
						log.error("monster" + " " + fighter.getId() + "(" + ((Monster)fighter).getModelId() + ") position " + fighter.getPosition() + " out map " + map.getMapModelid() + "!");
					}else if(fighter instanceof Pet){
						log.error("pet" + " " + fighter.getId() + "(" + ((Pet)fighter).getModelId() + ") position " + fighter.getPosition() + " out map " + map.getMapModelid() + "!");
					}
				}else{
					if(fighter instanceof Player){
						log.error("player" + " " + fighter.getId() + "(" + ") position " + fighter.getPosition() + " out map !");
					}else if(fighter instanceof Monster){
						log.error("monster" + " " + fighter.getId() + "(" + ((Monster)fighter).getModelId() + ") position " + fighter.getPosition() + " out map !");
					}else if(fighter instanceof Pet){
						log.error("pet" + " " + fighter.getId() + "(" + ((Pet)fighter).getModelId() + ") position " + fighter.getPosition() + " out map !");
					}
				}
				continue;
			}

			if (!checkCanAttack(attacker, fighter, skillModelId)) {
				continue;
			}

			double distance = MapUtils.countDistance(center, grid.getCenter());

//			log.debug("圆形选择" + fighter.getId() + "距离：" + distance);
			//是否在半径内
			if (distance <= radius) {
				targets.add(fighter);
			}
		}

		return targets;
	}

	/**
	 * 扇形攻击技能选择
	 *
	 * @param attacker 攻击者
	 * @param center 技能中心
	 * @param fighters 战斗者集合
	 * @param direction 方向
	 * @param width 宽度
	 * @param radius 半径
	 * @param type 玩家类型选择 0-全部 1-非同队 2-非本战盟
	 * @return
	 */
	private List<Fighter> getFighterInSector(Fighter attacker, Position center, List<Fighter> fighters, int direction, int angle, int radius, int playerAttackType, Grid[][] grids, int skillModelId) {
		//目标集合
		List<Fighter> targets = new ArrayList<Fighter>();
// 7：↖， 6：←， 5：↙， 4：↓， 3：↘， 2：→，1：↗，0：↑
//		log.debug("攻击者" + attacker.getId() + "坐标：" + attacker.getPosition().toString());
		//最小角度
		double minAngle = Math.PI / 4 * direction - ((double) angle) / 180 * Math.PI / 2;
		//最大角度
		double maxAngle = Math.PI / 4 * direction + ((double) angle) / 180 * Math.PI / 2;
		if (maxAngle > Math.PI * 2) {
			maxAngle = maxAngle - Math.PI * 2;
			minAngle = minAngle - Math.PI * 2;
		}
//		log.debug("扇形角度：" + Math.PI / 4 * direction);
//		log.debug("扇形角度最小：" + minAngle);
//		log.debug("扇形角度最大：" + maxAngle);

		//选择目标
		Iterator<Fighter> iter = fighters.iterator();
		while (iter.hasNext()) {
			Fighter fighter = (Fighter) iter.next();
			if (fighter.isDie()) {
				continue;
			}
			if (fighter.getId() == attacker.getId()) {
				continue;
			}

			Grid grid = MapUtils.getGrid(fighter.getPosition(), grids);
			if (grid == null) {
				Map map = ManagerPool.mapManager.getMap((Person)fighter);
				if(map!=null){
					if(fighter instanceof Player){
						log.error("player" + " " + fighter.getId() + "(" + ") position " + fighter.getPosition() + " out map " + map.getMapModelid() + "!");
					}else if(fighter instanceof Monster){
						log.error("monster" + " " + fighter.getId() + "(" + ((Monster)fighter).getModelId() + ") position " + fighter.getPosition() + " out map " + map.getMapModelid() + "!");
					}else if(fighter instanceof Pet){
						log.error("pet" + " " + fighter.getId() + "(" + ((Pet)fighter).getModelId() + ") position " + fighter.getPosition() + " out map " + map.getMapModelid() + "!");
					}
				}else{
					if(fighter instanceof Player){
						log.error("player" + " " + fighter.getId() + "(" + ") position " + fighter.getPosition() + " out map !");
					}else if(fighter instanceof Monster){
						log.error("monster" + " " + fighter.getId() + "(" + ((Monster)fighter).getModelId() + ") position " + fighter.getPosition() + " out map !");
					}else if(fighter instanceof Pet){
						log.error("pet" + " " + fighter.getId() + "(" + ((Pet)fighter).getModelId() + ") position " + fighter.getPosition() + " out map !");
					}
				}
				continue;
			}

			if (!checkCanAttack(attacker, fighter, skillModelId)) {
				continue;
			}

			double distance = MapUtils.countDistance(center, grid.getCenter());

//			log.debug("扇形选择" + fighter.getId() + "距离：" + distance);
//			log.debug("扇形选择" + fighter.getId() + "坐标：" + grid.getCenter().toString());
			if (distance <= radius) {
				if (distance == 0) {
					targets.add(fighter);
					continue;
				}
				int x = grid.getCenter().getX() - center.getX();
				int y = grid.getCenter().getY() - center.getY();

				//战斗者角度
				double fighterAngle = 0;
				if (x >= 0) {
					fighterAngle = Math.asin(-y / distance);
					if (fighterAngle < 0) {
						fighterAngle = fighterAngle + Math.PI * 2;
					}
				} else if (x < 0) {
					fighterAngle = Math.PI - Math.asin(-y / distance);
				}
//				log.debug("扇形选择" + fighter.getId() + "扇形角度：" + fighterAngle);
				if (fighterAngle >= minAngle && fighterAngle <= maxAngle) {
					targets.add(fighter);
				} else if (fighterAngle - Math.PI * 2 >= minAngle && fighterAngle - Math.PI * 2 <= maxAngle) {
					targets.add(fighter);
				}
			}
		}

		return targets;
	}

	/**
	 * 检查是否可以攻击
	 * @param attacker
	 * @param target
	 * @return
	 */
	public boolean checkCanAttack(Fighter attacker, Fighter target, int skillModelId) {
		try{
			if(attacker==null || target==null){
				try{
					throw new Exception();
				}catch (Exception e) {
					log.error(e, e);
				}
				log.error("攻击者" + attacker + "或被攻击者" + target + "为空!");
				return false;
			}
			
			if (attacker instanceof Player) {
				if (!target.canSee((Player) attacker)) {
					return false;
				}
			} else if (attacker instanceof Pet) {
				Player att = ManagerPool.playerManager.getPlayer(((Pet) attacker).getOwnerId());
				if (att != null && !target.canSee(att)) {
					return false;
				}
			}else if (attacker instanceof SummonPet) {
				Player att = ManagerPool.playerManager.getPlayer(((SummonPet) attacker).getOwnerId());
				if (att != null && !target.canSee(att)) {
					return false;
				}
			}
			
			//攻击者和被攻击者同时为怪物时
			if(attacker instanceof Monster && target instanceof Monster){
				return false;
			}
			
			//攻击者和被攻击者同时为玩家时
			//增加判定玩家是否在赤色要塞
			if(attacker instanceof Player && (target instanceof Player || ((Player)attacker).getMapModelId() == CountryManager.SIEGE_MAPID)){
				//有些技能不允许打人,战士：霹雳回旋斩(10007);法师：黑龙波(10016);弓箭手：多重穿透箭(10023)
				if(skillModelId == 10007 || skillModelId == 10016 || skillModelId == 10023) {
					return false;
				}
				
				Player attackerPlayer = (Player) attacker;
				if (target instanceof Player && attackerPlayer.getMapModelId() == CountryManager.SIEGE_MAPID) {
					if(attackerPlayer.getGroupmark() > 0 && attackerPlayer.getGroupmark() == ((Player) target).getGroupmark() ) {
						MessageUtil.notify_player(attackerPlayer, Notifys.SUCCESS, ResManager.getInstance().getString("相同阵营不能攻击"));
						return false;
					}
				}
			}
			
			//攻击者或者被攻击者为怪物时
			if(target instanceof Monster){
				return ((Monster) target).canBeAttack(attacker);
	//			return true;
			}
			
			if (attacker instanceof Monster) {
				return ((Monster) attacker).canAttack(target);
	//			return true;
			}
			
			IAttackCheckScript script = (IAttackCheckScript) ManagerPool.scriptManager.getScript(ScriptEnum.CHECKATTACK);
			if (script != null) {
				try {
					if(!script.check(attacker, target)){
						return false;
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			} else {
				log.error("攻击检查脚本不存在！");
			}
			
			
	
			Player att = null;
			Player tar = null;
	
			if (attacker instanceof Player) {
				att = (Player) attacker;
			} else if (attacker instanceof Pet) {
				att = ManagerPool.playerManager.getPlayer(((Pet) attacker).getOwnerId());
			} else if (attacker instanceof SummonPet) {
				att = ManagerPool.playerManager.getPlayer(((SummonPet) attacker).getOwnerId());
			}
			
			if(target instanceof Player){
				tar = (Player)target;
			}else if(target instanceof Pet){
				if(PetRunState.SWIM == ((Pet)target).getRunState()){
					return false;
				}
				tar = ManagerPool.playerManager.getPlayer(((Pet)target).getOwnerId());
			}else if(target instanceof SummonPet){
				if(SummonPetRunState.SWIM == ((SummonPet)target).getRunState()){
					return false;
				}
				tar = ManagerPool.playerManager.getPlayer(((SummonPet)target).getOwnerId());
			}
	
			if(att==null || tar==null){
				try{
					throw new Exception();
				}catch (Exception e) {
					log.error(e, e);
				}
				log.error("攻击者" + att + "(" + attacker +  ")或被攻击者" + tar + "(" + target + ")为空!");
				return false;
			}
			
			if (att.getId() == tar.getId()) {
				return false;
			}
	
			Map map = ManagerPool.mapManager.getMap(attacker.getServerId(), attacker.getLine(), attacker.getMap());
			if (map == null) {
				return false;
			}
			Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer.getMap().get(map.getMapModelid());
	
			int pkMinLevel = DEFAULT_PK_MIN_LEVEL, pkDiffLevel = DEFAULT_PK_DIFF_LEVEL;
			Q_globalBean pkMinLevelGolbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PK_MIN_LEVEL.getValue());
			if(pkMinLevelGolbalBean != null) pkMinLevel = pkMinLevelGolbalBean.getQ_int_value();
			Q_globalBean pkDiffLevelGolbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PK_DIFF_LEVEL.getValue());
			if(pkDiffLevelGolbalBean != null) pkDiffLevel = pkDiffLevelGolbalBean.getQ_int_value();
			
			if(!FighterState.FORCEPK.compare(tar.getFightState())){
				//Grid grid= MapUtils.getGrid(tar.getPosition(), grids);
				//安全区检查
				if (ManagerPool.mapManager.isSafe(tar.getPosition(), map.getMapModelid())) {
					return false;
				}
		
				//x级差检查
				if (Math.abs(att.getLevel() - tar.getLevel()) > pkDiffLevel && mapBean.getQ_map_pk() == 1) {
					return false;
				}
		
				//pk保护检查
				if (FighterState.PKBAOHU.compare(tar.getFightState())) {
					log.info("玩家(" + tar.getId() + ")处于和平保护！");
					return false;
				}
		
				//挂机pk保护检查
				if (FighterState.PKBAOHUFORNIGHT.compare(tar.getFightState())) {
					return false;
				}
			}else{
				log.error("玩家(" + att.getId() + ")PK状态为(" + att.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(att.getState()) + ")敌人列表为(" + MessageUtil.castListToString(att.getEnemys().values()) + ")对玩家(" + tar.getId() + ")PK状态为(" + tar.getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(tar.getState()) + ")强行攻击因为强行攻击状态");	
			}
			
			// 游泳中
			if (PlayerState.SWIM.compare(tar.getState())) {
				return false;
			}
	
			//x级以下新手保护
			if (tar.getLevel() < pkMinLevel && mapBean.getQ_map_pkprotection() == 1) {
				return false;
			}
	
			//TODO 非本国家人
	

			//pk模式检查
			if (att.getPkState() == 0) {
				//! 非组队情况下可攻击红名玩家或敌对列表中玩家
				if (att.getEnemys().containsKey(tar.getId()) || tar.getPkValue() != 0) {
					//! 但若在同一队伍中不可攻击
					if (att.getTeamid() == tar.getTeamid() && att.getTeamid() != 0){
						return false;
					}
					return true;
				}
				return false;
			} else if (att.getPkState() == 1) {
				//同队伍检查
				if (att.getTeamid() == tar.getTeamid() && att.getTeamid() != 0) {
					Q_skill_modelBean skillmodel = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skillModelId + "_" + 1);
					// 如果是自爆技能 则可以 伤害 友方玩家
					if(skillmodel.getQ_skill_type() != 5){
						return false;
					}
				}
				
				//同战盟检查
				if ((att.getGuildId() == tar.getGuildId() || GuildServerManager.getInstance().isFriendGuild(att.getGuildId(), tar.getGuildId())) && att.getGuildId() != 0) {
					
					Q_skill_modelBean skillmodel = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skillModelId + "_" + 1);
					// 如果是自爆技能 则可以 伤害 友方玩家
					if(skillmodel.getQ_skill_type() != 5){
						return false;
					}
					
				}
			}
			return true;
		}catch (Exception e) {
			log.error(e, e);
		}
		return false;
	}

	/**
	 * 线攻击技能选择
	 *
	 * @param attacker 攻击者
	 * @param center 技能中心
	 * @param direction 方向
	 * @param width 宽度
	 * @param radius 半径
	 * @return
	 */
	protected void showGridInLine(Player attacker, Position center, int direction, int width, int radius, Grid[][] grids) {
		//目标集合
		List<Integer> targets = new ArrayList<Integer>();

		int half = width / 2;
		double sin45 = Math.sin(Math.PI / 4);

		List<Grid> gridlist = MapUtils.getRoundGrid(MapUtils.getGrid(center, grids), radius, grids);
		//选择目标
		Iterator<Grid> iter = gridlist.iterator();
		while (iter.hasNext()) {
			Grid grid = (Grid) iter.next();
			int x = grid.getCenter().getX() - center.getX();
			int y = -(grid.getCenter().getY() - center.getY());
			switch (direction) {
				case 0:
					if (x >= 0 && x <= radius && Math.abs(y) <= half) {
						targets.add(grid.getKey());
					}
					break;
				case 1:
					if (y + x >= 0 && y + x <= radius / sin45 && Math.abs(y - x) <= half / sin45) {
						targets.add(grid.getKey());
					}
					break;
				case 2:
					if (y >= 0 && y <= radius && Math.abs(x) <= half) {
						targets.add(grid.getKey());
					}
					break;
				case 3:
					if (y - x >= 0 && y - x <= radius / sin45 && Math.abs(y + x) <= half / sin45) {
						targets.add(grid.getKey());
					}
					break;
				case 4:
					if (x <= 0 && x >= -radius && Math.abs(y) <= half) {
						targets.add(grid.getKey());
					}
					break;
				case 5:
					if (y + x <= 0 && y + x >= -radius / sin45 && Math.abs(y - x) <= half / sin45) {
						targets.add(grid.getKey());
					}
					break;
				case 6:
					if (y <= 0 && y >= -radius && Math.abs(x) <= half) {
						targets.add(grid.getKey());
					}
					break;
				case 7:
					if (y - x <= 0 && y - x >= -radius / sin45 && Math.abs(y + x) <= half / sin45) {
						targets.add(grid.getKey());
					}
					break;
				default:
					break;
			}
		}

		ResAttackRangeMessage msg = new ResAttackRangeMessage();
		msg.setGrids(targets);
		MessageUtil.tell_player_message(attacker, msg);
	}

	/**
	 * 圆攻击技能选择
	 *
	 * @param attacker 攻击者
	 * @param center 技能中心
	 * @param radius 半径
	 * @return
	 */
	protected void showGridInCircle(Player attacker, Position center, int radius, Grid[][] grids) {
		//目标集合
		List<Integer> targets = new ArrayList<Integer>();

		List<Grid> gridlist = MapUtils.getRoundGrid(MapUtils.getGrid(center, grids), radius, grids);
		//选择目标
		Iterator<Grid> iter = gridlist.iterator();
		while (iter.hasNext()) {
			Grid grid = (Grid) iter.next();
			double distance = MapUtils.countDistance(center, grid.getCenter());

			//是否在半径内
			if (distance <= radius) {
				targets.add(grid.getKey());
			}
		}

		ResAttackRangeMessage msg = new ResAttackRangeMessage();
		msg.setGrids(targets);
		MessageUtil.tell_player_message(attacker, msg);
	}

	/**
	 * 扇形攻击技能选择
	 *
	 * @param attacker 攻击者
	 * @param center 技能中心
	 * @param direction 方向
	 * @param width 宽度
	 * @param radius 半径
	 * @return
	 */
	protected void showGridInSector(Player attacker, Position center, int direction, int angle, int radius, Grid[][] grids) {
		//目标集合
		List<Integer> targets = new ArrayList<Integer>();

		//最小角度
		double minAngle = Math.PI / 4 * direction - ((double) angle) / 180 * Math.PI / 2;
		//最大角度
		double maxAngle = Math.PI / 4 * direction + ((double) angle) / 180 * Math.PI / 2;
		if (maxAngle > Math.PI * 2) {
			maxAngle = maxAngle - Math.PI * 2;
			minAngle = minAngle - Math.PI * 2;
		}

		List<Grid> gridlist = MapUtils.getRoundGrid(MapUtils.getGrid(center, grids), radius, grids);
		//选择目标
		Iterator<Grid> iter = gridlist.iterator();
		while (iter.hasNext()) {
			Grid grid = (Grid) iter.next();
			double distance = MapUtils.countDistance(center, grid.getCenter());

			if (distance <= radius) {
				if (distance == 0) {
					targets.add(grid.getKey());
					continue;
				}

				int x = grid.getCenter().getX() - center.getX();
				int y = grid.getCenter().getY() - center.getY();

				//战斗者角度
				double fighterAngle = 0;
				if (x >= 0) {
					fighterAngle = Math.asin(-y / distance);
					if (fighterAngle < 0) {
						fighterAngle = fighterAngle + Math.PI * 2;
					}
				} else if (x < 0) {
					fighterAngle = Math.PI - Math.asin(-y / distance);
				}
				if (fighterAngle >= minAngle && fighterAngle <= maxAngle) {
					targets.add(grid.getKey());
				} else if (fighterAngle - Math.PI * 2 >= minAngle && fighterAngle - Math.PI * 2 <= maxAngle) {
					targets.add(grid.getKey());
				}
			}
		}
		
		ResAttackRangeMessage msg = new ResAttackRangeMessage();
		msg.setGrids(targets);
		MessageUtil.tell_player_message(attacker, msg);
	}

	private boolean isWudi(Player player) {
		if (GmState.WUDI.compare(player.getGmState())) {
			return true;
		}
		return false;
	}

	/**
	 * 切换攻击锁定目标
	 *
	 * @param player 操作玩家
	 * @param targetID	目标ID
	 * @param targetType 目标类型 1玩家 2怪物 3美人 0 取消锁定
	 */
	public void chanteAttackTarget(Player player, long targetID, int targetType) {
		if (targetType == 0) {
//			Pet showPet = PetInfoManager.getInstance().getShowPet(player);
//			if (showPet != null) {
//				PetOptManager.getInstance().changeAttackTarget(showPet,null, PetOptManager.FIGHTTYPE_PET_IDEL);
//				showPet.changeStateTo(PetState.IDEL);
//				showPet.setFightState(0);
//			}
		}
//		Fighter fighter=null;
//		Map map = MapManager.getInstance().getMap(player);
//		if(map==null){
//			return;
//		}
//		
//		switch (targetType) {
//		case 1:
//			fighter=map.getPlayers().get(targetID);
//			break;
//		case 2:
//			fighter=map.getMonsters().get(targetID);
//			break;
//		case 3:
//			//美人设为主人
//			Pet pet=MapManager.getInstance().getMapPet(map, targetID);
//			if(pet!=null&&!pet.isDie()){
//				fighter= PlayerManager.getInstance().getPlayer(pet.getOwnerId());
//			}
//			break;
//		case 0:
//			break;
//		default:
//			break;
//		}
//		Pet showPet = PetInfoManager.getInstance().getShowPet(player);
//		if (showPet != null) {
//			if (fighter != null && !fighter.isDie()) {
//				// 设置锁定目标
//				showPet.setAttackTarget(fighter);
//			} else {
//				// 清除战斗状态
//				showPet.setAttackTarget(null);
//				showPet.changeStateTo(PetState.IDEL);
//				showPet.setFightState(0);
//			}
//		}
	}
	
	/**
	 * panic add
	 * 玩家范围攻击一个格子
	 *
	 * @param roleId 玩家Id
	 * @param  int mapModelId, int line, Position Dpostion, Position 位置像素
	 * @param skillId 攻击技能
	 * @param direction 攻击方向
	 */
	public void playerAttackPosition(Player player,  int mapModelId, int line, Position Dposition, int skillId, int direction,  List<Long> fightTargets, List<Byte> fightTypes ) {		
		if(/*player.getServerId()!=  ||*/ player.getLine()!= line || player.getMapModelId()!= mapModelId) return;
		
		//停止采集
		ManagerPool.npcManager.playerStopGather(player);

		//冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.PLAYER_ATTACK, null)) {
			log.debug("攻击者（玩家）攻击冷却");
			return;
		}
		
		//冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL, String.valueOf(skillId))) {
			long remain = (long) ManagerPool.cooldownManager.getCooldownTime(player, CooldownTypes.SKILL, String.valueOf(skillId));
			
			ManagerPool.playerManager.playercheck(player, PlayerCheckType.ATTACK_SPEED, remain);
			//技能冷却中
			if (remain > 10 * 1000) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("技能冷却中，请稍后再试，剩余{1}秒"), String.valueOf((int)(remain/1000)));
			}

			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.cooldown, player, -1, skillId, direction));
			return;
		}

		//玩家已经死亡
		if (player.isDie()) {
			log.debug("攻击者（玩家）死亡");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.isDie, player, -1, skillId, direction));
			return;
		}

		//定身或睡眠中
		if (FighterState.DINGSHEN.compare(player.getFightState()) || FighterState.SHUIMIAN.compare(player.getFightState())) {
			log.debug("攻击者（玩家）定身或睡眠");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.DINGSHENSHUIMIAN, player, -1, skillId, direction));
			return;
		}

		//停止格挡
		if (PlayerState.BLOCKPREPARE.compare(player.getState()) || PlayerState.BLOCK.compare(player.getState())) {
			ManagerPool.mapManager.playerStopBlock(player);
		}

		// 游泳中
		if (PlayerState.SWIM.compare(player.getState())) {
			log.debug("攻击者（玩家）游泳中了");
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("游泳区内无法使用技能"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.SWIM, player, -1, skillId, direction));
			return;
		}

		//技能判断
		Skill skill = ManagerPool.skillManager.getSkillByModelId(player, skillId);
		//技能是否创建时学会
		boolean defaultStudy = false;
		if (skill == null) {
			skill = new Skill();
			skill.setSkillModelId(skillId);
			skill.setSkillLevel(1);
			defaultStudy = true;
		}

		Q_skill_modelBean model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getRealLevel(player));
		if (model == null) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.modelnull, player, -1, skillId, direction));
			return;
		}

		if (defaultStudy) {
			//不是默认学会技能
			if (model.getQ_default_study() != 1) {
				log.debug("攻击者（玩家）技能不对");
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultstudyno, player, -1, skillId, direction));
				return;
			}
		}

        //获取玩家身上的变身buff
		List<Buff> shapeChangebuffs = ManagerPool.buffManager.getBuffShapeChange(player);
		//如果没有变身
		if(shapeChangebuffs.size() == 0){
			if(model.getQ_shapechange_buffid() !=0){
				log.debug("攻击者（玩家）变身技能不对");
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultshapechangeno, player, 0, skillId, direction));
				return;			
			 }
		}else{
			//有变身
			boolean defaultShapeChange = false;
			for(Buff per:shapeChangebuffs){
				if(per.getModelId()== model.getQ_shapechange_buffid()){
					defaultShapeChange = true;
					break;
				}
			}
			if(!defaultShapeChange){
				log.debug("攻击者（玩家）变身技能不对");
				MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.defaultshapechangeno, player, 0, skillId, direction));
				return;		
			}
		}
		
		//是否主动技能
		if (model.getQ_trigger_type() != 1 ) {
			log.debug("攻击者（玩家）非主动技能");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.triggertypeno, player, -1, skillId, direction));
			return;
		}

		//如果是召唤怪技能特殊处理
		boolean zhaohuanguai = false;
		if (model.getQ_animal_id() != null && model.getQ_animal_id().length() >= 3) {
			zhaohuanguai = true;
		}
		
		//是否范围技能,并且不是召唤怪
		if (model.getQ_area_shape() == 1 &&  !zhaohuanguai) {
			log.debug("攻击者（玩家）非范围技能");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.areashapesingle, player, -1, skillId, direction));
			return;
		}
		
		//是否人物技能
		if (model.getQ_skill_user() != 1) {
			log.debug("攻击者（玩家）非人物技能");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.skilluserno, player, -1, skillId, direction));
			return;
		}

		//目标不符，无法释放
		if (model.getQ_target() == 1 || model.getQ_target() == 2) {
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("目标不符，无法释放"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.targetno, player, -1, skillId, direction));
			return;
		}

		if (PlayerState.CHANGEMAP.compare(player.getState())) {
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.CHANGEMAP, player, -1, skillId, direction));
			return;
		}

		Map map = ManagerPool.mapManager.getMap(player);
		if (map == null) {
			MessageUtil.tell_player_message(player,getFightFailedBroadcastMessage(ResultFailType.mapno, player, -1, skillId,
							direction));
			return;
		}
		//panic add
		// 地图与要释放的地图不一致
		if (map.getMapModelid() != mapModelId || map.getLineId() != line) {
			return;
		}
		
		Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer.getMap().get(map.getMapModelid());

		Grid[][] mapBlocks = ManagerPool.mapManager.getMapBlocks(mapModelId);
		Grid center = MapUtils.getGrid(Dposition, mapBlocks);
		// 坐标是否超出范围
		if (center == null) {
			return;
		}
		//panic 
		//梅花副本不能使用群攻技能
//		if(map.getMapModelid()==42121 && model.getQ_area_shape()!=1){
//			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("本地图不能使用此技能"));
//			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(0, player, -1, skillId, direction));
//			return;
//		}

		// 距离检查
		Grid playerGrid = MapUtils.getGrid(player.getPosition(),
				ManagerPool.mapManager.getMapBlocks(map.getMapModelid()));
		Grid monsterGrid = MapUtils.getGrid(Dposition,
				ManagerPool.mapManager.getMapBlocks(map.getMapModelid()));
		// 计算格子之间距离，放宽一格
		if (model.getQ_range_limit() >= 0 && MapUtils.countDistance(playerGrid, monsterGrid) > model.getQ_range_limit() + 1 && !zhaohuanguai) {
			// 超出攻击距离
			log.debug("超出攻击距离");
			ManagerPool.mapManager.broadcastPlayerForceStop(player);
			MessageUtil.tell_player_message(player,
					getFightFailedBroadcastMessage(ResultFailType.rangelimit, player, -1, skillId,direction));
			return;
		}

		// 消耗检查
		if (player.getMp() < model.getQ_need_mp()) {
			//魔法值不足
			MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("魔法值不足，无法释放"));
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.Mpno, player, -1, skillId, direction));
			return;
		}

		//公共冷却检查
		if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()))) {
			int remain = (int) ManagerPool.cooldownManager.getCooldownTime(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level())) / 1000;
			//技能公共冷却中
			if (remain > 10) {
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("技能冷却中，请稍后再试，剩余{1}秒"), String.valueOf(remain));
			}
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.SKILLPUBLICNO, player, -1, skillId, direction));
			return;
		}

		//跳跃状态不能攻击
		if (model.getQ_is_Jump_skill() == 0 && (PlayerState.JUMP.compare(player.getState()) || PlayerState.DOUBLEJUMP.compare(player.getState()))) {
			log.debug("跳跃状态不能攻击");
			MessageUtil.tell_player_message(player, getFightFailedBroadcastMessage(ResultFailType.JUMPNO, player, -1, skillId, direction));
			return;
		}

		//攻击时要下马
		if (ManagerPool.horseManager.isRidding(player)){
			ManagerPool.horseManager.unride(player);
		}
		/*panic god 屏蔽
		PlayerDaZuoManager.getInstacne().breakDaZuo(player);
		*/
		//攻击怪物不进入或延迟战斗状态
		//玩家进入攻击状态
		//player.setState(PlayerState.FIGHT);
		//玩家转向
		player.setDirection((byte) direction);
		//魔法值消耗
		player.setMp(player.getMp() - model.getQ_need_mp());
		ManagerPool.playerManager.onMpChange(player);

		//开始冷却
		double speed = ((double) player.getAttackSpeed()-100)*10;
//		System.out.println("攻击时间：" + System.currentTimeMillis());
//		System.out.println("冷却：" + (long)(model.getQ_cd() ));
//		System.out.println("公共冷却：" + (long)(model.getQ_cd() ));
		//添加技能冷却
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL, String.valueOf(model.getQ_skillID()), (long) (model.getQ_cd() ));
		//添加技能公共冷却
		ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.SKILL_PUBLIC, String.valueOf(model.getQ_public_cd_level()), (long) ((model.getQ_public_cd() - speed)>300?(model.getQ_public_cd() - speed):300));
		long fightId = Config.getId();

		MessageUtil.tell_round_message(player, getFightPostionBroadcastMessage(fightId, player, Dposition.getX(),Dposition.getY() ,skillId, direction));

		//如果是召唤怪，特殊处理
		if(zhaohuanguai){	
			// 召唤兽(格式：召唤兽ID_数量_时间（秒）_属性百分比；召唤兽ID_数量_属性百分比_时间（秒）_属性百分比) 
			String q_animals = model.getQ_animal_id();
			//或者其他召唤怪（有存活时间限制的）
			if(model.getQ_skill_type() == 6){
				ManagerPool.monsterManager.createrMonstersBySkill(q_animals, player);
			}else{
				//如果是猎人使用  猎人召唤怪技能（没有存活时间限制的）
				String[] split = q_animals.split(";");
				for (String string : split) {
					if (StringUtils.isBlank(string)) {
						continue;
					}
					String[] split2 = string.split("_");
					int q_animal_id = Integer.parseInt(split2[0]);
					int num = Integer.parseInt(split2[1]);
					int times = Integer.parseInt(split2[2]);
					
					//数量和时间暂时不用
			        SummonPetOptManager.getInstance().showSummonPet(player, q_animal_id);
				}
			}
			return;
		}

		boolean action = false;
		//技能目标选择
//		ISkillScript script = null;
//		if(model.getQ_skill_script() > 0){
//			script = (ISkillScript) ManagerPool.scriptManager.getScript(model.getQ_skill_script());
//		}
//		if(script!=null){
//			try{
//				//技能脚本行为
//				action = script.defaultAction(player, monster);
//			}catch(Exception e){
//				log.error(e, e);
//			}
//		}
		if(!action){
			//技能飞行时间计算
			//long fly = model.getQ_delay_time();
			long fly = (long) (model.getQ_delay_time() *(model.getQ_public_cd() - speed)/model.getQ_public_cd());
			//再加一个判断，不能小于50毫秒
			if(fly <=50){
				fly = 50;
			}

			if (fly > 0) {
				//延时伤害
				HitPostionTimer timer = new HitPostionTimer(fightId, player, mapModelId,  line, Dposition, skill, direction, fly, true, fightTargets, fightTypes);
				TimerUtil.addTimerEvent(timer);
			} else {
				//即时伤害
				attackPostion(fightId, player, mapModelId,  line, Dposition, skill, direction, true, fightTargets, fightTypes);
     		}
		}
	}

	//panic add
	/**
	 * 攻击
	 *
	 * @param attacker 攻击者
	 * @param defender 防御者
	 * @param skill 使用技能
	 * @param direction 方向
	 *  无敌 > 霸体 >技能效果* > 无视一击* > 卓越一击* > 命中判定 > 会心一击* > 伤害结算 
技能触发 无视一击、卓越一击效果时必定命中目标
注释：
技能效果*:指代游戏中技能命中目标后带来的电击、冰冻、毒素、定身、眩晕等附带效果 
无视一击*:触发时无视目标的防御力，请查看主角属性文档
会心一击*:触发时攻击力取值为最大攻击，请查看主角属性文档
卓越一击*:触发时攻击伤害加成值120%，请查看主角属性文档
霸体*(定身、眩晕)
	 */
	/**
	 * @param fightId
	 * @param attacker
	 * @param mapModelId
	 * @param line
	 * @param Dpostion
	 * @param skill
	 * @param direction
	 * @param trigger
	 * @param fightTargets
	 * @param fightTypes
	 */
	public void attackPostion(long fightId, Fighter attacker, int mapModelId, int line, Position Dpostion, Skill skill, int direction, boolean trigger,  List<Long>fightTargets,  List<Byte> fightTypes) {
		//死亡检查
		if (attacker.isDie()) {
			return;
		}
		
		//获得技能模板
		Q_skill_modelBean model = null;
		if (attacker instanceof Player) {
			model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getRealLevel((Player) attacker));
		} else {
			model = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
		}
		
		// 火墙技能 走特殊流程
		if (model != null && model.getQ_skill_type() == 1
				&& attacker instanceof Player && attacker != null) {
			this.useGroundMagic((Player) attacker, skill, attacker.getMap(),
					attacker.getLine(), attacker.getPosition());
			return;
		}
		
		//技能目标选择
		ISkillScript skillscript = null;
		if(model.getQ_skill_script() > 0){
			skillscript = (ISkillScript) ManagerPool.scriptManager.getScript(model.getQ_skill_script());
		}

		Map map = ManagerPool.mapManager.getMap(attacker.getServerId(), attacker.getLine(), attacker.getMap());
		if (map == null) {
			return;
		}
		Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer.getMap().get(map.getMapModelid());

		Grid[][] grids = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());

		//中心点
		Position center = null;
		if (model.getQ_area_target() == 1) {
			//自身
			Grid grid = MapUtils.getGrid(attacker.getPosition(), grids);
			center = grid.getCenter();
		} else {
			//敌人
			Grid grid = MapUtils.getGrid(Dpostion, grids);
			center = grid.getCenter();
		}

		List<Fighter> fighters = null;

		//设置敌人类型 0-全部, 1-玩家和宠物, 2-怪物
		int type = 0;
		//玩家类型选择 0-和平 1-强制 2-全体
		int playerAttackType = 0;
		
		Player role = null;

		if (attacker instanceof Player || attacker instanceof Pet || attacker instanceof SummonPet) {

			if (attacker instanceof Player) {
				role = (Player) attacker;
			}
			if (attacker instanceof Pet) {
				role = PlayerManager.getInstance().getPlayer(((Pet) attacker).getOwnerId());
			}
			//人物pk模式
			playerAttackType = role.getPkState();
			//人物是否站在安全区内
			if (ManagerPool.mapManager.isSafe(role.getPosition(), map.getMapModelid())) {
				playerAttackType = 0;
			}
			int pkMinLevel = DEFAULT_PK_MIN_LEVEL;
			Q_globalBean pkMinLevelGolbalBean = ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.PK_MIN_LEVEL.getValue());
			if(pkMinLevelGolbalBean != null) pkMinLevel = pkMinLevelGolbalBean.getQ_int_value();
			//自己等级检查
			if (role.getLevel() < pkMinLevel && mapBean.getQ_map_pkprotection() == 1) {
				playerAttackType = 0;
			}
		} else if (attacker instanceof Monster) {
			type = 0;
			playerAttackType = 2;
		}

		/*/panic god 屏蔽
		if (attacker instanceof Player && defender != null) {
			ManagerPool.petOptManager.onwerAttack((Player) attacker, defender, 0);
			ManagerPool.summonpetOptManager.onwerAttack((Player) attacker, defender, 0);
		}*/
		
		boolean issingle = false;
		//点
		if (model.getQ_area_shape() == 1) {
			return;
		} //线形
		else if (model.getQ_area_shape() == 2) {
			//计算角度
			direction = countDirection(direction);
			//测试用 显示范围
			if (attacker instanceof Player && syncArea.contains(attacker.getId())) {
				showGridInLine((Player) attacker, center, direction, model.getQ_area_width(), model.getQ_area_length(), grids);
			}
			//获取范围内战斗者
			fighters = getFighterInLine(attacker, center, getFighters(center, map, model.getQ_area_length(), type), direction, model.getQ_area_width(), model.getQ_area_length(), playerAttackType, grids, skill.getSkillModelId());
		} //锥形
		else if (model.getQ_area_shape() == 3) {
			if(attacker instanceof Player){
				int sdfsdf= 0;
			}
			//计算角度
			direction = countDirection(direction);
			//测试用 显示范围
			if (attacker instanceof Player && syncArea.contains(attacker.getId())) {
				showGridInSector((Player) attacker, center, direction, model.getQ_sector_start(), model.getQ_sector_radius(), grids);
			}
			//获取范围内战斗者
			fighters = getFighterInSector(attacker, center, getFighters(center, map, model.getQ_sector_radius(), type), direction, model.getQ_sector_start(), model.getQ_sector_radius(), playerAttackType, grids, skill.getSkillModelId());
			//再套一个小圆圈
			fighters.addAll(getFighterInCircle(attacker, center, getFighters(center, map, 100, type), 100, playerAttackType, grids, skill.getSkillModelId()));

		} //圆形
		else if (model.getQ_area_shape() == 4) {
			//测试用 显示范围
			if (attacker instanceof Player && syncArea.contains(attacker.getId())) {
				showGridInCircle((Player) attacker, center, model.getQ_circular_radius(), grids);
			}
			//获取范围内战斗者
			fighters = getFighterInCircle(attacker, center, getFighters(center, map, model.getQ_circular_radius(), type), model.getQ_circular_radius(), playerAttackType, grids, skill.getSkillModelId());
		}//特殊处理，黑龙波由客户端发目标过来
		else if (model.getQ_area_shape() == 5 && fightTargets != null && fightTypes!=null ) {
			 Fighter fighter = null;
			//目标集合
			 fighters = new ArrayList<Fighter>();
			 int size_len = fightTargets.size();
			 if(size_len != fightTypes.size()){
				 log.error("fightTargets size_len err");
				 return; 
			 }
			 if(size_len > model.getQ_target_max()){
				 size_len = model.getQ_target_max();
			 }
					 
		   for (int j = 0; j < size_len; j++) {
			   Long targetID = fightTargets.get(j);
			   int targetType = fightTypes.get(j);
			   if(targetType == 1){
				   fighter=map.getPlayers().get(targetID);
			   }else if(targetType == 2){
				   fighter=map.getMonsters().get(targetID);
			   }else if(targetType == 5){
				   fighter=map.getPets().get(targetID);
			   }else if(targetType == 4){
				   fighter=map.getSummonpets().get(targetID);
			   }
				//距离检查
				if (fighter != null &&!fighter.isDie() ) {
					//计算格子之间距离，放宽一格
					if (model.getQ_circular_radius() >= 0 && MapUtils.countDistance(attacker.getPosition(), fighter.getPosition()) > model.getQ_circular_radius() + 1) {
						continue;
					}
					
					if (!checkCanAttack(attacker, fighter, skill.getSkillModelId())) {
						continue;
					}
					
					fighters.add(fighter);
				}
			}	  
		}
		
		if(fighters==null || fighters.size()==0){
			//如果是自爆的技能，就把自爆人血量设为0q_skill_type 5 为自爆人
			if(model.getQ_skill_type() == 5 && attacker instanceof Player){
				attacker.setHp(0);
				ManagerPool.playerManager.die((Player) attacker, null);
			}
			return;
		}
		
		//随机选择战斗者
		if (model.getQ_area_shape() != 5){
			fighters = randomSelectFighters(fighters, model.getQ_target_max());
		}
		

//		//攻击前可触发Skill
//		List<Skill> beforeAttackSkills = null;
		//攻击可触发Skill
		List<Skill> attackSkills = null;
		//攻击者可触发Buff
		List<Buff> attackBuffs = null;
		//攻击者可触发弓箭技能Buff
		List<Skill> bowSkills = null;
		boolean triggerBow = false;
		int triggerNum = 0;
		int maxTriggerNum = 0;
		boolean triggerHorseWeapon = false;
		if(trigger){
			if (attacker instanceof Player) {
	//			beforeAttackSkills = ManagerPool.skillManager.getSkillTriggerBeforeAttack((Player)attacker);
				attackSkills = ManagerPool.skillManager.getSkillTriggerByAttack((Player) attacker);

				// 暗器触发
				if (fighters.get(0) != null) {
					triggerHiddenWeaponSkill(fightId, (Player) attacker, fighters.get(0), direction);
				}

			} else if (attacker instanceof Pet) {
				attackSkills = ManagerPool.skillManager.getPetSkillTriggerByAttack((Pet) attacker);
			}
			
			
			if (attacker instanceof Player) {
				bowSkills = ManagerPool.arrowManager.tiggerSkillList((Player) attacker);
				maxTriggerNum = ManagerPool.arrowManager.tiggerSkillNum((Player) attacker);
			}
			
			attackBuffs = ManagerPool.buffManager.getBuffTriggerByAttack(attacker);
		}
		// 无视一击* 现在改为不用计算闪避，一定命中，并且最后伤害*3
		// 卓越一击* 取暴击概率1.2 然后还可以加暴击概率值
		// 会心一击*取伤害上限最大值
		int critMul = Global.MAX_PROBABILITY;
		int isIGNORE_ATTACKPERCENT = 0;
		int isPERFECT_ATTACKPERCENT = 0;
		int isKNOWING_ATTACKPERCENT = 0;

		if (attacker instanceof Player) {//无视一击
			if (RandomUtils.random(Global.MAX_PROBABILITY) < ((Player) attacker)
					.getIgnore_attackPercent()) {
				isIGNORE_ATTACKPERCENT = Global.IGNORE_ATTACKPERCENT;
			}
			// 卓越一击
			if (RandomUtils.random(Global.MAX_PROBABILITY) < ((Player) attacker)
					.getPerfect_attackPercent()) {
				// 暴击倍率
				critMul = (int) ((Global.CRIT_ZHUOYUE + (double) ((Player) attacker)
						.getPerfect_addattackPercent() / Global.MAX_PROBABILITY) * Global.MAX_PROBABILITY);
				isPERFECT_ATTACKPERCENT = Global.PERFECT_ATTACKPERCENT;
			}
			// 会心一击
			if (RandomUtils.random(Global.MAX_PROBABILITY) < ((Player) attacker)
					.getKnowing_attackPercent()) {
				isKNOWING_ATTACKPERCENT = Global.KNOWING_ATTACKPERCENT;
			}
		}
		FighterInfo attackerinfo = new FighterInfo();
		attackerinfo.copyFighter(attacker);
		
		boolean topHorseFlag = true;
		for (int i = 0; i < fighters.size(); i++) {
			Fighter fighter = fighters.get(i);
			Player defencePlayer = null;
			
			if (fighter instanceof Pet) {
				defencePlayer = ManagerPool.playerManager.getPlayer(((Pet) fighter).getOwnerId());
				if(defencePlayer.isDie()) continue;
			}else if (fighter instanceof SummonPet) {
				defencePlayer = ManagerPool.playerManager.getPlayer(((SummonPet) fighter).getOwnerId());
				if(defencePlayer.isDie()) continue;
			}
			
			//! PK值大于10无法攻击
			if (model.getQ_target() == 3 && role != null && role.getPkValue() >= 10 && defencePlayer != null && mapBean.getQ_map_pkpoint() == 1){
				MessageUtil.notify_player(role, Notifys.ERROR, ResManager.getInstance().getString("您的杀孽过重，系统将限制您恶意攻击其他玩家的行为"));
				continue;
			}
			
			//攻击结果0-成功 1-MISS 2-跳闪 4-暴击  8-格挡 6-跳闪+暴击 12-格挡+暴击 16-无敌 32-死亡中被打 64-被秒杀
			FightResult fightResult = new FightResult();
			//无敌
			if (FighterState.WUDI.compare(fighter.getFightState())) {
				fightResult.fightResult = 16;
				MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId, attacker, fighter, skill, fightResult.fightResult, 0, 0, 0, 0));
				continue;
			}
			if(isPERFECT_ATTACKPERCENT == Global.PERFECT_ATTACKPERCENT){
				fightResult.fightResult = fightResult.fightResult | 4;
				fightResult.special |= 1 << 1;
			}
			//技能效果* > 无视一击* > 卓越一击* > 命中判定 > 会心一击* >
//			10009 冰
//			10010 毒
//			10098 雷
			// 技能元素属性（0=冰 1=雷 2=毒 3=无属性（指定概率））
			if (model.getQ_skill_elementtype() == 0) {//1100 冰
				if (RandomUtils.random(model.getQ_element_ice() + fighter.getIce_def() +7 ) > fighter.getIce_def() +7) {
					int result = ManagerPool.buffManager.addBuff(attacker, fighter, Integer.valueOf(model.getQ_passive_buff()),
							1, 0, 0, 0, 0);
					if (result == 0) {
						//MessageUtil.notify_player(fighter, Notifys.ERROR, "使用出错。");
					}
				}
			}else if (model.getQ_skill_elementtype() == 2) {
				if (RandomUtils.random(model.getQ_element_bane() + fighter.getPoison_def() +7) > fighter.getPoison_def() +7) { //1099  毒
					int result = ManagerPool.buffManager.addBuff(attacker,fighter, Integer.valueOf(model.getQ_passive_buff()), 1, 0, 0, 0, 0);
					if (result == 0) {
						// MessageUtil.notify_player(fighter, Notifys.ERROR,
						// "使用出错。");
					}

				}
			} 

			//造成伤害
			if (model.getQ_trigger_figth_hurt() == 1) {
				/*panic god暂时屏蔽
				boolean sidestep = true;
				if(skillscript!=null){
					try{
						//技能是否能被跳闪
						sidestep = skillscript.canJumpSidestep(attacker, fighter);
					}catch(Exception e){
						log.error(e, e);
					}
				}
				if(sidestep){
					if (!ManagerPool.skillManager.isIgnoreJumpMiss(model) && fighter instanceof Player) {
						//TODO 跳跃中闪避
						//if (PlayerState.JUMP.compare(((Player) fighter).getState()) || PlayerState.DOUBLEJUMP.compare(((Player) fighter).getState())) {
						if(((Player) fighter).isJumpProtect()){
							fightResult.fightResult = 2;
						}
						
					} else if (fighter instanceof Pet) {
						//跳跃中闪避
						if (((Pet) fighter).getJumpState() != PetJumpState.NOMAL) {
							fightResult.fightResult = 2;
						}
					}else if (fighter instanceof SummonPet) {
						//跳跃中闪避
						if (((SummonPet) fighter).getJumpState() != SummonPetJumpState.NOMAL) {
							fightResult.fightResult = 2;
						}
					}
				}
				*/
				//对象死亡
				if (fighter.isDie()) {
					continue;
				}
				//死亡中被打特殊
				/*panic god暂时屏蔽
				if (fighter instanceof Monster && MonsterState.DIEING.compare(((Monster) fighter).getState())) {
					fightResult.fightResult = 32;
//					fighter.setReduce(0);
					MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId, attacker, fighter, skill, fightResult.fightResult, 0, 0, 0));
					ManagerPool.monsterManager.die((Monster) fighter, attacker, skill.getSkillModelId());
					continue;
				}
				*/

				//连击秒怪
				if (fighter instanceof Monster && attacker instanceof Player) {
					Monster monster = (Monster) fighter;
					Player player = (Player) attacker;
					if (!monster.isDie() && ManagerPool.batterManager.checkEvencut(player, monster)) {
						fightResult.fightResult = 64;
						addHatred(monster, player, 100);
						int hp = monster.getHp();
						monster.getDamages().put(player.getId(), hp);
						monster.setHp(0);
						MonsterManager.getInstance().die(monster, player, skill.getSkillModelId());
						MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId, attacker, fighter, skill, fightResult.fightResult, hp, 0, 0, 0));
						continue;
					}
				}

				if(!(attacker instanceof Pet || fighter instanceof Pet)){
					//未跳闪
					if (fightResult.fightResult == 0) {
						if(!ManagerPool.skillManager.isIgnoreMiss(model) && FighterState.FORCEDODGE.compare(fighter.getFightState())){
							fightResult.fightResult = 1;
							ManagerPool.buffManager.removeByType(fighter, BuffType.ZIMANGBUFF);
//							fighter.setReduce(0);
							MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId, attacker, fighter, skill, fightResult.fightResult, 0, 0, 0, 0));
							continue;
						}
	//					//怪物死亡中状态不会闪避
	//					if(!(fighter instanceof Monster && MonsterState.DIEING.compare(((Monster)fighter).getState()))){
						//命中计算
						/*panic god 屏蔽
						int dodge = 0;//(int)(Global.MAX_PROBABILITY * ((double)fighter.getDodge() / (fighter.getDodge() + attacker.getDodge())  * (double)fighter.getLevel() / (fighter.getLevel() + attacker.getLevel())));
						if (fighter.getDodge() == 0) {
							dodge = 0;
						} else {
	//						 (int) (10000 * ((double) 被攻击者闪避 / (被攻击者闪避 + 攻击者闪避) * 被攻击者等级/ (被攻击者等级 + 攻击者等级)));
							dodge = (int) (Global.MAX_PROBABILITY * ((double) fighter.getDodge() / (fighter.getDodge() + attacker.getDodge()) * (double) fighter.getLevel() / (fighter.getLevel() + attacker.getLevel())));
						}
						if (fighter instanceof Monster) {
							dodge = (int) ((double) dodge * 0.2);
						}
						log.debug("闪避值" + dodge + "-->攻击者名字" + attacker.getName() + "攻击者等级" + attacker.getLevel() + "攻击者闪避" + attacker.getDodge() + "防御者名字" + fighter.getName() + "防御者等级" + fighter.getLevel() + "防御者闪避" + fighter.getDodge());
						if (dodge < 500) {
							dodge = 500;
						} else if (dodge > 9500) {
							dodge = 9500;
						}*/
						//闪避  被攻击者的闪避 / 攻击者的命中
						if(isIGNORE_ATTACKPERCENT != Global.IGNORE_ATTACKPERCENT){
							int hit = attacker.getHit();//(int)(Global.MAX_PROBABILITY * ((double)fighter.getDodge() / (fighter.getDodge() + attacker.getDodge())  * (double)fighter.getLevel() / (fighter.getLevel() + attacker.getLevel())));
							int dodge = fighter.getDodge();//(int)(Global.MAX_PROBABILITY * ((double)fighter.getDodge() / (fighter.getDodge() + attacker.getDodge())  * (double)fighter.getLevel() / (fighter.getLevel() + attacker.getLevel()))); 
							int succ = (int)( Global.MAX_PROBABILITY* 0.95 *dodge/(hit+dodge));
							if(dodge > 0){
								if((!ManagerPool.skillManager.isIgnoreMiss(model) && RandomUtils.random(Global.MAX_PROBABILITY) < succ) ){
									fightResult.fightResult = 1;
									MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId, attacker, fighter, skill, fightResult.fightResult, 0, 0, 0, 0));
									continue;
								}
							}
						}
						if (fighter instanceof Player) {
							if (isWudi((Player) fighter)) {
								fightResult.fightResult = 1;
								MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId, attacker, fighter, skill, fightResult.fightResult, 0, 0, 0, 0));
								continue;
							}
							//攻击时要下马
							if (!(attacker instanceof Monster) && ManagerPool.horseManager.isRidding((Player) fighter)){
								ManagerPool.horseManager.unride((Player) fighter);
							}
						}
					}
				}


				/* panic god 屏蔽
				//暴击buff
				List<Buff> critBuffs = ManagerPool.buffManager.getBuffByType(attacker, BuffType.MULCRIT);
				if (critBuffs.size() > 0) {
					Buff critBuff = critBuffs.get(0);
					int result = critBuff.action(attacker, attacker);
					if (result == 2) {
						//移除buff
						ManagerPool.buffManager.removeBuff(attacker, critBuff);
					}
					Q_buffBean buffModel = ManagerPool.dataManager.q_buffContainer.getMap().get(critBuff.getModelId());
					critMul = buffModel.getQ_effect_ratio();
					fightResult.fightResult = fightResult.fightResult | 4;
				}

				//皇权buff
				Buff powerBuff = null;
				if ((fightResult.fightResult & 4) == 0) {
					List<Buff> powerBuffs = ManagerPool.buffManager.getBuffByType(attacker, BuffType.BAQUANBUFF);
					if (powerBuffs.size() > 0) {
						powerBuff = powerBuffs.get(0);
						Q_buffBean buffModel = ManagerPool.dataManager.q_buffContainer.getMap().get(powerBuff.getModelId());
						if (RandomUtils.random(Global.MAX_PROBABILITY) < buffModel.getQ_trigger_probability()) {
							//暴击倍率
							critMul = Global.MAX_PROBABILITY * Global.CRIT_MULTIPLE;
							fightResult.fightResult = fightResult.fightResult | 4;
						} else {
							powerBuff = null;
						}
					}
				}
                
				//未暴击过 
				if ((fightResult.fightResult & 4) == 0) {
					//命中计算
					int crit = 0;//(int)(Global.MAX_PROBABILITY * ((double)attacker.getCrit() / (fighter.getCrit() + attacker.getCrit())  * (double)attacker.getLevel() / (fighter.getLevel() + attacker.getLevel())));
					if (fighter.getCrit() == 0) {
						crit = 0;
					} else {
						crit = (int) (Global.MAX_PROBABILITY * ((double) attacker.getCrit() / (fighter.getCrit() + attacker.getCrit()) * (double) attacker.getLevel() / (fighter.getLevel() + attacker.getLevel())));
					}
					if (attacker instanceof Monster) {
						crit = (int) ((double) crit * 0.2);
					}else if(attacker instanceof Player){
						crit = (int) ((double) crit * 0.4);
					}
					log.debug("暴击值" + crit + "-->攻击者名字" + attacker.getName() + "攻击者等级" + attacker.getLevel() + "攻击者暴击" + attacker.getCrit() + "防御者名字" + fighter.getName() + "防御者等级" + fighter.getLevel() + "防御者暴击" + fighter.getCrit());
					if (crit < 500) {
						crit = 500;
					} else if (crit > 5000) {
						crit = 5000;
					}
					if (RandomUtils.random(Global.MAX_PROBABILITY) < crit) {
						//暴击倍率
						critMul = Global.MAX_PROBABILITY * Global.CRIT_MULTIPLE;
						fightResult.fightResult = fightResult.fightResult | 4;
					}
				}
                */
				//计算伤害
				if(attacker instanceof Pet){
					fightResult.damage = attacker.getAttack();
				}else if(attacker instanceof Player){
						// 伤害类型(0=物理 1=魔法)
						if(model.getQ_hurttype() == 0){
							if(isKNOWING_ATTACKPERCENT == Global.KNOWING_ATTACKPERCENT){
								fightResult.damage = ((Player)attacker).getPhysic_attackupper();
								fightResult.damage += fightResult.damage*((Player)attacker).getKnowing_addattackPercent()/Global.MAX_PROBABILITY;
								fightResult.special |= 1;
							}else{
								fightResult.damage = RandomUtils.random(((Player)attacker).getPhysic_attacklower() ,((Player)attacker).getPhysic_attackupper());	
							}
						}else{
							if(isKNOWING_ATTACKPERCENT == Global.KNOWING_ATTACKPERCENT){
								fightResult.damage = ((Player)attacker).getMagic_attackupper();	
								fightResult.damage += fightResult.damage*((Player)attacker).getKnowing_addattackPercent()/Global.MAX_PROBABILITY;
								fightResult.special |= 1;
							}else{
								fightResult.damage = RandomUtils.random(((Player)attacker).getMagic_attacklower() ,((Player)attacker).getMagic_attackupper());
							}
						}
				}else{
					fightResult.damage = attacker.getAttack();
				}
			    
				/*xiaozhuoming: 暂时屏蔽
				//在会心一击判定完攻击力之后，乘一个等级压制系数min(int(max(自己等级-对方等级，0)/50 + 1),3)
				fightResult.damage = fightResult.damage * Math.min((int) (Math.max(attacker.getLevel() - fighter.getLevel(), 0) / 50 + 1), 3);*/
				
				//在会心一击判定完攻击力之后，乘一个等级压制系数min(int(max(怪物等级-人物等级，0)/50+1),3), 当怪物攻击玩家时有效
				if(attacker instanceof Monster && fighter instanceof Player) {
					fightResult.damage = fightResult.damage * Math.min((int) (Math.max(attacker.getLevel() - fighter.getLevel(), 0) / 50 + 1), 3);
				}
				
				if (fightResult.damage < 0) {
					fightResult.damage = 0;
				}
				/*panic god暂时屏蔽反弹伤害
				fightResult.backDamage += model.getQ_ignore_defence();
				if (attacker instanceof Monster ||attacker instanceof SummonPet) {
					//怪物无视防御
					fightResult.backDamage += ((Monster) attacker).getIgnoreDamage();
				} else if (attacker instanceof Player) {
					fightResult.backDamage += ((Player) attacker).getNegDefence();//境界属性 无视防御
					if (fighter instanceof Monster) {
						Q_monsterBean q_monsterBean = ManagerPool.dataManager.q_monsterContainer.getMap().get(((Monster) fighter).getModelId());
						if (q_monsterBean != null) {
							if (q_monsterBean.getQ_monster_type()==3) {
								fightResult.hitDamage = ManagerPool.batterManager.getbossBatter(((Player) attacker));
								fightResult.backDamage += fightResult.hitDamage;
							}else if (q_monsterBean.getQ_monster_type()==1){
								fightResult.backDamage += ((Player) attacker).getEvencutatk();
							}
						}
					}
				}
               */
				//随即攻击比例（最大攻击比例和最小攻击比例之间）
				int percent = Global.MAX_PROBABILITY;
				/*
				int max_damage = 12000;
				int min_damage = 8000 + attacker.getLuck() * 35;
				int percent = RandomUtils.random(min_damage, max_damage);
                */
				int defense = fighter.getDefense();
				
//				//计算被攻击者防御减少
//				int defenseMul = Global.MAX_PROBABILITY;
//				//计算被攻击者防御减少buff
//				List<Buff> redefenseBuffs = ManagerPool.buffManager.getBuffByType(fighter, BuffType.MEIHUABUFF);
//				if ((fightResult & 2) == 0 && redefenseBuffs.size() > 0) {
//					for (int j = 0; j < redefenseBuffs.size(); j++) {
//						Buff redefenseBuff = redefenseBuffs.get(j);
//						redefenseBuff.action(fighter, fighter);
//						defenseMul += redefenseBuff.getParameter();
//					}
//				}
//				if(defenseMul < 0){
//					defenseMul = 0;
//				}
//				defense = (int)((double)defense * defenseMul / Global.MAX_PROBABILITY);
				
				if(!(attacker instanceof Pet)){
					if(RandomUtils.random(Global.MAX_PROBABILITY) < ((Player) attacker).getignore_defendPercent()){
						defense = defense * (1 - ((Player) attacker).getignore_defend_add_Percent() / Global.MAX_PROBABILITY);
					}
				}
				if (ManagerPool.skillManager.isIgnoreDefense(model)) {
					defense = 0;
				}
				
				//! 计算防御
				if(!(attacker instanceof Pet)){
					fightResult.damage = fightResult.damage - defense;
				}
				//计算伤害加成
				//panic god 暂时屏蔽fightResult.damage = (int) (((double) fightResult.damage) * model.getQ_hurt_correct_factor() / Global.MAX_PROBABILITY);
				if (fightResult.damage <= 0) {
					fightResult.damage = 1;
				}
				fightResult.damage = (int) (fightResult.damage * ((double) critMul / Global.MAX_PROBABILITY));
				//计算技能加成伤害
				if (skill != null) {
					Q_skill_modelBean skillModel =null;
					if(attacker instanceof Player){
						skillModel =  ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getRealLevel((Player)attacker));
					}else{
						 skillModel= ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
					}
					//! changed by xuliang 先计算除法，以免int溢出
					fightResult.damage = (int)(fightResult.damage * ((double)(Global.MAX_PROBABILITY + skillModel.getQ_skill_attack())/Global.MAX_PROBABILITY)) + skillModel.getQ_attack_addition();
				}
				//增加伤害比例
				if(attacker instanceof Player){
					fightResult.damage = (int)(fightResult.damage * ((double)((Player) attacker).getAddInjure()/Global.MAX_PROBABILITY));
				}
				//吸收伤害比例
				if(fighter instanceof Player){
					fightResult.damage = fightResult.damage *((Player) fighter).getReduceInjure()/Global.MAX_PROBABILITY;	
				}
				//无视一击结算
				if(attacker instanceof Player  && isIGNORE_ATTACKPERCENT !=0){
					fightResult.damage = fightResult.damage *3;
					fightResult.special |= (1 << 2); 
				}
				
				//backDamage = backDamage * percent / Global.MAX_PROBABILITY * model.getQ_hurt_correct_factor() / Global.MAX_PROBABILITY * critMul / Global.MAX_PROBABILITY;
				//计算被攻击者伤害加深
				int damageMul = Global.MAX_PROBABILITY;
				//计算被攻击者伤害加深buff
				///*panic god 屏蔽
				List<Buff> damageBuffs = ManagerPool.buffManager.getBuffByType(attacker, BuffType.DEEPENDAMAGE);
				if ((fightResult.fightResult & 2) == 0 && damageBuffs.size() > 0) {
					for (int j = 0; j < damageBuffs.size(); j++) {
						Buff damageBuff = damageBuffs.get(j);
						damageBuff.action(fighter, fighter);
						damageMul += damageBuff.getParameter();
					}
				}
				//伤害加深
				fightResult.damage = (int) ((double) fightResult.damage * damageMul / Global.MAX_PROBABILITY);
				//backDamage = backDamage * damageMul / Global.MAX_PROBABILITY ;

				log.debug("attacker " + attacker.getName() + " damage " + fightResult.damage + " backdamage " + fightResult.backDamage);

				if (fightResult.damage < 0) {
					fightResult.damage = 0;
				}

				//伤害减少
				/*panic god 暂时屏蔽
				if (fighter instanceof Player) {
					//格挡中 墨子剑法Buff
					if (PlayerState.BLOCK.compare(((Player) fighter).getState())) {
						fightResult.fightResult = fightResult.fightResult | 8;
						//减少伤害
						int reduceDamage = (int) ((double) fightResult.damage * ManagerPool.dataManager.q_globalContainer.getMap().get(CommonConfig.SHIELD_REDUCTION.getValue()).getQ_int_value() / Global.MAX_PROBABILITY);
						fightResult.damage -= reduceDamage;
						//只反弹前3式的
						if (!FighterState.FANTAN.compare(fighter.getFightState())) {
							fightResult.damage += fightResult.backDamage;
							fightResult.backDamage = 0;
						} else {
							boolean rebound = false;
							List<Buff> buffs = ManagerPool.buffManager.getBuffByType(fighter, BuffType.REBOUND);
							for (int j = 0; j < buffs.size(); j++) {
								Buff buff = buffs.get(j);
								Q_buffBean buffModel = ManagerPool.dataManager.q_buffContainer.getMap().get(buff.getModelId());
								if (buffModel == null) {
									continue;
								}
								String[] rebounds = buffModel.getQ_Bonus_skill().split(Symbol.DOUHAO_REG);
								for (int k = 0; k < rebounds.length; k++) {
									if (skill.getSkillModelId() == Integer.parseInt(rebounds[k])) {
										rebound = true;
										break;
									}
								}
								if (rebound) {
									break;
								}
							}
							if (!rebound) {
								fightResult.damage += fightResult.backDamage;
								fightResult.backDamage = 0;
							}
						}
					} else {
						fightResult.damage += fightResult.backDamage;
						fightResult.backDamage = 0;
					}
				} else {
					fightResult.damage += fightResult.backDamage;
					fightResult.backDamage = 0;
				}
                */
				if(skillscript!=null){
					try{
						//技能附加伤害
						skillscript.damage(attacker, fighter, fightResult);
					}catch(Exception e){
						log.error(e, e);
					}
				}
				
				//固定伤害型怪物
				if (fighter instanceof Monster) {
					//获得怪物模型
					Q_monsterBean monsterModel = ManagerPool.dataManager.q_monsterContainer.getMap().get(((Monster) fighter).getModelId());
					//收到固定伤害
					if (monsterModel.getQ_fixed_hurt() == 1) {
						fightResult.damage = monsterModel.getQ_fiexd_value();
					}
				}

				//侍宠固定掉血
				else if(fighter instanceof Pet){
					//获得侍宠模型
					Q_petinfoBean petModel = ManagerPool.dataManager.q_petinfoContainer.getMap().get(((Pet) fighter).getModelId());
					//收到固定伤害
					fightResult.damage = petModel.getQ_fiexd_value();
				}
				
//				if (fighter.getReduce() > 0) {
//					fightResult.damage = fightResult.damage - fighter.getReduce();
//					fighter.setReduce(0);
//				}

				if (fightResult.damage < 0) {
					fightResult.damage = 0;
				}

				//受到伤害脚本触发
				IHitDamageScript script = (IHitDamageScript) ManagerPool.scriptManager.getScript(ScriptEnum.HIT_DAMAGE);
				if (script != null) {
					try {
						script.onDamage(attacker, fighter, fightResult);
					} catch (Exception e) {
						log.error(e, e);
					}
				} else {
					log.error("攻击伤害脚本不存在！");
				}

				//未跳跃闪避
				if ((fightResult.fightResult & 2) == 0) {

					FighterInfo fighterinfo = new FighterInfo();
					fighterinfo.copyFighter(fighter);
					
					// xuliang add
					fightResult.damage = fightResult.damage <= 0 ? 1 : fightResult.damage;

					fighter.setHp(fighter.getHp() - fightResult.damage);
					//反弹伤害,其他反弹伤害的地方暂时屏蔽
					if(fighter instanceof  Player){
						fightResult.backDamage = fightResult.damage * ((Player)fighter).getRebound_damage()/Global.MAX_PROBABILITY;
					}
					
					if (fighter.getHp() < 0) {
						fighter.setHp(0);
					}

					//技能可造成仇恨
					if (model.getQ_trigger_figth_hurt() > 0 && fightResult.damage > 0) {
						
						if (attacker instanceof Player) {
							ManagerPool.petOptManager.onwerDamage((Player) attacker, fighter, fightResult.damage);
							ManagerPool.summonpetOptManager.onwerDamage((Player) attacker, fighter, fightResult.damage);
							
							//赤色要塞中，攻击玩家或怪物均会移除复活保护的buff
							//! add by xuliang
							if (mapBean.getQ_map_id() == ManagerPool.countryManager.SIEGE_MAPID){
								ManagerPool.buffManager.removeByBuffId((Player) attacker, Global.GCZ_PROTECT_FOR_KILLED);
							}else if(mapBean.getQ_map_id() == CsysManger.getInstance().CSYS_MAPID){
								ManagerPool.buffManager.removeByBuffId((Player) attacker, Global.CSYS_PROTECT_FOR_KILLED);
							}
						}

						if (attacker instanceof Player && (fighter instanceof Player || fighter instanceof Pet || fighter instanceof SummonPet)) {
							//主动Pk移除被杀保护Buff
							if (FighterState.PKBAOHU.compare(attacker.getFightState())) {
								ManagerPool.buffManager.removeByBuffId((Player) attacker, Global.PROTECT_FOR_KILLED);
								if(fighter instanceof Player){
									log.error("玩家(" + attacker.getId() + ")PK状态为(" + ((Player)attacker).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)attacker).getState()) + ")敌人列表为(" + MessageUtil.castListToString(((Player)attacker).getEnemys().values()) + ")对玩家(" + fighter.getId() + ")PK状态为(" + ((Player)fighter).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)fighter).getState()) + ")群体攻击导致和平保护buff消失");
								}else{
									log.error("玩家(" + attacker.getId() + ")PK状态为(" + ((Player)attacker).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)attacker).getState()) + ")敌人列表为(" + MessageUtil.castListToString(((Player)attacker).getEnemys().values()) + ")对宠物(" + fighter.getId() + ")群体攻击导致和平保护buff消失");
								}
								MessageUtil.notify_player((Player) attacker, Notifys.NORMAL, "您主动发起了对其他玩家的PK，和平保护BUFF消失了");
							}

							//主动Pk移除夜晚保护Buff
							if (FighterState.PKBAOHUFORNIGHT.compare(attacker.getFightState())) {
								ManagerPool.buffManager.removeByBuffId((Player) attacker, Global.PROTECT_IN_NIGHT);
								if(fighter instanceof Player){
									log.error("玩家(" + attacker.getId() + ")PK状态为(" + ((Player)attacker).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)attacker).getState()) + ")敌人列表为(" + MessageUtil.castListToString(((Player)attacker).getEnemys().values()) + ")对玩家(" + fighter.getId() + ")PK状态为(" + ((Player)fighter).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)fighter).getState()) + ")群体攻击导致夜晚和平保护buff消失");
								}else{
									log.error("玩家(" + attacker.getId() + ")PK状态为(" + ((Player)attacker).getPkState() + ")挂机状态为(" + PlayerState.AUTOFIGHT.compare(((Player)attacker).getState()) + ")敌人列表为(" + MessageUtil.castListToString(((Player)attacker).getEnemys().values()) + ")对宠物(" + fighter.getId() + ")群体攻击导致夜晚和平保护buff消失");
								}
							}
						}

						if (fighter instanceof Monster) {
							Monster monster = (Monster) fighter;

							Player owner = null;
							//增加敌对对象
							if (attacker instanceof Player) {
								owner = (Player) attacker;
								/* xuliang 弃用Boss连击
								//增加连击
								if (ManagerPool.monsterManager.isBoss(((Monster) fighter).getModelId())) {
									ManagerPool.batterManager.bossBatter(owner);
								}
								*/
							} else if (attacker instanceof Pet) {
								owner = ManagerPool.playerManager.getPlayer(((Pet) attacker).getOwnerId());
							}else if (attacker instanceof SummonPet) {
								owner = ManagerPool.playerManager.getPlayer(((SummonPet) attacker).getOwnerId());
							}
							
							if(owner!=null){
								 if (attacker instanceof SummonPet){
										//增加召唤怪的仇恨
										addHatred(monster, attacker, model.getQ_enmity());
										//增加仇恨，加一半
										addHatred(monster, owner, model.getQ_enmity()/2);
									 }else{
										//增加仇恨
										addHatred(monster, owner, model.getQ_enmity());
									 }
								//增加伤害统计
								if (monster.getDamages().containsKey(owner.getId())) {
									monster.getDamages().put(owner.getId(), monster.getDamages().get(owner.getId()) + fightResult.damage);
								} else {
									monster.getDamages().put(owner.getId(), fightResult.damage);
								}
	
								//怪物受到伤害脚本
								Q_monsterBean q_monsterBean = ManagerPool.dataManager.q_monsterContainer.getMap().get(monster.getModelId());
								if (q_monsterBean != null) {
									if (q_monsterBean.getQ_script_id() > 0) {
										IMonsterAiScript aiScript = (IMonsterAiScript) ManagerPool.scriptManager.getScript(q_monsterBean.getQ_script_id());
										if (aiScript != null) {
											aiScript.wasHit(monster,attacker,fightResult.damage);
										}
									}
								}
							}
						} else if (fighter instanceof Player) {
							checkEnemy(attacker, (Player)fighter, mapBean);
							
							ManagerPool.petOptManager.ownerDefence(attacker, (Player) fighter, fightResult.damage);
							ManagerPool.summonpetOptManager.ownerDefence(attacker, (Player) fighter, fightResult.damage);
							//玩家进入战斗状态
							if (!fighter.isDie() && !(attacker instanceof Monster)) {
								((Player) fighter).setState(PlayerState.FIGHT);
							}
							
							//人物受到伤害脚本
							IPlayerWasHitScript hscript = (IPlayerWasHitScript) ManagerPool.scriptManager.getScript(ScriptEnum.PLAYERWASHIT);
							if (hscript != null) {
								try{
									hscript.wasHit(attacker, (Player) fighter);
								}catch (Exception e) {
									log.error(e, e);
								}
							}
						} else if (fighter instanceof Pet) {
							Player owner = ManagerPool.playerManager.getPlayer(((Pet) fighter).getOwnerId());
							
							checkEnemy(attacker, owner, mapBean);
							//增加敌对对象
							/* xuliang 移除无用代码
							if (attacker instanceof Player) {
								if(!((Player) attacker).getEnemys().containsKey(owner.getId())){
									if(!owner.isDie()) owner.addEnemy((Player) attacker);
								}
								((Player) owner).getHits().add(attacker.getId());
							}else if (attacker instanceof SummonPet) {
								Player aOwner = ManagerPool.playerManager.getPlayer(((SummonPet) attacker).getOwnerId());								
								if(!aOwner.getEnemys().containsKey(owner.getId())){
									if(!owner.isDie()) owner.addEnemy((Player) attacker);
								}
								((Player) owner).getHits().add(aOwner.getId());

							}
							*/
							ManagerPool.petOptManager.petDefence(attacker, (Pet) fighter, fightResult.damage);
							//ManagerPool.summonpetOptManager.summonpetDefence(attacker, (SummonPet) fighter, fightResult.damage);
							//玩家进入战斗状态
							if (!owner.isDie()&& !(attacker instanceof Monster)) {
								owner.setState(PlayerState.FIGHT);
							}
							
							//宠物受到伤害脚本
							IPetWasHitScript hscript = (IPetWasHitScript) ManagerPool.scriptManager.getScript(ScriptEnum.PETWASHIT);
							if (hscript != null) {
								try{
									hscript.wasHit(attacker, (Pet) fighter);
								}catch (Exception e) {
									log.error(e, e);
								}
							}
						}else if (fighter instanceof SummonPet) {
							Player owner = ManagerPool.playerManager.getPlayer(((SummonPet) fighter).getOwnerId());
							
							checkEnemy(attacker, owner, mapBean);
							//增加敌对对象
							/* xuliang 移除无用代码
							if (attacker instanceof Player) {
								if(!((Player) attacker).getEnemys().containsKey(owner.getId())){
									if(!owner.isDie()) owner.addEnemy((Player) attacker);
								}
								((Player) owner).getHits().add(attacker.getId());
							} else if (attacker instanceof Pet) {
								Player aOwner = ManagerPool.playerManager.getPlayer(((Pet) attacker).getOwnerId());								
								if(!aOwner.getEnemys().containsKey(owner.getId())){
									if(!owner.isDie()) owner.addEnemy(aOwner);
								}
								((Player) owner).getHits().add(aOwner.getId());

							}
							*/
//							else if (attacker instanceof Pet) {
//								Player aOwner = ManagerPool.playerManager.getPlayer(((Pet) attacker).getOwnerId());
//								owner.addEnemy(aOwner);
//							}
							//ManagerPool.petOptManager.petDefence(attacker, (Pet) fighter, fightResult.damage);
							ManagerPool.summonpetOptManager.summonpetDefence(attacker, (SummonPet) fighter, fightResult.damage);
							//玩家进入战斗状态
							if (!owner.isDie()&& !(attacker instanceof Monster)) {
								owner.setState(PlayerState.FIGHT);
							}
							
							//受到伤害脚本
							/*
							IPetWasHitScript hscript = (IPetWasHitScript) ManagerPool.scriptManager.getScript(ScriptEnum.PETWASHIT);
							if (hscript != null) {
								try{
									hscript.wasHit(attacker, (Pet) fighter);
								}catch (Exception e) {
									log.error(e, e);
								}
							}
							*/
						}
					}

					//怪物或玩家死亡
					if (fighter.getHp() == 0) {
						if (fighter instanceof Monster) {
							ManagerPool.monsterManager.die((Monster) fighter, attacker, skill.getSkillModelId());
						} else if (fighter instanceof Player) {
							ManagerPool.playerManager.die((Player) fighter, attacker);
						} else if (fighter instanceof Pet) {
							ManagerPool.petOptManager.die((Pet) fighter, attacker);
						}else if (fighter instanceof SummonPet) {
							ManagerPool.summonpetOptManager.die((SummonPet) fighter, attacker);
						}
					}

					//反弹伤害
					if (fightResult.backDamage > 0 && !attacker.isDie()) {
						attacker.setHp(attacker.getHp() - fightResult.backDamage);
						if (attacker.getHp() < 0) {
							attacker.setHp(0);
							fightResult.special = 1;
			                MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId,  fighter, attacker, skill, 0, 0, 0, fightResult.backDamage, fightResult.special));

						}

						if (attacker instanceof Monster) {
							Monster monster = (Monster) attacker;
							//增加伤害统计
							if (monster.getDamages().containsKey(fighter.getId())) {
								monster.getDamages().put(fighter.getId(), monster.getDamages().get(fighter.getId()) + fightResult.backDamage);
							} else {
								monster.getDamages().put(fighter.getId(), fightResult.backDamage);
							}
						}

						//怪物或玩家死亡
						if (attacker.getHp() == 0) {
							if (attacker instanceof Monster) {
								ManagerPool.monsterManager.die((Monster) attacker, fighter, skill.getSkillModelId());
							} else if (attacker instanceof Player) {
								ManagerPool.playerManager.die((Player) attacker, fighter);
							} else if (attacker instanceof Pet) {
								ManagerPool.petOptManager.die((Pet) attacker, fighter);
							}else if (fighter instanceof SummonPet) {
								ManagerPool.summonpetOptManager.die((SummonPet) fighter, attacker);
							}
						}
					}

					//攻击者未死亡
					if (!attacker.isDie()) {
						//触发进攻技能
						if (skill.getSkillModelId() == 25027) { // 断骨草的群攻触发技能,仅仅生效一次
							if (topHorseFlag) {
								ManagerPool.skillManager.triggerSkill(attacker, fighter, skill, true);
								topHorseFlag = false;
							}
						} else {
							ManagerPool.skillManager.triggerSkill(attacker, fighter, skill, true);
						}
						
						if(trigger){
							//弓箭被动
							if(attacker instanceof Player){
	 							if(!ManagerPool.buffManager.isHaveLongyuanBuff(fighter)){
									List<Skill> horseWeaponSkills = ManagerPool.horseWeaponManager.getHorseWeaponSkillTriggerByAttack((Player)attacker);
									for (int j = 0; j < horseWeaponSkills.size() - 1; j++) {
										if(ManagerPool.skillManager.triggerSkill(attacker, fighter, horseWeaponSkills.get(j), true)>0){
											break;
										}
									}
								}
							}
							
							//皇权buff
							/*
							if (powerBuff != null) {
								powerBuff.action(attacker, fighter);
							}
	                       */
							if (attackSkills != null) {
								//计算攻击触发Skill
								for (int j = 0; j < attackSkills.size(); j++) {
									ManagerPool.skillManager.triggerSkill(attacker, fighter, attackSkills.get(j), false);
								}
							}
							
							if (attackBuffs != null) {
								//计算攻击触发Buff
								for (int j = 0; j < attackBuffs.size(); j++) {
									ManagerPool.buffManager.triggerBuff(attacker, fighter, attackBuffs.get(j));
								}
							}
						}
					}

					if (!fighter.isDie() && trigger) {
						//睡眠buff
						List<Buff> sleepBuffs = ManagerPool.buffManager.getBuffByType(fighter, BuffType.SLEEP);
						if (sleepBuffs.size() > 0) {
							//移除睡眠buff
							for (int j = 0; j < sleepBuffs.size(); j++) {
								Buff sleepBuff = sleepBuffs.get(j);
								sleepBuff.remove(fighter);
							}
						}

						//防御者可触发Skill
						List<Skill> defenseSkills = null;
						if (fighter instanceof Player) {
							defenseSkills = ManagerPool.skillManager.getSkillTriggerByDefense((Player) fighter);
						} else if (fighter instanceof Pet) {
							defenseSkills = ManagerPool.skillManager.getPetSkillTriggerByDefense((Pet) fighter);
						}

						//防御者可触发Buff
						List<Buff> defenseBuffs = ManagerPool.buffManager.getBuffTriggerByDefense(fighter);

						if (defenseSkills != null) {
							//计算攻击触发Skill
							for (int j = 0; j < defenseSkills.size(); j++) {
								Skill defenseSkill = defenseSkills.get(j);
								//紫芒技能
								if(defenseSkill.getSkillModelId()==Global.ZIMANG_SKILL){
									//暴击
									if(!(attacker instanceof Monster) && (fightResult.fightResult&4)>0 && fightResult.damage>(fighter.getMaxHp()/2)){
										ManagerPool.skillManager.triggerSkill(fighter, attacker, defenseSkills.get(j), false);
									}
								}else{
									ManagerPool.skillManager.triggerSkill(fighter, attacker, defenseSkills.get(j), false);
								}
							}
						}

						if (defenseBuffs != null) {
							//计算攻击触发Buff
							for (int j = 0; j < defenseBuffs.size(); j++) {
								ManagerPool.buffManager.triggerBuff(fighter, attacker, defenseBuffs.get(j));
							}
						}

						if (fighter instanceof Pet) {
							//美人被攻击 进入战斗状态
							Pet pet = (Pet) fighter;
							pet.setLastFightTime(System.currentTimeMillis());
						}else if (fighter instanceof SummonPet) {
							//召唤怪被攻击 进入战斗状态
							SummonPet summonpet = (SummonPet) fighter;
							summonpet.setLastFightTime(System.currentTimeMillis());
						}
					}
					/*panic god 暂时屏蔽弓箭技能,骑兵武器
					try{
						//攻击者未死亡
						if (!attacker.isDie() && (attacker instanceof Player) && trigger) {
							if (bowSkills != null && !triggerBow) {// && (fighter instanceof Player)
								//计算攻击触发Skill
								for (int j = 0; j < bowSkills.size(); j++) {
									boolean triggerResult = triggerBowSkill((Player)attacker, fighter, bowSkills.get(j),issingle);//ManagerPool.skillManager.triggerSkill(attacker, fighter, bowSkills.get(j), false);
									if(triggerResult){
										triggerBow = true;
										triggerNum++;
										if(triggerNum >= maxTriggerNum) break;
									}
								}
							}
							
							if(!triggerHorseWeapon){
								//长虹贯日
								List<Skill> horseWeaponSkills = ManagerPool.horseWeaponManager.getHorseWeaponSkillTriggerByAttack((Player)attacker);
								if(horseWeaponSkills.size() > 0){
									triggerBowSkill((Player)attacker, fighter, horseWeaponSkills.get(horseWeaponSkills.size() - 1),issingle);
									triggerHorseWeapon = true;
								}
							}
						}
					}catch (Exception e) {
						log.error(e, e);
					}
					*///panic god 暂时屏蔽弓箭技能，骑兵武器
//					if(fighter instanceof Player||attacker instanceof Player){
//						//玩家攻击  或者被攻击时 通知到宠物
//						PetManager.getInstance().playerAttack(attacker, fighter);
//					}
					//比较变化 发送消息
					fighterinfo.compareFighter(fighter);
				}

				MessageUtil.tell_round_message(fighter, getAttackResultMessage(fightId, attacker, fighter, skill, fightResult.fightResult, fightResult.damage, fightResult.hitDamage, fightResult.backDamage, fightResult.special));
			}
		}
		//比较变化 发送消息
		attackerinfo.compareFighter(attacker);
		
		if(model.getQ_skill_type() == 5 && attacker instanceof Player){
			attacker.setHp(0);
			ManagerPool.playerManager.die((Player) attacker, null);
		}
	}
	
	private void checkEnemy(Fighter fighter, Player defender, Q_mapBean mapBean){
		if (mapBean.getQ_map_pkpoint() == 1){
			Player attacker = null;
			if (fighter instanceof Player){
				attacker = (Player)fighter;
			}else if (fighter instanceof Pet){
				attacker = PlayerManager.getInstance().getPlayer(((Pet) fighter).getOwnerId());
			}else if (fighter instanceof SummonPet){
				attacker = PlayerManager.getInstance().getPlayer(((SummonPet) fighter).getOwnerId());
			}else{
				return;
			}
			
			if (defender.getPkValue() == 0 && attacker != null){
				
				if(!attacker.getEnemys().containsKey(defender.getId()) && !defender.getEnemys().containsKey(attacker.getId())){
					defender.addEnemy(attacker);
					
					ResChangePlayerEnemiesToClientMessage msg = new ResChangePlayerEnemiesToClientMessage();
					
					msg.getEnemys().addAll(defender.getEnemys().keySet());
					MessageUtil.tell_player_message(defender, msg);
					
					MessageUtil.notify_player(defender, Notifys.ERROR, ResManager.getInstance().getString("您正遭到{1}的恶意攻击,30分钟内您可以正当反击"), attacker.getName());
				}
				defender.getHits().add(attacker.getId());
			}
		}
	}
}
