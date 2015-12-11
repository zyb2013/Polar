package com.game.zones.handler;

import org.apache.log4j.Logger;

import com.game.zones.message.ReqAddBuffMessage;
import com.game.zones.message.ReqClearZoneCDMessage;
import com.game.zones.message.ReqInventedZoneMessage;
import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqClearZoneCDHandler extends Handler{

	Logger log = Logger.getLogger(ReqClearZoneCDHandler.class);

	public void action(){

		try{
			ReqClearZoneCDMessage msg = (ReqClearZoneCDMessage)this.getMessage();
			ManagerPool.zonesFlopManager.stReqClearZoneCDMessage((Player)this.getParameter(),msg.getZoneid());
			
		}catch(ClassCastException e){
			log.error(e);
		}
	
		
	}
}