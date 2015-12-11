package com.game.map.timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.game.cooldown.structs.CooldownTypes;
import com.game.fight.structs.Fighter;
import com.game.manager.ManagerPool;
import com.game.map.message.ResRunPositionsMessage;
import com.game.map.message.ResSummonPetRunPositionsMessage;
import com.game.map.structs.Area;
import com.game.map.structs.Map;
import com.game.monster.structs.Monster;
import com.game.monster.structs.MonsterState;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerState;
import com.game.structs.Grid;
import com.game.structs.Position;
import com.game.summonpet.struts.SummonPet;
import com.game.timer.TimerEvent;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
import com.game.utils.RandomUtils;

/** 
 * @author  
 * 
 * @version 1.0.0
 * 
 * @since 2011-8-31
 * 
 * 玩家移动
 */
public class PlayerRunTimer extends TimerEvent {
	
	protected Logger log = Logger.getLogger(PlayerRunTimer.class);
	
	private int serverId;
	
	private int lineId;
	
	private int mapId;
	
	public PlayerRunTimer(int serverId, int lineId, int mapId){
		super(-1, 100);
		this.serverId = serverId;
		this.lineId = lineId;
		this.mapId = mapId;
	}

	@Override
	public void action() {
		//获取地图
		Map map = ManagerPool.mapManager.getMap(serverId, lineId, mapId);
		
		//获取玩家
		Iterator<Player> iter = map.getPlayers().values().iterator();
		
		//为机器人测试用
		//robot begin
		if(map.isEmpty()) 
			return;			
		//地图阻挡信息
		Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());
		//robot end
		
		while (iter.hasNext()) {
			Player player = (Player) iter.next();
			
			if(player.getMap()!=mapId || player.getLine()!=lineId){
				iter.remove();
				continue;
			}
			//机器人测试 robot
			//panic god test
			if(player.getSex() == 9){	
				//加一个这个判断，来4100毫秒进入一次这个逻辑
				if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.ROBOT_RUN_STEP, null, 0)) {
					continue;
				}
				
				if(PlayerState.FIGHT.compare(player.getState())){
					continue;
				}
				if(player.getLastRoads()!= null){
					if(player.getLastRoads().size() >0){
						continue;
					}
				}
				if (ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.RUN_STEP, null, 0)) {
					continue;
				}
				if(RandomUtils.random(1000) < 900){
					continue;
				}
				//学习技能
				if(player.getLevel() ==1){
					if(player.getJob() == 1){
						ManagerPool.skillManager.addSkill(player, 10005);
						ManagerPool.skillManager.addSkill(player, 10007);
						ManagerPool.skillManager.addSkill(player, 10008);
						ManagerPool.skillManager.addSkill(player, 10009);
					}else if(player.getJob() == 2){
						ManagerPool.skillManager.addSkill(player, 10012);
						ManagerPool.skillManager.addSkill(player, 10013);
						ManagerPool.skillManager.addSkill(player, 10014);
						ManagerPool.skillManager.addSkill(player, 10018);
						ManagerPool.skillManager.addSkill(player, 10019);
					}else if(player.getJob() == 3){
						ManagerPool.skillManager.addSkill(player, 10021);
						ManagerPool.skillManager.addSkill(player, 10022);
						ManagerPool.skillManager.addSkill(player, 10023);
						ManagerPool.skillManager.addSkill(player, 10024);
					}
				}
				
				
				if(map.isEmpty()) return;			
				List<Position> roads = new ArrayList<Position>();
				//地图上随机找一个怪
				//寻找警戒半径内的怪物
				List<Monster> Monsters = new ArrayList<Monster>();
				int Q_eyeshot = 300;
				
//				HashMap<Long, Monster> monstermaps = map.getMonsters();
//				Iterator<Long> iter2 = monstermaps.keySet().iterator();	
//				while (iter2.hasNext()) {
//				    Long key = iter2.next();
//				    Monster monster = monstermaps.get(key);
//				    Monsters.add(monster);
//				}
				//////
				//寻找警戒半径内的玩家
				
				//获得警戒半径以内区域
				int[] rounds = ManagerPool.mapManager.getRoundAreas(player.getPosition(), map, Q_eyeshot * MapUtils.GRID_BORDER);
				for (int i = 0; i < rounds.length; i++) {
					Area roundArea = map.getAreas().get(rounds[i]);
					if(roundArea==null) continue;
					//遍历区域内怪物
					Iterator<Long> iter2 = roundArea.getMonsters().keySet().iterator();	
					while (iter2.hasNext()) {
					    Long key = iter2.next();
					    Monster monster = roundArea.getMonsters().get(key);
						if(!monster.canSee(player)) continue;
						if(!monster.canAttack(player)) continue;
						//计算距离
						double distance = MapUtils.countGridDistance(player.getPosition(), monster.getPosition(), blocks);
						//在警戒范围内
						if(distance <= Q_eyeshot){
							Monsters.add(monster);
						}
					}
				}
				
				
				
				//////
				
				Fighter target2 = null;
				int cout2=0;
				if(Monsters.size()<=0){
					continue;
				}
				target2  = Monsters.get(RandomUtils.random(Monsters.size()-1));	
