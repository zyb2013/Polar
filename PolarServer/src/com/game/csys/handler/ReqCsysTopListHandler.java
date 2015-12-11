package com.game.csys.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqCsysTopListHandler extends Handler{

	Logger log = Logger.getLogger(ReqCsysTopListHandler.class);

	public void action(){
		try{
			//ReqCountryOpenTopToGameMessage msg = (ReqCountryOpenTopToGameMessage)this.getMessage();
			ManagerPool.csysManger.sendCsystopinfo((Player)this.getParameter());
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}