package com.game.summonpet.timer;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.game.cooldown.structs.CooldownTypes;
import com.game.data.bean.Q_skill_modelBean;
import com.game.fight.structs.Fighter;
import com.game.fight.structs.FighterState;
import com.game.manager.ManagerPool;
import com.game.map.structs.Map;
import com.game.monster.structs.Monster;
import com.game.pet.struts.Pet;
import com.game.pet.struts.PetRunState;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerState;
import com.game.skill.structs.Skill;
import com.game.structs.Grid;
import com.game.summonpet.struts.SummonPet;
import com.game.summonpet.struts.SummonPetRunState;
import com.game.timer.TimerEvent;
import com.game.utils.MapUtils;

public class SummonPetAttackTimer extends TimerEvent {

	/**
	 * Logger for this class
	 */
	protected static final Logger logger = Logger.getLogger(SummonPetAttackTimer.class);
	
	private int serverId;
	private int lineId;
	private int mapId;
	
	public SummonPetAttackTimer(int server_id, int line_id,int map_Id) {
		super(-1,100);
		serverId=server_id;
		lineId=line_id;
		mapId=map_Id;
	}
	
	@Override
	public void action() {
		// 获取地图
		Map map = ManagerPool.mapManager.getMap(serverId, lineId, mapId);
		if (map.isEmpty())
			return;
		
		//地图阻挡信息
		Grid[][] blocks = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());
				
		Iterator<SummonPet> iterator = map.getSummonpets().values().iterator();
		while (iterator.hasNext()) {
			SummonPet summonpet = (SummonPet) iterator.next();
			
			if(summonpet.getServerId()!=this.serverId || summonpet.getLine()!=this.lineId || summonpet.getMap()!=this.mapId){
				continue;
			}
			
			//定身或睡眠中
			if (FighterState.DINGSHEN.compare(summonpet.getFightState()) || FighterState.SHUIMIAN.compare(summonpet.getFightState())) {
				continue;
			}
			
			if (!summonpet.isShow() || summonpet.isDie()) {
				// 不显示的 和死的不处理
				continue;
			}
			
			// 召唤怪游泳中
			if (SummonPetRunState.SWIM == summonpet.getRunState()) {
				logger.debug("召唤怪游泳中了");
				continue;
			}
			
			//攻击冷却检查
			if(ManagerPool.cooldownManager.isCooldowning(summonpet, CooldownTypes.SUMMONPET_ATTACK, null)){
				continue;
			}
			
			Player owner = ManagerPool.playerManager.getPlayer(summonpet.getOwnerId());
			if(owner==null){
				logger.error("召唤怪主人不存在：" + summonpet.getOwnerId());
				continue;
			}
			
			// 游泳中
			if (PlayerState.SWIM.compare(owner.getState())) {
				logger.debug("攻击者主人（玩家）游泳中了");
				continue;
			}
			
			//获取目标
			Fighter target = summonpet.getAttTarget();
			
			//无攻击目标
			if(target==null) continue;
			//目标死亡
			if(target.isDie()){
				continue;
			}
			
			if(target instanceof Pet){
				continue;
			}
			
			if(target.getMap()!=summonpet.getMap() || target.getLine()!=summonpet.getLine()){
				continue;
			}
			
			if(target instanceof Player){
				Player player = (Player)target;
				//游泳状态
				if(PlayerState.SWIM.compare(player.getState())){
					continue;
				}
			}
			
			//怪物所在格子
			Grid petGrid = MapUtils.getGrid(summonpet.getPosition(), blocks);
			//获取目标所在格子
			Grid grid = MapUtils.getGrid(target.getPosition(), blocks);
			//是否在攻击范围内
			int distance = MapUtils.countDistance(petGrid, grid);
			//获取默认技能
			Skill skill = null;
			skill = summonpet.getDefaultSingleSkill();
			/*panic god 屏蔽
			if(target instanceof Monster){
				//skill = summonpet.getDefaultMutileSkill();
				
			}else if(target instanceof Player){
				skill = summonpet.getDefaultSingleSkill();
			}else{
				skill = summonpet.getDefaultSingleSkill();
			}*/
			if(skill==null) continue;
			//获取技能范围
			Q_skill_modelBean skillModel = ManagerPool.dataManager.q_skill_modelContainer.getMap().get(skill.getSkillModelId() + "_" + skill.getSkillLevel());
			if(skillModel==null){
				continue;
			}
			
			//在攻击范围内
			if(distance <= skillModel.getQ_range_limit() - 1){
				//停止走路
				ManagerPool.mapManager.summonpetStopRun(summonpet);
				
				if(ManagerPool.cooldownManager.isCooldowning(summonpet, CooldownTypes.SUMMONPET_RUN, null)) continue;
				
				if(target instanceof Player){
					ManagerPool.fightManager.summonpetAttackPlayer(summonpet, (Player)target, skill);
				}else if(target instanceof Monster){
					ManagerPool.fightManager.summonpetAttackMonster(summonpet, (Monster)target, skill);
				}
			}
		}
	}

}
