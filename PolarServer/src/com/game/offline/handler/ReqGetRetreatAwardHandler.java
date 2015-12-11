package com.game.offline.handler;

import org.apache.log4j.Logger;

import com.game.offline.message.ReqGetRetreatAwardMessage;
import com.game.command.Handler;
import com.game.offline.manager.OffLineManager;
import com.game.player.structs.Player;

public class ReqGetRetreatAwardHandler extends Handler{

	Logger log = Logger.getLogger(ReqGetRetreatAwardHandler.class);

	public void action(){
		try{
			ReqGetRetreatAwardMessage msg = (ReqGetRetreatAwardMessage)this.getMessage();
			OffLineManager.getInstance().getAward((Player) this.getParameter(), msg.getGetType());
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}