package com.game.dataserver.handler;

import org.apache.log4j.Logger;

import com.game.dataserver.message.ResCancelTeamEnterToGameServerMessage;
import com.game.command.Handler;

public class ResCancelTeamEnterToGameServerHandler extends Handler{

	Logger log = Logger.getLogger(ResCancelTeamEnterToGameServerHandler.class);

	public void action(){
		try{
			ResCancelTeamEnterToGameServerMessage msg = (ResCancelTeamEnterToGameServerMessage)this.getMessage();
			//TODO 添加消息处理
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}