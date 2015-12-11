package com.game.question.handler;

import org.apache.log4j.Logger;

import com.game.player.structs.Player;
import com.game.question.message.ReqGetQuestionJoinToWorldMessage;
import com.game.utils.MessageUtil;
import com.game.command.Handler;

public class ReqGetQuestionJoinHandler extends Handler{

	Logger log = Logger.getLogger(ReqGetQuestionJoinHandler.class);

	public void action(){
		try{
			ReqGetQuestionJoinToWorldMessage msg = new ReqGetQuestionJoinToWorldMessage();
			msg.setPlayerId(((Player)this.getParameter()).getId());
			MessageUtil.send_to_world(msg);
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}