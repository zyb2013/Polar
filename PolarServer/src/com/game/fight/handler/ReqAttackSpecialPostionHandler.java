package com.game.fight.handler;
import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.fight.message.ReqAttackSpecialMessage;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.structs.Position;
import com.game.utils.MapUtils;

public class ReqAttackSpecialPostionHandler extends Handler{

	Logger log = Logger.getLogger(ReqAttackSpecialPostionHandler.class);

	public void action(){
		try{
			ReqAttackSpecialMessage msg = (ReqAttackSpecialMessage)this.getMessage();	
			 Position pos=null;
			 pos =  MapUtils.buildPosition( msg.getx() , msg.gety()) ;
			//攻击Postion
			ManagerPool.fightManager.playerAttackPosition((Player)this.getParameter(), msg.getmapModelId(), msg.getline(), pos, msg.getFightType(), msg.getFightDirection(), msg.getFightTargets(), msg.getFightTypes());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}