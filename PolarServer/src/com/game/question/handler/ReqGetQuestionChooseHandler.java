package com.game.question.handler;

import org.apache.log4j.Logger;

import com.game.player.structs.Player;
import com.game.question.message.ReqGetQuestionChooseMessage;
import com.game.question.message.ReqGetQuestionChooseToWorldMessage;
import com.game.utils.MessageUtil;
import com.game.command.Handler;

public class ReqGetQuestionChooseHandler extends Handler{

	Logger log = Logger.getLogger(ReqGetQuestionChooseHandler.class);

	public void action(){
		try{
			ReqGetQuestionChooseMessage msg = (ReqGetQuestionChooseMessage)this.getMessage();
			ReqGetQuestionChooseToWorldMessage send = new ReqGetQuestionChooseToWorldMessage();
			send.setChoose(msg.getChoose());
			send.setPlayerId(((Player)this.getParameter()).getId());
			MessageUtil.send_to_world(send);
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}