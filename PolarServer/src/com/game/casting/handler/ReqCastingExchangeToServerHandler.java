package com.game.casting.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;

public class ReqCastingExchangeToServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqCastingExchangeToServerHandler.class);

	public void action(){
		try{
			/*xiaozhuoming: 暂时屏蔽
			ReqCastingExchangeToServerMessage msg = (ReqCastingExchangeToServerMessage)this.getMessage();
			ManagerPool.castingManager.reqCastingExchange((Player) this.getParameter(), msg.getExchangeID());*/
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}