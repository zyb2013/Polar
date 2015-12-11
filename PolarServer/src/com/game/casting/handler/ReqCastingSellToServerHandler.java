package com.game.casting.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqCastingSellToServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqCastingSellToServerHandler.class);

	public void action(){
		try{
//			ReqCastingSellToServerMessage msg = (ReqCastingSellToServerMessage)this.getMessage();
			ManagerPool.castingManager.reqCastingSell((Player) this.getParameter(), -1);
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}