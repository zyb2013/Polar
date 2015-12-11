package com.game.summonpet.timer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.game.data.bean.Q_petattributeBean;
import com.game.data.bean.Q_petinfoBean;
import com.game.data.manager.DataManager;
import com.game.dazuo.manager.PlayerDaZuoManager;
import com.game.manager.ManagerPool;
import com.game.map.message.ResRoundPetMessage;
import com.game.map.structs.Map;
import com.game.pet.manager.PetInfoManager;
import com.game.pet.manager.PetScriptManager;
import com.game.pet.message.ResPetReviveMessage;
import com.game.pet.struts.Pet;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.structs.Grid;
import com.game.summonpet.manager.SummonPetInfoManager;
import com.game.summonpet.struts.SummonPet;
import com.game.timer.TimerEvent;
import com.game.utils.Global;
import com.game.utils.MessageUtil;
/**
 * 召唤怪回血
 * @author  
 *
 */
public class SummonPetHeartTimer extends TimerEvent {
	
	private static final Logger logger = Logger.getLogger(SummonPetHeartTimer.class);
	
	private int serverId;
	
	private int lineId;
	
	private int mapId;
	
	public SummonPetHeartTimer(int serverId, int lineId, int mapId) {
		super(-1, 1000);
		this.serverId = serverId;
		this.lineId = lineId;
		this.mapId = mapId;
	}

	@Override
	public void action() {

		// 获取地图
		Map map = ManagerPool.mapManager.getMap(serverId, lineId, mapId);
		if (map.isEmpty())
			return;				
		Iterator<SummonPet> iterator = map.getSummonpets().values().iterator();
		List<SummonPet> hides = new ArrayList<SummonPet>();
		try{
			while (iterator.hasNext()) {
				SummonPet summonpet = (SummonPet) iterator.next();
				
				if(summonpet.getServerId()!=this.serverId || summonpet.getLine()!=this.lineId || summonpet.getMap()!=this.mapId){
					continue;
				}
				/*
				if(!summonpet.isDie()&&summonpet.isShow()){
					PetScriptManager.getInstance().petTimmerAction(summonpet);
				}*/
				Player player = ManagerPool.playerManager.getPlayer(summonpet.getOwnerId());
				if(player.isDie()){
					hides.add(summonpet);
					continue;
				}
				
				if(!SummonPetInfoManager.getInstance().isFullHp(summonpet) && !summonpet.isDie()){
					//10秒回一次
					int recover = summonpet.getMaxHp()*3/100;   // model.getQ_recover_hp();
					//设定召唤怪物每间隔10秒回复一次血量，回复量为当前血量上限的3%(暂定)
						long beforeRecovery = System.currentTimeMillis() - summonpet.getLastRecoveryTime();
						if(recover>0 && beforeRecovery>=Global.PET_RECOVERY_INTERVALTIME){
							   summonpet.setLastRecoveryTime(System.currentTimeMillis());
							   SummonPetInfoManager.getInstance().addHp(summonpet, recover);
							}
						}	
				   /*panic 屏蔽，设定召唤怪物每间隔10秒回复一次血量，回复量为当前血量上限的3%(暂定),即使在战斗状态下也回
				    * 
				    int recover = summonpet.getMaxHp()/10000;   // model.getQ_recover_hp();
					long ideltime = System.currentTimeMillis() - summonpet.getLastFightTime();
					if(ideltime>=Global.PET_RECOVERY_NEEDIDELTIME){
						long beforeRecovery = System.currentTimeMillis() - summonpet.getLastRecoveryTime();
						if(recover>0 && beforeRecovery>=Global.PET_RECOVERY_INTERVALTIME){
							   summonpet.setLastRecoveryTime(System.currentTimeMillis());
							   SummonPetInfoManager.getInstance().addHp(summonpet, recover);
							}
						}	
					}
                   * */
				}	
			
		}catch (Exception e) {
			logger.error(e,e);
		}

		for (int i = 0; i < hides.size(); i++) {
			Player player = PlayerManager.getInstance().getPlayer(hides.get(i).getOwnerId());
			if (player != null) logger.error("角色[" + player.getId() + "]召唤怪[" + hides.get(i).getId() + "]操作[timer force hide]");
			ManagerPool.summonpetOptManager.forceKillSummonPet(hides.get(i));
		}
		
	}
}
