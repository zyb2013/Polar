package com.game.dataserver.handler;

import org.apache.log4j.Logger;

import com.game.dataserver.message.ReqPlayerRewardFromDataServerMessage;
import com.game.command.Handler;

public class ReqPlayerRewardFromDataServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqPlayerRewardFromDataServerHandler.class);

	public void action(){
		try{
			ReqPlayerRewardFromDataServerMessage msg = (ReqPlayerRewardFromDataServerMessage)this.getMessage();
			//TODO 添加消息处理
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}