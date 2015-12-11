package com.game.zones.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;

public class ReqPtMainHandler extends Handler{

	Logger log = Logger.getLogger(ReqPtMainHandler.class);

	public void action(){
		try{
//			ManagerPool.zonesManager.openPtMain((Player) this.getParameter());
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}