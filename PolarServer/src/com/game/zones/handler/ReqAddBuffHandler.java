package com.game.zones.handler;

import org.apache.log4j.Logger;

import com.game.zones.message.ReqAddBuffMessage;
import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqAddBuffHandler extends Handler{

	Logger log = Logger.getLogger(ReqAddBuffHandler.class);

	public void action(){
		try{
			ReqAddBuffMessage msg = (ReqAddBuffMessage)this.getMessage();
			ManagerPool.zonesFlopManager.stReqAddBuffMessage((Player)this.getParameter(),msg.getType());
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}