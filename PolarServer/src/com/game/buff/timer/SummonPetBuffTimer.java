package com.game.buff.timer;

import java.util.Iterator;

import com.game.manager.ManagerPool;
import com.game.map.structs.Map;
import com.game.summonpet.struts.SummonPet;
import com.game.timer.TimerEvent;

public class SummonPetBuffTimer extends TimerEvent {

	//private Logger log = Logger.getLogger(MonsterBuffTimer.class);
	
	private int serverId;
	
	private int lineId;
	
	private int mapId;
	
	public SummonPetBuffTimer(int serverId, int lineId, int mapId){
		super(-1, 1000);
		this.serverId = serverId;
		this.lineId = lineId;
		this.mapId=mapId;
	}
	
	@Override
	public void action() {
		//按地图，区域遍历怪物列表
		Map map = ManagerPool.mapManager.getMap(serverId, lineId, mapId);
		
		//遍历地区
//		Iterator<Area> areaIter = map.getAreas().values().iterator();
//		while (areaIter.hasNext()) {
//			Area area = (Area) areaIter.next();
		Iterator<SummonPet> iter = map.getSummonpets().values().iterator();
		while (iter.hasNext()) {
			SummonPet summonpet = (SummonPet) iter.next();
			if(summonpet.isDie()) continue;

			if(summonpet.getBuffs().size()==0) continue;
			
			ManagerPool.buffManager.countBuff(summonpet);
		}
//		}
	}
}