//				while(true){
//					target2  = Monsters.get(RandomUtils.random(Monsters.size()-1));	
//					cout2++;
//					//距离不能太远
//					 double distances = MapUtils.countGridDistance(player.getPosition(), target2.getPosition(), blocks );
//					 if(distances >300){
//						 if(cout2 >1000){
//							 break;	
//						 }
//						 continue;
//					 }else{
//						 break;
//					 }
//					 
//				}
				if(cout2 >1000){
					continue;
				}
				if(target2 == null){
					continue;
				}
				// 寻路到攻击目标	
				roads = MapUtils.findRoads(blocks, player.getPosition(), target2.getPosition(), -1, false);
				//先切换一半的路径
//				List<Position> roads_two = new ArrayList<Position>();
//				int lenj = roads.size()/10;
//				if(lenj>2){
//					Position   b = roads.get(lenj);
//					Grid stand = MapUtils.getGrid(b , blocks);
//					roads_two = MapUtils.findRoads(blocks, player.getPosition(), b, -1, false);
//					//做个判断
//					for(int gri = 0; gri <roads_two.size(); gri++){
//						Position prev = roads_two.get(gri);
//						Grid grid = MapUtils.getGrid(prev, blocks);
//						if (grid == null || grid.getBlock() == 0) {
//							log.error("");
//						}
//					}
//
//					
//					
//				}
				
				
				//roads 转换为 List<Byte>
//				ManagerPool.cooldownManager.removeCooldown(player, CooldownTypes.RUN_STEP, null);
//				if (!ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.RUN_STEP, null, 0)) {
//					player.setLastPosition(player.getPosition());
//					player.setLastRoads(ManagerPool.mapManager.robotcastPositionToByte(player.getPosition(), roads_two, blocks));
//				}else{
//					player.setLastPosition(null);
//					player.setLastRoads(null);
//				}

				//开始走动
				// 确定方向
				if (roads.size() > 0) {
					player.setDirection((byte) MapUtils.countDirection(
						MapUtils.getGrid(player.getPosition(), blocks),
						MapUtils.getGrid(roads.get(0), blocks)));
				}
				// 清空当前移动路径
				player.getRoads().clear();
				// 设置移动路径
				player.setRoads(roads);
				//100毫秒冷却
				ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.RUN_STEP, null, 200);
				
				if (!ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.RUN_STEP, null)) {
					player.setPrevStep(System.currentTimeMillis());
					player.setCost(0);
				}
				
				if (player.isDie()) {
					log.debug("死亡中不能走路");
					// 清空当前移动路径
					ManagerPool.mapManager.broadcastPlayerForceStop(player);
					return;
				}
				
				//移除跳跃保护
				player.setJumpProtect(false);
				/**
				 * 计算区域 *
				 */
				// 设置移动起点
				Position old = player.getPosition();
				// 原来所在区域
				int oldAreaId = ManagerPool.mapManager.getAreaId(old);
				// 现在所在区域
				int newAreaId = ManagerPool.mapManager.getAreaId(player.getPosition());
				// 区域未变
				if (oldAreaId != newAreaId) {
					ManagerPool.mapManager.playerChangeArea(player, oldAreaId, newAreaId);
				}

				player.setPrevStep(System.currentTimeMillis());
				player.setCost(0);
				ManagerPool.cooldownManager.removeCooldown(player, CooldownTypes.RUN, null);

				// 开始移动
				HashMap<Long, Player> runnings = map.getRunningPlayers();
				if (!runnings.containsKey(player.getId())) {
					runnings.put(player.getId(), player);
				}

				ResRunPositionsMessage other_msg = new ResRunPositionsMessage();
				other_msg.setPersonId(player.getId());
				other_msg.setPosition(player.getPosition());
				other_msg.setPositions(ManagerPool.mapManager.robotcastPositionToByte(player.getPosition(), roads, blocks));

				MessageUtil.tell_round_message(player, other_msg);

				ManagerPool.mapManager.syncPlayerPosition(player);
				if (player.getTeamid() > 0) {
					ManagerPool.teamManager.showMapTeamMember(player);
				}
				
				//4100毫秒冷却
				ManagerPool.cooldownManager.addCooldown(player, CooldownTypes.ROBOT_RUN_STEP, null, 4000);
				continue;
			}
				
			if(ManagerPool.cooldownManager.isCooldowning(player, CooldownTypes.RUN_STEP, null, 0)){
				continue;
			}else if(player.getLastPosition()==null || player.getLastRoads()==null || player.getLastRoads().size()==0){
				continue;
			}else{
				ManagerPool.mapManager.playerRunning(player, player.getLastPosition(), player.getLastRoads(), System.currentTimeMillis());
			}
		}
	}
	
}
