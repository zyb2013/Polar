package com.game.task.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.player.structs.Player;
import com.game.task.manager.TaskManager;
import com.game.task.message.ReqTaskTransMessage;

public class ReqTaskTransHandler extends Handler{

	Logger log = Logger.getLogger(ReqTaskTransHandler.class);

	public void action(){
		try{
			ReqTaskTransMessage msg = (ReqTaskTransMessage) this.getMessage();
			TaskManager.getInstance().taskTrans((Player) this.getParameter(), msg.getType(), msg.getMapid(), msg.getX(), msg.getY(), msg.getLine(),msg.getTaskId());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}