package com.game.player.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.message.ReqChangeOneAttributeMessage;
import com.game.player.structs.Player;

public class ReqChangePlayerPlusPointHandler extends Handler{

	Logger log = Logger.getLogger(ReqChangePlayerPlusPointHandler.class);

	public void action(){
		try{
			ReqChangeOneAttributeMessage msg = (ReqChangeOneAttributeMessage)this.getMessage();
			ManagerPool.playerManager.changeOneAttibuteHandle((Player)this.getParameter(),msg);
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}