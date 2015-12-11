package com.game.map.timer;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.game.cooldown.structs.CooldownTypes;
import com.game.data.bean.Q_skill_modelBean;
import com.game.fight.structs.Fighter;
import com.game.fight.structs.FighterState;
import com.game.manager.ManagerPool;
import com.game.map.structs.Map;
import com.game.monster.structs.Monster;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerState;
import com.game.skill.structs.Skill;
import com.game.structs.Grid;
import com.game.summonpet.struts.SummonPet;
import com.game.timer.TimerEvent;
import com.game.utils.MapUtils;
import com.game.utils.RandomUtils;

public class RobotPlayerAttackTimer extends TimerEvent {

	private Logger log = Logger.getLogger(RobotPlayerAttackTimer.class);
	
	private int serverId;
	
	private int lineId;
	
	private int mapId;
	
	public RobotPlayerAttackTimer(int serverId, int lineId, int mapId){
		super(-1, 100);
		this.serverId = serverId;
		this.lineId = lineId;
		this.mapId = mapId;
	}
	
	@Override
	public void action() {
		//按地图，区域遍历机器人列表
		Map map = ManagerPool.mapManager.getMap(serverId, lineId, mapId);
		
		if(map.isEmpty()) return;
		
		//地图阻挡信息
		Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());
		
		//遍历地区
//		Iterator<Area> areaIter = map.getAreas().values().iterator();
//		while (areaIter.hasNext()) {
//			Area area = (Area) areaIter.next();
//			
//			Monster[] monsters = area.getMonsters().values().toArray(new Monster[0]);
//			for (Monster monster : monsters) {
		Iterator<Player> iter = map.getPlayers().values().iterator();
		while (iter.hasNext()) {
			Player player = (Player) iter.next();
            if(player.getSex() != 9){
            	continue;
			}
			//机器人死亡或正在跑回
			if(PlayerState.DIE.compare(player.getState()) ){
				continue;
			}
			
			//CD
			if(ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.SKILL_PUBLIC, null)){
				continue;
			}

			//获取人物的技能
			List<Skill> skills = player.getSkills();
			Skill skill = null;
			if(skills.size()>0){
				skill = skills.get(RandomUtils.random(skills.size()));
			}

			Q_skill_modelBean skillModel = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getRealLevel(player));
			if (skillModel == null) {
				continue;
			}
			Grid[][] grids = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());
			//设置敌人类型 0-全部, 1-玩家和宠物和召唤怪, 2-怪物
			int type = 2;
			//玩家类型选择 0-和平 1-强制 2-全体
			int playerAttackType = 0;
			//获取目标 //机器人周围随机找一个怪
			//获取范围内战斗者
			List<Fighter> fighters = null;
			fighters = ManagerPool.fightManager.robotgetFighters(player.getPosition(), map, skillModel.getQ_circular_radius()<200?skillModel.getQ_circular_radius():200, type);
			Fighter target = null;
			if(fighters.size()>0){
				target = fighters.get(0);
			}
			//无攻击目标
			if(target==null) continue;
			//目标死亡
			if(target.isDie()) continue;
			
			if(target.getMap()!=player.getMap() || target.getLine()!=player.getLine()) continue;
			
			//定身或睡眠中
			if(FighterState.DINGSHEN.compare(player.getFightState()) || FighterState.SHUIMIAN.compare(player.getFightState())) continue;
			
			//机器人所在格子
			Grid monsterGrid = MapUtils.getGrid(player.getPosition(), blocks);
			//获取目标所在格子
			Grid grid = MapUtils.getGrid(target.getPosition(), blocks);
			//是否在攻击范围内
			int distance = MapUtils.countDistance(monsterGrid, grid);
						
			//在攻击范围内
			if(distance <= skillModel.getQ_range_limit() - 1){
				//停止走路
				ManagerPool.mapManager.playerStopRun(player);
				int direction = MapUtils.countDirection(player.getPosition(), target.getPosition());
				
				if(ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.RUN_STEP, null)) continue;
				
				//log.info("机器人" + monster.getId() + "在" + monster.getPosition() + "使用范围为" + skillModel.getQ_range_limit() + "的" + skill.getSkillModelId() + "技能攻击坐标为" + target.getPosition() + "的目标" + target.getId());
				//攻击
				if(skill!=null){
					if(target instanceof Player){
						ManagerPool.fightManager.playerAttackPlayer(player, (long)target.getId(), skillModel.getQ_skillID(),direction );
					}else if(target instanceof Monster){
						ManagerPool.fightManager.playerAttackMonster(player, (long)target.getId(), skillModel.getQ_skillID(),direction );
					}else if(target instanceof SummonPet){
						ManagerPool.fightManager.playerAttackSummonPet(player, (long)player.getId(), (long)target.getId(), skillModel.getQ_skillID(), direction );
					}
				}
			}
		}
	}
}
