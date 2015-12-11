package com.game.summonpet.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.game.backpack.structs.Item;
import com.game.config.Config;
import com.game.data.bean.Q_mapBean;
import com.game.data.bean.Q_monsterBean;
import com.game.dazuo.manager.PlayerDaZuoManager;
import com.game.dblog.LogService;
import com.game.fight.structs.Fighter;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.map.manager.MapManager;
import com.game.map.message.ResRoundSummonPetDisappearMessage;
import com.game.map.message.ResRoundSummonPetMessage;
import com.game.map.message.ResSummonPetJumpPositionsMessage;
import com.game.map.structs.Area;
import com.game.map.structs.Jump;
import com.game.map.structs.Map;
import com.game.monster.structs.Monster;
import com.game.pet.log.PetAddLog;
import com.game.pet.manager.PetInfoManager;
import com.game.pet.manager.PetOptManager;
import com.game.pet.manager.PetScriptManager;
import com.game.pet.message.ResPetHidenMessage;
import com.game.pet.message.ResPetShowMessage;
import com.game.pet.struts.Pet;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.structs.Grid;
import com.game.structs.Position;
import com.game.summonpet.message.ResSummonPetAddMessage;
import com.game.summonpet.message.ResSummonPetDieBroadcastMessage;
import com.game.summonpet.message.ResSummonPetDieMessage;
import com.game.summonpet.struts.SummonPet;
import com.game.summonpet.struts.SummonPetJumpState;
import com.game.summonpet.struts.SummonPetRunState;
import com.game.summonpet.struts.SummonPetState;
import com.game.utils.Global;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
/**
 * 召唤怪操作管理 
 * @author  
 *
 */
public class SummonPetOptManager {
	/**
	 * 玩家主动攻击
	 */
	public static int FIGHTTYPE_PLAYER_ATTACK=1;
	/**
	 * 玩家攻击波及
	 */
	public static int FIGHTTYPE_PLAYER_DAMAGE=2;
	/**
	 * 玩家被攻击
	 */
	public static int FIGHTTYPE_PLAYER_DEFENCE=3;
	/**
	 * 召唤怪被攻击
	 */
	public static int FIGHTTYPE_PET_DEFENCE=4;
	
	/**
	 * 无战斗状态
	 */
	public static int FIGHTTYPE_PET_IDEL=0;
	
	/**
	 * 合体增加最多的属性条数（暂时用不到）
	 */
	public static int HTADDATTRIBUTECOUNT=3;
	
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SummonPetOptManager.class);

	private static final SummonPetOptManager instance=new SummonPetOptManager();
	
