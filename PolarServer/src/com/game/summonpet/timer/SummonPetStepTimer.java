package com.game.summonpet.timer;


import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.game.cooldown.structs.CooldownTypes;
import com.game.fight.structs.FighterState;
import com.game.manager.ManagerPool;
import com.game.map.structs.Map;
import com.game.pet.struts.Pet;
import com.game.pet.struts.PetJumpState;
import com.game.pet.struts.PetRunState;
import com.game.structs.Grid;
import com.game.structs.Position;
import com.game.summonpet.struts.SummonPet;
import com.game.summonpet.struts.SummonPetJumpState;
import com.game.summonpet.struts.SummonPetRunState;
import com.game.timer.TimerEvent;
import com.game.utils.Global;
import com.game.utils.MapUtils;

/**
 * 召唤怪行走
 * @author  
 *
 */
public class SummonPetStepTimer extends TimerEvent {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SummonPetStepTimer.class);

	private int serverid;
	private int lineid;
	private int mapid;

	public SummonPetStepTimer(int server_id, int line_id,int mapId) {
		super(-1,50);
		this.serverid=server_id;
		this.lineid=line_id;
		this.mapid=mapId;
	}

	@Override
	public void action() {
		//获取地图
		Map map = ManagerPool.mapManager.getMap(serverid, lineid, mapid);
		if(map.isEmpty()) return;
		
		//获取地图格子
		Grid[][] grids = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());

		Iterator<SummonPet> iter = map.getRunningsummonPets().values().iterator();
		while (iter.hasNext()) {
			SummonPet summonpet = (SummonPet) iter.next();
			
			if(summonpet.getServerId()!=this.serverid || summonpet.getLine()!=this.lineid || summonpet.getMap()!=this.mapid){
				iter.remove();
				continue;
			}
			
			if(summonpet.getJumpState()!=SummonPetJumpState.NOMAL){
				
				if(System.currentTimeMillis() >= summonpet.getJump().getJumpStartTime() + summonpet.getJump().getTotalTime()){
					//跳跃结束
					iter.remove();
					
					summonpet.setJumpState(SummonPetJumpState.NOMAL);
					ManagerPool.cooldownManager.addCooldown(summonpet, CooldownTypes.JUMP_COOLDOWN, null, 500);
				}
				
//				ManagerPool.mapManager.syncPlayerPosition(player);
//				ManagerPool.mapManager.broadcastPlayerStop(player);
				//player.setState(PlayerState.STAND);
//				ManagerPool.mapManager.setPlayerPosition(player, player.getPosition());

				continue;
			}
			
			// 召唤怪死亡
			if (summonpet.isDie()) {
				summonpet.getRoads().clear();
				iter.remove();
				continue;
			}
			
			if (ManagerPool.cooldownManager.isCooldowning(summonpet, CooldownTypes.SUMMONPET_RUN, null)) {
				continue;
			}
			
			//定身或睡眠中
			if (FighterState.DINGSHEN.compare(summonpet.getFightState()) || FighterState.SHUIMIAN.compare(summonpet.getFightState())) {
				continue;
			}
			
			//移动路径
			List<Position> roads = summonpet.getRoads();
			
			//路径为空或不存在
			if(roads==null || roads.size()==0){
				//移动完成
				iter.remove();
				ManagerPool.mapManager.broadcastSummonPetStop(summonpet);
				
				continue;
			}
			
			int time = 0;
			
			double cost = summonpet.getPrevStep() + summonpet.getCost() - System.currentTimeMillis();

			// 召唤怪原来坐标
			Position old = summonpet.getPosition();
			// 移动距离
			double speed = 0;

			if(grids==null){
				logger.error(summonpet.getMapModelId()+"找不到阻挡点信息");
				return;
			}
			while (time <= 0 && roads.size() > 0) {
				if(SummonPetRunState.RUN==summonpet.getRunState()){
					speed = summonpet.getSpeed();
				}else{
					speed = Global.SPEED_FOR_SWIM;
				}
	
				// 第一拐点
				Position position = roads.remove(0);
				// 与第一拐点距离
				double distance = MapUtils.countDistance(summonpet.getPosition(), position);

				double use = distance * 1000 / speed;
				cost += use;
				//获取地图格子
				summonpet.setDirection((byte) MapUtils.countDirection(MapUtils.getGrid(summonpet.getPosition(), grids), MapUtils.getGrid(position, grids)));
				ManagerPool.summonpetOptManager.setSummonPetPosition(summonpet, position);
//				logger.debug("[" + System.currentTimeMillis() + "]召唤怪" + pet.getId() + "移动到：" + position.getX() + "," + position.getY() + ",移动真实耗时" + use+ ", 移动补充后耗时:" + cost);

				time = (int) cost;
				if (time > 0) {
					summonpet.setPrevStep(System.currentTimeMillis());
					summonpet.setCost(time);
					ManagerPool.cooldownManager.addCooldown(summonpet, CooldownTypes.PET_RUN, null, time);
				}
			}
	
			/**计算区域**/
			ManagerPool.mapManager.summonpetChangeArea(summonpet, old, summonpet.getPosition());
		}
	}
}

	
