package com.game.pray.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.pray.message.ReqPrayMessage;

public class ReqPrayHandler extends Handler{

	Logger log = Logger.getLogger(ReqPrayHandler.class);

	public void action(){
		try{
			ReqPrayMessage msg = (ReqPrayMessage)this.getMessage();
			ManagerPool.prayManager.stReqPray((Player)this.getParameter(), msg.getType());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}