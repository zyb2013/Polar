package com.game.bank.handler;

import org.apache.log4j.Logger;

import com.game.bank.message.ReqQueryBankMessage;
import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqQueryBankHandler extends Handler{

	Logger log = Logger.getLogger(ReqQueryBankHandler.class);

	public void action(){
		try{
			ReqQueryBankMessage msg = (ReqQueryBankMessage)this.getMessage();
			ManagerPool.bankManager.sendBankStatToPlayer((Player)this.getParameter(), msg.getType());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}