//	private static int maxpets=24;
	//最大可拥有的数量
	public static SummonPetOptManager getInstance(){
		return instance;
	}
	private SummonPetOptManager(){
	}
	
	/**
	 * 召唤怪出战
	 * @param player
	 * @param summonpetId
	 */
	public void showSummonPet(Player player, int modelId) {
		if(player.isDie()){
			return;
		}
		long action=Config.getId();
		SummonPet summonpet = SummonPetInfoManager.getInstance().getShowSummonPet(player);
		forceKillSummonPet(summonpet);
        //增加召唤怪
		addSummonPet(player, modelId, "召唤怪" , action , true);
		summonpet = SummonPetInfoManager.getInstance().getShowSummonPet(player);
		if(summonpet != null){
		    MapManager.getInstance().enterMap(summonpet);
		}
	}
	/**
	 * 增加召唤怪
	 * @param player
	 * @param modelId 模型 
	 * @param reason 原因 
	 */
	public void addSummonPet(Player player, int modelId,String reason,long actionId, boolean exclude) {
		addSummonPet(player, modelId, reason, actionId);
	}
	
	public void addSummonPet(Player player, int modelId,String reason,long actionId) {
		SummonPet summonpet = new SummonPet(player, modelId);
		player.addSummonPet(summonpet);
		summonpet.setHp(summonpet.getMaxHp());
		summonpet.setSp(summonpet.getMaxSp());
		summonpet.setMp(summonpet.getMaxMp());
		summonpet.setShow(true);
		ResSummonPetAddMessage msg = new ResSummonPetAddMessage();
		msg.setSummonPet(SummonPetInfoManager.getInstance().getDetailInfo(summonpet));
		MessageUtil.tell_player_message(player, msg);
		
		/*
		try{
			if(player.getLevel() >= Global.SYNC_PLAYER_LEVEL && pet.getModelId() >= Global.SYNC_EVENT_PET){
				ReqSyncPlayerPetMessage petmsg = new ReqSyncPlayerPetMessage();
				petmsg.setPlayerId(player.getId());
				petmsg.setPet(JSONserializable.toString(pet));
				MessageUtil.send_to_world(petmsg);
			}
		}catch (Exception e) {
			logger.error(e,e);
		}*/
		logger.debug("玩家" + player.getId() + "得到召唤怪" + modelId);
	}
	
	/**
	 * 召唤怪强制取消
	 * @param player
	 * @param petId
	 */
	public void forceKillSummonPet(SummonPet  summonpet){
		if(summonpet==null){
			return;
		}
		
		Player player = ManagerPool.playerManager.getPlayer(summonpet.getOwnerId());
		if(player==null){
			return;
		}

		MapManager.getInstance().quitMap(summonpet);	

		//移出召唤怪
		if (player.getSummonPetList().contains(summonpet)) {
			player.getSummonPetList().remove(summonpet);
		}
		summonpet.setShow(false);
		summonpet.setForceHide(true);
		summonpet.changeStateTo(SummonPetState.DIE);
		summonpet.setDieTime(System.currentTimeMillis());
		
		ResSummonPetDieMessage msg=new ResSummonPetDieMessage();
		msg.setPet(SummonPetInfoManager.getInstance().getDetailInfo(summonpet));
		MessageUtil.tell_player_message(player, msg);

	}

	public void die(SummonPet summonpet, Fighter killer){
		   forceKillSummonPet(summonpet);
	}

	

	
	/**
	 * 玩家攻击
	 * @param player
	 * @param fighter
	 * @param damage
	 */
	public void onwerAttack(Player player, Fighter fighter, int damage){
		if(fighter==null||fighter.isDie()){
			return;
		}
		
		if(player.getSummonPetList().size()>0){
			List<SummonPet> summonpetList = player.getSummonPetList();
			for (SummonPet summonpet : summonpetList) {
				if(summonpet.isShow() && !summonpet.isDie()){
					//切换召唤怪的攻击目标
//					pet.setAttackTarget(fighter);
					SummonPetOptManager.getInstance().changeAttackTarget(summonpet, fighter, SummonPetOptManager.FIGHTTYPE_PLAYER_ATTACK);
				}
			}
		}
	}
	
	/**
	 * 给召唤怪buff
	 * @param player
	 * @param fighter
	 * @param damage
	 */
	public SummonPet onwerBuff(Player player){
		if(player==null||player.isDie()){
			return null;
		}
		
		if(player.getSummonPetList().size()>0){
			List<SummonPet> summonpetList = player.getSummonPetList();
			for (SummonPet summonpet : summonpetList) {
				if(summonpet.isShow() && !summonpet.isDie()){
					return summonpet;
				}
			}
		}
		return null;
	}
	
	/**
	 * 玩家攻击波及
	 * @param player
	 * @param fighter
	 * @param damage
	 */
	public void onwerDamage(Player player, Fighter fighter, int damage){
		if(fighter==null||fighter.isDie()){
			return;
		}
		
		if(player.getSummonPetList().size()>0&&damage>0){
			List<SummonPet> summonpetList = player.getSummonPetList();
			for (SummonPet summonpet : summonpetList) {
				if(summonpet.isShow() && !summonpet.isDie()){
					//切换召唤怪的攻击目标
//					pet.setAttackTarget(fighter);
					if(summonpet.getTargetType()==FIGHTTYPE_PLAYER_ATTACK){
						continue;
					}
					SummonPetOptManager.getInstance().changeAttackTarget(summonpet ,fighter, SummonPetOptManager.FIGHTTYPE_PLAYER_DAMAGE);
				}
			}
		}
	}
	
	/**
	 * 玩家被攻击
	 * @param fighter
	 * @param player
	 * @param damage
	 */
	public void ownerDefence(Fighter fighter, Player player, int damage){
		if(fighter==null||fighter.isDie()){
			return;
		}
		if(damage>0)
		PlayerDaZuoManager.getInstacne().breakDaZuo(player);
		if(fighter instanceof Pet){
			return;
		}	
		if(player.getSummonPetList().size()>0 && damage>0){
			List<SummonPet> summonpetList = player.getSummonPetList();
			for (SummonPet summonpet : summonpetList) {
				if(!summonpet.isShow()){
					continue;
				}
				if(summonpet.isDie()){
					continue;
				}
				if(summonpet.getTargetType()==FIGHTTYPE_PLAYER_ATTACK){
					continue;
				}
				if(summonpet.getTargetType()==FIGHTTYPE_PLAYER_DAMAGE){
					continue;
				}
				changeAttackTarget(summonpet, fighter, FIGHTTYPE_PLAYER_DEFENCE);
			}
		}
	}
	
	/**
	 * 切换攻击目标
	 * @param pet
	 * @param fighter
	 * @param type	切换原因  用于判断优先级
	 */
	public void changeAttackTarget(SummonPet pet, Fighter fighter, int type){
		if(fighter instanceof Pet || !pet.isShow() || pet.isDie()){
			return;
		}
		if(pet.getTargetType()==type && type!=FIGHTTYPE_PLAYER_ATTACK){
			if(fighter!=null && !pet.getAttackTargets().contains(fighter)){
				if(fighter instanceof Player){
					Iterator<Fighter> iter = pet.getAttackTargets().iterator();
					while (iter.hasNext()) {
						Fighter fighter2 = (Fighter) iter.next();
						if(fighter2 instanceof Monster) iter.remove();
					}
				}
				if(pet.getAttackTargets().size()<10){
					pet.getAttackTargets().add(fighter);
					logger.debug("增加目标"+fighter+" "+type +" 目标个数:"+pet.getAttackTargets().size());
				}
			}
		}else{
			pet.setTargetType(type);
			pet.getAttackTargets().clear();
			if(fighter!=null) pet.getAttackTargets().add(fighter);
			logger.debug("切换目标"+fighter+" "+type +" 目标个数:"+pet.getAttackTargets().size());
		}
		
	}
	
	
	
	/**
	 * 召唤怪被攻击
	 * @param fighter
	 * @param pet
	 * @param damage
	 */
	public void summonpetDefence(Fighter fighter, SummonPet summonpet, int damage){
		if(fighter==null || fighter.isDie()){
			return;
		}
		if(damage<=0){
			return;
		}
		if(fighter instanceof SummonPet){
			logger.error("召唤怪不可以攻击召唤怪");
			return;
		}
		
		if(!summonpet.isShow()){
			return;
		}
		
		if(summonpet.isDie()){
			return;
		}
		Player player = PlayerManager.getInstance().getPlayer(summonpet.getOwnerId());
		PlayerDaZuoManager.getInstacne().breakDaZuo(player);
		if(summonpet.getTargetType()==FIGHTTYPE_PLAYER_ATTACK){
			return;
		}
		if(summonpet.getTargetType()==FIGHTTYPE_PLAYER_DAMAGE){
			return;
		}
		if(summonpet.getTargetType()==FIGHTTYPE_PLAYER_DEFENCE){
			return;
		}
		changeAttackTarget(summonpet, fighter, FIGHTTYPE_PET_DEFENCE);
	}
	
	/**
	 * 传送到主人附近
	 * @param pet
	 * @param mapid
	 * @param pos
	 */
	public void summonpetTransToOwner(SummonPet summonpet){
		Map map = ManagerPool.mapManager.getMap(summonpet.getServerId(), summonpet.getLine(), summonpet.getMap());
		if(map==null) return;
		Player player = PlayerManager.getInstance().getPlayer(summonpet.getOwnerId());
		Position pos = MapUtils.getBackPoint(player.getPosition(), player.getDirection(), map.getMapModelid());
		ManagerPool.mapManager.summonpetTrans(summonpet, pos);
	}
	
	public boolean summonpetJump(SummonPet pet, int type, Position target){
		if(pet.getJumpState()!=SummonPetJumpState.NOMAL){
			return false;
		}
		Map map = MapManager.getInstance().getMap(pet);
		if(map==null) return false;
		
		boolean result = false;
		if(type == 1){
			MapManager.getInstance().summonpetStopRun(pet);
//			target = getJumpTarget(map, pet.getPosition(), target).getCenter();
			//一跳
			Jump jump = pet.getJump();
			jump.setJumpStart(pet.getPosition());
			jump.setJumpEnd(target);
			
			jump.setJumpStartTime(System.currentTimeMillis());
			jump.setSpeed(pet.getSpeed() * Global.JUMP_SPEED/ Global.MAX_PROBABILITY);
			// 设置跳跃总时间
			int time = (int) (MapUtils.countDistance(jump.getJumpStart(),jump.getJumpEnd()) * 1000 / jump.getSpeed());
			jump.setTotalTime(time);
			
			pet.setJumpState(SummonPetJumpState.JUMP);
			
			result =  true;
		}else if(type == 2){
			//一跳过程中
			//触发二跳
			Jump jump = pet.getJump();
			jump.setJumpStart(pet.getPosition());
			jump.setJumpEnd(target);
 			jump.setSpeed(pet.getSpeed() * Global.JUMP_SPEED/ Global.MAX_PROBABILITY);
			jump.setJumpStartTime(System.currentTimeMillis());
			// 设置跳跃总时间
			int time = (int) (MapUtils.countDistance(jump.getJumpStart(),jump.getJumpEnd()) * 1000 / jump.getSpeed());
			jump.setTotalTime(time);
			
			pet.setJumpState(SummonPetJumpState.DOUBJUMP);

			result =  true;
		}
		
		if(result){
			Position start = pet.getPosition();
			ManagerPool.summonpetOptManager.setSummonPetPosition(pet, target);
			
			// 开始移动
			HashMap<Long, SummonPet> runnings = map.getRunningsummonPets();
			if (!runnings.containsKey(pet.getId())) {
				runnings.put(pet.getId(), pet);
			}
			/**
			 * 计算区域 *
			 */
			// 起跳所在区域
			int startAreaId = ManagerPool.mapManager.getAreaId(start);
			// 结束所在区域
			int endAreaId = ManagerPool.mapManager.getAreaId(pet.getPosition());
			
			logger.debug("pet " + pet.getId() + "(" + pet.getModelId() + ") changarea from " +startAreaId + " to " + endAreaId);
			// 跳跃区域改变
			if (startAreaId != endAreaId) {
				Area startArea = map.getAreas().get(startAreaId);
				if (startArea == null) {
					logger.debug("start area " + startAreaId + " is null, pet "
						+ pet.getId() + " position" + start);
				}
				if (!startArea.getSummonpets().contains(pet)) {
					logger.error("changearea area " + startArea.getId() + " not contains pet "
						+ pet.getId());
					Iterator<Area> iter = map.getAreas().values().iterator();
					while (iter.hasNext()) {
						Area area = (Area) iter.next();
						if (area.getPets().contains(pet)) {
							logger.error("changearea area " + area.getId() + " contains pet "
								+ pet.getMapModelId());
							area.getSummonpets().remove(pet);
						}
					}
				}else{
					startArea.getSummonpets().remove(pet);
				}
				Area newArea = map.getAreas().get(endAreaId);
				if (newArea == null) {
					logger.error("new area " + endAreaId + " is null, pet "
						+ pet.getId() + " position" + pet.getPosition());
				}
				newArea.getSummonpets().add(pet);
			}

	
			// 跳跃信息
			ResSummonPetJumpPositionsMessage msg=new ResSummonPetJumpPositionsMessage();
			msg.getPositions().add(start);
			msg.getPositions().add(target);
			msg.setState((byte)(pet.getJumpState()== SummonPetJumpState.DOUBJUMP?2:1));
			msg.setTime(pet.getJump().getTotalTime());
			msg.setPetId(pet.getId());
	
			//对起点区域和落点区域附近广播
			HashSet<Area> round = new HashSet<Area>();
			List<Area> oldRound = ManagerPool.mapManager.getRoundAreas(map, startAreaId);
			for (int i = 0; i < oldRound.size(); i++) {
				round.add(oldRound.get(i));
			}
			
			List<Area> newRound = ManagerPool.mapManager.getRoundAreas(map, endAreaId);
			for (int i = 0; i < newRound.size(); i++) {
				round.add(newRound.get(i));
			}
	
			Iterator<Area> iterArea = round.iterator();
			while (iterArea.hasNext()) {
				Area area = (Area) iterArea.next();
				Iterator<Player> iterPlayer = area.getPlayers().iterator();
				while (iterPlayer.hasNext()) {
					Player other = (Player) iterPlayer.next();
					msg.getRoleId().add(other.getId());
				}
			}
	
			MessageUtil.send_to_gate(map.getSendId(), msg);
			
			// 站立区域改变
			if (endAreaId != startAreaId) {
				
				HashSet<Area> oldSet = new HashSet<Area>();
				for (int i = 0; i < oldRound.size(); i++) {
					oldSet.add(oldRound.get(i));
				}

				HashSet<Area> newSet = new HashSet<Area>();
				for (int i = 0; i < newRound.size(); i++) {
					newSet.add(newRound.get(i));
				}
				
				ResRoundSummonPetDisappearMessage hidemsg = new ResRoundSummonPetDisappearMessage();
				hidemsg.getPetIds().add(pet.getId());
				for (int i = 0; i < oldRound.size(); i++) {
					Area area = oldRound.get(i);
					if (!newSet.contains(area)) {
//						System.out.println("area " + endAreaId + " hideid " + area.getId());
						Iterator<Player> iter = area.getPlayers().iterator();
						while (iter.hasNext()) {
							Player other = (Player) iter.next();
							if (pet.canSee(other)) {
								hidemsg.getRoleId().add(other.getId());
//								System.out.println("hide pet area " + area.getId() + " player " + other.getId());
							}
						}
					}
				}
				//召唤怪在原视野消失
				if (hidemsg.getRoleId().size() > 0) {
					MessageUtil.send_to_gate(map.getSendId(), hidemsg);
				}
				
				Grid[][] grids = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());
				ResRoundSummonPetMessage showmsg = new ResRoundSummonPetMessage();
				showmsg.setPet(ManagerPool.mapManager.getSummonPetInfo(pet, grids));
		
				for (int i = 0; i < newRound.size(); i++) {
					Area area = newRound.get(i);
					if (!oldSet.contains(area)) {
//						System.out.println("area " + endAreaId + " showid " + area.getId());
						Iterator<Player> iter = area.getPlayers().iterator();
						while (iter.hasNext()) {
							Player other = (Player) iter.next();
		
							if (pet.canSee(other)) {
								showmsg.getRoleId().add(other.getId());
//								System.out.println("show pet area " + area.getId() + " player " + other.getId());
							}
						}
					}
				}
				//召唤怪在新视野中出现
				if (showmsg.getRoleId().size() > 0) {
					MessageUtil.send_to_gate(map.getSendId(), showmsg);
				}
			}
		}
		return false;
	}
	
	/**
	 * 切换地点
	 *
	 * @param player
	 * @param pos 坐标
	 *
	 */
	public void setSummonPetPosition(SummonPet pet, Position pos) { 

		Q_mapBean mapBean = ManagerPool.dataManager.q_mapContainer.getMap().get(pet.getMapModelId());

		Grid[][] grids = ManagerPool.mapManager.getMapBlocks(mapBean.getQ_map_id());

		if (pos.getX() > mapBean.getQ_map_width() || pos.getY() > mapBean.getQ_map_height()) {
			return;
		}

		pet.setPosition(pos);

		Grid grid = MapUtils.getGrid(pos, grids);
		if (ManagerPool.mapManager.isSwimGrid(grid)) {
			if (SummonPetRunState.RUN==pet.getRunState()) {
				//游泳状态
				pet.setRunState(SummonPetRunState.SWIM);
				
				logger.debug("切换游泳状态");
			}
		} else {
			if (SummonPetRunState.SWIM==pet.getRunState()) {
				//走路状态
				pet.setRunState(SummonPetRunState.RUN);
				
				logger.debug("切换走路状态");
			}
		}
	}
	
	
