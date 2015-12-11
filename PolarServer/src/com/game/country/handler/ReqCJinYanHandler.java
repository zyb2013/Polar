package com.game.country.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.country.message.ReqCJinYanMessage;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqCJinYanHandler extends Handler{

	Logger log = Logger.getLogger(ReqCJinYanHandler.class);

	public void action(){
		try{
			ReqCJinYanMessage msg = (ReqCJinYanMessage)this.getMessage();
			ManagerPool.countryManager.jinyan((Player)this.getParameter(),msg.getPlayername());
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}