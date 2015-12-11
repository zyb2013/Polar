package com.game.player.handler;

import org.apache.log4j.Logger;

import com.game.player.message.ReqSyncPlayerSkillMessage;
import com.game.command.Handler;
import com.game.toplist.manager.TopListManager;
import com.game.toplist.structs.GestTop;

public class ReqSyncPlayerSkillHandler extends Handler{

	Logger log = Logger.getLogger(ReqSyncPlayerSkillHandler.class);

	public void action(){
		try{
			ReqSyncPlayerSkillMessage msg = (ReqSyncPlayerSkillMessage)this.getMessage();
			GestTop gestTop = new GestTop(msg.getPlayerId(), msg.getSkillLevel(), msg.getSkillTime());
			TopListManager.getInstance().updateTopData(gestTop);
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}