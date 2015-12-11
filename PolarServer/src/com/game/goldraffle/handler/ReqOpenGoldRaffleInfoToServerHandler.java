package com.game.goldraffle.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.goldraffle.manager.GoldRaffleManager;
import com.game.player.structs.Player;

public class ReqOpenGoldRaffleInfoToServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqOpenGoldRaffleInfoToServerHandler.class);

	public void action(){
		try{
			GoldRaffleManager.getInstance().reqOpenGoldRaffleInfo((Player) this.getParameter());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}