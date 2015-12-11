package com.game.dataserver.handler;

import org.apache.log4j.Logger;

import com.game.dataserver.message.ReqReceivePlayerRewardToDataServerMessage;
import com.game.command.Handler;

public class ReqReceivePlayerRewardToDataServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqReceivePlayerRewardToDataServerHandler.class);

	public void action(){
		try{
			ReqReceivePlayerRewardToDataServerMessage msg = (ReqReceivePlayerRewardToDataServerMessage)this.getMessage();
			//TODO 添加消息处理
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}