package com.game.dataserver.handler;

import org.apache.log4j.Logger;

import com.game.dataserver.message.ResCancelPlayerEnterToGameServerMessage;
import com.game.command.Handler;

public class ResCancelPlayerEnterToGameServerHandler extends Handler{

	Logger log = Logger.getLogger(ResCancelPlayerEnterToGameServerHandler.class);

	public void action(){
		try{
			ResCancelPlayerEnterToGameServerMessage msg = (ResCancelPlayerEnterToGameServerMessage)this.getMessage();
			//TODO 添加消息处理
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}