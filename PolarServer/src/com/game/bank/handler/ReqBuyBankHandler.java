package com.game.bank.handler;

import org.apache.log4j.Logger;

import com.game.bank.message.ReqBuyBankMessage;
import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqBuyBankHandler extends Handler{

	Logger log = Logger.getLogger(ReqBuyBankHandler.class);

	public void action(){
		try{
			ReqBuyBankMessage msg = (ReqBuyBankMessage)this.getMessage();
			if(msg.getBuyLv()==0){
				ManagerPool.bankManager.buyMonth((Player)this.getParameter());
			}else{
				ManagerPool.bankManager.buyLevelBank((Player)this.getParameter(), msg.getBuyLv());
			}
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}