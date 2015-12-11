package com.game.fight.handler;

import org.apache.log4j.Logger;
import com.game.fight.message.ReqAttackSummonPetMessage;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.command.Handler;

public class ReqAttackSummonPetHandler extends Handler{
	Logger log = Logger.getLogger(ReqAttackPetHandler.class);
	public void action(){
		try{
			ReqAttackSummonPetMessage msg = (ReqAttackSummonPetMessage)this.getMessage();
			ManagerPool.fightManager.playerAttackSummonPet((Player)getParameter(), msg.getOwherId(), msg.getFightTarget(), msg.getFightType(), msg.getFightDirection());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}