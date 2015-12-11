package com.game.question.handler;

import org.apache.log4j.Logger;

import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.question.manager.QuestionManager;
import com.game.question.message.ReqGetQuestionJoinToWorldMessage;
import com.game.command.Handler;

public class ReqGetQuestionJoinToWorldHandler extends Handler{

	Logger log = Logger.getLogger(ReqGetQuestionJoinToWorldHandler.class);

	public void action(){
		try{
			ReqGetQuestionJoinToWorldMessage msg = (ReqGetQuestionJoinToWorldMessage)this.getMessage();
			Player player = PlayerManager.getInstance().getPlayer(msg.getPlayerId());
			if (player != null) {
				QuestionManager.getInstance().join(player);
			}
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}