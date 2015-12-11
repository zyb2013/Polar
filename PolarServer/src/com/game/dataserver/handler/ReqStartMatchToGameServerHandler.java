package com.game.dataserver.handler;

import org.apache.log4j.Logger;

import com.game.dataserver.message.ReqStartMatchToGameServerMessage;
import com.game.manager.ManagerPool;
import com.game.command.Handler;

public class ReqStartMatchToGameServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqStartMatchToGameServerHandler.class);

	public void action(){
		try{
			ReqStartMatchToGameServerMessage msg = (ReqStartMatchToGameServerMessage)this.getMessage();
			//开始比赛
			ManagerPool.enterManager.reqStartMatchToGameServer(msg);
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}