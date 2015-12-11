package com.game.zones.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.player.structs.Player;
import com.game.zones.manager.ZonesManager;

public class ReqTowerIndexHandler extends Handler{

	Logger log = Logger.getLogger(ReqTowerIndexHandler.class);

	public void action(){
		try{
			ZonesManager.getInstance().getPtIndexMsg((Player)this.getParameter());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}