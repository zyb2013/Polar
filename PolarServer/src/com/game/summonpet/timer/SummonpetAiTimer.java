package com.game.summonpet.timer;

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
import com.game.monster.structs.MonsterState;
import com.game.pet.manager.PetOptManager;
import com.game.pet.message.ResPetTargetMessage;
import com.game.summonpet.manager.SummonPetOptManager;
import com.game.summonpet.message.ResSummonPetTargetMessage;
import com.game.summonpet.struts.SummonPet;
import com.game.summonpet.struts.SummonPetJumpState;
import com.game.summonpet.struts.SummonPetState;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerState;
import com.game.skill.structs.Skill;
import com.game.structs.Grid;
import com.game.structs.Position;
import com.game.timer.TimerEvent;
import com.game.utils.Global;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
import com.game.utils.RandomUtils;

public class SummonpetAiTimer extends TimerEvent {
	/**
	 * Logger for this class
	 */
	protected static final Logger logger = Logger.getLogger(SummonpetAiTimer.class);
	
	private int serverId;
	private int lineId;
	private int mapId;
	
	public SummonpetAiTimer(int server_id, int line_id,int map_Id) {
		super(-1, 500);
		serverId=server_id;
		lineId=line_id;
		mapId=map_Id;
	}

	@Override
	public void action() {
		Map map = ManagerPool.mapManager.getMap(serverId, lineId, mapId);
		if(map==null){
			logger.error(mapId+"找不到",new Exception());
			return;
		}
		if(map.isEmpty()) return;
		
		//地图阻挡信息
		Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());
				
