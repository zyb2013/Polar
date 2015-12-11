package com.game.zones.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.player.structs.Player;
import com.game.zones.manager.ZonesManager;

public class ReqTowerRewardHandler extends Handler{

	Logger log = Logger.getLogger(ReqTowerRewardHandler.class);

	public void action(){
		try{
			ZonesManager.getInstance().gainPtAward((Player)this.getParameter());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}