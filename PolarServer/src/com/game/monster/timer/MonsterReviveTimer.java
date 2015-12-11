package com.game.monster.timer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.game.challenge.manager.ChallengeManager;
import com.game.config.Config;
import com.game.data.bean.Q_clone_activityBean;
import com.game.data.bean.Q_monsterBean;
import com.game.data.manager.DataManager;
import com.game.languageres.manager.ResManager;
import com.game.liveness.manager.LivenessManager;
import com.game.manager.ManagerPool;
import com.game.map.message.ResRoundMonsterDisappearMessage;
import com.game.map.structs.Area;
import com.game.map.structs.Map;
import com.game.monster.message.ReqMonsterSyncMessage;
import com.game.monster.message.ResMonsterReviveMessage;
import com.game.monster.structs.Monster;
import com.game.monster.structs.MonsterState;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.skill.structs.Skill;
import com.game.structs.Grid;
import com.game.task.struts.Task;
import com.game.task.struts.TaskEnum;
import com.game.timer.TimerEvent;
import com.game.utils.MessageUtil;
import com.game.utils.Symbol;
import com.game.utils.TimeUtil;
import com.game.zones.manager.ZonesFlopManager;
import com.game.zones.manager.ZonesManager;
import com.game.zones.message.ResZoneLifeTimeMessage;
import com.game.zones.structs.ZoneContext;

public class MonsterReviveTimer extends TimerEvent {

	protected Logger log = Logger.getLogger(MonsterReviveTimer.class);

	private int serverId;

	private int lineId;

	private int mapId;

	// BOSS集合
	private static List<Integer> bosses = Arrays.asList(91, 151, 211, 261, 341,
			391, 441, 541, 581, 631, 681, 682, 683, 684, 2010, 2011, 2012,
			2013, 2014, 2015, 2016, 2017, 2018, 2301, 4091, 4151, 4211, 4261,
			4341, 4391, 4441, 4541, 4581, 4631, 4681);

	public MonsterReviveTimer(int serverId, int lineId, int mapId) {
		super(-1, 1000);
		this.serverId = serverId;
		this.lineId = lineId;
		this.mapId = mapId;
	}

