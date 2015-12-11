package com.game.dataserver.handler;

import org.apache.log4j.Logger;

import com.game.dataserver.message.ResPlayerRewardFromDataServerMessage;
import com.game.command.Handler;

public class ResPlayerRewardFromDataServerHandler extends Handler{

	Logger log = Logger.getLogger(ResPlayerRewardFromDataServerHandler.class);

	public void action(){
		try{
			ResPlayerRewardFromDataServerMessage msg = (ResPlayerRewardFromDataServerMessage)this.getMessage();
			//TODO 添加消息处理
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}