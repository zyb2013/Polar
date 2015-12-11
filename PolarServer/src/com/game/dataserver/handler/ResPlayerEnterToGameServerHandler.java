package com.game.dataserver.handler;

import org.apache.log4j.Logger;

import com.game.dataserver.message.ResPlayerEnterToGameServerMessage;
import com.game.command.Handler;

public class ResPlayerEnterToGameServerHandler extends Handler{

	Logger log = Logger.getLogger(ResPlayerEnterToGameServerHandler.class);

	public void action(){
		try{
			ResPlayerEnterToGameServerMessage msg = (ResPlayerEnterToGameServerMessage)this.getMessage();
			//TODO 添加消息处理
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}