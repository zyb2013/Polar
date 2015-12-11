package com.game.dataserver.handler;

import org.apache.log4j.Logger;

import com.game.dataserver.message.ResTeamEnterToGameServerMessage;
import com.game.command.Handler;

public class ResTeamEnterToGameServerHandler extends Handler{

	Logger log = Logger.getLogger(ResTeamEnterToGameServerHandler.class);

	public void action(){
		try{
			ResTeamEnterToGameServerMessage msg = (ResTeamEnterToGameServerMessage)this.getMessage();
			//TODO 添加消息处理
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}