	@Override
	public void action() {

		// 按地图，区域遍历怪物列表
		Map map = ManagerPool.mapManager.getMap(serverId, lineId, mapId);

		Grid[][] grids = ManagerPool.mapManager.getMapBlocks(map
				.getMapModelid());

		
		// 血色城堡  特殊处理
		if (map.getMapModelid() >= 400001 && map.getMapModelid() <= 400008) {
			long startTime = System.currentTimeMillis() - map.getCreate();
			ZoneContext zone = ManagerPool.zonesManager.getContexts().get(
					map.getZoneId());
			Q_clone_activityBean zoneBean = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(map.getZoneModelId());
			// 血色城堡  到时间  将玩家提出
			if ((startTime > (zoneBean.getQ_exist_time()-(2*60+50)* 1000) ) && (Integer) zone.getOthers().get("zoneprocess")!=-1) {
				for (Entry<Long, Player> entry : map.getPlayers().entrySet()) {
					ZonesManager.getInstance().outZone(entry.getValue());
				}
			}
		}
		
		// 恶魔广场 特殊处理
		if (map.getMapModelid() >= 300001 && map.getMapModelid() <= 300007) {
			ZoneContext zone = ManagerPool.zonesManager.getContexts().get(
					map.getZoneId());
			long startTime = System.currentTimeMillis() - map.getCreate();
			// //恶魔广场 特殊事件
			if (startTime > (10*60+10) * 1000 || (map.getMapModelid()==300001 && startTime > (4*60+10) * 1000)) {
				
				if(zone!=null){
					zone.getOthers().put("zoneprocess", -1);
					// 通关失败消息
					if (zone.getOthers().get("isget") == null) {
						zone.getOthers().put("isget", true);
						byte ispass = 0;
						Q_clone_activityBean zoneBean = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(map.getZoneModelId());
						
						int star = ZonesFlopManager.getInstance().getStart((Integer) zone.getOthers().get("kill"), zoneBean.getQ_time_evaluate())   ;
						
						if(star==-1){
							for (Entry<Long, Player> entry : map.getPlayers().entrySet()) {
							MessageUtil.notify_player(entry.getValue(), Notifys.ERROR,ResManager.getInstance().getString("副本评价计算    配置错误"));
							}
							return;
						}
						
						
						
						if (zone.getOthers().get("kill") == null || star==0) {
							zone.getOthers().put("kill", 0);
						} else {
							ispass = 1;
							map.getParameters().put("isfinsh", true);
						}
						
					
					
						if(star>5){
							star= 5;
						}
						
						Iterator<Monster> monsters =    map.getMonsters().values().iterator();
						ResRoundMonsterDisappearMessage hidemsg = new ResRoundMonsterDisappearMessage();
						while (monsters.hasNext()) {
							Monster monster = monsters.next();
							monster.setState(MonsterState.DIE);
							hidemsg.getMonstersIds().add(monster.getId());
							
						}
						for (Entry<Long, Player> entry : map.getPlayers().entrySet()) {
							Player player = entry.getValue();
//							if(map.getPlayers().size()>1 && ispass==1){
								LivenessManager.getInstance().completeEmgc(player);
//							}
							ManagerPool.zonesFlopManager.getZoneReward(player, map.getZoneModelId(),star , 0,(byte)ispass);
							
							//通过副本： 怪物够 80只
							if(ispass == 1){
								ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.COMPLETECLASSICBATTLE, 1);
							}
							//通过副本并且等到s级评价：
							if(star>=3){
								ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.S_ZONE, 1);
								if(star>=4){
									ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.SS_ZONE, 1);
									if(star>=5){
										ManagerPool.taskManager.action(player, Task.ACTION_TYPE_RANK, TaskEnum.SSS_ZONE, 1);
									}
								}
							}
							MessageUtil.tell_round_message(player, hidemsg);
						}
						
					}
				}
			}
			
			if(map.getMapModelid()==300001){
			
				if (startTime < 1000 * 60 * 1) {
					
					
					re(map, grids);
				
				} else if (startTime > 1000 * 60 * 1 && startTime < 1000 * 60 * 2) {
					re(map, grids, 0);
					sendMessage(map, zone, 2);
				} else if (startTime > 1000 * 60 * 2 && startTime < 1000 * 60 * 3) {
					re(map, grids, 1);
					sendMessage(map, zone, 3);
				} else if (startTime > 1000 * 60 * 3 && startTime < 1000 * 60 * 4) {
					re(map, grids, 2);
					sendMessage(map, zone, 4);
				}
				
				
			}else{
			
			
				if (startTime < 1000 * 60 * 2.5) {
					re(map, grids);
				} else if (startTime > 1000 * 60 * 2.5 && startTime < 1000 * 60 * 5) {
					re(map, grids, 0);
					sendMessage(map, zone, 2);
				} else if (startTime > 1000 * 60 * 5 && startTime < 1000 * 60 * 7.5) {
					re(map, grids, 1);
					sendMessage(map, zone, 3);
				} else if (startTime > 1000 * 60 * 7.5 && startTime < 1000 * 60 * 9.5) {
					re(map, grids, 2);
					sendMessage(map, zone, 4);
				}
			}
			// 普通模式
		} else {
			re(map, grids);
		}
	}
	
	
	public void sendMessage(Map map,ZoneContext zone,int process) {
		if((int)zone.getOthers().get("zoneprocess")!=process){
			ResZoneLifeTimeMessage timemsg = new ResZoneLifeTimeMessage();
			Q_clone_activityBean zoneBean = ManagerPool.dataManager.q_clone_activityContainer.getMap().get(zone.getZonemodelid());
			timemsg.setSurplustime((int)(zoneBean.getQ_exist_time()/1000)- (((int)(System.currentTimeMillis()/1000))-(Integer)zone.getOthers().get("time")));
			timemsg.setZoneid(zoneBean.getQ_id());
			timemsg.setZoneprocess(process);
			zone.getOthers().put("zoneprocess",process);
//			System.out.println("阶段转换至："+process);
			for (Entry<Long, Player> entry : map.getPlayers().entrySet()) {
				Player player = entry.getValue();
				MessageUtil.tell_player_message(player, timemsg);
			}
		}
	}

	// 普通方式刷怪
	public void re(Map map, Grid[][] grids) {

		// 遍历怪物列表
		Iterator<Monster> iter = map.getRevives().values().iterator();
		while (iter.hasNext()) {
			Monster monster = (Monster) iter.next();
			if (monster.getRevive() > 0) {
				monster.setRevive(monster.getRevive() - 1);
				if (monster.getRevive() > 0)
					continue;
			}
			// 是否在复活时间段内
			Q_monsterBean q_monsterBean = DataManager.getInstance().q_monsterContainer
					.getMap().get(monster.getModelId());
//			if (q_monsterBean.getQ_refreshtime() != null&& !q_monsterBean.getQ_refreshtime().equals("")) {
//				if (!TimeUtil.checkRangeTime(q_monsterBean.getQ_refreshtime()))
//					continue;
//			}
			// 重置怪物状态
			monster.reset();
			monster.setPosition(monster.getBirthPos());
			if (bosses.contains(monster.getModelId()))
				log.error("Monster(" + monster.getModelId() + ") "
						+ monster.getId() + " revive!");
			// 获得怪物所在区域
			Area area = map.getAreas().get(
					ManagerPool.mapManager.getAreaId(monster.getPosition()));
			if (area == null) {
				iter.remove();
				continue;
			}
			// 地图上添加怪物
			area.getMonsters().put(monster.getId(), monster);
			if (bosses.contains(monster.getModelId()))
				log.error("Monster(" + monster.getModelId() + ") "
						+ monster.getId() + " enter map " + map.getId() + ":"
						+ map.getLineId() + " area " + area.getId() + "!");
			// 通知周围玩家
			ResMonsterReviveMessage msg = new ResMonsterReviveMessage();
			msg.setMonster(ManagerPool.mapManager
					.getMonsterInfo(monster, grids));
			MessageUtil.tell_round_message(monster, msg);
			
			//! 特殊Boss处理
			if (ChallengeManager.getInstance().isNeedBoss(monster)){
				ChallengeManager.getInstance().BossChangeState(monster.getModelId(), false);
			}
			
			if (q_monsterBean.getQ_info_sync() > 0) {
				ReqMonsterSyncMessage syncmsg = new ReqMonsterSyncMessage();
				syncmsg.setModelId(monster.getModelId());
				syncmsg.setLineId(monster.getLine());
				syncmsg.setServerId(monster.getServerId());
				syncmsg.setMonsterId(monster.getId());
				syncmsg.setCurrentHp(monster.getHp());
				syncmsg.setMaxHp(monster.getMaxHp());
				syncmsg.setState(1);
				syncmsg.setBirthX(monster.getBirthPos().getX());
				syncmsg.setBirthY(monster.getBirthPos().getY());
				MessageUtil.send_to_world(syncmsg);
				MessageUtil.tell_world_message(syncmsg);
			}
			iter.remove();
		}
	}

	// 刷怪机制 特殊处理 恶魔广场
	public void re(Map map, Grid[][] grids, int index) {
		// 遍历怪物列表
		Iterator<Monster> iter = map.getRevives().values().iterator();
		while (iter.hasNext()) {
			Monster monster = (Monster) iter.next();
			if (monster.getRevive() > 0) {
				monster.setRevive(monster.getRevive() - 1);
				if (monster.getRevive() > 0)
					continue;
			}

			int newModelId = monster.getMapModelId();
			if (monster.getParameters().get("q_scene_monsters") != null) {
				newModelId = Integer.parseInt(monster.getParameters().get("q_scene_monsters").toString()
						.split(",")[index]);
			} else {
				log.error("monster.getQ_add_monster_model() is NULL  !!!");
			}
			
			//更换怪物
			if(monster.getMapModelId()!=newModelId){
				//重置怪物技能
				try {
					//获得怪物模型
					Q_monsterBean model = ManagerPool.dataManager.q_monsterContainer.getMap().get(newModelId);
					
					if (model.getQ_passive_skill() != null && !("").equals(model.getQ_passive_skill())) {
						//技能模板_技能等级
						String[] morphs = model.getQ_passive_skill().split(Symbol.FENHAO_REG);
						for (int i = 0; i < morphs.length; i++) {
							String[] paras = morphs[i].split(Symbol.SHUXIAN_REG);
							Skill skill = new Skill();
							skill.setId(Config.getId());
							skill.setSkillModelId(Integer.parseInt(paras[0]));
							skill.setSkillLevel(Integer.parseInt(paras[1]));
							monster.getSkills().add(skill);
						}
					}
				} catch (NumberFormatException e) {
					log.error(e, e);
				}
			}
			
			monster.setModelId(newModelId);

			// 是否在复活时间段内
			Q_monsterBean q_monsterBean = DataManager.getInstance().q_monsterContainer
					.getMap().get(monster.getModelId());
//			if (q_monsterBean.getQ_refreshtime() != null
//					&& !q_monsterBean.getQ_refreshtime().equals("")) {
//				if (!TimeUtil.checkRangeTime(q_monsterBean.getQ_refreshtime()))
//					continue;
//			}
			// 重置怪物状态
			monster.reset();
			monster.setPosition(monster.getBirthPos());
			if (bosses.contains(monster.getModelId()))
				log.error("Monster(" + monster.getModelId() + ") "
						+ monster.getId() + " revive!");
			// 获得怪物所在区域
			Area area = map.getAreas().get(
					ManagerPool.mapManager.getAreaId(monster.getPosition()));
			if (area == null) {
				iter.remove();
				continue;
			}
			// 地图上添加怪物
			area.getMonsters().put(monster.getId(), monster);
			if (bosses.contains(monster.getModelId()))
				log.error("Monster(" + monster.getModelId() + ") "
						+ monster.getId() + " enter map " + map.getId() + ":"
						+ map.getLineId() + " area " + area.getId() + "!");
			// 通知周围玩家
			ResMonsterReviveMessage msg = new ResMonsterReviveMessage();
			msg.setMonster(ManagerPool.mapManager
					.getMonsterInfo(monster, grids));
			MessageUtil.tell_round_message(monster, msg);
			if (q_monsterBean.getQ_info_sync() > 0) {
				ReqMonsterSyncMessage syncmsg = new ReqMonsterSyncMessage();
				syncmsg.setModelId(monster.getModelId());
				syncmsg.setLineId(monster.getLine());
				syncmsg.setServerId(monster.getServerId());
				syncmsg.setMonsterId(monster.getId());
				syncmsg.setCurrentHp(monster.getHp());
				syncmsg.setMaxHp(monster.getMaxHp());
				syncmsg.setState(1);
				syncmsg.setBirthX(monster.getBirthPos().getX());
				syncmsg.setBirthY(monster.getBirthPos().getY());
				MessageUtil.send_to_world(syncmsg);
				MessageUtil.tell_world_message(syncmsg);
			}
			iter.remove();
		}
	}

}
