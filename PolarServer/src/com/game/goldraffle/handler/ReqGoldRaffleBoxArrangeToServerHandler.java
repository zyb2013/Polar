package com.game.goldraffle.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqGoldRaffleBoxArrangeToServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqGoldRaffleBoxArrangeToServerHandler.class);

	public void action(){
		try{
//			ReqGoldRaffleBoxArrangeToServerMessage msg = (ReqGoldRaffleBoxArrangeToServerMessage)this.getMessage();
			ManagerPool.goldRaffleManager.reqGoldRaffleBoxArrange((Player) this.getParameter());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}