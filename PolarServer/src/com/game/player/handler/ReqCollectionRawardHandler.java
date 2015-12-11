package com.game.player.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqCollectionRawardHandler extends Handler{

	Logger log = Logger.getLogger(ReqCollectionRawardHandler.class);

	public void action(){
		try{
			//结束挂机
			ManagerPool.playerManager.collectionReward((Player)getParameter());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}