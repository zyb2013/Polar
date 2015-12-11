package com.game.vip.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.player.structs.Player;
import com.game.vip.manager.VipManager;

/**
 * 
 * @author luminghua
 * 
 * @date 2013年12月28日 下午4:15:33
 * 
 *       打开vip面板
 */
public class ReqPlayerVIPInfoHandler extends Handler{

	Logger log = Logger.getLogger(ReqPlayerVIPInfoHandler.class);

	public void action(){
		try{
			Player player = (Player)this.getParameter();
			VipManager.getInstance().sendPlayerVipInfo(player);
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}