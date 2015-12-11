package com.game.dataserver.handler;

import org.apache.log4j.Logger;

import com.game.dataserver.message.ResReceivePlayerRewardToDataServerMessage;
import com.game.command.Handler;

public class ResReceivePlayerRewardToDataServerHandler extends Handler{

	Logger log = Logger.getLogger(ResReceivePlayerRewardToDataServerHandler.class);

	public void action(){
		try{
			ResReceivePlayerRewardToDataServerMessage msg = (ResReceivePlayerRewardToDataServerMessage)this.getMessage();
			//TODO 添加消息处理
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}