package com.game.goldraffle.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.goldraffle.manager.GoldRaffleManager;
import com.game.goldraffle.message.ReqGoldRaffleBoxUseItemToServerMessage;
import com.game.player.structs.Player;

public class ReqGoldRaffleBoxUseItemToServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqGoldRaffleBoxUseItemToServerHandler.class);

	public void action(){
		try{
			ReqGoldRaffleBoxUseItemToServerMessage msg = (ReqGoldRaffleBoxUseItemToServerMessage)this.getMessage();
			GoldRaffleManager.getInstance().reqGoldRaffleBoxUseItem((Player) this.getParameter(), msg.getGrididx(), msg.getType());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}