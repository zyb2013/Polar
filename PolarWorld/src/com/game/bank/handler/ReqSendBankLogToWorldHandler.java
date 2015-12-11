package com.game.bank.handler;

import org.apache.log4j.Logger;

import com.game.backend.message.ResPlayerInfoToWorldMessage;
import com.game.bank.message.ReqSendBankLogToWorldMessage;
import com.game.command.Handler;
import com.game.manager.ManagerPool;

public class ReqSendBankLogToWorldHandler extends Handler{

	Logger log = Logger.getLogger(ReqSendBankLogToWorldHandler.class);

	public void action(){
		try{
			ReqSendBankLogToWorldMessage msg = (ReqSendBankLogToWorldMessage)this.getMessage();
			ManagerPool.bankManager.reqSendBankMessage(msg);
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}