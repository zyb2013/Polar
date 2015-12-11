package com.game.player.handler;

import org.apache.log4j.Logger;

import com.game.player.message.ReqSyncPlayerPataMessage;
import com.game.command.Handler;
import com.game.toplist.manager.TopListManager;
import com.game.toplist.structs.PataTop;

public class ReqSyncPlayerPataHandler extends Handler {
	Logger log = Logger.getLogger(ReqSyncPlayerPataHandler.class);

	public void action(){
		try{
			ReqSyncPlayerPataMessage msg = (ReqSyncPlayerPataMessage)this.getMessage();
			PataTop pataTop = new PataTop(msg.getPlayerId(), msg.getPataId(), msg.getPataTime());
			TopListManager.getInstance().updateTopData(pataTop);
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}