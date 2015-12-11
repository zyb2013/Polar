package com.game.country.handler;

import org.apache.log4j.Logger;

import com.game.country.message.ReqCountrysalaryToGameMessage;
import com.game.country.message.ResGetTopGuildListToGameMessage;
import com.game.command.Handler;
import com.game.country.manager.CountryAwardManager;
import com.game.country.manager.CountryManager;
import com.game.player.structs.Player;

public class ReqGetTopGuildListToGameHandler extends Handler{

	Logger log = Logger.getLogger(ReqGetTopGuildListToGameHandler.class);

	public void action(){
		try{
			ResGetTopGuildListToGameMessage msg = (ResGetTopGuildListToGameMessage)this.getMessage();
			CountryManager.getInstance().stRescheckLV((Player)this.getParameter(), msg);
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}