		Iterator<SummonPet> iterator = map.getSummonpets().values().iterator();
		while (iterator.hasNext()) {
			SummonPet summonpet = (SummonPet) iterator.next();

			if(summonpet.getServerId()!=this.serverId || summonpet.getLine()!=this.lineId || summonpet.getMap()!=this.mapId){
				continue;
			}
			
			//攻击冷却检查
			if(ManagerPool.cooldownManager.isCooldowning(summonpet, CooldownTypes.SUMMONPET_ACTION, null)){
				continue;
			}
			
			//定身或睡眠中
			if (FighterState.DINGSHEN.compare(summonpet.getFightState()) || FighterState.SHUIMIAN.compare(summonpet.getFightState())) {
				continue;
			}
			
			if(summonpet.getJumpState()!=SummonPetJumpState.NOMAL){
				//跳跃中
				continue;
			}
			
			if (!summonpet.isShow() || summonpet.isDie()) {
				// 不显示的 和死的不处理
				continue;
			}

			//召唤怪所在阻挡格
			Grid petGrid = MapUtils.getGrid(summonpet.getPosition(), blocks);
			
			Player player = ManagerPool.playerManager.getPlayer(summonpet.getOwnerId());
			if(player==null){
				logger.error("召唤怪主人不存在：" + summonpet.getOwnerId());
				continue;
			}
			//获得召唤怪主人中心
			Position center = player.getPosition();
			//获取目标所在格子
			Grid ownergrid = MapUtils.getGrid(center, blocks);
			
			//查看与主人的距离
			int distance = MapUtils.countDistance(petGrid, ownergrid);
			
			//召唤怪和主人之间走路不可达到
			boolean runAble = (petGrid.getBlock()==ownergrid.getBlock());
			//距离过大
			if(distance >= Global.PET_TIGGER_TRANSFER_DISTANCE){
				// 跳跃中
				if (PlayerState.JUMP.compare(player.getState())
					|| PlayerState.DOUBLEJUMP.compare(player.getState())) {
					//主人跳跃中暂时不传送
					continue;
				}
				//清除走路路径
				summonpet.getRoads().clear();
				summonpet.setDest(null);
				//停止攻击
				SummonPetOptManager.getInstance().changeAttackTarget(summonpet, null, SummonPetOptManager.FIGHTTYPE_PET_IDEL);
				//寻找召唤怪与玩家之间的坐标
				List<Grid> grids = MapUtils.getLineGrids(summonpet.getPosition(), player.getPosition(), map.getMapModelid());
				Grid grid = null;
				for (int i = 0; i < grids.size(); i++) {
					grid = grids.get(i);
					if(grid==null) continue;
					int dis = MapUtils.countDistance(grid, ownergrid);
					if(dis > Global.PET_TIGGER_TRANSFER_TO || grid.getBlock()==0 || ManagerPool.mapManager.isSwimGrid(petGrid)){
						continue;
					}else{
						break;
					}
				}
				if(grid==null){
					grid = ownergrid;
				}
				//传送坐标
				ManagerPool.mapManager.summonpetTrans(summonpet, grid.getCenter());
//				logger.error("需要传送了！");
				continue;
			}
			
			//主人游泳状态或打坐状态
			if(PlayerState.SWIM.compare(player.getState()) || ManagerPool.dazuoManager.isSit(player)){
				//停止攻击
				SummonPetOptManager.getInstance().changeAttackTarget(summonpet, null, SummonPetOptManager.FIGHTTYPE_PET_IDEL);
			}
			
			//获取进攻目标
			List<Fighter> targets = summonpet.getAttackTargets();
			Fighter target = null;
			
			Iterator<Fighter> iter = targets.iterator();
			while (iter.hasNext()) {
				Fighter fighter = (Fighter) iter.next();
				
				if(fighter.isDie()){
					iter.remove();
					continue;
				}
				
				if(fighter.getMap()!=summonpet.getMap() || fighter.getLine()!=summonpet.getLine()){
					iter.remove();
					continue;
				}
				
				Grid grid = MapUtils.getGrid(fighter.getPosition(), blocks);
				//是否在攻击范围内
				int dis = MapUtils.countDistance(petGrid, grid);
				//传送距离外不攻击
				if(dis > Global.PET_TIGGER_TRANSFER_DISTANCE){
					iter.remove();
					continue;
				}
				
				if(fighter instanceof Player){
					Player tplayer = (Player)fighter;
					//游泳状态
					if(PlayerState.SWIM.compare(tplayer.getState())){
						iter.remove();
						continue;
					}
				}else if(fighter instanceof Monster){
					if(MonsterState.RUNBACK.compare(((Monster) fighter).getState())){
						iter.remove();
						continue;
					}
				}
			}
			
			if(targets.size()==0){
				summonpet.setTargetType(0);
			}else{
				target = targets.get(0);
			}
			
			if(target!=null && summonpet.getAttTarget()!=target){
//				logger.error("owner " + player.getId() + " pet " + pet.getId() + " 切换目标 " + target.getId());
				ResSummonPetTargetMessage targetmsg = new ResSummonPetTargetMessage();
				targetmsg.setTargetId(target.getId());
				MessageUtil.tell_player_message(player, targetmsg);
			}
			
			if(summonpet.getAttTarget()!=null && target==null){
				ResSummonPetTargetMessage targetmsg = new ResSummonPetTargetMessage();
				targetmsg.setTargetId(0);
				MessageUtil.tell_player_message(player, targetmsg);
			}
			
			summonpet.setAttTarget(target);

			//无攻击目标
			
			if(target==null){
				//追赶主人
				/* panic god 暂时没有跳
				if((distance >= Global.PET_TIGGER_JUMP_DISTANCE || !runAble) && !ManagerPool.mapManager.isSwimGrid(petGrid)){
					//需要跳跃
					boolean jump = true;
					// 跳跃中
					if (PlayerState.JUMP.compare(player.getState())
						|| PlayerState.DOUBLEJUMP.compare(player.getState())) {
						//主人跳跃中暂时不传送
						long time = System.currentTimeMillis() - player.getJump().getJumpStartTime();
						if(time < 1000){
							jump = false;
						}
					}
					
					if(ManagerPool.cooldownManager.isCooldowning(summonpet, CooldownTypes.JUMP_COOLDOWN, null)){
						jump = false;
					}
					
					//40%几率跳跃
					if(runAble && distance < Global.PET_TIGGER_DOUBLEJUMP_DISTANCE){
						int prob = RandomUtils.random(Global.MAX_PROBABILITY);
						if(prob > 4000){
							jump = false;
						}
					}
					
					if(jump){
						int prob = RandomUtils.random(Global.MAX_PROBABILITY);
						int type = 1;
						if(distance >= Global.PET_TIGGER_DOUBLEJUMP_DISTANCE && prob > 5000){
							type = 2;
						}
						//清除走路路径
						summonpet.getRoads().clear();
						summonpet.setDest(null);
						//停止攻击
						SummonPetOptManager.getInstance().changeAttackTarget(summonpet, null, SummonPetOptManager.FIGHTTYPE_PET_IDEL);
						//跳跃坐标
						SummonPetOptManager.getInstance().summonpetJump(summonpet, type, MapUtils.getBackPoint(player.getPosition(), player.getDirection(), map.getMapModelid()));
	//					logger.error("需要跳跃了！");
						continue;
					}
				}
			*/
				if(distance >= Global.PET_TIGGER_FOLLOW_DISTANCE){
					//寻路到目标身边
					List<Position> roads = MapUtils.findRoads(blocks, summonpet.getPosition(), MapUtils.getRandomGrid(center, 50, map.getMapModelid()).getCenter(), -1, true);
					logger.debug("pet " + summonpet.getId() + " owner " + center + " run " + roads);
					//开始走动
					if(roads.size() > 0){
						summonpet.changeStateTo(SummonPetState.FLLOW);
						summonpet.setDest(center);
						ManagerPool.mapManager.summonpetRunning(summonpet, roads);
						
						continue;
					}
				}else if(summonpet.getState().getValue() == SummonPetState.FLLOW.getValue() && summonpet.getDest()!=null){
					if(distance > Global.PET_RUNNING_FOLLOW_DISTANCE){
						if(!summonpet.getDest().equal(center)){
							//寻路到目标身边
							List<Position> roads = MapUtils.findRoads(blocks, summonpet.getPosition(), MapUtils.getRandomGrid(center, 50, map.getMapModelid()).getCenter(), -1, true);
							logger.debug("pet " + summonpet.getId() + " owner " + center + " run " + roads);
							//开始走动
							if(roads.size() > 0){
								summonpet.changeStateTo(SummonPetState.FLLOW);
								summonpet.setDest(center);
								
								ManagerPool.mapManager.summonpetRunning(summonpet, roads);
								
								continue;
							}
						}
					}else{
						summonpet.changeStateTo(SummonPetState.IDEL);
						summonpet.getRoads().clear();
						
						continue;
					}
				}
				
				
				//主人附近来回走动

//					//计算几率
//					if(RandomUtils.random(Global.MAX_PROBABILITY) >= monster.getPatrolPro()){
//						continue;
//					}
//						
//					//路径集
//					List<Position> roads = new ArrayList<Position>();
//					
//					Grid centerGrid = MapUtils.getGrid(birth, blocks);
//					
//					int dir = RandomUtils.random(8);
//					
//					int[] add = MapUtils.countDirectionAddtion(dir);
//					
//					boolean over = false;
//					int gridx = monsterGrid.getX();
//					int gridy = monsterGrid.getY();
//					//确定目标点
//					while(!over){
//						gridx = gridx + add[0];
//						gridy = gridy + add[1];
//						Grid grid = MapUtils.getGrid(gridx, gridy, blocks);
//						
//						//在地图内是不可行走点
//						if(grid==null || grid.getBlock() == 0 || grid.getBlock()!=monsterGrid.getBlock()){
//							over = true;
//							continue;
//						}
//						//斜向判断周围是不可行走点
//						else if(dir % 2!=0){
//							if(blocks[gridy][gridx - add[0]].getBlock() == 0 && blocks[gridy - add[1]][gridx].getBlock() == 0){
//								over = true;
//								continue;
//							}
//						}
//						//就是当前怪物所在格
//						else if(MapUtils.countDistance(grid, centerGrid) > model.getQ_patrol()){
//							over = true;
//							continue;
//						}
//						//就是当前怪物所在格
//						else if(monster.equals(grid)){
//							over = true;
//							continue;
//						}else{
//							roads.add(grid.getCenter());
//							if(roads.size()>=5){
//								over = true;
//							}
//						}
//					}
//
//					//开始走动
//					if(roads.size() > 0){
//						roads.add(0, monsterGrid.getCenter());
//						ManagerPool.cooldownManager.addCooldown(monster, CooldownTypes.MONSTER_PATROL, null, monster.getPatrolTime());
//						ManagerPool.mapManager.monsterRunning(monster, roads);
//					}
			}
			
			//已有攻击目标
			else {
				//获取目标所在格子
				Grid grid = MapUtils.getGrid(target.getPosition(), blocks);
				//是否在攻击范围内
				distance = MapUtils.countDistance(petGrid, grid);
				//传送距离外不攻击
				if(distance > Global.PET_TIGGER_TRANSFER_DISTANCE){
					continue;
				}
				//获取默认技能
				Skill skill = null;
				if(target instanceof Monster){
					//skill = summonpet.getDefaultMutileSkill();
					skill = summonpet.getDefaultSingleSkill();
				}else if(target instanceof Player){
					skill = summonpet.getDefaultSingleSkill();
				}else{
					skill = summonpet.getDefaultSingleSkill();
				}
				if(skill==null) continue;
				//获取技能范围
				Q_skill_modelBean skillModel = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
				if(skillModel==null) continue;
				
				//在攻击范围内
				if(distance > skillModel.getQ_range_limit() - 1){
					//与敌方目标之间是否可以走路到达
					runAble = (petGrid.getBlock()==grid.getBlock());
					
					/* panic god 屏蔽跳跃
					if((!runAble || distance > Global.PET_TIGGER_JUMP_DISTANCE) && !ManagerPool.mapManager.isSwimGrid(petGrid)){
						
						boolean jump = true;
						if(ManagerPool.cooldownManager.isCooldowning(summonpet, CooldownTypes.JUMP_COOLDOWN, null)){
							jump = false;
						}
						
						//40%几率跳跃
						if(runAble && distance < Global.PET_TIGGER_DOUBLEJUMP_DISTANCE){
							int prob = RandomUtils.random(Global.MAX_PROBABILITY);
							if(prob > 4000){
								jump = false;
							}
						}
						
						if(jump){
							int prob = RandomUtils.random(Global.MAX_PROBABILITY);
							int type = 1;
							if(distance >= Global.PET_TIGGER_DOUBLEJUMP_DISTANCE && prob > 5000){
								type = 2;
							}
							//清除走路路径
							summonpet.getRoads().clear();
							summonpet.setDest(null);
							//跳跃坐标
							SummonPetOptManager.getInstance().summonpetJump(summonpet, type, MapUtils.getBackPoint(target.getPosition(), player.getDirection(), map.getMapModelid()));
		//					logger.error("需要跳跃了！");
							continue;
						}
					}
					*/
					if(runAble){
						//寻路到目标身边
						List<Position> roads = MapUtils.findRoads(blocks, summonpet.getPosition(), MapUtils.getRandomGrid(target.getPosition(), 50, map.getMapModelid()).getCenter(), -1, true);
						logger.debug("pet " + summonpet.getId() + "target" + target.getPosition() + " run " + roads);
						//开始走动
						if(roads.size() > 0){
							if(roads.get(roads.size() - 1).equal(target.getPosition())) roads.remove(roads.size() - 1);
							//log.info("怪物" + monster.getId() + "(" + monster.getPosition() + ")追击目标" + target.getId() + "(" + target.getPosition() + ")路径为" + other_msg.toString());
							
							ManagerPool.mapManager.summonpetRunning(summonpet, roads);
						}
					}
				}
			}
		}
	}
	
}
