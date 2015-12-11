package com.game.player.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.message.ReqSyncPlayerJobToWorldMessage;

public class ReqSyncPlayerJobToWorldHandler extends Handler{

	Logger log = Logger.getLogger(ReqSyncPlayerJobToWorldHandler.class);

	public void action(){
		try{
			ReqSyncPlayerJobToWorldMessage msg = (ReqSyncPlayerJobToWorldMessage) this.getMessage();
			ManagerPool.playerManager.changePlayerJob(msg.getPlayerID(), msg.getJob());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}