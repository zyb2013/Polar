package com.game.dataserver.handler;

import org.apache.log4j.Logger;

import com.game.dataserver.message.ReqCheckPlayerEnterToGameServerMessage;
import com.game.manager.ManagerPool;
import com.game.command.Handler;

public class ReqCheckPlayerEnterToGameServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqCheckPlayerEnterToGameServerHandler.class);

	public void action(){
		try{
			ReqCheckPlayerEnterToGameServerMessage msg = (ReqCheckPlayerEnterToGameServerMessage)this.getMessage();
			//个人报名检查
			ManagerPool.enterManager.reqCheckPlayerEnterToGameServer(msg);
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}