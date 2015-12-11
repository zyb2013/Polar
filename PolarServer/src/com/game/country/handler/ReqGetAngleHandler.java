package com.game.country.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.country.manager.CountryAwardManager;
import com.game.country.message.ReqGetAngleMessage;
import com.game.country.message.ResGetTopGuildListToGameMessage;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqGetAngleHandler extends Handler{

	Logger log = Logger.getLogger(ReqGetAngleHandler.class);

	public void action(){
		try{
			ReqGetAngleMessage msg = (ReqGetAngleMessage)this.getMessage();
			CountryAwardManager.getInstance().getAngleBox((Player)this.getParameter(),msg.getCount());
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}