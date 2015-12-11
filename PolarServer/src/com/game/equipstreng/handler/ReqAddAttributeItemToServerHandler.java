package com.game.equipstreng.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.equipstreng.message.ReqAddAttributeItemToServerMessage;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqAddAttributeItemToServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqAddAttributeItemToServerHandler.class);

	public void action(){
		try{
			ReqAddAttributeItemToServerMessage msg = (ReqAddAttributeItemToServerMessage)this.getMessage();
			ManagerPool.equipAttributeAddManager.equipAddAttribute((Player) this.getParameter(), msg);
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}