package com.game.login.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.login.message.ReqLoadFinishMessage;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

public class ReqLoadFinishHandler extends Handler{

	Logger log = Logger.getLogger(ReqLoadFinishHandler.class);

	public void action(){
		try{
			ReqLoadFinishMessage msg = (ReqLoadFinishMessage)this.getMessage();
//			//panic god 暂时自己添加
//			if (msg.getType() == 2) {
//				Player player = ManagerPool.playerManager.getPlayer(msg
//						.getRoleId().get(0));
//				if (player == null) {
//					// 加载人物
//					player = ManagerPool.playerManager.loadPlayer(msg
//							.getRoleId().get(0));
//					if (player == null) {
//						log.error("ReqLoadFinishMessage player "
//								+ msg.getRoleId().get(0) + " is null!");
//						return;
//					}					
//				}
//				this.setParameter(player);
//			}
			
			//登录完成
			ManagerPool.playerManager.login((Player)this.getParameter(), msg.getType(), msg.getWidth(), msg.getHeight());
		}catch(ClassCastException e){
			log.error(e, e);
		}
	}
}