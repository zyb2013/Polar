package com.game.signwage.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqSignToClientHandler extends Handler{

	Logger log = Logger.getLogger(ReqSignToClientHandler.class);

	public void action(){
		try{
			ManagerPool.signWageManager.openSign((Player)this.getParameter());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}