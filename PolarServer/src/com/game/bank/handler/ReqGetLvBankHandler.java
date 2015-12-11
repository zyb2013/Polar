package com.game.bank.handler;

import org.apache.log4j.Logger;

import com.game.bank.message.ReqGetLvBankMessage;
import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqGetLvBankHandler extends Handler{

	Logger log = Logger.getLogger(ReqGetLvBankHandler.class);

	public void action(){
		try{
			ReqGetLvBankMessage msg = (ReqGetLvBankMessage)this.getMessage();
			
				ManagerPool.bankManager.getLevelBankByLevel((Player)this.getParameter(), msg.getLv());
		
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}