package com.game.liveness.handler;

import org.apache.log4j.Logger;

import com.game.command.Handler;
import com.game.liveness.message.ReqGainBoxToServerMessage;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;

/**
 * 领取宝箱奖励请求处理类
 * @author hongxiao.z
 * @date   2013-12-26  下午3:03:54
 */
public class ReqGainBoxToServerHandler extends Handler
{

	Logger log = Logger.getLogger(ReqGainBoxToServerHandler.class);

	public void action()
	{
		try
		{
			ReqGainBoxToServerMessage msg = (ReqGainBoxToServerMessage)this.getMessage();
			ManagerPool.livenessManager.gainBox((Player) this.getParameter(), (short)msg.getBoxid());
		}
		catch(ClassCastException e)
		{
			log.error(e,e);
		}
	}
}