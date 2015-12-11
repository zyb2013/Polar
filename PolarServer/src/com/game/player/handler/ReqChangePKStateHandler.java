package com.game.player.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.map.manager.MapManager;
import com.game.player.message.ReqChangePKStateMessage;
import com.game.player.structs.Player;

public class ReqChangePKStateHandler extends Handler{

	Logger log = Logger.getLogger(ReqChangePKStateHandler.class);

	public void action(){
		try{
			ReqChangePKStateMessage msg = (ReqChangePKStateMessage)this.getMessage();

			Player player = (Player) this.getParameter();
			boolean isCopy = MapManager.getInstance().getMap(player).isCopy();
			if (player != null && isCopy == true) {
				return;
			}
			
			//更改攻击模式
			ManagerPool.playerManager.changePkState(player, msg.getPkState(), msg.getAuto());
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}