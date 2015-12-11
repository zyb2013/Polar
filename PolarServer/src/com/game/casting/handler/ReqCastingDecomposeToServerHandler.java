package com.game.casting.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;

public class ReqCastingDecomposeToServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqCastingDecomposeToServerHandler.class);

	public void action(){
		try{
//			ReqCastingDecomposeToServerMessage msg = (ReqCastingDecomposeToServerMessage)this.getMessage();
			/*xiaozhuoming: 暂时屏蔽
			ManagerPool.castingManager.reqCastingDecompose((Player) this.getParameter(), -1);*/
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}