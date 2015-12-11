package com.game.signwage.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.signwage.message.ReqFillSignMessage;

/**
 * 补签处理类
 * @author hongxiao.z
 * @date   2013-12-30  下午4:20:07
 */
public class ReqFillSignHandler extends Handler{

	Logger log = Logger.getLogger(ReqFillSignHandler.class);

	public void action(){
		try{
			ReqFillSignMessage msg = (ReqFillSignMessage)this.getMessage();
			//心跳信息检测
			Player player = (Player)this.getParameter();
			
			if(player!=null)
			{
				ManagerPool.signWageManager.fillSign(player, msg.getDay());
			}
			
		}catch(ClassCastException e){
			log.error(e);
		}
	}
}