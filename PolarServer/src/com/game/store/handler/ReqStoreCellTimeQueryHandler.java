package com.game.store.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;

public class ReqStoreCellTimeQueryHandler extends Handler{

	Logger log = Logger.getLogger(ReqStoreCellTimeQueryHandler.class);

	public void action(){
		try{
			/*
			 * luminghua hide
			 * ReqStoreCellTimeQueryMessage msg = (ReqStoreCellTimeQueryMessage)this.getMessage();
			ManagerPool.storeManager.cellTimeQuery((Player)this.getParameter(), msg.getCellId(),msg.getNpcid());*/
		}catch(ClassCastException e){
			log.error(e,e);
		}
	}
}