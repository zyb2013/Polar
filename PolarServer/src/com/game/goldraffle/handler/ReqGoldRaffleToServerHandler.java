package com.game.goldraffle.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.goldraffle.manager.GoldRaffleManager;
import com.game.goldraffle.message.ReqGoldRaffleToServerMessage;
import com.game.player.structs.Player;

public class ReqGoldRaffleToServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqGoldRaffleToServerHandler.class);

	public void action(){
		try{
			ReqGoldRaffleToServerMessage msg = (ReqGoldRaffleToServerMessage)this.getMessage();
			GoldRaffleManager.getInstance().reqGoldRaffle((Player) this.getParameter(), msg.getType());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}