package com.game.gradegift.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.gradegift.manager.GradeGiftManager;
import com.game.gradegift.message.ReqGainGradeGiftMessage;
import com.game.player.structs.Player;

public class ReqGainGradeGiftHandler extends Handler{

	Logger log = Logger.getLogger(ReqGainGradeGiftHandler.class);

	public void action(){
		try{
			ReqGainGradeGiftMessage msg = (ReqGainGradeGiftMessage)this.getMessage();
			GradeGiftManager.gainReward((Player)this.getParameter(), msg.getGiftId());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}