package com.game.dazuo.handler;

import org.apache.log4j.Logger;

import com.game.dazuo.message.ReqCohesionZhenQiMessage;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.command.Handler;

public class ReqCohesionZhenQiHandler extends Handler{

	Logger log = Logger.getLogger(ReqCohesionZhenQiHandler.class);

	public void action(){
		try{
			ReqCohesionZhenQiMessage msg = (ReqCohesionZhenQiMessage)this.getMessage();

			ManagerPool.dazuoManager.stReqCohesionZhenQiMessage((Player)this.getParameter(),msg);
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}