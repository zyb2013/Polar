package com.game.backpack.handler;

import org.apache.log4j.Logger;

import com.game.backpack.message.ReqDelItemMessage;
import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.structs.Reasons;
/**
 * @author  
 *
 */
public class ReqDelItemHandler extends Handler{

	Logger log = Logger.getLogger(ReqDelItemHandler.class);

	public void action(){
		try{
			ReqDelItemMessage msg = (ReqDelItemMessage)this.getMessage();
			
			ManagerPool.backpackManager.removeItem((Player)this.getParameter(), msg.getItemId(),Reasons.ACTIVITY_DEL,0l);
		}catch(ClassCastException e){
			log.error(e,e);
		}
	}
}