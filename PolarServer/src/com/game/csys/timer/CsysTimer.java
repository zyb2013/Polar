package com.game.csys.timer;

import java.util.List;

import com.game.country.bean.WarRewardInfo;
import com.game.country.manager.CountryManager;
import com.game.country.message.ResKingCityTimeRewardToClientMessage;
import com.game.country.message.ResKingCityYuXiCoordinateToClientMessage;
import com.game.country.structs.KingCity;
import com.game.data.bean.Q_characterBean;
import com.game.data.manager.DataManager;
import com.game.languageres.manager.ResManager;
import com.game.manager.ManagerPool;
import com.game.map.structs.Map;
import com.game.npc.struts.NPC;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
import com.game.timer.TimerEvent;
import com.game.utils.MessageUtil;
import com.game.utils.TimeUtil;

public class CsysTimer extends TimerEvent{

	private int serverId;
	
	private int lineId;
	
	private int mapId;

	
	
	public CsysTimer(int serverId, int lineId, int mapId) {
		super(-1,1000);
		this.serverId=serverId;
		this.lineId=lineId;
		this.mapId = mapId;
	}
	

	@Override
	public void action() {
		//获取地图
		Map map = ManagerPool.mapManager.getMap(serverId, lineId, mapId);
		if (map.getMapModelid() != ManagerPool.csysManger.CSYS_MAPID) {
			return;
		}
		long systime = System.currentTimeMillis()/1000;
		if( systime %10 == 0){	
			ManagerPool.csysManger.sendCsystopinfo(map);
		}
	}
	
	
	
	
}
