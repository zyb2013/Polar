package com.game.toplist.handler;

import org.apache.log4j.Logger;

import com.game.toplist.message.ReqGetTopGuildListToWorldMessage;
import com.game.toplist.message.ReqGetTopListToWorldMessage;
import com.game.command.Handler;
import com.game.toplist.manager.TopListManager;

public class ReqGetGuildTopListToWorldHandler extends Handler{

	Logger log = Logger.getLogger(ReqGetGuildTopListToWorldHandler.class);

	public void action(){
		try{
			ReqGetTopGuildListToWorldMessage msg = (ReqGetTopGuildListToWorldMessage)this.getMessage();
			TopListManager.getInstance().reqGetTopGuildListToWorld(msg);
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}