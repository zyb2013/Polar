package com.game.bank.handler;

import org.apache.log4j.Logger;

import com.game.backend.message.ResPlayerInfoToWorldMessage;
import com.game.bank.message.ReqQueryBankLogToWorldMessage;
import com.game.bank.message.ReqSendBankLogToWorldMessage;
import com.game.command.Handler;
import com.game.manager.ManagerPool;

public class ReqQueryBankLogToWorldHandler extends Handler{

	Logger log = Logger.getLogger(ReqQueryBankLogToWorldHandler.class);

	public void action(){
		try{
			ReqQueryBankLogToWorldMessage msg = (ReqQueryBankLogToWorldMessage)this.getMessage();
			ManagerPool.bankManager.ReqQueryBankLogToWorldMessage(msg);
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}