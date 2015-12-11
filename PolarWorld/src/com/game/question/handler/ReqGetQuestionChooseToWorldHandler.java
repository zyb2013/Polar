package com.game.question.handler;

import org.apache.log4j.Logger;

import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.question.manager.QuestionManager;
import com.game.question.message.ReqGetQuestionChooseToWorldMessage;
import com.game.command.Handler;

public class ReqGetQuestionChooseToWorldHandler extends Handler{

	Logger log = Logger.getLogger(ReqGetQuestionChooseToWorldHandler.class);

	public void action(){
		try{
			ReqGetQuestionChooseToWorldMessage msg = (ReqGetQuestionChooseToWorldMessage)this.getMessage();
			Player player = PlayerManager.getInstance().getPlayer(msg.getPlayerId());
			if (player != null) {
				QuestionManager.getInstance().chooseMsg(player, msg.getChoose());
			}
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}