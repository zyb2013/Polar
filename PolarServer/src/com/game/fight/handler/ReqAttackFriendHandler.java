package com.game.fight.handler;

import org.apache.log4j.Logger;

import com.game.fight.message.ReqAttackFriendMessage;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.command.Handler;

public class ReqAttackFriendHandler extends Handler{

	Logger log = Logger.getLogger(ReqAttackFriendHandler.class);

	public void action(){
		try{
			ReqAttackFriendMessage msg = (ReqAttackFriendMessage)this.getMessage();
			//攻击怪物
			ManagerPool.fightManager.playerAttackFriend((Player)this.getParameter(), msg.getFightTarget(),msg.getTargetType(), msg.getFightType(), msg.getFightDirection());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}