//	/**
//	 * 获取可跳点
//	 * @param pet
//	 * @param pos
//	 * @return
//	 */
//	public Grid getJumpTarget(Map map, Position source, Position target){
////		Map map = MapManager.getInstance().getMap(pet);
//		Grid[][] mapBlocks = ManagerPool.mapManager.getMapBlocks(map.getMapModelid());
//		//起跳格
//		Grid sourceGrid= MapUtils.getGrid(source, mapBlocks);
//		//终点格
//		Grid targetGrid= MapUtils.getGrid(target, mapBlocks);
//		while(MapUtils.countDistance(sourceGrid.getCenter(), targetGrid.getCenter())>Global.PET_MAX_JUMPDISTANCE||MapUtils.isBlock(targetGrid)){
////			 8:⊙,7：↖， 6：←， 5：↙， 4：↓， 3：↘， 2：→，1：↗，0：↑
//			int countDirection = countDirection(targetGrid, sourceGrid);
//			switch (countDirection) {
//			case 0:
//				//上方 X减一 Y轴不变
//				targetGrid=mapBlocks[targetGrid.getX()][targetGrid.getY()-1];
//				break;
//			case 1:
//				targetGrid=mapBlocks[targetGrid.getX()+1][targetGrid.getY()-1];
//				break;
//			case 2:
//				targetGrid=mapBlocks[targetGrid.getX()][targetGrid.getY()+1];
//				break;
//			case 3:
//				targetGrid=mapBlocks[targetGrid.getX()+1][targetGrid.getY()+1];
//				break;
//			case 4:
//				targetGrid=mapBlocks[targetGrid.getX()+1][targetGrid.getY()];
//				break;
//			case 5:
//				targetGrid=mapBlocks[targetGrid.getX()-1][targetGrid.getY()+1];
//				break;
//			case 6:
//				targetGrid=mapBlocks[targetGrid.getX()-1][targetGrid.getY()];
//				break;
//			case 7:
//				targetGrid=mapBlocks[targetGrid.getX()-1][targetGrid.getY()-1];
//				break;
//			case 8:
//				targetGrid=sourceGrid;
//			default:
//				break;
//			}	
//		}
//		if(MapUtils.countDistance(sourceGrid.getCenter(), targetGrid.getCenter())<=Global.PET_MAX_JUMPDISTANCE&&!MapUtils.isBlock(targetGrid)){
//			return targetGrid;
//		}		
//		return sourceGrid;
//	}
//	
//	/**
//	 * 获取两点间的中间点
//	 * @param map
//	 * @param source
//	 * @param target
//	 * @return
//	 */
//	public Grid getMiddleGrid(Map map,Position source,Position target){
//		int x=(source.getX()+target.getX())/2;
//		int y=(source.getY()-target.getY())/2;
//		Grid[][] mapBlocks = MapManager.getInstance().getMapBlocks(map.getMapModelid());
//		return mapBlocks[x][y];
//	}
//	
//	/**
//	 * 计算以1为原点 2所在的方向  8:⊙,7：↖， 6：←， 5：↙， 4：↓， 3：↘， 2：→，1：↗，0：↑
//	 * @param grid1
//	 * @param grid2
//	 * @return
//	 */
//	private static int countDirection(Grid grid1, Grid grid2){
//		if(grid1.getY() == grid2.getY() && grid1.getX() == grid2.getX()){
//			return 8;
//		}else if(grid1.getY() == grid2.getY()) {
//			if(grid1.getX() < grid2.getX()){
//				return 2;
//			}else{
//				return 6;
//			}
//		}else if(grid1.getX() == grid2.getX()) {
//			if(grid1.getY() < grid2.getY()){
//				return 4;
//			}else{
//				return 0;
//			}
//		}else if(grid1.getX() < grid2.getX()){
//			if(grid1.getY() < grid2.getY()){
//				return 3;
//			}else{
//				return 1;
//			}
//		}else{
//			if(grid1.getY() < grid2.getY()){
//				return 5;
//			}else{
//				return 7;
//			}
//		}
//	}
}
