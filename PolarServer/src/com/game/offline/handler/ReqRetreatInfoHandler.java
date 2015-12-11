package com.game.offline.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.offline.manager.OffLineManager;
import com.game.player.structs.Player;

public class ReqRetreatInfoHandler extends Handler{

	Logger log = Logger.getLogger(ReqRetreatInfoHandler.class);

	public void action(){
		try{
			OffLineManager.getInstance().reqRetreatInfoMessageToServer((Player) this.getParameter());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}