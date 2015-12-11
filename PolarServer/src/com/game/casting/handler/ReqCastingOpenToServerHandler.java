package com.game.casting.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqCastingOpenToServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqCastingOpenToServerHandler.class);

	public void action(){
		try{
//			ReqCastingOpenToServerMessage msg = new ReqCastingOpenToServerMessage();
			ManagerPool.castingManager.reqCastingOpen((Player) this.getParameter());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}