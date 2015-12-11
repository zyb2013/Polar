package com.game.casting.handler;

import org.apache.log4j.Logger;

import com.game.casting.message.ReqCastingUseItemToServerMessage;
import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqCastingUseItemToServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqCastingUseItemToServerHandler.class);

	public void action(){
		try{
			ReqCastingUseItemToServerMessage msg = (ReqCastingUseItemToServerMessage)this.getMessage();
			ManagerPool.castingManager.reqCastingUseItem((Player) this.getParameter(), msg.getGrididx(), msg.getType());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}