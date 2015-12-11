package com.game.goldraffle.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.goldraffle.manager.GoldRaffleManager;
import com.game.goldraffle.message.ReqFractionToServerMessage;
import com.game.player.structs.Player;

public class ReqFractionToServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqFractionToServerHandler.class);

	public void action(){
		try{
			ReqFractionToServerMessage msg = (ReqFractionToServerMessage)this.getMessage();
			GoldRaffleManager.getInstance().reqFraction((Player) this.getParameter(), msg.getFractionID());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}