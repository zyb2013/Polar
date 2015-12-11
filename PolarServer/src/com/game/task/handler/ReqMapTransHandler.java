package com.game.task.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.map.manager.MapManager;
import com.game.player.structs.Player;
import com.game.task.message.ReqMapTransMessage;

/**
 * 
 * @author luminghua
 * 
 * @date 2013年12月26日 下午8:18:12
 * 
 *       原名是ReqConqueryTaskTransHandler，但是任务统一用ReqTaskTransMessage，所以这里改名用作小地图传送
 */
public class ReqMapTransHandler extends Handler{

	Logger log = Logger.getLogger(ReqMapTransHandler.class);

	public void action(){
		try{

			ReqMapTransMessage msg = (ReqMapTransMessage) this.getMessage();
			// // if (msg.getTaskId() != 0){
			// TaskManager.getInstance().transByConquerTask((Player) getParameter(), msg.getTaskId(), msg.getMapid(), msg.getX(), msg.getY(), msg.getLine());
			// // }
			// // else{
			MapManager.getInstance().playerTransToMonster((Player) getParameter(), msg.getMapid(), msg.getX(), msg.getY(), msg.getLine());
			// // }
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}