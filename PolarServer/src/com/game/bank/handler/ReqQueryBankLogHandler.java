package com.game.bank.handler;

import org.apache.log4j.Logger;

import com.game.bank.message.ReqQueryBankLogMessage;
import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqQueryBankLogHandler extends Handler{

	Logger log = Logger.getLogger(ReqQueryBankLogHandler.class);

	public void action(){
		try{
			ReqQueryBankLogMessage msg = (ReqQueryBankLogMessage)this.getMessage();
			ManagerPool.bankManager.queryPlayersHistory(msg,(Player)this.getParameter() );
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}