package com.game.bank.handler;

import org.apache.log4j.Logger;

import com.game.bank.message.ReqGetMonthBankMessage;
import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqGetMonthBankHandler extends Handler{

	Logger log = Logger.getLogger(ReqGetMonthBankHandler.class);

	public void action(){
		try{
			ReqGetMonthBankMessage msg = (ReqGetMonthBankMessage)this.getMessage();
			if(msg.getType()==0){
				ManagerPool.bankManager.getMonthRewardByFrist((Player)this.getParameter());
			}else{
				ManagerPool.bankManager.getMonthRewardByDay((Player)this.getParameter());
			}
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}