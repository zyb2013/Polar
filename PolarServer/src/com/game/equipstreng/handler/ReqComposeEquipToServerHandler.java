package com.game.equipstreng.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.equipstreng.message.ReqComposeEquipToServerMessage;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqComposeEquipToServerHandler extends Handler{

	Logger log = Logger.getLogger(ReqComposeEquipToServerHandler.class);

	public void action(){
		try{
			ReqComposeEquipToServerMessage msg = (ReqComposeEquipToServerMessage)this.getMessage();
			ManagerPool.euipComposeManager.equipCompose((Player) this.getParameter(), msg);
			
		}catch(ClassCastException e){
			log.error("panic god" + e);
		}
	}
}