package com.game.casting.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqCastingRewardToServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqCastingRewardToServerHandler.class);

	public void action(){
		try{
//			ReqCastingRewardToServerMessage msg = (ReqCastingRewardToServerMessage)this.getMessage();
			ManagerPool.castingManager.reqCastingReward((Player) this.getParameter());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}