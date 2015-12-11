package com.game.zones.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.player.structs.Player;
import com.game.zones.manager.ZonesManager;

public class ReqTowerNextLvHandler extends Handler{

	Logger log = Logger.getLogger(ReqTowerNextLvHandler.class);

	public void action(){
		try{
			ZonesManager.getInstance().changePtLevel((Player)this.getParameter());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}