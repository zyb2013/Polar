package com.game.dataserver.handler;

import org.apache.log4j.Logger;

import com.game.dataserver.message.ReqCheckTeamEnterToGameServerMessage;
import com.game.manager.ManagerPool;
import com.game.command.Handler;

public class ReqCheckTeamEnterToGameServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqCheckTeamEnterToGameServerHandler.class);

	public void action(){
		try{
			ReqCheckTeamEnterToGameServerMessage msg = (ReqCheckTeamEnterToGameServerMessage)this.getMessage();
			//队伍报名检查
			ManagerPool.enterManager.reqCheckTeamEnterToGameServer(msg);
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}