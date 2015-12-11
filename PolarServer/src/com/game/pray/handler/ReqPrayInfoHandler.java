package com.game.pray.handler;

import org.apache.log4j.Logger;
import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqPrayInfoHandler extends Handler{

	Logger log = Logger.getLogger(ReqPrayInfoHandler.class);

	public void action(){
		try{
//			ReqPrayInfoMessage msg = (ReqPrayInfoMessage)this.getMessage();
			ManagerPool.prayManager.stReqPrayInfo((Player)this.getParameter());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}