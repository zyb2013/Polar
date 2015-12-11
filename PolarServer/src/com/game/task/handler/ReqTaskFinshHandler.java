package com.game.task.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.player.structs.Player;
import com.game.task.manager.TaskManager;
import com.game.task.message.ReqTaskFinshMessage;

public class ReqTaskFinshHandler extends Handler{

	Logger log = Logger.getLogger(ReqTaskFinshHandler.class);

	public void action(){
		try{
			ReqTaskFinshMessage msg = (ReqTaskFinshMessage)this.getMessage();
			TaskManager.getInstance().finshTask((Player) getParameter(),msg.getType(), msg.getTaskId(),msg.getIsDoubleRewards() == 1);
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}