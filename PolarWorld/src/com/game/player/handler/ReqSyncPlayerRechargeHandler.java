package com.game.player.handler;

import org.apache.log4j.Logger;

import com.game.player.message.ReqSyncPlayerRechargeMessage;
import com.game.command.Handler;
import com.game.toplist.manager.TopListManager;
import com.game.toplist.structs.RechargeTop;

public class ReqSyncPlayerRechargeHandler extends Handler {
	Logger log = Logger.getLogger(ReqSyncPlayerRechargeHandler.class);

	public void action(){
		try{
			ReqSyncPlayerRechargeMessage msg = (ReqSyncPlayerRechargeMessage)this.getMessage();
			RechargeTop rechargeTop = new RechargeTop(msg.getPlayerId(), msg.getRechargeGold(), msg.getRechargeTime());
			TopListManager.getInstance().updateTopData(rechargeTop);
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}
