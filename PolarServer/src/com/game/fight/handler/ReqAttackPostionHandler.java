package com.game.fight.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.fight.message.ReqAttackPostionMessage;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.structs.Position;
import com.game.utils.MapUtils;

public class ReqAttackPostionHandler extends Handler{

	Logger log = Logger.getLogger(ReqAttackPlayerHandler.class);

	public void action(){
		try{
			ReqAttackPostionMessage msg = (ReqAttackPostionMessage)this.getMessage();	
			 Position pos=null;
			 pos =  MapUtils.buildPosition( msg.getx() , msg.gety()) ;
			//攻击Postion
			ManagerPool.fightManager.playerAttackPosition((Player)this.getParameter(), msg.getmapModelId(), msg.getline(), pos, msg.getFightType(), msg.getFightDirection(), null, null);
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}